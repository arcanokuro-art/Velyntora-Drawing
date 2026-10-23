package app.velyntora.windows.brushes;
import art.velyntora.core.BrushPresets;
import app.velyntora.windows.canvas.CanvasPanel;
import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;
/** Same category -> preset menu as Android, with original preview images. */
public final class BrushSelector {
 public static JButton create(CanvasPanel canvas){JButton button=new JButton("Pinceles ▾");button.addActionListener(e->{JPopupMenu popup=new JPopupMenu();ButtonGroup selected=new ButtonGroup();JRadioButtonMenuItem classic=new JRadioButtonMenuItem("Pincel clásico",canvas.preset==null&&!canvas.antialiased),smooth=new JRadioButtonMenuItem("Pincel con bordes suavizados",canvas.preset==null&&canvas.antialiased);classic.addActionListener(event->{canvas.preset=null;canvas.antialiased=false;button.setText("Clásico ▾");});smooth.addActionListener(event->{canvas.preset=null;canvas.antialiased=true;button.setText("Bordes suavizados ▾");});selected.add(classic);selected.add(smooth);popup.add(classic);popup.add(smooth);LinkedHashMap<String,JMenu> categories=new LinkedHashMap<>();for(var preset:BrushPresets.ALL){String[] parts=preset.label().split(" · ",2);String category=parts[0],label=parts.length>1?parts[1]:preset.toString();JMenu group=categories.computeIfAbsent(category,key->{JMenu menu=new JMenu(key.replace('_',' '));popup.add(menu);return menu;});JRadioButtonMenuItem item=new JRadioButtonMenuItem(label.replace('_',' '),canvas.preset==preset);var resource=BrushSelector.class.getResource("/brushes/"+preset.id()+"/preview.png");if(resource!=null)item.setIcon(new ImageIcon(new ImageIcon(resource).getImage().getScaledInstance(48,32,Image.SCALE_SMOOTH)));item.addActionListener(event->{canvas.preset=preset;canvas.antialiased=false;button.setText(label.replace('_',' ')+" ▾");});selected.add(item);group.add(item);}popup.show(button,0,button.getHeight());});return button;}
}
