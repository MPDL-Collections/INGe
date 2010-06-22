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
* Copyright 2006-2010 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/ 

package de.mpg.escidoc.pubman.util;

import java.util.List;

import javax.faces.event.ValueChangeEvent;

import de.mpg.escidoc.pubman.appbase.FacesBean;
import de.mpg.escidoc.pubman.editItem.EditItemSessionBean;
import de.mpg.escidoc.services.common.valueobjects.metadata.OrganizationVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.TextVO;

/**
 * Presentation wrapper for OrganizationVO.
 *
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class OrganizationVOPresentation extends OrganizationVO
{
    private EditItemSessionBean bean;

    public OrganizationVOPresentation()
    {
        super();
        setName(new TextVO());
    }
    
    public OrganizationVOPresentation(OrganizationVO organizationVO)
    {
        this.setAddress(organizationVO.getAddress());
        this.setIdentifier(organizationVO.getIdentifier());
        this.setName(organizationVO.getName());
    }
    
    /**
     * Adds an organization to the list after this organization.
     * 
     * @return Always empty
     */
    public String add()
    {
        OrganizationVOPresentation organizationPresentation = new OrganizationVOPresentation();
        organizationPresentation.setBean(bean);
        bean.getCreatorOrganizations().add(getNumber(), organizationPresentation);

        for (CreatorVOPresentation creator : bean.getCreators())
        {
            int[] ous = creator.getOus();
            String newOuNumbers = "";
            for (int i = 0; i < ous.length; i++)
            {
                if (ous[i] <= getNumber() || ous[i] >= getList().size())
                {
                    if (!"".equals(newOuNumbers))
                    {
                        newOuNumbers += ",";
                    }
                    newOuNumbers += ous[i];
                }
                else if (ous[i] > getNumber())
                {
                    if (!"".equals(newOuNumbers))
                    {
                        newOuNumbers += ",";
                    }
                    newOuNumbers += (ous[i] + 1);
                }
            }
            creator.setOuNumbers(newOuNumbers);
        }
        return "";
    }

    /**
     * Removes this organization from the list.
     * 
     * @return Always empty
     */
    public String remove()
    {
        getList().remove(this);
        for (CreatorVOPresentation creator : bean.getCreators())
        {
            int[] ous = creator.getOus();
            String newOuNumbers = "";
            for (int i = 0; i < ous.length; i++)
            {
                if (ous[i] < getNumber())
                {
                    if (!"".equals(newOuNumbers))
                    {
                        newOuNumbers += ",";
                    }
                    newOuNumbers += ous[i];
                }
                else if (ous[i] > getNumber())
                {
                    if (!"".equals(newOuNumbers))
                    {
                        newOuNumbers += ",";
                    }
                    newOuNumbers += (ous[i] - 1);
                }
            }
            creator.setOuNumbers(newOuNumbers);
        }
        return "";
    }
    
    /**
     * @return the position in the list, starting with 1.
     */
    public int getNumber()
    {
        return getList().indexOf(this) + 1;
    }

    /**
     * @return the list
     */
    public List<OrganizationVOPresentation> getList()
    {
        return bean.getCreatorOrganizations();
    }

    /**
     * @param list the list to set
     */
    public void setBean(EditItemSessionBean bean)
    {
        this.bean = bean;
    }
    
    public void nameListener(ValueChangeEvent event)
    {
        if(event.getNewValue() != event.getOldValue())
        {
            this.setName(new TextVO(event.getNewValue().toString()));
        }
    }
    
    public boolean getLast()
    {
    	return (this.equals(bean.getCreatorOrganizations().get(bean.getCreatorOrganizations().size() - 1)));
    }
    
    public boolean isEmpty()
    {
    	if (this.getAddress() != null && !"".equals(this.getAddress()))
    	{
    		return false;
    	}
    	else if (this.getName() != null && this.getName().getValue() != null && !"".equals(this.getName().getValue()))
    	{
    		return false;
    	}
    	else
    	{
    		return true;
    	}
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj)
    {
        return (this == obj);
    }
}
