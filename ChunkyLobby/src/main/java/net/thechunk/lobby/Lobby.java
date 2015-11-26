package net.thechunk.lobby;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.bukkit.util.Vector;

import java.util.ArrayList;

@Data
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
public class Lobby {
    private String name;
    private String world;
    private String server;
    private String status1;
    private String status2;
    private int cap;
    private int vipCap = 0;
    private boolean vip;
    private final List<String> players = new ArrayList<>();
    private Vector signLocation;
    private Vector portal;

    public int getRegularCap() {
        return cap;
    }

    public int getTotalCap() {
        return cap + vipCap;
    }

    public void addPlaying(String pl) {
        players.add(pl);
    }

    public void removePlaying(String pl) {
        players.remove(pl);
    }

    public boolean hasPlayer(String pl) {
        return players.contains(pl);
    }

    public int getPlaying() {
        return players.size();
    }
}
