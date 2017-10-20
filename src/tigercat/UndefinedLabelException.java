package tigercat;

public class UndefinedLabelException extends Exception
{
  String label;
  
  public UndefinedLabelException(String label)
  {
    this.label = label;
  }
}
