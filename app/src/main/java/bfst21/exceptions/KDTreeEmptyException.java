package bfst21.exceptions;

public class KDTreeEmptyException extends Exception {
    public KDTreeEmptyException(String message) {
        super(message);
    }
    public KDTreeEmptyException(){
        super("KDTree is empty!");
    }
}
