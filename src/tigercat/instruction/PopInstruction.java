package tigercat.instruction;

public class PopInstruction extends Instruction
{
  static final int POP_ENCODING = 0x11;

  public PopInstruction(String[] tokens, boolean encodingValid)
          throws InvalidDataWidthException, InstructionArgumentCountException, InvalidOpcodeException,
          InstructionSyntaxError, InvalidRegisterException, XmlLookupException {
    super(tokens, encodingValid, POP_ENCODING, ONE_ARGUMENT);
  }

}
