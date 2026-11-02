package art.velyntora.core;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import java.io.InputStream;
import java.io.OutputStream;



final class ToolsMenu {
 private final MainActivity host;
 ToolsMenu(MainActivity host) {this.host=host;}

  void populate(LinearLayout menus){host.button(menus,"Herramientas",this::show);}

  AlertDialog show(){
    java.util.ArrayList<String> labels=new java.util.ArrayList<>();
    java.util.ArrayList<Runnable> actions=new java.util.ArrayList<>();
    if(BrushSettingsPolicy.available(host.readDrawing().currentTool())){
      labels.add("Configurar pincel");actions.add(host::configureBrush);
    }
    labels.add("Elegir color…");actions.add(host::configureColor);
    labels.add("Color secundario…");actions.add(()->host.configureColor(true));
    labels.add("Intercambiar colores");actions.add(()->{
      int first=host.readActiveColor();host.setActiveColor(host.readSecondaryColor());host.setSecondaryColor(first);
    });
    labels.add("Configurar degradado…");actions.add(host::configureGradient);
    labels.add("Confirmar Línea/Curva");actions.add(host.readDrawing()::confirmCurve);
    labels.add("Cancelar Línea/Curva");actions.add(host.readDrawing()::cancelCurve);
    return new AlertDialog.Builder(host).setTitle("Herramientas").setItems(labels.toArray(new String[0]),(dialog,index)->{
      if(host.readProjectProgress()==null)actions.get(index).run();
    }).show();
  }
}
