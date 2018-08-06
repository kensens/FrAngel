package frangel;

import java.util.*;

import frangel.interpreter.Interpreter;
import frangel.model.FunctionData.Kind;
import frangel.model.Program;
import frangel.model.expression.*;
import frangel.model.expression.OpExpression.Op;
import frangel.model.generator.StatementGenerator.StatementCategory;
import frangel.model.statement.*;
import frangel.utils.ProgramUtils;
import frangel.utils.TimeLogger;
import frangel.utils.Utils;

public class Cleaner {

    private Program program;
    private SynthesisTask task;
    private BitSet indices;

    public Cleaner(Program p, SynthesisTask task, BitSet indices) {
        this.program = p;
        this.task = task;
        this.indices = indices;
    }

    private void resetProgramVars() {
        Set<String> usedVars = ProgramUtils.getUsedVars(program);
        usedVars.addAll(program.getArgVars()); // Don't delete info about arguments, even if they weren't used
        program.getVariables().keySet().retainAll(usedVars);
        program.getLocalVars().keySet().retainAll(usedVars);
        program.getLoopVars().retainAll(usedVars);
        program.getElemVars().retainAll(usedVars);
        program.getInScope().retainAll(usedVars);
    }

    private boolean checkClean() {
        for (int i = indices.nextSetBit(0); i >= 0; i = indices.nextSetBit(i+1)) {
            boolean result = Interpreter.runProgram(program, task.getExample(i), null, false).isSuccess();
            if (!result)
                return false;
        }
        return true;
    }

    private static class ExpressionWithSize implements Comparable<ExpressionWithSize> {
        Expression e;
        int size;
        ExpressionWithSize(Expression e) {
            this.e = e;
            size = ProgramUtils.size(e);
        }
        @Override
        public int compareTo(ExpressionWithSize other) {
            return size - other.size;
        }
    }

    // List of possible replacements for exp. All can be assigned to the type of exp. Roughly ordered from simplest to most complex
    private List<Expression> getReplacements(Expression exp) {
        return getReplacements(exp, exp.getType());
    }

    private List<Expression> getReplacements(Expression exp, Class<?> type) {
        Set<Class<?>> types = new HashSet<>();
        types.add(type);
        return getReplacements(exp, types);
    }

    private List<Expression> getReplacements(Expression exp, Set<Class<?>> types) {
        List<Expression> replacements = new ArrayList<Expression>();
        if (exp instanceof LiteralExpression) {
            LiteralExpression l = (LiteralExpression) exp;
            if (l.getType() == int.class && (Integer) l.getLiteral() != 0)
                replacements.add(new LiteralExpression(0, int.class));
            if (l.getType() == long.class && (Long) l.getLiteral() != 0)
                replacements.add(new LiteralExpression(0L, long.class));
            if (l.getType() == double.class && (Double) l.getLiteral() != 0.0)
                replacements.add(new LiteralExpression(0.0, double.class));
            if (l.getType().equals(String.class) && !"".equals(l.getLiteral()))
                replacements.add(new LiteralExpression("", String.class));
            return replacements;
        }

        for (Class<?> type : types) {
            if (!Settings.SYPET_MODE) {
                List<Object> literalChoices = task.getLiteralsForType(type);
                if (literalChoices != null)
                    for (Object lit : literalChoices)
                        replacements.add(new LiteralExpression(lit, type));
            }
            // Allow "null" literal if type isn't primitive
            if (!type.isPrimitive())
                replacements.add(new LiteralExpression(null, type));
        }

        if (exp instanceof VarExpression) {
            if (!program.getArgVars().contains(((VarExpression) exp).getName())) {
                Class<?> expType = exp.getType();
                for (String argName : program.getArgNames()) {
                    Class<?> argType = program.getVariables().get(argName);
                    if (expType.isAssignableFrom(argType))
                        replacements.add(new VarExpression(argName, argType));
                }
            }
            return replacements;
        }

        List<ExpressionWithSize> subtreeReplacements = new ArrayList<>();
        for (Expression sub : ProgramUtils.getSubExpressions(exp)) {
            boolean good = false;
            for (Class<?> type : types) {
                if (type.isAssignableFrom(sub.getType())) {
                    good = true;
                    break;
                }
            }
            if (good)
                subtreeReplacements.add(new ExpressionWithSize(sub));
        }

        Collections.sort(subtreeReplacements);
        for (ExpressionWithSize ews : subtreeReplacements)
            replacements.add(ews.e);
        return replacements;
    }

    private void quickCleanExpression(Expression exp) {
        Expression original;
        List<Expression> replacements;
        boolean replaced;

        if (exp instanceof FuncExpression) {
            FuncExpression f = (FuncExpression) exp;
            if (!f.getData().isStatic()) {
                original = f.getCalledFrom();
                if (f.getData().getKind() == Kind.METHOD)
                    replacements = getReplacements(original, JavaFunctionLoader.getPossibleCallingTypes(f.getData().getMethod()));
                else
                    replacements = getReplacements(original);
                replaced = false;
                for (Expression replacement : replacements) {
                    f.setCalledFrom(replacement);
                    if (checkClean()) {
                        replaced = true;
                        break;
                    }
                }
                if (!replaced)
                    f.setCalledFrom(original);
                quickCleanExpression(f.getCalledFrom());
            }

            for (int i = 0; i < f.getArgs().length; i++) {
                original = f.getArgs()[i];
                replacements = getReplacements(original, f.getData().getArgTypes()[i]);
                replaced = false;
                for (Expression replacement : replacements) {
                    f.getArgs()[i] = replacement;
                    if (checkClean()) {
                        replaced = true;
                        break;
                    }
                }
                if (!replaced)
                    f.getArgs()[i] = original;
                quickCleanExpression(f.getArgs()[i]);
            }
        } else if (exp instanceof OpExpression) {
            OpExpression o = (OpExpression) exp;

            if (o.getLeft() != null && o.getType().equals(int.class)) {
                if (o.getOp() == Op.MINUS && o.getLeft() instanceof LiteralExpression && ((LiteralExpression) o.getLeft()).getLiteral().equals(0)) {
                    o.setOp(Op.TIMES);
                    o.setLeft(new LiteralExpression(-1, int.class));
                } else if ((o.getOp() == Op.TIMES || o.getOp() == Op.DIV) && o.getRight() instanceof LiteralExpression && ((LiteralExpression) o.getRight()).getLiteral().equals(-1)) {
                    Expression neg1 = o.getRight();
                    o.setRight(o.getLeft());
                    o.setLeft(neg1);
                    o.setOp(Op.TIMES);
                }
            }

            if (o.getLeft() != null) {
                original = o.getLeft();
                replacements = getReplacements(original);
                replaced = false;
                for (Expression replacement : replacements) {
                    o.setLeft(replacement);
                    if (checkClean()) {
                        replaced = true;
                        break;
                    }
                }
                if (!replaced)
                    o.setLeft(original);
                quickCleanExpression(o.getLeft());
            }

            original = o.getRight();
            replacements = getReplacements(original);
            replaced = false;
            for (Expression replacement : replacements) {
                o.setRight(replacement);
                if (checkClean()) {
                    replaced = true;
                    break;
                }
            }
            if (!replaced)
                o.setRight(original);
            quickCleanExpression(o.getRight());
        }
    }

    private void quickCleanStatement(Statement s) {
        Expression original;
        List<Expression> replacements;
        boolean replaced;

        if (s instanceof ForLoop) {
            ForLoop f = (ForLoop) s;
            quickCleanBlock(f.getBody());
            if (!f.isAngelic()) {
                original = f.getCondition();
                replacements = getReplacements(original);
                replaced = false;
                for (Expression replacement : replacements) {
                    f.setCondition(replacement);
                    if (checkClean()) {
                        replaced = true;
                        break;
                    }
                }
                if (!replaced)
                    f.setCondition(original);
                quickCleanExpression(f.getCondition());
            }
        } else if (s instanceof IfStatement) {
            IfStatement i = (IfStatement) s;
            quickCleanBlock(i.getBody());
            if (!i.isAngelic()) {
                original = i.getCondition();
                replacements = getReplacements(original);
                replaced = false;
                for (Expression replacement : replacements) {
                    i.setCondition(replacement);
                    if (checkClean()) {
                        replaced = true;
                        break;
                    }
                }
                if (!replaced)
                    i.setCondition(original);
                quickCleanExpression(i.getCondition());
            }
        } else if (s instanceof ForEachLoop) {
            quickCleanBlock(((ForEachLoop) s).getBody());
        } else if (s instanceof VarAssignment) {
            VarAssignment v = (VarAssignment) s;
            original = v.getValue();
            replacements = getReplacements(original);
            replaced = false;
            for (Expression replacement : replacements) {
                v.setValue(replacement);
                if (checkClean()) {
                    replaced = true;
                    break;
                }
            }
            if (!replaced)
                v.setValue(original);
            quickCleanExpression(v.getValue());
        } else if (s instanceof FuncStatement) {
            quickCleanExpression(((FuncStatement) s).getFunc());
        } else {
            System.out.println("Unknown statement class in cleanStatement");
        }
    }

    private void quickCleanBlock(List<Statement> block) {
        for (int i = 0; i < block.size(); ) {
            Statement s = block.get(i);
            block.remove(i);
            if (checkClean())
                continue;

            if (s instanceof ForLoop) {
                ForLoop f = (ForLoop) s;

                if (!f.isAngelic()) {
                    Expression condition = f.getCondition();
                    List<Expression> replacements = getReplacements(condition);
                    replacements.add(condition);
                    for (Expression replacement : replacements) {
                        if (replacement instanceof FuncExpression) {
                            block.add(i, new FuncStatement((FuncExpression) replacement, 0));
                            if (checkClean())
                                continue;
                            block.remove(i);
                        }
                    }
                }

                block.addAll(i, f.getBody());
                if (checkClean())
                    continue;
                for (int j = 0; j < f.getBody().size(); j++)
                    block.remove(i);
            } else if (s instanceof IfStatement) {
                IfStatement is = (IfStatement) s;

                if (!is.isAngelic()) {
                    Expression condition = is.getCondition();
                    List<Expression> replacements = getReplacements(condition);
                    replacements.add(condition);
                    for (Expression replacement : replacements) {
                        if (replacement instanceof FuncExpression) {
                            block.add(i, new FuncStatement((FuncExpression) replacement, 0));
                            if (checkClean())
                                continue;
                            block.remove(i);
                        }
                    }
                }

                block.addAll(i, is.getBody());
                if (checkClean())
                    continue;
                for (int j = 0; j < is.getBody().size(); j++)
                    block.remove(i);
            } else if (s instanceof ForEachLoop) {
                ForEachLoop f = (ForEachLoop) s;
                Set<String> used = new HashSet<>();
                for (Statement inner : f.getBody())
                    ProgramUtils.getUsedVars(inner, used, true);
                if (!used.contains(f.getVarName())) {
                    block.addAll(i, f.getBody());
                    if (checkClean())
                        continue;
                    for (int j = 0; j < f.getBody().size(); j++)
                        block.remove(i);
                }
            } else if (s instanceof VarAssignment) {
                VarAssignment v = (VarAssignment) s;
                Expression value = v.getValue();
                if (value instanceof FuncExpression) {
                    FuncStatement newStatement = new FuncStatement((FuncExpression) value, v.getIndent());
                    block.add(i, newStatement);
                    if (checkClean())
                        continue;
                    block.remove(i);
                }
            }

            block.add(i, s);
            quickCleanStatement(s);
            i++;
        }
    }

    public void quickCleanOnce() {
        quickCleanBlock(program.getStatements());
        if (program.returns()) {
            Expression original = program.getReturnVal();
            List<Expression> replacements = getReplacements(original);
            boolean replaced = false;
            for (Expression replacement : replacements) {
                program.setReturnVal(replacement);
                if (checkClean()) {
                    replaced = true;
                    break;
                }
            }
            if (!replaced)
                program.setReturnVal(original);
            quickCleanExpression(program.getReturnVal());
        }

        resetProgramVars();
    }

    // Cleans up a program, making sure it still passes the given subset of examples
    public void quickClean() {
        TimeLogger.start("Cleaner.quickClean()");
        if (Settings.VERBOSE > 1)
            System.out.println("Quick-cleaning program:\n" + program.toJava());

        String java = program.toJava();
        while (true) {
            quickCleanOnce();
            String newJava = program.toJava();
            if (newJava.equals(java))
                break;
            java = newJava;
        }
        ProgramUtils.resetIndents(program);

        if (Settings.VERBOSE > 1)
            System.out.println("Cleaned (quick):\n" + program.toJava());
        TimeLogger.stop("Cleaner.quickClean()");
    }

    private Expression deepCleanExpressionReplacement(Expression original, int size) {
        return Utils.randBoolean(0.75) ?
                program.getExpressionGenerator().newOrSimilar(original, size + Settings.SIMILAR_NEW_EXTRA_SIZE) :
                    program.getExpressionGenerator().genAnyExp(size + Settings.SIMILAR_NEW_EXTRA_SIZE, original.getType(), false);
    }

    private void deepCleanBlock(List<Statement> block, long timeout) {
        for (int i = 0; i < block.size() && !Utils.timeout(timeout); i++) {
            Statement original = block.get(i);
            int originalSize = ProgramUtils.size(original);
            Statement replacement = (Utils.randBoolean(0.75) ?
                    program.getStatementGenerator().newOrSimilar(original, originalSize + Settings.SIMILAR_NEW_EXTRA_SIZE) :
                        program.getStatementGenerator().genStatement(originalSize + Settings.SIMILAR_NEW_EXTRA_SIZE,
                                new StatementCategory[] {StatementCategory.ASSIGN, StatementCategory.FUNC}, original.getIndent(), false));

            if (replacement != null) {
                int replacementSize = ProgramUtils.size(replacement);
                if (replacementSize < originalSize || (replacementSize == originalSize && replacement.toJava(false).length() < original.toJava(false).length())) {
                    block.set(i, replacement);
                    if (!checkClean())
                        block.set(i, original);
                }
            }

            Statement s = block.get(i);

            if (s instanceof ForLoop) {
                ForLoop f = (ForLoop) s;
                IfStatement is = new IfStatement(f.getCondition(), f.getBody(), f.getIndent());
                block.set(i, is);
                if (!checkClean())
                    block.set(i, f);
            }

            s = block.get(i);

            if (s instanceof ForLoop) {
                ForLoop f = (ForLoop) s;
                Expression originalCond = f.getCondition();
                int originalCondSize = ProgramUtils.size(originalCond);
                program.addToScope(f.getVarName());
                Expression replacementCond = deepCleanExpressionReplacement(originalCond, originalCondSize);
                if (replacementCond != null) {
                    int replacementCondSize = ProgramUtils.size(replacementCond);
                    if (replacementCondSize < originalCondSize || (replacementCondSize == originalCondSize && replacementCond.toJava().length() < originalCond.toJava().length())) {
                        f.setCondition(replacementCond);
                        if (!checkClean())
                            f.setCondition(originalCond);
                    }
                }
                deepCleanBlock(f.getBody(), timeout);
                program.removeFromScope(f.getVarName());
            } else if (s instanceof IfStatement) {
                IfStatement is = (IfStatement) s;
                Expression originalCond = is.getCondition();
                int originalCondSize = ProgramUtils.size(originalCond);
                Expression replacementCond = deepCleanExpressionReplacement(originalCond, originalCondSize);
                if (replacementCond != null) {
                    int replacementCondSize = ProgramUtils.size(replacementCond);
                    if (replacementCondSize < originalCondSize || (replacementCondSize == originalCondSize && replacementCond.toJava().length() < originalCond.toJava().length())) {
                        is.setCondition(replacementCond);
                        if (!checkClean())
                            is.setCondition(originalCond);
                    }
                }
                deepCleanBlock(is.getBody(), timeout);
            } else if (s instanceof ForEachLoop) {
                ForEachLoop f = (ForEachLoop) s;
                program.addToScope(f.getVarName());
                deepCleanBlock(f.getBody(), timeout);
                program.removeFromScope(f.getVarName());
            }
        }
    }

    private void cleanLoopCounters(List<Statement> block) {
        for (int i = 0; i < block.size(); i++) {
            Statement s = block.get(i);
            if (s instanceof ForLoop) {
                ForLoop f = (ForLoop) s;
                if (!f.isVarLocal()) {
                    block.remove(i);
                    Set<String> usedVars = ProgramUtils.getUsedVars(program);
                    block.add(i, f);
                    if (!usedVars.contains(f.getVarName())) {
                        f.setVarLocal(true);
                        program.declareLoopVarInLoop(f.getVarName());
                    }
                }
                cleanLoopCounters(f.getBody());
            } else if (s instanceof IfStatement) {
                cleanLoopCounters(((IfStatement) s).getBody());
            } else if (s instanceof ForEachLoop) {
                cleanLoopCounters(((ForEachLoop) s).getBody());
            }
        }
    }

    private void makeWhileLoops(List<Statement> block, Set<String> actuallyUsed) {
        for (Statement s : block) {
            if (s instanceof ForLoop) {
                ForLoop f = (ForLoop) s;
                if (!actuallyUsed.contains(f.getVarName()))
                    f.setWhileLoop(true);
                makeWhileLoops(f.getBody(), actuallyUsed);
            } else if (s instanceof IfStatement) {
                makeWhileLoops(((IfStatement) s).getBody(), actuallyUsed);
            } else if (s instanceof ForEachLoop) {
                makeWhileLoops(((ForEachLoop) s).getBody(), actuallyUsed);
            }
        }
    }

    private void makeWhileLoops() {
        Set<String> actuallyUsed = ProgramUtils.getUsedVars(program, false);
        makeWhileLoops(program.getStatements(), actuallyUsed);
    }

    private static final String[] VAR_PREFIXES = {"var", "i", "elem"};
    private void reduceVars() {
        Set<String> used = ProgramUtils.getUsedVars(program);
        Set<String> varNames = new TreeSet<>(program.getVariables().keySet());
        Map<String, String> replacements = new HashMap<>();
        for (String oldName : used) {
            String prefix = null;
            for (String p : VAR_PREFIXES) {
                if (oldName.startsWith(p)) {
                    prefix = p;
                    break;
                }
            }
            if (prefix == null)
                continue;
            int num = -1;
            try {
                num = Integer.parseInt(oldName.substring(prefix.length()));
            } catch (NumberFormatException e) {
                // OK, can be a user-provided argument name that starts with a prefix
            }
            if (num == -1)
                continue;
            for (int i = 1; i < num; i++) {
                String newName = prefix + i;
                if (!varNames.contains(newName)) {
                    replacements.put(oldName, newName);
                    varNames.remove(oldName);
                    varNames.add(newName);
                    break;
                }
            }
        }
        ProgramUtils.replaceVars(program, replacements);
    }

    public void deepClean(double synthesisTime, long timeout) {
        TimeLogger.start("Cleaner.deepClean()");

        double cleaningTime = Settings.SIMPLIFICATION_TIME * synthesisTime;
        timeout = Math.min(timeout, Utils.getTimeout(cleaningTime));
        long start = System.nanoTime();
        if (Settings.VERBOSE > 1)
            System.out.println("Deep-cleaning program:\n" + program.toJava());

        int iters = 0;
        while (!Utils.timeout(timeout)) {
            deepCleanBlock(program.getStatements(), timeout);
            if (program.returns()) {
                Expression original = program.getReturnVal();
                int originalSize = ProgramUtils.size(original);
                Expression replacement = deepCleanExpressionReplacement(original, originalSize);
                if (replacement != null) {
                    int replacementSize = ProgramUtils.size(replacement);
                    if (replacementSize < originalSize || (replacementSize == originalSize && replacement.toJava().length() < original.toJava().length())) {
                        program.setReturnVal(replacement);
                        if (!checkClean())
                            program.setReturnVal(original);
                    }
                }
            }
            resetProgramVars();
            iters++;
        }
        ProgramUtils.resetIndents(program);
        cleanLoopCounters(program.getStatements());
        makeWhileLoops();
        resetProgramVars();
        reduceVars();
        resetProgramVars();

        if (Settings.VERBOSE > 1)
            System.out.println("Cleaned (deep, " + Utils.timeSince(start) + " sec, " + iters + " iterations):\n" + program.toJava());
        TimeLogger.stop("Cleaner.deepClean()");

        quickClean();
    }
}
