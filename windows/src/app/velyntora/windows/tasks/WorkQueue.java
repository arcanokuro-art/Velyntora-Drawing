package app.velyntora.windows.tasks;
import javax.swing.*;
import java.util.ArrayDeque;
import java.util.concurrent.Callable;
/** EDT-owned serial queue. Modal progress blocks edits while allowing repaint and timers. */
public final class WorkQueue {
 private final JFrame owner;private final ArrayDeque<Job> pending=new ArrayDeque<>();private boolean running,closed;private java.util.function.Consumer<Boolean> activity=value->{};
 public void setActivityHandler(java.util.function.Consumer<Boolean> handler){activity=handler;}
 private record Job(String message,Callable<?> work,Runnable complete){}
 public WorkQueue(JFrame owner){this.owner=owner;}
 public boolean busy(){return running;}
 @SuppressWarnings("unchecked") public <T>T call(String message,Callable<T> action)throws Exception{if(running)throw new IllegalStateException("Espera a que termine la operación actual");Object[] result={null};Exception[] error={null};submit(message,()->{try{result[0]=action.call();}catch(Exception failure){error[0]=failure;}return true;},()->{});if(error[0]!=null)throw error[0];return (T)result[0];}
 public void submit(String message,Callable<?> work,Runnable complete){if(!SwingUtilities.isEventDispatchThread())throw new IllegalStateException("Queue must be used on EDT");if(closed)return;pending.add(new Job(message,work,complete));if(!running)next();}
 public void close(){closed=true;pending.clear();}
 private void next(){Job job=pending.poll();if(job==null||closed){running=false;return;}running=true;activity.accept(true);
  JDialog progress=new JDialog(owner,job.message(),true);progress.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);JProgressBar bar=new JProgressBar();bar.setIndeterminate(true);JPanel panel=new JPanel(new java.awt.BorderLayout(10,10));panel.setBorder(BorderFactory.createEmptyBorder(18,24,18,24));panel.add(new JLabel(job.message()),java.awt.BorderLayout.NORTH);panel.add(bar);progress.add(panel);progress.pack();progress.setLocationRelativeTo(owner);
  new SwingWorker<Object,Void>(){protected Object doInBackground()throws Exception{return job.work().call();}protected void done(){progress.dispose();activity.accept(false);try{get();if(!closed)job.complete().run();}catch(Exception error){if(!closed)JOptionPane.showMessageDialog(owner,error.getCause()==null?error.toString():error.getCause().toString(),"Velyntora Drawing",JOptionPane.ERROR_MESSAGE);}finally{running=false;if(!closed&&!pending.isEmpty())SwingUtilities.invokeLater(WorkQueue.this::next);}}}.execute();progress.setVisible(true);
 }
}
