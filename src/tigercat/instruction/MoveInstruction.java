package tigercat.instruction;

import java.util.HashMap;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import tigercat.Assembler;
import tigercat.Label;

public class MoveInstruction extends TwoArgumentInstruction
{
  Instruction[] childInstructions;
  
  @Override
  public Integer getSize()
  {
    int size = 0;
    for (int index = 0; index < childInstructions.length; index ++)
    {
      size += childInstructions[index].getSize();
    }
    return size;
  }
  
  @Override
  public Byte[] getMachineCode()
  {
    Byte[] toReturn = new Byte[this.getSize() * SIZEOF_WORD / SIZEOF_BYTE];
    int current_pointer;
    Byte[][] child_encodings = new Byte[childInstructions.length][];
    
    for (int index = 0; index < childInstructions.length; index ++)
    {
      child_encodings[index] = childInstructions[index].getMachineCode();
    }
    
    current_pointer = 0;
    for (int index = 0; index < child_encodings.length; index ++)
    {
      for (int jdex = 0; jdex < child_encodings[index].length; jdex ++)
      {
        toReturn[current_pointer] = child_encodings[index][jdex];
        current_pointer ++;
      }
    }
    
    return toReturn;
  }

  public MoveInstruction(String[] tokens, HashMap<String, Label> labelMapping)
      throws InvalidDataWidthException, InstructionSyntaxError, InstructionArgumentCountException,
      InvalidOpcodeException, InvalidRegisterException
  {
    // TODO: Top-level pseudo-instruction (dummy) constructor
    super(tokens, labelMapping, 0x00);
    
    if (this.dataWidth == DataWidth.SINGLE_WORD)
    {
      childInstructions = new Instruction[1];
      String child = "addw " + Instruction.REGISTER_PREFIX + Argument.ZERO_REG + " " + tokens[1] + " " + tokens[2]; 
      childInstructions[0] = Instruction.createInstruction(child, labelMapping);
    }
    else if (this.dataWidth == DataWidth.DOUBLE_WORD)
    {
      childInstructions = new Instruction[2];
      // Decompose to two movw instructions
      // TODO: Handle double-word move
      throw new NotImplementedException();
    }
  }

}
