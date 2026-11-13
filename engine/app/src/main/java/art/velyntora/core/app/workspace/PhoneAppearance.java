package art.velyntora.core;

import android.content.res.ColorStateList;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;

final class PhoneAppearance {
  static final int BACKGROUND=0xff13161c, SURFACE=0xff20252e, BORDER=0xff353d49;
  static final int TEXT=0xfff1f4fa, MUTED=0xffb3bfce, ACCENT=0xff9ce8d5;
  private PhoneAppearance(){}
  static GradientDrawable surface(MainActivity host,int color,int radius,boolean outline){
    GradientDrawable shape=new GradientDrawable();
    shape.setColor(color);shape.setCornerRadius(host.dp(radius));
    if(outline)shape.setStroke(host.dp(1),BORDER);
    return shape;
  }
  static RippleDrawable action(MainActivity host,boolean prominent){
    return new RippleDrawable(ColorStateList.valueOf(0x3389ebd7),
        surface(host,prominent?0xff29493f:SURFACE,14,false),null);
  }
}
