package tigercat.instruction;

public class PushInstruction extends Instruction
{
  static final int PUSH_ENCODING = 0x10;

  public PushInstruction(String[] tokens, boolean encodingValid, Integer returnAddress)
          throws InvalidDataWidthException, InstructionArgumentCountException, InvalidOpcodeException,
          InstructionSyntaxError, InvalidRegisterException, XmlLookupException {
    super(tokens, encodingValid, PUSH_ENCODING, ONE_ARGUMENT);
  }

}
