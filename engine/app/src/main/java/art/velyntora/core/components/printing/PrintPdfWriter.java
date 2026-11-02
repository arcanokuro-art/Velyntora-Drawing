package art.velyntora.core;
import android.content.Context;
import android.graphics.*;
import android.graphics.pdf.PdfDocument;
import android.os.CancellationSignal;
import android.print.PrintAttributes;
import android.print.pdf.PrintedPdfDocument;
import java.io.*;
/** One image per page at original resolution, decoded in bounded horizontal strips. */
public final class PrintPdfWriter {
  public static void write(Context context,PrintSnapshot snapshot,PrintAttributes attributes,OutputStream destination,CancellationSignal cancellation)throws IOException{
    cancellation.throwIfCanceled();
    try(PrintedPdfDocument pdf=new PrintedPdfDocument(context,attributes)){
      BitmapRegionDecoder decoder=BitmapRegionDecoder.newInstance(snapshot.file.toString(),false);
      if(decoder==null)throw new IOException("No se pudo leer la copia de impresión");
      PdfDocument.Page page=null;
      try{
        page=pdf.startPage(0);Canvas canvas=page.getCanvas();
        Rect content=page.getInfo().getContentRect();
        // PdfDocument already translates the canvas to the content origin.
        PrintLayout.Placement fit=PrintLayout.fit(snapshot.width,snapshot.height,content.width(),content.height());
        canvas.drawColor(Color.WHITE);Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG);
        if(attributes.getColorMode()==PrintAttributes.COLOR_MODE_MONOCHROME){ColorMatrix monochrome=new ColorMatrix();monochrome.setSaturation(0);paint.setColorFilter(new ColorMatrixColorFilter(monochrome));}
        canvas.save();canvas.clipRect(0,0,content.width(),content.height());
        canvas.translate(fit.left(),fit.top());canvas.scale(fit.width()/snapshot.width,fit.height()/snapshot.height);
        BitmapFactory.Options options=new BitmapFactory.Options();options.inPreferredConfig=Bitmap.Config.ARGB_8888;
        int rowsPerBlock=Math.max(1,Math.min(128,262144/snapshot.width));
        for(int y=0;y<snapshot.height;y+=rowsPerBlock){
          cancellation.throwIfCanceled();int rows=Math.min(rowsPerBlock,snapshot.height-y);
          Bitmap block=decoder.decodeRegion(new Rect(0,y,snapshot.width,y+rows),options);
          if(block==null)throw new IOException("Bloque de impresión no válido");
          try{canvas.drawBitmap(block,null,new RectF(0,y,snapshot.width,y+rows),paint);}finally{block.recycle();}
        }
        canvas.restore();pdf.finishPage(page);page=null;cancellation.throwIfCanceled();
        OutputStream checked=new FilterOutputStream(destination){
          @Override public void write(byte[] bytes,int offset,int length)throws IOException{cancellation.throwIfCanceled();out.write(bytes,offset,length);}
          @Override public void write(int value)throws IOException{cancellation.throwIfCanceled();out.write(value);}
        };
        pdf.writeTo(checked);checked.flush();cancellation.throwIfCanceled();
      }finally{if(page!=null)pdf.finishPage(page);decoder.recycle();}
    }
  }
  private PrintPdfWriter(){}
}
