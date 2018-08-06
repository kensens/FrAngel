package frangel.model.statement;

import java.util.ArrayList;
import java.util.List;

import frangel.model.expression.Expression;
import frangel.utils.Utils;

public class ForEachLoop extends Statement {
    private String varName;
    private Expression container;
    private List<Statement> body;
    private Class<?> varType;

    public ForEachLoop(Class<?> varType, String varName, Expression container, List<Statement> body, int indent) {
        this.varType = varType;
        this.varName = varName;
        this.container = container;
        this.body = body;
        setIndent(indent);
    }

    @Override
    public void toJava(StringBuilder sb, boolean indent) {
        if (indent)
            sb.append(Utils.indent(getIndent()));
        sb.append("for (").append(Utils.getClassName(varType)).append(' ').append(varName).append(" : ");
        container.toJava(sb);
        sb.append(") {\n");
        for (Statement s : body)
            s.toJava(sb);
        if (indent)
            sb.append(Utils.indent(getIndent()));
        sb.append("}\n");
    }

    @Override
    public void encode(StringBuilder sb) {
        sb.append("FE").append(varName);
        container.encode(sb);
        for (Statement s : body)
            s.encode(sb);
        sb.append("}");
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        ForEachLoop other = (ForEachLoop) obj;
        return varType.equals(other.varType) && varName.equals(other.varName)
                && container.equals(other.container) && body.equals(other.body);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + body.hashCode();
        result = prime * result + container.hashCode();
        result = prime * result + varName.hashCode();
        result = prime * result + varType.hashCode();
        return result;
    }

    @Override
    public Statement clone() {
        List<Statement> newBody = new ArrayList<>();
        for (Statement s : body)
            newBody.add(s.clone());
        return new ForEachLoop(varType, varName, container.clone(), newBody, getIndent());
    }

    public Class<?> getVarType() {
        return varType;
    }

    public String getVarName() {
        return varName;
    }

    public List<Statement> getBody() {
        return body;
    }

    public Expression getContainer() {
        return container;
    }

    public void setVarName(String varName) {
        this.varName = varName;
    }
}
