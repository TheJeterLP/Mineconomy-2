/*
 * Copyright (C) 2019 TheJeterLP
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package me.mjolnir.mineconomy;

import java.io.File;
import java.io.IOException;
import java.util.List;
import me.mjolnir.mineconomy.internal.util.MCFormat;
import org.bukkit.configuration.file.YamlConfiguration;

public enum Config {

    MYSQL_USE("MySQL.Use", false, "true=Use MySQL, false=use SQLite"),
    MYSQL_IP("MySQL.IP", "127.0.0.1", "The ip of the mysql server"),
    MYSQL_PORT("MySQL.Port", 3306, "The port of the MySQL server"),
    MYSQL_DATABASE("MySQL.Database", "minecraft", "The name of the dataabse"),
    MYSQL_USER("MySQL.User", "root", "The user of the database"),
    MYSQL_PASSWORD("MySQL.Password", "password", "The password of the user"),
    STARTING_BALANCE("Balance.StartingBalance", 0, "The Balance a new player starts with"),
    MAX_BALANCE("Balance.MaxBalance", 9999999.99, "The maximum amount a player can have"),
    MAX_DEBT("Balance.MaxDebt", 0, "The maximum debt a player can have"),
    CURRENCY_NAME("Balance.CurrencyName", "Dollars", "The name of the currency"),
    
    ;

    private final Object value;
    private final String path;
    private final String description;
    private static YamlConfiguration cfg;
    private static final File f = new File(MineConomy.getInstance().getDataFolder(), "config.yml");

    private Config(String path, Object val, String description) {
        this.path = path;
        this.value = val;
        this.description = description;
    }

    public String getPath() {
        return path;
    }

    public String getDescription() {
        return description;
    }

    public Object getDefaultValue() {
        return value;
    }

    public boolean getBoolean() {
        return cfg.getBoolean(path);
    }

    public double getDouble() {
        return cfg.getDouble(path);
    }

    public String getString() {
        return MCFormat.color(cfg.getString(path));
    }

    public int getInteger() {
        return cfg.getInt(path);
    }

    public List<String> getStringList() {
        return cfg.getStringList(path);
    }

    public static void load() {
        MineConomy.getInstance().getDataFolder().mkdirs();
        reload(false);
        String header = "";
        for (Config c : values()) {
            header += c.getPath() + ": " + c.getDescription() + System.lineSeparator();
            if (!cfg.contains(c.getPath())) {
                c.set(c.getDefaultValue(), false);
            }
        }
        cfg.options().header(header);
        try {
            cfg.save(f);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void set(Object value, boolean save) {
        cfg.set(path, value);
        if (save) {
            try {
                cfg.save(f);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            reload(false);
        }
    }

    public static void reload(boolean complete) {
        if (!complete) {
            cfg = YamlConfiguration.loadConfiguration(f);
            return;
        }
        load();
    }

    public static YamlConfiguration getConfig() {
        return cfg;
    }
}
