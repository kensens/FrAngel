package frangel.benchmarks.controlstructures;

import java.awt.Point;
import java.util.HashSet;
import java.util.List;

import frangel.Example;
import frangel.SynthesisTask;
import frangel.Tag;
import frangel.benchmarks.BenchmarkGroup;
import frangel.benchmarks.BenchmarkUtils;
import frangel.benchmarks.TaskCreator;

public enum ExtractCoords implements TaskCreator {
    INSTANCE;
    static {
        BenchmarkGroup.CONTROL_STRUCTURES.register(INSTANCE);
    }

    @Override
    public SynthesisTask createTask() {
        SynthesisTask task = new SynthesisTask()
                .setName("extractCoords")
                .setInputTypes(List.class)
                .addGenerics(List.class, Point.class)
                .setOutputType(HashSet.class)
                .addGenerics(HashSet.class, Integer.class)
                .setInputNames("points")
                .makeInputsImmutable()
                .addTags(Tag.FOREACH);

        task.addExample(new Example()
                .setInputs(() -> new Object[] { BenchmarkUtils.makeList(new Point(123, 234), new Point(-12, -23), new Point(), new Point(-12, 99)) })
                .setOutput(BenchmarkUtils.makeSet(123, 234, -12, -23, 0, 99)));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { BenchmarkUtils.makeList(new Point(123, 234)) })
                .setOutput(BenchmarkUtils.makeSet(123, 234)));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { BenchmarkUtils.makeList(new Point(123, 0)) })
                .setOutput(BenchmarkUtils.makeSet(0, 123)));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { BenchmarkUtils.makeList(new Point(0, 234)) })
                .setOutput(BenchmarkUtils.makeSet(0, 234)));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { BenchmarkUtils.makeList(new Point()) })
                .setOutput(BenchmarkUtils.makeSet(0)));

        return task;
    }

    static HashSet<Integer> solution(List<Point> points) {
        HashSet<Integer> coords = new HashSet<>();
        for (Point p : points) {
            coords.add(p.x);
            coords.add(p.y);
        }
        return coords;
    }
}
