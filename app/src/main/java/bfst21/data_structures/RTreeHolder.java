package bfst21.data_structures;

import bfst21.Osm_Elements.Element;
import bfst21.utils.VehicleType;
import bfst21.view.MapCanvas;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Sorts elements into different R-trees depending on their type, returns range search list of elements from rTrees based on paint order.
 */
public class RTreeHolder implements Serializable {
    @Serial
    private static final long serialVersionUID = 4579015712304488555L;

    private final Map<String, RTree> rTreeMap;
    private final ArrayList<ArrayList<RTree>> rTreeList;
    private final int returnListSize;
    private final Map<String, Byte> zoomMap;
    private RTree closetRoadRTree;

    /**
     * Creates R-tree holder which makes list of R-trees. R-trees are made based on which type they represent. The R-trees are then sorted in lists based zoom level.
     * @param minimumChildren of Rtree nodes
     * @param maximumChildren of Rtree nodes
     * @param numberOfCoordinates dimension of coordinates.
     * @param returnListSize Paint layers.
     */
    public RTreeHolder(int minimumChildren, int maximumChildren, int numberOfCoordinates, int returnListSize) {
        rTreeList = new ArrayList<>();
        rTreeMap = new HashMap<>();
        this.returnListSize = returnListSize;
        zoomMap = MapCanvas.zoomMap;

        while (rTreeList.size() <= MapCanvas.MAX_ZOOM_LEVEL) {
            rTreeList.add(new ArrayList<>());
        }

        for (String type : zoomMap.keySet()) {
            RTree rTree = new RTree(minimumChildren, maximumChildren, numberOfCoordinates);
            rTreeMap.put(type, rTree);
            rTreeList.get(zoomMap.get(type)).add(rTree);
        }
    }

    /**
     * Sets the Rtree that solely holds ways which can be traveled on.
     * @param rTree Rtree with only travelable ways.
     */
    public void setClosetRoadRTree(RTree rTree) {
        closetRoadRTree = rTree;
    }

    /**
     * Inserts element into the Rtree corresponding to type.
     * @param element element to insert.
     */
    public void insert(Element element) {
        String type = element.getType();
        if (zoomMap.get(type) != null) {
            rTreeMap.get(type).insert(element);
        }
    }

    /**
     * Search for elements in the R-tree based on the current zoom level.
     * @param xMin The x-min coordinate of the bounding box.
     * @param xMax The x-man coordinate of the bounding box.
     * @param yMin The y-min coordinate of the bounding box.
     * @param yMax The y-max coordinate of the bounding box.
     * @param debug True, if debug elements should be shown. Otherwise, false.
     * @param currentZoomLevel The current zoom level.
     * @return A nested list with elements within the bounding box sorted based on zoom levels.
     */
    public ArrayList<ArrayList<Element>> search(float xMin, float xMax, float yMin, float yMax, boolean debug, int currentZoomLevel) {
        ArrayList<ArrayList<Element>> results = getCleanArrayList();
        for (int i = 0; i <= currentZoomLevel; i++) {
            for (RTree rTree : rTreeList.get(i)) {
                rTree.search(xMin, xMax, yMin, yMax, debug, results);
            }
        }

        return results;
    }

    /**
     * Gets the closest NearestRoadPriorityQueueEntry to a query point.
     * @param x Query x-coordinate.
     * @param y Query y-coordinate.
     * @param addressWayName The name of the address street name if searching for the closest road to an address.
     *                       Otherwise, null if searching for closet road to the cursor.
     * @return The closest NearestRoadPriorityQueueEntry.
     */
    public RTree.NearestRoadPriorityQueueEntry getNearestRoad(float x, float y, String addressWayName, VehicleType vehicleType) {
        return closetRoadRTree.getNearestRoad(x, y, addressWayName, vehicleType);
    }

    /**
     * Make a list with as many lists as layers for the map.
     *
     * @return An ArrayList with empty nested Arraylists.
     */
    private ArrayList<ArrayList<Element>> getCleanArrayList() {
        ArrayList<ArrayList<Element>> results = new ArrayList<>();
        while (results.size() <= returnListSize) {
            results.add(new ArrayList<>());
        }
        return results;
    }
}
