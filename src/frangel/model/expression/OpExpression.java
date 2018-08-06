package frangel.model.expression;

import frangel.model.Precedence;

public class OpExpression extends Expression {
    public enum Op { PLUS, MINUS, TIMES, DIV, MOD, AND, OR, EQUALS, LESS, LEQ, NOT };
    public static int numOp() {
        return Op.values().length;
    }

    private Op op;
    private Expression left; // null for unary operators
    private Expression right; // never null

    public OpExpression(Op op, Expression left, Expression right) {
        this.op = op;
        this.left = left;
        this.right = right;
    }

    private String getSymbol() {
        switch (op) {
        case PLUS: return "+";
        case MINUS: return "-";
        case TIMES: return "*";
        case DIV: return "/";
        case MOD: return "%";
        case AND: return "&&";
        case OR: return "||";
        case EQUALS: return "==";
        case LESS: return "<";
        case LEQ: return "<=";
        case NOT: return "!";
        default: return "";
        }
    }

    @Override
    public Precedence getPrecedence() {
        switch (op) {
        case PLUS:
        case MINUS: return Precedence.ADDITIVE;
        case TIMES:
        case DIV:
        case MOD: return Precedence.MULTIPLICATIVE;
        case AND: return Precedence.AND;
        case OR: return Precedence.OR;
        case EQUALS: return Precedence.EQUALITY;
        case LESS:
        case LEQ: return Precedence.RELATIONAL;
        case NOT: return Precedence.NOT;
        default: return null;
        }
    }

    @Override
    public void toJava(StringBuilder sb) {
        Precedence thisPrecedence = getPrecedence();
        if (left != null) {
            if (thisPrecedence.parenLeft(left.getPrecedence())) {
                sb.append('(');
                left.toJava(sb);
                sb.append(')');
            } else {
                left.toJava(sb);
            }
        }
        String symbol = getSymbol();
        if (op == Op.NOT)
            sb.append(symbol);
        else
            sb.append(' ').append(symbol).append(' ');
        if (thisPrecedence.parenRight(right.getPrecedence())) {
            sb.append('(');
            right.toJava(sb);
            sb.append(')');
        } else {
            right.toJava(sb);
        }
    }

    @Override
    public void encode(StringBuilder sb) {
        sb.append(getSymbol());
        if (left != null)
            left.encode(sb);
        right.encode(sb);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        OpExpression other = (OpExpression) obj;
        if (op != other.op || !right.equals(other.right))
            return false;
        if (left == null) {
            if (other.left != null)
                return false;
        } else if (!left.equals(other.left))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 1009;
        int result = 1;
        result = prime * result + op.hashCode();
        result = prime * result + (left == null ? 0 : left.hashCode());
        result = prime * result + right.hashCode();
        return prime * result;
    }

    @Override
    public Expression clone() {
        return new OpExpression(op, left == null ? null : left.clone(), right.clone());
    }

    @Override
    public Class<?> getType() {
        if (super.getType() != null)
            return super.getType();
        Class<?> t;
        if (op == Op.EQUALS || op == Op.LESS || op == Op.LEQ || op == Op.NOT)
            t = boolean.class;
        else if (left.getType().equals(String.class) || right.getType().equals(String.class))
            t = String.class;
        else if (left.getType().equals(double.class) || right.getType().equals(double.class))
            t = double.class;
        else
            t = left.getType();
        setType(t);
        return t;
    }

    public Op getOp() {
        return op;
    }

    public void setOp(Op op) {
        this.op = op;
    }

    public Expression getLeft() {
        return left;
    }

    public void setLeft(Expression left) {
        this.left = left;
    }

    public Expression getRight() {
        return right;
    }

    public void setRight(Expression right) {
        this.right = right;
    }
}