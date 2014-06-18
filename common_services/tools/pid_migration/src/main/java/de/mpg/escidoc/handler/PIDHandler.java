package de.mpg.escidoc.handler;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import de.mpg.escidoc.handler.PreHandler.Type;
import de.mpg.escidoc.main.PIDMigrationManager;
import de.mpg.escidoc.main.PIDProviderIf;
import de.mpg.escidoc.util.Util;

public class PIDHandler extends IdentityHandler
{
    private static Logger logger = Logger.getLogger(PIDHandler.class);

    protected PreHandler preHandler;
    private PIDMigrationManager pidMigrationManager;
    private PIDProviderIf pidProvider;
    
    private String actualRelsExtId = "";
    
    protected boolean inRelsExt = false;
    protected boolean inObjectPid = false;
    protected boolean inVersionPidOrReleasePid = false;
    protected boolean inVersionHistoryPid = false;
    protected boolean inRdfDescription = false;
    protected boolean systemBuildClosed = false;
    
    // flag indicating if a modify has taken place.
    protected boolean updateDone = false;
    
    protected static final String DUMMY_HANDLE = "someHandle";
    
    protected Map<String, String> replacedPids = new HashMap<String, String>();
    
    public PIDHandler(PreHandler preHandler) throws Exception 
    {
        this.preHandler = preHandler;
/*        
        preHandler.getVersionNumber();
        preHandler.getReleaseNumber();
        preHandler.getEscidocId();*/
        
        this.init();       
    }
    
    private void init() throws Exception
    {
        Class<?> pidProviderClass = Class.forName(Util.getProperty("escidoc.pidprovider.class"));
        
        this.pidProvider = (PIDProviderIf)pidProviderClass.newInstance();                
        this.pidProvider.init();
    }

    public void setPIDMigrationManager(PIDMigrationManager mgr)
    {
        this.pidMigrationManager = mgr;
    }
    
    @Override
    public void startDocument() throws SAXException
    {
        append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
    }

    @Override
    public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException
    {
        logger.debug("startElement uri=<" + uri + "> localName = <" + localName + "> name = <" + name + "> attributes = <" + attributes + ">");
        
        
        if (preHandler.getObjectType().equals(Type.COMPONENT) && inRdfDescription && systemBuildClosed)
        {
            insertPropPid(actualRelsExtId, name, attributes);
        }
        
        super.startElement(uri, localName, name, attributes);
        
        if (("foxml:datastream".equals(name) && "RELS-EXT".equals(attributes.getValue("ID"))))
        { 
            inRelsExt = true;
            logger.debug(" startElement inRelsExt= " + inRelsExt);
        }
        else if ("foxml:datastreamVersion".equals(name) && inRelsExt)
        {
            actualRelsExtId = attributes.getValue("ID");
            logger.debug("startElement actualRelsExtId = " + actualRelsExtId);
        }
        else if (inRelsExt && "prop:pid".equals(name))
        {
            inObjectPid = true;
        }
        else if (inRelsExt && ("version:pid".equals(name) || "release:pid".equals(name)))
        {
            inVersionPidOrReleasePid = true;
        }
        else if ("escidocVersions:pid".equals(name))
        { 
            inVersionHistoryPid = true;
        }
        else if (inRelsExt && "rdf:Description".equals(name))
        { 
            inRdfDescription = true;
            systemBuildClosed = false;
        }
    }

    @Override
    public void content(String uri, String localName, String name, String content) throws SAXException
    {
        logger.debug("content      uri=<" + uri + "> localName = <" + localName + "> name = <" + name + "> content = <"
                + content + ">");
        
        // fallback if pidcache isn't reachable
        String oldContent = content;
        
        if (inObjectPid )
        {            
            try
            {
                content = getPid(content, false);
            }
            catch (PIDProviderException e)
            {
                pidMigrationManager.onError(e);
            }
            inObjectPid = false;
        }
        else if (inVersionPidOrReleasePid)
        {
            try
            {
                content = getPid(content, true);
            }
            catch (PIDProviderException e)
            {
                pidMigrationManager.onError(e);
            }
            inVersionPidOrReleasePid = false;
        }
        else if (inVersionHistoryPid)
        {          
            try
            {
                content = getPid(content, true);
            }
            catch (PIDProviderException e)
            {
                pidMigrationManager.onError(e);
            }
            inVersionHistoryPid = false;
        }
        
        if (!content.equals(oldContent))
        {
            updateDone = true;
        }
        
        super.content(uri, localName, name, content );
    }

    private String getPid(String content, boolean withVersion) throws PIDProviderException
    {
        Matcher m = AssertionHandler.handlePattern.matcher(content);
        // already a real pid
        if (m.matches())
        {
            return content;
        }
        
        // pid attribute has already been requested
        if (replacedPids.get(content) != null)
        {
            return replacedPids.get(content);
        }
        
        // a new pid has to be requested
        
        String oldContent = content;
                
        // older foxmls contain elements with an empty String value - in this case we don't do anything
        if ("".equals(oldContent))
        {
            return oldContent;
        }
        
        String update = Util.getProperty("triplestore.datasource.table.update");
        
        if (withVersion)
        {
            String versionInDummyHandle = content.substring(content.lastIndexOf(":") + 1);
            String versionNumber = preHandler.getVersionNumber(actualRelsExtId);
            
            if (!versionInDummyHandle.equals(versionNumber))
            {
                logger.warn("inconsistent versions for <" + preHandler.getEscidocId() + "> versionInDummyHandle <" 
                        + versionInDummyHandle + "> versionNumber <" + versionNumber + ">  in <" + actualRelsExtId + ">");
            }
            content = pidProvider.getPid(preHandler.getEscidocId() + ":" + versionNumber, preHandler.getObjectType(), preHandler.getTitle());
        }
        else
        {
            content = pidProvider.getPid(preHandler.getEscidocId(), preHandler.getObjectType(), preHandler.getTitle());
        }
            
        content = doReplace(content);
        replacedPids.put(oldContent, content);
        
        return content;
    }

    // helper method to treat the following cases:
    // if a PID is fetched from PidCache Service, a plain PID without prefix hdl: is returned. If already a PID is available in the content parameter, 
    // the content already starts with the prefix.
    private String doReplace(String content)
    {
        if (content == null || "".equals(content))
            return "";
        
        if (content.startsWith("hdl"))
            return content;
            
        return "hdl:" + content;
    }

    @Override
    public void endElement(String uri, String localName, String name) throws SAXException
    {       
        logger.debug("endElement   uri=<" + uri + "> localName = <" + localName + "> name = <" + name + "> ");
        if ("foxml:datastream".equals(name))
        {
            inRelsExt = false;
        } 
        else if (inRelsExt && inRdfDescription && "system:build".equals(name))
        { 
            systemBuildClosed = true;
        }
        else if (inRelsExt && "rdf:Description".equals(name))
        { 
            inRdfDescription = false;
        }
        
        super.endElement(uri, localName, name);
    }
    
    // helper method to repair missing prop:pid elements in foxmls for component type
    private void insertPropPid(String actRelsExtId, String name, Attributes attributes) throws SAXException
    {
        if (!preHandler.getObjectType().equals(Type.COMPONENT) || !preHandler.isObjectPidToInsert(actRelsExtId))
            return;
        
        // components have no version pids, so the only one to occur is one single object pid
        if (replacedPids.values().size() == 1)
        {
            String replacedObjectPid = replacedPids.values().iterator().next();
            super.startElement("", "", "prop:pid", attributes);
            super.content("", "", "prop:pid", replacedObjectPid);
            super.endElement("", "", "prop:pid");
        }
        
        systemBuildClosed = false;
    }
    
    public boolean isUpdateDone()
    {
        return updateDone;
    }

    public int getTotalNumberOfPidsRequested()
    {
        return this.pidProvider.getTotalNumberOfPidsRequested();
    }
}
