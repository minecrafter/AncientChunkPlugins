package net.thechunk.chunkbungee.chat;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import java.util.HashMap;
import java.util.Map;

public class ChatThrottleListener implements Listener {
    private Map<String, ThrottleInfo> throttle = new HashMap<>();
    private Map<String, Long> muted = new HashMap<>();
    private static final TextComponent FAST_TYPING = new TextComponent();

    static {
        FAST_TYPING.setText("Woah! Slow down your fingers for a bit.");
        FAST_TYPING.setColor(ChatColor.RED);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onChat(ChatEvent event) {
        if (event.isCancelled())
            return;

        if (event.getSender() instanceof ProxiedPlayer) {
            ProxiedPlayer sender = (ProxiedPlayer) event.getSender();
            if (sender.hasPermission("chunkbungee.nothrottle")) {
                return;
            }
            if (muted.containsKey(sender.getName())) {
                long expiry = muted.get(sender.getName());
                if (System.currentTimeMillis() >= expiry) {
                    muted.remove(sender.getName());
                } else {
                    sender.sendMessage(FAST_TYPING);
                    muted.put(sender.getName(), expiry + 1500); // 1.5 second penalty
                    event.setCancelled(true);
                    return;
                }
            }
            if (throttle.containsKey(sender.getName())) {
                ThrottleInfo info = throttle.get(sender.getName());
                if (System.currentTimeMillis() >= info.endMs) {
                    throttle.remove(sender.getName());
                    return;
                }
                info.count++;
                if (info.count > 2) {
                    sender.sendMessage(FAST_TYPING);
                    muted.put(sender.getName(), System.currentTimeMillis() + 3000);
                    event.setCancelled(true);
                }
            } else {
                throttle.put(sender.getName(), new ThrottleInfo());
            }
        }
    }

    // DAE wish that Java had unions?
    private class ThrottleInfo {
        public int count = 1;
        public long endMs = System.currentTimeMillis() + 1000;
    }
}
