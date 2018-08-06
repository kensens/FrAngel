package frangel.benchmarks.controlstructures;

import java.util.List;

import frangel.Example;
import frangel.SynthesisTask;
import frangel.Tag;
import frangel.benchmarks.BenchmarkGroup;
import frangel.benchmarks.BenchmarkUtils;
import frangel.benchmarks.TaskCreator;

public enum FillFibonacci implements TaskCreator {
    INSTANCE;
    static {
        BenchmarkGroup.CONTROL_STRUCTURES.register(INSTANCE);
    }

    @Override
    public SynthesisTask createTask() {
        SynthesisTask task = new SynthesisTask()
                .setName("fillFibonacci")
                .setInputTypes(List.class, int.class)
                .addGenerics(List.class, Double.class)
                .setInputNames("list", "num")
                .setOutputType(void.class)
                .addTags(Tag.FOR);

        task.addExample(new Example()
                .setInputs(() -> new Object[] { BenchmarkUtils.makeList(1.5, -0.6), 5 })
                .setModifiedInput(1, BenchmarkUtils.makeList(1.5, -0.6, 0.9, 0.3, 1.2, 1.5, 2.7) ));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { BenchmarkUtils.makeList(1.5, -0.6), 1 })
                .setModifiedInput(1, BenchmarkUtils.makeList(1.5, -0.6, 0.9) ));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { BenchmarkUtils.makeList(12.3, 1.0), 1 })
                .setModifiedInput(1, BenchmarkUtils.makeList(12.3, 1.0, 13.3) ));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { BenchmarkUtils.makeList(1.0, 12.3), 1 })
                .setModifiedInput(1, BenchmarkUtils.makeList(1.0, 12.3, 13.3) ));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { BenchmarkUtils.makeList(0.0, -0.6), 1 })
                .setModifiedInput(1, BenchmarkUtils.makeList(0.0, -0.6, -0.6) ));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { BenchmarkUtils.makeList(1.5, 0.0), 1 })
                .setModifiedInput(1, BenchmarkUtils.makeList(1.5, 0.0, 1.5) ));

        return task;
    }

    static void solution(List<Double> list, int num) {
        for (int i = 0; i < num; i++)
            list.add(list.get(i) + list.get(i + 1));
    }
}
