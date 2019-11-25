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
package de.thejeterlp.onlineconomy.commands;

import de.thejeterlp.onlineconomy.Locales;
import de.thejeterlp.onlineconomy.MCCom;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Handles commands given to OnlineConomy.
 *
 * @author TheJeterLP
 */
public class ChatExecutor implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("mc") || command.getName().equalsIgnoreCase("money")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(Locales.ERROR_WRONG_USER.getString());
                return true;
            }

            Player player = (Player) sender;

            try {
                if (player.hasPermission("OnlineConomy.account.have")) {
                    boolean HasAccount = MCCom.exists(player.getUniqueId());
                    if (HasAccount == false) {
                        MCCom.create(player.getUniqueId());
                    }
                } else {
                    warn(player);
                }

                switch (args.length) {
                    case 0:
                        if (player.hasPermission("OnlineConomy.balance.check")) {
                            Balance.check(player);
                        } else {
                            warn(player);
                        }
                        break;
                    case 1:
                        if (args[0].equalsIgnoreCase("balance")) {
                            if (player.hasPermission("OnlineConomy.balance.check")) {
                                Balance.check(player);
                            } else {
                                warn(player);
                            }
                        } else if (args[0].equalsIgnoreCase("help")) {
                            if (player.hasPermission("OnlineConomy.help")) {
                                Balance.help(player, 1);
                            } else {
                                warn(player);
                            }
                        } else {
                            player.sendMessage(Locales.ERROR_WRONG_USE.getString());
                        }
                        break;
                    case 2:
                        if (args[0].equalsIgnoreCase("get")) {
                            if (player.hasPermission("OnlineConomy.balance.get")) {
                                Balance.get(player, args[1]);
                            } else {
                                warn(player);
                            }
                        } else if (args[0].equalsIgnoreCase("empty")) {
                            if (player.hasPermission("OnlineConomy.balance.empty")) {
                                Balance.empty(player, args[1]);
                            } else {
                                warn(player);
                            }
                        } else if (args[0].equalsIgnoreCase("help")) {
                            if (player.hasPermission("OnlineConomy.help")) {
                                try {
                                    Balance.help(player, Integer.parseInt(args[1]));
                                } catch (NumberFormatException e) {
                                    player.sendMessage(Locales.ERROR_INT.getString());
                                }
                            } else {
                                warn(player);
                            }
                        } else {
                            player.sendMessage(Locales.ERROR_WRONG_USE.getString());
                        }
                        break;
                    case 3:
                        if (args[0].equalsIgnoreCase("pay")) {
                            if (player.hasPermission("OnlineConomy.balance.pay")) {
                                try {
                                    Balance.pay(player, args[1], Double.parseDouble(args[2]));
                                } catch (NumberFormatException e) {
                                    player.sendMessage(Locales.ERROR_MONEY_FORMAT.getString());
                                }
                            } else {
                                warn(player);
                            }
                        } else if (args[0].equalsIgnoreCase("set")) {
                            if (player.hasPermission("OnlineConomy.balance.set")) {
                                try {
                                    Balance.set(player, args[1], Double.parseDouble(args[2]));
                                } catch (NumberFormatException e) {
                                    player.sendMessage(Locales.ERROR_MONEY_FORMAT.getString());
                                }
                            } else {
                                warn(player);
                            }
                        } else if (args[0].equalsIgnoreCase("give")) {
                            if (player.hasPermission("OnlineConomy.balance.give")) {
                                Balance.give(player, args[1], args[2]);
                            } else {
                                warn(player);
                            }
                        } else if (args[0].equalsIgnoreCase("take")) {
                            if (player.hasPermission("OnlineConomy.balance.take")) {
                                Balance.take(player, args[1], args[2]);
                            } else {
                                warn(player);
                            }
                        } else {
                            player.sendMessage(Locales.ERROR_WRONG_USE.getString());
                        }
                        break;
                    default:
                        player.sendMessage(Locales.ERROR_WRONG_USE.getString());
                        break;
                }
            } catch (IndexOutOfBoundsException e) {
                player.sendMessage(Locales.ERROR_INVALID_ARGS.getString());
                e.printStackTrace();
            }
        }
        return true;

    }

    private void warn(Player player) {
        player.sendMessage(Locales.ERROR_NOPERM.getString());
    }
}
