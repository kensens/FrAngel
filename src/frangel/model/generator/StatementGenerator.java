// Generates Statement objects.

package frangel.model.generator;

import java.util.*;

import frangel.JavaFunctionLoader;
import frangel.Settings;
import frangel.model.FunctionData;
import frangel.model.Program;
import frangel.model.expression.Expression;
import frangel.model.expression.FuncExpression;
import frangel.model.expression.VarExpression;
import frangel.model.generator.ExpressionGenerator.ExpCategory;
import frangel.model.statement.*;
import frangel.utils.ProgramUtils;
import frangel.utils.Utils;

public class StatementGenerator {
    public enum StatementCategory { ASSIGN, FUNC, FOR, FOREACH, IF };

    // The Program that owns this ExpressionGenerator. In particular, this Program stores the
    // available variables' names and types.
    private Program program;

    public StatementGenerator(Program program) {
        this.program = program;
    }

    public Statement newOrSimilar(Statement s, int indent) {
        return newOrSimilar(s, ProgramUtils.size(s) + Settings.SIMILAR_NEW_EXTRA_SIZE, indent);
    }

    public Statement newOrSimilar(Statement s, int size, int indent) {
        if (Utils.randBoolean(Settings.GEN_SIMILAR_PROB_NEW)) {
            return genStatement(size, indent, true); // disable fragments
        } else {
            return genSimilarStatement(s, indent);
        }
    }

    private Statement genSimilarStatement(Statement s, int indent) {
        ExpressionGenerator expGen = program.getExpressionGenerator();
        boolean isAngelic = program.isAngelic();
        if (s instanceof VarAssignment) {
            VarAssignment v = (VarAssignment) s;
            VarExpression var;
            if (Utils.randBoolean(Settings.GEN_SIMILAR_PROB_NEW))
                var = expGen.genVarExp(1, v.getVar().getType());
            else
                var = (VarExpression) expGen.genSimilarExpression(v.getVar());
            Expression value = expGen.newOrSimilar(v.getValue());
            if (var == null || value == null)
                return null;
            return new VarAssignment(var, value, indent);
        } else if (s instanceof FuncStatement) {
            FuncStatement f = (FuncStatement) s;
            FuncExpression fExp = (FuncExpression) expGen.genSimilarExpression(f.getFunc());
            if (fExp == null)
                return null;
            return new FuncStatement(fExp, indent);
        } else if (s instanceof IfStatement) {
            IfStatement i = (IfStatement) s;
            List<Statement> body = new ArrayList<Statement>();
            for (Statement inner : i.getBody()) {
                Statement newInner = newOrSimilar(inner, indent + 1);
                if (newInner != null)
                    body.add(newInner);
            }
            if (isAngelic) {
                IfStatement ret = new IfStatement(null, body, indent);
                ret.rememberCondition(i.getCondition());
                return ret;
            } else {
                Expression newCond = expGen.newOrSimilar(i.getCondition());
                if (newCond == null)
                    return null;
                return new IfStatement(newCond, body, indent);
            }
        } else if (s instanceof ForLoop) {
            ForLoop f = (ForLoop) s;
            List<Statement> body = new ArrayList<Statement>();
            String varName = f.getVarName();
            program.addToScope(varName);
            for (Statement inner : f.getBody()) {
                Statement newInner = newOrSimilar(inner, indent + 1);
                if (newInner != null)
                    body.add(newInner);
            }
            program.removeFromScope(varName);
            if (isAngelic) {
                ForLoop ret = new ForLoop(varName, null, body, indent);
                ret.rememberCondition(f.getCondition());
                return ret;
            } else {
                Expression newCond = expGen.newOrSimilar(f.getCondition());
                if (newCond == null)
                    return null;
                return new ForLoop(varName, newCond, body, indent);
            }
        } else if (s instanceof ForEachLoop) {
            ForEachLoop f = (ForEachLoop) s;
            List<Statement> body = new ArrayList<>();
            String varName = f.getVarName();
            program.addToScope(varName);
            for (Statement inner : f.getBody()) {
                Statement newInner = newOrSimilar(inner, indent + 1);
                if (newInner != null)
                    body.add(newInner);
            }
            program.removeFromScope(varName);
            return new ForEachLoop(f.getVarType(), varName, f.getContainer().clone(), body, indent);
        } else {
            System.err.println("Unknown statement class in genSimilarStatement");
            return null;
        }
    }

    // A block could have zero statements if generating statements always fails
    List<Statement> genBlock(int size, int indent) {
        List<Statement> block = new ArrayList<Statement>();
        int maxStatements = Math.min(size / Settings.MIN_STATEMENT_SIZE, Settings.MAX_BLOCK_SIZE);
        if (maxStatements == 0)
            return block;
        int numStatements = 1 + Utils.randInt(maxStatements);
        size = Math.max(numStatements * Settings.MIN_STATEMENT_SIZE, size); // Increase size if it's below the minimum possible
        int[] sizes = Utils.randPartition(size, numStatements, Settings.MIN_STATEMENT_SIZE);
        for (int i = 0; i < numStatements; i++) {
            boolean disableFragments = Utils.randBoolean();
            Statement s = genStatement(sizes[i], indent, disableFragments);
            if (s != null)
                block.add(s);
        }
        return block;
    }

    public Statement genStatement(int size, int indent, boolean disableFragments) {
        StatementCategory[] categories =
                Settings.SYPET_MODE ? new StatementCategory[] {StatementCategory.ASSIGN, StatementCategory.FUNC} :
                    new StatementCategory[] {StatementCategory.ASSIGN, StatementCategory.FUNC, StatementCategory.FOR, StatementCategory.FOREACH, StatementCategory.IF};
                return genStatement(size, categories, indent, disableFragments);
    }

    public Statement genStatement(int size, StatementCategory[] categories, int indent, boolean disableFragments) {
        if (size <= 0)
            return null;

        List<Statement> list = program.getStatementFragments();
        if (!disableFragments && program.useFragments() && list != null && !list.isEmpty() && Utils.randBoolean()) {
            Statement randElement = Utils.randElement(list).clone();
            ProgramUtils.makeVarsCompatible(randElement, new HashMap<String, String>(), program);
            if (Utils.randBoolean())
                return randElement;
            Statement similar = genSimilarStatement(randElement, indent);
            if (similar != null)
                return similar;
        }

        Utils.shuffle(categories);
        for (StatementCategory category : categories) {
            Statement s = null;
            switch (category) {
            case ASSIGN:
                s = genVarAssignment(size, indent, disableFragments); break;
            case FUNC:
                s = genFuncStatement(size, indent, disableFragments); break;
            case FOR:
                s = genForLoop(size, indent); break;
            case FOREACH:
                s = genForEachLoop(size, indent); break;
            case IF:
                s = genIfStatement(size, indent); break;
            }
            if (s != null)
                return s;
        }
        return null;
    }

    VarAssignment genVarAssignment(int size, int indent, boolean disableFragments) {
        List<String> mutable = new ArrayList<String>(program.getLocalVars().keySet());
        mutable.addAll(program.getArgVars());
        if (mutable.isEmpty())
            return null;
        String name = Utils.randElement(mutable);
        Class<?> type = program.getVariables().get(name);
        Expression value = program.getExpressionGenerator().genAnyExp(size - 2, type, disableFragments);
        if (value == null)
            return null;
        if (value instanceof VarExpression && ((VarExpression)value).getName().equals(name))
            return null;
        return new VarAssignment(new VarExpression(name, type), value, indent);
    }

    FuncStatement genFuncStatement(int size, int indent, boolean disableFragments) {
        FuncExpression func = program.getExpressionGenerator().genFuncExp(size - 1, void.class, disableFragments);
        if (func == null)
            return null;
        return new FuncStatement(func, indent);
    }

    ForLoop genForLoop(int size, int indent) {
        if (size < 1 + Settings.MIN_LOOP_COND_SIZE + Settings.MIN_STATEMENT_SIZE)
            return null;

        // Tell the function about the new loop variable
        String varName = program.getFreshLoopVar();
        program.addLoopVar(varName);
        program.addToScope(varName);

        int conditionSize = 0;
        Expression condition = null;

        if (!program.isAngelic()) {
            int maxCondSize = Math.min(Settings.MAX_LOOP_COND_SIZE, size - 1 - Settings.MIN_STATEMENT_SIZE);
            conditionSize = Utils.randInt(Settings.MIN_LOOP_COND_SIZE, maxCondSize + 1);
            ExpCategory[] noLiteral = new ExpCategory[] {ExpCategory.FUNC, ExpCategory.OP, ExpCategory.VAR};
            condition = program.getExpressionGenerator().genExp(conditionSize, boolean.class, noLiteral, true);
            if (condition == null) {
                program.removeFromScope(varName);
                return null;
            }
        }

        List<Statement> body = genBlock(size - 1 - conditionSize, indent + 1);
        program.removeFromScope(varName);
        if (body.isEmpty())
            return null;

        ForLoop ret = new ForLoop(varName, condition, body, indent);
        return ret;
    }

    private static class Container {
        enum ContainerKind {ARRAY, STRING, LIST, SET, MAP, QUEUE, ITERABLE};
        String containerName;
        ContainerKind kind;
        Class<?> containerType;
        Class<?> elemType;
        Container(String containerName, ContainerKind kind, Class<?> containerType, Class<?> elemType) {
            this.containerName = containerName;
            this.kind = kind;
            this.containerType = containerType;
            this.elemType = elemType;
        }
        Expression getExpression() {
            VarExpression containerExp = new VarExpression(containerName, containerType);
            switch (kind) {
            case ARRAY:
            case LIST:
            case SET:
            case QUEUE:
            case ITERABLE:
                return containerExp;
            case MAP:
                FunctionData data = JavaFunctionLoader.getKeySetData();
                if (data != null)
                    return new FuncExpression(new Expression[0], containerExp, data);
                else
                    System.err.println("null keySetData");
                return null;
            case STRING:
                // TODO: return str.toCharArray() FuncExpression, uncomment below
                break;
            }
            return null;
        }
    }

    ForEachLoop genForEachLoop(int size, int indent) {
        if (size < 1 + Settings.MIN_LOOP_COND_SIZE + Settings.MIN_STATEMENT_SIZE)
            return null;

        List<Container> containers = new ArrayList<>();
        for (String containerName : program.getVariables().keySet()) {
            Class<?> containerType = program.getVariables().get(containerName);
            if (containerType.isArray()) {
                containers.add(new Container(containerName, Container.ContainerKind.ARRAY, containerType, containerType.getComponentType()));
//            } else if (containerType.equals(String.class)) {
//                containers.add(new Container(containerName, Container.ContainerKind.STRING, containerType, char.class));
            } else {
                Container.ContainerKind kind = null;
                if (List.class.isAssignableFrom(containerType))
                    kind = Container.ContainerKind.LIST;
                else if (Set.class.isAssignableFrom(containerType))
                    kind = Container.ContainerKind.SET;
                else if (Map.class.isAssignableFrom(containerType))
                    kind = Container.ContainerKind.MAP;
                else if (Queue.class.isAssignableFrom(containerType))
                    kind = Container.ContainerKind.QUEUE;
                else if (Iterable.class.isAssignableFrom(containerType))
                    kind = Container.ContainerKind.ITERABLE;
                if (kind != null) {
                    Class<?> elemType = Utils.getParameterTypeForClass(containerType, program.getParameterTypeMap());
                    if (elemType != null)
                        containers.add(new Container(containerName, kind, containerType, elemType));
                }
            }
        }
        if (containers.isEmpty())
            return null;
        Container container = Utils.randElement(containers);

        String varName = program.getFreshElemVar();
        program.addElemVar(varName, container.elemType);

        program.addToScope(varName);
        List<Statement> body = genBlock(size - 1, indent + 1);
        program.removeFromScope(varName);

        if (body.isEmpty())
            return null;

        ForEachLoop ret = new ForEachLoop(container.elemType, varName, container.getExpression(), body, indent);
        return ret;
    }

    IfStatement genIfStatement(int size, int indent) {
        if (size < 1 + Settings.MIN_IF_COND_SIZE + Settings.MIN_STATEMENT_SIZE)
            return null;

        int conditionSize = 0;
        Expression condition = null;

        if (!program.isAngelic()) {
            int maxCondSize = Math.min(Settings.MAX_IF_COND_SIZE, size - 1 - Settings.MIN_STATEMENT_SIZE);
            conditionSize = Utils.randInt(Settings.MIN_IF_COND_SIZE, maxCondSize + 1);
            ExpCategory[] noLiteral = new ExpCategory[] {ExpCategory.FUNC, ExpCategory.OP, ExpCategory.VAR};
            condition = program.getExpressionGenerator().genExp(conditionSize, boolean.class, noLiteral, true);
            if (condition == null)
                return null;
        }

        List<Statement> body = genBlock(size - 1 - conditionSize, indent + 1);
        if (body.isEmpty())
            return null;
        return new IfStatement(condition, body, indent);
    }
}
