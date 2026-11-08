package art.velyntora.core;

import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

final class CommandBar {
  private final MainActivity host;
  private HorizontalScrollView shortcuts;
  private Button save, tools, layers, phoneSave;
  private LinearLayout leading, trailing;
  CommandBar(MainActivity host) {this.host=host;}

  void create(LinearLayout root) {
    int height=host.dp(48);
    LinearLayout header=host.row();
    leading=host.row();
    host.iconButton(leading,"Menú principal","menu",this::openMenu);
    LinearLayout commands=host.row();
    host.iconButton(commands,"Nuevo","new",()->host.protectDocument(()->host.configureDimensions(0)));
    host.iconButton(commands,"Abrir","open",host::openImage);
    save=host.iconButton(commands,"Guardar","save",host::openSaveMenu);
    host.iconButton(commands,"Deshacer","undo",host::undo);
    host.iconButton(commands,"Rehacer","redo",host::redo);
    shortcuts=new HorizontalScrollView(host);
    shortcuts.setHorizontalScrollBarEnabled(false);
    shortcuts.addView(commands);
    leading.addView(shortcuts,new LinearLayout.LayoutParams(0,height,1));
    header.addView(leading,new LinearLayout.LayoutParams(0,height,2));
    host.writeDocumentTitle(host.text("Imagen no guardada — Velyntora Drawing"));
    host.readDocumentTitle().setSingleLine(true);
    host.readDocumentTitle().setEllipsize(android.text.TextUtils.TruncateAt.END);
    host.readDocumentTitle().setGravity(Gravity.CENTER);
    header.addView(host.readDocumentTitle(),new LinearLayout.LayoutParams(0,height,1));
    trailing=host.row();
    trailing.setGravity(Gravity.END|Gravity.CENTER_VERTICAL);
    tools=host.iconButton(trailing,"Herramientas","tools",()->host.togglePanel(host.readToolScroll()));
    layers=host.iconButton(trailing,"Capas","layers",()->host.togglePanel(host.readLayerScroll()));
    phoneSave=host.iconButton(trailing,"Guardar en celular","save",host::openSaveMenu);
    header.addView(trailing,new LinearLayout.LayoutParams(0,height,2));
    root.addView(header);
  }

  void update(boolean phone) {
    int height=host.dp(48);
    for(LinearLayout side:new LinearLayout[]{leading,trailing})
      side.setLayoutParams(phone?new LinearLayout.LayoutParams(height,height):new LinearLayout.LayoutParams(0,height,2));
    shortcuts.setVisibility(phone?View.GONE:View.VISIBLE);
    save.setContentDescription(phone?"Guardar (barra amplia)":"Guardar");
    phoneSave.setContentDescription(phone?"Guardar":"Guardar en celular");
    tools.setVisibility(phone?View.GONE:View.VISIBLE);
    layers.setVisibility(phone?View.GONE:View.VISIBLE);
    phoneSave.setVisibility(phone?View.VISIBLE:View.GONE);
  }

  private void openMenu() {
    if(shortcuts.getVisibility()==View.VISIBLE){host.openMainMenu();return;}
    MenuChoices.show(host,"Velyntora Drawing",new String[]{"Nueva ilustración","Abrir imagen","Menú completo"},
        ()->host.protectDocument(()->host.configureDimensions(0)),host::openImage,host::openMainMenu);
  }
}
