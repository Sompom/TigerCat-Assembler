package tigercat.instruction;

public class InstructionSyntaxError extends Exception
{
  private static final long serialVersionUID = 5097284501913462594L;
  
  public String message;
  
  public InstructionSyntaxError(String message)
  {
    this.message = message;
  }
}
