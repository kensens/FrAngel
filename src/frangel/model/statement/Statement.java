package frangel.model.statement;

public abstract class Statement {
    private int indent;

    // Includes terminating newline
    public String toJava(boolean indent) {
        StringBuilder sb = new StringBuilder();
        toJava(sb, false);
        return sb.toString();
    }
    public String toJava() {
        return toJava(true);
    }
    public abstract void toJava(StringBuilder sb, boolean indent);
    public void toJava(StringBuilder sb) {
        toJava(sb, true);
    }
    public abstract void encode(StringBuilder sb); // See comment for Program.encode()

    @Override
    public abstract boolean equals(Object obj);
    @Override
    public abstract int hashCode();
    @Override
    public abstract Statement clone();

    public int getIndent() {
        return indent;
    }
    public void setIndent(int indent) {
        this.indent = indent;
    }
}
