package tigercat.instruction;

public class ConvsInstruction extends Instruction
{
  static final int CONVS_ENCODING = 0x0B;

  protected ConvsInstruction(String[] tokens, boolean encodingValid, Integer returnAddress)
          throws InvalidDataWidthException, InstructionArgumentCountException, InvalidOpcodeException,
          InstructionSyntaxError, InvalidRegisterException, XmlLookupException {
    super(tokens, encodingValid, CONVS_ENCODING, TWO_ARGUMENTS);
    
    if (this.dataWidth == DataWidth.DOUBLE_WORD)
    {
      throw new InstructionSyntaxError("Convs is not defined for double-word data");
    }
  }

}
