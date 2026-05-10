package nukeduck.armorchroma.mixin;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.player.PlayerEntity;
import nukeduck.armorchroma.ArmorChroma;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Replaces the vanilla armor rendering with the mod's */
@Mixin(InGameHud.class)
public abstract class InGameHudMixin {

  /** Mojmap name: LINE_HEIGHT */
  @Shadow @Final private static int field_32170;

  /** Replaces the vanilla armor bar */
  @Inject(method = "renderArmor", at = @At("HEAD"), cancellable = true)
  private static void onBeforeRenderArmor(
      DrawContext context,
      PlayerEntity player,
      int top,
      int heartRows,
      int heartRowsSpacing,
      int left,
      CallbackInfo info) {
    if (ArmorChroma.config.isEnabled()) {
      info.cancel();
      top -= (heartRows - 1) * heartRowsSpacing + field_32170;
      ArmorChroma.GUI.draw(context, left, top);
    }
  }
}
