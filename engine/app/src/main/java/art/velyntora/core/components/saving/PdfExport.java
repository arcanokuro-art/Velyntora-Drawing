package art.velyntora.core;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.CancellationSignal;
import android.os.OperationCanceledException;
import java.io.*;

/** Direct SAF PDF export: no print service, no project-save side effects. */
final class PdfExport implements AutoCloseable {
  static final int SAVE_PDF=8201;
  private final MainActivity host;private CancellationSignal working;
  PdfExport(MainActivity host){this.host=host;}
  private boolean ready(){
    if(host.readProjectProgress()!=null){host.message("Espera a que termine la operación actual");return false;}
    if(host.readDrawing().placedImage().active()){host.message("Confirma o cancela la imagen o el texto antes de guardar");return false;}
    return true;
  }
  void chooseDestination(){
    if(!ready())return;
    Intent intent=new Intent(Intent.ACTION_CREATE_DOCUMENT).addCategory(Intent.CATEGORY_OPENABLE)
      .setType("application/pdf").putExtra(Intent.EXTRA_TITLE,"dibujo.pdf");
    try{host.startActivityForResult(intent,SAVE_PDF);}
    catch(RuntimeException error){host.message("No se pudo abrir el selector para guardar PDF");}
  }
  void export(Uri destination){
    if(!ready())return;
    host.readDrawing().confirmCurve();
    CancellationSignal cancellation=new CancellationSignal();working=cancellation;
    ProgressDialog progress=new ProgressDialog(host);progress.setMessage("Guardando PDF…");progress.setCancelable(false);
    host.writeProjectProgress(progress);host.readDrawing().setEnabled(false);progress.show();
    String title=host.readDocumentName();
    PrintSnapshot.Source source=new PrintSnapshot.Source(){
      public int width(){return DrawingView.nativeWidth();}public int height(){return DrawingView.nativeHeight();}public long revision(){return DrawingView.nativeRevision();}
      public int[] region(int y,int width,int rows){return DrawingView.nativeRegionPixels(0,y,width,rows);}
    };
    new Thread(()->{
      String failure=null;File prepared=null;
      try(PrintSnapshot snapshot=PrintSnapshot.capture(new File(host.getCacheDir(),"pdf-export"),source,title,cancellation)){
        prepared=File.createTempFile("artwork-",".pdf",host.getCacheDir());
        try(OutputStream output=new BufferedOutputStream(new FileOutputStream(prepared))){ArtworkPdfWriter.write(snapshot,output,cancellation);}
        cancellation.throwIfCanceled();
        try(InputStream input=new BufferedInputStream(new FileInputStream(prepared));OutputStream raw=host.getContentResolver().openOutputStream(destination,"wt")){
          if(raw==null)throw new IOException("No se pudo abrir el archivo PDF");
          BufferedOutputStream output=new BufferedOutputStream(raw);byte[] block=new byte[65536];int count;
          while((count=input.read(block))!=-1){cancellation.throwIfCanceled();output.write(block,0,count);}
          output.flush();
        }
        cancellation.throwIfCanceled();
      }catch(OperationCanceledException ignored){failure="Se canceló la exportación PDF";}
      catch(Exception|OutOfMemoryError error){failure="No se pudo guardar el PDF. "+(error.getMessage()==null?"Inténtalo de nuevo.":error.getMessage());}
      finally{if(prepared!=null)prepared.delete();}
      String error=failure;
      host.runOnUiThread(()->{
        if(working==cancellation)working=null;
        if(host.isDestroyed())return;
        progress.dismiss();if(host.readProjectProgress()==progress)host.writeProjectProgress(null);host.readDrawing().setEnabled(true);
        host.message(error==null?"PDF guardado":error);
      });
    },"velyntora-pdf-export").start();
  }
  @Override public void close(){if(working!=null)working.cancel();}
}
