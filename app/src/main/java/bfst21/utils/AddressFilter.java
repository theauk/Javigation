package bfst21.utils;

import bfst21.Osm_Elements.Node;
import bfst21.data_structures.AddressTrieNode;
import bfst21.data_structures.AddressTriesTree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddressFilter implements Filter {

    private AddressTriesTree addressTree;
    private List<String> suggestions;
    private Node matchedAddress;

    private final String addressRegex = "^ *(?<street>[a-zæøå0-9 \\-.]+?),? *(?<number>\\d{1,3}[a-zæøå]?)?,? *(?<postCode>\\d{1,4})?(?: (?<city>[a-zæøå]+?|[a-zæøå]+? *[a-zæøå]+)?)? *$";
    private final Pattern pattern = Pattern.compile(addressRegex);
    private Matcher matcher;

    public AddressFilter() {
        suggestions = new ArrayList<>();
    }

    public void search(String prefix) {
        suggestions = new ArrayList<>();
        String street = "";
        String houseNumber = "";
        int postCode = 0;
        String city = "";

        matcher = pattern.matcher(prefix);
        if(matcher.matches()) {
            if(matches("street")) street = matcher.group("street");
            if(matches("number")) houseNumber = matcher.group("number");
            if(matches("postCode")) postCode = Integer.parseInt(matcher.group("postCode"));
            if(matches("city")) city = matcher.group("city");

            List<AddressTrieNode> searchResult = addressTree.searchWithPrefix(street);
            if(searchResult.size() == 0) return;
            validateInput(searchResult, houseNumber, postCode, city);
        } else suggestions.add("No matches!");
    }

    private void validateInput(List<AddressTrieNode> searchResult, String houseNumber, int postCode, String city) {
        if(!isMatch(searchResult.get(0), houseNumber, postCode, city)) makeSuggestions(searchResult, houseNumber, postCode, city);
        else matchedAddress = searchResult.get(0).findNode(houseNumber, postCode);
    }

    private void makeSuggestions(List<AddressTrieNode> searchResult, String houseNumber, int postCode, String city) {
        suggestions = filter(searchResult, houseNumber, postCode, city);
        Collections.sort(suggestions);
    }

    private List<String> filter(List<AddressTrieNode> searchResult, String houseNumber, int postCode, String city) {
        if(!houseNumber.isBlank() && postCode != 0 && !city.isBlank()) return getAddressesWithNumberPostCodeAndCity(searchResult, houseNumber, postCode, city);
        else if(!houseNumber.isBlank() && postCode == 0 && city.isBlank()) return getAddressesWithNumber(searchResult, houseNumber);
        return getAddresses(searchResult);
    }

    private boolean isMatch(AddressTrieNode node, String houseNumber, int postCode, String city) {
        return node.isValidAddress(houseNumber, postCode, city);
    }

    private List<String> getAddresses(List<AddressTrieNode> searchResult) {
        List<String> list = new ArrayList<>();

        for(AddressTrieNode node: searchResult) {
            list.addAll(node.getAddressesOnStreet());
        }

        return list;
    }

    private List<String> getAddressesWithNumber(List<AddressTrieNode> searchResult, String houseNumber) {
        List<String> list = new ArrayList<>();

        for(AddressTrieNode node: searchResult) {
            list.addAll(node.getAddressFor(houseNumber));
        }

        return list;
    }

    private List<String> getAddressesWithNumberPostCodeAndCity(List<AddressTrieNode> searchResult, String houseNumber, int postCode, String city) {
        return new ArrayList<>(searchResult.get(0).getAddressesFor(houseNumber, postCode, city));
    }

    private boolean matches(String group) {
        return matcher.group(group) != null;
    }

    public void setAddressTree(AddressTriesTree addressTree) {
        this.addressTree = addressTree;
    }

    public List<String> getSuggestions() {
        return suggestions;
    }

    public Node getMatchedAddress() {
        return matchedAddress;
    }
}
