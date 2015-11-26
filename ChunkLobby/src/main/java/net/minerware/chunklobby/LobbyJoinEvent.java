package net.minerware.chunklobby;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class LobbyJoinEvent extends PlayerEvent implements Cancellable {
    private boolean cancelled = false;
    private static final HandlerList handlers = new HandlerList();
    private Lobby lobby;

    public LobbyJoinEvent(Player who, Lobby to) {
        super(who);
        lobby = to;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }

    public Lobby getLobby() {
        return lobby;
    }
}
