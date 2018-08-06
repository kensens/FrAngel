package frangel.benchmarks.geometry;

import java.awt.geom.Rectangle2D;
import java.util.List;

import frangel.Example;
import frangel.SynthesisTask;
import frangel.Tag;
import frangel.benchmarks.BenchmarkGroup;
import frangel.benchmarks.BenchmarkUtils;
import frangel.benchmarks.TaskCreator;

public enum CountSquares implements TaskCreator {
    INSTANCE;
    static {
        BenchmarkGroup.GEOMETRY.register(INSTANCE);
    }

    @Override
    public SynthesisTask createTask() {
        SynthesisTask task = new SynthesisTask()
                .setName("countSquares")
                .setInputTypes(List.class)
                .addGenerics(List.class, Rectangle2D.class)
                .setInputNames("rectList")
                .setOutputType(int.class)
                .makeInputsImmutable()
                .addTags(Tag.FOREACH, Tag.IF);

        task.addExample(new Example()
                .setInputs(() -> new Object[] { BenchmarkUtils.makeList(
                        new Rectangle2D.Double(12, 34, 56.7, 56.7)
                        )})
                .setOutput(1));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { BenchmarkUtils.makeList(
                        new Rectangle2D.Double(9.9, 8.8, 77.1, 77),
                        new Rectangle2D.Double(9.9, 8.8, 76.9, 77)
                        )})
                .setOutput(0));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { BenchmarkUtils.makeList(
                        new Rectangle2D.Double(9.9, 8.8, 77, 77),
                        new Rectangle2D.Double(9.9, 8.8, 77, 76.9)
                        )})
                .setOutput(1));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { BenchmarkUtils.makeList(
                        new Rectangle2D.Double(9.9, 8.8, 77, 76.9),
                        new Rectangle2D.Double(9.9, 8.8, 77, 77)
                        )})
                .setOutput(1));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { BenchmarkUtils.makeList(
                        new Rectangle2D.Double(9.9, 8.8, 77, 77),
                        new Rectangle2D.Double(9.9, 8.8, 76.9, 76.9)
                        )})
                .setOutput(2));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { BenchmarkUtils.makeList(
                        new Rectangle2D.Double(0, 0.1, 12, 2000),
                        new Rectangle2D.Double(0, 0, 12.3, 12.3),
                        new Rectangle2D.Double(0.1, 0, 17, 17),
                        new Rectangle2D.Double(0, -0.1, 12, 0),
                        new Rectangle2D.Double(0, 0, 9999.9, 9999.9),
                        new Rectangle2D.Double(0, 0, 3.14, 3.14),
                        new Rectangle2D.Double(0, 0, 0, 12),
                        new Rectangle2D.Double(-0.1, 0, 0.1, 0.1)
                        )})
                .setOutput(5));

        return task;
    }

    static int solution(List<Rectangle2D> rectList) {
        // Assume positive width or height
        int count = 0;
        for (Rectangle2D rect : rectList)
            if (rect.getWidth() == rect.getHeight())
                count++;
        return count;
    }
}
