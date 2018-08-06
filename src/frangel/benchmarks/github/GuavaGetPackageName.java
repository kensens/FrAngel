package frangel.benchmarks.github;

import frangel.Example;
import frangel.SynthesisTask;
import frangel.Tag;
import frangel.benchmarks.BenchmarkGroup;
import frangel.benchmarks.TaskCreator;

public enum GuavaGetPackageName implements TaskCreator {
    INSTANCE;
    static {
        BenchmarkGroup.GITHUB.register(INSTANCE);
    }

    @Override
    public SynthesisTask createTask() {
        SynthesisTask task = new SynthesisTask()
                .setName("guava_getPackageName")
                .setInputTypes(String.class)
                .setInputNames("classFullName")
                .setOutputType(String.class)
                .addLiterals(char.class, '.')
                .addTags(Tag.IF);

        // unit tests from guava-master/guava-tests/test/com/google/common/reflect/ReflectionTest.java
        task.addExample(new Example()
                .setInputs(() -> new Object[] { "java.MyType" })
                .setOutput("java"));
        task.addExample(new Example()
                .setInputs(() -> new Object[] { Iterable.class.getName() })
                .setOutput("java.lang"));
        task.addExample(new Example()
                .setInputs(() -> new Object[] { "NoPackage" })
                .setOutput(""));

        // added examples
        task.addExample(new Example()
                .setInputs(() -> new Object[] { "ab.cd" })
                .setOutput("ab"));

        return task;
    }

    // from guava-master/guava/src/com/google/common/reflect/Reflection.java
    static String solution(String classFullName) {
        int lastDot = classFullName.lastIndexOf('.');
        return (lastDot < 0) ? "" : classFullName.substring(0, lastDot);
    }
}
