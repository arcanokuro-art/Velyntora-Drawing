package art.velyntora.core;

import android.app.AlertDialog;

/** All save/export destinations belong to the toolbar arrow, never the File menu. */
final class SaveMenu {
  private final MainActivity host;
  SaveMenu(MainActivity host){this.host=host;}
  AlertDialog show(){
    if(host.readProjectProgress()!=null){host.message("Espera a que termine la operación actual");return null;}
    if(host.readDrawing().placedImage().active()){host.message("Confirma o cancela la imagen o el texto antes de guardar");return null;}
    String[] labels={"Guardar proyecto", "Guardar OpenRaster", "Guardar PNG", "Guardar JPEG",
      "Guardar WebP", "Guardar BMP", "Guardar TGA", "Guardar TIFF", "Guardar GIF (imagen fija)",
      "Guardar ICO (hasta 256 px)", "Guardar PPM", "Guardar PDF", "Imprimir…"};
    Runnable[] actions={
      ()->host.projectPicker(true),()->host.openRasterPicker(true),host::savePng,
      ()->host.saveImage("image/jpeg","dibujo.jpg",host.readSAVE_JPEG()),
      ()->host.saveImage("image/webp","dibujo.webp",host.readSAVE_WEBP()),
      ()->host.saveImage("image/bmp","dibujo.bmp",host.readSAVE_BMP()),
      ()->host.saveImage("image/x-tga","dibujo.tga",host.readSAVE_TGA()),
      ()->host.saveImage("image/tiff","dibujo.tiff",host.readSAVE_TIFF()),
      ()->{host.message("GIF: colores reducidos y transparencia sin semitransparencias");host.saveImage("image/gif","dibujo.gif",host.readSAVE_GIF());},
      ()->host.saveImage("image/vnd.microsoft.icon","dibujo.ico",host.readSAVE_ICO()),
      ()->host.saveImage("image/x-portable-pixmap","dibujo.ppm",host.readSAVE_PPM()),
      host::savePdf,host::printDocument
    };
    return new AlertDialog.Builder(host).setTitle("Guardar y exportar")
      .setAdapter(new MenuIconAdapter(host,labels,"Imprimir…",PrintIcon.load(host)),(dialog,index)->{
        if(host.readProjectProgress()==null)actions[index].run();
      }).setNegativeButton("Cancelar",null).show();
  }
}
