package fr.bakaaless.inventory;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.stream.IntStream;

public class InventoryAPI implements Listener {

    private Inventory inventory;
    private int size;
    private String title;
    private List<ItemAPI> items;
    private Consumer<InventoryAPI> function;
    private boolean refreshed;
    private boolean build;

    private JavaPlugin plugin;

    private InventoryAPI(final JavaPlugin plugin) {
        this.plugin = plugin;
        this.size = 9;
        this.title = "";
        this.items = new ArrayList<>();
        this.refreshed = false;
    }

    private InventoryAPI() {
    }

    public static InventoryAPI create(final JavaPlugin plugin) {
        return new InventoryAPI(plugin);
    }

    public static InventoryAPI create(final Class<? extends JavaPlugin> plugin) {
        return new InventoryAPI(JavaPlugin.getProvidingPlugin(plugin));
    }

    public InventoryAPI setSize(final int size) {
        if (build) {
            this.plugin.getLogger().log(Level.WARNING, "Can't edit \"size\" option in InventoryAPI 'cause the inventory is already built");
            return this;
        }
        if (this.size <= 0 || this.size % 9 != 0 || this.size >= 54) {
            plugin.getLogger().severe("This inventory can't have a size of " + size);
            return this;
        }
        if (this.inventory != null && this.size != size) {
            this.inventory.clear();
            this.inventory = Bukkit.createInventory(null, size, this.title);
        }
        this.size = size;
        return this;
    }

    public InventoryAPI setTitle(final String title) {
        if (build) {
            this.plugin.getLogger().log(Level.WARNING, "Can't edit \"title\" option in InventoryAPI 'cause the inventory is already built");
            return this;
        }
        if (this.inventory != null && !this.title.equals(title)) {
            this.inventory.clear();
            this.inventory = Bukkit.createInventory(null, this.size, title);
        }
        this.title = title;
        return this;
    }

    public InventoryAPI setRefresh(final boolean refreshed) {
        if (build) {
            this.plugin.getLogger().log(Level.WARNING, "Can't edit \"refresh\" option in InventoryAPI 'cause the inventory is already built");
            return this;
        }
        this.refreshed = refreshed;
        return this;
    }

    public InventoryAPI setFunction(Consumer<InventoryAPI> function) {
        if (build) {
            this.plugin.getLogger().log(Level.WARNING, "Can't edit \"function\" option in InventoryAPI 'cause the inventory is already built");
            return this;
        }
        this.function = function;
        return this;
    }

    public int getSize() {
        return this.size;
    }

    public String getTitle() {
        return this.title;
    }

    public List<ItemAPI> getItems() {
        return this.items;
    }

    public boolean isRefreshed() {
        return this.refreshed;
    }

    JavaPlugin getPlugin() {
        return this.plugin;
    }

    Inventory getInventory() {
        return this.inventory;
    }

    public Consumer<InventoryAPI> getFunction() {
        return function;
    }

    public InventoryAPI clearSlot(final int slot) {
        Optional<ItemAPI> itemAPI = Optional.empty();
        for (final Iterator<ItemAPI> it = this.items.iterator(); it.hasNext(); itemAPI = Optional.ofNullable(it.next())) {
            itemAPI.ifPresent(item -> {
                if (item.getSlot() == slot)
                    it.remove();
            });
        }
        return this;
    }

    public Optional<ItemAPI> getItem(final int slot) {
        return this.items.stream().filter(item -> item.getSlot() == slot).findFirst();
    }

    public InventoryAPI addItem(final int slot, final ItemStack itemStack) {
        return this.addItem(slot, itemStack, true, inventoryClickEvent -> {
        });
    }

    public InventoryAPI addItem(final int slot, final Function<Object, ItemStack> function) {
        return this.addItem(slot, function, true, inventoryClickEvent -> {
        });
    }

    public InventoryAPI addItem(final int slot, final ItemStack itemStack, final boolean cancelled) {
        return this.addItem(slot, itemStack, cancelled, inventoryClickEvent -> {
        });
    }

    public InventoryAPI addItem(final int slot, final Function<Object, ItemStack> function, final boolean cancelled) {
        return this.addItem(slot, function, cancelled, inventoryClickEvent -> {
        });
    }

    public InventoryAPI addItem(final int slot, final ItemStack itemStack, final boolean cancelled, final Consumer<InventoryClickEvent> consumer) {
        return this.addItem(new ItemAPI(slot, itemStack, cancelled, consumer));
    }

    public InventoryAPI addItem(final int slot, final Function<Object, ItemStack> function, final boolean cancelled, final Consumer<InventoryClickEvent> consumer) {
        return this.addItem(new ItemAPI(slot, function, cancelled, consumer));
    }

    public InventoryAPI addItem(final ItemAPI itemAPI) {
        this.clearSlot(itemAPI.getSlot());
        this.items.add(itemAPI);
        return this;
    }

    public int[] getBorders() {
        int size = this.inventory.getSize();
        return IntStream.range(0, size).filter(i -> size < 27 || i < 9 || i % 9 == 0 || (i - 8) % 9 == 0 || i > size - 9).toArray();
    }

    public InventoryAPI setBorder(final ItemStack itemStack) {
        for (int index : this.getBorders())
            this.addItem(index, itemStack, true, inventoryClickEvent -> {});
        return this;
    }

    public InventoryAPI setBorder(final ItemStack itemStack, final boolean cancelled) {
        for (int index : this.getBorders())
            this.addItem(index, itemStack, cancelled, inventoryClickEvent -> {});
        return this;
    }

    public InventoryAPI setBorder(final Function<Object, ItemStack> function, final boolean cancelled) {
        for (int index : this.getBorders())
            this.addItem(index, function, cancelled, inventoryClickEvent -> {});
        return this;
    }

    public InventoryAPI setBorder(final ItemStack itemStack, final boolean cancelled, final Consumer<InventoryClickEvent> consumer) {
        for (int index : this.getBorders())
            this.addItem(index, itemStack, cancelled, consumer);
        return this;
    }

    public InventoryAPI setBorder(final Function<Object, ItemStack> function, final boolean cancelled, final Consumer<InventoryClickEvent> consumer) {
        for (int index : this.getBorders())
            this.addItem(index, function, cancelled, consumer);
        return this;
    }

    public void build(final Player player) {
        this.build = true;
        if (this.inventory == null) {
            this.inventory = Bukkit.createInventory(player, this.size, this.title);
            if (this.function != null)
                this.function.accept(this);
            this.items.forEach(itemAPI -> {
                if (this.inventory.getSize() <= itemAPI.getSlot())
                    return;
                itemAPI.refresh(this);
                this.inventory.setItem(itemAPI.getSlot(), itemAPI.getItem());
            });
            player.openInventory(this.inventory);
            if (this.refreshed)
                Scheduler.getInstance().add(this);
            plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
        }
        else {
            if (this.function != null)
                this.function.accept(this);
            this.items.forEach(itemAPI -> {
                if (this.inventory.getSize() <= itemAPI.getSlot())
                    return;
                itemAPI.refresh(this);
                this.inventory.setItem(itemAPI.getSlot(), itemAPI.getItem());
            });
        }
    }

    public void stop() {
        HandlerList.unregisterAll(this);
        if (this.refreshed)
            Scheduler.getInstance().remove(this);
        this.inventory = null;
    }

    @EventHandler
    public void onClose(final InventoryCloseEvent e) {
        if (e.getView().getTopInventory().equals(this.inventory))
            this.stop();
        if (!e.getInventory().equals(this.inventory))
            return;
        if (e.getInventory().getHolder() == null) {
            this.stop();
        }
    }

    @EventHandler
    public void onInteract(final InventoryClickEvent e) {
        if (e.getClickedInventory() == null)
            return;
        if (!e.getClickedInventory().equals(this.inventory))
            return;
        this.items.forEach(itemAPI -> {
            if (e.getSlot() != itemAPI.getSlot())
                return;
            if (e.getCurrentItem() == null)
                return;
            e.setCancelled(itemAPI.isCancelled());
            itemAPI.getConsumer().accept(e);
        });
    }
}
