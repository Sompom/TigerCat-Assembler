package tigercat.instruction;

public class SurInstruction extends Instruction
{
  static final int SUR_ENCODING = 0x09;

  protected SurInstruction(String[] tokens, boolean encodingValid)
          throws InvalidDataWidthException, InstructionArgumentCountException, InvalidOpcodeException,
          InstructionSyntaxError, InvalidRegisterException, XmlLookupException {
    super(tokens, encodingValid, SUR_ENCODING, THREE_ARGUMENTS);
  }

}
