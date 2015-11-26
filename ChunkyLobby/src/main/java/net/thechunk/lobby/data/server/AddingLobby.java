package net.thechunk.lobby.data.server;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AddingLobby {
    private String user;
    private String server;
    private String world;
}
