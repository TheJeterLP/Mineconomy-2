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
import me.mjolnir.mineconomy.Config;
import me.mjolnir.mineconomy.MineConomy;
import me.mjolnir.mineconomy.database.DatabaseFactory;
import me.mjolnir.mineconomy.exceptions.DivideByZeroException;
import me.mjolnir.mineconomy.exceptions.InsufficientFundsException;
import me.mjolnir.mineconomy.exceptions.NaturalNumberException;
import me.mjolnir.mineconomy.exceptions.NoAccountException;

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
     * @param account
     * @return double
     * @throws NoAccountException
     */
    public static double getBalance(String account) {
        return accounting.getBalance(account);
    }

    /**
     * Sets the balance of the specified player.
     *
     * @param account
     * @param balance
     * @throws NoAccountException
     */
    public static void setBalance(String account, double balance) {
        accounting.setBalance(account, balance);
    }

    /**
     * Returns true if specified player has an account.
     *
     * @param account
     * @return boolean
     */
    public static boolean exists(String account) {
        return accounting.exists(account);
    }

    /**
     * Returns true if specified player has specified amount in their account.
     *
     * @param account
     * @param amount
     * @return boolean
     * @throws NoAccountException
     */
    public static boolean canAfford(String account, double amount) {
        return accounting.getBalance(account) >= amount;
    }

    /**
     * Adds the specified amount to specified account.
     *
     * @param account
     * @param amount
     * @throws NoAccountException
     */
    public static void add(String account, double amount) {
        amount = Math.abs(amount);
        accounting.setBalance(account, accounting.getBalance(account) + amount);
    }

    /**
     * Subtracts the specified amount from specified account.
     *
     * @param account
     * @param amount
     * @throws NoAccountException
     * @throws InsufficientFundsException
     */
    public static void subtract(String account, double amount) {
        amount = Math.abs(amount);
        if (accounting.getBalance(account) >= amount) {
            accounting.setBalance(account, accounting.getBalance(account) - amount);
        } else {
            throw new InsufficientFundsException("MCCom: public void subtract(String account, double amount)", "amount");
        }
    }

    /**
     * Multiplies the specified account by the specified multiplier.
     *
     * @param account
     * @param multiplier
     * @throws NoAccountException
     * @throws NaturalNumberException
     */
    public static void multiply(String account, double multiplier) {
        multiplier = Math.abs(multiplier);
        accounting.setBalance(account, accounting.getBalance(account) * multiplier);
    }

    /**
     * Sets the specified account's balance to 0.
     *
     * @param account
     * @throws NoAccountException
     */
    public static void empty(String account) {
        accounting.setBalance(account, 0);
    }

    /**
     * Creates a new account with the specified name.
     *
     * @param account
     */
    public static void create(String account) {
        if (!exists(account)) {
            accounting.create(account);
        }
    }

    /**
     * Deletes an existing account with the specified name.
     *
     * @param account
     * @throws NoAccountException
     */
    public static void delete(String account) {
        accounting.delete(account);
    }

    /**
     * The specified amount is added to the specified FROM account and
     * subtracted from the specified TO account.
     *
     * @param accountFrom
     * @param accountTo
     * @param amount
     * @throws NoAccountException
     * @throws InsufficientFundsException
     */
    public static void transfer(String accountFrom, String accountTo, double amount) {
        if (accounting.getBalance(accountTo) >= amount) {
            accounting.setBalance(accountFrom, accounting.getBalance(accountFrom) - amount);
            accounting.setBalance(accountTo, accounting.getBalance(accountTo) + amount);
        } else {
            throw new InsufficientFundsException("MCCom: public void transfer(String accountFrom, String accountTo, double amount)", "amount");
        }
    }

    /**
     * Gets an ArrayList with all MineConomy accounts.
     *
     * @return An ArrayList of MineConomy Accounts
     */
    public static List<String> getAccounts() {
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
        return MCCom.canAfford(account, amount);
    }

    /**
     * Returns the balance of the specified account.
     *
     * @param account
     * @return Balance
     */
    public static double getExternalBalance(String account) {
        return MCCom.getBalance(account);
    }

    /**
     * Sets the specified account's balance to the specified amount.
     *
     * @param account
     * @param balance
     */
    public static void setExternalBalance(String account, double balance) {
        MCCom.setBalance(account, balance);
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
