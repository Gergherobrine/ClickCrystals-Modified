# ClickCrystals Modified 1.4.5-dev.4 — ClickScript Thread Safety Hotfix

**Pre-release · Fabric: Minecraft 1.21.11, 26.1.2 and 26.2**

## English

### Fixed

The ClickScript `wait` command previously resumed execution on a background scheduler thread. A subsequent `input use` could raise:

    IllegalStateException: Tried to access render state from outside the main render thread!

Two engine paths are fixed on all three game versions:
- `WaitCmd`: validates the interval, snapshots the continuation command, and dispatches it via the Minecraft client executor (`mc.execute`).
- `ClickScript.dynamicExecutionHelper`: returns delayed multi-line continuations to the Minecraft client thread.

### AnchorDual

A corrected AnchorDual CCS script is available separately. It does not call `wait`, and instead advances through game ticks while verifying that the anchor appeared and received a charge. No 16 HP requirement or forced ninth-slot/offhand totem. Automatic camera-turn-based protection was removed because it could not guarantee safety.

**This is not guaranteed explosion-safe.** No promise is made about server rules, anticheat bypass, or compatibility with all additional mods.

### Installation

Remove the old `1.4.5-dev.3` JAR, install exactly one dev.4 JAR for your Minecraft version, and remove duplicate ClickCrystals copies. Replace the previous AnchorDual CCS file rather than loading two modules with the same ID. Disable other AnchorSwitch/AutoAnchor modules during testing.

Original upstream MIT notices remain in the source repository. Fork maintainer: **Gergherobrine**.

---

## Русский

### Исправление

Ранее отложенная команда CCS `wait` выполняла продолжение в фоновом потоке. После `wait ... func` вызов `input use` приводил к ошибке `Tried to access render state from outside the main render thread`.

Теперь `WaitCmd` и механизм отложенного выполнения многострочных команд используют `mc.execute`, передавая действия в основной поток Minecraft. Исправление внесено для **1.21.11, 26.1.2 и 26.2**.

### Новый AnchorDual

В исправленном CCS-файле вообще нет `wait`: он проверяет появление якоря и зарядку по игровым тикам с ограниченным ожиданием. Удалены обязательные 16 HP, тотемы в левой руке и девятом слоте. Автоповорот для псевдозащитной стенки удалён: **безопасность от взрыва не гарантируется**.

### Установка

Удали JAR `1.4.5-dev.3`, поставь соответствующий `1.4.5-dev.4` и замени старый AnchorDual.ccs. Не оставляй два файла с одинаковым ID модуля. На время проверки отключи другие AutoAnchor/AnchorSwitch и макросы на B. При повторении ошибки отправь `.clickcrystals/current.log`.

Лицензия MIT и обязательные уведомления правообладателей сохранены. Разработчик форка: **Gergherobrine**.
