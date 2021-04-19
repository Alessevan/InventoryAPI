package fr.bakaaless.api.inventory;

import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * An object to create items, which can be used in {@link InventoryAPI}.
 */
public class ItemAPI {

    private final int slot;
    private Function<Object, ItemStack> function;
    private ItemStack item;
    private boolean cancelled;
    private Consumer<InventoryClickEvent> consumer;

    /**
     * @param slot      The slot where will be located the item.
     * @param item      The wished ItemStack
     * @param cancelled The boolean to enable/disable the interaction protection for this slot
     * @param consumer  A lambda expression that correspond to the executed code when item is clicked
     */
    public ItemAPI(final int slot, final ItemStack item, final boolean cancelled, final Consumer<InventoryClickEvent> consumer) {
        this.slot = slot;
        this.item = item;
        this.function = null;
        this.cancelled = cancelled;
        this.consumer = consumer;
    }

    /**
     * @param slot The slot where will be located the item.
     * @param function A function that return an ItemStack, for the refresh task
     * @param cancelled The boolean to enable/disable the interaction protection for this slot
     * @param consumer A lambda expression that correspond to the executed code when item is clicked
     */
    public ItemAPI(final int slot, final Function<Object, ItemStack> function, final boolean cancelled, final Consumer<InventoryClickEvent> consumer) {
        this.slot = slot;
        this.function = function;
        this.item = new ItemStack(Material.AIR);
        this.refresh(this);
        this.cancelled = cancelled;
        this.consumer = consumer;
    }

    /**
     * Refresh this item at its slot.
     * @param o An {@link InventoryAPI} instance.
     */
    public void refresh(final Object o) {
        if (this.function == null)
            return;
        this.item = this.function.apply(o);
    }

    /**
     * Get the slot of this item
     * @return The slot, an integer
     */
    public int getSlot() {
        return this.slot;
    }

    /**
     * Get the ItemStack of this item
     * @return The ItemStack.
     */
    public ItemStack getItem() {
        if (this.item == null)
            this.refresh(this);
        if (this.item == null)
            return new ItemStack(Material.AIR);
        return this.item;
    }

    /**
     * Check if interaction protection is enabled
     * @return A boolean, true if enabled, else false
     */
    public boolean isCancelled() {
        return this.cancelled;
    }

    /**
     * Get the consumer for the InventoryClickEvent
     * @return The consumer for the InventoryClickEvent
     */
    public Consumer<InventoryClickEvent> getConsumer() {
        return this.consumer;
    }

    /**
     * Change the function that create the ItemStack of the ItemAPI.
     * @param function the function.
     */
    public void setFunction(final Function<Object, ItemStack> function) {
        this.function = function;
    }

    /**
     * Change the item of the ItemAPI.
     * @param item the new item.
     */
    public void setItem(final ItemStack item) {
        this.item = item;
    }

    /**
     * Change id the interaction is cancelled.
     * @param cancelled is the interaction cancelled.
     */
    public void setCancelled(final boolean cancelled) {
        this.cancelled = cancelled;
    }

    /**
     * Change the consumer of the ItemAPI.
     * @param consumer the consumer to execute.
     */
    public void setConsumer(final Consumer<InventoryClickEvent> consumer) {
        this.consumer = consumer;
    }
}
