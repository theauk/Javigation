package bfst21.addressparser;

import java.util.HashMap;
import java.util.Scanner;


public class PostcodesCreator {
    private static HashMap<String, String> cities = new HashMap<>();
    private static Scanner scanText;

    public PostcodesCreator(){
        readPostCodeFile();

    }

    private void readPostCodeFile(){
        scanText = new Scanner(getClass().getClassLoader().getResourceAsStream("postnumre.txt"), "UTF-8");
        scanText.useDelimiter("\\n");
        
        //String regex = "(?<postcode>\\d{4}) +(?<city>[a-zA-ZæøåÆØÅ ]*)";
        // Gradle/java did not wanna play with regexing here...

        while(scanText.hasNext()){

        String postcode;
        String city;             
        String currentLine= scanText.next();
        Scanner scanLine = new Scanner(currentLine);
        
        scanLine.useDelimiter(" ");
        
        postcode = scanLine.next();
        city = scanLine.next();

         while(scanLine.hasNext()){
             city += " " + scanLine.next();
         }
         cities.put(postcode, city);
         scanLine.close();      
    }}

    public HashMap<String, String> getPostcodes(){
        return cities;
    }
}

 
