package io.github.itzispyder.clickcrystals.events;

import io.github.itzispyder.clickcrystals.Global;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

public class EventBus implements Global {

    private final ConcurrentLinkedQueue<Listener> subscribedListeners;
    private final EventBusCache listenersCache;

    public EventBus() {
        this.subscribedListeners = new ConcurrentLinkedQueue<>();
        this.listenersCache = new EventBusCache();
    }

    public void subscribe(Listener listener) {
        if (listener == null || subscribedListeners.contains(listener))
            return;
        subscribedListeners.add(listener);
    }

    public void unsubscribe(Listener listener) {
        subscribedListeners.remove(listener);
    }

    /**
     * Passes an event
     * @param event event
     * @return true if cancelled, false if not
     * @param <E> event type
     */
    public <E extends Event> boolean pass(E event) {
        if (event == null) return false;
        Class<?> eventType = event.getClass();
        subscribedListeners.forEach(listener ->
                listenersCache.lookup(listener).forEvent(eventType,
                        method -> tryInvoke(method, listener, event)));
        return event instanceof Cancellable c && c.isCancelled();
    }

    public <C extends CallbackInfo, E extends Event> void passWithCallbackInfo(C info, E event, Consumer<CallbackInfo> action) {
        pass(event);
        if (event instanceof Cancellable c && c.isCancelled()) {
            action.accept(info);
        }
    }

    public <C extends CallbackInfo, E extends Event> void passWithCallbackInfo(C info, E event) {
        passWithCallbackInfo(info, event, CallbackInfo::cancel);
    }

    private <E extends Event> void tryInvoke(EventBusCache.ListenerCacheEntry methodCache, Listener listener, E event) {
        Method method = methodCache.method();
        try {
            method.invoke(listener, event);
        }
        catch (Exception ex) {
            Throwable cause = ex.getCause() == null ? ex : ex.getCause();
            system.printErrF("EventBus handler failed: method=%s listener=%s event=%s cause=%s",
                    method.getName(), listener.getClass().getSimpleName(),
                    event.getClass().getSimpleName(), cause);
        }
    }

    public Set<Listener> listeners() {
        return new HashSet<>(subscribedListeners);
    }
}
