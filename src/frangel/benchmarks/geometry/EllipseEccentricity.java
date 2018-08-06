package frangel.benchmarks.geometry;

import java.awt.geom.Ellipse2D;

import frangel.Example;
import frangel.SynthesisTask;
import frangel.benchmarks.BenchmarkGroup;
import frangel.benchmarks.TaskCreator;

public enum EllipseEccentricity implements TaskCreator {
    INSTANCE;
    static {
        BenchmarkGroup.GEOMETRY.register(INSTANCE);
    }

    @Override
    public SynthesisTask createTask() {
        SynthesisTask task = new SynthesisTask()
                .setName("ellipseEccentricity")
                .setInputTypes(Ellipse2D.class)
                .setInputNames("ellipse")
                .setOutputType(double.class)
                .addClasses(Math.class)
                .makeInputsImmutable()
                .addTags(/* none applicable */);

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new Ellipse2D.Double(12.3, -45.6, 9, 13) })
                .setOutput(2 * Math.sqrt(22) / 13));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new Ellipse2D.Double(12.3, -45.6, 13, 9) })
                .setOutput(2 * Math.sqrt(22) / 13));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new Ellipse2D.Double(12.3, -45.6, 5.6, 5.6) })
                .setOutput(0.0));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new Ellipse2D.Double(12.3, -45.6, 5.6, 0) })
                .setOutput(1.0));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new Ellipse2D.Double(12.3, -45.6, 5.6, 1) })
                .setOutput(Math.sqrt(5.6*5.6 - 1) / 5.6));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new Ellipse2D.Double(12.3, -45.6, 1, 5.6) })
                .setOutput(Math.sqrt(5.6*5.6 - 1) / 5.6));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new Ellipse2D.Double(12.3, -45.6, 0.7, 1) })
                .setOutput(Math.sqrt(0.51)));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new Ellipse2D.Double(12.3, -45.6, 1, 0.7) })
                .setOutput(Math.sqrt(0.51)));

        return task;
    }

    static double solution(Ellipse2D ellipse) {
        // Assume nonnegative width and height, not both 0
        double a = Math.max(ellipse.getWidth(), ellipse.getHeight());
        double b = Math.min(ellipse.getWidth(), ellipse.getHeight());
        return Math.sqrt(1 - b*b/(a*a));
    }
}
