package tigercat.instruction;

import java.util.HashMap;

import tigercat.Label;

public class AddInstruction extends Instruction
{
  static final int ADD_ENCODING = 0x00;
  static final int ADDC_ENCODING = 0x01;

  protected AddInstruction(String[] tokens, boolean encodingValid)
      throws InvalidDataWidthException, InstructionArgumentCountException, InvalidOpcodeException,
      InstructionSyntaxError, InvalidRegisterException
  {
    super(tokens, encodingValid, ADD_ENCODING, THREE_ARGUMENTS);
    
    if (tokens[0].startsWith("addc"))
    {
      opcode_encoding = ADDC_ENCODING;
    }
  }

}
