package me.mjolnir.mineconomy.internal;

import java.util.List;

import org.bukkit.Bukkit;

import me.mjolnir.mineconomy.MineConomy;
import me.mjolnir.mineconomy.exceptions.AccountNameConflictException;
import me.mjolnir.mineconomy.exceptions.BankNameConflictException;
import me.mjolnir.mineconomy.exceptions.DivideByZeroException;
import me.mjolnir.mineconomy.exceptions.InsufficientFundsException;
import me.mjolnir.mineconomy.exceptions.MaxDebtException;
import me.mjolnir.mineconomy.exceptions.NaturalNumberException;
import me.mjolnir.mineconomy.exceptions.NoAccountException;
import me.mjolnir.mineconomy.exceptions.NoBankException;
import me.mjolnir.mineconomy.exceptions.NoCurrencyException;
import me.mjolnir.mineconomy.internal.util.IOH;

/**
 * Handles exterior classes reading/writing account values.
 *
 * @author MjolnirCommando
 */
public class MCCom {

    private static AccountingBase accounting = null;

    public static String getAccount(String account) {
        if (accounting.hashaccount.containsKey(account.toLowerCase())) {
            return accounting.hashaccount.get(account.toLowerCase());
        }

        Object[] accounts = accounting.treeaccount.toArray();

        try {
            return accounting.hashaccount.get(binarySearch(accounting.treeaccount.toArray(), account.toLowerCase(), 0, accounts.length - 1, ""));
        } catch (NoAccountException ex) {
            String result = accounting.loadAccount(account);
            if (result != "") {
                return result;
            } else {
                throw ex;
            }
        }

    }

    private static String binarySearch(Object[] array, String value, int left, int right, String ex1) {
        if (left > right) {
            throw new NoAccountException(ex1, "account");
        }

        int middle = (left + right) / 2;

        try {
            if (((String) array[middle]).substring(0, value.length()).equals(value)) {
                try {
                    if (((String) array[middle + 1]).substring(0, value.length()).equals(value)) {
                        throw new NoAccountException(ex1, "account");
                    }
                } catch (IndexOutOfBoundsException e) {
                    //
                }

                try {
                    if (((String) array[middle - 1]).substring(0, value.length()).equals(value)) {
                        throw new NoAccountException(ex1, "account");
                    }
                } catch (IndexOutOfBoundsException e) {
                    //
                }

                return (String) array[middle];
            }
        } catch (StringIndexOutOfBoundsException e) {
            //
        }

        int j = 0;

        while (true) {
            try {
                if (((String) array[middle]).charAt(j) > value.charAt(j)) {
                    return binarySearch(array, value, left, middle - 1, ex1);
                }

                if (((String) array[middle]).charAt(j) < value.charAt(j)) {
                    return binarySearch(array, value, middle + 1, right, ex1);
                }

                j++;
            } catch (StringIndexOutOfBoundsException e) {
                return binarySearch(array, value, middle + 1, right, ex1);
            }
        }
    }

    // MineConomy Account Methods ----------------------------------------------
    /**
     * Returns the balance of the specified player.
     *
     * @param account
     * @return double
     * @throws NoAccountException
     */
    public static double getBalance(String account) {
        return accounting.getBalance(getAccount(account));
    }

    /**
     * Sets the balance of the specified player.
     *
     * @param account
     * @param balance
     * @throws NoAccountException
     * @throws MaxDebtException
     */
    public static void setBalance(String account, double balance) {
        account = getAccount(account);
        if (balance >= -Settings.maxDebt) {
            accounting.setBalance(account, balance);
        } else {
            throw new MaxDebtException(
                    "MCCom: public void setBalance(String account, double balance)",
                    "balance");
        }
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
        account = getAccount(account);
        return accounting.getBalance(account) + Settings.maxDebt >= amount;
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
        account = getAccount(account);
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
        account = getAccount(account);
        if (accounting.getBalance(account) + Settings.maxDebt >= amount) {
            accounting.setBalance(account, accounting.getBalance(account) - amount);
        } else {
            throw new InsufficientFundsException(
                    "MCCom: public void subtract(String account, double amount)",
                    "amount");
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
        account = getAccount(account);
        accounting.setBalance(account, accounting.getBalance(account) * multiplier);
    }

    /**
     * Divides the specified account by the specified divisor.
     *
     * @param account
     * @param divisor
     * @throws NoAccountException
     * @throws NaturalNumberException
     * @throws InsufficientFundsException
     */
    public static void divide(String account, double divisor) {
        divisor = Math.abs(divisor);
        account = getAccount(account);
        if (accounting.getBalance(account) / divisor >= Settings.maxDebt) {
            if (divisor > 0) {
                accounting.setBalance(account, accounting.getBalance(account) / divisor);
            } else {
                throw new DivideByZeroException(
                        "MCCom: public void divide(String account, double divisor)",
                        "divisor");
            }
        } else {
            throw new InsufficientFundsException("MCCom: public void divide(String account, double divisor)",
                    "account");
        }
    }

    /**
     * Sets the specified account's balance to 0.
     *
     * @param account
     * @throws NoAccountException
     */
    public static void empty(String account) {
        account = getAccount(account);
        accounting.setBalance(account, 0);
    }

    /**
     * Creates a new account with the specified name.
     *
     * @param account
     * @throws AccountNameConflictException
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
        account = getAccount(account);
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
        accountFrom = getAccount(accountFrom);
        accountTo = getAccount(accountTo);
        if (accounting.getBalance(accountTo) + Settings.maxDebt >= amount) {
            accounting.setBalance(accountFrom,
                    accounting.getBalance(accountFrom) - amount);
            accounting.setBalance(accountTo,
                    accounting.getBalance(accountTo) + amount);
        } else {
            throw new InsufficientFundsException(
                    "MCCom: public void transfer(String accountFrom, String accountTo, double amount)",
                    "amount");
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

    // MineConomy Bank Methods -------------------------------------------------
    /**
     * Returns the balance of the specified account in the specified bank.
     *
     * @param bank
     * @param account
     * @return balance
     * @throws NoAccountException
     * @throws NoBankException
     */
    public static double getBalance(String bank, String account) {
        if (bankExists(bank)) {
            if (accountExists(bank, account)) {
                return Banking.getBalance(bank, account);
            } else {
                throw new NoAccountException(
                        "MCCom: public static double getBalance(String bank, String account)",
                        "account");
            }
        } else {
            throw new NoBankException(
                    "MCCom: public static double getBalance(String bank, String account)",
                    "bank");
        }
    }

    /**
     * Returns the balance of the specified account in the specified bank.
     *
     * @param bank
     * @param account
     * @param balance
     * @throws NoAccountException
     * @throws NoBankException
     * @throws MaxDebtException
     */
    public static void setBalance(String bank, String account, double balance) {
        if (bankExists(bank)) {
            if (accountExists(bank, account)) {
                if (balance >= Banking.getMaxDebt(bank)) {
                    Banking.setBalance(bank, account, balance);
                } else {
                    throw new MaxDebtException(
                            "MCCom: public static void setBalance(String bank, String account, double balance)",
                            "balance");
                }
            } else {
                throw new NoAccountException(
                        "MCCom: public static void setBalance(String bank, String account, double balance)",
                        "account");
            }
        } else {
            throw new NoBankException(
                    "MCCom: public static void setBalance(String bank, String account, double balance)",
                    "bank");
        }
    }

    /**
     * Returns true if the specified account exists in the specified bank.
     *
     * @param bank
     * @param account
     * @return True if the specified account exists in the specified bank.
     */
    public static boolean accountExists(String bank, String account) {
        return Banking.accountExists(bank, account);
    }

    /**
     * Returns true if the specified bank exists.
     *
     * @param bank
     * @return boolean
     */
    public static boolean bankExists(String bank) {
        return Banking.bankExists(bank);
    }

    /**
     * Returns true if the specified account in the specified bank has the
     * specified amount in their account.
     *
     * @param bank
     * @param account
     * @param amount
     * @return boolean
     * @throws NoAccountException
     * @throws NoBankException
     */
    public static boolean canAfford(String bank, String account, double amount) {
        if (bankExists(bank)) {
            if (accountExists(bank, account)) {
                return Banking.getBalance(bank, account) + Banking.getMaxDebt(bank) >= amount;
            } else {
                throw new NoAccountException(
                        "MCCom: public static boolean canAfford(String bank, String account, double amount)",
                        "account");
            }
        } else {
            throw new NoBankException(
                    "MCCom: public static boolean canAfford(String bank, String account, double amount)",
                    "bank");
        }
    }

    /**
     * Creates an account with the specified name in the specified bank.
     *
     * @param bank
     * @param account
     * @throws AccountNameConflictException
     */
    public static void create(String bank, String account) {
        if (Banking.accountExists(bank, account)) {
            throw new AccountNameConflictException("MCCom: public static void create(String bank, String account)", "account");
        } else {
            Banking.create(bank, account);
        }
    }

    /**
     * Creates a bank with the specified name.
     *
     * @param bank
     * @throws BankNameConflictException
     */
    public static void createBank(String bank) {
        if (Banking.bankExists(bank)) {
            throw new BankNameConflictException("MCCom: public static void createBank(String bank)", "bank");
        } else {
            Banking.create(bank);
        }
    }

    /**
     * Deletes the account with the specified name in the specified bank.
     *
     * @param bank
     * @param account
     * @throws NoAccountException
     */
    public static void delete(String bank, String account) {
        if (Banking.accountExists(bank, account)) {
            Banking.delete(bank, account);
        } else {
            throw new NoAccountException("MCCom: public static void delete(String bank, String account)", "account");
        }
    }

    /**
     * Deletes the bank with the specified name.
     *
     * @param bank
     * @throws NoBankException
     */
    public static void deleteBank(String bank) {
        if (Banking.bankExists(bank)) {
            Banking.delete(bank);
        } else {
            throw new NoBankException("MCCom: public static void deleteBank(String bank)", "bank");
        }
    }

    /**
     * Sets the specified account's balance, in the specified bank, to 0.
     *
     * @param bank
     * @param account
     * @throws NoBankException
     * @throws NoAccountException
     */
    public static void empty(String bank, String account) {
        if (Banking.bankExists(bank)) {
            if (Banking.accountExists(bank, account)) {
                Banking.setBalance(bank, account, 0);
            } else {
                throw new NoAccountException("MCCom: public static void empty(String bank, String account)", "account");
            }
        } else {
            throw new NoBankException("MCCom: public static void empty(String bank, String account)", "bank");
        }
    }

    /**
     * Renames the specified account in the respective bank to the new account
     * name in its respective bank.
     *
     * @param oldBank
     * @param oldAccount
     * @param newBank
     * @param newAccount
     * @throws NoBankException
     * @throws NoAccountException
     * @throws AccountNameConflictException
     */
    public static void rename(String oldBank, String oldAccount, String newBank, String newAccount) {
        if (Banking.bankExists(oldBank)) {
            if (Banking.bankExists(newBank)) {
                if (Banking.accountExists(oldBank, oldAccount)) {
                    if (Banking.accountExists(newBank, newAccount)) {
                        throw new AccountNameConflictException("MCCom: public static void rename(String oldBank, String oldAccount, String newBank, String newAccount)", "oldAccount");
                    } else {
                        Banking.create(newBank, newAccount);
                        Banking.setBalance(newBank, newAccount, Banking.getBalance(oldBank, oldAccount));
                        Banking.setStatus(newBank, newAccount, Banking.getStatus(oldBank, oldAccount));
                        Banking.delete(oldBank, oldAccount);
                    }
                } else {
                    throw new NoAccountException("MCCom: public static void rename(String oldBank, String oldAccount, String newBank, String newAccount)", "oldAccount");
                }
            } else {
                throw new NoBankException("MCCom: public static void rename(String oldBank, String oldAccount, String newBank, String newAccount)", "newBank");
            }
        } else {
            throw new NoBankException("MCCom: public static void rename(String oldBank, String oldAccount, String newBank, String newAccount)", "oldBank");
        }
    }

    /**
     * Renames the specified bank with the new specified name.
     *
     * @param oldBank
     * @param newBank
     * @throws NoBankException
     * @throws BankNameConflictException
     */
    public static void renameBank(String oldBank, String newBank) {
        if (Banking.bankExists(oldBank)) {
            if (Banking.bankExists(newBank)) {
                throw new BankNameConflictException("MCCom: public static void renameBank(String oldBank, String newBank)", "newBank");
            } else {
                Banking.rename(oldBank, newBank);
            }
        } else {
            throw new NoBankException("MCCom: public static void renameBank(String oldBank, String newBank)", "oldBank");
        }
    }

    /**
     * The specified amount is added to the specified FROM account and
     * subtracted from the specified TO account in their respective banks.
     *
     * @param bankFrom
     * @param accountFrom
     * @param bankTo
     * @param accountTo
     * @param amount
     * @throws NoBankException
     * @throws NoAccountException
     * @throws InsufficientFundsException
     */
    public static void transfer(String bankFrom, String accountFrom, String bankTo, String accountTo, double amount) {
        if (Banking.bankExists(bankFrom)) {
            if (Banking.bankExists(bankTo)) {
                if (Banking.accountExists(bankFrom, accountFrom)) {
                    if (Banking.accountExists(bankTo, accountTo)) {
                        if (Banking.getBalance(bankFrom, accountFrom) >= Banking.getMaxDebt(bankFrom)) {
                            Banking.setBalance(bankFrom, accountFrom, Banking.getBalance(bankFrom, accountFrom) - amount);
                            Banking.setBalance(bankTo, accountTo, Banking.getBalance(bankTo, accountTo) + amount);
                        } else {
                            throw new InsufficientFundsException("MCCom: public static void transfer(String bankFrom, String accountFrom, String bankTo, String accountTo, double amount)", "amount");
                        }
                    } else {
                        throw new NoAccountException("MCCom: public static void transfer(String bankFrom, String accountFrom, String bankTo, String accountTo, double amount)", "accountTo");
                    }
                } else {
                    throw new NoBankException("MCCom: public static void transfer(String bankFrom, String accountFrom, String bankTo, String accountTo, double amount)", "bankTo");
                }
            } else {
                throw new NoAccountException("MCCom: public static void transfer(String bankFrom, String accountFrom, String bankTo, String accountTo, double amount)", "accountFrom");
            }
        } else {
            throw new NoBankException("MCCom: public static void transfer(String bankFrom, String accountFrom, String bankTo, String accountTo, double amount)", "bankFrom");
        }
    }

    /**
     * Adds the specified amount to the specified account's balance in the
     * specified bank.
     *
     * @param bank
     * @param account
     * @param amount
     * @throws NoBankException
     * @throws NoAccountException
     */
    public static void add(String bank, String account, double amount) {
        amount = Math.abs(amount);

        if (Banking.bankExists(bank)) {
            if (Banking.accountExists(bank, account)) {
                Banking.setBalance(bank, account, Banking.getBalance(bank, account) + amount);
            } else {
                throw new NoAccountException("MCCom: public static void add(String bank, String account, double amount)", "account");
            }
        } else {
            throw new NoBankException("MCCom: public static void add(String bank, String account, double amount)", "bank");
        }
    }

    /**
     * Subtracts the specified amount from the specified account's balance in
     * the specified bank.
     *
     * @param bank
     * @param account
     * @param amount
     * @throws NoBankException
     * @throws NoAccountException
     * @throws InsufficientFundsException
     */
    public static void subtract(String bank, String account, double amount) {
        amount = Math.abs(amount);

        if (Banking.bankExists(bank)) {
            if (Banking.accountExists(bank, account)) {
                if (Banking.getBalance(bank, account) + Banking.getMaxDebt(bank) >= amount) {
                    Banking.setBalance(bank, account,
                            Banking.getBalance(bank, account) - amount);
                } else {
                    throw new InsufficientFundsException("MCCom: public static void subtract(String bank, String account, double amount)", "amount");
                }
            } else {
                throw new NoAccountException("MCCom: public static void subtract(String bank, String account, double amount)", "account");
            }
        } else {
            throw new NoBankException("MCCom: public static void subtract(String bank, String account, double amount)", "bank");
        }
    }

    /**
     * Multiplies the specified account's balance with the specified multiplier
     * in the specified bank.
     *
     * @param bank
     * @param account
     * @param multiplier
     * @throws NoBankException
     * @throws NoAccountException
     */
    public static void multiply(String bank, String account, double multiplier) {
        multiplier = Math.abs(multiplier);

        if (Banking.bankExists(bank)) {
            if (Banking.accountExists(bank, account)) {
                Banking.setBalance(bank, account, Banking.getBalance(bank, account) * multiplier);
            } else {
                throw new NoAccountException("MCCom: public static void multiply(String bank, String account, double multiplier)", "account");
            }
        } else {
            throw new NoBankException("MCCom: public static void multiply(String bank, String account, double multiplier)", "bank");
        }
    }

    /**
     * Divides the specified account's balance with the specified divisor in the
     * specified bank.
     *
     * @param bank
     * @param account
     * @param divisor
     * @throws NoBankException
     * @throws NoAccountException
     * @throws InsufficientFundsException
     */
    public static void divide(String bank, String account, double divisor) {
        divisor = Math.abs(divisor);

        if (Banking.bankExists(bank)) {
            if (Banking.accountExists(bank, account)) {
                if (Banking.getBalance(bank, account) / divisor >= Banking.getMaxDebt(bank)) {
                    Banking.setBalance(bank, account,
                            Banking.getBalance(bank, account) - divisor);
                } else {
                    throw new InsufficientFundsException("MCCom: public static void divide(String bank, String account, double divisor)", "diviosr");
                }
            } else {
                throw new NoAccountException("MCCom: public static void divide(String bank, String account, double divisor)", "account");
            }
        } else {
            throw new NoBankException("MCCom: public static void divide(String bank, String account, double divisor)", "bank");
        }
    }

    /**
     * Returns a list of all existing banks.
     *
     * @return All banks
     */
    public static List<String> getBanks() {
        return Banking.getBanks();
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
     * @param account
     * @return Balance
     */
    public static double getExternalBalance(String account) {
        return MCCom.getBalance(account);
    }

    /**
     * Sets the specified account's balance to the specified amount.
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
     * Initializes MCCom base classes. (Required for SQL)
     */
    public static void initialize() {
        if (Settings.dbtype.equalsIgnoreCase("mysql")) {
            IOH.log("MySQL is enabled for Accounts.", IOH.DEV);
            Bukkit.getConsoleSender().sendMessage("�9[Mineconomy] �fMySQL is enabled for Accounts.");
            accounting = new MySqlAccounting();
        }
        IOH.log("MySQL is not yet configured! Plugin will NOT work!", IOH.VERY_IMPORTANT);
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
