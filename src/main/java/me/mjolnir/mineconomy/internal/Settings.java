package me.mjolnir.mineconomy.internal;

import java.io.File;
import java.io.IOException;

import me.mjolnir.mineconomy.MineConomy;
import me.mjolnir.mineconomy.internal.util.IOH;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * Handles loading settings from saved YML.
 *
 * @author MjolnirCommando
 */
@SuppressWarnings("javadoc")
public class Settings {

    private static File propFile = new File(MineConomy.getInstance().getDataFolder(), "config.yml");
    private static YamlConfiguration config;

    public static double startingBalance = 0.0;
    public static double maxDebt = 0.0;
    public static double maxBalance = 9999999.99;
    public static String lang = "en";
    public static int logPriority = 4;
    public static String dburl = "";
    public static String dbname = "";
    public static String dbuser = "";
    public static String dbpass = "";
    public static String dbtype = "none";

    /**
     * Loads the Configuration
     */
    public static void load() {
        config = YamlConfiguration.loadConfiguration(propFile);
        config.options().header("=== MineConomy Configuration ===\n\n    \n");

        if (!propFile.exists()) {
            IOH.log("Config file not found. Creating Config file...", IOH.DEV);
            Bukkit.getConsoleSender().sendMessage("�9[Mineconomy] �fConfig file not found. Creating Config file...");
            config.set("Balance.Starting Balance", 0);
            config.set("Balance.Max Debt", 0);
            config.set("Balance.Max Balance", 9999999.99);
            config.set("Warn Ops", true);
            config.set("Log Priority", 4);           
            config.set("Database.URL", "");
            config.set("Database.Name", "");
            config.set("Database.Username", "");
            config.set("Database.Password", "");
            config.set("Database.Type", "none");
            config.set("Lang", "en");
            config.set("Auto-Save Interval", "1h");
            IOH.log("Config file created!", IOH.DEV);
            Bukkit.getConsoleSender().sendMessage("�9[Mineconomy] �fConfig file created!");
            save();
        }

        IOH.log("Loading Config file...", IOH.DEV);
        Bukkit.getConsoleSender().sendMessage("�9[Mineconomy] �fLoading Config File...");

        reload();

        IOH.log("Config file loaded!", IOH.DEV);
        Bukkit.getConsoleSender().sendMessage("�9[Mineconomy] �fConfig file Loaded!");
    }

    /**
     * Reloads the Configuration
     */
    public static void reload() {
        config = YamlConfiguration.loadConfiguration(propFile);

        startingBalance = config.getDouble("Balance.Starting Balance", startingBalance);
        maxDebt = Math.abs(config.getDouble("Balance.Max Debt", maxDebt));
        maxBalance = config.getDouble("Balance.Max Balance", maxBalance);      
        dburl = config.getString("Database.URL", dburl);
        dbname = config.getString("Database.Name", dbname);
        dbuser = config.getString("Database.Username", dbuser);
        dbpass = config.getString("Database.Password", dbpass);
        dbtype = config.getString("Database.Type", dbtype);
        lang = config.getString("Lang", lang);
        logPriority = config.getInt("Log Priority", logPriority);
    }

    /**
     * Saves the Configuration
     */
    public static void save() {
        config.set("Balance.Starting Balance", startingBalance);
        config.set("Balance.Max Debt", maxDebt);
        config.set("Balance.Max Balance", maxBalance);
        config.set("Database.URL", dburl);
        config.set("Database.Name", dbname);
        config.set("Database.Username", dbuser);
        config.set("Database.Password", dbpass);
        config.set("Database.Type", dbtype);
        config.set("Lang", lang);
        config.set("Log Priority", logPriority);

        try {
            config.save(propFile);
        } catch (IOException e) {
            IOH.error("IOException", e);
        }

        reload();
    }
}
