package cz.gennario.singledungeon;

import cz.gennario.singledungeon.commands.StartCommand;
import cz.gennario.singledungeon.system.DungeonGame;
import cz.gennario.singledungeon.system.DungeonGang;
import cz.gennario.singledungeon.system.DungeonMob;
import cz.gennario.singledungeon.system.DungeonRoom;
import cz.gennario.singledungeon.utils.ConfigManager;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public final class Main extends JavaPlugin {

    private static Main instance;
    private ConfigManager configManager;

    private Map<String, DungeonGang> gangMap;
    private Map<String, DungeonRoom> roomMap;
    private Map<Player, DungeonGame> gameMap;
    private List<Integer> gameMobs;

    @Override
    public void onEnable() {
        instance = this;

        configManager = new ConfigManager(this, getDataFolder() + "/config.yml/", "config.yml");

        gangMap = new ConcurrentHashMap<>();
        for (String key : configManager.get().getConfigurationSection("gangs").getKeys(false)) {
            gangMap.put(key, new DungeonGang(configManager.get().getConfigurationSection("gangs." + key)));
        }
        roomMap = new ConcurrentHashMap<>();
        for (String key : configManager.get().getConfigurationSection("rooms").getKeys(false)) {
            roomMap.put(key, new DungeonRoom(key, configManager.get().getConfigurationSection("rooms." + key)));
        }

        gameMap = new ConcurrentHashMap<>();
        gameMobs = new ArrayList<>();

        Bukkit.getPluginManager().registerEvents(new Events(), this);
        getCommand("start").setExecutor(new StartCommand());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static Main getInstance() {
        return instance;
    }
}
