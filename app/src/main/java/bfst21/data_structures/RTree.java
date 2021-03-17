package bfst21.data_structures;

import bfst21.Osm_Elements.Element;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RTree {
    private int maxEntries, size;
    private RTreeNode root;

    public RTree(int maxEntries) {
        this.maxEntries = maxEntries;
        this.root = null;
        this.size = 0;
    }

    // TODO: 3/17/21 delete both root methods later 
    public void setRoot(RTreeNode n) {
        root = n;
    }

    public RTreeNode getRoot() {
        return root;
    }

    public List<Element> search(float xMin, float xMax, float yMin, float yMax) {
        float[] searchCoordinates = new float[]{xMin, xMax, yMin, yMax};
        ArrayList<Element> results = new ArrayList<>();
        search(searchCoordinates, root, results);
        System.out.println(root);
        return results;
    }

    private void search(float[] searchCoordinates, RTreeNode node, ArrayList<Element> results) {
        System.out.println("root: " + node.getCoordinates()[0] + " " + node.getCoordinates()[1] + " " + node.getCoordinates()[2] + " " + node.getCoordinates()[3]);
        if (intersects(searchCoordinates, node.getCoordinates())) {
            System.out.println("intersects");
            results.addAll(Arrays.asList(node.getElements()));
        }

        if (!node.isLeaf()) {
            System.out.println("not leaf");
            for (RTreeNode currentNode : node.getChildren()) {
                search(searchCoordinates, currentNode, results);
            }
        }
    }

    public void insert(Element element) {

    }

    public Boolean intersects(float[] coordinates1, float[] coordinates2) {
        System.out.println("in intersects");
        for (int i = 0; i < coordinates1.length; i += 2) {
            if (doesNotIntersect(coordinates1[i], coordinates2[i + 1])) {
                return false;
            } else if (doesNotIntersect(coordinates2[i], coordinates1[i + 1])) {
                return false;
            }
        }
        return true;
    }

    private boolean doesNotIntersect(float minCoordinateFirstElement, float maxCoordinateSecondElement) {
        System.out.println(minCoordinateFirstElement + " " + maxCoordinateSecondElement);
        System.out.println(minCoordinateFirstElement >= maxCoordinateSecondElement);
        return minCoordinateFirstElement >= maxCoordinateSecondElement;
    }

    private Boolean contains(Element outerElement, Element innerElement) {
        if (outerElement.getxMin() > innerElement.getxMin()) {
            return false;
        } else if (outerElement.getxMax() < outerElement.getxMax()) {
            return false;
        } else if (outerElement.getyMin() > innerElement.getyMin()) {
            return false;
        } else if (outerElement.getyMax() < innerElement.getyMax()) {
            return false;
        }
        return true;
    }


}
