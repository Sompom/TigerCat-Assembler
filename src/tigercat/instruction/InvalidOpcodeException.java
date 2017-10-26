package tigercat.instruction;

public class InvalidOpcodeException extends Exception
{
  private static final long serialVersionUID = 5262827982056232129L;
  
  public String opcode;
  
  public InvalidOpcodeException(String opcode)
  {
    this.opcode = opcode;
  }
}
