package tigercat.instruction;

public class InvalidDataWidthException extends Exception
{
  String opcode;
  
  public InvalidDataWidthException(String opcode)
  {
    this.opcode = opcode;
  }
}
