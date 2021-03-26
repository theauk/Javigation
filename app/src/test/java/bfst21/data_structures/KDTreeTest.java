package bfst21.data_structures;

import bfst21.Osm_Elements.Node;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

public class KDTreeTest {
    private static KDTree<Node> tree;

    @BeforeEach
    void setUp() {
        tree = new KDTree<>();
        List<Node> list = new ArrayList<>();
            list.add(new Node(1,(float)12.5972367,(float)55.6952510));
            list.add(new Node(2,(float)12.5991499,(float)55.6951851));
            list.add(new Node(3,(float)12.6002220,(float)55.6961339));
            list.add(new Node(4,(float)12.5823799,(float)55.6685247));
        tree.addALl(list);
    }

    @Test
    void searchGetNearestNodeTest() {
        Node node = new Node(5, (float)12.5823800, (float)55.6685260);
        assertEquals(4, tree.getNearestNode(node.getxMax(),node.getyMax()).getId());
    }

     @AfterAll
        static void afterAll() {
         tree = null;
     }
}