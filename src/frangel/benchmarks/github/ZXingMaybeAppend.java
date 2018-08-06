package frangel.benchmarks.github;

import com.google.zxing.client.result.ParsedResult;

import frangel.Example;
import frangel.SynthesisTask;
import frangel.Tag;
import frangel.benchmarks.BenchmarkGroup;
import frangel.benchmarks.BenchmarkUtils;
import frangel.benchmarks.TaskCreator;

public enum ZXingMaybeAppend implements TaskCreator {
    INSTANCE;
    static {
        BenchmarkGroup.GITHUB.register(INSTANCE);
    }

    @Override
    public SynthesisTask createTask() {
        SynthesisTask task = new SynthesisTask()
                .setName("zxing_maybeAppend")
                .setInputTypes(String[].class, StringBuilder.class)
                .setInputNames("values", "result")
                .setOutputType(void.class)
                .addClasses(ParsedResult.class)
                .excludeMethods(BenchmarkUtils.getMethod(ParsedResult.class, "maybeAppend", String[].class, StringBuilder.class)) // to synthesize
                .addEqualityTester(StringBuilder.class, (StringBuilder s1, StringBuilder s2) -> s1.toString().equals(s2.toString()))
                .addTags(Tag.IF, Tag.FOREACH);

        // no unit tests

        // added examples
        task.addExample(new Example()
                .setInputs(() -> new Object[] { new String[] {"a", "bc", "123"}, new StringBuilder("xyz") })
                .setModifiedInput(2, new StringBuilder("xyz\na\nbc\n123")));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new String[] {"a", "bc", "123"}, new StringBuilder() })
                .setModifiedInput(2, new StringBuilder("a\nbc\n123")));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { null, new StringBuilder("xyz") })
                .setModifiedInput(2, new StringBuilder("xyz")));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { null, new StringBuilder() })
                .setModifiedInput(2, new StringBuilder()));

        return task;
    }

    // from zxing-master/core/src/main/java/com/google/zxing/client/result/ParsedResult.java
    static void solution(String[] values, StringBuilder result) {
        if (values != null) {
            for (String value : values) {
                ParsedResult.maybeAppend(value, result);
            }
        }
    }
}
