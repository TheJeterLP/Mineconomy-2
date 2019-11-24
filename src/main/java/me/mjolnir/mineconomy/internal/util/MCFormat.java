package me.mjolnir.mineconomy.internal.util;

import java.text.NumberFormat;
import java.util.Locale;
import me.mjolnir.mineconomy.exceptions.FormatException;
import org.bukkit.ChatColor;

@SuppressWarnings("javadoc")
public class MCFormat {

    public static String color(String message) {
        message = message.replace("&0", ChatColor.BLACK + "");
        message = message.replace("&1", ChatColor.DARK_BLUE + "");
        message = message.replace("&2", ChatColor.DARK_GREEN + "");
        message = message.replace("&3", ChatColor.DARK_AQUA + "");
        message = message.replace("&4", ChatColor.DARK_RED + "");
        message = message.replace("&5", ChatColor.DARK_PURPLE + "");
        message = message.replace("&6", ChatColor.GOLD + "");
        message = message.replace("&7", ChatColor.GRAY + "");
        message = message.replace("&8", ChatColor.DARK_GRAY + "");
        message = message.replace("&9", ChatColor.BLUE + "");

        message = message.replace("&a", ChatColor.GREEN + "");
        message = message.replace("&b", ChatColor.AQUA + "");
        message = message.replace("&c", ChatColor.RED + "");
        message = message.replace("&d", ChatColor.LIGHT_PURPLE + "");
        message = message.replace("&e", ChatColor.YELLOW + "");
        message = message.replace("&f", ChatColor.WHITE + "");

        return message;
    }

    public static String decolor(String message) {
        message = message.replace(ChatColor.BLACK + "", "&0");
        message = message.replace(ChatColor.DARK_BLUE + "", "&1");
        message = message.replace(ChatColor.DARK_GREEN + "", "&2");
        message = message.replace(ChatColor.DARK_AQUA + "", "&3");
        message = message.replace(ChatColor.DARK_RED + "", "&4");
        message = message.replace(ChatColor.DARK_PURPLE + "", "&5");
        message = message.replace(ChatColor.GOLD + "", "&6");
        message = message.replace(ChatColor.GRAY + "", "&7");
        message = message.replace(ChatColor.DARK_GRAY + "", "&8");
        message = message.replace(ChatColor.BLUE + "", "&9");

        message = message.replace(ChatColor.GREEN + "", "&a");
        message = message.replace(ChatColor.AQUA + "", "&b");
        message = message.replace(ChatColor.RED + "", "&c");
        message = message.replace(ChatColor.LIGHT_PURPLE + "", "&d");
        message = message.replace(ChatColor.YELLOW + "", "&e");
        message = message.replace(ChatColor.WHITE + "", "&f");

        return message;
    }

    public static String format(double amount) {
        return format(amount, true);
    }

    public static String format(double amount, boolean flag) {
        NumberFormat numberFormatter = NumberFormat
                .getNumberInstance(new Locale("US"));

        String result = numberFormatter.format(amount);

        if (flag) {
            int length = 0;
            try {
                length = result.replace(".", "-").split("-")[1].length();
            } catch (Exception e) {
                length = 0;
            }
            if (length == 0) {
                result += ".00";
            } else if (length == 1) {
                result += "0";
            }
        }

        return result;
    }
}
