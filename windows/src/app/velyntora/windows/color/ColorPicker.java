package app.velyntora.windows.color;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Locale;

/** Desktop HSV/RGBA picker; edits stay local until Apply. */
public final class ColorPicker extends JPanel {
 private final int[] colors;private int selected;private boolean syncing;
 private final JSpinner[] channels=new JSpinner[7];private final JSlider[] sliders=new JSlider[7];
 private final JTextField hex=new JTextField(8);private final Plane plane=new Plane();
 private final JButton[] swatches={new JButton("Principal"),new JButton("Secundario")};
 private final JComboBox<String> layout=new JComboBox<>(new String[]{"Cuadrado","Rueda"});
 private static final int[] MAX={360,100,100,255,255,255,255};
 public ColorPicker(int primary,int secondary,boolean second){super(new BorderLayout(8,8));colors=new int[]{primary,secondary};selected=second?1:0;
  JPanel top=new JPanel();for(int i=0;i<2;i++){final int n=i;swatches[i].addActionListener(e->{selected=n;sync();});top.add(swatches[i]);}top.add(layout);layout.addActionListener(e->plane.repaint());add(top,BorderLayout.NORTH);
  plane.setPreferredSize(new Dimension(250,250));add(plane,BorderLayout.CENTER);JPanel fields=new JPanel(new GridLayout(0,3,4,4));String[] labels={"Tono","Saturación","Valor","Rojo","Verde","Azul","Alfa"};
  for(int i=0;i<7;i++){final int n=i;fields.add(new JLabel(labels[i]));sliders[i]=new JSlider(0,MAX[i]);channels[i]=new JSpinner(new SpinnerNumberModel(0,0,MAX[i],1));channels[i].setName(labels[i]);fields.add(sliders[i]);fields.add(channels[i]);sliders[i].addChangeListener(e->{if(!syncing)change(n,sliders[n].getValue());});channels[i].addChangeListener(e->{if(!syncing)change(n,(int)channels[n].getValue());});}
  fields.add(new JLabel("Hex RGBA"));hex.setName("Hex RGBA");fields.add(hex);fields.add(new JLabel("RRGGBBAA"));hex.getDocument().addDocumentListener(new DocumentListener(){public void insertUpdate(DocumentEvent e){apply();}public void removeUpdate(DocumentEvent e){apply();}public void changedUpdate(DocumentEvent e){apply();}private void apply(){if(!syncing&&hex.getText().matches("[0-9a-fA-F]{8}")){colors[selected]=fromHex(hex.getText());sync(false);}}});add(fields,BorderLayout.SOUTH);sync();
 }
 public static int fromHex(String rgba){if(!rgba.matches("[0-9a-fA-F]{8}"))throw new IllegalArgumentException("Usa RRGGBBAA");long value=Long.parseLong(rgba,16);return (int)((value>>>8)|((value&255)<<24));}
 public static String toHex(int argb){return String.format(Locale.ROOT,"%08X",((long)(argb&0xffffff)<<8)|(argb>>>24));}
 public int[] colors(){return colors.clone();}
 public static int[] show(Component owner,int primary,int secondary,boolean second){ColorPicker picker=new ColorPicker(primary,secondary,second);return JOptionPane.showConfirmDialog(owner,picker,"Elegir color",JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE)==JOptionPane.OK_OPTION?picker.colors():null;}
 private float[] hsv(){int c=colors[selected];return Color.RGBtoHSB(c>>16&255,c>>8&255,c&255,null);}
 private void change(int n,int value){int c=colors[selected];if(n<3){float[] h=hsv();h[n]=value/(n==0?360f:100f);colors[selected]=(c&0xff000000)|(Color.HSBtoRGB(h[0],h[1],h[2])&0xffffff);}else{int shift=n==3?16:n==4?8:n==5?0:24;colors[selected]=(c&~(255<<shift))|(value<<shift);}sync();}
 private void sync(){sync(true);}private void sync(boolean updateHex){syncing=true;try{int c=colors[selected];float[] h=hsv();int[] values={Math.round(h[0]*360),Math.round(h[1]*100),Math.round(h[2]*100),c>>16&255,c>>8&255,c&255,c>>>24};for(int i=0;i<7;i++){channels[i].setValue(values[i]);sliders[i].setValue(values[i]);}if(updateHex)hex.setText(toHex(c));for(int i=0;i<2;i++){swatches[i].setIcon(new Swatch(colors[i],28));swatches[i].setBorder(BorderFactory.createLineBorder(i==selected?Color.CYAN:Color.GRAY,2));}}finally{syncing=false;}plane.repaint();}
 public static final class Swatch implements Icon {private final int argb,size;public Swatch(int argb,int size){this.argb=argb;this.size=size;}public int getIconWidth(){return size;}public int getIconHeight(){return size;}public void paintIcon(Component c,Graphics g,int x,int y){for(int yy=0;yy<size;yy+=6)for(int xx=0;xx<size;xx+=6){g.setColor(((xx/6+yy/6)&1)==0?Color.WHITE:Color.LIGHT_GRAY);g.fillRect(x+xx,y+yy,Math.min(6,size-xx),Math.min(6,size-yy));}g.setColor(new Color(argb,true));g.fillRect(x,y,size,size);g.setColor(Color.GRAY);g.drawRect(x,y,size-1,size-1);}}
 private final class Plane extends JComponent {Plane(){MouseAdapter input=new MouseAdapter(){public void mousePressed(MouseEvent e){pick(e);}public void mouseDragged(MouseEvent e){pick(e);}};addMouseListener(input);addMouseMotionListener(input);}
  private void pick(MouseEvent e){float[] h=hsv();if(layout.getSelectedIndex()==0){h[1]=Math.max(0,Math.min(1,e.getX()/(float)Math.max(1,getWidth()-1)));h[2]=1-Math.max(0,Math.min(1,e.getY()/(float)Math.max(1,getHeight()-1)));}else{double dx=e.getX()-getWidth()/2.,dy=e.getY()-getHeight()/2.;h[0]=(float)((Math.atan2(dy,dx)/(2*Math.PI)+1)%1);h[1]=(float)Math.min(1,Math.hypot(dx,dy)/(Math.min(getWidth(),getHeight())/2.));}colors[selected]=(colors[selected]&0xff000000)|(Color.HSBtoRGB(h[0],h[1],h[2])&0xffffff);sync();}
  protected void paintComponent(Graphics graphics){int w=getWidth(),h=getHeight();if(w<1||h<1)return;float[] hsv=hsv();java.awt.image.BufferedImage img=new java.awt.image.BufferedImage(w,h,2);double r=Math.min(w,h)/2.;for(int y=0;y<h;y++)for(int x=0;x<w;x++){if(layout.getSelectedIndex()==0)img.setRGB(x,y,Color.HSBtoRGB(hsv[0],x/(float)Math.max(1,w-1),1-y/(float)Math.max(1,h-1)));else{double dx=x-w/2.,dy=y-h/2.,s=Math.hypot(dx,dy)/r;if(s<=1)img.setRGB(x,y,Color.HSBtoRGB((float)((Math.atan2(dy,dx)/(2*Math.PI)+1)%1),(float)s,hsv[2]));}}graphics.drawImage(img,0,0,null);int x,y;if(layout.getSelectedIndex()==0){x=Math.round(hsv[1]*(w-1));y=Math.round((1-hsv[2])*(h-1));}else{x=(int)(w/2.+Math.cos(hsv[0]*2*Math.PI)*hsv[1]*r);y=(int)(h/2.+Math.sin(hsv[0]*2*Math.PI)*hsv[1]*r);}graphics.setColor(Color.BLACK);graphics.drawOval(x-5,y-5,10,10);graphics.setColor(Color.WHITE);graphics.drawOval(x-4,y-4,8,8);}
 }
}
