package app.velyntora.windows.app;
import java.nio.file.*;
/** OS-specific names and XDG paths; the editor modules remain shared. */
public final class DesktopPlatform {
 private DesktopPlatform(){}
 public static boolean linux(){return System.getProperty("os.name","").toLowerCase(java.util.Locale.ROOT).contains("linux");}
 public static String label(){return linux()?"Linux x64":"Windows x64";}
 public static String pressureHelp(){return linux()?"La presión usa XInput2 y depende de la tableta y su controlador; con ratón se usa presión uniforme.":"La presión usa Windows Ink y depende del controlador de la tableta.";}
 private static Path xdg(String variable,String fallback){String value=System.getenv(variable);if(value!=null&&!value.isBlank()){Path path=Path.of(value);if(path.isAbsolute())return path;}return Path.of(System.getProperty("user.home"),fallback);}
 public static Path stateDirectory(){if(linux())return xdg("XDG_STATE_HOME",".local/state").resolve("velyntora/drawing");return Path.of(System.getenv().getOrDefault("LOCALAPPDATA",System.getProperty("user.home")),"Velyntora","Drawing");}
 public static Path cacheDirectory(){if(linux())return xdg("XDG_CACHE_HOME",".cache").resolve("velyntora/drawing");return Path.of(System.getProperty("java.io.tmpdir"),"velyntora-drawing");}
}
