package frangel.interpreter;

public class EvaluationInfo {
    private boolean isError;
    private Object returnValue;
    private String angelicCodePath;
    private int angelicCodePathIndex;
    private String actualCodePath;
    private int loopIterations;
    private boolean success;
    private boolean slow;

    public EvaluationInfo(String angelicCodePath) {
        isError = false;
        returnValue = null;
        this.angelicCodePath = angelicCodePath;
        angelicCodePathIndex = 0;
        actualCodePath = "";
        loopIterations = 0;
        success = false;
        slow = false;
    }

    public boolean isError() {
        return isError;
    }
    public void setError(boolean isError) {
        this.isError = isError;
    }

    public Object getReturnValue() {
        return returnValue;
    }
    public void setReturnValue(Object returnValue) {
        this.returnValue = returnValue;
    }

    public boolean hasAngelicCodePath() {
        return angelicCodePath != null;
    }
    public boolean getNextAngelicConditionValue() {
        if (angelicCodePathIndex >= angelicCodePath.length())
            return false;
        return angelicCodePath.charAt(angelicCodePathIndex++) == '1';
    }

    public String getActualCodePath() {
        return actualCodePath;
    }
    public void logActualCodePath(boolean condition) {
        actualCodePath += (condition ? "1" : "0");
    }

    public int getLoopIterations() {
        return loopIterations;
    }
    public int incLoopIterations() {
        return ++loopIterations; // returns incremented value
    }

    public boolean isSuccess() {
        return success;
    }
    public void setSuccess(boolean success) {
        this.success = success;
    }

    public boolean isSlow() {
        return slow;
    }
    public void setSlow(boolean slow) {
        this.slow = slow;
    }
}
