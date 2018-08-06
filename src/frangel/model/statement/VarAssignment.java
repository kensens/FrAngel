package frangel.model.statement;

import frangel.model.expression.Expression;
import frangel.model.expression.VarExpression;
import frangel.utils.Utils;

public class VarAssignment extends Statement {
    private VarExpression var;
    private Expression value;

    public VarAssignment(VarExpression var, Expression value, int indent) {
        this.var = var;
        this.value = value;
        setIndent(indent);
    }

    @Override
    public void toJava(StringBuilder sb, boolean indent) {
        if (indent)
            sb.append(Utils.indent(getIndent()));
        sb.append(var.getName()).append(" = ");
        value.toJava(sb);
        sb.append(";\n");
    }

    @Override
    public void encode(StringBuilder sb) {
        sb.append("=");
        var.encode(sb);
        value.encode(sb);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        VarAssignment other = (VarAssignment) obj;
        return var.equals(other.var) && value.equals(other.value);
    }

    @Override
    public int hashCode() {
        final int prime = 107;
        int result = 1;
        result = prime * result + value.hashCode();
        result = prime * result + var.hashCode();
        return result;
    }

    @Override
    public Statement clone() {
        return new VarAssignment((VarExpression) var.clone(), value.clone(), getIndent());
    }

    public VarExpression getVar() {
        return var;
    }

    public Expression getValue() {
        return value;
    }

    public void setValue(Expression value) {
        this.value = value;
    }
}
