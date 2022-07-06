package cz.gennario.singledungeon;

import cz.gennario.singledungeon.system.DungeonGame;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class Events implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onTarget(EntityTargetEvent event) {
        if (!Main.getInstance().getGameMobs().isEmpty()) {
            if (Main.getInstance().getGameMobs().contains(event.getEntity().getEntityId())) {
                for (DungeonGame game : Main.getInstance().getGameMap().values()) {
                    if (game.getMobs().contains(event.getEntity().getEntityId())) {
                        event.setTarget(game.getPlayer());
                        return;
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onHit(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            Player player = (Player) event.getDamager();
            if (Main.getInstance().getGameMobs().contains(event.getEntity().getEntityId())) {
                if (Main.getInstance().getGameMap().containsKey(player)) {
                    DungeonGame game = Main.getInstance().getGameMap().get(player);
                    if (!game.getMobs().contains(event.getEntity().getEntityId())) {
                        event.setCancelled(true);
                    }
                } else {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onKill(EntityDeathEvent event) {
        if (event.getEntity() instanceof Monster) {
            if (event.getEntity().getKiller() != null) {
                Player player = event.getEntity().getKiller();
                if (Main.getInstance().getGameMobs().contains(event.getEntity().getEntityId())) {
                    if (Main.getInstance().getGameMap().containsKey(player)) {
                        DungeonGame game = Main.getInstance().getGameMap().get(player);

                        game.getMobs().remove((Object) event.getEntity().getEntityId());
                        game.checkNextWave();
                        Main.getInstance().getGameMobs().remove((Object) event.getEntity().getEntityId());
                    }
                }
            } else if (Main.getInstance().getGameMobs().contains(event.getEntity().getEntityId())) {
                for (DungeonGame game : Main.getInstance().getGameMap().values()) {
                    if (game.getMobs().contains(event.getEntity().getEntityId())) {
                        game.getMobs().remove((Object) event.getEntity().getEntityId());
                        game.checkNextWave();
                        Main.getInstance().getGameMobs().remove((Object) event.getEntity().getEntityId());
                    }
                }
            }
        } else if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (Main.getInstance().getGameMobs().contains(event.getEntity().getEntityId())) {
                if (Main.getInstance().getGameMap().containsKey(player)) {
                    DungeonGame game = Main.getInstance().getGameMap().get(player);
                    game.loseRoom();
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerKill(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if (Main.getInstance().getGameMap().containsKey(player)) {
            DungeonGame game = Main.getInstance().getGameMap().get(player);
            game.loseRoom();
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (Main.getInstance().getGameMap().containsKey(player)) {
            DungeonGame game = Main.getInstance().getGameMap().get(player);
            game.loseRoom();
        }
    }

}
