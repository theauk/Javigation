package bfst21;


import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import bfst21.Osm_Elements.Node;

public class Node2DTree{
    private Node root;
    private final Comparator<Node> comparatorX = new Comparator<Node>() {

        @Override
        public int compare(Node p1, Node p2) {
            return Double.compare(p1.getX(), p2.getY());
        }
    };

    private final Comparator<Node> comparatorY = new Comparator<Node>() {

        @Override
        public int compare(Node p1, Node p2) {
            return Double.compare(p1.getY(), p2.getY());
        }
    };

   

    public Node2DTree(List<Node> nodes){
        buildTree(nodes);
    }


    private void buildTree(List<Node> nodes){
        nodes.sort(comparatorX);
        int lo = 0;
        int hi = nodes.size();
        int mid = (lo + hi) / 2;

        root = nodes.get(mid);
        root.setIsOnXAxis(true);        
        buildTree(true, nodes.subList(lo, mid), root);
        buildTree(false, nodes.subList(mid, hi-1), root);

    }

    
    private void buildTree(boolean isLeft,List<Node> nodes, Node parent){
        if (nodes == null || nodes.isEmpty()) return;

        nodes.sort(parent.IsOnXAxis() ? comparatorX : comparatorY);
        int lo = 0;
        int hi = nodes.size();
        int mid = (lo + hi) / 2;

        Node child = nodes.get(mid);
        child.setIsOnXAxis(!parent.IsOnXAxis());
        
        if(isLeft){
            parent.setLeftChild(child);
        } else{
            parent.setRightChild(child);
        }

        buildTree(true, nodes.subList(0, mid), child);
        buildTree(false, nodes.subList(mid, hi-1), child);
        
    }

    

   


    private int compareNodes(Node currentNode, Node node){
        if(currentNode.IsOnXAxis()){
            return compareTo(currentNode.getX(), node.getX());
        }
        else {
            return compareTo(currentNode.getY(), node.getY());
        }
        
    }
    private int compareTo(float currentNode, float node){
        // less than (return -1)    equal to (return 0)       greater than (return 1)
        if(currentNode < node){
            return -1;
        }
        if(currentNode > node){
            return 1;
        }
        else {
            //TODO edgecase??
            return 0;
        }
    }

     //TODO does the x and y coordinates from the screen match lon and lat?
     public Node getNearestNode(long lon, long lat){
        double shortestDistance;
        Node NearestNode;

        return null;
    }

    private float getDistance(Node from, long lon, long lat){
        //TODO 
        return 0;
    }
    
}
