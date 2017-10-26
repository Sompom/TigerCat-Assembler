package tigercat;

public abstract class AssemblerException extends Exception {
    private boolean hasContext = false;
    private int contextLineNum;
    private String contextLine;

    protected String getContextError() {
        if(hasContext)
            return "Error on line " + contextLineNum + ": " + contextLine + ". ";
        else
            return "";
    }

    public void setContext(int contextLineNum, String contextLine) {
        hasContext = true;
        this.contextLineNum = contextLineNum + 1;
        this.contextLine = contextLine;
    }

    public abstract String getDiagnostic();

}
