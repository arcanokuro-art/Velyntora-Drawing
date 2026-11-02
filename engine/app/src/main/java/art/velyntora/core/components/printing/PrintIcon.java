package art.velyntora.core;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import java.io.InputStream;
/** User-provided asset, without tinting, background replacement or regeneration. */
public final class PrintIcon {
  public static final String ASSET="printing/impresora.webp";
  public static Drawable load(Context context){
    try{
      BitmapFactory.Options bounds=new BitmapFactory.Options();bounds.inJustDecodeBounds=true;
      try(InputStream input=context.getAssets().open(ASSET)){BitmapFactory.decodeStream(input,null,bounds);}
      BitmapFactory.Options options=new BitmapFactory.Options();options.inSampleSize=1;
      int target=Math.max(32,Math.round(32*context.getResources().getDisplayMetrics().density));
      while(Math.max(bounds.outWidth,bounds.outHeight)/(options.inSampleSize*2)>=target)options.inSampleSize*=2;
      Bitmap bitmap;
      try(InputStream input=context.getAssets().open(ASSET)){bitmap=BitmapFactory.decodeStream(input,null,options);}
      if(bitmap==null)throw new IllegalStateException("Icono de impresión no válido");
      BitmapDrawable icon=new BitmapDrawable(context.getResources(),bitmap);icon.setFilterBitmap(true);return icon;
    }catch(java.io.IOException error){throw new IllegalStateException("Falta el icono de impresión",error);}
  }
  private PrintIcon(){}
}
