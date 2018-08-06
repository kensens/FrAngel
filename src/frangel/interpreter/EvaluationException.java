// If this gets thrown, there is a bug in Interpreter.

package frangel.interpreter;

@SuppressWarnings("serial")
public class EvaluationException extends Exception {
    public EvaluationException() {
        super();
    }
    public EvaluationException(String message) {
        super(message);
    }
}
