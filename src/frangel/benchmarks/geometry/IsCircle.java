package frangel.benchmarks.geometry;

import java.awt.geom.Ellipse2D;

import frangel.Example;
import frangel.SynthesisTask;
import frangel.Tag;
import frangel.benchmarks.BenchmarkGroup;
import frangel.benchmarks.TaskCreator;

public enum IsCircle implements TaskCreator {
    INSTANCE;
    static {
        BenchmarkGroup.GEOMETRY.register(INSTANCE);
    }

    @Override
    public SynthesisTask createTask() {
        SynthesisTask task = new SynthesisTask()
                .setName("isCircle")
                .setInputTypes(Ellipse2D.class)
                .setInputNames("ellipse")
                .setOutputType(boolean.class)
                .makeInputsImmutable()
                .addTags(Tag.SINGLE_LINE);

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new Ellipse2D.Double(12.3, -45.6, 7.8, 7.9) })
                .setOutput(false));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new Ellipse2D.Double(12.3, -45.6, 7.8, 7.8) })
                .setOutput(true));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new Ellipse2D.Double(12.3, -45.6, 7.8, 7.7) })
                .setOutput(false));

        return task;
    }

    static boolean solution(Ellipse2D ellipse) {
        // Assume positive width or height
        return ellipse.getWidth() == ellipse.getHeight();
    }
}
