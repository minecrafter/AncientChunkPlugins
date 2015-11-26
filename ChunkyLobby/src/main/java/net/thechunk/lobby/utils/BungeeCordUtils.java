package net.thechunk.lobby.utils;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.thechunk.lobby.ChunkyLobby;
import org.bukkit.entity.Player;

public class BungeeCordUtils {
    public static void connect(Player player, String server) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(server);
        player.sendPluginMessage(ChunkyLobby.getPlugin(), "BungeeCord", out.toByteArray());
    }
}
