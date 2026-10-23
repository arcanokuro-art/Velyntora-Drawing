package art.velyntora.core;
import java.util.Map;
public final class DesktopToneCurve {public static int[] table(Map<Integer,Integer> points,int channel){ToneCurve curve=new ToneCurve();for(var point:points.entrySet())curve.set(point.getKey(),point.getValue());int[] result=java.util.Arrays.copyOf(curve.table(),257);result[256]=channel;return result;}}
