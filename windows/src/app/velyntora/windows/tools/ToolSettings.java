package app.velyntora.windows.tools;
import java.util.prefs.Preferences;
/** Per-tool opacity, width and hardness, preserved between sessions. */
public final class ToolSettings {
 private static final Preferences prefs=Preferences.userRoot().node("app/velyntora/drawing/tools");
 public record Values(int width,float opacity,float hardness){}
 public static Values load(Tool tool){return new Values(prefs.getInt(tool.name()+".width",8),prefs.getFloat(tool.name()+".opacity",1),prefs.getFloat(tool.name()+".hardness",1));}
 public static void save(Tool tool,int width,float opacity,float hardness){prefs.putInt(tool.name()+".width",width);prefs.putFloat(tool.name()+".opacity",opacity);prefs.putFloat(tool.name()+".hardness",hardness);}
}
