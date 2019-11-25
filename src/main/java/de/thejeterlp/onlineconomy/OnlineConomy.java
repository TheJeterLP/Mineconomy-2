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
package de.thejeterlp.onlineconomy;

import de.thejeterlp.onlineconomy.database.DatabaseFactory;
import de.thejeterlp.onlineconomy.commands.ChatExecutor;
import de.thejeterlp.onlineconomy.listeners.MCListener;
import org.bukkit.plugin.java.JavaPlugin;

public class OnlineConomy extends JavaPlugin {

    private static OnlineConomy INSTANCE;

    @Override
    public void onEnable() {
        INSTANCE = this;
        log("Enabling plugin...");

        Locales.load();
        Config.load();

        DatabaseFactory.init();

        getServer().getPluginManager().registerEvents(new MCListener(), this);

        ChatExecutor executor = new ChatExecutor();
        getCommand("mc").setExecutor(executor);
        getCommand("money").setExecutor(executor);

        log("Version " + getDescription().getVersion() + " by " + getDescription().getAuthors() + " is enabled!");

    }

    @Override
    public void onDisable() {
        log("OnlineConomy is disabled.");
    }

    public static OnlineConomy getInstance() {
        return INSTANCE;
    }

    public static void log(String msg) {
        INSTANCE.getLogger().info(msg);
    }
}
