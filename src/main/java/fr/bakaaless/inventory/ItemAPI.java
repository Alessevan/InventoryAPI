package fr.bakaaless.inventory;

import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;
import java.util.function.Function;

public class ItemAPI {

    private final int slot;
    private final Function<Object, ItemStack> function;
    private ItemStack item;
    private final boolean cancelled;
    private final Consumer<InventoryClickEvent> consumer;

    public ItemAPI(final int slot, final ItemStack item, final boolean cancelled, final Consumer<InventoryClickEvent> consumer) {
        this.slot = slot;
        this.item = item;
        this.function = null;
        this.cancelled = cancelled;
        this.consumer = consumer;
    }

    public ItemAPI(final int slot, final Function<Object, ItemStack> function, final boolean cancelled, final Consumer<InventoryClickEvent> consumer) {
        this.slot = slot;
        this.function = function;
        this.item = new ItemStack(Material.AIR);
        this.refresh(this);
        this.cancelled = cancelled;
        this.consumer = consumer;
    }

    public void refresh(final Object o) {
        if (this.function == null)
            return;
        this.item = this.function.apply(o);
    }

    public int getSlot() {
        return this.slot;
    }

    public ItemStack getItem() {
        if (this.item == null)
            this.refresh(this);
        if (this.item == null)
            return new ItemStack(Material.AIR);
        return this.item;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public Consumer<InventoryClickEvent> getConsumer() {
        return this.consumer;
    }
}
