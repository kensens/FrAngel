package frangel.benchmarks.controlstructures;

import java.util.*;

import frangel.Example;
import frangel.SynthesisTask;
import frangel.Tag;
import frangel.benchmarks.BenchmarkGroup;
import frangel.benchmarks.BenchmarkUtils;
import frangel.benchmarks.TaskCreator;

public enum FindIdentityIgnoringCase implements TaskCreator {
    INSTANCE;
    static {
        BenchmarkGroup.CONTROL_STRUCTURES.register(INSTANCE);
    }

    @Override
    public SynthesisTask createTask() {
        SynthesisTask task = new SynthesisTask()
                .setName("findIdentityIgnoringCase")
                .setInputTypes(Map.class)
                .addGenerics(Map.class, String.class)
                .setInputNames("map")
                .setOutputType(Set.class)
                .addGenerics(Set.class, String.class)
                .addClasses(HashSet.class)
                .makeInputsImmutable()
                .addTags(Tag.FOREACH, Tag.IF);

        final Map<String, String> map = new HashMap<>();
        map.put("a", "un");
        map.put("Animal", "animal");
        map.put("Bicycle", "bicicleta");
        map.put("color", "color");
        map.put("extra", "EXTRA");
        map.put("Family", "familia");
        map.put("Idea", "idea");
        map.put("Minute", "minuto");
        map.put("One", "uno");
        map.put("TERRIBLE", "terrible");
        map.put("To", "a");

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new HashMap<>(map) })
                .setOutput(BenchmarkUtils.makeSet("Animal", "color", "extra", "Idea", "TERRIBLE")));

        final Map<String, String> map2 = new HashMap<>();
        map2.put("A", "a");
        map2.put("B", "B");

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new HashMap<>(map2) })
                .setOutput(BenchmarkUtils.makeSet(new String("A"), new String("B"))));

        final Map<String, String> map3 = new HashMap<>();
        map3.put("B", "A");
        map3.put("A", "B");

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new HashMap<>(map3) })
                .setOutput(BenchmarkUtils.makeSet()));

        final Map<String, String> map4 = new HashMap<>();
        map4.put("A", "a");
        map4.put("B", "B");
        map4.put("a", "A");
        map4.put("b", "c");

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new HashMap<>(map4) })
                .setOutput(BenchmarkUtils.makeSet("A", "B", "a")));

        return task;
    }

    static Set<String> solution(Map<String, String> map) {
        Set<String> ans = new HashSet<>();
        for (String key : map.keySet())
            if (map.get(key).equalsIgnoreCase(key))
                ans.add(key);
        return ans;
    }
}
