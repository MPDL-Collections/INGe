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
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/ 

package de.mpg.escidoc.pubman.search.bean.criterion;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.mpg.escidoc.services.common.exceptions.TechnicalException;
import de.mpg.escidoc.services.search.query.MetadataSearchCriterion;
import de.mpg.escidoc.services.search.query.MetadataSearchCriterion.CriterionType;
import de.mpg.escidoc.services.search.query.MetadataSearchCriterion.LogicalOperator;

/**
 * date criterion vo for the advanced search
 * @created 15-Mai-2007 15:46:13
 * @author NiH
 * @version 1.0
 * Revised by NiH: 13.09.2007
 */
public class DateCriterion extends Criterion
{
	
    public enum DateType
    {
        ACCEPTED, CREATED, MODIFIED, PUBLISHED_ONLINE, PUBLISHED_PRINT, SUBMITTED
    }
    
    //date range for the search criterion
    private String from;
	private String to;
    //type of date
    private List<DateType> dateTypeList;

	/**
	 * constructor.
	 */
	public DateCriterion()
    {
        super();
	}

	public String getFrom()
    {
		return from;
	}

	public String getTo()
    {
		return to;
	}

	public void setFrom(String newVal)
    {
		from = newVal;
	}

	public void setTo(String newVal)
    {
		to = newVal;
	}

    public List<DateType> getDateType()
    {
        return dateTypeList;
    }

    public void setDateType(List<DateType> dateType)
    {
        this.dateTypeList = dateType;
    }
    
    private boolean isFromEmpty() {
    	if ( from == null || from.trim().equals("") ) {
			return true;
		}
		else {
			return false;
		}
    }
    
    private boolean isToEmpty() {
    	if ( to == null || to.trim().equals("") ) {
			return true;
		}
		else {
			return false;
		}
    }
    
    private CriterionType getCriterionByDateType( DateType dateType ) throws TechnicalException {
    	switch( dateType ) {
    	case ACCEPTED: 
    		return CriterionType.DATE_ACCEPTED;
    	case CREATED:
    		return CriterionType.DATE_CREATED;
    	case MODIFIED:
    		return CriterionType.DATE_MODIFIED;
    	case PUBLISHED_ONLINE:
    		return CriterionType.DATE_PUBLISHED_ONLINE;
    	case PUBLISHED_PRINT:
    		return CriterionType.DATE_ISSUED;
    	case SUBMITTED:
    		return CriterionType.DATE_SUBMITTED;
    	default:
    		throw new TechnicalException( "DateType is unknown. Cannot map." );
    	}
    }
    
    public ArrayList<MetadataSearchCriterion> createSearchCriterion() throws TechnicalException {
    	ArrayList<MetadataSearchCriterion> criterions = new ArrayList<MetadataSearchCriterion>(); 	
    	if( isFromEmpty() == true ) {
    		// return an empty criteria list
    	}
    	else {
    		if( isToEmpty() == true ) {
    			Date currentDate = new Date();   //actual date
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    			 for( int i = 0; i < dateTypeList.size(); i++ ) {
    				 MetadataSearchCriterion criterionFrom = 
    					 new MetadataSearchCriterion( getCriterionByDateType( dateTypeList.get( i ) ), from, LogicalOperator.OR );
    				 MetadataSearchCriterion criterionTo = 
    					 new MetadataSearchCriterion( getCriterionByDateType( 
    							 dateTypeList.get( i ) ), dateFormat.format(currentDate), LogicalOperator.AND );
    			 criterions.add( criterionFrom );
    			 criterions.add( criterionTo );
    			 }
    		}
    		else {
    			for( int i = 0; i < dateTypeList.size(); i++ ) {
    				MetadataSearchCriterion criterionFrom = 
    					new MetadataSearchCriterion( getCriterionByDateType( dateTypeList.get( i ) ), from, LogicalOperator.OR );
    				MetadataSearchCriterion criterionTo = 
    					new MetadataSearchCriterion( getCriterionByDateType( dateTypeList.get( i ) ), to, LogicalOperator.AND );
    				criterions.add( criterionFrom );
    				criterions.add( criterionTo );
    			}		
    		}
    	}
	   	return criterions;
	} 
}