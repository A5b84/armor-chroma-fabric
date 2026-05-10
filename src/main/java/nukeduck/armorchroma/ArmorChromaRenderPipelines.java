package nukeduck.armorchroma;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DestFactor;
import com.mojang.blaze3d.platform.SourceFactor;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.util.Identifier;

public class ArmorChromaRenderPipelines {

  private static final BlendFunction MASKED_ICON_BLEND_FUNCTION =
      new BlendFunction(SourceFactor.DST_COLOR, DestFactor.ZERO);

  /** Based on {@link RenderPipelines#GUI_TEXTURED}. */
  public static final RenderPipeline MASKED_ICON_PIPELINE =
      RenderPipelines.register(
          RenderPipeline.builder(RenderPipelines.POSITION_TEX_COLOR_SNIPPET)
              .withLocation(Identifier.of(ArmorChroma.MODID, "pipeline/masked_icon"))
              // .withDepthTestFunction(DepthTestFunction.EQUAL_DEPTH_TEST)
              .withBlend(MASKED_ICON_BLEND_FUNCTION)
              .build());
}
