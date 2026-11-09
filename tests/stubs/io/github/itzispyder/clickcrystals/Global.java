package io.github.itzispyder.clickcrystals;

public interface Global {
    TestSystem system = new TestSystem();
    final class TestSystem {
        public void printErr(String message) { throw new AssertionError(message); }
        public void printErrF(String format, Object... args) {
            throw new AssertionError(format.formatted(args));
        }
    }
}
