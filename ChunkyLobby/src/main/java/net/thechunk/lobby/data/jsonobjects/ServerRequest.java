package net.thechunk.lobby.data.jsonobjects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServerRequest {
    private Action action;
    private String[] args;

    public enum Action {
        PING,
        GET_LOBBY_FOR_PLAYER,
        JOINED_LOBBY,
        LEFT_LOBBY,
        LOBBY_INFO,
        UPDATE_LOBBY_STATUS,
        GET_HUB_SERVER
    }
}
