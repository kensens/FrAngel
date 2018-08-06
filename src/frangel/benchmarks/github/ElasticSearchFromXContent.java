package frangel.benchmarks.github;

import java.io.IOException;

import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.index.IndexResponse.Builder;
import org.elasticsearch.common.xcontent.NamedXContentRegistry;
import org.elasticsearch.common.xcontent.XContentParser;
import org.elasticsearch.common.xcontent.XContentType;

import frangel.Example;
import frangel.SynthesisTask;
import frangel.Tag;
import frangel.benchmarks.BenchmarkGroup;
import frangel.benchmarks.BenchmarkUtils;
import frangel.benchmarks.TaskCreator;

public enum ElasticSearchFromXContent implements TaskCreator {
    INSTANCE;
    static {
        BenchmarkGroup.GITHUB.register(INSTANCE);
    }

    @Override
    public SynthesisTask createTask() {
        SynthesisTask task = new SynthesisTask()
                .setName("elasticsearch_fromXContent")
                .setInputTypes(XContentParser.class)
                .setInputNames("parser")
                .setOutputType(IndexResponse.class)
                .excludeMethods(BenchmarkUtils.getMethod(IndexResponse.class, "fromXContent", XContentParser.class)) // to synthesize
                .addEqualityTester(IndexResponse.class, (IndexResponse a, IndexResponse b)
                        -> a.getVersion() == b.getVersion() && a.getSeqNo() == b.getSeqNo() && a.getPrimaryTerm() == b.getPrimaryTerm())
                .addTags(Tag.WHILE);

        // randomized unit tests at elasticsearch-master/server/src/test/java/org/elasticsearch/action/index/IndexResponseTests.java

        // added examples
        task.addExample(new Example()
                .setInputs(() -> new Object[] { createJsonParser(
                        "{\"junk\":[1, 2, 3],\"_version\":123,\"_seq_no\":45,\"\":\"\",\"_primary_term\":67}") })
                .setOutput(new IndexResponse(null, null, null, 45, 67, 123, true))
                .setModifiedInputChecker(1, (XContentParser p) -> {
                    try {
                        if (p.nextToken() == null)
                            return true;
                    } catch (IOException e) {}
                    return false;
                }));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { createJsonParser("{\"_version\":123}") })
                .setOutput(new IndexResponse(null, null, null, -2, 0, 123, true)));

        return task;
    }

    // from elasticsearch-master/server/src/main/java/org/elasticsearch/action/index/IndexResponse.java
    static IndexResponse solution(XContentParser parser) throws IOException {
        //ensureExpectedToken(XContentParser.Token.START_OBJECT, parser.nextToken(), parser::getTokenLocation);
        Builder context = new Builder();
        while (parser.nextToken() != XContentParser.Token.END_OBJECT) {
            IndexResponse.parseXContentFields(parser, context);
        }
        return context.build();
    }

    // adapted from elasticsearch-master/test/framework/src/main/java/org/elasticsearch/test/ESTestCase.java
    static XContentParser createJsonParser(String data) {
        try {
            XContentParser parser = XContentType.JSON.xContent().createParser(NamedXContentRegistry.EMPTY, data);
            parser.nextToken(); // accounts for the ensureExpectedToken line
            return parser;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
