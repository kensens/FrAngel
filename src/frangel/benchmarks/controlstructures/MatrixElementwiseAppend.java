package frangel.benchmarks.controlstructures;

import frangel.Example;
import frangel.SynthesisTask;
import frangel.Tag;
import frangel.benchmarks.BenchmarkGroup;
import frangel.benchmarks.TaskCreator;

public enum MatrixElementwiseAppend implements TaskCreator {
    INSTANCE;
    static {
        BenchmarkGroup.CONTROL_STRUCTURES.register(INSTANCE);
    }

    @Override
    public SynthesisTask createTask() {
        SynthesisTask task = new SynthesisTask()
                .setName("matrixElementwiseAppend")
                .setInputTypes(String[][].class, String.class)
                .setInputNames("mat", "suffix")
                .setOutputType(void.class)
                .makeInputsImmutable()
                .addTags(Tag.FOR);

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new String[][] {{"123", "", "?"}, {}, {"cb", "1"}}, "ab" })
                .setModifiedInput(1, new String[][] {{"123ab", "ab", "?ab"}, {}, {"cbab", "1ab"}}));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new String[][] {{"123"}}, "!!" })
                .setModifiedInput(1, new String[][] {{"123!!"}}));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new String[][] {{""}}, "abc" })
                .setModifiedInput(1, new String[][] {{"abc"}}));

        return task;
    }

    static void solution(String[][] mat, String suffix) {
        for (int i = 0; i < mat.length; i++)
            for (int j = 0; j < mat[i].length; j++)
                mat[i][j] += suffix;
    }
}
