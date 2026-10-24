package app.velyntora.windows.input;
import art.velyntora.core.DrawingView;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import java.awt.Window;
/** Windows Ink -> AWT mouse promotion with native pen-pressure samples. */
public final class PointerInput {public static void attach(Window window){if(app.velyntora.windows.app.DesktopPlatform.linux()){DrawingView.nativeAttachPointer(0);return;}if(window.isDisplayable())DrawingView.nativeAttachPointer(Pointer.nativeValue(Native.getWindowPointer(window)));}}
