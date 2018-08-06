// Returns random Java expressions with a given type.

package frangel.model.generator;

import java.util.*;

import frangel.JavaFunctionLoader;
import frangel.Settings;
import frangel.model.FunctionData;
import frangel.model.Program;
import frangel.model.expression.*;
import frangel.model.expression.OpExpression.Op;
import frangel.utils.ProgramUtils;
import frangel.utils.Utils;

public class ExpressionGenerator {
    public enum ExpCategory { FUNC, OP, LIT, VAR };

    // The Program that owns this ExpressionGenerator. In particular, this Program stores the
    // available variables' names and types.
    private Program program;

    public ExpressionGenerator(Program program) {
        this.program = program;
    }

    public Expression newOrSimilar(Expression exp) {
        return newOrSimilar(exp, ProgramUtils.size(exp) + Settings.SIMILAR_NEW_EXTRA_SIZE);
    }

    public Expression newOrSimilar(Expression exp, int size) {
        if (exp == null)
            return null;
        if (exp.getType().equals(int.class) && (exp instanceof LiteralExpression || exp instanceof VarExpression)
                && Utils.randBoolean(Settings.PROB_REPLACE_WITH_LOOPVAR)) {
            List<String> loopVarsInScope = new ArrayList<String>(program.getLoopVars());
            loopVarsInScope.retainAll(program.getInScope());
            if (!loopVarsInScope.isEmpty())
                return new OpExpression(Op.PLUS, new VarExpression(Utils.randElement(loopVarsInScope), int.class), exp);
        }
        if (Utils.randBoolean(Settings.GEN_SIMILAR_PROB_NEW)) {
            return genAnyExp(size, exp.getType(), true); // disable fragments
        } else {
            return genSimilarExpression(exp);
        }
    }

    public Expression genSimilarExpression(Expression exp) {
        if (exp instanceof LiteralExpression || exp instanceof VarExpression) {
            return exp.clone();
        } else if (exp instanceof OpExpression) {
            OpExpression o = (OpExpression) exp;
            Expression left = null;
            if (o.getLeft() != null) {
                left = newOrSimilar(o.getLeft());
                if (left == null)
                    return null;
            }
            Expression right = newOrSimilar(o.getRight());
            if (right == null)
                return null;
            return new OpExpression(o.getOp(), left, right);
        } else if (exp instanceof FuncExpression) {
            FuncExpression f = (FuncExpression) exp;
            Expression[] args = new Expression[f.getArgs().length];
            for (int i = 0; i < args.length; i++) {
                args[i] = newOrSimilar(f.getArgs()[i]);
                if (args[i] == null)
                    return null;
            }
            Expression calledFrom = null;
            if (f.getCalledFrom() != null) {
                calledFrom = newOrSimilar(f.getCalledFrom());
                if (calledFrom == null)
                    return null;
            }
            FuncExpression e = new FuncExpression(args, calledFrom, f.getData());
            return e;
        } else {
            System.err.println("Unknown expression class in genSimilarExpression");
            return null;
        }
    }

    public Expression genAnyExp(int size, Class<?> type, boolean disableFragments) {
        ExpCategory[] categories =
                Settings.SYPET_MODE ? new ExpCategory[] {ExpCategory.FUNC, ExpCategory.VAR} :
                    new ExpCategory[] {ExpCategory.FUNC, ExpCategory.OP, ExpCategory.VAR, ExpCategory.LIT};
                return genExp(size, type, categories, disableFragments);
    }

    // Generates a random expression using the given categories, with the given maximum size and return type.
    // If this is not possible, return null.
    public Expression genExp(int size, Class<?> type, ExpCategory[] categories, boolean disableFragments) {
        if (size <= 0)
            return null;

        if (!disableFragments && program.useFragments() && program.getExpressionFragments() != null && Utils.randBoolean()) {
            List<Expression> list = program.getExpressionFragments().get(type);
            if (list != null && !list.isEmpty()) {
                Expression randElement = Utils.randElement(list).clone();
                ProgramUtils.makeVarsCompatible(randElement, new HashMap<String, String>(), program);
                if (Utils.randBoolean())
                    return randElement;
                Expression similar = genSimilarExpression(randElement);
                if (similar != null)
                    return similar;
            }
        }

        Utils.shuffle(categories);
        for (ExpCategory category : categories) {
            Expression exp = null;
            switch (category) {
            case FUNC:
                exp = genFuncExp(size, type, disableFragments); break;
            case OP:
                exp = genOpExp(size, type, disableFragments); break;
            case LIT:
                exp = genLitExp(size, type); break;
            case VAR:
                exp = genVarExp(size, type); break;
            }
            if (exp != null)
                return exp;
        }
        return null;
    }

    public FuncExpression genFuncExp(int size, Class<?> type, boolean disableFragments) {
        List<FunctionData> dataList = null;
        boolean calledFromVar = false;
        if (type.equals(void.class)) {
            int choice = Utils.randInt(3);
            if (choice == 0) {
                // Choose a void-returning method callable by some variable in scope
                Set<Class<?>> callableClasses = new HashSet<>();
                for (Class<?> cls : program.getArgTypes())
                    if (!cls.isPrimitive() && !cls.isArray())
                        callableClasses.add(cls);
                for (String localVar : program.getLocalVars().keySet()) {
                    Class<?> cls = program.getVariables().get(localVar);
                    if (!cls.isPrimitive() && !cls.isArray())
                        callableClasses.add(cls);
                }
                dataList = JavaFunctionLoader.getCallableVoidMethods(callableClasses);
                if (dataList != null && !dataList.isEmpty())
                    calledFromVar = true;
            } else if (choice == 1) {
                dataList = JavaFunctionLoader.getFunctionsByReturnType(void.class); // Void-returning methods
            }
            if (dataList == null || dataList.isEmpty())
                dataList = JavaFunctionLoader.getAllMethods(); // No return type required, but can still use a function that returns something
        } else {
            dataList = JavaFunctionLoader.getFunctionsByReturnType(type);
        }
        if (dataList == null || dataList.isEmpty())
            return null;

        ExpCategory[] calledFrom;
        if (calledFromVar) {
            calledFrom = new ExpCategory[] {ExpCategory.VAR};
        } else {
            calledFrom = Settings.SYPET_MODE ? new ExpCategory[] {ExpCategory.FUNC, ExpCategory.VAR} :
                new ExpCategory[] {ExpCategory.FUNC, ExpCategory.VAR, ExpCategory.LIT};
        }

        for (int i = 0; i < Settings.GEN_FUNCTION_TRIES; i++) {
            FunctionData data = Utils.randElement(dataList);

            int numPartitions = data.getArgTypes().length;
            if (!data.isStatic())
                numPartitions++;
            int[] sizes = null;
            if (numPartitions > 0) {
                sizes = Utils.randPartition(size - 1, numPartitions, Settings.MIN_EXP_SIZE);
                if (sizes == null)
                    continue;
            }

            Expression[] args = new Expression[data.getArgTypes().length];
            boolean shouldContinue = false;
            for (int j = 0; j < data.getArgTypes().length; j++) {
                args[j] = genAnyExp(sizes[j], data.getArgTypes()[j], disableFragments);
                if (args[j] == null) {
                    shouldContinue = true;
                    break;
                }
            }
            if (shouldContinue)
                continue;

            Expression callerExp = null;
            if (!data.isStatic()) {
                callerExp = genExp(sizes[sizes.length - 1], data.getCallerClass(), calledFrom, disableFragments);
                if (callerExp == null)
                    continue;
                if (callerExp instanceof LiteralExpression && ((LiteralExpression) callerExp).getLiteral() == null)
                    continue;
            }
            FuncExpression exp = new FuncExpression(args, callerExp, data);
            return exp;
        }
        return null;
    }

    public OpExpression genOpExp(int size, Class<?> type, boolean disableFragments) {
        return genOpExp(size, type, null, disableFragments);
    }

    public OpExpression genOpExp(int size, Class<?> type, Op op, boolean disableFragments) {
        int[] sizes = Utils.randPartition(size - 1, 2, Settings.MIN_EXP_SIZE);
        if (sizes == null)
            return null;

        Class<?> leftType = null, rightType = null; // Set these to force a particular type, keep null to use choices
        Class<?>[] leftChoices = null, rightChoices = null; // List of choices to use if no single type is required
        boolean operandsSameType = false; // Use this to force the two operand types to be the same out of multiple choices

        if (type.equals(String.class)) {
            // Allow String + (String | char) and vice versa
            op = Op.PLUS;
            if (Utils.randBoolean()) {
                leftChoices = new Class<?>[] {String.class, char.class};
                rightType = String.class;
            } else {
                leftType = String.class;
                rightChoices = new Class<?>[] {String.class, char.class};
            }
        } else if (type.equals(boolean.class)) {
            if (op == null)
                op = Utils.randElement(new Op[] {Op.EQUALS, Op.AND, Op.OR, Op.LESS, Op.LEQ, Op.NOT});
            operandsSameType = true;
            if (op == Op.EQUALS)
                leftChoices = new Class<?>[] {int.class, double.class, boolean.class, char.class, Object.class};
                else if (op == Op.LESS || op == Op.LEQ)
                    leftChoices = new Class<?>[] {int.class, double.class};
                    else
                        leftType = rightType = boolean.class;
        } else if (type.equals(int.class)) {
            if (op == null)
                op = Utils.randElement(new Op[] {Op.PLUS, Op.MINUS, Op.TIMES, Op.DIV, Op.MOD});
            leftType = rightType = type;
        } else if (type.equals(double.class)) {
            if (op == null)
                op = Utils.randElement(new Op[] {Op.PLUS, Op.MINUS, Op.TIMES, Op.DIV});
            leftType = rightType = type;
        } else {
            return null;
        }

        Expression left = null, right = null;
        if (leftType == null) {
            // Try choices in random order until one works
            Utils.shuffle(leftChoices);
            for (Class<?> choice : leftChoices) {
                left = genAnyExp(sizes[0], choice, disableFragments);
                if (left != null) {
                    if (operandsSameType)
                        rightType = choice;
                    break;
                }
            }
        } else if (op != Op.NOT) {
            left = genAnyExp(sizes[0], leftType, disableFragments);
        }
        if (left == null && op != Op.NOT)
            return null; // None of the specified types work

        if (rightType == null) {
            Utils.shuffle(rightChoices);
            for (Class<?> choice : rightChoices) {
                right = genAnyExp(sizes[1], choice, disableFragments);
                if (right != null)
                    break;
            }
        } else {
            if ((rightType == int.class || rightType == double.class) && (op == Op.MOD || op == Op.DIV)) {
                for (int i = 0; ; i++) {
                    if (i == 3)
                        return null;
                    right = genAnyExp(sizes[1], rightType, disableFragments);
                    if (right instanceof LiteralExpression) {
                        String java = ((LiteralExpression) right).toJava();
                        if (java.equals("0") || java.equals("0.0"))
                            continue;
                    }
                    break;
                }
            } else if (op == Op.NOT) {
                right = genExp(sizes[1], rightType, new ExpCategory[] {ExpCategory.FUNC, ExpCategory.OP, ExpCategory.VAR}, disableFragments);
            } else {
                right = genAnyExp(sizes[1], rightType, disableFragments);
            }
        }
        if (right == null)
            return null;

        if (op == Op.PLUS) {
            // Don't add (String) null
            if (left instanceof LiteralExpression && ((LiteralExpression) left).getLiteral() == null)
                return null;
            if (right instanceof LiteralExpression && ((LiteralExpression) right).getLiteral() == null)
                return null;
        }

        return new OpExpression(op, left, right);
    }

    public LiteralExpression genLitExp(int size, Class<?> type) {
        List<Object> choices = new ArrayList<Object>();
        Map<Class<?>, List<Object>> literals = program.getSynthesisTask().getLiterals();
        for (Class<?> cls : literals.keySet())
            if (type.isAssignableFrom(cls))
                choices.addAll(literals.get(cls));
        if (choices.size() == 0) {
            if (!type.isPrimitive() && Utils.randBoolean(0.1))
                return new LiteralExpression(null, type); // null as an object literal is very rarely correct
            return null;
        }
        return new LiteralExpression(Utils.randElement(choices), type);
    }

    public VarExpression genVarExp(int size, Class<?> type) {
        List<String> list = new ArrayList<String>();
        for (String var : program.getInScope()) {
            Class<?> varType = program.getVariables().get(var);
            if (Settings.SYPET_MODE && Settings.HARDCODE_POLYMORPHISM && Settings.POLYMORPHISM_MAP.containsKey(type)) {
                if (varType.equals(type) || Settings.POLYMORPHISM_MAP.get(type).contains(varType))
                    list.add(var);
            } else if (type.isAssignableFrom(varType)) {
                list.add(var);
            }
        }
        if (list.isEmpty())
            return null;
        String name = Utils.randElement(list);
        return new VarExpression(name, program.getVariables().get(name));
    }
}
