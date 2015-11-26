package net.thechunk.lobby.server;

import com.google.common.base.Joiner;
import net.thechunk.lobby.ChunkyLobby;
import net.thechunk.lobby.data.server.AddingLobby;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created with IntelliJ IDEA.
 * User: tux
 * Date: 10/12/13
 * Time: 10:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class ServerCommands implements CommandExecutor {
    public ServerCommands() {
        ChunkyLobby.getPlugin().getCommand("joinlobby").setExecutor(this);
        ChunkyLobby.getPlugin().getCommand("lobbies").setExecutor(this);
        ChunkyLobby.getPlugin().getCommand("createlobby").setExecutor(this);
        ChunkyLobby.getPlugin().getCommand("deletelobby").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equals("lobbies")) {
            sender.sendMessage("Available lobbies: " + Joiner.on(", ").join(ChunkyLobby.getPlugin().getLobbies().keySet()));
            return true;
        }
        Player p;
        if (sender instanceof Player)
            p = (Player)sender;
        else
            return true;

        if (command.getName().equals("joinlobby")) {
            if (!sender.hasPermission("chunklobby.join")) {
                sender.sendMessage(ChatColor.RED + "You don't have permission.");
                return true;
            }
            if (args.length > 0) {
                String name = Joiner.on(" ").join(args);
                if (ChunkyLobby.getPlugin().getLobbies().containsKey(name))
                    ServerHandler.joinLobby(p, ChunkyLobby.getPlugin().getLobbies().get(Joiner.on(" ").join(args)));
                else
                    p.sendMessage(ChatColor.RED + "Lobby not found.");
            } else {
                p.sendMessage(ChatColor.RED + "No lobby name was supplied. Use /lobbies for a full list of names.");
            }
            return true;
        }

        if (command.getName().equals("createlobby")) {
            if (!sender.hasPermission("chunklobby.join")) {
                sender.sendMessage(ChatColor.RED + "You don't have permission.");
                return true;
            }

            if (args.length > 2) {
                ServerListener.addingPortals.add(new AddingLobby(sender.getName(), args[0], args[1]));
                sender.sendMessage(ChatColor.GREEN + "Click on the bottom block of where the portal is to be located.");
            } else {
                sender.sendMessage(ChatColor.RED + "/createlobby SERVER WORLD");
            }
        }
        return true;
    }
}
