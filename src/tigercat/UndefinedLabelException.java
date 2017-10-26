package tigercat;

public class UndefinedLabelException extends Exception
{
  private static final long serialVersionUID = -2822124951403421695L;
  
  public String label;
  
  public UndefinedLabelException(String label)
  {
    this.label = label;
  }
}
