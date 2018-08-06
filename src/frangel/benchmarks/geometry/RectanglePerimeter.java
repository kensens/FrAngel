package frangel.benchmarks.geometry;

import java.awt.geom.Rectangle2D;

import frangel.Example;
import frangel.SynthesisTask;
import frangel.Tag;
import frangel.benchmarks.BenchmarkGroup;
import frangel.benchmarks.TaskCreator;

public enum RectanglePerimeter implements TaskCreator {
    INSTANCE;
    static {
        BenchmarkGroup.GEOMETRY.register(INSTANCE);
    }

    @Override
    public SynthesisTask createTask() {
        SynthesisTask task = new SynthesisTask()
                .setName("rectanglePerimeter")
                .setInputTypes(Rectangle2D.class)
                .setInputNames("rect")
                .setOutputType(double.class)
                .makeInputsImmutable()
                .addTags(Tag.SINGLE_LINE);

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new Rectangle2D.Double(1.2, 3.4, 5.6, 7.8) })
                .setOutput(26.8));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new Rectangle2D.Double(1.2, 3.4, 5.6, 0) })
                .setOutput(11.2));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new Rectangle2D.Double(1.2, 3.4, 0, 7.8) })
                .setOutput(15.6));

        return task;
    }

    static double solution(Rectangle2D rect) {
        // Assume nonnegative width and height
        return 2 * (rect.getWidth() + rect.getHeight());
    }
}
