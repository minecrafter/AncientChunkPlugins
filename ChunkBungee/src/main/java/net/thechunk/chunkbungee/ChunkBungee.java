package net.thechunk.chunkbungee;

import com.google.common.io.ByteStreams;
import lombok.Getter;
import net.md_5.bungee.api.plugin.Plugin;
import net.thechunk.chunkbungee.chat.ChatThrottleListener;
import net.thechunk.chunkbungee.command.SlashServerCommandExecutor;
import net.thechunk.chunkbungee.command.TidbitCommand;
import net.thechunk.chunkbungee.misc.TidbitManager;
import net.thechunk.chunkbungee.misc.providers.BungeeCordProvider;
import net.thechunk.chunkbungee.misc.providers.RedisBungeeProvider;
import net.thechunk.chunkbungee.misc.providers.ServerInfoProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

public class ChunkBungee extends Plugin {
    @Getter
    private static ChunkBungee plugin;
    @Getter
    private static ServerInfoProvider provider;

    @Override
    public void onEnable() {
        plugin = this;

        if (getProxy().getPluginManager().getPlugin("RedisBungee") != null)
            provider = new RedisBungeeProvider();
        else
            provider = new BungeeCordProvider();

        getLogger().info("Using " + provider.getClass().getSimpleName() + " as provider.");

        // Chat listeners:
        getProxy().getPluginManager().registerListener(this, new ChatThrottleListener());

        // Command executors:
        getProxy().getPluginManager().registerCommand(this, new SlashServerCommandExecutor("hub", "Hub"));
        getProxy().getPluginManager().registerCommand(this, new SlashServerCommandExecutor("minerware", "MW-Lobby", "mwlobby", "mw"));
        getProxy().getPluginManager().registerCommand(this, new SlashServerCommandExecutor("ender", "EnderLobby", "enderlobby"));
        getProxy().getPluginManager().registerCommand(this, new SlashServerCommandExecutor("arcade", "Arcade", "games"));
        getProxy().getPluginManager().registerCommand(this, new SlashServerCommandExecutor("pigwars", "PW-Lobby", "pigwarslobby", "pwlobby", "pw"));
        getProxy().getPluginManager().registerCommand(this, new SlashServerCommandExecutor("thimble", "ThimbleLobby", "thimblelobby"));
        getProxy().getPluginManager().registerCommand(this, new SlashServerCommandExecutor("gldesert", "GLDesert", "gld"));

        loadTidbitsConfig();
        for (Map.Entry<String, String> tidbit : TidbitManager.getTidbits().entrySet()) {
            getProxy().getPluginManager().registerCommand(this, new TidbitCommand(tidbit.getKey(), tidbit.getValue()));
        }
    }

    private void loadTidbitsConfig() {
        File folder = this.getDataFolder();
        if (!folder.exists())
            folder.mkdir();
        File tidbitsConfigFile = new File(folder, "tidbits.yml");
        try {
            if (!tidbitsConfigFile.exists()) {
                tidbitsConfigFile.createNewFile();
                try (InputStream in = getResourceAsStream("tidbits.yml");
                     OutputStream out = new FileOutputStream(tidbitsConfigFile)) {
                    ByteStreams.copy(in, out);
                }
            }
            TidbitManager.loadTidbitsFrom(tidbitsConfigFile);
        } catch (Exception ex) {
            ex.printStackTrace();
            getLogger().info("Unable to read or save tidbits.");
        }
    }
}
