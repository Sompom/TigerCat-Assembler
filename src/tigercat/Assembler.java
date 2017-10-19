/*
 * Author       : Team TigerCat
 * Date         : 16 October 2017
 * Filename     : Assembler.java
 * Description  : Assembler for the TigerCat architecture 
 */

package tigercat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import tigercat.instruction.Instruction;
import tigercat.instruction.InstructionArgumentCountException;
import tigercat.instruction.InstructionSyntaxError;
import tigercat.instruction.InvalidDataWidthException;
import tigercat.instruction.InvalidOpcodeException;
import tigercat.instruction.InvalidRegisterException;

public class Assembler
{
  /**
   * Memory address where the CPU will look for the first instruction
   */
  public static final int MACHINE_CODE_START = 0x0; // Machine code starts at the top of memory

  /**
   * Comments start with this symbol
   */
  static final String COMMENT_PREFIX = "#";
  
  public Assembler()
  {
    // For testability, the assembler should have no class-level data
  }
  
  /**
   * Convert the passed TigerCat assembly code to machine code
   * @param assembly Assembly code to assemble
   * @return A machine-code representation of the passed assembly
   * @throws InvalidDataWidthException 
   * @throws InvalidRegisterException 
   * @throws InstructionSyntaxError 
   * @throws InvalidOpcodeException 
   * @throws InstructionArgumentCountException 
   */
  public byte[] assemble(String assembly) throws InstructionArgumentCountException, InvalidOpcodeException, InstructionSyntaxError, InvalidRegisterException, InvalidDataWidthException
  {
    HashMap<String, Label> labelMapping = firstPass(assembly);
    Byte[] machineCode = secondPass(assembly, labelMapping);
    
    // Convert Byte[] to byte[]
    byte[] toReturn = new byte[machineCode.length];
    for (int index = 0; index < machineCode.length; index ++)
    {
      toReturn[index] = machineCode[index];
    }
    
    return toReturn;
  }
  
  /**
   * Collect a mapping of label names (strings) to address (integers)
   * @param assembly Assembly lines to parse
   * @return Mapping of strings to addresses
   * @throws InvalidDataWidthException 
   * @throws InvalidRegisterException 
   * @throws InstructionSyntaxError 
   * @throws InvalidOpcodeException 
   * @throws InstructionArgumentCountException 
   */
  protected HashMap<String, Label> firstPass(String assembly) throws InstructionArgumentCountException, InvalidOpcodeException, InstructionSyntaxError, InvalidRegisterException, InvalidDataWidthException
  {
    HashMap<String, Label> labelMapping = new HashMap<String, Label>();
    Integer offsetAddress = MACHINE_CODE_START; // Offset from the first instruction
    
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
    for (int lineIndex = 0; lineIndex < lines.length; lineIndex++)
    {
      String line = lines[lineIndex];
      
      // Ignore blank lines
      if (line.matches("^\\s*$"))
      {
        continue;
      }
      
      // Ignore comment lines
      if (line.matches("^\\s*" + COMMENT_PREFIX))
      {
        continue;
      }
      
      // Determine whether the line starts with a label
      // Labels are defined as whitespace, followed by a sequence of upper-case letters followed by a colon
      if (line.matches("^\\s*[A-Z]*:"))
      {
        String labelName = line.trim();
        // Remove colon
        labelName = labelName.substring(0, labelName.length() - 1);
        
        // For simplicity, this assembler requires labels be on their own line
        if (!(line.matches("^\\s*[A-Z]*:\\s$")))
        {
          throw new InstructionSyntaxError("Labels must be on their own line");
        }
        String nextLine;
        if (lineIndex < lines.length - 1)
        {
          // This label is at the end of the assembly file. Why?
          // Implement this when a sensible solution has been found, otherwise
          // don't write assembly which does this!
          throw new NotImplementedException();
        }
        nextLine = lines[lineIndex + 1];
        // We need to peek the next line to check for a data declaration
        if (nextLine.matches("^\\s*\\.data"))
        {
          // TODO: Store data label
          throw new NotImplementedException();
        } else
        {
          // If not a data label, this is an address label
          Label toStore = new Label(labelName, offsetAddress);
          labelMapping.put(labelName, toStore);
        }
        continue;
      }
      
      // If none of the other patterns, line is either an assembly code or invalid
      // If it is invalid, trying to create an instruction will throw a useful exception
      offsetAddress = Instruction.createInstruction(line, null).getSize();
    }
    
    return labelMapping;
  }
  
  protected Byte[] secondPass(String assembly, HashMap<String, Label> labelMapping)
  {
    ArrayList<Byte> machineCode = new ArrayList<Byte>();
    
    //  For each line in the assembly body:
    //    Ignore lines which start with a comment. Strip comments from end-of-lines
    //    Determine if the line is a label or an instruction
    //      Assemble an instruction (to its 32-bit machine-code representation)
    //        Replace labels with addresses as encountered
    //        Throw an exception for undefined labels
    //        Write that machine code to the output
    //      Ignore labels
    //  Write all data-type label's bodies to the end of the output
    
    return machineCode.toArray(null);
  }
  
}
