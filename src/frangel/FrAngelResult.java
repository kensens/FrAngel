package frangel;

import java.util.ArrayList;
import java.util.List;

import frangel.model.Program;
import frangel.model.expression.Expression;
import frangel.model.statement.Statement;
import frangel.utils.ProgramUtils;
import frangel.utils.Utils;

public class FrAngelResult {
    // Crucial info
    private String name; // task name
    private boolean success;
    private double time; // in seconds
    private String program;
    private String group;

    // Other data
    private int numExamples;
    private int numComponents;
    private int programSize;
    private String simpleProgram; // without packages
    private boolean sypetMode;
    private List<String> tags;
    private String alg;

    private String unCleanedProgram;
    private int unCleanedProgramSize;

    private int numRememberedPrograms;
    private int numFragments;
    private double averageFragmentUsefulness; // for each fragment, the largest fraction of its nodes that match the program

    private int numProgramsGen;
    private int numProgramsRun;
    private int numAngelicGen;
    private int numAngelicRun;
    private int numNonAngelicGen;
    private int numNonAngelicRun;

    public FrAngelResult(FrAngel frangel, Program program, double time, String unCleanedProgram, int unCleanedProgramSize) {
        SynthesisTask task = frangel.getSynthesisTask();
        name = task.getName();
        group = task.getGroup();
        success = program != null;
        this.time = time;

        boolean oldValue = Settings.USE_SIMPLE_NAME;
        Settings.USE_SIMPLE_NAME = false;
        this.program = success ? program.toJava() : "";
        Settings.USE_SIMPLE_NAME = true;
        simpleProgram = success ? program.toJava() : "";
        Settings.USE_SIMPLE_NAME = oldValue;

        numExamples = task.numExamples();
        numComponents = Utils.numComponents(task);
        programSize = success ? ProgramUtils.size(program) : 0;
        sypetMode = Settings.SYPET_MODE;
        tags = new ArrayList<>();
        for (Tag t : task.getTags())
            tags.add(t.toString());

        if (Settings.MINE_FRAGMENTS)
            alg = Settings.USE_ANGELIC_CONDITIONS ? "FrAngel" : "Fragments";
        else
            alg = Settings.USE_ANGELIC_CONDITIONS ? "Angelic" : "Baseline";

        this.unCleanedProgram = unCleanedProgram;
        this.unCleanedProgramSize = unCleanedProgramSize;

        numRememberedPrograms = 0;
        if (Settings.MINE_FRAGMENTS) {
            numRememberedPrograms = frangel.getFragmentPrograms().size();
            List<Object> allFragments = new ArrayList<Object>();
            for (List<Expression> list : frangel.getExpressionFragments().values())
                allFragments.addAll(list);
            allFragments.addAll(frangel.getStatementFragments());
            numFragments = allFragments.size();
            averageFragmentUsefulness = success ? computeUsefulness(allFragments, program) : 0.0;
        } else {
            numFragments = 0;
            averageFragmentUsefulness = 0.0;
        }

        numProgramsGen = frangel.getGenCount();
        numProgramsRun = frangel.getRunCount();
        numAngelicGen = frangel.getGenAngelicCount();
        numAngelicRun = frangel.getRunAngelicCount();
        numNonAngelicGen = frangel.getGenNoAngelicCount();
        numNonAngelicRun = frangel.getRunNoAngelicCount();
    }

    private double computeUsefulness(List<Object> fragments, Program program) {
        if (fragments.isEmpty())
            return 0.0;
        double sum = 0.0;
        if (Settings.VERBOSE > 2)
            System.out.println("Computing usefulness to program:\n" + program.toJava());
        for (Object fragment : fragments) {
            double usefulness;
            if (fragment instanceof Expression) {
                usefulness = ProgramUtils.computeUsefulness((Expression) fragment, program);
                if (Settings.VERBOSE > 2)
                    System.out.println("Fragment Expression: " + ((Expression) fragment).toJava() + ", usefulness: " + usefulness);
            } else {
                usefulness = ProgramUtils.computeUsefulness((Statement) fragment, program);
                if (Settings.VERBOSE > 2)
                    System.out.println("Fragment Statement: " + ((Statement) fragment).toJava()  + ", usefulness: " + usefulness);
            }
            sum += usefulness;
        }
        return sum / fragments.size();
    }

    public void print() {
        System.out.println("Name: " + name);
        if (group != null)
            System.out.println("Group: " + group);
        System.out.println("Success: " + success);
        System.out.printf("Time: %.3f sec\n", time);
        System.out.println("# Examples: " + numExamples);
        System.out.println("# Components: " + numComponents);
        if (success) {
            System.out.println("Program Size: " + programSize);
            printCounts();
            System.out.println(program);
        } else {
            printCounts();
        }
        System.out.println();
    }

    private void printCounts() {
        if (Settings.VERBOSE > 1) {
            System.out.println("All programs:     generated " + numProgramsGen + ", ran " + numProgramsRun);
            System.out.println("Only non-angelic: generated " + numNonAngelicGen + ", ran " + numNonAngelicRun);
            System.out.println("Only angelic:     generated " + numAngelicGen + ", ran " + numAngelicGen);
        }
    }

    public String getName() {
        return name;
    }
    public boolean isSuccess() {
        return success;
    }
    public double getTime() {
        return time;
    }
    public String getProgram() {
        return program;
    }
    public String getSimpleProgram() {
        return simpleProgram;
    }
    public String getGroup() {
        return group;
    }
    public int getNumExamples() {
        return numExamples;
    }
    public int getNumComponents() {
        return numComponents;
    }
    public int getProgramSize() {
        return programSize;
    }
    public boolean getSyPetMode() {
        return sypetMode;
    }
    public List<String> getTags() {
        return tags;
    }
    public String getAlg() {
        return alg;
    }
    public String getUnCleanedProgram() {
        return unCleanedProgram;
    }
    public int getUnCleanedProgramSize() {
        return unCleanedProgramSize;
    }
    public int getNumRememberedPrograms() {
        return numRememberedPrograms;
    }
    public int getNumFragments() {
        return numFragments;
    }
    public double getAverageFragmentUsefulness() {
        return averageFragmentUsefulness;
    }
    public int getNumProgramsGen() {
        return numProgramsGen;
    }
    public int getNumProgramsRun() {
        return numProgramsRun;
    }
    public int getNumAngelicGen() {
        return numAngelicGen;
    }
    public int getNumAngelicRun() {
        return numAngelicRun;
    }
    public int getNumNonAngelicGen() {
        return numNonAngelicGen;
    }
    public int getNumNonAngelicRun() {
        return numNonAngelicRun;
    }
}
