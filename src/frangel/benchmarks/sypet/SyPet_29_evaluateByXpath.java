package frangel.benchmarks.sypet;

import java.io.File;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import frangel.Example;
import frangel.SynthesisTask;
import frangel.Tag;
import frangel.benchmarks.BenchmarkGroup;
import frangel.benchmarks.TaskCreator;

public enum SyPet_29_evaluateByXpath implements TaskCreator {
    INSTANCE;
    static {
        BenchmarkGroup.SYPET.register(INSTANCE);
    }

    @Override
    public SynthesisTask createTask() {
        SynthesisTask task = new SynthesisTask()
                .setName("sypet_29_evaluateByXpath")
                .setInputTypes(File.class, String.class, QName.class)
                .setInputNames("file", "query", "qname")
                .setOutputType(Object.class)
                .addPackages("org.w3c.dom", "javax.xml", "org.xml.sax")
                .addTags(Tag.SINGLE_LINE); // Easily written in one line

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new File("benchmarkFiles/sypet_29_doc.xml"), "/html/body/div[@id='container']", XPathConstants.NODE })
                .setOutputChecker((Object node) -> node != null && node instanceof Node && ((Node) node).getNodeName().equals("div")));

        return task;
    }

    public static Object solution(File arg0, String arg1, QName arg2) throws Throwable {
        XPathFactory v1 = XPathFactory.newInstance();
        DocumentBuilderFactory v2 = DocumentBuilderFactory.newInstance();
        XPath v3 = v1.newXPath();
        DocumentBuilder v4 = v2.newDocumentBuilder();
        Document v5 = v4.parse(arg0);
        Object v6 = v3.evaluate(arg1, v5, arg2);
        return v6;
    }
}
