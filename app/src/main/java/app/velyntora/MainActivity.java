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
    private static final int OPEN_IMAGE=2001;
    public void openImage(){
        Intent intent=new Intent(Intent.ACTION_OPEN_DOCUMENT).addCategory(Intent.CATEGORY_OPENABLE)
                .setType("image/*").addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        try{startActivityForResult(intent,OPEN_IMAGE);}
        catch(ActivityNotFoundException unavailable){
            try{startActivityForResult(intent.setAction(Intent.ACTION_GET_CONTENT),OPEN_IMAGE);}
            catch(ActivityNotFoundException noPicker){info("Abrir imagen","No hay un selector de imágenes disponible en este dispositivo.");}
        }
    }
    @Override protected void onActivityResult(int request,int result,Intent data){super.onActivityResult(request,result,data);if(request==OPEN_IMAGE&&result==RESULT_OK&&data!=null&&data.getData()!=null)app.velyntora.editor.EditorNavigator.open(this,data.getData());}
    public void showRecentProjects(){info("Proyectos recientes","Todavía no hay proyectos guardados en esta versión piloto.");}
    public void showPreferences(){info("Preferencias","Las preferencias estarán disponibles en una próxima etapa del prototipo.");}
    public void showHelp(){info("Velyntora Drawing · Versión piloto","Versión piloto con el motor de Velyntora Core integrado. Crea ilustraciones y edita imágenes.\n\nPowered By Zaskar Startup");}
}
