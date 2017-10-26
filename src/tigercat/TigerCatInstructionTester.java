package tigercat;

import static org.junit.Assert.*;

import java.util.HashMap;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import tigercat.instruction.Instruction;
import tigercat.instruction.InstructionArgumentCountException;
import tigercat.instruction.InstructionSyntaxError;
import tigercat.instruction.InvalidDataWidthException;
import tigercat.instruction.InvalidOpcodeException;
import tigercat.instruction.InvalidRegisterException;
import tigercat.instruction.UnencodeableImmediateException;

public class TigerCatInstructionTester
{
  @Rule
  public final ExpectedException exception = ExpectedException.none();

  @Test
  public void testConvertIntToByteArray1()
  {
    int test = 0x55AA55AA;
    Byte[] toCheck = Instruction.convertIntToByteArray(test);
    
    Assert.assertArrayEquals(new Byte[]{0x55, (byte) 0xAA, 0x55, (byte) 0xAA}, toCheck);
  }

  @Test
  public void testConvertIntToByteArray2()
  {
    int test = 0xFEDCBA98;
    Byte[] toCheck = Instruction.convertIntToByteArray(test);
    
    Assert.assertArrayEquals(new Byte[]{(byte) 0xFE, (byte) 0xDC, (byte) 0xBA, (byte) 0x98}, toCheck);
  }

  @Test
  public void testCreateInstructionNoMap() throws InstructionArgumentCountException, InvalidOpcodeException, InstructionSyntaxError, InvalidRegisterException, InvalidDataWidthException
  {
    Instruction toCheck = Instruction.createInstruction("addd %arg1 %arg1 %arg1", false);
  }

  @Test
  public void testCreateInstructionWithMap() throws InstructionArgumentCountException, InvalidOpcodeException, InstructionSyntaxError, InvalidRegisterException, InvalidDataWidthException
  {
    Instruction toCheck = Instruction.createInstruction("addd %arg1 %arg1 %arg1", true);
  }

  @Test
  public void testGetSizeNoMap() throws InstructionArgumentCountException, InvalidOpcodeException, InstructionSyntaxError, InvalidRegisterException, InvalidDataWidthException
  {
    Instruction toCheck = Instruction.createInstruction("addd %arg1 %arg1 %arg1", false);
    Assert.assertEquals((Integer)2, toCheck.getSize());
  }

  @Test
  public void testGetSizeWithMap() throws InstructionArgumentCountException, InvalidOpcodeException, InstructionSyntaxError, InvalidRegisterException, InvalidDataWidthException
  {
    Instruction toCheck = Instruction.createInstruction("addd %arg1 %arg1 %arg1", true);
    Assert.assertEquals((Integer)2, toCheck.getSize());
  }

  @Test
  public void testGetMachineCodeWithMap() throws InstructionArgumentCountException, InvalidOpcodeException, InstructionSyntaxError, InvalidRegisterException, InvalidDataWidthException, UnencodeableImmediateException
  {
    Instruction toCheck = Instruction.createInstruction("addd %arg1 %arg1 %arg1", true);
    Assert.assertArrayEquals(new Byte[]{0x06, (byte) 0x92, 0x00, 0x00}, toCheck.getMachineCode());
  }

  @Test
  public void testGetADDCDMachineCode() throws InstructionArgumentCountException, InvalidOpcodeException, InstructionSyntaxError, InvalidRegisterException, InvalidDataWidthException, UnencodeableImmediateException
  {
    Instruction toCheck = Instruction.createInstruction("addcd %arg1 %arg1 %arg1", true);
    Assert.assertArrayEquals(new Byte[]{0x0E, (byte) 0x92, 0x00, 0x00}, toCheck.getMachineCode());
  }

  @Test
  public void testGetADDDMachineCode() throws InstructionArgumentCountException, InvalidOpcodeException, InstructionSyntaxError, InvalidRegisterException, InvalidDataWidthException, UnencodeableImmediateException
  {
    Instruction toCheck = Instruction.createInstruction("addd %arg1 %arg1 %arg1", true);
    Assert.assertArrayEquals(new Byte[]{0x06, (byte) 0x92, 0x00, 0x00}, toCheck.getMachineCode());
  }

  @Test
  public void testGetSUBCDMachineCode() throws InstructionArgumentCountException, InvalidOpcodeException, InstructionSyntaxError, InvalidRegisterException, InvalidDataWidthException, UnencodeableImmediateException
  {
    Instruction toCheck = Instruction.createInstruction("subcd %arg1 %arg1 %arg1", true);
    Assert.assertArrayEquals(new Byte[]{0x1E, (byte) 0x92, 0x00, 0x00}, toCheck.getMachineCode());
  }

  @Test
  public void testGetSUBDMachineCode() throws InstructionArgumentCountException, InvalidOpcodeException, InstructionSyntaxError, InvalidRegisterException, InvalidDataWidthException, UnencodeableImmediateException
  {
    Instruction toCheck = Instruction.createInstruction("subd %arg1 %arg1 %arg1", true);
    Assert.assertArrayEquals(new Byte[]{0x16, (byte) 0x92, 0x00, 0x00}, toCheck.getMachineCode());
  }

  @Test
  public void testGetMOVWMachineCode() throws InstructionArgumentCountException, InvalidOpcodeException, InstructionSyntaxError, InvalidRegisterException, InvalidDataWidthException, UnencodeableImmediateException
  {
    Instruction toCheck = Instruction.createInstruction("movw %r1l %a1l", true);
    Assert.assertArrayEquals(Instruction.createInstruction("addw %r1l %zero %a1l", true).getMachineCode(), toCheck.getMachineCode());
  }
  
  @Test
  public void testADDWImmediate() throws InstructionArgumentCountException, InvalidOpcodeException, InstructionSyntaxError, InvalidRegisterException, InvalidDataWidthException, UnencodeableImmediateException
  {
    String toTest = "addw %r1l %r1l $0x500";
    Byte[] correct = {0x00, 0x00, 0x05, 0x00};
    Instruction toCheck = Instruction.createInstruction(toTest, true);
    Assert.assertArrayEquals(correct, toCheck.getMachineCode());
  }
  
  @Test
  public void testInvalidOpcode1() throws InstructionArgumentCountException, InvalidOpcodeException, InstructionSyntaxError, InvalidRegisterException, InvalidDataWidthException
  {
    String toTest = "adddw %r1l %r1l %r1l";
    
    exception.expect(InvalidOpcodeException.class);
    Instruction.createInstruction(toTest, false);
  }
  
  @Test
  /**
   * Test that an opcode with the wrong number of arguments throws an error
   */
  public void testArgumentCount1() throws InstructionArgumentCountException, InvalidOpcodeException, InstructionSyntaxError, InvalidRegisterException, InvalidDataWidthException
  {
    String toTest = "movw %r1l %r1l %r1l";
    
    exception.expect(InstructionArgumentCountException.class);
    Instruction.createInstruction(toTest, true);
  }

}
