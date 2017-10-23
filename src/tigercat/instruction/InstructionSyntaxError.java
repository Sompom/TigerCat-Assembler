package tigercat.instruction;

public class InstructionSyntaxError extends Exception
{
  public String message;
  
  public InstructionSyntaxError(String message)
  {
    this.message = message;
  }
}
