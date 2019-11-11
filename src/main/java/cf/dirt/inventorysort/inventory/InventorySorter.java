package cf.dirt.inventorysort.inventory;

import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class InventorySorter {

    private final Comparator<Material> materialComparator;
    private final Set<InventoryType> inventoryTypes;

    public InventorySorter(List<List<Material>> materialGroups, Set<InventoryType> inventoryTypes) {
        this.materialComparator = new GroupingComparator<>(materialGroups);
        this.inventoryTypes = inventoryTypes;
    }

    public boolean isSupported(InventoryType type) {
        return inventoryTypes.contains(type);
    }

    public void transferItems(Inventory from, Inventory to, Material type) {
        for (int i = 0, j = to.firstEmpty(); i < from.getSize() && j != -1; i++, j = to.firstEmpty()) {
            ItemStack stack = from.getItem(i);
            if (stack != null && stack.getType() == type) {
                from.setItem(i, null);
                to.setItem(j, stack);
            }
        }
    }

    public void sortItems(Inventory inventory) {
        ItemStack[] stacks = inventory.getStorageContents();
        sortItems(inventory, stacks, inventory.getType() != InventoryType.PLAYER ? 0 : 9); // shift for player inventory
    }

    private void sortItems(Inventory inventory, ItemStack[] stacks, int shift) {
        Map.Entry<Material, Stack<ItemStack>>[] sortedGroups = sortGroups(groupStacks(stacks, shift));

        for (Map.Entry<Material, Stack<ItemStack>> sortedGroup : sortedGroups) {
            for (ItemStack stack : sortedGroup.getValue()) {
                inventory.setItem(shift++, stack);
            }
        }

        for (int i = shift; i < inventory.getSize() && i < 36; i++) { // cap for player inventory
            inventory.setItem(i, null);
        }
    }

    private static void pushGroup(Stack<ItemStack> previousStacks, ItemStack currentStack) {
        ItemStack previousStack = previousStacks.peek();

        final int previousSlots = previousStack.getMaxStackSize() - previousStack.getAmount();
        final int currentSlots = currentStack.getAmount();

        if (previousSlots < currentSlots) {
            previousStack.setAmount(previousStack.getAmount() + previousSlots);
            currentStack.setAmount(currentStack.getAmount() - previousSlots);
            previousStacks.add(currentStack);
        } else {
            previousStack.setAmount(previousStack.getAmount() + currentSlots);
        }
    }

    private static Map<Material, Stack<ItemStack>> groupStacks(ItemStack[] stacks, final int shift) {
        Map<Material, Stack<ItemStack>> groupedStacks = new HashMap<>();

        for (int i = shift; i < stacks.length; i++) {
            ItemStack currentStack = stacks[i];

            if (currentStack != null) {
                Material currentType = currentStack.getType();
                Stack<ItemStack> previousStacks = groupedStacks.get(currentType);

                if (previousStacks != null) {
                    pushGroup(previousStacks, currentStack);
                } else {
                    previousStacks = new Stack<>();
                    previousStacks.add(currentStack);
                    groupedStacks.put(currentType, previousStacks);
                }
            }
        }

        return groupedStacks;
    }

    @SuppressWarnings("unchecked")
    private Map.Entry<Material, Stack<ItemStack>>[] sortGroups(Map<Material, Stack<ItemStack>> groups) {
        Map.Entry<Material, Stack<ItemStack>>[] groupEntries = groups.entrySet().toArray(
                (Map.Entry<Material, Stack<ItemStack>>[]) new Map.Entry[groups.size()]
        );

        Arrays.sort(groupEntries, (e1, e2) -> {
            final int materialDifference = materialComparator.compare(e1.getKey(), e2.getKey());
            if (materialDifference != 0) {
                return materialDifference;
            }

            final int sizeDifference = Integer.compare(e2.getValue().size(), e1.getValue().size());
            if (sizeDifference != 0) {
                return sizeDifference;
            }

            return Integer.compare(
                    e2.getValue().peek().getAmount(),
                    e1.getValue().peek().getAmount()
            );
        });

        return groupEntries;
    }
}
