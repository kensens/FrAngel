package frangel.benchmarks.geometry;

import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

import frangel.Example;
import frangel.SynthesisTask;
import frangel.Tag;
import frangel.benchmarks.BenchmarkGroup;
import frangel.benchmarks.TaskCreator;

public enum DistanceInCircle implements TaskCreator {
    INSTANCE;
    static {
        BenchmarkGroup.GEOMETRY.register(INSTANCE);
    }

    @Override
    public SynthesisTask createTask() {
        SynthesisTask task = new SynthesisTask()
                .setName("distanceInCircle")
                .setInputTypes(Point2D.class, Ellipse2D.class)
                .setInputNames("point", "circle")
                .setOutputType(double.class)
                .addClasses(Double.class)
                .makeInputsImmutable()
                .addTags(Tag.IF);

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new Point2D.Double(3.6, -5), new Ellipse2D.Double(-7, -6, 20, 20) })
                .setOutput(Math.sqrt(81.36)));

        final double mult = 12.3;
        final double px = 3 * mult;
        final double py = 4 * mult;
        final double d  = 5 * mult;

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new Point2D.Double(px, py), new Ellipse2D.Double(-1.1*d, -1.1*d, 2.2*d, 2.2*d) })
                .setOutput(d));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new Point2D.Double(px, py), new Ellipse2D.Double(-0.9*d, -0.9*d, 1.8*d, 1.8*d) })
                .setOutput(Double.POSITIVE_INFINITY));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new Point2D.Double(px + 45.6, py), new Ellipse2D.Double(-1.1*d + 45.6, -1.1*d, 2.2*d, 2.2*d) })
                .setOutput(d));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new Point2D.Double(px, py + 45.6), new Ellipse2D.Double(-1.1*d, -1.1*d + 45.6, 2.2*d, 2.2*d) })
                .setOutput(d));

        return task;
    }

    static double solution(Point2D point, Ellipse2D circle) {
        // Assume circle has positive width and height, point not on boundary
        if (circle.contains(point))
            return point.distance(circle.getCenterX(), circle.getCenterY());
        else
            return Double.POSITIVE_INFINITY;
    }
}
