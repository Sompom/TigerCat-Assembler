package tigercat;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import tigercat.instruction.*;

public class TigerCatInstructionTester
{
  /**
   * convenience method for converting a 'binary' string into a byte array
   */
  private Byte[] convertStringToBytes(String binaryString) {

    binaryString = binaryString.replaceAll("\\s", "");
    int translatedString = Integer.parseUnsignedInt(binaryString, 2);
    byte[] expectedBytes = ByteBuffer.allocate(4).putInt(translatedString).array(); //todo: check endianness
    Byte[] javaBullshittery = new Byte[expectedBytes.length];

    int i = 0;
    for (byte b : expectedBytes) {
      javaBullshittery[i++] = b; //autoboxing
    }

    return javaBullshittery;
  }

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
  public void testCreateInstructionNoMap() throws InstructionArgumentCountException, InvalidOpcodeException, InstructionSyntaxError, InvalidRegisterException, InvalidDataWidthException, XmlLookupException {
    Instruction.createInstruction("addd %arg1 %arg1 %arg1", false);
    // Not throwing an exception indicates success
  }

  @Test
  public void testCreateInstructionWithMap() throws InstructionArgumentCountException, InvalidOpcodeException, InstructionSyntaxError, InvalidRegisterException, InvalidDataWidthException, XmlLookupException {
    Instruction.createInstruction("addd %arg1 %arg1 %arg1", true);
    // Not throwing an exception indicates success
  }

  @Test
  public void testGetSizeNoMap() throws InstructionArgumentCountException, InvalidOpcodeException, InstructionSyntaxError, InvalidRegisterException, InvalidDataWidthException, XmlLookupException {
    Instruction toCheck = Instruction.createInstruction("addd %arg1 %arg1 %arg1", false);
    Assert.assertEquals((Integer)2, toCheck.getSize());
  }

  @Test
  public void testGetSizeWithMap() throws InstructionArgumentCountException, InvalidOpcodeException, InstructionSyntaxError, InvalidRegisterException, InvalidDataWidthException, XmlLookupException {
    Instruction toCheck = Instruction.createInstruction("addd %arg1 %arg1 %arg1", true);
    Assert.assertEquals((Integer)2, toCheck.getSize());
  }

  @Test
  public void testGetMachineCodeWithMap() throws InstructionArgumentCountException, InvalidOpcodeException, InstructionSyntaxError, InvalidRegisterException, InvalidDataWidthException, UnencodeableImmediateException, XmlLookupException {
    Instruction toCheck = Instruction.createInstruction("addd %arg1 %arg1 %arg1", true);
    Assert.assertArrayEquals(new Byte[]{(byte)0xC6, (byte) 0x92, 0x00, 0x00}, toCheck.getMachineCode());
  }

  @Test
  public void testGetADDCDMachineCode() throws InstructionArgumentCountException, InvalidOpcodeException, InstructionSyntaxError, InvalidRegisterException, InvalidDataWidthException, UnencodeableImmediateException, XmlLookupException {
    Instruction toCheck = Instruction.createInstruction("addcd %arg1 %arg1 %arg1", true);
    Assert.assertArrayEquals(new Byte[]{(byte)0xCE, (byte) 0x92, 0x00, 0x00}, toCheck.getMachineCode());
  }

  @Test
  public void testGetADDDMachineCode() throws InstructionArgumentCountException, InvalidOpcodeException, InstructionSyntaxError, InvalidRegisterException, InvalidDataWidthException, UnencodeableImmediateException, XmlLookupException {
    Instruction toCheck = Instruction.createInstruction("addd %arg1 %arg1 %arg1", true);
    Assert.assertArrayEquals(new Byte[]{(byte)0xC6, (byte) 0x92, 0x00, 0x00}, toCheck.getMachineCode());
  }

  @Test
  public void testGetSUBCDMachineCode() throws InstructionArgumentCountException, InvalidOpcodeException, InstructionSyntaxError, InvalidRegisterException, InvalidDataWidthException, UnencodeableImmediateException, XmlLookupException {
    Instruction toCheck = Instruction.createInstruction("subcd %arg1 %arg1 %arg1", true);
    Assert.assertArrayEquals(new Byte[]{(byte)0xDE, (byte) 0x92, 0x00, 0x00}, toCheck.getMachineCode());
  }

  @Test
  public void testGetSUBDMachineCode() throws InstructionArgumentCountException, InvalidOpcodeException, InstructionSyntaxError, InvalidRegisterException, InvalidDataWidthException, UnencodeableImmediateException, XmlLookupException {
    Instruction toCheck = Instruction.createInstruction("subd %arg1 %arg1 %arg1", true);
    Assert.assertArrayEquals(new Byte[]{(byte)0xD6, (byte) 0x92, 0x00, 0x00}, toCheck.getMachineCode());
  }

  @Test
  public void testGetMOVWMachineCode() throws InstructionArgumentCountException, InvalidOpcodeException, InstructionSyntaxError, InvalidRegisterException, InvalidDataWidthException, UnencodeableImmediateException, XmlLookupException {
    Instruction toCheck = Instruction.createInstruction("movw %r1l %a1l", true);
    Assert.assertArrayEquals(Instruction.createInstruction("addw %r1l %zero %a1l", true).getMachineCode(), toCheck.getMachineCode());
  }

  @Test
  public void testGetLOADMachineCode() throws InstructionArgumentCountException, InvalidOpcodeException, InstructionSyntaxError, InvalidRegisterException, InvalidDataWidthException, UnencodeableImmediateException, XmlLookupException {
    Instruction toCheck;
    String expectedString;
    Byte[] expectedBytes;

    toCheck = Instruction.createInstruction("loadw %a1l %a2l", true);
    expectedString = "10100  0  1  0010  0011  0000 0000 0000 0000 0";
    expectedBytes = convertStringToBytes(expectedString);
    Assert.assertArrayEquals(expectedBytes, toCheck.getMachineCode());

    toCheck = Instruction.createInstruction("loadw %a1l $0x89AB", true);
    expectedString = "10100  0  0  0010  0 0000  1000 1001 1010 1011";
    expectedBytes = convertStringToBytes(expectedString);
    Assert.assertArrayEquals(expectedBytes, toCheck.getMachineCode());

    toCheck = Instruction.createInstruction("loadd %arg1 %arg2", true);
    expectedString = "10100  1  1  010  011  000 0000 0000 0000 0000";
    expectedBytes = convertStringToBytes(expectedString);
    Assert.assertArrayEquals(expectedBytes, toCheck.getMachineCode());

    toCheck = Instruction.createInstruction("loadd %arg1 $0x2FEDCB", true);
    expectedString = "10100  1  0  010   10 1111 1110 1101 1100 1011";
    expectedBytes = convertStringToBytes(expectedString);
    Assert.assertArrayEquals(expectedBytes, toCheck.getMachineCode());
  }

  @Test
  public void testGetJMPMachineCode() throws InstructionArgumentCountException, InvalidOpcodeException, InstructionSyntaxError, InvalidRegisterException, InvalidDataWidthException, UnencodeableImmediateException, XmlLookupException {
    Instruction toCheck;
    String expectedString;
    Byte[] expectedBytes;

    //condition code checks

    //unconditional
    toCheck = Instruction.createInstruction("jmp %arg1", true);
    expectedString = "01100  1  1  1111  010  0000 0000 0000 0000 00";
    expectedBytes = convertStringToBytes(expectedString);
    Assert.assertArrayEquals(expectedBytes, toCheck.getMachineCode());
    
    //a
    toCheck = Instruction.createInstruction("jmpa %arg1", true);
    expectedString = "01100  1  1  0001  010  0000 0000 0000 0000 00";
    expectedBytes = convertStringToBytes(expectedString);
    Assert.assertArrayEquals(expectedBytes, toCheck.getMachineCode());

    //ae
    toCheck = Instruction.createInstruction("jmpae %arg1", true);
    expectedString = "01100  1  1  0010  010  0000 0000 0000 0000 00";
    expectedBytes = convertStringToBytes(expectedString);
    Assert.assertArrayEquals(expectedBytes, toCheck.getMachineCode());

    //b
    toCheck = Instruction.createInstruction("jmpb %arg1", true);
    expectedString = "01100  1  1  0011  010  0000 0000 0000 0000 00";
    expectedBytes = convertStringToBytes(expectedString);
    Assert.assertArrayEquals(expectedBytes, toCheck.getMachineCode());


    //be
    toCheck = Instruction.createInstruction("jmpbe %arg1", true);
    expectedString = "01100  1  1  0100  010  0000 0000 0000 0000 00";
    expectedBytes = convertStringToBytes(expectedString);
    Assert.assertArrayEquals(expectedBytes, toCheck.getMachineCode());

    //g
    toCheck = Instruction.createInstruction("jmpg %arg1", true);
    expectedString = "01100  1  1  0101  010  0000 0000 0000 0000 00";
    expectedBytes = convertStringToBytes(expectedString);
    Assert.assertArrayEquals(expectedBytes, toCheck.getMachineCode());

    //ge
    toCheck = Instruction.createInstruction("jmpge %arg1", true);
    expectedString = "01100  1  1  0110  010  0000 0000 0000 0000 00";
    expectedBytes = convertStringToBytes(expectedString);
    Assert.assertArrayEquals(expectedBytes, toCheck.getMachineCode());

    //l
    toCheck = Instruction.createInstruction("jmpl %arg1", true);
    expectedString = "01100  1  1  0111  010  0000 0000 0000 0000 00";
    expectedBytes = convertStringToBytes(expectedString);
    Assert.assertArrayEquals(expectedBytes, toCheck.getMachineCode());

    //le
    toCheck = Instruction.createInstruction("jmple %arg1", true);
    expectedString = "01100  1  1  1000  010  0000 0000 0000 0000 00";
    expectedBytes = convertStringToBytes(expectedString);
    Assert.assertArrayEquals(expectedBytes, toCheck.getMachineCode());

    //e
    toCheck = Instruction.createInstruction("jmpe %arg1", true);
    expectedString = "01100  1  1  1001  010  0000 0000 0000 0000 00";
    expectedBytes = convertStringToBytes(expectedString);
    Assert.assertArrayEquals(expectedBytes, toCheck.getMachineCode());

    //o
    toCheck = Instruction.createInstruction("jmpo %arg1", true);
    expectedString = "01100  1  1  1010  010  0000 0000 0000 0000 00";
    expectedBytes = convertStringToBytes(expectedString);
    Assert.assertArrayEquals(expectedBytes, toCheck.getMachineCode());

    //c
    toCheck = Instruction.createInstruction("jmpc %arg1", true);
    expectedString = "01100  1  1  1011  010  0000 0000 0000 0000 00";
    expectedBytes = convertStringToBytes(expectedString);
    Assert.assertArrayEquals(expectedBytes, toCheck.getMachineCode());

    //s
    toCheck = Instruction.createInstruction("jmps %arg1", true);
    expectedString = "01100  1  1  1100  010  0000 0000 0000 0000 00";
    expectedBytes = convertStringToBytes(expectedString);
    Assert.assertArrayEquals(expectedBytes, toCheck.getMachineCode());

    //z
    toCheck = Instruction.createInstruction("jmpz %arg1", true);
    expectedString = "01100  1  1  1101  010  0000 0000 0000 0000 00";
    expectedBytes = convertStringToBytes(expectedString);
    Assert.assertArrayEquals(expectedBytes, toCheck.getMachineCode());

    //f
    toCheck = Instruction.createInstruction("jmpf %arg1", true);
    expectedString = "01100  1  1  0000  010  0000 0000 0000 0000 00";
    expectedBytes = convertStringToBytes(expectedString);
    Assert.assertArrayEquals(expectedBytes, toCheck.getMachineCode());

    //t
    toCheck = Instruction.createInstruction("jmpt %arg1", true);
    expectedString = "01100  1  1  1111  010  0000 0000 0000 0000 00";
    expectedBytes = convertStringToBytes(expectedString);
    Assert.assertArrayEquals(expectedBytes, toCheck.getMachineCode());


    //Immediate encoding check

    toCheck = Instruction.createInstruction("jmpge $0x1FEDCB", true);
    expectedString = "01100  1  0  0110  1 1111 1110 1101 1100 1011";
    expectedBytes = convertStringToBytes(expectedString);
    Assert.assertArrayEquals(expectedBytes, toCheck.getMachineCode());
  }

  @Test
  public void testGetSTOMachineCode() throws InstructionArgumentCountException, InvalidOpcodeException, InstructionSyntaxError, InvalidRegisterException, InvalidDataWidthException, UnencodeableImmediateException, XmlLookupException {
    Instruction toCheck;
    String expectedString;
    Byte[] expectedBytes;

    toCheck = Instruction.createInstruction("stow %a1l %a2l", true);
    expectedString = "10101  0  1  0010  0011  0000 0000 0000 0000 0";
    expectedBytes = convertStringToBytes(expectedString);
    Assert.assertArrayEquals(expectedBytes, toCheck.getMachineCode());

    toCheck = Instruction.createInstruction("stow %a1l $0x89AB", true);
    expectedString = "10101  0  0  0010  0 0000  1000 1001 1010 1011";
    expectedBytes = convertStringToBytes(expectedString);
    Assert.assertArrayEquals(expectedBytes, toCheck.getMachineCode());

    toCheck = Instruction.createInstruction("stod %arg1 %arg2", true);
    expectedString = "10101  1  1  010  011  000 0000 0000 0000 0000";
    expectedBytes = convertStringToBytes(expectedString);
    Assert.assertArrayEquals(expectedBytes, toCheck.getMachineCode());

    toCheck = Instruction.createInstruction("stod %arg1 $0x2FEDCB", true);
    expectedString = "10101  1  0  010   10 1111 1110 1101 1100 1011";
    expectedBytes = convertStringToBytes(expectedString);
    Assert.assertArrayEquals(expectedBytes, toCheck.getMachineCode());
  }

  @Test
  public void testGetPUSHW_Reg_MachineCode() throws InstructionArgumentCountException, InvalidOpcodeException, InstructionSyntaxError, InvalidRegisterException, InvalidDataWidthException, UnencodeableImmediateException, XmlLookupException {
    Instruction toCheck = Instruction.createInstruction("pushw %a1l", true);

    String expectedString = "10000  0  1  0010   0 0000 0000 0000 0000 0000";
    Byte[] expectedBytes = convertStringToBytes(expectedString);
    Assert.assertArrayEquals(expectedBytes, toCheck.getMachineCode());
  }

  @Test
  public void testGetPUSHW_Imm_MachineCode() throws InstructionArgumentCountException, InvalidOpcodeException, InstructionSyntaxError, InvalidRegisterException, InvalidDataWidthException, UnencodeableImmediateException, XmlLookupException {
    Instruction toCheck = Instruction.createInstruction("pushw $0xF0F0", true);

    String expectedString = "10000  0  0  0 0000 0000 1111 0000 1111 0000";
    Byte[] expectedBytes = convertStringToBytes(expectedString);
    Assert.assertArrayEquals(expectedBytes, toCheck.getMachineCode());
  }

  @Test
  public void testGetPUSHD_Reg_MachineCode() throws InstructionArgumentCountException, InvalidOpcodeException, InstructionSyntaxError, InvalidRegisterException, InvalidDataWidthException, UnencodeableImmediateException, XmlLookupException {
    Instruction toCheck = Instruction.createInstruction("pushd %arg1", true);

    String expectedString = "10000  1  1  010 00 0000 0000 0000 0000 0000";
    Byte[] expectedBytes = convertStringToBytes(expectedString);
    Assert.assertArrayEquals(expectedBytes, toCheck.getMachineCode());
  }

  @Test
  public void testGetPUSHD_Imm_MachineCode() throws InstructionArgumentCountException, InvalidOpcodeException, InstructionSyntaxError, InvalidRegisterException, InvalidDataWidthException, UnencodeableImmediateException, XmlLookupException {
    Instruction toCheck = Instruction.createInstruction("pushd $0xF0F0F0", true);

    String expectedString = "10000  1  0  0 1111 0000 1111 0000 1111 0000";
    Byte[] expectedBytes = convertStringToBytes(expectedString);
    Assert.assertArrayEquals(expectedBytes, toCheck.getMachineCode());
  }

  @Test
  public void testGetPOPW_Reg_MachineCode() throws InstructionArgumentCountException, InvalidOpcodeException, InstructionSyntaxError, InvalidRegisterException, InvalidDataWidthException, UnencodeableImmediateException, XmlLookupException {
    Instruction toCheck = Instruction.createInstruction("popw %a4h", true);

    String expectedString = "10001  0  1  1101   0 0000 0000 0000 0000 0000";
    Byte[] expectedBytes = convertStringToBytes(expectedString);
    Assert.assertArrayEquals(expectedBytes, toCheck.getMachineCode());
  }

  @Test
  public void testGetPOPD_Reg_MachineCode() throws InstructionArgumentCountException, InvalidOpcodeException, InstructionSyntaxError, InvalidRegisterException, InvalidDataWidthException, UnencodeableImmediateException, XmlLookupException {
    Instruction toCheck = Instruction.createInstruction("popd %arg3", true);

    String expectedString = "10001  1  1  100 00 0000 0000 0000 0000 0000";
    Byte[] expectedBytes = convertStringToBytes(expectedString);
    Assert.assertArrayEquals(expectedBytes, toCheck.getMachineCode());
  }
  
  @Test
  public void testGetMOVDMachineCodeImmediate() throws InstructionArgumentCountException, InvalidOpcodeException, InstructionSyntaxError, InvalidRegisterException, InvalidDataWidthException, UnencodeableImmediateException, XmlLookupException {
    Instruction toCheck = Instruction.createInstruction("movd %ret1 $0xFEDCBA98", true);
    Instruction child1 = Instruction.createInstruction("addw %r1l %zero $0xBA98", true);
    Instruction child2 = Instruction.createInstruction("addw %r1h %zero $0xFEDC", true);
    
    ArrayList<Byte> expected = new ArrayList<Byte>();
    expected.addAll(Arrays.asList(child1.getMachineCode()));
    expected.addAll(Arrays.asList(child2.getMachineCode()));
    
    Assert.assertArrayEquals(expected.toArray(), toCheck.getMachineCode());
  }
  
  @Test
  public void testGetMOVDMachineCodeRegister() throws InstructionArgumentCountException, InvalidOpcodeException, InstructionSyntaxError, InvalidRegisterException, InvalidDataWidthException, UnencodeableImmediateException, XmlLookupException {
    Instruction toCheck = Instruction.createInstruction("movd %ret1 %arg1", true);
    Instruction child1 = Instruction.createInstruction("addd %ret1 %arg1 $0x0", true);
    
    ArrayList<Byte> expected = new ArrayList<Byte>();
    expected.addAll(Arrays.asList(child1.getMachineCode()));
    
    Assert.assertArrayEquals(expected.toArray(), toCheck.getMachineCode());
  }
  
  @Test
  public void testADDWImmediate() throws InstructionArgumentCountException, InvalidOpcodeException, InstructionSyntaxError, InvalidRegisterException, InvalidDataWidthException, UnencodeableImmediateException, XmlLookupException {
    String toTest = "addw %r1l %r1l $0x500";
    Byte[] correct = {(byte)0xC0, 0x00, 0x05, 0x00};
    Instruction toCheck = Instruction.createInstruction(toTest, true);
    Assert.assertArrayEquals(correct, toCheck.getMachineCode());
  }

  @Test
  public void testGetANDW_Reg_MachineCode() throws InstructionArgumentCountException, InvalidOpcodeException, InstructionSyntaxError, InvalidRegisterException, InvalidDataWidthException, UnencodeableImmediateException, XmlLookupException {
    Instruction toCheck = Instruction.createInstruction("andw %a2l %a2h %a4l", true);

    String expectedString = "11100  0  1  0011 1011 0101 0 0000 0000 0000";
    Byte[] expectedBytes = convertStringToBytes(expectedString);
    Assert.assertArrayEquals(expectedBytes, toCheck.getMachineCode());
  }

  @Test
  public void testGetANDW_Imm_MachineCode() throws InstructionArgumentCountException, InvalidOpcodeException, InstructionSyntaxError, InvalidRegisterException, InvalidDataWidthException, UnencodeableImmediateException, XmlLookupException {
    Instruction toCheck = Instruction.createInstruction("andw %a2l %rand $0x5F5F", true);

    String expectedString = "11100  0  0  0011 0111 0 0101 1111 0101 1111";
    Byte[] expectedBytes = convertStringToBytes(expectedString);
    Assert.assertArrayEquals(expectedBytes, toCheck.getMachineCode());
  }

  @Test
  public void testGetANDD_Reg_MachineCode() throws InstructionArgumentCountException, InvalidOpcodeException, InstructionSyntaxError, InvalidRegisterException, InvalidDataWidthException, UnencodeableImmediateException, XmlLookupException {
    Instruction toCheck = Instruction.createInstruction("andd %arg1 %ret1 %ret2", true);

    String expectedString = "11100  1  1  010 000 001 0000 0000 0000 0000";
    Byte[] expectedBytes = convertStringToBytes(expectedString);
    Assert.assertArrayEquals(expectedBytes, toCheck.getMachineCode());
  }

  @Test
  public void testGetANDD_Imm_MachineCode() throws InstructionArgumentCountException, InvalidOpcodeException, InstructionSyntaxError, InvalidRegisterException, InvalidDataWidthException, UnencodeableImmediateException, XmlLookupException {
    Instruction toCheck = Instruction.createInstruction("andd %ret2 %IP $0x70505", true);

    String expectedString = "11100  1  0  001 111 111 0000 0101 0000 0101";
    Byte[] expectedBytes = convertStringToBytes(expectedString);
    Assert.assertArrayEquals(expectedBytes, toCheck.getMachineCode());
  }
  
  @Test
  /**
   * Test that a mistyped opcode will throw an exception
   */
  public void testInvalidOpcode1() throws InstructionArgumentCountException, InvalidOpcodeException, InstructionSyntaxError, InvalidRegisterException, InvalidDataWidthException, XmlLookupException {
    String toTest = "adddw %r1l %r1l %r1l";
    
    exception.expect(InvalidOpcodeException.class);
    Instruction.createInstruction(toTest, false);
  }

  @Test
  /**
   * Test that a mistyped opcode will throw an exception
   */
  public void testInvalidRegWidth() throws InstructionArgumentCountException, InvalidOpcodeException, InstructionSyntaxError, InvalidRegisterException, InvalidDataWidthException, XmlLookupException {
    String toTest = "addd %arg1 %r1l %r1l";

    exception.expect(InvalidRegisterException.class);
    Instruction.createInstruction(toTest, true);
  }
  
  @Test
  /**
   * Test that an opcode with the wrong number of arguments throws an error
   */
  public void testArgumentCount1() throws InstructionArgumentCountException, InvalidOpcodeException, InstructionSyntaxError, InvalidRegisterException, InvalidDataWidthException, XmlLookupException {
    String toTest = "movw %r1l %r1l %r1l";
    
    exception.expect(InstructionArgumentCountException.class);
    Instruction.createInstruction(toTest, true);
  }
  
  @Test
  /**
   * Test that an instruction which uses a mistyped register throws an exception
   */
  public void testInvalidRegister() throws InstructionArgumentCountException, InvalidOpcodeException, InstructionSyntaxError, InvalidRegisterException, InvalidDataWidthException, XmlLookupException {
    String toTest = "movw %r1l %r5l";
    
    exception.expect(InvalidRegisterException.class);
    Instruction.createInstruction(toTest, true);
  }
  
  @Test
  /**
   * Test that a single-word instruction which uses a too-big immediate throws an exception
   */
  public void testUnencodeableImmediate1() throws InstructionArgumentCountException, InvalidOpcodeException, InstructionSyntaxError, InvalidRegisterException, InvalidDataWidthException, UnencodeableImmediateException, XmlLookupException {
    String toTest = "addw %r1l %a1l $0x10000";
    
    exception.expect(UnencodeableImmediateException.class);
    Instruction.createInstruction(toTest, true).getMachineCode();
  }
  
  @Test
  /**
   * Test that a double-word instruction which uses a too-big immediate throws an exception
   */
  public void testUnencodeableImmediate2() throws InstructionArgumentCountException, InvalidOpcodeException, InstructionSyntaxError, InvalidRegisterException, InvalidDataWidthException, UnencodeableImmediateException, XmlLookupException {
    String toTest = "addd %ret1 %arg1 $0x80000";
    
    exception.expect(UnencodeableImmediateException.class);
    Instruction.createInstruction(toTest, true).getMachineCode();
  }
  
  @Test
  /**
   * Test that the largest legal immediate can be encoded for double-word add
   */
  public void testADDDLargeImmediate() throws UnencodeableImmediateException, InstructionArgumentCountException, InvalidOpcodeException, InstructionSyntaxError, InvalidRegisterException, InvalidDataWidthException, XmlLookupException {
    String toTest = "addd %ret1 %arg1 $0x7FFFF";

    Instruction.createInstruction(toTest, true).getMachineCode();
    // If an exception is not thrown, this test succeeds
  }

}
