package app.velyntora.ui.start;

import android.content.Context;
import android.widget.*;
import static app.velyntora.ui.common.Ui.*;

/** Paths for returning to saved work or learning the editor. */
final class RecentProjectsPanel {
    static LinearLayout create(Context context,StartActions actions){
        LinearLayout panel=column(context);
        panel.setPadding(dp(context,16),dp(context,12),dp(context,16),dp(context,12));
        panel.setBackground(background(PANEL));
        panel.addView(text(context,"Continuar un dibujo",16,true));
        TextView detail=text(context,"Abre un proyecto .vlycore u .ora para seguir editando sus capas.",14,false);
        detail.setTextColor(MUTED);
        panel.addView(detail);
        addAction(context,panel,"Abrir proyecto",actions::openProject);
        addAction(context,panel,"Cómo empezar",actions::showHelp);
        return panel;
    }
    private static void addAction(Context c,LinearLayout panel,String title,Runnable action){
        LinearLayout.LayoutParams size=new LinearLayout.LayoutParams(-1,dp(c,48));
        size.topMargin=dp(c,8);
        panel.addView(button(c,title,false,action),size);
    }
}
