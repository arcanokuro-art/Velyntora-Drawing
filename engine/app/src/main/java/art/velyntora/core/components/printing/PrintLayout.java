package art.velyntora.core;
/** Pure page geometry: fit and center the entire image; never stretch or crop. */
public final class PrintLayout {
  public record Placement(float left,float top,float width,float height) {}
  public static Placement fit(int imageWidth,int imageHeight,int contentWidth,int contentHeight) {
    if(imageWidth<=0||imageHeight<=0||contentWidth<=0||contentHeight<=0)
      throw new IllegalArgumentException("Área de impresión no válida");
    double scale=Math.min(contentWidth/(double)imageWidth,contentHeight/(double)imageHeight);
    float width=(float)(imageWidth*scale),height=(float)(imageHeight*scale);
    return new Placement((contentWidth-width)/2f,(contentHeight-height)/2f,width,height);
  }
  private PrintLayout(){}
}
