package art.velyntora.core;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.*;
/** Drawing start-screen entry; preserves Core's document protection and import pipeline. */
public final class EditorEntry {
    private EditorEntry(){}
    private static final String CREATE="app.velyntora.drawing.CREATE", OPEN="app.velyntora.drawing.OPEN";
    private static final String WIDTH="canvasWidth", HEIGHT="canvasHeight";
    public static Intent create(android.content.Context context,int width,int height){
        return new Intent(context,MainActivity.class).setAction(CREATE).putExtra(WIDTH,width).putExtra(HEIGHT,height);
    }
    public static Intent open(android.content.Context context,android.net.Uri uri){
        return new Intent(context,MainActivity.class).setAction(OPEN).setData(uri).addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
    }
    static boolean hasRequest(Intent intent){return intent!=null && (CREATE.equals(intent.getAction()) || OPEN.equals(intent.getAction()));}
    static void enter(MainActivity host,Bundle state){
        Intent intent=host.getIntent();
        if(state!=null || !hasRequest(intent))return;
        if(CREATE.equals(intent.getAction())){
            if(!host.readDrawing().newDocument(intent.getIntExtra(WIDTH,0),intent.getIntExtra(HEIGHT,0))){
                host.message("No se pudo crear la ilustración con el tamaño elegido");
                host.finish();
                return;
            }
            host.markDocumentClean("Sin título");
        }
        else if(intent.getData()!=null)host.protectDocument(()->host.openDetected(intent.getData()));
    }
    static void immersive(Window window){
        if(Build.VERSION.SDK_INT>=30){
            window.setDecorFitsSystemWindows(false);
            WindowInsetsController controller=window.getInsetsController();
            if(controller!=null){controller.hide(WindowInsets.Type.systemBars());controller.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);}
        }else window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY|View.SYSTEM_UI_FLAG_FULLSCREEN|View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_LAYOUT_STABLE|View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
    }
}
