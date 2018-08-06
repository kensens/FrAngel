package frangel.benchmarks.sypet;

import javax.swing.text.Document;
import javax.swing.text.Element;

import frangel.Example;
import frangel.SynthesisTask;
import frangel.Tag;
import frangel.benchmarks.BenchmarkGroup;
import frangel.benchmarks.TaskCreator;

public enum SyPet_23_getOffsetForLine implements TaskCreator {
    INSTANCE;
    static {
        BenchmarkGroup.SYPET.register(INSTANCE);
    }

    @Override
    public SynthesisTask createTask() {
        SynthesisTask task = new SynthesisTask()
                .setName("sypet_23_getOffsetForLine")
                .setInputTypes(Document.class, int.class)
                .setInputNames("doc", "line")
                .setOutputType(int.class)
                .addPackages("javax.swing.text")
                .addTags(Tag.SINGLE_LINE); // Easily written in one line

        task.addExample(new Example()
                .setInputs(() -> {
                    String html = " <html>\n"
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
                .setOutput(3));

        return task;
    }

    public static int solution(Document arg0, int arg1) {
        Element v1 = arg0.getDefaultRootElement();
        Element v2 = v1.getElement(arg1);
        int v3 = v2.getStartOffset();
        return v3;
    }
}
