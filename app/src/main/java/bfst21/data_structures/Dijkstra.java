package bfst21.data_structures;

import bfst21.Osm_Elements.Node;

import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Stack;

public class Dijkstra {

    private Node from;
    private Node to;
    private String vehicleType;
    private String fastestOrShortest;
    private HashMap<Long, Double> distTo;
    private HashMap<Long, Node> edgeTo;
    private PriorityQueue<Node> pq;


    public Dijkstra(Node from, Node to, String vehicleType, String fastestOrShortest) {
        this.from = from;
        this.to = to;
        this.vehicleType = vehicleType;
        this.fastestOrShortest = fastestOrShortest;
        distTo = new HashMap<>();
        edgeTo = new HashMap<>();
        pq = new PriorityQueue<>();
    }

    private void relax() {

        /*for(Node e : v.adj()) {

        }*/
    }

    private double findTo(long v) {
        return distTo.get(v) == null ? Double.POSITIVE_INFINITY : findTo(v);
    }

    private boolean hasPathTo(long v) {
        return distTo.get(v) < Double.POSITIVE_INFINITY;
    }

    public void pathTo(long v) {
        if (!hasPathTo(v)) return;
        Stack<Node> path = new Stack<>();
        /*for (Node e = edgeTo.get(v); e != null; e = edgeTo.get(e.from()))
            path.push(e);
        return path;*/
    }
}
