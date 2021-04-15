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


    /**
     * Not yet implemented!
     */
    public void put(Node node, String city, String streetname, int postcode, String houseNumber) {
        addressNode = new AddressTrieNode(node, city, streetname, postcode, houseNumber);
        insert(root, addressNode);
    }

    public void insert(AddressTrieNode root, AddressTrieNode addressNode) {
        insert_address(root, addressNode, 0);

    }

    public void insert_address(AddressTrieNode trieNode, AddressTrieNode addressNode, int index) {
        var stringPostcode = Integer.toString(addressNode.getPostcode());
        if (index == stringPostcode.length()) {
            trieNode.addAddressNode(addressNode);
        } else {
            Character currentChar = stringPostcode.charAt(index);
            if (!trieNode.getChildren().containsKey(currentChar)) {
                AddressTrieNode new_child = new AddressTrieNode();
                trieNode.getChildren().put(currentChar, new_child);
            }
            insert_address(trieNode.getChildren().get(currentChar), addressNode, index + 1);
        }
    }

    /**
     * Not yet implemented!
     */
    public Node getAddressNode(String address) {
        return null;
    }

    /**
     * Not yet Implemented
     */
    public List<Node> getPossibleAddresses(String address) {
        return null;
    }

    public List<AddressTrieNode> searchWithPostcode(AddressTrieNode trieNode, String postcode, int index) {
        // returns NULL if there is no user going by that name
        if (index == postcode.length()) {
            return trieNode.getAddressNodes();
            //return trieNode.getAddressNode().getPostcode(); // this works for one node
        } else {
            Character current_char = postcode.charAt(index);
            if (!trieNode.getChildren().containsKey(current_char)) {
                return null;
            } else {
                return searchWithPostcode(trieNode.getChildren().get(current_char), postcode, index + 1);
            }
        }
    }

    // quick print test
    public static void main(String[] args) {
        Node node1 = new Node(340551927, 55.6786770f, 12.5694510f);
        Node node2 = new Node(340551928, 55.6786400f, 12.5698360f);
        AddressTriesTree addressTriesTree = new AddressTriesTree();
        addressTriesTree.put(node1, "København K", "Studiestræde", 1455, "18");
        addressTriesTree.put(node2, "København K", "Studiestræde", 1455, "19");

        System.out.println(addressTriesTree.searchWithPostcode(addressTriesTree.root, "1455", 0).size());

                for(var address: addressTriesTree.searchWithPostcode(addressTriesTree.root, "1455",0)){
                    System.out.println(address.getAddress());
        }

        }
    }
