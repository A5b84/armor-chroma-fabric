package nukeduck.armorchroma.config;

public enum SpecialIconKey {
  DEFAULT("default"),
  LEADING_MASK("leadingMask"),
  TRAILING_MASK("trailingMask");

  public final String value;

  SpecialIconKey(String value) {
    this.value = value;
  }
}
