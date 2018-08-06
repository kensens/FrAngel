package frangel.benchmarks.github;

import frangel.Example;
import frangel.SynthesisTask;
import frangel.Tag;
import frangel.benchmarks.BenchmarkGroup;
import frangel.benchmarks.TaskCreator;

public enum ZXingGetMax implements TaskCreator {
    INSTANCE;
    static {
        BenchmarkGroup.GITHUB.register(INSTANCE);
    }

    @Override
    public SynthesisTask createTask() {
        SynthesisTask task = new SynthesisTask()
                .setName("zxing_getMax")
                .setInputTypes(int[].class)
                .setInputNames("values")
                .setOutputType(int.class)
                .addClasses(Math.class)
                .addTags(Tag.FOREACH);

        // no unit tests

        // added examples
        task.addExample(new Example()
                .setInputs(() -> new Object[] { new int[] {12, 34, 32, 99, 98, 23} })
                .setOutput(99));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new int[] {12, 34, 32, 97, 98, 23} })
                .setOutput(98));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new int[] {123} })
                .setOutput(123));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new int[0] })
                .setOutput(-1));

        return task;
    }

    // from zxing-master/core/src/main/java/com/google/zxing/pdf417/decoder/PDF417ScanningDecoder.java
    static int solution(int[] values) {
        int maxValue = -1;
        for (int value : values) {
            maxValue = Math.max(maxValue, value);
        }
        return maxValue;
    }
}
