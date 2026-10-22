package app.velyntora.windows.brushes;
import art.velyntora.core.BrushPresets;
import app.velyntora.windows.canvas.CanvasPanel;
import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
public final class BrushSelector {
 public static JComboBox<Object> create(CanvasPanel canvas){JComboBox<Object> box=new JComboBox<>();box.addItem("Clásico");box.addItem("Bordes suavizados");for(var preset:BrushPresets.ALL)box.addItem(preset);HashMap<Integer,ImageIcon> icons=new HashMap<>();box.setRenderer(new DefaultListCellRenderer(){public Component getListCellRendererComponent(JList<?> list,Object value,int index,boolean selected,boolean focused){JLabel row=(JLabel)super.getListCellRendererComponent(list,value,index,selected,focused);if(value instanceof BrushPresets.Preset preset){ImageIcon icon=icons.get(preset.id());if(icon==null){var resource=BrushSelector.class.getResource("/brushes/"+preset.id()+"/preview.png");if(resource!=null){icon=new ImageIcon(new ImageIcon(resource).getImage().getScaledInstance(48,32,Image.SCALE_SMOOTH));icons.put(preset.id(),icon);}}row.setIcon(icon);}return row;}});box.setMaximumSize(new Dimension(320,40));box.setMaximumRowCount(15);box.addActionListener(e->{Object selected=box.getSelectedItem();canvas.preset=selected instanceof BrushPresets.Preset?(BrushPresets.Preset)selected:null;canvas.antialiased="Bordes suavizados".equals(selected);});return box;}
}
