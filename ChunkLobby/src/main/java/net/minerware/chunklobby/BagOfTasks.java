package net.minerware.chunklobby;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class BagOfTasks {
    /**
     * The sign updater does what it says on the tin: Update signs.
     * <p/>
     * It is async with some sync sprinkled in.
     */
    public static class SignUpdater implements Runnable {

        private final ChunkLobby plugin;

        public SignUpdater(ChunkLobby plugin) {
            this.plugin = plugin;
        }

        public void run() {
            List<String> sl = new ArrayList<String>();
            for (Lobby l : plugin.getLobbies().values()) {
                String f = l.getIp() + ":" + l.getPort();
                if (!sl.contains(f)) sl.add(f);
            }

            for (String i : sl) {
                ServerListPing slp = new ServerListPing();
                slp.setAddress(i);
                try {
                    slp.fetchData();
                } catch (IOException e) {
                    ChunkLobby.getInstance().getLogger().info("Note: " + i + " is down!");
                    continue;
                }

                JSONObject ob = (JSONObject) JSONValue.parse(slp.getMotd());
                JSONArray oba = (JSONArray) ob.get("lobbies");

                for (Object obe : oba) {
                    JSONObject obj = (JSONObject) obe;
                    final String[] signText = new String[4];
                    signText[0] = (String) obj.get("name");
                    signText[1] = (String) obj.get("status1");
                    signText[2] = (String) obj.get("status2");
                    signText[3] = String.valueOf(obj.get("playing")) + "/" + obj.get("cap");

                    plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {

                        public void run() {
                            World hub = plugin.getServer().getWorld(plugin.hubWorldName);
                            Lobby x = plugin.getLobbies().get(signText[0]);
                            Block b = hub.getBlockAt(x.getSignLocation().getBlockX(),
                                    x.getSignLocation().getBlockY(),
                                    x.getSignLocation().getBlockZ());
                            Sign s = null;
                            if (b.getState() instanceof Sign) {
                                s = (Sign) b.getState();
                            } else {
                                plugin.getLogger().info("Block type at location was " + b.getType().toString());
                            }
                            for (int i = 0; i < signText.length; i++) {
                                if (s != null) {
                                    s.setLine(i, signText[i]);
                                }
                            }
                        }
                    });
                }
            }
        }
    }
}
