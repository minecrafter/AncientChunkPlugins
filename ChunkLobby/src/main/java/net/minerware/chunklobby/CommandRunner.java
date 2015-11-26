package net.minerware.chunklobby;

import com.google.common.base.Joiner;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

class CommandRunner implements CommandExecutor {

    private final ChunkLobby plugin;

    public CommandRunner(ChunkLobby chunkLobby) {
        plugin = chunkLobby;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equals("lobbies")) {
            sender.sendMessage("Available lobbies: " + Joiner.on(", ").join(plugin.lobbies.keySet()));
            return true;
        }
        Player pl;
        if (sender instanceof Player) {
            pl = (Player) sender;
        } else {
            sender.sendMessage(ChatColor.RED + "Most ChunkLobby commands are invoked in the context of a player.");
            return true;
        }
        if (command.getName().equals("joinlobby")) {
            if (args.length == 0) {
                return false;
            }
            if (plugin.lobbies.containsKey(args[0]))
                plugin.joinLobby(pl, args[0]);
        }
        if (command.getName().equals("leave")) {
            if (plugin.isHub()) {
                sender.sendMessage(ChatColor.RED + "You can't leave the hub. Please use the portal at spawn, or log off.");
            } else {
                Utilities.sendRawBungeeCordMessage(pl, new String[]{"Connect", plugin.hubServer});
            }
        }
        return true;
    }
}
