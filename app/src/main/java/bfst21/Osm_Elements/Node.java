package bfst21.Osm_Elements;

import javafx.scene.canvas.GraphicsContext;

import java.io.Serial;
import java.io.Serializable;

/**
 * Node class represents a singular point on the map, with one x and y coordinate.
 */
public class Node extends Element implements Serializable {
    @Serial
    private static final long serialVersionUID = -2738011707251247970L;

    public Node(long id, float lon, float lat) {
        super(id);
        this.xMin = lon;
        this.yMin = lat;
    }

    public void setX(float x) {
        xMin = x;
    }

    public void setY(float y) {
        yMin = y;
    }

    @Override
    public void draw(GraphicsContext gc) {

    }
}
