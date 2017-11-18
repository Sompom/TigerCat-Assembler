package tigercat.instruction;

public class SlInstruction extends Instruction
{
  static final int SL_ENCODING = 0x0A;

  protected SlInstruction(String[] tokens, boolean encodingValid)
          throws InvalidDataWidthException, InstructionArgumentCountException, InvalidOpcodeException,
          InstructionSyntaxError, InvalidRegisterException, XmlLookupException {
    super(tokens, encodingValid, SL_ENCODING, THREE_ARGUMENTS);
  }

}
