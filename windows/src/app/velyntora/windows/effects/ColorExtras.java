package app.velyntora.windows.effects;
import art.velyntora.core.DrawingView;
import javax.swing.*;
import java.awt.*;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
public final class ColorExtras {
 private record Entry(String title,String method,String[] labels,int[] min,int[] max,int[] initial){}
 private static Entry one(String title,String method,int min,int max,int initial){return new Entry(title,method,new String[]{title},new int[]{min},new int[]{max},new int[]{initial});}
 public static JMenu menu(JFrame owner,Consumer<Callable<Boolean>> execute){JMenu menu=new JMenu("Extras de color");Entry[] entries={
 one("Brillo","nativeBrightnessActive",-100,100,0),one("Contraste","nativeContrastActive",-100,100,0),one("Umbral","nativeThresholdActive",0,255,128),one("Posterizar","nativePosterizeActive",2,256,4),one("Solarizar","nativeSolarizeActive",0,255,128),one("Saturación","nativeSaturationActive",-100,100,0),one("Gamma (%)","nativeGammaActive",10,300,100),
 new Entry("Desplazamiento RGB","nativeTintActive",new String[]{"Rojo","Verde","Azul"},new int[]{-255,-255,-255},new int[]{255,255,255},new int[]{0,0,0}),
 one("Intercambiar canales: 0 R/G, 1 R/B, 2 G/B","nativeSwapChannelsActive",0,2,0),
 new Entry("Balance RGB (%)","nativeColorBalanceActive",new String[]{"Rojo","Verde","Azul"},new int[]{0,0,0},new int[]{200,200,200},new int[]{100,100,100}),one("Rotación de tono","nativeHueRotateActive",-180,180,0),new Entry("Niveles","nativeLevelsActive",new String[]{"Negro","Blanco"},new int[]{0,1},new int[]{254,255},new int[]{0,255}),one("Exposición (%)","nativeExposureActive",0,200,100),one("Grises desde canal: 0 R, 1 G, 2 B","nativeDesaturateChannelActive",0,2,0),one("Alfa de píxeles (%)","nativeAdjustAlphaActive",0,200,100),one("Eliminar canal: 0 R, 1 G, 2 B","nativeRemoveChannelActive",0,2,0),one("Cuantizar colores","nativeQuantizeActive",1,255,16),one("Limitar luces","nativeClampHighlightsActive",0,255,224),one("Elevar sombras","nativeLiftShadowsActive",0,255,32),new Entry("Añadir a canal","nativeAdjustChannelActive",new String[]{"Canal 0 R, 1 G, 2 B","Cantidad"},new int[]{0,-255},new int[]{2,255},new int[]{0,20})};
 for(Entry entry:entries){JMenuItem item=new JMenuItem(entry.title());item.addActionListener(e->{JPanel panel=new JPanel(new GridLayout(0,2));JSpinner[] fields=new JSpinner[entry.labels().length];for(int i=0;i<fields.length;i++){panel.add(new JLabel(entry.labels()[i]));fields[i]=new JSpinner(new SpinnerNumberModel(entry.initial()[i],entry.min()[i],entry.max()[i],1));panel.add(fields[i]);}if(JOptionPane.showConfirmDialog(owner,panel,entry.title(),JOptionPane.OK_CANCEL_OPTION)==JOptionPane.OK_OPTION){Object[] params=new Object[fields.length];Class<?>[] types=new Class<?>[fields.length];for(int i=0;i<fields.length;i++){params[i]=fields[i].getValue();types[i]=int.class;}execute.accept(()->(Boolean)DrawingView.class.getMethod(entry.method(),types).invoke(null,params));}});menu.add(item);}JMenuItem normalize=new JMenuItem("Normalizar colores");normalize.addActionListener(e->execute.accept(()->DrawingView.nativeNormalizeActive()));menu.add(normalize);return menu;}
}
