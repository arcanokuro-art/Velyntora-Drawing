package art.velyntora.core;

import android.app.*;
import android.content.Intent;
import android.view.*;
import android.widget.*;
import androidx.test.platform.app.InstrumentationRegistry;
import org.junit.Test;
import static org.junit.Assert.*;

public final class DrawingProjectSetupTest {
  private final Instrumentation instrumentation=InstrumentationRegistry.getInstrumentation();
  private View find(View root,String description){
    if(description.contentEquals(root.getContentDescription()==null?"":root.getContentDescription()))return root;
    if(root instanceof ViewGroup){ViewGroup group=(ViewGroup)root;for(int i=0;i<group.getChildCount();i++){View found=find(group.getChildAt(i),description);if(found!=null)return found;}}
    return null;
  }
  private CheckBox checkbox(View root){
    if(root instanceof CheckBox)return (CheckBox)root;
    if(root instanceof ViewGroup){ViewGroup group=(ViewGroup)root;for(int i=0;i<group.getChildCount();i++){CheckBox found=checkbox(group.getChildAt(i));if(found!=null)return found;}}
    return null;
  }
  @Test public void presetsMatchReferenceAndRespectCanvasBudget(){
    DrawingPresets.Preset[] presets=DrawingPresets.all();assertEquals(7,presets.length);
    int[][] reference={{1920,1080},{1280,720},{1080,1920},{720,1280},{1700,2400},{4299,6071},{4960,7016}};
    for(int i=0;i<presets.length;i++){
      DrawingPresets.Preset preset=presets[i];assertEquals(reference[i][0],preset.width);assertEquals(reference[i][1],preset.height);
      assertEquals(i<5,preset.supported());int[] size=preset.compatibleSize();
      assertTrue(art.velyntora.core.document.CanvasLimits.accepts(size[0],size[1]));
      if(i<5)assertArrayEquals(reference[i],size);else{assertEquals(6000,size[1]);assertEquals((double)preset.width/preset.height,(double)size[0]/size[1],0.0001);}
    }
  }
  @Test public void linkedDimensionsPresetValidationCreateAndCancel(){
    MainActivity editor=(MainActivity)instrumentation.startActivitySync(new Intent(instrumentation.getTargetContext(),MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    final AlertDialog[] dialog={null};final EditText[] width={null},height={null};final Spinner[] presets={null};final CheckBox[] linked={null};
    try{
      instrumentation.runOnMainSync(()->{
        assertTrue(editor.readDrawing().newDocument(64,32));editor.markDocumentClean("Prueba");
        dialog[0]=new DrawingProjectSetup(editor).show();View root=dialog[0].getWindow().getDecorView();
        width[0]=(EditText)find(root,"Ancho en píxeles");height[0]=(EditText)find(root,"Alto en píxeles");
        presets[0]=(Spinner)find(root,"Presets de dibujo");linked[0]=checkbox(root);
        assertNotNull(width[0]);assertNotNull(height[0]);assertNotNull(linked[0]);assertEquals(8,presets[0].getCount());
      });
      java.util.concurrent.atomic.AtomicBoolean laidOut=new java.util.concurrent.atomic.AtomicBoolean();
      long layoutDeadline=android.os.SystemClock.uptimeMillis()+5000;
      do{
        instrumentation.waitForIdleSync();
        instrumentation.runOnMainSync(()->laidOut.set(presets[0].isLaidOut()&&presets[0].getSelectedItemPosition()==0));
        if(!laidOut.get())android.os.SystemClock.sleep(50);
      }while(!laidOut.get()&&android.os.SystemClock.uptimeMillis()<layoutDeadline);
      assertTrue("Setup must complete its first layout before user interaction",laidOut.get());
      instrumentation.runOnMainSync(()->{
        linked[0].setChecked(true);width[0].setText("128");assertEquals("64",height[0].getText().toString());
        height[0].setText("96");assertEquals("192",width[0].getText().toString());
        linked[0].setChecked(false);width[0].setText("0");dialog[0].getButton(AlertDialog.BUTTON_POSITIVE).performClick();
        assertTrue(dialog[0].isShowing());assertEquals(64,editor.readDrawing().documentWidth());
        presets[0].setSelection(2);
      });
      // Spinner selection is delivered on a later layout frame, after the main queue is idle.
      java.util.concurrent.atomic.AtomicBoolean selected=new java.util.concurrent.atomic.AtomicBoolean();
      long deadline=android.os.SystemClock.uptimeMillis()+5000;
      do{
        instrumentation.waitForIdleSync();
        instrumentation.runOnMainSync(()->selected.set("1280".contentEquals(width[0].getText())&&"720".contentEquals(height[0].getText())));
        if(!selected.get())android.os.SystemClock.sleep(50);
      }while(!selected.get()&&android.os.SystemClock.uptimeMillis()<deadline);
      instrumentation.runOnMainSync(()->assertTrue("Preset must update dimensions; selection="+presets[0].getSelectedItemPosition()+", width="+width[0].getText()+", height="+height[0].getText(),selected.get()));
      instrumentation.runOnMainSync(()->{
        assertEquals("1280",width[0].getText().toString());assertEquals("720",height[0].getText().toString());
        linked[0].setChecked(true);width[0].setText("640");assertEquals("360",height[0].getText().toString());
        dialog[0].getButton(AlertDialog.BUTTON_POSITIVE).performClick();assertFalse(dialog[0].isShowing());
        assertEquals(640,editor.readDrawing().documentWidth());assertEquals(360,editor.readDrawing().documentHeight());
        dialog[0]=new DrawingProjectSetup(editor).show();
        ((EditText)find(dialog[0].getWindow().getDecorView(),"Ancho en píxeles")).setText("100");
        dialog[0].getButton(AlertDialog.BUTTON_NEGATIVE).performClick();assertEquals(640,editor.readDrawing().documentWidth());assertEquals(360,editor.readDrawing().documentHeight());
      });
    }finally{instrumentation.runOnMainSync(()->{if(dialog[0]!=null)dialog[0].dismiss();editor.finish();});}
  }
}
