package nukeduck.armorchroma;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.platform.DestFactor;
import com.mojang.blaze3d.platform.SourceFactor;
import java.util.function.Function;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.util.Identifier;
import net.minecraft.util.TriState;
import net.minecraft.util.Util;

public class ArmorChromaRenderLayers {

  private static final BlendFunction MASKED_ICON_BLEND_FUNCTION =
      new BlendFunction(SourceFactor.DST_COLOR, DestFactor.ZERO);

  /** Based on {@link RenderPipelines#GUI_TEXTURED_OVERLAY}. */
  private static final RenderPipeline MASKED_ICON_PIPELINE =
      RenderPipelines.register(
          RenderPipeline.builder(RenderPipelines.POSITION_TEX_COLOR_SNIPPET)
              .withLocation(Identifier.of(ArmorChroma.MODID, "pipeline/masked_icon"))
              .withDepthTestFunction(DepthTestFunction.EQUAL_DEPTH_TEST)
              .withBlend(MASKED_ICON_BLEND_FUNCTION)
              .build());

  /** Based on {@link RenderLayer#GUI_TEXTURED_OVERLAY}. */
  @SuppressWarnings("JavadocReference")
  private static final Function<Identifier, RenderLayer> MASKED_ICON_RENDER_LAYER =
      Util.memoize(
          texture ->
              RenderLayer.of(
                  "armor_chroma_masked_icon",
                  RenderLayer.DEFAULT_BUFFER_SIZE,
                  MASKED_ICON_PIPELINE,
                  RenderLayer.MultiPhaseParameters.builder()
                      .texture(new RenderPhase.Texture(texture, TriState.DEFAULT, false))
                      .build(false)));

  public static RenderLayer getMaskedIcon(Identifier texture) {
    return MASKED_ICON_RENDER_LAYER.apply(texture);
  }
}
