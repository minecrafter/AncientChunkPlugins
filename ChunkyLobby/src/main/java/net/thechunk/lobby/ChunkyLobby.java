package net.thechunk.lobby;

import com.google.common.collect.ImmutableMap;
import net.thechunk.chunklib.bukkit.Worlds;
import net.thechunk.lobby.client.ClientCommands;
import net.thechunk.lobby.client.ClientHandler;
import net.thechunk.lobby.client.ClientListener;
import net.thechunk.lobby.server.NettyLauncher;
import net.thechunk.lobby.server.ServerCommands;
import net.thechunk.lobby.server.ServerHandler;
import net.thechunk.lobby.server.ServerListener;
import com.google.gson.Gson;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ChunkyLobby extends JavaPlugin {
    private static ChunkyLobby plugin;
    private final Map<String, Lobby> lobbies = new HashMap<>();
    private final Map<String, String> lobbyMoves = Collections.synchronizedMap(new HashMap<String, String>());
    private Gson gson = new Gson();

    public static ChunkyLobby getPlugin() {
        return plugin;
    }

    public boolean isHub() {
        return getConfig().getBoolean("hub", false);
    }

    public void addMove(Player player, Lobby lobby) {
        lobbyMoves.put(player.getName(), lobby.getName());
    }

    public void onEnable() {
        long start = System.currentTimeMillis();
        plugin = this;

        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        saveDefaultConfig();

        if (isHub()) {
            getLogger().info("Running as hub.");
            getLogger().info("Setting up hub world.");
            Worlds.initialize(getServer().getWorlds().get(0).getName());
            getLogger().info("Loading lobbies.");
            for (String serverName : getConfig().getConfigurationSection("servers").getKeys(false)) {
                for (String worldName : getConfig().getConfigurationSection("servers." + serverName + ".lobbies").getKeys(false)) {
                    String configSection = "servers." + serverName + ".lobbies." + worldName;
                    Lobby l = new Lobby();
                    l.setName(getConfig().getString(configSection + ".name", worldName));
                    l.setWorld(worldName);
                    l.setServer(serverName);
                    l.setCap(getConfig().getInt(configSection + ".cap", 14));
                    l.setVipCap(getConfig().getInt(configSection + ".extra", 1));
                    l.setVip(getConfig().getBoolean(configSection + ".vip", false));
                    l.setPortal(Vector.deserialize(getConfig().getConfigurationSection(configSection + ".portal").getValues(false)));
                    l.setSignLocation(Vector.deserialize(getConfig().getConfigurationSection(configSection + ".sign").getValues(false)));
                    lobbies.put(l.getName(), l);
                }
            }
            new ServerHandler();
            new ServerCommands();
            getLogger().info("Starting hub communication server.");
            new Thread(new NettyLauncher(6666)).start();
            getServer().getPluginManager().registerEvents(new ServerListener(), this);
        } else {
            getLogger().info("Running as slave.");
            String resp = null;
            int tries = 1;
            while (resp == null || !resp.equals("PONG")) {
                getLogger().info("Attempting connection to the mothership... (attempt " + tries + ")");
                try {
                    new ClientHandler();
                    resp = ClientHandler.getConnectionManager().getResponse("PING");
                } catch (IOException e) {
                    tries++;
                }
            }
            new ClientCommands();
            getServer().getPluginManager().registerEvents(new ClientListener(), this);
        }

        getLogger().info("Loaded ChunkLobby (took " + String.valueOf(System.currentTimeMillis() - start) + "ms)");
    }

    public Map<String, Lobby> getLobbies() {
        return ImmutableMap.copyOf(lobbies);
    }

    public void updateLobby(String name, Lobby lobby) {
        lobbies.put(name, lobby);
    }

    public String getLobbyDestinationFor(String player) {
        String s = lobbyMoves.get(player);
        if (s != null) lobbyMoves.remove(player);
        return s;
    }

    public Gson getGson() {
        return gson;
    }
}
