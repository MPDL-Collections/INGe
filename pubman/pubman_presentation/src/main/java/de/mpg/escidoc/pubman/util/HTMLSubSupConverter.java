package de.mpg.escidoc.pubman.util;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.List;
import java.util.Stack;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.*;
import de.mpg.escidoc.services.citationmanager.utils.Utils;
import de.mpg.escidoc.services.common.util.HtmlUtils;

public class HTMLSubSupConverter implements Converter{
    public static final String CONVERTER_ID = "HTMLSubSupConverter";
    
	public HTMLSubSupConverter()
	{
		
	}

	public Object getAsObject(FacesContext arg0, UIComponent arg1, String text) 
	{
		return null;
	}
	
	public String getAsString(FacesContext arg0, UIComponent arg1, Object object) 
	{
        String snippet = (String) object;
		return CommonUtils.escapeHtmlExceptSubSup(snippet);
	}
	
	


}
 