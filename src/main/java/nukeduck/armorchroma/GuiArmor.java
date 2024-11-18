package nukeduck.armorchroma;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import nukeduck.armorchroma.config.ArmorIcon;
import nukeduck.armorchroma.config.Util;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import static net.minecraft.client.render.item.ItemRenderer.ITEM_ENCHANTMENT_GLINT;
import static nukeduck.armorchroma.ArmorChroma.TEXTURE_SIZE;

/**
 * Renders the armor bar in the HUD
 */
public class GuiArmor {

    private static final Identifier BACKGROUND = Identifier.of(ArmorChroma.MODID, "textures/gui/background.png");

    /**
     * The colors used for the border of the bar at different levels
     * @see #drawBackground(DrawContext, int, int, int)
     */
    private static final int[] BG_COLORS = {0xff3acaff, 0xff3be55a, 0xffffff00, 0xffff9d00, 0xffed3200, 0xff7130c1};

    /**
     * The vertical distance between the top of each row
     */
    private static final int ROW_SPACING = 5;

    /**
     * The number of armor points per row in the armor bar
     */
    private static final int ARMOR_PER_ROW = 20;

    /**
     * Fallback attributes required when getting the player's armor
     */
    private static final DefaultAttributeContainer FALLBACK_ATTRIBUTES = DefaultAttributeContainer.builder()
            .add(EntityAttributes.ARMOR).build();

    private final MinecraftClient client = MinecraftClient.getInstance();

    /**
     * Render the bar as a full replacement for vanilla
     */
    public void draw(DrawContext context, int left, int top) {
        Map<EquipmentSlot, Integer> pointsMap = new LinkedHashMap<>();
        int totalPoints = getArmorPoints(client.player, pointsMap);
        if (totalPoints <= 0) return;

        // Total points in all rows so far
        int barPoints = 0;

        int compressedRows = ArmorChroma.config.compressBar() ? compressRows(pointsMap, totalPoints) : 0;

        context.getMatrices().push();
        addZOffset(context, -2); // Accounts for the +2 glint rect offset

        for (Entry<EquipmentSlot, Integer> entry : pointsMap.entrySet()) {
            //noinspection ConstantConditions (nullable stuff)
            drawPiece(context, left, top, barPoints, entry.getValue(), client.player.getEquippedStack(entry.getKey()));
            barPoints += entry.getValue();
        }
        // Most negative zOffset here
        drawBackground(context, left, top, compressedRows);
    }

    /**
     * Draws the armor bar background and, if {@code level > 0}, with a border
     * @param level The colored border level where a level is one full row
     * ({@link #ARMOR_PER_ROW} armor points)
     */
    private void drawBackground(DrawContext context, int x, int y, int level) {
        boolean drawBorder = level > 0;

        // Plain background
        if (ArmorChroma.config.renderBackground() || drawBorder) {
            RenderSystem.setShaderColor(1, 1, 1, 1);
            context.drawTexture(RenderLayer::getGuiTextured, BACKGROUND, x, y, 0, 0, 81, 9, TEXTURE_SIZE, TEXTURE_SIZE);

            // Colored border
            if (drawBorder) {
                int color = level <= BG_COLORS.length ? BG_COLORS[level - 1] : BG_COLORS[BG_COLORS.length - 1];
                context.drawTexture(RenderLayer::getGuiTextured, BACKGROUND, x - 1, y - 1, 81, 0, 83, 11, TEXTURE_SIZE, TEXTURE_SIZE, color);
            }
        }

    }

    /**
     * Draws all the rows needed for a single piece of armor
     * @param barPoints The number of points in the bar before this piece
     */
    private void drawPiece(DrawContext context, int left, int top, int barPoints, int stackPoints, ItemStack stack) {
        int space;
        top -= (barPoints / ARMOR_PER_ROW) * ROW_SPACING; // Offset to account for full bars

        // Repeatedly fill rows when possible
        while ((space = ARMOR_PER_ROW - (barPoints % ARMOR_PER_ROW)) <= stackPoints) {
            drawPartialRow(context, left, top, ARMOR_PER_ROW - space, space, stack);
            addZOffset(context, -3); // Move out of range of glint offset

            // Move up a row
            top -= ROW_SPACING;
            barPoints += space;
            stackPoints -= space;
        }

        // Whatever's left over (doesn't fill the whole row)
        if (stackPoints > 0) {
            drawPartialRow(context, left, top, ARMOR_PER_ROW - space, stackPoints, stack);
            addZOffset(context, -1);
        }
    }

    /**
     * Renders a partial row of icons, {@code stackPoints} wide
     * @param barPoints The points already in the bar
     */
    private void drawPartialRow(DrawContext context, int left, int top, int barPoints, int stackPoints, ItemStack stack) {
        ArmorIcon icon = ArmorChroma.ICON_DATA.getIcon(stack);
        boolean glint = ArmorChroma.config.renderGlint() && stack.hasGlint();

        if (glint) {
            addZOffset(context, 2); // Glint rows should appear on top of normal rows
        }

        int i = barPoints & 1;
        int x = left + barPoints * 4;

        // Drawing icons starts here

        if (i == 1) { // leading half icon
            drawMaskedIcon(context, x - 4, top, icon, ArmorChroma.ICON_DATA.getSpecial(Util.getModid(stack), "leadingMask"));
            x += 4;
        }

        for (; i < stackPoints - 1; i += 2, x += 8) { // Main body icons
            icon.draw(context, x, top);
        }

        if (i < stackPoints) { // Trailing half icon
            drawMaskedIcon(context, x, top, icon, ArmorChroma.ICON_DATA.getSpecial(Util.getModid(stack), "trailingMask"));
        }

        if (glint) { // Draw one glint quad for the whole row
            drawTexturedGlintRect(context, left + barPoints * 4, top, left, top, stackPoints * 4 + 1, ArmorIcon.ICON_SIZE);
            addZOffset(context, -2);
        }
    }

    /**
     * Finds all items in the player's equipment slots that provide armor
     * @param player The player holding the items
     * @param pointsMap The map of each slot's points
     * @return The total number of armor points the player has
     */
    private int getArmorPoints(ClientPlayerEntity player, Map<EquipmentSlot, Integer> pointsMap) {
        AttributeContainer attributes = new AttributeContainer(FALLBACK_ATTRIBUTES);
        EntityAttributeInstance armor = attributes.getCustomInstance(EntityAttributes.ARMOR);
        if (armor == null) return 0;

        int displayedArmorCap = ArmorChroma.config.getDisplayedArmorCap();
        int attrLast = (int) ((EntityAttributeInstanceAccess) armor).armorChroma_getUnclampedValue();

        EquipmentSlot[] slots = EquipmentSlot.values();
        if (ArmorChroma.config.reverse()) {
            Util.reverse(slots);
        }

        for (EquipmentSlot slot : slots) {
            player.getEquippedStack(slot).applyAttributeModifiers(slot, (attribute, modifier) -> {
                if (attribute == EntityAttributes.ARMOR) {
                    armor.addTemporaryModifier(modifier);
                }
            });

            int attrNext = Math.min(displayedArmorCap, (int) ((EntityAttributeInstanceAccess) armor).armorChroma_getUnclampedValue());
            int points = attrNext - attrLast;
            attrLast = attrNext;

            if (points > 0) pointsMap.put(slot, points);
        }

        return attrLast;
    }

    /**
     * Removes leading full rows from the points map
     * @param pointsMap The map of slots to points, traversed in entrySet order
     * @return The number of compressed rows
     */
    private int compressRows(Map<EquipmentSlot, Integer> pointsMap, int totalPoints) {
        int compressedRows = (totalPoints - 1) / ARMOR_PER_ROW;
        int compressedPoints = compressedRows * ARMOR_PER_ROW;
        Iterator<Entry<EquipmentSlot, Integer>> it = pointsMap.entrySet().iterator();

        for (int i = 0; i < compressedPoints; ) {
            Entry<EquipmentSlot, Integer> entry = it.next();
            int d = Math.min(compressedPoints - i, entry.getValue());

            if (d == entry.getValue()) {
                it.remove();
            } else {
                entry.setValue(entry.getValue() - d);
            }
            i += d;
        }
        return compressedRows;
    }

    private void drawMaskedIcon(DrawContext context, int x, int y, ArmorIcon icon, ArmorIcon mask) {
        mask.draw(context, x, y);
        icon.drawMasked(context, x, y);
    }

    /**
     * Render an item glint over the specified quad, blending with equal depth
     */
    @SuppressWarnings("SameParameterValue")
    private void drawTexturedGlintRect(DrawContext context, int x, int y, float u, float v, int width, int height) {
        float intensity = client.options.getGlintStrength().getValue().floatValue()
                * ArmorChroma.config.glintIntensity();
        int color = ColorHelper.getWhite(MathHelper.clamp(intensity, 0, 1));
        context.drawTexture(id -> RenderLayer.getGlint(), ITEM_ENCHANTMENT_GLINT, x, y, u, v, width, height, TEXTURE_SIZE, TEXTURE_SIZE, color);
    }

    private void addZOffset(DrawContext context, int z) {
        context.getMatrices().translate(0, 0, z);
    }
}
