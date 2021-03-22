package bfst21.data_structures;

import bfst21.Osm_Elements.NodeHolder;

import java.util.ArrayList;

public class RTreeNode {
    private float[] coordinates;
    private ArrayList<RTreeNode> children;
    private boolean leaf;
    private ArrayList<NodeHolder> entries;
    private int minimumEntrySize, maximumChildren;
    private RTreeNode parent;
    public int id;

    public RTreeNode(float[] coordinates, boolean leaf, int minimumChildren, int maximumChildren, RTreeNode parent, int id) { // TODO: 3/22/21 delete id
        this.coordinates = coordinates;
        this.leaf = leaf;
        this.maximumChildren = maximumChildren;
        this.minimumEntrySize = minimumChildren;
        this.entries = new ArrayList<>();
        this.parent = parent;
        children = new ArrayList<>();
        this.id = id;
    }

    public boolean isLeaf() {
        return leaf;
    }

    public void setIsLeaf(boolean value) {
        leaf = value;
    }

    public float[] getCoordinates() {
        return coordinates;
    }

    public void updateCoordinate(int index, float newCoordinate) {
        coordinates[index] = newCoordinate;
    }

    public ArrayList<RTreeNode> getChildren() {
        return children;
    }

    public ArrayList<NodeHolder> getNodeHolderEntries() {
        return entries;
    }

    public boolean overflow() {
        System.out.println("current number of children " + children.size() + " the max children is: " + maximumChildren);
        return children.size() > maximumChildren;
    }

    public boolean underflow() {
        return children.size() < minimumEntrySize;
    }

    public void addNodeHolderEntry(NodeHolder n) {
        entries.add(n);
    }

    public void addChild(RTreeNode r) {
        children.add(r);
        System.out.println("currently " + children.size() + " added child(ren)");
        r.addParent(this); // TODO: 3/22/21 do i need this 
    }

    public void addChildren(ArrayList<RTreeNode> children) {
        this.children.addAll(children);
    }

    public void removeChildren() {
        children.clear();
    }

    private void addParent(RTreeNode rTreeNode) {
        parent = rTreeNode;
    }

    public RTreeNode getParent() {
        return parent;
    }
}
