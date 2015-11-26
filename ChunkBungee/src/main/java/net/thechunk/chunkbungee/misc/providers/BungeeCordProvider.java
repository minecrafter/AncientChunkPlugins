package net.thechunk.chunkbungee.misc.providers;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.thechunk.chunkbungee.ChunkBungee;

import java.util.HashSet;
import java.util.Set;

public class BungeeCordProvider implements ServerInfoProvider {
    @Override
    public int getTotalPlayerCount() {
        return ChunkBungee.getPlugin().getProxy().getOnlineCount();
    }

    @Override
    public Set<String> getPlayersOnServer(String server) {
        Set<String> players = new HashSet<>();
        for (ProxiedPlayer p : ChunkBungee.getPlugin().getProxy().getServerInfo(server).getPlayers())
            players.add(p.getName());
        return players;
    }
}
