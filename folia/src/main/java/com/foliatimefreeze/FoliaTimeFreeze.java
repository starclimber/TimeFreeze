package com.foliatimefreeze;

import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * FoliaTimeFreeze —— Folia 服务端专用插件
 *
 * 功能：服务器无人在线时，暂停昼夜循环与天气变化；有玩家在线时自动恢复。
 *
 * 实现要点（Folia 多线程兼容）：
 * 1. 在线人数用 AtomicInteger 计数，因为 PlayerJoinEvent / PlayerQuitEvent
 *    在 Folia 中可能在不同的线程触发，需要保证线程安全。
 * 2. 昼夜 / 天气属于全局状态，修改 GameRule 必须在全局线程（global region）执行，
 *    因此统一通过 GlobalRegionScheduler 调度，避免跨线程访问全局数据。
 * 3. 暂停 / 恢复通过 GameRule（doDaylightCycle / doWeatherCycle）实现，
 *    这是最干净、无副作用的方式。
 */
public final class FoliaTimeFreeze extends JavaPlugin implements Listener {

    /** 当前在线玩家数（线程安全） */
    private final AtomicInteger onlineCount = new AtomicInteger(0);

    /** 无人时是否暂停昼夜循环 */
    private boolean pauseDaylight = true;
    /** 无人时是否暂停天气变化 */
    private boolean pauseWeather = true;
    /** 插件卸载时是否恢复默认（昼夜 / 天气恢复运转） */
    private boolean restoreOnDisable = true;
    /** 是否输出状态变化日志 */
    private boolean logEnabled = true;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        reloadConfigValues();

        getServer().getPluginManager().registerEvents(this, this);

        // 记录插件启用时已经在线的人数（例如插件重载、reload 的场景）
        onlineCount.set(Bukkit.getOnlinePlayers().size());

        // 延迟几秒应用初始状态：Folia 中世界是在服务器启动稍后才完成加载的，
        // 立即遍历 Bukkit.getWorlds() 可能拿不到主世界。
        Bukkit.getGlobalRegionScheduler().runDelayed(this, task -> applyState(onlineCount.get() > 0), 40L);

        getLogger().info("FoliaTimeFreeze 已启用。");
        getLogger().info("无人暂停昼夜=" + pauseDaylight + "，无人暂停天气=" + pauseWeather + "。");
    }

    @Override
    public void onDisable() {
        if (restoreOnDisable) {
            // 卸载时把昼夜 / 天气恢复为默认（正常运转）
            setDaylightAndWeather(true);
        }
        getLogger().info("FoliaTimeFreeze 已停用。");
    }

    /** 从 config.yml 重新读取配置项 */
    private void reloadConfigValues() {
        reloadConfig();
        pauseDaylight = getConfig().getBoolean("pause-daylight", true);
        pauseWeather = getConfig().getBoolean("pause-weather", true);
        restoreOnDisable = getConfig().getBoolean("restore-on-disable", true);
        logEnabled = getConfig().getBoolean("log", true);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // 第一个玩家加入：从「无人」变为「有人」，恢复昼夜与天气
        if (onlineCount.incrementAndGet() == 1) {
            applyState(true);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        // 最后一个玩家离开：从「有人」变为「无人」，暂停昼夜与天气
        if (onlineCount.decrementAndGet() == 0) {
            applyState(false);
        }
    }

    /**
     * 根据是否有人在线，应用对应的昼夜 / 天气状态。
     *
     * @param hasPlayers true 表示有玩家在线（恢复），false 表示无人（暂停）
     */
    private void applyState(boolean hasPlayers) {
        // 全局状态必须在全局线程操作
        Bukkit.getGlobalRegionScheduler().execute(this, () -> {
            setDaylightAndWeather(hasPlayers);
            if (logEnabled) {
                getLogger().info(hasPlayers
                        ? "检测到有玩家在线，昼夜循环与天气已恢复。"
                        : "服务器无人在线，昼夜循环与天气已暂停。");
            }
        });
    }

    /**
     * 对所有已加载的世界设置昼夜 / 天气 GameRule。
     *
     * @param enabled true = 正常运转；false = 暂停
     */
    private void setDaylightAndWeather(boolean enabled) {
        for (World world : Bukkit.getWorlds()) {
            if (pauseDaylight) {
                world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, enabled);
            }
            if (pauseWeather) {
                world.setGameRule(GameRule.DO_WEATHER_CYCLE, enabled);
            }
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            return false;
        }
        switch (args[0].toLowerCase()) {
            case "reload" -> {
                reloadConfigValues();
                // 重载后按当前在线情况重新应用状态
                applyState(onlineCount.get() > 0);
                sender.sendMessage("§aFoliaTimeFreeze 配置已重载。");
                return true;
            }
            case "status" -> {
                int online = onlineCount.get();
                sender.sendMessage("§a当前在线玩家：§f" + online);
                sender.sendMessage("§a昼夜循环：§f" + (online > 0 ? "正常运转" : "已暂停")
                        + "  §a天气：§f" + (online > 0 ? "正常运转" : "已暂停"));
                return true;
            }
            default -> {
                return false;
            }
        }
    }
}
