package tigercat.instruction;

public class StoreInstruction extends Instruction
{
  static final int STORE_ENCODING = 0x15;

  public StoreInstruction(String[] tokens, boolean encodingValid)
          throws InvalidDataWidthException, InstructionArgumentCountException, InvalidOpcodeException,
          InstructionSyntaxError, InvalidRegisterException, XmlLookupException {
    super(tokens, encodingValid, STORE_ENCODING, TWO_ARGUMENTS);
  }

}
