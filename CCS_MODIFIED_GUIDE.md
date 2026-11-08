# ClickCrystals Modified — Advanced ClickScript Reference (1.4.5-dev.3)

**Developer preview** · Creator: **Gergherobrine** · Minecraft 1.21.11 / 26.1.2 / 26.2 (Fabric)

> These commands read information the client already knows. They **do not automatically attack, bypass anticheat, predict server-side damage, move the mouse, or guarantee an attribute-swap exploit**. Server plugins and version differences may change combat behavior.

## English

### 8 new commands (+ 1 alias)

| Command | Syntax | Purpose |
| --- | --- | --- |
| `sense` | `sense <metric> <variable>` | Save a snapshot of current game state in a per-script variable |
| `read_sense` | Same as `sense` | Alias |
| `if_sense` | `if_sense <metric> <operator> [value] then <command>` | Execute a CCS command if a live metric matches |
| `unless_sense` | Same as `if_sense` | Execute on a valid, nonmatching reading |
| `sense_item` | `sense_item minecraft:ender_crystal <variable>` | Count items in 9 hotbar slots |
| `if_item` | `if_item minecraft:glowstone ge 2 then <command>` | Conditional quantity check in 9 hotbar slots |
| `sense_slot` | `sense_slot 9 <variable>` | Read the ID of the item in hotbar position 1–9 |
| `once` | `once <variable> then <command>` | Run an action once until reset |
| `reset_once` | `reset_once <variable>` | Reset one named once-latch |

The original `state`, `if_state`, and `gate` improvements remain available.

### Live metrics (`sense`, `if_sense`, `unless_sense`)

| Group | Metric names |
| --- | --- |
| Health and survival | `health`, `max_health`, `health_pct`, `absorption`, `food`, `hunger`, `saturation`, `armor`, `air`, `xp_level` |
| Attack and movement | `fall_distance`, `velocity_y`, `attack_cooldown`, `crit_candidate`, `grounded`, `falling`, `moving`, `sprinting`, `sneaking`, `swimming`, `gliding`, `using_item`, `in_water`, `on_fire` |
| Held items | `selected_slot`, `mainhand_item`, `offhand_item`, `mainhand_count`, `offhand_count`, `mainhand_durability`, `offhand_durability` |
| Hotbar | `hotbar_empty_slots`, `totems_hotbar`, `crystals_hotbar`, `obsidian_hotbar`, `anchors_hotbar`, `glowstone_hotbar`, `wind_charges_hotbar`, `pearls_hotbar` |
| Kit readiness | `kit_crystal`, `kit_anchor`, `kit_mace`, `kit_cart` |
| Environment | `fps`, `ping`, `dimension`, `x`, `y`, `z` |
| Crosshair | `target_kind`, `target_block`, `target_anchor_charge`, `target_distance` |

**Definitions and limitations:** `health_pct` is 0–100; `attack_cooldown` is a local approximation on the 0–1 scale; `crit_candidate` only checks descending fall state and cooldown, **not all critical-hit exclusions**. `target_anchor_charge` is null when the crosshair is not on an anchor, so no condition passes. A non-damageable held item has durability `-1`; this is not a percentage. `selected_slot` and `sense_slot` use **1-based** positions. `target_distance` uses the player's eye position and the visible hit point, not collision or server reach validation.

`kit_crystal` requires crystals + obsidian + a hotbar/offhand totem. `kit_anchor` requires anchors + glowstone + a hotbar/offhand totem. `kit_mace` requires mace + wind charge or ender pearl in the hotbar. `kit_cart` requires rails + TNT minecart + bow/crossbow in the hotbar. These are **convenience inventory checks, not complete kit presets**.

### Examples

**Low-health reminder without repeated spam**

```ccs
on pre_tick 2 {
    if_sense health_pct le 35 then gate low_health 2500 then print "Low health!"
}
```

**Check for anchor resources without moving any item**

```ccs
on key_press b {
    if_sense kit_anchor true then print "Anchor kit available"
    if_item minecraft:glowstone lt 2 then print "Need more glowstone"
}
```

**Prevent an accidentally repeated action**

```ccs
once action_notice then print "Activated"
reset_once action_notice
```

**Check critical-hit candidate and actual targeted block**

```ccs
on pre_tick 2 {
    if_sense crit_candidate true then gate crit_hint 700 then print "Possible critical timing"
    if_sense target_block eq minecraft:respawn_anchor then sense target_anchor_charge anchor_charges
}
```

**Read the backup hotbar slot and count totems**

```ccs
sense_slot 9 backup
sense_item minecraft:totem_of_undying totems
if_state backup ne minecraft:totem_of_undying then print "Slot 9 is not a totem"
```

Script commands are **event-driven** and do not execute on their own. Examples are intended for local testing with ClickCrystals scripts. Other registered commands and modules still depend on the game version.

### Cross-version notes

- **1.21.11:** Yarn-mapped client access; Java 21.
- **26.1.2:** targets the actual Minecraft 26.1.2 version and matching Fabric API; Java 25.
- **26.2:** uses newer official-mapping client APIs; Java 25.
- **Attribute swapping:** an exploit that can be changed or patched. Minecraft 26.2 pre-release 3 explicitly reverted an attempted fix and said it is not supported as a game feature. Do not assume consistent exploit timing across servers or versions.

## Русский

### Новые команды

- **`sense` / `read_sense`** — сохранить текущий показатель (здоровье, кулдаун, состояние, предмет и т. д.) в переменную.
- **`if_sense` / `unless_sense`** — выполнять команду по проверке игрового состояния или при несоответствии.
- **`sense_item`** — количество указанного предмета в девяти слотах хотбара.
- **`if_item`** — проверка количества предмета без переключения слотов.
- **`sense_slot`** — получить ID предмета в конкретном слоте хотбара.
- **`once` / `reset_once`** — выполнить действие один раз и позже сбросить блокировку.

Поддерживаются более 50 показателей: здоровье, поглощение, голод, броня, скорость падения, кулдаун атаки, возможный крит, спринт, плавание, элитры, состояние рук, наличие тотемов и кристаллов, готовность базового комплекта, FPS, пинг, измерение, блок под прицелом и заряд якоря.

Проверки **не отправляют пакеты и не делают свапы сами по себе**. Для `if_sense` подходят операторы `eq`, `ne`, `gt`, `ge`, `lt`, `le`, `contains`, `starts`, `ends`, `empty`, `nonempty`, `true`, `false`.

**Пример для Crystal PvP:**

```ccs
sense_item minecraft:totem_of_undying spare_totems
if_sense offhand_item ne minecraft:totem_of_undying then print "No offhand totem"
if_item minecraft:end_crystal lt 1 then print "No crystals in hotbar"
```

**Пример для Mace PvP:**

```ccs
if_sense kit_mace true then print "Mace kit ready"
if_sense crit_candidate true then print "Possible falling critical"
```

**Ограничения:** `crit_candidate` — оценка, а не точное подтверждение критического удара. `kit_*` проверяет базовый набор предметов, а не полноту зачарований и брони. `target_anchor_charge` не вычисляет урон от взрыва и не гарантирует безопасность. Различия Minecraft и серверных античитов сохраняются.

### Исследованные механики и источники
- [Minecraft PvP guide](https://minecraft.wiki/w/Tutorial:PvP_(Java_Edition))
- [Attribute swapping mechanics](https://minecraft.wiki/w/Tutorial:Attribute_swapping)
- [Official Minecraft 26.2 pre-release 3 changelog](https://feedback.minecraft.net/hc/en-us/articles/46332172222861-Minecraft-Java-Edition-26-2-Pre-release-3)
- [Every Crystal PvP Technique Explained (video)](https://www.youtube.com/watch?v=pyw4qcLk0gQ)
- [Every Mace PvP Technique Explained (video)](https://www.youtube.com/watch?v=568an0Md-gY)

The user-facing fork is maintained by Gergherobrine. Upstream MIT copyright and licensing notices remain in the source repository as legally required.
