package tigercat;

public class UndefinedLabelException extends Exception
{
  public String label;
  
  public UndefinedLabelException(String label)
  {
    this.label = label;
  }
}
