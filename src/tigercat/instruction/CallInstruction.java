package tigercat.instruction;

public class CallInstruction extends Instruction
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

    for (int index = 0; index < childInstructions.length; index++)
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
  public CallInstruction(String[] tokens, boolean encodingValid, Integer returnAddress)
      throws InvalidDataWidthException, InstructionSyntaxError, InstructionArgumentCountException,
      InvalidOpcodeException, InvalidRegisterException, XmlLookupException
  {
    // TODO: Top-level pseudo-instruction (dummy) constructor
    super(tokens, encodingValid, 0x00, ONE_ARGUMENT);

    if (this.instructionType == DataType.REGISTER)
    {
      // push (returnAddress + 4) on to stack
      // jmp with address of register in the instruction
      childInstructions = new Instruction[2];
      String child1 = "pushd " + IMMEDIATE_PREFIX + "0x" + Integer.toHexString(returnAddress + 4); //add 4 to return to the next instruction
      String child2 = "jmp " + tokens[1];

      childInstructions[0] = Instruction.createInstruction(child1, encodingValid, returnAddress);
      childInstructions[0] = Instruction.createInstruction(child2, encodingValid, returnAddress);
    } else if (this.instructionType == DataType.IMMEDIATE)
    {
      // Decompose to two a push and a jmp
      childInstructions = new Instruction[2];

      if (!encodingValid)
      {
        // Do not attempt to finish this instruction because the labels may not be replaced
        // Generate two garbage instructions to allow getSize to compute
        String child1 = "pushd %arg1";
        String child2 = "jmp %arg1";

        childInstructions[0] = Instruction.createInstruction(child1, encodingValid, returnAddress);
        childInstructions[1] = Instruction.createInstruction(child2, encodingValid, returnAddress);
        return;
      }

      String child1 = "push " + IMMEDIATE_PREFIX + "0x" + Integer.toHexString(returnAddress + 4);
      String child2 = "jmp " + tokens[1];

      childInstructions[0] = Instruction.createInstruction(child1, encodingValid, returnAddress);
      childInstructions[1] = Instruction.createInstruction(child2, encodingValid, returnAddress);
    } else
    {
      assert false : "Invalid Instruction Type";
    }
  }

}
