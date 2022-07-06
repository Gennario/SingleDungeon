package cz.gennario.singledungeon.utils;

import com.iridium.iridiumcolorapi.IridiumColorAPI;
import cz.gennario.singledungeon.utils.centermessage.CenterMessage;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;

public final class Utils {

    public static Date dateFromString(String string) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
        try {
            return formatter.parse(string);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String colorize(Player player, String string) {
        String playerName = "none";
        if(player != null && player.isOnline()) playerName = player.getName();
        string = string.replace("%player%", playerName);

        String s = string;
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            if(player != null && player.isOnline()) {
                s = PlaceholderAPI.setPlaceholders(player, string);
            }else {
                s = PlaceholderAPI.setPlaceholders(null, string);
            }
        }
        s = s.replace("§l", "&l");
        s = IridiumColorAPI.process(s);
        if(string.startsWith("<center>")) {
            s = CenterMessage.getCenteredMessage(s.replace("<center>", ""));
        }

        return s;
    }

    public static Date dateFromString(String string, String format) {
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        try {
            return formatter.parse(string);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<Location> drawCircle(Location location, int points, double radius, String direcion) {
        Location origin = location.clone();
        List<Location> locations = new ArrayList<>();
        for (int i = 0; i < points; i++) {
            double angle = 2 * Math.PI * i / points;
            if (direcion.equals("RIGHT")) {
                Location point = origin.clone().add(radius * Math.cos(angle), 0.0d, radius * Math.sin(angle));
                locations.add(point);
            } else if (direcion.equals("LEFT")) {
                Location point = origin.clone().add(radius * Math.sin(angle), 0.0d, radius * Math.cos(angle));
                locations.add(point);
            }
        }
        return locations;
    }

    public static String getMinecraftVersion(Server server) {
        String version = server.getVersion();
        int start = version.indexOf("MC: ") + 4;
        int end = version.length() - 1;
        return version.substring(start, end);
    }

    public static boolean isOldVersion() {
        return Integer.parseInt(Utils.getMinecraftVersion(Bukkit.getServer()).split("\\.")[1]) < 13;
    }

    public static Location getLocation(String s) {
        String[] splitted = s.replace(")", "").split("\\(");
        if (splitted.length == 2) {
            World world = Bukkit.getWorld(splitted[0]);
            double x = Double.parseDouble(splitted[1].split(",")[0]);
            double y = Double.parseDouble(splitted[1].split(",")[1]);
            double z = Double.parseDouble(splitted[1].split(",")[2]);
            return new Location(world, x, y, z);
        }
        return null;
    }

    public static String locationToString(Location location) {
        String loc = location.getWorld().getName() + "(";
        loc += location.getX() + ",";
        loc += location.getY() + ",";
        loc += location.getZ() + ")";
        return loc;
    }

    public static String locationToStringCenter(Location location) {
        String loc = location.getWorld().getName() + "(";
        loc += (location.getX() + 0.5) + ",";
        loc += location.getY() + ",";
        loc += (location.getZ() + 0.5) + ")";
        return loc;
    }

    public static boolean isBetweenDates(Date date, Date dateStart, Date dateEnd) {
        if (date != null && dateStart != null && dateEnd != null) {
            if (date.after(dateStart) && date.before(dateEnd)) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    public static void sendErrorLog(String message) {
        Bukkit.getServer().getLogger().log(Level.WARNING, "§4[RotatingHeads] §eSomething went wrong! " + message);
    }

}
