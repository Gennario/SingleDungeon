package cz.gennario.singledungeon.commands;

import cz.gennario.singledungeon.Main;
import cz.gennario.singledungeon.system.DungeonGame;
import cz.gennario.singledungeon.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class StartCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if(sender instanceof Player player) {
            if(args.length != 1) {
                player.sendMessage(Utils.colorize(player, Main.getInstance().getConfigManager().get().getString("messages.usage")));
            }else {
                String roomID = args[0];
                if(Main.getInstance().getRoomMap().containsKey(roomID)) {
                    new DungeonGame(player, Main.getInstance().getRoomMap().get(roomID));
                }
            }
        }else {
            sender.sendMessage("This command is only for players!");
        }
        return false;
    }
}
