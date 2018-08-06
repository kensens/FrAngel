package frangel.benchmarks.geometry;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import frangel.Example;
import frangel.SynthesisTask;
import frangel.Tag;
import frangel.benchmarks.BenchmarkGroup;
import frangel.benchmarks.BenchmarkUtils;
import frangel.benchmarks.TaskCreator;

public enum ConnectPoints implements TaskCreator {
    INSTANCE;
    static {
        BenchmarkGroup.GEOMETRY.register(INSTANCE);
    }

    @Override
    public SynthesisTask createTask() {
        SynthesisTask task = new SynthesisTask()
                .setName("connectPoints")
                .setInputTypes(Point2D[].class)
                .setInputNames("points")
                .setOutputType(List.class)
                .addClasses(ArrayList.class)
                .addGenerics(List.class, Line2D.Double.class)
                .makeInputsImmutable()
                .addEqualityTester(Line2D.class, BenchmarkUtils::equalsLine2D)
                .addTags(Tag.FOR);

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new Point2D[] {
                        new Point2D.Double(1.2, 3.4),
                        new Point2D.Double(5.6, 7.8),
                        new Point2D.Double(2.1, 4.3),
                        new Point2D.Double()
                }})
                .setOutput(BenchmarkUtils.makeList(
                        new Line2D.Double(1.2, 3.4, 5.6, 7.8),
                        new Line2D.Double(5.6, 7.8, 2.1, 4.3),
                        new Line2D.Double(2.1, 4.3, 0, 0)
                        )));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new Point2D[] {
                        new Point2D.Double(1.2, 3.4),
                        new Point2D.Double(5.6, 7.8)
                }})
                .setOutput(BenchmarkUtils.makeList(
                        new Line2D.Double(1.2, 3.4, 5.6, 7.8)
                        )));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new Point2D[] {
                        new Point2D.Double(),
                        new Point2D.Double(5.6, 7.8)
                }})
                .setOutput(BenchmarkUtils.makeList(
                        new Line2D.Double(0, 0, 5.6, 7.8)
                        )));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new Point2D[] {
                        new Point2D.Double(1.2, 3.4),
                        new Point2D.Double()
                }})
                .setOutput(BenchmarkUtils.makeList(
                        new Line2D.Double(1.2, 3.4, 0, 0)
                        )));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new Point2D[] {
                        new Point2D.Double(),
                        new Point2D.Double()
                }})
                .setOutput(BenchmarkUtils.makeList(
                        new Line2D.Double()
                        )));

        return task;
    }

    static List<Line2D.Double> solution(Point2D[] points) {
        List<Line2D.Double> ans = new ArrayList<>();
        for (int i = 0; i < points.length - 1; i++)
            ans.add(new Line2D.Double(points[i], points[i + 1]));
        return ans;
    }
}
