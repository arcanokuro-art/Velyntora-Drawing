package app.velyntora.windows.app;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
/** UI commands are geometry rather than Unicode symbols from an installed font. */
public final class CommandIcon implements Icon {
 private final String name;public CommandIcon(String name){this.name=name;}public int getIconWidth(){return 24;}public int getIconHeight(){return 24;}
 private static void line(Graphics2D g,int... points){Path2D p=new Path2D.Float();p.moveTo(points[0],points[1]);for(int i=2;i<points.length;i+=2)p.lineTo(points[i],points[i+1]);g.draw(p);}
 public void paintIcon(Component c,Graphics graphics,int x,int y){Graphics2D g=(Graphics2D)graphics.create();g.translate(x,y);g.setColor(c.isEnabled()?c.getForeground():Color.GRAY);g.setStroke(new BasicStroke(1.7f,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
  switch(name){
   case "menu":line(g,3,5,21,5);line(g,3,12,21,12);line(g,3,19,21,19);break;
   case "new":line(g,6,21,6,3,15,3,19,7,19,12);line(g,14,3,14,8,19,8);line(g,15,17,23,17);line(g,19,13,19,21);break;
   case "open":line(g,3,20,3,5,10,5,13,8,21,8,21,11);line(g,3,20,7,11,23,11,19,20,3,20);break;
   case "save":line(g,12,3,12,16);line(g,7,11,12,16,17,11);line(g,4,17,4,21,20,21,20,17);break;
   case "undo":line(g,9,4,3,10,9,16);g.draw(new Arc2D.Float(3,9,18,12,-130,240,Arc2D.OPEN));break;
   case "redo":line(g,15,4,21,10,15,16);g.draw(new Arc2D.Float(3,9,18,12,70,240,Arc2D.OPEN));break;
   case "layers":line(g,2,8,12,2,22,8,12,14,2,8);line(g,2,13,12,19,22,13);line(g,2,18,12,24,22,18);break;
   case "confirm":line(g,3,12,9,18,21,5);break;
   case "cancel":line(g,5,5,19,19);line(g,19,5,5,19);break;
   case "add":line(g,12,4,12,20);line(g,4,12,20,12);break;
   case "delete":line(g,5,7,19,7);line(g,9,4,15,4);line(g,7,7,8,21,16,21,17,7);break;
   case "visible":g.draw(new Ellipse2D.Float(2,7,20,10));g.fill(new Ellipse2D.Float(9,9,6,6));break;
   case "up":line(g,5,12,12,5,19,12);line(g,12,5,12,21);break;
   case "down":line(g,5,12,12,19,19,12);line(g,12,3,12,19);break;
   case "duplicate":g.drawRect(4,4,12,12);g.drawRect(9,9,12,12);break;
   default:g.drawRect(5,5,14,14);
  }g.dispose();
 }
}
