package nukeduck.armorchroma.mixin;

import net.minecraft.client.item.TooltipType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Shadow public abstract Item getItem();

    /**
     * Adds the item material to the tooltip
     */
    @Inject(method = "getTooltip", at = @At("RETURN"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void onGetTooltip(Item.TooltipContext context, @Nullable PlayerEntity player, TooltipType type, CallbackInfoReturnable<List<Text>> info) {
        if (type.isAdvanced() && getItem() instanceof ArmorItem item) {
            String material = item.getMaterial().getIdAsString();
            info.getReturnValue().add(
                    Text.translatable("armorchroma.tooltip.material", material)
                            .formatted(Formatting.DARK_GRAY));
        }
    }

}
