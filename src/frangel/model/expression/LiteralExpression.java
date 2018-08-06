package frangel.model.expression;

import frangel.model.Precedence;
import frangel.utils.Utils;

public class LiteralExpression extends Expression {
    private Object literal; // This must be immutable or null

    public LiteralExpression(Object literal, Class<?> type) {
        this.literal = literal;
        setType(type);
    }

    @Override
    public void toJava(StringBuilder sb) {
        if (literal == null)
            sb.append("null");
        else if (literal instanceof String)
            sb.append(Utils.toStringLiteral((String) literal));
        else if (literal instanceof Character)
            sb.append(Utils.toCharLiteral((char) literal));
        else
            sb.append(literal.toString());
    }

    @Override
    public Precedence getPrecedence() {
        return Precedence.ATOMIC;
    }

    @Override
    public void encode(StringBuilder sb) {
        if (literal == null)
            sb.append("~");
        else if (literal instanceof String)
            sb.append(Utils.toStringLiteral((String) literal));
        else if (literal instanceof Character)
            sb.append(Utils.toCharLiteral((char) literal));
        else
            sb.append(literal).append("$"); // add $ to unambiguously end the literal
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        LiteralExpression other = (LiteralExpression) obj;
        if (literal == null)
            return other.literal == null;
        else
            return literal.equals(other.literal);
    }

    @Override
    public int hashCode() {
        return literal == null ? 199 : literal.hashCode();
    }

    @Override
    public Expression clone() {
        return this; // LiteralExpression is immutable
    }

    public Object getLiteral() {
        return literal;
    }
}