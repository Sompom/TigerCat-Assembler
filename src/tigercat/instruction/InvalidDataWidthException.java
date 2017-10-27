package tigercat.instruction;

import tigercat.AssemblerException;

public class InvalidDataWidthException extends AssemblerException
{
  private static final long serialVersionUID = -4846871555503481757L;
  
  public String opcode;
  
  public InvalidDataWidthException(String opcode)
  {
    this.opcode = opcode;
  }

  private String generateMessage() {
    return "Invalid data width on opcode: " + opcode;
  }

  @Override
  public String getDiagnostic() {
    return getContextError() + generateMessage();
  }
}
