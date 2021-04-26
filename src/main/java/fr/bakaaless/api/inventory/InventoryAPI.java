package fr.bakaaless.api.inventory;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.stream.IntStream;

/**
 * The main object of <b>InventoryAPI</b>, used to build inventories.
 */
public class InventoryAPI implements Listener {

    private Inventory inventory;
    private int size;
    private String title;
    private InventoryType type;
    private List<ItemAPI> items;
    private Consumer<InventoryAPI> function;
    private Consumer<InventoryCloseEvent> closeEvent;
    private Consumer<InventoryClickEvent> clickEvent;
    private boolean interactionCancel;
    private boolean refreshed;
    private boolean build;

    private JavaPlugin plugin;

    private InventoryAPI(final JavaPlugin plugin) {
        this.plugin = plugin;
        this.size = 9;
        this.title = "";
        this.type = null;
        this.items = new ArrayList<>();
        this.refreshed = false;
        this.interactionCancel = false;
    }

    private InventoryAPI() {
    }

    /**
     * Create an <b>InventoryAPI</b> instance.
     * @param plugin An instance of the main class of your plugin.
     * @return New InventoryAPI object
     */
    public static InventoryAPI create(final JavaPlugin plugin) {
        return new InventoryAPI(plugin);
    }

    /**
     * Same as {@link #create(JavaPlugin)} but you can give directly the class.
     * @param plugin The class object of your main class. (Like MyPlugin.class)
     * @return New InventoryAPI object
     */
    public static InventoryAPI create(final Class<? extends JavaPlugin> plugin) {
        return new InventoryAPI(JavaPlugin.getProvidingPlugin(plugin));
    }

    /**
     * Set the size of your inventory. Can't be used if the inventory type isn't null.
     * @param size The size of the inventory (Multiple of 9, 54 at the maximum)
     * @return Your InventoryAPI object
     */
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
            this.inventory = generate();
        }
        this.size = size;
        return this;
    }

    /**
     * Set the title of your inventory
     * @param title The title of your inventory
     * @return Your InventoryAPI object
     */
    public InventoryAPI setTitle(final String title) {
        if (build) {
            this.plugin.getLogger().log(Level.WARNING, "Can't edit \"title\" option in InventoryAPI 'cause the inventory is already built");
            return this;
        }
        if (this.inventory != null && !this.title.equals(title)) {
            this.inventory.clear();
            this.inventory = generate();
        }
        this.title = title;
        return this;
    }

    /**
     * Set the type of your inventory. Have to be null to use the size.
     * @param type The type of the inventory, null at default.
     * @return Your InventoryAPI object.
     */
    public InventoryAPI setType(final InventoryType type) {
        if (build) {
            this.plugin.getLogger().log(Level.WARNING, "Can't edit \"type\" option in InventoryAPI 'cause the inventory is already built");
            return this;
        }
        if (this.inventory != null && !this.type.equals(type)) {
            this.inventory.clear();
            this.inventory = generate();
        }
        this.type = type;
        return this;
    }

    /**
     * Enable the refresh status (Every 2 ticks).
     * @param refreshed A boolean to enable/disable the refresh status
     * @return Your InventoryAPI object
     */
    public InventoryAPI setRefresh(final boolean refreshed) {
        if (build) {
            this.plugin.getLogger().log(Level.WARNING, "Can't edit \"refresh\" option in InventoryAPI 'cause the inventory is already built");
            return this;
        }
        this.refreshed = refreshed;
        return this;
    }

    /**
     * Define all of the content of the refresh function.
     * @param function A consumer, that will contains all of your modifications.
     * @return Your InventoryAPI object
     */
    public InventoryAPI setFunction(final Consumer<InventoryAPI> function) {
        if (build) {
            this.plugin.getLogger().log(Level.WARNING, "Can't edit \"function\" option in InventoryAPI 'cause the inventory is already built");
            return this;
        }
        this.function = function;
        return this;
    }

    /**
     * Define the close function.
     * @param function A consumer, that will be triggered when the player will close the inventory.
     * @return Your InventoryAPI object
     */
    public InventoryAPI setCloseFunction(final Consumer<InventoryCloseEvent> function) {
        this.closeEvent = function;
        return this;
    }

    /**
     * Define the click function.
     * @param function A consumer, that will be triggered when the player click while the inventory is opened.
     * @return Your InventoryAPI object
     */
    public InventoryAPI setClickFunction(final Consumer<InventoryClickEvent> function) {
        this.clickEvent = function;
        return this;
    }

    /**
     * Enable interaction protection for your Inventory
     * @param interactionCancelled A boolean, to enable/disable
     * @return Your InventoryAPI object
     */
    public InventoryAPI setInteractionCancelled(final boolean interactionCancelled) {
        this.interactionCancel = interactionCancelled;
        return this;
    }

    /**
     * Get the size of your inventory.
     * @return The size of your Inventory, an <b>integer</b>.
     */
    public int getSize() {
        return this.size;
    }

    /**
     * Get the title of your inventory.
     * @return The title of your Inventory, a <b>String</b>.
     */
    public String getTitle() {
        return this.title;
    }

    /**
     * Get the type of your inventory.
     * @return the type of your inventory, a <b>InventoryType</b>.
     */
    @Nullable
    public InventoryType getType() {
        return this.type;
    }

    /**
     * Get the list of your inventory's items
     * @return The items in your inventory, a list of {@link ItemAPI}
     */
    public List<ItemAPI> getItems() {
        return this.items;
    }

    /**
     * Get the boolean of the refresh task.
     * @return A boolean, true if enabled, else false.
     */
    public boolean isRefreshed() {
        return this.refreshed;
    }

    /**
     * Get the {@link JavaPlugin} which is used to create the inventory.
     * @return the {@link JavaPlugin}
     */
    public JavaPlugin getPlugin() {
        return this.plugin;
    }

    /**
     * Get the {@link Inventory} which is built after the {@link #build(Player)} method.
     * @return the {@link Inventory}
     */
    Inventory getInventory() {
        return this.inventory;
    }

    /**
     * Get the consumer of the refresh task of your Inventory.
     * @return A consumer, that corresponds to the refresh task.
     */
    public Consumer<InventoryAPI> getFunction() {
        return this.function;
    }

    /**
     * Get the consumer which will be triggered when the player will close the inventory.
     * @return A consumer, that corresponds to close event.
     */
    public Consumer<InventoryCloseEvent> getCloseEvent() {
        return this.closeEvent;
    }

    /**
     * Get the consumer which will be triggered when the player will click will the inventory is opened.
     * @return A consumer, that corresponds to click event.
     */
    public Consumer<InventoryClickEvent> getClickEvent() {
        return this.clickEvent;
    }

    /**
     * Get the interactionCancelled boolean.
     * @return A boolean, true if interaction protection is enabled, else false.
     */
    public boolean isInteractionCancelled() {
        return this.interactionCancel;
    }

    /**
     * Clear a slot of your inventory
     * @param slot The id of the slot, an integer
     * @return Your InventoryAPI object
     */
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

    /**
     * Get the item that correspond to the given slot.
     * @param slot The id of the slot, an integer
     * @return An {@link ItemAPI} object if slot is set, else null.
     */
    public Optional<ItemAPI> getItem(final int slot) {
        return this.items.stream().filter(item -> item.getSlot() == slot).findFirst();
    }

    /**
     * Set an item in a slot of your inventory.
     * @param slot The id of the slot, an integer
     * @param itemStack The ItemStack to set
     * @return Your InventoryAPI object
     */
    public InventoryAPI addItem(final int slot, final ItemStack itemStack) {
        return this.addItem(slot, itemStack, true, inventoryClickEvent -> {
        });
    }

    /**
     * Set an item in a slot of your inventory.
     * @param slot The id of the slot, an integer
     * @param function A function that return an ItemStack, for the refresh task.
     * @return Your InventoryAPI object
     */
    public InventoryAPI addItem(final int slot, final Function<Object, ItemStack> function) {
        return this.addItem(slot, function, true, inventoryClickEvent -> {
        });
    }

    /**
     * Same as {@link #addItem(int, ItemStack)}, except you can change the interaction protection for this slot.
     * @param slot The id of the slot, an integer
     * @param itemStack The ItemStack to set
     * @param cancelled The boolean to enable/disable the interaction protection for this slot
     * @return Your InventoryAPI object
     */
    public InventoryAPI addItem(final int slot, final ItemStack itemStack, final boolean cancelled) {
        return this.addItem(slot, itemStack, cancelled, inventoryClickEvent -> {
        });
    }

    /**
     * Same as {@link #addItem(int, Function, boolean)}, except you can change the interaction protection for this slot.
     * @param slot The id of the slot, an integer
     * @param function A function that return an ItemStack, for the refresh task.
     * @param cancelled The boolean to enable/disable the interaction protection for this slot
     * @return Your InventoryAPI object
     */
    public InventoryAPI addItem(final int slot, final Function<Object, ItemStack> function, final boolean cancelled) {
        return this.addItem(slot, function, cancelled, inventoryClickEvent -> {
        });
    }

    /**
     * Same as {@link #addItem(int, ItemStack, boolean, Consumer)}, except you can add custom actions to the InventoryClickEvent.
     * @param slot The id of the slot, an integer
     * @param itemStack The ItemStack to set
     * @param cancelled The boolean to enable/disable the interaction protection for this slot
     * @param consumer A lambda expression that correspond to the executed code when item is clicked
     * @return Your InventoryAPI object
     */
    public InventoryAPI addItem(final int slot, final ItemStack itemStack, final boolean cancelled, final Consumer<InventoryClickEvent> consumer) {
        return this.addItem(new ItemAPI(slot, itemStack, cancelled, consumer));
    }

    /**
     * Same as {@link #addItem(int, Function, boolean, Consumer)}, except you can add custom actions to the InventoryClickEvent.
     * @param slot The id of the slot, an integer
     * @param function A function that return an ItemStack, for the refresh task
     * @param cancelled The boolean to enable/disable the interaction protection for this slot
     * @param consumer A lambda expression that correspond to the executed code when item is clicked
     * @return Your InventoryAPI object
     */
    public InventoryAPI addItem(final int slot, final Function<Object, ItemStack> function, final boolean cancelled, final Consumer<InventoryClickEvent> consumer) {
        return this.addItem(new ItemAPI(slot, function, cancelled, consumer));
    }

    /**
     * Set an item in your inventory, using {@link ItemAPI}
     * @param itemAPI An {@link ItemAPI}
     * @return Your InventoryAPI object
     */
    public InventoryAPI addItem(final ItemAPI itemAPI) {
        this.clearSlot(itemAPI.getSlot());
        this.items.add(itemAPI);
        return this;
    }

    /**
     * Get the borders of your inventory, depends of the size of the inventory
     * @return An integer array, that contains all the slots of the border
     */
    public int[] getBorders() {
        return IntStream.range(0, this.size).filter(i -> this.size < 27 || i < 9 || i % 9 == 0 || (i - 8) % 9 == 0 || i > this.size - 9).toArray();
    }

    /**
     * Set the item in the border of your inventory.
     * @param itemStack The item to set, an ItemStack
     * @return Your InventoryAPI object
     */
    public InventoryAPI setBorder(final ItemStack itemStack) {
        for (int index : this.getBorders())
            this.addItem(index, itemStack, true, inventoryClickEvent -> {});
        return this;
    }

    /**
     * Same as {@link #setBorder(ItemStack)}
     * @param itemStack The item to set, an ItemStack
     * @param cancelled The boolean to enable/disable the interaction protection for this slot
     * @return Your InventoryAPI object
     */
    public InventoryAPI setBorder(final ItemStack itemStack, final boolean cancelled) {
        for (int index : this.getBorders())
            this.addItem(index, itemStack, cancelled, inventoryClickEvent -> {});
        return this;
    }

    /**
     * Same as {@link #setBorder(ItemStack, boolean, Consumer)}, except you give a Function instead of an ItemStack
     * @param function A function that return an ItemStack, for the refresh task
     * @param cancelled The boolean to enable/disable the interaction protection for this slot
     * @return Your InventoryAPI object
     */
    public InventoryAPI setBorder(final Function<Object, ItemStack> function, final boolean cancelled) {
        for (int index : this.getBorders())
            this.addItem(index, function, cancelled, inventoryClickEvent -> {});
        return this;
    }

    /**
     * Same as {@link #setBorder(ItemStack, boolean)}, except you
     * @param itemStack The item to set, an ItemStack
     * @param cancelled The boolean to enable/disable the interaction protection for this slot
     * @param consumer A lambda expression that correspond to the executed code when item is clicked
     * @return Your InventoryAPI object
     */
    public InventoryAPI setBorder(final ItemStack itemStack, final boolean cancelled, final Consumer<InventoryClickEvent> consumer) {
        for (int index : this.getBorders())
            this.addItem(index, itemStack, cancelled, consumer);
        return this;
    }

    /**
     * Same as {@link #setBorder(ItemStack, boolean, Consumer)}, except you give a Function instead of an ItemStack
     * @param function A function that return an ItemStack, for the refresh task
     * @param cancelled The boolean to enable/disable the interaction protection for this slot
     * @param consumer A lambda expression that correspond to the executed code when item is clicked
     * @return Your InventoryAPI object
     */
    public InventoryAPI setBorder(final Function<Object, ItemStack> function, final boolean cancelled, final Consumer<InventoryClickEvent> consumer) {
        for (int index : this.getBorders())
            this.addItem(index, function, cancelled, consumer);
        return this;
    }

    /**
     * Build and open the inventory to a player
     * @param player The player to open the inventory
     */
    public void build(final Player player) {
        this.build = true;
        if (this.inventory == null) {
            this.inventory = generate();
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

    /**
     * Stop the refresh method for this inventory
     */
    public void stop() {
        HandlerList.unregisterAll(this);
        if (this.refreshed)
            Scheduler.getInstance().remove(this);
        this.inventory = null;
        this.build = false;
    }

    private Inventory generate() {
        if (this.type == null)
            return this.plugin.getServer().createInventory(null, this.size, this.title);
        else
            return this.plugin.getServer().createInventory(null, this.type, this.title);
    }

    @EventHandler
    public void onClose(final InventoryCloseEvent e) {
        if (e.getView().getTopInventory().equals(this.inventory)) {
            if (this.closeEvent != null)
                this.closeEvent.accept(e);
            this.stop();
        }
        if (!e.getInventory().equals(this.inventory))
            return;
        if (e.getInventory().getHolder() == null) {
            if (this.closeEvent != null)
                this.closeEvent.accept(e);
            this.stop();
        }
    }

    @EventHandler
    public void onInteract(final InventoryClickEvent e) {
        if (e.getView() == null || e.getView().getTopInventory() == null)
            return;
        if (e.getView().getTopInventory().equals(this.inventory))
            if (this.clickEvent != null)
                this.clickEvent.accept(e);
        if (e.getClickedInventory() == null)
            return;
        if (!e.getClickedInventory().equals(this.inventory))
            return;
        e.setCancelled(this.interactionCancel);
        final AtomicBoolean slotRegister = new AtomicBoolean(false);
        this.items.forEach(itemAPI -> {
            if (e.getSlot() != itemAPI.getSlot())
                return;
            slotRegister.set(true);
            if (e.getCurrentItem() == null)
                return;
            e.setCancelled(itemAPI.isCancelled());
            itemAPI.getConsumer().accept(e);
        });
        if (!slotRegister.get()) {
            if (e.getInventory().getItem(e.getSlot()) == null || e.getInventory().getItem(e.getSlot()).getType() == Material.AIR)
                this.clearSlot(e.getSlot());
            else
                this.addItem(e.getSlot(), e.getInventory().getItem(e.getSlot()), false);
        }
    }

    @EventHandler
    public void onMove(final InventoryMoveItemEvent e) {
        if (!e.getSource().equals(this.inventory) || !e.getInitiator().equals(this.inventory) && !e.getDestination().equals(this.inventory))
            return;
        e.setCancelled(this.interactionCancel);
    }

    @EventHandler
    public void onDrag(final InventoryDragEvent e) {
        if (!e.getInventory().equals(this.inventory))
            return;
        this.items.stream().filter(item -> e.getInventorySlots().contains(item.getSlot()) || e.getRawSlots().contains(item.getSlot()))
                .forEach(item -> e.setCancelled(e.isCancelled() || item.isCancelled()));
    }
}
