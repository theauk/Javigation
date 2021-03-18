package bfst21;

import bfst21.Osm_Elements.Node;
import bfst21.Osm_Elements.Way;
import bfst21.data.NonRoadData;
import bfst21.data.NonRoadElements;
import bfst21.data.RefData;
import bfst21.data.RoadData;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;


/*
Creates Objects such as Nodes, Ways and Relations from the .osm file given from the Loader.
 */
public class Creator {

    ArrayList<Drawable> roads;
    List<Drawable> residentialRoads = new ArrayList<>();
    List<Drawable> highways = new ArrayList<>();
    ArrayList<Drawable> coastlines = new ArrayList<>();
    private NonRoadData nonRoadData;
    private NonRoadElements nonRoadElements;
    private RefData refData;
    private RoadData roadData;
    List<Drawable> footway = new ArrayList<>();
    List<Drawable> bridges = new ArrayList<>();
    List<Long> refnumbers = new ArrayList<>();
    List<Node> nodeRefnumbers = new ArrayList<>();
    float minx, miny, maxx, maxy;
    boolean iscoastline, isRoad, isPrimaryHighway, isBridge, isFootWay, ispedestrianRoad, isresidentialRoad;
    boolean isRelation;
    //ArrayList<Way> relation = new ArrayList<>();

    public Creator(InputStream input) throws IOException, XMLStreamException {
        create(input);
    }

    public void create(InputStream input) throws IOException, XMLStreamException {
        XMLStreamReader reader = XMLInputFactory
                .newInstance()
                .createXMLStreamReader(new BufferedInputStream(input));
        var idToNode = new HashMap<Long,Node>();
        Way way = null;
        Node node = null;
        var roads = new ArrayList<>();
        var member = new ArrayList<Long>();
        var coastlinesTemp = new ArrayList<Way>();
        var highWayTemp = new ArrayList<Way>();
        while (reader.hasNext()) {
            switch (reader.next()) {
                case START_ELEMENT:
                    switch (reader.getLocalName()) {
                        case "bounds":
                            minx = Float.parseFloat(reader.getAttributeValue(null, "minlon"));
                            maxx = Float.parseFloat(reader.getAttributeValue(null, "maxlon"));
                            maxy = Float.parseFloat(reader.getAttributeValue(null, "minlat")) / -0.56f;
                            miny = Float.parseFloat(reader.getAttributeValue(null, "maxlat")) / -0.56f;
                            break;
                        case "relation":
                            // adding memebers like Node and Way into the list
                            break;
                        case "node":
                            var id = Long.parseLong(reader.getAttributeValue(null, "id"));
                            var lon = Float.parseFloat(reader.getAttributeValue(null, "lon"));
                            var lat = Float.parseFloat(reader.getAttributeValue(null, "lat"));
                            idToNode.put(id, new Node(id,lat,lon));
                            break;
                        case "way":
                            way = new Way();
                            allBooleansFalse();
                            way.setId(Long.parseLong(reader.getAttributeValue(null, "id")));
                            break;
                        case "tag":
                            var k = reader.getAttributeValue(null, "k");
                            var v = reader.getAttributeValue(null, "v");
                            if (k.equals("natural") && v.equals("coastline")) {
                                iscoastline = true;
                            } else if (k.equals("highway")){
                                if (v.equals("primary")) isPrimaryHighway = true;
                                if (v.equals("pedestrian")) ispedestrianRoad = true;
                                if(v.equals("residential")) isresidentialRoad = true;
                                else isRoad = true;
                            }

                                break;
                                case "nd":
                                    var refNode = Long.parseLong(reader.getAttributeValue(null, "ref"));
                                    if(isRoad) nodeRefnumbers.add(idToNode.get(refNode));
                                    if(isPrimaryHighway) nodeRefnumbers.add(idToNode.get(refNode));
                                    if (ispedestrianRoad) nodeRefnumbers.add(idToNode.get(refNode));
                                    if(isresidentialRoad) nodeRefnumbers.add(idToNode.get(refNode));
                                    way.add(idToNode.get(refNode));


                                    break;
                        case "member":
                            if(isRelation){
                                var refWay = Long.parseLong(reader.getAttributeValue(null,"ref"));
                                member.add(refWay);
                            }
                            break;
                            }
                            break;
                        case END_ELEMENT:
                            switch (reader.getLocalName()) {
                                case "way":
                                    if (iscoastline) coastlines.add(way);
                                    if (isRoad) {
                                        roads.add(way);
                                        refnumbers.add(way.getId());
                                    }
                                    if (isPrimaryHighway) {
                                        highways.add(way);
                                        refnumbers.add(way.getId());
                                    }
                                    if (isFootWay) {
                                        footway.add(way);
                                        refnumbers.add(way.getId());
                                    }
                                    if(isresidentialRoad){
                                        residentialRoads.add(way);
                                        refnumbers.add(way.getId());
                                    }
                                    if (isBridge) bridges.add(way);
                                    break;
                            }
                            break;
                    }
            }
        }
    private void allBooleansFalse(){
        iscoastline = false;
        isRoad = false;
        isPrimaryHighway = false;
        isBridge = false;
        isFootWay = false;
        ispedestrianRoad = false;
        isresidentialRoad = false;
    }
    public void printRefnumbers() {
        System.out.println("References for ways");
        for (Long ref : refnumbers) {
            System.out.println(ref);
        }
    }
    public void printNodeRefnumbers(){
        System.out.println("references for nodes in ways:");
        for(Node node: nodeRefnumbers){
            System.out.println("test");
            System.out.println(node.getID());
        }
    }
        public List<Long>getList(){
            return refnumbers;
        }
        public List<Node>getNodeRefnumbers(){
            return nodeRefnumbers;
        }
        public ArrayList<Drawable> getRoads(){
            return roads;
        }
    public ArrayList<Drawable> getcoastlines(){
    return coastlines;

    }

    public float getMaxx() {
        return maxx;
    }

    public float getMaxy() {
        return maxy;
    }

    public float getMinx() {
        return minx;
    }

    public float getMiny() {
        return miny;
    }

    public List<Drawable> getHighways() {
        return highways;
    }

    public List<Drawable> getBridges() {
        return bridges;
    }

    public List<Drawable> getFootway() {
        return footway;
    }

    public List<Drawable> getResidentialRoads() {
        return residentialRoads;
    }
}


    //create Node Object

    //create Way Object

    //create Road Object

    //Create Relation Object

    // add methods to the different Datasets
