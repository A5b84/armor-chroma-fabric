package nukeduck.armorchroma.config;

import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.Colors;

public final class Util {

  private Util() {}

  public static int getColor(ItemStack stack) {
    if (stack != null && stack.isIn(ItemTags.DYEABLE)) {
      return DyedColorComponent.getColor(stack, DyedColorComponent.DEFAULT_COLOR);
    } else {
      return Colors.WHITE;
    }
  }

  public static String getModId(ItemStack stack) {
    return stack != null ? Registries.ITEM.getId(stack.getItem()).getNamespace() : null;
  }

  public static <V> V getGlob(Map<String, V> map, String string) {
    if (map == null) return null;

    V value = map.get(string);
    if (value != null) return value;

    for (Entry<String, V> entry : map.entrySet()) {
      if (globMatches(entry.getKey(), string)) return entry.getValue();
    }

    return null;
  }

  /**
   * @return {@code true} if the name matches the template with wildcards
   */
  public static boolean globMatches(String template, String name) {
    if (template == null || name == null) return false;

    int tOffset, nOffset = 0, cardLength;
    int wild = template.indexOf('*');

    if (wild == -1) { // No wildcards
      return template.equals(name);
    } else if (!template.regionMatches(0, name, 0, wild)) { // Test leading card
      return false;
    }
    cardLength = wild;

    // Test *card until no more wildcards are left
    while (true) {
      // Card starts after nOffset, template wildcard is at tOffset
      tOffset = wild + 1;
      nOffset += cardLength;

      wild = template.indexOf('*', tOffset);

      // Loop break condition - the next card is at the end of the string
      if (wild == -1) {
        cardLength = template.length() - tOffset; // Length of the card
        return template.regionMatches(tOffset, name, name.length() - cardLength, cardLength);
      }

      cardLength = wild - tOffset;
      nOffset = indexOfRegion(name, nOffset, template, tOffset, cardLength);

      // Loop break condition - the current card is not present
      if (nOffset == -1) {
        return false;
      }
    }
  }

  /**
   * @return The lowest {@code i >= fromIndex} such that the first {@code length} characters of
   *     {@code haystack} starting at {@code i} match those of {@code needle} starting at {@code
   *     nOffset}
   * @see String#startsWith(String, int)
   */
  private static int indexOfRegion(
      String haystack, int fromIndex, String needle, int nOffset, int length) {
    for (int i = fromIndex; i <= haystack.length() - length; i++) {
      if (haystack.regionMatches(i, needle, nOffset, length)) {
        return i;
      }
    }
    return -1;
  }
}
