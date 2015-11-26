package net.thechunk.lobby.data.jsonobjects;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ClientResponse {
    public LobbyStatus[] lobbies;
}
