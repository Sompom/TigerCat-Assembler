package tigercat.instruction;

import tigercat.instruction.Register.HalfReg;

public class MoveInstruction extends Instruction
{
  private Instruction[] childInstructions;
  
  @Override
  public Integer getSize()
  {
    int size = 0;
    for (Instruction childInstruction : childInstructions)
    {
      size += childInstruction.getSize();
    }
    return size;
  }
  
  @SuppressWarnings("Duplicates")
  @Override
  public Byte[] getMachineCode() throws UnencodeableImmediateException
  {
    Byte[] toReturn = new Byte[this.getSize() * SIZEOF_WORD / SIZEOF_BYTE];
    int current_pointer;
    Byte[][] child_encodings = new Byte[childInstructions.length][];
    
    for (int index = 0; index < childInstructions.length; index ++)
    {
      child_encodings[index] = childInstructions[index].getMachineCode();
    }
    
    current_pointer = 0;
    for (Byte[] child_encoding : child_encodings)
    {
      for (Byte aChild_encoding : child_encoding)
      {
        toReturn[current_pointer] = aChild_encoding;
        current_pointer++;
      }
    }
    
    return toReturn;
  }

  @SuppressWarnings("ConstantConditions")
  public MoveInstruction(String[] tokens, boolean encodingValid, Integer returnAddress)
          throws InvalidDataWidthException, InstructionSyntaxError, InstructionArgumentCountException,
          InvalidOpcodeException, InvalidRegisterException, XmlLookupException {
    // TODO: Top-level pseudo-instruction (dummy) constructor
    super(tokens, encodingValid, 0x00, TWO_ARGUMENTS);
    
    if (this.dataWidth == DataWidth.SINGLE_WORD)
    {
      childInstructions = new Instruction[1];
      // Construct the pseudo instruction by adding the mov argument to zero and storing into the mov destination 
      String child = "addw " + tokens[1] + " " + Instruction.REGISTER_PREFIX + Argument.ZERO_REG + " " + tokens[2]; 
      childInstructions[0] = Instruction.createInstruction(child, encodingValid, returnAddress);
    }
    else if (this.dataWidth == DataWidth.DOUBLE_WORD)
    {
      if (this.instructionType == DataType.REGISTER)
      {
        // Encode moving register to register by adding immediate 0x0
        childInstructions = new Instruction[1];
        String child = "addd " + tokens[1] + " " + tokens[2] + " " +  Instruction.IMMEDIATE_PREFIX + "0x0";
        childInstructions[0] = Instruction.createInstruction(child, encodingValid, returnAddress);
      } else if (this.instructionType == DataType.IMMEDIATE)
      {
        // Decompose to two movw instructions
        childInstructions = new Instruction[2];
        
        if (!encodingValid)
        {
          // Do not attempt to finish this instruction because the labels may not be replaced
          // Generate two garbage ADDW instructions to allow getSize to compute
          String child1 = "movw %a1l %al1";
          String child2 = "movw %a1l %al1";

          childInstructions[0] = Instruction.createInstruction(child1, encodingValid, returnAddress);
          childInstructions[1] = Instruction.createInstruction(child2, encodingValid, returnAddress);
          return;
        }
        
        Argument immediateArg = new Argument(tokens[2].substring(IMMEDIATE_PREFIX.length()), DataWidth.DOUBLE_WORD, DataType.IMMEDIATE);
        
        int immediate = immediateArg.getMachineCodeRepresentation();
        
        int lowerImmediate = immediate & 0xFFFF;
        int upperImmediate = (immediate & ~0xFFFF) >>> 16; 
        
        // For the strip the leading prefix character from the destination register
        String dest = tokens[1].substring(REGISTER_PREFIX.length());
        
        String child1 = "movw " + REGISTER_PREFIX + Register.ConvertDoubleRegNameToSingleReg(dest, HalfReg.LOWER_HALF_REG)
                        + " " + IMMEDIATE_PREFIX + "0x" + Integer.toUnsignedString(lowerImmediate, 16);
        String child2 = "movw " + REGISTER_PREFIX + Register.ConvertDoubleRegNameToSingleReg(dest, HalfReg.UPPER_HALF_REG)
            + " " + IMMEDIATE_PREFIX + "0x" + Integer.toUnsignedString(upperImmediate, 16);

        childInstructions[0] = Instruction.createInstruction(child1, encodingValid, returnAddress);
        childInstructions[1] = Instruction.createInstruction(child2, encodingValid, returnAddress);
      } else
      {
        assert false : "Invalid Instruction Type";
      }
    } else
    {
      assert false : "Undefined dataWidth";
    }
  }

}
