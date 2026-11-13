package app.velyntora.ui.start;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import static app.velyntora.ui.common.Ui.*;
public final class StartScreen {
    private final Context context;private final StartActions actions;
    public StartScreen(Context context,StartActions actions){this.context=context;this.actions=actions;}
    public View create(){
        Context c=context;
        android.content.res.Configuration config=c.getResources().getConfiguration();
        boolean wide=config.screenWidthDp>=700&&(config.screenHeightDp>=600||config.screenWidthDp>=1000);
        if(!wide)return PhoneStartScreen.create(c,actions);
        LinearLayout root=new LinearLayout(c);root.setOrientation(LinearLayout.HORIZONTAL);root.setBackgroundColor(BG);
        View hero=DrawingHero.create(c,actions,false);
        root.addView(hero,new LinearLayout.LayoutParams(0,-1,0.36f));
        ScrollView scroll=new ScrollView(c);scroll.setFillViewport(true);
        LinearLayout right=column(c);right.setPadding(dp(c,24),dp(c,24),dp(c,24),dp(c,24));
        ImageView logo=new ImageView(c);logo.setImageBitmap(app.velyntora.ui.common.BrandLogo.load(c));logo.setScaleType(ImageView.ScaleType.FIT_CENTER);right.addView(logo,new LinearLayout.LayoutParams(-1,dp(c,64)));
        TextView drawing=text(c,"Drawing",22,true);drawing.setGravity(Gravity.CENTER);right.addView(drawing);
        TextView pilot=text(c,"VERSIÓN PILOTO · Prototipo en desarrollo",13,false);pilot.setTextColor(MUTED);pilot.setGravity(Gravity.CENTER);pilot.setPadding(0,dp(c,10),0,dp(c,24));right.addView(pilot);
        right.addView(RecentProjectsPanel.create(c,actions),new LinearLayout.LayoutParams(-1,-2));
        scroll.addView(right);
        root.addView(scroll,new LinearLayout.LayoutParams(0,-1,0.64f));
        return root;
    }
}
