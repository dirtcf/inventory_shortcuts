package cf.dirt.inventorygroup;

import cf.dirt.inventorygroup.inventory.InventoryGrouper;
import cf.dirt.inventorygroup.listeners.InventoryListener;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.LinkedList;
import java.util.List;

@SuppressWarnings("unused")
public final class Plugin extends JavaPlugin {

    @SuppressWarnings("unchecked")
    public void onEnable() {
        saveDefaultConfig();

        try {
            List<InventoryType> inventoryTypes = new LinkedList<>();

            for (String typeString : (List<String>) getConfig().get("inventory_types")) {
                try {
                    InventoryType type = InventoryType.valueOf(typeString);
                    inventoryTypes.add(type);
                }
                catch (IllegalArgumentException exception) {
                    getLogger().warning(String.format(
                            "Skipping inventory type: %s, not found", typeString
                    ));
                }
            }

            getLogger().info(String.format(
                    "Loaded %d inventory types: %s",
                    inventoryTypes.size(), inventoryTypes
            ));

            List<List<Material>> materialGroups = new LinkedList<>();

            for (List<String> groupStrings : (List<List<String>>) getConfig().get("material_groups")) {
                List<Material> materialGroup = new LinkedList<>();

                for (String materialString : groupStrings) {
                    try {
                        Material material = Material.valueOf(materialString);
                        materialGroup.add(material);
                    }
                    catch (IllegalArgumentException exception) {
                        getLogger().warning(String.format(
                                "Skipping material: %s, not found", materialString
                        ));
                    }
                }

                materialGroups.add(materialGroup);
            }

            getLogger().info(String.format(
                    "Loaded %d material groups: %s",
                    materialGroups.size(), materialGroups
            ));

            Bukkit.getPluginManager().registerEvents(new InventoryListener(
                    new InventoryGrouper(materialGroups, inventoryTypes)
            ), this);
        }
        catch (NullPointerException | ClassCastException exception) {
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
