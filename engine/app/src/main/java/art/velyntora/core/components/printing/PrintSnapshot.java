package art.velyntora.core;
import android.os.CancellationSignal;
import android.os.OperationCanceledException;
import java.io.*;
/** Immutable on-disk composite of visible layers, captured at a single revision. */
public final class PrintSnapshot implements AutoCloseable {
  public interface Source {
    int width();int height();long revision();
    int[] region(int y,int width,int rows);
  }
  public final File file;
  public final int width,height;
  public final String title;
  private PrintSnapshot(File file,int width,int height,String title){this.file=file;this.width=width;this.height=height;this.title=title;}
  public static PrintSnapshot capture(File directory,Source source,String title,CancellationSignal cancellation)throws IOException{
    cancellation.throwIfCanceled();
    if(!directory.isDirectory()&&!directory.mkdirs())throw new IOException("No se puede preparar la carpeta de impresión");
    long expected=source.revision();int width=source.width(),height=source.height();
    if(!art.velyntora.core.document.CanvasLimits.accepts(width,height))throw new IOException("No hay un lienzo válido para imprimir");
    File temporary=File.createTempFile("illustration-",".png",directory);boolean success=false;
    final int blockRows=Math.max(1,Math.min(64,262144/width));
    final int[][] block={null};final int[] firstRow={-1};
    try{
      try(OutputStream output=new BufferedOutputStream(new FileOutputStream(temporary))){
      PngWriter.write(output,width,height,(y,row)->{
        cancellation.throwIfCanceled();
        if(source.revision()!=expected)throw new IllegalStateException("El documento cambió durante la preparación; vuelve a imprimir");
        if(block[0]==null||y>=firstRow[0]+block[0].length/width){
          firstRow[0]=y;int rows=Math.min(blockRows,height-y);block[0]=source.region(y,width,rows);
          if(block[0]==null||block[0].length!=width*rows)throw new IllegalStateException("No se pudo leer el dibujo completo");
          if(source.revision()!=expected)throw new IllegalStateException("El documento cambió durante la preparación; vuelve a imprimir");
        }
        System.arraycopy(block[0],(y-firstRow[0])*width,row,0,width);
      });
      cancellation.throwIfCanceled();
      if(source.revision()!=expected)throw new IOException("El documento cambió durante la preparación; vuelve a imprimir");
      output.flush();
      }
      cancellation.throwIfCanceled();success=true;
      return new PrintSnapshot(temporary,width,height,title==null||title.trim().isEmpty()?"Ilustración":title);
    }finally{block[0]=null;if(!success)temporary.delete();}
  }
  @Override public void close(){file.delete();}
}
