package tigercat.instruction;

import java.util.HashMap;

import tigercat.Label;

public class SubInstruction extends Instruction
{
  static final int SUB_ENCODING = 0x02;
  static final int SUBC_ENCODING = 0x03;

  public SubInstruction(String[] tokens, HashMap<String, Label> labelMapping)
      throws InvalidDataWidthException, InstructionArgumentCountException, InvalidOpcodeException,
      InstructionSyntaxError, InvalidRegisterException
  {
    super(tokens, labelMapping, SUB_ENCODING, THREE_ARGUMENTS);
    
    if (tokens[0].startsWith("subc"))
    {
      opcode_encoding = SUBC_ENCODING;
    }
  }

}
