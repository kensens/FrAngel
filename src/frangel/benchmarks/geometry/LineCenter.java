package frangel.benchmarks.geometry;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import frangel.Example;
import frangel.SynthesisTask;
import frangel.Tag;
import frangel.benchmarks.BenchmarkGroup;
import frangel.benchmarks.BenchmarkUtils;
import frangel.benchmarks.TaskCreator;

public enum LineCenter implements TaskCreator {
    INSTANCE;
    static {
        BenchmarkGroup.GEOMETRY.register(INSTANCE);
    }

    @Override
    public SynthesisTask createTask() {
        SynthesisTask task = new SynthesisTask()
                .setName("lineCenter")
                .setInputTypes(Line2D.class)
                .setInputNames("line")
                .setOutputType(Point2D.class)
                .addPackages("java.awt.geom")
                .makeInputsImmutable()
                .addEqualityTester(Line2D.class, BenchmarkUtils::equalsLine2D)
                .addEqualityTester(Point2D.class, BenchmarkUtils::equalsPoint2D)
                .addTags(Tag.SINGLE_LINE);

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new Line2D.Double(2.3, 4.5, 20, 30) })
                .setOutput(new Point2D.Double(11.15, 17.25)));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new Line2D.Double(2.3, 0, 20, 0) })
                .setOutput(new Point2D.Double(11.15, 0)));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new Line2D.Double(-2, 4.5, 4, 30) })
                .setOutput(new Point2D.Double(1, 17.25)));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new Line2D.Double(2, 4.5, 0, -4.5) })
                .setOutput(new Point2D.Double(1, 0)));

        return task;
    }

    static Point2D solution(Line2D line) {
        return new Point2D.Double(line.getBounds2D().getCenterX(), line.getBounds2D().getCenterY());
    }
}
