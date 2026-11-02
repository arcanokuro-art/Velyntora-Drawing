package art.velyntora.core;

import android.app.*;
import android.content.*;
import android.graphics.*;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.*;
import android.provider.MediaStore;
import android.view.*;
import android.widget.*;
import androidx.test.platform.app.InstrumentationRegistry;
import org.junit.Test;
import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;
import static org.junit.Assert.*;

public final class SaveMenuDeviceTest {
  private final Instrumentation instrumentation=InstrumentationRegistry.getInstrumentation();
  private MainActivity editor(){return (MainActivity)instrumentation.startActivitySync(new Intent(instrumentation.getTargetContext(),MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));}
  private View find(View root,String label){
    if(label.contentEquals(root.getContentDescription()==null?"":root.getContentDescription()))return root;
    if(root instanceof ViewGroup){ViewGroup group=(ViewGroup)root;for(int i=0;i<group.getChildCount();i++){View result=find(group.getChildAt(i),label);if(result!=null)return result;}}
    return null;
  }
  private void back(){long time=SystemClock.uptimeMillis();assertTrue(instrumentation.getUiAutomation().injectInputEvent(new KeyEvent(time,time,KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_BACK,0),true));assertTrue(instrumentation.getUiAutomation().injectInputEvent(new KeyEvent(time,SystemClock.uptimeMillis(),KeyEvent.ACTION_UP,KeyEvent.KEYCODE_BACK,0),true));}
  @Test public void toolbarArrowOffersEverySaveFormatAndFileOnlyOpens()throws Exception{
    MainActivity editor=editor();final AlertDialog[] dialog={null};
    IntentFilter filter=new IntentFilter(Intent.ACTION_CREATE_DOCUMENT);filter.addCategory(Intent.CATEGORY_OPENABLE);filter.addDataType("application/pdf");
    Instrumentation.ActivityMonitor picker=instrumentation.addMonitor(filter,new Instrumentation.ActivityResult(Activity.RESULT_CANCELED,null),true);
    try{
      instrumentation.runOnMainSync(()->{
        View arrow=find(editor.getWindow().getDecorView(),"Guardar");assertNotNull(arrow);assertTrue(arrow.performClick());
      });
      boolean opened=false;long deadline=SystemClock.uptimeMillis()+5000;
      do{
        android.view.accessibility.AccessibilityNodeInfo root=instrumentation.getUiAutomation().getRootInActiveWindow();
        if(root!=null){opened=!root.findAccessibilityNodeInfosByText("Guardar y exportar").isEmpty();root.recycle();}
        if(!opened)SystemClock.sleep(50);
      }while(!opened&&SystemClock.uptimeMillis()<deadline);
      assertTrue("The toolbar arrow must open saving destinations",opened);back();instrumentation.waitForIdleSync();
      instrumentation.runOnMainSync(()->{
        dialog[0]=editor.openSaveMenu();assertNotNull(dialog[0]);ListAdapter adapter=dialog[0].getListView().getAdapter();
        String[] expected={"Guardar proyecto","Guardar OpenRaster","Guardar PNG","Guardar JPEG","Guardar WebP","Guardar BMP","Guardar TGA","Guardar TIFF","Guardar GIF (imagen fija)","Guardar ICO (hasta 256 px)","Guardar PPM","Guardar PDF","Imprimir…"};
        assertEquals(expected.length,adapter.getCount());for(int i=0;i<expected.length;i++)assertEquals(expected[i],adapter.getItem(i));
        dialog[0].getListView().performItemClick(null,11,11);
      });
      instrumentation.waitForIdleSync();assertEquals("Guardar PDF must open the application/pdf file picker directly",1,picker.getHits());
      instrumentation.runOnMainSync(()->{
        assertNull("Cancelling the picker must not start an export",editor.readProjectProgress());
        dialog[0]=new FileMenu(editor).show();ListAdapter adapter=dialog[0].getListView().getAdapter();assertEquals(9,adapter.getCount());
        for(int i=0;i<adapter.getCount();i++){String label=adapter.getItem(i).toString();assertFalse(label.contains("Guardar"));assertFalse(label.contains("Imprimir"));}
      });
    }finally{instrumentation.removeMonitor(picker);instrumentation.runOnMainSync(()->{if(dialog[0]!=null)dialog[0].dismiss();editor.finish();});}
  }
  @Test public void directPdfExportKeepsCanvasSizePixelsAndProjectState()throws Exception{
    Context context=instrumentation.getTargetContext();MainActivity editor=editor();Uri destination=null;
    final long[] revision={0};final boolean[] dirty={false};
    try{
      ContentValues values=new ContentValues();values.put(MediaStore.Downloads.DISPLAY_NAME,"velyntora-pdf-test.pdf");values.put(MediaStore.Downloads.MIME_TYPE,"application/pdf");
      destination=context.getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI,values);assertNotNull(destination);Uri target=destination;
      instrumentation.runOnMainSync(()->{
        int[] pixels=new int[79*53];java.util.Arrays.fill(pixels,Color.MAGENTA);assertTrue(DrawingView.nativeLoadBitmap(79,53,pixels));
        revision[0]=DrawingView.nativeRevision();dirty[0]=editor.dirty();
        editor.onActivityResult(PdfExport.SAVE_PDF,Activity.RESULT_OK,new Intent().setData(target));assertNotNull(editor.readProjectProgress());
      });
      AtomicBoolean finished=new AtomicBoolean();long deadline=SystemClock.uptimeMillis()+15000;
      do{instrumentation.runOnMainSync(()->finished.set(editor.readProjectProgress()==null));if(!finished.get())SystemClock.sleep(50);}while(!finished.get()&&SystemClock.uptimeMillis()<deadline);
      assertTrue("PDF export must finish",finished.get());
      try(ParcelFileDescriptor descriptor=context.getContentResolver().openFileDescriptor(destination,"r");PdfRenderer renderer=new PdfRenderer(descriptor);PdfRenderer.Page page=renderer.openPage(0)){
        assertEquals(1,renderer.getPageCount());assertEquals(79,page.getWidth());assertEquals(53,page.getHeight());
        Bitmap bitmap=Bitmap.createBitmap(79,53,Bitmap.Config.ARGB_8888);
        try{page.render(bitmap,null,null,PdfRenderer.Page.RENDER_MODE_FOR_PRINT);assertEquals(Color.MAGENTA,bitmap.getPixel(20,20));}finally{bitmap.recycle();}
      }
      instrumentation.runOnMainSync(()->{assertEquals(revision[0],DrawingView.nativeRevision());assertEquals(dirty[0],editor.dirty());assertTrue(editor.readDrawing().isEnabled());});
      File snapshots=new File(context.getCacheDir(),"pdf-export");assertTrue(snapshots.list()==null||snapshots.list().length==0);
      File[] temporary=context.getCacheDir().listFiles((directory,name)->name.startsWith("artwork-")&&name.endsWith(".pdf"));assertNotNull(temporary);assertEquals(0,temporary.length);
    }finally{if(destination!=null)context.getContentResolver().delete(destination,null,null);instrumentation.runOnMainSync(editor::finish);}
  }
}
