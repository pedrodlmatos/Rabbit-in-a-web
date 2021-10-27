package summaryGenerator.model;

import java.util.HashMap;
import java.util.Map;

public class ItemToItemMap {

    private MappableItem sourceItem;
    private MappableItem targetItem;
    private Map<String, String> extraFieldToValue = new HashMap<>();
    private String comment;
    private String logic;
    private boolean complete;

    public ItemToItemMap(MappableItem sourceItem, MappableItem targetItem) {
        this.sourceItem = sourceItem;
        this.targetItem = targetItem;
    }


    public ItemToItemMap(Field sourceField, Field targetField, String logic) {
        this.sourceItem = sourceField;
        this.targetItem = targetField;
        this.logic = logic;
    }

    public ItemToItemMap(MappableItem sourceItem, MappableItem targetItem, String logic, boolean complete) {
        this.sourceItem = sourceItem;
        this.targetItem = targetItem;
        this.logic = logic;
        this.complete = complete;
    }

    public MappableItem getSourceItem() {
        return sourceItem;
    }

    public void setSourceItem(MappableItem sourceItem) {
        this.sourceItem = sourceItem;
    }

    public MappableItem getTargetItem() {
        return targetItem;
    }

    public void setTargetItem(MappableItem targetItem) {
        this.targetItem = targetItem;
    }

    public Map<String, String> getExtraFieldToValue() {
        return extraFieldToValue;
    }

    public void setExtraFieldToValue(Map<String, String> extraFieldToValue) {
        this.extraFieldToValue = extraFieldToValue;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getLogic() {
        return logic;
    }

    public void setLogic(String logic) {
        this.logic = logic;
    }

    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }
}
