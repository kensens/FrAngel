package frangel.benchmarks.controlstructures;

import frangel.Example;
import frangel.SynthesisTask;
import frangel.Tag;
import frangel.benchmarks.BenchmarkGroup;
import frangel.benchmarks.TaskCreator;

public enum FizzBuzz implements TaskCreator {
    INSTANCE;
    static {
        BenchmarkGroup.CONTROL_STRUCTURES.register(INSTANCE);
    }

    @Override
    public SynthesisTask createTask() {
        SynthesisTask task = new SynthesisTask()
                .setName("fizzBuzz")
                .setInputTypes(int.class)
                .setInputNames("num")
                .setOutputType(String.class)
                .addLiterals(int.class, 3, 5, 15)
                .addLiterals(String.class, "Fizz", "Buzz", "FizzBuzz")
                .addTags(Tag.IF);

        task.addExample(new Example()
                .setInputs(() -> new Object[] { 3 })
                .setOutput("Fizz"));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { 21 })
                .setOutput("Fizz"));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { 84 })
                .setOutput("Fizz"));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { 5 })
                .setOutput("Buzz"));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { 40 })
                .setOutput("Buzz"));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { 85 })
                .setOutput("Buzz"));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { 28 })
                .setOutput("28"));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { 47 })
                .setOutput("47"));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { 61 })
                .setOutput("61"));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { 15 })
                .setOutput("FizzBuzz"));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { 60 })
                .setOutput("FizzBuzz"));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { 75 })
                .setOutput("FizzBuzz"));

        return task;
    }

    static String solution(int num) {
        String ans = String.valueOf(num);
        if (num % 3 == 0)
            ans = "Fizz";
        if (num % 5 == 0)
            ans = "Buzz";
        if (num % 15 == 0)
            ans = "FizzBuzz";
        return ans;
    }
}
