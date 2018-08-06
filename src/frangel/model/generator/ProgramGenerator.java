package frangel.model.generator;

import java.util.List;
import java.util.Map;

import frangel.Settings;
import frangel.SynthesisTask;
import frangel.model.Program;
import frangel.model.expression.Expression;
import frangel.model.statement.Statement;
import frangel.utils.TimeLogger;
import frangel.utils.Utils;

public class ProgramGenerator {

    private SynthesisTask task;

    private Map<Class<?>, List<Expression>> expressionFragments;
    private List<Statement> statementFragments;

    public ProgramGenerator(SynthesisTask task) {
        this.task = task;
        expressionFragments = null;
        statementFragments = null;
    }

    public void useFragments(Map<Class<?>, List<Expression>> expressionFragments, List<Statement> statementFragments) {
        this.expressionFragments = expressionFragments;
        this.statementFragments = statementFragments;
    }

    public Program generateProgram(boolean angelic) {
        TimeLogger.start("ProgramGenerator.generateProgram()");
        long start = System.nanoTime();
        Program p;

        while (true) {
            p = new Program(task, angelic);
            p.useFragments(expressionFragments, statementFragments);

            int size = Utils.randInt(Settings.MIN_SIZE, Settings.MAX_SIZE + 1);
            int numLocalVars = Utils.randInt(1, Settings.MAX_LOCAL_VARS + 1);
            size -= numLocalVars;
            int returnSize = 0;
            int bodySize = size;
            if (p.returns()) {
                returnSize = Utils.randInt(Settings.MIN_EXP_SIZE, size + 1);
                bodySize = size - returnSize;
            }

            generateLocalVars(p, numLocalVars);
            generateStatements(p, bodySize);
            if (generateReturn(p, returnSize))
                break;
        }
        long time = System.nanoTime() - start;
        if (time > 10.0e9)
            System.err.println("Generating program took " + (time/1.0e9) + " seconds:\n" + p.toJava());
        TimeLogger.stop("ProgramGenerator.generateProgram()");
        return p;
    }

    void generateLocalVars(Program p, int numVars) {
        if (task.getClasses().size() == 0)
            return;
        for (int i = 0; i < numVars; i++) {
            Class<?> cls;
            if (!task.getOutputType().equals(void.class) && !task.getOutputType().equals(Object.class) && Utils.randBoolean())
                cls = task.getOutputType();
            else
                cls = Utils.randElement(task.getClassesCachedArray());
            String name = p.getFreshLocalVar();
            p.addLocalVar(name, cls);
            p.addToScope(name);
        }
    }

    void generateStatements(Program p, int size) {
        p.setStatements(p.getStatementGenerator().genBlock(size, 1));
    }

    // Returns true if successful
    boolean generateReturn(Program p, int size) {
        if (!p.returns())
            return true;
        for (int i = 0; i < Settings.GEN_RETURN_TRIES; i++) {
            p.setReturnVal(p.getExpressionGenerator().genAnyExp(size, p.getReturnType(), false));
            if (p.getReturnVal() != null)
                return true;
        }
        p.setReturnVal(p.getExpressionGenerator().genVarExp(size, p.getReturnType()));
        return p.getReturnVal() != null;
    }
}
