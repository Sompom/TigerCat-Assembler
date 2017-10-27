package tigercat.instruction;

import tigercat.AssemblerException;

public class InvalidOpcodeException extends AssemblerException
{
  private static final long serialVersionUID = 5262827982056232129L;
  
  private String opcode;
  
  public InvalidOpcodeException(String opcode)
  {
    this.opcode = opcode;
  }

  private String generateMessage() {
    return "Invalid opcode: " + opcode;
  }

  @Override
  public String getDiagnostic() {
    return getContextError() + generateMessage();
  }
}
