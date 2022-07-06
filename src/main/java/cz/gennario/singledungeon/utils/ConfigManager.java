package cz.gennario.singledungeon.utils;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class ConfigManager {

    private final JavaPlugin plugin;

    private final String configPath;
    private final String resourcePath;


    private File cfgFile;
    private YamlConfiguration cfg;


    public ConfigManager(JavaPlugin plugin, String configPath, String resourcePath) {
        this.plugin = plugin;
        this.configPath = configPath;
        this.resourcePath = resourcePath;

        create();
    }

    private void create() {
        if (!plugin.getDataFolder().exists()) plugin.getDataFolder().mkdir();

        cfgFile = new File(configPath);

        if (!cfgFile.exists()) {
            try {
                Files.copy(plugin.getResource(resourcePath), cfgFile.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        cfg = YamlConfiguration.loadConfiguration(cfgFile);
    }

    public YamlConfiguration get() {
        return cfg;
    }

    public void save() {
        try {
            cfg.save(cfgFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reload() {
        this.cfg = YamlConfiguration.loadConfiguration(cfgFile);
    }
}
