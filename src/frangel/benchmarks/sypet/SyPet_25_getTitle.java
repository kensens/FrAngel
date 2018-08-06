package frangel.benchmarks.sypet;

import org.jsoup.Connection;
import org.jsoup.helper.HttpConnection;
import org.jsoup.nodes.Document;

import frangel.Example;
import frangel.SynthesisTask;
import frangel.Tag;
import frangel.benchmarks.BenchmarkGroup;
import frangel.benchmarks.TaskCreator;

public enum SyPet_25_getTitle implements TaskCreator {
    INSTANCE;
    static {
        BenchmarkGroup.SYPET.register(INSTANCE);
    }

    @Override
    public SynthesisTask createTask() {
        SynthesisTask task = new SynthesisTask()
                .setName("sypet_25_getTitle")
                .setInputTypes(String.class)
                .setInputNames("url")
                .setOutputType(String.class)
                .addPackages("org.jsoup")
                .addTags(Tag.SINGLE_LINE); // Easily written in one line

        task.addExample(new Example()
                .setInputs(() -> new Object[] { "https://www.google.com/" })
                .setOutput("Google"));

        return task;
    }

    public static String solution(String arg0) throws Throwable {
        Connection v1 = HttpConnection.connect(arg0);
        Document v2 = v1.get();
        String v3 = v2.title();
        return v3;
    }
}
