package bfst21.data_structures;

import bfst21.Osm_Elements.Element;
import bfst21.Osm_Elements.NodeHolder;

import java.util.ArrayList;

public class RTreeNode {
    private float[] coordinates;
    private RTreeNode parent;
    private ArrayList<RTreeNode> children;
    private boolean leaf;
    private NodeHolder[] elements;
    private int elementsSize;
    private int numberOfElements;

    public RTreeNode(float xMin, float xMax, float yMin, float yMax, boolean leaf, int elementsSize) {
        this.coordinates = new float[]{xMin, xMax, yMin, yMax};
        this.leaf = leaf;
        this.elementsSize = elementsSize;
        this.elements = new NodeHolder[elementsSize];
        children = new ArrayList<>();
        numberOfElements = 0;
    }

    public float[] getCoordinates() {
        return coordinates;
    }

    public RTreeNode getParent() {
        return parent;
    }

    public void setParent(RTreeNode r) {
        parent = r;
    }

    public ArrayList<RTreeNode> getChildren() {
        return children;
    }

    public boolean isLeaf() {
        return leaf;
    }

    public NodeHolder[] getNodeHolderElements() {
        return elements;
    }

    public boolean isFull() {
        return numberOfElements == elementsSize;
    }

    public void addNodeHolderElement(NodeHolder n) {
        elements[numberOfElements] = n;
        numberOfElements++;
    }

    public void addChild(RTreeNode r) {
        children.add(r);
        r.setParent(this);
    }
}
