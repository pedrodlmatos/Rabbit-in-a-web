/* Adapted from Mapping (rabbit-core) */
package rabbitinahat.model;

import java.util.ArrayList;
import java.util.List;

public class Mapping<T extends MappableItem> {

    private List<T> sourceItems;
    private List<T> targetItems;
    private List<ItemToItemMap> sourceToTargetMaps;

    public Mapping(List<T> sourceItems, List<T> targetItems, List<ItemToItemMap> sourceToTargetMaps) {
        this.sourceItems = sourceItems;
        this.targetItems = targetItems;
        this.sourceToTargetMaps = sourceToTargetMaps;
    }

    public void addSourceToTargetMap(MappableItem sourceItem, MappableItem targetItem) {
        sourceToTargetMaps.add(new ItemToItemMap(sourceItem, targetItem));
    }

    public void addSourceToTargetMap(ItemToItemMap itemToItemMap) {
        sourceToTargetMaps.add(itemToItemMap);
    }

    public List<MappableItem> getSourceItems() {
        return new ArrayList<>(sourceItems);
    }

    @SuppressWarnings("unchecked")
    public void setSourceItems(List<MappableItem> sourceItems) {
        this.sourceItems.clear();
        for (MappableItem item : sourceItems)
            this.sourceItems.add((T) item);
    }

    @SuppressWarnings("unchecked")
    public void setTargetItems(List<? extends MappableItem> targetItems) {
        this.targetItems.clear();
        for (MappableItem item : targetItems)
            this.targetItems.add((T) item);
    }

    public List<MappableItem> getTargetItems() {
        return new ArrayList<>(targetItems);

    }

    public List<ItemToItemMap> getSourceToTargetMaps() {
        return sourceToTargetMaps;
    }

    public List<ItemToItemMap> getSourceToTargetMapsOrderedByCdmItems() {
        List<ItemToItemMap> result = new ArrayList<>();
        for (MappableItem targetItem : targetItems) {
            boolean sourceFound = false;
            for (MappableItem sourceItem : sourceItems) {
                ItemToItemMap mapping = getSourceToTargetMap(sourceItem, targetItem);
                if (mapping != null) {
                    result.add(mapping);
                    sourceFound = true;
                }
            }
            if (!sourceFound)
                result.add(null);
        }

		//result.removeAll(Collections.singleton(null));
        return result;
    }

    public void removeSourceToTargetMap(MappableItem sourceItem, MappableItem targetItem) {
        sourceToTargetMaps.removeIf(sourceToTargetMap ->
                sourceToTargetMap.getSourceItem().equals(sourceItem) && sourceToTargetMap.getTargetItem().equals(targetItem)
        );
    }

    public void removeAllSourceToTargetMaps() {
        sourceToTargetMaps.clear();
    }

    public ItemToItemMap getSourceToTargetMap(MappableItem sourceItem, MappableItem targetItem) {
        for (ItemToItemMap sourceToTargetMap : sourceToTargetMaps) {
            if (sourceToTargetMap.getSourceItem().equals(sourceItem) && sourceToTargetMap.getTargetItem().equals(targetItem))
                return sourceToTargetMap;
        }
        return null;
    }

    public ItemToItemMap getSourceToTargetMapByName(MappableItem sourceItem, MappableItem targetItem) {
        for (ItemToItemMap sourceToTargetMap : sourceToTargetMaps) {
            if (sourceToTargetMap.getSourceItem().getName().equals(sourceItem.getName())
                    && sourceToTargetMap.getTargetItem().getName().equals(targetItem.getName()))
                return sourceToTargetMap;
        }
        return null;
    }

    public int size() {
        return sourceToTargetMaps.size();
    }
}
