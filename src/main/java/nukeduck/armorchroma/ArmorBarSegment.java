package nukeduck.armorchroma;

import nukeduck.armorchroma.config.ArmorIcon;

public class ArmorBarSegment {

  private int armorPoints;
  private final ArmorIcon icon;
  private final ArmorIcon leadingMask;
  private final ArmorIcon trailingMask;
  private final boolean hasGlint;

  public ArmorBarSegment(
      int armorPoints,
      ArmorIcon icon,
      ArmorIcon leadingMask,
      ArmorIcon trailingMask,
      boolean hasGlint) {
    this.armorPoints = armorPoints;
    this.icon = icon;
    this.leadingMask = leadingMask;
    this.trailingMask = trailingMask;
    this.hasGlint = hasGlint;
  }

  public int getArmorPoints() {
    return armorPoints;
  }

  public void setArmorPoints(int armorPoints) {
    this.armorPoints = armorPoints;
  }

  public void addArmorPoints(int delta) {
    armorPoints += delta;
  }

  public ArmorIcon getIcon() {
    return icon;
  }

  public ArmorIcon getLeadingMask() {
    return leadingMask;
  }

  public ArmorIcon getTrailingMask() {
    return trailingMask;
  }

  public boolean hasGlint() {
    return hasGlint;
  }

  public boolean canMergeWith(ArmorBarSegment segment) {
    return hasGlint == segment.hasGlint
        && icon.equals(segment.icon)
        && leadingMask.equals(segment.leadingMask)
        && trailingMask.equals(segment.trailingMask);
  }
}
