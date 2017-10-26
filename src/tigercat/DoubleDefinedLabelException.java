package tigercat;

public class DoubleDefinedLabelException extends AssemblerException
{
  private static final long serialVersionUID = 5617359729250311205L;
  
  String label;

  public DoubleDefinedLabelException(String label)
  {
    this.label = label;
  }

  private String generateMessage() {
    return "Label " + label + " has already been declared";
  }

  @Override
  public String getDiagnostic() {
    return getContextError() + generateMessage();
  }

}
