package bfst21.data_structures;

import bfst21.Exceptions.NoNavigationResultException;
import bfst21.MapMath;
import bfst21.Osm_Elements.Node;
import bfst21.Osm_Elements.Relation;
import bfst21.Osm_Elements.Way;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;

public class RouteNavigation implements Serializable {
    @Serial
    private static final long serialVersionUID = -488598808136557757L;
    
    private Node to;
    private ElementToElementsTreeMap<Node, Way> nodeToWayMap;
    private ElementToElementsTreeMap<Node, Relation> nodeToRestriction;
    private ElementToElementsTreeMap<Way, Relation> wayToRestriction;
    private ArrayList<Node> path;
    private HashMap<Node, DistanceAndTimeEntry> unitsTo;
    private HashMap<Node, Node> nodeBefore;
    private HashMap<Node, Way> wayBefore;
    private PriorityQueue<Node> pq;
    private boolean car;
    private boolean bike;
    private boolean walk;
    private boolean fastest;
    private boolean needToCheckUTurns;
    private boolean aStar;
    private double bikingSpeed;
    private double walkingSpeed;
    private int maxSpeed;

    private double currentDistanceDescription;
    private double currentTimeDescription;
    private ArrayList<String> routeDescription;
    private HashSet<String> specialPathFeatures;

    public RouteNavigation(ElementToElementsTreeMap<Node, Way> nodeToWayMap, ElementToElementsTreeMap<Node, Relation> nodeToRestriction, ElementToElementsTreeMap<Way, Relation> wayToRestriction) {
        this.nodeToRestriction = nodeToRestriction;
        this.wayToRestriction = wayToRestriction;
        this.nodeToWayMap = nodeToWayMap;
        this.maxSpeed = 130;
    }

    private void setup(Node from, Node to, boolean car, boolean bike, boolean walk, boolean fastest, boolean aStar) {
        this.to = to;
        this.car = car;
        this.bike = bike;
        this.walk = walk;
        this.fastest = fastest;
        this.aStar = aStar;
        needToCheckUTurns = false;
        unitsTo = new HashMap<>();
        nodeBefore = new HashMap<>();
        wayBefore = new HashMap<>();
        pq = new PriorityQueue<>((a, b) -> Integer.compare(unitsTo.get(a).compareTo(unitsTo.get(b)), 0)); // different comparator
        bikingSpeed = 16; // from Google Maps 16 km/h
        walkingSpeed = 5; // from Google Maps 5 km/h
        pq.add(from);
        unitsTo.put(from, new DistanceAndTimeEntry(0, 0, 0));
        routeDescription = new ArrayList<>();
        specialPathFeatures = new HashSet<>();
    }

    /**
     * Gets either the fastest or the shortest path between two Nodes.
     *
     * @param from    The from Node.
     * @param to      The to Node.
     * @param car     True if travelling by car. Otherwise, false.
     * @param bike    True if travelling by bike. Otherwise, false.
     * @param walk    True if walking. Otherwise, false.
     * @param fastest True if fastest route needs to be found. False if shortest route should be found.
     * @param aStar   True if the A* algorithm should be used. False if Dijkstra should be used.
     * @return An ArrayList with Nodes that make up the path in reverse order.
     * @throws NoNavigationResultException If no route can be found.
     */
    public ArrayList<Node> getPath(Node from, Node to, boolean car, boolean bike, boolean walk, boolean fastest, boolean aStar) throws NoNavigationResultException {
        setup(from, to, car, bike, walk, fastest, aStar);
        Node n = checkNode();

        if (n != to) {
            setup(from, to, car, bike, walk, fastest, aStar);
            needToCheckUTurns = true; // TODO: 4/19/21 really not the most beautiful thing... for u-turns
            n = checkNode();
            if (n != to) throw new NoNavigationResultException();
        }
        path = getTrack(new ArrayList<>(), n);

        getRouteDescription();
        return path;
    }

    /**
     * Get the total distance for the path.
     *
     * @return The total distance.
     */
    public double getTotalDistance() {
        if (unitsTo.get(to) != null) return unitsTo.get(to).distance; // TODO: 4/26/21 back to exception instead?
        else return 0;
    }

    /**
     * Gets the total travelling time for the path.
     *
     * @return The total travelling time.
     */
    public double getTotalTime() {
        if (unitsTo.get(to) != null) return unitsTo.get(to).time;
        else return 0;
    }

    /**
     * Gets a list of the directions to navigate the computed route.
     * @return An ArrayList with the directions where each entry is a navigation step.
     */
    public ArrayList<String> getDirections() {
        return routeDescription;
    }

    /**
     * Gets a list of special features on a path such as if it is necessary to take a ferry, if the route has tolls, etc. // TODO: 4/29/21 toll??
     * @return A list of special path features.
     */
    public HashSet<String> getSpecialPathFeatures() {
        return specialPathFeatures;
    }

    /**
     * Checks the next node in the priority queue with the smallest units/cost.
     *
     * @return The final Node found for the path.
     */
    private Node checkNode() {
        Node n = null;
        while (!pq.isEmpty()) {
            n = pq.poll();
            if (n != to) relax(n);
            else break;
        }
        return n;
    }

    /**
     * Gets a list of the Nodes that make up the path.
     *
     * @param nodes       A list of the Nodes making up the path.
     * @param currentNode The Node which should be checked for the Node before it.
     * @return A list of the Nodes making up the path in reverse order (to first and from at the end).
     */
    private ArrayList<Node> getTrack(ArrayList<Node> nodes, Node currentNode) {
        if (currentNode != null) {
            nodes.add(currentNode);
            getTrack(nodes, nodeBefore.get(currentNode));
        }
        return nodes;
    }

    /**
     * Relaxes the Nodes adjacent to the current Node.
     *
     * @param currentFrom The current Node to examine.
     */
    private void relax(Node currentFrom) {
        ArrayList<Way> waysWithFromNode = nodeToWayMap.getElementsFromNode(currentFrom);

        for (Way w : waysWithFromNode) {
            ArrayList<Node> adjacentNodes = new ArrayList<>();

            if (car) {
                if (w.isDriveable()) {
                    if (!w.isOnewayRoad()) {
                        getPreviousNode(adjacentNodes, w, currentFrom);
                    }
                    getNextNode(adjacentNodes, w, currentFrom);
                }
            } else if (bike) {
                if (w.isCycleable()) {
                    if (!w.isOneWayForBikes()) {
                        getPreviousNode(adjacentNodes, w, currentFrom);
                    }
                    getNextNode(adjacentNodes, w, currentFrom);
                }
            } else {
                if (w.isWalkable()) {
                    getPreviousNode(adjacentNodes, w, currentFrom);
                    getNextNode(adjacentNodes, w, currentFrom);
                }
            }
            if (!adjacentNodes.isEmpty()) {
                for (Node n : adjacentNodes) {
                    if (!isThereARestriction(wayBefore.get(currentFrom), currentFrom, w)) {
                        if (aStar) {
                            checkDistanceAStar(currentFrom, n, w);
                        } else {
                            checkDistanceDijkstra(currentFrom, n, w);
                        }
                    }
                }
            }
        }
    }

    /**
     * Gets the Node before a specified Node on a certain Way.
     *
     * @param adjacentNodes The list of adjacent Nodes to the current from Node.
     * @param w             The current Way.
     * @param currentFrom   The current from Node.
     */
    private void getPreviousNode(ArrayList<Node> adjacentNodes, Way w, Node currentFrom) {
        Node previousNode = w.getPreviousNode(currentFrom);
        if (previousNode != null) adjacentNodes.add(previousNode);
    }

    /**
     * Gets the next Node after a specified Node on a certain Way.
     *
     * @param adjacentNodes The list of adjacent Nodes to the current from Node.
     * @param w             The current Way.
     * @param currentFrom   The current from Node.
     */
    private void getNextNode(ArrayList<Node> adjacentNodes, Way w, Node currentFrom) {
        Node nextNode = w.getNextNode(currentFrom);
        if (nextNode != null) adjacentNodes.add(nextNode);
    }

    /**
     * Checks if is a _no restriction (e.g. no left-turn).
     *
     * @param fromWay The Way the path is coming from.
     * @param viaNode The Node the path is trying to go via.
     * @param toWay   The Way the path is trying to go to.
     * @return True if there is a restriction. False if not.
     */
    private boolean isThereARestriction(Way fromWay, Node viaNode, Way toWay) {
        if (checkRestrictionViaNode(fromWay, viaNode, toWay)) return true;
        if (fromWay != null) return checkRestrictionViaWay(fromWay, viaNode, toWay);
        return false;
    }

    /**
     * Checks if there is a _no restriction via a specified Node.
     *
     * @param fromWay The Way the path is coming from.
     * @param viaNode The Node the path is trying to go via.
     * @param toWay   The Way the path is trying to go to.
     * @return True if there is a restriction. False if not.
     */
    private boolean checkRestrictionViaNode(Way fromWay, Node viaNode, Way toWay) {
        ArrayList<Relation> restrictionsViaNode = nodeToRestriction.getElementsFromNode(viaNode);
        if (restrictionsViaNode != null) {
            for (Relation restriction : restrictionsViaNode) {
                if (restriction.getRestriction().contains("no_") && restriction.getFrom() == fromWay && restriction.getViaNode() == viaNode && restriction.getTo() == toWay) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Checks if there is a _no restriction via a specified Way.
     *
     * @param fromWay The Way the path is coming from.
     * @param viaNode The Node the path is trying to go via.
     * @param toWay   The Way the path is trying to go to.
     * @return True if there is a restriction. False if not.
     */
    private boolean checkRestrictionViaWay(Way fromWay, Node viaNode, Way toWay) {
        ArrayList<Relation> restrictionsViaWay = wayToRestriction.getElementsFromNode(fromWay);
        if (restrictionsViaWay != null) {
            for (Relation restriction : restrictionsViaWay) {
                if (restriction.getRestriction().contains("no_") && restriction.getViaWay() == fromWay && restriction.getTo() == toWay) {
                    Node beforeNode = nodeBefore.get(viaNode);

                    while (wayBefore.get(beforeNode) == fromWay) { // "walk back" until finding a Node on a different Way
                        beforeNode = nodeBefore.get(beforeNode);
                    }

                    if (wayBefore.get(beforeNode) == restriction.getFrom()) { // find the Way with the Node – then check if that is the restriction's from Way
                        if (needToCheckUTurns)
                            unitsTo.put(viaNode, new DistanceAndTimeEntry(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Find the cost between two Nodes to update the priority queue.
     *
     * @param currentFrom The from Node.
     * @param currentTo   The to Node.
     * @param w           The Way between the two Nodes.
     */
    private void checkDistanceAStar(Node currentFrom, Node currentTo, Way w) {
        double currentCost = unitsTo.get(currentTo) == null ? Double.POSITIVE_INFINITY : unitsTo.get(currentTo).cost;

        double distanceBetweenFromTo = MapMath.distanceBetweenTwoNodes(currentFrom, currentTo);
        double timeBetweenFromTo = getTravelTime(distanceBetweenFromTo, w);

        if (fastest) {
            double unitsToCurrentTo = unitsTo.get(currentFrom).time + timeBetweenFromTo;
            double unitsCurrentToToFinalTo = MapMath.distanceBetweenTwoNodes(currentTo, to) / maxSpeed;
            double newCost = unitsToCurrentTo + unitsCurrentToToFinalTo;
            if (newCost < currentCost) {
                updateMapsAndPQ(currentTo, currentFrom, w, distanceBetweenFromTo, timeBetweenFromTo, newCost);
            }
        } else {
            double unitsToCurrentTo = unitsTo.get(currentFrom).distance + distanceBetweenFromTo;
            double unitsCurrentToToFinalTo = MapMath.distanceBetweenTwoNodes(currentTo, to);
            double newCost = unitsToCurrentTo + unitsCurrentToToFinalTo;
            if (newCost < currentCost) {
                updateMapsAndPQ(currentTo, currentFrom, w, distanceBetweenFromTo, timeBetweenFromTo, newCost);
            }
        }
    }

    /**
     * Find the units between two Nodes to update the priority queue.
     *
     * @param currentFrom The from Node.
     * @param currentTo   The to Node.
     * @param w           The Way between the Nodes.
     */
    private void checkDistanceDijkstra(Node currentFrom, Node currentTo, Way w) {
        double currentDistanceTo = unitsTo.get(currentTo) == null ? Double.POSITIVE_INFINITY : unitsTo.get(currentTo).distance;
        double currentTimeTo = unitsTo.get(currentTo) == null ? Double.POSITIVE_INFINITY : unitsTo.get(currentTo).time;

        double distanceBetweenFromTo = MapMath.distanceBetweenTwoNodes(currentFrom, currentTo);
        double timeBetweenFromTo = getTravelTime(distanceBetweenFromTo, w);

        if (fastest) {
            double newCost = unitsTo.get(currentFrom).time + timeBetweenFromTo;
            if (newCost < currentTimeTo) {
                updateMapsAndPQ(currentTo, currentFrom, w, distanceBetweenFromTo, timeBetweenFromTo, newCost);
            }
        } else {
            double newCost = unitsTo.get(currentFrom).distance + distanceBetweenFromTo;
            if (newCost < currentDistanceTo) {
                updateMapsAndPQ(currentTo, currentFrom, w, distanceBetweenFromTo, timeBetweenFromTo, newCost);
            }
        }
    }

    /**
     * Update the maps with new distance and time along with the unit/cost priority queue.
     *
     * @param currentTo             The from Node.
     * @param currentFrom           The to Node.
     * @param w                     The Way between the Nodes.
     * @param distanceBetweenFromTo The distance between the two Nodes.
     * @param timeBetweenFromTo     The travelling time between the two Nodes.
     * @param newCost               The cost between the two Nodes.
     */
    private void updateMapsAndPQ(Node currentTo, Node currentFrom, Way w, double distanceBetweenFromTo, double timeBetweenFromTo, double newCost) {
        nodeBefore.put(currentTo, currentFrom);
        wayBefore.put(currentTo, w);
        if (unitsTo.containsKey(currentTo))
            pq.remove(currentTo); //TODO: 4/23/21 før var check + tilføj til pq O(1) fordi det var HM. NU: check er O(1) mens remove og add er log
        unitsTo.put(currentTo, new DistanceAndTimeEntry(unitsTo.get(currentFrom).distance + distanceBetweenFromTo, unitsTo.get(currentFrom).time + timeBetweenFromTo, newCost));
        pq.add(currentTo);
    }

    /**
     * Gets the travel time for a given distance and speed.
     *
     * @param distance The distance to be used for the calculation.
     * @param w        The Way used to find the speed if travelling by car.
     * @return The travelling time.
     */
    private double getTravelTime(double distance, Way w) {
        double speed;
        if (bike) speed = bikingSpeed;
        else if (walk) speed = walkingSpeed;
        else speed = w.getMaxSpeed();
        return distance / (speed * (5f / 18f));
    }

    /**
     * Gets the route description.
     */
    private void getRouteDescription() { 
        currentDistanceDescription = unitsTo.get(path.get(path.size() - 2)).distance - unitsTo.get(path.get(path.size() - 1)).distance;
        currentTimeDescription = unitsTo.get(path.get(path.size() - 2)).time - unitsTo.get(path.get(path.size() - 1)).time;

        if (path.size() >= 3) getRouteDescriptionMoreThanTwoNodes();
        else getRouteDescriptionLessThanThreeNodes();
    }

    /**
     * Gets the route description for a path that has less than three nodes.
     */
    private void getRouteDescriptionLessThanThreeNodes() {
        Node f = path.get(path.size() - 1);
        Node t = path.get(path.size() - 2);
        routeDescription.add("Head " + MapMath.compassDirection(f, t).toLowerCase() + " on " + wayBefore.get(t).getName() + " and you will arrive at your destination" + getCurrentDistanceAndTimeText());
    }

    /**
     * Gets the route description for a path that has more than two nodes.
     */
    private void getRouteDescriptionMoreThanTwoNodes() {
        boolean roundabout = false;
        boolean keepRight = false;
        boolean ferry = false;
        int roundAboutStartNodeIndex = 0;

        for (int i = path.size() - 1; i >= 2; i--) {
            Node f = path.get(i);
            Node v = path.get(i - 1);
            Node t = path.get(i - 2);
            Way wayBeforeVia = wayBefore.get(v);
            Way wayBeforeTo = wayBefore.get(t);
            String wayBeforeViaName = wayBeforeVia.getName() != null ? wayBeforeVia.getName() : "unnamed road";
            String wayBeforeToName = wayBeforeTo.getName() != null ? wayBeforeTo.getName() : "unnamed road";

            if (!wayBeforeViaName.equals(wayBeforeToName)) {
                if (roundabout) {
                    routeDescription.add(getRoundaboutText(roundAboutStartNodeIndex, i, wayBeforeVia, wayBeforeToName));
                    roundabout = false;
                } else if (ferry) {
                    routeDescription.add(getFerryText(wayBeforeViaName));
                    ferry = false;
                } else {
                    if (keepRight) {
                        routeDescription.add(getKeepRightText(wayBeforeViaName));
                        keepRight = false;
                    } else {
                        routeDescription.add("Follow " + wayBeforeViaName + getCurrentDistanceAndTimeText());
                    }

                    currentDistanceDescription = unitsTo.get(t).distance - unitsTo.get(v).distance;
                    currentTimeDescription = unitsTo.get(t).time - unitsTo.get(v).time;

                    String directionBetweenViaAndToWay = getDirection(MapMath.turnAngle(f, v, t), wayBeforeTo, wayBeforeToName);
                    switch (directionBetweenViaAndToWay) {
                        case "ROUNDABOUT" -> {
                            roundabout = true;
                            roundAboutStartNodeIndex = i - 1;
                        }
                        case "KEEP_RIGHT" -> keepRight = true;
                        case "FERRY" -> {
                            ferry = true;
                            specialPathFeatures.add("a ferry");
                        }
                        default -> routeDescription.add(directionBetweenViaAndToWay);
                    }
                }
            } else {
                currentDistanceDescription += unitsTo.get(t).distance - unitsTo.get(v).distance;
                currentTimeDescription += unitsTo.get(t).time - unitsTo.get(v).time;
            }
        }

        routeDescription.add(getArrivedAtDestinationText(roundabout, ferry));
        fixFirstDirection();
    }

    /**
     * Gets the current distance and time as a string on different lines.
     * @return The current distance and time on different lines.
     */
    private String getCurrentDistanceAndTimeText() {
        return "\n" + currentDistanceDescription + " m " + "\n" + currentTimeDescription + " s";
    }

    /**
     * Gets the direction for a ferry.
     * @param wayBeforeViaName The name of the way before the via Node.
     * @return The ferry direction string.
     */
    private String getFerryText(String wayBeforeViaName) {
        return "Take the " + wayBeforeViaName + " ferry " + getCurrentDistanceAndTimeText(); // TODO: 4/29/21 time in this case? 
    }

    /**
     * Gets the direction text for a roundabout, including the exit number.
     * @param roundAboutStartNodeIndex The index on the path where it enters the roundabout.
     * @param i The current index.
     * @param wayBeforeVia The way before the via Node.
     * @param wayBeforeToName The name of the way before the to Node.
     * @return The direction string for the roundabout.
     */
    private String getRoundaboutText(int roundAboutStartNodeIndex, int i, Way wayBeforeVia, String wayBeforeToName) {
        return "At the roundabout, take the " + getRoundaboutExit(roundAboutStartNodeIndex, i - 1, wayBeforeVia) + ". exit onto " + wayBeforeToName + getCurrentDistanceAndTimeText();
    }

    /**
     * Gets the text for keep right directions.
     * @param wayBeforeViaName The name of the way which should be kept right on.
     * @return The keep right direction.
     */
    private String getKeepRightText(String wayBeforeViaName) {
        String keepRightName = "";
        if (wayBeforeViaName.contains("Exit")) keepRightName = " and take " + wayBeforeViaName;
        else if (!wayBeforeViaName.equals("unnamed way")) keepRightName = " on " + wayBeforeViaName;
        return "Keep right" + keepRightName + getCurrentDistanceAndTimeText();
    }

    /**
     * Gets the final text for the direction, which informs the user that they have arrived at their destination.
     * @param roundabout If the route ends in a roundabout.
     * @param ferry If the route ends on a ferry.
     * @return The final direction.
     */
    private String getArrivedAtDestinationText(boolean roundabout, boolean ferry) {
        String text = "";
        String wayName = wayBefore.get(path.get(0)).getName();
        if (wayName.equals("null")) wayName = "unnamed way";

        if (roundabout) text = "Follow the roundabout";
        else if (ferry) text = "Take the " + wayName + " ferry";
        else text = "Follow " + wayName;

        text += " and you will arrive at your destination" + getCurrentDistanceAndTimeText();
        return text;
    }

    /**
     * Gets the direction between two ways depending on the angle between them and the type of the current to way.
     * @param angle The angle between two ways.
     * @param wayBeforeTo The way before the current to Node.
     * @param wayBeforeToName The name of the way before the current to Node.
     * @return The direction between two ways.
     */
    private String getDirection(double angle, Way wayBeforeTo, String wayBeforeToName) {

        System.out.println("Angle: " + angle);
        String type = wayBeforeTo.getType();

        if (type.contains("_toll")) specialPathFeatures.add("toll"); // TODO: 4/29/21 fix 

        if (type.equals("ferry")) {
            return "FERRY";
        } else if (angle > 0) {
            if (type.equals("roundabout")) return "ROUNDABOUT";
            else if (type.equals("primary_link") || type.equals("motorway_link")) return "KEEP_RIGHT";
            else return "Turn right onto " + wayBeforeToName;
        } else if (angle < 0) {
            return "Turn left onto " + wayBeforeToName;
        } else {
            throw new RuntimeException("getDirection Error"); // TODO: 4/29/21 ???
        }
    }

    /**
     * Gets the number corresponding to the roundabout exit the path goes along.
     * @param roundaboutStartNodeIndex The index of the Node in the path where it enters the roundabout.
     * @param roundaboutEndIndex The index of the Node in the path where it leaves the roundabout.
     * @param roundaboutWay The Way object for the roundabout.
     * @return The roundabout exit number,
     */
    private String getRoundaboutExit(int roundaboutStartNodeIndex, int roundaboutEndIndex, Way roundaboutWay) {
        int exits = 0;
        for (int i = roundaboutStartNodeIndex - 1; i >= roundaboutEndIndex; i--) {
            ArrayList<Way> ways = nodeToWayMap.getElementsFromNode(path.get(i));
            if (ways.size() > 1) {
                for (Way w : ways) {
                    if (w != roundaboutWay) {
                        if (!w.isOnewayRoad()) exits++;
                        else if (w.getNextNode(path.get(i)) != null) exits++; // to ensure that we do not count one-way roads
                        break;
                    }
                }
            }
        }
        return String.valueOf(exits);
    }

    /**
     * Changes the first direction from follow to head in a certain compass direction. This needs to be done at the end
     * because the path might have several segments from the same road in the beginning and we need to get the full
     * distance for all of the segments.
     */
    private void fixFirstDirection() {
        Node f = path.get(path.size() - 1);
        Node t = path.get(path.size() - 2);
        routeDescription.add(1, routeDescription.get(0).replace("Follow", "Head " + MapMath.compassDirection(f, t).toLowerCase() + " on"));
        routeDescription.remove(0);
    }

    /**
     * Class which holds the distance and a time to a certain node along with the cost for A-star.
     * The class is necessary to keep track of both variables as time various by the road type for cars.
     */
    private class DistanceAndTimeEntry implements Comparable<DistanceAndTimeEntry> {
        private double distance, time, cost;

        public DistanceAndTimeEntry(double distance, double time, double cost) {
            this.distance = distance;
            this.time = time;
            this.cost = cost;
        }

        @Override
        public int compareTo(DistanceAndTimeEntry o) {
            return Double.compare(cost, o.cost);
        }
    }
}