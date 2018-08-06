package frangel.interpreter;

import java.awt.Shape;
import java.awt.geom.*;
import java.lang.reflect.Array;
import java.util.*;

import frangel.Example;
import frangel.Settings;
import frangel.model.FunctionData;
import frangel.model.Program;
import frangel.model.expression.*;
import frangel.model.expression.OpExpression.Op;
import frangel.model.statement.*;
import frangel.utils.TimeLogger;

public class Interpreter {
    private static final double SLOW_CUTOFF = 1.0e9;

    public static EvaluationInfo runProgram(Program program, Example example, String angelicCodePath) {
        return runProgram(program, example, angelicCodePath, true);
    }

    // Cleaner sets logTiming to false
    public static EvaluationInfo runProgram(Program program, Example example, String angelicCodePath, boolean logTiming) {
        if (logTiming)
            TimeLogger.start("Interpreter.runProgram()");
        Object[] input = example.getInputs();

        long start = System.nanoTime();
        EvaluationInfo info = evaluate(program, input, angelicCodePath, start);
        long time = System.nanoTime() - start;
        if (time > SLOW_CUTOFF) {
            System.err.println("Interpreter took " + (time/1.0e9) + " sec to evaluate (angelicCodePath = "
                    + angelicCodePath + "):\n" + program.toJava());
            info.setSlow(true);
        }

        boolean success = true;

        if (info.isError())
            success = false;

        try {
            Object actualOutput = info.getReturnValue();
            if (success && program.returns() && !example.checkOutput(actualOutput))
                success = false;
            if (success && !example.checkModifiedInputs(input))
                success = false;
        } catch (StackOverflowError e) {
            System.err.println("StackOverflowError while comparing results for program:\n" + program.toJava());
            // Don't print stack trace!
            success = false;
        } catch (Exception e) {
            // Sometimes customEquals() will throw an error for weird inputs
            System.err.println("Exception while comparing results in runProgram (angelicCodePath = " + angelicCodePath + "), program:\n" + program.toJava());
            e.printStackTrace();
            success = false;
        }

        info.setSuccess(success);
        if (logTiming)
            TimeLogger.stop("Interpreter.runProgram()");
        return info;
    }

    private static EvaluationInfo evaluate(Program program, Object[] arguments, String angelicCodePath, long start) {
        EvaluationInfo info = new EvaluationInfo(angelicCodePath);
        Environment env = new Environment();
        for (int i = 0; i < program.getArgNames().length; i++)
            env.set(program.getArgNames()[i], arguments[i]);

        boolean error = false;
        try {
            for (Map.Entry<String, Expression> pair : program.getLocalVars().entrySet())
                env.set(pair.getKey(), evaluateExpression(pair.getValue(), null));
            for (String var : program.getLoopVars())
                env.set(var, 0);

            evaluateBlock(program.getStatements(), env, info, start);
            if (program.returns())
                info.setReturnValue(evaluateExpression(program.getReturnVal(), env));
        } catch (EvaluationException e) {
            // Something bad happened
            error = true;
            System.err.println("Program had error:\n" + program.toJava());
            e.printStackTrace();
        } catch (StackOverflowError e) {
            error = true;
            System.err.println("Program caused StackOverflowError:\n" + program.toJava());
            // Don't print stack trace!
        } catch (Exception e) {
            // Ignore all other exceptions
            error = true;
        }
        info.setError(error);
        return info;
    }

    private static void evaluateBlock(List<Statement> list, Environment env, EvaluationInfo info, long start) throws Exception {
        for (Statement s : list)
            evaluateStatement(s, env, info, start);
    }

    private static void evaluateStatement(Statement s, Environment env, EvaluationInfo info, long start) throws Exception {
        if (s instanceof VarAssignment)
            evaluateVarAssignment((VarAssignment) s, env);
        else if (s instanceof FuncStatement)
            evaluateFuncStatement((FuncStatement) s, env);
        else if (s instanceof IfStatement)
            evaluateIfStatement((IfStatement) s, env, info, start);
        else if (s instanceof ForLoop)
            evaluateForLoop((ForLoop) s, env, info, start);
        else if (s instanceof ForEachLoop)
            evaluateForEachLoop((ForEachLoop) s, env, info, start);
        else
            throw new EvaluationException("evaluateStatement, unknown statement class: " + s.getClass());
    }

    private static void evaluateVarAssignment(VarAssignment s, Environment env) throws Exception {
        Object value = evaluateExpression(s.getValue(), env);
        env.set(s.getVar().getName(), value);
    }

    private static void evaluateFuncStatement(FuncStatement s, Environment env) throws Exception {
        evaluateFuncExp(s.getFunc(), env);
    }

    private static void evaluateIfStatement(IfStatement s, Environment env, EvaluationInfo info, long start) throws Exception {
        boolean condition;
        if (s.isAngelic()) {
            if (!info.hasAngelicCodePath())
                throw new EvaluationException("Evaluating angelic if statement, but no angelic code path provided");
            condition = info.getNextAngelicConditionValue();
            info.logActualCodePath(condition);
        } else {
            condition = (boolean) evaluateExpression(s.getCondition(), env);
        }
        if (condition)
            evaluateBlock(s.getBody(), env, info, start);
    }

    private static void evaluateForLoop(ForLoop s, Environment env, EvaluationInfo info, long start) throws Exception {
        int iterations = 0;
        env.resetLoopCounter(s.getVarName());
        while (true) {
            if (info.incLoopIterations() >= Settings.MAX_LOOP_ITERATIONS || iterations++ >= Settings.MAX_SINGLE_LOOP_ITERATIONS || env.isTooLarge())
                throw new IncompleteRunException();
            if (System.nanoTime() - start > SLOW_CUTOFF)
                throw new IncompleteRunException();

            boolean condition;
            if (s.isAngelic()) {
                if (!info.hasAngelicCodePath())
                    throw new EvaluationException("Evaluating angelic for loop, but no angelic code path provided");
                condition = info.getNextAngelicConditionValue();
                info.logActualCodePath(condition);
            } else {
                condition = (boolean) evaluateExpression(s.getCondition(), env);
            }

            if (!condition)
                break;
            evaluateBlock(s.getBody(), env, info, start);
            env.incLoopCounter(s.getVarName());
        }
    }

    private static void evaluateForEachLoop(ForEachLoop s, Environment env, EvaluationInfo info, long start) throws Exception {
        Object containerObj = evaluateExpression(s.getContainer(), env);
        String elemName = s.getVarName();
        if (env.contains(elemName))
            throw new IncompleteRunException("For-each loop element variable already in scope");
        boolean isArray = s.getContainer().getType().isArray();
        int i = 0;
        int len = isArray ? Array.getLength(containerObj) : 0;
        Iterator<?> it = isArray ? null : ((Iterable<?>) containerObj).iterator();
        int iterations = 0;
        while (true) {
            if (info.incLoopIterations() >= Settings.MAX_LOOP_ITERATIONS || iterations++ >= Settings.MAX_SINGLE_LOOP_ITERATIONS || env.isTooLarge())
                throw new IncompleteRunException();
            if (System.nanoTime() - start > SLOW_CUTOFF)
                throw new IncompleteRunException();

            if (isArray) {
                if (i >= len)
                    break;
                env.set(elemName, Array.get(containerObj, i));
                i++;
            } else {
                if (it.hasNext())
                    env.set(elemName, it.next());
                else
                    break;
            }
            evaluateBlock(s.getBody(), env, info, start);
        }
        env.remove(elemName);
    }

    private static Object evaluateExpression(Expression exp, Environment env) throws Exception {
        if (exp instanceof LiteralExpression)
            return evaluateLiteral((LiteralExpression) exp);
        else if (exp instanceof VarExpression)
            return evaluateVariable((VarExpression) exp, env);
        else if (exp instanceof OpExpression)
            return evaluateOperator((OpExpression) exp, env);
        else if (exp instanceof FuncExpression)
            return evaluateFuncExp((FuncExpression) exp, env);
        else
            throw new EvaluationException("evaluateExpression, unknown expression class: " + exp.getClass());
    }

    private static Object evaluateLiteral(LiteralExpression exp) {
        return exp.getLiteral();
    }

    private static Object evaluateVariable(VarExpression exp, Environment env) throws Exception {
        return env.get(exp.getName());
    }

    private static Object evaluateOperator(OpExpression exp, Environment env) throws Exception {
        Op op = exp.getOp();
        Object left = null, right = null;
        Class<?> leftType = null, rightType = null;
        if (exp.getLeft() != null) {
            left = evaluateExpression(exp.getLeft(), env);
            leftType = exp.getLeft().getType();
        }
        if (exp.getRight() != null) {
            right = evaluateExpression(exp.getRight(), env);
            rightType = exp.getRight().getType();
        }

        switch (op) {
        case AND:
            return ((boolean) left) && ((boolean) right);
        case OR:
            return ((boolean) left) || ((boolean) right);
        case NOT:
            return !((boolean) right);
        case EQUALS:
            if (leftType.isPrimitive())
                return left.equals(right); // .equals on wrapper types should emulate == for primitive types
            else
                return left == right;
        case LEQ:
            if (leftType.equals(int.class))
                return ((int) left) <= ((int) right);
            else if (leftType.equals(double.class))
                return ((double) left) <= ((double) right);
            else
                throw new EvaluationException("evaluateOperator, case LEQ, left type: " + leftType);
        case LESS:
            if (leftType.equals(int.class))
                return ((int) left) < ((int) right);
            else if (leftType.equals(double.class))
                return ((double) left) < ((double) right);
            else
                throw new EvaluationException("evaluateOperator, case LESS, left type: " + leftType);
        case PLUS:
            if (leftType.equals(int.class)) {
                return ((int) left) + ((int) right);
            } else if (leftType.equals(double.class)) {
                return ((double) left) + ((double) right);
            } else if (leftType.equals(String.class) || rightType.equals(String.class) || leftType.equals(Object.class) || rightType.equals(Object.class)) {
                if (left == null && right == null && exp.getLeft() instanceof LiteralExpression && exp.getRight() instanceof LiteralExpression)
                    throw new IncompleteRunException("Cannot add two null literals");
                return "" + left + right;
            } else {
                throw new EvaluationException("evaluateOperator, case PLUS, left type " + leftType);
            }
        case MINUS:
            if (leftType.equals(int.class))
                return ((int) left) - ((int) right);
            else if (leftType.equals(double.class))
                return ((double) left) - ((double) right);
            else
                throw new EvaluationException("evaluateOperator, case MINUS, left type: " + leftType);
        case TIMES:
            if (leftType.equals(int.class))
                return ((int) left) * ((int) right);
            else if (leftType.equals(double.class))
                return ((double) left) * ((double) right);
            else
                throw new EvaluationException("evaluateOperator, case TIMES, left type: " + leftType);
        case DIV:
            if (leftType.equals(int.class))
                return ((int) left) / ((int) right);
            else if (leftType.equals(double.class))
                return ((double) left) / ((double) right);
            else
                throw new EvaluationException("evaluateOperator, case DIV, left type: " + leftType);
        case MOD:
            if (leftType.equals(int.class))
                return ((int) left) % ((int) right);
            else
                throw new EvaluationException("evaluateOperator, case MOD, left type: " + leftType);
        default:
            throw new EvaluationException("evaluateOperator, unknown op case: " + op);
        }
    }

    private static Object evaluateFuncExp(FuncExpression exp, Environment env) throws Exception {
        FunctionData data = exp.getData();

        Object[] args = new Object[exp.getArgs().length];
        for (int i = 0; i < args.length; i++)
            args[i] = evaluateExpression(exp.getArgs()[i], env);

        Object callerObj = null;
        if (!data.isStatic()) {
            callerObj = evaluateExpression(exp.getCalledFrom(), env);
            if (callerObj == null)
                throw new NullPointerException();
        }

        Object returnVal;
        switch (data.getKind()) {
        case METHOD:
            returnVal = data.getMethod().invoke(callerObj, args);
            if (data.returnsGeneric() && returnVal != null && !data.getReturnType().isAssignableFrom(returnVal.getClass()))
                throw new IncompleteRunException(data.getName() + " returned " + returnVal.getClass() + ", expected " + data.getReturnType());
            checkMemory(returnVal);
            checkMemory(callerObj);
            return returnVal;
        case CONSTRUCTOR:
            returnVal = data.getConstructor().newInstance(args);
            checkMemory(returnVal);
            return returnVal;
        case FIELD:
            returnVal = data.getField().get(callerObj);
            if (data.returnsGeneric() && returnVal != null && !data.getReturnType().isAssignableFrom(returnVal.getClass()))
                throw new IncompleteRunException(data.getName() + " returned " + returnVal.getClass() + ", expected " + data.getReturnType());
            return returnVal;
        case ARR_GET:
            return Array.get(args[0], (int) args[1]);
        case ARR_SET:
            Array.set(args[0], (int) args[1], args[2]);
            return null;
        case ARR_LEN:
            return Array.getLength(args[0]);
        default:
            throw new EvaluationException("evaluateFuncExp, unknown FunctionData.Kind: " + data.getKind());
        }
    }

    private static boolean validPath(Path2D path) {
        PathIterator it = path.getPathIterator(null);
        double[] coords = new double[6];
        while (!it.isDone()) {
            it.currentSegment(coords);
            for (double d : coords)
                if (!Double.isFinite(d))
                    return false;
            it.next();
        }
        return true;
    }

    @SuppressWarnings("rawtypes")
    private static void checkMemory(Object o) throws IncompleteRunException {
        if (o != null) {
            Class<?> oCls = o.getClass();
            if (CharSequence.class.isAssignableFrom(oCls) && ((CharSequence) o).length() > Settings.MAX_STRING_LEN)
                throw new IncompleteRunException("checkMemory: CharSequence too long");
            if (Collection.class.isAssignableFrom(oCls) && ((Collection) o).size() > Settings.MAX_COLLECTION_SIZE)
                throw new IncompleteRunException("checkMemory: Collection too large");
            if (Map.class.isAssignableFrom(oCls) && ((Map) o).size() > Settings.MAX_COLLECTION_SIZE)
                throw new IncompleteRunException("checkMemory: Collection too large");
            if (oCls.isArray() && Array.getLength(o) > Settings.MAX_ARRAY_LEN)
                throw new IncompleteRunException("checkMemory: array too long");

            // These can cause the Area(Shape) constructor to hang
            if (Shape.class.isAssignableFrom(oCls)) {
                if (QuadCurve2D.class.isAssignableFrom(oCls)) {
                    QuadCurve2D c = (QuadCurve2D) o;
                    if (!Double.isFinite(c.getX1()) || !Double.isFinite(c.getY1()) ||
                            !Double.isFinite(c.getX2()) || !Double.isFinite(c.getY2()) ||
                            !Double.isFinite(c.getCtrlX()) || !Double.isFinite(c.getCtrlY()))
                        throw new IncompleteRunException("checkMemory: non-finite value in QuadCurve2D");
                } else if (CubicCurve2D.class.isAssignableFrom(oCls)) {
                    CubicCurve2D c = (CubicCurve2D) o;
                    if (!Double.isFinite(c.getX1()) || !Double.isFinite(c.getY1()) ||
                            !Double.isFinite(c.getX2()) || !Double.isFinite(c.getY2()) ||
                            !Double.isFinite(c.getCtrlX1()) || !Double.isFinite(c.getCtrlY1()) ||
                            !Double.isFinite(c.getCtrlX2()) || !Double.isFinite(c.getCtrlY2()))
                        throw new IncompleteRunException("checkMemory: non-finite value in CubicCurve2D");
                } else if (Path2D.class.isAssignableFrom(oCls)) {
                    if (!validPath((Path2D) o))
                        throw new IncompleteRunException("checkMemory: non-finite value in Path2D");
                }
            }

            if (Settings.SYPET_MODE) {
                if (o.getClass().equals(Integer.class) && Math.abs((int) o) > 2000)
                    throw new IncompleteRunException("checkMemory: int too large (SyPet mode)");
                if (o.getClass().isArray() && Array.getLength(o) > 5)
                    throw new IncompleteRunException("checkMemory: array too long (SyPet mode)");
                if (o.getClass().equals(Double.class) && (double) o != 0.0 && Math.abs((double) o) < 1e-3)
                    throw new IncompleteRunException("checkMemory: double too close to 0 (SyPet mode)");
                if (o.getClass().equals(Double.class) && Double.isNaN((double) o))
                    throw new IncompleteRunException("checkMemory: double NaN (SyPet mode)"); // NaN breaks solveAllComplex() for sypet_3_findRoots
            }
        }
    }
}
