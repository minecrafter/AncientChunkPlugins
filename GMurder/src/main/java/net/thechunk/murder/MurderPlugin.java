package net.thechunk.murder;

import lombok.Getter;
import net.thechunk.chunklib.bukkit.Worlds;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Random;

public class MurderPlugin extends JavaPlugin {
    private MurderGame game = new MurderGame(this);
    @Getter private World world;
    @Getter private Random random = new Random();

    @Override
    public void onEnable() {
        world = Worlds.initialize("murder");
    }
}
