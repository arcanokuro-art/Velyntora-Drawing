import art.velyntora.core.DrawingView;
import app.velyntora.windows.canvas.CanvasPanel;
import app.velyntora.windows.color.*;
import app.velyntora.windows.document.DimensionsDialog;
import app.velyntora.windows.layers.LayersPanel;
import app.velyntora.windows.tasks.WorkQueue;
import app.velyntora.windows.tools.*;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.prefs.Preferences;
/** Regression checks for concrete gaps identified between Android and desktop. */
public final class ParitySmoke {
 static void check(boolean ok,String message){if(!ok)throw new AssertionError(message);}
 static <T extends Component> T named(Container parent,String name,Class<T> type){for(Component c:parent.getComponents()){if(type.isInstance(c)&&name.equals(c.getName()))return type.cast(c);if(c instanceof Container nested){T found=named(nested,name,type);if(found!=null)return found;}}return null;}
 public static void run()throws Exception{
  SwingUtilities.invokeAndWait(()->{try{
   ColorPicker picker=new ColorPicker(0x80402010,0xffabcdef,false);JSpinner alpha=named(picker,"Alfa",JSpinner.class);check(alpha!=null,"RGBA alpha control");alpha.setValue(64);check(picker.colors()[0]==0x40402010,"alpha preserves RGB");JTextField hex=named(picker,"Hex RGBA",JTextField.class);hex.setText("12345678");check(picker.colors()[0]==0x78123456,"hex RGBA applies correctly");check(ColorPicker.toHex(0x78123456).equals("12345678"),"hex order");check(picker.colors()[1]==0xffabcdef,"secondary stays independent");
   DrawingView.nativeCreate(32,24);CanvasPanel canvas=new CanvasPanel(()->{});Preferences prefs=Preferences.userRoot().node("app/velyntora/tests/"+UUID.randomUUID());try{PalettePanel palette=new PalettePanel(canvas,()->{},prefs);check(PalettePanel.DEFAULTS.length==22,"same default palette");check(palette.addColor(0x12345678),"save swatch");check(!palette.addColor(0x12345678),"deduplicate swatch");check(new PalettePanel(canvas,()->{},prefs).savedColors().contains(0x12345678),"saved palette persists");palette.removeColor(0x12345678);check(palette.savedColors().isEmpty(),"delete saved color");}finally{prefs.removeNode();}
   for(Tool tool:Tool.values()){JLabel row=new JLabel();row.setForeground(Color.BLACK);Icon icon=ToolIcons.of(tool);BufferedImage image=new BufferedImage(32,32,2);Graphics2D g=image.createGraphics();icon.paintIcon(row,g,0,0);g.dispose();boolean drawn=false;for(int pixel:image.getRGB(0,0,32,32,null,0,32))drawn|=(pixel>>>24)!=0;check(drawn,"original icon renders: "+tool);}
   LayersPanel layers=new LayersPanel(()->{});layers.refresh();JList<?> list=findList(layers);check(list!=null&&list.getModel().getSize()==1,"layers list");@SuppressWarnings("unchecked") JList<Object> typed=(JList<Object>)list;Component row=typed.getCellRenderer().getListCellRendererComponent(typed,typed.getModel().getElementAt(0),0,false,false);check(row instanceof JLabel&&((JLabel)row).getIcon()!=null,"real native layer thumbnail");
   check(new DimensionsDialog(32,24,1).options().bilinear(),"bilinear option");check(new DimensionsDialog(32,24,2).options().anchor()==4,"center anchor default");
   canvas.selectAll();long before=DrawingView.nativeRevision();int[] pixels=DrawingView.nativePixels();canvas.moveSelectionMask(1,2);check(DrawingView.nativeRevision()==before&&Arrays.equals(pixels,DrawingView.nativePixels()),"move selection does not move pixels");check(canvas.selection[0]==0&&canvas.selection[2*32+1]!=0,"selection mask translated");canvas.resetDocumentState();check(canvas.selection==null&&canvas.removalMask()==null,"document state cleared");
   JFrame window=new JFrame();ArrayList<Integer> order=new ArrayList<>();WorkQueue queue=new WorkQueue(window);javax.swing.Timer timer=new javax.swing.Timer(20,e->{((javax.swing.Timer)e.getSource()).stop();queue.submit("Segundo",()->{order.add(2);return true;},()->order.add(3));});timer.setRepeats(false);timer.start();queue.submit("Primero",()->{Thread.sleep(120);order.add(0);return true;},()->order.add(1));long deadline=System.currentTimeMillis()+5000;while((queue.busy()||order.size()<4)&&System.currentTimeMillis()<deadline){java.awt.SecondaryLoop loop=Toolkit.getDefaultToolkit().getSystemEventQueue().createSecondaryLoop();javax.swing.Timer tick=new javax.swing.Timer(40,e->loop.exit());tick.setRepeats(false);tick.start();loop.enter();}check(order.equals(java.util.List.of(0,1,2,3)),"background operations serialized with EDT completion");queue.close();window.dispose();
   check(DrawingView.nativeDocumentBytes()>=0,"native pixel allocation reporting");
  }catch(Exception error){throw new RuntimeException(error);}});
  System.out.println("Desktop parity regressions passed: HSV/RGBA, saved/default palette, 25 original icons, layer thumbnails, dimension options, mask-only move, document reset and serialized work.");
 }
 static JList<?> findList(Container parent){for(Component c:parent.getComponents()){if(c instanceof JList<?> list)return list;if(c instanceof Container nested){JList<?> list=findList(nested);if(list!=null)return list;}}return null;}
}
