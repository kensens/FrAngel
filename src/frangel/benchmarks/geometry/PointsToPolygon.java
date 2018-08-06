package frangel.benchmarks.geometry;

import java.awt.Point;
import java.awt.Polygon;

import frangel.Example;
import frangel.SynthesisTask;
import frangel.Tag;
import frangel.benchmarks.BenchmarkGroup;
import frangel.benchmarks.BenchmarkUtils;
import frangel.benchmarks.TaskCreator;

public enum PointsToPolygon implements TaskCreator {
    INSTANCE;
    static {
        BenchmarkGroup.GEOMETRY.register(INSTANCE);
    }

    @Override
    public SynthesisTask createTask() {
        SynthesisTask task = new SynthesisTask()
                .setName("pointsToPolygon")
                .setInputTypes(Point[].class)
                .setInputNames("points")
                .setOutputType(Polygon.class)
                .makeInputsImmutable()
                .addEqualityTester(Polygon.class, BenchmarkUtils::equalsPolygon)
                .addTags(Tag.FOREACH);

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new Point[] {
                        new Point(12, 34),
                        new Point(),
                        new Point(-1, -11),
                        new Point(99, 88)
                } })
                .setOutput(new Polygon(new int[] {12, 0, -1, 99}, new int[] {34, 0, -11, 88}, 4)));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new Point[] {new Point(12, 0)} })
                .setOutput(new Polygon(new int[] {12}, new int[] {0}, 1)));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new Point[] {new Point(0, 34)} })
                .setOutput(new Polygon(new int[] {0}, new int[] {34}, 1)));

        return task;
    }

    static Polygon solution(Point[] points) {
        Polygon poly = new Polygon();
        for (Point p : points)
            poly.addPoint(p.x, p.y);
        return poly;
    }
}
