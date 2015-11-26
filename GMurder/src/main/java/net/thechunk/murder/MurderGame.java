package net.thechunk.murder;

import net.thechunk.chunklib.bukkit.Locations;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.*;

public class MurderGame implements Listener {
    private enum Status {
        ALIVE,
        DEAD,
        MURDERER
    }

    private final MurderPlugin plugin;
    private Map<String, Status> status = new HashMap<>();
    private Map<String, Long> gunCooldown = new HashMap<>();
    private FileConfiguration worldCfg;
    private int timeLeft;

    public MurderGame(MurderPlugin plugin) {
        this.plugin = plugin;
        worldCfg = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "game.yml"));
    }

    public void join(Player player) {
        clean(player);
        player.teleport(Locations.deserialize(worldCfg.getConfigurationSection("holding").getValues(false)));
    }

    private void clean(Player player) {
        player.getInventory().clear();
    }

    public void start() {
        Location spawn = Locations.deserialize(worldCfg.getConfigurationSection("spawn").getValues(false));

        // Initialize player statuses
        for (Player player : plugin.getWorld().getPlayers()) {
            status.put(player.getName(), Status.ALIVE);
            player.teleport(spawn);
        }

        // Schedule our murderer task.
        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {
                List<Player> players = plugin.getWorld().getPlayers();
                Player murderer = players.get(plugin.getRandom().nextInt(players.size()));
                Player gunner;
                do {
                    gunner = players.get(plugin.getRandom().nextInt(players.size()));
                } while (gunner.getName().equals(murderer.getName()));
                for (Player player : players) {
                    if (player.getName().equals(murderer.getName())) {
                        player.sendMessage(ChatColor.RED + "You are the murderer. You are just like a bystander, so act natural and be careful.");
                        player.sendMessage(ChatColor.RED + "You have a knife and a throwing knife. Use these weapons strategically, and when nobody is looking...");
                        status.put(player.getName(), Status.MURDERER);
                        ItemStack knife = new ItemStack(Material.IRON_SWORD, 1);
                        ItemMeta knifeMeta = knife.getItemMeta();
                        knifeMeta.setDisplayName(ChatColor.BLUE + "Knife");
                        knife.setItemMeta(knifeMeta);
                        ItemStack tKnife = new ItemStack(Material.IRON_PICKAXE, 1);
                        ItemMeta tKnifeMeta = tKnife.getItemMeta();
                        tKnifeMeta.setDisplayName(ChatColor.BLUE + "Throwing Knife");
                        tKnife.setItemMeta(tKnifeMeta);
                        player.getInventory().setItem(8, knife);
                        player.getInventory().setItem(9, tKnife);
                    } else if (player.getName().equals(gunner.getName())) {
                        player.sendMessage(ChatColor.RED + "You have the first gun.");
                        player.sendMessage(ChatColor.RED + "You have one round and a slight cooldown period.");
                        player.sendMessage(ChatColor.RED + "You can shoot anyone, as long as you have suspicion to think they are the murderer.");
                        ItemStack tKnife = new ItemStack(Material.STONE_PICKAXE, 1);
                        ItemMeta tKnifeMeta = tKnife.getItemMeta();
                        tKnifeMeta.setDisplayName(ChatColor.BLUE + "Gun");
                        tKnife.setItemMeta(tKnifeMeta);
                        player.getInventory().setItem(9, tKnife);
                    } else {
                        player.sendMessage(ChatColor.RED + "All roles have been selected.");
                    }
                }
            }
        }, 100L);

        // Schedule the gun cooldown task.
        plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            @Override
            public void run() {
                for (Iterator<Map.Entry<String, Long>> i = gunCooldown.entrySet().iterator(); i.hasNext(); ) {
                    Map.Entry<String, Long> cooldownEntry = i.next();
                    if (System.currentTimeMillis() - cooldownEntry.getValue() >= 3000) {
                        i.remove();
                    }
                }
            }
        }, 100L, 20L);
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player) {
            if (event.getDamager() instanceof Player) {
                if (status.get(((Player) event.getDamager()).getName()) == Status.MURDERER) {
                    for (Player player : plugin.getWorld().getPlayers()) {
                        if (player.getName().equals(((Player) event.getDamager()).getName())) {
                            player.sendMessage(ChatColor.RED + "You have murdered " + ((Player) event.getEntity()).getName() + "!");
                        } else if (player.getName().equals(((Player) event.getEntity()).getName())) {
                            player.sendMessage(ChatColor.RED + "You have been mysteriously murdered.");
                        } else {
                            player.sendMessage(ChatColor.RED + ((Player) event.getEntity()).getName() + " has been mysteriously murdered.");
                        }
                    }
                    status.put(((Player) event.getEntity()).getName(), Status.DEAD);
                }
            }
        }
    }
}
