package cf.dirt.inventorysort;

import cf.dirt.inventorysort.inventory.InventorySorter;
import cf.dirt.inventorysort.listeners.InventoryListener;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public final class Plugin extends JavaPlugin {

    @SuppressWarnings("unchecked")
    public void onEnable() {
        saveDefaultConfig();

        try {
            List<List<String>> materialGroups = (List<List<String>>) getConfig().get("material_groups");
            List<String> inventoryTypes = (List<String>) getConfig().get("inventory_types");

            Bukkit.getPluginManager().registerEvents(new InventoryListener(
                    new InventorySorter(
                            materialGroups.stream()
                                .map(list -> list.stream()
                                       .map(Material::valueOf)
                                       .collect(Collectors.toList())
                                )
                                .collect(Collectors.toList()),
                            inventoryTypes.stream()
                                    .map(InventoryType::valueOf)
                                    .collect(Collectors.toSet())
                    )

            ), this);
        }
        catch (NullPointerException | IllegalArgumentException | ClassCastException exception) {
            StringWriter writer = new StringWriter();
            exception.printStackTrace(new PrintWriter(writer));
            getLogger().severe(
                    String.format(
                            "Failed to load configuration: \n%s",
                            writer.toString()
                    )
            );
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }
}
