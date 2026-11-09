package art.velyntora.core;

import android.app.AlertDialog;

/** All save/export destinations belong to the toolbar arrow, never the File menu. */
final class SaveMenu {
  private final MainActivity host;
  SaveMenu(MainActivity host){this.host=host;}
  AlertDialog show(){
    if(host.readProjectProgress()!=null){host.message("Espera a que termine la operación actual");return null;}
    if(host.readDrawing().placedImage().active()){host.message("Confirma o cancela la imagen o el texto antes de guardar");return null;}
    String[] labels={"Guardar proyecto","Exportar PNG","Exportar PDF","Imprimir…","Más formatos"};
    String[] notes={"Conserva las capas para seguir editando (.vlycore).",
      "Crea una imagen para compartir.","Crea un documento PDF.","Abre las opciones de impresión de Android.",
      "JPEG, WebP, OpenRaster y otros formatos."};
    Runnable[] actions={()->host.projectPicker(true),host::savePng,host::savePdf,host::printDocument,this::showOtherFormats};
    return choose("Guardar y exportar",labels,notes,actions);
  }

  AlertDialog showOtherFormats(){
    String[] labels={"Guardar OpenRaster","Guardar JPEG","Guardar WebP","Guardar BMP","Guardar TGA",
      "Guardar TIFF","Guardar GIF (imagen fija)","Guardar ICO (hasta 256 px)","Guardar PPM"};
    Runnable[] actions={()->host.openRasterPicker(true),
      ()->host.saveImage("image/jpeg","dibujo.jpg",host.readSAVE_JPEG()),
      ()->host.saveImage("image/webp","dibujo.webp",host.readSAVE_WEBP()),
      ()->host.saveImage("image/bmp","dibujo.bmp",host.readSAVE_BMP()),
      ()->host.saveImage("image/x-tga","dibujo.tga",host.readSAVE_TGA()),
      ()->host.saveImage("image/tiff","dibujo.tiff",host.readSAVE_TIFF()),
      ()->{host.message("GIF: colores reducidos y transparencia sin semitransparencias");host.saveImage("image/gif","dibujo.gif",host.readSAVE_GIF());},
      ()->host.saveImage("image/vnd.microsoft.icon","dibujo.ico",host.readSAVE_ICO()),
      ()->host.saveImage("image/x-portable-pixmap","dibujo.ppm",host.readSAVE_PPM())
    };
    return choose("Más formatos",labels,null,actions);
  }

  private AlertDialog choose(String title,String[] labels,String[] notes,Runnable[] actions){
    return new AlertDialog.Builder(host).setTitle(title)
      .setAdapter(new MenuIconAdapter(host,labels,"Imprimir…",PrintIcon.load(host),notes),(dialog,index)->{
        if(host.readProjectProgress()==null)actions[index].run();
      }).setNegativeButton("Cancelar",null).show();
  }
}
