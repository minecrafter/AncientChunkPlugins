package net.minerware.chunklobby;

import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ChunkLobby extends JavaPlugin {

    final HashMap<String, Lobby> lobbies = new HashMap<String, Lobby>();
    String hubWorldName;
    String server;
    String hubServer;
    @Getter
    private static ChunkLobby instance;
    @Getter
    private static JedisPool redisPool;

    @Override
    public void onEnable() {
        long start = System.currentTimeMillis();
        instance = this;
        saveDefaultConfig();

        getServer().getPluginManager().registerEvents(new ChunkLobbyListener(this), this);

        String redisDB = getConfig().getString("redis-server", "localhost");
        redisPool = new JedisPool(new JedisPoolConfig(), redisDB);

        hubServer = getConfig().getString("hub.server", "hub");
        server = getConfig().getString("server", "hub");

        getLogger().info("Loading lobbies.");
        for (String serverName : getConfig().getConfigurationSection("servers").getKeys(false)) {
            for (String worldName : getConfig().getConfigurationSection("servers." + serverName + ".lobbies").getKeys(false)) {
                String fakeName = "lby" + String.valueOf((int) (Math.random() * 300));
                String configSection = "servers." + serverName + ".lobbies." + worldName;
                Lobby l = new Lobby();
                l.setName(getConfig().getString(configSection + ".name", fakeName));
                l.setWorld(worldName);
                l.setServer(serverName);
                l.setCap(getConfig().getInt(configSection + ".cap", 14));
                l.setVipCap(getConfig().getInt(configSection + ".extra", 1));
                if (isHub()) {
                    l.setIp(getConfig().getString("servers." + serverName + ".address.ip", "127.0.0.1"));
                    l.setPort(getConfig().getInt("servers." + serverName + ".address.port", 25565));
                    l.setPortal(Vector.deserialize(getConfig().getConfigurationSection(configSection + ".portal").getValues(false)));
                    l.setSignLocation(Vector.deserialize(getConfig().getConfigurationSection(configSection + ".sign").getValues(false)));
                }
                lobbies.put(l.getName(), l);
            }
        }
        getLogger().info("Lobbies loaded.");

        if (isHub()) {
            getLogger().info("Running as the hub.");
            // The hub has the special distinction of running some of the tasks.
            getServer().getScheduler().runTaskTimerAsynchronously(this, new BagOfTasks.SignUpdater(this),
                    40L, 40L);
            World hub = getServer().getWorld(hubWorldName);
            hub.setAnimalSpawnLimit(0);
            hub.setMonsterSpawnLimit(0);
            hub.setGameRuleValue("doDaylightCycle", "false");
            hub.setTime(6000L);
        } else {
            getLogger().info("Running as a slave.");
        }
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        getLogger().info("Loaded ChunkLobby (took " + String.valueOf(System.currentTimeMillis() - start) + "ms)");
        getCommand("joinlobby").setExecutor(new CommandRunner(this));
    }

    public boolean isHub() {
        return getConfig().getBoolean("hub.is", false);
    }

    public Map<String, Lobby> getLobbies() {
        return lobbies;
    }

    public void joinLobby(Player player, String lobby) {
        if (getLobbies().get(lobby) != null) {
            Lobby l = getLobbies().get(lobby);
            if ((l.getPlaying() < l.getRegularCap() && !l.isVip()) || (player.hasPermission("chunklobby.vip.extra") &&
                    l.getPlaying() < l.getTotalCap())) {
                Jedis redis = getRedisPool().getResource();
                try {
                    redis.set("lobby:" + hubServer + ":" + player.getName(), l.getName());
                } finally {
                    getRedisPool().returnResource(redis);
                }
                Utilities.sendRawBungeeCordMessage(player, new String[]{"Connect", lobby});
            } else {
                if ((l.getPlaying() >= l.getRegularCap() && l.getTotalCap() > l.getPlaying())
                        && !player.hasPermission("chunklobby.vip.extra")) {
                    player.sendMessage(ChatColor.BLUE + "[Lobby] " + ChatColor.RED +
                            "This lobby is full! However, if you had premium you could join this lobby.");
                    return;
                }
                if (l.isVip() && !player.hasPermission("chunklobby.vip.extra")) {
                    player.sendMessage(ChatColor.BLUE + "[Lobby] " + ChatColor.RED +
                            "This lobby is a premium lobby. You must purchase premium in order to join this lobby.");
                    return;
                }
                if (l.getPlaying() >= l.getTotalCap()) {
                    player.sendMessage(ChatColor.BLUE + "[Lobby] " + ChatColor.RED +
                            "This lobby is full!");
                }
            }
        }
    }

    // This is a transitional method for now.
    public static String findLobbyByWorld(String world) {
        for (Lobby i : ChunkLobby.getRawLobbies()) {
            if (i.getWorld().equals(world)) return i.getName();
        }
        return null;
    }

    private static Collection<Lobby> getRawLobbies() {
        return instance.lobbies.values();
    }

    public static void updateLobby(String id, String s1, String s2) {
        if (instance.lobbies.containsKey(id)) {
            instance.lobbies.get(id).setStatus1(s1);
            instance.lobbies.get(id).setStatus2(s2);
        }
    }
}
