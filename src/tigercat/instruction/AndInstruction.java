package tigercat.instruction;

public class AndInstruction extends Instruction
{
  static final int AND_ENCODING = 0x1C;

  protected AndInstruction(String[] tokens, boolean encodingValid)
          throws InvalidDataWidthException, InstructionArgumentCountException, InvalidOpcodeException,
          InstructionSyntaxError, InvalidRegisterException, XmlLookupException {
    super(tokens, encodingValid, AND_ENCODING, THREE_ARGUMENTS);
  }

}
