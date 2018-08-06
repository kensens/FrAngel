package frangel.model.expression;

import frangel.model.Precedence;

public abstract class Expression {
    private Class<?> t;

    // Includes terminating newline
    public String toJava() {
        StringBuilder sb = new StringBuilder();
        toJava(sb);
        return sb.toString();
    }
    public abstract void toJava(StringBuilder sb);
    public abstract Precedence getPrecedence();

    public abstract void encode(StringBuilder sb); // See comment for Program.encode()

    @Override
    public abstract boolean equals(Object obj);
    @Override
    public abstract int hashCode();
    @Override
    public abstract Expression clone();

    public Class<?> getType() {
        return t;
    }

    public void setType(Class<?> t) {
        this.t = t;
    }
}
