package art.velyntora.core;
import android.content.Context;
import android.os.*;
import android.print.*;
import java.io.*;
import java.util.concurrent.*;
/** Android print lifecycle, repeatable previews, page ranges and cooperative cancellation. */
public final class ArtworkPrintAdapter extends PrintDocumentAdapter {
  private final Context context;private final PrintSnapshot snapshot;
  private final ExecutorService worker=Executors.newSingleThreadExecutor();
  private final Handler main=new Handler(Looper.getMainLooper());
  private final java.util.concurrent.ConcurrentLinkedQueue<CancellationSignal> writing=new java.util.concurrent.ConcurrentLinkedQueue<>();
  private PrintAttributes attributes;private volatile boolean finished;
  public ArtworkPrintAdapter(Context context,PrintSnapshot snapshot){this.context=context.getApplicationContext();this.snapshot=snapshot;}
  @Override public void onLayout(PrintAttributes oldAttributes,PrintAttributes newAttributes,CancellationSignal cancellation,LayoutResultCallback callback,Bundle extras){
    if(cancellation.isCanceled()||finished){callback.onLayoutCancelled();return;}
    attributes=newAttributes;
    callback.onLayoutFinished(new PrintDocumentInfo.Builder(snapshot.title+".pdf").setContentType(PrintDocumentInfo.CONTENT_TYPE_PHOTO).setPageCount(1).build(),!newAttributes.equals(oldAttributes));
  }
  @Override public void onWrite(PageRange[] pages,ParcelFileDescriptor destination,CancellationSignal cancellation,WriteResultCallback callback){
    if(cancellation.isCanceled()||finished){callback.onWriteCancelled();return;}
    boolean include=false;for(PageRange page:pages)if(page.getStart()==0)include=true;
    if(!include){callback.onWriteFinished(new PageRange[0]);return;}
    final PrintAttributes selected=attributes;
    if(selected==null){callback.onWriteFailed("Configura el tamaño de papel antes de imprimir");return;}
    writing.add(cancellation);
    try{worker.execute(()->{
      try(OutputStream output=new ParcelFileDescriptor.AutoCloseOutputStream(ParcelFileDescriptor.dup(destination.getFileDescriptor()))){
        if(finished)cancellation.cancel();
        PrintPdfWriter.write(context,snapshot,selected,output,cancellation);
        main.post(()->{if(cancellation.isCanceled()||finished)callback.onWriteCancelled();else callback.onWriteFinished(new PageRange[]{new PageRange(0,0)});});
      }catch(OperationCanceledException error){main.post(callback::onWriteCancelled);}
      catch(IOException|RuntimeException|OutOfMemoryError error){main.post(()->{if(cancellation.isCanceled()||finished)callback.onWriteCancelled();else callback.onWriteFailed("No se pudo preparar la impresión. "+message(error));});}
      finally{writing.remove(cancellation);}
    });}catch(RejectedExecutionException closed){writing.remove(cancellation);callback.onWriteCancelled();}
  }
  @Override public void onFinish(){if(finished)return;finished=true;for(CancellationSignal signal:writing)signal.cancel();worker.execute(snapshot::close);worker.shutdown();}
  private static String message(Throwable error){return error.getMessage()==null?"Inténtalo de nuevo.":error.getMessage();}
}
