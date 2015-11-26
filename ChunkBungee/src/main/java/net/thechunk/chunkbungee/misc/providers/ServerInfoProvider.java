package net.thechunk.chunkbungee.misc.providers;

import java.util.Set;

public interface ServerInfoProvider {
    public int getTotalPlayerCount();
    public Set<String> getPlayersOnServer(String server);
}
