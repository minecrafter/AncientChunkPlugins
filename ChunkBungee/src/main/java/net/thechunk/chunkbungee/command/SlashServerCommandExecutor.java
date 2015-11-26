package net.thechunk.chunkbungee.command;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class SlashServerCommandExecutor extends Command {
    private String server;

    public SlashServerCommandExecutor(String name, String server, String... aliases) {
        super(name, null, aliases);
        this.server = server;
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        if (commandSender instanceof ProxiedPlayer) {
            // Don't do this on GLD.
            if (((ProxiedPlayer) commandSender).getServer().getInfo().getName().equalsIgnoreCase("GLDesert")) return;
            if (ProxyServer.getInstance().getServerInfo(server) != null) {
                ((ProxiedPlayer) commandSender).connect(ProxyServer.getInstance().getServerInfo(server));
            }
        }
    }
}
