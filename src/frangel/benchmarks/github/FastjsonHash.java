package frangel.benchmarks.github;

import frangel.Example;
import frangel.SynthesisTask;
import frangel.Tag;
import frangel.benchmarks.BenchmarkGroup;
import frangel.benchmarks.TaskCreator;

public enum FastjsonHash implements TaskCreator {
    INSTANCE;
    static {
        BenchmarkGroup.GITHUB.register(INSTANCE);
    }

    @Override
    public SynthesisTask createTask() {
        SynthesisTask task = new SynthesisTask()
                .setName("fastjson_hash")
                .setInputTypes(char[].class, int.class, int.class)
                .setInputNames("buffer", "offset", "len")
                .setOutputType(int.class)
                .addClasses(CharToInt.class)
                .addLiterals(int.class, 31)
                .addTags(Tag.FOR);

        // no unit tests

        // added examples
        task.addExample(new Example()
                .setInputs(() -> new Object[] { "Hello, World!".toCharArray(), 7, 4 })
                .setOutput(31*31*31*'W' + 31*31*'o' + 31*'r' + 'l'));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { "Hello, World!".toCharArray(), 11, 2 })
                .setOutput(31*'d' + '!'));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { "Hello, World\0".toCharArray(), 11, 2 })
                .setOutput(31*'d'));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { "Hello, World!".toCharArray(), 9, 1 })
                .setOutput((int) 'r'));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new char[] {31, 0, 0, 0, 0, 0, 0}, 0, 7 })
                .setOutput(31*31*31*31*31*31*31));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new char[] {1}, 0, 1 })
                .setOutput(1));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { "XYZ".toCharArray(), 0, 3 })
                .setOutput(31*31*'X' + 31*'Y' + 'Z'));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { "XY".toCharArray(), 0, 2 })
                .setOutput(31*'X' + 'Y'));

        return task;
    }

    // from fastjson-master/src/main/java/com/alibaba/fastjson/parser/SymbolTable.java
    static int solution(char[] buffer, int offset, int len) {
        int h = 0;
        int off = offset;
        for (int i = 0; i < len; i++) {
            h = 31 * h + buffer[off++];
        }
        return h;
    }

    static class CharToInt {
        public static int cast(char c) {
            return c;
        }
    }
}
