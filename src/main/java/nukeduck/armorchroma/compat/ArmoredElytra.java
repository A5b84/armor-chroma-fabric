package nukeduck.armorchroma.compat;

import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;

public class ArmoredElytra {
    public static ItemStack getChestplate(ItemStack stack)
    {
        if (stack.getSubNbt("armElyData") != null)
        {
            return ItemStack.fromNbt(stack.getSubNbt("armElyData").getCompound("chestplate"));
        }
        return null;
    }
    public static int getColor(ItemStack stack)
    {
        ItemStack chestplate = getChestplate(stack);

        if(chestplate.getItem() == Items.LEATHER_CHESTPLATE)
        {
            if(chestplate.getNbt().contains("display"))
            {
                NbtCompound displayData = chestplate.getSubNbt("display");
                if(displayData.contains("color"))
                {
                    return displayData.getInt("color");
                }
            }
            DyeableItem dyeableItem = (DyeableItem) Items.LEATHER_CHESTPLATE;
            return dyeableItem.getColor(Items.LEATHER_CHESTPLATE.getDefaultStack());
        }
        return 0xffffff;
    }
}
