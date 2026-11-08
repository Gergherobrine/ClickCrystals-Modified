package io.github.itzispyder.clickcrystals.modules.modules.anchoring;

import io.github.itzispyder.clickcrystals.combat.CombatDecisionEngine;
import io.github.itzispyder.clickcrystals.combat.CombatDecisionEngine.*;
import io.github.itzispyder.clickcrystals.events.EventHandler;
import io.github.itzispyder.clickcrystals.events.events.world.ClientTickEndEvent;
import io.github.itzispyder.clickcrystals.modrinth.ModrinthNoNo;
import io.github.itzispyder.clickcrystals.modules.Categories;
import io.github.itzispyder.clickcrystals.modules.ModuleSetting;
import io.github.itzispyder.clickcrystals.modules.modules.ListenerModule;
import io.github.itzispyder.clickcrystals.modules.modules.crystalling.AutoClicker;
import io.github.itzispyder.clickcrystals.modules.settings.SettingSection;
import io.github.itzispyder.clickcrystals.util.minecraft.ChatUtils;
import io.github.itzispyder.clickcrystals.util.minecraft.InteractionUtils;
import io.github.itzispyder.clickcrystals.util.minecraft.PlayerUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.registry.Registries;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;

import java.util.Locale;

/**
 * Client-side, crosshair-only TriggerBot. Does not select targets, rotate
 * camera, spoof motion, spoof onGround, or modify sprint packets.
 *
 * Everything executes on the normal client game thread at END_CLIENT_TICK.
 * No queued automatic attack can survive a target/world transition.
 */
@ModrinthNoNo
public final class AdvancedTriggerBot extends ListenerModule {

    private final CombatDecisionEngine engine = new CombatDecisionEngine();
    private final SettingSection behavior = createSettingSection("combat-behavior");

    // 0 = auto, 1 = sword, 2 = OP sword, 3 = diamond pot,
    // 4 = netherite pot, 5 = SMP, 6 = axe, 7 = custom.
    public final ModuleSetting<Integer> profile = behavior.add(createIntSetting()
            .name("profile-0-auto-1-sword-2-op-3-dia-pot-4-net-pot-5-smp-6-axe-7-custom")
            .description("0 auto; 1 sword; 2 OP; 3 diamond pot; 4 netherite pot; 5 SMP; 6 axe; 7 custom")
            .min(0).max(7).def(0).build());

    // 0 strict, 1 adaptive, 2 combo, 3 smart, 4 manual assist.
    public final ModuleSetting<Integer> criticals = behavior.add(createIntSetting()
            .name("crit-0-strict-1-adaptive-2-combo-3-smart-4-assist")
            .description("SmartCrits strategy; 4 is informative only")
            .min(0).max(4).def(3).build());

    // 0 full, 1 crit, 2 combo, 3 adaptive, 4 custom.
    public final ModuleSetting<Integer> timing = behavior.add(createIntSetting()
            .name("timing-0-full-1-crit-2-combo-3-adaptive-4-custom")
            .description("Attack cooldown strategy")
            .min(0).max(4).def(3).build());

    public final ModuleSetting<Integer> customCharge = behavior.add(createIntSetting()
            .name("custom-charge-pct")
            .description("Minimum charge percent when timing is custom")
            .min(85).max(100).def(95).build());

    public final ModuleSetting<Double> maxReach = behavior.add(createDoubleSetting()
            .name("max-crosshair-distance")
            .description("Additional cap in blocks; never expands vanilla interaction reach")
            .min(1.0).max(6.0).def(3.2).build());

    public final ModuleSetting<Boolean> playersOnly = behavior.add(createBoolSetting()
            .name("players-only")
            .description("Never attack mobs or armor stands when enabled")
            .def(true).build());

    public final ModuleSetting<Boolean> respectHurt = behavior.add(createBoolSetting()
            .name("respect-target-hurt-animation")
            .description("Conservatively wait during observed target damage immunity")
            .def(true).build());

    public final ModuleSetting<Boolean> diagnosticChat = behavior.add(createBoolSetting()
            .name("diagnostic-chat")
            .description("Rate-limited, client-only decision messages")
            .def(false).build());

    private Object lastWorld;
    private long tickCounter;
    private long lastReportTick = -100;
    private Reason lastReportedReason;
    private Decision lastDecision;
    private Observation lastObservation;
    private String diagnostic = "IDLE";

    public AdvancedTriggerBot() {
        super("advanced-triggerbot", Categories.PVP,
                "Crosshair-only adaptive TriggerBot with real critical timing and combo diagnostics");
    }

    @Override
    protected void onEnable() {
        resetForWorld();
        super.onEnable();
    }

    @Override
    protected void onDisable() {
        super.onDisable();
        resetForWorld();
    }

    private void resetForWorld() {
        engine.resetFight();
        lastWorld = null;
        tickCounter = 0;
        lastDecision = null;
        lastObservation = null;
        lastReportedReason = null;
        lastReportTick = -100;
        diagnostic = "IDLE";
        // No key state is owned by this module: inputAttack is one shot.
    }

    /** Local observational diagnostics, not an acknowledgement from the server. */
    public String getCombatDiagnostics() {
        return diagnostic;
    }

    public Statistics getCombatStatistics() {
        return engine.statistics();
    }

    public Decision getLastCombatDecision() {
        return lastDecision;
    }

    public Observation getLastCombatObservation() {
        return lastObservation;
    }

    private static Profile profileFromIndex(int i) {
        return Profile.values()[Math.max(0, Math.min(i, Profile.values().length - 1))];
    }

    private static CritMode critFromIndex(int i) {
        return CritMode.values()[Math.max(0, Math.min(i, CritMode.values().length - 1))];
    }

    private static Timing timingFromIndex(int i) {
        return Timing.values()[Math.max(0, Math.min(i, Timing.values().length - 1))];
    }

    private Policy currentPolicy() {
        return new Policy(profileFromIndex(profile.getVal()),
                critFromIndex(criticals.getVal()), timingFromIndex(timing.getVal()),
                customCharge.getVal() / 100.0, maxReach.getVal(), respectHurt.getVal());
    }

    private static Weapon classifyWeapon(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return Weapon.OTHER;
        Item item = stack.getItem();
        if (item instanceof SwordItem) return Weapon.SWORD;
        if (item instanceof AxeItem) return Weapon.AXE;
        String path = Registries.ITEM.getId(item).getPath();
        if (path.endsWith("_spear") || path.equals("spear")) return Weapon.SPEAR;
        if (path.equals("mace") || path.endsWith("_mace")) return Weapon.MACE;
        return Weapon.OTHER;
    }

    /**
     * Only a conservative 0..2-tick distance forecast. It is not used
     * to extend reach, rotate the player, or create a synthetic raycast.
     */
    private static double projectDistance(Vec3d eye, Vec3d hitPos,
                                          Vec3d playerVelocity, Vec3d targetVelocity,
                                          double actualDistance, long ping) {
        if (actualDistance < 1.0e-5) return actualDistance;
        Vec3d relative = targetVelocity.subtract(playerVelocity);
        Vec3d direction = hitPos.subtract(eye).normalize();
        double radialTicks = relative.dotProduct(direction);
        double bounded = Math.max(-0.35, Math.min(0.35, radialTicks));
        double horizonTicks = ping > 180 ? 1.0 : 2.0;
        return Math.max(0.0, actualDistance + bounded * horizonTicks);
    }

    @EventHandler
    private void onTick(ClientTickEndEvent event) {
        if (PlayerUtils.invalid() || !mc.player.isAlive()) {
            if (lastWorld != null) resetForWorld();
            diagnostic = "INVALID_WORLD_OR_PLAYER";
            return;
        }
        if (lastWorld != mc.world) {
            resetForWorld();
            lastWorld = mc.world;
        }
        tickCounter++;

        // Existing clicker may synthesize an attack independently. Never
        // compete with its enabled left-click loop.
        AutoClicker clicker = io.github.itzispyder.clickcrystals.modules.Module.get(AutoClicker.class);
        if (clicker != null && clicker.isEnabled() && clicker.left.getVal()) {
            diagnostic = "PAUSED_AUTOCLICKER_CONFLICT";
            engine.resetFight();
            lastDecision = null;
            lastObservation = null;
            return;
        }

        LivingEntity target = null;
        double distance = Double.POSITIVE_INFINITY;
        double forecast = Double.POSITIVE_INFINITY;
        boolean visible = false;
        if (mc.crosshairTarget instanceof EntityHitResult hit) {
            if (hit.getEntity() instanceof LivingEntity candidate &&
                    candidate.isAlive() && candidate != mc.player &&
                    (!playersOnly.getVal() || candidate instanceof PlayerEntity)) {
                target = candidate;
                Vec3d eye = mc.player.getEyePos();
                Vec3d hitPos = hit.getPos();
                distance = eye.distanceTo(hitPos);
                forecast = projectDistance(eye, hitPos, mc.player.getVelocity(),
                        candidate.getVelocity(), distance, PlayerUtils.getPing());
                visible = true;
            }
        }

        var p = mc.player;
        boolean environmentalBlock = p.isTouchingWater() || p.isClimbing() ||
                p.hasVehicle() || p.isGliding() ||
                p.hasStatusEffect(StatusEffects.SLOW_FALLING) ||
                p.hasStatusEffect(StatusEffects.BLINDNESS) ||
                p.hasStatusEffect(StatusEffects.LEVITATION);

        Observation observation = new Observation(tickCounter,
                target == null ? Integer.MIN_VALUE : target.getId(),
                target != null, p.isAlive(), mc.currentScreen != null,
                mc.options.attackKey.isPressed(), p.isUsingItem(), visible,
                classifyWeapon(p.getMainHandStack()),
                distance, forecast, p.getAttackCooldownProgress(0.0F),
                p.isOnGround(), !p.isOnGround() && p.fallDistance > 0.0F &&
                p.getVelocity().y < -0.01, p.isSprinting(),
                environmentalBlock, target != null && target.isBlocking(),
                target == null ? 0 : target.hurtTime, (int) PlayerUtils.getPing());
        lastObservation = observation;

        Decision decision = engine.evaluate(observation, currentPolicy());
        lastDecision = decision;
        diagnostic = String.format(Locale.ROOT,
                "phase=%s reason=%s profile=%s charge=%.3f crit=%s target=%s range=%.2f attempts=%d observed-hurt=%d",
                decision.phase(), decision.reason(), decision.activeProfile(),
                observation.cooldown(), decision.critCandidate(),
                observation.validTarget() ? observation.targetId() : "none",
                observation.distance(), engine.statistics().attemptedAttacks(),
                engine.statistics().observedHurtEvents());

        if (diagnosticChat.getVal() && (decision.attack() ||
                (decision.reason() != lastReportedReason && tickCounter - lastReportTick >= 20))) {
            ChatUtils.sendPrefixMessage("[TriggerBot] " + diagnostic);
            lastReportTick = tickCounter;
            lastReportedReason = decision.reason();
        }

        if (!decision.attack()) return;
        // Revalidate immediately before the one-shot vanilla attack path.
        if (mc.currentScreen != null || p.isUsingItem() ||
                mc.options.attackKey.isPressed() || !p.isAlive() ||
                !(mc.crosshairTarget instanceof EntityHitResult current) ||
                current.getEntity() != target || !target.isAlive() ||
                p.getEyePos().distanceTo(current.getPos()) > maxReach.getVal()) {
            diagnostic = "CANCELLED_REVALIDATION";
            return;
        }

        InteractionUtils.inputAttack();
    }
}
