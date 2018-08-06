package frangel.benchmarks.geometry;

import java.awt.Point;
import java.awt.Polygon;
import java.util.ArrayList;

import frangel.Example;
import frangel.SynthesisTask;
import frangel.Tag;
import frangel.benchmarks.BenchmarkGroup;
import frangel.benchmarks.BenchmarkUtils;
import frangel.benchmarks.TaskCreator;

public enum PolygonToPoints implements TaskCreator {
    INSTANCE;
    static {
        BenchmarkGroup.GEOMETRY.register(INSTANCE);
    }

    @Override
    public SynthesisTask createTask() {
        SynthesisTask task = new SynthesisTask()
                .setName("polygonToPoints")
                .setInputTypes(Polygon.class)
                .setInputNames("poly")
                .setOutputType(ArrayList.class)
                .addGenerics(ArrayList.class, Point.class)
                .makeInputsImmutable()
                .addEqualityTester(Polygon.class, BenchmarkUtils::equalsPolygon)
                .addTags(Tag.FOR);

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new Polygon() })
                .setOutput(new ArrayList<Point>()));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new Polygon(new int[] {0}, new int[] {0}, 1) })
                .setOutput(BenchmarkUtils.makeList(new Point())));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new Polygon(new int[] {1}, new int[] {0}, 1) })
                .setOutput(BenchmarkUtils.makeList(new Point(1, 0))));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new Polygon(new int[] {12}, new int[] {0}, 1) })
                .setOutput(BenchmarkUtils.makeList(new Point(12, 0))));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new Polygon(new int[] {0}, new int[] {34}, 1) })
                .setOutput(BenchmarkUtils.makeList(new Point(0, 34))));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new Polygon(new int[] {12}, new int[] {34}, 1) })
                .setOutput(BenchmarkUtils.makeList(new Point(12, 34))));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new Polygon(new int[] {12, -1}, new int[] {34, -11}, 2) })
                .setOutput(BenchmarkUtils.makeList(
                        new Point(12, 34),
                        new Point(-1, -11)
                        )));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new Polygon(new int[] {12, 0, -1, 99}, new int[] {34, 0, -11, 88}, 4) })
                .setOutput(BenchmarkUtils.makeList(
                        new Point(12, 34),
                        new Point(),
                        new Point(-1, -11),
                        new Point(99, 88)
                        )));

        return task;
    }

    static ArrayList<Point> solution(Polygon poly) {
        ArrayList<Point> points = new ArrayList<>();
        for (int i = 0; i < poly.npoints; i++)
            points.add(new Point(poly.xpoints[i], poly.ypoints[i]));
        return points;
    }
}
