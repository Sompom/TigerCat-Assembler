package tigercat.instruction;

public class SsrInstruction extends Instruction
{
  static final int SSR_ENCODING = 0x08;

  protected SsrInstruction(String[] tokens, boolean encodingValid)
          throws InvalidDataWidthException, InstructionArgumentCountException, InvalidOpcodeException,
          InstructionSyntaxError, InvalidRegisterException, XmlLookupException {
    super(tokens, encodingValid, SSR_ENCODING, THREE_ARGUMENTS);
  }

}
