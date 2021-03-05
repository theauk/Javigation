package bfst21.addressparser;

import java.util.ArrayList;
import java.util.HashMap;


public class Model {
    private ArrayList<Address> createdAddresses;
    private HashMap<String, String> cities;

    public Model(){
        createdAddresses = new ArrayList<>();
        cities = new PostcodesCreator().getPostcodes();
    }

    public void addCreatedAddress(Address address){
        createdAddresses.add(address);
    }

    public ArrayList<Address> getCreatedAddresses(){
        return createdAddresses;
    }

    public String getCity(String postcode) throws badInputException{
        
        if(cities.containsKey(postcode)){
            return cities.get(postcode);

        } else {
            throw new badInputException("Postcode " + postcode + " does not belong to any city");
        }

    }


}
