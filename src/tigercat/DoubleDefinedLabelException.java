package tigercat;

public class DoubleDefinedLabelException extends Exception
{
  private static final long serialVersionUID = 5617359729250311205L;
  
  String label;

  public DoubleDefinedLabelException(String label)
  {
    this.label = label;
  }

}
