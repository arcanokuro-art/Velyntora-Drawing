package art.velyntora.core;

import android.widget.LinearLayout;
import android.app.AlertDialog;

/** File creation and opening; save/export destinations live in components/saving. */
final class FileMenu {
  private final MainActivity host;
  FileMenu(MainActivity host){this.host=host;}
  void populate(LinearLayout menus){
    host.button(menus,"Archivo",this::show);
  }
  AlertDialog show(){
    String[] labels={
      "Nuevo…","Abrir archivo (detección automática)","Insertar imagen como capa…",
      "Abrir TGA","Abrir TIFF","Abrir ICO","Abrir Netpbm (PBM/PGM/PPM)",
      "Abrir OpenRaster","Abrir proyecto"
    };
    Runnable[] actions={
      ()->host.protectDocument(()->host.configureDimensions(0)),host::openImage,
      ()->host.readDrawing().placedImage().pick(),host::openTga,host::openTiff,host::openIco,host::openPpm,
      ()->host.openRasterPicker(false),()->host.projectPicker(false)
    };
    return new AlertDialog.Builder(host).setTitle("Archivo").setItems(labels,(dialog,index)->{
      if(host.readProjectProgress()==null)actions[index].run();
    }).show();
  }
}
