package tigercat.instruction;

import java.util.HashMap;

import tigercat.Label;

public class TwoArgumentInstruction extends Instruction
{

  public TwoArgumentInstruction(String[] tokens, HashMap<String, Label> labelMapping, int opcode_encoding)
      throws InvalidDataWidthException, InstructionSyntaxError, InstructionArgumentCountException, InvalidOpcodeException, InvalidRegisterException
  {
    super(tokens, labelMapping, opcode_encoding, TWO_ARGUMENTS);
  }

}
