package frangel.model.expression;

import frangel.model.Precedence;

public class VarExpression extends Expression {
    private String name;

    public VarExpression(String name, Class<?> type) {
        this.name = name;
        setType(type);
    }

    @Override
    public void toJava(StringBuilder sb) {
        sb.append(name);
    }

    @Override
    public Precedence getPrecedence() {
        return Precedence.ATOMIC;
    }

    @Override
    public void encode(StringBuilder sb) {
        sb.append(name).append("%"); // add % to unambiguously end the name
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        return name.equals(((VarExpression) obj).name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public Expression clone() {
        return new VarExpression(name, getType());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
