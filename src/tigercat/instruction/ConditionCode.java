package tigercat.instruction;

import org.w3c.dom.NodeList;

import javax.xml.xpath.*;

public class ConditionCode extends Argument{

  public ConditionCode(String argument, Instruction.DataWidth dataWidth)
      throws InvalidRegisterException, XmlLookupException, InvalidOpcodeException {

    //unconditional jump
    if (argument.equals("")) {
      argument = "t";
    }

    XPathFactory xPathfactory = XPathFactory.newInstance();
    XPath xpath = xPathfactory.newXPath();
    String protoExpr = String.format("/lookup/condition-codes/code[@condition='%s']",
        argument);
    //  /lookup/register-numbers/register[@data_width='%s' and @name='%s']

    int conditionCode;

    try {
      XPathExpression expr = xpath.compile(protoExpr);
      NodeList protoResult = (NodeList) expr.evaluate(Instruction.lookupDoc, XPathConstants.NODESET);

      //check to make sure we only found one matching register
      if(protoResult.getLength() == 0)
        throw new InvalidOpcodeException(argument);
      if(protoResult.getLength() > 1)
        throw new XmlLookupException("More than one matching condition code found for: " + argument
            + " (this is a bug in the assembler.");

      conditionCode = Integer.decode(protoResult.item(0).getTextContent());

    } catch (XPathExpressionException e) {
      throw new XmlLookupException("XML lookup of condition code: '" + argument + "' failed.");
    }


    machineCodeRepresentation = conditionCode;
    size = 4; //todo: put this in a #define
    argumentType = Instruction.DataType.REGISTER;

  }

}
