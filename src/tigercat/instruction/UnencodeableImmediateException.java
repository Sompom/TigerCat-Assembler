package tigercat.instruction;

import tigercat.AssemblerException;

public class UnencodeableImmediateException extends AssemblerException
{
  private static final long serialVersionUID = 1009185537424169665L;
  
  private String message;
  private int immediateValue;

  public UnencodeableImmediateException(String message, int immediateValue)
  {
    this.message = message;
    this.immediateValue = immediateValue;
  }

  private String generateMessage() {
    return message + ": " + Integer.toHexString(immediateValue);
  }

  @Override
  public String getDiagnostic() {
    return getContextError() + generateMessage();
  }

}
