/* Adapted from MappingPanel (rabbit-in-a-hat) */
package rabbitinahat;

import rabbitinahat.model.ItemToItemMap;
import rabbitinahat.model.MappableItem;
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
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class MappingPanel {

    private static final int ITEM_WIDTH = 200;
    private static final int ITEM_HEIGHT = 50;
    private static final int HEADER_HEIGHT = 25;
    private static final int HEADER_TOP_MARGIN = 0;
    private static final int MARGIN = 10;
    private static final int MIN_SPACE_BETWEEN_COLUMNS = 200;
    
    private int sourceX;
    private int targetX;
    private int maxHeight = Integer.MAX_VALUE;

    private Mapping<?> mapping;
    private List<LabeledRectangle> sourceComponents = new ArrayList<>();
    private List<LabeledRectangle> targetComponents = new ArrayList<>();
    private List<Arrow> arrows = new ArrayList<>();

    public MappingPanel(Mapping<?> mapping) {
        this.mapping = mapping;
        renderModel();
    }

    private void renderModel() {
        sourceComponents.clear();
        targetComponents.clear();
        arrows.clear();

        for (MappableItem item : mapping.getSourceItems()) {
            if (isConnected(item))
                if (item.isStem())
                    sourceComponents.add(new LabeledRectangle(0, 400, ITEM_WIDTH, ITEM_HEIGHT, item, new Color(160, 0, 160)));
                else
                    sourceComponents.add(new LabeledRectangle(0, 400, ITEM_WIDTH, ITEM_HEIGHT, item, new Color(255, 128, 0)));
        }

        for (MappableItem item : mapping.getTargetItems()) {
            if (isConnected(item))
                if (item.isStem())
                    targetComponents.add(new LabeledRectangle(0, 400, ITEM_WIDTH, ITEM_HEIGHT, item, new Color(160, 0, 160)));
                else
                    targetComponents.add(new LabeledRectangle(0, 400, ITEM_WIDTH, ITEM_HEIGHT, item, new Color(128, 128, 255)));
        }

        // arrows
        for (ItemToItemMap map : mapping.getSourceToTargetMaps()) {
            Arrow component = new Arrow(getComponentWithItem(map.getSourceItem(), sourceComponents), getComponentWithItem(map.getTargetItem(), targetComponents), map);
            arrows.add(component);
        }
        
        layoutItems();
        // repaint();
    }

    private LabeledRectangle getComponentWithItem(MappableItem item, List<LabeledRectangle> components) {
        for (LabeledRectangle component : components) {
            if (component.getItem().equals(item))
                return component;
        }
        return null;
    }

    private void layoutItems() {
        setLabeledRectanglesLocation(sourceComponents, sourceX);
        setLabeledRectanglesLocation(targetComponents, targetX);
    }

    private void setLabeledRectanglesLocation(List<LabeledRectangle> components, int xpos) {
        int y = HEADER_HEIGHT + HEADER_TOP_MARGIN;
        for (LabeledRectangle component : components) {
            if (component.getItem() instanceof Table) {
                component.setLocation(xpos, y);
                y += MARGIN + ITEM_HEIGHT;
            }
        }
    }

    private boolean isConnected(MappableItem item) {
        for (ItemToItemMap map : mapping.getSourceToTargetMaps()) {
            if (map.getSourceItem() == item || map.getTargetItem() == item)
                return true;
        }
        return false;
    }

    public Dimension getMinimumSize() {
        Dimension dimension = new Dimension();
        dimension.width = 2 * (ITEM_WIDTH + MARGIN) + MIN_SPACE_BETWEEN_COLUMNS;
        dimension.height = Math.min(HEADER_HEIGHT + HEADER_TOP_MARGIN + Math.max(sourceComponents.size(), targetComponents.size()) * (ITEM_HEIGHT + MARGIN),
                maxHeight);
        return dimension;
    }

    public void setSize(int width, int height) {
        sourceX = MARGIN;
        targetX = width - MARGIN - ITEM_WIDTH;
        layoutItems();
    }

    public void paint(BufferedImage image) {
        Graphics2D graphics2D = image.createGraphics();

        graphics2D.setBackground(Color.WHITE);
        graphics2D.clearRect(0, 0, image.getWidth(), image.getHeight());

        RenderingHints renderingHints = new RenderingHints(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        graphics2D.setRenderingHints(renderingHints);

        graphics2D.setColor(Color.BLACK);
        addLabel(graphics2D, this.getSourceDBName(), sourceX + ITEM_WIDTH / 2, HEADER_TOP_MARGIN + HEADER_HEIGHT / 2);
        addLabel(graphics2D, this.getTargetDBName(), targetX + ITEM_WIDTH / 2, HEADER_TOP_MARGIN + HEADER_HEIGHT / 2);

        for (LabeledRectangle component : sourceComponents) {
            component.paint(graphics2D);
        }

        for (LabeledRectangle component : targetComponents) {
            component.paint(graphics2D);
        }

        for (Arrow arrow : arrows) {
            arrow.paint(graphics2D);
        }

        // paint arrows

        graphics2D.dispose();
    }

    private String getTargetDBName() {
        String resString = "Target";
        if (this.mapping.getTargetItems().size() > 0) {
            if (this.mapping.getTargetItems().get(0).getDb() != null) {
                resString = this.mapping.getTargetItems().get(0).getDb().getName();
            }
        }
        return resString;
    }

    private void addLabel(Graphics2D g2d, String string, int x, int y) {
        g2d.setFont(new Font("default", Font.PLAIN, 20));
        FontMetrics fm = g2d.getFontMetrics();
        Rectangle2D r = fm.getStringBounds(string, g2d);
        g2d.drawString(string, x - Math.round(r.getWidth() / 2), y - Math.round(r.getHeight() / 2) + fm.getAscent());
    }

    private String getSourceDBName() {
        String resString = "Source";
        if (this.mapping.getSourceItems().size() > 0) {
            if (this.mapping.getSourceItems().get(0).getDb() != null) {
                resString = this.mapping.getSourceItems().get(0).getDb().getName();
            }
        }
        return resString;
    }


}

