package frangel.benchmarks.sypet;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import frangel.Example;
import frangel.SynthesisTask;
import frangel.Tag;
import frangel.benchmarks.BenchmarkGroup;
import frangel.benchmarks.TaskCreator;

public enum SyPet_21_dayOfWeek implements TaskCreator {
    INSTANCE;
    static {
        BenchmarkGroup.SYPET.register(INSTANCE);
    }

    @Override
    public SynthesisTask createTask() {
        SynthesisTask task = new SynthesisTask()
                .setName("sypet_21_dayOfWeek")
                .setInputTypes(String.class, String.class)
                .setInputNames("dateStr", "formatStr")
                .setOutputType(String.class)
                .addPackages("org.joda.time")
                .addTags(Tag.SINGLE_LINE); // Easily written in one line

        task.addExample(new Example()
                .setInputs(() -> new Object[] { "2015/11/10", "yyyy/MM/dd" })
                .setOutput("Tuesday"));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { "2015/11/11", "yyyy/MM/dd" })
                .setOutput("Wednesday"));

        return task;
    }

    public static String solution(String arg0, String arg1) {
        DateTimeFormatter v1 = DateTimeFormat.forPattern(arg1);
        DateTime v2 = DateTime.parse(arg0, v1);
        DateTime.Property v3 = v2.dayOfWeek();
        String v4 = v3.getAsText();
        return v4;
    }
}
