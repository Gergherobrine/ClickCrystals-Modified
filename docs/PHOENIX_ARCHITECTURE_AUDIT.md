# Phoenix — architecture audit and engineering risk register

Date: 2026-11-09. Target branches: phoenix/dev10-12111, phoenix/dev10-2612 and phoenix/dev10-262.

**Scope declaration:** The complete 1,516-file cumulative Java path inventory was generated separately in PHOENIX_SOURCE_INVENTORY.md. This report contains targeted inspection of critical source classes, build workflows and the main subsystem relationships. It is not a line-by-line security review of each Java file or an in-game verification.

## Source architecture examined

| Area | Actual source points | Observed responsibility |
|---|---|---|
| Client/bootstrap | ClickCrystals.java; client/ClickCrystalsClient.java; fabric.mod.json | Fabric initialization and metadata |
| Modules and persistence | client/system/ClickCrystalsSystem.java; modules/Module.java; client/system/Config.java | Module registration, configs and sessions |
| Events | events/EventBus.java; events/EventBusCache.java; events/EventHandler.java | Exact-event listeners; reflection |
| Input | events/listeners/TickEventListener.java; scripting/runtime/InputLeaseManager.java | User/script input and generation tickets |
| CCS source and parser | scripting/ScriptParser.java; scripting/ScriptComments.java; scripting/ScriptSourceReader.java | Source loading, preprocessing and command syntax |
| CCS execution | scripting/ClickScript.java; scripting/runtime/TickTaskQueue.java; scripting/runtime/ScriptExecutionBudget.java | Script commands, async continuation, limits |
| Extensions | scripting/runtime/ScriptExtensions.java | Trusted sensor/action registry |
| Async work | scripting/runtime/AsyncDecisionBridge.java | Bounded workers and main-thread result delivery |
| Navigation | scripting/runtime/nav/IncrementalPathfinder.java | Incremental path calculation, not movement execution |
| Inventory | scripting/runtime/PlayerInventorySlotMap.java; util/minecraft/InvUtils.java | Slot mapping and client interactions |
| Graphics | gui/hud/Hud.java; gui/hud/TextHud.java; util/minecraft/render/; gui/screens/ | HUD and existing per-version rendering primitives |
| Performance diagnostics | util/performance/AetherPerformance.java; client/commands/commands/DebugCommand.java | Opt-in CPU measurements, not GPU timing |

## Verified fixes in Phoenix dev.10 (automated CI only)

- **CCS source read (P1):** ScriptParser.readFile previously swallowed exceptions and returned an empty script. ScriptSourceReader now uses UTF-8, closes files, propagates IO failures with context, and has regression tests.
- **Event dispatch (P2):** EventBus formerly walked every listener method and called setAccessible on every event. The new indexed cache resolves exact event type once per listener class; isolated tests preserve priority, private handler execution, unsubscribe and cancellable semantics.
- **Idle script queue allocation (P2):** TickTaskQueue now increments ticks and returns early if no work exists; historical scheduler tests are retained.
- **HUD bounds (dev.9):** GUI geometry and fullscreen overlay use appropriate outward-rounded bounds/scaled coordinates.
- **Aether CPU profiler (experimental):** Counts samples and durations of EventBus, CCS command execution and TextHud. Disabled by default, switched by debug aether commands. This is a profiling tool, not a benchmark demonstrating faster FPS.

## Confirmed or plausible remaining risks

- **P1 / release gate:** No verified Minecraft client launch or 10 real-world scenarios across supported versions. CI build does not establish playability.
- **P2:** TickEventListener.onTickStart catches Exception with an empty handler. Failures may be hidden; a bounded diagnostic with a targeted test is required.
- **P2:** ClickScript.currentFile is a shared AtomicReference and DEFAULT_DISPATCHER is static. Evaluate re-entry and multiple script lifecycles, reconnection and concurrent callbacks before changing.
- **P2:** InputLeaseManager provides generation tickets but not a complete cross-module and physical-key arbiter.
- **P2:** AsyncDecisionBridge is a bounded compute facility. It does not constitute a trained, loadable and validated AI model.
- **P2:** The pathfinder is not a complete world-navigation, movement and recovery engine.
- **P2:** Actual server confirmation of inventory and attacks is not guaranteed by local events.
- **P2:** Sodium/Iris/Android compatibility, text quality and accurate GPU-frame metrics have not been tested.
- **P2:** Aether profiling counts inclusive nested work; adding categories may require exclusive timing and sampling.
- **P3:** Static review of every source class remains incomplete, even though every path is inventoried. Prioritize high-impact classes by runtime reachability and user-reported defects.

## Dependency-safe development order

1. Close remaining CCS parser, input and lifecycle regressions; run unit and integration tests.
2. Make inventory action and combat observation contracts explicit, with server-state distinctions.
3. Implement deterministic Combat/PvE action coordinators before autonomous strategy and ML.
4. Benchmark real CPU/GPU and FPS bottlenecks. Introduce Aether render backend adapters only for demonstrated needs; preserve fallback compatibility.
5. Keep neural inference optional, isolated from Minecraft threads and absent from default JAR without a measured reason.
6. Complete ten-world tests for each version and repeated startup/reconnect/GUI-scale test rounds.
7. Publish a new developer-only draft only when gates pass. Stable v1.4.4 and original authorship/LICENSE stay unchanged.

## Verification record

- GitHub Actions phoenix-dev10-verify: isolated runtime, source reader, event dispatch, Aether profiler and GUI geometry tests; Gradle builds for 1.21.11/26.1.2/26.2.
- Gameplay, AI training and FPS improvements: **NOT VERIFIED**.
- Explicit original attribution required: ImproperIssues/ItziSpyder original, Gergherobrine unofficial fork maintainer. Preserve MIT copyright.

**This document is a first technical audit, not the completion of the entire Phoenix master prompt.**
