package cf.dirt.inventorysort.inventory;

import java.util.*;

public class GroupingComparator<T> implements Comparator<T> {

    private final Map<T, Integer> groups;

    public GroupingComparator(List<? extends Iterable<T>> groups) {
        this.groups = new HashMap<>();
        for (int i = 0; i < groups.size(); i++) {
            for (T key : groups.get(i)) {
                this.groups.put(key, groups.size() - i); // we want groups to be first
            }
        }
    }

    @SafeVarargs
    public GroupingComparator(Iterable<T>... groups) {
        this(List.of(groups));
    }

    @Override
    public int compare(T key1, T key2) {
        return Integer.compare(
                groups.getOrDefault(key2, 0), // same as above
                groups.getOrDefault(key1, 0)
        );
    }
}
