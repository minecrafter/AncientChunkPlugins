package net.thechunk.lobby.client;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

/**
 * Created with IntelliJ IDEA.
 * User: tux
 * Date: 10/11/13
 * Time: 10:12 PM
 * To change this template use File | Settings | File Templates.
 */
class LobbyJoinEvent extends PlayerEvent {
    private final HandlerList list = new HandlerList();
    private final World lobby;

    public LobbyJoinEvent(Player who, World lobby) {
        super(who);
        this.lobby = lobby;
    }

    @Override
    public HandlerList getHandlers() {
        return list;
    }

    public World getLobby() {
        return lobby;
    }
}
