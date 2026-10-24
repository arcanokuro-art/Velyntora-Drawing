package app.velyntora.windows.app;
import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.metal.*;
import java.awt.*;
import java.io.*;
import java.util.*;
/** Bundled physical font and application-owned flat controls, including under Wine. */
public final class EditorTheme {
 public static final Color BACKGROUND=new Color(43,43,43), PANEL=new Color(32,32,32), TEXT=new Color(230,230,230), ACCENT=new Color(128,203,196);
 private static Font font;
 private EditorTheme(){}
 public static Font font(float size){if(font==null)throw new IllegalStateException("Install EditorTheme before creating UI");return font.deriveFont(size);}
 public static void install() throws Exception {
  if(font==null){try(InputStream input=EditorTheme.class.getResourceAsStream("/fonts/DejaVuSans-UI.ttf")){if(input==null)throw new IOException("Falta la fuente incluida del editor");font=Font.createFont(Font.TRUETYPE_FONT,input).deriveFont(13f);}GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(font);}
  MetalLookAndFeel.setCurrentTheme(new DefaultMetalTheme(){
   private final FontUIResource uiFont=new FontUIResource(font);
   public FontUIResource getControlTextFont(){return uiFont;}public FontUIResource getSystemTextFont(){return uiFont;}public FontUIResource getUserTextFont(){return uiFont;}public FontUIResource getMenuTextFont(){return uiFont;}public FontUIResource getWindowTitleFont(){return uiFont;}public FontUIResource getSubTextFont(){return uiFont;}
   protected ColorUIResource getPrimary1(){return new ColorUIResource(ACCENT);}protected ColorUIResource getPrimary2(){return new ColorUIResource(70,100,98);}protected ColorUIResource getPrimary3(){return new ColorUIResource(55,115,83);}
   protected ColorUIResource getSecondary1(){return new ColorUIResource(100,100,100);}protected ColorUIResource getSecondary2(){return new ColorUIResource(65,65,65);}protected ColorUIResource getSecondary3(){return new ColorUIResource(BACKGROUND);}
   public ColorUIResource getControlTextColor(){return new ColorUIResource(TEXT);}public ColorUIResource getSystemTextColor(){return new ColorUIResource(TEXT);}public ColorUIResource getUserTextColor(){return new ColorUIResource(TEXT);}public ColorUIResource getMenuForeground(){return new ColorUIResource(TEXT);}
  });
  UIManager.setLookAndFeel(new MetalLookAndFeel());
  UIDefaults defaults=UIManager.getLookAndFeelDefaults();for(Object key:new ArrayList<>(defaults.keySet())){Object value=defaults.get(key);if(value instanceof Font)UIManager.put(key,new FontUIResource(font));if(key.toString().endsWith(".gradient"))UIManager.put(key,null);}
  for(String key:new String[]{"Label","Panel","Button","ToggleButton","ToolBar","MenuBar","Menu","MenuItem","PopupMenu","CheckBox","RadioButton","Slider","Viewport","ScrollPane","ComboBox","Spinner","TextField","FormattedTextField","TextArea","TextPane","List","OptionPane","ToolTip","Tree","Table","TabbedPane"}){
   UIManager.put(key+".background",new ColorUIResource(BACKGROUND));UIManager.put(key+".foreground",new ColorUIResource(TEXT));UIManager.put(key+".font",new FontUIResource(font));
  }
  UIManager.put("defaultFont",new FontUIResource(font));UIManager.put("OptionPane.messageFont",new FontUIResource(font));UIManager.put("OptionPane.buttonFont",new FontUIResource(font));UIManager.put("ButtonUI","javax.swing.plaf.basic.BasicButtonUI");UIManager.put("ToggleButtonUI","javax.swing.plaf.basic.BasicToggleButtonUI");UIManager.put("SliderUI",EditorSliderUI.class.getName());
  UIManager.put("List.background",new ColorUIResource(PANEL));UIManager.put("List.selectionBackground",new ColorUIResource(55,115,83));UIManager.put("List.selectionForeground",new ColorUIResource(TEXT));UIManager.put("ToggleButton.select",new ColorUIResource(76,76,76));UIManager.put("Button.select",new ColorUIResource(65,65,65));
  UIManager.put("TextField.caretForeground",TEXT);UIManager.put("FormattedTextField.caretForeground",TEXT);UIManager.put("TextField.inactiveForeground",TEXT);UIManager.put("ScrollBar.width",10);
  UIManager.put("ScrollPane.border",BorderFactory.createEmptyBorder());UIManager.put("ToolBar.border",BorderFactory.createEmptyBorder(4,8,4,8));
 }
 public static void flat(AbstractButton b){b.setFocusPainted(false);b.setBorder(BorderFactory.createEmptyBorder(6,8,6,8));b.setContentAreaFilled(false);b.setForeground(TEXT);b.setBackground(BACKGROUND);}
}
