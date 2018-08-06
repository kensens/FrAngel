// Thrown when a SynthesisTask is not set up correctly.

package frangel;

@SuppressWarnings("serial")
public class SynthesisTaskException extends RuntimeException {
    public SynthesisTaskException() {
        super();
    }
    public SynthesisTaskException(String message) {
        super(message);
    }
}
