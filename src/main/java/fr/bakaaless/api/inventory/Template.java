package fr.bakaaless.api.inventory;

import java.util.ArrayList;
import java.util.List;

public class Template {

    private final List<ItemAPI> items;

    public Template(final List<ItemAPI> items) {
        this.items = new ArrayList<>();
        for (final ItemAPI item : items)
            this.items.add(item.clone());
    }

    public List<ItemAPI> getItems() {
        return this.items;
    }

    @Override
    public String toString() {
        return "Template{" +
                "items=" + this.items +
                '}';
    }
}
