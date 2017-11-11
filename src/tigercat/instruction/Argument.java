package tigercat.instruction;

import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import java.io.IOException;

class Argument
{
  protected static final String ZERO_REG = "zero";

  protected int machineCodeRepresentation;
  protected Instruction.DataType argumentType;
  protected int size;

  public Instruction.DataType getArgumentType()
  {
    return argumentType;
  }

  public int getMachineCodeRepresentation()
  {
    return machineCodeRepresentation;
  }

  protected int getRegisterCode(String registerName, Instruction.DataWidth dataWidth) throws XmlLookupException, InvalidRegisterException {
    //set data width
    String widthArg;
    if (dataWidth == Instruction.DataWidth.SINGLE_WORD) {
      widthArg = "single";
    } else if (dataWidth == Instruction.DataWidth.DOUBLE_WORD) {
      widthArg = "double";
    } else {
      throw new XmlLookupException("Unrecognized datawidth argument");
    }
    XPathFactory xPathfactory = XPathFactory.newInstance();
    XPath xpath = xPathfactory.newXPath();
    String protoExpr = String.format("/lookup/register-numbers/register[@data_width='%s' and @name='%s']",
                                     widthArg, registerName);

    try {
      XPathExpression expr = xpath.compile(protoExpr);
      NodeList protoResult = (NodeList) expr.evaluate(Instruction.lookupDoc, XPathConstants.NODESET);

      //check to make sure we only found one matching register
      if(protoResult.getLength() == 0)
        throw new InvalidRegisterException(registerName);
      if(protoResult.getLength() > 1)
        throw new XmlLookupException("More than one matching register found for: " + registerName
                                             + " (this is a bug in the assembler.");

      return Integer.decode(protoResult.item(0).getTextContent());

    } catch (XPathExpressionException e) {
      throw new XmlLookupException("XML lookup of register: '" + registerName + "' failed.");
    }
  }

  /**
   * Returns the size of the encoding of this argument
   *
   * @return The size of this argument
   */
  public int getEncodingSize()
  {
    assert argumentType == Instruction.DataType.REGISTER : "getEncodingSize undefined for non-register arguments";
    return size;
  }

  public Argument() throws XmlLookupException {
    //one-time setup
    try {
      if(Instruction.lookupDoc == null) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Instruction.lookupDoc = builder.parse(Instruction.LOOKUP_FILE_URI);
      }
    } catch (ParserConfigurationException | SAXException | IOException e) {
      throw new XmlLookupException("XML file not found: " + Instruction.LOOKUP_FILE_URI + ".");
    }

  }
  public Argument(String argument, Instruction.DataWidth dataWidth, Instruction.DataType argumentType)
          throws InvalidRegisterException, XmlLookupException {

    //set up xml lookup document
    this();

    this.argumentType = argumentType;
    if (argumentType == Instruction.DataType.IMMEDIATE)
    {
      machineCodeRepresentation = this.parseImmediate(argument);
    } else if (argumentType == Instruction.DataType.REGISTER)
    {
      machineCodeRepresentation = this.parseRegister(argument, dataWidth);
      switch(dataWidth)
      {
      case SINGLE_WORD:
        size = Instruction.SIZEOF_SINGLE_WORD_REG_ENCODING;
        break;
      case DOUBLE_WORD:
        size = Instruction.SIZEOF_DOUBLE_WORD_REG_ENCODING;
        break;
      }
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
  protected int parseRegister(String argument, Instruction.DataWidth dataWidth) throws InvalidRegisterException, XmlLookupException {
    if (dataWidth == Instruction.DataWidth.SINGLE_WORD | dataWidth == Instruction.DataWidth.DOUBLE_WORD)
    {
      return getRegisterCode(argument, dataWidth);
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
   *          A string containing a hexadecimal immediate value, including 0x prefix
   * @return Machine Code representation of the immediate value
   */
  protected int parseImmediate(String argument)
  {
    // Strip 0x prefix
    argument = argument.substring(2);
    return Integer.parseUnsignedInt(argument, 16);
  }
}
