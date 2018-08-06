package frangel.model.expression;

import java.util.Arrays;

import frangel.Settings;
import frangel.model.FunctionData;
import frangel.model.Precedence;
import frangel.utils.Utils;

public class FuncExpression extends Expression {
    private Expression[] args;
    private Expression calledFrom;
    private FunctionData data;

    public FuncExpression(Expression[] args, Expression calledFrom, FunctionData data) {
        this.args = args;
        this.calledFrom = calledFrom;
        this.data = data;
        setType(data.getReturnType());
    }

    @Override
    public Precedence getPrecedence() {
        switch (data.getKind()) {
        case METHOD:
        case FIELD:
        case ARR_GET:
        case ARR_LEN:
            return Precedence.DOT;
        case CONSTRUCTOR:
            return Precedence.NEW;
        case ARR_SET:
            return Precedence.ASSIGNMENT;
        default:
            return null;
        }
    }

    @Override
    public void toJava(StringBuilder sb) {
        Precedence thisPrecedence = getPrecedence();
        String sep = "";
        switch (data.getKind()) {
        case METHOD:
            if (calledFrom == null) {
                sb.append(Utils.getClassName(data.getCallerClass()));
            } else {
                if (thisPrecedence.parenLeft(calledFrom.getPrecedence())) {
                    sb.append('(');
                    calledFrom.toJava(sb);
                    sb.append(')');
                } else {
                    calledFrom.toJava(sb);
                }
            }
            sb.append('.').append(getName()).append('(');
            for (Expression e : args) {
                sb.append(sep);
                e.toJava(sb);
                sep = ", ";
            }
            sb.append(')');
            break;
        case CONSTRUCTOR:
            sb.append("new ").append(getName()).append('(');
            for (Expression e : args) {
                sb.append(sep);
                e.toJava(sb);
                sep = ", ";
            }
            sb.append(')');
            break;
        case FIELD:
            if (data.isStatic()) {
                sb.append(Utils.getClassName(data.getCallerClass())).append('.').append(getName());
            } else {
                if (thisPrecedence.parenLeft(calledFrom.getPrecedence())) {
                    sb.append('(');
                    calledFrom.toJava(sb);
                    sb.append(')');
                } else {
                    calledFrom.toJava(sb);
                }
                sb.append('.').append(getName());
            }
            break;
        case ARR_GET:
            if (thisPrecedence.parenLeft(args[0].getPrecedence())) {
                sb.append('(');
                args[0].toJava(sb);
                sb.append(')');
            } else {
                args[0].toJava(sb);
            }
            sb.append('[');
            args[1].toJava(sb);
            sb.append(']');
            break;
        case ARR_SET:
            if (thisPrecedence.parenLeft(args[0].getPrecedence())) {
                sb.append('(');
                args[0].toJava(sb);
                sb.append(')');
            } else {
                args[0].toJava(sb);
            }
            sb.append('[');
            args[1].toJava(sb);
            sb.append("] = "); // Only allowed as a Statement
            args[2].toJava(sb);
            break;
        case ARR_LEN:
            if (thisPrecedence.parenLeft(args[0].getPrecedence())) {
                sb.append('(');
                args[0].toJava(sb);
                sb.append(')');
            } else {
                args[0].toJava(sb);
            }
            sb.append(".length");
            break;
        }
    }

    @Override
    public void encode(StringBuilder sb) {
        switch (data.getKind()) {
        case METHOD:
        case CONSTRUCTOR:
        case FIELD:
            sb.append("f");
            data.encode(sb);
            sb.append(":");
            if (calledFrom != null)
                calledFrom.encode(sb);
            for (Expression e : args)
                e.encode(sb);
            break;
        case ARR_GET:
            sb.append("g");
            args[0].encode(sb);
            args[1].encode(sb);
            break;
        case ARR_SET:
            sb.append("s");
            args[0].encode(sb);
            args[1].encode(sb);
            args[2].encode(sb);
            break;
        case ARR_LEN:
            sb.append("l");
            args[0].encode(sb);
            break;
        default:
            System.err.println("Unknown kind of FunctionData: " + data.getKind());
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        FuncExpression other = (FuncExpression) obj;
        if (!data.equals(other.data) || !Arrays.equals(args, other.args))
            return false;
        if (calledFrom == null) {
            if (other.calledFrom != null)
                return false;
        } else if (!calledFrom.equals(other.calledFrom))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 1759;
        int result = 1;
        result = prime * result + (data == null ? 0 : data.hashCode());
        result = prime * result + Arrays.hashCode(args);
        result = prime * result + (calledFrom == null ? 0 : calledFrom.hashCode());
        return prime * result;
    }

    @Override
    public Expression clone() {
        Expression[] newArgs = new Expression[args.length];
        for (int i = 0; i < args.length; i++)
            newArgs[i] = args[i].clone();
        return new FuncExpression(newArgs, calledFrom == null ? null : calledFrom.clone(), data);
    }

    public String getName() {
        return data.getName(Settings.USE_SIMPLE_NAME);
    }

    public Expression[] getArgs() {
        return args;
    }

    public Expression getCalledFrom() {
        return calledFrom;
    }

    public void setCalledFrom(Expression calledFrom) {
        this.calledFrom = calledFrom;
    }

    public FunctionData getData() {
        return data;
    }
}