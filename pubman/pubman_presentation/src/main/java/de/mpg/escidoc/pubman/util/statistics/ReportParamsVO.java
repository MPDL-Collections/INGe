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
* Copyright 2006-2007 Fachinformationszentrum Karlsruhe Gesellschaft
* f�r wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur F�rderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/ 

package de.mpg.escidoc.pubman.util.statistics;

import java.util.ArrayList;

import de.mpg.escidoc.services.common.valueobjects.ValueObject;



/**
 * TODO Description
 *
 * @author haarlae1 (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class ReportParamsVO extends ValueObject
{
    
    private String reportDefinitionObjID;

    private ArrayList<ReportParameterVO> parameters = new ArrayList<ReportParameterVO>();;
    
    
    
    public String getReportDefinitionObjID()
    {
        return reportDefinitionObjID;
    }

    public void setReportDefinitionObjID(String reportDefinitionObjID)
    {
        this.reportDefinitionObjID = reportDefinitionObjID;
    }
    
    public void addReportParameter(ReportParameterVO p) {
        
        parameters.add(p);
    }
    
    public ArrayList<ReportParameterVO> getReportParameters() {
        return parameters;
    }
   
 

    
  

    
}
