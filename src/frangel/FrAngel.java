package frangel;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

import frangel.interpreter.EvaluationInfo;
import frangel.interpreter.Interpreter;
import frangel.model.Program;
import frangel.model.expression.Expression;
import frangel.model.expression.FuncExpression;
import frangel.model.expression.LiteralExpression;
import frangel.model.generator.ProgramGenerator;
import frangel.model.statement.*;
import frangel.utils.*;

public class FrAngel {
    private SynthesisTask task;

    private Map<BitSet, Program> fragmentPrograms;
    private Map<Class<?>, List<Expression>> expressionFragments;
    private List<Statement> statementFragments;

    private Set<String> nonAngelicPrograms;
    private Set<String> angelicPrograms;

    private int genCount, runCount, genAngelicCount, runAngelicCount, genNoAngelicCount, runNoAngelicCount;

    private long lastDebug, lastResolveConditions;

    public static void synthesize(SynthesisTask task) {
        synthesize(task, 1000000, 1);
    }
    public static void synthesize(SynthesisTask task, double seconds) {
        synthesize(task, seconds, 1);
    }
    public static void synthesize(SynthesisTask task, double seconds, int verbose) {
        int oldVerbose = Settings.VERBOSE;
        Settings.VERBOSE = verbose;
        FrAngel frangel = new FrAngel(task);
        System.out.println("Synthesizing " + task.getName() + "...");
        FrAngelResult result = frangel.run(Utils.getTimeout(seconds));
        System.out.println();
        if (result.isSuccess()) {
            System.out.printf(Colors.color(Colors.GREEN, "Found solution in %.3f seconds:") + "\n%s\n", result.getTime(), result.getProgram());
            if (verbose > 1)
                result.print();
        } else {
            System.out.println(Colors.color(Colors.BRIGHT_RED, "Could not find solution."));
        }
        Settings.VERBOSE = oldVerbose;
    }

    public static boolean test(SynthesisTask task, Class<?> cls) {
        return UnitTester.test(task, cls, task.getName());
    }

    public static boolean test(SynthesisTask task, Class<?> cls, String methodName) {
        return UnitTester.test(task, cls, methodName);
    }

    public FrAngel(SynthesisTask task) {
        task.finalizeSetup();
        this.task = task;

        if (Settings.MINE_FRAGMENTS) {
            fragmentPrograms = new HashMap<BitSet, Program>();
            expressionFragments = new HashMap<Class<?>, List<Expression>>();
            statementFragments = new ArrayList<Statement>();
        }
        nonAngelicPrograms = new HashSet<String>();
        angelicPrograms = new HashSet<String>();
        genCount = runCount = genAngelicCount = runAngelicCount = genNoAngelicCount = runNoAngelicCount = 0;
    }

    public FrAngelResult run(long timeout) {
        // Don't time this. SyPet doesn't include JAR parsing (about 7 sec) in their time. This is FrAngel's equivalent.
        // This typically takes ~0.2 sec
        JavaFunctionLoader.resetData(task);

        long start = System.nanoTime();

        lastDebug = lastResolveConditions = start;

        ProgramGenerator generator = new ProgramGenerator(task);
        if (Settings.MINE_FRAGMENTS)
            generator.useFragments(expressionFragments, statementFragments);

        Program ans = null;
        int numExamples = task.numExamples();

        String unCleanedProgram = "";
        int unCleanedProgramSize = 0;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yy h:mm:ss a").withZone(ZoneId.systemDefault());

        while (!Utils.timeout(timeout)) {
            printDebugInfo();

            boolean useAngelic = Utils.randBoolean() ? false : Settings.USE_ANGELIC_CONDITIONS; // sometimes generate concrete conditions
            Program p = generator.generateProgram(useAngelic);
            int numAngelic = ProgramUtils.numAngelic(p);
            boolean isAngelic = numAngelic > 0;
            genCount++;
            if (isAngelic)
                genAngelicCount++;
            else
                genNoAngelicCount++;

            if (!preprocessProgram(p))
                continue;

            if (skipProgram(p, numAngelic))
                continue;

            if (Settings.VERBOSE > 2) {
                System.out.println("Current time: " + formatter.format(Instant.now()));
                System.out.println("Evaluating program (angelic: " + isAngelic + "):\n" + p.toJava());
            }

            BitSet passed;
            runCount++;
            if (isAngelic) {
                runAngelicCount++;
                passed = evaluateAngelic(p);
            } else {
                runNoAngelicCount++;
                passed = evaluateNoAngelic(p);
            }

            if (Settings.VERBOSE > 2)
                System.out.println("Passed examples (0-based): " + passed);

            if (passed == null || passed.isEmpty())
                continue;

            if (isAngelic) {
                passed = resolveConditions(p, passed, timeout);
                if (passed == null)
                    continue;
            }

            if (Settings.MINE_FRAGMENTS)
                passed = mineFragments(p, passed);

            if (passed.cardinality() == numExamples) {
                if (!ProgramUtils.getUsedVars(p).containsAll(p.getArgVars()))
                    continue;

                // last check
                boolean good;
                if (ProgramUtils.numAngelic(p) != 0) {
                    good = false;
                } else {
                    p.setAngelic(false);
                    passed = evaluateNoAngelic(p);
                    good = (passed != null && passed.cardinality() == numExamples);
                }
                if (good) {
                    unCleanedProgram = p.toJava();
                    unCleanedProgramSize = ProgramUtils.size(p);

                    ans = p.clone();
                    BitSet allExamples = new BitSet();
                    allExamples.flip(0, numExamples);
                    Cleaner cleaner = new Cleaner(ans, task, allExamples);
                    cleaner.deepClean(Utils.timeSince(start), timeout);

                    if (!ProgramUtils.getUsedVars(ans).containsAll(ans.getArgVars())) {
                        System.err.println("Deep-cleaning produced bad program (reverting to uncleaned version):\n" + ans.toJava());
                        ans = p;
                    }

                    break;
                } else {
                    System.err.println("Bad program for " + task.getName() + ", probably nondeterministic:\n" + p.toJava());
                    continue;
                }
            }
        }

        return new FrAngelResult(this, ans, Utils.timeSince(start), unCleanedProgram, unCleanedProgramSize);
    }

    private void printDebugInfo() {
        long now = System.nanoTime();
        if (now > lastDebug + Settings.PROGRESS_DELAY * 1e9) {
            lastDebug = now;

            if (Settings.VERBOSE > 1) {
                if (Settings.MINE_FRAGMENTS) {
                    System.out.println("Keeping " + fragmentPrograms.size() + " fragment programs:\n");
                    for (BitSet passed : fragmentPrograms.keySet()) {
                        Program p = fragmentPrograms.get(passed);
                        System.out.println("Program for " + passed + ":\n" + p.toJava() + "\n");
                    }

                    for (Class<?> cls : expressionFragments.keySet()) {
                        System.out.println("Fragments of type " + cls.getName() + ":");
                        for (Expression e : expressionFragments.get(cls))
                            System.out.println("  " + e.toJava());
                    }
                    if (!statementFragments.isEmpty())
                        System.out.println("\nStatement fragments:");
                    for (Statement s : statementFragments)
                        System.out.print("  " + s.toJava());
                }
                printCounts();
            }
        }
    }

    public void printCounts() {
        System.out.println("All programs:     generated " + genCount + ", ran " + runCount);
        System.out.println("Only non-angelic: generated " + genNoAngelicCount + ", ran " + runNoAngelicCount);
        System.out.println("Only angelic:     generated " + genAngelicCount + ", ran " + runAngelicCount);
    }

    private boolean preprocessBlock(List<Statement> statements) {
        for (int i = 0; i < statements.size(); i++) {
            Statement s = statements.get(i);

            if (s instanceof FuncStatement || s instanceof VarAssignment) {
                if (ProgramUtils.size(s) > Settings.MAX_LINE_SIZE) {
                    statements.remove(i--);
                    continue;
                }
            }

            if (s instanceof FuncStatement) {
                FuncExpression exp = ((FuncStatement) s).getFunc();
                if (!ProgramUtils.containsVar(exp))
                    statements.remove(i--);
            } else if (s instanceof ForLoop) {
                ForLoop f = (ForLoop) s;
                if (!f.isAngelic() && ProgramUtils.size(f.getCondition()) > Settings.MAX_LINE_SIZE)
                    return false;
                List<Statement> body = f.getBody();
                preprocessBlock(body);
                if (body.isEmpty() && (f.isAngelic() || !ProgramUtils.containsVar(f.getCondition())))
                    statements.remove(i--);
            } else if (s instanceof IfStatement) {
                IfStatement is = (IfStatement) s;
                if (!is.isAngelic() && ProgramUtils.size(is.getCondition()) > Settings.MAX_LINE_SIZE)
                    return false;
                List<Statement> body = is.getBody();
                preprocessBlock(body);
                if (body.isEmpty() && (is.isAngelic() || !ProgramUtils.containsVar(is.getCondition())))
                    statements.remove(i--);
            } else if (s instanceof ForEachLoop) {
                List<Statement> body = ((ForEachLoop) s).getBody();
                preprocessBlock(body);
                if (body.isEmpty())
                    statements.remove(i--);
            }
        }
        return true;
    }

    private boolean preprocessProgram(Program p) {
        if (p.returns() && ProgramUtils.size(p.getReturnVal()) > Settings.MAX_LINE_SIZE)
            return false;
        if (!preprocessBlock(p.getStatements()))
            return false;
        return true;
    }

    private boolean skipProgram(Program p, int numAngelic) {
        TimeLogger.start("FrAngel.skipProgram()");
        boolean skip = false;
        String encoding = p.encode();
        if (numAngelic > 0) {
            if (numAngelic > Settings.MAX_ANGELIC_CONDITIONS) {
                skip = true;
            } else if (angelicPrograms.contains(encoding)) {
                if (Utils.randBoolean(Settings.SKIP_DUPLICATE_ANGELIC_PROB))
                    skip = true;
            } else {
                if (angelicPrograms.size() < Settings.MAX_ANGELIC_SET_SIZE)
                    angelicPrograms.add(encoding);
            }
        } else {
            if (nonAngelicPrograms.contains(encoding)) {
                skip = true;
            } else {
                if (nonAngelicPrograms.size() < Settings.MAX_NON_ANGELIC_SET_SIZE)
                    nonAngelicPrograms.add(encoding);
            }
        }
        TimeLogger.stop("FrAngel.skipProgram()");
        return skip;
    }

    private static void findCodePaths(int numTrue, StringBuilder path, List<String> paths, BitStringTrie used, int limit) {
        if (paths.size() >= limit || used.containsPrefix(path))
            return;
        if (numTrue == 0) {
            paths.add(path.toString());
            return;
        }
        path.append('1');
        findCodePaths(numTrue - 1, path, paths, used, limit);
        path.setCharAt(path.length() - 1, '0');
        findCodePaths(numTrue, path, paths, used, limit);
        path.setLength(path.length() - 1);
    }

    private boolean angelicCheck(Program p, int exampleNum) {
        BitStringTrie used = new BitStringTrie();
        for (int numTrue = 0; used.size() < Settings.NUM_ANGELIC_CODE_PATHS; numTrue++) {
            TimeLogger.start("FrAngel.findCodePaths()");
            List<String> paths = new ArrayList<String>();
            findCodePaths(numTrue, new StringBuilder(), paths, used, Settings.NUM_ANGELIC_CODE_PATHS - used.size());
            if (paths.isEmpty()) {
                TimeLogger.stop("FrAngel.findCodePaths()");
                return false; // Exhausted all paths
            }
            TimeLogger.stop("FrAngel.findCodePaths()");
            for (String path : paths) {
                EvaluationInfo info = Interpreter.runProgram(p, task.getExample(exampleNum), path);
                used.add(info.getActualCodePath());
                if (Settings.VERBOSE > 3)
                    System.out.println("  path: " + path + ", actual: " + info.getActualCodePath() + ", success: " + info.isSuccess());
                if (info.isSuccess())
                    return true;
                if (info.isSlow())
                    return false;
            }
        }
        return false;
    }

    private BitSet evaluateAngelic(Program p) {
        BitSet passed = new BitSet();
        int numExamples = task.numExamples();
        int failedCount = 0;
        for (int i = 0; i < numExamples; i++) {
            if (angelicCheck(p, i)) {
                passed.set(i);
            } else {
                failedCount++;
                if (failedCount / (double) numExamples > 1 - Settings.FRACTION_SMALL)
                    return null;
            }
        }
        return passed;
    }

    private BitSet evaluateAngelic(Program p, BitSet passed) {
        BitSet newPassed = new BitSet();
        int numExamples = task.numExamples();

        // Must pass all that were previously passed (minus a few)
        int failedCount = 0;
        for (int i = passed.nextSetBit(0); i >= 0; i = passed.nextSetBit(i + 1)) {
            if (angelicCheck(p, i)) {
                newPassed.set(i);
            } else {
                failedCount++;
                if (failedCount > Settings.RESOLVE_CONDITION_STRICTNESS)
                    return null;
            }
        }

        for (int i = passed.nextClearBit(0); i >= 0 && i < numExamples; i = passed.nextClearBit(i + 1))
            if (angelicCheck(p, i))
                newPassed.set(i);

        if (newPassed.cardinality() / (double) numExamples < Settings.FRACTION_SMALL)
            return null;

        return newPassed;
    }

    private BitSet evaluateNoAngelic(Program p) {
        BitSet passed = new BitSet();
        int numExamples = task.numExamples();
        for (int i = 0; i < numExamples; i++) {
            EvaluationInfo result = Interpreter.runProgram(p, task.getExample(i), null);
            if (result.isSuccess())
                passed.set(i);
            else if (!Settings.MINE_FRAGMENTS || result.isSlow())
                return passed; // Break early if we don't need to check all examples
        }
        return passed;
    }

    private void setCondition(Statement statement, Expression condition) {
        if (statement instanceof IfStatement)
            ((IfStatement) statement).setCondition(condition);
        else
            ((ForLoop) statement).setCondition(condition);
    }

    private BitSet resolveSingleCondition(Program p, AngelicStatementInfo info, BitSet passed, long timeout) {
        long start = System.nanoTime();
        HashSet<String> used = new HashSet<String>();

        Statement angelicStatement = info.getStatement();

        Expression rememberedCondition = null;
        if (angelicStatement instanceof IfStatement)
            rememberedCondition = ((IfStatement) angelicStatement).getRememberedCondition();
        else
            rememberedCondition = ((ForLoop) angelicStatement).getRememberedCondition();
        if (rememberedCondition != null) {
            setCondition(angelicStatement, rememberedCondition);
            TimeLogger.stop("FrAngel.resolveConditions()"); // Don't overlap timing with program execution
            BitSet newPassed = evaluateAngelic(p, passed);
            TimeLogger.start("FrAngel.resolveConditions()");

            if (newPassed != null) {
                if (Settings.VERBOSE > 1) {
                    System.out.printf("Resolved with remembered condition: %s\n", rememberedCondition.toJava());
                    System.out.println("Now passes: " + newPassed);
                }
                return newPassed;
            }
        }

        for (String var : info.getInScopeVars())
            p.addToScope(var);

        boolean triedTrue = false;
        boolean triedFalse = false;
        int i;
        for (i = 0; !Utils.timeout(timeout); i++) {
            Expression condition = p.getExpressionGenerator().genAnyExp(Utils.randInt(1, Settings.MAX_RESOLVE_COND_SIZE), boolean.class, false);

            if (condition instanceof LiteralExpression) {
                boolean literal = (Boolean) ((LiteralExpression) condition).getLiteral();
                if (literal) {
                    if (triedTrue)
                        continue;
                    triedTrue = true;
                } else {
                    if (triedFalse)
                        continue;
                    triedFalse = true;
                }
            } else if (!ProgramUtils.containsVar(condition)) {
                continue;
            }

            StringBuilder sb = new StringBuilder();
            condition.encode(sb);
            String encoding = sb.toString();
            if (used.contains(encoding))
                continue;
            used.add(encoding);

            setCondition(angelicStatement, condition);

            TimeLogger.stop("FrAngel.resolveConditions()"); // Don't overlap timing with program execution
            BitSet newPassed = evaluateAngelic(p, passed);
            TimeLogger.start("FrAngel.resolveConditions()");

            if (newPassed != null) {
                if (Settings.VERBOSE > 1) {
                    System.out.printf("Resolved a angelic condition (%d tries, %.3f sec): %s\n", i+1, Utils.timeSince(start), condition.toJava());
                    System.out.println("Now passes: " + newPassed);
                }
                for (String var : info.getInScopeVars())
                    p.removeFromScope(var);
                return newPassed;
            }
        }
        if (Settings.VERBOSE > 1)
            System.out.printf("Could not resolve angelic condition (%d tries, %.3f sec)\n", i+1, Utils.timeSince(start));
        setCondition(angelicStatement, null);
        for (String var : info.getInScopeVars())
            p.removeFromScope(var);
        return null;
    }

    private static class AngelicStatementInfo implements Comparable<AngelicStatementInfo> {
        private Statement statement;
        private int depth;
        private boolean isLoop;
        private Set<String> inScopeVars;
        AngelicStatementInfo(Statement s, int depth, Set<String> inScopeVars) {
            statement = s;
            this.depth = depth;
            isLoop = s instanceof ForLoop;
            this.inScopeVars = new HashSet<>(inScopeVars);
        }
        Statement getStatement() {
            return statement;
        }
        Set<String> getInScopeVars() {
            return inScopeVars;
        }
        @Override
        public int compareTo(AngelicStatementInfo o) {
            if (depth == o.depth) {
                if (isLoop && !o.isLoop)
                    return -1;
                if (!isLoop && o.isLoop)
                    return 1;
                return 0;
            }
            return o.depth - depth;
        }
    }

    private void findAngelic(List<Statement> statements, int depth, Set<String> inScopeVars, List<AngelicStatementInfo> list) {
        for (Statement s : statements) {
            if (s instanceof ForLoop) {
                ForLoop f = (ForLoop) s;
                inScopeVars.add(f.getVarName());
                if (f.isAngelic())
                    list.add(new AngelicStatementInfo(f, depth, inScopeVars));
                findAngelic(f.getBody(), depth + 1, inScopeVars, list);
                inScopeVars.remove(f.getVarName());
            } else if (s instanceof IfStatement) {
                IfStatement i = (IfStatement) s;
                if (i.isAngelic())
                    list.add(new AngelicStatementInfo(i, depth, inScopeVars));
                findAngelic(i.getBody(), depth + 1, inScopeVars, list);
            } else if (s instanceof ForEachLoop) {
                ForEachLoop f = (ForEachLoop) s;
                inScopeVars.add(f.getVarName());
                findAngelic(f.getBody(), depth + 1, inScopeVars, list);
                inScopeVars.remove(f.getVarName());
            }
        }
    }

    private List<AngelicStatementInfo> findAngelic(Program angelicProgram) {
        List<AngelicStatementInfo> list = new ArrayList<AngelicStatementInfo>();
        findAngelic(angelicProgram.getStatements(), 0, new HashSet<String>(), list);
        Collections.sort(list);
        return list;
    }

    private BitSet resolveConditions(Program p, BitSet passed, long timeout) {
        TimeLogger.start("FrAngel.resolveConditions()");
        if (Settings.VERBOSE > 1)
            System.out.println("Resolving angelic conditions for angelic program:\n" + p.toJava() + "\nPasses: " + passed);

        BitSet copy = (BitSet) passed.clone();
        double secSinceLast = (System.nanoTime() - lastResolveConditions) / 1.0e9;
        double singleTimeout = Math.max(Settings.MIN_RESOLVE_CONDITIONS_SEC, Math.min(2 * secSinceLast, Settings.MAX_RESOLVE_CONDITIONS_SEC));
        if (Settings.VERBOSE > 1)
            System.out.println("resolveSingleCondition timeout: " + singleTimeout + " sec");

        List<AngelicStatementInfo> list = findAngelic(p);

        for (AngelicStatementInfo info : list) {
            passed = resolveSingleCondition(p, info, passed, Math.min(timeout, Utils.getTimeout(singleTimeout)));
            if (passed == null)
                break;
        }

        if (passed == null && list.size() > 1) {
            if (Settings.VERBOSE > 1)
                System.out.println("Could not resolve conditions inside-first, trying outside-first");
            for (AngelicStatementInfo info : list)
                setCondition(info.getStatement(), null);
            passed = copy;
            list = findAngelic(p);
            Collections.reverse(list);
            for (AngelicStatementInfo info : list) {
                passed = resolveSingleCondition(p, info, passed, Math.min(timeout, Utils.getTimeout(singleTimeout / 2)));
                if (passed == null)
                    break;
            }
        }

        lastResolveConditions = System.nanoTime();
        TimeLogger.stop("FrAngel.resolveConditions()");
        return passed;
    }

    private BitSet mineFragments(Program p, BitSet passed) {
        TimeLogger.start("Cleaner.quickCleanOnce()");
        Cleaner cleaner = new Cleaner(p, task, passed);
        cleaner.quickCleanOnce();
        ProgramUtils.resetIndents(p);
        TimeLogger.stop("Cleaner.quickCleanOnce()");

        passed = evaluateNoAngelic(p);

        TimeLogger.start("FrAngel.mineFragments()");
        // Keep if no simpler program passes (at least) the same examples
        boolean keep = true;
        for (Map.Entry<BitSet, Program> entry : fragmentPrograms.entrySet()) {
            BitSet otherPassed = entry.getKey();
            if (otherPassed.cardinality() < passed.cardinality())
                continue;

            BitSet copy = (BitSet) otherPassed.clone();
            copy.or(passed);
            if (copy.equals(otherPassed)) { // other program passes (at least) the same examples
                Program otherProgram = entry.getValue();
                int curSize = ProgramUtils.size(p);
                int otherSize = ProgramUtils.size(otherProgram);

                if (otherSize < curSize || (otherSize == curSize && otherProgram.toJava().length() <= p.toJava().length())) {
                    keep = false; // other program is simpler
                    break;
                }
            }
        }
        if (keep) {
            fragmentPrograms.put(passed, p);
            if (Settings.VERBOSE > 0) {
                String passedStr = "";
                String sep = "";
                for (int i = passed.nextSetBit(0); i >= 0; i = passed.nextSetBit(i+1)) {
                    Example example = task.getExample(i);
                    passedStr += sep + example.getNameWithIndex();
                    sep = ", ";
                }
                System.out.println("\nSimplest program so far for " + passedStr + "\n" + p.toJava());
            }

            // Find strict subsets of passed
            for (BitSet otherPassed : new HashSet<BitSet>(fragmentPrograms.keySet())) {
                if (otherPassed.cardinality() >= passed.cardinality())
                    continue;
                BitSet copy = (BitSet) passed.clone();
                copy.or(otherPassed);
                if (copy.equals(passed) && ProgramUtils.size(fragmentPrograms.get(otherPassed)) >= ProgramUtils.size(p))
                    fragmentPrograms.remove(otherPassed);
            }
            TimeLogger.stop("FrAngel.mineFragments()");
            reloadFragments();
        } else {
            TimeLogger.stop("FrAngel.mineFragments()");
        }
        return passed;
    }

    private void reloadFragments() {
        TimeLogger.start("FrAngel.reloadFragments()");
        expressionFragments.clear();
        statementFragments.clear();
        List<Expression> expressions = new ArrayList<Expression>();
        List<Statement> statements = new ArrayList<Statement>();

        for (BitSet passed : fragmentPrograms.keySet()) {
            Program p = fragmentPrograms.get(passed);
            if (Settings.VERBOSE > 2)
                System.out.println("\nProgram for examples (0-indexed): " + passed + "\n" + p.toJava());
            ProgramUtils.getFragments(p, expressions, statements);
        }
        Set<String> set = new HashSet<String>();
        for (Expression e : expressions) {
            StringBuilder sb = new StringBuilder();
            e.encode(sb);
            String encoding = sb.toString();
            if (!set.contains(encoding)) {
                set.add(encoding);
                Class<?> type = e.getType();
                if (!expressionFragments.containsKey(type))
                    expressionFragments.put(type, new ArrayList<Expression>());
                expressionFragments.get(type).add(e);
            }
        }
        set.clear();
        for (Statement s : statements) {
            StringBuilder sb = new StringBuilder();
            s.encode(sb);
            String encoding = sb.toString();
            if (!set.contains(encoding)) {
                set.add(encoding);
                statementFragments.add(s);
            }
        }
        TimeLogger.stop("FrAngel.reloadFragments()");
    }

    public SynthesisTask getSynthesisTask() {
        return task;
    }
    public Map<BitSet, Program> getFragmentPrograms() {
        return fragmentPrograms;
    }
    public Map<Class<?>, List<Expression>> getExpressionFragments() {
        return expressionFragments;
    }
    public List<Statement> getStatementFragments() {
        return statementFragments;
    }
    public int getGenCount() {
        return genCount;
    }
    public int getRunCount() {
        return runCount;
    }
    public int getGenAngelicCount() {
        return genAngelicCount;
    }
    public int getRunAngelicCount() {
        return runAngelicCount;
    }
    public int getGenNoAngelicCount() {
        return genNoAngelicCount;
    }
    public int getRunNoAngelicCount() {
        return runNoAngelicCount;
    }
}
