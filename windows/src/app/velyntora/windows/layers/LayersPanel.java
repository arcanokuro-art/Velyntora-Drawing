package app.velyntora.windows.layers;
import art.velyntora.core.DrawingView;
import javax.swing.*;
import java.awt.*;
public final class LayersPanel extends JPanel {
 private final DefaultListModel<String> model=new DefaultListModel<>();private final JList<String> list=new JList<>(model);private boolean syncing;private final Runnable changed;
 public LayersPanel(Runnable changed){super(new BorderLayout());this.changed=changed;setPreferredSize(new Dimension(210,400));setBorder(BorderFactory.createTitledBorder("Capas"));add(new JScrollPane(list),BorderLayout.CENTER);
  list.addListSelectionListener(e->{if(!syncing&&!e.getValueIsAdjusting()&&list.getSelectedIndex()>=0){DrawingView.nativeSelectLayer(model.size()-1-list.getSelectedIndex());changed.run();}});
  JPanel buttons=new JPanel(new GridLayout(2,3));button(buttons,"+",()->DrawingView.nativeAddLayer());button(buttons,"−",()->DrawingView.nativeDeleteLayer());button(buttons,"Visible",()->DrawingView.nativeToggleLayer());button(buttons,"↑",()->DrawingView.nativeMoveLayer(1));button(buttons,"↓",()->DrawingView.nativeMoveLayer(-1));button(buttons,"Duplicar",()->DrawingView.nativeLayerAction(0));
  JPanel bottom=new JPanel(new BorderLayout());bottom.add(buttons,BorderLayout.NORTH);JButton props=new JButton("Propiedades / mezcla");props.addActionListener(e->properties());bottom.add(props,BorderLayout.SOUTH);add(bottom,BorderLayout.SOUTH);
 }
 private void button(JPanel panel,String title,Runnable action){JButton b=new JButton(title);b.addActionListener(e->{action.run();changed.run();refresh();});panel.add(b);}
 public void refresh(){syncing=true;model.clear();int count=DrawingView.nativeLayerCount();for(int i=count-1;i>=0;i--)model.addElement((DrawingView.nativeLayerVisible(i)?"● ":"○ ")+DrawingView.nativeLayerName(i));list.setSelectedIndex(count-1-DrawingView.nativeActiveLayer());syncing=false;}
 private void properties(){int i=DrawingView.nativeActiveLayer();JTextField name=new JTextField(DrawingView.nativeLayerName(i));JSlider opacity=new JSlider(0,100,Math.round(DrawingView.nativeIndexedLayerOpacity(i)*100));String[] modes={"Normal","Multiplicar","Pantalla","Superponer","Oscurecer","Aclarar","Diferencia","Exclusión","Luz fuerte","Luz suave","Sobreexponer color","Subexponer color","Tono","Saturación","Color","Luminosidad","XOR"};JComboBox<String> blend=new JComboBox<>(modes);blend.setSelectedIndex(Math.min(modes.length-1,DrawingView.nativeLayerBlendMode(i)));JPanel form=new JPanel(new GridLayout(0,1));form.add(name);form.add(new JLabel("Opacidad"));form.add(opacity);form.add(blend);if(JOptionPane.showConfirmDialog(this,form,"Propiedades",JOptionPane.OK_CANCEL_OPTION)==JOptionPane.OK_OPTION){DrawingView.nativeLayerProperties(i,name.getText(),DrawingView.nativeLayerVisible(i),opacity.getValue()/100f,blend.getSelectedIndex());changed.run();refresh();}}
}
