package nukeduck.armorchroma.config;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.fabricmc.fabric.api.resource.SimpleResourceReloadListener;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import nukeduck.armorchroma.ArmorChroma;

public class IconData implements SimpleResourceReloadListener<Void> {
  private final Map<String, IconTable> mods = new HashMap<>();

  private static final ArmorIcon FALLBACK_ICON = new ArmorIcon(0);
  private static final String DEFAULT = "default";
  private static final String MINECRAFT = Identifier.DEFAULT_NAMESPACE;
  private static final Identifier ID = Identifier.of(ArmorChroma.MODID, "icondata");

  @Override
  public Identifier getFabricId() {
    return ID;
  }

  /**
   * @return The armor icon corresponding to {@code stack}
   */
  public ArmorIcon getIcon(ItemStack stack) {
    String modId = Util.getModId(stack);
    IconTable mod = mods.get(modId);

    Integer i;

    if (mod != null) {
      i = mod.getIconIndex(stack);

      if (i != null) {
        return new ArmorIcon(modId, i, Util.getColor(stack));
      } else {
        return getSpecial(modId, SpecialIconKey.DEFAULT);
      }
    }

    return getSpecial(SpecialIconKey.DEFAULT);
  }

  public ArmorIcon getSpecial(SpecialIconKey key) {
    return getSpecial(MINECRAFT, key);
  }

  public ArmorIcon getSpecial(String modid, SpecialIconKey key) {
    ArmorIcon icon = getSpecialWithoutFallback(modid, key);

    if (icon == null && !modid.equals(MINECRAFT)) {
      icon = getSpecialWithoutFallback(MINECRAFT, key);
    }

    return icon != null ? icon : FALLBACK_ICON;
  }

  private ArmorIcon getSpecialWithoutFallback(String modid, SpecialIconKey key) {
    IconTable mod = mods.get(modid);

    if (mod != null) {
      Integer i = mod.getSpecialIndex(key.value);

      if (i != null) {
        return new ArmorIcon(modid, i);
      }
    }

    return null;
  }

  @Override
  public CompletableFuture<Void> load(ResourceManager manager, Executor executor) {
    return CompletableFuture.supplyAsync(
        () -> {
          mods.clear();

          for (ModContainer modContainer : FabricLoader.getInstance().getAllMods()) {
            String modid = modContainer.getMetadata().getId();
            List<Resource> resources =
                manager.getAllResources(Identifier.of(modid, "textures/gui/armor_chroma.json"));
            if (!resources.isEmpty()) {
              for (Resource resource : resources) {
                try (Reader reader =
                    new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
                  IconTable mod = new Gson().fromJson(reader, IconTable.class);

                  mods.merge(
                      modid,
                      mod,
                      (a, b) -> {
                        a.putAll(b);
                        return a;
                      });
                } catch (JsonSyntaxException | JsonIOException | IOException e) {
                  // If an error is caught here, continue to read the other files
                  ArmorChroma.LOGGER.error("[Armor Chroma] Error loading icons for {}", modid, e);
                }
              }
            } else if (MINECRAFT.equals(modid)) {
              throw new RuntimeException("Missing fallback icons. The mod is damaged");
            }
          }

          IconTable minecraft = mods.get(MINECRAFT);
          if (minecraft == null
              || minecraft.getSpecialIndex(DEFAULT) == null
              || minecraft.getSpecialIndex("leadingMask") == null
              || minecraft.getSpecialIndex("trailingMask") == null) {
            // This should never happen unless the mod has been edited
            throw new RuntimeException("Missing fallback icons. The mod is damaged");
          }

          // Ignore modid minecraft
          ArmorChroma.LOGGER.info("[Armor Chroma] Loaded icons for {} mods", mods.size() - 1);

          return null;
        });
  }

  @Override
  public CompletableFuture<Void> apply(Void data, ResourceManager manager, Executor executor) {
    return CompletableFuture.completedFuture(null);
  }
}
