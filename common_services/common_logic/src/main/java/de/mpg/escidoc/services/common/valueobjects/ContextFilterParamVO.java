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

package de.mpg.escidoc.services.common.valueobjects;

/**
 * This VO is used to retrieve contexts filtered by certain filter criteria. It corresponds to filter-contexts.xsd.
 * 
 * @version $Revision$ $LastChangedDate$ by $Author$
 * @created 09-Okt-2007 16:17:57
 * @revised by MuJ: 09.10.2007
 */
public class ContextFilterParamVO extends FilterParamVO
{
    
     private static final long serialVersionUID = 1L;

    /**
     * The context state to filter for.
     */
    private ContextVO.State state;

    /**
     * @return The context state to filter for.
     */
    public ContextVO.State getState()
    {
        return state;
    }

    /**
     * The context state to filter for.
     * 
     * @param newVal
     */
    public void setState(ContextVO.State newVal)
    {
        state = newVal;
    }

}