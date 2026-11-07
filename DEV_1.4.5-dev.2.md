# ClickCrystals Modified 1.4.5-dev.2 — Startup Hotfix

**Development preview.** Replaces the broken `1.4.5-dev.1` test build. The earlier `1.4.4` release is unchanged.

## English

### Critical fix: startup crash on version-suffixed builds
The upstream version helper mistakenly removes **both** the Minecraft version prefix and the mod's numeric version from identifiers such as `1.21.11-1.4.5-dev.1`. It leaves `dev.1`, which caused a `NumberFormatException` during static module initialization on Minecraft 1.21.11. The newer 26.x version helpers had equivalent prerelease-parsing limitations.

Changes:
- Introduced a version parser that removes only the Minecraft prefix and preserves the mod's `-dev.N` suffix.
- Made the numeric version parser explicitly read the semantic version core, with validation for invalid strings.
- Fixed the legacy 1.21.11 version comparator to handle differently sized version components.
- Added regression tests covering `1.21.11`, `26.1.2`, and `26.2` game prefixes, stable builds, prereleases, and invalid version identifiers.

### Downloads
Pick **exactly one** Fabric JAR for your Minecraft version:
- `ClickCrystals-Modified-1.21.11-1.4.5-dev.2.jar` — Minecraft 1.21.11, Java 21
- `ClickCrystals-Modified-26.1.2-1.4.5-dev.2.jar` — Minecraft 26.1.2 target, Java 25
- `ClickCrystals-Modified-26.2-1.4.5-dev.2.jar` — Minecraft 26.2, Java 25

### Installation
1. Remove the previous `1.4.5-dev.1` JAR and any original ClickCrystals JAR from your `mods` folder.
2. Install the matching dev.2 JAR; only one ClickCrystals version may be installed.
3. Keep Fabric Loader and Fabric API appropriate to the game.
4. Start the game before re-enabling custom scripts and other optional addons.
5. If Minecraft still exits, check `logs/latest.log` and `crash-reports/`, including the final `Caused by:` section.

### Development changes retained
The previous development updates to built-in Crystal PvP modules (ClickCrystal, CrystAnchor, AnchorSwitch, CrystSwitch, ObiSwitch), AutoClicker, and ClickScript `state`/`if_state`/`gate` remain included. See [detailed dev notes](DEV_1.4.5.md).

**Verification:** successful Java regression tests and three separate Fabric/Gradle compilations. A clean startup with a 163-mod Android/Zalith instance must still be tested; passing CI alone cannot guarantee compatibility with every launcher or addon.

### Legal
Unofficial derivative of ClickCrystals, MIT License. Original copyright and credits retained.

---

## Русский

### Критическое исправление вылета при запуске
В оригинальном обработчике версий регулярное выражение удаляло слишком большую часть номера `1.21.11-1.4.5-dev.1`. Вместо `1.4.5-dev.1` получалось `dev.1`, а при попытке разобрать строку в Java возникала `NumberFormatException` уже при инициализации мода. У веток 26.x также была проблема с суффиксами `-dev`.

Исправления:
- Отделено имя версии Minecraft от реального номера мода.
- Теперь корректно обрабатываются суффиксы `-dev.1`, `-dev.2` и стабильные номера без суффикса.
- Исправлено сравнение номеров версий разной длины для Minecraft 1.21.11.
- Добавлены отдельные автоматические тесты парсера для трёх целевых версий Minecraft.

### Установка
1. Удали старый `1.4.5-dev.1.jar` и оригинальный ClickCrystals из папки `mods`.
2. Скачай **один** JAR версии `dev.2` для своей версии игры.
3. Убедись, что установлены подходящие Fabric Loader и Fabric API.
4. Сначала запусти игру без дополнительных пользовательских CCS-скриптов.
5. Если сбой остался, открой `logs/latest.log` и `crash-reports/` — особенно последние строки и `Caused by:`.

### Остальные изменения
Улучшения встроенных Java-модулей для Crystal PvP, исправления AutoClicker и расширения CCS сохранены. Подробнее — в [заметках разработчика](DEV_1.4.5.md).

**Проверено:** тесты Java и компиляция трёх отдельных JAR. Запуск на Android через Zalith со всеми 163 установленными модами не подтверждён: его необходимо проверить отдельно.

**Авторство:** неофициальный форк ClickCrystals под MIT. Оригинальные авторы и обязательные уведомления сохранены.
