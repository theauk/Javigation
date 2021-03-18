package bfst21.Osm_Elements;

import bfst21.Drawable;
import javafx.scene.canvas.GraphicsContext;

import java.util.ArrayList;
import java.util.List;

public class Way extends Element implements Drawable {
    private List<Node> nodes = new ArrayList<>();
    private Long id;

    public Node first() {
        return nodes.get(0);
    }
    
    public void add(Node node) {
        nodes.add(node);
    }

    public void setId(Long _id){
        id = _id;
    }
    public Long getId(){
        return id;
    }

    @Override
    public void draw(GraphicsContext gc) {
        gc.beginPath();
        gc.moveTo(nodes.get(0).getX(), nodes.get(0).getY());
        for (var node : nodes) {
            gc.lineTo(node.getX(), node.getY());
        }
        gc.stroke();
    }
}