package tigercat.instruction;

public class JumpInstruction extends Instruction
{
  static final int JUMP_ENCODING = 0x0C;

  public JumpInstruction(String[] tokens, boolean encodingValid)
          throws InvalidDataWidthException, InstructionArgumentCountException, InvalidOpcodeException,
          InstructionSyntaxError, InvalidRegisterException, XmlLookupException {
    super(tokens, encodingValid, JUMP_ENCODING, TWO_ARGUMENTS);
  }

}
