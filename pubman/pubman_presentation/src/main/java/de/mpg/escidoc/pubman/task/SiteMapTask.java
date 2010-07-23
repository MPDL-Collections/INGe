/*
*
* CDDL HEADER START
*
* The contents of this file are subject to the terms of the
* Common Development and Distribution License, Version 1.0 only
* (the "License"). You may not use this file except in compliance
* with the License.
*
* You can obtain a copy of the license at license/ESCIDOC.LICENSE
* or http://www.escidoc.de/license.
* See the License for the specific language governing permissions
* and limitations under the License.
*
* When distributing Covered Code, include this CDDL HEADER in each
* file and include the License file at license/ESCIDOC.LICENSE.
* If applicable, add the following below this CDDL HEADER, with the
* fields enclosed by brackets "[]" replaced with your own identifying
* information: Portions Copyright [yyyy] [name of copyright owner]
*
* CDDL HEADER END
*/

/*
* Copyright 2006-2010 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/ 

package de.mpg.escidoc.pubman.task;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.naming.InitialContext;

import org.apache.log4j.Logger;

import de.mpg.escidoc.services.common.exceptions.TechnicalException;
import de.mpg.escidoc.services.common.util.ResourceUtil;
import de.mpg.escidoc.services.common.valueobjects.AffiliationVO;
import de.mpg.escidoc.services.common.valueobjects.ContainerVO;
import de.mpg.escidoc.services.common.valueobjects.ItemVO;
import de.mpg.escidoc.services.common.valueobjects.interfaces.SearchResultElement;
import de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO;
import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.search.Search;
import de.mpg.escidoc.services.search.query.ItemContainerSearchResult;
import de.mpg.escidoc.services.search.query.OrgUnitsSearchResult;
import de.mpg.escidoc.services.search.query.PlainCqlQuery;
import de.mpg.escidoc.services.search.query.SearchQuery;

/**
 * Thread that creates Sitemap files.
 *
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class SiteMapTask extends Thread
{
    private static final Logger logger = Logger.getLogger(SiteMapTask.class);
    
    private Search search;
    private ArrayList<String> contentModels;
    private FileWriter fileWriter = null;
    private String instanceUrl;
    private String contextPath;
    private String itemPattern;
    private SimpleDateFormat dateFormat;
    private String contentModel;
    
    private int interval;
    
    private int maxItemsPerFile;
    private int maxItemsPerRetrieve;
    
    private int retrievalTimeout;

    private boolean signal = false;
    
    private List<File> files = new ArrayList<File>();
    
    /**
     * {@inheritDoc}
     */
    public void run()
    {
        
        try
        {
            logger.info("Starting to create Sitemap.");
            
            InitialContext context = new InitialContext();
            search = (Search) context.lookup(Search.SERVICE_NAME);
            
            instanceUrl = PropertyReader.getProperty("escidoc.pubman.instance.url");
            contextPath = PropertyReader.getProperty("escidoc.pubman.instance.context.path");
            itemPattern = PropertyReader.getProperty("escidoc.pubman.item.pattern");
            
            interval = Integer.parseInt(PropertyReader.getProperty("escidoc.pubman.sitemap.task.interval"));

            maxItemsPerFile = Integer.parseInt(PropertyReader.getProperty("escidoc.pubman.sitemap.max.items"));
            maxItemsPerRetrieve = Integer.parseInt(PropertyReader.getProperty("escidoc.pubman.sitemap.retrieve.items"));
            
            retrievalTimeout = Integer.parseInt(PropertyReader.getProperty("escidoc.pubman.sitemap.retrieve.timeout"));

            contentModel = PropertyReader.getProperty("escidoc.framework_access.content-model.id.publication");
            
            contentModels = new ArrayList<String>();
            contentModels.add(contentModel);
            
            dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            changeFile();
            
            int alreadyWritten = addViewItemPages();
            addOUSearchResultPages(alreadyWritten);
            
            finishSitemap();
            
            String appPath;
            try
            {
                appPath = ResourceUtil.getResourceAsFile("EditItemPage.jsp").getAbsolutePath();
            }
            catch (Exception e)
            {
                logger.error("EditItemPage.jsp was not found in web root, terminating sitemap task", e);
                return;
            }
            appPath = appPath.substring(0, appPath.lastIndexOf(System.getProperty("file.separator")) + 1);
            
            if (files.size() == 1)
            {
                File finalFile = new File(appPath + "sitemap.xml");
                try
                {
                    finalFile.delete();
                }
                catch (Exception e)
                {
                    // Unable to delete file, it probably didn't exist
                }
                fileWriter = new FileWriter(appPath + "sitemap.xml");
                
                File newSiteMap = new File(appPath + "sitemap.xml");
                this.copySiteMap(files.get(0), finalFile, (int) files.get(0).length(), true);
            }
            else
            {
                String currentDate = dateFormat.format(new Date());
                
                File indexFile = File.createTempFile("sitemap", ".xml");
                FileWriter indexFileWriter = new FileWriter(indexFile);
                
                indexFileWriter.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                        + "<sitemapindex xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\" "
                        + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
                        + "xsi:schemaLocation=\"http://www.sitemaps.org/schemas/sitemap/0.9 "
                        + "http://www.sitemaps.org/schemas/sitemap/0.9/sitemap.xsd\">\n");
                
                for (int i = 0; i < files.size(); i++)
                {
                    File finalFile = new File(appPath + "sitemap" + (i + 1) + ".xml");
                    try
                    {
                        finalFile.delete();
                    }
                    catch (Exception e)
                    {
                        // Unable to delete file, it probably didn't exist
                    }
                    this.copySiteMap(files.get(i), finalFile, (int) files.get(i).length(), true);
                    
                    indexFileWriter.write("\t<sitemap>\n\t\t<loc>"
                            + instanceUrl + contextPath + "/sitemap"
                            + (i + 1) + ".xml</loc>\n\t\t<lastmod>"
                            + currentDate + "</lastmod>\n\t</sitemap>\n");
                    
                }
                
                indexFileWriter.write("</sitemapindex>\n");
                indexFileWriter.flush();
                indexFileWriter.close();
                
                File finalFile = new File(appPath + "sitemap.xml");
                logger.info("Sitemap file: " + finalFile.getAbsolutePath());
                try
                {
                    finalFile.delete();
                }
                catch (Exception e)
                {
                    // Unable to delete file, it probably didn't exist
                }
                boolean success = this.copySiteMap(indexFile, finalFile, (int) indexFile.length(), true);
                logger.debug("Renaming succeeded: " + success);
            }
            
            
            logger.info("Finished creating Sitemap.");

            sleep(interval * 60 * 1000);
            
            if (!signal)
            {
                Thread nextThread = new SiteMapTask();
                nextThread.start();
            }
        }
        catch (Exception e)
        {
            logger.error("Error creating Sitemap", e);
        }

    }
    
    private boolean copySiteMap(File src, File dest, int bufSize,
            boolean force) throws IOException {
        boolean successful = false;
        if(dest.exists()) {
            if(force) {
                dest.delete();
            } else {
                throw new IOException(
                        "Cannot overwrite existing file: " + dest.getName());
            }
        }
        byte[] buffer = new byte[bufSize];
        int read = 0;
        InputStream in = null;
        OutputStream out = null;
        try {
            in = new FileInputStream(src);
            out = new FileOutputStream(dest);
            while(true) {
                read = in.read(buffer);
                if (read == -1) {
                    break;
                }
                out.write(buffer, 0, read);
                successful = true;
            }
        } finally {
            if (in != null) {
                try {
                    in.close();
                    successful |= src.delete();
                }
                finally {
                    if (out != null) {
                        out.close();
                    }
                }
            }
        }
        return successful;
    }


    private int addViewItemPages()
    {
        
        
        int firstRecord = 0;
        int totalRecords = 0;
        
        //fileWriter.write("<ul>");
        do
        {

            ItemContainerSearchResult itemSearchResult = getItems(firstRecord);
            totalRecords = itemSearchResult.getTotalNumberOfResults().intValue();
            addItemsToSitemap(itemSearchResult);

            firstRecord += maxItemsPerRetrieve;
            
            if (firstRecord <= totalRecords && firstRecord % maxItemsPerFile == 0)
            {
                changeFile();
            }
            
            try
            {
                sleep(retrievalTimeout * 1000);
            }
            catch (InterruptedException e)
            {
                logger.info("Sitemap task interrupted.");
            }
            
        }
        while (firstRecord <= totalRecords);

        return totalRecords;
    }

    private void addOUSearchResultPages(int alreadyWritten)
    {
        
        changeFile();
        
        int firstRecord = 0;
        int totalRecords = 0;
        
        //fileWriter.write("<ul>");
        do
        {

            
            OrgUnitsSearchResult ouSearchResult = getOUs(firstRecord);
            totalRecords = ouSearchResult.getTotalNumberOfResults().intValue();
            addOUsToSitemap(ouSearchResult);
    
            firstRecord += maxItemsPerRetrieve;
            
            if (firstRecord <= totalRecords && firstRecord % maxItemsPerFile == 0)
            {
                changeFile();
            }
            
            try
            {
                sleep(retrievalTimeout * 1000);
            }
            catch (InterruptedException e)
            {
                logger.info("Sitemap task interrupted.");
            }
            
        }
        while (firstRecord <= totalRecords);

    }
    
    private void changeFile()
    {
        try
        {
            if (fileWriter != null)
            {
                finishSitemap();
            }
            
            File file = File.createTempFile("sitemap", ".xml");
            fileWriter = new FileWriter(file);
            files.add(file);

            startSitemap();

        }
        catch (Exception e)
        {
            logger.error("Error creating sitemap file.", e);
        }
    }

    /**
     * @param contentModels
     * @param orgUnit
     * @return
     * @throws TechnicalException
     * @throws Exception
     */
    private ItemContainerSearchResult getItems(int firstRecord)
    {
        SearchQuery itemQuery = new PlainCqlQuery("(escidoc.content-model.objid=" + contentModel + ")");
        itemQuery.setStartRecord(firstRecord + "");
        itemQuery.setMaximumRecords(maxItemsPerRetrieve + "");
        try
        {
            ItemContainerSearchResult itemSearchResult = search.searchForItemContainer(itemQuery);
            return itemSearchResult;
        }
        catch (Exception e)
        {
            logger.error("Error getting items", e);
            return null;
        }
    }

    /**
     * @param contentModels
     * @param orgUnit
     * @return
     * @throws TechnicalException
     * @throws Exception
     */
    private OrgUnitsSearchResult getOUs(int firstRecord)
    {
        //SearchQuery ouQuery = new PlainCqlQuery("(escidoc.any-identifier=e*)");
        SearchQuery ouQuery = new PlainCqlQuery("(escidoc.public-status=opened or escidoc.public-status=closed)");
        ouQuery.setStartRecord(firstRecord + "");
        ouQuery.setMaximumRecords(maxItemsPerRetrieve + "");
        try
        {
            OrgUnitsSearchResult ouSearchResult = search.searchForOrganizationalUnits(ouQuery);
            return ouSearchResult;
        }
        catch (Exception e)
        {
            logger.error("Error getting ous", e);
            return null;
        }
    }
    
    private void startSitemap()
    {
        try
        {
            fileWriter.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                    + "<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\" "
                    + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
                    + "xsi:schemaLocation=\"http://www.sitemaps.org/schemas/sitemap/0.9 "
                    + "http://www.sitemaps.org/schemas/sitemap/0.9/sitemap.xsd\">\n");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    private void addItemsToSitemap(ItemContainerSearchResult searchResult)
    {
        List<SearchResultElement> results = searchResult.getResultList();
        for (SearchResultElement result : results)
        {
            if (result instanceof ItemVO)
            {
                PubItemVO pubItemVO = new PubItemVO((ItemVO) result);
                try
                {
                    fileWriter.write("\t<url>\n\t\t<loc>");
                    fileWriter.write(instanceUrl);
                    fileWriter.write(contextPath);
                    fileWriter.write(itemPattern.replace("$1", pubItemVO.getVersion().getObjectId()));
                    fileWriter.write("</loc>\n\t\t<lastmod>");
                    fileWriter.write(dateFormat.format(pubItemVO.getModificationDate()));
                    fileWriter.write("</lastmod>\n\t</url>\n");
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
            else
            {
                logger.error("Search result is not an ItemVO, "
                        + "but a " + result.getClass().getSimpleName());
                if (result instanceof ContainerVO)
                {
                    logger.error("Container is " + ((ContainerVO) result).getVersion().getObjectId());
                }
            }
        }
    }
    
    private void addOUsToSitemap(OrgUnitsSearchResult searchResult)
    {
        List<AffiliationVO> results = searchResult.getResults();
        for (AffiliationVO result : results)
        {

            try
            {
                fileWriter.write("\t<url>\n\t\t<loc>");
                fileWriter.write(instanceUrl);
                fileWriter.write(contextPath);
                fileWriter.write("/faces/SearchResultListPage.jsp?cql=((escidoc.any-organization-pids%3D%22");
                fileWriter.write(result.getReference().getObjectId());
                fileWriter.write("%22)+and+(escidoc.objecttype%3D%22item%22))+and+(escidoc.content-model.objid%3D%22");
                fileWriter.write(contentModel);
                fileWriter.write("%22)&amp;searchType=org");
                fileWriter.write("</loc>\n\t</url>\n");
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

        }
    }
    
    private void finishSitemap()
    {
        try
        {
            fileWriter.write("</urlset>");
            fileWriter.flush();
            fileWriter.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Signals this thread to finish itself.
     */
    public void terminate()
    {
        logger.info("Sitemap creation task signalled to terminate.");
        signal = true;
    }
    
    /**
     * @param args String arguments
     */
    public static void main(String[] args)
    {
        Thread nextThread = new SiteMapTask();
        nextThread.start();
    }
}
