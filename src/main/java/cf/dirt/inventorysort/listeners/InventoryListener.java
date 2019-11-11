package cf.dirt.inventorysort.listeners;

import cf.dirt.inventorysort.inventory.InventorySorter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;

public final class InventoryListener implements Listener {

    private final InventorySorter sorter;

    public InventoryListener(InventorySorter sorter) {
        this.sorter = sorter;
    }

    @EventHandler (priority = EventPriority.LOWEST)
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getClick() != ClickType.MIDDLE) {
            return;
        }

        if (event.getCurrentItem() == null) {
            return;
        }

        if (event.getClickedInventory() == null) {
            return;
        }

        Inventory clickedInventory = event.getClickedInventory();

        if (!sorter.isSupported(clickedInventory.getType())) {
            return;
        }

        Material clickedType = event.getCurrentItem().getType();
        Player player = (Player) event.getWhoClicked();

        if (clickedType != Material.AIR) {
            Inventory otherInventory;

            if (clickedInventory.getType() == InventoryType.PLAYER) {
                InventoryView view = player.getOpenInventory();
                otherInventory = view.getTopInventory();
            } else {
                otherInventory = player.getInventory();
            }

            InventoryType otherType = otherInventory.getType();

            if (!sorter.isSupported(otherType)) {
                return;
            }

            sorter.transferItems(clickedInventory, otherInventory, clickedType);
        } else {
            sorter.sortItems(clickedInventory);
        }

        player.updateInventory();
    }
}
