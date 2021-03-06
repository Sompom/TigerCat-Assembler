package tigercat.instruction;

public class AddInstruction extends Instruction
{
  static final int ADD_ENCODING = 0x18;
  static final int ADDC_ENCODING = 0x19;

  protected AddInstruction(String[] tokens, boolean encodingValid, Integer returnAddress)
          throws InvalidDataWidthException, InstructionArgumentCountException, InvalidOpcodeException,
          InstructionSyntaxError, InvalidRegisterException, XmlLookupException {
    super(tokens, encodingValid, ADD_ENCODING, THREE_ARGUMENTS);
    
    if (tokens[0].matches("^addc.$"))
    {
      opcode_encoding = ADDC_ENCODING;
    }
  }

}
