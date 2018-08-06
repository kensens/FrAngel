package frangel.benchmarks.controlstructures;

import java.util.List;

import frangel.Example;
import frangel.SynthesisTask;
import frangel.Tag;
import frangel.benchmarks.BenchmarkGroup;
import frangel.benchmarks.BenchmarkUtils;
import frangel.benchmarks.TaskCreator;

public enum MaxDouble implements TaskCreator {
    INSTANCE;
    static {
        BenchmarkGroup.CONTROL_STRUCTURES.register(INSTANCE);
    }

    @Override
    public SynthesisTask createTask() {
        SynthesisTask task = new SynthesisTask()
                .setName("maxDouble")
                .setInputTypes(List.class)
                .addGenerics(List.class, Double.class)
                .setInputNames("list")
                .setOutputType(double.class)
                .makeInputsImmutable()
                .addTags(Tag.FOREACH, Tag.IF);

        task.addExample(new Example()
                .setInputs(() -> new Object[] { BenchmarkUtils.makeList(-9.9, 8.8, -3.2, 0.3, 1.0, 99.9, 99.91, 0.0, 9.9) })
                .setOutput(99.91));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { BenchmarkUtils.makeList(-9.9, 8.8, -3.2, 0.3, 1.0, 99.91, 99.9, 0.0, 9.9) })
                .setOutput(99.91));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { BenchmarkUtils.makeList(-0.8, -0.9, -0.7) })
                .setOutput(-0.7));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { BenchmarkUtils.makeList(-9.8, -9.7, -9.9) })
                .setOutput(-9.7));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { BenchmarkUtils.makeList(-9.8) })
                .setOutput(-9.8));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { BenchmarkUtils.makeList() })
                .setOutput(Double.NEGATIVE_INFINITY));

        return task;
    }

    static double solution(List<Double> list) {
        double max = Double.NEGATIVE_INFINITY;
        for (double d : list)
            if (d > max)
                max = d;
        return max;
    }
}
