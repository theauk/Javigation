package bfst21.data_structures;

import bfst21.Osm_Elements.Element;
import bfst21.Osm_Elements.Node;
import bfst21.Osm_Elements.Way;
import bfst21.view.MapCanvas;

import java.util.ArrayList;
import java.util.Map;

public class RTreeHolder {
    private ArrayList<RTree> rTreeList;
    private int returnListSize;
    private Map<String, Byte> zoomMap;
    private RTree closetRoadRTree;

    public RTreeHolder(int minimumChildren, int maximumChildren, int numberOfCoordinates, int returnListSize){
        rTreeList = new ArrayList<>();
        this.returnListSize = returnListSize;
        zoomMap = MapCanvas.zoomMap;
        for(int i= 0; i<MapCanvas.MAX_ZOOM_LEVEL; i++){
            rTreeList.add(new RTree(minimumChildren, maximumChildren, numberOfCoordinates));
        }
    }

    public void setClosetRoadRTree(RTree rTree){
        closetRoadRTree = rTree;
    }

    public void insert(Element element){
        String type = element.getType();
        if(zoomMap.get(type) != null) {
            rTreeList.get(zoomMap.get(type)).insert(element);
        }
    }

    public ArrayList<ArrayList<Element>> search(float xMin, float xMax, float yMin, float yMax, boolean debug, int currentZoomLevel){
        ArrayList<ArrayList<Element>> results = getCleanArrayList();
        for(int i = 0; i<= currentZoomLevel; i++){
                rTreeList.get(i).search(xMin, xMax, yMin, yMax, debug, results);
        }

        return results;
    }

    public Way getNearestRoad(float x, float y){
        return closetRoadRTree.getNearestRoad(x,y);
    }

    /**
     * Make a list with as many lists as layers for the map.
     * @return An ArrayList with empty nested Arraylists.
     */
    private ArrayList<ArrayList<Element>> getCleanArrayList(){
        ArrayList<ArrayList<Element>> results = new ArrayList<>();
        while (results.size() <= returnListSize) {
            results.add(new ArrayList<>());
        }
        return results;
    }
}
