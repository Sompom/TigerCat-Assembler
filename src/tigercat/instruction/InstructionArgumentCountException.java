package tigercat.instruction;

public class InstructionArgumentCountException extends Exception
{
  private static final long serialVersionUID = -6732438117796174143L;
  
  public int expected;
  public int actual;
  
  public InstructionArgumentCountException(int expected, int actual)
  {
    this.expected = expected;
    this.actual = actual;
  }
}
