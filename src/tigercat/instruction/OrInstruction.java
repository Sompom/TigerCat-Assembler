package tigercat.instruction;

public class OrInstruction extends Instruction
{
  static final int OR_ENCODING = 0x1D;

  protected OrInstruction(String[] tokens, boolean encodingValid)
          throws InvalidDataWidthException, InstructionArgumentCountException, InvalidOpcodeException,
          InstructionSyntaxError, InvalidRegisterException, XmlLookupException {
    super(tokens, encodingValid, OR_ENCODING, THREE_ARGUMENTS);
  }

}
