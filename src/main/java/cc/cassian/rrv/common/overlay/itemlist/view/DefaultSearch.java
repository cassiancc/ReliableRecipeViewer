package cc.cassian.rrv.common.overlay.itemlist.view;

import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/// Pure matching over text captured on the client thread.
final class DefaultSearch {
    static final int NONE = 0;
    static final int FIRST = 1;
    static final int SECOND = 2;
    static final int THIRD = 3;
    static final int ALIAS = 4;
    static final int TOOLTIP = 5;

    record Entry(String name, List<String> aliases) {}

    private DefaultSearch() {}

    static int[] match(List<Entry> entries, String query, boolean searchTooltips) {
        String lowerQuery = query.toLowerCase(Locale.ROOT);
        String[] words = lowerQuery.contains(" ") ? lowerQuery.split(" ") : null;
        int[] matches = new int[entries.size()];
        for (int i = 0; i < entries.size(); i++) {
            Entry entry = entries.get(i);
            if (entry.name().startsWith(lowerQuery)) {
                matches[i] = FIRST;
            } else if (entry.name().contains(lowerQuery)) {
                matches[i] = SECOND;
            } else if (words != null && Arrays.stream(words).allMatch(entry.name()::contains)) {
                matches[i] = THIRD;
            } else if (!entry.aliases().isEmpty()) {
                if (entry.aliases().stream().anyMatch(alias -> alias.contains(lowerQuery))) {
                    matches[i] = ALIAS;
                }
            } else if (searchTooltips) {
                matches[i] = TOOLTIP;
            }
        }
        return matches;
    }
}
