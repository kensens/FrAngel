package demo;

import java.awt.geom.Point2D;

import frangel.Example;
import frangel.FrAngel;
import frangel.SynthesisTask;

// rotate Point2D about the origin by some angle:
// Point2D rotatePoint(Point2D point, double angle)

public class Demo {
    public static void main(String[] args) {
        SynthesisTask task = new SynthesisTask()
                .setName("rotatePoint")
                .setInputTypes(Point2D.class, double.class)
                .setInputNames("point", "angle")
                .setOutputType(Point2D.class)
                .addEqualityTester(Point2D.class, Demo::pointEquals)
                .addClasses(Math.class)
                .addPackages("java.awt.geom");

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new Point2D.Double(3, 0), 60.0 })
                .setOutput(new Point2D.Double(1.5, 1.5 * Math.sqrt(3))));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new Point2D.Double(3, 0), 180 / Math.PI })
                .setOutput(new Point2D.Double(3 * Math.cos(1), 3 * Math.sin(1))));

        FrAngel.synthesize(task);
//        FrAngel.test(task, Demo.class);
    }

    static java.awt.geom.Point2D rotatePoint(java.awt.geom.Point2D point, double angle) {
        return java.awt.geom.AffineTransform.getRotateInstance(angle * Math.PI / 180).transform(point, null);
    }

    static boolean pointEquals(Point2D p1, Point2D p2) {
        return Math.abs(p1.getX() - p2.getX()) < 1e-5 && Math.abs(p1.getY() - p2.getY()) < 1e-5;
    }
}
