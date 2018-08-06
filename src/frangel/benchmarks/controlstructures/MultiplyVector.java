package frangel.benchmarks.controlstructures;

import java.util.List;

import frangel.Example;
import frangel.SynthesisTask;
import frangel.Tag;
import frangel.benchmarks.BenchmarkGroup;
import frangel.benchmarks.BenchmarkUtils;
import frangel.benchmarks.TaskCreator;

public enum MultiplyVector implements TaskCreator {
    INSTANCE;
    static {
        BenchmarkGroup.CONTROL_STRUCTURES.register(INSTANCE);
    }

    @Override
    public SynthesisTask createTask() {
        SynthesisTask task = new SynthesisTask()
                .setName("multiplyVector")
                .setInputTypes(List.class, Double.class)
                .addGenerics(List.class, Double.class)
                .setInputNames("vector", "factor")
                .setOutputType(void.class)
                .addTags(Tag.FOR);

        task.addExample(new Example()
                .setInputs(() -> new Object[] { BenchmarkUtils.makeList(1.2, 3.4, 17.0, 0.0, -1.0, 0.0, 100.0), 5.0 })
                .setModifiedInput(1, BenchmarkUtils.makeList(6.0, 17.0, 85.0, 0.0, -5.0, 0.0, 500.0)));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { BenchmarkUtils.makeList(1.2, 1.2), 1.1 })
                .setModifiedInput(1, BenchmarkUtils.makeList(1.32, 1.32)));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { BenchmarkUtils.makeList(1.2), 2.0 })
                .setModifiedInput(1, BenchmarkUtils.makeList(2.4)));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { BenchmarkUtils.makeList(1.2), 0.0 })
                .setModifiedInput(1, BenchmarkUtils.makeList(0.0)));

        return task;
    }

    static void solution(List<Double> vector, Double factor) {
        for (int i = 0; i < vector.size(); i++)
            vector.set(i, vector.get(i) * factor);
    }
}
