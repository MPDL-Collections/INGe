package org.fao.oa.ingestion.eimscdr;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;

import noNamespace.EimsresourcesDocument;
import noNamespace.ITEMType;
import noNamespace.ItemType;
import noNamespace.ResourcesDocument;

import org.apache.xmlbeans.XmlException;
import org.fao.oa.ingestion.utils.ComponentParser;
import org.fao.oa.ingestion.utils.IngestionProperties;

public class EimsCdrItem
{
    private static File eimsExportFile = null;
    public static final String EIMS_BASE_DIR = IngestionProperties.get("eims.export.file.location");


    public static void main(String... strings)
    {
        String[] filenames = IngestionProperties.get("eims.export.file.names").split(" ");
        // System.out.println(getAll(eimsExportFile).length);
        //ItemType eimsItem = getById(eimsExportFile, "19000");
        //System.out.println(eimsItem.getIdentifierArray(0));
        //System.out.println(eimsItem.getJobnoArray(0));
        parseTest(filenames);
        
        //ArrayList<ItemType> itemList = allEIMSItemsAsList(filenames);
        //System.out.println("total number of EIMS items: " + itemList.size());
        //String filter = "other";
        //filteredList(filenames, filter);
        //ArrayList<ItemType> list = allEIMSItemsAsList(filenames);
        
        //ItemType item = getById(itemList, "49785");
        //System.out.println(item);
        
    }

    public static ItemType[] getAll(File file)
    {
        try
        {
            EimsresourcesDocument resourceDoc = EimsresourcesDocument.Factory.parse(file);
            ItemType[] items = resourceDoc.getEimsresources().getItemArray();
            return items;
        }
        catch (XmlException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    
    public static ItemType getById(ArrayList<ItemType> itemList, String id)
    {
        for (ItemType item : itemList)
        {
                if (item.getIdentifier().equalsIgnoreCase(id))
                {
                    return item;
                }
        }
        return null;
    }
       
    
    public static void parseTest(String[] names)
    {
        int eimsitems = 0;

        for (String name : names)
        {
            File faodocItemFile = new File(EIMS_BASE_DIR + name);
            System.out.println("Attempt to parse file " + faodocItemFile.getName());
            try
            {
                EimsresourcesDocument resDoc = EimsresourcesDocument.Factory.parse(faodocItemFile);
                ItemType[] items = resDoc.getEimsresources().getItemArray();
                System.out.println(name + " contains " + items.length + " items");
                eimsitems = eimsitems + items.length;
                for (ItemType i : items)
                {
                    if (i.getContext() != null)
                    {
                        System.out.println(i.getContext().getCode() + " " + i.getContext().getStringValue());
                    }
                }
            }
            catch (XmlException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        System.out.println("total number of eimsitems " + eimsitems);
    }
    
    public static ArrayList<ItemType> allEIMSItemsAsList(String[] filenames)
    {
        ArrayList<ItemType> allEIMSItemsList = new ArrayList<ItemType>();
        Set<String> maintypeList = new HashSet<String>();
        Set<String> genreList = new HashSet<String>();

        for (String name : filenames)
        {
            File eimsFile = new File(EIMS_BASE_DIR + name);
            try
            {
                EimsresourcesDocument resDoc = EimsresourcesDocument.Factory.parse(eimsFile);
                ItemType[] items = resDoc.getEimsresources().getItemArray();
                for (ItemType item : items)
                {
                    allEIMSItemsList.add(item);
                    
                    String maintype = item.getMaintype().getStringValue();
                    maintypeList.add(maintype);
                    String genre = item.getGenre().getStringValue();
                    genreList.add(genre);
                }
            }
            catch (XmlException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        System.out.println("List contains " + allEIMSItemsList.size() + " EIMS items");
        System.out.println("maintypes are " + maintypeList.toString());
        System.out.println("genres are " + genreList.toString());

        return allEIMSItemsList;
    }
    
    
    public static ArrayList<ItemType> filteredList(String[] filenames, String filter)
    {
        ArrayList<ItemType> filteredEimsList = new ArrayList<ItemType>();
        int itemCounter = 0;
        for (String name : filenames)
        {
                File eimsFile = new File(EIMS_BASE_DIR + name);
                try
                {
                    EimsresourcesDocument resDoc = EimsresourcesDocument.Factory.parse(eimsFile);
                    ItemType[] items = resDoc.getEimsresources().getItemArray();

                    for (ItemType item : items)
                    {
                        //if (item.getMaintype().getStringValue().equalsIgnoreCase(filter))
                        if (item.getDate() != null)
                        {
                            filteredEimsList.add(item);
                        }
                        else
                        {
                            itemCounter++;
                            System.out.println(itemCounter + " " + item.getIdentifier() + " has no date " + item.getJobno());
                        }
                    }
                }
                catch (XmlException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                catch (IOException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
        }
        System.out.println("List contains " + filteredEimsList.size() + " EIMS items with maintype " + filter);
        return filteredEimsList;
    }
    
    public static void checkXMLStructure(ArrayList<ItemType> eimsItemList) throws Exception
    {
        Set moreThanOnce = new TreeSet<String>();
        Set onlyOnce = new TreeSet<String>();
        Set zero = new TreeSet<String>();
        for (ItemType eimsitem : eimsItemList)
        {
            Class itemTypeClass = eimsitem.getClass();
            for (Method method : itemTypeClass.getDeclaredMethods())
            {
                if (method.getName().contains("sizeOf"))
                {
                    Object size = method.invoke(eimsitem, null);
                    
                    if (Integer.valueOf(size.toString()) > 1)
                    {
                        moreThanOnce.add(method.getName());
                    }
                    
                    if (Integer.valueOf(size.toString()) == 1)
                    {
                        onlyOnce.add(method.getName());
                    }
                    
                    if (Integer.valueOf(size.toString()) == 0)
                    {
                        zero.add(method.getName());
                    }
                    
                }
            }
        }
        
        System.out.println("zero: " + zero);
        System.out.println("once: " + onlyOnce);
        System.out.println("many: " + moreThanOnce);
    }
}
