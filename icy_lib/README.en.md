# Icy Lib

> A foundational library that provides attributes, damage types, defense resolution, progression, item quality and rich-text UI systems for the icy series mods.

[简体中文](./README.md) | **English**

## Overview

**Icy Lib** is a core library mod for Minecraft 1.21.1 / NeoForge. It adds no obtainable items, blocks or mobs on its own and changes nothing in the game when installed alone. Its purpose is to provide the full set of building blocks required by a "Japanese-style RPG numeric system", so that downstream mods such as *Icy Punishing* can reuse them directly:

- A full set of custom attributes and damage types (Physical, Ice, Fire, Light, Shadow, Magic, Poison, Void, Punishing);
- A **defense resolution pipeline** (Shield → Armor → Resistance → Defense), supporting both sequential and simultaneous modes;
- A progression framework for player levels, promotions, affix upgrades and consciousness enhancement;
- Item quality ranks (2–6 stars) with matching borders, slot backgrounds and tooltip colors;
- A rich-text artistic typography system called **FancyText**;
- HUD elements and upgrade / recycle menu components.

If you only play the content, installing *Icy Punishing* (or other add-on mods) will pull this in automatically. If you are a modpack author or mod developer, you can use it as a general-purpose front-end library and reuse these systems in your own mods.

***

## Basic Information

| Item            | Content               |
| --------------- | --------------------- |
| Mod Name        | Icy Lib               |
| Mod ID          | `icy_lib`             |
| Current Version | 1.1.2                 |
| Minecraft       | 1.21.1                |
| Loader          | NeoForge 21.1.235     |
| Author          | icylemonheart         |
| License         | MIT                   |
| Dependencies    | None                  |
| Install Side    | Both client & server  |
| Type            | Core / Library mod    |

***

## Features

### 1. Attribute System

15 synced attributes across three lines — critical strike, defense and elemental resistance:

| Line              | Attributes                                                                 |
| ----------------- | -------------------------------------------------------------------------- |
| Critical Strike   | Critical Chance, Critical Damage (additive), Critical Damage (multiplier), Critical Defense |
| Defense           | Defense, Physical Resistance, Elemental Resistance, Universal Resistance  |
| Elemental Resist. | Ice, Fire, Light, Shadow, Magic, Poison, Void                             |

Attributes grow automatically with player level (when the upgrade system is in use), and can also be temporarily overridden by equipment, consciousness items and potion effects.

### 2. Damage Types

The mod registers 9 standalone damage types, so other mods can implement "elemental advantage / resistance differentiation":

`icy_physical`, `icy_ice`, `icy_fire`, `icy_light`, `icy_shadow`, `icy_magic`, `icy_poison`, `icy_void`, `icy_punishing`.

- All types share a single set of classification helpers for quick categorization;
- Built-in "pierce" tags: can bypass shields, resistances, enchantments, armor or effects;
- Punishing damage (`icy_punishing`) ignores all defense layers by default.

### 3. Defense Resolution

Incoming damage is processed in the following order:

1. **Shield layer** absorbs first;
2. **Armor layer** absorbs the remainder;
3. Divided by physical / elemental / universal resistances;
4. Defense is subtracted, clamped to a minimum of 0;
5. When the result is 0, the hit can optionally be cancelled (no health loss, no hit animation).

Highlights:

- Defense layers are extensible — other mods can register their own layers with a custom priority;
- Both "sequential" and "simultaneous" resolution modes are supported;
- Events are fired before and after resolution, allowing external mods to modify the remaining damage;
- Mobs gain a default defense of `max health × 10%` and a critical defense of `armor × 10%` (multipliers are configurable).

### 4. Status Effects (8)

| Effect ID                 | Name         | Effect                                                          |
| ------------------------- | ------------ | --------------------------------------------------------------- |
| `fortitude`               | Fortitude    | Immune to knockback; clears negative effects every second       |
| `hyper_calculation_space` | Hyper Space  | Slows nearby enemies                                            |
| `rage`                    | Rage         | Basic attack damage +20%                                        |
| `scorching`               | Scorching    | Fire resistance -15%                                            |
| `electrocution`           | Electrocution| Lightning resistance -15%                                       |
| `catherine_shield`        | Catherine's Shield | Gain 1000 shield and +15% physical damage                |
| `piercing`                | Piercing     | Physical resistance -7%                                         |
| `corrosion`               | Corrosion    | Shadow resistance -15%                                          |

### 5. Progression System

- **Levels**: up to level 80, 500,000 total EXP; each level raises health, attack, defense and crit.
- **Promotions**: 14 military ranks (Private → Sergeant → Elite → Special → Ace → Hero), consuming Nuts and unlocking attribute rewards.
- **Affix Upgrades**: upgrade attributes step by step, consuming materials directly in the UI.
- **Consciousness Enhancement**: provides independent level / EXP / attribute growth data for "consciousness" equipment.
- **Currencies & Materials**: Nuts, EXP capsules, attribute upgrade items and so on are registered by content mods; the library only provides the framework.

### 6. Quality System

Items can be assigned a 2–6 star quality, mapped to colors:

| Stars | Color  |
| ----- | ------ |
| 2★    | Green  |
| 3★    | Blue   |
| 4★    | Purple |
| 5★    | Orange |
| 6★    | Red    |

Quality affects the item border, slot background and tooltip text color.

### 7. FancyText Rich Text

The most distinctive feature of this library: you can write colored, animated text directly in item descriptions, tooltips and UI text.

- Syntax: `{{styleId}}text content{{/}}`
- 56 built-in preset styles, such as band scan, crystal shimmer, hyper space, starlight flow, etc.;
- Supports wave, pulse, rainbow, blink, opacity breathing and other animations;
- Tooltip backgrounds support solid color, image and other modes;
- Hooks into vanilla font rendering via Mixin, so other mods' text needs no changes.

### 8. UI & HUD

- Armor bar HUD and shield bar HUD;
- Hyper Space full-screen overlay (visual feedback when entering the slow state);
- Reusable button and panel styles for content mods;
- Upgrade menu and recycle menu with a pluggable screen renderer;
- Tooltip left-side portrait support (`Known incompatibility: may be overridden by other tooltip mods`).

***

## Configuration

The configuration file is located under `config/icy/`. Main options:

| Option                          | Default | Description                                                      |
| ------------------------------- | ------- | ---------------------------------------------------------------- |
| Cancel hit animation on 0 damage | off     | Don't play the hit animation when damage is fully negated        |
| Mob defense multiplier          | 0.1     | Mob defense = max health × this value                            |
| Mob critical defense multiplier | 0.1     | Mob critical defense = max health × this value                   |
| setHealth bypass handling       | off     | Whether damage that sets health directly bypasses defense        |
| Player movement speed cap       | 0.5     | Prevents the speed attribute from stacking without limit         |

***

## For Developers

Icy Lib exposes the following reusable extension points:

- **Register attributes / damage types**: call the initialization entry to register them and participate in unified damage classification.
- **Register a defense layer**: implement the defense layer interface and assign a priority to insert a third layer beyond shield / armor (e.g. "Barrier", "Energy Shield").
- **Register item quality**: `ItemQualityRegistry.register(item, rank)`
- **Register rich-text styles**: add custom `TextEffect` instances or full presets to the style registry.
- **Register progression materials**: register items as nut packs, EXP capsules or skill points to plug into the upgrade UI automatically.

> For exact API names and call patterns, refer to the `com.icylib` package in the source. Each subsystem is organized in its own package.

***

## Installation

1. Install the NeoForge version matching Minecraft 1.21.1 (21.1.235 or compatible);
2. Drop `icy_lib` into the `mods` folder;
3. Install on both client and server;
4. For the full experience, pair it with *Icy Punishing* and other add-on mods.

***

## FAQ

**Q: What content does Icy Lib add on its own?**
A: No obtainable items or blocks — it is only a library. Seeing nothing new in the creative menu when installed alone is expected.

**Q: Why do mobs become so tanky after entering a world?**
A: Mobs gain defense based on their max health by default. Lower or set to 0 the "mob defense multiplier / mob critical defense multiplier" in the config.

**Q: Will it conflict with other armor / damage-reduction mods?**
A: Defense is resolved early in the hurt event, so it usually coexists. If you see damage being reduced twice, try turning off "cancel hit animation on 0 damage" and check the reduction order of other mods.

**Q: Why is text showing colors / flashing?**
A: That is the FancyText rich-text effect — intended behavior of this library, though currently only one mod makes use of it.

***

## License

### Code License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

### Asset License

All texture files (PNG) are subject to the **CC BY-NC-SA 4.0** license.

[CC BY-NC-SA 4.0](https://creativecommons.org/licenses/by-nc-sa/4.0/)
