package frangel.benchmarks.sypet;

import org.joda.time.Days;
import org.joda.time.LocalDate;

import frangel.Example;
import frangel.SynthesisTask;
import frangel.Tag;
import frangel.benchmarks.BenchmarkGroup;
import frangel.benchmarks.TaskCreator;

public enum SyPet_16_daysUntilNow implements TaskCreator {
    INSTANCE;
    static {
        BenchmarkGroup.SYPET.register(INSTANCE);
    }

    @Override
    public SynthesisTask createTask() {
        SynthesisTask task = new SynthesisTask()
                .setName("sypet_16_daysUntilNow")
                .setInputTypes(LocalDate.class)
                .setInputNames("date")
                .setOutputType(int.class)
                .addPackages("org.joda.time")
                .addTags(Tag.SINGLE_LINE); // Easily written in one line

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new LocalDate(2015, 11, 12) })
                .setOutput(Days.daysBetween(new LocalDate(2015, 11, 12), LocalDate.now()).getDays()));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new LocalDate(2014, 11, 12) })
                .setOutput(Days.daysBetween(new LocalDate(2014, 11, 12), LocalDate.now()).getDays()));

        return task;
    }

    public static int solution(LocalDate arg0) {
        LocalDate v1 = new LocalDate();
        Days v2 = Days.daysBetween(arg0, v1);
        int v3 = v2.getDays();
        return v3;
    }
}
