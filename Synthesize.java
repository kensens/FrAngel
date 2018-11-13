// A minimal Java program that uses FrAngel to synthesize an "add" operation.

import frangel.Example;
import frangel.FrAngel;
import frangel.SynthesisTask;

public class Synthesize {
    public static void main(String[] args) {
        // Specifies the function signature "static int add(int x, int y)".
        SynthesisTask task = new SynthesisTask()
            .setName("add")
            .setInputTypes(int.class, int.class)
            .setInputNames("x", "y")
            .setOutputType(int.class);

        // An input-output example specifying that add(12, 34) should return 46.
        task.addExample(new Example()
                .setInputs(() -> new Object[] { 12, 34 })
                .setOutput(46));

        // Invokes the FrAngel synthesizer on the SynthesisTask.
        FrAngel.synthesize(task);
    }
}
