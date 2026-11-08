# ClickCrystals Modified — Runtime Engine 1.4.5-dev.5

Maintainer: **Gergherobrine** · Fabric: **1.21.11 / 26.1.2 / 26.2**

This is a development runtime update. It is not the complete implementation of Baritone, a JavaScript VM or a neural network. Existing ClickScript syntax and gameplay modules are retained.

## Architecture / Архитектура

The runtime supports deterministic game-tick scheduling, keyboard-gesture classification, per-script state, bounded recursion and an extension API for trusted Java modules.

**Thread rule:** Minecraft state and interactions run on the Minecraft client thread. Computationally heavy algorithms should operate on immutable snapshots, never the live world from worker threads.

**Lifecycle:** named script tasks are cancelled on module disable, script reload, client exit and world disconnection. Repeat tasks are never allowed to create threads. Recursive calls are guarded to prevent stack exhaustion.

**Resource safety:** 64 named tasks per script, maximum 32 due executions per tick per script, maximum 32 nested calls, gesture thresholds limited to 1–100 game ticks. The limits exist to prevent freezing or crashing a player's client; they are not anticheat presets.

## New CCS commands / Новые команды

| CCS syntax | English | Русский |
| --- | --- | --- |
| `task after NAME 10 then COMMAND` | One action 10 ticks later | Одно действие через 10 тиков |
| `task every NAME 5 then COMMAND` | Repeat every 5 ticks | Повторять каждые 5 тиков |
| `task cancel NAME` | Stop a named task | Остановить задачу |
| `task cancel_all` | Stop every task owned by the script | Остановить все задачи скрипта |
| `task count VAR` | Save active-task count to state | Число активных задач в переменную |
| `on key_tap v 5 then COMMAND` | Short press, measured at release | Короткое нажатие V |
| `on key_hold v 5 then COMMAND` | One event after five held ticks | Событие после пяти тиков удержания |
| `on key_held v 5 2 then COMMAND` | Repeat every two ticks while holding | Повторять каждые два тика удержания |
| `ext available nav:goto support` | Check whether a Java plugin provides a function | Проверить наличие расширения |
| `ext read nav:status status` | Copy a Java sensor reading into CCS state | Прочитать датчик Java-расширения |
| `ext call nav:goto 120 64 -30` | Invoke a trusted registered Java action | Вызвать действие Java-расширения |

**Important:** `nav:goto` and `nav:status` are illustrative extension IDs. This build does not include a finished navigation controller. Another installed Java module must register those actions/sensors before they will work.

### Example: short tap vs hold

```ccs
def module gesture-demo
def desc "Differentiates short and held key presses"

on key_tap v 5 then print "Tapped V"
on key_hold v 5 then print "Started holding V"
on key_held v 5 3 then print "Still holding V"
```

### Example: cancellable tasks

```ccs
def module timed-demo
def desc "A cancellable tick-based script"

on key_tap b 5 then task after delayed_notice 20 then print "One second passed"
on key_tap n 5 then task cancel delayed_notice
on key_hold b 5 then task every repeating_notice 10 then print "Repeating"
on key_release b then task cancel repeating_notice
```

Unlike older `wait` code, these tasks never wake on background threads and can be cancelled by name. All times are in **game ticks**; the duration depends on actual game tick cadence.

## Java extension interface / Расширение Java

Trusted Fabric mods can register their own actions and sensors without changing the CCS parser:

```java
import io.github.itzispyder.clickcrystals.scripting.runtime.ScriptExtensions;

// Call during the extension's client initialization.
ScriptExtensions.registerSensor("example:mode", ctx -> "ready");
ScriptExtensions.registerAction("example:checkpoint", (ctx, args) -> {
    ctx.writeState("checkpoint", args.isEmpty() ? "unset" : args.get(0));
});
```

Then the CCS script can use:

```ccs
ext available example:checkpoint installed
if_state installed true then ext call example:checkpoint base
ext read example:mode plugin_status
```

Callbacks execute on the client thread. Installed Java extensions are trusted code and must use their own permissions, cancellation logic and dependency compatibility checks. The runtime does not download or execute external source or JAR files.

### Navigation and agent preparation

`runtime.nav.IncrementalPathfinder` supplies a **bounded incremental A-star search** independent of Minecraft's mapping names. A game-specific adapter must still provide collision/passability, hazards, jump/fall costs, world loading, obstacles, and a path follower. A complete Baritone-like navigation system would also need route invalidation, stuck recovery, chunk streaming, inventory management, terrain modification and goal persistence.

A planner implementation can run a small number of node expansions per client tick, return a route, and be cancelled without blocking game rendering. This creates a foundation for survival automation, building, mining, PvE and PvP routing, but it does not itself move the player.

For ML inference, a trusted Java module can register an action that consumes model outputs, but should run compute on immutable data off the game thread and post validated actions back to the client thread. No neural model is bundled.

## Version compatibility

- **Minecraft 1.21.11:** Yarn mappings, Java 21.
- **Minecraft 26.1.2:** official unmapped game classes, Java 25.
- **Minecraft 26.2:** 26.2 client API, Java 25, including different GUI accessor.
- **26.3:** deliberately excluded.

## Validation

Continuous integration compiles the three Fabric variants, verifies packaged classes, and runs pure Java regression tests for task scheduling, key gestures, registered extension calls and incremental A-star search.

Successful compilation is not a substitute for gameplay tests in Zalith or for testing against third-party mods. No feature guarantees bypassing server-side anticheat checks.

This derivative retains the legally required upstream MIT attribution and license notices. All user-facing work is maintained under **Gergherobrine**.
