package cz.gennario.singledungeon.system;

import cz.gennario.singledungeon.Main;
import cz.gennario.singledungeon.utils.ItemSystem;
import cz.gennario.singledungeon.utils.Utils;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy;
import net.minecraft.server.network.PlayerConnection;
import org.bukkit.Bukkit;
import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftPlayer;
import org.bukkit.entity.*;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class DungeonMob {

    private final EntityType entityType;
    private final double health, damage;
    private final String name;
    private ItemStack helmet, chestplate, leggings, boots, leftHand, rightHand;

    public DungeonMob(ConfigurationSection section) {
        entityType = EntityType.valueOf(section.getString("type"));
        health = section.getDouble("health");
        damage = section.getDouble("damage");
        name = Utils.colorize(null, section.getString("name"));


        if(section.contains("helmet")) helmet = ItemSystem.itemFromConfig(section.getConfigurationSection("helmet"), null);
        if(section.contains("chestplate")) chestplate = ItemSystem.itemFromConfig(section.getConfigurationSection("chestplate"), null);
        if(section.contains("leggings")) leggings = ItemSystem.itemFromConfig(section.getConfigurationSection("leggings"), null);
        if(section.contains("boots")) boots = ItemSystem.itemFromConfig(section.getConfigurationSection("boots"), null);
        if(section.contains("leftHand")) leftHand = ItemSystem.itemFromConfig(section.getConfigurationSection("leftHand"), null);
        if(section.contains("rightHand")) rightHand = ItemSystem.itemFromConfig(section.getConfigurationSection("rightHand"), null);
    }

    public List<Integer> spawn(Player player, Location spawnLocation, int amount) {
        List<Integer> entityIds = new ArrayList<>();

        for (int i = 0; i < amount; i++) {
            Monster entity = (Monster) spawnLocation.getWorld().spawnEntity(spawnLocation, entityType);
            entity.setSilent(true);
            if(!name.isEmpty()) {
                entity.setCustomNameVisible(true);
                entity.setCustomName(name);
            }
            entity.setMaxHealth(health);
            entity.setHealth(health);
            entity.setTarget(player);
            EntityEquipment entityEquipment = entity.getEquipment();
            entityEquipment.setHelmet(helmet);
            entityEquipment.setChestplate(chestplate);
            entityEquipment.setLeggings(leggings);
            entityEquipment.setBoots(boots);
            entityEquipment.setItemInMainHand(rightHand);
            entityEquipment.setItemInOffHand(leftHand);

            PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(entity.getEntityId());
            for (DungeonGame dungeonGame : Main.getInstance().getGameMap().values()) {
                if (dungeonGame.getPlayer() != player) {
                    PlayerConnection connection = ((CraftPlayer) dungeonGame.getPlayer()).getHandle().b;
                    connection.a(packet);
                }
            }

            entityIds.add(entity.getEntityId());
        }

        return entityIds;
    }

}
