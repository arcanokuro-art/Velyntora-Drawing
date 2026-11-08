package art.velyntora.core;

import android.view.Gravity;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

final class CommandBar {
 private final MainActivity host;
 CommandBar(MainActivity host) {this.host=host;}

  void create(LinearLayout root) {
    LinearLayout header = host.row();
    int height = host.dp(48);
    LinearLayout leading = host.row();
    host.iconButton(leading, "Menú principal", "menu", host::openMainMenu);
    LinearLayout commands = host.row();
    host.iconButton(commands, "Nuevo", "new", () -> host.protectDocument(() -> host.configureDimensions(0)));
    host.iconButton(commands, "Abrir", "open", host::openImage);
    host.iconButton(commands, "Guardar", "save", host::openSaveMenu);
    host.iconButton(commands, "Deshacer", "undo", host::undo);
    host.iconButton(commands, "Rehacer", "redo", host::redo);
    HorizontalScrollView shortcuts = new HorizontalScrollView(host);
    shortcuts.setHorizontalScrollBarEnabled(false);
    shortcuts.addView(commands);
    leading.addView(shortcuts, new LinearLayout.LayoutParams(0, height, 1));
    // Equal side widths keep the title centered on the full bar.
    header.addView(leading, new LinearLayout.LayoutParams(0, height, 2));
    host.writeDocumentTitle(host.text("Imagen no guardada — Velyntora Drawing"));
    host.readDocumentTitle().setSingleLine(true);
    host.readDocumentTitle().setEllipsize(android.text.TextUtils.TruncateAt.END);
    host.readDocumentTitle().setGravity(Gravity.CENTER);
    header.addView(host.readDocumentTitle(), new LinearLayout.LayoutParams(0, height, 1));
    LinearLayout trailing = host.row();
    trailing.setGravity(Gravity.END | Gravity.CENTER_VERTICAL);
    host.iconButton(trailing, "Herramientas", "tools", () -> host.togglePanel(host.readToolScroll()));
    host.iconButton(trailing, "Capas", "layers", () -> host.togglePanel(host.readLayerScroll()));
    header.addView(trailing, new LinearLayout.LayoutParams(0, height, 2));
    root.addView(header);

  }
}
