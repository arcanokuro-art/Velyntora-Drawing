package art.velyntora.core;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.*;
import android.widget.*;
/** Standard menu rows with an optional icon; equal text alignment for every item. */
final class MenuIconAdapter extends ArrayAdapter<String> {
  private final String iconLabel;private final Drawable icon;
  MenuIconAdapter(Context context,String[] labels,String iconLabel,Drawable icon){super(context,android.R.layout.simple_list_item_1,labels);this.iconLabel=iconLabel;this.icon=icon;}
  private int dp(int value){return Math.round(value*getContext().getResources().getDisplayMetrics().density);}
  @Override public View getView(int position,View recycled,ViewGroup parent){
    LinearLayout row=new LinearLayout(getContext());row.setGravity(Gravity.CENTER_VERTICAL);row.setMinimumHeight(dp(48));row.setPadding(dp(20),dp(10),dp(20),dp(10));
    ImageView picture=new ImageView(getContext());picture.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
    String label=getItem(position);if(iconLabel.equals(label))picture.setImageDrawable(icon);else picture.setVisibility(View.INVISIBLE);
    row.addView(picture,new LinearLayout.LayoutParams(dp(28),dp(28)));
    TextView text=new TextView(getContext());text.setTextAppearance(android.R.style.TextAppearance_Material_Subhead);text.setText(label);
    LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(0,-2,1);params.leftMargin=dp(14);row.addView(text,params);return row;
  }
}
