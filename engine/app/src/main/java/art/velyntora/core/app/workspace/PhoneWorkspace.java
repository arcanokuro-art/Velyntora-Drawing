package art.velyntora.core;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

/** Phone controls reuse the editor's tool state, palette, layers and history. */
final class PhoneWorkspace {
  private final MainActivity host;
  final LinearLayout navigation, history;
  private final LinearLayout tools, placement;
  private final Button brush, eraser, color;
  private FrameLayout frame, overlay;
  private View openPanel;
  private boolean enabled;
  private int heightDp, lastColor;
  private boolean hasColor, lastPlacement;

  PhoneWorkspace(MainActivity host) {
    this.host=host;
    navigation=new LinearLayout(host);
    navigation.setOrientation(LinearLayout.VERTICAL);
    navigation.setBackgroundColor(0xff242424);
    tools=host.row();navigation.addView(tools);
    brush=button(tools,"Pincel",Tools.icon(DrawingView.BRUSH,host),()->select(DrawingView.BRUSH));
    brush.setOnLongClickListener(anchor->{
      if(blocked())return true;
      select(DrawingView.BRUSH);
      BrushSelector.show(anchor,host.readDrawing(),host::syncToolState);
      return true;
    });
    eraser=button(tools,"Borrador",Tools.icon(DrawingView.ERASER,host),()->select(DrawingView.ERASER));
    color=button(tools,"Color",null,()->toggle(host.readPaletteScroll(),"Color"));
    button(tools,"Capas",new WorkspaceIconDrawable("layers"),()->toggle(host.readLayerScroll(),"Capas"));
    button(tools,"Más",new WorkspaceIconDrawable("more"),()->toggle(host.readToolScroll(),"Más herramientas"));

    placement=host.row();navigation.addView(placement);
    PlacedImageSession session=host.readDrawing().placedImage();
    button(placement,"Girar o voltear",new WorkspaceIconDrawable("redo"),()->PlacedImageControls.showOrientation(host,session));
    Button cancel=button(placement,"Cancelar imagen",new WorkspaceIconDrawable("cancel"),session::cancel);
    cancel.setText("Cancelar");
    Button confirm=button(placement,"Confirmar imagen",new WorkspaceIconDrawable("confirm"),session::confirm);
    confirm.setText("Confirmar");

    history=host.row();
    history.setBackgroundColor(0xff242424);
    host.iconButton(history,"Deshacer","undo",host::undo);
    host.iconButton(history,"Rehacer","redo",host::redo);
    history.addView(new View(host),new LinearLayout.LayoutParams(0,1,1));
    host.iconButton(history,"Ajustar a pantalla","fit",host.readDrawing()::fitCanvas);
  }

  private Button button(LinearLayout row,String label,Drawable icon,Runnable action){
    Button button=host.button(row,label,action);
    button.setTextSize(11);
    button.setMaxLines(2);
    button.setPadding(host.dp(2),host.dp(3),host.dp(2),host.dp(3));
    button.setCompoundDrawablePadding(host.dp(2));
    button.setGravity(Gravity.CENTER);
    button.setLayoutParams(new LinearLayout.LayoutParams(0,host.dp(56),1));
    android.graphics.drawable.StateListDrawable background=new android.graphics.drawable.StateListDrawable();
    background.addState(new int[]{android.R.attr.state_pressed},surface(0xff3a5251));
    background.addState(new int[]{android.R.attr.state_selected},surface(0xff305451));
    background.addState(new int[]{},surface(0xff242424));
    button.setBackground(background);
    if(icon!=null)setIcon(button,icon);
    return button;
  }

  private GradientDrawable surface(int color){
    GradientDrawable shape=new GradientDrawable();
    shape.setColor(color);shape.setCornerRadius(host.dp(12));
    return shape;
  }

  private void setIcon(Button button,Drawable icon){
    icon.setBounds(0,0,host.dp(22),host.dp(22));
    button.setCompoundDrawables(null,icon,null,null);
  }

  private boolean blocked(){return host.readProjectProgress()!=null||host.readDrawing().placedImage().active();}

  private void select(int tool){
    if(blocked())return;
    closePanel();
    host.readDrawing().setTool(tool);
    host.readDrawing().setColor(host.readActiveColor());
    host.syncToolState();
  }

  void layout(boolean enabled,FrameLayout frame,int heightDp){
    closePanel();
    this.enabled=enabled;this.frame=frame;this.heightDp=heightDp;
    navigation.setVisibility(enabled?View.VISIBLE:View.GONE);
    history.setVisibility(enabled?View.VISIBLE:View.GONE);
    sync();
  }

  void sync(){
    if(!enabled)return;
    boolean placing=host.readDrawing().placedImage().active();
    if(placing!=lastPlacement){closePanel();lastPlacement=placing;}
    tools.setVisibility(placing?View.GONE:View.VISIBLE);
    placement.setVisibility(placing?View.VISIBLE:View.GONE);
    int active=host.readDrawing().currentTool();
    brush.setSelected(active==DrawingView.BRUSH);
    eraser.setSelected(active==DrawingView.ERASER);
    int value=host.readActiveColor();
    if(!hasColor||lastColor!=value){
      hasColor=true;lastColor=value;
      GradientDrawable swatch=new GradientDrawable();swatch.setShape(GradientDrawable.OVAL);
      swatch.setColor(value);swatch.setStroke(host.dp(1),0xffeeeeee);
      setIcon(color,swatch);
    }
  }

  boolean toggle(View panel,String title){
    if(!enabled)return false;
    if(openPanel==panel){closePanel();return true;}
    if(blocked())return true;
    closePanel();
    overlay=new FrameLayout(host);
    View scrim=new View(host);scrim.setBackgroundColor(0x66000000);
    scrim.setOnClickListener(v->closePanel());
    overlay.addView(scrim,new FrameLayout.LayoutParams(-1,-1));
    LinearLayout sheet=new LinearLayout(host);sheet.setOrientation(LinearLayout.VERTICAL);
    sheet.setBackgroundColor(0xff282828);sheet.setClickable(true);
    LinearLayout header=host.row();
    TextView sheetTitle=host.text(title);sheetTitle.setTextSize(16);
    sheetTitle.setPadding(host.dp(16),0,0,0);
    header.addView(sheetTitle,new LinearLayout.LayoutParams(0,-2,1));
    host.iconButton(header,"Cerrar panel","cancel",this::closePanel);
    sheet.addView(header);
    openPanel=panel;
    host.detach(panel);panel.setVisibility(View.VISIBLE);
    int body=panel==host.readPaletteScroll()?-2:host.dp(Math.min(360,Math.max(96,heightDp/2-48)));
    sheet.addView(panel,new LinearLayout.LayoutParams(-1,body));
    overlay.addView(sheet,new FrameLayout.LayoutParams(-1,-2,Gravity.BOTTOM));
    frame.addView(overlay,new FrameLayout.LayoutParams(-1,-1));
    host.readDrawing().setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS);
    return true;
  }

  boolean closePanel(){
    if(openPanel==null)return false;
    host.detach(openPanel);openPanel.setVisibility(View.GONE);openPanel=null;
    host.detach(overlay);overlay=null;
    host.readDrawing().setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_AUTO);
    return true;
  }
}
