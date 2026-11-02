package art.velyntora.core;

import android.content.Context;
import android.graphics.*;
import android.graphics.pdf.PdfRenderer;
import android.os.*;
import android.print.PrintAttributes;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.io.*;
import java.security.MessageDigest;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class PrintingDeviceTest {
  private final Context context=InstrumentationRegistry.getInstrumentation().getTargetContext();
  private File directory()throws IOException{File file=File.createTempFile("printing-test-","",context.getCacheDir());assertTrue(file.delete());assertTrue(file.mkdir());return file;}
  private PrintSnapshot.Source source(){return new PrintSnapshot.Source(){
    public int width(){return 4;}public int height(){return 2;}public long revision(){return 1;}
    public int[] region(int y,int w,int rows){int[] pixels=new int[w*rows];for(int i=0;i<pixels.length;i++)pixels[i]=new int[]{Color.RED,Color.RED,Color.TRANSPARENT,Color.BLUE}[i%4];return pixels;}
  };}
  private PrintAttributes attributes(int color){return new PrintAttributes.Builder().setMediaSize(new PrintAttributes.MediaSize("square","Square",4000,4000)).setResolution(new PrintAttributes.Resolution("test","Test",300,300)).setMinMargins(new PrintAttributes.Margins(500,500,500,500)).setColorMode(color).build();}
  private Bitmap render(PrintSnapshot snapshot,int color)throws Exception{
    File output=File.createTempFile("printing-pdf-",".pdf",context.getCacheDir());
    try{
      try(OutputStream stream=new FileOutputStream(output)){PrintPdfWriter.write(context,snapshot,attributes(color),stream,new CancellationSignal());}
      try(ParcelFileDescriptor descriptor=ParcelFileDescriptor.open(output,ParcelFileDescriptor.MODE_READ_ONLY);PdfRenderer renderer=new PdfRenderer(descriptor);PdfRenderer.Page page=renderer.openPage(0)){
        assertEquals(1,renderer.getPageCount());assertEquals(288,page.getWidth());assertEquals(288,page.getHeight());
        Bitmap bitmap=Bitmap.createBitmap(288,288,Bitmap.Config.ARGB_8888);bitmap.eraseColor(Color.WHITE);page.render(bitmap,null,null,PdfRenderer.Page.RENDER_MODE_FOR_PRINT);return bitmap;
      }
    }finally{output.delete();}
  }
  @Test public void pdfFitsWholeImageAndPreservesMarginsAndTransparency()throws Exception{
    File directory=directory();
    try(PrintSnapshot snapshot=PrintSnapshot.capture(directory,source(),"Prueba",new CancellationSignal())){
      Bitmap bitmap=render(snapshot,PrintAttributes.COLOR_MODE_COLOR);
      try{assertEquals(Color.RED,bitmap.getPixel(50,130));assertEquals(Color.WHITE,bitmap.getPixel(160,130));assertEquals(Color.BLUE,bitmap.getPixel(235,130));assertEquals(Color.WHITE,bitmap.getPixel(50,50));assertEquals(Color.WHITE,bitmap.getPixel(10,130));}finally{bitmap.recycle();}
      Bitmap mono=render(snapshot,PrintAttributes.COLOR_MODE_MONOCHROME);
      try{int pixel=mono.getPixel(50,130);assertEquals(Color.red(pixel),Color.green(pixel));assertEquals(Color.red(pixel),Color.blue(pixel));}finally{mono.recycle();}
      CancellationSignal cancelled=new CancellationSignal();cancelled.cancel();
      try{PrintPdfWriter.write(context,snapshot,attributes(PrintAttributes.COLOR_MODE_COLOR),new ByteArrayOutputStream(),cancelled);fail("Cancelled print wrote a PDF");}catch(OperationCanceledException expected){}
    }finally{assertEquals(0,directory.list().length);directory.delete();}
  }
  @Test public void revisionChangesAndCancellationDeletePartialSnapshots()throws Exception{
    File directory=directory();final long[] revision={1};
    PrintSnapshot.Source changing=new PrintSnapshot.Source(){public int width(){return 4;}public int height(){return 2;}public long revision(){return revision[0];}public int[] region(int y,int width,int rows){revision[0]++;return new int[width*rows];}};
    try{
      try{PrintSnapshot.capture(directory,changing,"Changed",new CancellationSignal());fail("Mixed revisions accepted");}catch(IllegalStateException expected){}
      assertEquals(0,directory.list().length);
      CancellationSignal cancelled=new CancellationSignal();
      PrintSnapshot.Source large=new PrintSnapshot.Source(){public int width(){return 6000;}public int height(){return 6000;}public long revision(){return 1;}public int[] region(int y,int width,int rows){assertTrue(width*rows<=262144);cancelled.cancel();return new int[width*rows];}};
      try{PrintSnapshot.capture(directory,large,"Large",cancelled);fail("Cancellation ignored");}catch(OperationCanceledException expected){}
      assertEquals(0,directory.list().length);
    }finally{directory.delete();}
  }
  @Test public void originalPrinterIconIsPackagedUnmodified()throws Exception{
    MessageDigest digest=MessageDigest.getInstance("SHA-256");
    try(InputStream stream=context.getAssets().open("printing/impresora.webp")){byte[] block=new byte[8192];int count;while((count=stream.read(block))!=-1)digest.update(block,0,count);}
    StringBuilder hex=new StringBuilder();for(byte value:digest.digest())hex.append(String.format("%02x",value&255));
    assertEquals("092a0f409cc7d7ae956db3cb9598788703ac2e52f4c07c8bb973c2919f26061f",hex.toString());assertNotNull(PrintIcon.load(context));
    PrintLayout.Placement fit=PrintLayout.fit(200,400,600,600);assertEquals(150f,fit.left(),0.01f);assertEquals(0f,fit.top(),0.01f);assertEquals(300f,fit.width(),0.01f);assertEquals(600f,fit.height(),0.01f);
  }
}
