package bfst21.Osm_Elements;

import java.util.ArrayList;
import java.util.List;

public class Way extends Element implements Spatializable {
    private List<Node> nodes = new ArrayList<>();
    private Long id;
    private float xMin;
    private float xMax;
    private float yMin;
    private float yMax;

    public Node firstNode() {
        return nodes.get(0);
    }
    
    public void addNode(Node node) {
        nodes.add(node);
        if(nodes.size() > 1) {
            checkX(node.getX());
            checkY(node.getY());
        } else {
            xMin = node.getX();
            xMax = node.getX();
            yMin = node.getY();
            yMax = node.getY();
        }
    }

    private void checkX(float xValue) {
        if(xValue > xMax) {
            xMax = xValue;
        } else if(xValue < xMin) {
            xMin = xValue;
        }
    }

    private void checkY(float yValue) {
        if(yValue > yMax) {
            yMax = yValue;
        } else if(yValue < yMin) {
            yMin = yValue;
        }
    }

    public void setId(Long id){
        this.id = id;
    }

    public Long getId(){
        return id;
    }

    public float getxMin() {
        return xMin;
    }

    public float getxMax() {
        return xMax;
    }

    public float getyMin() {
        return yMin;
    }

    public float getyMax() {
        return yMax;
    }
}