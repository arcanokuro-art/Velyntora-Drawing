package art.velyntora.core;

/** Exact user-requested exclusions; all other tools retain their brush settings access. */
final class BrushSettingsPolicy {
  static boolean available(int tool){
    return switch(tool){
      case DrawingView.MOVE_PIXELS,DrawingView.MOVE_SELECTION,DrawingView.ZOOM,
        DrawingView.PICKER,DrawingView.PAN,DrawingView.SELECT_RECTANGLE,
        DrawingView.SELECT_ELLIPSE,DrawingView.SELECT_FREE -> false;
      default -> true;
    };
  }
  private BrushSettingsPolicy(){}
}
