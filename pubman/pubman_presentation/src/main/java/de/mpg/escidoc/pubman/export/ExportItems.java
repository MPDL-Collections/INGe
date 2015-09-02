/*
* CDDL HEADER START
*
* The contents of this file are subject to the terms of the
* Common Development and Distribution License, Version 1.0 only
* (the "License"). You may not use this file except in compliance
* with the License.
*
* You can obtain a copy of the license at license/ESCIDOC.LICENSE
* or http://www.escidoc.org/license.
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

package de.mpg.escidoc.pubman.export;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.faces.component.html.HtmlMessages;
import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;

import de.mpg.escidoc.pubman.ErrorPage;
import de.mpg.escidoc.pubman.ItemControllerSessionBean;
import de.mpg.escidoc.pubman.RightsManagementSessionBean;
import de.mpg.escidoc.pubman.appbase.FacesBean;
import de.mpg.escidoc.pubman.breadcrumb.BreadcrumbItemHistorySessionBean;
import de.mpg.escidoc.pubman.search.SearchRetrieverRequestBean;
import de.mpg.escidoc.services.common.exceptions.TechnicalException;
import de.mpg.escidoc.services.common.valueobjects.ExportFormatVO;
import de.mpg.escidoc.services.common.valueobjects.FileFormatVO;


/**
  * Fragment class for item exporting.
  * This class provides all functionality for exporting items according the selected export format 
  * (layout or structured) and the selected file format (PDF, TXT, etc..).  
  * @author:  Galina Stancheva, created 02.08.2007
  * @version: $Revision$ $LastChangedDate$
  *  Revised by StG: 28.09.2007
*/
public class ExportItems extends FacesBean
{
    private static Logger logger = Logger.getLogger(ExportItems.class);
    
    public static final String BEAN_NAME = "ExportItems";
 
    // constant for the function export to check the rights and/or if the function has to be disabled (DiT)
    private final String FUNCTION_EXPORT = "export";

    // binded components in JSP
    private HtmlMessages valMessage = new HtmlMessages();
    //private HtmlSelectOneMenu cboLayoutCitStyles = new HtmlSelectOneMenu();

    
//    public SelectItemGroup CITATIONSTYLES_GROUP = new SelectItemGroup(getLabel("Export_CitationStyles_Group"), "", false, new SelectItem[]{EXPORTFORMAT_APA, EXPORTFORMAT_AJP});
//    public SelectItem[] EXPORTFORMAT_OPTIONS = new SelectItem[]{EXPORTFORMAT_ENDNOTE, EXPORTFORMAT_BIBTEX, EXPORTFORMAT_XML, CITATIONSTYLES_GROUP};
    
 
    // constants for error and status messages
    public static final String MESSAGE_NO_ITEM_FOREXPORT_SELECTED = "exportItems_NoItemSelected";
    // constants for error and status messages
    public static final String MESSAGE_NO_EXPORTDATA_DELIVERED = "exportItems_NoDataDelivered";
    public static final String MESSAGE_EXPORT_EMAIL_SENT = "exportItems_EmailSent";
    public static final String MESSAGE_EXPORT_EMAIL_NOTSENT = "exportItems_EmailNotSent";
    public static final String MESSAGE_EXPORT_EMAIL_RECIPIENTS_ARE_NOT_DEFINED = "exportItems_RecipientsAreNotDefined";
    public static final String MESSAGE_EXPORT_EMAIL_UNKNOWN_RECIPIENTS = "exportItems_UnknownRecipients";
    public static final String MESSAGE_EXPORT_EMAIL_TEXT = "exportItems_EmailText";
    public static final String MESSAGE_EXPORT_EMAIL_SUBJECT_TEXT = "exportItems_EmailSubjectText";
    
    /**
     * Default constructor.
     */
    public ExportItems()
    {
        this.init();
    }
    
    /**
     * Callback method that is called whenever a page containing this page fragment is navigated to, either directly via
     * a URL, or indirectly via page navigation. 
     */
    public void init()
    {
       logger.debug(" init ExportItems >>>");   
       super.init();
       //setExportFormats();

    }
   
    
    /**
     * Returns the RightsManagementSessionBean.
     * @author StG
     * @return a reference to the scoped data bean (RightsManagementSessionBean)
     */
    protected RightsManagementSessionBean getRightsManagementSessionBean()
    {
        return (RightsManagementSessionBean)getSessionBean(RightsManagementSessionBean.class);
    }

    /**
     * Returns true if the AdvancedSearch should be disabled by the escidoc properties file.
     * @author StG
     * @return
     */
    public boolean getVisibleExport()
    {
        return !this.getRightsManagementSessionBean().isDisabled(getRightsManagementSessionBean().PROPERTY_PREFIX_FOR_DISABLEING_FUNCTIONS + "." + this.FUNCTION_EXPORT);
    }    
    
    
    public SelectItem[] getEXPORTFORMAT_OPTIONS()
    {
        // constants for comboBoxes and HtmlSelectOneRadios
        SelectItem EXPORTFORMAT_MARCXML = new SelectItem("MARCXML", getLabel("Export_ExportFormat_MARCXML"));
        SelectItem EXPORTFORMAT_ENDNOTE = new SelectItem("ENDNOTE", getLabel("Export_ExportFormat_ENDNOTE"));
        SelectItem EXPORTFORMAT_BIBTEX = new SelectItem("BIBTEX", getLabel("Export_ExportFormat_BIBTEX"));
        SelectItem EXPORTFORMAT_ESCIDOC_XML = new SelectItem("ESCIDOC_XML_V13", getLabel("Export_ExportFormat_ESCIDOC_XML"));
        SelectItem EXPORTFORMAT_APA = new SelectItem("APA", getLabel("Export_ExportFormat_APA"));
        SelectItem EXPORTFORMAT_APA_CJK = new SelectItem("APA(CJK)", getLabel("Export_ExportFormat_APA_CJK"));
        SelectItem EXPORTFORMAT_AJP = new SelectItem("AJP", getLabel("Export_ExportFormat_AJP"));
        // JUS
        SelectItem EXPORTFORMAT_JUS = new SelectItem("JUS", getLabel("Export_ExportFormat_JUS"));
        // CitationStyleEditor
        SelectItem EXPORTFORMAT_CSL = new SelectItem("CSL", "CSL");
        // Test citation styles
//        SelectItem EXPORTFORMAT_DEFAULT = new SelectItem("Default", getLabel("Export_ExportFormat_DEFAULT"));
//        SelectItem EXPORTFORMAT_TEST = new SelectItem("Test", getLabel("Export_ExportFormat_TEST"));
        
//        SelectItem[] EXPORTFORMAT_OPTIONS = new SelectItem[]{EXPORTFORMAT_ENDNOTE, EXPORTFORMAT_BIBTEX, EXPORTFORMAT_ESCIDOC_XML, EXPORTFORMAT_APA, EXPORTFORMAT_AJP, EXPORTFORMAT_JUS, EXPORTFORMAT_DEFAULT, EXPORTFORMAT_TEST};
        SelectItem[] EXPORTFORMAT_OPTIONS = new SelectItem[]{EXPORTFORMAT_MARCXML, EXPORTFORMAT_ENDNOTE, EXPORTFORMAT_BIBTEX, EXPORTFORMAT_ESCIDOC_XML, EXPORTFORMAT_APA, EXPORTFORMAT_APA_CJK, EXPORTFORMAT_AJP, EXPORTFORMAT_JUS, EXPORTFORMAT_CSL};
        return EXPORTFORMAT_OPTIONS;
    }
    
    public SelectItem[] getEXPORTFORMAT_OPTIONS_EXTENDED()
    {
        SelectItem[] EXPORTFORMAT_OPTIONS = Arrays.copyOf(getEXPORTFORMAT_OPTIONS(), getEXPORTFORMAT_OPTIONS().length+1);
        EXPORTFORMAT_OPTIONS[EXPORTFORMAT_OPTIONS.length-1]= new SelectItem("EDOC_IMPORT", "EDOC_IMPORT");
        return EXPORTFORMAT_OPTIONS;
    }
 
    public SelectItem[] getFILEFORMAT_OPTIONS()
    {
        SelectItem FILEFORMAT_PDF = new SelectItem("pdf", getLabel("Export_FileFormat_PDF"));
        SelectItem FILEFORMAT_DOCX = new SelectItem("docx", getLabel("Export_FileFormat_DOCX"));
        SelectItem FILEFORMAT_HTML_PLAIN = new SelectItem("html_plain", getLabel("Export_FileFormat_HTML_PLAIN"));
        SelectItem FILEFORMAT_HTML_LINKED = new SelectItem("html_linked", getLabel("Export_FileFormat_HTML_LINKED"));
        SelectItem FILEFORMAT_ESCIDOC_SNIPPET = new SelectItem("escidoc_snippet", getLabel("Export_FileFormat_ESCIDOC_SNIPPET"));
        SelectItem FILEFORMAT_RTF = new SelectItem("rtf", getLabel("Export_FileFormat_RTF"));
        SelectItem FILEFORMAT_ODT = new SelectItem("odt", getLabel("Export_FileFormat_ODT"));
        SelectItem FILEFORMAT_HTML_STYLED = new SelectItem("html_styled", getLabel("Export_FileFormat_HTML_STYLED"));
        SelectItem[] FILEFORMAT_OPTIONS = new SelectItem[]{FILEFORMAT_PDF, FILEFORMAT_DOCX, FILEFORMAT_HTML_PLAIN, FILEFORMAT_HTML_LINKED, FILEFORMAT_ESCIDOC_SNIPPET, FILEFORMAT_RTF, FILEFORMAT_ODT, FILEFORMAT_HTML_STYLED};
        return FILEFORMAT_OPTIONS;
    }
    
    

    /*
     * Gets the session bean. 
     */
    
    public ExportItemsSessionBean getSessionBean()
    {
        return (ExportItemsSessionBean)getSessionBean(ExportItemsSessionBean.class);
    }

    /**
     * Returns a reference to the scoped data bean (the ItemControllerSessionBean). 
     * @return a reference to the scoped data bean
     */
    protected ItemControllerSessionBean getItemControllerSessionBean()
    {
        return (ItemControllerSessionBean)getSessionBean(ItemControllerSessionBean.class);
    }

        
    
    /*
     * Updates the GUI relatively the selected export format. 
     */
    public void updateExportFormats(){
        
        // get the selected export format by the FacesBean
        

        ExportItemsSessionBean sb = this.getSessionBean(); 
//        String selExportFormat = sb.getExportFormatType(); 
        String selExportFormat = sb.getExportFormatName(); 
        
        if (logger.isDebugEnabled())
        {
            logger.debug(">>>  New export format: " + selExportFormat);                     
            logger.debug("curExportFormat:" + this.getSessionBean().getCurExportFormatVO());
        }

        sb.setExportFormatName(selExportFormat);
        
        if ( "APA".equalsIgnoreCase(selExportFormat) 
                || "AJP".equalsIgnoreCase(selExportFormat) 
                || "JUS".equalsIgnoreCase(selExportFormat) 
                || "DEFAULT".equalsIgnoreCase(selExportFormat) 
                || "TEST".equalsIgnoreCase(selExportFormat)
                || "APA(CJK)".equalsIgnoreCase(selExportFormat) 
                || "CSL".equalsIgnoreCase(selExportFormat)
        )
        {
            //set default fileFormat for APA or AJP to pdf 
            String fileFormat = sb.getFileFormat();  
            if ( fileFormat != null || fileFormat.trim().equals("") || 
                    fileFormat.trim().equals(FileFormatVO.TEXT_NAME)
                )
                sb.setFileFormat(FileFormatVO.DEFAULT_NAME); 
        }
        else
        {
        	String fileFormat=null;
        	
            if ("ESCIDOC_XML".equals(selExportFormat) || "ESCIDOC_XML_V13".equals(selExportFormat))
			{
            	fileFormat = FileFormatVO.ESCIDOC_XML_NAME;
            }
    		else if ("MARCXML".equals(selExportFormat))
			{
    			fileFormat = FileFormatVO.ESCIDOC_XML_NAME;
    		}
    		else
			{
				//txt for all other
	            fileFormat = FileFormatVO.TEXT_NAME;
	      	}
    		sb.setFileFormat(fileFormat);
        }
        
    }

     ///////////////////////////////////////////////////////////////////////////////////////
    ///////// next methods are used by EMailing

    
    /*
     * Disables the export components when the email page gets open. 
     */
    public void disableExportPanComps(boolean b){
    
    }
        
        
    /**
     * redirects the user to the list he came from
     * 
     * @return String nav rule for loading the page the user came from
     */
    public String backToList()
    {
        ExportItemsSessionBean sb = this.getSessionBean();
        cleanUpEmailFields();   
        return sb.getNavigationStringToGoBack() != null ? 
            sb.getNavigationStringToGoBack() : 
            SearchRetrieverRequestBean.LOAD_SEARCHRESULTLIST;
    }
    
    /**
     * Clean up some fields on the Email interface
     */
    private void cleanUpEmailFields() 
    {
        ExportItemsSessionBean sb = this.getSessionBean();
        //To
        sb.setEmailRecipients(null);
        //CC
        sb.setEmailCCRecipients(null);
        //ReplyTo
        sb.setExportEmailReplyToAddr(null);
    }

    public HtmlMessages getValMessage()
    {
        return valMessage;
    }

    public void setValMessage(HtmlMessages valMessage)
    {
        this.valMessage = valMessage;
    }
    /**
     * Adds and removes messages concerning item lists.
     */
    
    /**
     * redirects the user to the list he came from
     * 
     * @return String nav rule for loading the page the user came from
     */

      public String sendEMail() 
    {        
            logger.debug(">>>  sendEMail");
            String status = "not sent";
            String smtpHost = this.getSessionBean().getEmailServernameProp();
            String withAuth = this.getSessionBean().getEmailWithAuthProp();
            String usr = this.getSessionBean().getEmailAuthUserProp();
            String pwd = this.getSessionBean().getEmailAuthPwdProp();
            String senderAddress =this.getSessionBean().getEmailSenderProp();//someone@web.de
            String subject = this.getSessionBean().getExportEmailSubject();
            String text = this.getSessionBean().getExportEmailTxt();
            String[] replyToAddresses = new String[] {this.getSessionBean().getExportEmailReplyToAddr()};
            String[] attachments = new String[]{this.getSessionBean().getAttExportFile().getPath()};
            String recipientsAddressesStr = this.getSessionBean().getEmailRecipients();
            String recipientsCCAddressesStr = this.getSessionBean().getEmailCCRecipients();
            
            String[] recipientsAddresses = null;
            boolean OK = false;
            if ( recipientsAddressesStr != null && ! recipientsAddressesStr.trim().equals("") )
            {
                recipientsAddresses = recipientsAddressesStr.split(",");
                FOR: for ( String ra : recipientsAddresses )
                {
                    if ( !ra.trim().equals("") )
                    {
                        OK = true;
                        break FOR;
                    }
                }

            }
            
            if ( !OK )
            {
                error(getMessage(ExportItems.MESSAGE_EXPORT_EMAIL_RECIPIENTS_ARE_NOT_DEFINED));
                return null;
            }
           
           String[] recipientsCCAddresses = recipientsCCAddressesStr.split(",");
           
            try { 
                 status =  this.getItemControllerSessionBean().sendEmail(smtpHost, withAuth, usr, pwd,
                         senderAddress, 
                         recipientsAddresses, 
                         recipientsCCAddresses,
                         null,
                         replyToAddresses, 
                         subject, text, attachments);
                 cleanUpEmailFields();
            }
            catch (TechnicalException e)
            {
                logger.error("Could not send the export formats." + "\n" + e.toString());
                //normal 
                Throwable ecc = e.getCause().getCause();
                if ( ecc != null && ecc instanceof com.sun.mail.smtp.SMTPAddressFailedException )
                {
                    error(getMessage(ExportItems.MESSAGE_EXPORT_EMAIL_UNKNOWN_RECIPIENTS));
                    return null;                    
                }
                
                ((ErrorPage)getRequestBean(ErrorPage.class)).setException(e);
                return ErrorPage.LOAD_ERRORPAGE;
            }
            
            if (status.equals("sent")){
                logger.debug(ExportItems.MESSAGE_EXPORT_EMAIL_SENT);           
                info(getMessage(ExportItems.MESSAGE_EXPORT_EMAIL_SENT));
                
                //redirect to last breadcrumb
                BreadcrumbItemHistorySessionBean bhsb = (BreadcrumbItemHistorySessionBean)getSessionBean(BreadcrumbItemHistorySessionBean.class);
                try
                {
                    getFacesContext().getExternalContext().redirect(bhsb.getPreviousItem().getPage());
                }
                catch (IOException e)
                {
                   error("Could not redirect!");
                }
                return "";
               
            } 
            return status;
      }
       
}
