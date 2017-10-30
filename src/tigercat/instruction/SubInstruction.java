package tigercat.instruction;

public class SubInstruction extends Instruction
{
  static final int SUB_ENCODING = 0x1A;
  static final int SUBC_ENCODING = 0x1B;

  public SubInstruction(String[] tokens, boolean encodingValid)
      throws InvalidDataWidthException, InstructionArgumentCountException, InvalidOpcodeException,
      InstructionSyntaxError, InvalidRegisterException
  {
    super(tokens, encodingValid, SUB_ENCODING, THREE_ARGUMENTS);
    
    if (tokens[0].matches("^subc.$"))
    {
      opcode_encoding = SUBC_ENCODING;
    }
  }

}
