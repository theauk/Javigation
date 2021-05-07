package bfst21.Osm_Elements;

/**
 * Interface used for elements that have coordinates.
 */
public interface Spatializable {

    public float getxMax();

    public float getxMin();

    public float getyMax();

    public float getyMin();

    public float[] getCoordinates();
}
