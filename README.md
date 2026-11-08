# ClickCrystals Modified

**Maintained by Gergherobrine.** A configurable Minecraft Java Edition PvP toolkit with expanded ClickScript automation and player-directed controls.

## Downloads / Загрузки

**Current development preview:** [v1.4.5-dev.3](../../releases/tag/v1.4.5-dev.3)

**Previous releases:** [v1.4.5-dev.2](../../releases/tag/v1.4.5-dev.2) · [v1.4.4](../../releases/tag/v1.4.4)

Choose **one** Fabric JAR matching your exact Minecraft version. The original ClickCrystals and Modified share the same mod ID and should not be installed simultaneously.

| Minecraft | Java | Development source | Build |
| --- | --- | --- | --- |
| 1.21.11 | 21 | [dev/1.21.11](../../tree/dev/1.21.11) | Compiled in GitHub Actions |
| 26.1.2 | 25 | [dev/26.1.2](../../tree/dev/26.1.2) | Compiled against Minecraft 26.1.2 |
| 26.2 | 25 | [dev/26.2](../../tree/dev/26.2) | Compiled in GitHub Actions |

Minecraft 26.3 is **not** in this preview.

## New: Advanced ClickScript / Расширенный CCS

Eight commands plus an alias add over 50 read-only player, world, inventory and combat measurements.

| Function | Syntax | Purpose / Назначение |
| --- | --- | --- |
| Snapshot | `sense health hp` | Save the current health to a script variable / Прочитать здоровье |
| Read-only condition | `if_sense grounded true then print "Grounded"` | Run when the condition is true / Проверка условия |
| Negative condition | `unless_sense offhand_item eq minecraft:totem_of_undying then print "Need totem"` | Check when a condition is false |
| Inventory quantity | `sense_item minecraft:end_crystal crystals` | Count crystals in the hotbar / Посчитать кристаллы |
| Resource guard | `if_item minecraft:glowstone ge 2 then print "Ready"` | Execute only with enough items |
| Item position | `sense_slot 9 backup` | Read ninth hotbar slot / Прочитать девятый слот |
| One-shot latch | `once warning then print "Warning"` | Avoid repeated execution / Выполнить один раз |
| Reset latch | `reset_once warning` | Allow a future activation / Сбросить блокировку |

The existing `state`, `if_state` and `gate` functionality remains supported. No script automatically attacks, swaps items or claims anticheat invisibility merely by reading these sensors.

**Full English/Russian syntax and examples:** [CCS_MODIFIED_GUIDE.md](CCS_MODIFIED_GUIDE.md).

## Interface / Интерфейс

The Modified main screen uses a centered row with **Modules**, **Scripts**, **HUD Editor** and **Source Code** instead of upstream promotional shortcuts. Background news polling and unsolicited update announcements have been disabled.

### Installation / Установка

1. Install Minecraft Java Edition with Fabric Loader and a matching Fabric API.
2. Remove original ClickCrystals or earlier Modified JARs from `.minecraft/mods/`.
3. Place exactly one JAR corresponding to the Minecraft version into the mods directory.
4. Start Minecraft and verify that it initializes before enabling custom CCS modules.
5. Test any custom combat behavior in a local world first.

Скачай один JAR под свою версию Minecraft, установи Fabric Loader и подходящий Fabric API. Удали предыдущие JAR ClickCrystals из папки `mods`, затем проверь запуск без пользовательских скриптов. Перед игрой на серверах сначала протестируй конфигурацию в одиночном мире.

## Development and tests

- [Java & Fabric build verification for three versions](../../actions/workflows/build-dev3-container.yml)
- [Development release notes (EN/RU)](DEV_1.4.5-dev.3.md)
- [Minecraft client startup smoke tests](../../actions/workflows/smoke-dev3.yml)

GitHub Actions compilation verifies source and packaging, not full gameplay compatibility.

## License / Лицензия

ClickCrystals Modified is an unofficial fork of ClickCrystals, originally created by ImproperIssues and contributors. Licensed under the MIT License. The legally required original copyright notice, `LICENSE`, and `CREDITS.txt` are retained; the fork is independently maintained by **Gergherobrine**.
