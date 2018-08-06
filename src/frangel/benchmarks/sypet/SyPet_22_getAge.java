package frangel.benchmarks.sypet;

import org.joda.time.DateTime;
import org.joda.time.Years;

import frangel.Example;
import frangel.SynthesisTask;
import frangel.Tag;
import frangel.benchmarks.BenchmarkGroup;
import frangel.benchmarks.TaskCreator;

public enum SyPet_22_getAge implements TaskCreator {
    INSTANCE;
    static {
        BenchmarkGroup.SYPET.register(INSTANCE);
    }

    @Override
    public SynthesisTask createTask() {
        SynthesisTask task = new SynthesisTask()
                .setName("sypet_22_getAge")
                .setInputTypes(DateTime.class)
                .setInputNames("birthdate")
                .setOutputType(int.class)
                .addPackages("org.joda.time")
                .addTags(Tag.SINGLE_LINE); // Easily written in one line

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new DateTime(1990, 11, 13, 2, 0) })
                .setOutput(Years.yearsBetween(new DateTime(1990, 11, 13, 2, 0), DateTime.now()).getYears()));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new DateTime(1980, 11, 13, 2, 0) })
                .setOutput(Years.yearsBetween(new DateTime(1980, 11, 13, 2, 0), DateTime.now()).getYears()));

        return task;
    }

    public static int solution(DateTime arg0) {
        DateTime v1 = DateTime.now();
        Years v2 = Years.yearsBetween(arg0, v1);
        int v3 = v2.getYears();
        return v3;
    }
}
