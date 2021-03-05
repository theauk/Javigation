package bfst21.addressparser;

import java.util.regex.*;


public class Address {
  public final String street, house, floor, side, postcode, city;
  

  private Address(String _street, String _house, String _floor, String _side, String _postcode, String _city) {
    street = _street;
    house = _house;
    floor = _floor;
    side = _side;
    postcode = _postcode;
    city = _city;
    
  }

  public String toStringSpecial(){
    String string = "Street : " + street + "\n";
    string += "House : " + house + "\n";
    string += "Floor : " + floor + "\n";
    string += "Side : " + side + "\n";
    string += "Postcode : " + postcode + "\n";
    string += "City : " + city + "\n";

    return string;
  }

  public String toString() {
    return street + " " + house + ", " + floor + " " + side + "\n" + postcode + " " + city;
  }

  static String regex = "(?<street>[a-zA-ZæøåÆØÅ ]*?),? +(?<house>\\d+[a-zA-ZæøåÆØÅ]*?),?\\.? +(?<floor>(\\d+\\.?,? *?(sal|Sal)?)|[a-zA-Z]*\\.?)?,? *(?<side>(\\D{2})|(\\d+)\\.?)?,?\\.? *(?<postcode>[0-9]{4}),? *(?<city>[a-zA-ZæøåÆØÅ ]*)";
  static Pattern pattern = Pattern.compile(regex);

  public static Address parse(String input) throws badInputException {
    Model info = Controller.getModel();
    var matcher = pattern.matcher(input);
    Builder build = new Builder();

    if (matcher.matches()) {
      build.street(matcher.group("street"));
      build.house(matcher.group("house"));

      if (matcher.group("floor") != null ){
        build.floor(matcher.group("floor"));
      } else {build.floor("");}
      
      if (matcher.group("side") != null ){
        build.side(matcher.group("side"));
      } else {build.side("");}

      build.postcode(matcher.group("postcode"));

     
      if (matcher.group("city") == "" && matcher.group("postcode").length() == 4) {
          build.city(info.getCity(matcher.group("postcode")));      
      } else {
      build.city(matcher.group("city"));
    } 
  } else {
    throw new badInputException("Input provided is not an address");
  }
  return build.build();
} 
  public static class Builder {
    private String street, house, floor, side, postcode, city;

    public Builder street(String _street) {
      street = _street;
      return this;
    }

    public Builder house(String _house) {
      house = _house;
      return this;
    }

    public Builder floor(String _floor) {
      floor = _floor;
      return this;
    }

    public Builder side(String _side) {
      side = _side;
      return this;
    }

    public Builder postcode(String _postcode) {
      postcode = _postcode;
      return this;
    }

    public Builder city(String _city) {
      city = _city;
      return this;
    }

    public Address build() {
      return new Address(street, house, floor, side, postcode, city);
    }
  }
}
