package nukeduck.armorchroma;

import java.util.Optional;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.item.Item;
import net.minecraft.item.equipment.EquipmentAsset;
import net.minecraft.item.equipment.EquipmentAssetKeys;
import net.minecraft.registry.RegistryKey;
import org.jetbrains.annotations.Nullable;

public class MaterialHelper {

  private static final String TURTLE_MATERIAL = "turtle";

  @Nullable
  public static String getMaterial(Item item) {
    EquippableComponent equippableComponent =
        item.getComponents().get(DataComponentTypes.EQUIPPABLE);

    if (equippableComponent != null) {
      // Armor materials don't have IDs anymore since 1.21.2, and models
      // are the closest thing to materials (with some exceptions such
      // as turtle scutes)
      Optional<RegistryKey<EquipmentAsset>> optionalAsset = equippableComponent.assetId();

      if (optionalAsset.isPresent()) {
        RegistryKey<EquipmentAsset> asset = optionalAsset.get();

        if (asset.equals(EquipmentAssetKeys.TURTLE_SCUTE)) {
          return TURTLE_MATERIAL;
        } else {
          return asset.getValue().getPath();
        }
      }
    }

    return null;
  }
}
