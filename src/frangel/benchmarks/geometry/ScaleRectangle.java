package frangel.benchmarks.geometry;

import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import frangel.Example;
import frangel.SynthesisTask;
import frangel.Tag;
import frangel.benchmarks.BenchmarkGroup;
import frangel.benchmarks.BenchmarkUtils;
import frangel.benchmarks.TaskCreator;

public enum ScaleRectangle implements TaskCreator {
    INSTANCE;
    static {
        BenchmarkGroup.GEOMETRY.register(INSTANCE);
    }

    @Override
    public SynthesisTask createTask() {
        SynthesisTask task = new SynthesisTask()
                .setName("scaleRectangle")
                .setInputTypes(Rectangle2D.class, double.class, double.class)
                .setInputNames("rect", "scaleX", "scaleY")
                .setOutputType(Rectangle2D.class)
                .addPackages("java.awt.geom")
                .makeInputsImmutable()
                .addEqualityTester(Rectangle2D.class, BenchmarkUtils::equalsRectangle2D)
                .addTags(Tag.SINGLE_LINE);

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new Rectangle2D.Double(1.2, 3.4, 5.6, 7.8), 0.5, 0.4 })
                .setOutput(new Rectangle2D.Double(0.6, 1.36, 2.8, 3.12)));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new Rectangle2D.Double(1.2, 3.4, 5.6, 7.8), 1.0, 0.0 })
                .setOutput(new Rectangle2D.Double(1.2, 0.0, 5.6, 0.0)));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new Rectangle2D.Double(1.2, 3.4, 5.6, 7.8), 0.0, 1.0 })
                .setOutput(new Rectangle2D.Double(0.0, 3.4, 0.0, 7.8)));

        return task;
    }

    static Rectangle2D solution(Rectangle2D rect, double scaleX, double scaleY) {
        return AffineTransform.getScaleInstance(scaleX, scaleY).createTransformedShape(rect).getBounds2D();
    }
}
