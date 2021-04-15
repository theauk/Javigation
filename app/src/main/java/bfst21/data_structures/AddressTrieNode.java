package bfst21.data_structures;

import bfst21.Osm_Elements.Node;

import java.util.ArrayList;
import java.util.HashMap;

public class AddressTrieNode {
    private HashMap<Character, AddressTrieNode> children;
    private ArrayList<AddressTrieNode> addressNodes;
    private Node node;
    private String city;
    private String streetname;
    private Integer postcode;
    private String houseNumber;

    // for the root
    public AddressTrieNode(){
        this.children = new HashMap<>();
        this.addressNodes = new ArrayList<>();
    }
    // for the other trienodes
    public AddressTrieNode(Node node, String city, String streetname, int postcode, String houseNumber){
        this.children = new HashMap<>();
        this.addressNodes = new ArrayList<>();
        this.node = node;
        this.city = city;
        this.streetname = streetname;
        this.postcode = postcode;
        this.houseNumber = houseNumber;

    }

    public ArrayList<AddressTrieNode> getAddressNodes() {
        return addressNodes;
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
    }

    public String getCity() {
        return city;
    }

    public String getStreetname() {
        return streetname;
    }

    public Integer getPostcode() {
        return postcode;
    }

    public String getHouseNumber() {
        return houseNumber;
    }
    public String getAddress(){
        return this.streetname + " " + this.houseNumber + ", " + this.postcode + " " + this.city;
    }
}
