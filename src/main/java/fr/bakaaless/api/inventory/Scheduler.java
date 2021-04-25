package fr.bakaaless.api.inventory;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;

public class Scheduler {

    private static Scheduler instance;

    public static Scheduler getInstance() {
        if (instance == null)
            instance = new Scheduler();
        return instance;
    }

    private final List<InventoryAPI> inventories = new ArrayList<>();
    private Timer timer;
    private TimerTask task;
    private boolean isRunning;

    private int step;

    private Scheduler() {
        this.step = 0;
        this.isRunning = false;
        this.timer = new Timer();
        this.task = new TimerTask() {
            @Override
            public void run() {
                if (isRunning)
                    exec();
            }
        };
    }

    public void exec() {
        if (step++ % 2 == 0)
            this.inventories.forEach(inventoryAPI -> {
                inventoryAPI.getInventory().clear();
                if (inventoryAPI.getFunction() != null)
                    inventoryAPI.getFunction().accept(inventoryAPI);
                inventoryAPI.getItems().forEach(itemAPI -> inventoryAPI.getInventory().setItem(itemAPI.getSlot(), itemAPI.getItem()));
            });
        else
            this.inventories.forEach(inventoryAPI -> inventoryAPI.getItems().forEach(itemAPI -> itemAPI.refresh(inventoryAPI)));
    }

    private void start() {
        if (!this.isRunning) {
            this.isRunning = true;
            this.step = 0;
            this.timer.schedule(this.task, 0L, 50L);
        }
    }

    private void stop() {
        if (this.isRunning) {
            this.timer.cancel();
            this.timer = null;
            this.task = null;
            this.timer = new Timer();
            this.task = new TimerTask() {
                @Override
                public void run() {
                    if (isRunning)
                        exec();
                }
            };
            this.isRunning = false;
        }
    }

    public void forceStop(final JavaPlugin plugin) {
        plugin.getLogger().log(Level.WARNING, "Try to shutdown InventoryAPI's scheduler");
        this.stop();
    }

    synchronized void add(final InventoryAPI inv) {
        this.inventories.add(inv);
        if (this.inventories.size() == 1)
            this.start();
    }

    synchronized void remove(final InventoryAPI inv) {
        this.inventories.remove(inv);
        if (this.inventories.size() == 0)
            this.stop();
    }

    synchronized int getStep() {
        return this.step;
    }

}
