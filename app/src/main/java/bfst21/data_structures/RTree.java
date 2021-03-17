package bfst21.data_structures;

import bfst21.Osm_Elements.Spatializable;

public class RTree<Item extends Spatializable> {

    public RTree() {

    }

    private Boolean contains(Item outerItem, Item innerItem) {
        if (outerItem.getxMin() > innerItem.getxMin()) {
            return false;
        } else if (outerItem.getxMax() < outerItem.getxMax()) {
            return false;
        } else if (outerItem.getyMin() > innerItem.getyMin()) {
            return false;
        } else if (outerItem.getyMax() < innerItem.getyMax()) {
            return false;
        }
        return true;
    }

    private Boolean intersects(Item item1, Item item2) {
        if (item2.getxMin() > item1.getxMax()) {
            return false;
        } else if (item2.getyMin() > item1.getyMax()) {
            return false;
        } else if (item1.getxMin() > item2.getxMax()) {
            return false;
        } else if (item1.getyMin() > item2.getyMax()) {
            return false;
        }
        return true;
    }

}
