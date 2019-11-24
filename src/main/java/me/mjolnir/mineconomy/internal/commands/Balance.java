package me.mjolnir.mineconomy.internal.commands;

import java.util.ArrayList;
import java.util.List;
import me.mjolnir.mineconomy.MineConomy;
import me.mjolnir.mineconomy.exceptions.AccountNameConflictException;
import me.mjolnir.mineconomy.exceptions.BankNameConflictException;
import me.mjolnir.mineconomy.exceptions.InsufficientFundsException;
import me.mjolnir.mineconomy.exceptions.MaxDebtException;
import me.mjolnir.mineconomy.exceptions.NoAccountException;
import me.mjolnir.mineconomy.exceptions.NoBankException;
import me.mjolnir.mineconomy.internal.MCCom;
import me.mjolnir.mineconomy.internal.MCLang;
import me.mjolnir.mineconomy.internal.util.MCFormat;
import org.bukkit.entity.Player;

/**
 * Keeps MCCommandExecutor code neater.
 *
 * @author MjolnirCommando
 */
public class Balance {

    /**
     * Sends the help message to player.
     *
     * @param player
     * @param page
     */
    public static void help(Player player, int page) {
        String pgcontent = "";

        switch (page) {
            case 1:
                pgcontent = MCLang.messageHelp1;
                break;
            case 2:
                pgcontent = MCLang.messageHelp2;
                break;
            case 3:
                pgcontent = MCLang.messageHelp3;
                break;
            default:
                break;
        }

        String[] breaks = pgcontent.split("<br>");

        for (int i = 0; breaks.length > i; i++) {
            player.sendMessage(breaks[i]);
        }
    }

    /**
     * Checks the player's balance.
     *
     * @param player
     */
    public static void check(Player player) {
        double balance = 0;

        try {
            balance = MCCom.getBalance(player.getName());
        } catch (NoAccountException e) {
            noAccount(player);
            return;
        }

        String[] args = {balance + ""};

        player.sendMessage(MCLang.tag + MCLang.parse(MCLang.messageAccountBalance, args));
    }

    /**
     * Gets a player's balance.
     *
     * @param player
     * @param toPlayer
     */
    public static void get(Player player, String toPlayer) {
        try {
            toPlayer = MCCom.getAccount(toPlayer);
        } catch (NoAccountException e) {
            noAccount(player);
            return;
        }
        String[] args = {toPlayer, MCCom.getBalance(toPlayer) + ""};

        player.sendMessage(MCLang.tag + MCLang.parse(MCLang.messageGetBalance, args));
    }

    /**
     * Sets a player's balance.
     *
     * @param player
     * @param toPlayer
     * @param amount
     */
    public static void set(Player player, String toPlayer, double amount) {
        try {
            toPlayer = MCCom.getAccount(toPlayer);
        } catch (NoAccountException e) {
            noAccount(player);
            return;
        }
        try {
            MCCom.setBalance(toPlayer, amount);

            String[] args = {toPlayer, MCCom.getBalance(toPlayer) + ""};

            player.sendMessage(MCLang.tag + MCLang.parse(MCLang.messageSetBalance, args));
        } catch (MaxDebtException e) {
            player.sendMessage(MCLang.tag + MCLang.errorMaxDebt);
        }
    }

    /**
     * Pays a player.
     *
     * @param player
     * @param toPlayer
     * @param payAmount
     */
    public static void pay(Player player, String toPlayer, double payAmount) {
        String name = player.getName();
        try {
            name = MCCom.getAccount(name);
        } catch (NoAccountException e) {
            noAccount(player);
            return;
        }
        double balance = MCCom.getBalance(name);
        double amount = Math.abs(payAmount);

        try {
            toPlayer = MCCom.getAccount(toPlayer);
        } catch (NoAccountException e) {
            noAccount(player);
            return;
        }

        if (MCCom.canAfford(name, amount)) {
            MCCom.setBalance(name, balance - amount);
            double toBalance = MCCom.getBalance(toPlayer);
            
            MCCom.setBalance(toPlayer, toBalance + amount);

            String[] args = {amount + "", toPlayer};

            player.sendMessage(MCLang.tag
                    + MCLang.parse(MCLang.messagePayedTo, args));
            try {
                Player reciever = MineConomy.getInstance().getServer().getPlayer(toPlayer);

                String[] args2 = {name, amount + ""};

                reciever.sendMessage(MCLang.tag + MCLang.parse(MCLang.messagePayedFrom, args2));
            } catch (NullPointerException e) {
                //IOH.error("NullPointerException", e); In case player is offline
            }
        } else {
            player.sendMessage(MCLang.tag
                    + MCLang.errorYouEnough);
        }
    }

    /**
     * Gives a player money.
     *
     * @param player
     * @param toPlayer
     * @param payAmount
     */
    public static void give(Player player, String toPlayer, String payAmount) {
        try {
            toPlayer = MCCom.getAccount(toPlayer);
        } catch (NoAccountException e) {
            noAccount(player);
            return;
        }
        double amount = Double.parseDouble(payAmount);
        amount += MCCom.getBalance(toPlayer);
        MCCom.setBalance(toPlayer, amount);

        String[] args = {toPlayer, payAmount};

        player.sendMessage(MCLang.tag + MCLang.parse(MCLang.messageGive, args));
    }

    /**
     * Takes money from a player.
     *
     * @param player
     * @param toPlayer
     * @param takeAmount
     */
    public static void take(Player player, String toPlayer, String takeAmount) {
        try {
            toPlayer = MCCom.getAccount(toPlayer);
        } catch (NoAccountException e) {
            noAccount(player);
            return;
        }
        double amount = Double.parseDouble(takeAmount);
        double balance = MCCom.getBalance(toPlayer);
        if (MCCom.canAfford(toPlayer, amount)) {
            MCCom.setBalance(toPlayer, balance - amount);

            String[] args = {amount + "", toPlayer};

            player.sendMessage(MCLang.tag
                    + MCLang.parse(MCLang.messageTook, args));
        } else {
            String[] args = {toPlayer};
            player.sendMessage(MCLang.tag
                    + MCLang.parse(MCLang.errorTheyEnough, args));
        }
    }

    /**
     * Sets a player's balance to 0.
     *
     * @param player
     * @param toPlayer
     */
    public static void empty(Player player, String toPlayer) {
        try {
            toPlayer = MCCom.getAccount(toPlayer);
        } catch (NoAccountException e) {
            noAccount(player);
            return;
        }
        MCCom.setBalance(toPlayer, 0);
        try {
            String[] args = {toPlayer};

            player.sendMessage(MCLang.tag
                    + MCLang.parse(MCLang.messageEmpty, args));
        } catch (NullPointerException e) {
            // IGNORE
        }
    }

    /**
     * Creates a new account.
     *
     * @param player
     * @param toPlayer
     */
    public static void create(Player player, String toPlayer) {
        String[] args = {toPlayer};

        try {
            MCCom.create(toPlayer);

            player.sendMessage(MCLang.tag + MCLang.parse(MCLang.messageCreated, args));
        } catch (AccountNameConflictException e) {
            player.sendMessage(MCLang.tag + MCLang.parse(MCLang.errorAccountExists, args));
        }
    }

    /**
     * Deletes an account.
     *
     * @param player
     * @param toPlayer
     */
    public static void delete(Player player, String toPlayer) {
        try {
            toPlayer = MCCom.getAccount(toPlayer);
        } catch (NoAccountException e) {
            noAccount(player);
            return;
        }
        MCCom.delete(toPlayer);

        String[] args = {toPlayer};

        player.sendMessage(MCLang.tag + MCLang.parse(MCLang.messageDeleted, args));
    }
     
    /**
     * Check's the player's bank balance.
     *
     * @param bank
     * @param player
     */
    public static void check(String bank, Player player) {
        double balance = 0;

        try {
            balance = MCCom.getBalance(bank, player.getName());
        } catch (NoAccountException e) {
            noAccount(player, bank);
            return;
        } catch (NoBankException e) {
            noBank(player, bank);
            return;
        }

        String[] args = {bank, balance + ""};

        player.sendMessage(MCLang.tag + MCLang.parse(MCLang.messageBankBalance, args));
    }

    /**
     * Creates new bank.
     *
     * @param player
     * @param bank
     */
    public static void createBank(Player player, String bank) {
        String[] args = {bank};
        try {
            MCCom.createBank(bank);
            player.sendMessage(MCLang.tag + MCLang.parse(MCLang.messageBankCreated, args));
        } catch (BankNameConflictException e) {
            player.sendMessage(MCLang.tag + MCLang.parse(MCLang.errorBankExists, args));
        }
    }

    /**
     * Creates new bank account.
     *
     * @param player
     * @param bank
     * @param account
     */
    public static void create(Player player, String bank, String account) {
        String[] args = {account, bank};

        try {
            MCCom.create(bank, account);
            player.sendMessage(MCLang.tag + MCLang.parse(MCLang.messageBankAccountCreated, args));
        } catch (AccountNameConflictException e) {
            player.sendMessage(MCLang.tag + MCLang.parse(MCLang.errorBankAccountExists, args));
        }
    }

    /**
     * Deletes specified bank.
     *
     * @param player
     * @param bank
     */
    public static void deleteBank(Player player, String bank) {
        String[] args = {bank};
        try {
            MCCom.deleteBank(bank);
            player.sendMessage(MCLang.tag + MCLang.parse(MCLang.messageBankDeleted, args));
        } catch (NoBankException e) {
            noBank(player, bank);
        }
    }

    /**
     * Deletes specified bank account.
     *
     * @param player
     * @param bank
     * @param account
     */
    public static void delete(Player player, String bank, String account) {
        String[] args = {account, bank};

        try {
            MCCom.delete(bank, account);
            player.sendMessage(MCLang.tag + MCLang.parse(MCLang.messageBankAccountDeleted, args));
        } catch (NoAccountException e) {
            noAccount(player, bank);
        }
    }

    /**
     * Displays balance of specified bank account.
     *
     * @param player
     * @param bank
     * @param account
     */
    public static void get(Player player, String bank, String account) {
        try {
            double balance = MCCom.getBalance(bank, account);
            String[] args = {account, bank, balance + ""};
            player.sendMessage(MCLang.tag + MCLang.parse(MCLang.messageGetBankAccount, args));
        } catch (NoAccountException e) {
            noAccount(player, bank);
        } catch (NoBankException e) {
            noBank(player, bank);
        }
    }

    /**
     * Sets account balance.
     *
     * @param player
     * @param bank
     * @param account
     * @param balance
     */
    public static void set(Player player, String bank, String account, double balance) {
        try {
            MCCom.setBalance(bank, account, balance);
            String[] args = {account, bank, balance + ""};
            player.sendMessage(MCLang.tag + MCLang.parse(MCLang.messageSetBankAccount, args));
        } catch (NoAccountException e) {
            noAccount(player, bank);
        } catch (NoBankException e) {
            noBank(player, bank);
        } catch (MaxDebtException e) {
            player.sendMessage(MCLang.tag + MCLang.errorMaxDebt);
        }
    }

    /**
     * Deposits amount in bank account.
     *
     * @param player
     * @param bank
     * @param account
     * @param amount
     */
    public static void deposit(Player player, String bank, double amount) {
        amount = Math.abs(amount);

        try {
            MCCom.subtract(player.getName(), amount);

            double toBalance = MCCom.getBalance(bank, player.getName());

            MCCom.setBalance(bank, player.getName(), toBalance + amount);

            String[] args = {bank, player.getName(), amount + ""};
            player.sendMessage(MCLang.tag + MCLang.parse(MCLang.messageBankAccountDeposit, args));
        } catch (NoAccountException e) {
            if (e.getMethodCause().contains("subtract")) {
                noAccount(player);
            } else {
                noAccount(player, bank);
            }
        } catch (NoBankException e) {
            noBank(player, bank);
        } catch (InsufficientFundsException e) {
            player.sendMessage(MCLang.tag + MCFormat.color(MCLang.errorYouEnough));
        }
    }

    /**
     * Withdraws amount from bank account.
     *
     * @param player
     * @param bank
     * @param account
     * @param amount
     */
    public static void withdraw(Player player, String bank, double amount) {
        amount = Math.abs(amount);

        try {
            MCCom.subtract(bank, player.getName(), amount);

            double toBalance = MCCom.getBalance(player.getName());          
            MCCom.setBalance(player.getName(), toBalance - amount);

            String[] args = {bank, player.getName(), amount + ""};
            player.sendMessage(MCLang.tag + MCLang.parse(MCLang.messageBankAccountWithdraw, args));
        } catch (NoAccountException e) {
            if (e.getMethodCause().contains("add")) {
                noAccount(player);
            } else {
                noAccount(player, bank);
            }
        } catch (NoBankException e) {
            noBank(player, bank);
        } catch (InsufficientFundsException e) {
            String[] args = {player.getName()};
            player.sendMessage(MCLang.tag
                    + MCLang.parse(MCLang.errorTheyEnough, args));
        }
    }

    /**
     * Empties specified bank account.
     *
     * @param player
     * @param bank
     * @param account
     */
    public static void empty(Player player, String bank, String account) {
        try {
            MCCom.empty(bank, account);
            String[] args = {account, bank};
            player.sendMessage(MCLang.tag + MCLang.parse(MCLang.messageEmptyBankAccount, args));
        } catch (NoAccountException e) {
            noAccount(player, bank);
        } catch (NoBankException e) {
            noBank(player, bank);
        }
    }

    /**
     * Renames specified bank to specified name.
     *
     * @param player
     * @param bank
     * @param newBank
     */
    public static void renameBank(Player player, String bank, String newBank) {
        String[] args = {newBank, bank};
        try {
            MCCom.renameBank(bank, newBank);
            player.sendMessage(MCLang.tag + MCLang.parse(MCLang.messageBankRenamed, args));
        } catch (NoBankException e) {
            noBank(player, bank);
        } catch (BankNameConflictException e) {
            player.sendMessage(MCLang.tag + MCLang.parse(MCLang.errorBankExists, args));
        }
    }

    /**
     * Renames specified bank account to specified bank account.
     *
     * @param player
     * @param bank
     * @param account
     * @param newBank
     * @param newAccount
     */
    public static void rename(Player player, String bank, String account, String newBank, String newAccount) {
        String[] args = {newAccount, newBank, account, bank};

        try {
            MCCom.rename(bank, account, newBank, newAccount);
            player.sendMessage(MCLang.tag + MCLang.parse(MCLang.messageBankAccountRenamed, args));
        } catch (NoBankException e) {
            if (e.getVariableCause().equals("oldBank")) {
                noBank(player, bank);
            } else {
                noBank(player, newBank);
            }
        } catch (NoAccountException e) {
            noAccount(player, bank);
        } catch (AccountNameConflictException e) {
            player.sendMessage(MCLang.tag + MCLang.parse(MCLang.errorBankAccountExists, args));
        }
    }

    /**
     * Transfers specified amount between the specified bank accounts.
     *
     * @param player
     * @param bank
     * @param account
     * @param toBank
     * @param toAccount
     * @param amount
     */
    public static void transfer(Player player, String bank, String account, String toBank, String toAccount, double amount) {
        amount = Math.abs(amount);

        try {
            MCCom.transfer(bank, account, toBank, toAccount, amount);
        } catch (NoBankException e) {
            if (e.getVariableCause().equals("bankFrom")) {
                noBank(player, bank);
            } else {
                noBank(player, toBank);
            }
        } catch (NoAccountException e) {
            if (e.getVariableCause().equals("accountFrom")) {
                noAccount(player, account);
            } else {
                noAccount(player, toAccount);
            }
        } catch (InsufficientFundsException e) {
            player.sendMessage(MCLang.tag + MCFormat.color(MCLang.errorYouEnough));
        }
    }

    private static void noAccount(Player p) {
        p.sendMessage(MCLang.tag + MCLang.errorNoAccount);
    }

    private static void noAccount(Player p, String bank) {
        String[] args = {bank};

        p.sendMessage(MCLang.tag + MCLang.parse(MCLang.errorNoBankAccount, args));
    }

    private static void noBank(Player p, String bank) {
        String[] args = {bank};

        p.sendMessage(MCLang.tag + MCLang.parse(MCLang.errorNoBank, args));
    }
}
