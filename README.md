# TimeFreeze

> 无人时冻结 Minecraft 服务器的时间与天气，有玩家进入后自动恢复。

![Version](https://img.shields.io/badge/version-1.0.0-blue)
![API](https://img.shields.io/badge/API-1.20--26.2-green)
![Paper](https://img.shields.io/badge/Paper-supported-brightgreen)
![Folia](https://img.shields.io/badge/Folia-supported-brightgreen)
![Java](https://img.shields.io/badge/Java-17%2B-orange)

一个轻量级的 Minecraft 服务器插件，解决"空服时时间白白流逝"的痛点：服务器无人在线时自动冻结昼夜循环与天气变化，有玩家进入后立即恢复正常运转。

> English: [README_EN.md](README_EN.md) | 日本語: [README_JA.md](README_JA.md)

## 功能特性

### ⏸️ 无人时自动冻结
最后一个玩家离开服务器后，插件立即暂停昼夜循环与天气变化——时间不再流逝、天气不再变化，世界就像被按下了暂停键。

### ▶️ 有人时自动恢复
第一个玩家进入服务器后，昼夜与天气立即恢复正常运转，世界从离开时的状态无缝继续，玩家感受不到任何停顿。

## 兼容性

| 版本 | 服务端 | 支持 MC 版本 |
|------|--------|-------------|
| PaperTimeFreeze | Paper / Purpur / Spigot / Bukkit 系 | 1.20.x · 1.21.x · 26.1.x · 26.2.x |
| FoliaTimeFreeze | Folia | 1.20.x · 1.21.x · 26.1.x · 26.2.x |

> 两个 jar 均以最低版本 API 编译、字节码为 Java 17，可运行于 Java 17/21/25 环境。

## 安装

1. 根据你的服务端类型，下载对应的 jar（见 [Releases](../../releases)）：
   - 普通单线程服务端 → `PaperTimeFreeze-*.jar`
   - Folia 多线程服务端 → `FoliaTimeFreeze-*.jar`
2. 将 jar 文件放入服务器的 `plugins/` 目录。
3. 启动服务器，插件会自动生成配置文件 `plugins/TimeFreeze/config.yml`（按需修改）。
4. 修改配置后执行重载命令即可生效，无需重启服务器。

## 配置

插件首次启动会自动生成 `config.yml`，全部配置项如下：

```yaml
# 无人时是否暂停昼夜循环（doDaylightCycle）
pause-daylight: true

# 无人时是否暂停天气变化（doWeatherCycle）
pause-weather: true

# 插件卸载/重载时是否恢复默认（昼夜、天气恢复运转）
restore-on-disable: true

# 是否在控制台输出状态变化日志
log: true
```

| 配置项 | 默认值 | 说明 |
|--------|--------|------|
| `pause-daylight` | `true` | 无人时是否冻结昼夜循环 |
| `pause-weather` | `true` | 无人时是否冻结天气变化 |
| `restore-on-disable` | `true` | 插件卸载时是否恢复昼夜/天气运转 |
| `log` | `true` | 是否在控制台打印状态变化日志 |

## 命令与权限

### Paper 版（PaperTimeFreeze）

| 命令 | 权限 | 说明 |
|------|------|------|
| `/papertimefreeze` | `papertimefreeze.admin` | 主命令 |
| `/papertimefreeze status` | `papertimefreeze.admin` | 查看在线人数与昼夜/天气状态 |
| `/papertimefreeze reload` | `papertimefreeze.admin` | 重载配置文件 |

别名：`/ptf`（权限默认授予 OP）

### Folia 版（FoliaTimeFreeze）

| 命令 | 权限 | 说明 |
|------|------|------|
| `/foliatimefreeze` | `foliatimefreeze.admin` | 主命令 |
| `/foliatimefreeze status` | `foliatimefreeze.admin` | 查看在线人数与昼夜/天气状态 |
| `/foliatimefreeze reload` | `foliatimefreeze.admin` | 重载配置文件 |

别名：`/ftf`（权限默认授予 OP）

## 编译

### 环境要求
- JDK 17 或更高
- Maven 3.6 或更高

### 编译步骤

```bash
# 编译 Paper 版
cd paper
mvn clean package
# 产物：paper/target/PaperTimeFreeze-1.0.0.jar

# 编译 Folia 版
cd folia
mvn clean package
# 产物：folia/target/FoliaTimeFreeze-1.0.0.jar
```

### 依赖说明

两个子项目均以最低版本 API 编译，保证向上兼容：

| 版本 | Maven 依赖 | 仓库 |
|------|-----------|------|
| Paper 版 | `io.papermc.paper:paper-api:1.20.1` | `https://repo.papermc.io/repository/maven-public/` |
| Folia 版 | `dev.folia:folia-api:1.20.1` | `https://repo.papermc.io/repository/maven-public/` |

## 技术特点

- **无副作用实现**：通过切换 `doDaylightCycle` / `doWeatherCycle` 两个 GameRule 实现"冻结/恢复"，不修改服务器时间数据。
- **多线程安全**：Folia 版严格遵循区域化多线程规范，使用 `AtomicInteger` 计数 + `GlobalRegionScheduler` 操作全局状态。
- **轻量零依赖**：无任何第三方依赖，单个 jar 即装即用。
- **跨版本稳定**：仅依赖 Bukkit 最核心稳定的 API，长期无需维护。

## 常见问题

**Q: 为什么服务器没人的时候时间不动了？**
A: 这是插件的核心功能——最后一名玩家离开后自动冻结昼夜循环，有玩家进入即恢复。

**Q: 天气也会被冻结吗？**
A: 会。`pause-weather` 开启时，无人期间天气状态不再变化。注意：如果最后一名玩家离开时正在下雨，雨会一直下到有人回来（这是"冻结"而非"停雨"）。

**Q: Paper 和 Folia 版怎么选？**
A: 看你的服务端类型。普通的 Paper / Purpur / Spigot 服选 Paper 版；Folia 多线程服选 Folia 版。

**Q: 支持哪些 Minecraft 版本？**
A: 1.20.x、1.21.x、26.1.x、26.2.x。

**Q: 修改配置后需要重启吗？**
A: 不需要，执行 `/ptf reload`（Paper 版）或 `/ftf reload`（Folia 版）即可。

## 支持与反馈

- 问题反馈：欢迎在 [Issues](../../issues) 中提出
- 觉得好用的话点个 Star ⭐

## 开源协议

Copyright (C) 2026.

本项目使用 [GNU General Public License v3.0](LICENSE) 开源。
