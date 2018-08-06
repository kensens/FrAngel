package frangel.benchmarks.sypet;

import org.joda.time.DateTime;

import frangel.Example;
import frangel.SynthesisTask;
import frangel.Tag;
import frangel.benchmarks.BenchmarkGroup;
import frangel.benchmarks.TaskCreator;

public enum SyPet_18_isLeapYear implements TaskCreator {
    INSTANCE;
    static {
        BenchmarkGroup.SYPET.register(INSTANCE);
    }

    @Override
    public SynthesisTask createTask() {
        SynthesisTask task = new SynthesisTask()
                .setName("sypet_18_isLeapYear")
                .setInputTypes(int.class)
                .setInputNames("year")
                .setOutputType(boolean.class)
                .addPackages("org.joda.time")
                .addTags(Tag.SINGLE_LINE); // Easily written in one line

        task.addExample(new Example()
                .setInputs(() -> new Object[] { 2000 })
                .setOutput(true));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { 1900 })
                .setOutput(false));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { 2011 })
                .setOutput(false));

        return task;
    }

    public static boolean solution(int arg0) {
        DateTime v1 = DateTime.now();
        DateTime v2 = v1.withWeekyear(arg0);
        DateTime.Property v3 = v2.year();
        boolean v4 = v3.isLeap();
        return v4;
    }
}
