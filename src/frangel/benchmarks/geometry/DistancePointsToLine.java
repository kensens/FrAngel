package frangel.benchmarks.geometry;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import frangel.Example;
import frangel.SynthesisTask;
import frangel.Tag;
import frangel.benchmarks.BenchmarkGroup;
import frangel.benchmarks.BenchmarkUtils;
import frangel.benchmarks.TaskCreator;

public enum DistancePointsToLine implements TaskCreator {
    INSTANCE;
    static {
        BenchmarkGroup.GEOMETRY.register(INSTANCE);
    }

    @Override
    public SynthesisTask createTask() {
        SynthesisTask task = new SynthesisTask()
                .setName("distancePointsToLine")
                .setInputTypes(Point2D[].class, Line2D.class)
                .setInputNames("points", "line")
                .setOutputType(double.class)
                .makeInputsImmutable()
                .addEqualityTester(Line2D.class, BenchmarkUtils::equalsLine2D)
                .addTags(Tag.FOREACH);

        task.addExample(new Example()
                .setInputs(() -> new Object[] {
                        new Point2D[] { new Point2D.Double(1, 8) },
                        new Line2D.Double(0, 3, 2, 0)
                })
                .setOutput(Math.sqrt(13)));

        task.addExample(new Example()
                .setInputs(() -> new Object[] {
                        new Point2D[] { new Point2D.Double(1, 8), new Point2D.Double(4 - 3*12.3, -3 - 2*12.3) },
                        new Line2D.Double(0, 3, 2, 0)
                })
                .setOutput(Math.sqrt(13) + 12.3 * Math.sqrt(13)));

        task.addExample(new Example()
                .setInputs(() -> new Object[] {
                        new Point2D[] { new Point2D.Double(1, 8), new Point2D.Double(), new Point2D.Double(1, 1.5), new Point2D.Double(4 - 3*12.3, -3 - 2*12.3) },
                        new Line2D.Double(0, 3, 2, 0)
                })
                .setOutput(Math.sqrt(13) + 6/Math.sqrt(13) + 0 + 12.3 * Math.sqrt(13)));

        return task;
    }

    static double solution(Point2D[] points, Line2D line) {
        double sum = 0.0;
        for (Point2D p : points)
            sum += line.ptLineDist(p);
        return sum;
    }
}
