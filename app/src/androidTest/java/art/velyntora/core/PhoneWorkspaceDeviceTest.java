package art.velyntora.core;

import android.app.Instrumentation;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import androidx.test.platform.app.InstrumentationRegistry;
import org.junit.Test;
import static org.junit.Assert.*;

public final class PhoneWorkspaceDeviceTest {
  private View visible(View view,String name){
    if(!view.isShown())return null;
    if(name.equals(view.getContentDescription()))return view;
    if(view instanceof ViewGroup){
      ViewGroup group=(ViewGroup)view;
      for(int i=0;i<group.getChildCount();i++){
        View child=visible(group.getChildAt(i),name);
        if(child!=null)return child;
      }
    }
    return null;
  }
  @Test public void phoneDockChangesToolsAndPanelsWithoutReplacingCanvas(){
    Instrumentation test=InstrumentationRegistry.getInstrumentation();
    MainActivity editor=(MainActivity)test.startActivitySync(new Intent(test.getTargetContext(),MainActivity.class)
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    try{
      test.waitForIdleSync();
      test.runOnMainSync(()->{
        var config=editor.getResources().getConfiguration();
        assertTrue(new WorkspaceLayout(config.screenWidthDp,config.screenHeightDp,config.fontScale).phone);
        View root=editor.getWindow().getDecorView();
        DrawingView canvas=editor.readDrawing();
        assertSame(editor.readWorkspaceFrame(),canvas.getParent());
        assertNotNull(visible(root,"Guardar"));
        assertTrue(visible(root,"Borrador").performClick());
        assertEquals(DrawingView.ERASER,canvas.currentTool());
        assertTrue(visible(root,"Capas").performClick());
        assertTrue(editor.readLayerScroll().isShown());
        editor.onBackPressed();
        assertFalse(editor.readLayerScroll().isShown());
        assertTrue(visible(root,"Color").performClick());
        assertTrue(editor.readPaletteScroll().isShown());
        assertTrue(visible(root,"Más").performClick());
        assertFalse(editor.readPaletteScroll().isShown());
        assertTrue(editor.readToolScroll().isShown());
        assertTrue(visible(root,"Texto").performClick());
        assertEquals(DrawingView.TEXT,canvas.currentTool());
        assertFalse(editor.readToolScroll().isShown());
        assertSame(canvas,editor.readDrawing());
        assertTrue(visible(root,"Pincel").performClick());
      });
    }finally{test.runOnMainSync(editor::finish);}
  }
}
