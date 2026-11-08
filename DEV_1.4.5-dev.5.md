# ClickCrystals Modified 1.4.5-dev.5 — Automation Runtime Preview

**Fabric: Minecraft 1.21.11 · 26.1.2 · 26.2**

**Maintainer:** Gergherobrine

## English

### Runtime rewrite

This preview adds an event-driven runtime layer to the existing ClickScript (CCS) language.

- **Debounced key gestures**: `on key_tap`, `on key_hold`, and `on key_held` detect short presses, the beginning of a hold, and repeated ticks while holding. Holding a key does not spuriously retrigger the tap event.
- **Named tick tasks**: `task after`, `task every`, `task cancel`, `task cancel_all`, and `task count`. All scheduled actions execute during game ticks, not in worker render threads.
- **Lifecycle management**: scheduled tasks are discarded when a module is disabled, scripts reload, the game disconnects, or the client stops. Scripted-module definitions are scoped to their own source files rather than a global mutable module pointer.
- **Execution guards**: up to 64 pending tasks per script, at most 32 scheduled executions per script per tick, and protection against deeply recursive script calls. `await_sense` supports `waiting`, `matched`, `timeout`, and `error` states.
- **Java plugin interface**: `ext available`, `ext read`, and `ext call` allow installed, trusted Java mods to register additional sensors and actions without modifying the CCS parser.
- **Navigation foundation**: bounded incremental A-star algorithm for plugin-supplied terrain graphs, cancellation and configurable node-expansion budgets. No game-world navigation adapter or path follower is bundled yet.
- **ML integration foundation**: bounded background compute for immutable snapshots, safe client-thread result delivery and cancellation of stale planning/inference results. No trained AI model is bundled.
- **Bug fix**: corrected the endlessly recursive `mcExecuteRunnable()` helper.

### Installation

Remove the previous Modified JAR from `.minecraft/mods/`. Install **exactly one** dev.5 JAR matching the Minecraft/Fabric version, and keep the compatible Fabric API installed. Do not install original ClickCrystals simultaneously.

### Example

```ccs
def module runtime-demo
def desc "Tap vs hold and cancellable actions"
on key_tap v 5 then print "Tap!"
on key_hold v 5 then task every notice 20 then print "Holding V"
on key_release v {
    task cancel notice
}
```

### Testing and limitations

Three independent Gradle builds, runtime unit tests, and headless client initialization checks are used. **Successful initialization does not mean every automation scenario has been tested in gameplay**. Full Baritone-grade navigation, autonomous PvE behavior, neural model runtime, and universal mod compatibility are **not yet implemented**.

Neither anticheat invisibility nor server-side action confirmation can be guaranteed. Respect server rules.

For exact command syntax and Java extension examples, see **CCS_RUNTIME_DEV5.md**, also attached to this release.

---

## Русский

### Новый движок автоматизации

В CCS добавлен самостоятельный событийный слой, пригодный не только для PvP, но и для разработки модулей выживания, навигации, строительства и PvE.

- **События клавиатуры**: `key_tap` — короткое нажатие, `key_hold` — начало удержания, `key_held` — повторение при удержании.
- **Задачи по тикам**: `task after`, `task every`, `task cancel`, `task cancel_all`, `task count`. Все действия возвращаются в игровой поток Minecraft.
- **Отмена и жизненный цикл**: при выключении модуля, перезагрузке скриптов, выходе из мира и закрытии клиента задачи очищаются. Регистрация модулей изолирована по файлам, чтобы один CCS-файл не прикреплял обработчики к чужому модулю.
- **Контроль ресурсов**: защита от бесконечной рекурсии и неограниченного количества задач, чтобы ошибочный скрипт не зависал.
- **Расширения на Java**: `ext` подключает новые действия и датчики, предоставленные установленными доверенными Fabric-модами.
- **Основа навигации**: добавлен инкрементальный поиск маршрута A* с ограничением нагрузки и отменой. Это **не готовый Baritone**: проверку столкновений, обработку мира и движение персонажа ещё предстоит подключить отдельным модулем.
- **Основа для нейросетей**: безопасное выполнение расчётов на отдельных потоках с возвратом результата в главный поток игры. Готовая нейросеть в мод не включена.
- **Исправление**: устранена бесконечная рекурсия в `mcExecuteRunnable()`.

### Установка

Удали старый JAR ClickCrystals Modified, установи **один** JAR dev.5 под свою версию Minecraft и подходящий Fabric API. Оригинал и форк одновременно устанавливать нельзя.

### Статус

Для трёх версий предусмотрены независимые CI-сборки, Java-тесты и проверка запуска. Полноценная реализация автопилота, добычи, AI-агентов и всех возможных механик ещё не завершена. Движок готов к **расширению**, но его нельзя считать безграничным или полностью протестированным во всех игровых ситуациях.

Подробный справочник: **CCS_RUNTIME_DEV5.md**. Обязательные уведомления MIT об авторах первоначального проекта сохранены в исходниках; разработчик форка — Gergherobrine.
