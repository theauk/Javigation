package bfst21.data_structures;


import bfst21.Exceptions.KDTreeEmptyException;
import bfst21.Osm_Elements.Element;
import javafx.geometry.Point2D;

import java.util.*;

public class KDTree<Value extends Element> {
    private KDTreeNode root;
    private List<KDTreeNode> list;
    private boolean isSorted;
    private int removes = 0;
    public KDTreeNode theaRoot;
    private int startDim;
    private int numCor;
    private int numDim;

    public KDTree(int startDim, int numCor) {
        this.startDim = startDim;
        this.numCor = numCor;
        numDim = numCor / 2;
        list = new ArrayList<>();
        isSorted = false;
    }

    private final Comparator<KDTreeNode> comparatorX = new Comparator<KDTreeNode>() {
        @Override
        public int compare(KDTreeNode p1, KDTreeNode p2) {
            return Double.compare(p1.node.getxMax(), p2.node.getxMax());
        }
    };
    private final Comparator<KDTreeNode> comparatorY = new Comparator<KDTreeNode>() {
        @Override
        public int compare(KDTreeNode p1, KDTreeNode p2) {
            return Double.compare(p1.node.getyMax(), p2.node.getyMax());
        }
    };




    /*public void theaInsertAll(String name, List<Value> nodes) {
        for(Value node : nodes) {
            theaInsert(name, node);
        }
    }

    public void theaInsert(String name, Value node) { // TODO: 4/3/21 need add all for way with nodes (add all nodes)
        if(theaRoot == null) {
            theaRoot = new KDTreeNode(name, node);
            theaRoot.onXAxis = true; // TODO: 4/4/21 Get rid of axis stuff
        } else {
            theaInsert(name, node, theaRoot, 0, 4);
        }
    }

    public KDTreeNode theaInsert(String name, Value node, KDTreeNode currentNode, int cutDim, int numCor) {
        if (currentNode == null) {
            currentNode = new KDTreeNode(name, node);
            if(cutDim == 0) {
                currentNode.onXAxis = true; // TODO: 4/4/21 Get rid of axis stuff
            } else {
                currentNode.onXAxis = false;
            }
        } else {
            if (!Arrays.equals(node.getCoordinates(), currentNode.node.getCoordinates())) { // To avoid duplicates
                if (node.getCoordinates()[cutDim] <= currentNode.node.getCoordinates()[cutDim]) {
                    currentNode.leftChild = theaInsert(name, node, currentNode.leftChild, (cutDim + 2) % numCor, numCor);
                } else if (node.getCoordinates()[cutDim] > currentNode.node.getCoordinates()[cutDim]) {
                    currentNode.rightChild = theaInsert(name, node, currentNode.rightChild, (cutDim + 2) % numCor, numCor);
                }
            }
        }
        return currentNode;
    }*/



    private Comparator<KDTreeNode> getComparatorFromDimension(int dim) {
        return dim == 0 ? comparatorX : comparatorY;
    }

    public int getMedian(int low, int high) {
        return (low + high) / 2; // TODO: 4/4/21 minus one? Otherwise it always takes an index higher?
    }

    public void addAll(String name, List<Value> nodes) {
        for (Value node : nodes) {
            list.add(new KDTreeNode(name, node));
        }
    }

    /*private void buildTree() throws KDTreeEmptyException {
        if (list.isEmpty()) {
            throw new KDTreeEmptyException("No nodes in the kd-tree");
        }
        list.sort(getComparatorFromDimension(startDim));
        int lo = 0;
        int hi = list.size();
        int mid = getMedian(lo, hi);

        root = list.get(mid);
        buildTree(list.subList(mid + 1, hi), 0);
        buildTree(list.subList(0, mid), 0);
    }*/

    public void testBuild() { // TODO: 4/4/21 for debugging 
        buildTree(list, startDim, null);
    }

    private KDTreeNode buildTree(List<KDTreeNode> nodes, int dim, KDTreeNode parent) {
        if (nodes.isEmpty()) {
            return null;
        }

        Comparator<KDTreeNode> comp = getComparatorFromDimension(dim % numCor);
        nodes.sort(comp);

        int med = getMedian(0, nodes.size());
        KDTreeNode medNode = nodes.get(med);
        medNode.onXAxis = dim % numCor == 0;

        if (root == null) {
            root = medNode;
        } else if (Arrays.equals(parent.node.getCoordinates(), medNode.node.getCoordinates())) { // to avoid duplicates
            return null;
        }

        medNode.leftChild = buildTree(nodes.subList(0, med), dim + numDim, medNode);
        medNode.rightChild = buildTree(nodes.subList(med + 1, nodes.size()), dim + numDim, medNode);

        return medNode;



        /*List<KDTreeNode> nodesCopy;

        if (isParentAndNextMidSame(nodes, parent)) {
            removeDuplicates(nodes, (nodes.size()) / 2);
            nodesCopy = new ArrayList<>(nodes);
        } else {
            nodesCopy = nodes;
        }

        if (nodesCopy.isEmpty()) {
            return;
        }

        nodesCopy.sort(parent.onXAxis ? comparatorX : comparatorY);


        KDTreeNode child = nodesCopy.get(nodesCopy.size() / 2);

        child.onXAxis = !parent.onXAxis;

        if (isLeft) {
            parent.leftChild = child;
        } else {
            parent.rightChild = child;
        }

        buildTree(true, nodesCopy.subList(0, nodesCopy.size() / 2), child);
        if ((nodesCopy.size() / 2) + 1 < nodesCopy.size()) {
            buildTree(false, nodesCopy.subList(nodesCopy.size() / 2 + 1, nodesCopy.size()), child);
        }*/

    }

    /*private void removeDuplicates(List<KDTreeNode> nodes, int mid) {
        removes += 1;
        nodes.remove(mid);
    }

    private boolean isParentAndNextMidSame(List<KDTreeNode> nodes, KDTreeNode parent) {
        int lo = 0;
        int hi = nodes.size();
        int mid = (lo + hi) / 2;
        return (nodes.get(mid).node.getxMax() == parent.node.getxMax() && nodes.get(mid).node.getyMax() == parent.node.getyMax());
    }*/





    public String getNearestNode(float x, float y) throws KDTreeEmptyException {
        if (!isSorted) {
            buildTree(list, startDim, null);
            isSorted = true;
        }
        double shortestDistance = Double.POSITIVE_INFINITY;
        KDTreeNode nearestNode = getNearestNode(root, x, y, shortestDistance, null, true);
        return nearestNode.name;

    }

    private KDTreeNode getNearestNode(KDTreeNode currentNode, float x, float y, double shortestDistance, KDTreeNode nearestNode, Boolean xAxis) {
        if (currentNode == null) {
            return nearestNode;
        }

        double newDistance = getDistance(currentNode, x, y);
        if (newDistance < shortestDistance) {
            shortestDistance = newDistance;
            nearestNode = currentNode;
        }

        //checks if we should search the left or right side of the tree first, to save time/space.
        double compare = xAxis ? Math.abs(x - currentNode.node.getxMax()) : Math.abs(y - currentNode.node.getyMax());

        KDTreeNode node1 = compare < 0 ? currentNode.leftChild : currentNode.rightChild; // TODO: 4/3/21 THEA: compare kan aldrig være negativ grundet Math.abs så her er det altid right child
        KDTreeNode node2 = compare < 0 ? currentNode.rightChild : currentNode.leftChild; // TODO: 4/3/21 THEA: og her er det altid left child

        nearestNode = getNearestNode(node1, x, y, shortestDistance, nearestNode, !xAxis);

        // Checks if its worth checking on the other side of tree.
        if (possibleCloserNode(shortestDistance, currentNode, x, y)) {

            nearestNode = getNearestNode(node2, x, y, shortestDistance, nearestNode, !xAxis);
        }
        return nearestNode;

    }

    private boolean possibleCloserNode(Double shortestDistance, KDTreeNode currentNode, float x, float y) {
        double possibleNewDistance = Math.abs(currentNode.onXAxis ? x - currentNode.node.getxMax() : y - currentNode.node.getyMax());
        return shortestDistance > Math.abs(possibleNewDistance);
    }

    private double getDistance(KDTreeNode from, float x, float y) {
        Point2D p = new Point2D(x, y);
        return p.distance(from.node.getxMax(), from.node.getyMax());
    }

    // TODO: 26-03-2021 remove both print methods when no longer needed.
    public void printTree() {
        Integer level = 1;
        HashMap<Integer, ArrayList<KDTreeNode>> result = new HashMap<>();
        result = getPrintTree(root, level, result);

        while (result.get(level) != null) {
            System.out.println("");
            System.out.println("Level: " + level);
            for (KDTreeNode node : result.get(level)) {
                System.out.println("Node id: " + node.node.getId() + " : x: " + node.node.getxMax() + " y: " + node.node.getyMax() + " axis: " + node.onXAxis + " name: " + node.name);
                if (node.leftChild != null) {
                    System.out.println("Has left child, id: " + node.leftChild.node.getId() + " name: " + node.leftChild.name);
                }
                if (node.rightChild != null) {
                    System.out.println("Has right child, id: " + node.rightChild.node.getId()+ " name: " + node.rightChild.name);
                }
            }
            level++;
        }
        System.out.println("");
    }

    private HashMap<Integer, ArrayList<KDTreeNode>> getPrintTree(KDTreeNode node, Integer level, HashMap<Integer, ArrayList<KDTreeNode>> result) {
        if (node != null) {
            if (result.get(level) == null) {
                ArrayList<KDTreeNode> newAL = new ArrayList<>();
                newAL.add(node);
                result.put(level, newAL);
            } else {
                ArrayList<KDTreeNode> current = result.get(level);
                current.add(node);
                result.put(level, current);
            }
            level += 1;
            int levelCopy = level;

            getPrintTree(node.leftChild, level, result);
            getPrintTree(node.rightChild, levelCopy, result);
        }
        return result;
    }

    private class KDTreeNode {
        private String name;
        private Value node;
        private KDTreeNode leftChild;
        private KDTreeNode rightChild;
        private Boolean onXAxis;


        public KDTreeNode(String name, Value node) {
            this.node = node;
            this.name = name;
        }
    }
}