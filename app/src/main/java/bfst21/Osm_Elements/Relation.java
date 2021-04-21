package bfst21.Osm_Elements;

import javafx.scene.canvas.GraphicsContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Relation extends NodeHolder {

    private ArrayList<Way> ways;
    private String name;
    private boolean isMultiPolygon;

    private ArrayList<Way> innerWays;
    private ArrayList<Way> outerWays;


    private String restriction;
    private Way to,from;
    private Node via;

    public Way getTo() {
        return to;
    }

    public void setTo(Way to) {
        this.to = to;
    }

    public Way getFrom() {
        return from;
    }

    public void setFrom(Way from) {
        this.from = from;
    }

    public Node getVia() {
        return via;
    }

    public void setVia(Node via) {
        this.via = via;
    }

    public Relation(long id) {
        super(id);
        ways = new ArrayList<>();
    }

    public ArrayList<Way> getWays() {
        return ways;
    }
    public void addWay(Way way) {
        if (way != null) {
            ways.add(way);
            updateCoordinates(way);
        }
    }

    private void updateCoordinates(Way way) {
        checkX(way.xMin);
        checkX(way.xMax);
        checkY(way.yMin);
        checkY(way.yMax);
    }

    public void addInnerOuterWay(Way way, boolean inner) {
        if (innerWays == null || outerWays == null) {
            innerWays = new ArrayList<>();
            outerWays = new ArrayList<>();
        }
        if (inner) {
            innerWays.add(way);
        } else {
            outerWays.add(way);
        }
        updateCoordinates(way);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRestriction(String restriction) {
        this.restriction = restriction;
    }

    @Override
    public void draw(GraphicsContext gc) {
        if (isMultiPolygon) {
            if (innerWays.size() > 0) {
                for (Way w : innerWays) {
                    w.draw(gc);
                }
            }

            if (outerWays.size() > 0) {
                for (Way w : outerWays) {
                    w.draw(gc);
                }
            }
        } else {
            if(ways != null) {
                for (Way w : ways) {
                    w.draw(gc);
                }
            } if(nodes != null && nodes.size()!= 0){
                super.draw(gc);
            }
        }
    }

    public void setIsMultiPolygon() {
        isMultiPolygon = true;
    }

    public void mergeWays(){
        ways = mergeWays(ways);
    }

    private ArrayList<Way> mergeWays(ArrayList<Way> toMerge) {
        /*
         * Inner and outer rings are created from closed ways whenever possible,
         * except when these ways become very large (on the order of 2000 nodes). W
         * ays are usually not shared by different multipolygons.
         * From OSM wiki - mapping stype best practice with Relations
         */
        Map<Node, Way> pieces = new HashMap<>();
        for (Way way : toMerge) {
            Way before = pieces.remove(way.getNodes().get(0));
            Way after = pieces.remove(way.getNodes().get(way.getNodes().size()-1));
            if (before == after) after = null;
            if(before != null){
                way = mergeTwoWays(before, way);
            }
            if(after != null){
                mergeTwoWays(way, after);
            }
            pieces.put(way.getNodes().get(0), way);
            pieces.put(way.getNodes().get(way.getNodes().size()-1), way);
        }
        ArrayList<Way> merged = new ArrayList<>();
        pieces.forEach((node, way) -> {
            if (way.getNodes().get(way.getNodes().size()-1) == node) {
                merged.add(way);
            }
        });
        return merged;
    }
     private ArrayList<Way> mergeWaysTemp(ArrayList<Way> toMerge) {
         Map<Node, Way> pieces = new HashMap<>();
            for (Way way : toMerge) {
                Way before = pieces.remove(way.getNodes().get(0));
                Way after = pieces.remove(way.getNodes().get(way.getNodes().size()-1));
                if (before == after) after = null;
        }
            return null;
     }

     private Way mergeTwoWays(Way w1, Way w2){
        List<Node> list = w2.getNodes();
        list.remove(1);
        w1.getNodes().addAll(list);
        return w1;

     }

}