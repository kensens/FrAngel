package frangel.benchmarks.controlstructures;

import java.util.ArrayList;
import java.util.List;

import frangel.Example;
import frangel.SynthesisTask;
import frangel.Tag;
import frangel.benchmarks.BenchmarkGroup;
import frangel.benchmarks.BenchmarkUtils;
import frangel.benchmarks.TaskCreator;

public enum AddArrayToListOrNull implements TaskCreator {
    INSTANCE;
    static {
        BenchmarkGroup.CONTROL_STRUCTURES.register(INSTANCE);
    }

    @Override
    public SynthesisTask createTask() {
        SynthesisTask task = new SynthesisTask()
                .setName("addArrayToListOrNull")
                .setInputTypes(Object[].class, List.class)
                .setInputNames("arr", "list")
                .setOutputType(List.class)
                .addClasses(ArrayList.class)
                .addTags(Tag.IF, Tag.FOREACH);

        task.addExample(new Example()
                .setInputs(() -> new Object[] {
                        new Object[] {"abc", null, 123, false, ""},
                        BenchmarkUtils.makeList(null, "")
                })
                .setOutput(BenchmarkUtils.makeList(null, "", "abc", null, 123, false, "")));

        task.addExample(new Example()
                .setInputs(() -> new Object[] {
                        new Object[] {"abc", null, 123, false, ""},
                        null
                })
                .setOutput(BenchmarkUtils.makeList("abc", null, 123, false, "")));

        task.addExample(new Example()
                .setInputs(() -> new Object[] {
                        new Object[] {"abc"},
                        BenchmarkUtils.makeList(null, "")
                })
                .setOutput(BenchmarkUtils.makeList(null, "", "abc")));

        task.addExample(new Example()
                .setInputs(() -> new Object[] {
                        new Object[] {"abc"},
                        null
                })
                .setOutput(BenchmarkUtils.makeList("abc")));

        return task;
    }

    static List<Object> solution(Object[] arr, List<Object> list) {
        if (list == null)
            list = new ArrayList<>();
        for (Object o : arr)
            list.add(o);
        return list;
    }
}
