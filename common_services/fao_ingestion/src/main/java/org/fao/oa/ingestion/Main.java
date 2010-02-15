package org.fao.oa.ingestion;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import noNamespace.BibDocument;
import noNamespace.EimsDocument;
import noNamespace.ITEMType;
import noNamespace.ItemType;
import noNamespace.KosDocument;
import noNamespace.FAOJournalDocument.FAOJournal;

import org.fao.oa.ingestion.eimscdr.EimsCdrItem;
import org.fao.oa.ingestion.faodoc.AgrovocSkos;
import org.fao.oa.ingestion.faodoc.ConferenceName;
import org.fao.oa.ingestion.faodoc.FaodocItem;
import org.fao.oa.ingestion.faodoc.JournalName;
import org.fao.oa.ingestion.faodoc.SeriesName;
import org.fao.oa.ingestion.foxml.AgrisAPDatastream;
import org.fao.oa.ingestion.foxml.BibDatastream;
import org.fao.oa.ingestion.foxml.EimsDatastream;
import org.fao.oa.ingestion.foxml.Foxml;
import org.fao.oa.ingestion.foxml.KosDatastream;
import org.fao.oa.ingestion.foxml.ModsDatastream;
import org.fao.oa.ingestion.uris.FaoUris;
import org.fao.oa.ingestion.uris.FaoUris.URI_TYPE;
import org.fao.oa.ingestion.utils.IngestionProperties;
import org.fao.oa.ingestion.utils.XBeanUtils;
import org.purl.agmes.x11.ResourcesDocument;

import fedora.fedoraSystemDef.foxml.DigitalObjectDocument;
import gov.loc.mods.v3.ModsDocument;

public class Main
{
    public static final String FOXML_DESTINATION_DIR = IngestionProperties.get("fao.foxml.destination.location");
    
    public static void main(String[] args)
    {
        if (args.length < 1)
        {
            System.out.println("USAGE: duplicates / foxml [all / start-end]");
        }
        else
        {
            if (args.length == 1 && args[0].equalsIgnoreCase("duplicates"))
            {
                DuplicateDetection dd = new DuplicateDetection();
                dd.checkMMS();
            }
            else
            {
                if (args.length == 2 && args[0].equalsIgnoreCase("foxml"))
                {
                   createFoxml(args[1]);
                }
            }
        }
    }

   
    

    public static void testURIFiles()
    {
        FaoUris uris = new FaoUris();
        ArrayList<Object> uriList = uris.getUriList(URI_TYPE.JOURNALS);
        for (Object o : uriList)
        {
            String uri = ((FAOJournal)o).getID();
            System.out.println(uri);
        }
    }

    public static List<String[]> parseLogFile(String arg) throws Exception
    {
        File logfile = new File("ingestion.log");
        BufferedReader reader = new BufferedReader(new FileReader(logfile));
        ArrayList<String[]> dups = new ArrayList<String[]>();
        List<String[]> subList = null;
        String line;
        while ((line = reader.readLine()) != null)
        {
            String[] values = line.split("\t");
            dups.add(values);
        }
        
        System.out.println(dups.size() + " items !!!");
        if (arg.equalsIgnoreCase("all"))
        {
            subList = dups;
        }
        else
        {
            String[] indices = arg.split("-");
            subList = dups.subList(Integer.valueOf(indices[0]), Integer.valueOf(indices[1]));
        }
        System.out.println(subList.size());
        for (String[] vals : subList)
        {
            //System.out.println(vals[0] + "  " + vals[1] + "  " + vals[2]);
            if (vals.length > 1)
            {
                System.out.println(vals[0].substring(vals[0].length() -12, vals[0].length()) + "  " + vals[1] + "  " + vals[2]);
            }
            else
            {
                System.out.println(vals[0].substring(vals[0].length() -12, vals[0].length()));
            }
        }
        return subList;
    }

    public static <T> List getDuplicate(Collection<T> list)
    {
        final List<T> duplicatedObjects = new ArrayList<T>();
        Set<T> set = new HashSet<T>()
        {
            @Override
            public boolean add(T e)
            {
                if (contains(e))
                {
                    duplicatedObjects.add(e);
                }
                return super.add(e);
            }
        };
        for (T t : list)
        {
            set.add(t);
        }
        return duplicatedObjects;
    }
    
    public static void testObjectMerge()
    {
        String[] faodocFiles = IngestionProperties.get("faodoc.export.file.names").split(" ");
        String filter = "M";
        ArrayList<ITEMType> faodocList = FaodocItem.filteredList(faodocFiles, filter);
        String arn = "XF2006123588";
        ITEMType faodoc = FaodocItem.getByARN(faodocList, arn);
        String[] eimsFiles = IngestionProperties.get("eims.export.file.names").split(" ");
        ArrayList<ItemType> eimsList = EimsCdrItem.allEIMSItemsAsList(eimsFiles);
        String id = "169463";
        ItemType eims = EimsCdrItem.getById(eimsList, id);
        
        System.out.println(faodoc.xmlText(XBeanUtils.getDefaultOpts()));
        System.out.println(eims.xmlText(XBeanUtils.getDefaultOpts()));
        
        ModsDocument merged = new ModsDatastream().merge(eims, faodoc);
        if (XBeanUtils.validation(merged))
        {
            System.out.println(merged.xmlText(XBeanUtils.getModsOpts()));
        }
        else
        {
            System.out.println("INVALID MODS");
            System.out.println(merged.xmlText(XBeanUtils.getModsOpts()));
        }
        ResourcesDocument agris;
        agris = new AgrisAPDatastream().merge(faodoc, eims);
        if (XBeanUtils.validation(agris))
        {
            System.out.println(agris.xmlText(XBeanUtils.getAgrisOpts()));
        }
        else
        {
            System.out.println("INVALID AGRIS_AP");
            System.out.println(agris.xmlText(XBeanUtils.getAgrisOpts()));
        }
        EimsDocument eimsDoc;
        eimsDoc = new EimsDatastream().merge(eims, faodoc);
        if (XBeanUtils.validation(eimsDoc))
        {
            System.out.println(eimsDoc.xmlText(XBeanUtils.getDefaultOpts()));
        }
        else
        {
            System.out.println("INVALID EIMS!!!");
            System.out.println(eimsDoc.xmlText(XBeanUtils.getDefaultOpts()));
        }
        
        BibDocument bibDoc;
        bibDoc = new BibDatastream().create(faodoc);
        if (XBeanUtils.validation(bibDoc))
        {
            System.out.println(bibDoc.xmlText(XBeanUtils.getDefaultOpts()));
        }
        else
        {
            System.out.println("INVALID BIB!!!");
            System.out.println(bibDoc.xmlText(XBeanUtils.getDefaultOpts()));
        }
        
        KosDocument kosDoc;
        kosDoc = new KosDatastream().merge(faodoc, eims);
        if (XBeanUtils.validation(kosDoc))
        {
            System.out.println(kosDoc.xmlText(XBeanUtils.getDefaultOpts()));
        }
        else
        {
            System.out.println("INVALID BIB!!!");
            System.out.println(kosDoc.xmlText(XBeanUtils.getDefaultOpts()));
        }
        
        
    }
    
    public static void testObjectCreation()
    {
        String[] faodocFiles = IngestionProperties.get("faodoc.export.file.names").split(" ");
        String filter = "M";
        ArrayList<ITEMType> faodocList = FaodocItem.filteredList(faodocFiles, filter);
        String arn = "XF2006219523";
        new FaodocItem();
        ITEMType faodoc = FaodocItem.getByARN(faodocList, arn);
        String[] eimsFiles = IngestionProperties.get("eims.export.file.names").split(" ");
        ArrayList<ItemType> eimsList = EimsCdrItem.allEIMSItemsAsList(eimsFiles);
        String id = "137824";
        ItemType eims = EimsCdrItem.getById(eimsList, id);
        
        
        ModsDocument merged = new ModsDatastream().faodoc(faodoc);
        if (XBeanUtils.validation(merged))
        {
            System.out.println(merged.xmlText(XBeanUtils.getModsOpts()));
        }
        else
        {
            System.out.println("INVALID MODS");
        }
        ResourcesDocument agris;
        agris = new AgrisAPDatastream().create4Faodoc(faodoc);
        if (XBeanUtils.validation(agris))
        {
            System.out.println(agris.xmlText(XBeanUtils.getAgrisOpts()));
        }
        else
        {
            System.out.println("INVALID AGRIS_AP");
            System.out.println(agris.xmlText(XBeanUtils.getAgrisOpts()));
        }
        
        BibDocument bibDoc;
        bibDoc = new BibDatastream().create(faodoc);
        if (XBeanUtils.validation(bibDoc))
        {
            System.out.println(bibDoc.xmlText(XBeanUtils.getDefaultOpts()));
        }
        else
        {
            System.out.println("INVALID BIB!!!");
            System.out.println(bibDoc.xmlText(XBeanUtils.getDefaultOpts()));
        }
        
        KosDocument kosDoc;
        kosDoc = new KosDatastream().create4Faodoc(faodoc);
        if (XBeanUtils.validation(kosDoc))
        {
            System.out.println(kosDoc.xmlText(XBeanUtils.getDefaultOpts()));
        }
        else
        {
            System.out.println("INVALID BIB!!!");
            System.out.println(kosDoc.xmlText(XBeanUtils.getDefaultOpts()));
        }
        
        
    }
    public static void testControlledVocab()
    {
        SeriesName sname = new SeriesName();
        String[] vals = sname.getSpanish("Serie Tecnica. Manual Tecnico - Centro Agronomico Tropical de Investigacion y Ensenanza (Costa Rica)");
        System.out.println(vals[0] + " " + vals[1] + " " + vals[2]);
        JournalName jname = new JournalName();
        String[] jvals = jname.get("Boletim de Informacao (OAM)");
        System.out.println(jvals[0] + " " + jvals[1] + " " + jvals[2]);
        String[] filenames = IngestionProperties.get("faodoc.export.file.names").split(" ");
        String filter = "M";
        ArrayList<ITEMType> itemList = FaodocItem.filteredList(filenames, filter);
        String id = "XF2006361095";
        ITEMType faodoc = FaodocItem.getByARN(itemList, id);
        System.out.println(faodoc.getCONFERENCEArray(0).getCONFEN());
        ConferenceName cname = new ConferenceName();
        String[] cvals = cname.getEnglish(faodoc.getCONFERENCEArray(0), faodoc.getCONFERENCEArray(0).getCONFEN());
        System.out.println(cvals[0] + " " + cvals[1]);
      //LanguageCodes lc = new LanguageCodes();
        //String[] codes = lc.getIso639Codes2("ar");
        //System.out.println(codes[0] + "  " + codes[1] + "  " + codes[2]);
    }
    
    public static void createFoxml(String arg)
    {
        String[] faodocFiles = IngestionProperties.get("faodoc.export.file.names").split(" ");
        String filter = "M";
        ArrayList<ITEMType> faodocList = FaodocItem.filteredList(faodocFiles, filter);
        String[] eimsFiles = IngestionProperties.get("eims.export.file.names").split(" ");
        ArrayList<ItemType> eimsList = EimsCdrItem.allEIMSItemsAsList(eimsFiles);
        try
        {
            List<String[]> duplicates = parseLogFile(arg);
            for (String[] duplicate : duplicates)
            {
                String arn = null;
                String id = null;
                ITEMType faodoc = null;
                ItemType eims = null;
                int size = duplicate.length;
                arn = duplicate[0].substring(duplicate[0].length() -12, duplicate[0].length());
                faodoc = FaodocItem.getByARN(faodocList, arn);
                if (size > 1)
                {
                    id = duplicate[1];
                    eims = EimsCdrItem.getById(eimsList, id);
                }
                if (arn != null && id != null)
                {
                    System.out.println("Merging EIMS " + id + " with FAODOC " + arn);
                    DigitalObjectDocument fox = new Foxml().merge(faodoc, eims);
                    fox.save(new File(FOXML_DESTINATION_DIR + arn + "_" + id), XBeanUtils.getFoxmlOpts());
                }
                else
                {
                    if (arn != null)
                    {
                        System.out.println("Creating FOXML for " + arn);
                        DigitalObjectDocument fox = new Foxml().merge(faodoc, null);
                        fox.save(new File(FOXML_DESTINATION_DIR + arn), XBeanUtils.getFoxmlOpts());
                    }
                }
                //DigitalObjectDocument fox = new Foxml().merge(faodoc, eims);
                //fox.save(new File(FOXML_DESTINATION_DIR + arn + "_" + id), XBeanUtils.getFoxmlOpts());
                //System.out.println("file name " + FOXML_DESTINATION_DIR + arn + "_" + id);
            }
        }
        catch (Exception e1)
        {
            e1.printStackTrace();
        }
    }
}
