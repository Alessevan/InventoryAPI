package fr.bakaaless;

import fr.bakaaless.inventory.Scheduler;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public class InventoryPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        this.getLogger().log(Level.INFO, "InventoryAPI loaded.");
    }

    @Override
    public void onDisable() {
        Scheduler.getInstance().forceStop(this);
        this.getLogger().log(Level.INFO, "InventoryAPI unloaded.");
    }
}
