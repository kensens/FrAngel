package frangel.benchmarks.github;

import frangel.Example;
import frangel.SynthesisTask;
import frangel.Tag;
import frangel.benchmarks.BenchmarkGroup;
import frangel.benchmarks.TaskCreator;

public enum ZXingParseName implements TaskCreator {
    INSTANCE;
    static {
        BenchmarkGroup.GITHUB.register(INSTANCE);
    }

    @Override
    public SynthesisTask createTask() {
        SynthesisTask task = new SynthesisTask()
                .setName("zxing_parseName")
                .setInputTypes(String.class)
                .setInputNames("name")
                .setOutputType(String.class)
                .addLiterals(char.class, ',', ' ')
                .addTags(Tag.IF);

        // no unit tests

        // added examples
        task.addExample(new Example()
                .setInputs(() -> new Object[] { "Last,First" })
                .setOutput("First Last"));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { "ab,cde,f" })
                .setOutput("cde,f ab"));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { "abcd," })
                .setOutput(" abcd"));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { ",abc,d" })
                .setOutput("abc,d "));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { "abcd" })
                .setOutput("abcd"));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { "" })
                .setOutput(""));

        return task;
    }

    // from zxing-master/core/src/main/java/com/google/zxing/client/result/AddressBookDoCoMoResultParser.java
    static String solution(String name) {
        int comma = name.indexOf(',');
        if (comma >= 0) {
            return name.substring(comma + 1) + ' ' + name.substring(0, comma);
        }
        return name;
    }
}
