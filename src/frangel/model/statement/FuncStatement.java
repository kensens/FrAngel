package frangel.model.statement;

import frangel.model.expression.FuncExpression;
import frangel.utils.Utils;

public class FuncStatement extends Statement {
    private FuncExpression func;

    public FuncStatement(FuncExpression func, int indent) {
        this.func = func;
        setIndent(indent);
    }

    @Override
    public void toJava(StringBuilder sb, boolean indent) {
        if (indent)
            sb.append(Utils.indent(getIndent()));
        func.toJava(sb);
        sb.append(";\n");
    }

    @Override
    public void encode(StringBuilder sb) {
        func.encode(sb);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        return func.equals(((FuncStatement) obj).func);
    }

    @Override
    public int hashCode() {
        final int prime = 113;
        return prime + func.hashCode();
    }

    @Override
    public Statement clone() {
        return new FuncStatement((FuncExpression) func.clone(), getIndent());
    }

    public FuncExpression getFunc() {
        return func;
    }
}