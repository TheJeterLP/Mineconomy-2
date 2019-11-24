package me.mjolnir.mineconomy;

import java.util.logging.Level;
import me.mjolnir.mineconomy.database.DatabaseFactory;
import me.mjolnir.mineconomy.internal.Banking;
import me.mjolnir.mineconomy.internal.MCLang;
import me.mjolnir.mineconomy.internal.commands.ChatExecutor;
import me.mjolnir.mineconomy.internal.listeners.MCListener;
import org.bukkit.plugin.java.JavaPlugin;

public class MineConomy extends JavaPlugin {

    private static MineConomy INSTANCE;

    @Override
    public void onEnable() {
        INSTANCE = this;
        log("Enabling plugin...");

        Config.load();
        DatabaseFactory.init();
        
        Banking.load();
        MCLang.load();

        saveResource("README.txt", true);

        getServer().getPluginManager().registerEvents(new MCListener(), this);

        ChatExecutor executor = new ChatExecutor();

        getCommand("mc").setExecutor(executor);
        getCommand("money").setExecutor(executor);
        getCommand("mcb").setExecutor(executor);

        log("Version " + getDescription().getVersion() + " by " + getDescription().getAuthors() + " is enabled!");

    }

    @Override
    public void onDisable() {
        log("Disabling MineConomy...");

        Banking.save();
        MCLang.save();

        log("MineConomy is disabled.");
    }
    
    public static MineConomy getInstance() {
        return INSTANCE;
    }

    public static void log(String msg) {
        INSTANCE.getLogger().log(Level.INFO, msg);
    }
}
