package demo;

import java.awt.Point;

import frangel.Example;
import frangel.FrAngel;
import frangel.SynthesisTask;

/*
 * Objective: synthesize the following function.
 *
 * // Returns the element-wise sum of two Points without modifying the inputs.
 * static Point addPoints(Point p1, Point p2) {
 *     ...
 * }
 *
 * For example, if the two input Points are (11, 20) and (15, -50), then
 * addPoints should return the Point (26, -30).
 */

public class TutorialStarter {
    public static void main(String[] args) {

        /*
         * Step 1: Create a SynthesisTask describing the desired function.
         *
         * The input and output types are specified with Class objects. Other
         * examples of Class objects include int.class, String.class,
         * List.class, double[].class, and void.class.
         */
        SynthesisTask task = new SynthesisTask()
                .setName("addPoints")
                .setInputTypes(Point.class, Point.class)
                .setInputNames("p1", "p2")
                .setOutputType(Point.class);

        /*
         * Step 2: Add Example objects to the SynthesisTask.
         *
         * Note that setInputs() takes a *lambda* that accepts no arguments
         * and returns an Object[] containing the desired inputs.
         */
        task.addExample(new Example()
                .setInputs(() -> new Object[] { new Point(11, 20), new Point(15, -50) })
                .setOutput(new Point(26, -30)));

        // More examples can be added by calling addExample() again:
        // task.addExample(...);

        // Step 3: Run the synthesizer.
        FrAngel.synthesize(task);
    }
}
