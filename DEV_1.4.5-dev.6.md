# ClickCrystals Modified 1.4.5-dev.6 — PvP Runtime and Custom Script Fixes

**Developer / разработчик Modified:** Gergherobrine  
**Minecraft/Fabric:** 1.21.11 · 26.1.2 · 26.2  
**Java:** 21 for 1.21.11, 25 for 26.x  
**Status:** Development prerelease. Not proven anticheat-safe or universally gameplay-tested.

## English

### What changed
- **Spear Swap v3:** a dedicated `pvp lunge_swap` CCS instruction. When a base weapon's attack cooldown is charged, the client switches to a hotbar spear and issues a single attack **within the same client tick**, without waiting for spear cooldown. A deferred callback restores the original selected slot only when the player has not manually changed it. Requires a spear with Lunge for the desired Lunge effect. The underlying attribute-timing interaction is implementation/server dependent.
- **Anchor Dual v3:** a single, guarded `pvp anchor_dual` operation. It attempts one placement, waits a bounded number of ticks for an observed anchor, attempts one glowstone charge, waits for observed charge 1, then attempts one activation with a non-glowstone, non-anchor item. It aborts when targeting changes, items are missing, charge is unexpected, or the current dimension is the Nether. No sustained right-click input; no uncontrolled repetition on held B.
- **Input cleanup:** `cancelTickInputs()` now clears the synthetic pressed-state of automated buttons rather than only clearing their flags.
- **Custom Made:** scripts that exist as files but have not registered are visible as `[NOT LOADED]`; registered scripts with execution errors are labelled `SCRIPT ERROR` and can be inspected in the editor.
- **Movement sensors:** `target_velocity_x/y/z`, `target_speed`, `target_speed_horizontal`, `target_sprinting`, `target_grounded`, `target_entity_type` for the entity currently under the crosshair. Read-only, no target acquisition.
- **Modified edition attribution:** shows **Gergherobrine** as developer in the client UI instead of showing the original remotely fetched owners as the fork's team. The upstream project authorship and MIT license notices remain intact.
- Custom scripts `01_Gergherobrine_AnchorDual_v3.ccs` (B) and `02_Gergherobrine_SpearSwap_v3.ccs` (G) are attached.

### Installation
1. Choose **exactly one JAR matching your game version** from the release assets. Do not install multiple ClickCrystals variants together.
2. Put that JAR in `mods` with Fabric Loader and compatible Fabric API.
3. Delete the older Anchor Dual and Spear Swap `.ccs` variants before copying the v3 files into `.minecraft/.clickcrystals/scripts/`. The scripts require the **dev.6** JAR.
4. In-game reload scripts using the ClickCrystals reload command, or restart.
5. Test on a disposable local world first. One action per B/G key press; do not assume instant response under high ping.

### Limitations
Server-side state may differ from client-observed anchor/attribute timing. One-tick input order does not ensure the server accepts a Lunge bypass. This prerelease must not be represented as bypassing anticheats, guaranteeing safe explosions, or passing every combination of mods. The in-game video behavior was not independently reproduced.

## Русский

### Изменения
- **Spear Swap v3 / Attribute Swap:** выбор копья и одиночный удар в том же клиентском тике, без ожидания кулдауна копья; возврат исходного слота на следующем тике при условии, что игрок не переключил его вручную.
- **Anchor Dual v3:** ограниченная последовательность установки, проверки наличия якоря, однократной зарядки, проверки уровня 1 и активации. Скрипт не должен продолжать зажимать ПКМ после отпускания B, не повторяет установку без подтверждения и не работает в Нижнем мире.
- **Исправление удержания клавиш:** при отмене автоматически нажатые кнопки освобождаются.
- **Custom Made:** не загрузившиеся скрипты видны как `[NOT LOADED]`, ошибки зарегистрированных модулей отмечаются `SCRIPT ERROR`.
- **CCS-датчики:** скорость и движение сущности под прицелом для будущих PvP-модулей.
- **Разработчик форка:** Gergherobrine указан в панели информации; лицензия и авторство исходного ClickCrystals сохранены.
- Вложения: два новых скрипта Anchor Dual v3 и Spear Swap v3.

### Установка
Установить **один** файл JAR под свою версию Minecraft в папку `mods`, удалить старую версию форка. Скопировать прилагаемые `.ccs` в `.minecraft/.clickcrystals/scripts/`, удалить старые версии этих двух скриптов. Не устанавливать одновременно оригинальный ClickCrystals и Modified.

### Проверка и ограничения
Каждая версия проходит отдельную Gradle-сборку, проверку метаданных Fabric, содержимого JAR и контрольной суммы SHA-256 перед публикацией. Успешная сборка **не равна подтверждённой работе на сервере**. Работа механики Attribute Swap зависит от версии и обработки действий сервером. Никаких обещаний обхода античитов.

### File selection / Выбор файла

| Minecraft | Artifact |
| --- | --- |
| 1.21.11 | `ClickCrystals-Modified-1.21.11-1.4.5-dev.6.jar` |
| 26.1.2 | `ClickCrystals-Modified-26.1.2-1.4.5-dev.6.jar` |
| 26.2 | `ClickCrystals-Modified-26.2-1.4.5-dev.6.jar` |

**Not supported / Не поддерживается:** 26.3.

### Credits / Авторы
Fork development and maintenance: **Gergherobrine**. Original ClickCrystals authors retain mandatory attribution in LICENSE and third-party notices.
