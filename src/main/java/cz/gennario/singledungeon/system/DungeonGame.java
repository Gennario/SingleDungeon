package cz.gennario.singledungeon.system;

import cz.gennario.singledungeon.Main;
import cz.gennario.singledungeon.utils.Utils;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy;
import net.minecraft.server.network.PlayerConnection;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Getter
@Setter
public class DungeonGame {

    private final Player player;
    private final DungeonRoom room;

    private List<Integer> mobs;
    private int wave;

    private Location playerLocation;
    private YamlConfiguration configuration = Main.getInstance().getConfigManager().get();

    public DungeonGame(Player player, DungeonRoom dungeonRoom) {
        this.player = player;
        this.room = dungeonRoom;

        this.mobs = new ArrayList<>();
        this.wave = 1;

        Main.getInstance().getGameMap().put(player, this);

        for (DungeonGame dungeonGame : Main.getInstance().getGameMap().values()) {
            dungeonGame.getPlayer().hidePlayer(Main.getInstance(), player);
            player.hidePlayer(Main.getInstance(), dungeonGame.getPlayer());
            if (!dungeonGame.getMobs().isEmpty()) {
                for (Integer mob : dungeonGame.getMobs()) {
                    PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(mob);
                    PlayerConnection connection = ((CraftPlayer) player).getHandle().b;
                    connection.a(packet);
                }
            }
        }

        start();
    }

    public void start() {
        playerLocation = player.getLocation().clone();
        player.teleport(room.getSpawnLocation());

        player.sendTitle(Utils.colorize(player, configuration.getString("messages.prepare.title").replace("%name%", room.getId().toUpperCase(Locale.ROOT)).replace("%prepare_time%", "" + room.getPrepareTime())),
                Utils.colorize(player, configuration.getString("messages.prepare.subtitle").replace("%name%", room.getId().toUpperCase(Locale.ROOT)).replace("%prepare_time%", "" + room.getPrepareTime())),
                20, 80, 20);
        for (String s : configuration.getStringList("messages.prepare.messages")) {
            player.sendMessage(Utils.colorize(player, s.replace("%name%", room.getId().toUpperCase(Locale.ROOT))
                    .replace("%prepare_time%", "" + room.getPrepareTime())
                    .replace("%player%", player.getName())));
        }

        for (String command : room.getStartCommands()) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player%", player.getName()));
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                List<Integer> list = room.startWave(wave, player);
                mobs.addAll(list);
                Main.getInstance().getGameMobs().addAll(list);

                player.sendMessage(Utils.colorize(player, configuration.getString("messages.start")));
                player.sendMessage(Utils.colorize(player, configuration.getString("messages.wave").replace("%wave%", wave + "")));

            }
        }.runTaskLater(Main.getInstance(), room.getPrepareTime() * 20);
    }

    public void nextWave() {
        wave++;
        if (!room.waveExist(wave)) {
            winRoom();
            return;
        }
        List<Integer> list = room.startWave(wave, player);
        mobs.addAll(list);
        Main.getInstance().getGameMobs().addAll(list);
        player.sendMessage(Utils.colorize(player, configuration.getString("messages.wave").replace("%wave%", wave + "")));
    }

    public void winRoom() {
        player.teleport(playerLocation);
        for (String command : room.getWinCommands()) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player%", player.getName()));
        }
        if (!mobs.isEmpty()) {
            for (Entity entity : room.getMobSpawnLocation().getWorld().getEntities()) {
                if (mobs.contains(entity.getEntityId())) entity.remove();
            }
        }
        player.sendMessage(Utils.colorize(player, configuration.getString("messages.win")));

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            onlinePlayer.showPlayer(Main.getInstance(), player);
        }

        Main.getInstance().getGameMap().remove(player);
    }

    public void loseRoom() {
        player.teleport(playerLocation);
        for (String command : room.getLoseCommands()) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player%", player.getName()));
        }
        if (!mobs.isEmpty()) {
            for (Entity entity : room.getMobSpawnLocation().getWorld().getEntities()) {
                if (mobs.contains(entity.getEntityId())) {
                    Main.getInstance().getGameMobs().remove((Object) entity.getEntityId());
                    entity.remove();
                }
            }
        }

        player.sendMessage(Utils.colorize(player, configuration.getString("messages.lose").replace("%wave%", wave + "")));

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            onlinePlayer.showPlayer(Main.getInstance(), player);
        }

        Main.getInstance().getGameMap().remove(player);
    }

    public void checkNextWave() {
        if (mobs.isEmpty()) {
            nextWave();
        }
    }

}
