package io.github.itzispyder.clickcrystals.events;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/** Event handlers are indexed by exact runtime event class, preserving priority order. */
public final class EventBusCache {
    private final Map<Class<? extends Listener>, ListenerCache> data = new ConcurrentHashMap<>();

    public ListenerCache lookup(Listener listener) {
        return data.computeIfAbsent(listener.getClass(), ListenerCache::new);
    }

    public static final class ListenerCache {
        private final List<ListenerCacheEntry> all;
        private final Map<Class<?>, List<ListenerCacheEntry>> byType;

        public ListenerCache(Class<? extends Listener> listenerClass) {
            Map<Class<?>, List<ListenerCacheEntry>> index = new HashMap<>();
            List<ListenerCacheEntry> sorted = new ArrayList<>();
            Arrays.stream(listenerClass.getDeclaredMethods())
                    .filter(method -> method.getParameterCount() == 1
                            && method.getReturnType() == void.class
                            && method.isAnnotationPresent(EventHandler.class))
                    .sorted(Comparator.comparingInt(
                            (Method method) -> method.getAnnotation(EventHandler.class).priority()).reversed())
                    .forEach(method -> {
                        method.trySetAccessible();
                        var entry = new ListenerCacheEntry(method, method.getParameterTypes()[0]);
                        sorted.add(entry);
                        index.computeIfAbsent(entry.methodEventType(), ignored -> new ArrayList<>()).add(entry);
                    });
            all = List.copyOf(sorted);
            Map<Class<?>, List<ListenerCacheEntry>> copy = new HashMap<>();
            index.forEach((key, value) -> copy.put(key, List.copyOf(value)));
            byType = Map.copyOf(copy);
        }

        public void forEvent(Class<?> type, Consumer<ListenerCacheEntry> action) {
            var methods = byType.get(type);
            if (methods != null) methods.forEach(action);
        }

        public void forEach(Consumer<ListenerCacheEntry> action) {
            all.forEach(action);
        }
    }

    public record ListenerCacheEntry(Method method, Class<?> methodEventType) {}
}
