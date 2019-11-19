package cf.dirt.inventorygroup.inventory;

import java.util.*;

public class GroupingComparator<T extends Enum<T>> implements Comparator<T> {

    private final Map<T, Integer> groups;

    public GroupingComparator(Class<T> clazz, List<? extends Iterable<T>> groups) {
        this.groups = new EnumMap<>(clazz); // for lookup performance
        for (int i = 0; i < groups.size(); i++) {
            for (T key : groups.get(i)) {
                this.groups.put(key, groups.size() - i); // we want groups to be first
            }
        }
    }

    @SafeVarargs
    public GroupingComparator(Class<T> clazz, Iterable<T>... groups) {
        this(clazz, List.of(groups));
    }

    @Override
    public int compare(T key1, T key2) {
        return Integer.compare(
                groups.getOrDefault(key2, 0), // same as above
                groups.getOrDefault(key1, 0)
        );
    }
}
