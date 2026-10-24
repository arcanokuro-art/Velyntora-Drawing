import art.velyntora.core.*;
import app.velyntora.windows.document.DocumentIO;
import java.nio.file.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
public final class DesktopSmoke {
 static void check(boolean value,String description){if(!value)throw new AssertionError(description);}
 public static void main(String[] args)throws Exception{
  SwingUtilities.invokeAndWait(()->{try{app.velyntora.windows.app.EditorTheme.install();}catch(Exception error){throw new IllegalStateException(error);}});
  ThemeSmoke.run();
  HomeSmoke.run();
  Path folder=Files.createTempDirectory("velyntora-windows-");DrawingView.nativeHistoryDirectory(folder.toString());
  check(DrawingView.nativeCreate(128,96),"native create");long empty=DrawingView.nativeRevision();DrawingView.nativeBeginEdit();DrawingView.nativeStyledStroke(10,10,50,40,4,0xffff0000,1,1,false,false);check(DrawingView.nativeRevision()!=empty,"revision");check(DrawingView.nativePixels()[10*128+10]==0xffff0000,"native stroke");check(DrawingView.nativeUndo(),"undo");check(DrawingView.nativePixels()[10*128+10]==0xffffffff,"undo pixels");check(DrawingView.nativeRedo(),"redo");
  check(DrawingView.nativeAddLayer(),"layers");check(DrawingView.nativeRenameLayer(1,"Capa 日本 🖌"),"unicode layer name");
  Path project=folder.resolve("Dibujo 日本.vlycore");DocumentIO.saveProject(project);check(DrawingView.nativeCreate(20,20),"reset");DocumentIO.open(project);check(DrawingView.nativeWidth()==128&&DrawingView.nativeLayerCount()==2,"project roundtrip");check(DrawingView.nativeLayerName(1).contains("日本"),"unicode persistence");
  Path png=folder.resolve("drawing.png");DocumentIO.export(png);check(javax.imageio.ImageIO.read(png.toFile()).getWidth()==128,"PNG export");
  for(var preset:BrushPresets.ALL){check(BrushPresets.begin(preset),"brush "+preset.label());DrawingView.nativeBeginEdit();DrawingView.nativeTipBrushStroke(10,20,60,20,5,0xff204080,.8f,1);}
  check(BrushPresets.ALL.length>=30,"bundled catalog");
  ExtendedSmoke.run(folder);
  ParitySmoke.run();
  DrawingView.nativeCreate(32,24);
  SwingUtilities.invokeAndWait(()->{try{var home=new app.velyntora.windows.app.HomeWindow();home.setVisible(true);check(home.isShowing(),"Windows home");var editor=new app.velyntora.windows.app.EditorWindow(home);editor.setVisible(true);app.velyntora.windows.input.PointerInput.attach(editor);editor.refresh();editor.canvas.setSize(800,600);editor.canvas.zoom=1;editor.canvas.rotation=0;editor.canvas.offsetX=editor.canvas.offsetY=0;
    String capture=System.getProperty("velyntora.uiCapture");if(capture!=null){
     DrawingView.nativeCreate(1920,1080);editor.canvas.setTool(app.velyntora.windows.tools.Tool.LINE);editor.canvas.size=183;editor.refresh();
     for(int[] dimensions:new int[][]{{1366,768},{1536,960}}){editor.setSize(dimensions[0],dimensions[1]);editor.validate();editor.canvas.fit();check(editor.canvas.getWidth()>400,"usable drawing area");JToggleButton line=ParitySmoke.named(editor,"Línea/Curva",JToggleButton.class);check(line!=null&&line.isSelected(),"active curve tool");check(!line.isContentAreaFilled(),"borderless sidebar");check(line.getParent().getComponent(20).getBounds().y+line.getParent().getComponent(20).getHeight()<=line.getParent().getHeight(),"all tools fit viewport");BufferedImage windowImage=new BufferedImage(editor.getWidth(),editor.getHeight(),BufferedImage.TYPE_INT_RGB);java.awt.Graphics2D graphics=windowImage.createGraphics();editor.paint(graphics);graphics.dispose();String path=dimensions[0]==1536?capture:capture.replace(".png","-1366.png");javax.imageio.ImageIO.write(windowImage,"png",Path.of(path).toFile());}
     DrawingView.nativeCreate(32,24);editor.canvas.setTool(app.velyntora.windows.tools.Tool.BRUSH);editor.refresh();editor.canvas.setSize(800,600);editor.canvas.zoom=1;editor.canvas.rotation=0;editor.canvas.offsetX=editor.canvas.offsetY=0;
    }
    var canvas=editor.canvas;long before=DrawingView.nativeRevision();canvas.tool=app.velyntora.windows.tools.Tool.BRUSH;
    canvas.dispatchEvent(new java.awt.event.MouseEvent(canvas,java.awt.event.MouseEvent.MOUSE_PRESSED,System.currentTimeMillis(),java.awt.event.InputEvent.BUTTON1_DOWN_MASK,5,5,1,false,java.awt.event.MouseEvent.BUTTON1));
    canvas.dispatchEvent(new java.awt.event.MouseEvent(canvas,java.awt.event.MouseEvent.MOUSE_DRAGGED,System.currentTimeMillis(),java.awt.event.InputEvent.BUTTON1_DOWN_MASK,10,10,0,false,java.awt.event.MouseEvent.NOBUTTON));
    canvas.dispatchEvent(new java.awt.event.MouseEvent(canvas,java.awt.event.MouseEvent.MOUSE_RELEASED,System.currentTimeMillis(),0,10,10,1,false,java.awt.event.MouseEvent.BUTTON1));check(DrawingView.nativeRevision()!=before,"real desktop gesture reaches engine");
    canvas.tool=app.velyntora.windows.tools.Tool.SELECT;
    canvas.dispatchEvent(new java.awt.event.MouseEvent(canvas,java.awt.event.MouseEvent.MOUSE_PRESSED,System.currentTimeMillis(),java.awt.event.InputEvent.BUTTON1_DOWN_MASK,4,4,1,false,java.awt.event.MouseEvent.BUTTON1));
    canvas.dispatchEvent(new java.awt.event.MouseEvent(canvas,java.awt.event.MouseEvent.MOUSE_DRAGGED,System.currentTimeMillis(),java.awt.event.InputEvent.BUTTON1_DOWN_MASK,14,14,0,false,java.awt.event.MouseEvent.NOBUTTON));
    canvas.dispatchEvent(new java.awt.event.MouseEvent(canvas,java.awt.event.MouseEvent.MOUSE_RELEASED,System.currentTimeMillis(),0,14,14,1,false,java.awt.event.MouseEvent.BUTTON1));check(canvas.selection[8*32+8]!=0,"selection gesture");canvas.invertSelection();check(canvas.selection[8*32+8]==0,"invert selection");canvas.invertSelection();canvas.morphSelection(true);check(canvas.selection[8*32+3]!=0,"expand selection");canvas.morphSelection(false);check(canvas.selection[8*32+3]==0,"shrink selection");canvas.clearSelection();BufferedImage screenshot=new BufferedImage(800,600,BufferedImage.TYPE_INT_ARGB);editor.canvas.paint(screenshot.getGraphics());javax.imageio.ImageIO.write(screenshot,"png",folder.resolve("canvas.png").toFile());editor.dispose();home.dispose();}catch(Exception error){throw new RuntimeException(error);}});
  System.out.println("Windows smoke passed: JNI, stroke, undo/redo, layers, Unicode paths, VLYCORE, PNG, "+BrushPresets.ALL.length+" brushes, home/editor render.");
 }
}
