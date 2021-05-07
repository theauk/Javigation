package bfst21.data_structures;

import bfst21.Osm_Elements.Node;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;

public class AddressTrieNode implements Comparable<AddressTrieNode>, Serializable {
    @Serial
    private static final long serialVersionUID = -9059402923966729263L;

    private final Map<Character, AddressTrieNode> children;
    private Map<Integer, List<HouseNumberNode>> citiesWithThisStreet;
    private String streetname;
    private Map<Integer, String> addresses;
    private boolean isAddress;

    public AddressTrieNode() {
        children = new HashMap<>();
        isAddress = false;
    }

    public void setAddress(Node node, int postcode, String streetname, String houseNumber){
        citiesWithThisStreet = new HashMap<>();
        List<HouseNumberNode> list = new ArrayList<>();
        list.add(new HouseNumberNode(node, houseNumber));
        citiesWithThisStreet.put(postcode, list);
        this.streetname = streetname;
        isAddress = true;
    }

    public boolean isAddress(){
        return isAddress;
    }

    public void addHouseNumber(int postcode, Node node, String houseNumber){
        if(citiesWithThisStreet.containsKey(postcode)){
            citiesWithThisStreet.get(postcode).add(new HouseNumberNode(node, houseNumber));
            Collections.sort(citiesWithThisStreet.get(postcode));
        } else {
            ArrayList<HouseNumberNode> list = new ArrayList<>();
            list.add(new HouseNumberNode(node, houseNumber));
            citiesWithThisStreet.put(postcode, list);
        }
    }

    public Map<Character, AddressTrieNode> getChildren(){
        return children;
    }

    public Map<Integer, String> getAddresses(){
        if(addresses != null) {
            return addresses;
        } else {
            addresses = new HashMap<>();
            for(int val : citiesWithThisStreet.keySet()){
                addresses.put(val, getAddressWithOutHouseNumber(val));
            }
        }
        return addresses;
    }

    private String getAddressWithOutHouseNumber(int postcode){
        return (this.streetname +  ", " + postcode + " " + postCodesToCities.get(postcode));
    }

    public Map<String, Node> getHouseNumbersOnStreet(int postcode){
        Map<String, Node> map = new HashMap<>();
        for(HouseNumberNode houseNumberNode : citiesWithThisStreet.get(postcode) ){
            map.put((this.streetname + " " + houseNumberNode.houseNumber + ", " + postcode + " " + postCodesToCities.get(postcode)), houseNumberNode.node);
        }

        return map;
    }

    public List<String> getAddressesOnStreet() {
        return new ArrayList<>(getAddresses().values());
    }

    /**
     * Searches for and returns all streets that start with a specified house number in each city.
     *
     * @param houseNumber the house number prefix for the addresses
     * @return a list of addresses starting with the specified house number.
     */
    public List<String> getAddressFor(String houseNumber) {
        List<String> list = new ArrayList<>();

        for(Map.Entry<Integer, List<HouseNumberNode>> entry : citiesWithThisStreet.entrySet()) {    //Get the key/value set for postcode to list of house nodes
            for(HouseNumberNode node: entry.getValue()) {   //Run through each house number node to check if the specified address is present
                if(node.houseNumber.startsWith(houseNumber)) {
                    String address = streetname + " " + node.houseNumber + ", " + entry.getKey() + " " + AddressTriesTree.POSTCODE_TO_CITIES.get(entry.getKey());
                    list.add(address);
                }
            }
        }

        return list;
    }

    public List<String> getAddressesFor(String houseNumber, int postCode, String city) {
        List<String> list = new ArrayList<>();

        if(!isValidCity(postCode, city)) return list;

        List<HouseNumberNode> nodes = citiesWithThisStreet.get(postCode);

        for(HouseNumberNode node: nodes) {
            if(node.houseNumber.startsWith(houseNumber)) {
                String address = streetname + " " + node.houseNumber + ", " + postCode + " " + city;
                list.add(address);
            }
        }

        return list;
    }

    public Node findNode(String houseNumber, int postCode) {
        List<HouseNumberNode> nodes = citiesWithThisStreet.get(postCode);
        return nodes.get(getHouseNumberIndex(nodes, houseNumber)).node;
    }

    public boolean isValidAddress(String houseNumber, int postCode, String city)  {
        if(!isValidCity(postCode, city)) return false;
        List<HouseNumberNode> nodes = citiesWithThisStreet.get(postCode);

        return getHouseNumberIndex(nodes, houseNumber) != -1;
    }

    private boolean isValidCity(int postCode, String city) {
        String realCity = AddressTriesTree.POSTCODE_TO_CITIES.get(postCode);
        if(realCity == null) return false;
        return realCity.equals(city);
    }

    private int getHouseNumberIndex(List<HouseNumberNode> nodes, String houseNumber)
    {
        int left = 0;
        int right = nodes.size() - 1;

        while (left <= right) {
            int m = left + (right - left) / 2;

            int res = houseNumber.compareTo(nodes.get(m).houseNumber);

            if (res == 0) return m;
            if (res > 0) left = m + 1;
            else right = m - 1;
        }

        return -1;
    }

    public String getStreetName() {
        return streetname;
    }

    @Override
    public int compareTo(AddressTrieNode o) {
        return this.streetname.compareTo(o.streetname);
    }

    private class HouseNumberNode implements Comparable<HouseNumberNode>, Serializable {
        @Serial
        private static final long serialVersionUID = -2565597371430349023L;

        Node node;
        String houseNumber;

        public HouseNumberNode(Node _node, String _houseNumber){
            node = _node;
            houseNumber = _houseNumber;
        }

        @Override
        public int compareTo(HouseNumberNode o) {
            return this.houseNumber.compareTo(o.houseNumber);
        }
    }
}
