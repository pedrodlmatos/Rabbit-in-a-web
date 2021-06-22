/* Adapted from LabeledRectangle (rabbit-in-a-hat) */
package rabbitinahat;

import rabbitinahat.model.MappableItem;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.event.ChangeListener;


public class LabeledRectangle implements MappingComponent {

    private int x;
    private int y;
    private int width;
    private int height;
    private MappableItem item;
    private Color baseColor;
    private Color transparentColor;

    private static int FONT_SIZE = 18;
    private static Stroke stroke = new BasicStroke(2);


    public LabeledRectangle(int x, int y, int width, int height, MappableItem item, Color baseColor) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.item = item;
        this.baseColor = baseColor;
        this.transparentColor = new Color(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), 128);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public MappableItem getItem() {
        return item;
    }

    public void setLocation(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean isVisible() {
        return true;
    }


    @Override
    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setColor(transparentColor);
        g2d.fillRect(x, y, width, height);
        g2d.setColor(baseColor);
        g2d.setStroke(stroke);
        g2d.drawRect(x, y, width, height);
        g2d.setColor(Color.BLACK);

        g2d.setFont(new Font("default", Font.PLAIN, FONT_SIZE));
        FontMetrics fm = g2d.getFontMetrics();

        Rectangle2D r = fm.getStringBounds(item.outputName(), g2d);
        if (r.getWidth() >= width) {
            int breakPoint = 0;
            int index = nextBreakPoint(item.outputName(), 0);
            double midPoint = item.outputName().length() / 2d;
            while (index != -1) {
                if (Math.abs(index - midPoint) < Math.abs(breakPoint - midPoint))
                    breakPoint = index;
                index = nextBreakPoint(item.outputName(), index + 1);
            }
            if (breakPoint == 0) {
                breakPoint = (int) midPoint + 1;
            } else {
                breakPoint++;
            }
            String line1 = item.outputName().substring(0, breakPoint);
            String line2 = item.outputName().substring(breakPoint);

            Rectangle2D r1 = fm.getStringBounds(line1, g2d);
            Rectangle2D r2 = fm.getStringBounds(line2, g2d);
            if (r1.getWidth() >= width) {
                line1 = item.outputName().substring(0, (int) midPoint);
                line2 = item.outputName().substring((int) midPoint);
                r1 = fm.getStringBounds(line1, g2d);
                r2 = fm.getStringBounds(line2, g2d);
            } else if (r2.getWidth() >= width) {
                line1 = item.outputName().substring(0, (int) midPoint);
                line2 = item.outputName().substring((int) midPoint);
                r1 = fm.getStringBounds(line1, g2d);
                r2 = fm.getStringBounds(line2, g2d);
            }
            // If both lines are too wide, then the text will still go out of bounds
            int textX1 = (this.getWidth() - (int) r1.getWidth()) / 2;
            int textY1 = (this.getHeight() / 2 - (int) r1.getHeight()) / 2 + fm.getAscent();
            g2d.drawString(line1, x + textX1, y + textY1);

            int textX2 = (this.getWidth() - (int) r2.getWidth()) / 2;
            int textY2 = (int) Math.round(this.getHeight() * 1.5 - (int) r2.getHeight()) / 2 + fm.getAscent();
            g2d.drawString(line2, x + textX2, y + textY2);
        } else {
            int textX = (this.getWidth() - (int) r.getWidth()) / 2;
            int textY = (this.getHeight() - (int) r.getHeight()) / 2 + fm.getAscent();
            g2d.drawString(item.outputName(), x + textX, y + textY);
        }
    }

    private int nextBreakPoint(String string, int start) {
        int index1 = string.indexOf(' ', start);
        int index2 = string.indexOf('_', start);
        if (index1 == -1)
            return index2;
        else if (index2 == -1)
            return index1;
        else
            return Math.min(index1, index2);
    }
}
