# TimeFreeze

TimeFreeze is a lightweight, server-side Minecraft plugin that pauses the day/night cycle and weather while no players are online, and resumes them when players join.

- **Server-side only**
- **Paper & Folia supported**
- **No dependencies required**
- **Works out of the box** — no configuration needed

## Features

### Freeze when empty
When the last player leaves, TimeFreeze immediately pauses the day/night cycle and weather. Time and weather stop advancing until someone comes back.

### Resume when players join
When the first player joins, both game rules are re-enabled, and the world continues exactly where it left off — players never notice a gap.

## Installation

1. Download the jar that matches your server:
   - Regular single-threaded servers → `PaperTimeFreeze-*.jar`
   - Folia multithreaded servers → `FoliaTimeFreeze-*.jar`
2. Drop the jar into your server's `plugins/` folder.
3. Restart the server. The plugin works immediately — no configuration required.

Optional configuration is generated at `plugins/TimeFreeze/config.yml` on first run.

## Commands and permissions

### Paper version

| Command | Permission | Purpose |
|---|---|---|
| `/papertimefreeze` | `papertimefreeze.admin` | Main command |
| `/papertimefreeze status` | `papertimefreeze.admin` | Show online count and freeze state |
| `/papertimefreeze reload` | `papertimefreeze.admin` | Reload configuration |

Alias: `/ptf`

### Folia version

| Command | Permission | Purpose |
|---|---|---|
| `/foliatimefreeze` | `foliatimefreeze.admin` | Main command |
| `/foliatimefreeze status` | `foliatimefreeze.admin` | Show online count and freeze state |
| `/foliatimefreeze reload` | `foliatimefreeze.admin` | Reload configuration |

Alias: `/ftf`

Permissions default to server operators.

## Configuration

The plugin generates a `config.yml` on first run:

```yaml
pause-daylight: true       # freeze the day/night cycle when empty
pause-weather: true        # freeze the weather cycle when empty
restore-on-disable: true   # restore defaults when the plugin is unloaded
log: true                  # log state changes to the console
```

## Compatibility

| Version | Server | Supported MC versions |
|---|---|---|
| PaperTimeFreeze | Paper / Purpur / Spigot / all Bukkit-based servers | 1.20.x, 1.21.x, 26.1.x, 26.2.x |
| FoliaTimeFreeze | Folia | 1.20.x, 1.21.x, 26.1.x, 26.2.x |

## Support

- [Source code](https://github.com/starclimber/TimeFreeze)
- [Issue tracker](https://github.com/starclimber/TimeFreeze/issues)

## License

TimeFreeze is licensed under the [GNU General Public License v3.0](https://github.com/starclimber/TimeFreeze/blob/main/LICENSE).
