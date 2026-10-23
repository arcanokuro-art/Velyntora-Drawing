package app.velyntora.windows.document;
import art.velyntora.core.DrawingView;
import java.nio.file.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import javax.swing.Timer;
/** Revision-checked recovery written atomically after three idle seconds. */
public final class DocumentRecovery implements AutoCloseable {
 public static Path file(){return Path.of(System.getenv().getOrDefault("LOCALAPPDATA",System.getProperty("user.home")),"Velyntora","Drawing","recovery.vlycore");}
 private final ExecutorService worker=Executors.newSingleThreadExecutor(r->{Thread t=new Thread(r,"drawing-recovery");t.setDaemon(true);return t;});private final Timer timer;private final Object fileLock=new Object();private final AtomicInteger generation=new AtomicInteger();private long observed=-1,stored=-1,changedAt;private volatile boolean busy;
 private java.util.function.BooleanSupplier paused=()->false;public void setPaused(java.util.function.BooleanSupplier paused){this.paused=paused;}
 public DocumentRecovery(){timer=new Timer(500,e->{if(paused.getAsBoolean())return;long current=DrawingView.nativeRevision();if(current!=observed){observed=current;changedAt=System.currentTimeMillis();}else if(!busy&&stored!=current&&DrawingView.nativeWidth()>0&&System.currentTimeMillis()-changedAt>=3000){busy=true;long revision=current;int token=generation.get();worker.execute(()->{Path temp=null;try{Files.createDirectories(file().getParent());temp=Files.createTempFile(file().getParent(),"recovery-",".tmp");if(DrawingView.nativeRecoveryPath(temp.toString(),revision)){synchronized(fileLock){if(generation.get()!=token)return;try{Files.move(temp,file(),StandardCopyOption.REPLACE_EXISTING,StandardCopyOption.ATOMIC_MOVE);}catch(AtomicMoveNotSupportedException error){Files.move(temp,file(),StandardCopyOption.REPLACE_EXISTING);}stored=revision;}}}catch(Exception ignored){}finally{if(temp!=null)try{Files.deleteIfExists(temp);}catch(Exception ignored){}busy=false;}});}});timer.start();}
 public void clear(){synchronized(fileLock){generation.incrementAndGet();try{Files.deleteIfExists(file());}catch(Exception ignored){}}}
 @Override public void close(){timer.stop();synchronized(fileLock){generation.incrementAndGet();}worker.shutdown();}
}
