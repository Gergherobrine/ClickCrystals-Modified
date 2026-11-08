package io.github.itzispyder.clickcrystals.combat;

/**
 * Deterministic, game-thread-only decision core for Advanced TriggerBot.
 * No Minecraft dependencies, packet mutations, aim changes, or artificial
 * onGround/fall state. Its input is an explicitly uncertain client snapshot.
 *
 * "Observed damage" below is a local indication, NEVER server confirmation
 * that this module landed the hit.
 */
public final class CombatDecisionEngine {

    public enum Weapon { SWORD, AXE, MACE, SPEAR, OTHER }
    public enum Profile { AUTO, SWORD, OP_SWORD, DIAMOND_POT, NETHERITE_POT, SMP, AXE, CUSTOM }
    public enum CritMode { STRICT, ADAPTIVE, COMBO, SMART, MANUAL_ASSIST }
    public enum Timing { FULL_DAMAGE, CRITICAL_PRIORITY, COMBO_PRIORITY, ADAPTIVE, CUSTOM }
    public enum Phase {
        IDLE, TARGET_ACQUIRED, OPENING, FIRST_HIT, COMBO_ACTIVE,
        COMBO_PRESSURE, CRITICAL_OPPORTUNITY, TARGET_KNOCKBACK,
        COMBO_RECOVERY, DEFENSIVE_WINDOW, DISENGAGE
    }
    public enum Reason {
        NO_TARGET, DEAD, SCREEN_OPEN, MANUAL_CONTROL, USING_ITEM,
        NO_LINE_OF_SIGHT, UNSUPPORTED_WEAPON, SHIELDING,
        OUT_OF_RANGE, TARGET_MOVING_OUT, INVALID_COOLDOWN,
        COOLDOWN, TARGET_INVULNERABLE, SPRINT_CONFLICT,
        WAIT_FOR_CRIT, WAIT_FOR_LANDING, RATE_LIMITED,
        MANUAL_ASSIST, ATTACK_CRITICAL, ATTACK_COMBO, ATTACK_FULL,
        ATTACK_OPPORTUNITY
    }

    public record Policy(Profile profile, CritMode critMode, Timing timing,
                         double customCharge, double maxReach,
                         boolean respectTargetHurtTime) {
        public Policy {
            if (profile == null || critMode == null || timing == null)
                throw new IllegalArgumentException("Combat policy cannot have null modes");
            if (!Double.isFinite(customCharge) || customCharge < 0.0 || customCharge > 1.0)
                throw new IllegalArgumentException("customCharge out of range");
            if (!Double.isFinite(maxReach) || maxReach <= 0.0 || maxReach > 6.0)
                throw new IllegalArgumentException("maxReach out of range");
        }
    }

    /**
     * Distance is measured from the eye to the current vanilla crosshair hit.
     * predictedDistance may only affect scheduling; it never authorizes a hit.
     * visible means the current hit result is a real entity hit, not an AABB
     * fabricated by a prediction module.
     */
    public record Observation(
            long tick, int targetId, boolean validTarget, boolean alive,
            boolean screenOpen, boolean manualAttack, boolean usingItem,
            boolean visible, Weapon weapon, double distance, double predictedDistance,
            double cooldown, boolean grounded, boolean falling, boolean sprinting,
            boolean critEnvironmentBlocked, boolean targetShielding,
            int targetHurtTicks, int pingMillis) { }

    public record Decision(boolean attack, Reason reason, Phase phase,
                           Profile activeProfile, boolean critCandidate,
                           long nextPossibleTick) { }

    public record Statistics(long attemptedAttacks, long criticalCandidatesUsed,
                             long observedHurtEvents, long missedOpportunities,
                             long comboResets, int currentLocalCombo,
                             Reason lastReason, Phase lastPhase) { }

    private Phase phase = Phase.IDLE;
    private Reason lastReason = Reason.NO_TARGET;
    private int targetId = Integer.MIN_VALUE;
    private int priorHurtTicks = -1;
    private int observedCombo = 0;
    private long lastAttackTick = Long.MIN_VALUE / 2;
    private long lastObservedHurtTick = Long.MIN_VALUE / 2;
    private long attempts, criticalAttempts, observedHurt, missed, resets;
    private long lastSampleTick = Long.MIN_VALUE;

    public static Profile resolveProfile(Profile desired, Weapon weapon) {
        if (desired != Profile.AUTO) return desired;
        return switch (weapon) {
            case AXE -> Profile.AXE;
            case SWORD -> Profile.SWORD;
            default -> Profile.SMP;
        };
    }

    public static double recommendedCharge(Profile profile) {
        return switch (profile) {
            case AXE, OP_SWORD, NETHERITE_POT -> 0.99;
            case DIAMOND_POT -> 0.91;
            case SWORD -> 0.96;
            case SMP -> 0.97;
            case CUSTOM -> 0.95;
            default -> 0.96;
        };
    }

    /**
     * A fresh world/dimension, server, respawn, or module disable must invalidate
     * the current combat chain. Lifetime counters can be reset separately.
     */
    public void resetFight() {
        targetId = Integer.MIN_VALUE;
        priorHurtTicks = -1;
        observedCombo = 0;
        lastAttackTick = Long.MIN_VALUE / 2;
        lastObservedHurtTick = Long.MIN_VALUE / 2;
        lastSampleTick = Long.MIN_VALUE;
        lastReason = Reason.NO_TARGET;
        phase = Phase.IDLE;
    }

    public void resetAll() {
        resetFight();
        attempts = criticalAttempts = observedHurt = missed = resets = 0;
    }

    public Statistics statistics() {
        return new Statistics(attempts, criticalAttempts, observedHurt, missed,
                resets, observedCombo, lastReason, phase);
    }

    /**
     * A decision is a proposal, not an executed attack. Call only after the
     * vanilla one-shot attack path was invoked, after final revalidation.
     * This records local attempts, never claimed server-confirmed hits.
     */
    public boolean confirmAttack(Decision decision, long tick) {
        if (decision == null || !decision.attack() ||
                tick != lastSampleTick || tick <= lastAttackTick) return false;
        lastAttackTick = tick;
        attempts++;
        if (decision.critCandidate()) criticalAttempts++;
        return true;
    }

    private Decision result(boolean attack, Reason why, Profile profile,
                            boolean critical, long nextTick) {
        lastReason = why;
        return new Decision(attack, why, phase, profile, critical, nextTick);
    }

    /**
     * Each tick must be evaluated at most once; duplicate event delivery cannot
     * create a second hit. The caller should also arbitrate against other modules.
     */
    public Decision evaluate(Observation s, Policy policy) {
        if (s == null || policy == null)
            throw new IllegalArgumentException("Null combat decision input");
        Profile profile = resolveProfile(policy.profile(), s.weapon() == null ? Weapon.OTHER : s.weapon());
        if (s.tick() <= lastSampleTick)
            return result(false, Reason.RATE_LIMITED, profile, false, lastSampleTick + 1);
        lastSampleTick = s.tick();

        if (!s.validTarget() || !s.alive()) {
            if (phase != Phase.IDLE && phase != Phase.DISENGAGE) resets++;
            targetId = Integer.MIN_VALUE;
            priorHurtTicks = -1;
            observedCombo = 0;
            phase = Phase.IDLE;
            return result(false, s.alive() ? Reason.NO_TARGET : Reason.DEAD, profile, false, s.tick() + 1);
        }
        if (s.targetId() != targetId) {
            if (targetId != Integer.MIN_VALUE) resets++;
            targetId = s.targetId();
            priorHurtTicks = -1;
            observedCombo = 0;
            phase = Phase.TARGET_ACQUIRED;
        }

        // A rising client hurt animation is only an observation. Other entities
        // can have caused it. Do not label it a confirmed hit or a critical.
        if (s.targetHurtTicks() > priorHurtTicks && s.targetHurtTicks() >= 7) {
            if (s.tick() - lastAttackTick <= 5 && s.tick() > lastObservedHurtTick) {
                observedHurt++;
                observedCombo++;
                lastObservedHurtTick = s.tick();
                phase = observedCombo == 1 ? Phase.FIRST_HIT : Phase.COMBO_ACTIVE;
            }
        }
        priorHurtTicks = s.targetHurtTicks();

        if (s.screenOpen()) {
            phase = Phase.DEFENSIVE_WINDOW;
            return result(false, Reason.SCREEN_OPEN, profile, false, s.tick() + 1);
        }
        if (s.manualAttack()) {
            phase = Phase.DEFENSIVE_WINDOW;
            return result(false, Reason.MANUAL_CONTROL, profile, false, s.tick() + 1);
        }
        if (s.usingItem()) {
            phase = Phase.DEFENSIVE_WINDOW;
            return result(false, Reason.USING_ITEM, profile, false, s.tick() + 1);
        }
        if (!s.visible()) {
            phase = Phase.DISENGAGE;
            return result(false, Reason.NO_LINE_OF_SIGHT, profile, false, s.tick() + 1);
        }
        if (s.weapon() == Weapon.OTHER || s.weapon() == Weapon.SPEAR || s.weapon() == null) {
            // Spear jab/charge have distinct 1.21.11 min/max range rules.
            // Never silently treat them as ordinary sword swings.
            return result(false, Reason.UNSUPPORTED_WEAPON, profile, false, s.tick() + 1);
        }
        if (s.targetShielding() && s.weapon() != Weapon.AXE) {
            phase = Phase.DEFENSIVE_WINDOW;
            return result(false, Reason.SHIELDING, profile, false, s.tick() + 1);
        }
        if (!Double.isFinite(s.distance()) || s.distance() < 0.0 ||
                s.distance() > policy.maxReach()) {
            phase = Phase.COMBO_RECOVERY;
            return result(false, Reason.OUT_OF_RANGE, profile, false, s.tick() + 1);
        }
        if (!Double.isFinite(s.cooldown()) || s.cooldown() < 0.0) {
            return result(false, Reason.INVALID_COOLDOWN, profile, false, s.tick() + 1);
        }

        boolean realCrit = !s.grounded() && s.falling() && !s.sprinting() &&
                !s.critEnvironmentBlocked() && s.cooldown() >= 0.848 &&
                s.weapon() != Weapon.MACE; // Mace has distinct smash mechanics
        if (policy.critMode() == CritMode.MANUAL_ASSIST)
            return result(false, Reason.MANUAL_ASSIST, profile, realCrit, s.tick() + 1);

        if (s.tick() - lastAttackTick < 2)
            return result(false, Reason.RATE_LIMITED, profile, realCrit, lastAttackTick + 2);

        // This is a *conservative local guess* about the damage-immunity window.
        if (policy.respectTargetHurtTime() && s.targetHurtTicks() > 6) {
            phase = Phase.COMBO_PRESSURE;
            return result(false, Reason.TARGET_INVULNERABLE, profile, realCrit, s.tick() + 1);
        }

        double minimum = policy.timing() == Timing.CUSTOM
                ? policy.customCharge() : recommendedCharge(profile);
        if (policy.timing() == Timing.FULL_DAMAGE) minimum = 0.999;
        if (policy.timing() == Timing.CRITICAL_PRIORITY) minimum = Math.max(0.93, minimum);
        if (policy.timing() == Timing.COMBO_PRIORITY && observedCombo > 0)
            minimum = Math.min(minimum, 0.88);
        if (policy.timing() == Timing.ADAPTIVE && observedCombo > 1 &&
                profile != Profile.AXE) minimum = Math.min(minimum, 0.92);
        // Restrict proposed fast combos to the actual Java critical threshold.
        minimum = Math.max(0.848, minimum);

        if (s.cooldown() + 1.0e-6 < minimum) {
            if (phase == Phase.TARGET_ACQUIRED) phase = Phase.OPENING;
            return result(false, Reason.COOLDOWN, profile, realCrit, s.tick() + 1);
        }

        if (policy.critMode() == CritMode.STRICT && !realCrit) {
            phase = Phase.CRITICAL_OPPORTUNITY;
            return result(false, s.sprinting() ? Reason.SPRINT_CONFLICT
                    : Reason.WAIT_FOR_CRIT, profile, false, s.tick() + 1);
        }

        boolean targetMayLeave = Double.isFinite(s.predictedDistance()) &&
                s.predictedDistance() > policy.maxReach() - 0.12;
        boolean waitingForFall = !s.grounded() && !s.falling() &&
                !s.critEnvironmentBlocked();
        boolean prioritizesCrit = policy.critMode() == CritMode.SMART ||
                policy.critMode() == CritMode.COMBO ||
                policy.critMode() == CritMode.ADAPTIVE ||
                policy.timing() == Timing.CRITICAL_PRIORITY;
        if (!realCrit && prioritizesCrit && waitingForFall && !targetMayLeave &&
                s.weapon() != Weapon.MACE) {
            phase = Phase.CRITICAL_OPPORTUNITY;
            return result(false, Reason.WAIT_FOR_LANDING, profile, false, s.tick() + 1);
        }
        if (!realCrit && policy.timing() == Timing.CRITICAL_PRIORITY &&
                !targetMayLeave && s.sprinting() && s.weapon() != Weapon.MACE) {
            phase = Phase.CRITICAL_OPPORTUNITY;
            return result(false, Reason.SPRINT_CONFLICT, profile, false, s.tick() + 1);
        }

        // A predicted position cannot authorize a hit. It only changes whether
        // to wait for a better future hit. Actual hit validity is current-only.
        if (realCrit) phase = Phase.CRITICAL_OPPORTUNITY;
        else if (observedCombo > 1) phase = Phase.COMBO_ACTIVE;
        else if (observedCombo == 1) phase = Phase.COMBO_PRESSURE;
        else phase = Phase.OPENING;

        Reason reason = realCrit ? Reason.ATTACK_CRITICAL
                : observedCombo > 0 ? Reason.ATTACK_COMBO
                : targetMayLeave ? Reason.ATTACK_OPPORTUNITY
                : Reason.ATTACK_FULL;
        return result(true, reason, profile, realCrit, s.tick() + 2);
    }
}
