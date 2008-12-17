package test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.sun.syndication.io.FeedException;

import de.mpg.escidoc.services.syndication.Feeds;
import de.mpg.escidoc.services.syndication.Syndication;
import de.mpg.escidoc.services.syndication.SyndicationHandler;
import de.mpg.escidoc.services.syndication.SyndicationException;
import de.mpg.escidoc.services.syndication.Utils;
import de.mpg.escidoc.services.syndication.feed.Feed;

/**
 * Unit test for simple App.
 */
public class SyndicationTest  
{

    private Logger logger = Logger.getLogger(SyndicationTest.class);
	
	SyndicationHandler sh;

    /**
     * 
     * @throws SyndicationManagerException 
     * @throws IOException 
     */
    @Before
    public void init() throws SyndicationException, IOException
    {
    	sh = new Syndication();
    }
    
    @Test
    public void checkExplainFeedsXML()
    {
    	assertTrue("Empty explainFeedsXml", Utils.checkVal(sh.explainFeedsXML()));
		logger.info("explainFeedsXML: \n" + sh.explainFeedsXML()); 
    }
    
    @Test
    public void checkGetFeedFormatList()
    {
    	logger.info("feedList---");
    	String[] fl = sh.getFeedList();
    	assertTrue("Empty feed list",  fl != null && fl.length > 0 );
    	for (String f : fl)
    	{
    		logger.info("feed Id: " + f);
        	String[] ffl = sh.getFeedFormatList(f);
        	assertTrue("Empty feed format list",  ffl != null && ffl.length > 0 );
    		for (String ff : ffl) {
    			logger.info("---format:" + ff);
    		}
    	}
 
    }
    
    @Test
    public void checkGetFeed() throws SyndicationException, IOException, URISyntaxException, FeedException
    {

    	String uri;
    	long start;
    	
    	uri = "http://pubman.mpdl.mpg.de/atom_1.0/publications/organization/escidoc:persistent22";
    	start = System.currentTimeMillis();
    	logger.info( new String(sh.getFeed(uri)) );
		logger.info("Processing time: " + (System.currentTimeMillis() - start) );
    	
    	uri = "http://pubman.mpdl.mpg.de/rss_2.0/publications/organization/escidoc:persistent22";
    	start = System.currentTimeMillis();
    	logger.info( new String(sh.getFeed(uri)) );
    	logger.info("Processing time: " + (System.currentTimeMillis() - start) );
    }
    
    
}
