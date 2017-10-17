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
  protected String name;
  protected Integer address;
  protected Integer size;
  
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

  public Label(String name, Integer address, Integer size)
  {
    this.name = name;
    this.address = address;
    this.size = size;
  }
}
