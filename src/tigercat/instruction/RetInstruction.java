package tigercat.instruction;

public class RetInstruction extends Instruction
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
  
  protected RetInstruction(String[] tokens, boolean encodingValid, Integer returnAddress)
          throws InvalidDataWidthException, InstructionArgumentCountException, InvalidOpcodeException,
          InstructionSyntaxError, InvalidRegisterException, XmlLookupException {
    super(tokens, encodingValid, 0x00, ZERO_ARGUMENTS);

    String child = "popd %IP";
    
    childInstruction = Instruction.createInstruction(child, encodingValid, returnAddress);
  }
  
}
