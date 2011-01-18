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
* Copyright 2006-2011 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/ 

package de.mpg.escidoc.services.pubman.exceptions;

import de.mpg.escidoc.services.common.referenceobjects.ContextRO;

/**
 * Exception class used for missing pubcollections.
 * 
 * @author Miriam Doelle (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$ 
 * @revised by MuJ: 19.09.2007
 */
public class PubCollectionNotFoundException extends PubManException
{
    /**
     * The reference of the collection that could not be found.
     */
    private ContextRO pubCollectionRef;
    
    /**
     * Creates a new instance, sets the according member variable.
     * 
     * @param collection The collection that caused this exception.
     * @param cause The throwable that caused this exception.
     */
    public PubCollectionNotFoundException(ContextRO collection, Throwable cause)
    {
        super(cause);
        this.pubCollectionRef = collection;         
    }

    /**
     * @return The reference of the collection that could not be found. 
     */
    public ContextRO getPubCollectionRef()
    {
        return pubCollectionRef;
    }

}