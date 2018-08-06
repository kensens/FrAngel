package frangel.benchmarks.sypet;

import org.joda.time.*;

import frangel.Example;
import frangel.SynthesisTask;
import frangel.Tag;
import frangel.benchmarks.BenchmarkGroup;
import frangel.benchmarks.TaskCreator;

public enum SyPet_17_daysBetween implements TaskCreator {
    INSTANCE;
    static {
        BenchmarkGroup.SYPET.register(INSTANCE);
    }

    @Override
    public SynthesisTask createTask() {
        SynthesisTask task = new SynthesisTask()
                .setName("sypet_17_daysBetween")
                .setInputTypes(DateTime.class, DateTime.class)
                .setInputNames("date1", "date2")
                .setOutputType(int.class)
                .addPackages("org.joda.time")
                .addTags(Tag.SINGLE_LINE); // Easily written in one line

        task.addExample(new Example()
                .setInputs(() -> {
                    DateTimeZone PORTUGAL = DateTimeZone.forID("Europe/Lisbon");
                    DateTime start = new DateTime(2013, 9, 16, 5, 0, 0, PORTUGAL);
                    DateTime end = new DateTime(2013, 10, 21, 13, 0, 0, PORTUGAL);
                    return new Object[] { start, end };
                })
                .setOutput(35));

        task.addExample(new Example()
                .setInputs(() -> {
                    DateTimeZone BRAZIL = DateTimeZone.forID("America/Sao_Paulo");
                    DateTimeZone PORTUGAL = DateTimeZone.forID("Europe/Lisbon");
                    DateTime start = new DateTime(2013, 10, 13, 23, 59, BRAZIL);
                    DateTime end = new DateTime(2013, 10, 20, 3, 0, PORTUGAL);
                    return new Object[] { start, end };
                })
                .setOutput(7));

        task.addExample(new Example()
                .setInputs(() -> {
                    DateTimeZone SH = DateTimeZone.forID("Asia/Shanghai");
                    DateTimeZone CT = DateTimeZone.forID("America/Chicago");
                    DateTime start = new DateTime(2013, 11, 13, 10, 59, SH);
                    DateTime end = new DateTime(2013, 11, 20, 5, 0, CT);
                    return new Object[] { start, end };
                })
                .setOutput(7));

        return task;
    }

    public static int solution(DateTime arg0, DateTime arg1) {
        LocalDate v1 = arg1.toLocalDate();
        LocalDate v2 = arg0.toLocalDate();
        Days v3 = Days.daysBetween(v2, v1);
        int v4 = v3.getDays();
        return v4;
    }
}
