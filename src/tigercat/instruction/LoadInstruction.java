package tigercat.instruction;

public class LoadInstruction extends Instruction
{
  static final int LOAD_ENCODING = 0x14;

  public LoadInstruction(String[] tokens, boolean encodingValid)
          throws InvalidDataWidthException, InstructionArgumentCountException, InvalidOpcodeException,
          InstructionSyntaxError, InvalidRegisterException, XmlLookupException {
    super(tokens, encodingValid, LOAD_ENCODING, TWO_ARGUMENTS);
  }

}
