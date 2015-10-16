package me.mjolnir.mineconomy;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Logger;

import me.mjolnir.mineconomy.internal.Banking;
import me.mjolnir.mineconomy.internal.Currency;
import me.mjolnir.mineconomy.internal.MCCom;
import me.mjolnir.mineconomy.internal.MCLang;
import me.mjolnir.mineconomy.internal.Settings;
import me.mjolnir.mineconomy.internal.commands.ChatExecutor;
import me.mjolnir.mineconomy.internal.commands.Interest;
import me.mjolnir.mineconomy.internal.commands.Tax;
import me.mjolnir.mineconomy.internal.gui.GUI;
import me.mjolnir.mineconomy.internal.listeners.MCListener;
import me.mjolnir.mineconomy.internal.util.IOH;
import me.mjolnir.mineconomy.internal.util.Update;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;


/**
 * A simple, mid-weight Economy Bukkit Plugin.
 *
 * @author MjolnirCommando
 * @version 1.1
 */
public class MineConomy extends JavaPlugin {

    /**
     * Holds the main directory of the MineConomy data files.
     */
    public static String maindir = "plugins/MineConomy/";
  
    private static ChatColor primaryColor;
    
    public static	Logger logger;   

    /**
     * MineConomy shortcut to plugin.
     */
    public static MineConomy plugin;

    /**
     * The id of the Bukkit build which is currently running.
     */
    public static String bukkitVersion = "";

    private static String version;

    public void onEnable() {
        plugin = this;

        PluginDescriptionFile pdfFile = this.getDescription();
        version = pdfFile.getVersion();

        IOH.loadLog();

        IOH.log("Enabling plugin...", IOH.DEV);
        Bukkit.getConsoleSender().sendMessage("§9[Mineconomy] §aEnabling plugin...!");

        //checkVersion();

        new File(maindir).mkdir();

        load();

        saveResource("README.txt", true);

        getServer().getPluginManager().registerEvents(new MCListener(), this);

        ChatExecutor executor = new ChatExecutor(this);

        getCommand("mc").setExecutor(executor);
        getCommand("money").setExecutor(executor);
        getCommand("mcb").setExecutor(executor);

        if (Settings.autosaveInterval > 0) {
            this.getServer().getScheduler()
                    .scheduleSyncRepeatingTask(this, new Runnable() {
                        public void run() {
                            IOH.log("Auto-Saving files...", IOH.DEV);
                            Bukkit.getConsoleSender().sendMessage("§9[Mineconomy] §cAuto-Saving files...");

                            save();

                            IOH.log("Finished auto-save.", IOH.DEV);
                            Bukkit.getConsoleSender().sendMessage("§9[Mineconomy] §aFinished auto-save!");
                        }
                    }, 20, Settings.autosaveInterval * 20);
        } else {
            IOH.log("Auto-Saving Disabled.", IOH.INFO);
        }

        if (Settings.interestInterval > 0) {
            this.getServer().getScheduler()
                    .scheduleSyncRepeatingTask(this, new Runnable() {
                        public void run() {
                            IOH.log("Giving interest...", IOH.INFO);

                            if (Settings.interestMode.equalsIgnoreCase("none")) {
                                IOH.log("Your Interest Mode is set to \"none\". "
                                        + "To improve server performance, please set Interest Interval to 0.",
                                        IOH.IMPORTANT);
                            } else if (Settings.interestMode
                            .equalsIgnoreCase("fixed")) {
                                IOH.log("Interest Mode set to FIXED.", IOH.INFO);
                                Interest.fixed();
                            } else if (Settings.interestMode
                            .equalsIgnoreCase("percent")) {
                                IOH.log("Interest Mode set to PERCENT.",
                                        IOH.INFO);
                                Interest.percent();
                            } else {
                                IOH.log("Interest Mode could not be identified.",
                                        IOH.IMPORTANT);
                            }

                            IOH.log("Finished giving interest.", IOH.INFO);
                        }
                    }, 20, Settings.interestInterval * 20);
        } else {
            IOH.log("Interest Disabled.", IOH.INFO);
        }

        if (Settings.taxInterval > 0) {
            this.getServer().getScheduler()
                    .scheduleSyncRepeatingTask(this, new Runnable() {
                        public void run() {
                            IOH.log("Taxing...", IOH.INFO);

                            if (Settings.taxMode.equalsIgnoreCase("none")) {
                                IOH.log("Your Tax Mode is set to \"none\". "
                                        + "To improve server performance, please set Tax Interval to 0.",
                                        IOH.IMPORTANT);
                            } else if (Settings.taxMode.equalsIgnoreCase("fixed")) {
                                IOH.log("Tax Mode set to FIXED.", IOH.INFO);
                                Tax.fixed();
                            } else if (Settings.taxMode
                            .equalsIgnoreCase("percent")) {
                                IOH.log("Tax Mode set to PERCENT.", IOH.INFO);
                                Tax.percent();
                            } else {
                                IOH.log("Tax Mode could not be identified.",
                                        IOH.IMPORTANT);
                            }

                            IOH.log("Finished taxes.", IOH.INFO);
                        }
                    }, 20, Settings.taxInterval * 20);
        } else {
            IOH.log("Tax Disabled.", IOH.INFO);
        }

        if (Settings.gui) {
            IOH.log("Creating GUI...", IOH.INFO);
            new GUI();
        } else {
            IOH.log("GUI Disabled.", IOH.INFO);
        }

        IOH.log("Checking for updates...", IOH.DEV);
        Bukkit.getConsoleSender().sendMessage("§9[Mineconomy] §cChecking for updates is disabled, wait for next version :c");

 /*       if (Update.check()) {
            IOH.log("Updates available. Check http://dev.bukkit.org/server-mods/mineconomy/ for the update.",
                    IOH.VERY_IMPORTANT);
        } else {
            IOH.log("No updates available. MineConomy is up to date!", IOH.INFO);
        }*/

        File f;

        if (Settings.migrate.equalsIgnoreCase("iconomy")) {

            f = new File(maindir + "../iConomy/accounts.mini");

            if (f.exists()) {
                File n = new File(maindir + "accounts.yml.convert");

                if (!n.exists()) {
                    IOH.log("MineConomy has found an iConomy accounts file. MineConomy will convert it and save it to the MineConomy folder as \"accounts.yml.convert\".",
                            IOH.VERY_IMPORTANT);

                    try {
                        YamlConfiguration accounts = new YamlConfiguration();
                        accounts.options()
                                .header("=== MineConomy Accounts ===\n\n    Do not edit!\n");

                        Scanner in = new Scanner(f);

                        int linenum = 0;

                        while (in.hasNextLine()) {
                            String line = in.nextLine();
                            linenum += 1;

                            if (line != "") {
                                String[] args = line.split(" ");

                                try {
                                    accounts.set("Accounts." + args[0]
                                            + ".Balance", Double
                                            .parseDouble(args[1].replace(
                                                            "balance:", "")));
                                    accounts.set("Accounts." + args[0]
                                            + ".Status", "NORMAL");
                                    accounts.set("Accounts." + args[0]
                                            + ".Currency",
                                            MCCom.getDefaultCurrency());
                                } catch (Exception e) {
                                    IOH.error("Parse Data (Line " + linenum
                                            + "): \"" + line
                                            + "\"\nParse Error", e);
                                }
                            }
                        }

                        in.close();

                        accounts.save(n);
                    } catch (FileNotFoundException e) {
                        IOH.error("FileNotFoundException", e);
                    } catch (IOException e) {
                        IOH.error("IOException", e);
                    }
                } else {
                    IOH.log("MineConomy could NOT find an iConomy accounts file. MineConomy can NOT convert files.",
                            IOH.VERY_IMPORTANT);
                }
            }

        } else if (Settings.migrate.equalsIgnoreCase("mysql")) {

            f = new File(maindir + "accounts.yml");

            if (f.exists() && Settings.dbtype.equalsIgnoreCase("mysql")) {
                IOH.log("MineConomy will now migrate your account data from your \"accounts.yml\" file to your MySQL database.",
                        IOH.VERY_IMPORTANT);

                YamlConfiguration accounts = YamlConfiguration
                        .loadConfiguration(f);

                Object[] t = accounts.getConfigurationSection("Accounts").getKeys(true).toArray();

                ArrayList<String> accountlist = new ArrayList<String>();

                for (int i = 0; t.length > i; i++) {
                    String[] parent = t[i].toString().replace(".", "-")
                            .split("-");

                    if (parent.length == 1) {
                        accountlist.add(parent[0]);
                    }
                }

                Connection con = null;
                String driver = "com.mysql.jdbc.Driver";

                try {
                    Class.forName(driver).newInstance();
                    con = DriverManager.getConnection("jdbc:mysql://"
                            + Settings.dburl + Settings.dbname,
                            Settings.dbuser, Settings.dbpass);

                    try {
                        Statement st = con.createStatement();
                        String com = "SELECT * FROM mineconomy_accounts WHERE id = '1'";
                        st.execute(com);
                    } catch (Exception e) {
                        Statement st = con.createStatement();
                        String com = "CREATE TABLE mineconomy_accounts(id int NOT NULL AUTO_INCREMENT, account text NOT NULL, balance double NOT NULL, currency text NOT NULL, status text NOT NULL, PRIMARY KEY (id) )";
                        st.execute(com);
                    }

                    for (int i = 0; accountlist.size() > i; i++) {
                        Statement st = con.createStatement();
                        String com = "INSERT INTO mineconomy_accounts(account, balance, currency, status) VALUES ('"
                                + accountlist.get(i)
                                + "', "
                                + accounts.get("Accounts." + accountlist.get(i) + ".Balance").toString()
                                + ", '"
                                + accounts.get("Accounts." + accountlist.get(i) + ".Currency").toString()
                                + "', 'NORMAL')";
                        st.execute(com);
                    }
                } catch (Exception e) {
                    IOH.error("MySQL Error", e);
                }
            } else {
                IOH.log("MineConomy could not find a valid accounts file to migrate from.",
                        IOH.VERY_IMPORTANT);
            }
        }

        Bukkit.getServer().getServicesManager()
                .register(MineConomy.class, this, getPlugin(), null);

        IOH.log("Version " + pdfFile.getVersion()
                + " by Bodyash is enabled!", IOH.IMPORTANT);

    }

    public void onDisable() {
        IOH.log("Disabling MineConomy...", IOH.INFO);

        if (Settings.gui) {
            GUI.window.setVisible(false);
        }

        save();
        IOH.log("MineConomy is disabled.", IOH.IMPORTANT);
    }

    private static void load() {
        Settings.load();
        MCCom.initialize();
        MCCom.getAccounting().load();
        Banking.load();
        Currency.load();
        MCLang.langFile = new File(MineConomy.maindir + "lang/" + "lang-"
                + Settings.lang + ".yml");
        MCLang.load();
    }

    /**
     * Reloads MineConomy's Resources.
     */
    public static void reload() {
        MCCom.getAccounting().reload();
        Banking.reload();
        Currency.reload();
        Settings.reload();
        MCLang.reload();
    }

    /**
     * Saves MineConomy's Resources.
     */
    public static void save() {
        MCCom.getAccounting().save();
        Banking.save();
        Currency.save();
        Settings.save();
        MCLang.save();
        IOH.saveLog();
    }

    /**
     * Returns the current MineConomy version.
     *
     * @return String
     */
    public static String getVersion() {
        return version;
    }

    /**
     * Returns the current plugin name.
     *
     * @return MineConomy
     */
    public static String getPluginName() {
        return "MineConomy";
    }

    /**
     * Returns MineConomy.
     *
     * @return MineConomy
     */
    public MineConomy getPlugin() {
        return this;
    }

    private void checkVersion() {
        String v = Bukkit.getServer().getVersion();
        v = v.substring(v.length() - 20, v.length() - 16);
        int version = 0;
        try {
            version = Integer.parseInt(v);
        } catch (NumberFormatException e) {
            IOH.log("Found non-Bukkit server; Unable to check server version.",
                    IOH.VERY_IMPORTANT);
            bukkitVersion = "Tekkit?";
            return;
        }

        bukkitVersion = v;

        if (version >= 2586) {
        	Bukkit.getConsoleSender().sendMessage("§9[Mineconomy] §fFound CraftBukkit [" + v + "].");
        	Bukkit.getConsoleSender().sendMessage("§9[Mineconomy] §a It is compatible! If This Plugins doesn`t works - let me know: §fhttp://dev.bukkit.org/profiles/Bodyash/");
        } else {
        	Bukkit.getConsoleSender().sendMessage("§9[Mineconomy] §fFound CraftBukkit [" + v + "].");
        	Bukkit.getConsoleSender().sendMessage("§9[Mineconomy] §4 It may be not compatible, but MineConomy will try to work on this bukkit build.");
        	Bukkit.getConsoleSender().sendMessage("§9[Mineconomy] §4 Use it for your §OWN RISK!!!.");
        	Bukkit.getConsoleSender().sendMessage("§9[Mineconomy] §a If This Plugins doesn`t works - let me know: §fhttp://dev.bukkit.org/profiles/Bodyash/");
        }
    }
}
