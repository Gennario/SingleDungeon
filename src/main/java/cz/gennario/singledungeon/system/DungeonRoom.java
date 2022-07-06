package cz.gennario.singledungeon.system;

import cz.gennario.singledungeon.Main;
import cz.gennario.singledungeon.utils.Utils;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

@Getter
@Setter
public class DungeonRoom {

    private final String id;
    private Location spawnLocation, mobSpawnLocation;
    private int prepareTime;
    private List<String> startCommands, loseCommands, winCommands;

    private TreeMap<Integer, DungeonWave> waves;

    public DungeonRoom(String id, ConfigurationSection section) {
        this.id = id;

        this.spawnLocation = Utils.getLocation(section.getString("spawn-location"));
        this.mobSpawnLocation = Utils.getLocation(section.getString("mob-spawn-location"));
        this.prepareTime = section.getInt("prepare-time");

        this.startCommands = section.getStringList("start-commands");
        this.loseCommands = section.getStringList("lose-commands");
        this.winCommands = section.getStringList("win-commands");

        this.waves = new TreeMap<>();

        int wavePosition = 1;
        for (String key : section.getConfigurationSection("waves").getKeys(false)) {
            ConfigurationSection waveSection = section.getConfigurationSection("waves."+key);

            List<DungeonGang> gangs = new ArrayList<>();
            for (String s : Main.getInstance().getGangMap().keySet()) {
                if(waveSection.getStringList("gangs").contains(s)) {
                    gangs.add(Main.getInstance().getGangMap().get(s));
                }
            }

            waves.put(wavePosition, new DungeonWave(key, waveSection.getString("name"), gangs));
            wavePosition++;
        }
    }

    public List<Integer> startWave(int wave, Player player) {
        if (waves.containsKey(wave)) {
            List<Integer> list = new ArrayList<>();

            DungeonWave dungeonWave = waves.get(wave);
            for (DungeonGang gang : dungeonWave.getGangs()) {
                list.addAll(gang.spawnMobs(player, mobSpawnLocation));
            }

            return list;
        } else {
            return null;
        }
    }

    public boolean waveExist(int wave) {
        return waves.containsKey(wave);
    }

}
