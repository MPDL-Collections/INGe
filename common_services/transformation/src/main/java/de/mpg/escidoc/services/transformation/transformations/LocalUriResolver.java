﻿/*
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
* Copyright 2006-2007 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/ 

package de.mpg.escidoc.services.transformation.transformations;

import java.io.FileNotFoundException;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;

import de.mpg.escidoc.services.common.util.ResourceUtil;

/**
 * This class handle URIs in XSLT stylesheets such as xsl:import.
 * In a jar the stylesheet can only be loaded as InputStream.
 * Without this URIResolver it is not possible to work with import statements.
 *
 * @author mfranke
 * @author $Author: mfranke $
 * @version $Revision: 1951 $$LastChangedDate: 2009-05-07 10:27:06 +0200 (Do, 07 Mai 2009) $
 */
public class LocalUriResolver implements URIResolver
{

    private String base = "";
    
    /**
     * Default constructor.
     */
    public LocalUriResolver()
    {
        
    }
    
    /**
     * Field-based constructor.
     * 
     * @param base The base URI.
     */
    public LocalUriResolver(String base)
    {
        this.base = base;
    }
    
    /**
     * {@inheritDoc}
     */
    public final Source resolve(String href, String altBase) throws TransformerException
    {
        if (altBase == null)
        {
            altBase = "";
        }
        
        try
        {
            //Source source = new StreamSource(ResourceUtil.getResourceAsStream(href));
            Source source = new StreamSource(ResourceUtil.getResourceAsStream(this.base + altBase + "/" + href));

            return source;
        }
        catch (FileNotFoundException e)
        {
            //throw new TransformerException("Cannot resolve URI: " + href);
            throw new TransformerException("Cannot resolve URIdgdsgdfgdfgd: " + this.base + altBase + "/" + href, e);
        }
    }
}
