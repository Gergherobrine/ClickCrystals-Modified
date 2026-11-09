# Project Phoenix — 10-world functional and rendering QA

**Current status: NOT EXECUTED.** This is a reproducible in-game test specification. It must not be presented as proof that Minecraft was actually launched.

## Baseline and environment

Test Minecraft 1.21.11 (Java 21), 26.1.2 (Java 25) and 26.2 (Java 25) separately. Create clean launcher profiles; backup CCS scripts, user configs and keybinds. Record Fabric Loader/API, exact mods, OS, CPU/GPU, driver, RAM, Java flags, resolution, render distance, simulation distance, VSync, FPS cap, resource packs, shaders and experimental renderer settings. Compare identical world seeds/saves and settings against the prior baseline (dev.9) and candidate (dev.10).

Each world: cold load, warm-up, repeatable camera path, 3 measured sessions, GUI toggle, resize/GUI-scale change, screenshots of representative effects, Java heap and GC, frame-time P95/P99, average FPS, 1% low if the measurement method is valid. Report real crash/exception with full log and reproduction steps. Compare CPU profiling ON separately; its overhead affects results. Only claim performance improvement from repeated comparable measurements.

| # | World | Functional test | Rendering/performance focus | Actual run |
|---:|---|---|---|---|
| 1 | Superflat Clean | Fresh world; CCS parser errors, startup, script enable/disable | Baseline, HUD scale, text and blank screen | NOT RUN |
| 2 | Dense Forest | PvE observations, lost target, walking cancellations | Foliage, vegetation overdraw, resource allocation | NOT RUN |
| 3 | Mountain Terrain | Movement sensor conditions, unloaded chunks | High-distance view, camera/terrain visibility | NOT RUN |
| 4 | Underground Caves | Conditional script waits, cancellation during world unload | Lighting, particles, geometry and scissor behavior | NOT RUN |
| 5 | Nether | Anchor charge/use/switch-back with abort and item state | Lava, fire effects, dimension changes | NOT RUN |
| 6 | The End | Crystal interactions and inventory swap order | Long-range render, transparency and client overlays | NOT RUN |
| 7 | Ocean Inventory | Inventory/offhand, containers, manual slot override | Water/transparency, GUI scaling, status widgets | NOT RUN |
| 8 | Entity Stress | Sword/axe readiness, PvE entity observation | Crowded render, particles and CPU frame time | NOT RUN |
| 9 | Redstone Build Stress | Repeated world events, task cancellation and reconnect | Block entities and tick stalls | NOT RUN |
| 10 | PvP Visual Stress | Mixed-kit modules, target loss, combat state, key conflicts | HUD + GUI + overlays under simultaneous load | NOT RUN |

## Required ten functional scenarios (independent of visual worlds)

1. Clean startup with no private scripts.
2. Valid and malformed CCS loads (Unicode, comments, semicolons, incorrect file encoding).
3. Inventory, offhand and container state transitions.
4. Sword/Axe critical readiness and attack cooldown observations.
5. Crystal PvP observation and manual interaction consistency.
6. Anchor PvP multistep placement/charge/activation with safe interruption.
7. Mace and Spear mechanics only where available in exact Minecraft version.
8. PvE entities, hazards and movement-state interruption.
9. Navigation over obstacles and unloaded chunks with cancellation.
10. High-load event, task scheduler, input and multiple-script stress.

## Additional edge cases

- Disconnect/reconnect 10 cycles, dimensional transitions, rapid module toggle, opening GUI during a scripted action.
- Physical key held concurrently with CCS virtual keys, delayed release and recovery after script cancellation.
- F3+A, resource-pack reload, alt-tab, framebuffer resize, fullscreen/windowed and GUI scale 1 to maximum.
- Vanilla Fabric, compatible Sodium, Sodium+Iris, Mod Menu and other inventory/input mods, where matching versions exist.
- Compare screenshots with an identical camera position for lost geometry, texture coordinates, missing particles, font artifacts and incorrect object visibility.

## Actual evidence checklist

Record for each {Minecraft version, world, launcher, mod set, baseline/candidate}: exact source SHA; JAR hash; test date; start/end time; outcome PASS/FAIL/SKIP; screenshots; game logs; FPS/frame time measurements and conditions; Aether CPU stats when enabled; relevant test actions; observations and known remaining limitations. Keep personal information out of public logs.

**Acceptance gate:** 10 real worlds are only counted after actual Minecraft loads and logged results. Unit tests, simulated worlds, successful Gradle compilation or main-menu startup do not satisfy this requirement. A failed world must be retested after a distinct tracked code fix.
