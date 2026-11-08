# Icy Lib

A core library mod for Minecraft NeoForge 1.21.1 providing utilities and attributes for icy series mods.

## Features

- **Custom Damage Types**: Physical, Ice, Fire, Light, Shadow, Magic, Poison, Punishing, and Void damage types
- **Attributes System**: Critical strike, defense, critical strike defense and resistance attributes
- **Quality System**: Rank-based item slot background and tooltip rendering with per-rank gradient colors
- **Fancy Text Tooltips**: Rich text tooltip effects, including custom backgrounds and fonts
- **Defense System**: Layered defense framework where shields (SHIELD) are drained before armor (ARMOR), with extensible layer priorities and bypass hooks
- **Shield / Armor System**: Shield and armor management with HUD display
- **Dodge System**: Generic perfect-dodge window API — cancels incoming living-attacker damage during the window and fires a callback
- **Player Upgrade System**: Level, EXP, nuts and promotion stages (Private → Hero) with per-stage attribute values, plus skill points
- **Network Layer**: Serverbound/clientbound payloads for player upgrade actions and client-side upgrade cache sync
- **Effects**: Various effects including Rage, Scorching, Electrocution, Piercing, Corrosion, Fortitude, Hyper Calculation Space, and Catherine Shield
- **Upgrade System**: Framework for item upgrading and breakthrough
- **Menu System**: Upgrade and recycle menus, shared GUI widgets and style presets
- **Migration Protection API**: Keeps downstream `ADD_MULTIPLIED_TOTAL` modifiers in the independent multiply zone

## Requirements

- Minecraft NeoForge 1.21.1 (21.1.235)
- Java 21

## Building

```bash
./gradlew build
```

## Installation

1. Build the mod using `./gradlew build`
2. Copy `build/libs/icy_lib-1.1.2.jar` to your NeoForge mod folder
3. Launch Minecraft with NeoForge

## License

### Code License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

### Asset License

All texture files (PNG) are subject to the **CC BY-NC-SA 4.0** license.

[CC BY-NC-SA 4.0](https://creativecommons.org/licenses/by-nc-sa/4.0/)
