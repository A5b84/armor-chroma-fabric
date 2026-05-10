package nukeduck.armorchroma.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.autoconfig.AutoConfig;
import nukeduck.armorchroma.ArmorChroma;
import nukeduck.armorchroma.config.ArmorChromaConfig.ArmorChromaAutoConfig;

public class ModMenuIntegration implements ModMenuApi {

  private static final ConfigScreenFactory<?> FACTORY =
      ArmorChroma.USE_AUTO_CONFIG
          ? parent -> AutoConfig.getConfigScreen(ArmorChromaAutoConfig.class, parent).get()
          : parent -> null;

  @Override
  public ConfigScreenFactory<?> getModConfigScreenFactory() {
    return FACTORY;
  }
}
