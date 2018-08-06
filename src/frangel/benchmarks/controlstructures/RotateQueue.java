package frangel.benchmarks.controlstructures;

import java.util.Queue;

import frangel.Example;
import frangel.SynthesisTask;
import frangel.Tag;
import frangel.benchmarks.BenchmarkGroup;
import frangel.benchmarks.BenchmarkUtils;
import frangel.benchmarks.TaskCreator;

public enum RotateQueue implements TaskCreator {
    INSTANCE;
    static {
        BenchmarkGroup.CONTROL_STRUCTURES.register(INSTANCE);
    }

    @Override
    public SynthesisTask createTask() {
        SynthesisTask task = new SynthesisTask()
                .setName("rotateQueue")
                .setInputTypes(Queue.class, int.class)
                .setInputNames("queue", "amount")
                .setOutputType(void.class)
                .addTags(Tag.IF, Tag.FOR);

        task.addExample(new Example()
                .setInputs(() -> new Object[] { BenchmarkUtils.makeQueue("a", "b", "c", "d", "e"), 1})
                .setModifiedInput(1, BenchmarkUtils.makeQueue("b", "c", "d", "e", "a")));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { BenchmarkUtils.makeQueue("b", "c", "d", "e", "a"), 4})
                .setModifiedInput(1, BenchmarkUtils.makeQueue("a", "b", "c", "d", "e")));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { BenchmarkUtils.makeQueue("a", "b", "c", "d", "e"), 7})
                .setModifiedInput(1, BenchmarkUtils.makeQueue("c", "d", "e", "a", "b")));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { BenchmarkUtils.makeQueue("b", "c", "d", "e", "a"), 0})
                .setModifiedInput(1, BenchmarkUtils.makeQueue("b", "c", "d", "e", "a")));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { BenchmarkUtils.makeQueue(), 4})
                .setModifiedInput(1, BenchmarkUtils.makeQueue()));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { BenchmarkUtils.makeQueue("b", "c", "d", "e", "a"), 10003})
                .setModifiedInput(1, BenchmarkUtils.makeQueue("e", "a", "b", "c", "d")));

        return task;
    }

    static void solution(Queue<String> queue, int amount) {
        if (!queue.isEmpty())
            for (int i = 0; i < amount % queue.size(); i++)
                queue.add(queue.remove());
    }
}
