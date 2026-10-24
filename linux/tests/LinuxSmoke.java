import app.velyntora.windows.app.DesktopPlatform;
import app.velyntora.windows.document.*;
import art.velyntora.core.DrawingView;
import java.nio.file.*;
import java.util.Arrays;
public final class LinuxSmoke {
 private static void check(boolean value,String message){if(!value)throw new AssertionError(message);}
 public static void main(String[] args)throws Exception {
  check(DesktopPlatform.linux(),"Linux platform detection");check(DesktopPlatform.label().equals("Linux x64"),"Linux UI identity");String state=System.getenv("XDG_STATE_HOME");Path expected=state!=null&&!state.isBlank()&&Path.of(state).isAbsolute()?Path.of(state):Path.of(System.getProperty("user.home"),".local/state");check(DesktopPlatform.stateDirectory().equals(expected.resolve("velyntora/drawing")),"XDG absolute path or standard fallback");check(DocumentRecovery.file().startsWith(DesktopPlatform.stateDirectory()),"XDG recovery path");check(!DocumentRecovery.file().toString().contains("LOCALAPPDATA"),"no Windows data location");
  DesktopSmoke.main(args);
  Path folder=Files.createTempDirectory("velyntora-😀-日本-");Path project=folder.resolve("Ilustración 🖌.vlycore");check(DrawingView.nativeCreate(32,24),"Linux new document");DrawingView.nativeBeginEdit();DrawingView.nativeStyledStroke(1,1,20,12,2,0xffcc2244,1,1,false,false);int[] before=DrawingView.nativePixels();DocumentIO.saveProject(project);check(DrawingView.nativeCreate(8,8),"Linux reset");DocumentIO.open(project);check(Arrays.equals(before,DrawingView.nativePixels()),"POSIX supplementary Unicode project roundtrip");
  Path recovery=folder.resolve("Recuperación 😀.tmp");check(DrawingView.nativeRecoveryPath(recovery.toString(),DrawingView.nativeRevision()),"POSIX supplementary Unicode recovery");check(Files.size(recovery)>32,"valid Linux recovery payload");
  DrawingView.nativeAttachPointer(0);float pressure=DrawingView.nativePointerPressure();check(pressure>0&&pressure<=1,"XInput2 or mouse pressure fallback");
  System.out.println("Linux integration passed: XDG paths, POSIX file descriptors, supplementary Unicode, recovery, all desktop modules and pressure API.");
 }
}
