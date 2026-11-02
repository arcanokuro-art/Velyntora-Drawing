package app.velyntora;
import static org.junit.Assert.*;
import android.app.*;
import android.content.*;
import android.graphics.*;
import android.net.Uri;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.view.*;
import android.widget.EditText;
import androidx.test.platform.app.InstrumentationRegistry;
import org.junit.Test;
/** Exercises the real host -> library -> JNI path, not a mock editor. */
public final class EditorIntegrationTest {
    private final Instrumentation instrumentation=InstrumentationRegistry.getInstrumentation();
    private MainActivity home(){return (MainActivity)instrumentation.startActivitySync(new Intent(instrumentation.getTargetContext(),MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));}
    private View find(View view,Class<?> type){if(type.isInstance(view))return view;if(view instanceof ViewGroup){ViewGroup g=(ViewGroup)view;for(int i=0;i<g.getChildCount();i++){View found=find(g.getChildAt(i),type);if(found!=null)return found;}}return null;}
    private void systemBack(){
        android.app.UiAutomation automation=instrumentation.getUiAutomation();
        long now=SystemClock.uptimeMillis();
        assertTrue(automation.injectInputEvent(new KeyEvent(now,now,KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_BACK,0),true));
        assertTrue(automation.injectInputEvent(new KeyEvent(now,SystemClock.uptimeMillis(),KeyEvent.ACTION_UP,KeyEvent.KEYCODE_BACK,0),true));
    }
    @Test public void homeCreateOpensNativeEditorAndDimensions(){
        MainActivity home=home();
        Instrumentation.ActivityMonitor monitor=instrumentation.addMonitor("art.velyntora.core.MainActivity",null,false);
        instrumentation.runOnMainSync(home::createIllustration);
        Activity editor=instrumentation.waitForMonitorWithTimeout(monitor,10000);
        assertNotNull("Create must launch Core editor",editor);
        instrumentation.waitForIdleSync();
        instrumentation.runOnMainSync(()->{
            assertNotNull(find(editor.getWindow().getDecorView(),art.velyntora.core.DrawingView.class));
            assertTrue("Immersive navigation enabled",(editor.getWindow().getDecorView().getSystemUiVisibility()&View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)!=0);
            Bitmap logo=app.velyntora.ui.common.BrandLogo.load(home);
            boolean transparent=false;for(int y=0;y<logo.getHeight()&&!transparent;y++)for(int x=0;x<logo.getWidth();x++)if(Color.alpha(logo.getPixel(x,y))==0){transparent=true;break;}
            assertTrue("Logo background must be transparent",transparent);
        });
        // The new-canvas dialog is the only focusable window with dimension inputs.
        assertTrue("Dimension dialog must own window focus",editor.hasWindowFocus()==false);
        systemBack();
        instrumentation.runOnMainSync(()->{editor.finish();home.finish();});
        instrumentation.removeMonitor(monitor);
    }
    @Test public void imageUriIsImportedByCoreAndOpensAndroidPrintPanel(){
        Context context=instrumentation.getTargetContext();
        ContentValues values=new ContentValues();values.put(MediaStore.Images.Media.DISPLAY_NAME,"drawing-integration.png");values.put(MediaStore.Images.Media.MIME_TYPE,"image/png");
        Uri uri=context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);assertNotNull(uri);
        Activity editor=null;
        try{
            Bitmap image=Bitmap.createBitmap(79,53,Bitmap.Config.ARGB_8888);image.eraseColor(Color.MAGENTA);
            try(java.io.OutputStream out=context.getContentResolver().openOutputStream(uri)){assertTrue(image.compress(Bitmap.CompressFormat.PNG,100,out));}catch(java.io.IOException error){throw new AssertionError(error);}finally{image.recycle();}
            Intent intent=new Intent(context,art.velyntora.core.MainActivity.class).setAction("app.velyntora.drawing.OPEN").setData(uri).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_GRANT_READ_URI_PERMISSION);
            editor=instrumentation.startActivitySync(intent);final Activity opened=editor;
            java.util.concurrent.atomic.AtomicBoolean imported=new java.util.concurrent.atomic.AtomicBoolean();
            long deadline=SystemClock.uptimeMillis()+15000;
            do{instrumentation.waitForIdleSync();instrumentation.runOnMainSync(()->{art.velyntora.core.DrawingView canvas=(art.velyntora.core.DrawingView)find(opened.getWindow().getDecorView(),art.velyntora.core.DrawingView.class);imported.set(canvas!=null&&canvas.documentWidth()==79&&canvas.documentHeight()==53);});if(!imported.get())SystemClock.sleep(50);}while(!imported.get()&&SystemClock.uptimeMillis()<deadline);
            assertTrue("Core must import the passed URI at its original dimensions",imported.get());
            // Dispatch the actual keyboard command, then inspect the system print activity.
            instrumentation.runOnMainSync(()->opened.onKeyDown(KeyEvent.KEYCODE_P,new KeyEvent(0,0,KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_P,0,KeyEvent.META_CTRL_ON)));
            boolean printing=false;deadline=SystemClock.uptimeMillis()+15000;
            do{
                android.view.accessibility.AccessibilityNodeInfo root=instrumentation.getUiAutomation().getRootInActiveWindow();
                if(root!=null){printing="com.android.printspooler".contentEquals(root.getPackageName()==null?"":root.getPackageName());root.recycle();}
                if(!printing)SystemClock.sleep(100);
            }while(!printing&&SystemClock.uptimeMillis()<deadline);
            assertTrue("Print command must open Android's system print panel",printing);
            try{instrumentation.getUiAutomation().waitForIdle(300,5000);}catch(java.util.concurrent.TimeoutException ignored){}
            systemBack();
            java.io.File snapshots=new java.io.File(context.getCacheDir(),"printing");
            deadline=SystemClock.uptimeMillis()+10000;
            long nextBack=SystemClock.uptimeMillis()+1500;
            while(snapshots.list()!=null&&snapshots.list().length>0&&SystemClock.uptimeMillis()<deadline){
                // A first Back may only dismiss the print UI keyboard or arrive during its opening transition.
                if(SystemClock.uptimeMillis()>=nextBack){
                    android.view.accessibility.AccessibilityNodeInfo root=instrumentation.getUiAutomation().getRootInActiveWindow();
                    boolean printStillOpen=root!=null&&"com.android.printspooler".contentEquals(root.getPackageName()==null?"":root.getPackageName());
                    if(root!=null)root.recycle();
                    if(printStillOpen)systemBack();
                    nextBack=SystemClock.uptimeMillis()+1500;
                }
                SystemClock.sleep(100);
            }
            assertTrue("Cancelled print must delete its snapshot",snapshots.list()==null||snapshots.list().length==0);
        }finally{if(editor!=null){final Activity finish=editor;instrumentation.runOnMainSync(finish::finish);}context.getContentResolver().delete(uri,null,null);}
    }
}
