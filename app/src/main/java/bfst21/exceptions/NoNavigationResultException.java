package bfst21.exceptions;

/**
 * Thrown if it is not possible to find a route between the chosen from and to Nodes.
 */
public class NoNavigationResultException extends Exception {
    public NoNavigationResultException() {
        super("It was not possible to find a route between the two points.");
    }
}
