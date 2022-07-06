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

    @EventHandler(priority = EventPriority.NORMAL)
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

    @EventHandler(priority = EventPriority.NORMAL)
    public void onHit(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player player) {
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

    @EventHandler(priority = EventPriority.NORMAL)
    public void onKill(EntityDeathEvent event) {
        if (event.getEntity() instanceof Monster monster) {
            if (event.getEntity().getKiller() != null) {
                Player player = event.getEntity().getKiller();
                if (Main.getInstance().getGameMobs().contains(monster.getEntityId())) {
                    if (Main.getInstance().getGameMap().containsKey(player)) {
                        DungeonGame game = Main.getInstance().getGameMap().get(player);

                        game.getMobs().remove((Object) monster.getEntityId());
                        game.checkNextWave();
                        Main.getInstance().getGameMobs().remove((Object) monster.getEntityId());
                    }
                }
            } else if (Main.getInstance().getGameMobs().contains(monster.getEntityId())) {
                for (DungeonGame game : Main.getInstance().getGameMap().values()) {
                    if (game.getMobs().contains(monster.getEntityId())) {
                        game.getMobs().remove((Object) monster.getEntityId());
                        game.checkNextWave();
                        Main.getInstance().getGameMobs().remove((Object) monster.getEntityId());
                    }
                }
            }
        } else if (event.getEntity() instanceof Player player) {
            if (Main.getInstance().getGameMobs().contains(event.getEntity().getEntityId())) {
                if (Main.getInstance().getGameMap().containsKey(player)) {
                    DungeonGame game = Main.getInstance().getGameMap().get(player);
                    game.loseRoom();
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerKill(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if (Main.getInstance().getGameMap().containsKey(player)) {
            DungeonGame game = Main.getInstance().getGameMap().get(player);
            game.loseRoom();
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (Main.getInstance().getGameMap().containsKey(player)) {
            DungeonGame game = Main.getInstance().getGameMap().get(player);
            game.loseRoom();
        }
    }

}
