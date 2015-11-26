package net.thechunk.chunkbungee.command;

import com.google.common.base.Joiner;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.thechunk.chunkbungee.ChunkBungee;

import java.util.Set;

public class TidbitCommand extends Command {
    private String[] response;
    private static Joiner joiner = Joiner.on(", ").skipNulls();

    public TidbitCommand(String name, String response) {
        super(name);
        this.response = response.split("\\n");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        String serverName = sender instanceof ProxiedPlayer ? ((ProxiedPlayer) sender).getServer().getInfo().getName() : null;
        for (String line : response) {
            Set<String> onCurrentServer = null;
            if (line.contains("{SERVLIST}") || line.contains("{SERVNUM}"))
                onCurrentServer = serverName != null ? ChunkBungee.getProvider().getPlayersOnServer(serverName) : null;
            sender.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&',
                    line
                            .replace("{PLAYER}", sender.getName())
                            .replace("{NUM}", Integer.toString(ChunkBungee.getProvider().getTotalPlayerCount()))
                            .replace("{SERVER}", serverName != null ? serverName : "CONSOLE")
                            .replace("{SERVLIST}", onCurrentServer != null ? joiner.join(onCurrentServer) : "CONSOLE")
                            .replace("{SERVNUM}", onCurrentServer != null ? Integer.toString(onCurrentServer.size()) : "1")
            )));
        }
    }
}
