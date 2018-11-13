package frangel;

import java.text.SimpleDateFormat;
import java.util.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import frangel.benchmarks.BenchmarkGroup;
import frangel.utils.TimeLogger;
import frangel.utils.Utils;

public class Main {

    static void stateSpace(int maxSize) {
        SynthesisTask b = frangel.benchmarks.controlstructures.GetRange.INSTANCE.createTask();
        b.finalizeSetup();
        JavaFunctionLoader.resetData(b);
        Set<String> programs = new HashSet<String>(1000*1000);
        int[] counts = new int[maxSize + 1];
        frangel.model.generator.ProgramGenerator generator = new frangel.model.generator.ProgramGenerator(b);
        long gen = 0;
        long lastTime = System.nanoTime();
        int lastSize = 0;
        while (programs.size() < 1000*1000*60) {
            frangel.model.Program p = generator.generateProgram(false);
            gen++;
            int size = frangel.utils.ProgramUtils.size(p);
            if (size <= maxSize) {
                String encoding = p.encode();
                if (!programs.contains(encoding)) {
                    programs.add(encoding);
                    counts[size]++;
                }
            }
            if (gen % 100000000 == 0) {
                System.out.println("Generated " + gen + " programs total.");
                System.out.println("Size counts: " + Arrays.toString(counts));
                System.out.println("# distinct programs = " + programs.size());
                long curTime = System.nanoTime();
                int curSize = programs.size();
                double rate = (curSize - lastSize) / ((curTime - lastTime) / 1.0e9);
                System.out.printf("(# distinct programs) increased by %.1f per sec (%.1f per hour)\n\n", rate, rate * 60 * 60);
                lastSize = curSize;
                lastTime = curTime;
            }
        }
    }

    public static void main(String[] args) {
//        stateSpace(7);
//        System.exit(0);

        TimeLogger.start("Total");

        for (String s : args) {
            try {
                int firstEquals = s.indexOf('=');
                String label = s.substring(0, firstEquals).toLowerCase();
                String value = s.substring(firstEquals + 1);

                if (label.equals("-fragments")) {
                    Settings.MINE_FRAGMENTS = Boolean.parseBoolean(value);
                } else if (label.equals("-angelic")) {
                    Settings.USE_ANGELIC_CONDITIONS = Boolean.parseBoolean(value);
                } else if (label.equals("-time")) {
                    Settings.TIME_LIMIT = Integer.parseInt(value);
                } else if (label.equals("-group")) {
                    Settings.BENCHMARK_GROUP = BenchmarkGroup.valueOf(value);
                } else if (label.equals("-run")) {
                    Settings.RUN = Integer.parseInt(value);
                } else if (label.equals("-results-folder")) {
                    Settings.RESULTS_FOLDER = value;
                } else if (label.equals("-all-in-group")) {
                    Settings.RUN_ALL_IN_GROUP = Boolean.parseBoolean(value);
                } else if (label.equals("-all-groups")) {
                    Settings.RUN_ALL_GROUPS = Boolean.parseBoolean(value);
                } else {
                    throw new Exception();
                }
            } catch (Exception e) {
                System.err.println("Bad argument: " + s);
            }
        }

        if (Settings.RUN_ALL_GROUPS)
            System.out.println("Running all benchmark groups.");
        else if (Settings.RUN_ALL_IN_GROUP)
            System.out.println("Running benchmark group " + Settings.BENCHMARK_GROUP + ".");
        else
            System.out.println("Running task " + Settings.TASK.getName() + ".");
        System.out.println("Time limit = " + Settings.TIME_LIMIT + " sec");
        System.out.println("Mine fragments = " + Settings.MINE_FRAGMENTS);
        System.out.println("Angelic conditions = " + Settings.USE_ANGELIC_CONDITIONS);
        System.out.println("Results folder = " + Settings.RESULTS_FOLDER);
        System.out.println();

        List<SynthesisTask> tasks = new ArrayList<>();
        List<Class<?>> creatorClasses = new ArrayList<>();

        if (Settings.RUN_ALL_GROUPS) {
            for (BenchmarkGroup group : BenchmarkGroup.values()) {
                tasks.addAll(group.getTasks());
                creatorClasses.addAll(group.getCreatorClasses());
            }
        } else if (Settings.RUN_ALL_IN_GROUP) {
            tasks.addAll(Settings.BENCHMARK_GROUP.getTasks());
            creatorClasses.addAll(Settings.BENCHMARK_GROUP.getCreatorClasses());
        } else {
            tasks.add(Settings.TASK);
            creatorClasses.add(Settings.TASK_CREATOR.getClass());
        }

        Set<String> names = new HashSet<>();
        for (SynthesisTask task : tasks) {
            String name = task.getName();
            if (names.contains(name))
                System.err.println("Duplicate name found: " + name);
            names.add(name);
        }

        if (Settings.CHECK_SOLUTIONS) {
            List<String> failed = new ArrayList<>();
            for (int i = 0; i < tasks.size(); i++) {
                if (!FrAngel.test(tasks.get(i), creatorClasses.get(i), "solution"))
                    failed.add(tasks.get(i).getName());
            }
            if (failed.isEmpty()) {
                System.out.println("All solutions pass!\n");
            } else {
                System.err.println("Failed solutions: " + failed);
                System.exit(-1);
            }
        }

        if (Settings.COUNT_EXAMPLES_AND_COMPONENTS) {
            List<Integer> numExamples = new ArrayList<>();
            List<Integer> numComponents = new ArrayList<>();
            for (SynthesisTask task : tasks) {
                Settings.SYPET_MODE = BenchmarkGroup.SYPET.toString().equals(task.getGroup());
                if (Settings.SYPET_MODE)
                    Settings.loadSyPetPolymorphismMap();
                numExamples.add(task.getExamples().size());
                task.finalizeSetup();
                JavaFunctionLoader.resetData(task);
                numComponents.add(Utils.numComponents(task));
                System.out.println(task.getName() + ", " + task.getExamples().size() + " examples, "
                        + Utils.numComponents(task) + " components");
            }
            System.out.println("    Num examples: " + numExamples);
            System.out.println("    Num components: " + numComponents);
            System.out.println("    Mean num examples:   " + Utils.mean(numExamples));
            System.out.println("    Median num examples: " + Utils.median(numExamples));
            System.out.println("    Mean num components:   " + Utils.mean(numComponents));
            System.out.println("    Median num components: " + Utils.median(numComponents));
            System.out.println();
        }

        System.out.println("Running " + tasks.size() + " task(s).\n");

        List<FrAngelResult> results = new ArrayList<FrAngelResult>();

        int success = 0, total = 0;
        for (SynthesisTask task : tasks) {
            Settings.SYPET_MODE = BenchmarkGroup.SYPET.toString().equals(task.getGroup());
            if (Settings.SYPET_MODE)
                Settings.loadSyPetPolymorphismMap();
            FrAngelResult result = runTask(task);
            if (result.isSuccess())
                success++;
            results.add(result);
            total++;
        }
        System.out.printf("Success rate: %d / %d = %.2f%%\n", success, total, success * 100.0 / total);

        Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
        if (Settings.VERBOSE > 1)
            System.out.println(gson.toJson(results));
        String outputFilename = Settings.RESULTS_FOLDER;
        if (Settings.RUN >= 0)
            outputFilename += "run" + Settings.RUN + "-";
        if (Settings.RUN_ALL_GROUPS)
            outputFilename += "ALL_GROUPS";
        else if (Settings.RUN_ALL_IN_GROUP)
            outputFilename += Settings.BENCHMARK_GROUP;
        else
            outputFilename += Settings.TASK.getName();
        outputFilename += "-" + Settings.TIME_LIMIT + "s-"
                + (Settings.MINE_FRAGMENTS ? "F" : "x") + (Settings.USE_ANGELIC_CONDITIONS ? "A" : "x") + "-"
                + new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(new Date()) + ".json";
        Utils.stringToFile(gson.toJson(results), outputFilename);
        System.out.println("Wrote results to " + outputFilename);

        TimeLogger.stop("Total");
        TimeLogger.printLog();
    }

    public static FrAngelResult runTask(SynthesisTask task) {
        if (Settings.VERBOSE > 0)
            System.out.println(task.toString());
        System.gc(); // garbage collect to reduce timing variance

        FrAngel frangel = new FrAngel(task);
        FrAngelResult result = frangel.run(Utils.getTimeout(Settings.TIME_LIMIT));
        result.print();
        return result;
    }
}
