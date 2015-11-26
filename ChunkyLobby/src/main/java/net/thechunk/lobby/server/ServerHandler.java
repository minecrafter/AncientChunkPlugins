package net.thechunk.lobby.server;

import net.thechunk.lobby.ChunkyLobby;
import net.thechunk.lobby.Lobby;
import net.thechunk.lobby.utils.BungeeCordUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Created with IntelliJ IDEA.
 * User: tux
 * Date: 10/12/13
 * Time: 9:40 PM
 * To change this template use File | Settings | File Templates.
 */
public class ServerHandler {
    public static void joinLobby(Player player, Lobby l) {
        if ((l.getPlaying() < l.getRegularCap() && !l.isVip()) || (player.hasPermission("chunklobby.vip.extra") &&
                l.getPlaying() < l.getTotalCap())) {
            ChunkyLobby.getPlugin().addMove(player, l);
            BungeeCordUtils.connect(player, l.getServer());
        } else {
            if ((l.getPlaying() >= l.getRegularCap() && l.getTotalCap() > l.getPlaying())
                    && !player.hasPermission("chunklobby.vip.extra")) {
                player.sendMessage(ChatColor.BLUE + "[Lobby] " + ChatColor.RED +
                        "This lobby is full! Buy a rank from http://thechunk.net/shop/ to access this lobby.");
                return;
            }
            if (l.isVip() && !player.hasPermission("chunklobby.vip.extra")) {
                player.sendMessage(ChatColor.BLUE + "[Lobby] " + ChatColor.RED +
                        "This lobby is a premium lobby. Buy a rank from http://thechunk.net/shop/ to access this lobby.");
                return;
            }
            if (l.getPlaying() >= l.getTotalCap()) {
                player.sendMessage(ChatColor.BLUE + "[Lobby] " + ChatColor.RED +
                        "This lobby is full!");
            }
        }
    }
}
