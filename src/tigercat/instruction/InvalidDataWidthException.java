package tigercat.instruction;

public class InvalidDataWidthException extends Exception
{
  private static final long serialVersionUID = -4846871555503481757L;
  
  public String opcode;
  
  public InvalidDataWidthException(String opcode)
  {
    this.opcode = opcode;
  }
}
