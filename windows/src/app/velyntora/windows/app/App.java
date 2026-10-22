package app.velyntora.windows.app;
import javax.swing.*;
import java.nio.file.*;
import art.velyntora.core.DrawingView;
public final class App {
 public static void main(String[] args){SwingUtilities.invokeLater(()->{try{
  UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
  Path cache=Paths.get(System.getProperty("java.io.tmpdir"),"velyntora-drawing");Files.createDirectories(cache);DrawingView.nativeHistoryDirectory(cache.toString());
  new HomeWindow().setVisible(true);
 }catch(Throwable error){error.printStackTrace();JOptionPane.showMessageDialog(null,error.toString(),"Velyntora Drawing",JOptionPane.ERROR_MESSAGE);}});}
}
