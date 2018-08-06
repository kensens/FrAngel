package frangel.benchmarks.sypet;

import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import frangel.Example;
import frangel.SynthesisTask;
import frangel.Tag;
import frangel.benchmarks.BenchmarkGroup;
import frangel.benchmarks.TaskCreator;

public enum SyPet_27_stringToElement implements TaskCreator {
    INSTANCE;
    static {
        BenchmarkGroup.SYPET.register(INSTANCE);
    }

    @Override
    public SynthesisTask createTask() {
        SynthesisTask task = new SynthesisTask()
                .setName("sypet_27_stringToElement")
                .setInputTypes(String.class)
                .setInputNames("xmlStr")
                .setOutputType(Element.class)
                .addPackages("org.w3c.dom", "javax.xml.parsers", "org.xml.sax")
                .addClasses(StringReader.class)
                .addTags(Tag.SINGLE_LINE); // Easily written in one line

        task.addExample(new Example()
                .setInputs(() -> new Object[] { "<MyXML id=\"pldi\">xml</MyXML>" })
                .setOutputChecker((Element elem) -> elem != null && elem.getAttribute("id").equals("pldi")));

        return task;
    }

    public static Element solution(String arg0) throws Throwable {
        DocumentBuilderFactory v1 = DocumentBuilderFactory.newInstance();
        DocumentBuilder v2 = v1.newDocumentBuilder();
        StringReader v3 = new StringReader(arg0);
        InputSource v4 = new InputSource(v3);
        Document v5 = v2.parse(v4);
        Element v6 = v5.getDocumentElement();
        return v6;
    }
}
