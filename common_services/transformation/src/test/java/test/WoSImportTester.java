package test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Test;

import de.mpg.escidoc.services.common.util.ResourceUtil;
import de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO;
import de.mpg.escidoc.services.common.xmltransforming.XmlTransformingBean;
import de.mpg.escidoc.services.transformation.Transformation;
import de.mpg.escidoc.services.transformation.TransformationBean;
import de.mpg.escidoc.services.transformation.Util;
import de.mpg.escidoc.services.transformation.transformations.otherFormats.wos.Pair;
import de.mpg.escidoc.services.transformation.transformations.otherFormats.wos.WoSTransformation;
import de.mpg.escidoc.services.transformation.transformations.otherFormats.ris.RISImport;
import de.mpg.escidoc.services.transformation.transformations.otherFormats.wos.WoSImport;
import de.mpg.escidoc.services.transformation.valueObjects.Format;

public class WoSImportTester {

	private final Logger logger = Logger.getLogger(WoSImportTester.class);
    private Util util = new Util();
    WoSTransformation wosTransformer = new WoSTransformation();
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		WoSImport imp = new WoSImport();
		
    	Transformation transformation = new TransformationBean();
    	Format inputFormat = new Format("WoS", "text/plain", "UTF-8");
    	Format outputFormat = new Format("eSciDoc-publication-item-list", "application/xml", "UTF-8");
    	
    	InputStream inputStream = ResourceUtil.getResourceAsStream("/home/kurt/Dokumente/wok-isi-test.txt");
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
    	byte[] buffer = new byte[2048];
    	int read;
    	while ((read = inputStream.read(buffer)) != -1)
    	{
    		baos.write(buffer, 0, read);
    	}
    	//String str = imp.readFile();
    	String out = imp.transformWoS2XML(new String(baos.toByteArray(),"UTF-8"));
    	//String out = imp.transformWoS2XML(str);
    	byte[] result = transformation.transform(baos.toByteArray(), inputFormat, outputFormat, "escidoc");
    	
    	
    	
    	
    	System.out.print(new String(result,"UTF-8"));
    	//System.out.print(out);
	}
	

    @Test
    public void wosListTransformation() throws Exception
    {
        this.logger.info("Transform WoS list to xml format");
        Format inputFormat = new Format("WoS", "text/plain", "utf-8");
        Format outputFormat = new Format("eSciDoc-publication-item-list", "application/xml", "utf-8");
        byte[] result = wosTransformer.transform(this.util.getResourceAsString("testFiles/wos/WoS.txt").getBytes("UTF-8"), inputFormat, outputFormat, "escidoc");
        
        XmlTransformingBean xmlTransforming = new XmlTransformingBean();
        List <PubItemVO> itemVOList = xmlTransforming.transformToPubItemList(new String(result));
        this.logger.info("PubItemVO List successfully created.");
    }


}
