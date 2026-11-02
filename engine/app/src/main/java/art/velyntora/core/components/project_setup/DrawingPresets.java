package art.velyntora.core;

import art.velyntora.core.document.CanvasLimits;

/** Drawing-only reference sizes, independent of UI and animation frame rates. */
public final class DrawingPresets {
  public static final class Preset {
    public final String name;
    public final int width,height;
    private Preset(String name,int width,int height){this.name=name;this.width=width;this.height=height;}
    public boolean supported(){return CanvasLimits.accepts(width,height);}
    public int[] compatibleSize(){
      double scale=Math.min(1d,Math.min((double)CanvasLimits.MAX_SIDE/width,(double)CanvasLimits.MAX_SIDE/height));
      return new int[]{Math.max(1,(int)Math.round(width*scale)),Math.max(1,(int)Math.round(height*scale))};
    }
    @Override public String toString(){return name+" · "+width+" × "+height+" px"+(supported()?"":" (requiere reducción)");}
  }
  private static final Preset[] ALL={
    new Preset("YouTube",1920,1080),new Preset("YouTube",1280,720),
    new Preset("TikTok",1080,1920),new Preset("TikTok",720,1280),
    new Preset("Pixiv",1700,2400),new Preset("Doujinshi",2500,3600),new Preset("Manga",3072,4096),
    new Preset("Manhwa",2400,3840)
  };
  public static Preset[] all(){return ALL.clone();}
  private DrawingPresets(){}
}
