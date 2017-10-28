package tigercat.instruction;

import tigercat.AssemblerException;

public class XmlLookupException extends AssemblerException
{
    private static final long serialVersionUID = 74629575625476366L;

    private String message;

    public XmlLookupException(String message)
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
