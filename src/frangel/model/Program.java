// Contains details of an entire program (i.e., function).

package frangel.model;

import java.util.*;

import frangel.Settings;
import frangel.SynthesisTask;
import frangel.model.expression.Expression;
import frangel.model.generator.ExpressionGenerator;
import frangel.model.generator.StatementGenerator;
import frangel.model.statement.Statement;
import frangel.utils.Utils;

public class Program {
    private String name;
    private String[] argNames;
    private Class<?>[] argTypes;
    private Class<?> returnType;
    private Map<Class<?>, Class<?>> parameterTypeMap;
    private SynthesisTask task;

    private List<Statement> statements;
    private Expression returnVal; // null if doesn't return

    private Map<String, Class<?>> variables; // variable name to type (all variables)
    private Map<Class<?>, List<String>> typeToVars; // type to list of variables of that type
    private Set<String> argVars; // copy of argNames but in Set form
    private Map<String, Expression> localVars; // local variables (varX), name to initial value
    private Set<String> loopVars; // for-loop counters (iX), by default declared outside the loop
    private Set<String> elemVars; // for-each loop variables (elemX), always declared in the loop
    private Set<String> inScope; // variables in scope
    private Set<String> loopVarsDeclaredInLoop; // for-loop variables declared in the loop

    private boolean angelic; // whether to generate angelic conditions

    // Every Program has its own ExpressionGenerator and StatementGenerator, both of which have a link back to this
    private ExpressionGenerator expressionGenerator;
    private StatementGenerator statementGenerator;

    private boolean useFragments = false;
    private Map<Class<?>, List<Expression>> expressionFragments = null;
    private List<Statement> statementFragments = null;

    // Creates a new (empty) Program
    public Program(SynthesisTask task, boolean angelic) {
        this.task = task;
        name = task.getName();
        int numArgs = task.getInputTypes().length;
        argNames = new String[numArgs];
        argTypes = new Class<?>[numArgs];
        for (int i = 0; i < task.getInputTypes().length; i++) {
            argNames[i] = task.getInputName(i);
            argTypes[i] = task.getInputTypes()[i];
        }

        returnType = task.getOutputType();
        parameterTypeMap = task.getParameterTypeMap();
        statements = new ArrayList<Statement>();

        variables = new HashMap<>();
        typeToVars = new HashMap<>();
        argVars = new HashSet<>(Arrays.asList(argNames));
        localVars = new HashMap<>();
        loopVars = new HashSet<>();
        elemVars = new HashSet<>();
        inScope = new HashSet<>();
        loopVarsDeclaredInLoop = new HashSet<>();
        for (int i = 0; i < argNames.length; i++) {
            String name = argNames[i];
            Class<?> type = argTypes[i];
            addVariable(name, type);
            inScope.add(name);
        }

        this.angelic = angelic;

        expressionGenerator = new ExpressionGenerator(this);
        statementGenerator = new StatementGenerator(this);
    }

    private void addVariable(String name, Class<?> type) {
        variables.put(name, type);
        List<String> list = typeToVars.get(type);
        if (list == null) {
            list = new ArrayList<String>();
            typeToVars.put(type, list);
        }
        list.add(name);
    }

    public void addLocalVar(String name, Class<?> type) {
        addVariable(name, type);
        localVars.put(name, Utils.getInitialValueForType(type, task));
    }

    public void addLoopVar(String name) {
        addVariable(name, int.class);
        loopVars.add(name);
    }

    public void addElemVar(String name, Class<?> type) {
        addVariable(name, type);
        elemVars.add(name);
    }

    // Finds a fresh variable name, without modifying the program state
    public String getFreshLocalVar() {
        for (int n = 1; ; n++) {
            String fresh = "var" + n;
            if (!variables.containsKey(fresh))
                return fresh;
        }
    }

    public String getFreshLoopVar() {
        for (int n = 1; ; n++) {
            String fresh = "i" + n;
            if (!variables.containsKey(fresh))
                return fresh;
        }
    }

    public String getFreshElemVar() {
        for (int n = 1; ; n++) {
            String fresh = "elem" + n;
            if (!variables.containsKey(fresh))
                return fresh;
        }
    }

    public void declareLoopVarInLoop(String v) {
        loopVarsDeclaredInLoop.add(v);
    }

    public void addToScope(String name) {
        inScope.add(name);
    }
    public void removeFromScope(String name) {
        inScope.remove(name);
    }

    public Map<String, Class<?>> getVariables() {
        return variables;
    }
    public Map<String, Expression> getLocalVars() {
        return localVars;
    }
    public Set<String> getLoopVars() {
        return loopVars;
    }
    public Set<String> getLoopVarsDeclaredInLoop() {
        return loopVarsDeclaredInLoop;
    }
    public Set<String> getElemVars() {
        return elemVars;
    }
    public Set<String> getInScope() {
        return inScope;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        Program other = (Program) obj;
        if (returnVal == null) {
            if (other.returnVal != null)
                return false;
        } else if (!returnVal.equals(other.returnVal))
            return false;
        return statements.equals(other.statements) && localVars.equals(other.localVars)
                && name.equals(other.name) && returnType.equals(other.returnType)
                && Arrays.equals(argNames, other.argNames) && Arrays.equals(argTypes, other.argTypes);
    }

    @Override
    public int hashCode() {
        // THIS HAS MANY COLLISIONS
        final int prime = 101;
        int result = 1;
        result = prime * result + (returnVal == null ? 0 : returnVal.hashCode());
        result = prime * result + statements.hashCode();
        result = prime * result + localVars.hashCode();
        result = prime * result + name.hashCode();
        result = prime * result + returnType.hashCode();
        result = prime * result + Arrays.hashCode(argNames);
        result = prime * result + Arrays.hashCode(argTypes);
        return result;
    }

    @Override
    public Program clone() {
        Program clone = new Program(task, angelic);
        clone.variables.putAll(variables);
        for (Map.Entry<Class<?>, List<String>> entry : typeToVars.entrySet())
            clone.typeToVars.put(entry.getKey(), new ArrayList<String>(entry.getValue()));
        for (Map.Entry<String, Expression> entry : localVars.entrySet())
            clone.localVars.put(entry.getKey(), entry.getValue().clone());
        clone.loopVars.addAll(loopVars);
        clone.elemVars.addAll(elemVars);
        clone.inScope.addAll(inScope);
        clone.loopVarsDeclaredInLoop.addAll(loopVarsDeclaredInLoop);
        for (Statement s : statements)
            clone.statements.add(s.clone());
        if (returnVal != null)
            clone.returnVal = returnVal.clone();
        clone.useFragments = useFragments;
        if (expressionFragments != null) {
            clone.expressionFragments = new HashMap<>();
            for (Map.Entry<Class<?>, List<Expression>> entry : expressionFragments.entrySet()) {
                List<Expression> list = new ArrayList<>();
                for (Expression e : entry.getValue())
                    list.add(e.clone());
                clone.expressionFragments.put(entry.getKey(), list);
            }
        }
        if (statementFragments != null) {
            clone.statementFragments = new ArrayList<>();
            for (Statement s : statementFragments)
                clone.statementFragments.add(s.clone());
        }
        return clone;
    }

    public void useFragments(Map<Class<?>, List<Expression>> expressionFragments, List<Statement> statementFragments) {
        useFragments = true;
        this.expressionFragments = expressionFragments;
        this.statementFragments = statementFragments;
    }

    public String toJava() {
        StringBuilder sb = new StringBuilder();
        sb.append("static ");
        sb.append(Utils.getParameterizedName(returnType, parameterTypeMap));
        sb.append(' ').append(name).append('(');
        for (int i = 0; i < argNames.length; i++) {
            if (i > 0)
                sb.append(", ");
            sb.append(Utils.getParameterizedName(argTypes[i], parameterTypeMap)).append(' ').append(argNames[i]);
        }
        sb.append(") {\n");
        for (String name : new TreeSet<String>(localVars.keySet())) {
            Class<?> type = variables.get(name);
            sb.append(Settings.INDENT).append(Utils.getParameterizedName(type, parameterTypeMap)).append(' ').append(name).append(" = ");
            localVars.get(name).toJava(sb);
            sb.append(";\n");
        }
        for (String name : new TreeSet<String>(loopVars)) {
            if (loopVarsDeclaredInLoop.contains(name))
                continue; // Declared in the loop
            sb.append(Settings.INDENT).append("int ").append(name).append(" = 0;\n");
        }
        for (Statement s : statements)
            s.toJava(sb);
        if (getReturnVal() != null) {
            sb.append(Settings.INDENT).append("return ");
            getReturnVal().toJava(sb);
            sb.append(";\n");
        }
        sb.append("}");

        return sb.toString();
    }

    /*
     *  Contract: for any two Program objects with the same SynthesisTask,
     *  encode() returns equal values iff the Program objects are equal
     *  (i.e., toJava() returns equal Strings).
     *
     *  This is only used to check uniqueness of Program objects (for the
     *  same SynthesisTask) before they are executed. An alternative is to use the
     *  Program object as the set key, but this strategy severely hinders
     *  garbage collection. toJava() could be used as the set key, but encode()
     *  offers a more compressed (but human-unreadable) String to use as the key.
     */
    public String encode() {
        StringBuilder sb = new StringBuilder();
        for (String name : new TreeSet<String>(localVars.keySet())) {
            sb.append("=").append(name).append("%");
            localVars.get(name).encode(sb);
        }
        for (Statement s : statements)
            s.encode(sb);
        if (getReturnVal() != null)
            getReturnVal().encode(sb);
        String s = sb.toString();
        return s;
    }

    public String getName() {
        return name;
    }

    public String[] getArgNames() {
        return argNames;
    }

    public Class<?>[] getArgTypes() {
        return argTypes;
    }

    public Set<String> getArgVars() {
        return argVars;
    }

    public Class<?> getReturnType() {
        return returnType;
    }

    public Map<Class<?>, Class<?>> getParameterTypeMap() {
        return parameterTypeMap;
    }

    public boolean returns() {
        return !returnType.equals(void.class);
    }

    public List<Statement> getStatements() {
        return statements;
    }

    public void setStatements(List<Statement> statements) {
        this.statements = statements;
    }

    public Expression getReturnVal() {
        return returnVal;
    }

    public void setReturnVal(Expression returnVal) {
        this.returnVal = returnVal;
    }

    public boolean isAngelic() {
        return angelic;
    }

    public void setAngelic(boolean angelic) {
        this.angelic = angelic;
    }

    public ExpressionGenerator getExpressionGenerator() {
        return expressionGenerator;
    }

    public StatementGenerator getStatementGenerator() {
        return statementGenerator;
    }

    public boolean useFragments() {
        return useFragments;
    }

    public void setUseFragments(boolean useFragments) {
        this.useFragments = useFragments;
    }

    public Map<Class<?>, List<Expression>> getExpressionFragments() {
        return expressionFragments;
    }

    public List<Statement> getStatementFragments() {
        return statementFragments;
    }

    public SynthesisTask getSynthesisTask() {
        return task;
    }
}
