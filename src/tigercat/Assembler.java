/*
 * Author       : Team TigerCat
 * Date         : 16 October 2017
 * Filename     : Assembler.java
 * Description  : Assembler for the TigerCat architecture 
 */

package tigercat;

import java.util.*;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import tigercat.instruction.*;

import static java.lang.System.exit;

public class Assembler
{
  /**
   * Memory address where the CPU will look for the first instruction
   */
  public static final int MACHINE_CODE_START = 0x0; // Machine code starts at the top of memory

  /**
   * Comments start with this symbol
   */
  public static final String COMMENT_PREFIX = "#";
  
  /**
   * Labels end with a colon
   */
  public static final String LABEL_SUFFIX = ":";
  
  /**
   * Constant values end with an equals sign
   */
  public static final String VALUE_SUFFIX = "=";
  
  /**
   * A label is defined by one or more capital letters or underscores,
   * followed by LABEL_SUFFIX
   */
  public static final String LABEL_REGEX = "\\s*[A-Z_0-9]+";
  
  public Assembler()
  {
    // For testability, the assembler should have no class-level data
  }
  
  /**
   * Convert the passed TigerCat assembly code to machine code
   * @param assembly Assembly code to assemble
   * @return A machine-code representation of the passed assembly
   */
  public byte[] assemble(String assembly)
  {
    byte[] machineCode = null;
    ArrayList<AssemblerException> exceptionList = new ArrayList<>();

    HashMap<String, Label> labelMapping = firstPass(assembly, exceptionList);
    if (exceptionList.size() > 0)
    {
      System.out.println("First-Pass Errors:");
      printExceptions(exceptionList);
      exit(1);
    }
    
    machineCode = secondPass(assembly, labelMapping, exceptionList);

    if(exceptionList.size() > 0) {
      System.out.println("Second-Pass Errors:");
      printExceptions(exceptionList);
      exit(1);
    }
    
    String[] orderedLabels = new String[labelMapping.keySet().size()];
    labelMapping.keySet().toArray(orderedLabels);
    Arrays.sort(orderedLabels);
    
    for (String labelName : orderedLabels)
    {
      System.out.println(labelName + ": 0x" + Integer.toHexString(labelMapping.get(labelName).getValue()));
    }
    
    return machineCode;
  }
  
  /**
   * Collect a mapping of label names (strings) to address (integers)
   * @param assembly Assembly lines to parse
   * @return Mapping of strings to addresses
   */
  protected HashMap<String, Label> firstPass(String assembly, ArrayList<AssemblerException> exceptionList)
  {
    HashMap<String, Label> labelMapping = new HashMap<String, Label>();
    Integer offsetAddress = MACHINE_CODE_START; // Offset from the first instruction
    int lineIndex = 0;
    
    //  For each line in the assembly body:
    //    Ignore lines which start with a comment.
    //    Determine if the line is a label or an instruction
    //      For instructions, add their size to the address counter
    //      For labels:
    //        If it is an address label, immediately store the name and address
    //        If it is a data label, create a new label record with the name and size
    //  For each label in the labelMapping:
    //    Ignore address-type labels
    //    Store address to data-type labels by using the offset at the end of the machine code
    //      and incrementing it by the size of each label encountered
    
    String[] lines = assembly.split(System.getProperty("line.separator"));

    for (lineIndex = 0; lineIndex < lines.length; lineIndex++) {
      try {
        String line = lines[lineIndex];

        // Ignore blank lines
        if (line.matches("^\\s*$")) {
          continue;
        }

        // Ignore comment lines
        if (line.matches("^\\s*" + COMMENT_PREFIX + ".*"))
        {
          continue;
        }
        
        // Get rid of end-of-line comments
        line = line.split(COMMENT_PREFIX)[0];
        
        line = line.trim();

        // Determine whether the line starts with a label
        // Labels are defined as whitespace, followed by a sequence of upper-case letters
        if (line.matches("^" + LABEL_REGEX + ".*")) {
          String labelName;
          
          line = line.trim();

          // A value is a label, followed by an equals sign, followed by a number in hexadecimal
          String constantValueRegex = "^" + LABEL_REGEX + VALUE_SUFFIX + "\\s*0x" + "[0-9A-Fa-f]+" + "$";
          
          // An address is a label, followed by a colon
          // For simplicity, we enforce below that a label must be on its own line
          String addressLabelRegex = "^" + LABEL_REGEX + LABEL_SUFFIX;
          
          if (line.matches(addressLabelRegex))
          {
            // Remove the colon
            labelName = line.substring(0, line.length() - 1);
          }
          else if (line.matches(constantValueRegex))
          {
            String[] tokens = line.split(VALUE_SUFFIX);
            labelName = tokens[0];
          } else
          {
            throw new InstructionSyntaxError("Label does not define a value (needs " + VALUE_SUFFIX + "),"
                + "nor an address (needs " + LABEL_SUFFIX +")");
          }

          if (labelMapping.containsKey(labelName)) {
            throw new DoubleDefinedLabelException(labelName);
          }

          Label toStore;
          
          // Check if the line is a constant value
          if (line.matches(constantValueRegex))
          {
            String[] tokens = line.split(VALUE_SUFFIX);
            
            // Remove any whitespace and the leading "0x"
            String value = tokens[1].trim().substring(2);
            
            toStore = new Label(labelName, Integer.parseUnsignedInt(value, 16));
            labelMapping.put(labelName, toStore);
            continue;
          }

          // For simplicity, this assembler requires labels be on their own line
          // A label is detected with the regex plus one more character, either a : or an =
          if (!((line.matches(addressLabelRegex + "\\s*$")))) {
            throw new InstructionSyntaxError("Labels must be on their own line");
          }
          String nextLine;
          if (lineIndex == lines.length - 1) {
            // This label is at the end of the assembly file. Why?
            // Implement this when a sensible solution has been found, otherwise
            // don't write assembly which does this!
            throw new NotImplementedException();
          }
          nextLine = lines[lineIndex + 1];
          // We need to peek the next line to check for a data declaration
          if (nextLine.matches("^\\s*\\.data")) {
            // TODO: Store data label
            throw new NotImplementedException();
          } else {
            // If not a data label, this is an address label
            toStore = new Label(labelName, offsetAddress);
          }

          labelMapping.put(labelName, toStore);
          continue;
        }

        // If none of the other patterns, line is either an assembly code or invalid
        // If it is invalid, trying to create an instruction will throw a useful exception
        offsetAddress += Instruction.createInstruction(line, false, offsetAddress).getSize();
      }
      catch (InstructionSyntaxError | InstructionArgumentCountException | InvalidOpcodeException
          | InvalidRegisterException | InvalidDataWidthException | DoubleDefinedLabelException
          | XmlLookupException e) {
      //for specific types of exceptions:
      //if (e instanceof InstructionSyntaxError) {...}
      
      //common actions for all exceptions
      //e.printStackTrace();
        e.setContext(lineIndex, lines[lineIndex]);
        exceptionList.add(e);
      }
      catch (NumberFormatException e)
      {
        UnencodeableImmediateException newException = new UnencodeableImmediateException("Integer parser threw an error " + e.getMessage() , 0);
        newException.setContext(lineIndex, lines[lineIndex]);
        exceptionList.add(newException);
      }
    }

    //printExceptions(exceptionList);
    return labelMapping;
  }

  private void printExceptions(ArrayList<AssemblerException> exceptionList) {
    System.out.println("Errors:");
    for (AssemblerException e : exceptionList) {
      System.out.println(e.getDiagnostic());
    }
  }

  protected byte[] secondPass(String assembly, HashMap<String, Label> labelMapping, ArrayList<AssemblerException> exceptionList)
  {
    ArrayList<Byte> machineCode = new ArrayList<Byte>();
    int lineIndex;
    Integer offsetAddress = MACHINE_CODE_START; // Offset from the first instruction


    //  For each line in the assembly body:
    //    Ignore lines which start with a comment. Strip comments from end-of-lines
    //    Determine if the line is a label or an instruction
    //      Assemble an instruction (to its 32-bit machine-code representation)
    //        Replace labels with addresses as encountered
    //        Throw an exception for undefined labels
    //        Write that machine code to the output
    //      Ignore labels
    //  Write all data-type label's bodies to the end of the output
    
    String[] lines = assembly.split(System.getProperty("line.separator"));
    
    for (lineIndex = 0; lineIndex < lines.length; lineIndex++)
    {
      try
      {
        String line = lines[lineIndex];
        
        // Ignore blank lines
        if (line.matches("^\\s*$"))
        {
          continue;
        }
        
        // Ignore comment lines
        if (line.matches("^\\s*" + COMMENT_PREFIX + ".*"))
        {
          continue;
        }
        
        // Ignore lines which start with a label
        // Labels are defined as whitespace, followed by a sequence of upper-case letters, followed by other stuff
        // These are syntax checked in the first pass. All we need is to ignore them.
        if (line.matches("^" + LABEL_REGEX + ".*"))
        {
          continue;
        }
        
        // Get rid of end-of-line comments
        line = line.split(COMMENT_PREFIX)[0];
        
        // Remove leading/trailing whitespace
        line = line.trim();
        
        // If we are here, the remainder should be assembly code
        // Replace labels with their values
        
        String[] tokens = line.split("\\s+");
        
        // If there is only one token, it could legally be a zero-argument instruction. No labels to replace
        if (tokens.length != 1)
        {
          // The only token which may legally be a label is the last one
          String lastArg = tokens[tokens.length - 1];
  
          // Check if the token doesn't look like a register
          if (!(lastArg.startsWith(Instruction.REGISTER_PREFIX)))
          {
            // Maybe it is an immediate?
            if (!(lastArg.startsWith(Instruction.IMMEDIATE_PREFIX)))
            {
              // Hopefully it is a label
              if (!(labelMapping.containsKey(lastArg)))
              {
                throw new UndefinedLabelException(lastArg);
              }
              
              // Replace the label before trying to construct machine code
              StringBuilder newLine = new StringBuilder();
              
              // Put the first arguments back together in the order they came
              for (int index = 0; index < tokens.length - 1; index++ )
              {
                newLine.append(tokens[index] + " ");
              }
              
              // Replace the label with its value
              newLine.append(Instruction.IMMEDIATE_PREFIX + "0x" + Integer.toHexString(labelMapping.get(lastArg).getValue()));
              
              line = newLine.toString();
            }
          }
        }

        // don't place short circuits
        // Construct the assembly instruction
        Instruction thisInstruction = Instruction.createInstruction(line, true, offsetAddress);
        offsetAddress += thisInstruction.getSize();
        machineCode.addAll(Arrays.asList((thisInstruction.getMachineCode())));
      }
      catch (UndefinedLabelException | UnencodeableImmediateException | InstructionArgumentCountException
              | InvalidOpcodeException | InstructionSyntaxError | InvalidRegisterException
              | InvalidDataWidthException | XmlLookupException e) {

        e.setContext(lineIndex, lines[lineIndex]);
        exceptionList.add(e);
      } catch (NumberFormatException e)
      {
        UnencodeableImmediateException newException = new UnencodeableImmediateException("Integer parser threw an error " + e.getMessage() , 0);
        newException.setContext(lineIndex, lines[lineIndex]);
        exceptionList.add(newException);
      }
    }
    
    // Convert ArrayList to byte[]
    byte[] toReturn = new byte[machineCode.size()];
    for (int index = 0; index < machineCode.size(); index ++)
    {
      toReturn[index] = machineCode.get(index);
    }
    return toReturn;
  }
  
}
