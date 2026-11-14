package art.velyntora.core;

import android.app.Activity;
import android.app.AlertDialog;

/** Beginner instructions shared by Home and the editor. */
public final class QuickStartGuide {
  private QuickStartGuide() {}

  public static void show(Activity host) {
    show(host, null);
  }

  static void show(Activity host, Runnable back) {
    showArticle(host, "Cómo empezar",
        "Haz tu primer dibujo\n\n"
        + "1. Desde Inicio, toca Crear ilustración, elige un tamaño y confirma. "
        + "Para practicar, elige un lienzo cuadrado.\n\n"
        + "2. Selecciona Pincel y abre Color para elegir con qué dibujar. "
        + "Traza una línea en el lienzo. Usa Deshacer para retirarla o Borrador para borrar solo una parte.\n\n"
        + "3. Abre Guardar → Guardar proyecto, elige una carpeta y ponle nombre al archivo .vlycore. "
        + "Así podrás seguir trabajando con las capas.\n\n"
        + "4. Cuando quieras continuar, vuelve a Inicio, toca Abrir proyecto y busca ese archivo.\n\n"
        + "¿Ya tienes una imagen? Usa Abrir imagen en Inicio para abrirla en el editor. "
        + "Si quieres añadir una imagen a un dibujo que ya tienes abierto, usa Archivo → Insertar imagen como capa…",
        back);
  }

  static void showArticle(Activity host, String title, String message, Runnable back) {
    AlertDialog.Builder dialog = new AlertDialog.Builder(host)
        .setTitle(title)
        .setMessage(message)
        .setPositiveButton("Cerrar", null);
    if (back != null) {
      dialog.setNeutralButton("Volver", (ignored, which) -> back.run());
    }
    dialog.show();
  }
}
