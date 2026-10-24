import app.velyntora.windows.app.home.HomeScreen;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.file.*;
import javax.imageio.ImageIO;
public final class HomeSmoke {
 private static void check(boolean condition,String message){if(!condition)throw new AssertionError(message);}
 static void layout(Container parent){parent.doLayout();for(Component child:parent.getComponents())if(child instanceof Container container)layout(container);}
 public static void run()throws Exception{SwingUtilities.invokeAndWait(()->{try{
  int[] clicks={0};HomeScreen home=new HomeScreen(ImageIO.read(HomeSmoke.class.getResource("/start_drawing.png")),ImageIO.read(HomeSmoke.class.getResource("/logo.png")),()->clicks[0]++,()->clicks[0]++,path->clicks[0]++,()->clicks[0]++,()->clicks[0]++,()->clicks[0]++);home.setRecentProjects(java.util.List.of());
  for(int[] dimensions:new int[][]{{850,580},{1366,768},{1536,960}}){home.setSize(dimensions[0],dimensions[1]);layout(home);JButton create=ParitySmoke.named(home,"Crear ilustración",JButton.class),open=ParitySmoke.named(home,"Abrir imagen",JButton.class);check(create!=null&&open!=null,"start actions");check(create.getWidth()>200&&create.getHeight()>30,"usable large start buttons");JButton preferences=ParitySmoke.named(home,"Preferencias de Velyntora",JButton.class),help=ParitySmoke.named(home,"? Ayuda",JButton.class);check(preferences.getX()>=0&&help.getX()+help.getWidth()<=help.getParent().getWidth(),"footer fits viewport");check(ParitySmoke.named(home,"Tus proyectos recientes aparecerán aquí.",JLabel.class).isVisible(),"empty recent state");check(Math.abs(create.getParent().getWidth()/(double)home.getWidth()-.36)<.002,"reference image proportion");String capture=System.getProperty("velyntora.uiCapture");if(capture!=null&&dimensions[0]>=1366){BufferedImage image=new BufferedImage(home.getWidth(),home.getHeight(),BufferedImage.TYPE_INT_RGB);Graphics2D graphics=image.createGraphics();home.paint(graphics);graphics.dispose();String path=capture.replace("editor-preview","home-preview");if(dimensions[0]==1366)path=path.replace(".png","-1366.png");ImageIO.write(image,"png",Path.of(path).toFile());}
  }
  for(String name:new String[]{"Crear ilustración","Abrir imagen","Preferencias de Velyntora","? Ayuda"})ParitySmoke.named(home,name,JButton.class).doClick();check(clicks[0]==4,"start actions remain connected");home.setRecentProjects(java.util.List.of("/tmp/Ilustración.vlycore"));check(!ParitySmoke.named(home,"Tus proyectos recientes aparecerán aquí.",JLabel.class).isVisible(),"recent entries replace empty state");
 }catch(Exception error){throw new RuntimeException(error);}});}
}
