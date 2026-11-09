import io.github.itzispyder.clickcrystals.util.performance.AetherPerformance;

public final class AetherPerformanceTest {
    private static void ok(boolean value, String name) {
        if (!value) throw new AssertionError(name);
    }

    public static void main(String[] ignored) {
        AetherPerformance.setEnabled(false);
        AetherPerformance.reset();
        AetherPerformance.recordEvent(1000);
        ok(AetherPerformance.snapshot().events().samples() == 0, "disabled mode");
        AetherPerformance.setEnabled(true);
        AetherPerformance.recordEvent(1000);
        AetherPerformance.recordEvent(3000);
        AetherPerformance.recordCcs(2500);
        AetherPerformance.recordHud(600);
        var snap = AetherPerformance.snapshot();
        ok(snap.events().samples() == 2, "event count");
        ok(snap.events().totalNanos() == 4000, "event total");
        ok(snap.events().maxNanos() == 3000, "event max");
        ok(Math.abs(snap.events().averageMicros() - 2.0) < 0.0001, "event average");
        ok(snap.ccs().samples() == 1 && snap.ccs().totalNanos() == 2500, "CCS count");
        ok(snap.hud().samples() == 1, "HUD count");
        AetherPerformance.setEnabled(false);
        AetherPerformance.reset();
        ok(AetherPerformance.snapshot().events().samples() == 0, "reset");
        System.out.println("AetherPerformanceTest: PASS");
    }
}
