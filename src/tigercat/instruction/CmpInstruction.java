package tigercat.instruction;

public class CmpInstruction extends Instruction
{
  static final int CMP_ENCODING = 0x0E;

  protected CmpInstruction(String[] tokens, boolean encodingValid)
          throws InvalidDataWidthException, InstructionArgumentCountException, InvalidOpcodeException,
          InstructionSyntaxError, InvalidRegisterException, XmlLookupException {
    super(tokens, encodingValid, CMP_ENCODING, TWO_ARGUMENTS);

    assert this.arguments.length == 2 : "CMP should have two real arguments";
    
    // CMP needs a dummy destination argument, so add it
    Argument[] cmpArguments = new Argument[this.arguments.length + 1];
    
    if (this.dataWidth == DataWidth.SINGLE_WORD)
    {
      cmpArguments[0] = new Argument("r1l", this.dataWidth, DataType.REGISTER);
    } else if (this.dataWidth == DataWidth.DOUBLE_WORD)
    {
      cmpArguments[0] = new Argument("ret1", this.dataWidth, DataType.REGISTER);
    } else
    {
      assert false : "Undefined DataWidth";
    }
    
    cmpArguments[1] = this.arguments[0];
    cmpArguments[2] = this.arguments[1];
    
    this.arguments = cmpArguments;
  }

}
