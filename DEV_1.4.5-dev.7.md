# ClickCrystals Modified 1.4.5-dev.7 — CCS Comment Parser Hotfix

Developer of Modified edition: **Gergherobrine**.

Minecraft/Fabric versions: **1.21.11, 26.1.2, 26.2** (no 26.3).

## English

### Fixed

Two scripts included in dev.6 could fail immediately on startup:

- Anchor Dual v3: `'holding' is not a value of [...] `
- Spear Swap v3: `'no' is not a value of [...] `

**Root cause:** ClickScript `ScriptParser.readFile` concatenated `//` comment lines before separating commands on semicolons. Comments such as `// ...; holding B ...` and `// ...; no auto-enchanting` were accidentally interpreted as executable commands.

The **dev.7 engine** now removes whole-line and trailing `//` comments before parsing. Quoted strings and normal URL tokens such as `https://` remain intact. The same normalization is applied to dynamic script input.

### Additional verification
- Standalone `ScriptCommentsSmokeTest` reproduces both reported cases and covers quoted strings, URLs, and inline comments.
- Build pipeline compiles all three versions independently and verifies the new `ScriptComments` and `PvpCmd` classes inside each JAR.
- Clean **Anchor Dual v4** and **Spear Swap v4** script files contain no comment text that could be mistakenly executed.
- All changes introduced in dev.6 remain, including the PvP runtime, Custom Made error indicators, movement sensors, creator panel and input cleanup.

### Installation
1. Remove all previous ClickCrystals Modified JAR files from `mods`; install **exactly one** dev.7 JAR appropriate to your Minecraft game version.
2. Remove older `01_Gergherobrine_AnchorDual_v3.ccs` and `02_Gergherobrine_SpearSwap_v3.ccs` (and any duplicate scripts defining the same modules) from `.minecraft/.clickcrystals/scripts/`.
3. Copy `01_Gergherobrine_AnchorDual_v4.ccs` and `02_Gergherobrine_SpearSwap_v4.ccs` from these release assets to that folder.
4. Restart Minecraft. Test in a disposable world before PvP.

The new example scripts also avoid the original comment errors in dev.6. However, use dev.7 for the engine-level fix.

**Important:** This release addresses the CCS parsing failures. It does not certify the in-game behavior, safety, or anticheat compatibility of Anchor Dual and Attribute Swap on every server.

## Русский

### Исправления

В dev.6 обнаружены ошибки загрузки двух скриптов:

- Anchor Dual v3: **`'holding' is not a value ...`**.
- Spear Swap v3: **`'no' is not a value ...`**.

**Причина:** парсер CCS объединял текст строк, не удалив комментарии `//`. После символа `;` оставшаяся часть комментария ошибочно считалась отдельной командой.

В **dev.7** исправлен парсер `ScriptParser`: комментарии удаляются до обработки команд; строки в кавычках и обычные ссылки `https://` сохраняются.

Добавлен тест на обе ошибки и выпущены чистые файлы **Anchor Dual v4** и **Spear Swap v4**. Изменения PvP-движка из dev.6 сохранены.

### Установка

- Удалить прежний JAR ClickCrystals Modified из `mods` и установить **только один** JAR dev.7 под свою версию.
- Удалить прежние Anchor Dual v3 / Spear Swap v3 и любые дубли модулей из папки `.minecraft/.clickcrystals/scripts/`.
- Скопировать туда **Anchor Dual v4** и **Spear Swap v4** из файлов этого релиза.
- Перезапустить игру. Проверить сначала в отдельном мире.

**Ограничение:** это подтверждённое исправление разбора комментариев, а не гарантия правильного нанесения урона/взрыва или работы Attribute Swap на любом сервере.

## Files / Файлы

- `ClickCrystals-Modified-1.21.11-1.4.5-dev.7.jar` — Java 21
- `ClickCrystals-Modified-26.1.2-1.4.5-dev.7.jar` — Java 25
- `ClickCrystals-Modified-26.2-1.4.5-dev.7.jar` — Java 25
- `01_Gergherobrine_AnchorDual_v4.ccs`
- `02_Gergherobrine_SpearSwap_v4.ccs`
- `SHA256SUMS.txt`

Upstream ClickCrystals authors are credited through the retained license and project notices.
