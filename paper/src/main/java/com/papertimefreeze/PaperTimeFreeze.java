package com.papertimefreeze;

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

/**
 * PaperTimeFreeze —— Paper 服务端专用插件
 *
 * 功能：服务器无人在线时，暂停昼夜循环与天气变化；有玩家在线时自动恢复。
 *
 * 实现要点：
 * 1. Paper 是单线程（主线程）模型，所有事件、任务都在主线程执行，
 *    因此可以直接在事件里操作 GameRule，无需额外的线程处理。
 * 2. 暂停 / 恢复通过 GameRule（doDaylightCycle / doWeatherCycle）实现，
 *    这是最干净、无副作用的方式。
 */
public final class PaperTimeFreeze extends JavaPlugin implements Listener {

    /** 当前在线玩家数（Paper 单线程，普通 int 即可） */
    private int onlineCount = 0;

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

        // Paper 在 onEnable 时主世界已加载，可直接按当前在线人数应用初始状态
        onlineCount = Bukkit.getOnlinePlayers().size();
        applyState(onlineCount > 0);

        getLogger().info("PaperTimeFreeze 已启用。");
        getLogger().info("无人暂停昼夜=" + pauseDaylight + "，无人暂停天气=" + pauseWeather + "。");
    }

    @Override
    public void onDisable() {
        if (restoreOnDisable) {
            setDaylightAndWeather(true);
        }
        getLogger().info("PaperTimeFreeze 已停用。");
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
        if (++onlineCount == 1) {
            applyState(true);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        // 最后一个玩家离开：从「有人」变为「无人」，暂停昼夜与天气
        if (--onlineCount == 0) {
            applyState(false);
        }
    }

    /**
     * 根据是否有人在线，应用对应的昼夜 / 天气状态。
     *
     * @param hasPlayers true 表示有玩家在线（恢复），false 表示无人（暂停）
     */
    private void applyState(boolean hasPlayers) {
        setDaylightAndWeather(hasPlayers);
        if (logEnabled) {
            getLogger().info(hasPlayers
                    ? "检测到有玩家在线，昼夜循环与天气已恢复。"
                    : "服务器无人在线，昼夜循环与天气已暂停。");
        }
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
    public boolean onCommand(CommandSender sender, Command command,
                             String label, String[] args) {
        if (args.length == 0) {
            return false;
        }
        switch (args[0].toLowerCase()) {
            case "reload" -> {
                reloadConfigValues();
                applyState(onlineCount > 0);
                sender.sendMessage("§aPaperTimeFreeze 配置已重载。");
                return true;
            }
            case "status" -> {
                sender.sendMessage("§a当前在线玩家：§f" + onlineCount);
                sender.sendMessage("§a昼夜循环：§f" + (onlineCount > 0 ? "正常运转" : "已暂停")
                        + "  §a天气：§f" + (onlineCount > 0 ? "正常运转" : "已暂停"));
                return true;
            }
            default -> {
                return false;
            }
        }
    }
}
