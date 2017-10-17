/*
 * Author       : Team TigerCat
 * Date         : 16 October 2017
 * Filename     : Instruction.java
 * Description  : Helper class for converting assembly string lines to machine code
 */

package tigercat.instruction;

/**
 * Helper class for converting assembly string lines to machine code
 *
 */
public abstract class Instruction
{

  /**
   * Record whether an instruction operates on single-word or double-word data
   */
  protected enum DataWidth
  {
    SINGLE_WORD(0), DOUBLE_WORD(1);

    protected int flag;

    DataWidth(int flag)
    {
      this.flag = flag;
    }

    public int getFlag()
    {
      return flag;
    }
  }

  /**
   * Record whether an instruction takes an immediate argument or not
   */
  protected enum DataType
  {
    IMMEDIATE(0), REGISTER(1);

    protected int flag;

    DataType(int flag)
    {
      this.flag = flag;
    }

    public int getFlag()
    {
      return flag;
    }
  }

  protected DataWidth dataWidth;
  protected DataType instructionType;

  /**
   * Return the machine code representation of this instruction
   * 
   * @return Machine code representation of this instruction
   */
  public abstract Byte[] getMachineCode();

  /**
   * Return the size, in words, of this instruction Currently all instructions
   * are two words (32-bits), but you never know...
   * 
   * @return The size of this instruction in machine words
   */
  public Integer getSize()
  {
    return 2;
  }

  /**
   * Converts the given line of assembly into an Instruction object
   * 
   * @param line
   *          Line of assembly which corresponds to a machine instruction
   * @return The newly-created instruction
   */
  public static Instruction createInstruction(String line)
  {
    // Switch statement on the first token of the line to create a new
    // instruction
    return null;
  }

  protected class Argument
  {
    protected Byte[] machineCodeRepresentation;

    public Byte[] getMachineCodeRepresentation()
    {
      return machineCodeRepresentation;
    }

    public Argument(String argument, DataWidth dataWidth, DataType argumentType) throws InvalidRegisterException
    {
      if (argumentType == DataType.IMMEDIATE)
      {
        machineCodeRepresentation = this.parseImmediate(argument);
      } else if (argumentType == DataType.REGISTER)
      {
        machineCodeRepresentation = this.parseRegister(argument, dataWidth);
      } else
      {
        throw new RuntimeException("Undefined Instruction Data Type");
      }
    }

    /**
     * Convert a register to its machine code representation
     * 
     * @param argument
     *          A string containing a register
     * @param dataWidth
     *          Whether a single- or double-word register should be encoded
     * @return The machine code encoding of the register
     * @throws InvalidRegisterException
     *           If an undefined register is encountered
     */
    protected Byte[] parseRegister(String argument, DataWidth dataWidth) throws InvalidRegisterException
    {
      if (dataWidth == DataWidth.SINGLE_WORD)
      {
        switch (argument)
        {
        case "$r1l":
          return new Byte[] { 0x0 };
        default:
          throw new InvalidRegisterException(argument);
        }
      } else if (dataWidth == DataWidth.DOUBLE_WORD)
      {
        switch (argument)
        {
        case "ret1":
          return new Byte[] { 0x0 };
        default:
          throw new InvalidRegisterException(argument);
        }
      } else
      {
        throw new RuntimeException("Undefined Data Width");
      }
    }

    /**
     * Convert an immediate value into a byte array The return value is always
     * 32-bits, even though no immediate can legally have that length
     * 
     * @param argument
     *          A string containing an immediate value
     * @return Machine Code representation of the immediate value
     */
    protected Byte[] parseImmediate(String argument)
    {
      Byte[] toReturn = new Byte[4];

      int arg = new Integer(argument);
      int mask = 0xFF000000;

      for (int index = 0; index < toReturn.length; index++)
      {
        toReturn[index] = (byte) (arg & mask);
        mask = mask >> 8;
      }

      return toReturn;
    }
  }
}
