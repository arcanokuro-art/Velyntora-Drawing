package art.velyntora.core;

import android.app.AlertDialog;
import android.widget.LinearLayout;

final class FileMenu {
  private final MainActivity host;
  FileMenu(MainActivity host) { this.host = host; }
  void populate(LinearLayout menus) { host.button(menus, "Archivo", this::show); }
  AlertDialog show() {
    return MenuChoices.show(host, "Archivo", new String[]{
      "Insertar imagen como capa…", "Abrir en formato específico…"
    }, () -> host.readDrawing().placedImage().pick(), this::openFormats);
  }
  AlertDialog openFormats() {
    return MenuChoices.show(host, "Abrir en formato específico", new String[]{
      "TGA", "TIFF", "ICO", "Netpbm (PBM/PGM/PPM)", "OpenRaster (.ora)", "Proyecto Velyntora (.vlycore)"
    }, host::openTga, host::openTiff, host::openIco, host::openPpm,
      () -> host.openRasterPicker(false), () -> host.projectPicker(false));
  }
}
