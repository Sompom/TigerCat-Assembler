package tigercat.instruction;

public class XorInstruction extends Instruction
{
  static final int XOR_ENCODING = 0x1E;

  protected XorInstruction(String[] tokens, boolean encodingValid)
          throws InvalidDataWidthException, InstructionArgumentCountException, InvalidOpcodeException,
          InstructionSyntaxError, InvalidRegisterException, XmlLookupException {
    super(tokens, encodingValid, XOR_ENCODING, THREE_ARGUMENTS);
  }

}
