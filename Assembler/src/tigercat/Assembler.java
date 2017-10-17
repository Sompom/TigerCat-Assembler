/*
 * Author       : Team TigerCat
 * Date         : 16 October 2017
 * Filename     : Assembler.java
 * Description  : Assembler for the TigerCat architecture 
 */

package tigercat;

import java.util.HashMap;

public class Assembler
{
  /**
   * Memory address where the CPU will look for the first instruction
   */
  public static final int MACHINE_CODE_START = 0x0; // Machine code starts at the top of memory
  
  public Assembler()
  {
    // For testability, the assembler should have no class-level data
  }
  
  /**
   * Convert the passed TigerCat assembly code to machine code
   * @param assembly Assembly code to assemble
   * @return A machine-code representation of the passed assembly
   */
  public String assemble(String assembly)
  {
    HashMap<String, Label> labelMapping = firstPass(assembly);
    String machineCode = secondPass(assembly, labelMapping);
    return machineCode;
  }
  
  /**
   * Collect a mapping of label names (strings) to address (integers)
   * @param assembly Assembly lines to parse
   * @return Mapping of strings to addresses
   */
  protected HashMap<String, Label> firstPass(String assembly)
  {
    HashMap<String, Label> labelMapping = new HashMap<String, Label>();
    Integer offsetAddress = MACHINE_CODE_START; // Offset from the first instruction
    
    //  For each line in the assembly body:
    //    Determine if the line is a label or an instruction
    //      For instructions, add their size to the address counter
    //      For labels:
    //        If it is an address label, immediately store the name and address
    //        If it is a data label, create a new label record with the name and size
    //  For each label in the labelMapping:
    //    Ignore address-type labels
    //    Store address to data-type labels by using the offset at the end of the machine code
    //      and incrementing it by the size of each label encountered
    
    return labelMapping;
  }
  
  protected String secondPass(String assembly, HashMap<String, Label> labelMapping)
  {
    StringBuilder machineCode = new StringBuilder();
    
    //  For each line in the assembly body:
    //    Determine if the line is a label or an instruction
    //      Assemble an instruction (to its 32-bit machine-code representation)
    //        Replace labels with addresses as encountered
    //        Throw an exception for undefined labels
    //        Write that machine code to the output
    //      Ignore labels
    //  Write all data-type label's bodies to the end of the output
    
    return machineCode.toString();
  }
  
}
