package net.thechunk.chunkbungee.misc.providers;

import com.imaginarycode.minecraft.redisbungee.RedisBungee;
import com.imaginarycode.minecraft.redisbungee.RedisBungeeAPI;

import java.util.Set;

public class RedisBungeeProvider implements ServerInfoProvider {
    private static RedisBungeeAPI redisBungee = RedisBungee.getApi();

    @Override
    public int getTotalPlayerCount() {
        return redisBungee.getPlayerCount();
    }

    @Override
    public Set<String> getPlayersOnServer(String server) {
        return redisBungee.getPlayersOnServer(server);
    }
}
