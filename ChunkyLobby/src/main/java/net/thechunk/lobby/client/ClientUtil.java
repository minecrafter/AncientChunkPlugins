package net.thechunk.lobby.client;

import net.thechunk.lobby.ChunkyLobby;
import net.thechunk.lobby.data.jsonobjects.ServerRequest;
import net.thechunk.lobby.data.jsonobjects.ServerResponse;

import java.io.IOException;

public class ClientUtil {
    public static ServerResponse getResponse(ServerRequest request) throws IOException {
        return ChunkyLobby.getPlugin().getGson().fromJson(ClientHandler.getConnectionManager().getResponse
                (ChunkyLobby.getPlugin().getGson().toJson(request)), ServerResponse.class);
    }
}
