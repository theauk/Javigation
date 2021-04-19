package bfst21.data_structures;

import bfst21.Osm_Elements.Node;

import java.util.List;

/**
 * Not yet implemented
 */
// TODO: 28-03-2021 implement
public class AddressTriesTree {
    private AddressTrieNode root;
    private AddressTrieNode addressNode;

    public AddressTriesTree() {
        root = new AddressTrieNode();
    }

// TODO: 19-04-2021 A subtrie inside the trie??
    /**
     *
     * @param node
     * @param city -> the city which the address is located at. comes after the postcode when viewed - etc. postcode Jerslev sj
     * @param streetname
     * @param postcode -> The 4 digit number that tells in what part of the country the address is located.
     *                 in Denmark it's how far they are from Copenhagen
     * @param houseNumber
     * @param method -> this number indicates wether the trie should insert the address with postcode or streetname.
     */
    public void put(Node node, String city, String streetname, int postcode, String houseNumber, int method) {
        addressNode = new AddressTrieNode(node, city, streetname, postcode, houseNumber);
        insert(root, addressNode, method);
    }

    public void insert(AddressTrieNode root, AddressTrieNode addressNode, int method) {
        if(method == 1){
            insert_address_withPostCode(root, addressNode, 0);
        }
        if(method == 2){
            insert_address_with_streetname(root, addressNode, 0);
        }

    }

    /**
     * This will insert the address into the trie by using its postcode as key.
     * @param trieNode -> when called for the first time, this would be the root.
     *                 afterwards in the recursive calls inside the methods will call the method with the next node, and proceed
     *                 to the bottom of the trie, where the addressNode will be added to the Arraylist in that last node's arraylist.
     *                 this @param trieNode could be omitted, but then the methods needs to be iterative instead
     * @param addressNode -> the address we want to insert.
     * @param index -> the start index is always 0, since the method will start from the root, and go down through the tree.
     *              index could be omitted as well, but the method would need to be made iterative instead of recursive.
     */
    private void insert_address_withPostCode(AddressTrieNode trieNode, AddressTrieNode addressNode, int index) {
        var stringPostcode = Integer.toString(addressNode.getPostcode());
        if (index == stringPostcode.length()) {
            trieNode.addAddressNode(addressNode);
        } else {
            Character currentChar = stringPostcode.charAt(index);
            if (!trieNode.getChildren().containsKey(currentChar)) {
                AddressTrieNode new_child = new AddressTrieNode();
                trieNode.getChildren().put(currentChar, new_child);
            }
            insert_address_withPostCode(trieNode.getChildren().get(currentChar), addressNode, index + 1);
        }
    }

    private void insert_address_with_streetname(AddressTrieNode trieNode, AddressTrieNode addressNode, int index) {
        var streetname = addressNode.getStreetname();
        if (index == streetname.length()) {
            trieNode.addAddressNode(addressNode);
        } else {
            Character currentChar = streetname.charAt(index);
            if (!trieNode.getChildren().containsKey(currentChar)) {
                AddressTrieNode new_child = new AddressTrieNode();
                trieNode.getChildren().put(currentChar, new_child);
            }
            insert_address_with_streetname(trieNode.getChildren().get(currentChar), addressNode, index + 1);
        }
    }

    /**
     * Not yet implemented!
     */
    public Node getAddressNode(String address) {
        return null;
    }

    /**
     * Calls recursively until a search-hit or a search-miss occurs, and returns the given results.
     * @param trieNode -> starts as the root
     * @param postcode -> the postcode we are searching for
     * @param index -> starts at 0 (at the root) and travels through the trie until search-hit or search-miss is found.
     * @return if there is a search-hit, it will return the given ArrayList for the serach-hit (currently tested on postcodes
     *        and streetnames. (if given as a string)
     */
    public List<AddressTrieNode> search(AddressTrieNode trieNode, String postcode, int index) {
        // returns NULL if there is no user going by that name
        if (index == postcode.length()) {
            return trieNode.getAddressNodes();
            //return trieNode.getAddressNode().getPostcode(); // this works for one node
        } else {
            Character current_char = postcode.charAt(index);
            if (!trieNode.getChildren().containsKey(current_char)) {
                return null;
            } else {
                return search(trieNode.getChildren().get(current_char), postcode, index + 1);
            }
        }
    }

    // quick print test
    public static void main(String[] args) {
        Node node1 = new Node(340551927, 55.6786770f, 12.5694510f);
        Node node2 = new Node(340551928, 55.6786400f, 12.5698360f);
        AddressTriesTree addressTriesTree = new AddressTriesTree();
        addressTriesTree.put(node1, "København K", "Studiestræde", 1455, "18",1);
        addressTriesTree.put(node2, "København K", "Studiestræde", 1455, "19",1);
        System.out.println(addressTriesTree.search(addressTriesTree.root, "1455",0).size());
        System.out.println("adresser via postnummer:");
        for (AddressTrieNode address: addressTriesTree.search(addressTriesTree.root, "1455",0)){
            System.out.println(address.getAddress());
        }
        System.out.println();
        System.out.println("adresser via gadenavn:");
        AddressTriesTree streetnameTrie = new AddressTriesTree();
        streetnameTrie.put(node1, "København K", "Studiestræde", 1455, "18",2);
        streetnameTrie.put(node2, "København K", "Studiestræde", 1455, "19",2);
        for (AddressTrieNode address: streetnameTrie.search(streetnameTrie.root, "Studiestræde",0)){
            System.out.println(address.getAddress());
        }
        //System.out.println(addressTriesTree.search(addressTriesTree.root, "1455", 0).size());
        }

        }
