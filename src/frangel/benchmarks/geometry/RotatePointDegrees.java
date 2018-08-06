package frangel.benchmarks.geometry;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import frangel.Example;
import frangel.SynthesisTask;
import frangel.Tag;
import frangel.benchmarks.BenchmarkGroup;
import frangel.benchmarks.BenchmarkUtils;
import frangel.benchmarks.TaskCreator;

public enum RotatePointDegrees implements TaskCreator {
    INSTANCE;
    static {
        BenchmarkGroup.GEOMETRY.register(INSTANCE);
    }

    @Override
    public SynthesisTask createTask() {
        SynthesisTask task = new SynthesisTask()
                .setName("rotatePointDegrees")
                .setInputNames("point", "degrees")
                .setInputTypes(Point2D.class, double.class)
                .setOutputType(void.class)
                .addClasses(Math.class)
                .addPackages("java.awt.geom")
                .addEqualityTester(Point2D.class, BenchmarkUtils::equalsPoint2D)
                .addTags(Tag.SINGLE_LINE);

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new Point2D.Double(3, 0), 60.0 })
                .setModifiedInput(1, new Point2D.Double(1.5, 1.5 * Math.sqrt(3))));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new Point2D.Double(3, 0), 180.0 / Math.PI })
                .setModifiedInput(1, new Point2D.Double(3 * Math.cos(1), 3 * Math.sin(1))));

        return task;
    }

    static void solution(Point2D point, double theta) {
        AffineTransform.getRotateInstance(theta * Math.PI / 180.0).transform(point, point);
    }
}
