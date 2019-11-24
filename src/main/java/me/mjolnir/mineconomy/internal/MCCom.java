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
package me.mjolnir.mineconomy.internal;

import me.mjolnir.mineconomy.database.AccountingBase;
import java.util.List;
import java.util.UUID;
import me.mjolnir.mineconomy.MineConomy;
import me.mjolnir.mineconomy.database.DatabaseFactory;
import me.mjolnir.mineconomy.exceptions.InsufficientFundsException;
import me.mjolnir.mineconomy.exceptions.NaturalNumberException;
import me.mjolnir.mineconomy.exceptions.NoAccountException;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

/**
 * Handles exterior classes reading/writing account values.
 *
 * @author TheJeterLP
 */
public class MCCom {

    private static final AccountingBase accounting = DatabaseFactory.getDatabase();

    // MineConomy Account Methods ----------------------------------------------
    /**
     * Returns the balance of the specified player.
     *
     * @param uuid
     * @return double
     * @throws NoAccountException
     */
    public static double getBalance(UUID uuid) {
        if (!exists(uuid)) {
            create(uuid);
        }
        return accounting.getBalance(uuid);
    }

    /**
     * Sets the balance of the specified player.
     *
     * @param uuid
     * @param balance
     * @throws NoAccountException
     */
    public static void setBalance(UUID uuid, double balance) {
        if (!exists(uuid)) {
            create(uuid);
        }
        accounting.setBalance(uuid, balance);
    }

    /**
     * Returns true if specified player has an account.
     *
     * @param uuid
     * @return boolean
     */
    public static boolean exists(UUID uuid) {
        return accounting.exists(uuid);
    }

    /**
     * Returns true if specified player has specified amount in their account.
     *
     * @param uuid
     * @param amount
     * @return boolean
     * @throws NoAccountException
     */
    public static boolean canAfford(UUID uuid, double amount) {
        if (!exists(uuid)) {
            create(uuid);
        }
        return accounting.getBalance(uuid) >= amount;
    }

    /**
     * Adds the specified amount to specified account.
     *
     * @param uuid
     * @param amount
     * @throws NoAccountException
     */
    public static void add(UUID uuid, double amount) {
        if (!exists(uuid)) {
            create(uuid);
        }
        amount = Math.abs(amount);
        accounting.setBalance(uuid, accounting.getBalance(uuid) + amount);
    }

    /**
     * Subtracts the specified amount from specified account.
     *
     * @param uuid
     * @param amount
     * @throws NoAccountException
     * @throws InsufficientFundsException
     */
    public static void subtract(UUID uuid, double amount) {
        if (!exists(uuid)) {
            create(uuid);
        }
        amount = Math.abs(amount);
        if (accounting.getBalance(uuid) >= amount) {
            accounting.setBalance(uuid, accounting.getBalance(uuid) - amount);
        } else {
            throw new InsufficientFundsException("MCCom: public void subtract(String uuid, double amount)", "amount");
        }
    }

    /**
     * Multiplies the specified account by the specified multiplier.
     *
     * @param uuid
     * @param multiplier
     * @throws NoAccountException
     * @throws NaturalNumberException
     */
    public static void multiply(UUID uuid, double multiplier) {
        if (!exists(uuid)) {
            create(uuid);
        }
        multiplier = Math.abs(multiplier);
        accounting.setBalance(uuid, accounting.getBalance(uuid) * multiplier);
    }

    /**
     * Sets the specified account's balance to 0.
     *
     * @param uuid
     * @throws NoAccountException
     */
    public static void empty(UUID uuid) {
        if (!exists(uuid)) {
            create(uuid);
        }
        accounting.setBalance(uuid, 0);
    }

    /**
     * Creates a new account with the specified name.
     *
     * @param uuid
     */
    public static void create(UUID uuid) {
        if (!exists(uuid)) {
            accounting.create(uuid);
        }
    }

    /**
     * The specified amount is added to the specified FROM account and
     * subtracted from the specified TO account.
     *
     * @param uuidFrom
     * @param uuidTo
     * @param amount
     * @throws NoAccountException
     * @throws InsufficientFundsException
     */
    public static void transfer(UUID uuidFrom, UUID uuidTo, double amount) {
        if (!exists(uuidFrom)) {
            create(uuidFrom);
        }
        
        if (!exists(uuidTo)) {
            create(uuidTo);
        }
        if (accounting.getBalance(uuidTo) >= amount) {
            accounting.setBalance(uuidFrom, accounting.getBalance(uuidFrom) - amount);
            accounting.setBalance(uuidTo, accounting.getBalance(uuidTo) + amount);
        } else {
            throw new InsufficientFundsException("MCCom: public void transfer(String uuidFrom, String uuidTo, double amount)", "amount");
        }
    }

    /**
     * Gets an ArrayList with all MineConomy accounts.
     *
     * @return An ArrayList of MineConomy Accounts
     */
    public static List<UUID> getAccounts() {
        return accounting.getAccounts();
    }

    // Vault Methods -----------------------------------------------------------
    /**
     * Returns true if the specified account has at least the specified amount.
     *
     * @param account
     * @param amount
     * @return True if the specified account has at least the specified amount.
     */
    public static boolean canExternalAfford(String account, double amount) {
        OfflinePlayer op = Bukkit.getOfflinePlayer(account);
        return MCCom.canAfford(op.getUniqueId(), amount);
    }

    /**
     * Returns the balance of the specified account.
     *
     * @param account
     * @return Balance
     */
    public static double getExternalBalance(String account) {
        OfflinePlayer op = Bukkit.getOfflinePlayer(account);
        return MCCom.getBalance(op.getUniqueId());
    }

    /**
     * Sets the specified account's balance to the specified amount.
     *
     * @param account
     * @param balance
     */
    public static void setExternalBalance(String account, double balance) {
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
        return "MineConomy";
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
}
