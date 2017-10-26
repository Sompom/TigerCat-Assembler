package tigercat.instruction;

public class InvalidRegisterException extends Exception
{
  private static final long serialVersionUID = 7319699311294089366L;
  
  public String invalid_register;
  
  public InvalidRegisterException(String invalid_register)
  {
    this.invalid_register = invalid_register;
  }
}
