package nukeduck.armorchroma;

import java.util.ArrayList;
import java.util.List;

/**
 * Object that can split {@link ArmorBarSegment}s into rows of {@link GuiArmor#ARMOR_PER_ROW} points
 * each.
 */
public class ArmorBarSegmentRowSplitter {

  private final List<List<ArmorBarSegment>> rows = new ArrayList<>();
  private int currentRowPoints = GuiArmor.ARMOR_PER_ROW;

  public void add(ArmorBarSegment segment) {
    while (true) {
      if (currentRowPoints >= GuiArmor.ARMOR_PER_ROW) {
        startNewRow();
      }

      int remainingOnCurrentRow = GuiArmor.ARMOR_PER_ROW - currentRowPoints;

      if (remainingOnCurrentRow >= segment.getArmorPoints()) {
        addWithoutSplitting(segment);
        break;
      } else {
        segment.addArmorPoints(-remainingOnCurrentRow);
        ArmorBarSegment addedSegment = segment.clone();
        addedSegment.setArmorPoints(remainingOnCurrentRow);
        addWithoutSplitting(addedSegment);
      }
    }
  }

  private void startNewRow() {
    rows.add(new ArrayList<>());
    currentRowPoints = 0;
  }

  private void addWithoutSplitting(ArmorBarSegment segment) {
    rows.getLast().add(segment);
    currentRowPoints += segment.getArmorPoints();
  }

  public List<List<ArmorBarSegment>> getRows() {
    return rows;
  }

  public static List<List<ArmorBarSegment>> splitIntoRows(List<ArmorBarSegment> segments) {
    ArmorBarSegmentRowSplitter splitter = new ArmorBarSegmentRowSplitter();

    for (ArmorBarSegment segment : segments) {
      splitter.add(segment);
    }

    return splitter.getRows();
  }
}
