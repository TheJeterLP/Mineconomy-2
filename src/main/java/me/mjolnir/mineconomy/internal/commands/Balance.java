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
package me.mjolnir.mineconomy.internal.commands;

import me.mjolnir.mineconomy.Locales;
import me.mjolnir.mineconomy.MineConomy;
import me.mjolnir.mineconomy.exceptions.NoAccountException;
import me.mjolnir.mineconomy.internal.MCCom;
import org.bukkit.entity.Player;

/**
 * Keeps MCCommandExecutor code neater.
 *
 * @author TheJeterLP
 */
public class Balance {

    /**
     * Sends the help message to player.
     *
     * @param player
     * @param page
     */
    public static void help(Player player, int page) {
        String pgcontent = Locales.HELP_1.getString();
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

        player.sendMessage(Locales.ACCOUNT_BALANCE.replace("%balance%", String.valueOf(balance)));
    }

    /**
     * Gets a player's balance.
     *
     * @param player
     * @param toPlayer
     */
    public static void get(Player player, String toPlayer) {
        String msg = Locales.ACCOUNT_BALANCE.replace("%balance%", MCCom.getBalance(toPlayer) + "");
        player.sendMessage(msg);
    }

    /**
     * Sets a player's balance.
     *
     * @param player
     * @param toPlayer
     * @param amount
     */
    public static void set(Player player, String toPlayer, double amount) {
        MCCom.setBalance(toPlayer, amount);
        player.sendMessage(Locales.MESSAGE_BALANCE_SET.replace("%amount%", MCCom.getBalance(toPlayer) + "").replaceAll("%player%", toPlayer));
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
        double balance = MCCom.getBalance(name);
        double amount = Math.abs(payAmount);

        if (MCCom.canAfford(name, amount)) {
            MCCom.setBalance(name, balance - amount);
            double toBalance = MCCom.getBalance(toPlayer);

            MCCom.setBalance(toPlayer, toBalance + amount);
            player.sendMessage(Locales.MESSAGE_PAYED_TO.replace("%amount%", amount + "").replaceAll("%player%", toPlayer));

            Player reciever = MineConomy.getInstance().getServer().getPlayer(toPlayer);

            reciever.sendMessage(Locales.MESSAGE_PAYED_FROM.replace("%amount%", amount + "").replaceAll("%player%", name));
        } else {
            player.sendMessage(Locales.ERROR_YOU_ENOUGH.getString());
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
        double amount = Double.parseDouble(payAmount);
        amount += MCCom.getBalance(toPlayer);
        MCCom.setBalance(toPlayer, amount);
        player.sendMessage(Locales.MESSAGE_GIVE.replace("%player%", toPlayer).replaceAll("%amount%", payAmount));
    }

    /**
     * Takes money from a player.
     *
     * @param player
     * @param toPlayer
     * @param takeAmount
     */
    public static void take(Player player, String toPlayer, String takeAmount) {
        double amount = Double.parseDouble(takeAmount);
        double balance = MCCom.getBalance(toPlayer);
        if (MCCom.canAfford(toPlayer, amount)) {
            MCCom.setBalance(toPlayer, balance - amount);
            player.sendMessage(Locales.MESSAGE_TOOK.replace("%amount%", amount + "").replaceAll("%player%", toPlayer));
        } else {
            player.sendMessage(Locales.ERROR_THEY_ENOUGH.replace("%player%", toPlayer));
        }
    }

    /**
     * Sets a player's balance to 0.
     *
     * @param player
     * @param toPlayer
     */
    public static void empty(Player player, String toPlayer) {
        MCCom.setBalance(toPlayer, 0);
        player.sendMessage(Locales.MESSAGE_EMPTY.replace("%player%", toPlayer));
    }

    private static void noAccount(Player p) {
        p.sendMessage(Locales.ERROR_NO_ACCOUNT.getString());
    }

}
