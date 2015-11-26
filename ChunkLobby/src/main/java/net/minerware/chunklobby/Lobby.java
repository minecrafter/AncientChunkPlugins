package net.minerware.chunklobby;

import lombok.Data;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

@Data
public class Lobby {
    private String name;
    private String world;
    private String server;
    private String status1;
    private String status2;
    private int cap;
    private int vipCap = 0;
    private String ip = "127.0.0.1";
    private int port = 25565;
    private boolean vip;
    private List<String> players = new ArrayList<String>();
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
