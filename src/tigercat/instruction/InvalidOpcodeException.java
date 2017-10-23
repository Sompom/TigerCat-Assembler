package tigercat.instruction;

public class InvalidOpcodeException extends Exception
{
  public String opcode;
  
  public InvalidOpcodeException(String opcode)
  {
    this.opcode = opcode;
  }
}
