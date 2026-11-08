package art.velyntora.core;

import android.graphics.*;

/** Image-local crop and document-space transform, independent from the viewport. */
final class PlacedImageTransform {
  RectF crop;
  float cx,cy,width,height,angle,opacity=1;
  boolean flippedX,flippedY;
  PlacedImageTransform(int w,int h,int canvasW,int canvasH){
    crop=new RectF(0,0,w,h);cx=canvasW/2f;cy=canvasH/2f;
    float fit=Math.min(1,Math.min(canvasW*.75f/w,canvasH*.75f/h));width=w*fit;height=h*fit;
  }
  PlacedImageTransform copy(){PlacedImageTransform t=new PlacedImageTransform(1,1,1,1);t.crop=new RectF(crop);t.cx=cx;t.cy=cy;t.width=width;t.height=height;t.angle=angle;t.opacity=opacity;t.flippedX=flippedX;t.flippedY=flippedY;return t;}
  float[] values(){return new float[]{crop.left,crop.top,crop.right,crop.bottom,cx,cy,flippedX?-width:width,flippedY?-height:height,angle};}
  void restore(float[] v){if(v==null || v.length!=9)throw new IllegalArgumentException("Image transform");crop=new RectF(v[0],v[1],v[2],v[3]);cx=v[4];cy=v[5];flippedX=v[6]<0;flippedY=v[7]<0;width=Math.abs(v[6]);height=Math.abs(v[7]);angle=v[8];}
  Matrix matrix(){
    Matrix m=new Matrix();m.setTranslate(-crop.centerX(),-crop.centerY());
    m.postScale((flippedX?-width:width)/crop.width(),(flippedY?-height:height)/crop.height());m.postRotate(angle);m.postTranslate(cx,cy);return m;
  }
  // Handles follow geometric sides, independent of how the source is mirrored.
  float[] handles(){
    float w=width/2,h=height/2;
    float[] points={-w,-h,0,-h,w,-h,w,0,w,h,0,h,-w,h,-w,0};
    Matrix frame=new Matrix();frame.setRotate(angle);frame.postTranslate(cx,cy);
    frame.mapPoints(points);return points;
  }
  void flip(boolean horizontal){
    // Reflect around a document-space axis through the object center.
    angle=-angle;
    if(horizontal)flippedX=!flippedX;else flippedY=!flippedY;
  }
  void cropTo(RectF next){
    float[] shift={next.centerX(),next.centerY()};matrix().mapPoints(shift);
    cx=shift[0];cy=shift[1];width*=next.width()/crop.width();height*=next.height()/crop.height();crop=new RectF(next);
  }
  boolean contains(float x,float y){Matrix inverse=new Matrix();if(!matrix().invert(inverse))return false;float[] p={x,y};inverse.mapPoints(p);return crop.contains(p[0],p[1]);}
}
