package app.velyntora.windows.app;
import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import java.awt.*;
/** Application-owned palette, independent of the operating system theme. */
public final class EditorTheme {
 public static final Color BACKGROUND=new Color(43,43,43), PANEL=new Color(32,32,32), TEXT=new Color(230,230,230), ACCENT=new Color(128,203,196);
 private EditorTheme(){}
 public static void install() throws Exception {
  UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
  for(String key:new String[]{"Panel","Button","ToggleButton","ToolBar","MenuBar","Menu","MenuItem","PopupMenu","CheckBox","Slider","Viewport","ScrollPane","ComboBox","Spinner","TextField","List","OptionPane"}){
   UIManager.put(key+".background",new ColorUIResource(BACKGROUND));UIManager.put(key+".foreground",new ColorUIResource(TEXT));UIManager.put(key+".font",new javax.swing.plaf.FontUIResource(Font.SANS_SERIF,Font.PLAIN,13));
  }
  UIManager.put("List.background",new ColorUIResource(PANEL));UIManager.put("List.selectionBackground",new ColorUIResource(55,115,83));UIManager.put("List.selectionForeground",new ColorUIResource(TEXT));
  UIManager.put("ToggleButton.select",new ColorUIResource(76,76,76));UIManager.put("Slider.foreground",new ColorUIResource(ACCENT));UIManager.put("ScrollBar.width",10);
  UIManager.put("ScrollPane.border",BorderFactory.createEmptyBorder());UIManager.put("ToolBar.border",BorderFactory.createEmptyBorder(4,8,4,8));
 }
 public static void flat(AbstractButton b){b.setFocusPainted(false);b.setBorder(BorderFactory.createEmptyBorder(8,10,8,10));b.setContentAreaFilled(false);b.setForeground(TEXT);}
}
