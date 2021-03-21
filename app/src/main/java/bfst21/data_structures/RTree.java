package bfst21.data_structures;

import bfst21.Osm_Elements.NodeHolder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class RTree {
    private int minimumChildren, maximumChildren, size, numberOfCoordinates;
    private RTreeNode root;

    public RTree(int minimumChildren, int maximumChildren, int numberOfCoordinates) {
        this.minimumChildren = minimumChildren;
        this.maximumChildren = maximumChildren;
        this.numberOfCoordinates = numberOfCoordinates;
        root = null;
        size = 0;
    }

    public RTreeNode getRoot() {
        return root;
    }

    // TODO: 3/17/21 delete both root methods later
    public void setRoot(RTreeNode n) {
        root = n;
    }

    public List<NodeHolder> search(float xMin, float xMax, float yMin, float yMax) {
        float[] searchCoordinates = new float[]{xMin, xMax, yMin, yMax};
        ArrayList<NodeHolder> results = new ArrayList<>();
        search(searchCoordinates, root, results);
        System.out.println(root);
        return results;
    }

    private void search(float[] searchCoordinates, RTreeNode node, ArrayList<NodeHolder> results) {
        if (node.isLeaf()) {
            for (NodeHolder n : node.getNodeHolderEntries()) {
                if (intersects(searchCoordinates, n.getCoordinates())) {
                    results.add(n);
                }
            }
        } else {
            for (RTreeNode r : node.getChildren()) {
                if (intersects(searchCoordinates, r.getCoordinates())) {
                    search(searchCoordinates, r, results);
                }
            }
        }
    }

    public void insert(NodeHolder nodeHolder) {

        System.out.println("");
        if(root == null) {
            System.out.println("insert root null");
            root = new RTreeNode(nodeHolder.getCoordinates(), true, minimumChildren, maximumChildren, null);
            RTreeNode dataLeaf = new RTreeNode(nodeHolder.getCoordinates(), false, minimumChildren, maximumChildren, root);
            root.addChild(dataLeaf);
        } else {
            System.out.println("ELSE INSERT");
            RTreeNode selectedNode = chooseLeaf(nodeHolder, root);
            System.out.println("selected node: " + selectedNode.getParent());

            RTreeNode newEntry = new RTreeNode(nodeHolder.getCoordinates(), false, minimumChildren, maximumChildren, selectedNode.getParent());
            newEntry.addNodeHolderEntry(nodeHolder);

            selectedNode.addChild(newEntry);

            if (selectedNode.overflow()) {
                System.out.println("insert overflow");
                RTreeNode[] result = splitNodeShuffle(selectedNode);
                System.out.println("split length; " + result[0] + " " + result[1]);
                System.out.println("new nodes child size: " + result[0].getChildren().size() + " " + result[1].getChildren().size());
                for(RTreeNode r : result[0].getChildren()) {
                    System.out.println(Arrays.toString(r.getCoordinates()));
                }
                for(RTreeNode r : result[1].getChildren()) {
                    System.out.println(Arrays.toString(r.getCoordinates()));
                }

                adjustTree(result[0], result[1]);
            } else {
                adjustTree(selectedNode, null);
                System.out.println("insert else");
            }
        }
    }

    private RTreeNode chooseLeaf(NodeHolder nodeHolder, RTreeNode node) {
        if (node.isLeaf()) {
            System.out.println("is leaf");
            return node;
        } else {
            ArrayList<RTreeNode> children = node.getChildren();
            RTreeNode smallestBoundingBoxNode = children.get(0);
            for (int i = 1; i < node.getChildren().size(); i++) {
                if (getNewBoundingBoxArea(nodeHolder, children.get(i)) < getNewBoundingBoxArea(nodeHolder, smallestBoundingBoxNode)) {
                    smallestBoundingBoxNode = children.get(i);
                }
            }
            return chooseLeaf(nodeHolder, smallestBoundingBoxNode);
        }
    }

    private void adjustTree(RTreeNode originalNode, RTreeNode newNode) {
        if (originalNode.getParent() == null) { // root
            System.out.println("adjust tree if: root");

            if (originalNode.overflow()) createNewRoot(originalNode);
            if(newNode != null) updateNodeCoordinates(newNode); // TODO: 3/21/21 necessary?

            updateNodeCoordinates(originalNode);

        } else if (newNode == null) {
            System.out.println("adjust else if: no split");
            updateNodeCoordinates(originalNode);
            adjustTree(originalNode.getParent(), null);
        } else {
            updateNodeCoordinates(originalNode);
            updateNodeCoordinates(newNode);
            if (originalNode.getParent().overflow()) {
                createNewParents(originalNode.getParent());
            }
        }
    }

    private void createNewParents(RTreeNode oldParent) {
        RTreeNode[] newParents = splitNodeShuffle(oldParent);
        adjustTree(newParents[0], newParents[1]);
    }

    private void createNewRoot(RTreeNode oldRoot) {
        RTreeNode[] rootChildren = splitNodeShuffle(oldRoot);
        RTreeNode newRoot = new RTreeNode(new float[numberOfCoordinates], false, minimumChildren, maximumChildren, null);
        newRoot.addChild(rootChildren[0]);
        newRoot.addChild(rootChildren[1]);
        updateNodeCoordinates(newRoot);
        root = newRoot;
    }

    private void updateNodeCoordinates(RTreeNode node) {
        for (RTreeNode childNode : node.getChildren()) {
            for (int i = 0; i < numberOfCoordinates; i += 2) {
                System.out.println("child: " + childNode.getCoordinates()[i] + " node: " + node.getCoordinates()[i]);
                System.out.println("child: " + childNode.getCoordinates()[i + 1] + " node: " + node.getCoordinates()[i + 1]);
                if (childNode.getCoordinates()[i] < node.getCoordinates()[i])
                    node.updateCoordinate(i, childNode.getCoordinates()[i]);
                if (childNode.getCoordinates()[i + 1] > node.getCoordinates()[i + 1])
                    node.updateCoordinate(i + 1, childNode.getCoordinates()[i + 1]);
            }
        }
    }

    private RTreeNode[] splitNodeShuffle(RTreeNode node) {

        ArrayList<RTreeNode> elementsToSplit = node.getChildren();
        Collections.shuffle(elementsToSplit);

        ArrayList<RTreeNode> childrenForOldNode = new ArrayList<>();
        ArrayList<RTreeNode> childrenForNewNode = new ArrayList<>();

        for (int i = 0; i < elementsToSplit.size(); i++) {
            if(i % 2 == 0) {
                childrenForOldNode.add(elementsToSplit.get(i));
            } else {
                childrenForNewNode.add(elementsToSplit.get(i));
            }
        }

        node.removeChildren();
        node.addChildren(childrenForOldNode);

        RTreeNode newNode = new RTreeNode(node.getCoordinates(), node.isLeaf(), minimumChildren, maximumChildren, node.getParent());
        newNode.addChildren(childrenForNewNode);

        return new RTreeNode[]{node, newNode};
    }

    /*
    private RTreeNode[] splitNodeExhaustive(RTreeNode leaf, NodeHolder nodeHolderToInsert) {

    }

    private RTreeNode splitNodeQuadraticCost(RTreeNode leaf) {

    }

    private RTreeNode splitNodeLinearCost(RTreeNode leaf) {

    }*/

    private float getNewBoundingBoxArea(NodeHolder nodeHolder, RTreeNode node) {
        float[] newCoordinates = new float[numberOfCoordinates];

        for (int i = 0; i < numberOfCoordinates; i += 2) {
            newCoordinates[i] = Math.min(nodeHolder.getCoordinates()[i], node.getCoordinates()[i]);
            newCoordinates[i + 1] = Math.max(nodeHolder.getCoordinates()[i + 1], node.getCoordinates()[i + 1]);
        }
        float area = 1;
        for (int j = 0; j < numberOfCoordinates - 1; j += 2) {
            area *= (newCoordinates[j + 1] - newCoordinates[j]);
        }
        return Math.abs(area);
    }

    private Boolean intersects(float[] coordinates1, float[] coordinates2) {
        for (int i = 0; i < numberOfCoordinates; i += 2) {
            if (doesNotIntersect(coordinates1[i], coordinates2[i + 1])) {
                return false;
            } else if (doesNotIntersect(coordinates2[i], coordinates1[i + 1])) {
                return false;
            }
        }
        return true;
    }

    private boolean doesNotIntersect(float minCoordinateFirstElement, float maxCoordinateSecondElement) {
        return minCoordinateFirstElement >= maxCoordinateSecondElement;
    }
}
