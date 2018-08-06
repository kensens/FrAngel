package frangel.benchmarks.controlstructures;

import java.util.List;

import frangel.Example;
import frangel.SynthesisTask;
import frangel.Tag;
import frangel.benchmarks.BenchmarkGroup;
import frangel.benchmarks.BenchmarkUtils;
import frangel.benchmarks.TaskCreator;

public enum ListReplaceNull implements TaskCreator {
    INSTANCE;
    static {
        BenchmarkGroup.CONTROL_STRUCTURES.register(INSTANCE);
    }

    @Override
    public SynthesisTask createTask() {
        SynthesisTask task = new SynthesisTask()
                .setName("listReplaceNull")
                .setInputTypes(List.class, String.class)
                .addGenerics(List.class, String.class)
                .setInputNames("list", "nullStr")
                .setOutputType(void.class)
                .addTags(Tag.FOR, Tag.IF);

        task.addExample(new Example()
                .setInputs(() -> new Object[] { BenchmarkUtils.makeList(null, "", null, "null", null), "<NULL>" })
                .setModifiedInput(1, BenchmarkUtils.makeList("<NULL>", "", "<NULL>", "null", "<NULL>")));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { BenchmarkUtils.makeList(null, null), "<NULL>" })
                .setModifiedInput(1, BenchmarkUtils.makeList("<NULL>", "<NULL>")));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { BenchmarkUtils.makeList(null, ""), "nul" })
                .setModifiedInput(1, BenchmarkUtils.makeList("nul", "")));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { BenchmarkUtils.makeList("abc", null), "null" })
                .setModifiedInput(1, BenchmarkUtils.makeList("abc", "null")));

        return task;
    }

    static void solution(List<String> list, String nullStr) {
        for (int i = 0; i < list.size(); i++)
            if (list.get(i) == null)
                list.set(i, nullStr);
    }
}
