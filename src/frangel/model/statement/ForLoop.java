package frangel.model.statement;

import java.util.ArrayList;
import java.util.List;

import frangel.model.expression.Expression;
import frangel.utils.Utils;

public class ForLoop extends Statement {
    private String varName;
    private Expression condition;
    private List<Statement> body;
    private Expression rememberedCondition;
    private boolean isVarLocal;
    private boolean isWhileLoop;

    public ForLoop(String varName, Expression condition, List<Statement> body, int indent) {
        this.varName = varName;
        this.condition = condition;
        this.body = body;
        setIndent(indent);
        rememberedCondition = null;
        isVarLocal = false;
        isWhileLoop = false;
    }

    public ForLoop(String varName, List<Statement> body, int indent) {
        this(varName, null, body, indent);
    }

    @Override
    public void toJava(StringBuilder sb, boolean indent) {
        if (isWhileLoop) {
            if (indent)
                sb.append(Utils.indent(getIndent()));
            sb.append("while (");
            if (isAngelic())
                sb.append("<ANGELIC>");
            else
                condition.toJava(sb);
            sb.append(") {\n");
        } else {
            if (indent)
                sb.append(Utils.indent(getIndent()));
            sb.append("for (");
            if (isVarLocal)
                sb.append("int ");
            sb.append(varName).append(" = 0; ");
            if (isAngelic())
                sb.append("<ANGELIC>");
            else
                condition.toJava(sb);
            sb.append("; ").append(varName).append("++) {\n");
        }
        for (Statement s : body)
            s.toJava(sb);
        if (indent)
            sb.append(Utils.indent(getIndent()));
        sb.append("}\n");
    }

    @Override
    public void encode(StringBuilder sb) {
        sb.append("F");
        if (isAngelic())
            sb.append("M");
        else
            condition.encode(sb);
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
        ForLoop other = (ForLoop) obj;
        if (!varName.equals(other.varName))
            return false;
        if (condition == null) {
            if (other.condition != null)
                return false;
        } else if (!condition.equals(other.condition))
            return false;
        return body.equals(other.body);
    }

    @Override
    public int hashCode() {
        final int prime = 127;
        int result = 1;
        result = prime * result + varName.hashCode();
        result = prime * result + ((condition == null) ? 0 : condition.hashCode());
        result = prime * result + body.hashCode();
        return result;
    }

    @Override
    public Statement clone() {
        List<Statement> newBody = new ArrayList<>();
        for (Statement s : body)
            newBody.add(s.clone());
        ForLoop clone = new ForLoop(varName, condition == null ? null : condition.clone(), newBody, getIndent());
        if (rememberedCondition != null)
            clone.rememberCondition(rememberedCondition.clone());
        clone.isVarLocal = isVarLocal;
        clone.isWhileLoop = isWhileLoop;
        return clone;
    }

    public String getVarName() {
        return varName;
    }

    public Expression getCondition() {
        return condition;
    }

    public void setCondition(Expression condition) {
        this.condition = condition;
    }

    public Expression getRememberedCondition() {
        return rememberedCondition;
    }

    public void rememberCondition(Expression condition) {
        this.rememberedCondition = condition.clone();
    }

    public List<Statement> getBody() {
        return body;
    }

    public boolean isAngelic() {
        return condition == null;
    }

    public boolean isVarLocal() {
        return isVarLocal;
    }

    public void setVarLocal(boolean l) {
        isVarLocal = l;
    }

    public void setVarName(String varName) {
        this.varName = varName;
    }

    public boolean isWhileLoop() {
        return isWhileLoop;
    }

    public void setWhileLoop(boolean whileLoop) {
        isWhileLoop = whileLoop;
    }
}
