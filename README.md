# ClickCrystals Modified

**Maintained by Gergherobrine.** A Minecraft Java Edition Fabric toolkit for extensible client-side automation, PvP, PvE and survival scripting.

## Downloads / Загрузки

**Current development preview: [v1.4.5-dev.5 — Automation Runtime](../../releases/tag/v1.4.5-dev.5)**

Previous: [v1.4.5-dev.4](../../releases/tag/v1.4.5-dev.4) · [v1.4.4](../../releases/tag/v1.4.4)

| Minecraft | Java | Development branch | CI status |
| --- | --- | --- | --- |
| 1.21.11 | 21 | [dev/1.21.11](../../tree/dev/1.21.11) | Build, unit tests and client initialization passed |
| 26.1.2 | 25 | [dev/26.1.2](../../tree/dev/26.1.2) | Build, unit tests and client initialization passed |
| 26.2 | 25 | [dev/26.2](../../tree/dev/26.2) | Build, unit tests and client initialization passed |

Minecraft 26.3 is **not** included.

### What's new in 1.4.5-dev.5

- **Keyboard gestures:** `on key_tap`, `on key_hold`, `on key_held`; differentiates a short tap, threshold crossing and continued hold.
- **Game-tick tasks:** `task after`, `task every`, `task cancel`, `task cancel_all`, `task count`.
- **Conditional waiting:** `await_sense` runs a command when a client-observed condition becomes true, or terminates on timeout/error.
- **Java extension SPI:** `ext available`, `ext read`, `ext call` link CCS to trusted sensors and actions registered by installed Fabric/Java modules.
- **Navigation planning:** an incremental bounded A-star pathfinding core, designed to support future terrain adapters and path followers.
- **AI/compute foundation:** a bounded asynchronous decision bridge returns immutable-snapshot computations to the main Minecraft thread and discards stale results.
- **Runtime safety:** main-thread execution, per-script task lifecycles, recursive call guards, task limits, and isolation of source-file module registrations.
- **Existing CCS:** `sense`, `if_sense`, `unless_sense`, `sense_item`, `if_item`, `sense_slot`, `state`, `gate`, `once` and other scripting functions remain available.

**Full documentation:** [CCS_RUNTIME_DEV5.md](CCS_RUNTIME_DEV5.md) (English and Russian).

### Example — tap and hold

```ccs
def module gesture-demo
def desc "Short tap versus held key"

on key_tap v 5 then print "Tapped V"
on key_hold v 5 then task every repeated 20 then print "Holding V"
on key_release v then task cancel repeated
```

A separate **runtime-engine-demo.ccs** example is attached to the release and available in the `examples/` folder of development branches.

## Installation / Установка

1. Install Fabric Loader and the matching Fabric API for your Minecraft version.
2. Remove the original ClickCrystals and earlier Modified JARs from the `mods` directory. Both variants share the mod ID `clickcrystals`.
3. Install **one** JAR matching your Minecraft version.
4. Start Minecraft once without your own CCS scripts. Test new tasks and gestures in a local world before combining them with complex mods.

Установи Fabric Loader, подходящий Fabric API и **один JAR** под нужную версию Minecraft. Удали из `mods` старую версию и оригинальный ClickCrystals. Проверь запуск, а затем тестируй сценарии в одиночном мире.

## Architecture notes / Архитектура

The runtime provides infrastructure for sophisticated modules, **not every possible game automation feature out of the box**. Full Baritone-level navigation still requires Minecraft-specific movement, collision, chunk and path-recovery controllers. A trained model and its inference backend must be provided by an independent trusted Java extension. Minecraft server state may differ from client-observed state.

Runtime limitations protect game stability; a script cannot have unlimited recursive calls or infinitely many concurrent operations. Anticheat bypass is not guaranteed.

**Testing:**
- [Three-version Java/Gradle build and regression tests](../../actions/runs/42474779541)
- [Three-version headless Minecraft client startup with CCS sample](../../actions/runs/42474783259)
- [Bilingual development release notes](DEV_1.4.5-dev.5.md)
- [Previous sensor reference](CCS_MODIFIED_GUIDE.md)

Successful CI and startup checks do not certify all third-party mods, Android launchers or PvP gameplay.

## License / Лицензия

ClickCrystals Modified is independently maintained by **Gergherobrine** under the upstream MIT License. The original copyright notice and legally required license attribution remain in `LICENSE` and `CREDITS.txt`. Original source: ClickCrystals by its respective copyright holders.
