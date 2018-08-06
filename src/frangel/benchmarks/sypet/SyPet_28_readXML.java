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

public enum SyPet_28_readXML implements TaskCreator {
    INSTANCE;
    static {
        BenchmarkGroup.SYPET.register(INSTANCE);
    }

    @Override
    public SynthesisTask createTask() {
        SynthesisTask task = new SynthesisTask()
                .setName("sypet_28_readXML")
                .setInputTypes(File.class)
                .setInputNames("file")
                .setOutputType(Document.class)
                .addPackages("org.w3c.dom", "javax.xml.parsers", "org.xml.sax")
                .addTags(Tag.SINGLE_LINE); // Easily written in one line

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new File("benchmarkFiles/sypet_28_popl.xml") })
                .setOutputChecker((Document doc) -> doc != null && doc.getFirstChild() != null && ((Element) doc.getFirstChild()).getAttribute("id").equals("popl")));

        return task;
    }

    public static Document solution(File arg0) throws Throwable {
        DocumentBuilderFactory v1 = DocumentBuilderFactory.newInstance();
        DocumentBuilder v2 = v1.newDocumentBuilder();
        Document v3 = v2.parse(arg0);
        return v3;
    }
}
