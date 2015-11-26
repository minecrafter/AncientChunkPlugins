package net.thechunk.lobby.client;

import net.thechunk.lobby.ChunkyLobby;
import net.thechunk.lobby.data.jsonobjects.ServerRequest;
import net.thechunk.lobby.data.jsonobjects.ServerResponse;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: tux
 * Date: 10/11/13
 * Time: 9:14 PM
 * To change this template use File | Settings | File Templates.
 */
public class ClientListener implements Listener {
    private final Map<String, String> lobbyJoins = new Hashtable<>();

    @EventHandler
    public void onAsyncPreLogin(AsyncPlayerPreLoginEvent event) {
        String lobby;
        String world = "";
        int count = 0;
        int maxTries = 3;
        boolean success = false;
        while (!success && count <= maxTries) {
            try {
                ServerResponse sr1 = ClientUtil.getResponse(new ServerRequest(ServerRequest.Action.GET_LOBBY_FOR_PLAYER,
                        new String[]{event.getName()}));
                if (sr1.getStatus() == ServerResponse.Status.OK) {
                    lobby = sr1.getReply()[0];
                    world = ClientUtil.getResponse(new ServerRequest(ServerRequest.Action.LOBBY_INFO, new String[]{lobby}))
                            .getReply()[0];
                } else {
                    ChunkyLobby.getPlugin().getLogger().severe("Error while handling player " + event.getName() + ": " + sr1.getReply()[0]);
                    event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "An internal error has occurred. We are working on fixing it.");
                }
                success = true;
            } catch (IOException e) {
                count++;
                e.printStackTrace();
            }
        }

        lobbyJoins.put(event.getName(), world);
    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        if (lobbyJoins.containsKey(event.getPlayer().getName())) {
            if (!ClientHandler.getHandler().worldExists(lobbyJoins.get(event.getPlayer().getName()))) {
                event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "Lobby information incomplete. Ask the administrator.");
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event) {
        try {
            ServerRequest sr = new ServerRequest(ServerRequest.Action.JOINED_LOBBY, new String[]{lobbyJoins.get(event.getPlayer().getName()), event.getPlayer().getName()});
            ClientHandler.getConnectionManager().send(ChunkyLobby.getPlugin().getGson().toJson(sr));
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!event.isAsynchronous()) {
            ChunkyLobby.getPlugin().getServer().getPluginManager().callEvent(new LobbyJoinEvent(event.getPlayer(),
                    ChunkyLobby.getPlugin().getServer().getWorld(lobbyJoins.get(event.getPlayer().getName()))));
        } else {
            ChunkyLobby.getPlugin().getServer().getScheduler().runTask(ChunkyLobby.getPlugin(), new Runnable() {
                @Override
                public void run() {
                    ChunkyLobby.getPlugin().getServer().getPluginManager().callEvent(new LobbyJoinEvent(event.getPlayer(),
                            ChunkyLobby.getPlugin().getServer().getWorld(lobbyJoins.get(event.getPlayer().getName()))));
                }
            });
        }
        lobbyJoins.remove(event.getPlayer().getName());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        try {
            ServerRequest sr = new ServerRequest(ServerRequest.Action.LEFT_LOBBY, new String[]{event.getPlayer().getName()});
            ClientHandler.getConnectionManager().send(ChunkyLobby.getPlugin().getGson().toJson(sr));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
