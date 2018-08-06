package frangel.benchmarks.github;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.elasticsearch.cluster.routing.IndexShardRoutingTable;
import org.elasticsearch.cluster.routing.ShardRouting;
import org.elasticsearch.common.io.stream.InputStreamStreamInput;
import org.elasticsearch.index.shard.ShardId;

import frangel.Example;
import frangel.SynthesisTask;
import frangel.Tag;
import frangel.benchmarks.BenchmarkGroup;
import frangel.benchmarks.BenchmarkUtils;
import frangel.benchmarks.TaskCreator;

public enum ElasticSearchGetAllNodeIds implements TaskCreator {
    INSTANCE;
    static {
        BenchmarkGroup.GITHUB.register(INSTANCE);
    }

    @Override
    public SynthesisTask createTask() {
        SynthesisTask task = new SynthesisTask()
                .setName("elasticsearch_getAllNodeIds")
                .setInputTypes(List.class)
                .addGenerics(List.class, ShardRouting.class)
                .setInputNames("shards")
                .setOutputType(Set.class)
                .addGenerics(Set.class, String.class)
                .addClasses(HashSet.class)
                .excludeMethods(BenchmarkUtils.getMethod(IndexShardRoutingTable.class, "getAllNodeIds", List.class)) // to synthesize
                .addTags(Tag.FOREACH);

        // no unit tests in elasticsearch-master/server/src/test/java/org/elasticsearch/cluster/routing/IndexShardRoutingTableTests.java

        // added examples
        task.addExample(new Example()
                .setInputs(() -> new Object[] { BenchmarkUtils.makeList(shardRouting("abc"), shardRouting("defg"), shardRouting("!")) })
                .setOutput(BenchmarkUtils.makeSet("abc", "defg", "!")));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { BenchmarkUtils.makeList(shardRouting("abc")) })
                .setOutput(BenchmarkUtils.makeSet("abc")));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { BenchmarkUtils.makeList(shardRouting("")) })
                .setOutput(BenchmarkUtils.makeSet("")));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { BenchmarkUtils.makeList() })
                .setOutput(BenchmarkUtils.makeSet()));

        return task;
    }

    // from elasticsearch-master/server/src/main/java/org/elasticsearch/cluster/routing/IndexShardRoutingTable.java
    static Set<String> solution(final List<ShardRouting> shards) {
        final Set<String> nodeIds = new HashSet<>();
        for (ShardRouting shard : shards) {
            nodeIds.add(shard.currentNodeId());
        }
        return nodeIds;
    }

    static ShardRouting shardRouting(String currentNodeId) {
        try {
            String encoding = "\01" + (char)(currentNodeId.length()) + currentNodeId + "\00\00\01\00\00\00";
            return new ShardRouting(new ShardId("index", "uuid", 123),
                    new InputStreamStreamInput(new ByteArrayInputStream(encoding.getBytes(StandardCharsets.UTF_8))));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
