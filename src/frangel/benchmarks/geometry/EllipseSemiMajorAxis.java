package frangel.benchmarks.geometry;

import java.awt.geom.Ellipse2D;

import frangel.Example;
import frangel.SynthesisTask;
import frangel.Tag;
import frangel.benchmarks.BenchmarkGroup;
import frangel.benchmarks.TaskCreator;

public enum EllipseSemiMajorAxis implements TaskCreator {
    INSTANCE;
    static {
        BenchmarkGroup.GEOMETRY.register(INSTANCE);
    }

    @Override
    public SynthesisTask createTask() {
        SynthesisTask task = new SynthesisTask()
                .setName("ellipseSemiMajorAxis")
                .setInputTypes(Ellipse2D.class)
                .setInputNames("ellipse")
                .setOutputType(double.class)
                .makeInputsImmutable()
                .addTags(Tag.SINGLE_LINE);

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new Ellipse2D.Double(1.2, 3.4, 10.24, 10.23) })
                .setOutput(5.12));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new Ellipse2D.Double(1.2, 3.4, 10.22, 10.23) })
                .setOutput(5.115));

        return task;
    }

    static double solution(Ellipse2D ellipse) {
        // Assume nonnegative width and height
        return Math.max(ellipse.getWidth(), ellipse.getHeight()) / 2;
    }
}
