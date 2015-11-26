package net.thechunk.lobby.server;

import net.thechunk.lobby.ChunkyLobby;
import net.thechunk.lobby.Lobby;
import net.thechunk.lobby.data.server.AddingLobby;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: tux
 * Date: 10/11/13
 * Time: 8:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class ServerListener implements Listener {

    public static List<AddingLobby> addingPortals = new ArrayList<>();

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {

    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (!ChunkyLobby.getPlugin().isHub()) {
            return;
        }
        Vector pot = event.getTo().toVector();
        for (Lobby l : ChunkyLobby.getPlugin().getLobbies().values()) {
            if (l.getPortal().getBlockX() == pot.getBlockX() && l.getPortal().getBlockY() == pot.getBlockY() ||
                    l.getPortal().getBlockZ() == pot.getBlockZ()) {
                ServerHandler.joinLobby(event.getPlayer(), l);
                break;
            }
        }
    }
}
