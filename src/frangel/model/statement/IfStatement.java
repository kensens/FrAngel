package frangel.model.statement;

import java.util.ArrayList;
import java.util.List;

import frangel.model.expression.Expression;
import frangel.utils.Utils;

public class IfStatement extends Statement {
    private Expression condition;
    private List<Statement> body;
    private Expression rememberedCondition;

    public IfStatement(Expression condition, List<Statement> body, int indent) {
        this.condition = condition;
        this.body = body;
        setIndent(indent);
        rememberedCondition = null;
    }

    public IfStatement(List<Statement> body, int indent) {
        this(null, body, indent);
    }

    @Override
    public void toJava(StringBuilder sb, boolean indent) {
        if (indent)
            sb.append(Utils.indent(getIndent()));
        sb.append("if (");
        if (isAngelic())
            sb.append("<ANGELIC>");
        else
            condition.toJava(sb);
        sb.append(") {\n");
        for (Statement s : body)
            s.toJava(sb);
        if (indent)
            sb.append(Utils.indent(getIndent()));
        sb.append("}\n");
    }

    @Override
    public void encode(StringBuilder sb) {
        sb.append("I");
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
        IfStatement other = (IfStatement) obj;
        if (condition == null) {
            if (other.condition != null)
                return false;
        } else if (!condition.equals(other.condition))
            return false;
        return body.equals(other.body);
    }

    @Override
    public int hashCode() {
        final int prime = 137;
        int result = 1;
        result = prime * result + (condition == null ? 0 : condition.hashCode());
        result = prime * result + body.hashCode();
        return result;
    }

    @Override
    public Statement clone() {
        List<Statement> newBody = new ArrayList<>();
        for (Statement s : body)
            newBody.add(s.clone());
        IfStatement clone = new IfStatement(condition == null ? null : condition.clone(), newBody, getIndent());
        if (rememberedCondition != null)
            clone.rememberCondition(rememberedCondition.clone());
        return clone;
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
}
