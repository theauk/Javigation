package bfst21.data_structures;

import bfst21.Osm_Elements.Node;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;

public class AddressTriesTree implements Serializable {
    @Serial
    private static final long serialVersionUID = 5713923887785799744L;

    private final AddressTrieNode root;
    public static final Map<Integer, String> POSTCODE_TO_CITIES = new HashMap<>();

    public AddressTriesTree() {
        root = new AddressTrieNode();
    }

    /**
     * calls insert that inserts the node into the trie via its streetname and city.
     *
     * @param node         contains the coordinates for the address.
     * @param city         the city which the address is located at.
     * @param streetname   the name of the street which the address belongs to.
     * @param postcode     The digit number that tells in what part of the country the address is located.
     *                    in Denmark it's how far they are from Copenhagen and are a 4-digit number
     * @param houseNumber the housenumber that the address has.
     *
     */
    public void put(Node node, String streetname, String houseNumber, int postcode, String city) {
        insert(root, node, streetname, houseNumber, postcode, city);
    }

    /**
     * Sets streetname, housenumber (some has letters in the housenumber) and the city to lowercase before
     * calling the insert method <br>
     * Puts the post and city into a Hashmap as well to make it easier to search
     *
     * @param root the root of the TrieTree
     * @param node the node which is getting inserted
     * @param streetname the streetname the given node has from the .osm file
     * @param houseNumber the house number the given node has from the .osm file
     * @param postcode the postcode the given node has from the .osm file
     * @param city the city the given node has from the .osm file
     */
    private void insert(AddressTrieNode root, Node node, String streetname, String houseNumber, int postcode, String city) {
        streetname = streetname.toLowerCase();
        houseNumber = houseNumber.toLowerCase();
        city = city.toLowerCase();
        POSTCODE_TO_CITIES.put(postcode, city);
        insertAddressWithStreetname(root,0 , node, streetname, houseNumber, postcode);
    }

    /**
     * This method is inspired by the method 'insert' from this post: https://stackoverflow.com/a/55470115
     *
     *
     * @param trieNode when called for the first time, this would be the root.
     *                     afterwards in the recursive calls the method will call itself with the next node (a child), and proceed
     *                     to the bottom of the trie, where the addressNode will be added to the Arraylist in that last node's arraylist.<br>
     *                     this @param trieNode could be omitted, but then the methods needs to be iterative instead of recursive.<br><br>
     * @param index the start index is always 0, since the method will start from the root, and will traverse through the tree.
     *                 index could be omitted as well, but the method would need to be made iterative instead of recursive. <br>
     *
     *              When index is the same length as the streetname the method will insert the address in the matching trienode's
     *              HashMap&lt;Integer,AddressTrieNode&gt; If the trieNode is not an address already - the method will set it to an address<br><br>
     *
     * @param node the node that contains the coordinates for the address
     * @param streetname the name of the street given by the .osm file.
     * @param postcode the postcode of the node given by the .osm file.
     * @param houseNumber the house number given by the .osm file.
     */
    private void insertAddressWithStreetname(AddressTrieNode trieNode, int index, Node node, String streetname, String houseNumber, int postcode) {
        if (index == streetname.length()) {
            if(trieNode.isAddress()) trieNode.addHouseNumber(node, houseNumber, postcode);
            else trieNode.setAddress(node, streetname, houseNumber, postcode);
        }
        else {
            Character currentChar = streetname.charAt(index);
            if (!trieNode.getChildren().containsKey(currentChar)) {
                AddressTrieNode new_child = new AddressTrieNode();
                trieNode.getChildren().put(currentChar, new_child);
            }
            insertAddressWithStreetname(trieNode.getChildren().get(currentChar), index + 1 , node, streetname, houseNumber, postcode);
        }
    }

    /**
     * Adapted from the Algorithms book by Sedgewick & Wayne.
     * The help methods are also adapted from the same book
     * @param prefix prefix to possible streetnames
     * @return  a list (ArrayList) with the possible streetnames that matches the prefix with help from the help methods.
     */
    public List<AddressTrieNode> searchWithPrefix(String prefix) {
        List<AddressTrieNode> queue = new ArrayList<>();
        prefix = prefix.toLowerCase();
        collect(get(root, prefix, 0), prefix, queue);
        Collections.sort(queue);
        return queue;
    }

    /**
     *
     * @param trieNode the trienode which the method is currently at. It starts from the root and goes through the tree until it
     *                 gets the node associated with key in the subtrie rooted at the trienode
     * @param key could also be called prefix. Calls the method recursively until index is the same length as the key, and then it returns the given trienode
     * @param index starts at 0, and increases up to the length of the key - this is so the method can be called recursively
     * @return returns the trienode that matches the key.
     */
    private AddressTrieNode get(AddressTrieNode trieNode, String key, int index) {
        if(trieNode == null) return null;
        if(index == key.length()) {
            return trieNode;
        }
        char character = key.charAt(index);
        return get(trieNode.getChildren().get(character),key, index+1);
    }

    /**
     *
     * @param trieNode the trienode the method is currently at. It starts from the root and traverses the tree until it
     *                 finds a streetname that matches the given prefix and add it to the list (queue).
     * @param prefix help method for searchWithPrefix adds streetnames to the list that begins with the given prefix
     * @param queue the list that searchWithPrefix uses that contains the possible streetnames
     */
    private void collect(AddressTrieNode trieNode, String prefix, List<AddressTrieNode> queue) {
        if(trieNode == null) return;
        if(trieNode.isAddress()) {
            queue.add(trieNode);
        }

        for (Map.Entry<Character, AddressTrieNode> child : trieNode.getChildren().entrySet()) {
            collect(child.getValue(), prefix + child.getKey(), queue);
        }
    }
}