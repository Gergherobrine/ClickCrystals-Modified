# ClickCrystals Modified 1.4.5-dev.3 — Advanced ClickScript Preview

> Pre-release for Minecraft Java Edition **1.21.11**, **26.1.2**, and **26.2** (Fabric). The published v1.4.4 and v1.4.5-dev.2 releases are unchanged.

## English

**Maintainer:** Gergherobrine

### What's new

This development build expands ClickScript with eight new commands and a command alias, giving over 50 read-only gameplay measurements that can be combined with existing CCS macros:

- `sense` / `read_sense`: capture a game-state reading in a per-script variable.
- `if_sense`, `unless_sense`: branch on live game-state predicates.
- `sense_item`, `if_item`: inspect exact item quantities in the nine hotbar slots.
- `sense_slot`: read the item ID from one hotbar slot (1–9).
- `once`, `reset_once`: perform an action once per script state and reset the latch.

### Supported PvP and gameplay information

Health, armor, absorption, attack cooldown, fall distance, movement, sprinting, gliding, hand items and durability, totem/crystal/anchor/obsidian/mace-support supplies, basic inventory kit checks, environmental coordinates and dimensions, ping/FPS, targeted block and observed anchor charge.

`crit_candidate` and `kit_*` are **helpers**, not guarantees of critical hits or complete kit readiness. Any value read by the client may disagree with the server due to latency or plugins. Attribute swap is version-sensitive and is **not** implemented as an automatically executed exploit.

### Interface and branding

- Recentered the main-screen shortcut row for a balanced layout on different displays.
- Replaced upstream promotional links with **Modules**, **Scripts**, **HUD Editor**, and **Source Code**.
- Updated visible mod metadata to **ClickCrystals Modified**, with author **Gergherobrine**.
- Removed the legacy news/announcements and update network requests during mod initialization.
- Retained the upstream MIT legal copyright and license notices, which must not be deleted.

### Installation

1. Install Fabric Loader and the compatible Fabric API for your game version.
2. Remove the original ClickCrystals and older Modified JARs from the mods directory (shared mod ID).
3. Download and install only the JAR matching your Minecraft version.
4. Start the game without custom scripts; then test new commands in a local world.
5. Check `logs/latest.log` and `.clickcrystals/current.log` if an error occurs.

### Limitations

The available CI tests compile all three targets and verify class files and metadata. They do not certify all interactions, PvP modules, resource packs, or third-party launcher combinations. This release offers neither guaranteed exploit compatibility nor anticheat bypass.

Full command syntax and examples: **CCS_MODIFIED_GUIDE.md**, attached to the release and also available in the repository.

---

## Русский

**Разработчик форка:** Gergherobrine

### Что добавлено

В этой тестовой версии появился расширенный язык ClickScript: **8 новых команд и 1 псевдоним**, более **50 показателей** игрового состояния.

- `sense` / `read_sense` — прочитать показатель в переменную.
- `if_sense` и `unless_sense` — условие по текущему состоянию игрока и мира.
- `sense_item` и `if_item` — количество нужного предмета в девяти слотах хотбара.
- `sense_slot` — предмет в указанном слоте (1–9).
- `once` и `reset_once` — однократное выполнение команды с возможностью сброса.

Теперь скрипты могут проверять здоровье, броню, поглощение, скорость падения, готовность атаки, наличие тотемов, якорей и кристаллов, предмет в левой руке, заполненность хотбара, прицеливание на блок, заряд якоря, пинг и другие показатели.

**Важно:** `crit_candidate` лишь оценивает вероятность критического удара по движению игрока, а `kit_*` проверяет минимальные предметы. Ни одно из этих условий не предсказывает точное серверное поведение.

### Главный экран

- Четыре центрированные кнопки: **Modules**, **Scripts**, **HUD Editor**, **Source Code**.
- Убраны ссылки на сторонние рекламные страницы первоначального клиента.
- В метаданных отображается **Gergherobrine** как автор данного форка.
- Автоматическая загрузка новостей и объявлений отключена.
- Исходные юридические уведомления MIT сохранены.

### Установка

1. Установи Fabric Loader и подходящий Fabric API.
2. Удали оригинальный ClickCrystals и предыдущие версии форка из папки `mods`.
3. Установи только один JAR под свою версию игры.
4. Сначала проверь запуск игры, затем тестируй новые CCS-команды в одиночном мире.
5. Если обнаружен сбой, проверь `logs/latest.log` и `.clickcrystals/current.log`.

### Статус

Все три версии проходят независимую сборку и регрессионные тесты. Компиляция не гарантирует совместимость со всеми модами и лаунчерами. Обход серверных античитов и работа эксплойтов не гарантируются.

Полная документация: **CCS_MODIFIED_GUIDE.md**, на английском и русском.
