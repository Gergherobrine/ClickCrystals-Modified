package io.github.itzispyder.clickcrystals.combat;

import io.github.itzispyder.clickcrystals.combat.CombatDecisionEngine.*;

/**
 * No-test-library deterministic regression suite. Run:
 * ./gradlew verifyCombatDecision
 *
 * Includes negative cases. It proves decision policies, not live multiplayer
 * timing, packet behavior, server-confirmed damage, or anti-cheat behavior.
 */
public final class CombatDecisionEngineTest {
    private static int passed;

    private static final class Case {
        long tick = 1;
        int id = 42, hurt = 0, ping = 60;
        boolean target = true, alive = true, screen = false, manual = false;
        boolean using = false, visible = true, grounded = true, falling = false;
        boolean sprint = false, blockedCrit = false, shield = false;
        double distance = 2.5, prediction = 2.5, cooldown = 1.0;
        Weapon weapon = Weapon.SWORD;
        Observation snap() {
            return new Observation(tick, id, target, alive, screen, manual,
                    using, visible, weapon, distance, prediction, cooldown,
                    grounded, falling, sprint, blockedCrit, shield, hurt, ping);
        }
    }

    private static Policy p(Profile profile, CritMode crit, Timing timing) {
        return new Policy(profile, crit, timing, 0.95, 3.2, true);
    }
    private static final Policy DEFAULT = p(Profile.AUTO, CritMode.SMART, Timing.ADAPTIVE);
    private static Decision check(String name, Case c, Policy policy, Reason expected) {
        Decision d = new CombatDecisionEngine().evaluate(c.snap(), policy);
        assertReason(name, d, expected);
        return d;
    }
    private static void assertReason(String name, Decision d, Reason expected) {
        if (d.reason() != expected)
            throw new AssertionError(name + ": expected " + expected + " but got " + d);
        passed++;
    }
    private static void that(String name, boolean condition) {
        if (!condition) throw new AssertionError(name);
        passed++;
    }

    public static void main(String[] args) {
        Case c = new Case();
        c.target = false;
        check("no target", c, DEFAULT, Reason.NO_TARGET);

        c = new Case();
        check("grounded full attack", c, DEFAULT, Reason.ATTACK_FULL);

        c = new Case();
        c.grounded = false;
        c.falling = false;
        check("rising; wait for falling phase", c, DEFAULT, Reason.WAIT_FOR_LANDING);

        c = new Case();
        c.grounded = false;
        c.falling = true;
        Decision crit = check("real falling crit", c, DEFAULT, Reason.ATTACK_CRITICAL);
        that("critical candidate is true", crit.critCandidate());

        c = new Case();
        c.grounded = false;
        c.falling = true;
        c.sprint = true;
        check("strict crit blocked by sprint", c,
                p(Profile.SWORD, CritMode.STRICT, Timing.FULL_DAMAGE),
                Reason.SPRINT_CONFLICT);

        c = new Case();
        c.grounded = false;
        c.falling = true;
        c.blockedCrit = true;
        check("water, vehicle, climb or effect inhibits crit", c,
                p(Profile.SWORD, CritMode.STRICT, Timing.ADAPTIVE),
                Reason.WAIT_FOR_CRIT);

        c = new Case();
        c.cooldown = .60;
        check("cooldown below crit threshold", c, DEFAULT, Reason.COOLDOWN);

        c = new Case();
        c.cooldown = .85;
        check("full-damage mode waits for 100 percent", c,
                p(Profile.SWORD, CritMode.ADAPTIVE, Timing.FULL_DAMAGE),
                Reason.COOLDOWN);

        c = new Case();
        c.distance = 3.4;
        check("never extends range", c, DEFAULT, Reason.OUT_OF_RANGE);

        c = new Case();
        c.grounded = false;
        c.prediction = 3.25;
        check("forecast only changes when to attack", c, DEFAULT,
                Reason.ATTACK_OPPORTUNITY);

        c = new Case();
        c.visible = false;
        check("no true crosshair hit", c, DEFAULT, Reason.NO_LINE_OF_SIGHT);

        c = new Case();
        c.manual = true;
        check("manual attack has priority", c, DEFAULT, Reason.MANUAL_CONTROL);

        c = new Case();
        c.screen = true;
        check("inventory open", c, DEFAULT, Reason.SCREEN_OPEN);

        c = new Case();
        c.using = true;
        check("healing or active item use", c, DEFAULT, Reason.USING_ITEM);

        c = new Case();
        c.shield = true;
        check("sword does not waste shield hit", c, DEFAULT, Reason.SHIELDING);

        c = new Case();
        c.shield = true;
        c.weapon = Weapon.AXE;
        check("axe allowed against shield", c, DEFAULT, Reason.ATTACK_FULL);

        c = new Case();
        c.weapon = Weapon.SPEAR;
        check("no incorrect spear jab", c, DEFAULT, Reason.UNSUPPORTED_WEAPON);

        c = new Case();
        c.weapon = Weapon.OTHER;
        check("only supported weapons", c, DEFAULT, Reason.UNSUPPORTED_WEAPON);

        c = new Case();
        check("manual assist never auto-attacks", c,
                p(Profile.SWORD, CritMode.MANUAL_ASSIST, Timing.ADAPTIVE),
                Reason.MANUAL_ASSIST);

        c = new Case();
        c.hurt = 9;
        check("local damage immunity wait", c, DEFAULT, Reason.TARGET_INVULNERABLE);

        c = new Case();
        c.alive = false;
        check("dead player", c, DEFAULT, Reason.DEAD);

        c = new Case();
        c.cooldown = Double.NaN;
        check("invalid local attack readiness", c, DEFAULT, Reason.INVALID_COOLDOWN);

        CombatDecisionEngine engine = new CombatDecisionEngine();
        c = new Case();
        assertReason("first tick attack", engine.evaluate(c.snap(), DEFAULT), Reason.ATTACK_FULL);
        assertReason("repeat same tick ignored", engine.evaluate(c.snap(), DEFAULT), Reason.RATE_LIMITED);
        c.tick = 2;
        assertReason("next tick attack limited", engine.evaluate(c.snap(), DEFAULT), Reason.RATE_LIMITED);
        c.tick = 3;
        assertReason("allowed after 2 ticks", engine.evaluate(c.snap(), DEFAULT), Reason.ATTACK_FULL);
        that("attempts tracked; not server hits", engine.statistics().attemptedAttacks() == 2);

        engine = new CombatDecisionEngine();
        c = new Case();
        engine.evaluate(c.snap(), DEFAULT);
        c.tick = 2;
        c.hurt = 10;
        engine.evaluate(c.snap(), DEFAULT);
        that("target damage animation only observed", engine.statistics().observedHurtEvents() == 1);
        that("first observed chain", engine.statistics().currentLocalCombo() == 1);
        c.id = 77;
        c.tick = 3;
        c.hurt = 0;
        engine.evaluate(c.snap(), DEFAULT);
        that("target switch invalidates chain", engine.statistics().currentLocalCombo() == 0);

        engine.resetFight();
        that("world reset clears attack rate limiter", engine.statistics().currentLocalCombo() == 0);
        c.tick = 1;
        assertReason("new world's first tick", engine.evaluate(c.snap(), DEFAULT), Reason.ATTACK_FULL);

        that("auto-axe profile", CombatDecisionEngine.resolveProfile(Profile.AUTO, Weapon.AXE) == Profile.AXE);
        that("auto-sword profile", CombatDecisionEngine.resolveProfile(Profile.AUTO, Weapon.SWORD) == Profile.SWORD);
        that("manual profile is respected", CombatDecisionEngine.resolveProfile(Profile.OP_SWORD, Weapon.AXE) == Profile.OP_SWORD);

        System.out.println("CombatDecisionEngine: " + passed + " assertions passed.");
    }
}
