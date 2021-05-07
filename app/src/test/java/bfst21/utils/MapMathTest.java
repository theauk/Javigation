package bfst21.utils;

import bfst21.Osm_Elements.Node;
import bfst21.Osm_Elements.Way;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MapMathTest {

    @BeforeEach
    void setUp() {

    }

    @Test
    void turnAngleTest() {
    }

    @Test
    void bearingTest() {

    }

    @Test
    void compassDirectionTest() {

    }

    @Test
    void colonTimeHoursTest() {
        String time1 = "10:30";
        double hours1 = MapMath.colonTimeToHours(time1);
        assertEquals(10.5, hours1);

        String time2 = "01:15";
        double hours2 = MapMath.colonTimeToHours(time2);
        assertEquals(1.25, hours2);
    }

    @Test
    void getTotalDistanceTest() {

    }

    @Test
    void shortestDistanceToElementTest() {
    }

    @Test
    void intersectionClosestPointTest() {
        Way w1 = new Way();
        Node n1 = new Node(0, 1, 0);
        Node n2 = new Node(0, 3, 2);
        w1.addNode(n1);
        w1.addNode(n2);
        float x = 3;
        float y = 0;
        Node intersectionNode = MapMath.getClosestPointOnWayAsNode(x, y, w1);
        assertEquals(2f, intersectionNode.getxMax());
        assertEquals(1f, intersectionNode.getyMax());
    }

    @Test
    void formatDistanceTest() {

    }

    @Test
    void formatTimeTest() {
    }

    @Test
    void updateNodeCoordinateIfEndOfWayTest() {

    }
}