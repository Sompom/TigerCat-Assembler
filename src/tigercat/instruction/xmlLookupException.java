package tigercat.instruction;

import tigercat.AssemblerException;

public class xmlLookupException extends AssemblerException
{
    private static final long serialVersionUID = 74629575625476366L;

    private String message;

    public xmlLookupException(String message)
    {
        this.message = message;
    }

    private String generateMessage() {
        return "Xml lookup error: " + message;
    }

    @Override
    public String getDiagnostic() {
        return getContextError() + generateMessage();
    }
}
