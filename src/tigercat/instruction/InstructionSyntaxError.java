package tigercat.instruction;

import tigercat.AssemblerException;

public class InstructionSyntaxError extends AssemblerException
{
  private static final long serialVersionUID = 5097284501913462594L;
  
  private String message;

  public InstructionSyntaxError(String message)
  {
    this.message = message;
  }

  @Override
  public String getDiagnostic()
  {
    return getContextError() + message;
  }
}
