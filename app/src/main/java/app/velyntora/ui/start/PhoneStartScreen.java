package app.velyntora.ui.start;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import static app.velyntora.ui.common.Ui.*;

/** Compact home presents actions before decorative content. */
final class PhoneStartScreen {
    private static final int INK=0xff13161c, CARD=0xff20252e, MINT=0xff9ce8d5;
    private PhoneStartScreen(){}

    static View create(Context c,StartActions actions){
        ScrollView scroll=new ScrollView(c);
        scroll.setFillViewport(true);
        scroll.setBackgroundColor(INK);
        LinearLayout page=column(c);
        page.setPadding(dp(c,20),dp(c,24),dp(c,20),dp(c,20));
        scroll.addView(page);
        ImageView logo=new ImageView(c);
        logo.setImageBitmap(app.velyntora.ui.common.BrandLogo.load(c));
        logo.setScaleType(ImageView.ScaleType.FIT_CENTER);
        logo.setContentDescription("Velyntora");
        page.addView(logo,new LinearLayout.LayoutParams(dp(c,142),dp(c,35)));
        TextView heading=text(c,"Drawing",32,true);
        page.addView(heading);
        TextView detail=text(c,"Tu espacio para dibujar y editar imágenes.",14,false);
        detail.setTextColor(MUTED);page.addView(detail);

        add(c,page,"Crear ilustración",true,actions::createIllustration,18);
        add(c,page,"Abrir imagen",false,actions::openImage,8);
        add(c,page,"Abrir proyecto",false,actions::openProject,8);
        add(c,page,"Cómo empezar",false,actions::showHelp,12);

        ImageView art=new ImageView(c);
        art.setImageResource(app.velyntora.R.drawable.start_drawing);
        art.setScaleType(ImageView.ScaleType.CENTER_CROP);
        art.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
        art.setBackground(shape(c,CARD));art.setClipToOutline(true);
        LinearLayout.LayoutParams cover=new LinearLayout.LayoutParams(-1,dp(c,126));
        cover.topMargin=dp(c,16);page.addView(art,cover);
        TextView credit=text(c,"Versión piloto · Powered by Zaskar Startup",12,false);
        credit.setGravity(Gravity.CENTER);credit.setTextColor(MUTED);
        credit.setPadding(0,dp(c,14),0,0);page.addView(credit);
        return scroll;
    }

    private static void add(Context c,LinearLayout page,String label,boolean primary,Runnable action,int gap){
        Button button=button(c,label,primary,action);
        button.setTextColor(primary?INK:TEXT);
        button.setGravity(Gravity.CENTER_VERTICAL|Gravity.START);
        button.setPadding(dp(c,16),0,dp(c,16),0);
        button.setBackground(new RippleDrawable(ColorStateList.valueOf(0x333c8c7a),
            shape(c,primary?MINT:CARD),null));
        LinearLayout.LayoutParams size=new LinearLayout.LayoutParams(-1,dp(c,52));
        size.topMargin=dp(c,gap);page.addView(button,size);
    }

    private static GradientDrawable shape(Context c,int color){
        GradientDrawable shape=new GradientDrawable();
        shape.setColor(color);shape.setCornerRadius(dp(c,16));return shape;
    }
}
