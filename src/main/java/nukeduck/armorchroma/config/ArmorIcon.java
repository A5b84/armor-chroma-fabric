package nukeduck.armorchroma.config;

import static nukeduck.armorchroma.ArmorChroma.TEXTURE_SIZE;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import java.util.Objects;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import nukeduck.armorchroma.ArmorChromaRenderPipelines;

public class ArmorIcon {
  public static final int ICON_SIZE = 9;
  private static final int SPAN = TEXTURE_SIZE / ICON_SIZE;
  private static final String TEXTURE_PATH = "textures/gui/armor_chroma.png";

  public final Identifier texture;
  public final int u, v;
  public final int color;

  public ArmorIcon(int i) {
    this(i, Colors.WHITE);
  }

  public ArmorIcon(int i, int color) {
    this(Identifier.DEFAULT_NAMESPACE, i, color);
  }

  public ArmorIcon(String modid, int i) {
    this(modid, i, Colors.WHITE);
  }

  public ArmorIcon(String modid, int i, int color) {
    texture = Identifier.of(modid, TEXTURE_PATH);

    if (i >= 0) {
      u = (i % SPAN) * ICON_SIZE;
      v = (i / SPAN) * ICON_SIZE;
    } else {
      u = TEXTURE_SIZE + (i % SPAN) * ICON_SIZE;
      v = TEXTURE_SIZE + ((i + 1) / SPAN - 1) * ICON_SIZE;
    }
    this.color = color;
  }

  public void draw(DrawContext context, int x, int y) {
    draw(context, RenderPipelines.GUI_TEXTURED, x, y);
  }

  public void drawMasked(DrawContext context, int x, int y) {
    draw(context, ArmorChromaRenderPipelines.MASKED_ICON_PIPELINE, x, y);
  }

  private void draw(DrawContext context, RenderPipeline pipeline, int x, int y) {
    context.drawTexture(
        pipeline, texture, x, y, u, v, ICON_SIZE, ICON_SIZE, TEXTURE_SIZE, TEXTURE_SIZE, color);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    ArmorIcon armorIcon = (ArmorIcon) o;
    return u == armorIcon.u
        && v == armorIcon.v
        && color == armorIcon.color
        && Objects.equals(texture, armorIcon.texture);
  }
}
