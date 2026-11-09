package art.velyntora.core;

import android.app.Activity;
import android.app.AlertDialog;

/** Beginner instructions shared by Home and the editor. */
public final class QuickStartGuide {
  private QuickStartGuide() {}

  public static void show(Activity host){
    new AlertDialog.Builder(host).setTitle("Cómo empezar").setMessage(
        "1. Abre un lienzo\n"
        + "En Inicio, toca Crear ilustración para dibujar desde cero, Abrir imagen para editar una foto "
        + "o Abrir proyecto para continuar un dibujo guardado.\n\n"
        + "2. Dibuja y cambia de herramienta\n"
        + "En celular, Pincel, Borrador, Color y Capas están abajo. Toca Más para ver las demás herramientas. "
        + "En tablet o PC, busca las herramientas a la izquierda, los colores abajo y las capas a la derecha.\n\n"
        + "3. Guarda tu trabajo\n"
        + "Toca la flecha ↓ (Guardar). Usa Guardar proyecto para conservar tus capas y continuar editando. "
        + "Usa Exportar PNG para compartir una imagen. Encontrarás PDF, impresión y otros formatos en ese mismo menú.")
        .setPositiveButton("Entendido",null).show();
  }
}
