package frangel.model;

public enum Precedence {
    // See https://introcs.cs.princeton.edu/java/11precedence/
    ASSIGNMENT(1),
    TERNARY(2),
    OR(3),
    AND(4),
    EQUALITY(8),
    RELATIONAL(9),
    ADDITIVE(11),
    MULTIPLICATIVE(12),
    NEW(13),
    NOT(14),
    DOT(16),
    ATOMIC(17); // Like a variable name or string literal

    private int level;
    Precedence(int l) {
        level = l;
    }
    public boolean parenLeft(Precedence left) {
        if (level == left.level) {
            // These are left-to-right associative, left side doesn't need parens
            return !(this == DOT || this == MULTIPLICATIVE || this == ADDITIVE || this == EQUALITY || this == AND || this == OR);
        }
        return left.level < level;
    }
    public boolean parenRight(Precedence right) {
        if (level == right.level) {
            // These are right-to-left associative, right side doesn't need parens
            return !(this == NEW || this == ASSIGNMENT);
        }
        return right.level < level;
    }
}
