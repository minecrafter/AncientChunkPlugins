package net.minerware.chunklobby;

import org.bukkit.entity.Player;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

class Utilities {
    public static void sendRawBungeeCordMessage(Player p, String[] array) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);

        for (String i : array)
            try {
                out.writeUTF(i);
            } catch (IOException e) {
                e.printStackTrace();
            }

        p.sendPluginMessage(ChunkLobby.getInstance(), "BungeeCord", b.toByteArray());
    }
}
