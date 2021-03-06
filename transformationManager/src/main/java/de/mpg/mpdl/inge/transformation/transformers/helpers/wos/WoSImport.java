package de.mpg.mpdl.inge.transformation.transformers.helpers.wos;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import de.mpg.mpdl.inge.transformation.transformers.helpers.Pair;

/**
 * provides the import of a RIS file
 * 
 * @author kurt (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * 
 */
public class WoSImport {
  private static final Logger logger = Logger.getLogger(WoSImport.class);

  private static final String URL = "/home/kurt/Dokumente/wok-isi-test.txt";

  public WoSImport() {}

  /**
   * reads the import file and transforms the items to XML
   * 
   * @return xml
   */
  public String transformWoS2XML(String file) {
    String result = "";

    String[] itemList = getItemListFromString(file, "(\nER\n+EF\n)|(\nER\n)");
    List<List<Pair>> items = new ArrayList<List<Pair>>();
    if (itemList != null && itemList.length > 1) { // transform items to XML

      for (String item : itemList) {
        List<Pair> itemPairs = getItemPairs(getItemFromString(item + "\n"));

        items.add(itemPairs);
      }
      result = transformItemListToXML(items);

    } else if (itemList != null && itemList.length == 1) {
      List<Pair> item = getItemPairs(getItemFromString(itemList[0] + "\n"));
      result = transformItemToXML(item);
    }
    return result;
  }

  /**
   * reads the file and stores it in a string
   * 
   * @return List<String> with file lines
   */
  public String readFile() {
    String file = "";
    FileReader fileReader = null;
    BufferedReader input = null;

    try {
      fileReader = new FileReader(URL);
      input = new BufferedReader(fileReader);

      String str;
      while ((str = input.readLine()) != null) {
        file = file + "\n" + str;
      }
    } catch (IOException e) {
      logger.error("An error occurred while reading WOS file.", e);
      throw new RuntimeException(e);
    } finally {
      if (input != null) {
        try {
          input.close();
        } catch (IOException e) {
          logger.error("An error occurred while reading WOS file.", e);
          throw new RuntimeException(e);
        } finally {
          if (fileReader != null) {
            try {
              fileReader.close();
            } catch (IOException e) {
              logger.error("An error occurred while reading WOS file.", e);
              throw new RuntimeException(e);
            }
          }
        }
      }
    }

    return file;
  }

  /**
   * identifies item lines from input string and stores it in a List<String>
   * 
   * @param string
   * @return
   */
  public List<String> getItemFromString(String itemStr) {
    Pattern pattern = Pattern.compile("((([A-Z]{1}[0-9]{1})|([A-Z]{2}))\\s(.*(\\r\\n|\\r|\\n))((\\s\\s\\s.*(\\r\\n|\\r|\\n))?)*)");
    // Pattern pattern =
    // Pattern.compile("(([A-Z]{1}[0-9]{1})|([A-Z]{2})) ((.*(\\r\\n|\\r|\\n))+?)");

    Matcher matcher = pattern.matcher(itemStr);
    List<String> lineStrArr = new ArrayList<String>();
    while (matcher.find()) {
      lineStrArr.add(matcher.group());
    }

    return lineStrArr;
  }

  /**
   * identifies RIS items from input string and stores it in an String Array
   * 
   * @param string
   * @return
   */
  public String[] getItemListFromString(String string, String pattern) {
    String strItemList[] = string.split(pattern);
    // strItemList[0] = "\n"+strItemList[0].split("(FN ISI Export Format)\\n(VR 1.0)\\n")[0]; // cut
    // file header

    return strItemList;
  }

  /**
   * get item pairs from item string (by regex string)
   * 
   * @param string - RIS item as string
   * @return String list with item key-value pairs
   */
  public List<Pair> getItemPairs(List<String> lines) {
    List<Pair> pairList = new ArrayList<Pair>();

    if (lines != null) {
      for (String line : lines) {
        Pair pair = createWoSPairByString(line);
        pairList.add(pair);
      }
    }

    return pairList;
  }

  /**
   * get a pair from line string (by regex string)
   * 
   * @param string - RIS line as string
   * @return Pair - key-value pair created by string line
   */
  public Pair createWoSPairByString(String line) {
    String key = line.substring(0, 2);
    String value = line.substring(3);
    Pair pair = null;
    pair = new Pair(key.trim(), escape(value.trim()));

    return pair;
  }

  /**
   * creates a single item in xml
   * 
   * @param item pair list
   * @return xml string of the whole item list
   */
  public String transformItemToXML(List<Pair> item) {
    if (item != null && item.size() > 0) {
      return createXMLElement("item", transformItemSubelementsToXML(item));
    }

    return "";
  }

  /**
   * creates the complete item list in xml
   * 
   * @param item pair list
   * @return xml string of the whole item list
   */
  public String transformItemListToXML(List<List<Pair>> itemList) {
    String xml = "<item-list>";

    if (itemList != null && itemList.size() > 0) {
      for (List<Pair> item : itemList) {
        xml = xml + "\n" + transformItemToXML(item);
      }
    }

    xml = xml + "</item-list>";

    return xml;
  }

  /**
   * creates an xml string of the item pair list
   * 
   * @param item pairs as list
   * @return xml String
   */
  public String transformItemSubelementsToXML(List<Pair> item) {
    String xml = "";
    if (item != null && item.size() > 0) {
      for (Pair pair : item) {
        xml = xml + createXMLElement(pair.getKey(), pair.getValue());
      }
    }

    return xml;
  }

  /**
   * creates a single element in xml
   * 
   * @param tag - tag name of the element
   * @param value - value of the element
   * @return xml element as string
   */
  public String createXMLElement(String tag, String value) {
    if (tag != null && tag != "") {
      return "<" + tag + ">" + value + "</" + tag + ">";
    }

    return "";
  }

  /**
   * escapes special characters
   * 
   * @param input string
   * @return string with escaped characters
   */
  public String escape(String input) {
    if (input != null) {
      input = input.replace("&", "&amp;");
      input = input.replace("<", "&lt;");
      input = input.replace(">", "&gt;");
      input = input.replace("\"", "&quot;");
    }

    return input;
  }
}
