package bfst21.data_structures;

import bfst21.Osm_Elements.NodeHolder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class RTree {
    private int minimumChildren, maximumChildren, size, numberOfCoordinates;
    private RTreeNode root;
    private int idCount; // TODO: 3/22/21 delete

    public RTree(int minimumChildren, int maximumChildren, int numberOfCoordinates) {
        this.minimumChildren = minimumChildren;
        this.maximumChildren = maximumChildren;
        this.numberOfCoordinates = numberOfCoordinates;
        root = null;
        size = 0;
        idCount = 0;
    }

    public RTreeNode getRoot() {
        return root;
    }

    // TODO: 3/17/21 delete both root methods later
    public void setRoot(RTreeNode n) {
        root = n;
    }

    public float[] createNewCoordinateArray() {
        return new float[]{Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY};
    }

    private int getId() {
        idCount += 1;
        return idCount;
    }

    public List<NodeHolder> search(float xMin, float xMax, float yMin, float yMax) {
        float[] searchCoordinates = new float[]{xMin, xMax, yMin, yMax};
        ArrayList<NodeHolder> results = new ArrayList<>();
        search(searchCoordinates, root, results);
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
        if (root == null) { // if there are no roots we need to create a new one which points to its data
            System.out.println("creating first root with leaf true");
            root = new RTreeNode(nodeHolder.getCoordinates(), true, minimumChildren, maximumChildren, null, getId());
            RTreeNode dataLeaf = new RTreeNode(nodeHolder.getCoordinates(), false, minimumChildren, maximumChildren, root, getId());
            root.addChild(dataLeaf);
        } else {
            System.out.println("root is already created -> invoke selectNode");
            RTreeNode selectedNode = chooseLeaf(nodeHolder, root); // select where to place the new node
            System.out.println("selected node id: " + selectedNode.id);
            System.out.println("selected node coordinates: " + Arrays.toString(selectedNode.getCoordinates()) + " with parent: " + selectedNode.getParent());

            // need to create new data entry node in the selected node. So we set leaf as false
            // we set its parent as the node that will hold it as a child
            RTreeNode newEntry = new RTreeNode(nodeHolder.getCoordinates(), false, minimumChildren, maximumChildren, selectedNode, getId());
            newEntry.addNodeHolderEntry(nodeHolder);
            selectedNode.addChild(newEntry);

            if (selectedNode.overflow()) {
                System.out.println("the selected node overflows -> split the node");
                RTreeNode[] result = splitNodeShuffle(selectedNode);
                System.out.println("the new node(s): " + result[0] + " " + result[1]);
                System.out.println("nr. children for new node(s): " + result[0].getChildren().size() + " " + result[1].getChildren().size());
                System.out.println("first coordinate: " + Arrays.toString(result[0].getCoordinates()));
                System.out.println("second coordinate: " + Arrays.toString(result[1].getCoordinates()));
                for (RTreeNode r : result[0].getChildren()) {
                    System.out.println("first: " + Arrays.toString(r.getCoordinates()));
                }
                for (RTreeNode r : result[1].getChildren()) {
                    System.out.println("second : " + Arrays.toString(r.getCoordinates()));
                }
                // adjust the new nodes
                adjustTree(result[0], result[1]);
            } else {
                System.out.println("the selected node does not overflow");
                adjustTree(selectedNode, null);
            }
        }
    }

    private RTreeNode chooseLeaf(NodeHolder nodeHolder, RTreeNode node) {
        if (node.isLeaf()) {
            System.out.println("is leaf");
            return node;
        } else {
            System.out.println("choose leaf else");
            System.out.println("nr of children: " + node.getChildren().size());
            ArrayList<RTreeNode> children = node.getChildren();
            RTreeNode smallestBoundingBoxNode = children.get(0);
            System.out.println("0 cor: " + Arrays.toString(smallestBoundingBoxNode.getCoordinates()));
            for (int i = 1; i < node.getChildren().size(); i++) {
                System.out.println("cor child: " + Arrays.toString(node.getChildren().get(i).getCoordinates()));
                if (getNewBoundingBoxArea(nodeHolder, children.get(i)) < getNewBoundingBoxArea(nodeHolder, smallestBoundingBoxNode)) {
                    smallestBoundingBoxNode = children.get(i);
                }
            }
            return chooseLeaf(nodeHolder, smallestBoundingBoxNode);
        }
    }

    private void adjustTree(RTreeNode originalNode, RTreeNode newNode) {
        if(originalNode.getParent() == null && newNode == null) { // only root
            System.out.println("we need to adjust the root");
            updateNodeCoordinates(originalNode);
        } else if (originalNode.getParent() == null && newNode != null) {// need to join them under one root
            System.out.println("we need to create new root");
            createNewRoot(originalNode, newNode);
            adjustTree(root, null);
        } else if (newNode == null) {
            System.out.println("adjust: no split");
            updateNodeCoordinates(originalNode);
            adjustTree(originalNode.getParent(), null);
        } else { // not root but two new nodes need to climb the tree
            System.out.println("");
            System.out.println("update node coordinates original node");
            updateNodeCoordinates(originalNode);
            System.out.println("");
            System.out.println("update node coordinates new node");
            updateNodeCoordinates(newNode);

            if (originalNode.getParent().overflow()) {
                System.out.println("The parent overflows");
                createNewParents(originalNode.getParent()); // TODO: 3/22/21 what if root? 
            }
            adjustTree(originalNode.getParent(), newNode.getParent());
        }
    }
 
    private void createNewParents(RTreeNode oldParent) {
        System.out.println("create new parents");
        RTreeNode[] newParents = splitNodeShuffle(oldParent);
        adjustTree(newParents[0], newParents[1]);
    }

    private void createNewRoot(RTreeNode firstNode, RTreeNode secondNode) {
        // the new root is not a leaf. Use the coordinates from one of the nodes to avoid problems with 0
        RTreeNode newRoot = new RTreeNode(createNewCoordinateArray(), false, minimumChildren, maximumChildren, null, getId());
        newRoot.addChild(firstNode);
        newRoot.addChild(secondNode);
        root = newRoot;
        System.out.println("new parent of nodes below root: " + firstNode.getParent() + " " + secondNode.getParent());
        System.out.println("should be null as root has no parent: " + firstNode.getParent().getParent());
        System.out.println("adjust the tree for the two new nodes below the root");
        //adjustTree(firstNode, secondNode);
        System.out.println("");
        System.out.println("new coordinates of first node: " + Arrays.toString(firstNode.getCoordinates()));
        System.out.println("new coordinates of second node: " + Arrays.toString(secondNode.getCoordinates()));
    }

    private void updateNodeCoordinates(RTreeNode node) {
        for (RTreeNode childNode : node.getChildren()) {
            System.out.println("");
            System.out.println("new child");
            for (int i = 0; i < numberOfCoordinates; i += 2) {
                System.out.println("i is: " + i);
                System.out.println("MIN child: " + childNode.getCoordinates()[i] + " node: " + node.getCoordinates()[i]);
                if (childNode.getCoordinates()[i] < node.getCoordinates()[i]) {
                    node.updateCoordinate(i, childNode.getCoordinates()[i]);
                    System.out.println("update min coordinate to: " + childNode.getCoordinates()[i]);
                }
                System.out.println("MAX child: " + childNode.getCoordinates()[i + 1] + " node: " + node.getCoordinates()[i + 1]);
                if (childNode.getCoordinates()[i + 1] > node.getCoordinates()[i + 1]) {
                    node.updateCoordinate(i + 1, childNode.getCoordinates()[i + 1]);
                    System.out.println("update max coordinate to: " + childNode.getCoordinates()[i + 1]);
                }
            }
        }
    }

    private RTreeNode[] splitNodeShuffle(RTreeNode node) {

        ArrayList<RTreeNode> elementsToSplit = node.getChildren();
        Collections.shuffle(elementsToSplit);

        ArrayList<RTreeNode> childrenForOldNode = new ArrayList<>();
        ArrayList<RTreeNode> childrenForNewNode = new ArrayList<>();

        for (int i = 0; i < elementsToSplit.size(); i++) {
            if (i % 2 == 0) {
                childrenForOldNode.add(elementsToSplit.get(i));
            } else {
                childrenForNewNode.add(elementsToSplit.get(i));
            }
        }

        node.removeChildren();
        node.addChildren(childrenForOldNode);

        RTreeNode newNode = new RTreeNode(createNewCoordinateArray(), node.isLeaf(), minimumChildren, maximumChildren, node.getParent(), getId());
        newNode.addChildren(childrenForNewNode);

        System.out.println("");
        System.out.println(Arrays.toString(createNewCoordinateArray()));
        System.out.println("in split first coordinates: " + Arrays.toString(node.getCoordinates()));
        System.out.println("in split second coordinates: " + Arrays.toString(newNode.getCoordinates()));
        System.out.println("");

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

    public void printTree() {
        int level = 0;
        printTree(root, level);
        System.out.println("");
    }

    public void printTree(RTreeNode theRoot, int level) {
        System.out.println("");
        System.out.println("Level: " + level);
        level += 1;
        if(theRoot != null) {
            if(theRoot.getParent() != null) {
                System.out.println("Node: " + theRoot.id + " coor: " + Arrays.toString(theRoot.getCoordinates()) + " parent: " + theRoot.getParent().id);
            } else {
                System.out.println("Node: " + theRoot.id + " coor: " + Arrays.toString(theRoot.getCoordinates()) + " parent: NONE");
            }
            for(RTreeNode child : theRoot.getChildren()) {
                printTree(child, level);
            }
        }
    }
}
