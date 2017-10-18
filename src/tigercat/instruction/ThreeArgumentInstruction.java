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
    super(tokens, labelMapping);
    
    this.arguments = new Argument[THREE_ARGUMENTS];
    
    this.opcode_encoding = opcode_encoding;
    
    if (tokens.length < 3)
    {
      throw new InstructionArgumentCountException();
    }
    
    checkInstructionSyntax(tokens, THREE_ARGUMENTS);

    String opcode = tokens[0];
    String dest = tokens[1];
    String lhs = tokens[2];
    String rhs = tokens[3];

    // Decide whether we are using immediate data or not
    // The only argument which can validly be immediate is rhs,
    // and the syntax check has already said the instruction is valid
    if (rhs.startsWith(IMMEDIATE_PREFIX))
    {
      this.instructionType = DataType.IMMEDIATE;
    } else if (rhs.startsWith(REGISTER_PREFIX))
    {
      this.instructionType = DataType.REGISTER;
    } else
    {
      // TODO: Handle label lookup
      throw new InstructionSyntaxError("Undefined prefix on " + rhs);
    }

    arguments[0] = new Argument(dest.substring(1), this.dataWidth, this.instructionType);
    arguments[1] = new Argument(lhs.substring(1), this.dataWidth, this.instructionType);
    arguments[2] = new Argument(rhs.substring(1), this.dataWidth, this.instructionType);
  }

}
