package tigercat.instruction;

import java.util.HashMap;

import tigercat.Label;
import tigercat.instruction.Instruction.Argument;
import tigercat.instruction.Instruction.DataType;

public class ThreeArgumentInstruction extends Instruction
{

  protected ThreeArgumentInstruction(String[] tokens, HashMap<String, Label> labelMapping, int opcode_encoding)
      throws InvalidDataWidthException, InstructionArgumentCountException, InvalidOpcodeException, InstructionSyntaxError, InvalidRegisterException
  {
    super(tokens, labelMapping, opcode_encoding, THREE_ARGUMENTS);
  }

}
