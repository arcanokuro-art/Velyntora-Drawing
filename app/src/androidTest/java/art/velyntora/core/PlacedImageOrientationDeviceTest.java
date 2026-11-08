package art.velyntora.core;

import org.junit.Test;
import static org.junit.Assert.assertArrayEquals;

public final class PlacedImageOrientationDeviceTest {
  @Test public void mirrorsRotatedImageInCanvasAxesAndRestoresEditableTransform(){
    PlacedImageTransform original=new PlacedImageTransform(100,50,200,200);
    original.angle=30;
    float[] before=sourcePoint(original);
    for(boolean horizontal:new boolean[]{true,false}){
      PlacedImageTransform mirrored=original.copy();
      mirrored.flip(horizontal);
      float[] expected={horizontal?2*original.cx-before[0]:before[0],
                        horizontal?before[1]:2*original.cy-before[1]};
      assertArrayEquals(expected,sourcePoint(mirrored),0.001f);
      PlacedImageTransform restored=new PlacedImageTransform(100,50,200,200);
      restored.restore(mirrored.values());
      assertArrayEquals(expected,sourcePoint(restored),0.001f);
      restored.flip(horizontal);
      assertArrayEquals(before,sourcePoint(restored),0.001f);
    }
  }

  private static float[] sourcePoint(PlacedImageTransform transform){
    float[] point={12,31};
    transform.matrix().mapPoints(point);
    return point;
  }
}
