package me.mjolnir.mineconomy;

import java.io.File;
import me.mjolnir.mineconomy.internal.Banking;
import me.mjolnir.mineconomy.internal.MCCom;
import me.mjolnir.mineconomy.internal.MCLang;
import me.mjolnir.mineconomy.internal.Settings;
import me.mjolnir.mineconomy.internal.commands.ChatExecutor;
import me.mjolnir.mineconomy.internal.listeners.MCListener;
import me.mjolnir.mineconomy.internal.util.IOH;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class MineConomy extends JavaPlugin {

    private static MineConomy INSTANCE;

    @Override
    public void onEnable() {
        INSTANCE = this;
        IOH.loadLog();

        IOH.log("Enabling plugin...", IOH.DEV);
        Bukkit.getConsoleSender().sendMessage("§9[Mineconomy] §aEnabling plugin...!");

        getDataFolder().mkdirs();

        load();

        saveResource("README.txt", true);

        getServer().getPluginManager().registerEvents(new MCListener(), this);

        ChatExecutor executor = new ChatExecutor();

        getCommand("mc").setExecutor(executor);
        getCommand("money").setExecutor(executor);
        getCommand("mcb").setExecutor(executor);

        IOH.log("Version " + getDescription().getVersion() + " by " + getDescription().getAuthors() + " is enabled!", IOH.IMPORTANT);

    }

    @Override
    public void onDisable() {
        IOH.log("Disabling MineConomy...", IOH.INFO);

        save();
        IOH.log("MineConomy is disabled.", IOH.IMPORTANT);
    }

    private void load() {
        Settings.load();
        MCCom.initialize();
        MCCom.getAccounting().load();
        Banking.load();
        MCLang.langFile = new File(getDataFolder(), "lang/" + "lang-" + Settings.lang + ".yml");
        MCLang.load();
    }

    public void reload() {
        MCCom.getAccounting().reload();
        Banking.reload();
        Settings.reload();
        MCLang.reload();
    }

    public void save() {
        MCCom.getAccounting().save();
        Banking.save();
        Settings.save();
        MCLang.save();
        IOH.saveLog();
    }

    public static MineConomy getInstance() {
        return INSTANCE;
    }
}
