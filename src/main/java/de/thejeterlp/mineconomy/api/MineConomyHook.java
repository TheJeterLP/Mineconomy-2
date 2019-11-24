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
package de.thejeterlp.mineconomy.api;

import de.thejeterlp.mineconomy.Config;
import de.thejeterlp.mineconomy.MineConomy;
import de.thejeterlp.mineconomy.database.AccountingBase;
import de.thejeterlp.mineconomy.database.DatabaseFactory;
import de.thejeterlp.mineconomy.MCCom;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public class MineConomyHook {
    
    private static final AccountingBase accounting = DatabaseFactory.getDatabase();

    // Vault Methods -----------------------------------------------------------
    /**
     * Returns true if the specified account has at least the specified amount.
     *
     * @param account
     * @param amount
     * @return True if the specified account has at least the specified amount.
     */
    public static boolean canAfford(String account, double amount) {
        OfflinePlayer op = Bukkit.getOfflinePlayer(account);
        return MCCom.canAfford(op.getUniqueId(), amount);
    }

    /**
     * Returns the balance of the specified account.
     *
     * @param account
     * @return Balance
     */
    public static double getBalance(String account) {
        OfflinePlayer op = Bukkit.getOfflinePlayer(account);
        return MCCom.getBalance(op.getUniqueId());
    }

    /**
     * Sets the specified account's balance to the specified amount.
     *
     * @param account
     * @param balance
     */
    public static void setBalance(String account, double balance) {
        OfflinePlayer op = Bukkit.getOfflinePlayer(account);
        MCCom.setBalance(op.getUniqueId(), balance);
    }

    // Basic Variable Getters --------------------------------------------------
    /**
     * Returns instance of accounting class to use.
     *
     * @return Accounting class
     */
    public static AccountingBase getAccounting() {
        return accounting;
    }

    /**
     * Returns the name of MineConomy.
     *
     * @return name
     */
    public static String getName() {
        return MineConomy.getInstance().getName();
    }

    /**
     * Returns the current version of MineConomy.
     *
     * @return version
     */
    public static String getVersion() {
        return MineConomy.getInstance().getDescription().getVersion();
    }

    /**
     * Returns the MineConomy plugin.
     *
     * @return MineConomy
     */
    public static MineConomy getPlugin() {
        return MineConomy.getInstance();
    }

    /**
     * Returns the currency name
     *
     * @return String
     */
    public static String getCurrencyName() {
        return Config.CURRENCY_NAME.getString();
    }

    /**
     * Creates a new account
     *
     * @param name username of a player
     */
    public static void create(String name) {
        OfflinePlayer op = Bukkit.getOfflinePlayer(name);
        if (!accounting.exists(op.getUniqueId())) {
            accounting.create(op.getUniqueId());
        }
    }
    
    /**
     * Checks if an account exists already for that given player
     * @param name palyername
     * @return always returns true, because a new account will be created
     */
    public static boolean hasAccount(String name) {
        create(name);
        return true;
    }
    
}
