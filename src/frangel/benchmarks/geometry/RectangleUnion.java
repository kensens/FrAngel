package frangel.benchmarks.geometry;

import java.awt.geom.Rectangle2D;

import frangel.Example;
import frangel.SynthesisTask;
import frangel.Tag;
import frangel.benchmarks.BenchmarkGroup;
import frangel.benchmarks.BenchmarkUtils;
import frangel.benchmarks.TaskCreator;

public enum RectangleUnion implements TaskCreator {
    INSTANCE;
    static {
        BenchmarkGroup.GEOMETRY.register(INSTANCE);
    }

    @Override
    public SynthesisTask createTask() {
        SynthesisTask task = new SynthesisTask()
                .setName("rectangleUnion")
                .setInputTypes(Rectangle2D[].class)
                .setInputNames("rects")
                .setOutputType(Rectangle2D.Double.class)
                .makeInputsImmutable()
                .addEqualityTester(Rectangle2D.class, BenchmarkUtils::equalsRectangle2D)
                .addTags(Tag.FOREACH);

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new Rectangle2D[] {
                        new Rectangle2D.Double(3, 4, 4, 2),
                        new Rectangle2D.Double(5, -4, 4, 5),
                        new Rectangle2D.Double(8, 2, 2, 2),
                        new Rectangle2D.Double(10, 5, 3, 3),
                        new Rectangle2D.Double(7, 7, 0, 0),
                } })
                .setOutput(new Rectangle2D.Double(3, -4, 10, 12)));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new Rectangle2D[] {
                        new Rectangle2D.Double(3, 4, 4, 2),
                        new Rectangle2D.Double(5, -4, 4, 5)
                } })
                .setOutput(new Rectangle2D.Double(3, -4, 6, 10)));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new Rectangle2D[] {
                        new Rectangle2D.Double(0.3, 0.4, 0.4, 0.2)
                } })
                .setOutput(new Rectangle2D.Double(0.3, 0.4, 0.4, 0.2)));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new Rectangle2D[] {
                        new Rectangle2D.Double()
                } })
                .setOutput(new Rectangle2D.Double()));

        return task;
    }

    static Rectangle2D.Double solution(Rectangle2D[] rects) {
        Rectangle2D.Double union = new Rectangle2D.Double();
        union.setRect(rects[0]);
        for (Rectangle2D r : rects)
            union.add(r);
        return union;
    }
}
