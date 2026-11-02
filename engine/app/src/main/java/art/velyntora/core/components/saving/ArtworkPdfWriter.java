package art.velyntora.core;

import android.graphics.pdf.PdfDocument;
import android.os.CancellationSignal;
import java.io.*;

/** A single PDF page matching the artwork's aspect ratio, without printer margins. */
public final class ArtworkPdfWriter {
  public static void write(PrintSnapshot snapshot,OutputStream destination,CancellationSignal cancellation)throws IOException{
    cancellation.throwIfCanceled();PdfDocument pdf=new PdfDocument();PdfDocument.Page page=null;
    try{
      // One canvas pixel maps to one PDF point; embedded raster pixels are not reduced.
      page=pdf.startPage(new PdfDocument.PageInfo.Builder(snapshot.width,snapshot.height,1).create());
      PrintPdfWriter.drawPage(page.getCanvas(),snapshot,snapshot.width,snapshot.height,false,cancellation);
      pdf.finishPage(page);page=null;PrintPdfWriter.writeTo(pdf,destination,cancellation);
    }finally{if(page!=null)pdf.finishPage(page);pdf.close();}
  }
  private ArtworkPdfWriter(){}
}
