import io.github.itzispyder.clickcrystals.events.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public final class EventBusDispatchTest {
    static void ok(boolean condition, String msg) { if (!condition) throw new AssertionError(msg); }
    static final class First extends Event {}
    static final class Second extends Event {}
    static final class Cancel extends Event implements Cancellable {
        private boolean cancelled;
        public void setCancelled(boolean value) { cancelled = value; }
        public boolean isCancelled() { return cancelled; }
    }
    static final class Probe implements Listener {
        StringBuilder order = new StringBuilder();
        @EventHandler(priority = EventPriority.HIGH)
        private void high(First event) { order.append("H"); }
        @EventHandler(priority = EventPriority.LOW)
        private void low(First event) { order.append("L"); }
        @EventHandler private void other(Second event) { order.append("S"); }
        @EventHandler private void cancel(Cancel event) { event.cancel(); }
    }
    public static void main(String[] args) {
        EventBus bus = new EventBus();
        Probe probe = new Probe();
        bus.subscribe(probe); bus.subscribe(probe);
        ok(!bus.pass(new First()), "noncancelled");
        ok(probe.order.toString().equals("HL"), "priority order and private handlers");
        bus.pass(new Second());
        ok(probe.order.toString().equals("HLS"), "event type dispatch");
        CallbackInfo info = new CallbackInfo();
        bus.passWithCallbackInfo(info, new Cancel());
        ok(info.isCancelled(), "cancellable callback");
        ok(bus.listeners().size() == 1, "duplicate listener");
        bus.unsubscribe(probe);
        bus.pass(new First());
        ok(probe.order.toString().equals("HLS"), "unsubscribe");
        System.out.println("EventBusDispatchTest: PASS");
    }
}
