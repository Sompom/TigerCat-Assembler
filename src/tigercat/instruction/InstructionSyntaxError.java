package tigercat.instruction;

public class InstructionSyntaxError extends Exception
{
  String message;
  
  public InstructionSyntaxError(String message)
  {
    this.message = message;
  }
}
