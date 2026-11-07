# ClickCrystals Modified 1.4.4 — Release Notes / Примечания к выпуску

> **Release candidate documentation.** This file does not mean that v1.4.4 has been published.
> **Документация для подготовки релиза.** Этот файл не означает, что версия 1.4.4 уже выпущена.

## English

### Overview
ClickCrystals Modified is an independent fork of ClickCrystals, distributed under the original MIT license.
The goal of v1.4.4 is a consistent scripting environment and independent Minecraft version builds.

### Planned downloads
Choose **only** the JAR matching your Minecraft Java Edition version:
- `ClickCrystals-Modified-1.21.11-1.4.4.jar` — Minecraft 1.21.11 (Fabric; Java 21)
- `ClickCrystals-Modified-26.1.2-1.4.4.jar` — Minecraft 26.1.2 (Fabric; Java 25)
- `ClickCrystals-Modified-26.2-1.4.4.jar` — Minecraft 26.2 (Fabric; Java 25)
- `ClickCrystals-Modified-26.3-1.4.4.jar` — Minecraft 26.3 (Fabric; Java 25)

**These filenames are targets, not currently verified artifacts.** Do not publish downloads until each has passed build and game-start tests.

### Installation
1. Install the Fabric Loader for the exact game version.
2. Install a matching Fabric API.
3. Remove any older ClickCrystals JAR from your `mods` directory; do not run the original and the fork together (they share internal IDs).
4. Copy exactly one matching ClickCrystals Modified JAR into `.minecraft/mods/`.
5. Launch Minecraft, review the mod's settings, and test custom scripts in a local world first.

### Changes currently committed on the 26.2 development branch
- Introduced independent per-script `state` variables and `if_state` comparisons.
- Introduced `gate` to limit repeated command execution.
- Fixed `getMacro()` checking the wrong map.
- Removed the first-open Discord invitation and Discord shortcut from the main screen.
- Disabled automatic bulletin fetching; retained the upstream MIT license.

### Compatibility and security
No claim is made that the client bypasses server anticheat systems. Respect server rules.
No guaranteed release-ready builds exist until CI and in-game tests finish.

### Credits and license
Based on ClickCrystals © 2023 ImproperIssues and its contributors, MIT License.
This is an unofficial derivative, not an official upstream release. Keep the upstream `LICENSE` and `CREDITS.txt`.

---

## Русский

### Обзор
ClickCrystals Modified — независимый форк ClickCrystals под оригинальной лицензией MIT.
Цель версии 1.4.4 — единая система создания пользовательских модулей и отдельные сборки для разных версий Minecraft.

### Планируемые файлы
Выбирай **только** JAR под свою версию Minecraft Java Edition:
- `ClickCrystals-Modified-1.21.11-1.4.4.jar` — Minecraft 1.21.11 (Fabric; Java 21)
- `ClickCrystals-Modified-26.1.2-1.4.4.jar` — Minecraft 26.1.2 (Fabric; Java 25)
- `ClickCrystals-Modified-26.2-1.4.4.jar` — Minecraft 26.2 (Fabric; Java 25)
- `ClickCrystals-Modified-26.3-1.4.4.jar` — Minecraft 26.3 (Fabric; Java 25)

**Это названия запланированных файлов, а не подтверждённые готовые сборки.** Не публикуй JAR до успешной сборки и запуска в игре.

### Установка
1. Установи Fabric Loader для нужной версии Minecraft.
2. Установи совместимый Fabric API.
3. Удали старый JAR ClickCrystals из папки `mods`: оригинал и форк одновременно запускать нельзя (внутренние идентификаторы совпадают).
4. Перенеси один подходящий JAR ClickCrystals Modified в `.minecraft/mods/`.
5. Запусти игру, проверь настройки и сначала тестируй пользовательские скрипты в одиночном мире.

### Реализованные изменения в ветке 26.2
- Переменные `state` с изолированным состоянием каждого скрипта и сравнения `if_state`.
- `gate` для ограничения частоты повторных команд.
- Исправлена ошибка поиска макросов через `getMacro()`.
- Убраны приглашение в Discord при первом открытии и ссылка на Discord в главном экране.
- Отключена автоматическая загрузка объявлений; лицензия MIT сохранена.

### Совместимость
Гарантий обхода античитов нет. Соблюдай правила серверов. Релиз нельзя считать готовым до CI и игровых тестов.

### Авторы и лицензия
Основано на ClickCrystals © 2023 ImproperIssues и работах участников проекта, MIT License.
Это неофициальная модификация. Файлы `LICENSE` и `CREDITS.txt` должны сохраняться.
