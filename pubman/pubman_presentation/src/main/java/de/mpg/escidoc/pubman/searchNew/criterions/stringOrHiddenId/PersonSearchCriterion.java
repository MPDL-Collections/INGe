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
package de.mpg.escidoc.pubman.searchNew.criterions.stringOrHiddenId;

import de.mpg.escidoc.pubman.searchNew.SearchParseException;
import de.mpg.escidoc.pubman.searchNew.criterions.SearchCriterionBase.DisplayType;
import de.mpg.escidoc.services.common.valueobjects.metadata.CreatorVO;

public class PersonSearchCriterion extends StringOrHiddenIdSearchCriterion {

	
	//private static String PERSON_ROLE_INDEX = "escidoc.publication.creator.role";
	
	private String[] cqlIndexForHiddenId;
	private String[] cqlIndexForSearchString;
	
	private String[] cqlIndexForHiddenIdAdmin; 
	private String[] cqlIndexForSearchStringAdmin;
	
	
	
	
	public PersonSearchCriterion(SearchCriterion role)
	{
		this.searchCriterion = role;
	}
	
	@Override
	public String[] getCqlIndexForHiddenId(Index indexName) {
		
		switch(indexName)
		{
			case ESCIDOC_ALL : return cqlIndexForHiddenId;
			case ITEM_CONTAINER_ADMIN : return cqlIndexForHiddenIdAdmin;
		}
		
		return null;
	}

	@Override
	public String[] getCqlIndexForSearchString(Index indexName) {
		switch(indexName)
		{
			case ESCIDOC_ALL : return cqlIndexForSearchString;
			case ITEM_CONTAINER_ADMIN : return cqlIndexForSearchStringAdmin;
		}
		
		return null;
	}

	

	
	@Override
	public String toCqlString(Index indexName)  throws SearchParseException{
		
		if(SearchCriterion.ANYPERSON.equals(getSearchCriterion()))
		{
			cqlIndexForHiddenId = new String[] {"escidoc.publication.creator.person.identifier"};
			cqlIndexForSearchString = new String[] {"escidoc.publication.creator.person.compound.person-complete-name"};
			
			cqlIndexForHiddenIdAdmin = new String[] {"\"/md-records/md-record/publication/creator/person/identifier\""};
			cqlIndexForSearchStringAdmin = new String[] {"\"/md-records/md-record/publication/creator/person/compound/person-complete-name\""}; 
			
			return super.toCqlString(indexName);
		}
		else
		{
			String roleUri = CreatorVO.CreatorRole.valueOf(getSearchCriterion().name()).getUri();
			String roleAbbr = roleUri.substring(roleUri.lastIndexOf('/')+1, roleUri.length());
			
			cqlIndexForHiddenId = new String[] {"escidoc.publication.creator.compound.role-person." + roleAbbr};
			cqlIndexForSearchString = new String[] {"escidoc.publication.creator.compound.role-person." + roleAbbr};
			
			cqlIndexForHiddenIdAdmin = new String[] {"\"/md-records/md-record/publication/creator/person/compound/role-person/"  + roleAbbr + "\""}; 
			cqlIndexForSearchStringAdmin = new String[] {"\"/md-records/md-record/publication/creator/person/compound/role-person/"  + roleAbbr + "\""}; 
			
			/*
			StringBuilder sb = new StringBuilder();
			sb.append("(");
			sb.append(superQuery);
			sb.append(" and ");
			sb.append(PERSON_ROLE_INDEX);
			sb.append("=\"");
			sb.append(escapeForCql(roleUri) + "\")");
			*/
			return  super.toCqlString(indexName);
		}
		
		
		
	}

	/*
	@Override
	public SearchCriterion getSearchCriterion() {
		return searchCriterion;
		
	}
	*/



	

}
