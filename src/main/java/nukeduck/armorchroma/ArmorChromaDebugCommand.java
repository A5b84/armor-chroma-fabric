package nukeduck.armorchroma;

import static com.mojang.brigadier.arguments.BoolArgumentType.bool;
import static com.mojang.brigadier.arguments.BoolArgumentType.getBool;
import static com.mojang.brigadier.arguments.DoubleArgumentType.doubleArg;
import static com.mojang.brigadier.arguments.DoubleArgumentType.getDouble;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ArmorChromaDebugCommand {

  private static final DynamicCommandExceptionType FAILED_ITEMLESS_EXCEPTION =
      new DynamicCommandExceptionType(
          entityName ->
              Text.stringifiedTranslatable("commands.enchant.failed.itemless", entityName));

  public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
    dispatcher.register(
        literal("armorchroma")
            .then(
                literal("setarmor")
                    .then(
                        argument("points", doubleArg())
                            .executes(ArmorChromaDebugCommand::executeSetArmor))
                    .then(literal("reset").executes(ArmorChromaDebugCommand::executeResetArmor)))
            .then(
                literal("setglint")
                    .then(
                        argument("glint", bool())
                            .executes(ArmorChromaDebugCommand::executeSetGlint))
                    .then(literal("reset").executes(ArmorChromaDebugCommand::executeResetGlint))));
  }

  private static int executeSetArmor(CommandContext<ServerCommandSource> context)
      throws CommandSyntaxException {
    ItemStack stack = getActiveStack(context);
    double points = getDouble(context, "points");
    AttributeModifiersComponent originalModifiersComponent =
        stack.get(DataComponentTypes.ATTRIBUTE_MODIFIERS);
    List<AttributeModifiersComponent.Entry> modifiers;

    if (originalModifiersComponent == null) {
      modifiers = new ArrayList<>(1);
    } else {
      modifiers =
          originalModifiersComponent.modifiers().stream()
              .filter(entry -> entry.attribute() != EntityAttributes.ARMOR)
              .collect(Collectors.toCollection(ArrayList::new));
    }

    EntityAttributeModifier attributeModifier =
        new EntityAttributeModifier(
            Identifier.of(ArmorChroma.MODID, UUID.randomUUID().toString()),
            points,
            EntityAttributeModifier.Operation.ADD_VALUE);
    modifiers.add(
        new AttributeModifiersComponent.Entry(
            EntityAttributes.ARMOR, attributeModifier, AttributeModifierSlot.ANY));
    stack.set(DataComponentTypes.ATTRIBUTE_MODIFIERS, new AttributeModifiersComponent(modifiers));

    return Command.SINGLE_SUCCESS;
  }

  private static int executeResetArmor(CommandContext<ServerCommandSource> context)
      throws CommandSyntaxException {
    ItemStack stack = getActiveStack(context);
    AttributeModifiersComponent oldComponent = stack.get(DataComponentTypes.ATTRIBUTE_MODIFIERS);

    if (oldComponent != null) {
      List<AttributeModifiersComponent.Entry> modifiers =
          oldComponent.modifiers().stream()
              .filter(
                  entry ->
                      entry.attribute() != EntityAttributes.ARMOR
                          || !entry.modifier().id().getNamespace().equals(ArmorChroma.MODID))
              .toList();

      stack.set(DataComponentTypes.ATTRIBUTE_MODIFIERS, new AttributeModifiersComponent(modifiers));
    }

    return Command.SINGLE_SUCCESS;
  }

  private static int executeSetGlint(CommandContext<ServerCommandSource> context)
      throws CommandSyntaxException {
    ItemStack stack = getActiveStack(context);
    boolean glint = getBool(context, "glint");

    if (glint) {
      stack.set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true);
    } else {
      stack.set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, false);
    }

    return Command.SINGLE_SUCCESS;
  }

  private static int executeResetGlint(CommandContext<ServerCommandSource> context)
      throws CommandSyntaxException {
    getActiveStack(context).remove(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE);
    return Command.SINGLE_SUCCESS;
  }

  private static ItemStack getActiveStack(CommandContext<ServerCommandSource> context)
      throws CommandSyntaxException {
    ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
    ItemStack stack = player.getMainHandStack();

    if (stack.isEmpty()) {
      throw FAILED_ITEMLESS_EXCEPTION.create(player.getName().getString());
    }

    return stack;
  }
}
