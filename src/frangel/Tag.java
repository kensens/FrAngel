package frangel;

public enum Tag {
    IF, // Solution uses if statement (or ternary operator)
    FOR, // Solution uses regular for loop (not including for-each loops)
    WHILE, // Solution uses while loop
    FOREACH, // Solution uses for-each loop
    SINGLE_LINE, // Solution is (or can reasonably be) a single line (without control structures)
    ;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
