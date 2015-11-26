package net.thechunk.lobby.client;

import net.thechunk.lobby.ChunkyLobby;
import net.thechunk.lobby.utils.BungeeCordUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ClientCommands implements CommandExecutor {
    public ClientCommands() {
        ChunkyLobby.getPlugin().getCommand("leave").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player)
            BungeeCordUtils.connect((Player)sender, ClientHandler.getHub());
        return true;
    }
}
