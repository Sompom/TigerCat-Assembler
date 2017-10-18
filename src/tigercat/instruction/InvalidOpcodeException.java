package tigercat.instruction;

public class InvalidOpcodeException extends Exception
{
  String opcode;
  
  public InvalidOpcodeException(String opcode)
  {
    this.opcode = opcode;
  }
}
