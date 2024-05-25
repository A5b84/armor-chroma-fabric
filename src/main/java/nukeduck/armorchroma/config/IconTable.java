package nukeduck.armorchroma.config;

import net.minecraft.item.ArmorItem;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import nukeduck.armorchroma.compat.ArmoredElytra;

import java.util.HashMap;
import java.util.Map;

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
        if(stack == null) return null;

        Integer i = null;
        Item item = stack.getItem();

        if(item instanceof ArmorItem armorItem) {
            i = Util.getGlob(materials, armorItem.getMaterial().getName());
        }
        if(item instanceof ElytraItem) {
            ItemStack elytraStack = ArmoredElytra.getChestplate(stack);
            if (elytraStack != null)
            {
                ArmorItem armorItem = (ArmorItem) elytraStack.getItem();
                i = Util.getGlob(materials, armorItem.getMaterial().getName());
            }
        }
        if(i == null) {
            i = Util.getGlob(items, Registries.ITEM.getId(item).getPath());
        }
        return i;
    }

    public Integer getSpecialIndex(String key) {
        return special.get(key);
    }
}
