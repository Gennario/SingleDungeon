package cz.gennario.singledungeon.utils;

import jline.internal.Nullable;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public final class ItemSystem {

    public static ItemStack itemFromConfig(ConfigurationSection section, @Nullable Player player) {

        Material material = Material.valueOf(section.getString("material"));
        int amount = 1;
        if (section.getString("amount") != null) amount = section.getInt("amount");
        if (section.getString("dynamic-amount") != null) amount = Integer.parseInt(Utils.colorize(player, section.getString("dynamic-amount")));
        byte data = 0;
        if (section.getString("data") != null) data = (byte) section.getInt("data");
        String name = null;
        if (section.getString("name") != null) name = Utils.colorize(player, section.getString("name"));
        List<String> lore = new ArrayList<>();
        if (section.getString("lore") != null) {
            for (String line : section.getStringList("lore")) {
                lore.add(Utils.colorize(player, line.replace('&', '§')));
            }
        }
        List<String> enchants = new ArrayList<>();
        if (section.getString("enchants") != null) {
            enchants.addAll(section.getStringList("enchants"));
        }
        List<String> itemFlags = new ArrayList<>();
        if (section.getString("itemflags") != null) {
            itemFlags.addAll(section.getStringList("itemflags"));
        }
        int customModelData = 0;
        if (section.getString("custommodeldata") != null) {
            customModelData = section.getInt("custommodeldata");
        }
        boolean unbreakable = false;
        if (section.getString("unbreakable") != null) {
            unbreakable = section.getBoolean("unbreakable");
        }

        // ITEM GENERATE

        ItemStack itemStack = null;
        if (section.getString("base64") != null) {
            itemStack = HeadManager.convert(HeadManager.HeadType.BASE64, section.getString("base64"));
        } else if (section.getString("skin") != null) {
            itemStack = HeadManager.convert(HeadManager.HeadType.PLAYER_HEAD, section.getString("skin"));
        } else {
            itemStack = new ItemStack(material, amount, data);
        }
        itemStack.setAmount(amount);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setUnbreakable(unbreakable);
        if (name != null) itemMeta.setDisplayName(name);
        if (!lore.isEmpty()) itemMeta.setLore(lore);
        if (!enchants.isEmpty()) {
            for (String e : enchants) {
                itemMeta.addEnchant(Enchantment.getByName(e.split(";")[0]), Integer.parseInt(e.split(";")[1]), true);
            }
        }
        if (!itemFlags.isEmpty()) itemFlags.forEach(flag -> itemMeta.addItemFlags(ItemFlag.valueOf(flag)));
        if (customModelData != 0) itemMeta.setCustomModelData(customModelData);
        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

}
