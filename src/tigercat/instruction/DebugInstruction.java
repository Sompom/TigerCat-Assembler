package tigercat.instruction;

public class DebugInstruction extends Instruction
{
  static final int DEBUG_ENCODING = 0x07;

  protected DebugInstruction(String[] tokens, boolean encodingValid)
          throws InvalidDataWidthException, InstructionArgumentCountException, InvalidOpcodeException,
          InstructionSyntaxError, InvalidRegisterException, XmlLookupException {
    super(tokens, encodingValid, DEBUG_ENCODING, ZERO_ARGUMENTS);
  }

}
