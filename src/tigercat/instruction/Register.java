package tigercat.instruction;

public class Register
{
  /**
   * Record whether an instruction takes an immediate argument or not
   */
  public enum HalfReg
  {
    UPPER_HALF_REG('h'), LOWER_HALF_REG('l');

    protected char flag;

    HalfReg(char flag)
    {
      this.flag = flag;
    }

    public char getFlag()
    {
      return flag;
    }
  }
  
  public static String ConvertDoubleRegNameToSingleReg(String doubleWordRegName, HalfReg half)
  {
    assert doubleWordRegName.length() == 4 : "Expect double-word register names to be four characters long";
    
    char firstChar = doubleWordRegName.charAt(0);
    char number = doubleWordRegName.charAt(3);
    
    return new String(new char[] { firstChar, number, half.flag} );
  }
}
