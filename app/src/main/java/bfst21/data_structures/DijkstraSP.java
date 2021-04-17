package bfst21.data_structures;

import bfst21.Osm_Elements.Node;
import bfst21.Osm_Elements.Relation;
import bfst21.Osm_Elements.Way;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class DijkstraSP {
    // TODO: 4/10/21 Add restrictions 
    // TODO: 4/10/21 Improve remove min
    // TODO: 4/10/21 Is distance between nodes correct?
    // TODO: 4/15/21 Walk and bike speed in Way

    // TODO: 4/16/21 Maybe wipe maps after use? 

    // TODO: 4/15/21 fastest and shortest for bike/walk should always be the same due to the same speed right? In that case, fastest/shortest selection does not make sense for walk/bike 

    private ElementToElementsTreeMap<Node, Way> nodeToWayMap;
    private ElementToElementsTreeMap<Node, Relation> nodeToRestriction;
    private Node from;
    private Node to;
    private HashMap<Long, Double> unitsTo;
    private HashMap<Long, Node> nodeBefore;
    private HashMap<Long, Way> wayBefore;
    private HashMap<Node, Double> pq;
    private boolean car;
    private boolean bike;
    private boolean walk;
    private boolean fastest;
    private double bikingSpeed;
    private double walkingSpeed;
    private double totalUnits;

    public DijkstraSP(ElementToElementsTreeMap<Node, Way> nodeToWayMap, ElementToElementsTreeMap<Node, Relation> nodeToRestriction) {
        this.nodeToRestriction = nodeToRestriction;
        this.nodeToWayMap = nodeToWayMap;
    }

    private void setup(Node from, Node to, boolean car, boolean bike, boolean walk, boolean fastest) {
        this.from = from;
        this.to = to;
        this.car = car;
        this.bike = bike;
        this.walk = walk;
        this.fastest = fastest;
        unitsTo = new HashMap<>();
        nodeBefore = new HashMap<>();
        wayBefore = new HashMap<>();
        pq = new HashMap<>();
        bikingSpeed = 16; // from Google Maps 16 km/h
        walkingSpeed = 5; // from Google Maps 5 km/h
        totalUnits = 0;
    }

    public ArrayList<Node> getPath(Node from, Node to, boolean car, boolean bike, boolean walk, boolean fastest) {
        setup(from, to, car, bike, walk, fastest);
        unitsTo.put(from.getId(), 0.0);
        pq.put(from, 0.0);

        Node n = null;
        while (!pq.isEmpty()) {
            n = temporaryRemoveAndGetMin();
            if (n != to) relax(n);
            else break;
        }
        if (n != to) {
            // TODO: 4/12/21 fix this / do something -> happens when a route cannot be found as the last node should be "to" node'en if it worked.
            System.err.println("Dijkstra: navigation is not possible with this from/to e.g. due to vehicle restrictions, island, etc.");
            return new ArrayList<>();
        } else {
            return getTrack(new ArrayList<>(), n);
        }
    }

    public double getTotalUnits() {
        return totalUnits; // TODO: 4/15/21 think its wrong... should be able to just do distanceto with current node 
    }

    private Node temporaryRemoveAndGetMin() { // TODO: 4/15/21 make more efficient 
        double minValue = Double.POSITIVE_INFINITY;
        Node minNode = null;

        for (Map.Entry<Node, Double> longDoubleEntry : pq.entrySet()) {
            if (longDoubleEntry.getValue() < minValue) {
                minValue = longDoubleEntry.getValue();
                minNode = longDoubleEntry.getKey();
            }
        }
        pq.remove(minNode);
        return minNode;
    }

    private ArrayList<Node> getTrack(ArrayList<Node> nodes, Node currentNode) {
        if (currentNode != null) {
            //System.out.println(unitsTo.get(currentNode.getId()));
            nodes.add(currentNode);
            totalUnits += unitsTo.get(currentNode.getId());
            getTrack(nodes, nodeBefore.get(currentNode.getId()));
        }
        return nodes;
    }

    private void relax(Node currentFrom) {
        ArrayList<Way> waysWithFromNode = nodeToWayMap.getElementsFromNode(currentFrom);
        ArrayList<Node> adjacentNodes = new ArrayList<>();

        HashMap<Node, Way> adjacentNodesWithWays = new HashMap<>(); // TODO: 4/17/21 fix it to implement this

        for (Way w : waysWithFromNode) {

            if (car) {
                if (w.isDriveable()) {
                    if (!w.isOnewayRoad()) {
                        getPreviousNode(adjacentNodesWithWays, w, currentFrom);
                    }
                    getNextNode(adjacentNodesWithWays, w, currentFrom);
                }
            } else if (bike) {
                if (w.isCycleable()) {
                    if (!w.isOneWayForBikes()) {
                        getPreviousNode(adjacentNodesWithWays, w, currentFrom);
                    }
                    getNextNode(adjacentNodesWithWays, w, currentFrom);
                }
            } else if (walk) {
                if (w.isWalkable()) {
                    getPreviousNode(adjacentNodesWithWays, w, currentFrom);
                    getNextNode(adjacentNodesWithWays, w, currentFrom);
                }
            }
            if (adjacentNodesWithWays.size() > 0) {
                for (Map.Entry<Node, Way> nodeWayEntry : adjacentNodesWithWays.entrySet()) {
                    if (!isThereARestriction(wayBefore.get(currentFrom.getId()), currentFrom, nodeWayEntry.getValue())) { // TODO: 4/16/21 should be moved so that the distance to a node is updated to very large if restriction apply
                        checkDistance(currentFrom, nodeWayEntry.getKey(), w);
                    } // TODO: 4/17/21 use the HM instead and send the fromWay = currentFromsWay, viaNode = currentFrom, toWay = currentTo way in the HM
                }
            }
        }
    }

    private void getPreviousNode(HashMap<Node, Way> adjacentNodesWithWays, Way w, Node currentFrom) {
        Node previousNode = w.getPreviousNode(currentFrom);
        if (previousNode != null) adjacentNodesWithWays.put(previousNode, w);
    }

    private void getNextNode(HashMap<Node, Way> adjacentNodesWithWays, Way w, Node currentFrom) {
        Node nextNode = w.getNextNode(currentFrom);
        if (nextNode != null) adjacentNodesWithWays.put(nextNode, w);
    }

    private boolean isThereARestriction(Way fromWay, Node viaNode, Way toWay) {
        ArrayList<Relation> restrictions = nodeToRestriction.getElementsFromNode(viaNode);

        if (restrictions != null) {
            for (Relation restriction : restrictions) {
                if (restriction.getFrom() == fromWay && restriction.getVia() == viaNode && restriction.getTo() == toWay) { // TODO: 4/16/21 er i tvivl om to way == via way...
                    return true;
                }
            }
        }
        return false;
    }

    private void checkDistance(Node currentFrom, Node currentTo, Way w) {
        long fromId = currentFrom.getId();
        long toId = currentTo.getId();

        double currentUnitsTo = unitsTo.get(toId) == null ? Double.POSITIVE_INFINITY : unitsTo.get(toId);
        double unitsBetweenFromTo = getDistanceBetweenTwoNodes(currentFrom, currentTo);
        if (fastest) {
            unitsBetweenFromTo = getTravelTime(unitsBetweenFromTo, w);
        }

        if (unitsTo.get(fromId) + unitsBetweenFromTo < currentUnitsTo) {
            unitsTo.put(toId, unitsTo.get(fromId) + unitsBetweenFromTo);
            nodeBefore.put(toId, currentFrom);
            wayBefore.put(toId, w);
            pq.put(currentTo, unitsTo.get(toId)); // do not need if else because updates if it is not there and inserts if not there
        }
    }

    private double getDistanceBetweenTwoNodes(Node from, Node to) { // TODO: 4/9/21 From mapcanvas w/small changes
        //Adapted from https://www.movable-type.co.uk/scripts/latlong.html
        //Calculations need y to be before x in a point.
        double earthRadius = 6371e3; //in meters

        double lat1 = Math.toRadians(convertToGeo(from.getyMax()));
        double lat2 = Math.toRadians(convertToGeo(to.getyMax()));
        double lon1 = from.getxMax();
        double lon2 = to.getxMax();

        double deltaLat = Math.toRadians(lat2 - lat1);
        double deltaLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) + Math.cos(lat1) * Math.cos(lat2) * Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return earthRadius * c;
    }

    private double convertToGeo(double value) {
        return -value * 0.56f;
    }

    private double getTravelTime(double distance, Way w) {
        double speed;
        if (bike) {
            speed = bikingSpeed;
        } else if (walk) {
            speed = walkingSpeed;
        } else {
            speed = w.getMaxSpeed();
        }
        return distance / speed;
    }

    private void printResult(ArrayList<Node> result) {
        int counter = 1;
        for (int i = result.size() - 1; i >= 0; i--) {
            System.out.println("");
            System.out.println("Node: " + counter + ", id: " + result.get(i).getId() + ", coordinates: " + Arrays.toString(result.get(i).getCoordinates()));
            System.out.println("Street(s) referenced:");
            System.out.println("");
            counter++;
        }
    }
}
