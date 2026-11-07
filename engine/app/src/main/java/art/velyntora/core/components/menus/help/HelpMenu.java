package art.velyntora.core;

import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.widget.LinearLayout;

final class HelpMenu {
  private final MainActivity host;
  HelpMenu(MainActivity host) { this.host = host; }

  void populate(LinearLayout menus) {
    host.button(menus, "Ayuda", () -> MenuChoices.show(host, "Ayuda",
      new String[]{"Guía rápida", "Atajos de teclado", "Acerca de Velyntora Drawing"},
      () -> info("Guía rápida",
        "Empieza con Nuevo o Abrir en la barra superior.\n\n"
        + "Elige una herramienta en el panel lateral. Sus ajustes aparecen junto al lienzo, en la barra de opciones. "
        + "Toca los colores de la paleta para cambiarlos y usa el icono de capas para organizarlas.\n\n"
        + "Usa ≡ para editar selecciones, cambiar el tamaño del lienzo, ajustar colores o aplicar efectos.\n\n"
        + "La flecha ↓ abre Guardar y exportar. Guarda un proyecto .vlycore para conservarlo editable; "
        + "usa PNG, PDF u otro formato para compartirlo."),
      () -> info("Atajos de teclado",
        "Ctrl+N — Nuevo\nCtrl+O — Abrir\nCtrl+S — Guardar proyecto\nCtrl+P — Imprimir\n\n"
        + "Ctrl+Z — Deshacer\nCtrl+Y / Ctrl+Mayús+Z — Rehacer\n\n"
        + "Ctrl+A — Seleccionar todo\nCtrl+D — Deseleccionar\n"
        + "Ctrl+C — Copiar\nCtrl+X — Cortar\nCtrl+V — Pegar"),
      this::about));
  }

  private void about() {
    String version = null;
    try {
      version = host.getPackageManager().getPackageInfo(host.getPackageName(), 0).versionName;
    } catch (PackageManager.NameNotFoundException ignored) {}
    info("Acerca de Velyntora Drawing",
        (version == null ? "" : "Versión " + version + "\n\n") + "Aplicación de dibujo para Android.\nDesarrollada por Zaskar.");
  }

  private void info(String title, String message) {
    new AlertDialog.Builder(host).setTitle(title).setMessage(message).setPositiveButton("Cerrar", null).show();
  }
}
