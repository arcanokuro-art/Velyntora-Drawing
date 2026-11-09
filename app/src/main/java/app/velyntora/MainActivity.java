package app.velyntora;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.content.ActivityNotFoundException;
import app.velyntora.ui.start.*;
public final class MainActivity extends Activity implements StartActions {
    @Override public void onCreate(Bundle state){super.onCreate(state);getWindow().setStatusBarColor(0xff151922);getWindow().setNavigationBarColor(0xff151922);setContentView(new StartScreen(this,this).create());app.velyntora.ui.common.ImmersiveMode.apply(getWindow());}
    @Override public void onWindowFocusChanged(boolean hasFocus){super.onWindowFocusChanged(hasFocus);if(hasFocus)app.velyntora.ui.common.ImmersiveMode.apply(getWindow());}
    @Override protected void onResume(){super.onResume();app.velyntora.ui.common.ImmersiveMode.apply(getWindow());}
    private void info(String title,String message){new AlertDialog.Builder(this).setTitle(title).setMessage(message).setPositiveButton("Aceptar",null).show();}
    public void createIllustration(){showCreateDialog();}
    AlertDialog showCreateDialog(){return app.velyntora.editor.EditorNavigator.create(this);}
    private static final int OPEN_IMAGE=2001, OPEN_PROJECT=2002;
    public void openImage(){chooseFile(OPEN_IMAGE,"image/*","Abrir imagen");}
    public void openProject(){chooseFile(OPEN_PROJECT,"*/*","Abrir proyecto");}
    private void chooseFile(int request,String type,String title){
        Intent intent=new Intent(Intent.ACTION_OPEN_DOCUMENT).addCategory(Intent.CATEGORY_OPENABLE)
                .setType(type).addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        try{startActivityForResult(intent,request);}
        catch(ActivityNotFoundException unavailable){
            try{startActivityForResult(intent.setAction(Intent.ACTION_GET_CONTENT),request);}
            catch(ActivityNotFoundException noPicker){info(title,"No hay un selector de archivos disponible en este dispositivo.");}
        }
    }
    @Override protected void onActivityResult(int request,int result,Intent data){
        super.onActivityResult(request,result,data);
        if((request==OPEN_IMAGE||request==OPEN_PROJECT)&&result==RESULT_OK&&data!=null&&data.getData()!=null)
            app.velyntora.editor.EditorNavigator.open(this,data.getData());
    }
    public void showHelp(){art.velyntora.core.QuickStartGuide.show(this);}
}
