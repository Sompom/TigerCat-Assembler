package tigercat;

public class UndefinedLabelException extends AssemblerException
{
  private static final long serialVersionUID = -2822124951403421695L;
  
  private String label;
  
  public UndefinedLabelException(String label)
  {
    this.label = label;
  }


  private String generateMessage() {
    return "Undefined label: " + label;
  }

  @Override
  public String getDiagnostic() {
    return getContextError() + generateMessage();
  }
}
