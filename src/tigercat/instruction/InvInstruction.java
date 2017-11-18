package tigercat.instruction;

public class InvInstruction extends Instruction
{
  static final int INV_ENCODING = 0x1F;

  protected InvInstruction(String[] tokens, boolean encodingValid, Integer returnAddress)
          throws InvalidDataWidthException, InstructionArgumentCountException, InvalidOpcodeException,
          InstructionSyntaxError, InvalidRegisterException, XmlLookupException {
    super(tokens, encodingValid, INV_ENCODING, TWO_ARGUMENTS);
    
    if (this.instructionType == DataType.IMMEDIATE
        && this.arguments[1].getMachineCodeRepresentation() > 0x7FFFF)
    {
      System.err.println("Attention: As of today (15 November, 2017),"
          + "the TigerCat hardware does not support invd with an immediate"
          + "larger than 0x7FFFF");
    }
  }

}
