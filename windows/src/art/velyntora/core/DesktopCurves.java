package art.velyntora.core;
import java.awt.geom.*;
import java.util.*;
public final class DesktopCurves {
 private static CurveDraft draft(java.util.List<Point2D.Double> nodes,Map<Point2D.Double,Float> tensions){CurveDraft c=new CurveDraft(0,0,0,0);int n=nodes.size();c.x=new float[n];c.y=new float[n];c.tension=new float[n];for(int i=0;i<n;i++){c.x[i]=(float)nodes.get(i).x;c.y[i]=(float)nodes.get(i).y;c.tension[i]=tensions.getOrDefault(nodes.get(i),i==0||i==n-1?0f:1f/3f);}return c;}
 public static Path2D path(java.util.List<Point2D.Double> nodes,Map<Point2D.Double,Float> tensions){Path2D path=new Path2D.Double();if(nodes.size()<2)return path;CurveDraft c=draft(nodes,tensions);path.moveTo(c.x[0],c.y[0]);for(int i=0;i<c.x.length-1;i++)path.curveTo(c.x[i]+c.tangent(i,true),c.y[i]+c.tangent(i,false),c.x[i+1]-c.tangent(i+1,true),c.y[i+1]-c.tangent(i+1,false),c.x[i+1],c.y[i+1]);return path;}
 public static int insertIndex(java.util.List<Point2D.Double> nodes,Point2D.Double point,float radius,Map<Point2D.Double,Float> tensions){return nodes.size()<2?-1:draft(nodes,tensions).insert((float)point.x,(float)point.y,radius);}
}
