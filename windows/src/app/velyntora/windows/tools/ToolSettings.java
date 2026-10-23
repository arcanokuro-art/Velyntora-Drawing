package app.velyntora.windows.tools;
import java.util.prefs.Preferences;
/** Per-tool opacity, width and hardness, preserved between sessions. */
public final class ToolSettings {
 private static final Preferences prefs=Preferences.userRoot().node("app/velyntora/drawing/tools");
 public record Values(int width,float opacity,float hardness,boolean square,boolean pressure){}
 public static Values load(Tool tool){return new Values(prefs.getInt(tool.name()+".width",8),prefs.getFloat(tool.name()+".opacity",1),prefs.getFloat(tool.name()+".hardness",1),prefs.getBoolean(tool.name()+".square",false),prefs.getBoolean(tool.name()+".pressure",true));}
 public static void save(Tool tool,int width,float opacity,float hardness){Values old=load(tool);save(tool,width,opacity,hardness,old.square(),old.pressure());}
 public static void save(Tool tool,int width,float opacity,float hardness,boolean square,boolean pressure){prefs.putBoolean(tool.name()+".square",square);prefs.putBoolean(tool.name()+".pressure",pressure);prefs.putInt(tool.name()+".width",width);prefs.putFloat(tool.name()+".opacity",opacity);prefs.putFloat(tool.name()+".hardness",hardness);}
}
