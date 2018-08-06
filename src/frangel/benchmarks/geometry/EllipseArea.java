package frangel.benchmarks.geometry;

import java.awt.geom.Ellipse2D;

import frangel.Example;
import frangel.SynthesisTask;
import frangel.Tag;
import frangel.benchmarks.BenchmarkGroup;
import frangel.benchmarks.TaskCreator;

public enum EllipseArea implements TaskCreator {
    INSTANCE;
    static {
        BenchmarkGroup.GEOMETRY.register(INSTANCE);
    }

    @Override
    public SynthesisTask createTask() {
        SynthesisTask task = new SynthesisTask()
                .setName("ellipseArea")
                .setInputTypes(Ellipse2D.class)
                .setInputNames("ellipse")
                .setOutputType(double.class)
                .addClasses(Math.class)
                .makeInputsImmutable()
                .addTags(Tag.SINGLE_LINE);

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new Ellipse2D.Double(12.3, -45.6, 7.8, 9) })
                .setOutput(3.9 * 4.5 * Math.PI));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new Ellipse2D.Double(12.3, -45.6, 7.8, 2) })
                .setOutput(3.9 * Math.PI));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new Ellipse2D.Double(12.3, -45.6, 2, 7.8) })
                .setOutput(3.9 * Math.PI));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new Ellipse2D.Double(12.3, -45.6, 2, 2) })
                .setOutput(Math.PI));

        return task;
    }

    static double solution(Ellipse2D ellipse) {
        // Assume nonnegative width and height
        return Math.PI * ellipse.getWidth() * ellipse.getHeight() / 4;
    }
}
