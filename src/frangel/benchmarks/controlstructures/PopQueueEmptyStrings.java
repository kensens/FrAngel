package frangel.benchmarks.controlstructures;

import java.util.Queue;

import frangel.Example;
import frangel.SynthesisTask;
import frangel.Tag;
import frangel.benchmarks.BenchmarkGroup;
import frangel.benchmarks.BenchmarkUtils;
import frangel.benchmarks.TaskCreator;

public enum PopQueueEmptyStrings implements TaskCreator {
    INSTANCE;
    static {
        BenchmarkGroup.CONTROL_STRUCTURES.register(INSTANCE);
    }

    @Override
    public SynthesisTask createTask() {
        SynthesisTask task = new SynthesisTask()
                .setName("popQueueEmptyStrings")
                .setInputTypes(Queue.class)
                .addGenerics(Queue.class, String.class)
                .setInputNames("queue")
                .setOutputType(int.class)
                .addTags(Tag.FOR);

        task.addExample(new Example()
                .setInputs(() -> new Object[] { BenchmarkUtils.makeQueue("", "", "", "", "not", "", "empty", "") })
                .setModifiedInput(1, BenchmarkUtils.makeQueue("not", "", "empty", ""))
                .setOutput(4));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { BenchmarkUtils.makeQueue("", "", "", " ", "", " ") })
                .setModifiedInput(1, BenchmarkUtils.makeQueue(" ", "", " "))
                .setOutput(3));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { BenchmarkUtils.makeQueue("", "", ".", "", ".", "") })
                .setModifiedInput(1, BenchmarkUtils.makeQueue(".", "", ".", ""))
                .setOutput(2));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { BenchmarkUtils.makeQueue("", "", "", "", "") })
                .setModifiedInput(1, BenchmarkUtils.makeQueue())
                .setOutput(5));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { BenchmarkUtils.makeQueue("") })
                .setModifiedInput(1, BenchmarkUtils.makeQueue())
                .setOutput(1));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { BenchmarkUtils.makeQueue(" ", "", "?", "") })
                .setModifiedInput(1, BenchmarkUtils.makeQueue(" ", "", "?", ""))
                .setOutput(0));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { BenchmarkUtils.makeQueue() })
                .setModifiedInput(1, BenchmarkUtils.makeQueue())
                .setOutput(0));

        return task;
    }

    static int solution(Queue<String> queue) {
        int i = 0;
        for ( ; !queue.isEmpty() && queue.peek().isEmpty(); i++)
            queue.poll();
        return i;
    }
}
