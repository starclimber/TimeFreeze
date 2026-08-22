# TimeFreeze

> Automatically pauses the daylight cycle and weather when no players are online, and resumes them when players join.

![Version](https://img.shields.io/badge/version-1.0.0-blue)
![API](https://img.shields.io/badge/API-1.20--26.2-green)
![Paper](https://img.shields.io/badge/Paper-supported-brightgreen)
![Folia](https://img.shields.io/badge/Folia-supported-brightgreen)
![Java](https://img.shields.io/badge/Java-17%2B-orange)

A lightweight Minecraft server plugin that solves the problem of "time passing while nobody is online": it freezes the day/night cycle and weather when the server is empty, and resumes them as soon as a player joins.

> 中文: [README.md](README.md) | 日本語: [README_JA.md](README_JA.md)

## Features

### ⏸️ Freeze When Empty
When the last player leaves, the plugin immediately pauses the day/night cycle and weather. Time and weather stop advancing until someone comes back.

### ▶️ Resume When Players Join
When the first player joins, both game rules are re-enabled, and the world continues exactly where it left off — players never notice a gap.

## Compatibility

| Version | Server | Supported MC versions |
|---------|--------|-----------------------|
| PaperTimeFreeze | Paper / Purpur / Spigot / all Bukkit-based servers | 1.20.x · 1.21.x · 26.1.x · 26.2.x |
| FoliaTimeFreeze | Folia | 1.20.x · 1.21.x · 26.1.x · 26.2.x |

> Both jars are compiled against the lowest API version with Java 17 bytecode, and run on Java 17/21/25.

## Installation

1. Download the jar that matches your server (see [Releases](../../releases)):
   - Regular single-threaded servers → `PaperTimeFreeze-*.jar`
   - Folia multithreaded servers → `FoliaTimeFreeze-*.jar`
2. Drop the jar into your server's `plugins/` folder.
3. Start the server. The plugin generates `plugins/TimeFreeze/config.yml` automatically (edit if needed).
4. After editing, run the reload command — no server restart required.

## Configuration

The plugin generates a `config.yml` on first run:

```yaml
# Freeze the day/night cycle when empty (doDaylightCycle)
pause-daylight: true

# Freeze the weather cycle when empty (doWeatherCycle)
pause-weather: true

# Restore defaults when the plugin is unloaded
restore-on-disable: true

# Log state changes to the console
log: true
```

| Option | Default | Description |
|--------|---------|-------------|
| `pause-daylight` | `true` | Freeze the day/night cycle when empty |
| `pause-weather` | `true` | Freeze the weather cycle when empty |
| `restore-on-disable` | `true` | Restore day/night and weather when the plugin is unloaded |
| `log` | `true` | Log state changes to the console |

## Commands and Permissions

### Paper version (PaperTimeFreeze)

| Command | Permission | Description |
|---------|------------|-------------|
| `/papertimefreeze` | `papertimefreeze.admin` | Main command |
| `/papertimefreeze status` | `papertimefreeze.admin` | Show online count and freeze state |
| `/papertimefreeze reload` | `papertimefreeze.admin` | Reload configuration |

Alias: `/ptf` (permission defaults to operators)

### Folia version (FoliaTimeFreeze)

| Command | Permission | Description |
|---------|------------|-------------|
| `/foliatimefreeze` | `foliatimefreeze.admin` | Main command |
| `/foliatimefreeze status` | `foliatimefreeze.admin` | Show online count and freeze state |
| `/foliatimefreeze reload` | `foliatimefreeze.admin` | Reload configuration |

Alias: `/ftf` (permission defaults to operators)

## Building

### Requirements
- JDK 17 or higher
- Maven 3.6 or higher

### Build steps

```bash
# Build the Paper version
cd paper
mvn clean package
# Output: paper/target/PaperTimeFreeze-1.0.0.jar

# Build the Folia version
cd folia
mvn clean package
# Output: folia/target/FoliaTimeFreeze-1.0.0.jar
```

### Dependencies

Both subprojects compile against the lowest API version for upward compatibility:

| Version | Maven dependency | Repository |
|---------|-----------------|------------|
| Paper | `io.papermc.paper:paper-api:1.20.1` | `https://repo.papermc.io/repository/maven-public/` |
| Folia | `dev.folia:folia-api:1.20.1` | `https://repo.papermc.io/repository/maven-public/` |

## Technical Notes

- **No side effects**: freezing/resuming is done by toggling the `doDaylightCycle` / `doWeatherCycle` game rules — no server time data is modified.
- **Thread-safe**: the Folia version follows the regionized multithreading model, using `AtomicInteger` for counting and `GlobalRegionScheduler` for global state access.
- **Lightweight & dependency-free**: no third-party dependencies — a single jar works out of the box.
- **Stable across versions**: relies only on the most stable core Bukkit APIs (player events + game rules), so it rarely needs maintenance.

## FAQ

**Q: Why does time stop when nobody is online?**
A: That's the core feature — the plugin freezes the day/night cycle when the last player leaves and resumes it when someone joins.

**Q: Is weather frozen too?**
A: Yes. When `pause-weather` is enabled, weather stops changing while the server is empty. Note: if it's raining when the last player leaves, the rain continues until someone returns (this is "freezing", not "clearing").

**Q: Which version should I use, Paper or Folia?**
A: It depends on your server. Regular Paper / Purpur / Spigot servers use the Paper version; Folia multithreaded servers use the Folia version.

**Q: Which Minecraft versions are supported?**
A: 1.20.x, 1.21.x, 26.1.x, 26.2.x.

**Q: Do I need to restart after changing the config?**
A: No. Run `/ptf reload` (Paper) or `/ftf reload` (Folia).

## Support

- Bug reports: welcome in [Issues](../../issues)
- If you find this useful, give it a Star ⭐

## License

Copyright (C) 2026.

This project is licensed under the [GNU General Public License v3.0](LICENSE).
