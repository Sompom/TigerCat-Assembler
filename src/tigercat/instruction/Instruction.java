/*
 * Author       : Team TigerCat
 * Date         : 16 October 2017
 * Filename     : Instruction.java
 * Description  : Helper class for converting assembly string lines to machine code
 */

package tigercat.instruction;

import org.w3c.dom.Document;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import tigercat.Assembler;

import javax.xml.crypto.Data;

/**
 * Helper class for converting assembly string lines to machine code
 *
 */
public abstract class Instruction
{

  /**
   * Every instruction is four bytes (32-bits, two words)
   */
  static final int BYTES_PER_INSTRUCTION = 4;

  // Size Definitions (in bits)
  static final int SIZEOF_INSTRUCTION               = 32;
  static final int SIZEOF_OPCODE                    = 5;
  static final int SIZEOF_SIZE_FLAG                 = 1;
  static final int SIZEOF_TYPE_FLAG                 = 1;
  static final int SIZEOF_SINGLE_WORD_REG_ENCODING  = 4;
  static final int SIZEOF_DOUBLE_WORD_REG_ENCODING  = 3;

  static final int SIZEOF_WORD                      = 16;
  static final int SIZEOF_BYTE                      = 8;
  
  // Bitshift Definitions
  static final int SHIFT_OPCODE     = SIZEOF_INSTRUCTION - SIZEOF_OPCODE;
  static final int SHIFT_SIZE_FLAG  = SHIFT_OPCODE - SIZEOF_SIZE_FLAG;
  static final int SHIFT_TYPE_FLAG  = SHIFT_SIZE_FLAG - SIZEOF_TYPE_FLAG;
  
  // Used to avoid magic numbers, at least a bit
  static final int THREE_ARGUMENTS = 3;
  static final int TWO_ARGUMENTS = 2;
  static final int ONE_ARGUMENT = 1;
  static final int ZERO_ARGUMENTS = 0;
  
  // What tokens the assembly expects
  public static final String REGISTER_PREFIX = "%";
  public static final String IMMEDIATE_PREFIX = "$";

  // The XML lookup file uri and DOM
  protected static final String  LOOKUP_FILE_URI = "./src/magicNumbers.xml";
  protected static Document lookupDoc = null;

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

  protected int opcode_encoding;
  protected DataWidth dataWidth;
  protected DataType instructionType;
  protected Argument[] arguments;
  
  protected int machineCode;

  /**
   * Return the machine code representation of this instruction
   * 
   * For the default implementation, it is expected that machineCode already contains the opcode
   * All other portions filled in based on local variables!
   * 
   * @return Machine code representation of this instruction
   * @throws UnencodeableImmediateException If the immediate is determined to be unencodable
   */
  public Byte[] getMachineCode() throws UnencodeableImmediateException
  {
    assert arguments != null : "Instruction defined with no labelMapping. Cannot get machine code.";

    this.machineCode |= opcode_encoding << SHIFT_OPCODE;
    
    this.machineCode |= dataWidth.flag << SHIFT_SIZE_FLAG;
    
    this.machineCode |= instructionType.flag << SHIFT_TYPE_FLAG;
    
    int shiftDistance = SHIFT_TYPE_FLAG;
    
    // Loop over all the register arguments and encode them
    for (int index = 0; index < arguments.length - 1; index ++)
    {
      // All but the last argument must be registers
      assert arguments[index].getArgumentType() == DataType.REGISTER : "Expected register argument";
      
      shiftDistance -= arguments[index].getEncodingSize();
      this.machineCode |= arguments[index].getMachineCodeRepresentation() << shiftDistance;
    }

    // If this instruction actually has arguments...
    if (!(arguments.length == 0))
    {
      //handle the last argument, which is an immediate
      int lastArg = arguments.length - 1;
      switch(arguments[lastArg].argumentType)
      {
      case REGISTER:
        // Shift in the register, as normal
        shiftDistance -= arguments[lastArg].getEncodingSize();
        this.machineCode |= arguments[lastArg].getMachineCodeRepresentation() << shiftDistance;
        break;
      case IMMEDIATE:
        // To encode an immediate value:
        // 1. Check if the immediate is too large to be encoded with the bits remaining.
        // 2. If the instruction uses single-word data, check for an immediate larger than
        //    16 bits
        // Throw an UnencodeableImmediateException if either above case is true
        // 3. bitwise or the immediate into place
  
        int immediateValue = arguments[lastArg].machineCodeRepresentation;
        
        // Create a mask with ones for all the bits we have already used
        // Conveniently, shiftDistance is the number of bits we have left
        int mask = ~((int)Math.pow(2, shiftDistance) - 1);
        
        // AND the mask with the immediate to encode
        // If the result is non-zero, the immediate is too large
        if (!((immediateValue & mask) == 0))
        {
          throw new UnencodeableImmediateException("Immediate too large to be encoded", immediateValue); 
        }
        
        if (this.dataWidth == DataWidth.SINGLE_WORD)
        {
          // If this is a single-word instruction
          // Create a mask with ones in the high 16-bits and zeros in the low 16-bits
          // As above, AND the mask with the immediate. If the result is non-zero, the immediate is too large 
          mask = ~0xFFFF;
          if (!((immediateValue & mask) == 0))
          {
            throw new UnencodeableImmediateException("Immediate too large for single-word instruction", immediateValue); 
          }
  
          // This immediate has passed the checks, so should be valid to encode
          // (Note: The immediate shall be right-aligned. Padding, if present, is before the immediate.)
          this.machineCode |= immediateValue;
        } else if (this.dataWidth == DataWidth.DOUBLE_WORD)
        {
          // The only check in the double word case, done above, is that the immediate
          // is not too large for the bits remaining.
          this.machineCode |= immediateValue;
        } else
        {
          assert false : "Unreachable code -- This should have already been checked";
        }
        
        break;
      }
    }
    
    return convertIntToByteArray(this.machineCode);
  }

  /**
   * Return a byte array representing the number. Converts a number from java's internal
   * form to a byte array for use in the assembled program.
   *
   * @param input The number to convert to bytes
   * @return A little-endian byte-array representation of the number
   */
  public static Byte[] convertIntToByteArray(int input)
  {
    Byte[] toReturn = new Byte[4];
    int mask = 0xFF000000;

    // Takes each byte in turn of the input and stores it into the toReturn array
    for (int index = 0; index < toReturn.length; index++)
    {
      toReturn[index] = (byte) ((input & mask) >> (8 * (toReturn.length - 1 - index)));
      mask = mask >>> 8; //logical right shift
    }

    return toReturn;
  }

  /**
   * Return the size, in words, of this instruction Currently all individual instructions
   * are two words (32-bits), but pseudo instructions may be longer
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
   * @param line Line of assembly which corresponds to a machine instruction
   * @param encodingValid Whether labels have been replaced yet
   * @return The newly-created instruction
   * @throws InvalidRegisterException 
   * @throws InstructionSyntaxError 
   * @throws InvalidOpcodeException 
   * @throws InstructionArgumentCountException 
   * @throws InvalidDataWidthException 
   */
  public static Instruction createInstruction(String line, boolean encodingValid)
          throws InstructionArgumentCountException,
          InvalidOpcodeException,
          InstructionSyntaxError,
          InvalidRegisterException,
          InvalidDataWidthException,
          XmlLookupException {
    String[] tokens = line.split("\\s+");
    String opcode = tokens[0];
    
    if (opcode.matches("^noop$"))
    {
      return new NoopInstruction(tokens, encodingValid);
    }
    if (opcode.matches("^debug$"))
    {
      return new DebugInstruction(tokens, encodingValid);
    }
    if (opcode.matches("^add.$"))
    {
      return new AddInstruction(tokens, encodingValid);
    }
    if (opcode.matches("^addc.$"))
    {
      return new AddInstruction(tokens, encodingValid);
    }
    if (opcode.matches("^sub.$"))
    {
      return new SubInstruction(tokens, encodingValid);
    }
    if (opcode.matches("^subc.$"))
    {
      return new SubInstruction(tokens, encodingValid);
    }
    if (opcode.matches("^convs$"))
    {
      return new ConvsInstruction(tokens, encodingValid);
    }
    if (opcode.matches("^convu$"))
    {
      // There are no plans to implement this
      throw new InvalidOpcodeException(opcode + " not assembleable: Use a movw $0x0 to the top bits");
    }
    if (opcode.matches("^ssr.$"))
    {
      return new SsrInstruction(tokens, encodingValid);
    }
    if (opcode.matches("^sur.$"))
    {
      return new SurInstruction(tokens, encodingValid);
    }
    if (opcode.matches("^sl.$"))
    {
      return new SlInstruction(tokens, encodingValid);
    }
    if (opcode.matches("^push.$"))
    {
      return new PushInstruction(tokens, encodingValid);
    }
    if (opcode.matches("^pop.$"))
    {
      return new PopInstruction(tokens, encodingValid);
    }
    if (opcode.matches("^mov.$"))
    {
      return new MoveInstruction(tokens, encodingValid);
    }
    if (opcode.matches("^load.$"))
    {
      return new LoadInstruction(tokens, encodingValid);
    }
    if (opcode.matches("^sto.$"))
    {
      return new StoreInstruction(tokens, encodingValid);
    }
    if (opcode.matches("^and.$"))
    {
      return new AndInstruction(tokens, encodingValid);
    }
    if (opcode.matches("^or.$"))
    {
      return new OrInstruction(tokens, encodingValid);
    }
    if (opcode.matches("^xor.$"))
    {
      return new XorInstruction(tokens, encodingValid);
    }
    if (opcode.matches("^inv.$"))
    {
      return new InvInstruction(tokens, encodingValid);
    }
    if (opcode.matches("^jmp.{0,2}$"))
    {
      return new JumpInstruction(tokens, encodingValid);
    }
    if (opcode.matches("^cmp.$"))
    {
      return new CmpInstruction(tokens, encodingValid);
    }
    if (opcode.matches("^readwcc$"))
    {
      // TODO: Implement
      throw new InvalidOpcodeException("opcode not implemented: " + opcode);
    }
    if (opcode.matches("^call$"))
    {
      // TODO: Implement
      throw new InvalidOpcodeException("opcode not implemented: " + opcode);
    }
    if (opcode.matches("^ret$"))
    {
      // TODO: Implement
      throw new InvalidOpcodeException("opcode not implemented: " + opcode);
    }

    throw new InvalidOpcodeException("Unable to create instruction from: " + line);
  }
  
  /**
   * Create an Instruction from the given string
   * 
   * If encodingValid is false, getMachineCode() is undefined
   * 
   * @param tokens The instruction to create
   * @param encodingValid Whether the passed token[] should be convertible to machine code
   *                      (I.e., whether labels have been replaced
   * @throws InvalidDataWidthException If the instruction specifies an unrecognized data width
   * @throws InstructionSyntaxError 
   * @throws InstructionArgumentCountException 
   * @throws InvalidOpcodeException 
   * @throws InvalidRegisterException 
   */
  protected Instruction(String[] tokens, boolean encodingValid, int opcode_encoding, int num_args)
          throws InvalidDataWidthException, InstructionSyntaxError, InstructionArgumentCountException,
          InvalidOpcodeException, InvalidRegisterException, XmlLookupException {
    this.machineCode = 0;
    this.arguments = new Argument[num_args];

    String opcode = tokens[0];

    // We should have the correct number of arguments plus the actual opcode
    if (tokens.length != num_args + 1)
    {
      throw new InstructionArgumentCountException(num_args, tokens.length - 1);
    }

    if (num_args == 0)
    {
      // Zero argument instructions don't have a data width, per-se
      this.dataWidth = DataWidth.SINGLE_WORD;
    }
    else {
      // Add the data-width flag to the machine code
      if (opcode.endsWith("w")) {
        this.dataWidth = DataWidth.SINGLE_WORD;
      } else if (opcode.endsWith("d")) {
        this.dataWidth = DataWidth.DOUBLE_WORD;
      } else {
        if (opcode.startsWith("jmp"))
        {
          this.dataWidth = DataWidth.DOUBLE_WORD;
        } else if (opcode.matches("^convs$"))
        {
          this.dataWidth = DataWidth.SINGLE_WORD;
        }
        else
        {
          throw new InvalidDataWidthException(opcode);
        }
      }
    }
    
    if (encodingValid)
    {
      // The syntax checker requires labels to be replaced with values
      checkInstructionSyntax(tokens);
    }

    this.opcode_encoding = opcode_encoding;
    String last_arg;
    last_arg = tokens[num_args];

    // Decide whether we are using immediate data or not
    // The only argument which can validly be immediate is the last one,
    // and the syntax check has already said the instruction is valid
    if (num_args == 0)
    {
      // Zero argument instructions don't have a data type
      this.instructionType = DataType.IMMEDIATE;
    } else if (last_arg.startsWith(IMMEDIATE_PREFIX))
    {
      this.instructionType = DataType.IMMEDIATE;
    } else if (last_arg.startsWith(REGISTER_PREFIX))
    {
      this.instructionType = DataType.REGISTER;
    } else if (last_arg.matches("^" + Assembler.LABEL_REGEX + "$"))
    {
      // Might be a label. Assume Immediate
      this.instructionType = DataType.IMMEDIATE;
    } else
    {
      // Basic idea is it is not a register, not an immediate, and isn't all caps (not a label)
      throw new InstructionSyntaxError("Invalid token: " + last_arg);
    }
    
    // If the encoding is not valid, label values have not been set yet, meaning
    // we should not continue to generate machine code
    if (!(encodingValid) || num_args == 0)
    {
      return;
    }
    
    // All but the last argument are certainly registers
    for (int index = 0; index < num_args - 1; index++) {
      arguments[index] = new Argument(tokens[index + 1].substring(1), this.dataWidth, DataType.REGISTER);
    }

    // The last argument may be an immediate, depending on the type of instruction
    arguments[num_args - 1] = new Argument(last_arg.substring(1), this.dataWidth, this.instructionType);
  }

  /**
   * Checks a given instruction against design invariants, throwing an exception if the
   * requirements are not met
   *
   * More syntax checking ought to be moved into this function
   *
   * @param tokens The instruction to check
   * @throws InstructionSyntaxError Triggers if an immediate was found where we weren't expecting one
   * or a token we could not classify was found.
   */
  protected static void checkInstructionSyntax(String[] tokens)
      throws InstructionSyntaxError
  {
    // Opcode broken out for clarity
    @SuppressWarnings("unused")
    String opcode = tokens[0];
    
    // Check that arguments are either:
    //   A register
    //   An immediate and the last argument
    for (int index = 1; index < tokens.length; index ++)
    {
      if (tokens[index].startsWith(REGISTER_PREFIX))
      {
        // No problem. This may be a register.
        continue;
      }
      
      if (tokens[index].startsWith(IMMEDIATE_PREFIX))
      {
        if (index == tokens.length - 1)
        {
          // No problem. This is the last argument and may be an immediate
          continue;
        }
        else 
        {
          throw new InstructionSyntaxError("Immediate encountered at non-end-of-line");
        }
      }
      throw new InstructionSyntaxError("Invalid token encountered: " + tokens[index]);
    }
  }

}
