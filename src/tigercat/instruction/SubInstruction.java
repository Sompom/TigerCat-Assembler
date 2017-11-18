package tigercat.instruction;

public class SubInstruction extends Instruction
{
  static final int SUB_ENCODING = 0x1A;
  static final int SUBC_ENCODING = 0x1B;

  public SubInstruction(String[] tokens, boolean encodingValid, Integer returnAddress)
          throws InvalidDataWidthException, InstructionArgumentCountException, InvalidOpcodeException,
          InstructionSyntaxError, InvalidRegisterException, XmlLookupException {
    super(tokens, encodingValid, SUB_ENCODING, THREE_ARGUMENTS);
    
    if (tokens[0].matches("^subc.$"))
    {
      opcode_encoding = SUBC_ENCODING;
    }
  }

}
