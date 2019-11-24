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
package de.thejeterlp.mineconomy;

import de.thejeterlp.mineconomy.database.AccountingBase;
import java.util.List;
import java.util.UUID;
import de.thejeterlp.mineconomy.database.DatabaseFactory;

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
     */
    public static void subtract(UUID uuid, double amount) {
        if (!exists(uuid)) {
            create(uuid);
        }
        amount = Math.abs(amount);
        if (accounting.getBalance(uuid) >= amount) {
            accounting.setBalance(uuid, accounting.getBalance(uuid) - amount);
        }
    }

    /**
     * Multiplies the specified account by the specified multiplier.
     *
     * @param uuid
     * @param multiplier
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
}
