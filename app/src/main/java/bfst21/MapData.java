package bfst21;

import bfst21.Exceptions.KDTreeEmptyException;
import bfst21.Osm_Elements.Element;
import bfst21.Osm_Elements.Node;

import bfst21.Osm_Elements.Way;
import bfst21.data_structures.*;
import bfst21.view.CanvasBounds;
import javafx.geometry.Point2D;
import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class MapData {
    private KDTree<Node> closetRoadTree;
    private RTree rTree;
    private List<Element> mapSegment; //Only content within bounds
    private float minX, minY, maxX, maxY;
    private AddressTriesTree addressTree;
    private RoadGraph roadGraph;
    private boolean rTreeDebug;

    public MapData() {
        mapSegment = new ArrayList<>();

    }

    public void addDataTrees(KDTree<Node> highWayRoadNodes, RTree rTree, RoadGraph roadGraph,AddressTriesTree addressTree ){
        this.rTree = rTree;
        this.closetRoadTree = highWayRoadNodes;
        this.addressTree = addressTree;
        this.roadGraph = roadGraph;
        buildTrees();
    }

    private void buildTrees() {
        closetRoadTree.buildTree();
    }


    public void searchInData(CanvasBounds bounds) {
        mapSegment = rTree.search(bounds.getMinX(), bounds.getMaxX(), bounds.getMinY(), bounds.getMaxY(), rTreeDebug);
    }

    public void setRTreeDebug(boolean selected) {
        rTreeDebug = selected;
    }

    public String getNearestRoad(float x, float y) {
        String names = "";
        //System.out.println(x + " " + y);
        HashSet<String> list = new HashSet<>();
        try {
            Node node =  closetRoadTree.getNearestNode(x,y);
            if(node.getReferencedHighWays() != null){
                for(Way way : node.getReferencedHighWays()){
                    if(way.getName()!=null)list.add(way.getName());
                }
                names = String.join(", ", list);
            }

        } catch (KDTreeEmptyException e) {
            names = e.getMessage();
        }
        return names;
    }

    public Node getNearestRoadNode(float x, float y) {
        Node nearestRoadNode = null;
        try {
            nearestRoadNode = closetRoadTree.getNearestNode(x, y);
        } catch (KDTreeEmptyException e) {
            e.printStackTrace();
        }
        return nearestRoadNode;
    }

    public ArrayList<Node> getDijkstraRoute(float x, float y) {
        ArrayList<Node> pathNodes = new ArrayList<>();
        try {
            //Node node1 = closetRoadTree.getNearestNode(12.594574f, -99.40638f);
            //Node node2 = closetRoadTree.getNearestNode( 12.595245f, -99.40244f);
            //Node node1 = closetRoadTree.getNearestNode(12.594574f, -99.40638f);
            //Node node2 = closetRoadTree.getNearestNode( 12.584137f, -99.425026f);
            Node node1 = closetRoadTree.getNearestNode(12.5877075f, -99.39949f);
            Node node2 = closetRoadTree.getNearestNode( 12.577728f, -99.42861f);
            DijkstraSP d = new DijkstraSP(node1, node2, "v", "f");
            pathNodes = d.getPath();
            return pathNodes;
        } catch (KDTreeEmptyException e) {
            e.printStackTrace();
        }
        return pathNodes;
    }

    public List<Element> getMapSegment() {
        return mapSegment;
    }



    public Node getAddressNode(String address) {
        return addressTree.getAddressNode(address);
    }


    public float getMinX() {
        return minX;
    }

    public void setMinX(float minX) {
        this.minX = minX;
    }

    public float getMinY() {
        return minY;
    }

    public void setMinY(float minY) {
        this.minY = minY;
    }

    public float getMaxX() {
        return maxX;
    }

    public void setMaxX(float maxX) {
        this.maxX = maxX;
    }

    public float getMaxY() {
        return maxY;
    }

    public void setMaxY(float maxY) {
        this.maxY = maxY;
    }
}
