package org.fao.oa.ingestion;

import java.util.ArrayList;

import noNamespace.ITEMType;
import noNamespace.ItemType;
import noNamespace.SERIESType;
import noNamespace.TitleType;

import org.apache.log4j.Logger;
import org.fao.oa.ingestion.eimscdr.EimsCdrItem;
import org.fao.oa.ingestion.faodoc.FaodocItem;
import org.fao.oa.ingestion.utils.IngestionProperties;

/**
 * @author Wilhelm Frank (MPDL)
 * @version class to perform duplicates detection.
 */
public class DuplicateDetection
{
    /**
     * public constructor.
     */
    public DuplicateDetection()
    {
    }

    ArrayList<ITEMType> faodocItems = null;
    ArrayList<ItemType> eimsItems = null;
    boolean hasDuplicate = false;
    Logger logger = Logger.getLogger("ingestion");

    /**
     * compare all FAODOC items with BIBLEVEL 'M' or 'MS' with all EIMS_CDR items of maintype 'publication'.
     */
    public void checkMMS()
    {
        String[] faodoc_filenames = IngestionProperties.get("faodoc.export.file.names").split(" ");
        String[] eims_filenames = IngestionProperties.get("eims.export.file.names").split(" ");
        faodocItems = FaodocItem.filteredList(faodoc_filenames, "M");
        eimsItems = EimsCdrItem.allEIMSItemsAsList(eims_filenames, "publications");
        for (ITEMType faodoc : faodocItems)
        {
            hasDuplicate = false;
            ArrayList<String> faodocJN = null;
            ArrayList<String> faodocLANG = null;
            if (faodoc.sizeOfJNArray() > 0 && faodoc.sizeOfLANGArray() > 0)
            {
                faodocJN = new ArrayList<String>();
                faodocLANG = new ArrayList<String>();
                for (String jobno : faodoc.getJNArray())
                {
                    faodocJN.add(jobno);
                }
                for (String lang : faodoc.getLANGArray())
                {
                    faodocLANG.add(lang);
                }
            }
            for (ItemType eims : eimsItems)
            {
                String eims_jobno = null;
                String eims_langkey = null;
                if (eims.getDate() != null)
                {
                    if (eims.getJobno() != null && eims.getLangkey() != null)
                    {
                        eims_jobno = eims.getJobno();
                        eims_langkey = eims.getLangkey();
                    }
                }
                if (faodocJN != null && eims_jobno != null && faodocLANG != null && eims_langkey != null)
                {
                    for (String jn : faodocJN)
                    {
                        for (String lang : faodocLANG)
                        {
                            if ((jn.equalsIgnoreCase(eims_jobno) || eims_jobno.startsWith(jn))
                                    && lang.startsWith(eims_langkey))
                            {
                                StringBuilder message = new StringBuilder(faodoc.getARNArray(0) + "\t"
                                        + eims.getIdentifier() + "\t");
                                message.append("jobno and lang\t");
                                message.append("[" + jn + " " + lang + "]\t[" + eims_jobno + " " + eims_langkey + "]");
                                logger.info(message);
                                hasDuplicate = true;
                            }
                        }
                    }
                }
                else
                {
                    checkURL(faodoc, eims);
                }
            }
            if (hasDuplicate == false)
            {
                logger.info(faodoc.getARNArray(0));
            }
        }
        System.out.println("Successfully parsed " + faodocItems.size() + " FAODOC items");
    }

    /**
     * compare a FAODOC item with an EIMS_CDR item. check if any FAODOC URL equals EIMS_CDR html or pdf URL.
     */
    public void checkURL(ITEMType faodoc, ItemType eims)
    {
        ArrayList<String> faodocURLs = null;
        if (faodoc.sizeOfURLArray() > 0)
        {
            faodocURLs = new ArrayList<String>();
            for (String url : faodoc.getURLArray())
            {
                faodocURLs.add(url);
            }
        }
        String eims_html = null;
        String eims_pdf = null;
        String eims_zip = null;
        if (eims.getURL() != null)
        {
            eims_html = eims.getURL().getStringValue();
        }
        if (eims.getPDFURL() != null)
        {
            eims_pdf = eims.getPDFURL().getStringValue();
        }
        if (eims.getZIPURL() != null)
        {
            eims_zip = eims.getZIPURL().getStringValue();
        }
        if (faodocURLs != null && (eims_html != null || eims_pdf != null || eims_zip != null))
        {
            for (String url : faodocURLs)
            {
                if (url.equalsIgnoreCase(eims_html) || url.equalsIgnoreCase(eims_pdf) || url.equalsIgnoreCase(eims_zip))
                {
                    hasDuplicate = true;
                    StringBuilder message = new StringBuilder(faodoc.getARNArray(0) + "\t" + eims.getIdentifier()
                            + "\t");
                    message.append("URL pdf / html\t");
                    message.append("[" + url + "]\t[" + eims_html + "]\t[" + eims_pdf + "]" + "]\t[" + eims_zip + "]");
                    logger.info(message);
                }
            }
        }
        else
        {
            // checkTitles(faodoc, eims);
            checkTitlesWorkarround(faodoc, eims);
        }
    }

    /**
     * compare a FAODOC item with an EIMS_CDR item. check if any FAODOC TITLE equals EIMS_CDR title(s).
     */
    public void checkTitles(ITEMType faodoc, ItemType eims)
    {
        ArrayList<String> faodocTitles = null;
        ArrayList<String> faodocDates = null;
        if (faodoc.sizeOfTITENArray() > 0 || faodoc.sizeOfTITESArray() > 0 || faodoc.sizeOfTITFRArray() > 0
                || faodoc.sizeOfTITOTArray() > 0 || faodoc.sizeOfTITTRArray() > 0)
        {
            faodocTitles = new ArrayList<String>();
            if (faodoc.sizeOfTITENArray() > 0)
            {
                for (String title : faodoc.getTITENArray())
                {
                    faodocTitles.add(title);
                }
            }
            if (faodoc.sizeOfTITESArray() > 0)
            {
                for (String title : faodoc.getTITESArray())
                {
                    faodocTitles.add(title);
                }
            }
            if (faodoc.sizeOfTITFRArray() > 0)
            {
                for (String title : faodoc.getTITFRArray())
                {
                    faodocTitles.add(title);
                }
            }
            if (faodoc.sizeOfTITOTArray() > 0)
            {
                for (String title : faodoc.getTITOTArray())
                {
                    faodocTitles.add(title);
                }
            }
            if (faodoc.sizeOfTITTRArray() > 0)
            {
                for (String title : faodoc.getTITTRArray())
                {
                    faodocTitles.add(title);
                }
            }
        }
        if (faodoc.sizeOfDATEISSUEArray() > 0 || faodoc.sizeOfPUBDATEArray() > 0 || faodoc.sizeOfPUBYEARArray() > 0
                || faodoc.sizeOfYEARPUBLArray() > 0)
        {
            faodocDates = new ArrayList<String>();
            if (faodoc.sizeOfDATEISSUEArray() > 0)
            {
                for (String date : faodoc.getDATEISSUEArray())
                {
                    faodocDates.add(date);
                }
            }
            if (faodoc.sizeOfPUBDATEArray() > 0)
            {
                for (String date : faodoc.getPUBDATEArray())
                {
                    faodocDates.add(date);
                }
            }
            if (faodoc.sizeOfPUBYEARArray() > 0)
            {
                for (String date : faodoc.getPUBYEARArray())
                {
                    faodocDates.add(date);
                }
            }
            if (faodoc.sizeOfYEARPUBLArray() > 0)
            {
                for (String date : faodoc.getYEARPUBLArray())
                {
                    faodocDates.add(date);
                }
            }
        }
        if (faodocDates != null && eims.getDate() != null)
        {
            if (eims.sizeOfTitleArray() > 0)
            {
                for (TitleType eims_title : eims.getTitleArray())
                {
                    if (faodocTitles != null)
                    {
                        if (faodocTitles.contains(eims_title.getStringValue()))
                        {
                            if (faodocDates.contains(eims.getDate().getStringValue()))
                            {
                                hasDuplicate = true;
                                StringBuilder message = new StringBuilder(faodoc.getARNArray(0) + "\t"
                                        + eims.getIdentifier() + "\t");
                                message.append("title and date \t");
                                message.append(faodocTitles + " " + faodocDates + "\t[" + eims_title.getStringValue()
                                        + " " + eims.getDate().getStringValue() + " " + eims_title.getLang() + "]");
                                logger.info(message);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * compare all FAODOC items with BIBLEVEL 'AS' with all EIMS_CDR items of maintype 'article'.
     */
    public void checkAS()
    {
        String[] faodoc_filenames = IngestionProperties.get("faodoc.export.file.names").split(" ");
        String[] eims_filenames = IngestionProperties.get("eims.export.file.names.articles").split(" ");
        faodocItems = FaodocItem.filteredList(faodoc_filenames, "AS");
        eimsItems = EimsCdrItem.allEIMSItemsAsList(eims_filenames, "articles");
        for (ITEMType faodoc : faodocItems)
        {
            if (faodoc.getBIBLEVELArray(0) != null && faodoc.getBIBLEVELArray(0).equalsIgnoreCase("AS"))
            {
                hasDuplicate = false;
                for (ItemType eims : eimsItems)
                {
                    if (eims.getMaintype() != null && eims.getMaintype().getStringValue().equalsIgnoreCase("article"))
                    {
                        checkURL(faodoc, eims);
                    }
                }
            }
            if (hasDuplicate == false)
            {
                logger.info(faodoc.getARNArray(0));
            }
        }
        System.out.println("Successfully parsed " + faodocItems.size() + " FAODOC items");
    }

    /**
     * compare a FAODOC item with an EIMS_CDR item. check if any FAODOC TITLE equals EIMS_CDR title(s). according to IS,
     * do this only, if the SER_TIT_XX equals 'Unasylva'
     */
    public void checkTitlesWorkarround(ITEMType faodoc, ItemType eims)
    {
        ArrayList<String> faodocTitles = null;
        boolean hasUnasylva = false;
        if (faodoc.sizeOfSERIESArray() > 0)
        {
            SERIESType[] series = faodoc.getSERIESArray();
            for (SERIESType s : series)
            {
                if (s.isSetSERTITEN() && s.getSERTITEN().startsWith("Unasylva"))
                {
                    hasUnasylva = true;
                }
                if (s.isSetSERTITFR() && s.getSERTITFR().startsWith("Unasylva"))
                {
                    hasUnasylva = true;
                }
                if (s.isSetSERTITES() && s.getSERTITES().startsWith("Unasylva"))
                {
                    hasUnasylva = true;
                }
                if (s.isSetSERTITOT() && s.getSERTITOT().startsWith("Unasylva"))
                {
                    hasUnasylva = true;
                }
            }
        }
        if ( hasUnasylva && (faodoc.sizeOfTITENArray() > 0 || faodoc.sizeOfTITESArray() > 0 || faodoc.sizeOfTITFRArray() > 0
                || faodoc.sizeOfTITOTArray() > 0 || faodoc.sizeOfTITTRArray() > 0))
        {
            faodocTitles = new ArrayList<String>();
            if (faodoc.sizeOfTITENArray() > 0)
            {
                for (String title : faodoc.getTITENArray())
                {
                    faodocTitles.add(title);
                }
            }
            if (faodoc.sizeOfTITESArray() > 0)
            {
                for (String title : faodoc.getTITESArray())
                {
                    faodocTitles.add(title);
                }
            }
            if (faodoc.sizeOfTITFRArray() > 0)
            {
                for (String title : faodoc.getTITFRArray())
                {
                    faodocTitles.add(title);
                }
            }
            if (faodoc.sizeOfTITOTArray() > 0)
            {
                for (String title : faodoc.getTITOTArray())
                {
                    faodocTitles.add(title);
                }
            }
            if (faodoc.sizeOfTITTRArray() > 0)
            {
                for (String title : faodoc.getTITTRArray())
                {
                    faodocTitles.add(title);
                }
            }
        }
        if (eims.sizeOfTitleArray() > 0)
        {
            for (TitleType eims_title : eims.getTitleArray())
            {
                if (faodocTitles != null)
                {
                    if (faodocTitles.contains(eims_title.getStringValue()))
                    {
                        hasDuplicate = true;
                        StringBuilder message = new StringBuilder(faodoc.getARNArray(0) + "\t" + eims.getIdentifier()
                                + "\t");
                        message.append("title and date \t");
                        message.append(faodocTitles + "\t[" + eims_title.getStringValue() + " " + eims_title.getLang()
                                + "]");
                        logger.info(message);
                    }
                }
            }
        }
    }
}
