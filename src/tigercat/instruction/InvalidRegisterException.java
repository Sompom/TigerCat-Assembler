package tigercat.instruction;

import tigercat.AssemblerException;

public class InvalidRegisterException extends AssemblerException
{
  private static final long serialVersionUID = 7319699311294089366L;
  
  private String invalid_register;
  
  public InvalidRegisterException(String invalid_register)
  {
    this.invalid_register = invalid_register;
  }

  private String generateMessage() {
    return "Invalid register: " + invalid_register;
  }

  @Override
  public String getDiagnostic() {
    return getContextError() + generateMessage();
  }
}
