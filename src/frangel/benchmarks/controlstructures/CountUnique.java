package frangel.benchmarks.controlstructures;

import java.util.HashSet;

import frangel.Example;
import frangel.SynthesisTask;
import frangel.Tag;
import frangel.benchmarks.BenchmarkGroup;
import frangel.benchmarks.TaskCreator;

public enum CountUnique implements TaskCreator {
    INSTANCE;
    static {
        BenchmarkGroup.CONTROL_STRUCTURES.register(INSTANCE);
    }

    @Override
    public SynthesisTask createTask() {
        SynthesisTask task = new SynthesisTask()
                .setName("countUnique")
                .setInputTypes(String[].class)
                .setInputNames("arr")
                .setOutputType(int.class)
                .addClasses(HashSet.class)
                .addGenerics(HashSet.class, String.class)
                .makeInputsImmutable()
                .addTags(Tag.FOREACH);

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new String[] {"a", "d", "a", "!@#", "c", "a", "?!", "A", " ", "b", "c", "123"} })
                .setOutput(9));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new String[] {"a", "d", "a", "!@#", "c", "a", "?!", "a", " ", "b", "c", "123"} })
                .setOutput(8));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new String[] {"a", "d", "a", "!@#", "c", "a", "?!", "a", " ", "c", "c", "123"} })
                .setOutput(7));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new String[] {"", null, " ", "null", "NULL", "  "} })
                .setOutput(6));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new String[] {"", null, " ", "null", "null", "  "} })
                .setOutput(5));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new String[] {"", null, " ", "null", null, " "} })
                .setOutput(4));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new String[] {} })
                .setOutput(0));

        return task;
    }

    static int solution(String[] arr) {
        HashSet<String> set = new HashSet<>();
        for (String s : arr)
            set.add(s);
        return set.size();
    }
}
