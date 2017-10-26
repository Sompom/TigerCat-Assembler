package tigercat.instruction;

import tigercat.AssemblerException;

public class InstructionArgumentCountException extends AssemblerException
{
  private static final long serialVersionUID = -6732438117796174143L;

  private int expected = 0;
  private int actual = 0;

  public InstructionArgumentCountException(int expected, int actual) {
    this.expected = expected;
    this.actual = actual;
  }

  private String generateMessage() {
    return "Opcode takes " + expected + " arguments but " + actual + " were provided";
  }

  @Override
  public String getDiagnostic() {
    return getContextError() + generateMessage();
  }
}
