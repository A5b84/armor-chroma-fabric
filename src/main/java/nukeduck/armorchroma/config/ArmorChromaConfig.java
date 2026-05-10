package nukeduck.armorchroma.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.Tooltip;
import nukeduck.armorchroma.ArmorChroma;

public class ArmorChromaConfig {

  public boolean isEnabled() {
    return true;
  }

  public boolean renderGlint() {
    return true;
  }

  public float glintIntensity() {
    return 1;
  }

  public boolean renderBackground() {
    return true;
  }

  public boolean compressBar() {
    return false;
  }

  public int getDisplayedArmorCap() {
    return 5 * 20;
  }

  public boolean reverse() {
    return false;
  }

  public boolean showMaterialInTooltip() {
    return false;
  }

  /** Config class requiring AutoConfig */
  @SuppressWarnings("FieldMayBeFinal")
  @Config(name = ArmorChroma.MODID)
  public static class ArmorChromaAutoConfig extends ArmorChromaConfig implements ConfigData {

    private boolean enabled = super.isEnabled();
    @Tooltip private boolean renderGlint = super.renderGlint();
    private float glintIntensity = super.glintIntensity();
    @Tooltip private boolean renderBackground = super.renderBackground();
    @Tooltip private boolean compressBar = super.compressBar();
    @Tooltip private int displayedArmorCap = super.getDisplayedArmorCap();
    private boolean reverse = super.reverse();
    @Tooltip private boolean showMaterialInTooltip = super.reverse();

    @Override
    public boolean isEnabled() {
      return enabled;
    }

    @Override
    public boolean renderGlint() {
      return renderGlint;
    }

    @Override
    public float glintIntensity() {
      return glintIntensity;
    }

    @Override
    public boolean renderBackground() {
      return renderBackground;
    }

    @Override
    public boolean compressBar() {
      return compressBar;
    }

    @Override
    public int getDisplayedArmorCap() {
      return displayedArmorCap;
    }

    @Override
    public boolean reverse() {
      return reverse;
    }

    @Override
    public boolean showMaterialInTooltip() {
      return showMaterialInTooltip;
    }
  }
}
