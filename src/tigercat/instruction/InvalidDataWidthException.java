package tigercat.instruction;

public class InvalidDataWidthException extends Exception
{
  public String opcode;
  
  public InvalidDataWidthException(String opcode)
  {
    this.opcode = opcode;
  }
}
