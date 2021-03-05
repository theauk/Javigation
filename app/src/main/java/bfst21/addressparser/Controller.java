package bfst21.addressparser;


public class Controller {
    private static Model model;

    private Controller(){

    }
    public static void setUp(){
        model = new Model();
        
    }

    public static Model getModel(){
        return model;
    }

    private static Address getAddress(String input) throws badInputException{
        return Address.parse(input);
       
    }

    public static String getCity(String postcode) throws badInputException{
        return model.getCity(postcode);
        }
    

    public static String createAddressClicked(){
        try{
            Address address = getAddress(GUI.getInput());
            model.addCreatedAddress(address);
            GUI.updateCreatedAddresses(address.toString());
            return address.toStringSpecial();
        }catch (badInputException e){
            return e.getMessage();
        }

    }
    public static String getCreatedAdressedClicked(){
            return model.getCreatedAddresses().get(GUI.getCreatedAddress()).toStringSpecial();
    }

}
