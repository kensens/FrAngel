package frangel.benchmarks.sypet;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import frangel.Example;
import frangel.SynthesisTask;
import frangel.Tag;
import frangel.benchmarks.BenchmarkGroup;
import frangel.benchmarks.TaskCreator;

public enum SyPet_19_getDayFromString implements TaskCreator {
    INSTANCE;
    static {
        BenchmarkGroup.SYPET.register(INSTANCE);
    }

    @Override
    public SynthesisTask createTask() {
        SynthesisTask task = new SynthesisTask()
                .setName("sypet_19_getDayFromString")
                .setInputTypes(String.class, String.class)
                .setInputNames("dateStr", "formatStr")
                .setOutputType(int.class)
                .addPackages("org.joda.time")
                .addTags(Tag.SINGLE_LINE); // Easily written in one line

        task.addExample(new Example()
                .setInputs(() -> new Object[] { "2015/10/21", "yyyy/MM/dd" })
                .setOutput(21));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { "2013/6/13", "yyyy/MM/dd" })
                .setOutput(13));

        return task;
    }

    public static int solution(String arg0, String arg1) {
        DateTimeFormatter v1 = DateTimeFormat.forPattern(arg1);
        LocalDate v2 = LocalDate.parse(arg0, v1);
        int v3 = v2.getDayOfMonth();
        return v3;
    }
}
