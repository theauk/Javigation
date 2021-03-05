package bfst21.vector;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.zip.ZipInputStream;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import bfst21.vector.osm.Node;
import bfst21.vector.osm.Way;


import static javax.xml.stream.XMLStreamConstants.*;

public class Model  {
    ArrayList<Drawable> relation = new ArrayList<>();
    List<Drawable> coastLines = new ArrayList<>();
    
    List<Drawable> buildings = new ArrayList<>();
    List<Runnable> observers = new ArrayList<>();
    List<Drawable> grass = new ArrayList<>();
    List<Drawable> water = new ArrayList<>();
    List<Drawable> primaryHighway = new ArrayList<>();
    List<Drawable> roads = new ArrayList<>();
    List<Drawable> bridges = new ArrayList<>();
    List<Drawable> railway = new ArrayList<>();
    List<Node> busStops = new ArrayList<>();
    List<Node> railWayStops = new ArrayList<>();
    List<Node> ferryStops = new ArrayList<>();
    List<Drawable> footway = new ArrayList<>();
    List<Drawable> ferries = new ArrayList<>();
    
    ArrayList<ArrayList<Drawable>> busRoutes = new ArrayList<>();
    boolean iscoastline, isbuilding, isGrass, isWater, isPrimaryHighway, isRoad, isBridge, isRailway, isBusstop, isBusRoute;
    boolean isRelation, isFootWay, isRailWayStop, isFerryRoute, isFerryStop;
    float minx,miny,maxx,maxy;
   

    public Model(String filename) throws IOException, XMLStreamException, FactoryConfigurationError {
        
        load(filename);
    }

    public void load(String filename) throws IOException, XMLStreamException, FactoryConfigurationError {
        long time = -System.nanoTime();
        if (filename.endsWith(".txt")) {
            coastLines = Files.lines(Path.of(filename)).map(Line::new).collect(Collectors.toList());
        } else if (filename.endsWith(".osm")) {
            loadOSM(filename);
        } else if (filename.endsWith(".zip")) {
            loadZIP(filename);
        }
        time += System.nanoTime();
        Logger.getGlobal().info(String.format("Load time: %dms", time / 1000000));
    }

    private void loadZIP(String filename) throws IOException, XMLStreamException, FactoryConfigurationError {
        var zip = new ZipInputStream(new FileInputStream(filename));
        zip.getNextEntry();
        loadOSM(zip);
    }

    private void loadOSM(String filename) throws FileNotFoundException, XMLStreamException, FactoryConfigurationError {
        loadOSM(new FileInputStream(filename));
    }

    private void loadOSM(InputStream input) throws FileNotFoundException, XMLStreamException, FactoryConfigurationError {
        XMLStreamReader reader = XMLInputFactory
            .newInstance()
            .createXMLStreamReader(new BufferedInputStream(input));
        var IdToway = new LongIndexWay();
        var idToNode = new LongIndexNode();
        Way way = null;
        Node node = null;
        var member = new ArrayList<Long>();
        var coastlinesTemp = new ArrayList<Way>();
        var highWayTemp = new ArrayList<Way>();
        //var waterTemp = new ArrayList<ArrayList<Way>>();
        
        allWayBooleansFalse();
        nodeBooleans();
        while (reader.hasNext()) {
            switch (reader.next()) {
                case START_ELEMENT:
                    switch (reader.getLocalName()) {
                        case "bounds":
                            minx = Float.parseFloat(reader.getAttributeValue(null, "minlon"));
                            maxx = Float.parseFloat(reader.getAttributeValue(null, "maxlon"));
                            maxy = Float.parseFloat(reader.getAttributeValue(null, "minlat"))/-0.56f;
                            miny = Float.parseFloat(reader.getAttributeValue(null, "maxlat"))/-0.56f;
                            break;
                        
                        case "node":
                            nodeBooleans();
                            var id = Long.parseLong(reader.getAttributeValue(null, "id"));
                            var lon = Float.parseFloat(reader.getAttributeValue(null, "lon"));
                            var lat = Float.parseFloat(reader.getAttributeValue(null, "lat"));
                            node = new Node(id,lat, lon);
                            idToNode.put(node );
                            break;
                        case "relation":
                            relationBooleans();
                            member = new ArrayList<>();
                            relation = new ArrayList<Drawable>();
                            isRelation = true;
                            break;
                        case "way":
                            way = new Way();
                            way.setId(Long.parseLong(reader.getAttributeValue(null, "id")));
                            allWayBooleansFalse();
                            break;
                        case "tag":
                            var k = reader.getAttributeValue(null, "k");
                            var v = reader.getAttributeValue(null, "v");
                            switch(k){
                                case "natural": 
                                    if(v.equals("coastline")) iscoastline = true;
                                    if(v.equals("water")) isWater = true;
                                    break;

                                case "building":
                                    if(v.equals("yes")) isbuilding = true;
                                    break;
                                

                                case "landuse":
                                    if(v.equals("grass")) isGrass = true;
                                    if(v.equals("recreation_ground")) isGrass = true;                                       
                                    if(v.equals("basin")) isWater = true;
                                    if(v.equals("forest")) isGrass = true;                                        
                                    break;

                                case "highway":
                                    if(v.equals("primary")) isPrimaryHighway = true;
                                    else if(v.equals("pedestrian")) ;
                                    else if(v.equals("bus_stop")) isBusstop = true;
                                    else if (v.equals("footway"))isFootWay = true;
                                    else if(v.equals("steps"));
                                    else isRoad = true;
                                    break;
                              
                                case "amenity":
                                    if(v.equals("ferry_terminal")) isFerryStop = true;                               
                                case "leirsure" :
                                    if(v.equals("park")) isGrass = true;
                                    break;

                                case "leisure" :
                                    if(v.equals("park")) isGrass = true;
                                    break;
                                
                                case "man_made":
                                    if(v.equals("bridge")) isBridge = true;
                                    break; 

                                case "railway":
                                    if(v.equals("subway")) isRailway = true;
                                    if(v.equals("station")) isRailWayStop = true;
                                    
                                    break;
                                
                                case
                                 "route":
                                    if(v.equals("bus")) isBusRoute = true;
                                    if(v.equals("ferry")) isFerryRoute = true;
                                    break;

                                case "operator":
                                    if(v.equals("Movia")) {
                                        isBusRoute= true;
                                        
                                    }
                                    break;

                                case "water":
                                    if(v.equals("canal")) isWater= true; 
                                    if(v.equals("moat")) isWater=true;
                                    if(v.equals("reservoir")) isWater = true;
                                    break;
                            }
                            
                            break;
                        case "nd":
                            var refNode = Long.parseLong(reader.getAttributeValue(null, "ref"));
                            way.add(idToNode.get(refNode));
                            break;

                        case "member":
                            if(isRelation){
                                var refWay = Long.parseLong(reader.getAttributeValue(null, "ref"));
                                member.add(refWay);
                            }
                            break;

                    }
                    break;
                case END_ELEMENT:
                    switch (reader.getLocalName()) {
                        case "way":
                            IdToway.put(way);
                            if (iscoastline) coastlinesTemp.add(way);
                            if (isbuilding) buildings.add(way);
                            if(isGrass) grass.add(way);
                            if(isWater) water.add(way);
                            if(isRoad) roads.add(way);
                            if(isPrimaryHighway) highWayTemp.add(way);
                            if(isRailway) railway.add(way);
                            if(isFootWay) footway.add(way);
                            if(isBusRoute) relation.add(way);
                            if(isBridge) bridges.add(way);
                            if(isFerryRoute) ferries.add(way);
                            allWayBooleansFalse();
                            way = new Way();
                            break;  
                        case "node":
                            if (isBusstop) busStops.add(node);
                            if(isRailWayStop) railWayStops.add(node);
                            if(isFerryStop) ferryStops.add(node);
                            
                            break;
                        case "relation":                            
                            if(isBusRoute){ 
                                for(var ref: member){
                                    if(IdToway.get(ref) != null ){
                                        relation.add(IdToway.get(ref));
                                    }                                    
                                }                                
                                busRoutes.add(relation);
                            }
                               
                       //     if(isWater){ 
                       //       var relation = new ArrayList<Way>();
                       //      for(var ref: member){
                       //          if(IdToway.get(ref) != null ){
                       //              relation.add(IdToway.get(ref));
                       //          }                                    
                       //     }
                       //    waterTemp.add(relation);       
                       // }
                          //Leftovers fra et desperat attempt på at tegne vand

                            relation = new ArrayList<>();
                            relationBooleans();
                            break; 

                            
                    }
                    break;
            }
        }
  // for(var list : waterTemp){
  //     water.addAll(mergeWays(list));
  // }
        primaryHighway = mergeWays(highWayTemp);
        coastLines = mergeWays(coastlinesTemp);
    }

    private List<Drawable> mergeWays(ArrayList<Way> ways) {
        Map<Node,Way> pieces = new HashMap<>();
        for (var way : ways) {
            var before = pieces.remove(way.first());
            var after = pieces.remove(way.last());
            if (before == after) after = null;
            var merged = Way.merge(before, way, after);
            pieces.put(merged.first(),merged);
            pieces.put(merged.last(),merged);
        }
        List<Drawable> merged = new ArrayList<>();
        pieces.forEach((node,way) -> {
            if (way.last() == node) {
                merged.add(way);
            }
        });
        return merged;
    }
    private void allWayBooleansFalse(){
       iscoastline = false;
        isbuilding = false;
        isGrass = false;
        isWater = false;
        isPrimaryHighway = false;
        isRoad = false;
        isBridge = false;
        isRailway = false;
        
        isFerryRoute = false;
        isFootWay = false;

    }

    private void relationBooleans(){
        isRelation = false;
        isBusRoute = false;
    }

    private void nodeBooleans(){
        isBusstop = false;
        isRailWayStop = false;
       
        isFerryStop = false;
    }

    

    public void save(String filename) throws FileNotFoundException {
        try (var out = new PrintStream(filename)) {
            for (var line : coastLines)
                out.println(line);
        }
    }

  

    
}
