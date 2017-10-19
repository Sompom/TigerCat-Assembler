package tigercat.instruction;

public class InvalidRegisterException extends Exception
{
  String invalid_register;
  
  public InvalidRegisterException(String invalid_register)
  {
    this.invalid_register = invalid_register;
  }
}