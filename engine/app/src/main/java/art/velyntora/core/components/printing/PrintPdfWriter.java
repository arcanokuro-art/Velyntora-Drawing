package art.velyntora.core;
import android.content.Context;
import android.graphics.*;
import android.graphics.pdf.PdfDocument;
import android.os.CancellationSignal;
import android.print.PrintAttributes;
import android.print.pdf.PrintedPdfDocument;
import java.io.*;
/** Shared bounded-memory artwork rendering for printer pages and direct PDF export. */
public final class PrintPdfWriter {
  public static void write(Context context,PrintSnapshot snapshot,PrintAttributes attributes,OutputStream destination,CancellationSignal cancellation)throws IOException{
    cancellation.throwIfCanceled();
    PrintedPdfDocument pdf=new PrintedPdfDocument(context,attributes);
    PdfDocument.Page page=null;
    try{
      page=pdf.startPage(0);Rect content=page.getInfo().getContentRect();
      // Android already translates this canvas to the printable content origin.
      drawPage(page.getCanvas(),snapshot,content.width(),content.height(),attributes.getColorMode()==PrintAttributes.COLOR_MODE_MONOCHROME,cancellation);
      pdf.finishPage(page);page=null;
      writeTo(pdf,destination,cancellation);
    }finally{if(page!=null)pdf.finishPage(page);pdf.close();}
  }
  static void drawPage(Canvas canvas,PrintSnapshot snapshot,int width,int height,boolean monochrome,CancellationSignal cancellation)throws IOException{
    cancellation.throwIfCanceled();
    BitmapRegionDecoder decoder=BitmapRegionDecoder.newInstance(snapshot.file.toString(),false);
    if(decoder==null)throw new IOException("No se pudo leer la copia del dibujo");
    int saved=canvas.save();
    try{
      PrintLayout.Placement fit=PrintLayout.fit(snapshot.width,snapshot.height,width,height);
      canvas.drawColor(Color.WHITE);Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG);
      if(monochrome){ColorMatrix matrix=new ColorMatrix();matrix.setSaturation(0);paint.setColorFilter(new ColorMatrixColorFilter(matrix));}
      canvas.clipRect(0,0,width,height);canvas.translate(fit.left(),fit.top());canvas.scale(fit.width()/snapshot.width,fit.height()/snapshot.height);
      BitmapFactory.Options options=new BitmapFactory.Options();options.inPreferredConfig=Bitmap.Config.ARGB_8888;
      int rowsPerBlock=Math.max(1,Math.min(128,262144/snapshot.width));
      for(int y=0;y<snapshot.height;y+=rowsPerBlock){
        cancellation.throwIfCanceled();int rows=Math.min(rowsPerBlock,snapshot.height-y);
        Bitmap block=decoder.decodeRegion(new Rect(0,y,snapshot.width,y+rows),options);
        if(block==null)throw new IOException("Bloque del dibujo no válido");
        try{canvas.drawBitmap(block,null,new RectF(0,y,snapshot.width,y+rows),paint);}finally{block.recycle();}
      }
    }finally{canvas.restoreToCount(saved);decoder.recycle();}
  }
  static void writeTo(PdfDocument pdf,OutputStream destination,CancellationSignal cancellation)throws IOException{
    cancellation.throwIfCanceled();
    OutputStream checked=new FilterOutputStream(destination){
      @Override public void write(byte[] bytes,int offset,int length)throws IOException{cancellation.throwIfCanceled();out.write(bytes,offset,length);}
      @Override public void write(int value)throws IOException{cancellation.throwIfCanceled();out.write(value);}
    };
    pdf.writeTo(checked);checked.flush();cancellation.throwIfCanceled();
  }
  private PrintPdfWriter(){}
}
