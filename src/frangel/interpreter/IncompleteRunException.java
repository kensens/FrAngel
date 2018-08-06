// Thrown when halting code interpretation due to timeout or memory constraints.

package frangel.interpreter;

@SuppressWarnings("serial")
public class IncompleteRunException extends Exception {
    public IncompleteRunException() {
        super();
    }
    public IncompleteRunException(String message) {
        super(message);
    }
}
