package tigercat.instruction;

public class JumpInstruction extends Instruction
{
  static final int JUMP_ENCODING = 0x0C;

  public JumpInstruction(String[] tokens, boolean encodingValid, Integer returnAddress)
          throws InvalidDataWidthException, InstructionArgumentCountException, InvalidOpcodeException,
          InstructionSyntaxError, InvalidRegisterException, XmlLookupException {
    super(tokens, encodingValid, JUMP_ENCODING, ONE_ARGUMENT);

    if (!encodingValid)
    {
      return;
    }
    
    assert this.arguments.length == 1 : "jmp constructed with wrong number of arguments";
    
    // Jumps have a condition code "argument", so add that
    
    Argument[] jumpArguments = new Argument[2];
    jumpArguments[0] = new ConditionCode(tokens[0].substring(3), dataWidth);
    jumpArguments[1] = new Argument(tokens[1].substring(1), this.dataWidth, this.instructionType);
        
    this.arguments = jumpArguments;
  }

}
