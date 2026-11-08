package art.velyntora.core;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;

/** Owns app/workspace behavior; the host is the workspace composition facade. */
final class WorkspaceComposition {
  private final MainActivity host;
  private CommandBar commandBar;
  private PhoneWorkspace phone;
  private LinearLayout topOptions, phoneOptions, desktopPalette;
  private boolean phoneLayout;
  boolean closePhonePanel(){return phone!=null&&phone.closePanel();}
  void refreshPhoneTools(){if(phone!=null)phone.sync();}

  WorkspaceComposition(MainActivity host) { this.host = host; }

  void onCreate(Bundle state) {
    
    boolean freshProcess = !DrawingView.hasDocument();
    host.writeDrawing(new DrawingView(host));
    if (host.readSavedRevision() < 0 && freshProcess) host.writeSavedRevision(host.readDrawing().revision());
    host.readDrawing().setOnTextPositionListener(host::configureText);
    host.readDrawing().setOnColorPickedListener(
        color -> {
          host.setActiveColor(color);
          host.message("Color seleccionado");
        });
    host.readDrawing().setOnCanvasChangedListener(
        () -> {
          host.syncToolState();
          host.updateStatus();
          host.readLayerRefreshHandler().removeCallbacks(host.readLayerRefreshTask());
          host.readLayerRefreshHandler().postDelayed(host.readLayerRefreshTask(), 120);
        });
    LinearLayout root = new LinearLayout(host);
    root.setOrientation(LinearLayout.VERTICAL);
    root.setBackgroundColor(0xFF2B2B2B);
    host.getWindow().setStatusBarColor(0xFF202020);
    host.getWindow().setNavigationBarColor(0xFF202020);

    host.composeWorkspaceMenus();

    commandBar=new CommandBar(host);
    commandBar.create(root);
    topOptions=column();
    root.addView(topOptions);
    host.composeToolControls(topOptions);
    host.composeLayersPanel(root);
    host.refreshLayerPanel();
    host.writeWorkspaceFrame(new FrameLayout(host));
    root.addView(host.readWorkspaceFrame(),new LinearLayout.LayoutParams(-1,0,1));
    phone=new PhoneWorkspace(host);
    root.addView(phone.history);
    phoneOptions=column();
    root.addView(phoneOptions);
    root.addView(phone.navigation);
    desktopPalette=column();
    root.addView(desktopPalette);
    host.composeColorPanel(desktopPalette);
    host.composeStatusBar(root);
    host.layoutWorkspace();
    host.setContentView(root);
    host.syncToolState();
    host.updateStatus();
    if (freshProcess && !EditorEntry.hasRequest(host.getIntent())) host.recoverDocument();
  }

  private LinearLayout column(){
    LinearLayout view=new LinearLayout(host);
    view.setOrientation(LinearLayout.VERTICAL);
    return view;
  }

  void updateWorkspaceChrome() {
    if(host.readStatus()!=null)host.readStatus().setVisibility(phoneLayout?View.GONE:View.VISIBLE);
  }

  void togglePanel(ScrollView panel) {
    if (panel == null) return;
    if(phone!=null&&phone.toggle(panel,panel==host.readLayerScroll()?"Capas":"Más herramientas"))return;
    boolean show = panel.getVisibility() != View.VISIBLE;
    if (show && !host.readPanelsInline()) {
      host.readToolScroll().setVisibility(View.GONE);
      host.readLayerScroll().setVisibility(View.GONE);
    }
    panel.setVisibility(show ? View.VISIBLE : View.GONE);
  }

  void detach(View view) {
    if (view.getParent() instanceof android.view.ViewGroup)
      ((android.view.ViewGroup) view.getParent()).removeView(view);
  }

  void layoutWorkspace() {
    android.content.res.Configuration config = host.getResources().getConfiguration();
    WorkspaceLayout layout =
        new WorkspaceLayout(
            Math.max(1, config.screenWidthDp),
            Math.max(1, config.screenHeightDp),
            config.fontScale);
    phoneLayout=layout.phone;
    phone.layout(phoneLayout,host.readWorkspaceFrame(),config.screenHeightDp);
    commandBar.update(phoneLayout);
    host.layoutToolOptions(layout,config.screenWidthDp);
    View options=host.toolOptionsView();
    host.detach(options);
    (phoneLayout?phoneOptions:topOptions).addView(options,new LinearLayout.LayoutParams(-1,-2));
    host.detach(host.readPaletteScroll());
    desktopPalette.setVisibility(phoneLayout?View.GONE:View.VISIBLE);
    if(!phoneLayout){
      desktopPalette.addView(host.readPaletteScroll(),new LinearLayout.LayoutParams(-1,-2));
      host.readPaletteScroll().setVisibility(View.VISIBLE);
    }
    host.writePanelsInline(layout.inline);
    host.updateWorkspaceChrome();
    host.detach(host.readDrawing());
    host.detach(host.readToolScroll());
    host.detach(host.readLayerScroll());
    host.readWorkspaceFrame().removeAllViews();
    if(phoneLayout){
      host.readWorkspaceFrame().addView(host.readDrawing(),new FrameLayout.LayoutParams(-1,-1));
      host.readToolScroll().setVisibility(View.GONE);
      host.readLayerScroll().setVisibility(View.GONE);
    } else if (host.readPanelsInline()) {
      LinearLayout row = host.row();
      row.addView(host.readToolScroll(), new LinearLayout.LayoutParams(host.dp(layout.toolsWidth), -1));
      row.addView(host.readDrawing(), new LinearLayout.LayoutParams(0, -1, 1));
      row.addView(host.readLayerScroll(), new LinearLayout.LayoutParams(host.dp(layout.layersWidth), -1));
      host.readWorkspaceFrame().addView(row, new android.widget.FrameLayout.LayoutParams(-1, -1));
      host.readToolScroll().setVisibility(View.VISIBLE);
      host.readLayerScroll().setVisibility(View.VISIBLE);
    } else {
      host.readWorkspaceFrame().addView(host.readDrawing(), new android.widget.FrameLayout.LayoutParams(-1, -1));
      host.readWorkspaceFrame().addView(
          host.readToolScroll(),
          new android.widget.FrameLayout.LayoutParams(host.dp(layout.toolsWidth), -1, Gravity.START));
      host.readWorkspaceFrame().addView(
          host.readLayerScroll(),
          new android.widget.FrameLayout.LayoutParams(host.dp(layout.layersWidth), -1, Gravity.END));
      host.readToolScroll().setVisibility(View.GONE);
      host.readLayerScroll().setVisibility(View.GONE);
    }
    host.readToolScroll().setBackgroundColor(0xFF2B2B2B);
    host.readLayerScroll().setBackgroundColor(0xFF202020);
    host.readToolScroll().setElevation(host.dp(6));
    host.readLayerScroll().setElevation(host.dp(6));
  }
}
