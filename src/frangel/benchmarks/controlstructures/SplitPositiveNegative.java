package frangel.benchmarks.controlstructures;

import java.util.List;
import java.util.Set;

import frangel.Example;
import frangel.SynthesisTask;
import frangel.Tag;
import frangel.benchmarks.BenchmarkGroup;
import frangel.benchmarks.BenchmarkUtils;
import frangel.benchmarks.TaskCreator;

public enum SplitPositiveNegative implements TaskCreator {
    INSTANCE;
    static {
        BenchmarkGroup.CONTROL_STRUCTURES.register(INSTANCE);
    }

    @Override
    public SynthesisTask createTask() {
        SynthesisTask task = new SynthesisTask()
                .setName("splitPositiveNegative")
                .setInputTypes(List.class, Set.class, Set.class)
                .addGenerics(List.class, Double.class)
                .addGenerics(Set.class, Double.class)
                .setInputNames("list", "positive", "negative")
                .setOutputType(void.class)
                .makeInputsImmutable()
                .addTags(Tag.FOREACH, Tag.IF);

        task.addExample(new Example()
                .setInputs(() -> new Object[] {
                        BenchmarkUtils.makeList(0.1, -3.3, -0.1, 0.0, 3.3, -0.2, 0.0, 0.2),
                        BenchmarkUtils.makeSet(6.7, 8.9),
                        BenchmarkUtils.makeSet(-6.7, -8.9)
                })
                .setModifiedInput(2, BenchmarkUtils.makeSet(0.1, 0.2, 3.3, 6.7, 8.9))
                .setModifiedInput(3, BenchmarkUtils.makeSet(-0.1, -0.2, -3.3, -6.7, -8.9)));

        task.addExample(new Example()
                .setInputs(() -> new Object[] {
                        BenchmarkUtils.makeList(-0.1, 0.1),
                        BenchmarkUtils.makeSet(6.7, 8.9),
                        BenchmarkUtils.makeSet(-6.7, -8.9)
                })
                .setModifiedInput(2, BenchmarkUtils.makeSet(0.1, 6.7, 8.9))
                .setModifiedInput(3, BenchmarkUtils.makeSet(-0.1, -6.7, -8.9)));

        task.addExample(new Example()
                .setInputs(() -> new Object[] {
                        BenchmarkUtils.makeList(0.1, -0.1),
                        BenchmarkUtils.makeSet(6.7, 8.9),
                        BenchmarkUtils.makeSet(-6.7, -8.9)
                })
                .setModifiedInput(2, BenchmarkUtils.makeSet(0.1, 6.7, 8.9))
                .setModifiedInput(3, BenchmarkUtils.makeSet(-0.1, -6.7, -8.9)));

        task.addExample(new Example()
                .setInputs(() -> new Object[] {
                        BenchmarkUtils.makeList(0.1),
                        BenchmarkUtils.makeSet(6.7, 8.9),
                        BenchmarkUtils.makeSet(-6.7, -8.9)
                })
                .setModifiedInput(2, BenchmarkUtils.makeSet(0.1, 6.7, 8.9))
                .setModifiedInput(3, BenchmarkUtils.makeSet(-6.7, -8.9)));

        task.addExample(new Example()
                .setInputs(() -> new Object[] {
                        BenchmarkUtils.makeList(-0.1),
                        BenchmarkUtils.makeSet(6.7, 8.9),
                        BenchmarkUtils.makeSet(-6.7, -8.9)
                })
                .setModifiedInput(2, BenchmarkUtils.makeSet(6.7, 8.9))
                .setModifiedInput(3, BenchmarkUtils.makeSet(-0.1, -6.7, -8.9)));

        return task;
    }

    static void solution(List<Double> list, Set<Double> positive, Set<Double> negative) {
        for (double d : list) {
            if (d > 0.0)
                positive.add(d);
            if (d < 0.0)
                negative.add(d);
        }
    }
}
