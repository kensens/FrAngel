package frangel.benchmarks.sypet;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import frangel.Example;
import frangel.SynthesisTask;
import frangel.Tag;
import frangel.benchmarks.BenchmarkGroup;
import frangel.benchmarks.TaskCreator;

public enum SyPet_20_daysOfMonth implements TaskCreator {
    INSTANCE;
    static {
        BenchmarkGroup.SYPET.register(INSTANCE);
    }

    @Override
    public SynthesisTask createTask() {
        SynthesisTask task = new SynthesisTask()
                .setName("sypet_20_daysOfMonth")
                .setInputTypes(String.class, String.class)
                .setInputNames("dateStr", "formatStr")
                .setOutputType(int.class)
                .addPackages("org.joda.time")
                .addTags(Tag.SINGLE_LINE); // Easily written in one line

        task.addExample(new Example()
                .setInputs(() -> new Object[] { "2012/02", "yyyy/MM" })
                .setOutput(29));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { "2014/03", "yyyy/MM" })
                .setOutput(31));

        return task;
    }

    public static int solution(String arg0, String arg1) {
        DateTimeFormatter v1 = DateTimeFormat.forPattern(arg1);
        DateTime v2 = DateTime.parse(arg0, v1);
        DateTime.Property v3 = v2.dayOfMonth();
        int v4 = v3.getMaximumValue();
        return v4;
    }
}
