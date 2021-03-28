package bfst21.Osm_Elements;

import javafx.scene.canvas.GraphicsContext;

import java.util.ArrayList;
import java.util.List;

public class Node extends Element {
    List<String> roadNames;

    public Node(long id, float lon, float lat) {
        super(id);
        this.xMin = lon;
        this.xMax = lon;
        this.yMin = -lat / 0.56f;
        this.yMax = -lat / 0.56f;

    }

    public Node(float lon, float lat) { // TODO: 3/28/21 for Rtree debug mode where the y should not be converted
        super(0);
        this.xMin = lon;
        this.xMax = lon;
        this.yMin = lat;
        this.yMax = lat;
    }

    @Override
    public void draw(GraphicsContext gc) {

    }

    public void addRoadname(String name) {
        //TODO half assed fix
        if (roadNames == null) {
            roadNames = new ArrayList<>();
        }
        if (!roadNames.contains(name)) {
            roadNames.add(name);
        }

    }

    public List<String> getName() {
        return roadNames;
    }
}
