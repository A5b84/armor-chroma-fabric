package nukeduck.armorchroma.config;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import nukeduck.armorchroma.MaterialHelper;

public class IconTable {
  public final Map<String, Integer> materials = new HashMap<>();
  public final Map<String, Integer> items = new HashMap<>();
  public final Map<String, Integer> special = new HashMap<>();

  public void putAll(IconTable other) {
    materials.putAll(other.materials);
    items.putAll(other.items);
    special.putAll(other.special);
  }

  public Integer getIconIndex(ItemStack stack) {
    if (stack == null) return null;

    Integer i = null;
    Item item = stack.getItem();
    String material = MaterialHelper.getMaterial(item);

    if (material != null) {
      i = Util.getGlob(materials, material);
    }

    if (i == null) {
      i = Util.getGlob(items, Registries.ITEM.getId(item).getPath());
    }

    return i;
  }

  public Integer getSpecialIndex(String key) {
    return special.get(key);
  }
}
