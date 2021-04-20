/* Adapted from MappingPanel (rabbit-in-a-hat) */
package rabbitinahat;

import rabbitinahat.model.Mapping;
import rabbitinahat.model.Table;

import javax.swing.JFrame;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

public class MappingPanel extends JFrame{

    public static final int ITEM_WIDTH = 200;
    public static final int ITEM_HEIGHT = 50;
    public static final int MARGIN = 10;
    public static final int MIN_SPACE_BETWEEN_COLUMNS = 200;
    public static final int HEADER_HEIGHT = 25;
    public static final int HEADER_TOP_MARGIN = 0;

    private int sourceX;
    private int targetX;


    private Mapping<?> mapping;
    private List<LabeledRectangle> sourceComponents = new ArrayList<>();
    private List<LabeledRectangle> targetComponents = new ArrayList<>();

    private int maxHeight = Integer.MAX_VALUE;
    private boolean minimized = false;
    private boolean showingArrowStarts = false;

    private boolean showOnlyConnectedItems = false;

    public MappingPanel(Mapping<?> mapping) {
        this.mapping = mapping;
        renderModel();
    }

    private void renderModel() {
        // clear

    }

    public Dimension getMinimumSize() {
        Dimension dimension = new Dimension();
        dimension.width = 2 * (ITEM_WIDTH + MARGIN) + MIN_SPACE_BETWEEN_COLUMNS;
        dimension.height = Math.min(HEADER_HEIGHT + HEADER_TOP_MARGIN + Math.max(sourceComponents.size(), targetComponents.size()) * (ITEM_HEIGHT + MARGIN),
                maxHeight);

        return dimension;
    }

    public void setShowOnlyConnectedItems(boolean value) {
        showOnlyConnectedItems = value;
        renderModel();
    }

    public List<LabeledRectangle> getVisibleSourceComponents() {
        return getVisibleRectangles(sourceComponents);
    }

    public List<LabeledRectangle> getVisibleTargetComponents() {
        return getVisibleRectangles(targetComponents);
    }

    public void paint(Graphics g) {
        Image offscreen = createVolatileImage(getWidth(), getHeight());
        Graphics2D g2d;

        if (offscreen == null) {
            g2d = (Graphics2D) g;
        } else {
            g2d = (Graphics2D) offscreen.getGraphics();
        }

        g2d.setBackground(Color.WHITE);
        g2d.clearRect(0, 0, getWidth(), getHeight());

        RenderingHints rh = new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHints(rh);

        g2d.setColor(Color.BLACK);
        addLabel(g2d, this.getSourceDbName(), sourceX + ITEM_WIDTH / 2, HEADER_TOP_MARGIN + HEADER_HEIGHT / 2);
        addLabel(g2d, this.getTargetDbName(), targetX + ITEM_WIDTH / 2, HEADER_TOP_MARGIN + HEADER_HEIGHT / 2);

        /*
        if (showingArrowStarts) {
            for (LabeledRectangle item : getVisibleSourceComponents())
                Arrow.drawArrowHead(g2d, Math.round(item.getX() + item.getWidth() + Arrow.headThickness), item.getY() + item.getHeight() / 2);
        }
        */

        for (LabeledRectangle component : getVisibleSourceComponents())
            component.paint(g2d);

        for (LabeledRectangle component : getVisibleTargetComponents())
            component.paint(g2d);
        /*
        for (int i = HighlightStatus.values().length - 1; i >= 0; i--) {
            HighlightStatus status = HighlightStatus.values()[i];
            for (Arrow arrow : arrowsByStatus(status)) {
                if (arrow != dragArrow) {
                    arrow.paint(g2d);
                }
            }
        }
        */

        if (offscreen != null)
            g.drawImage(offscreen, 0, 0, this);
    }

    private void addLabel(Graphics2D g2d, String string, int x, int y) {
        g2d.setFont(new Font("default", Font.PLAIN, 20));
        FontMetrics fm = g2d.getFontMetrics();
        Rectangle2D r = fm.getStringBounds(string, g2d);
        g2d.drawString(string, x - Math.round(r.getWidth() / 2), y - Math.round(r.getHeight() / 2) + fm.getAscent());
    }


    public void setSize(int width, int height) {
        sourceX = MARGIN;
        targetX = width - MARGIN - ITEM_WIDTH;
        //stemX = (sourceX + cdmX) / 2;

        layoutItems();
        //super.setSize(width, height);
    }

    private void layoutItems() {
        if (minimized) { // Only update x coordinate
            for (LabeledRectangle targetComponent : getVisibleTargetComponents()) {
                targetComponent.setLocation(targetX, targetComponent.getY());
            }
        } else {
            setLabeledRectanglesLocation(getVisibleSourceComponents(), sourceX);
            setLabeledRectanglesLocation(getVisibleTargetComponents(), targetX);
        }
    }

    public String getSourceDbName() {
        String resString = "Source";
        if (this.mapping.getSourceItems().size() > 0) {
            if (this.mapping.getSourceItems().get(0).getDb() != null)
                resString = this.mapping.getSourceItems().get(0).getDb().getName();
        }
        return resString;

    }

    public String getTargetDbName() {
        String resString = "Target";
        if (this.mapping.getTargetItems().size() > 0) {
            if (this.mapping.getTargetItems().get(0).getDb() != null)
                resString = this.mapping.getTargetItems().get(0).getDb().getName();
        }
        return resString;
    }


    public List<LabeledRectangle> getVisibleRectangles(List<LabeledRectangle> components) {
        List<LabeledRectangle> visible = new ArrayList<>();
        for (LabeledRectangle component : components) {
            if (component.isVisible())
                visible.add(component);
        }
        return visible;
    }

    // Sets the location of the Labeled Rectangles
    private void setLabeledRectanglesLocation(List<LabeledRectangle> components, int xpos) {
        int avoidY = Integer.MAX_VALUE;
        //if (dragRectangle != null && dragRectangle.getX() == xpos)
        //    avoidY = dragRectangle.getY();
        int y = HEADER_HEIGHT + HEADER_TOP_MARGIN;
        if (ObjectExchange.etl.hasStemTable()) {
            // Move all non-stem items
            y = HEADER_TOP_MARGIN + ITEM_HEIGHT;
        }
        for (LabeledRectangle component : components) {
            // Exception for laying out the stem table
            if (component.getItem().isStem() && component.getItem() instanceof Table) {
                //component.setLocation(stemX, HEADER_TOP_MARGIN);
                continue;
            }

            // All other tables and fields
            if (y > avoidY - ITEM_HEIGHT && y <= avoidY + MARGIN)
                y += MARGIN + ITEM_HEIGHT;

            /*
            if (dragRectangle == null || component != dragRectangle) {
                component.setLocation(xpos, y);
                y += MARGIN + ITEM_HEIGHT;
            }
            */
        }
    }

}

