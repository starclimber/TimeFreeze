# TimeFreeze

> サーバーに誰もいない間、昼夜サイクルと天気を自動的に停止し、プレイヤーが参加すると再開します。

![Version](https://img.shields.io/badge/version-1.0.0-blue)
![API](https://img.shields.io/badge/API-1.20--26.2-green)
![Paper](https://img.shields.io/badge/Paper-supported-brightgreen)
![Folia](https://img.shields.io/badge/Folia-supported-brightgreen)
![Java](https://img.shields.io/badge/Java-17%2B-orange)

サーバーが空の間、昼夜サイクルと天気を自動的に停止する軽量な Minecraft サーバープラグインです。プレイヤーが参加するとすぐに通常動作を再開します。

> 中文: [README.md](README.md) | English: [README_EN.md](README_EN.md)

## 機能

### ⏸️ 無人時に停止
最後のプレイヤーが退出すると、昼夜サイクルと天気を即座に停止します。時間も天気も進まなくなり、世界が一時停止された状態になります。

### ▶️ プレイヤー参加時に再開
最初のプレイヤーが参加すると、昼夜と天気がすぐに通常動作を再開し、退出時の状態からシームレスに続きます。

## 互換性

| バージョン | サーバー | 対応 MC バージョン |
|-----------|---------|------------------|
| PaperTimeFreeze | Paper / Purpur / Spigot / すべての Bukkit 系 | 1.20.x · 1.21.x · 26.1.x · 26.2.x |
| FoliaTimeFreeze | Folia | 1.20.x · 1.21.x · 26.1.x · 26.2.x |

> 両方の jar は最低バージョンの API でコンパイルされた Java 17 バイトコードで、Java 17/21/25 で動作します。

## インストール

1. サーバーの種類に合った jar をダウンロードします（[Releases](../../releases) 参照）：
   - 通常のシングルスレッドサーバー → `PaperTimeFreeze-*.jar`
   - Folia マルチスレッドサーバー → `FoliaTimeFreeze-*.jar`
2. jar をサーバーの `plugins/` フォルダに配置します。
3. サーバーを起動すると `plugins/TimeFreeze/config.yml` が自動生成されます（必要に応じて編集）。
4. 設定変更後はリロードコマンドを実行すれば反映されます。サーバー再起動は不要です。

## 設定

初回起動時に `config.yml` が自動生成されます：

```yaml
# 無人時に昼夜サイクルを停止するか（doDaylightCycle）
pause-daylight: true

# 無人時に天気の変化を停止するか（doWeatherCycle）
pause-weather: true

# プラグイン無効化時にデフォルトを復元するか
restore-on-disable: true

# 状態変化をコンソールに出力するか
log: true
```

| 設定項目 | デフォルト | 説明 |
|---------|-----------|------|
| `pause-daylight` | `true` | 無人時に昼夜サイクルを停止 |
| `pause-weather` | `true` | 無人時に天気の変化を停止 |
| `restore-on-disable` | `true` | プラグイン無効化時に昼夜/天気を復元 |
| `log` | `true` | 状態変化をコンソールに出力 |

## コマンドと権限

### Paper 版（PaperTimeFreeze）

| コマンド | 権限 | 説明 |
|---------|------|------|
| `/papertimefreeze` | `papertimefreeze.admin` | メインコマンド |
| `/papertimefreeze status` | `papertimefreeze.admin` | オンライン人数と状態を表示 |
| `/papertimefreeze reload` | `papertimefreeze.admin` | 設定をリロード |

エイリアス：`/ptf`（権限はデフォルトで OP）

### Folia 版（FoliaTimeFreeze）

| コマンド | 権限 | 説明 |
|---------|------|------|
| `/foliatimefreeze` | `foliatimefreeze.admin` | メインコマンド |
| `/foliatimefreeze status` | `foliatimefreeze.admin` | オンライン人数と状態を表示 |
| `/foliatimefreeze reload` | `foliatimefreeze.admin` | 設定をリロード |

エイリアス：`/ftf`（権限はデフォルトで OP）

## ビルド

### 要件
- JDK 17 以上
- Maven 3.6 以上

### ビルド手順

```bash
# Paper 版をビルド
cd paper
mvn clean package
# 出力：paper/target/PaperTimeFreeze-1.0.0.jar

# Folia 版をビルド
cd folia
mvn clean package
# 出力：folia/target/FoliaTimeFreeze-1.0.0.jar
```

### 依存関係

両方のサブプロジェクトは最低バージョンの API でコンパイルされ、上位互換性を保ちます：

| バージョン | Maven 依存 | リポジトリ |
|-----------|-----------|-----------|
| Paper 版 | `io.papermc.paper:paper-api:1.20.1` | `https://repo.papermc.io/repository/maven-public/` |
| Folia 版 | `dev.folia:folia-api:1.20.1` | `https://repo.papermc.io/repository/maven-public/` |

## 技術的な特徴

- **副作用なし**：`doDaylightCycle` / `doWeatherCycle` の GameRule を切り替えるだけで「停止/再開」を実現し、サーバーの時間データは変更しません。
- **スレッドセーフ**：Folia 版はリージョン化マルチスレッド仕様に準拠し、`AtomicInteger` と `GlobalRegionScheduler` を使用します。
- **軽量・依存なし**：サードパーティ依存がなく、単一の jar で即動作します。
- **バージョン間で安定**：Bukkit の最も安定したコア API のみに依存し、長期間メンテナンス不要です。

## よくある質問

**Q: 誰もいないと時間が止まるのはなぜ？**
A: これがコア機能です。最後のプレイヤーが退出すると昼夜サイクルを停止し、参加すると再開します。

**Q: 天気も停止されますか？**
A: はい。`pause-weather` が有効な場合、サーバーが空の間は天気が変化しません。なお、最後のプレイヤーが退出した時に雨が降っていた場合、その雨は誰かが戻るまで降り続けます（「停止」であり「晴れにする」ではありません）。

**Q: Paper 版と Folia 版のどちらを使えばいい？**
A: サーバーの種類によります。通常の Paper / Purpur / Spigot サーバーは Paper 版、Folia マルチスレッドサーバーは Folia 版を使用してください。

**Q: 対応している Minecraft のバージョンは？**
A: 1.20.x、1.21.x、26.1.x、26.2.x です。

**Q: 設定変更後に再起動は必要？**
A: 不要です。`/ptf reload`（Paper 版）または `/ftf reload`（Folia 版）を実行してください。

## サポート

- バグ報告：[Issues](../../issues) で受け付けています
- 役に立ったら Star をお願いします ⭐

## ライセンス

Copyright (C) 2026.

本プロジェクトは [GNU General Public License v3.0](LICENSE) で公開されています。
