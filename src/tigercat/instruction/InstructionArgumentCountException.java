package tigercat.instruction;

public class InstructionArgumentCountException extends Exception
{
  public int expected;
  public int actual;
  
  public InstructionArgumentCountException(int expected, int actual)
  {
    this.expected = expected;
    this.actual = actual;
  }
}
