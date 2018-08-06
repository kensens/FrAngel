package frangel.benchmarks.sypet;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import frangel.Example;
import frangel.SynthesisTask;
import frangel.Tag;
import frangel.benchmarks.BenchmarkGroup;
import frangel.benchmarks.TaskCreator;

public enum SyPet_30_getAttributeById implements TaskCreator {
    INSTANCE;
    static {
        BenchmarkGroup.SYPET.register(INSTANCE);
    }

    @Override
    public SynthesisTask createTask() {
        SynthesisTask task = new SynthesisTask()
                .setName("sypet_30_getAttributeById")
                .setInputTypes(File.class, String.class)
                .setInputNames("file", "id")
                .setOutputType(String.class)
                .addPackages("org.w3c.dom", "javax.xml.parsers", "org.xml.sax")
                .addTags(Tag.SINGLE_LINE); // Easily written in one line

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new File("benchmarkFiles/sypet_30_doc.xml"), "brand" })
                .setOutput("ut"));

        return task;
    }

    public static String solution(File arg0, String arg1) throws Throwable {
        DocumentBuilderFactory v1 = DocumentBuilderFactory.newInstance();
        DocumentBuilder v2 = v1.newDocumentBuilder();
        Document v3 = v2.parse(arg0);
        Element v4 = v3.getDocumentElement();
        String v5 = v4.getAttribute(arg1);
        return v5;
    }
}
