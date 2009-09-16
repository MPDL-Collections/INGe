package de.mpg.escidoc.services.transformation.transformations.otherFormats.ris;

import java.io.*;

import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import org.apache.log4j.Logger;

/**
 * provides the import of a RIS file 
 * 
 * @author kurt (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class RISImport{
	
    private String url = null;
    private Logger logger = Logger.getLogger(getClass());
    

    /**
     * Public Constructor RISImport.
     */
    public RISImport(){
    	 
    	
    }
    
    /**
     * reads the import file and transforms the items to XML  
     * @return xml 
     */
    public String transformRIS2XML(String file){
    	String result = "";
    	
    	String[] itemList = getItemListFromString(file, "(\\n|\\r|\\r\\n)ER\\s\\s-");    	// extract items to array
    	    	
    	List<List<Pair>> items = new ArrayList();
    	if(itemList!=null && itemList.length>1){ //transform items to XML
    		
    		for (String item : itemList) {
    			List<Pair> itemPairs = getItemPairs(getItemFromString(item, "([A-Z0-9]{2})\\s+- ((.*\n)+?)($|(?=(([A-Z0-9]{2})  -)))"));
    			for(Pair p : itemPairs){
    				System.out.print(p.getKey()+" : "+p.getValue()+"\n");
    			}
    			items.add(itemPairs);  
			}
    		result = transformItemListToXML(items); 
    		
    	}else if(itemList!=null && itemList.length==1){
    		List<Pair> item = getItemPairs(getItemFromString(itemList[0], "([A-Z0-9]{2})\\s+- ((.*\n)+?)($|(?=(([A-Z0-9]{2})  -)))"));
    		result = transformItemToXML(item);
    	}
    	return result;
    }
       
    /**
     * reads the file and stores it in a string
     * @return List<String> with file lines
     */
    public String readFile()
    {
    	
    	String file = "";        	
    	try{    		
    		BufferedReader input = new BufferedReader(new FileReader(this.url));    	
    		// string buffer for file reading  
            String str;               
            // reading line by line from file   
            while ((str = input.readLine()) != null) {            	          	
            		file = file + "\n" +str;            
            }            
    	}catch(Exception e){    		
    		this.logger.error("An error occurred while reading RIS file.", e);
            throw new RuntimeException(e);
    	}	
    	return file;
    }
   
    
    /**
     * identifies item lines from input string and stores it in a List<String>
     * @param string
     * @return
     */
    public List<String> getItemFromString(String string, String patternString){    	
    	   	
    	//Pattern p = Pattern.compile("([A-Z0-9]{2})  - ((.*\n)+?)($|(?=(([A-Z0-9]{2})  -)))");     	
    	Pattern pattern = Pattern.compile(patternString);
    	Matcher matcher = pattern.matcher(string);
    	List<String> strArr = new ArrayList();
    	while(matcher.find()){
    		strArr.add(matcher.group());
    	}
    	return strArr;
    }
    
    /**
     * identifies RIS items from input string and stores it in an String Array
     * @param string 
     * @return
     */
    public String[] getItemListFromString(String string, String pattern){
    	
    	//String s[] = string.split("ER\\s -");
    	
    	//replace first empty lines and BOM 
    	string = Pattern.compile("^.*?(\\w)", Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(string).replaceFirst("$1");
    	String itemList[] = string.split(pattern);
    	return itemList;
    }
    
    /**
     * get item pairs from item string (by regex string)
     * @param string - RIS item as string
     * @return String list with item key-value pairs
     */
    public List<Pair> getItemPairs(List<String> lines){
    	
    	List<Pair> pairList = new ArrayList();    	
    	if(lines !=null){    		
    		for (String line : lines) {
    			Pair pair = createRISPairByString(line);
    			pairList.add(pair);
			}
    	}    	
    	return pairList;
    }
    
    /**
     * get a pair from line string (by regex string)
     * @param string - RIS line as string
     * @return Pair - key-value pair created by string line
     */
    public Pair createRISPairByString(String line){
    	String[] lineArr = line.split("\\s-\\s");    	
    	Pair pair = null;
    	if(lineArr.length>1){
    		if(lineArr[0]!=null && lineArr[1]!=null){
    			pair = new Pair(lineArr[0].trim(), lineArr[1].trim());
    			
    		}
    	}
    	return pair;
    }
    
    /**
     * creates a single item in xml  
     * @param item pair list
     * @return xml string of the whole item list
     */
    public String transformItemToXML(List<Pair> item){
    	String xml = "";
    	if(item != null && item.size() > 0){
    		xml = createXMLElement("item",transformItemSubelementsToXML(item));    			   			
    	}
    	return xml;
    }
    
    /**
     * creates the complete item list in xml  
     * @param item pair list
     * @return xml string of the whole item list
     */
    public String transformItemListToXML(List<List<Pair>> itemList){
    	String xml = "<item-list>";
    	
    	if(itemList != null && itemList.size() > 0){
    		
    		for (List<Pair> item : itemList) {
    			xml = xml + "\n" + transformItemToXML(item);
			}
    	}
    	xml = xml + "</item-list>";
    	return xml;
    }
    
    /**
     * creates an xml string of the item pair list
     * @param item pairs as list
     * @return xml String
     */
    public String transformItemSubelementsToXML(List<Pair> item){
    	String xml = "";
    	if(item != null && item.size() > 0){    		
    		  	
    		for (Pair pair : item) {
    			xml = xml + createXMLElement(pair.getKey(),escape(pair.getValue()));
			}
    	}
    	return xml;
    }
    
    /**
     * creates a single element in xml 
     * @param tag - tag name of the element
     * @param value - value of the element
     * @return xml element as string
     */
    public String createXMLElement(String tag, String value){
    	String element = "";
    	if(tag!=null && tag!=""){
    		element = "<"+tag+">"+value+"</"+tag+">";    		
    	}
    	return element;
    }
    
    /**
     * escapes special characters
     * @param input string
     * @return string with escaped characters
     */
    public String escape(String input)
    {
        if(input != null){
            input = input.replace("&", "&amp;");
            input = input.replace("<", "&lt;");
            input = input.replace("\"", "&quot;");
        }
        return input;
    }
    
   
}


