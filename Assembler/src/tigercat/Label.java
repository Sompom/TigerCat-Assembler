/*
 * Author       : Team TigerCat
 * Date         : 16 October 2017
 * Filename     : Label.java
 * Description  : Class to store information relevant to a label 
 */

package tigercat;

/**
 * Container class for information relevant to a label
 * 
 * This currently includes an address and a size (for global arrays, etc.)
 *
 */
public class Label
{
  public enum Type
  {
    ADDRESS,  // Default Label Type. One address which has no representation in the final machine code
    DATA      // Some data (e.g., an array) which should be written to the END of the machine code
  }
  
  protected String name;
  protected Integer size;
  protected Integer address;
  protected Type type; 
  
  public String getName()
  {
    return name;
  }

  public Integer getAddress()
  {
    return address;
  }

  public Integer getSize()
  {
    return size;
  }
  
  public Type getType()
  {
    return type;
  }

  public void setAddress(Integer address)
  {
    this.address = address;
  }

  /**
   * Construct an address-type label, where the address is immediately known
   * 
   * @param name The label name
   * @param address The address to replace the name with in the machine code
   */
  public Label(String name, Integer address)
  {
    this.name = name;
    this.address = address;
    this.size = null; // Size is nonsensical for addresses
    this.type = Type.ADDRESS;
  }
  
  /**
   * Construct a non-address-type label, where the address is not yet known
   * 
   * @param name The Label name
   * @param size The size of the data at the Label
   * @param type The type of Label
   */
  public Label(String name, Integer size, Type type)
  {
    this.name = name;
    this.address = null;
    this.size = size;
    this.type = type;
  }
}
