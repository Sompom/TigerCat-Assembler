package tigercat.instruction;

import java.util.HashMap;

import tigercat.Label;

public class AddInstruction extends Instruction
{
  public static byte ADD_OPCODE = 0x00;
  
  protected AddInstruction(String line, HashMap<String, Label> labelMapping)
      throws InstructionArgumentCountException,
      InvalidOpcodeException,
      InstructionSyntaxError,
      InvalidRegisterException
  {
    super(line, labelMapping);
    if (labelMapping == null)
    {
      this.arguments = null;
      return;
    }

    this.arguments = new Argument[THREE_ARGUMENTS];
    this.machineCode = 0;
    String[] tokens = line.split("\\s+");
    
    checkInstructionSyntax(tokens, THREE_ARGUMENTS, "add".length());
    
    String opcode = tokens[0];
    String dest = tokens[1];
    String lhs = tokens[2];
    String rhs = tokens[3];
    
    assert opcode.startsWith("add") : "AddInstruction called with invalid opcode: " + tokens[0];
    
    this.machineCode &= ADD_OPCODE << SHIFT_OPCODE;
    
    // Add the data-width flag to the machine code
    if (opcode.endsWith("w"))
    {
      this.dataWidth = DataWidth.SINGLE_WORD;
    } else if (opcode.endsWith("d"))
    {
      this.dataWidth = DataWidth.DOUBLE_WORD;
    } else
    {
      throw new InvalidOpcodeException(opcode);
    }
    
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
    
    arguments[0] = new Argument(dest, this.dataWidth, this.instructionType);
    arguments[1] = new Argument(lhs, this.dataWidth, this.instructionType);
    arguments[2] = new Argument(rhs, this.dataWidth, this.instructionType);
  }
}
