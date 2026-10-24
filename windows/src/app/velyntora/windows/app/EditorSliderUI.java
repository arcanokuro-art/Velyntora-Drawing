package app.velyntora.windows.app;
import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.BasicSliderUI;
import java.awt.*;
/** Flat track and circular thumb; no operating-system gradient or font glyph. */
public final class EditorSliderUI extends BasicSliderUI {
 public EditorSliderUI(JSlider slider){super(slider);}public static ComponentUI createUI(JComponent c){return new EditorSliderUI((JSlider)c);}
 protected Dimension getThumbSize(){return new Dimension(14,14);}
 public void paintTrack(Graphics graphics){Graphics2D g=(Graphics2D)graphics.create();g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);g.setStroke(new BasicStroke(3,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));g.setColor(new Color(90,90,90));if(slider.getOrientation()==JSlider.HORIZONTAL){int y=trackRect.y+trackRect.height/2;g.drawLine(trackRect.x,y,trackRect.x+trackRect.width,y);g.setColor(EditorTheme.ACCENT);int value=xPositionForValue(slider.getValue()),start=xPositionForValue(slider.getMinimum());g.drawLine(start,y,value,y);}else{int x=trackRect.x+trackRect.width/2;g.drawLine(x,trackRect.y,x,trackRect.y+trackRect.height);}g.dispose();}
 public void paintThumb(Graphics graphics){Graphics2D g=(Graphics2D)graphics.create();g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);g.setColor(slider.isEnabled()?EditorTheme.ACCENT:Color.GRAY);g.fillOval(thumbRect.x,thumbRect.y,thumbRect.width,thumbRect.height);g.dispose();}
 public void paintFocus(Graphics g){}
}
