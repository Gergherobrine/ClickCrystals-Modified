# ClickCrystals Modified

An independent, MIT-licensed derivative of [ClickCrystals](https://github.com/clickcrystals-development/ClickCrystals). Designed for configurable Minecraft PvP workflows and an improved ClickScript scripting environment.

**Development status:** 1.4.4 preparation — **no stable 1.4.4 release yet**. Do not confuse a development branch with a tested JAR.

## Minecraft versions

| Minecraft | Source branch | Upstream foundation | Status |
| --- | --- | --- | --- |
| 1.21.11 | [mc/1.21.11](../../tree/mc/1.21.11) | ClickCrystals 1.3.9 | Compiled in GitHub Actions; runtime test pending |
| 26.1.2 | [mc/26.1.2](../../tree/mc/26.1.2) | ClickCrystals 1.4.0 (26.1 series) | Build verification pending |
| 26.2 | [mc/26.2](../../tree/mc/26.2) | ClickCrystals 1.4.3 + Modified CCS state | Build verification pending |
| 26.3 | [mc/26.3](../../tree/mc/26.3) | 26.2 source as porting base | Compile failed: incompatible 26.3 APIs |

The development branch for 26.3 is *not* presently a compatible 26.3 binary; it requires source migration and runtime tests.

## What's implemented

On `mc/26.2`, ClickScript gains:
- `state set <name> <value>`, `state add <name> <number>`, `state clear <name>`, `state reset`;
- `if_state <name> <op> <value> then <command>` with `eq`, `ne`, `gt`, `ge`, `lt`, `le`;
- `gate <name> <milliseconds> then <command>` for guarded repetitions;
- a macro lookup fix and removal of unsolicited Discord invitations.

**These 26.2 changes have not yet been ported to the other version branches.**

## Builds & release

[Build verification](../../actions/workflows/build-targets.yml) checks four branches independently.
[Draft release preparation](../../actions/workflows/prepare-release.yml) runs only on manual request. It requires all four JAR builds to pass and prepares a **draft**, not a published release.

[Release notes (English and Russian)](RELEASE_NOTES_1.4.4.md)

## Installation (English)

Install the compatible Fabric Loader and Fabric API, put **one** matching `.jar` in your `.minecraft/mods` directory, and remove the original ClickCrystals JAR before launching. Verify version compatibility and game startup; compilation alone is not sufficient.

## Установка (Русский)

Установи подходящий Fabric Loader и Fabric API, помести **один** JAR нужной версии в `.minecraft/mods/`. Перед запуском удали оригинальный ClickCrystals, чтобы избежать конфликта одинакового mod ID. Одной успешной компиляции недостаточно: нужен запуск и проверка в игре.

## License and attribution

Original project: ClickCrystals © 2023 ImproperIssues and contributors. Licensed under MIT. This repository preserves the upstream `LICENSE` and `CREDITS.txt`. This derivative is not officially endorsed by the original project.
