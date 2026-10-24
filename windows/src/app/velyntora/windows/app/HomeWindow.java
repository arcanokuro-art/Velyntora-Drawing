package app.velyntora.windows.app;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import app.velyntora.windows.document.DocumentIO;
public final class HomeWindow extends JFrame {
 private final app.velyntora.windows.app.home.HomeScreen screen;
 public HomeWindow()throws Exception{
  super("Velyntora Drawing");setDefaultCloseOperation(EXIT_ON_CLOSE);setSize(1366,850);setMinimumSize(new Dimension(850,580));setLocationRelativeTo(null);
  screen=new app.velyntora.windows.app.home.HomeScreen(ImageIO.read(getClass().getResource("/start_drawing.png")),ImageIO.read(getClass().getResource("/logo.png")),()->openEditor(false),()->openEditor(true),this::openEditorPath,()->app.velyntora.windows.app.home.HomePreferences.show(this),this::help,()->openEditorPath(app.velyntora.windows.document.DocumentRecovery.file().toString()));setContentPane(screen);screen.setRecoveryAvailable(java.nio.file.Files.exists(app.velyntora.windows.document.DocumentRecovery.file()));if(app.velyntora.windows.app.home.HomePreferences.maximized())setExtendedState(MAXIMIZED_BOTH);
  getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("F11"),"fullscreen");getRootPane().getActionMap().put("fullscreen",new AbstractAction(){public void actionPerformed(java.awt.event.ActionEvent e){GraphicsDevice device=getGraphicsConfiguration().getDevice();device.setFullScreenWindow(device.getFullScreenWindow()==HomeWindow.this?null:HomeWindow.this);}});
 }
 private void help(){JOptionPane.showMessageDialog(this,"Velyntora Drawing\n\nCrear ilustración: define las dimensiones y abre el editor.\nAbrir imagen: importa una imagen o abre un proyecto VLYCORE/ORA.\nProyectos recientes: doble clic o Enter para abrir. Ver más muestra la lista completa.\nF11: pantalla completa.\n\nEditor: Ctrl+N nuevo · Ctrl+O abrir · Ctrl+S guardar\nCtrl+Z deshacer · Ctrl+Y rehacer\nRueda: zoom · Alt+rueda: girar · Botón central: desplazar\n\nVersión piloto · Prototipo en desarrollo\nPowered By Zaskar Startup","Ayuda de Velyntora",JOptionPane.INFORMATION_MESSAGE);}
 @Override public void setVisible(boolean visible){if(visible&&screen!=null){screen.refreshRecent();screen.setRecoveryAvailable(java.nio.file.Files.exists(app.velyntora.windows.document.DocumentRecovery.file()));}super.setVisible(visible);}
 private void openEditor(boolean open){try{if(!open&&art.velyntora.core.DrawingView.nativeWidth()==0&&java.nio.file.Files.exists(app.velyntora.windows.document.DocumentRecovery.file())){if(JOptionPane.showConfirmDialog(this,"Hay un documento recuperable. ¿Descartarlo y crear uno nuevo?","Documento recuperable",JOptionPane.YES_NO_OPTION)!=JOptionPane.YES_OPTION)return;java.nio.file.Files.deleteIfExists(app.velyntora.windows.document.DocumentRecovery.file());}EditorWindow editor=new EditorWindow(this);if(open){if(!editor.open()){editor.dispose();return;}}else if(!editor.newDocument()){editor.dispose();return;}setVisible(false);editor.setVisible(true);}catch(Exception error){JOptionPane.showMessageDialog(this,error.toString());}}
 private void openEditorPath(String path){try{EditorWindow editor=new EditorWindow(this);editor.openPath(java.nio.file.Path.of(path));setVisible(false);editor.setVisible(true);}catch(Exception error){JOptionPane.showMessageDialog(this,error.toString());}}
}
