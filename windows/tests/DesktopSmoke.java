import art.velyntora.core.*;
import app.velyntora.windows.document.DocumentIO;
import java.nio.file.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
public final class DesktopSmoke {
 static void check(boolean value,String description){if(!value)throw new AssertionError(description);}
 public static void main(String[] args)throws Exception{
  Path folder=Files.createTempDirectory("velyntora-windows-");DrawingView.nativeHistoryDirectory(folder.toString());
  check(DrawingView.nativeCreate(128,96),"native create");long empty=DrawingView.nativeRevision();DrawingView.nativeBeginEdit();DrawingView.nativeStyledStroke(10,10,50,40,4,0xffff0000,1,1,false,false);check(DrawingView.nativeRevision()!=empty,"revision");check(DrawingView.nativePixels()[10*128+10]==0xffff0000,"native stroke");check(DrawingView.nativeUndo(),"undo");check(DrawingView.nativePixels()[10*128+10]==0xffffffff,"undo pixels");check(DrawingView.nativeRedo(),"redo");
  check(DrawingView.nativeAddLayer(),"layers");check(DrawingView.nativeRenameLayer(1,"Capa 日本 🖌"),"unicode layer name");
  Path project=folder.resolve("Dibujo 日本.vlycore");DocumentIO.saveProject(project);check(DrawingView.nativeCreate(20,20),"reset");DocumentIO.open(project);check(DrawingView.nativeWidth()==128&&DrawingView.nativeLayerCount()==2,"project roundtrip");check(DrawingView.nativeLayerName(1).contains("日本"),"unicode persistence");
  Path png=folder.resolve("drawing.png");DocumentIO.export(png);check(javax.imageio.ImageIO.read(png.toFile()).getWidth()==128,"PNG export");
  for(var preset:BrushPresets.ALL){check(BrushPresets.begin(preset),"brush "+preset.label());DrawingView.nativeBeginEdit();DrawingView.nativeTipBrushStroke(10,20,60,20,5,0xff204080,.8f,1);}
  check(BrushPresets.ALL.length>=30,"bundled catalog");
  ExtendedSmoke.run(folder);
  SwingUtilities.invokeAndWait(()->{try{var home=new app.velyntora.windows.app.HomeWindow();home.setVisible(true);check(home.isShowing(),"Windows home");var editor=new app.velyntora.windows.app.EditorWindow(home);editor.setVisible(true);app.velyntora.windows.input.PointerInput.attach(editor);editor.refresh();editor.canvas.setSize(800,600);BufferedImage screenshot=new BufferedImage(800,600,BufferedImage.TYPE_INT_ARGB);editor.canvas.paint(screenshot.getGraphics());javax.imageio.ImageIO.write(screenshot,"png",folder.resolve("canvas.png").toFile());editor.dispose();home.dispose();}catch(Exception error){throw new RuntimeException(error);}});
  System.out.println("Windows smoke passed: JNI, stroke, undo/redo, layers, Unicode paths, VLYCORE, PNG, "+BrushPresets.ALL.length+" brushes, home/editor render.");
 }
}
