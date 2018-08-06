package frangel.benchmarks.controlstructures;

import java.util.List;

import frangel.Example;
import frangel.SynthesisTask;
import frangel.Tag;
import frangel.benchmarks.BenchmarkGroup;
import frangel.benchmarks.BenchmarkUtils;
import frangel.benchmarks.TaskCreator;

public enum DelimitedStringConcat implements TaskCreator {
    INSTANCE;
    static {
        BenchmarkGroup.CONTROL_STRUCTURES.register(INSTANCE);
    }

    @Override
    public SynthesisTask createTask() {
        SynthesisTask task = new SynthesisTask()
                .setName("delimitedStringConcat")
                .setInputTypes(List.class, String.class, StringBuilder.class)
                .addGenerics(List.class, CharSequence.class)
                .setInputNames("strs", "delim", "result")
                .setOutputType(void.class)
                .excludeMethods(BenchmarkUtils.getMethod(String.class, "join", CharSequence.class, Iterable.class)) // too easy otherwise
                .makeInputsImmutable()
                .addTags(Tag.FOR, Tag.IF);

        task.addExample(new Example()
                .setInputs(() -> new Object[] { BenchmarkUtils.makeList("abc", "DEF", "12345", "?!"), ", ", new StringBuilder("init.") })
                .setModifiedInputChecker(3, (StringBuilder s) -> s != null && s.toString().equals("init.abc, DEF, 12345, ?!")));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { BenchmarkUtils.makeList("abc", "DEF"), ", ", new StringBuilder("abc") })
                .setModifiedInputChecker(3, (StringBuilder s) -> s != null && s.toString().equals("abcabc, DEF")));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { BenchmarkUtils.makeList("abc"), " ", new StringBuilder("xyz ") })
                .setModifiedInputChecker(3, (StringBuilder s) -> s != null && s.toString().equals("xyz abc")));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { BenchmarkUtils.makeList("", ""), ", ", new StringBuilder() })
                .setModifiedInputChecker(3, (StringBuilder s) -> s != null && s.toString().equals(", ")));

        return task;
    }

    static void solution(List<CharSequence> strs, String delim, StringBuilder result) {
        for (int i = 0; i < strs.size(); i++) {
            if (i > 0)
                result.append(delim);
            result.append(strs.get(i));
        }
    }
}
