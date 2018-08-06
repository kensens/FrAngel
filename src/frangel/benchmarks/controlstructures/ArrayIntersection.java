package frangel.benchmarks.controlstructures;

import java.util.HashSet;

import frangel.Example;
import frangel.SynthesisTask;
import frangel.Tag;
import frangel.benchmarks.BenchmarkGroup;
import frangel.benchmarks.BenchmarkUtils;
import frangel.benchmarks.TaskCreator;

public enum ArrayIntersection implements TaskCreator {
    INSTANCE;
    static {
        BenchmarkGroup.CONTROL_STRUCTURES.register(INSTANCE);
    }

    @Override
    public SynthesisTask createTask() {
        SynthesisTask task = new SynthesisTask()
                .setName("arrayIntersection")
                .setInputTypes(String[].class, String[].class)
                .setInputNames("arr1", "arr2")
                .setOutputType(HashSet.class)
                .addGenerics(HashSet.class, String.class)
                .makeInputsImmutable()
                .addTags(Tag.FOREACH, Tag.IF);

        task.addExample(new Example()
                .setInputs(() -> new Object[] {
                        new String[] {"abc", "", "hi", "1", "x"},
                        new String[] {"x!", "hi", "hi", "abc", "1", "def"},
                })
                .setOutput(BenchmarkUtils.makeSet("abc", "hi", "1")));

        task.addExample(new Example()
                .setInputs(() -> new Object[] {
                        new String[] {"abc", "x", "hi"},
                        new String[] {new String("hi"), new String("x")},
                })
                .setOutput(BenchmarkUtils.makeSet("hi", "x")));

        task.addExample(new Example()
                .setInputs(() -> new Object[] {
                        new String[] {new String("hi"), new String("x")},
                        new String[] {"abc", "x", "hi"},
                })
                .setOutput(BenchmarkUtils.makeSet("hi", "x")));

        task.addExample(new Example()
                .setInputs(() -> new Object[] {
                        new String[] {" hi", "x", "hi!"},
                        new String[] {"hi", new String("x")},
                })
                .setOutput(BenchmarkUtils.makeSet("x")));

        task.addExample(new Example()
                .setInputs(() -> new Object[] {
                        new String[] {"abc", "x", "hi"},
                        new String[] {"oh hi", "X", "xy"},
                })
                .setOutput(BenchmarkUtils.makeSet()));

        return task;
    }

    static HashSet<String> solution(String[] arr1, String[] arr2) {
        HashSet<String> ans = new HashSet<>();
        for (String s1 : arr1) {
            for (String s2 : arr2) {
                if (s1.equals(s2)) {
                    ans.add(s1);
                    break;
                }
            }
        }
        return ans;
    }
}
