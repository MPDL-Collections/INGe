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
 * Copyright 2006-2012 Fachinformationszentrum Karlsruhe Gesellschaft
 * für wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Förderung der Wissenschaft e.V.
 * All rights reserved. Use is subject to license terms.
 */
package de.mpg.escidoc.pubman.multipleimport;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.naming.InitialContext;

import org.apache.axis.types.NonNegativeInteger;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.log4j.Logger;
import org.richfaces.model.UploadItem;

import de.escidoc.www.services.om.ItemHandler;
import de.mpg.escidoc.pubman.multipleimport.ImportLog.ErrorLevel;
import de.mpg.escidoc.pubman.multipleimport.ImportLog.Status;
import de.mpg.escidoc.pubman.multipleimport.processor.ArxivProcessor;
import de.mpg.escidoc.pubman.multipleimport.processor.BibtexProcessor;
import de.mpg.escidoc.pubman.multipleimport.processor.BmcProcessor;
import de.mpg.escidoc.pubman.multipleimport.processor.EdocProcessor;
import de.mpg.escidoc.pubman.multipleimport.processor.EndnoteProcessor;
import de.mpg.escidoc.pubman.multipleimport.processor.EscidocProcessor;
import de.mpg.escidoc.pubman.multipleimport.processor.FormatProcessor;
import de.mpg.escidoc.pubman.multipleimport.processor.MabProcessor;
import de.mpg.escidoc.pubman.multipleimport.processor.Marc21Processor;
import de.mpg.escidoc.pubman.multipleimport.processor.MarcXmlProcessor;
import de.mpg.escidoc.pubman.multipleimport.processor.RisProcessor;
import de.mpg.escidoc.pubman.multipleimport.processor.WosProcessor;
import de.mpg.escidoc.pubman.multipleimport.processor.ZfNProcessor;
import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.common.referenceobjects.ContextRO;
import de.mpg.escidoc.services.common.util.ResourceUtil;
import de.mpg.escidoc.services.common.valueobjects.AccountUserVO;
import de.mpg.escidoc.services.common.valueobjects.FileVO;
import de.mpg.escidoc.services.common.valueobjects.ItemVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.IdentifierVO;
import de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO;
import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.framework.ProxyHelper;
import de.mpg.escidoc.services.framework.ServiceLocator;
import de.mpg.escidoc.services.pubman.PubItemDepositing;
import de.mpg.escidoc.services.search.Search;
import de.mpg.escidoc.services.search.query.ItemContainerSearchResult;
import de.mpg.escidoc.services.search.query.MetadataSearchCriterion;
import de.mpg.escidoc.services.search.query.MetadataSearchCriterion.CriterionType;
import de.mpg.escidoc.services.search.query.MetadataSearchCriterion.LogicalOperator;
import de.mpg.escidoc.services.search.query.MetadataSearchQuery;
import de.mpg.escidoc.services.transformation.Configurable;
import de.mpg.escidoc.services.transformation.Transformation;
import de.mpg.escidoc.services.transformation.TransformationBean;
import de.mpg.escidoc.services.transformation.valueObjects.Format;
import de.mpg.escidoc.services.validation.ItemValidating;
import de.mpg.escidoc.services.validation.valueobjects.ValidationReportItemVO;
import de.mpg.escidoc.services.validation.valueobjects.ValidationReportVO;

/**
 * TODO Description
 * 
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class ImportProcess extends Thread
{
    private static final Logger logger = Logger.getLogger(ImportProcess.class);

    public enum DuplicateStrategy
    {
        NO_CHECK, CHECK, ROLLBACK
    }

    private ImportLog log;
    private File file;
    private Transformation transformation;
    private ItemValidating itemValidating;
    private XmlTransforming xmlTransforming;
    private PubItemDepositing pubItemDepositing;
    private Search search;
    private Format format;
    private ContextRO escidocContext;
    private String publicationContentModel;
    private AccountUserVO user;
    private FormatProcessor formatProcessor;
    private String fileName;
    private boolean rollback;
    private DuplicateStrategy duplicateStrategy;
    private String itemContentModel;
    private Map<String, String> configuration = null;
    private boolean failed = false;
    private static final Format ESCIDOC_FORMAT = new Format("eSciDoc-publication-item", "application/xml", "utf-8");
    private static final Format ENDNOTE_FORMAT = new Format("endnote", "text/plain", "utf-8");
    private static final Format ENDNOTE_ICE_FORMAT = new Format("endnote-ice", "text/plain", "utf-8");
    private static final Format BIBTEX_FORMAT = new Format("bibtex", "text/plain", "utf-8");
    private static final Format ARXIV_FORMAT = new Format("arxiv", "application/xml", "utf-8");
    private static final Format EDOC_FORMAT = new Format("edoc", "application/xml", "utf-8");
    private static final Format EDOC_FORMAT_AEI = new Format("eDoc-AEI", "application/xml", "utf-8");
    private static final Format RIS_FORMAT = new Format("ris", "text/plain", "utf-8");
    private static final Format WOS_FORMAT = new Format("wos", "text/plain", "utf-8");
    private static final Format MAB_FORMAT = new Format("mab", "text/plain", "UTF-8");
    public static final Format ZFN_FORMAT = new Format("zfn_tei", "application/xml", "UTF-8");
    
    private static final Format MARC21_FORMAT = new Format("marc21viaxml", "application/marc", "UTF-8");
    private static final Format MARCXML_FORMAT = new Format("marcxml", "application/marcxml+xml", "UTF-8");
    private static final Format BMC_FORMAT = new Format("bmc_editura", "application/xml", "UTF-8");

    
    private String name;
    private long lastBeat = 0;

    public ImportProcess(String name, String fileName, File file, Format format,
            ContextRO escidocContext, AccountUserVO user, boolean rollback, int duplicateStrategy, Map<String, String> configuration)
    {
        try
        {
            this.publicationContentModel = PropertyReader.getProperty("escidoc.framework_access.content-model.id.publication");
        }
        catch (Exception e)
        {
            throw new RuntimeException("Error getting property 'escidoc.framework_access.content-model.id.publication'", e);
        }
        
        log = new ImportLog("import", user.getReference().getObjectId(), format.getName());
        
        log.setUserHandle(user.getHandle());
        
        log.setPercentage(5);
        // Say Hello
        log.startItem("import_process_started");
        log.finishItem();
        DuplicateStrategy strategy;
        if (duplicateStrategy == 1)
        {
            strategy = DuplicateStrategy.NO_CHECK;
        }
        else if (duplicateStrategy == 2)
        {
            strategy = DuplicateStrategy.CHECK;
        }
        else if (duplicateStrategy == 3)
        {
            strategy = DuplicateStrategy.ROLLBACK;
        }
        else
        {
            throw new RuntimeException("Invalid value " + duplicateStrategy + " for DuplicateStrategy");
        }
        // Initialize
        initialize(name, fileName, file, format, escidocContext, user, rollback, strategy, configuration);
        log.setPercentage(7);
        if (log.isDone())
        {
            return;
        }
        // Validate
        if (!validate(file, format))
        {
            return;
        }
        log.setPercentage(10);
    }

    /**
     * @param inputStream
     * @param format
     */
    private void initialize(String name, String fileName, File file, Format format,
            ContextRO escidocContext, AccountUserVO user, boolean rollback, DuplicateStrategy duplicateStrategy, Map<String, String> configuration)
    {
        log.startItem("import_process_initialize");
        try
        {
            log.setMessage(name);
            log.setContext(escidocContext.getObjectId());
            log.setFormat(format.getName());
            this.name = name;
            this.fileName = fileName;
            this.format = format;
            this.escidocContext = escidocContext;
            this.user = user;
            this.transformation = new TransformationBean();
            this.file = file;
            this.rollback = rollback;
            this.duplicateStrategy = duplicateStrategy;
            InitialContext context = new InitialContext();
            this.itemValidating = (ItemValidating) context.lookup("java:global/pubman_ear/validation/ItemValidatingBean");
            this.xmlTransforming = (XmlTransforming) context.lookup("java:global/pubman_ear/common_logic/XmlTransformingBean");
            this.pubItemDepositing = (PubItemDepositing) context.lookup("java:global/pubman_ear/pubman_logic/PubItemDepositingBean");
            this.search = (Search) context.lookup("java:global/pubman_ear/search/SearchBean");
            this.itemContentModel = PropertyReader.getProperty("escidoc.framework_access.content-model.id.publication");
            this.configuration = configuration;
        }
        catch (Exception e)
        {
            log.addDetail(ErrorLevel.FATAL, "import_process_initialization_failed");
            log.addDetail(ErrorLevel.FATAL, e);
            fail();
        }
        log.finishItem();
    }

    /**
     * @param inputStream
     * @param format
     */
    private boolean validate(File file, Format format)
    {
        log.startItem("import_process_validate");
        if (file == null)
        {
            log.addDetail(ErrorLevel.FATAL, "import_process_inputstream_unavailable");
            fail();
            return false;
        }
        else
        {
            log.addDetail(ErrorLevel.FINE, "import_process_inputstream_available");
        }
        if (format == null)
        {
            log.addDetail(ErrorLevel.FATAL, "import_process_format_unavailable");
            fail();
            return false;
        }
        else
        {
            log.addDetail(ErrorLevel.FINE, "import_process_format_available");
        }
        Format[] allSourceFormats = transformation.getSourceFormats(ESCIDOC_FORMAT);

        boolean found = false;
        for (Format sourceFormat : allSourceFormats)
        {
            if (format.matches(sourceFormat))
            {
                found = true;
                if (setProcessor(format))
                {
                    log.addDetail(ErrorLevel.FINE, "import_process_format_valid");
                }
                else
                {
                    log.addDetail(ErrorLevel.FATAL, "import_process_format_not_supported");
                    fail();
                }
                break;
            }
        }
        if (!found)
        {
            log.addDetail(ErrorLevel.FATAL, "import_process_format_invalid");
            fail();
            return false;
        }
        log.finishItem();
        return true;
    }

    private boolean setProcessor(Format format)
    {
        try
        {
            if (format == null)
            {
                return false;
            }
            else if (ENDNOTE_FORMAT.matches(format) || ENDNOTE_ICE_FORMAT.matches(format))
            {
                this.formatProcessor = new EndnoteProcessor();
            }
            else if (RIS_FORMAT.matches(format))
            {
                this.formatProcessor = new RisProcessor();
            }
            else if (BIBTEX_FORMAT.matches(format))
            {
                this.formatProcessor = new BibtexProcessor();
            }
            else if (ARXIV_FORMAT.matches(format))
            {
                this.formatProcessor = new ArxivProcessor();
            }
            else if (WOS_FORMAT.matches(format))
            {
                this.formatProcessor = new WosProcessor();
            }
            else if (ESCIDOC_FORMAT.matches(format))
            {
                this.formatProcessor = new EscidocProcessor();
            }
            else if (EDOC_FORMAT.matches(format) || EDOC_FORMAT_AEI.matches(format))
            {
                this.formatProcessor = new EdocProcessor();
            }
            else if (MAB_FORMAT.matches(format))
            {
                this.formatProcessor = new MabProcessor();
            }
            else if (ZFN_FORMAT.matches(format))
            {
                this.formatProcessor = new ZfNProcessor();
            }
            else if (BMC_FORMAT.matches(format))
            {
                this.formatProcessor = new BmcProcessor();
            }
            else if (MARCXML_FORMAT.matches(format))
            {
                this.formatProcessor = new MarcXmlProcessor();
            }
            else if (MARC21_FORMAT.matches(format))
            {
                this.formatProcessor = new Marc21Processor();
            }
            else
            {
                return false;
            }
        }
        catch (Exception e)
        {
            log.addDetail(ErrorLevel.FATAL, "import_process_format_error");
            log.addDetail(ErrorLevel.FATAL, e);
            fail();
        }
        this.formatProcessor.setEncoding(format.getEncoding());
        return true;
    }

    private void fail()
    {
        this.failed = true;
        log.finishItem();
        log.startItem(ErrorLevel.FATAL, "import_process_failed");
        log.finishItem();
        if (this.rollback)
        {
            log.setStatus(Status.ROLLBACK);
            rollback();
        }
        log.close();
    }

    private void rollback()
    {
        log.startItem(ErrorLevel.FINE, "import_process_rollback");
        log.finishItem();
        log.close();
        DeleteProcess deleteProcess = new DeleteProcess(log);
        deleteProcess.start();
    }

    /**
     * {@inheritDoc}
     */
    public void run()
    {
        int counter = 0;
        int itemCount = 1;
        if (!failed)
        {
            try
            {
                log.startItem("import_process_start_import");
                this.formatProcessor.setSourceFile(file);
                if (this.formatProcessor.hasNext())
                {
                    itemCount = this.formatProcessor.getLength();
                }
                log.finishItem();
                while (this.formatProcessor.hasNext() && !failed)
                {
                    try
                    {
                        if (log.getCurrentItem() == null)
                        {
                            log.startItem("import_process_import_item");
                        }
                        String singleItem = this.formatProcessor.next();
                        if (failed)
                        {
                            return;
                        }
                        if (singleItem != null && !"".equals(singleItem.trim()))
                        {
                            prepareItem(singleItem);
                        }
                        counter++;
                        log.setPercentage(30 * counter / itemCount + 10);
                    }
                    catch (Exception e)
                    {
                        logger.error("Error during import", e);
                        // log.finishItem();
                        // log.startItem(ErrorLevel.ERROR, "import_process_item_error");
                        log.addDetail(ErrorLevel.ERROR, e);
                        log.finishItem();
                        if (this.rollback)
                        {
                            fail();
                            break;
                        }
                    }
                    heartBeat();
                }
            }
            catch (Exception e)
            {
                logger.error("Error during import", e);
                log.addDetail(ErrorLevel.ERROR, e);
                log.finishItem();
                if (this.rollback)
                {
                    fail();
                }
            }
            if (failed)
            {
                return;
            }
            log.finishItem();
            log.startItem("import_process_preparation_finished");
            log.addDetail(ErrorLevel.FINE, "import_process_no_more_items");
            log.finishItem();
            log.setPercentage(40);
            counter = 0;
            for (int i = 0; i < log.getItems().size(); i++)
            {
                ImportLogItem item = log.getItems().get(i);
                if (item.getStatus() == Status.SUSPENDED && item.getItemVO() != null && !failed)
                {
                    try
                    {
                        log.activateItem(item);
                        
                        //Fetch files for zfn import
                        if (this.format.getName().equalsIgnoreCase("zfn_tei"))
                        {
                            try
                            {
                                //Set file
                                FileVO file = ((ZfNProcessor)this.formatProcessor).getFileforImport(this.configuration,this.user);
                                item.getItemVO().getFiles().add(file);
                            }
                            catch (Exception e) {
                                log.addDetail(ErrorLevel.WARNING, "Could not fetch file for import");
                            }
                        }
                                              
                        log.addDetail(ErrorLevel.FINE, "import_process_save_item");
                        

                        PubItemVO savedPubItem = pubItemDepositing.savePubItem(item.getItemVO(), user);
                        String objid = savedPubItem.getVersion().getObjectId();
                        log.setItemId(objid);
                        log.addDetail(ErrorLevel.FINE, "import_process_item_imported");
                        log.finishItem();
                        counter++;
                        log.setPercentage(55 * counter / itemCount + 40);
                    }
                    catch (Exception e)
                    {
                        logger.error("Error during import", e);
                        // log.finishItem();
                        // log.startItem(ErrorLevel.ERROR, "import_process_item_error");
                        log.addDetail(ErrorLevel.ERROR, e);
                        log.finishItem();
                        if (this.rollback)
                        {
                            fail();
                            break;
                        }
                    }
                }
            }
            if (!failed)
            {
                log.startItem("import_process_finished");
                log.addDetail(ErrorLevel.FINE, "import_process_import_finished");
                log.finishItem();
                try
                {
                    log.startItem("import_process_archive_log");
                    log.addDetail(ErrorLevel.FINE, "import_process_build_task_item");
                    // Store log in repository
                    String taskItemXml = createTaskItemXml();
                   
                    
                    ItemHandler itemHandler = ServiceLocator.getItemHandler(this.user.getHandle());
                    String savedTaskItemXml = itemHandler.create(taskItemXml);
                    
                    // log.addDetail(ErrorLevel.FINE, "import_process_retrieve_log_id");
                    Pattern pattern = Pattern.compile("objid=\"([^\"]+)\"");
                    Matcher matcher = pattern.matcher(savedTaskItemXml);
                    if (matcher.find())
                    {
                        String taskId = matcher.group(1);
                        // log.addDetail(ErrorLevel.FINE, taskId);
                        logger.info("Imported task item: " + taskId);
                    }
                    else
                    {
                        // log.addDetail(ErrorLevel.PROBLEM, "import_process_no_log_id");
                    }
                    log.setPercentage(100);
                }
                catch (Exception e)
                {
                    logger.error("Error during import", e);
                    log.finishItem();
                    log.startItem(ErrorLevel.ERROR, "import_process_error");
                    log.addDetail(ErrorLevel.ERROR, e);
                    fail();
                }
            }
        }
        
        //Close connection if no rollback is done. Otherwise, Connextion is still required for delete process
        if(!this.rollback)
        {
        	log.closeConnection();
        }
        
        file.delete();
    }

    /**
     * Send a request to the framework every 30 minutes to make sure the user handle will not expire.
     */
    private void heartBeat()
    {
        long now = new Date().getTime();
        if ((now - lastBeat) > 1000 * 60 * 30)
        {
            logger.info("Refreshing " + log.getUserHandle());
            lastBeat = now;
            try
            {
                ServiceLocator.getContextHandler(log.getUserHandle()).retrieve(log.getContext());
            }
            catch (Exception e) {
                logger.warn("Heartbeat error", e);
            }
        }
    }

    private String createTaskItemXml()
    {
        try
        {
            
        	String fwUrl = de.mpg.escidoc.services.framework.ServiceLocator.getFrameworkUrl();
            HttpClient client = new HttpClient();
            ProxyHelper.setProxy(client, fwUrl);
        
        	StringBuilder sb = new StringBuilder(ResourceUtil.getResourceAsString("multipleImport/ImportTaskTemplate.xml", ImportProcess.class.getClassLoader()));
        	replace("$01", escape(this.escidocContext.getObjectId()), sb);
        	replace("$02", escape(PropertyReader.getProperty("escidoc.import.task.content-model")), sb);
        	replace("$03", escape("Import Task Item for import " + name + " "), sb);

        	//Upload original data
            PutMethod method = new PutMethod(fwUrl + "/st/staging-file");
            method.setRequestHeader("Content-Type", this.format.toString());
            method.setRequestHeader("Cookie", "escidocCookie=" + this.user.getHandle());
            InputStream is = new FileInputStream(this.formatProcessor.getSourceFile());
            method.setRequestEntity(new InputStreamRequestEntity(is));
            client.executeMethod(method);
            is.close();
            String response = method.getResponseBodyAsString();
            URL originalDataUrl =  xmlTransforming.transformUploadResponseToFileURL(response);
            
           	replace("$04", escape(this.name), sb);
        	replace("$05", escape(this.fileName), sb);
        	replace("$06", escape(originalDataUrl.toExternalForm()), sb);
        	replace("$07", escape(log.getStoredId() + ""), sb);
        	replace("$08", escape(this.format.toString()), sb);
        	replace("$09", escape(String.valueOf(this.formatProcessor.getLength())), sb);
        	
        	//Upload and create task item xml
        	File tempLogXml = File.createTempFile("multipleImportLogXml", "xml");
        	Writer fw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tempLogXml), "UTF-8"));		
        	log.toXML(fw);
        	fw.flush();
        	fw.close();
        	
            PutMethod method2 = new PutMethod(fwUrl + "/st/staging-file");
            method2.setRequestHeader("Content-Type", "text/xml");
            method2.setRequestHeader("Cookie", "escidocCookie=" + this.user.getHandle());
        	is = new FileInputStream(tempLogXml);
            method2.setRequestEntity(new InputStreamRequestEntity(is));
            client.executeMethod(method2);
            is.close();
           
            response = method2.getResponseBodyAsString();
            URL logXmlUrl =  xmlTransforming.transformUploadResponseToFileURL(response);
        	
            replace("$10", escape(this.name), sb);
            replace("$11", "importlog.xml", sb);
            replace("$12", escape(logXmlUrl.toExternalForm()), sb);
            replace("$13", escape(log.getStoredId() + ""), sb);
            replace("$14", escape(String.valueOf(tempLogXml.length())), sb);
            
            tempLogXml.delete();
        	
        	/*
            String taskItemXml = ResourceUtil.getResourceAsString("multipleImport/ImportTaskTemplate.xml");
            taskItemXml = taskItemXml.replace("$1", escape(this.escidocContext.getObjectId()));
            taskItemXml = taskItemXml.replace("$2",
                    escape(PropertyReader.getProperty("escidoc.import.task.content-model")));
            taskItemXml = taskItemXml.replace("$4", escape(this.name));
            taskItemXml = taskItemXml.replace("$5", escape(this.fileName));
            taskItemXml = taskItemXml.replace("$6", escape(this.formatProcessor.getDataAsBase64()));
            taskItemXml = taskItemXml.replace("$7", escape(log.getStoredId() + ""));
            taskItemXml = taskItemXml.replace("$8", escape(this.format.toString()));
            taskItemXml = taskItemXml.replace("$9", escape(this.formatProcessor.getLength() + ""));
			*/
            log.finishItem();
            
            log.close();
            
            return sb.toString();
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
    
    public static void replace( String target, String replacement, StringBuilder builder ) { 
    	int indexOfTarget = -1;
    	while( ( indexOfTarget = builder.indexOf( target ) ) >= 0 ) { 
    		builder.replace( indexOfTarget, indexOfTarget + target.length() , replacement );
    	}
    }
    


    private String escape(String string)
    {
        if (string == null)
        {
            return null;
        }
        else
        {
            return string.replace("&", "&amp;").replace("\"", "&quot;").replace("<", "&lt;").replace(">", "&gt;");
        }
    }

    /**
     * @param writer
     * @param singleItem
     * @return
     */
    private void prepareItem(String singleItem)
    {
        log.addDetail(ErrorLevel.FINE, "import_process_source_data_found");
        log.addDetail(ErrorLevel.FINE, singleItem);
        log.addDetail(ErrorLevel.FINE, "import_process_start_transformation");
        String esidocXml = null;
        try
        {
            if (configuration != null && transformation instanceof Configurable)
            {
            	esidocXml = new String(((Configurable)transformation).transform(singleItem.getBytes(this.format.getEncoding()),
                        this.format, ESCIDOC_FORMAT, "escidoc", configuration), ESCIDOC_FORMAT.getEncoding());
            }
            else
            {
            	esidocXml = new String(transformation.transform(singleItem.getBytes(this.format.getEncoding()),
                        this.format, ESCIDOC_FORMAT, "escidoc"), ESCIDOC_FORMAT.getEncoding());
            }
            log.addDetail(ErrorLevel.FINE, esidocXml);
            log.addDetail(ErrorLevel.FINE, "import_process_transformation_done");
            PubItemVO pubItemVO = xmlTransforming.transformToPubItem(esidocXml);
            pubItemVO.setContext(escidocContext);
            pubItemVO.setContentModel(publicationContentModel);
            pubItemVO.getVersion().setObjectId(null);
            pubItemVO.getLocalTags().add("multiple_import");
            pubItemVO.getLocalTags().add(log.getMessage() + " " + log.getStartDateFormatted());           
            
            // Default validation
            log.addDetail(ErrorLevel.FINE, "import_process_default_validation");
            ValidationReportVO validationReportVO = this.itemValidating.validateItemObject(pubItemVO);
            if (validationReportVO.isValid())
            {
                if (!validationReportVO.hasItems())
                {
                    log.addDetail(ErrorLevel.FINE, "import_process_default_validation_successful");
                }
                else
                {
                    log.addDetail(ErrorLevel.WARNING, "import_process_default_validation_successful_with_warnings");
                    for (ValidationReportItemVO item : validationReportVO.getItems())
                    {
                        log.addDetail(ErrorLevel.WARNING, item.getContent());
                    }
                }
                // Release validation
                log.addDetail(ErrorLevel.FINE, "import_process_release_validation");
                validationReportVO = this.itemValidating.validateItemObject(pubItemVO, "submit_item");
                if (validationReportVO.isValid())
                {
                    if (!validationReportVO.hasItems())
                    {
                        log.addDetail(ErrorLevel.FINE, "import_process_release_validation_successful");
                    }
                    else
                    {
                        log.addDetail(ErrorLevel.WARNING, "import_process_release_validation_successful_with_warnings");
                        for (ValidationReportItemVO item : validationReportVO.getItems())
                        {
                            log.addDetail(ErrorLevel.WARNING, item.getContent());
                        }
                    }
                }
                else
                {
                    log.addDetail(ErrorLevel.WARNING, "import_process_release_validation_failed");
                    for (ValidationReportItemVO item : validationReportVO.getItems())
                    {
                        if (item.isRestrictive())
                        {
                            log.addDetail(ErrorLevel.WARNING, item.getContent());
                        }
                        else
                        {
                            log.addDetail(ErrorLevel.WARNING, item.getContent());
                        }
                    }
                }
                log.addDetail(ErrorLevel.FINE, "import_process_generate_item");
                log.setItemVO(pubItemVO);
                if (this.duplicateStrategy != DuplicateStrategy.NO_CHECK)
                {
                    log.addDetail(ErrorLevel.FINE, "import_process_check_duplicates_by_identifier");
                    boolean duplicatesDetected = checkDuplicatesByIdentifier(pubItemVO);
                    if (duplicatesDetected && this.duplicateStrategy == DuplicateStrategy.ROLLBACK)
                    {
                        this.rollback = true;
                        fail();
                    }
                    else if (duplicatesDetected)
                    {
                        log.addDetail(ErrorLevel.WARNING, "import_process_no_import");
                        log.finishItem();
                    }
                    else
                    {
                        log.suspendItem();
                    }
                }
                else
                {
                    log.suspendItem();
                }
            }
            else
            {
                log.addDetail(ErrorLevel.PROBLEM, "import_process_default_validation_failed");
                for (ValidationReportItemVO item : validationReportVO.getItems())
                {
                    if (item.isRestrictive())
                    {
                        log.addDetail(ErrorLevel.PROBLEM, item.getContent());
                    }
                    else
                    {
                        log.addDetail(ErrorLevel.WARNING, item.getContent());
                    }
                }
                log.addDetail(ErrorLevel.PROBLEM, "import_process_item_not_imported");
                log.finishItem();
            }
        }
        catch (Exception e)
        {
            log.addDetail(ErrorLevel.ERROR, e);
            log.addDetail(ErrorLevel.ERROR, "import_process_item_not_imported");
            if (this.rollback)
            {
                fail();
            }
            log.finishItem();
        }
    }

    private boolean checkDuplicatesByIdentifier(PubItemVO itemVO)
    {
        try
        {
            if (itemVO.getMetadata().getIdentifiers().size() > 0)
            {
                ArrayList<String> contentModels = new ArrayList<String>();
                contentModels.add(this.itemContentModel);
                ArrayList<MetadataSearchCriterion> criteria = new ArrayList<MetadataSearchCriterion>();
                boolean first = true;
                for (IdentifierVO identifierVO : itemVO.getMetadata().getIdentifiers())
                {
                    MetadataSearchCriterion criterion = new MetadataSearchCriterion(CriterionType.IDENTIFIER,
                            identifierVO.getId(), (first ? LogicalOperator.AND : LogicalOperator.OR));
                    first = false;
                    criteria.add(criterion);
                }
                MetadataSearchQuery query = new MetadataSearchQuery(contentModels, criteria);
                ItemContainerSearchResult searchResult = search.searchForItemContainer(query);
                if (searchResult.getTotalNumberOfResults().equals(NonNegativeInteger.ZERO))
                {
                    log.addDetail(ErrorLevel.FINE, "import_process_no_duplicate_detected");
                    return false;
                }
                else
                {
                    log.addDetail(ErrorLevel.FINE, "import_process_duplicates_detected");
                    for (ItemVO duplicate : searchResult.extractItemsOfSearchResult())
                    {
                        if (this.itemContentModel.equals(duplicate.getContentModel()))
                        {
                            PubItemVO duplicatePubItemVO = new PubItemVO(duplicate);
                            if (this.duplicateStrategy == DuplicateStrategy.ROLLBACK)
                            {
                                log.addDetail(ErrorLevel.PROBLEM, "import_process_duplicate_detected");
                                log.addDetail(ErrorLevel.PROBLEM, duplicatePubItemVO.getVersion().getObjectId() + " \""
                                        + duplicatePubItemVO.getMetadata().getTitle().getValue() + "\"",
                                        duplicatePubItemVO.getVersion().getObjectId());
                                return true;
                            }
                            else
                            {
                                log.addDetail(ErrorLevel.WARNING, "import_process_duplicate_detected");
                                log.addDetail(ErrorLevel.WARNING, duplicatePubItemVO.getVersion().getObjectId() + " \""
                                        + duplicatePubItemVO.getMetadata().getTitle().getValue() + "\"",
                                        duplicatePubItemVO.getVersion().getObjectId());
                            }
                        }
                        else
                        {
                            log.addDetail(ErrorLevel.WARNING, "import_process_detected_duplicate_no_publication");
                        }
                    }
                }
                return true;
            }
            else
            {
                log.addDetail(ErrorLevel.FINE, "import_process_no_identifier_for_duplicate_check");
                return false;
            }
        }
        catch (Exception e)
        {
            log.addDetail(ErrorLevel.WARNING, e);
            // An error while checking for duplicates should not cause the item not to be imported.
            //log.finishItem();
            return false;
        }
    }
}
