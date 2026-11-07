package art.velyntora.core;

import android.app.*;
import android.content.Intent;
import android.view.*;
import androidx.test.platform.app.InstrumentationRegistry;
import org.junit.Test;
import static org.junit.Assert.*;

public final class BrushSettingsDeviceTest {
  private View find(View root){
    if("Configurar pincel".contentEquals(root.getContentDescription()==null?"":root.getContentDescription()))return root;
    if(root instanceof ViewGroup){ViewGroup group=(ViewGroup)root;for(int i=0;i<group.getChildCount();i++){View found=find(group.getChildAt(i));if(found!=null)return found;}}
    return null;
  }
  @Test public void brushSettingsAreRemovedOnlyFromEightRequestedTools(){
    Instrumentation instrumentation=InstrumentationRegistry.getInstrumentation();
    MainActivity editor=(MainActivity)instrumentation.startActivitySync(new Intent(instrumentation.getTargetContext(),MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    int[] excluded={DrawingView.MOVE_PIXELS,DrawingView.MOVE_SELECTION,DrawingView.ZOOM,DrawingView.PICKER,DrawingView.PAN,DrawingView.SELECT_RECTANGLE,DrawingView.SELECT_ELLIPSE,DrawingView.SELECT_FREE};
    try{
      instrumentation.runOnMainSync(()->{
        for(int tool=DrawingView.BRUSH;tool<=DrawingView.REMOVE_AI;tool++){
          boolean available=true;for(int id:excluded)if(id==tool)available=false;
          editor.readDrawing().setTool(tool);editor.syncToolState();
          assertEquals("Toolbar access for tool "+tool,available,find(editor.getWindow().getDecorView())!=null);
        }
        // Repeated tool switches must remove the node and restore exactly one accessible button.
        editor.readDrawing().setTool(DrawingView.BRUSH);editor.syncToolState();View previous=find(editor.getWindow().getDecorView());assertNotNull(previous);
        editor.readDrawing().setTool(DrawingView.MOVE_PIXELS);editor.syncToolState();assertNull(previous.getParent());assertNull(find(editor.getWindow().getDecorView()));
        editor.configureBrush();assertNull(find(editor.getWindow().getDecorView()));
        editor.readDrawing().setTool(DrawingView.BRUSH);editor.syncToolState();assertNotNull(find(editor.getWindow().getDecorView()));
      });
    }finally{instrumentation.runOnMainSync(editor::finish);}
  }
}
