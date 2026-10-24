import app.velyntora.windows.app.*;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
public final class ThemeSmoke {
 public static void run() throws Exception {
  SwingUtilities.invokeAndWait(()->{
   String text="Archivo Editar Capas Fondo Opacidad Anchura Dureza Propiedades / mezcla Configurar pincel Línea/Curva Menú Sin título · Sin guardar áéíóúñü ¿Qué quieres crear?";
   for(String key:new String[]{"Label.font","Button.font","Menu.font","MenuItem.font","TextField.font","FormattedTextField.font","List.font","OptionPane.messageFont","ToolTip.font"}){
    Font font=UIManager.getFont(key);if(font==null||font.canDisplayUpTo(text)!=-1)throw new AssertionError("Missing Spanish UI glyphs: "+key);if(!font.getFamily().equals("DejaVu Sans"))throw new AssertionError("Unbundled UI font: "+key);
   }
   if(UIManager.getColor("Label.foreground").getRed()<200)throw new AssertionError("Dark label text on dark UI");
   if(UIManager.get("Button.gradient")!=null||UIManager.get("ToggleButton.gradient")!=null)throw new AssertionError("Native bright gradients");
   for(String name:new String[]{"menu","new","open","save","undo","redo","layers","confirm","cancel","add","delete","visible","up","down","duplicate"}){BufferedImage icon=new BufferedImage(24,24,2);Graphics2D g=icon.createGraphics();JLabel label=new JLabel();label.setForeground(EditorTheme.TEXT);new CommandIcon(name).paintIcon(label,g,0,0);g.dispose();int pixels=0;for(int pixel:icon.getRGB(0,0,24,24,null,0,24))if((pixel>>>24)!=0)pixels++;if(pixels<10)throw new AssertionError("Empty vector command: "+name);}
   JSlider slider=new JSlider();if(!(slider.getUI() instanceof EditorSliderUI))throw new AssertionError("Native slider UI");
  });
 }
}
