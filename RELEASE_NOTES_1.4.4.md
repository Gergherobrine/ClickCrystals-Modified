# ClickCrystals Modified 1.4.4

## English

### About this release
ClickCrystals Modified 1.4.4 is an **unofficial preview release** of a Fabric PvP utility mod, derived from ClickCrystals. It provides three separately compiled builds; Minecraft 26.3 is not supported in this release.

### Downloads — choose exactly one
| File | Minecraft Java Edition | Java |
| --- | --- | --- |
| `ClickCrystals-Modified-1.21.11-1.4.4.jar` | 1.21.11 | 21 |
| `ClickCrystals-Modified-26.1.2-1.4.4.jar` | 26.1.2 | 25 |
| `ClickCrystals-Modified-26.2-1.4.4.jar` | 26.2 | 25 |

The 26.1.2 build is derived from the upstream 26.1 series. It compiles successfully but has **not been smoke-tested in-game on 26.1.2**. All three builds passed automated Gradle compilation, not complete gameplay testing.

### Changes
- Introduced per-script variables: `state set`, `state add`, `state clear`, and `state reset`.
- Added `if_state` conditions and `gate` to reduce unintended repeated command execution.
- Removed automatic Discord invitations and prominent Discord shortcuts from the main UI.
- Updated branding and Gradle build identifiers.
- Corrected the macro lookup condition on the 26.2 branch.
- Preserved original modules and the upstream MIT license. Some optional upstream components and remote integrations remain and may receive further work.

### Installation
1. Install Minecraft Java Edition, Fabric Loader, and the matching Fabric API for your game version.
2. Remove the original ClickCrystals JAR and older copies of ClickCrystals Modified from the `mods` folder. The fork still uses the original internal mod ID, so the two versions must not run together.
3. Download the **one** JAR matching your Minecraft version.
4. Place the JAR in `.minecraft/mods/`, start the game, and check the mods list.
5. Test scripts and PvP modules in a local world before joining multiplayer servers.
6. The `SHA256SUMS.txt` file can be used to verify the downloaded JAR files.

### Notes
This is marked as a **pre-release** because the three game builds have not undergone in-game smoke tests. No claim is made that any automation is undetectable by anti-cheat systems. Follow the rules of each server.

### Attribution and license
Based on ClickCrystals, © 2023 ImproperIssues and contributors, under the MIT License. This is an unofficial modification, not endorsed by the upstream maintainers. The original `LICENSE` and `CREDITS.txt` are retained in the source repository.

---

## Русский

### О выпуске
ClickCrystals Modified 1.4.4 — **предварительный релиз** неофициального форка ClickCrystals для Fabric. Подготовлены три независимые сборки. Minecraft 26.3 в этот выпуск не входит.

### Загрузки — выбирай только один файл
| Файл | Minecraft Java Edition | Java |
| --- | --- | --- |
| `ClickCrystals-Modified-1.21.11-1.4.4.jar` | 1.21.11 | 21 |
| `ClickCrystals-Modified-26.1.2-1.4.4.jar` | 26.1.2 | 25 |
| `ClickCrystals-Modified-26.2-1.4.4.jar` | 26.2 | 25 |

Вариант 26.1.2 основан на серии 26.1 исходного проекта. Он успешно компилируется, но **не проходил проверку запуска в Minecraft 26.1.2**. Все три сборки проходят автоматическую компиляцию Gradle, а не полноценные игровые испытания.

### Изменения
- Добавлено состояние скриптов: команды `state set`, `state add`, `state clear`, `state reset`.
- Добавлены `if_state` и `gate` для условий и предотвращения лишних повторных действий.
- Убраны автоматическое приглашение в Discord и заметные Discord-ссылки из основного интерфейса.
- Обновлены название проекта и имена сборок.
- В версии 26.2 исправлена ошибка поиска макросов.
- Сохранены исходные модули и лицензия MIT. Некоторые дополнительные сетевые возможности оригинала ещё требуют отдельной переработки.

### Установка
1. Установи Minecraft Java Edition, Fabric Loader и подходящую Fabric API.
2. Удали из папки `mods` оригинальный ClickCrystals и прежние версии ClickCrystals Modified. У форка всё ещё исходный внутренний mod ID: одновременно они работать не должны.
3. Скачай **только один** JAR под свою версию Minecraft.
4. Перенеси его в `.minecraft/mods/`, запусти игру и проверь наличие мода.
5. Сначала проверь пользовательские скрипты и PvP-модули в одиночном мире.
6. При необходимости проверь контрольные суммы JAR по файлу `SHA256SUMS.txt`.

### Важно
Релиз обозначен как **предварительный**, потому что успешная сборка не подтверждает работу всех модулей в игре. Гарантий обхода античитов нет. Учитывай правила сервера.

### Авторы и лицензия
Основано на ClickCrystals, © 2023 ImproperIssues и участниках проекта, лицензия MIT. Это неофициальная модификация без одобрения авторов оригинального проекта. Исходные `LICENSE` и `CREDITS.txt` сохраняются в репозитории.
