package tigercat.instruction;

public class UnencodeableImmediateException extends Exception
{
  private static final long serialVersionUID = 1009185537424169665L;
  
  public String message;
  public int immediateValue;

  public UnencodeableImmediateException(String message, int immediateValue)
  {
    this.message = message;
    this.immediateValue = immediateValue;
  }

}
