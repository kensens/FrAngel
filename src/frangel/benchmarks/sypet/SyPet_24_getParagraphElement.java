package frangel.benchmarks.sypet;

import javax.swing.text.Document;
import javax.swing.text.Element;

import frangel.Example;
import frangel.SynthesisTask;
import frangel.benchmarks.BenchmarkGroup;
import frangel.benchmarks.TaskCreator;

public enum SyPet_24_getParagraphElement implements TaskCreator {
    INSTANCE;
    static {
        BenchmarkGroup.SYPET.register(INSTANCE);
    }

    @Override
    public SynthesisTask createTask() {
        SynthesisTask task = new SynthesisTask()
                .setName("sypet_24_getParagraphElement")
                .setInputTypes(Document.class, int.class)
                .setInputNames("doc", "offset")
                .setOutputType(Element.class)
                .addPackages("javax.swing.text")
                .addTags(/* none applicable */);

        task.addExample(new Example()
                .setInputs(() -> {
                    java.lang.String html = " <html>\n"
                            + "   <head>\n"
                            + "     <title>An example HTMLDocument</title>\n"
                            + "     <style type=\"text/css\">\n"
                            + "       div { background-color: silver; }\n"
                            + "       ul { color: red; }\n"
                            + "     </style>\n"
                            + "   </head>\n"
                            + "   <body>\n"
                            + "     <div id=\"BOX\">\n"
                            + "       <p>Paragraph 1</p>\n"
                            + "       <p>Paragraph 2</p>\n"
                            + "     </div>\n"
                            + "   </body>\n"
                            + " </html>\n";

                    java.io.Reader stringReader = new java.io.StringReader(html);
                    javax.swing.text.html.HTMLEditorKit htmlKit = new javax.swing.text.html.HTMLEditorKit();
                    javax.swing.text.html.HTMLDocument htmlDoc = (javax.swing.text.html.HTMLDocument) htmlKit.createDefaultDocument();
                    try {
                        htmlKit.read(stringReader, htmlDoc, 0);
                        return new Object[] { htmlDoc, 1 };
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                })
                .setOutputChecker((Element elem) -> elem != null && elem.getName().equals("head")));

        return task;
    }

    public static Element solution(Document arg0, int arg1) {
        Element v1 = arg0.getDefaultRootElement();
        int v2 = v1.getElementIndex(arg1);
        Element v3 = v1.getElement(v2);
        return v3;
    }
}
