package bfst21.data_structures;

import bfst21.Osm_Elements.Node;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

// TODO: 25-04-2021 implement HashMap<city(String), Arraylist<AddressTrieNodes> 

public class AddressTrieNode implements Serializable {
    @Serial
    private static final long serialVersionUID = -9059402923966729263L;
    private HashMap<Character, AddressTrieNode> children;
    private ArrayList<AddressTrieNode> addressNodes;
    private HashMap<String,ArrayList<AddressTrieNode>> cityMap;
    private Node node;
    private String city;
    private String streetname;
    private Integer postcode;
    private String houseNumber;
    private boolean isAddress;


    // for the root
    public AddressTrieNode() {
        this.children = new HashMap<>();
        isAddress = false;
    }


    public void setAddress(Node node, String city, String streetname, int postcode, String houseNumber){
        citiesWithThisStreet = new HashMap<>();
        citiesWithThisStreet.put(city, new City(city, node, houseNumber));
        this.streetname = streetname;
        this.postcode = postcode;
        isAddress = true;
    }

    public boolean isAddress(){
        return isAddress;
    }

    public void addHouseNumber(String city, Node node, String houseNumber){
        if(citiesWithThisStreet.containsKey(city)){
            citiesWithThisStreet.get(city).houseNumberNodes.add(new HouseNumberNode(node, houseNumber));
        } else {
            citiesWithThisStreet.put(city, new City(city, node, houseNumber));
        }
    }

    public HashMap<String, City> getCitiesWithThisStreet() {
        return citiesWithThisStreet;
    }

    public HashMap<Character, AddressTrieNode> getChildren(){
        return children;
    }

    // does this trienode contain an address?
    public boolean hasNode(){
        return this.addressNodes !=null;
    }

    public void addAddressNode(AddressTrieNode addressTrieNode){
        addressNodes.add(addressTrieNode);
        isAddress = true;
    }

    public String getCity() {
        //return this.city;
        return null;
    }

    public String getStreetname() {
        return this.streetname;
    }

    public int getPostcode() {
        return this.postcode;
    }

    public String getHouseNumber() {
        //return this.houseNumber;
        return null;
    }
    public String getAddress(){
        //return this.streetname + " " + this.houseNumber + ", " + this.postcode + " " + this.city;
        return null;
    }

    public AddressTrieNode getAddressTrieNode(){
        return this;
    }



    public class City{
        String city;
        ArrayList<HouseNumberNode> houseNumberNodes;
        public City(String city, Node node, String houseNumber){
            this.city = city;
            houseNumberNodes = new ArrayList<>();
            houseNumberNodes.add(new HouseNumberNode(node, houseNumber));
        }
    }
    private class HouseNumberNode{
        Node node;
        String houseNumber;

        public HouseNumberNode(Node _node, String _houseNumber){
            node = _node;
            houseNumber = _houseNumber;
        }
    }

}
