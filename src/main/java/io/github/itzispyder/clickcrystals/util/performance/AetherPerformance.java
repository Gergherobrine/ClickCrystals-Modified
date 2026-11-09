package io.github.itzispyder.clickcrystals.util.performance;

import java.util.concurrent.atomic.LongAccumulator;
import java.util.concurrent.atomic.LongAdder;

/**
 * Opt-in, low-allocation CPU diagnostics for ClickCrystals-owned work.
 * Measurements are inclusive; nested events or scripts may be counted twice.
 * These metrics are not GPU time and must not be reported as total frame time.
 */
public final class AetherPerformance {
    private static volatile boolean enabled = Boolean.getBoolean("clickcrystals.aether.profile");
    private static final Counter EVENTS = new Counter();
    private static final Counter CCS = new Counter();
    private static final Counter HUD = new Counter();

    private AetherPerformance() {}

    public static boolean isEnabled() {
        return enabled;
    }

    public static void setEnabled(boolean value) {
        enabled = value;
    }

    public static void recordEvent(long nanos) {
        if (enabled) EVENTS.record(nanos);
    }

    public static void recordCcs(long nanos) {
        if (enabled) CCS.record(nanos);
    }

    public static void recordHud(long nanos) {
        if (enabled) HUD.record(nanos);
    }

    public static Snapshot snapshot() {
        return new Snapshot(EVENTS.snapshot(), CCS.snapshot(), HUD.snapshot());
    }

    /** Call with sampling stopped to obtain precise reset semantics. */
    public static void reset() {
        EVENTS.reset();
        CCS.reset();
        HUD.reset();
    }

    public record Metric(long samples, long totalNanos, long maxNanos) {
        public double averageMicros() {
            return samples == 0 ? 0.0 : totalNanos / (1000.0 * samples);
        }
    }

    public record Snapshot(Metric events, Metric ccs, Metric hud) {}

    private static final class Counter {
        private final LongAdder samples = new LongAdder();
        private final LongAdder duration = new LongAdder();
        private final LongAccumulator max = new LongAccumulator(Long::max, 0L);

        void record(long nanos) {
            if (nanos < 0) return;
            samples.increment();
            duration.add(nanos);
            max.accumulate(nanos);
        }

        Metric snapshot() {
            return new Metric(samples.sum(), duration.sum(), max.get());
        }

        void reset() {
            samples.reset();
            duration.reset();
            max.reset();
        }
    }
}
