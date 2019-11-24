package me.mjolnir.mineconomy.internal.listeners;

import me.mjolnir.mineconomy.MineConomy;
import me.mjolnir.mineconomy.internal.MCCom;
import me.mjolnir.mineconomy.internal.MCLang;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Listens for players' actions.
 *
 * @author MjolnirCommando
 */
public class MCListener implements Listener {

    /**
     * MineConomy shortcut to plugin.
     */
    public static MineConomy plugin;

    /**
     * Fires when player joins the game.
     *
     * @param event
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (!(MCCom.exists(player.getName()))) {

            if (player.hasPermission("mineconomy.account.have")) {
                MCCom.create(player.getName());
                player.sendMessage(MCLang.tag + MCLang.messageWelcomeAccountCreated);
            } else {
                player.sendMessage(MCLang.tag + MCLang.errorPermissionHaveAccount);
                player.sendMessage(" ");
                return;
            }
        }

        if (MCLang.displayWelcome) {
            String[] args = {player.getName() + "", MCCom.getBalance(player.getName()) + ""};

            player.sendMessage(MCLang.tag + MCLang.parse(MCLang.welcomeMessage, args));
            player.sendMessage(" ");
        }        
    }
}
