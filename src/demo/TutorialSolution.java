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
 *
 * We will use all of the following examples:
 *    p1         p2            output
 * 1. (11, 20) + (15, -50) --> (26, -30)
 * 2. (11, 0)  + (15, 0)   --> (26, 0)
 * 3. (0, 20)  + (0, -50)  --> (0, -30)
 */

public class TutorialSolution {
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
                .setOutputType(Point.class)
                .makeInputsImmutable(); // Disallow modifying input points

        /*
         * Some classes do not implement .equals() in a sufficient way, so we
         * can provide custom equality tests. Similarly, we can customize how
         * objects are printed, which is useful for unit-testing (below). These
         * functions do not need to handle null values.
         */
        task.addEqualityTester(Point.class, (Point p, Point q) -> p.x == q.x && p.y == q.y);
        task.addToString(Point.class, (Point p) -> "(" + p.x + ", " + p.y + ")");

        /*
         * Step 2: Add Example objects to the SynthesisTask.
         *
         * Note that setInputs() takes a *lambda* that accepts no arguments
         * and returns an Object[] containing the desired inputs.
         */
        task.addExample(new Example()
                .setName("general") // Name optional
                .setInputs(() -> new Object[] { new Point(11, 20), new Point(15, -50) })
                .setOutput(new Point(26, -30)));

        task.addExample(new Example()
                .setName("only x coords")
                .setInputs(() -> new Object[] { new Point(11, 0), new Point(15, 0) })
                .setOutput(new Point(26, 0)));

        task.addExample(new Example()
                .setName("only y coords")
                .setInputs(() -> new Object[] { new Point(0, 20), new Point(0, -50) })
                .setOutput(new Point(0, -30)));

        /*
         * We can also perform simple checks on the output object instead of
         * performing a full object equality comparison.
         */
        @SuppressWarnings("unused")
        Example unusedExample = new Example()
        .setInputs(() -> new Object[] { new Point(11, 20), new Point(15, -50) })
        .setOutputChecker((Point p) -> p.x == 26);

        // Step 3: Run the synthesizer.
        FrAngel.synthesize(task);

        // Bonus: We can also unit-test implementations.
        FrAngel.test(task, TutorialSolution.class, "addPoints"); // Correct
        FrAngel.test(task, TutorialSolution.class, "buggy1");    // Buggy (typo: p2.y -> p1.y)
        FrAngel.test(task, TutorialSolution.class, "buggy2");    // Buggy (modifies inputs)
    }

    // Example implementations for unit testing
    static Point addPoints(Point p1, Point p2) {
        return new Point(p1.x + p2.x, p1.y + p2.y);
    }
    static Point buggy1(Point p1, Point p2) {
        return new Point(p1.x + p2.x, p1.y + p1.y);
    }
    static Point buggy2(Point p1, Point p2) {
        p1.translate(p2.x, p2.y); // Modifies p1
        return p1;
    }
}
