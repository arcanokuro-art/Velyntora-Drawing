package app.velyntora.windows.tools;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
/** Java2D rendering adapter for the original Android tool-local vector geometry. */
public abstract class DesktopToolIcon implements Icon {
 protected final Paint p=new Paint();abstract void geometry(Canvas canvas);
 public int getIconWidth(){return 28;}public int getIconHeight(){return 28;}
 public void paintIcon(Component component,Graphics graphics,int x,int y){Graphics2D g=(Graphics2D)graphics.create();g.translate(x,y);g.scale(28/24.,28/24.);g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);p.color=component.getForeground();p.alpha=255;p.width=1.8f;p.style=Paint.Style.STROKE;p.dash=null;geometry(new Canvas(g));g.dispose();}
 final void line(Canvas c,float... xy){Path2D path=new Path2D.Float();path.moveTo(xy[0],xy[1]);for(int i=2;i<xy.length;i+=2)path.lineTo(xy[i],xy[i+1]);c.draw(path,p);}
 final void node(Canvas c,float x,float y){c.drawRect(x-2,y-2,x+2,y+2,p);}
 final void move(Canvas c,float reach){float a=12-reach,b=12+reach;line(c,a,12,b,12);line(c,12,a,12,b);line(c,a+3,9,a,12,a+3,15);line(c,b-3,9,b,12,b-3,15);line(c,9,a+3,12,a,15,a+3);line(c,9,b-3,12,b,15,b-3);}
 protected static final class DashPathEffect{final float[] dash;DashPathEffect(float[] dash,int offset){this.dash=dash;}}
 protected static final class Typeface{static final Font DEFAULT_BOLD=new Font(Font.SANS_SERIF,Font.BOLD,21);}
 protected static final class Paint {enum Style{STROKE,FILL}Style style=Style.STROKE;Color color=Color.DARK_GRAY;int alpha=255;float width=1.8f,textSize=21;float[] dash;Font font=Typeface.DEFAULT_BOLD;void setStyle(Style s){style=s;}void setAlpha(int a){alpha=Math.min(255,a);}void setStrokeWidth(float w){width=w;}void setTextSize(float size){textSize=size;}void setTypeface(Font f){font=f;}void setPathEffect(DashPathEffect d){dash=d==null?null:d.dash;}}
 protected static final class Canvas {private final Graphics2D g;Canvas(Graphics2D g){this.g=g;}void setup(Paint p){g.setColor(new Color(p.color.getRed(),p.color.getGreen(),p.color.getBlue(),p.alpha));g.setStroke(new BasicStroke(p.width,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND,10,p.dash,0));}void draw(Shape s,Paint p){setup(p);if(p.style==Paint.Style.FILL)g.fill(s);else g.draw(s);}void drawOval(float l,float t,float r,float b,Paint p){draw(new Ellipse2D.Float(l,t,r-l,b-t),p);}void drawRect(float l,float t,float r,float b,Paint p){draw(new Rectangle2D.Float(l,t,r-l,b-t),p);}void drawCircle(float x,float y,float r,Paint p){drawOval(x-r,y-r,x+r,y+r,p);}void drawRoundRect(float l,float t,float r,float b,float rx,float ry,Paint p){draw(new RoundRectangle2D.Float(l,t,r-l,b-t,rx*2,ry*2),p);}void drawText(String text,float x,float y,Paint p){setup(p);g.setFont(p.font.deriveFont(p.textSize));g.drawString(text,x,y);}}
}
