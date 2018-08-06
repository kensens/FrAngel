package frangel.benchmarks.geometry;

import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;

import frangel.Example;
import frangel.SynthesisTask;
import frangel.Tag;
import frangel.benchmarks.BenchmarkGroup;
import frangel.benchmarks.TaskCreator;

public enum CenterAtOrigin implements TaskCreator {
    INSTANCE;
    static {
        BenchmarkGroup.GEOMETRY.register(INSTANCE);
    }

    @Override
    public SynthesisTask createTask() {
        SynthesisTask task = new SynthesisTask()
                .setName("centerAtOrigin")
                .setInputTypes(RectangularShape.class)
                .setInputNames("shape")
                .setOutputType(void.class)
                .addTags(Tag.SINGLE_LINE);

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new Ellipse2D.Double(12, 34, 5.6, 7.8) })
                .setModifiedInput(1, new Ellipse2D.Double(-2.8, -3.9, 5.6, 7.8)));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new Rectangle2D.Double(12, 34, 0.0, 7.8) })
                .setModifiedInput(1, new Rectangle2D.Double(0.0, -3.9, 0.0, 7.8)));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new Ellipse2D.Double(12, 34, 5.6, 0.0) })
                .setModifiedInput(1, new Ellipse2D.Double(-2.8, -0.0, 5.6, 0.0)));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new Ellipse2D.Double(12, 34, 2.0, 0.0) })
                .setModifiedInput(1, new Ellipse2D.Double(-1.0, -0.0, 2.0, 0.0)));

        return task;
    }

    static void solution(RectangularShape shape) {
        shape.setFrameFromCenter(0, 0, shape.getWidth() / 2, shape.getHeight() / 2);
    }
}
