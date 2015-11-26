package net.thechunk.lobby.data.jsonobjects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LobbyStatus {
    public String world;
    public int playing;
    public int cap;
    public boolean vip;
    public List<String> status;
}
