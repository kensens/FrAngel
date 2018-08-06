package frangel.benchmarks.geometry;

import java.awt.geom.Rectangle2D;

import frangel.Example;
import frangel.SynthesisTask;
import frangel.benchmarks.BenchmarkGroup;
import frangel.benchmarks.TaskCreator;

public enum RectangleIntersectionArea implements TaskCreator {
    INSTANCE;
    static {
        BenchmarkGroup.GEOMETRY.register(INSTANCE);
    }

    @Override
    public SynthesisTask createTask() {
        SynthesisTask task = new SynthesisTask()
                .setName("rectangleIntersectionArea")
                .setInputTypes(Rectangle2D.class, Rectangle2D.class)
                .setInputNames("rect1", "rect2")
                .setOutputType(double.class)
                .makeInputsImmutable()
                .addTags(/* none applicable */);

        task.addExample(new Example()
                .setInputs(() -> new Object[] {
                        new Rectangle2D.Double(3.0, 4.0, 5.6, 0.9),
                        new Rectangle2D.Double(8.0, 4.2, 11.1, 3.0),
                })
                .setOutput(0.42));

        task.addExample(new Example()
                .setInputs(() -> new Object[] {
                        new Rectangle2D.Double(3.0, 4.0, 6.0, 0.9),
                        new Rectangle2D.Double(8.0, 4.2, 11.1, 3.0),
                })
                .setOutput(0.7));

        task.addExample(new Example()
                .setInputs(() -> new Object[] {
                        new Rectangle2D.Double(3.0, 4.0, 5.6, 1.0),
                        new Rectangle2D.Double(8.0, 3.0, 11.1, 3.0),
                })
                .setOutput(0.6));

        return task;
    }

    static double solution(Rectangle2D rect1, Rectangle2D rect2) {
        Rectangle2D intersection = rect1.createIntersection(rect2);
        return intersection.getWidth() * intersection.getHeight();
    }
}
