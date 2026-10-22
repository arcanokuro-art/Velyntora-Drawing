package app.velyntora.windows.app;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import app.velyntora.windows.document.DocumentIO;
public final class HomeWindow extends JFrame {
 public HomeWindow()throws Exception{
  super("Velyntora Drawing · Windows");setDefaultCloseOperation(EXIT_ON_CLOSE);setSize(1200,780);setMinimumSize(new Dimension(850,580));setLocationRelativeTo(null);
  BufferedImage art=ImageIO.read(getClass().getResource("/start_drawing.png"));
  JPanel hero=new JPanel(new GridBagLayout()){@Override protected void paintComponent(Graphics gr){super.paintComponent(gr);Graphics2D g=(Graphics2D)gr.create();double scale=Math.max(getWidth()/(double)art.getWidth(),getHeight()/(double)art.getHeight());int w=(int)(art.getWidth()*scale),h=(int)(art.getHeight()*scale);g.drawImage(art,(getWidth()-w)/2,(getHeight()-h)/2,w,h,null);g.setColor(new Color(0,0,0,95));g.fillRect(0,0,getWidth(),getHeight());g.setColor(Color.WHITE);g.drawString("Powered By Zaskar Startup",25,getHeight()-25);g.dispose();}};
  JPanel actions=new JPanel(new GridLayout(0,1,0,14));actions.setOpaque(false);JLabel title=new JLabel("DIBUJO",SwingConstants.CENTER);title.setForeground(Color.WHITE);title.setFont(title.getFont().deriveFont(Font.BOLD,30));actions.add(title);
  JButton create=new JButton("Crear ilustración");create.addActionListener(e->openEditor(false));actions.add(create);JButton open=new JButton("Abrir imagen / proyecto");open.addActionListener(e->openEditor(true));actions.add(open);JButton recover=new JButton("Recuperar documento");recover.setVisible(java.nio.file.Files.exists(app.velyntora.windows.document.DocumentRecovery.file()));recover.addActionListener(e->openEditorPath(app.velyntora.windows.document.DocumentRecovery.file().toString()));actions.add(recover);hero.add(actions);
  JPanel right=new JPanel();right.setLayout(new BoxLayout(right,BoxLayout.Y_AXIS));right.setBackground(new Color(21,25,34));right.setBorder(BorderFactory.createEmptyBorder(40,30,30,30));
  JLabel logo=new JLabel(new ImageIcon(ImageIO.read(getClass().getResource("/logo.png")).getScaledInstance(360,120,Image.SCALE_SMOOTH)));logo.setAlignmentX(.5f);right.add(logo);
  JLabel brand=new JLabel("Drawing");brand.setForeground(Color.WHITE);brand.setAlignmentX(.5f);brand.setFont(brand.getFont().deriveFont(24f));right.add(brand);
  JLabel pilot=new JLabel("VERSIÓN PILOTO · Windows x64");pilot.setForeground(new Color(185,190,205));pilot.setAlignmentX(.5f);right.add(pilot);right.add(Box.createVerticalStrut(30));
  DefaultListModel<String> recent=new DefaultListModel<>();for(String path:DocumentIO.recent())recent.addElement(path);JList<String> list=new JList<>(recent);list.setBorder(BorderFactory.createTitledBorder("Proyectos recientes"));list.addListSelectionListener(e->{if(!e.getValueIsAdjusting()&&list.getSelectedValue()!=null)openEditorPath(list.getSelectedValue());});right.add(new JScrollPane(list));
  JSplitPane split=new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,hero,right);split.setResizeWeight(.36);split.setDividerLocation(420);setContentPane(split);
 }
 private void openEditor(boolean open){try{EditorWindow editor=new EditorWindow(this);if(open){if(!editor.open()){editor.dispose();return;}}else if(!editor.newDocument()){editor.dispose();return;}setVisible(false);editor.setVisible(true);}catch(Exception error){JOptionPane.showMessageDialog(this,error.toString());}}
 private void openEditorPath(String path){try{EditorWindow editor=new EditorWindow(this);editor.openPath(java.nio.file.Path.of(path));setVisible(false);editor.setVisible(true);}catch(Exception error){JOptionPane.showMessageDialog(this,error.toString());}}
}
