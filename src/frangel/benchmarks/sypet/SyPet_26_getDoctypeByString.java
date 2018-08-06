package frangel.benchmarks.sypet;

import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.xml.sax.InputSource;

import frangel.Example;
import frangel.SynthesisTask;
import frangel.Tag;
import frangel.benchmarks.BenchmarkGroup;
import frangel.benchmarks.TaskCreator;

public enum SyPet_26_getDoctypeByString implements TaskCreator {
    INSTANCE;
    static {
        BenchmarkGroup.SYPET.register(INSTANCE);
    }

    @Override
    public SynthesisTask createTask() {
        SynthesisTask task = new SynthesisTask()
                .setName("sypet_26_getDoctypeByString")
                .setInputTypes(String.class)
                .setInputNames("xmlStr")
                .setOutputType(DocumentType.class)
                .addPackages("org.w3c.dom", "javax.xml.parsers", "org.xml.sax")
                .addClasses(StringReader.class)
                .addTags(Tag.SINGLE_LINE); // Easily written in one line

        task.addExample(new Example()
                .setInputs(() -> new Object[] { "<?xml version=\"1.0\"?><!DOCTYPE note [<!ELEMENT note (to,from,heading,body)><!ELEMENT body (#PCDATA)>]><note><heading>Reminder</heading><body>Don't forget me this weekend</body></note>" })
                .setOutputChecker((DocumentType elem) -> elem != null && elem.getName().equals("note")));

        return task;
    }

    public static DocumentType solution(String arg0) throws Throwable {
        DocumentBuilderFactory v1 = DocumentBuilderFactory.newInstance();
        StringReader v2 = new StringReader(arg0);
        DocumentBuilder v3 = v1.newDocumentBuilder();
        InputSource v4 = new InputSource(v2);
        Document v5 = v3.parse(v4);
        DocumentType v6 = v5.getDoctype();
        return v6;
    }
}
