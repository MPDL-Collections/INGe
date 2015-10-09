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

package de.mpg.escidoc.services.search.presentation;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import javax.ejb.EJB;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import de.mpg.escidoc.services.citationmanager.CitationStyleHandler;
import de.mpg.escidoc.services.citationmanager.xslt.CitationStyleExecutor;
import de.mpg.escidoc.services.common.exceptions.TechnicalException;
import de.mpg.escidoc.services.common.valueobjects.FileFormatVO;
import de.mpg.escidoc.services.search.Search;
import de.mpg.escidoc.services.search.query.ExportSearchQuery;
import de.mpg.escidoc.services.search.query.ExportSearchResult;
import de.mpg.escidoc.services.search.query.SearchQuery.SortingOrder;
import de.mpg.escidoc.services.structuredexportmanager.StructuredExport;

/**
 * This servlet takes an cql query, calls the search service and returns the
 * result.
 * 
 * @author vmakarenko
 * 
 */
public class RestServlet extends HttpServlet
{

    /** Serial identifier. */
    private static final long serialVersionUID = 1L;
    /** Logging instance. */
    private static final Logger LOGGER = Logger.getLogger(RestServlet.class);

    /** EJB instance of search service. */
    @EJB
    private Search itemContainerSearch;

    /** Counter for the concurrent searches*/
    private static int searchCounter = 0;
    
    /** Max number of the simultaneous concurrent searches*/
    private static final int MAX_SEARCHES_NUMBER = 30;
    
    
    private static CitationStyleHandler cse;
    private static StructuredExport se;
    
    
    public RestServlet()
    {
        cse = new CitationStyleExecutor();
        se = new StructuredExport();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected final void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException,
            IOException
    {
        doPost(req, resp);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException,
            IOException
    {
        String cqlQuery = null;
        String language = null;
        String exportFormat = null;
        String outputFormat = null;
        String cslConeId = null;
        boolean isCitationStyle = false;
        try
        {
            String qs = req.getQueryString();
            LOGGER.debug("QueryString: " + qs);
            // Init exporting service
            //InitialContext ctx = new InitialContext();
            //itemContainerSearch = (Search) ctx.lookup(Search.SERVICE_NAME);
            cqlQuery = fixURLEncoding(req.getParameter("cqlQuery"));
            if (!checkVal(cqlQuery))
            {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "cqlQuery is not defined in the QueryString: " + qs);
                return;
            }

            // TODO: Case sensitive exportFormats ?  
            exportFormat = req.getParameter("exportFormat");
            exportFormat = 	"Default".equalsIgnoreCase(exportFormat) ? "Default" : 
            				"Test".equalsIgnoreCase(exportFormat) ? "Test" :   	
            				exportFormat.toUpperCase();
            
            exportFormat = !checkVal(exportFormat) ? "" : exportFormat.trim();

            if (exportFormat.equals(""))
            {
                // TODO: move default values to services
                exportFormat = "ENDNOTE";
                // if exportFormat is ENDNOTE set outputFormat forced to the
                // txt
            } else if ( ! ( 
            			cse.isCitationStyle(exportFormat) 
            		|| 	se.isStructuredFormat(exportFormat) 
            ))
            {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Wrong export format: " + exportFormat);
                return;
            }

            if (se.isStructuredFormat(exportFormat))
            {
            	
                outputFormat = 
                	exportFormat.equalsIgnoreCase("XML") ? "xml" : 
                	exportFormat.equalsIgnoreCase("ESCIDOC_XML") ? "escidoc_xml" : 
                	exportFormat.equalsIgnoreCase("ESCIDOC_XML_V13") ? "escidoc_xml_v13" : 
                	exportFormat.toUpperCase().startsWith("EDOC_") ? "xml" : 
                	exportFormat.equalsIgnoreCase("MARCXML") ? "xml" : 	
                	FileFormatVO.TEXT_NAME;
            } 
            else
            // citation style
            {
                outputFormat = req.getParameter("outputFormat").toLowerCase();
                outputFormat = !checkVal(outputFormat) ? "" : outputFormat.trim().toLowerCase();
                // get default outputFormat if it is not defined
                if (outputFormat.equals(""))
                {
                    outputFormat = FileFormatVO.DEFAULT_NAME;
                }
                // check output format consistency
                else if (cse.getMimeType(exportFormat, outputFormat) == null)
                {
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "File output format: " + outputFormat
                            + " is not supported for the export format: " + exportFormat);
                    return;
                }
                cslConeId = req.getParameter("cslConeId");
            }

            //check the max number of the concurrent searches
            if (cse.isCitationStyle(exportFormat))
            {
            	isCitationStyle = true;
            	LOGGER.debug("Number of the concurrent searches 1:" + searchCounter);
            	if( searchCounter > MAX_SEARCHES_NUMBER )
            	{
                    resp.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, "Too many Search&Export requests");
                    return;
            	}
            	searchCounter++;
            }

            // escidoc_all as static value
            String index = "escidoc_all";

            // create the query
            ExportSearchQuery query = new ExportSearchQuery(cqlQuery, index, exportFormat, outputFormat, cslConeId);

            // check if sortKeys is set
            if (checkVal(req.getParameter("sortKeys")))
            {
                query.setSortKeys(req.getParameter("sortKeys"));
            }

            // check if startRecord is set
            if (checkVal(req.getParameter("startRecord")))
            {
                query.setStartRecord(req.getParameter("startRecord"));
            }

            // check if maximum records are set
            if (checkVal(req.getParameter("maximumRecords")))
            {
                query.setMaximumRecords(req.getParameter("maximumRecords"));
            }

            // check if sortOrder is set
            if (checkVal(req.getParameter("sortOrder")))
            {
                String sortOrder = req.getParameter("sortOrder");
                if (sortOrder.contains("descending"))
                {
                    query.setSortOrder(SortingOrder.DESCENDING);
                } 
                else
                {
                    query.setSortOrder(SortingOrder.ASCENDING);
                }

            }
            
            
            // query the search service
            ExportSearchResult queryResult = itemContainerSearch.searchAndExportItems(query);

            byte[] result = queryResult.getExportedResults();

            String fileName = exportFormat + "_output" + getFileExtension(outputFormat);
            LOGGER.debug("fileName: " + fileName);

            setContentType(resp, outputFormat);
            LOGGER.debug("contentType: " + resp.getContentType());
            
            setCharacterEncoding(resp, outputFormat);
            LOGGER.debug("charset: " + resp.getCharacterEncoding());
            
            ServletOutputStream os = resp.getOutputStream();

            resp.addHeader("x-total-number-of-results", queryResult.getTotalNumberOfResults().toString());

            resp.addHeader("Content-Disposition", "attachment; filename=" + fileName);

            resp.setContentLength(result.length);

        	ByteArrayInputStream bais = new ByteArrayInputStream( result );
        	BufferedInputStream bis = new BufferedInputStream( bais );
        	byte[] ba = new byte[2048];
        	int len;
        	while ( (len = bis.read( ba ))!=-1 )
        	{
        		os.write(ba, 0, len);
        	}            
            os.close();
            
            decreaseCounter(isCitationStyle);
        } 
        catch (NamingException ne)
        {
            decreaseCounter(isCitationStyle);
            try
            {
                handleException(ne, resp);
            } 
            catch (Exception e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } catch (TechnicalException te)
        {
            decreaseCounter(isCitationStyle);
            try
            {
                handleException(te, resp);
            } 
            catch (Exception e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } 
        catch (Exception e)
        {
        	decreaseCounter(isCitationStyle);        	
            throw new ServletException(e);
        }
    }
 
	/**
     * Mapping of outputFormat to mime-type. TODO: Get the mapping directly from
     * ItemExportingBean
     * 
     * @param resp
     * @param outputFormat
     */
    private void setContentType(final HttpServletResponse resp, final String outputFormat)
    {
        resp.setContentType(FileFormatVO.getMimeTypeByName(outputFormat));
    }
    
    /**
     * set output format charset
     * 
     * @param resp
     * @param outputFormat
     */
    private void setCharacterEncoding(final HttpServletResponse resp, final String outputFormat)
    {
    	String cs = FileFormatVO.getCharsetByName(outputFormat);
    	if ( cs != null )
    	{
    		resp.setCharacterEncoding(cs);
    	}
    }

    /**
     * Mapping of the outputType file to the correct file extension. TODO: Get
     * the mapping directly from ItemExportingBean
     * 
     * @param outputFormat
     * @return mime-type according to the outputFormat
     */
    private String getFileExtension(final String outputFormat)
    {
        return "." + FileFormatVO.getExtensionByName(outputFormat);
    }
    

    /**
     * Take care on an incoming exception.
     * 
     * @param e
     *            exception
     * @param resp
     *            response stream
     * @throws Exception
     *             if the handling went wrong on any reason
     */
    private void handleException(final Exception e, final HttpServletResponse resp) throws Exception
    {
        PrintWriter pw = resp.getWriter();
        pw.print("Error: ");
        e.printStackTrace(pw);
        pw.close();
    }

    private boolean checkVal(String str)
    {
        return !(str == null || str.trim().equals(""));
    }
    
    //Decrease counter of the maximum allowed searches  
    private void decreaseCounter(boolean flag)
    {
    	if (flag)
    	{
			searchCounter--;
			LOGGER.debug("Number of the concurrent searches 2:" + searchCounter);
    	}
    }

    /**
     * Transforms broken ISO-8859-1 strings into (hopefully) correct UTF-8 strings.
     * 
     * @param brokenValue
     * @return
     */
    private String fixURLEncoding(String brokenValue)
    {
        if (brokenValue != null)
        {
            try
            {
                String utf8 = new String(brokenValue.getBytes("ISO-8859-1"), "UTF-8");
                if (utf8.equals(brokenValue) || utf8.contains("�") || utf8.length() == brokenValue.length())
                {
                    return brokenValue;
                }
                else
                {
                    return utf8;
                }
            }
            catch (UnsupportedEncodingException e)
            {
                throw new RuntimeException(e);
            }
        }
        else
        {
            return null;
        }
    }

}
