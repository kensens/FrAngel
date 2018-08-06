package frangel.benchmarks.controlstructures;

import java.util.Deque;

import frangel.Example;
import frangel.SynthesisTask;
import frangel.Tag;
import frangel.benchmarks.BenchmarkGroup;
import frangel.benchmarks.BenchmarkUtils;
import frangel.benchmarks.TaskCreator;

public enum SetDequeSize implements TaskCreator {
    INSTANCE;
    static {
        BenchmarkGroup.CONTROL_STRUCTURES.register(INSTANCE);
    }

    @Override
    public SynthesisTask createTask() {
        SynthesisTask task = new SynthesisTask()
                .setName("setDequeSize")
                .setInputTypes(Deque.class, int.class, Object.class)
                .setInputNames("deque", "size", "element")
                .setOutputType(void.class)
                .addTags(Tag.WHILE);

        task.addExample(new Example()
                .setInputs(() -> new Object[] { BenchmarkUtils.makeQueue("a", "b", "c", "d", "e"), 9, "xyz"})
                .setModifiedInput(1, BenchmarkUtils.makeQueue("a", "b", "c", "d", "e", "xyz", "xyz", "xyz", "xyz")));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { BenchmarkUtils.makeQueue("a", "b", "c", "d", "e"), 6, "xyz"})
                .setModifiedInput(1, BenchmarkUtils.makeQueue("a", "b", "c", "d", "e", "xyz")));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { BenchmarkUtils.makeQueue("a", "b", "c", "d", "e"), 4, "xyz"})
                .setModifiedInput(1, BenchmarkUtils.makeQueue("a", "b", "c", "d")));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { BenchmarkUtils.makeQueue("a", "b", "c", "d", "e"), 1, "xyz"})
                .setModifiedInput(1, BenchmarkUtils.makeQueue("a")));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { BenchmarkUtils.makeQueue(), 3, "a"})
                .setModifiedInput(1, BenchmarkUtils.makeQueue("a", "a", "a")));

        return task;
    }

    static void solution(Deque<Object> deque, int size, Object element) {
        while (deque.size() < size)
            deque.addLast(element);
        while (deque.size() > size)
            deque.removeLast();
    }
}
