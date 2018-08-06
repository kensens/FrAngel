package frangel.benchmarks.github;

import frangel.Example;
import frangel.SynthesisTask;
import frangel.Tag;
import frangel.benchmarks.BenchmarkGroup;
import frangel.benchmarks.TaskCreator;

public enum ZXingTrim implements TaskCreator {
    INSTANCE;
    static {
        BenchmarkGroup.GITHUB.register(INSTANCE);
    }

    @Override
    public SynthesisTask createTask() {
        SynthesisTask task = new SynthesisTask()
                .setName("zxing_trim")
                .setInputTypes(String.class)
                .setInputNames("s")
                .setOutputType(String.class)
                .addTags(Tag.IF);

        // no unit tests

        // added examples
        task.addExample(new Example()
                .setInputs(() -> new Object[] { "  \tabc \tdef\n \t  " })
                .setOutput("abc \tdef"));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { "  \t \t\n \t  " })
                .setOutput(null));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { null })
                .setOutput(null));

        return task;
    }

    // from zxing-master/android/src/com/google/zxing/client/android/encode/ContactEncoder.java
    static String solution(String s) {
        if (s == null) {
            return null;
        }
        String result = s.trim();
        return result.isEmpty() ? null : result;
    }
}
