package nukeduck.armorchroma.mixin;

import java.util.List;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import nukeduck.armorchroma.ArmorChroma;
import nukeduck.armorchroma.MaterialHelper;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

  @Shadow
  public abstract Item getItem();

  /** Adds the item material to the tooltip */
  @Inject(method = "getTooltip", at = @At("RETURN"))
  private void onGetTooltip(
      Item.TooltipContext context,
      @Nullable PlayerEntity player,
      TooltipType type,
      CallbackInfoReturnable<List<Text>> info) {
    if (ArmorChroma.config.showMaterialInTooltip()) {
      Item item = getItem();
      String material = MaterialHelper.getMaterial(item);

      if (material != null) {
        info.getReturnValue()
            .add(
                Text.translatable("armorchroma.tooltip.material", material)
                    .formatted(Formatting.DARK_GRAY));
      }
    }
  }
}
