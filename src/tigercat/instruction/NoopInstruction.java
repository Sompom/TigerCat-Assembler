package tigercat.instruction;

public class NoopInstruction extends Instruction
{
  Instruction childInstruction;
  
  @Override
  public Integer getSize()
  {
    return childInstruction.getSize();
  }
  
  @Override
  public Byte[] getMachineCode() throws UnencodeableImmediateException
  {
    return childInstruction.getMachineCode();
  }
  
  protected NoopInstruction(String[] tokens, boolean encodingValid)
          throws InvalidDataWidthException, InstructionArgumentCountException, InvalidOpcodeException,
          InstructionSyntaxError, InvalidRegisterException, XmlLookupException {
    super(tokens, encodingValid, 0x00, ZERO_ARGUMENTS);

    // Noop is encoded as an unconditionally false jump
    String child = new String("jmpf %arg1");
    
    childInstruction = Instruction.createInstruction(child, encodingValid);
  }
  
}
