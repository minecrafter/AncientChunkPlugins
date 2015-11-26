package net.minerware.chunklobby;

import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.util.Vector;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import redis.clients.jedis.Jedis;

class ChunkLobbyListener implements Listener {

    private final ChunkLobby plugin;

    public ChunkLobbyListener(ChunkLobby chunkLobby) {
        plugin = chunkLobby;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (plugin.isHub()) {
            event.getPlayer().teleport(plugin.getServer().getWorlds().get(0).getSpawnLocation());
            event.getPlayer().setGameMode(GameMode.ADVENTURE);
        } else {
            Jedis redis = ChunkLobby.getRedisPool().getResource();
            String joining = "";
            // Call the player join event.
            try {
                joining = redis.get("lobby:" + plugin.hubServer + ":" + event.getPlayer().getName());
                redis.del("lobby:" + plugin.hubServer + ":" + event.getPlayer().getName());
            } finally {
                ChunkLobby.getRedisPool().returnResource(redis);
            }
            if (joining == null || joining.equals("")) {
                event.getPlayer().kickPlayer("No lobby was found for you to join.");
                return;
            }
            LobbyJoinEvent lje = new LobbyJoinEvent(event.getPlayer(), plugin.lobbies.get(joining));
            plugin.getServer().getPluginManager().callEvent(lje);
            if (lje.isCancelled()) {
                event.getPlayer().kickPlayer("Your join request was rejected.");
                return;
            }
        }
    }

    @EventHandler
    public void onPlayerMove(final PlayerMoveEvent event) {
        if (!plugin.isHub()) {
            return;
        }
        Vector pot = event.getTo().toVector();
        for (Lobby l : plugin.getLobbies().values()) {
            if (l.getPortal().getBlockX() == pot.getBlockX() && l.getPortal().getBlockY() == pot.getBlockY() ||
                    l.getPortal().getBlockZ() == pot.getBlockZ()) {
                plugin.joinLobby(event.getPlayer(), l.getName());
                break;
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (plugin.isHub()) return;
        for (Lobby l : plugin.lobbies.values()) {
            if (l.hasPlayer(event.getPlayer().getName())) {
                l.removePlaying(event.getPlayer().getName());
            }
        }
    }

    @EventHandler
    public void onServerMOTD(ServerListPingEvent event) {
        if (plugin.isHub()) return;
        JSONArray lobbies = new JSONArray();
        JSONObject out = new JSONObject();
        for (Lobby l : plugin.lobbies.values()) {
            if (l.getServer().equals(plugin.server)) {
                JSONObject lo = new JSONObject();
                lo.put("name", l.getName());
                lo.put("playing", l.getPlaying());
                lo.put("cap", l.getCap());
                lo.put("vip", l.isVip());
                lo.put("status1", l.getStatus1());
                lo.put("status2", l.getStatus2());
                lobbies.add(lo);
            }
        }
        out.put("lobbies", lobbies);
        event.setMotd(out.toJSONString());
    }
}
