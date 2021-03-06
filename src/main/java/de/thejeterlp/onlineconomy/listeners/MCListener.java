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
package de.thejeterlp.onlineconomy.listeners;

import de.thejeterlp.onlineconomy.Locales;
import de.thejeterlp.onlineconomy.MCCom;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class MCListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (!(MCCom.exists(player.getUniqueId()))) {
            MCCom.create(player.getUniqueId());
            player.sendMessage(Locales.ACCOUNT_CREATED.getString());
        }

        player.sendMessage(Locales.MESSAGE_WELCOME.replace("%balance%", MCCom.getBalance(player.getUniqueId()) + "").replaceAll("%player%", player.getDisplayName()));
    }
}
