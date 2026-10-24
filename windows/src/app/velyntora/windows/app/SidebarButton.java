package app.velyntora.windows.app;
import javax.swing.*;
import java.awt.*;
/** Transparent tools; only the active tool gets the Android rounded outline. */
public final class SidebarButton extends JToggleButton {
 public SidebarButton(Icon icon){super(icon);setBorder(BorderFactory.createEmptyBorder());setContentAreaFilled(false);setFocusPainted(false);setOpaque(false);setForeground(EditorTheme.TEXT);setRolloverEnabled(true);}
 protected void paintComponent(Graphics graphics){Graphics2D g=(Graphics2D)graphics.create();g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);if(isSelected()||getModel().isRollover()){g.setColor(new Color(isSelected()?70:53,isSelected()?70:53,isSelected()?70:53));g.fillRoundRect(1,1,getWidth()-3,getHeight()-3,12,12);if(isSelected()){g.setColor(new Color(145,145,145));g.drawRoundRect(1,1,getWidth()-3,getHeight()-3,12,12);}}g.dispose();super.paintComponent(graphics);}
}
