package art.velyntora.core;

import android.content.pm.PackageManager;
import android.widget.LinearLayout;

final class HelpMenu {
  private final MainActivity host;

  HelpMenu(MainActivity host) {
    this.host = host;
  }

  void populate(LinearLayout menus) {
    host.button(menus, "Ayuda", this::show);
  }

  private void show() {
    MenuChoices.show(host, "Ayuda", new String[]{
        "Cómo empezar", "Conoce el editor", "Herramientas",
        "Guardar, exportar e imprimir", "Solución de problemas", "Manual y acerca de"
    }, () -> QuickStartGuide.show(host, this::show), this::editor, this::tools,
        () -> article("Guardar, exportar e imprimir", HelpContent.SAVING, this::show),
        this::troubleshooting, this::about);
  }

  private void editor() {
    MenuChoices.show(host, "Conoce el editor", new String[]{
        "Celular", "Tablet y modo PC", "Atajos de teclado", "Volver a Ayuda"
    }, () -> article("Celular", HelpContent.PHONE, this::editor),
        () -> article("Tablet y modo PC", HelpContent.WIDE, this::editor),
        () -> article("Atajos de teclado", HelpContent.SHORTCUTS, this::editor),
        this::show);
  }

  private void tools() {
    MenuChoices.show(host, "Herramientas", new String[]{
        "Dibujo y relleno", "Selección y transformación", "Texto y figuras",
        "Capas y color", "Imágenes importadas", "Volver a Ayuda"
    }, () -> article("Dibujo y relleno", HelpContent.PAINT, this::tools),
        () -> article("Selección y transformación", HelpContent.SELECTION, this::tools),
        () -> article("Texto y figuras", HelpContent.SHAPES, this::tools),
        () -> article("Capas y color", HelpContent.LAYERS, this::tools),
        () -> article("Imágenes importadas", HelpContent.IMAGES, this::tools),
        this::show);
  }

  private void troubleshooting() {
    article("Solución de problemas", HelpContent.TROUBLESHOOTING, this::show);
  }

  private void about() {
    String version = "";
    try {
      version = host.getPackageManager().getPackageInfo(host.getPackageName(), 0).versionName;
    } catch (PackageManager.NameNotFoundException ignored) {
      // The help remains readable if Android cannot resolve package metadata.
    }
    String versionLabel = "1.0.0".equals(version) ? "1.0.0 · Reynosa" : version;
    article("Manual y acerca de", "Velyntora Drawing\n"
        + (versionLabel.isEmpty() ? "" : "Versión " + versionLabel + "\n")
        + "Desarrollada por Zaskar Startup.\n\n"
        + "Puedes consultar las guías de este menú sin conexión. "
        + "Para empezar un dibujo, abre Cómo empezar. Para consultar cada herramienta, abre Herramientas.",
        this::show);
  }

  private void article(String title, String message, Runnable back) {
    QuickStartGuide.showArticle(host, title, message, back);
  }
}
