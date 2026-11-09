package org.spongepowered.asm.mixin.injection.callback;
public class CallbackInfo {
    private boolean cancelled;
    public void cancel() { cancelled = true; }
    public boolean isCancelled() { return cancelled; }
}
