package app.velyntora.windows.color;
import app.velyntora.windows.canvas.CanvasPanel;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.prefs.Preferences;
/** Same default swatches and saved-color actions as Android. Right click = long press. */
public final class PalettePanel extends JPanel {
 public static final int[] DEFAULTS={0xff000000,0xffffffff,0xff808080,0xffc0c0c0,0xffff0000,0xffff8080,0xffff9800,0xffffcc80,0xffffff00,0xffffff80,0xff00c000,0xff80ff80,0xff00ffff,0xff80ffff,0xff0000ff,0xff8080ff,0xffff00ff,0xffff80ff,0xff795548,0xffbcaaa4,0xff9c27b0,0xffce93d8};
 private final CanvasPanel canvas;private final Runnable changed;private final Preferences prefs;private final LinkedHashSet<Integer> saved=new LinkedHashSet<>();
 public PalettePanel(CanvasPanel canvas,Runnable changed){this(canvas,changed,Preferences.userRoot().node("app/velyntora/drawing/swatches"));}
 public PalettePanel(CanvasPanel canvas,Runnable changed,Preferences prefs){super(new FlowLayout(FlowLayout.LEFT,3,3));this.canvas=canvas;this.changed=changed;this.prefs=prefs;for(String hex:prefs.get("colors","").split(","))try{if(!hex.isBlank())saved.add((int)Long.parseLong(hex,16));}catch(NumberFormatException ignored){}render();}
 public boolean addColor(int color){if(saved.contains(color))return false;if(saved.size()>=64)throw new IllegalStateException("La paleta admite hasta 64 colores guardados");saved.add(color);persist();return true;}
 public void removeColor(int color){saved.remove(color);persist();}public java.util.List<Integer> savedColors(){return java.util.List.copyOf(saved);}
 private void persist(){prefs.put("colors",String.join(",",saved.stream().map(Integer::toHexString).toList()));render();}
 private void render(){removeAll();JButton save=new JButton("+");save.setToolTipText("Guardar color principal; clic derecho: guardar secundario");save.addActionListener(e->save(canvas.color));JPopupMenu saveMenu=new JPopupMenu();action(saveMenu,"Guardar color principal",()->save(canvas.color));action(saveMenu,"Guardar color secundario",()->save(canvas.secondary));save.setComponentPopupMenu(saveMenu);add(save);JPanel defaults=new JPanel(new GridLayout(2,0,1,1));for(int row=0;row<2;row++)for(int i=row;i<DEFAULTS.length;i+=2)defaults.add(swatch(DEFAULTS[i],false));add(defaults);if(!saved.isEmpty()){add(new JLabel("Guardados:"));JPanel custom=new JPanel(new GridLayout(2,0,1,1));Integer[] entries=saved.toArray(Integer[]::new);for(int row=0;row<2;row++)for(int i=row;i<entries.length;i+=2)custom.add(swatch(entries[i],true));add(custom);}revalidate();repaint();}
 private void save(int color){try{addColor(color);}catch(IllegalStateException error){JOptionPane.showMessageDialog(this,error.getMessage());}}
 private JButton swatch(int color,boolean stored){JButton b=new JButton(new ColorPicker.Swatch(color,26));b.setName((stored?"Guardado ":"Color ")+ColorPicker.toHex(color));b.setToolTipText(b.getName()+" · clic derecho: opciones");b.setBorder(BorderFactory.createEmptyBorder());b.setPreferredSize(new Dimension(26,26));b.setMargin(new Insets(0,0,0,0));b.addActionListener(e->{canvas.color=color;changed.run();});JPopupMenu menu=new JPopupMenu();action(menu,"Usar como color principal",()->{canvas.color=color;changed.run();});action(menu,"Usar como color secundario",()->{canvas.secondary=color;changed.run();});if(stored)action(menu,"Eliminar color guardado",()->removeColor(color));b.setComponentPopupMenu(menu);return b;}
 private static void action(JPopupMenu menu,String name,Runnable run){JMenuItem item=new JMenuItem(name);item.addActionListener(e->run.run());menu.add(item);}
}
