package frangel.benchmarks.geometry;

import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import frangel.Example;
import frangel.SynthesisTask;
import frangel.Tag;
import frangel.benchmarks.BenchmarkGroup;
import frangel.benchmarks.BenchmarkUtils;
import frangel.benchmarks.TaskCreator;

public enum FilterContainsPoint implements TaskCreator {
    INSTANCE;
    static {
        BenchmarkGroup.GEOMETRY.register(INSTANCE);
    }

    @Override
    public SynthesisTask createTask() {
        SynthesisTask task = new SynthesisTask()
                .setName("filterContainsPoint")
                .setInputTypes(Ellipse2D[].class, Point2D.class)
                .setInputNames("shapes", "point")
                .setOutputType(List.class)
                .addClasses(ArrayList.class)
                .addGenerics(List.class, Ellipse2D.class)
                .makeInputsImmutable()
                .addTags(Tag.FOREACH, Tag.IF);

        task.addExample(new Example()
                .setInputs(() -> new Object[] {
                        new Ellipse2D[] {
                                new Ellipse2D.Double(-4, -8, 7, 14),    // yes
                                new Ellipse2D.Double(-9, -8, 10, 10.5), // yes
                                new Ellipse2D.Double(-8, -9, 10, 10.1), // yes
                                new Ellipse2D.Double(-8, -9, 10, 9.5),  // no
                                new Ellipse2D.Double(-8, -9, 10.5, 10), // yes
                                new Ellipse2D.Double(-9, -8.5, 10, 10), // no
                                new Ellipse2D.Double(-8, -8.5, 10, 10), // yes
                        },
                        new Point2D.Double()
                })
                .setOutput(BenchmarkUtils.makeList(
                        new Ellipse2D.Double(-4, -8, 7, 14),
                        new Ellipse2D.Double(-9, -8, 10, 10.5),
                        new Ellipse2D.Double(-8, -9, 10, 10.1),
                        new Ellipse2D.Double(-8, -9, 10.5, 10),
                        new Ellipse2D.Double(-8, -8.5, 10, 10)
                        )));

        task.addExample(new Example()
                .setInputs(() -> new Object[] {
                        new Ellipse2D[] {
                                new Ellipse2D.Double(11, 43, 111, 6.6), // yes
                                new Ellipse2D.Double(11, 43, 111, 4.3), // yes
                        },
                        new Point2D.Double(12.3, 45.6)
                })
                .setOutput(BenchmarkUtils.makeList(
                        new Ellipse2D.Double(11, 43, 111, 6.6),
                        new Ellipse2D.Double(11, 43, 111, 4.3)
                        )));

        task.addExample(new Example()
                .setInputs(() -> new Object[] {
                        new Ellipse2D[] {
                                new Ellipse2D.Double(11, 43, 111, 6.7), // no
                                new Ellipse2D.Double(11, 43, 111, 4.3), // yes
                        },
                        new Point2D.Double(12.3, 45.6)
                })
                .setOutput(BenchmarkUtils.makeList(new Ellipse2D.Double(11, 43, 111, 4.3))));

        task.addExample(new Example()
                .setInputs(() -> new Object[] {
                        new Ellipse2D[] { new Ellipse2D.Double(34, 12, 10, 10) }, // yes
                        new Point2D.Double(38, 18)
                })
                .setOutput(BenchmarkUtils.makeList(new Ellipse2D.Double(34, 12, 10, 10))));

        return task;
    }

    static List<Ellipse2D> solution(Ellipse2D[] shapes, Point2D point) {
        // Assume point not on boundary
        List<Ellipse2D> list = new ArrayList<>();
        for (Ellipse2D e : shapes)
            if (e.contains(point))
                list.add(e);
        return list;
    }
}
