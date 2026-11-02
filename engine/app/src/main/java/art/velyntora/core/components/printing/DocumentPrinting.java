package art.velyntora.core;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.CancellationSignal;
import android.os.OperationCanceledException;
import android.print.*;
import java.io.File;
/** Workspace entry point. Snapshot preparation never marks the project as saved. */
final class DocumentPrinting implements AutoCloseable {
  private final MainActivity host;private CancellationSignal preparing;
  DocumentPrinting(MainActivity host){this.host=host;}
  void start(){
    if(host.readProjectProgress()!=null){host.message("Espera a que termine la operación actual");return;}
    if(host.readDrawing().placedImage().active()){host.message("Confirma o cancela la imagen o el texto antes de imprimir");return;}
    PrintManager manager=(PrintManager)host.getSystemService(Context.PRINT_SERVICE);
    if(manager==null){host.message("El servicio de impresión no está disponible en este dispositivo");return;}
    host.readDrawing().confirmCurve();
    CancellationSignal cancellation=new CancellationSignal();preparing=cancellation;
    ProgressDialog progress=new ProgressDialog(host);progress.setMessage("Preparando impresión…");progress.setCancelable(true);progress.setOnCancelListener(dialog->cancellation.cancel());
    host.writeProjectProgress(progress);host.readDrawing().setEnabled(false);progress.show();
    String title=host.readDocumentName();
    PrintSnapshot.Source source=new PrintSnapshot.Source(){
      public int width(){return DrawingView.nativeWidth();}public int height(){return DrawingView.nativeHeight();}public long revision(){return DrawingView.nativeRevision();}
      public int[] region(int y,int width,int rows){return DrawingView.nativeRegionPixels(0,y,width,rows);}
    };
    new Thread(()->{
      PrintSnapshot snapshot=null;String failure=null;
      try{snapshot=PrintSnapshot.capture(new File(host.getCacheDir(),"printing"),source,title,cancellation);}
      catch(OperationCanceledException ignored){}
      catch(Exception|OutOfMemoryError error){failure=error.getMessage()==null?"No se pudo preparar la impresión":error.getMessage();}
      PrintSnapshot completed=snapshot;String error=failure;
      host.runOnUiThread(()->{
        if(preparing==cancellation)preparing=null;
        if(!host.isDestroyed()){
          if(progress.isShowing())progress.dismiss();
          if(host.readProjectProgress()==progress)host.writeProjectProgress(null);
          host.readDrawing().setEnabled(true);
        }
        if(host.isDestroyed()||cancellation.isCanceled()){if(completed!=null)completed.close();return;}
        if(completed==null){if(error!=null)host.message(error);return;}
        ArtworkPrintAdapter adapter=new ArtworkPrintAdapter(host,completed);
        try{
          PrintAttributes.MediaSize paper=completed.width>completed.height?PrintAttributes.MediaSize.ISO_A4.asLandscape():PrintAttributes.MediaSize.ISO_A4.asPortrait();
          PrintAttributes attributes=new PrintAttributes.Builder().setMediaSize(paper).setResolution(new PrintAttributes.Resolution("velyntora","Velyntora Drawing",300,300)).setMinMargins(new PrintAttributes.Margins(250,250,250,250)).setColorMode(PrintAttributes.COLOR_MODE_COLOR).build();
          if(manager.print("Velyntora Drawing — "+completed.title,adapter,attributes)==null)adapter.onFinish();
        }catch(RuntimeException problem){adapter.onFinish();host.message("No se pudo abrir el panel de impresión de Android");}
      });
    },"velyntora-print-snapshot").start();
  }
  @Override public void close(){if(preparing!=null)preparing.cancel();}
}
