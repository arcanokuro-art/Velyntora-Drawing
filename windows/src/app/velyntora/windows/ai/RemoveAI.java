package app.velyntora.windows.ai;
import art.velyntora.core.*;
import javax.swing.*;
import java.awt.*;
import java.util.concurrent.atomic.AtomicBoolean;
public final class RemoveAI {
 public static void apply(JFrame owner,byte[] mask,Runnable changed){
  int w=DrawingView.nativeWidth(),h=DrawingView.nativeHeight(),layer=DrawingView.nativeActiveLayer();long revision=DrawingView.nativeRevision();if(mask==null||mask.length!=w*h){JOptionPane.showMessageDialog(owner,"Pinta la máscara Remove AI o selecciona la zona que quieres eliminar.");return;}
  int[] source=DrawingView.nativeRemoveSource();AtomicBoolean cancelled=new AtomicBoolean();JDialog progress=new JDialog(owner,"Remove AI · MI-GAN 512",false);JButton cancel=new JButton("Cancelar");cancel.addActionListener(e->{cancelled.set(true);progress.dispose();owner.setEnabled(true);owner.toFront();});progress.add(new JLabel("Reconstruyendo la zona sin conexión…"),BorderLayout.CENTER);progress.add(cancel,BorderLayout.SOUTH);progress.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);progress.addWindowListener(new java.awt.event.WindowAdapter(){public void windowClosing(java.awt.event.WindowEvent event){cancel.doClick();}});progress.setSize(390,130);progress.setLocationRelativeTo(owner);owner.setEnabled(false);progress.setVisible(true);
  new SwingWorker<int[],Void>(){protected int[] doInBackground()throws Exception{try(OfflineRemove backend=new OfflineRemove()){return backend.run(source,mask,w,h);}}protected void done(){progress.dispose();owner.setEnabled(true);if(cancelled.get())return;try{if(!DrawingView.nativeRemoveCommit(get(),mask,w,h,layer,revision))throw new IllegalStateException("El documento cambió durante la operación; no se aplicó el resultado.");changed.run();}catch(Exception error){JOptionPane.showMessageDialog(owner,error.toString(),"Remove AI",JOptionPane.ERROR_MESSAGE);}}}.execute();
 }
}
