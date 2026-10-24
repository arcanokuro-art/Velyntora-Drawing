package app.velyntora.windows.app.home;
import javax.swing.*;
import java.awt.*;
import java.util.prefs.Preferences;
public final class HomePreferences {
 private static final Preferences prefs=Preferences.userRoot().node("app/velyntora/drawing/preferences");
 private HomePreferences(){}public static boolean maximized(){return prefs.getBoolean("maximized",false);}public static boolean rememberRecent(){return prefs.getBoolean("rememberRecent",true);}
 public static void show(Component parent){JCheckBox maximized=new JCheckBox("Abrir el inicio maximizado",maximized()),recent=new JCheckBox("Guardar proyectos recientes",rememberRecent());JPanel form=new JPanel(new GridLayout(0,1,0,12));form.add(maximized);form.add(recent);if(JOptionPane.showConfirmDialog(parent,form,"Preferencias de Velyntora",JOptionPane.OK_CANCEL_OPTION)==JOptionPane.OK_OPTION){prefs.putBoolean("maximized",maximized.isSelected());prefs.putBoolean("rememberRecent",recent.isSelected());}}
}
