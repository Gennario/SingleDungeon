package cz.gennario.singledungeon.system;

import cz.gennario.singledungeon.utils.Pair;
import cz.gennario.singledungeon.utils.Utils;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class DungeonGang {

    private String name;
    private List<Pair<Integer, DungeonMob>> mobList;

    public DungeonGang(ConfigurationSection section) {
        name = Utils.colorize(null, section.getString("name"));
        mobList = new ArrayList<>();
        loadMobs(section);
    }

    public void loadMobs(ConfigurationSection section) {
        for (String key : section.getConfigurationSection("mobs").getKeys(false)) {
            ConfigurationSection mobSection = section.getConfigurationSection("mobs."+key);
            int amount = mobSection.getInt("amount");
            DungeonMob dungeonMob = new DungeonMob(mobSection);
            mobList.add(new Pair<>(amount, dungeonMob));
        }
    }

    public List<Integer> spawnMobs(Player player, Location spawnLocation) {
        List<Integer> list = new ArrayList<>();
        for (Pair<Integer, DungeonMob> mobPair : mobList) {
            list.addAll(mobPair.getValue().spawn(player, spawnLocation, mobPair.getKey()));
        }
        return list;
    }

}
