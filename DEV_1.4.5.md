# ClickCrystals Modified 1.4.5-dev.1 / Development preview

> Development branches only. The published **v1.4.4** release and its JAR files are unchanged. This preview is not a stable release.

## English

### Scope
This development cycle targets the three Fabric editions: Minecraft **1.21.11**, **26.1.2**, and **26.2**. The focus is predictable, player-directed interactions rather than automatic attacks or hidden packet manipulation. Server policy and anticheat compatibility cannot be guaranteed.

### Java module changes
- **ClickCrystal:** only redirects a deliberate click when the current crosshair hit matches the actual clicked crystal base.
- **CrystSwitch:** requires the aimed block to match the player's visible target and a crystal to be available before switching.
- **CrystAnchor:** checks anchor and glowstone availability before consuming the original interaction.
- **AnchorSwitch:** switches to glowstone after placing an anchor; returns to the anchor only after charging has been observed in the world while still aimed at that anchor. It does **not** automatically detonate an anchor.
- **ObiSwitch:** corrects the required hotbar item check (obsidian, not end crystal), uses a per-instance cooldown, and only interacts with the current target.
- **AutoClicker:** replaces an unbounded randomized scheduling loop (which could hang when probability was zero) with a bounded 20-tick distribution. Also prevents an initial null movement-position check.

The other original modules are retained but **not** audited or rewritten in this development build.

### Script commands
The `state` command supports:

| Category | Operations |
| --- | --- |
| Variables | `set`, `copy`, `clear`, `reset`, `show` |
| Numbers | `inc`, `dec`, `add`, `sub`, `mul`, `div`, `mod`, `min`, `max`, `clamp` |
| Strings | `append`, `length` |
| Booleans | `toggle` |

`if_state` supports `eq`, `ne`, `gt`, `ge`, `lt`, `le`, `contains`, `starts`, `ends`, `empty`, `nonempty`, `true`, and `false`. `gate` limits repetitions using a per-script monotonic cooldown.

Example:

```text
state set step 0
state inc step
if_state step ge 1 then print "Ready"
gate action 200 then print "Allowed"
```

### Design and safety
- Script variables are local to each script, capped at 128 names with 256 characters per value.
- Invalid numeric operations and divisions by zero are rejected.
- No arbitrary external code execution is added.
- Behavior should be confirmed in a local world before multiplayer use. Compile success is not the same as gameplay testing.

### Build verification
The `dev-validate.yml` GitHub Actions workflow compiles each version separately and runs `ScriptStateSmokeTest`. Its artifacts are development builds, not official Releases.

## Русский

### Область работы
Эта версия разрабатывается отдельно для **Minecraft 1.21.11**, **26.1.2** и **26.2** (Fabric). Основной принцип — предсказуемая реакция на действия игрока без скрытых атак и подмены его намерений. Отсутствие флагов античита гарантировать нельзя.

### Изменения встроенных модулей
- **ClickCrystal:** установка только по реально наведённому блоку при ручном нажатии.
- **CrystSwitch:** проверка совпадения цели и наличия кристалла.
- **CrystAnchor:** проверка наличия якоря и светокамня до переключения.
- **AnchorSwitch:** возврат к якорю после подтверждения зарядки; автоматического подрыва больше нет.
- **ObiSwitch:** исправлена проверка обсидиана и логика ограничения повторов.
- **AutoClicker:** устранён потенциально бесконечный цикл распределения кликов; исправлена возможная проверка движения при пустом предыдущем положении.

Другие исходные модули сохранены. **Полный аудит и переработка всех модулей пока не выполнены.**

### Новые возможности CCS
У `state` появились арифметика, строки, булевы значения и копирование переменных. У `if_state` появились проверки на пустое значение, содержимое строки и новые сравнения. `gate` предотвращает чрезмерно частые повторные вызовы.

Ограничения по состоянию: до 128 переменных на скрипт, значение — до 256 символов. Деление на ноль и некорректные значения отклоняются.

### Проверка
Каждая версия собирается отдельно в GitHub Actions; тест состояния `ScriptStateSmokeTest` выполняется без Minecraft. Перед использованием на серверах необходима проверка в одиночном мире. Эти файлы — тестовые сборки, не новый стабильный релиз.
