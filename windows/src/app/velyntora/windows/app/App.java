package app.velyntora.windows.app;
import javax.swing.*;
import java.nio.file.*;
import art.velyntora.core.DrawingView;
public final class App {
 public static void main(String[] args){
 if(args.length==2&&args[0].equals("--self-test")){try{Class.forName("DesktopSmoke").getMethod("main",String[].class).invoke(null,(Object)new String[0]);Files.writeString(Path.of(args[1]),"ok");System.exit(0);}catch(Throwable error){try{Files.writeString(Path.of(args[1]),error.toString());}catch(Exception ignored){}System.exit(1);}return;}
 SwingUtilities.invokeLater(()->{try{
  EditorTheme.install();
  Path cache=Paths.get(System.getProperty("java.io.tmpdir"),"velyntora-drawing");Files.createDirectories(cache);DrawingView.nativeHistoryDirectory(cache.toString());DrawingView.nativeImageStorage(cache.toString());
  new HomeWindow().setVisible(true);
 }catch(Throwable error){error.printStackTrace();JOptionPane.showMessageDialog(null,error.toString(),"Velyntora Drawing",JOptionPane.ERROR_MESSAGE);}});}
}
