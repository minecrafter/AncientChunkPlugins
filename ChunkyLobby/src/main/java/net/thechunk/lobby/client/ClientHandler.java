package net.thechunk.lobby.client;

import net.thechunk.lobby.ChunkyLobby;
import net.thechunk.lobby.data.jsonobjects.ServerRequest;
import net.thechunk.lobby.data.jsonobjects.ServerResponse;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * Created with IntelliJ IDEA.
 * User: tux
 * Date: 10/11/13
 * Time: 9:17 PM
 * To change this template use File | Settings | File Templates.
 */
public class ClientHandler {
    private static ClientHandler handler;
    private static ConnectionManager connectionManager;
    private static String hub = "hub";

    public ClientHandler() throws IOException {
        handler = this;
        connectionManager = new ConnectionManager(new InetSocketAddress("127.0.0.1", 6666));
    }

    public static ClientHandler getHandler() {
        return handler;
    }

    public static String getHub() {
        return hub;
    }

    public static ConnectionManager getConnectionManager() {
        return connectionManager;
    }

    public boolean worldExists(String world) {
        return ChunkyLobby.getPlugin().getServer().getWorld(world) != null;
    }

    public void updateLobbyInformation(final UpdateType type, final String lobbyName, final String data) {
        ChunkyLobby.getPlugin().getServer().getScheduler().runTaskAsynchronously(ChunkyLobby.getPlugin(), new Runnable() {
            @Override
            public void run() {
                String[] request = new String[2];
                switch (type) {
                    case STATUS_1:
                        request[0] = "s1";
                        break;
                    case STATUS_2:
                        request[0] = "s2";
                        break;
                    default:
                        return;
                }

                request[1] = data;
                try {
                    ServerResponse sr = ClientUtil.getResponse(new ServerRequest(ServerRequest.Action.UPDATE_LOBBY_STATUS, request));
                    if (sr.getStatus() == ServerResponse.Status.ERROR) {
                        ChunkyLobby.getPlugin().getLogger().warning("Error while updating lobby: " + sr.getReply()[0]);
                    }
                } catch (IOException ignored) {
                }
            }
        });
    }
}
