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
* Copyright 2006-2009 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/ 

package de.mpg.escidoc.services.dataacquisition;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Vector;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import noNamespace.SourceType;
import noNamespace.SourcesDocument;
import noNamespace.SourcesType;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlOptions;
import org.apache.xmlbeans.XmlString;
import org.purl.dc.elements.x11.SimpleLiteral;

import de.mpg.escidoc.services.dataacquisition.exceptions.FormatNotAvailableException;
import de.mpg.escidoc.services.dataacquisition.valueobjects.DataSourceVO;
import de.mpg.escidoc.services.dataacquisition.valueobjects.FullTextVO;
import de.mpg.escidoc.services.dataacquisition.valueobjects.MetadataVO;
import de.mpg.escidoc.services.transformation.Transformation;
import de.mpg.escidoc.services.transformation.valueObjects.Format;



/**
 * 
 * Helper methods for the DataAcquisition Service.
 *
 * @author kleinfe1 (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class Util
{

    Transformation transformer;
    private final Logger logger = Logger.getLogger(Util.class);
    
    /**
     * Public constructor.
     */
    public Util()
    {
        try
        {
            InitialContext initialContext = new InitialContext();
            this.transformer = (Transformation) initialContext.lookup(Transformation.SERVICE_NAME);
        }
        catch (NamingException e)
        {
           this.logger.warn(e);
        }
        
    }
    
    /**
     * 
     * @param formatName
     * @return
     */
    public String getDefaultEncoding (String formatName)
    {
        return "UTF-8";
    }
    
    /**
     * 
     * @param formatName
     * @return
     */
    public String getDefaultMimeType (String formatName)
    {
        if (formatName.toLowerCase().equals("escidoc")) { return "application/xml"; }          
        if (formatName.toLowerCase().equals("arxiv")) { return "application/xml"; } 
        if (formatName.toLowerCase().equals("pmc")) { return "application/xml"; } 
        if (formatName.toLowerCase().equals("apa")) { return "text/html"; }      
        if (formatName.toLowerCase().equals("ajp")) { return "text/html"; }    
        if (formatName.toLowerCase().equals("apa(snippet)")) { return "application/xml"; }     
        if (formatName.toLowerCase().equals("ajp(snippet)")) { return "application/xml"; }      
        if (formatName.toLowerCase().equals("endnote")) { return "text/plain"; } 
        if (formatName.toLowerCase().equals("bibtex")) { return "text/plain"; } 
        if (formatName.toLowerCase().equals("coins")) { return "text/plain"; } 
        if (formatName.toLowerCase().equals("mets")) { return "application/xml"; } 
        if (formatName.toLowerCase().equals("pdf")) { return "application/pdf"; } 
        if (formatName.toLowerCase().equals("ps")) { return "application/gzip"; } 
        if (formatName.toLowerCase().equals("esidoc-fulltext")) { return "unknown"; } 
        
        return "";
    }
    
    /**
     * This operation return the Metadata Object of the format to fetch from the source.
     * 
     * @param source
     * @param format
     * @return Metadata Object of the format to fetch
     * @throws FormatNotAvailableException 
     */
    public MetadataVO getMdObjectToFetch(DataSourceVO source, String trgFormatName, String trgFormatType, String trgFormatEndcoding)
    {
        MetadataVO sourceMd = null;
        DataSourceHandlerBean sourceHandler = new DataSourceHandlerBean();
        
        // First: check if format can be fetched directly
        for (int i = 0; i < source.getMdFormats().size(); i++)
        {
            sourceMd = source.getMdFormats().get(i);
            boolean fetchMd = true;
            
            if (!sourceMd.getName().trim().toLowerCase().equals(trgFormatName.trim().toLowerCase()))
            { fetchMd = false; }
            if (!sourceMd.getMdFormat().trim().toLowerCase().equals(trgFormatType.trim().toLowerCase()))
            { fetchMd = false; }
            if((!sourceMd.getEncoding().equals("*")) && (!trgFormatEndcoding.equals("*")))
            {
                if (!sourceMd.getEncoding().toLowerCase().trim().equals(trgFormatEndcoding.toLowerCase().trim())) 
                {
                    fetchMd = false;
                }
            }
            
            if (fetchMd)
            {return sourceHandler.getMdObjectfromSource(source, sourceMd.getName());}
        }
        
        // Second: check which format can be transformed into the given format
        Format[] possibleFormats = this.transformer.getSourceFormats(new Format (trgFormatName, trgFormatType, trgFormatEndcoding));
        
        for (int i = 0; i < source.getMdFormats().size(); i++)
        {
            sourceMd = source.getMdFormats().get(i);
            for (int x = 0; x < possibleFormats.length; x++)
            {
                Format possibleFormat = possibleFormats[x];
                boolean fetchMd = true;
                    
                if (!sourceMd.getName().trim().toLowerCase().equals(possibleFormat.getName().toLowerCase()))
                { fetchMd = false; }
                if (!sourceMd.getMdFormat().trim().toLowerCase().equals(possibleFormat.getType().toLowerCase()))
                { fetchMd = false; }
                if((!sourceMd.getEncoding().equals("*")) && (!possibleFormat.getEncoding().equals("*")))
                {
                    if (!sourceMd.getEncoding().toLowerCase().trim().equals(possibleFormat.getEncoding().toLowerCase().trim())) 
                    {
                        fetchMd = false;
                    }
                }
                    
                if (fetchMd)
                {return sourceHandler.getMdObjectfromSource(source, sourceMd.getName());}
            }
        }
        return null;
    }
    
    /**
     * Checks if a target format can be transformed from escidoc format.
     * Will be more dynamic in future! This part of arxiv hack for natasa ;)
     * @return
     */
    public boolean checkEscidocTransform(String trgFormatName, String trgFormatType, String trgFormatEncoding)
    {
        Format target = new Format (trgFormatName, trgFormatType, trgFormatEncoding);
        Format escidoc = new Format ("escidoc", "application/xml", "UTF-8");
        Format[] formats;

        formats = this.transformer.getTargetFormats(escidoc);
        
        for (int i = 0; i< formats.length; i++)
        {
            if (this.isFormatEqual(target, formats[i]))
            {
                return true;
            }
        }
        
        return false;
        
    }
    
    /**
     * This operation return the Fulltext Object of the format to fetch from the source.
     * 
     * @param source
     * @param format
     * @return Fulltext Object of the format to fetch
     */
    public FullTextVO getFtObjectToFetch(DataSourceVO source, String formatName, String formatType, String formatEncoding)
    {
        FullTextVO ft = null;
        
        for (int i = 0; i < source.getFtFormats().size(); i++)
        {
            ft = source.getFtFormats().get(i);
            boolean fetchMd = true;
            
            if (!ft.getName().trim().toLowerCase().equals(formatName.trim().toLowerCase()))
            { fetchMd = false; }
            if (!ft.getFtFormat().trim().toLowerCase().equals(formatType.trim().toLowerCase()))
            { fetchMd = false; }
            if((!ft.getEncoding().equals("*")) && (!formatEncoding.equals("*")))
            {
                if (!ft.getEncoding().toLowerCase().trim().equals(formatEncoding.toLowerCase().trim())) 
                {
                    fetchMd = false;
                }
            }
            
            if (fetchMd)
            {return ft;}
            else 
            {ft = null;}
        }
        return ft;
    }
    
    /**
     * For a more flexible interface for handling user input. This is the only source specific method, which should be
     * updated when a new source is specified for import
     * 
     * @param sourceName
     * @param identifier
     * @return a trimed identifier
     */
    public String trimIdentifier(String sourceName, String identifier)
    {
        // Trim the identifier arXiv
        if (sourceName.trim().toLowerCase().equals("arxiv"))
        {
            if (identifier.toLowerCase().startsWith("oai:arxiv.org:", 0))
            {
                identifier = identifier.substring(14);
                return identifier.trim();
            }
            if (identifier.toLowerCase().startsWith("arxiv:", 0))
            {
                identifier = identifier.substring(6);
                return identifier.trim();
            }
        }
        // Trim identifier for PubMedCentral
        if (sourceName.trim().toLowerCase().equals("pubmedcentral"))
        {
            if (identifier.toLowerCase().startsWith("pmcid:pmc", 0))
            {
                identifier = identifier.substring(9);
                return identifier.trim();
            }
            if (identifier.toLowerCase().startsWith("pmcid: pmc", 0))
            {
                identifier = identifier.substring(10);
                return identifier.trim();
            }
            if (identifier.toLowerCase().startsWith("pmcid:", 0))
            {
                identifier = identifier.substring(6);
                return identifier.trim();
            }
            if (identifier.toLowerCase().startsWith("pmc", 0))
            {
                identifier = identifier.substring(3);
                return identifier.trim();
            }
        }
        // Trim identifier for escidoc_qa
        if (sourceName.trim().toLowerCase().equals("escidoc_qa"))
        {
            if (identifier.toLowerCase().startsWith("escidocqa", 0))
            {
                identifier = identifier.replace("qa", "");
                return identifier.trim();
            }
        }
        return identifier.trim();
    }
    
    /**
     * 
     * @param fetchFormats
     * @return
     */
    public Vector<MetadataVO> getTransformFormats(Vector<MetadataVO> fetchFormats)
    {
        Vector<MetadataVO> allFormats = new Vector<MetadataVO>();

        for (int i =0; i< fetchFormats.size(); i++)
        {
            MetadataVO md = fetchFormats.get(i);
            Format format = new Format(md.getName(), md.getMdFormat(), md.getEncoding());
            Format [] formats = this.transformer.getTargetFormats(format);
            
            for (int x=0; x<formats.length; x++)
            {
                Format formatTrans = formats[x];
                MetadataVO mdTrans = new MetadataVO ();
                mdTrans.setName(formatTrans.getName()+ "_"+ formatTrans.getType());
                // To be tested: mdTrans.setMdLabel(formatTrans.getName()+ "_"+ formatTrans.getType());                        
                mdTrans.setMdFormat(formatTrans.getType());
                mdTrans.setEncoding(formatTrans.getEncoding());
                
                allFormats.add(mdTrans);
            }
        }
        return allFormats;
    }
    
    
    /**
     * Returns all formats in which the escidoc format can be transformed.
     * In this context escidoc acts like a transition format for all other formats.
     * @return
     */
    public Vector<MetadataVO> getTransformationsWithEscidocTransition ()
    {
        Format[] formatsArr;
        Vector<MetadataVO> formatsV = new Vector<MetadataVO>();
        
        Format escidoc = new Format ("escidoc", "application/xml", "UTF-8");
        
        formatsArr = this.transformer.getTargetFormats(escidoc);
        for (int i =0; i< formatsArr.length; i++)
        {
            Format format = formatsArr[i];
            MetadataVO md = new MetadataVO();
            md.setName(format.getName() + "_" + format.getType());
            md.setMdFormat(format.getType());
            md.setEncoding(format.getEncoding());
            //md.setMdLabel(format.getName() + "_" + format.getType());
            
            formatsV.add(md);
        }

        return formatsV;
    }
    
    /**
     * Eliminates duplicates in a Vector.
     * @param dirtyVector as Vector<MetadataVO>
     * @return Vector with unique entries
     */
    public Vector<MetadataVO> getRidOfDuplicatesInVector(Vector<MetadataVO> dirtyVector)
    {
        Vector<MetadataVO> cleanVector = new Vector<MetadataVO>();
        MetadataVO format1;
        MetadataVO format2;
        
        
        for (int i = 0; i < dirtyVector.size(); i++)
        {
            boolean duplicate = false;
            format1 = (MetadataVO) dirtyVector.get(i);
            for (int x = i + 1; x < dirtyVector.size(); x++)
            {
                format2 = (MetadataVO) dirtyVector.get(x);
                if (this.isMdFormatEqual(format1, format2))
                {
                    duplicate = true;
                }
            }
            if (!duplicate)
            {
                cleanVector.add(format1);  
            }
        }
        
        return cleanVector;
    }
    
    /**
     * Checks if the format of two MetadataVO Objects are equal.
     * @param src1
     * @param src2
     * @return true if equal, else false
     */
    public boolean isMdFormatEqual(MetadataVO src1, MetadataVO src2)
    {
        if (!src1.getName().toLowerCase().trim().equals(src2.getName().toLowerCase().trim())) {return false;}
        if (!src1.getMdFormat().toLowerCase().trim().equals(src2.getMdFormat().toLowerCase().trim())) {return false;}
        if(!src1.getEncoding().equals("*") || !src2.getEncoding().equals("*"))
        {
            if (!src1.getEncoding().toLowerCase().trim().equals(src2.getEncoding().toLowerCase().trim())) {return false;}
        }
        return true;
    }
    
    /**
     * Checks if two Format Objects are equal.
     * @param src1
     * @param src2
     * @return true if equal, else false
     */
    public boolean isFormatEqual(Format src1, Format src2)
    {
        if (!src1.getName().toLowerCase().trim().equals(src2.getName().toLowerCase().trim())) {return false;}
        if (!src1.getType().toLowerCase().trim().equals(src2.getType().toLowerCase().trim())) {return false;}
        if (src1.getEncoding().equals("*") || src2.getEncoding().equals("*"))
        {
            return true;
        }
        else 
        {
            if (!src1.getEncoding().toLowerCase().trim().equals(src2.getEncoding().toLowerCase().trim())) {return false;}
            else {return true;}
        }
    }
    
    /**
     * Creates the source description xml
     * @return xml as byte[]
     */
    public byte[] createUnapiSourcesXml ()
    {
        byte[] xml = null;
        
        Vector<DataSourceVO> sources;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataSourceHandlerBean sourceHandler = new DataSourceHandlerBean();
        
        try
        {
            sources = sourceHandler.getSources();
            SourcesDocument xmlSourceDoc = SourcesDocument.Factory.newInstance();
            SourcesType xmlSources = xmlSourceDoc.addNewSources();
            for (int i = 0; i < sources.size(); i++)
            {
                DataSourceVO source = sources.get(i);
                SourceType xmlSource = xmlSources.addNewSource();
                //Name
                SimpleLiteral name = xmlSource.addNewName();
                XmlString sourceName = XmlString.Factory.newInstance();
                sourceName.setStringValue(source.getName());
                name.set(sourceName);
                //Base url
                SimpleLiteral url = xmlSource.addNewIdentifier();
                XmlString sourceUrl = XmlString.Factory.newInstance();
                sourceUrl.setStringValue(source.getUrl().toExternalForm());
                url.set(sourceUrl);
                //Description
                SimpleLiteral desc = xmlSource.addNewDescription();
                XmlString sourceDesc = XmlString.Factory.newInstance();
                sourceDesc.setStringValue(source.getDescription());
                desc.set(sourceDesc);
                //Identifier prefix
                SimpleLiteral idPre = xmlSource.addNewIdentifierPrefix();
                XmlString sourceidPre = XmlString.Factory.newInstance();
                sourceidPre.setStringValue(source.getIdentifier());
                idPre.set(sourceidPre);
                //Identifier delimiter
                SimpleLiteral idDel = xmlSource.addNewIdentifierDelimiter();
                XmlString sourceidDel = XmlString.Factory.newInstance();
                sourceidDel.setStringValue(":");
                idDel.set(sourceidDel);
                //Identifier example
                SimpleLiteral idEx = xmlSource.addNewIdentifierExample();
                XmlString sourceidEx = XmlString.Factory.newInstance();
                sourceidEx.setStringValue(source.getIdentifierExample());
                idEx.set(sourceidEx);
                //Disclaimer
                // SimpleLiteral disclaim = xmlSource.addNewDisclaimer();
                // XmlString sourceDisclaim = XmlString.Factory.newInstance();
                // sourceDisclaim.setStringValue("Disclaimer will follow");
                // disclaim.set(sourceDisclaim);
            }
            XmlOptions xOpts = new XmlOptions();
            xOpts.setSavePrettyPrint();
            xOpts.setSavePrettyPrintIndent(4);
            xOpts.setUseDefaultNamespace();
            xmlSourceDoc.save(baos, xOpts);
        }
        catch (IOException e)
        {
            this.logger.error("Error when creating outputXml.", e);
            throw new RuntimeException();
        }
        
        xml = baos.toByteArray();        
        return xml;
    }
    
    /**
     * Extracts out of a url the escidoc import source name.
     * @param sourceName
     * @return
     */
    public String trimSourceName(String sourceName, String identifier)
    {
        if (identifier.startsWith("http://dev-pubman.mpdl.mpg.de:8080/"))
        {
            sourceName = "escidocdev";
        }
        if (identifier.startsWith("http://qa-pubman.mpdl.mpg.de:8080/"))
        {
            sourceName = "escidocqa";
        }
        if (identifier.startsWith("http://test-pubman.mpdl.mpg.de/"))
        {
            sourceName = "escidoctest";
        }
        if (identifier.startsWith("http://pubman.mpdl.mpg.de/"))
        {
            sourceName = "escidocprod";
        }        
        return sourceName;
    }
    
    /**
     * EsciDoc Identifier can consist of the citation URL, like.
     * http://pubman.mpdl.mpg.de:8080/pubman/item/escidoc:1048:3. This method extracts the identifier from the URL
     * @param identifier
     */
    public String setEsciDocIdentifier(String identifier)
    {
        String[] extracts = identifier.split("/");
        return extracts[extracts.length - 1];
    }
}
