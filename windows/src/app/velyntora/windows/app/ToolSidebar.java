package app.velyntora.windows.app;
import javax.swing.*;
import java.awt.*;
/** Two columns scale down in short windows before enabling vertical scrolling. */
public final class ToolSidebar extends JPanel implements Scrollable {
 public ToolSidebar(){setLayout(null);setOpaque(false);}
 private int rows(){return Math.max(1,(getComponentCount()+1)/2);}
 public Dimension getPreferredSize(){return new Dimension(120,rows()*58);}
 public Dimension getPreferredScrollableViewportSize(){return getPreferredSize();}
 public int getScrollableUnitIncrement(Rectangle r,int orientation,int direction){return 40;}
 public int getScrollableBlockIncrement(Rectangle r,int orientation,int direction){return Math.max(40,r.height-40);}
 public boolean getScrollableTracksViewportWidth(){return true;}
 public boolean getScrollableTracksViewportHeight(){return getParent()!=null&&getParent().getHeight()>=rows()*36;}
 public void doLayout(){int height=Math.min(58,Math.max(36,getHeight()/rows()));int width=Math.min(58,getWidth()/2);for(int i=0;i<getComponentCount();i++)getComponent(i).setBounds((i%2)*width,(i/2)*height,width,height);}
}
