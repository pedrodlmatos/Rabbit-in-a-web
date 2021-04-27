/* Adapted from Arrow (rabbit-in-a-hat) */
package rabbitinahat;

import rabbitinahat.model.ItemToItemMap;

import java.awt.*;

public class Arrow implements MappingComponent {

    private int x1;
    private int y1;
    private int x2;
    private int y2;
    private int width;
    private int height;

    private static int headThickness = 15;
    private static int thickness = 5;

    private Polygon polygon;

    private LabeledRectangle source;
    private LabeledRectangle target;
    private ItemToItemMap itemToItemMap;

    public Arrow(LabeledRectangle source, LabeledRectangle target, ItemToItemMap itemToItemMap) {
        this.source = source;
        this.target = target;
        this.itemToItemMap = itemToItemMap;
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D graphics2D = (Graphics2D) g;

        if (source != null) {
            x1 = source.getX() + source.getWidth();
            y1 = source.getY() + source.getHeight()/2;
            width = Math.abs(x1-x2);
            height = Math.abs(y1-y2);
        }
        if (target != null) {
            x2 = target.getX();
            y2 = target.getY() + target.getHeight()/2;
            width = Math.abs(x1-x2);
            height = Math.abs(y1-y2);
        }

        int nPoints = 25;
        int[] xPoints = new int[nPoints * 2 + 3];
        int[] yPoints = new int[nPoints * 2 + 3];
        float widthMinHead = getWidth() - headThickness;
        float stepSize = widthMinHead / (float) (nPoints - 1);

        for (int i = 0; i < nPoints; i++) {
            float x = x1 + stepSize * i;
            float y = (float) (y1 + (Math.cos(Math.PI * i / (float) nPoints) / 2d - 0.5) * (y1 - y2));
            xPoints[i] = Math.round(x);
            yPoints[i] = Math.round(y - thickness);
            xPoints[nPoints * 2 + 3 - i - 1] = Math.round(x);
            yPoints[nPoints * 2 + 3 - i - 1] = Math.round(y + thickness);
        }
        xPoints[nPoints] = x2 - headThickness;
        yPoints[nPoints] = y2 - headThickness;
        xPoints[nPoints + 1] = x2;
        yPoints[nPoints + 1] = y2;
        xPoints[nPoints + 2] = x2 - headThickness;
        yPoints[nPoints + 2] = y2 + headThickness;
        polygon = new Polygon(xPoints, yPoints, nPoints * 2 + 3);

        graphics2D.setColor(new Color(128, 128, 128, 192));
        graphics2D.fillPolygon(polygon);
    }

    private int getWidth() {
        return width;
    }

    @Override
    public boolean isVisible() {
        return false;
    }
}
