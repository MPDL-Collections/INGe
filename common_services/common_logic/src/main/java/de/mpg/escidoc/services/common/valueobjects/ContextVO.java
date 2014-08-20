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
 * or http://www.escidoc.org/license.
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

import java.util.ArrayList;
import java.util.List;

import de.mpg.escidoc.services.common.referenceobjects.AccountUserRO;
import de.mpg.escidoc.services.common.referenceobjects.AffiliationRO;
import de.mpg.escidoc.services.common.referenceobjects.ContextRO;
import de.mpg.escidoc.services.common.valueobjects.interfaces.Searchable;
import de.mpg.escidoc.services.common.valueobjects.publication.MdsPublicationVO;
import de.mpg.escidoc.services.common.valueobjects.publication.PublicationAdminDescriptorVO;

/**
 * Special type of container of data with specific workflow (i.e. the publication management workflow). A set of
 * publication objects which have some common denominator. Collection may contain one or more subcollections.
 * 
 * @revised by MuJ: 28.08.2007
 * @version $Revision$ $LastChangedDate$ by $Author$
 * @updated 05-Sep-2007 11:14:08
 */
public class ContextVO extends ValueObject implements Searchable
{
	/**
	 * Fixed serialVersionUID to prevent java.io.InvalidClassExceptions like
	 * 'de.mpg.escidoc.services.common.valueobjects.ItemVO; local class incompatible: stream classdesc
	 * serialVersionUID = 8587635524303981401, local class serialVersionUID = -2285753348501257286' that occur after
	 * JiBX enhancement of VOs. Without the fixed serialVersionUID, the VOs have to be compiled twice for testing (once
	 * for the Application Server, once for the local test).
	 * 
	 * @author Johannes Mueller
	 */

	/**
	 * The possible states of a collection.
	 * 
	 * @updated 05-Sep-2007 11:14:08
	 */
	public enum State
	{
		CREATED, CLOSED, OPENED, DELETED
	}

	/**
	 * The reference object identifying this pubCollection.
	 */
	private ContextRO reference;
	/**
	 * A unique name of the collection within the system.
	 */
	private String name;

	private String type;
	/**
	 * The state of the PubCollection.
	 */
	private State state;
	/**
	 * A short description of the collection and the collection policy.
	 */
	private String description;
	/**
	 * The default metadata.
	 */
	private MetadataSetVO defaultMetadata;
	/**
	 * The creator of the collection.
	 */
	private AccountUserRO creator;
	/**
	 * The set union of validation points for items in this collection.
	 */
	private java.util.List<ValidationPointVO> validationPoints = new java.util.ArrayList<ValidationPointVO>();
	/**
	 * The list of responsible affiliations for this collection.
	 */
	private java.util.List<AffiliationRO> responsibleAffiliations = new java.util.ArrayList<AffiliationRO>();

	private List<AdminDescriptorVO> adminDescriptors = new ArrayList<AdminDescriptorVO>();
	/**
	 * Default constructor.
	 */
	public ContextVO()
	{

	}

	/**
	 * Clone constructor.
	 *
	 * @param context The collection to be cloned.
	 */
	public ContextVO(ContextVO context)
	{
		this.creator = context.creator;
		this.defaultMetadata = context.defaultMetadata;
		this.description = context.description;
		this.name = context.name;
		this.reference = context.reference;
		this.responsibleAffiliations = context.responsibleAffiliations;
		this.state = context.state;
		this.validationPoints = context.validationPoints;
		this.adminDescriptors = context.adminDescriptors;
		this.type=context.type;
	}

	/**
	 * Helper method for JiBX transformations. This method helps JiBX to determine if this is a 'create' or an 'update'
	 * transformation.
	 */
	public boolean alreadyExistsInFramework()
	{
		return (this.reference != null);
	}

	/**
	 * Delivers the description of the collection, i. e. a short description of the collection and the collection
	 * policy.
	 */
	public String getDescription()
	{
		return description;
	}

	/**
	 * Delivers the state of the collection.
	 */
	public ContextVO.State getState()
	{
		return state;
	}

	/**
	 * Sets the description of the collection, i. e. a short description of the collection and the collection policy.
	 * 
	 * @param newVal
	 */
	public void setDescription(String newVal)
	{
		description = newVal;
	}

	/**
	 * Sets the state of the collection.
	 * 
	 * @param newVal
	 */
	public void setState(ContextVO.State newVal)
	{
		state = newVal;
	}

	/**
	 * Delivers the name of the collection, i. e. a unique name of the collection within the system.
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Sets the name of the collection, i. e. a unique name of the collection within the system.
	 * 
	 * @param newVal
	 */
	public void setName(String newVal)
	{
		name = newVal;
	}

	/**
	 * Delivers the refence object identifying this pubCollection.
	 * 
	 * @see de.mpg.escidoc.services.common.referenceobjects.ReferenceObject
	 */
	public ContextRO getReference()
	{
		return reference;
	}

	/**
	 * Sets the refence object identifying this pubCollection.
	 * 
	 * @see de.mpg.escidoc.services.common.referenceobjects.ReferenceObject
	 * @param newVal
	 */
	public void setReference(ContextRO newVal)
	{
		reference = newVal;
	}

	/**
	 * Delivers the default metadata for items of the collection.
	 */
	public MetadataSetVO getDefaultMetadata()
	{
		return defaultMetadata;
	}

	/**
	 * Sets the default metadata for items of the collection.
	 * 
	 * @param newVal
	 */
	public void setDefaultMetadata(MdsPublicationVO newVal)
	{
		defaultMetadata = newVal;
	}

	/**
	 * Delivers the validation points of this collection.
	 */
	public java.util.List<ValidationPointVO> getValidationPoints()
	{
		return validationPoints;
	}

	/**
	 * Delivers the reference of the creator of the collection.
	 */
	public AccountUserRO getCreator()
	{
		return creator;
	}

	/**
	 * Sets the reference of the creator of the collection.
	 * 
	 * @param newVal
	 */
	public void setCreator(AccountUserRO newVal)
	{
		creator = newVal;
	}

	/**
	 * Delivers the list of affiliations which are responsible for this collection.
	 */
	public java.util.List<AffiliationRO> getResponsibleAffiliations()
	{
		return responsibleAffiliations;
	}

	public List<AdminDescriptorVO> getAdminDescriptors()
	{
		return adminDescriptors;
	}

	public PublicationAdminDescriptorVO getAdminDescriptor()
	{
		if (getAdminDescriptors().size() > 0 && getAdminDescriptors().get(0) instanceof PublicationAdminDescriptorVO)
		{
			return (PublicationAdminDescriptorVO)getAdminDescriptors().get(0);
		}
		else
		{
			return null;
		}
	}

	public void setAdminDescriptor(PublicationAdminDescriptorVO adminDescriptorVO)
	{
		if (getAdminDescriptors().size() > 0)
		{
			getAdminDescriptors().set(0, adminDescriptorVO);
		}
		else
		{
			getAdminDescriptors().add(adminDescriptorVO);
		}
	}

	public String getType()
	{
		return type;
	}

	public void setType(String type)
	{
		this.type = type;
	}

	@Override
	public boolean equals(Object other)
	{
		if (other == null)
		{
			return false;
		}
		else if (!(other instanceof ContextVO))
		{
			return false;
		}
		else
		{
			ContextVO otherContextVO = (ContextVO) other;
			if (otherContextVO.adminDescriptors.containsAll(this.adminDescriptors)
					&& this.adminDescriptors.containsAll(otherContextVO.adminDescriptors)
					&& ((otherContextVO.creator == null && this.creator == null)
							|| otherContextVO.creator.equals(this.creator)
					)
					&& ((otherContextVO.defaultMetadata == null && this.defaultMetadata == null)
							|| otherContextVO.defaultMetadata.equals(this.defaultMetadata)
					)
					&& ((otherContextVO.description == null && this.description == null)
							|| otherContextVO.description.equals(this.description)
					)
					&& ((otherContextVO.name == null && this.name == null)
							|| otherContextVO.name.equals(this.name)
					)
					&& ((otherContextVO.reference == null && this.reference == null)
							|| otherContextVO.reference.equals(this.reference)
					)
					&& otherContextVO.responsibleAffiliations.containsAll(this.responsibleAffiliations)
					&& this.responsibleAffiliations.containsAll(otherContextVO.responsibleAffiliations)
					&& otherContextVO.state == this.state
					&& ((otherContextVO.type == null && this.type == null)
							|| otherContextVO.type.equals(this.type)
					)
					&& ((otherContextVO.type == null && this.type == null)
							|| otherContextVO.type.equals(this.type)
					)
					&& otherContextVO.validationPoints.containsAll(this.validationPoints)
					&& this.validationPoints.containsAll(otherContextVO.validationPoints)
			)
			{
				System.out.println("Contexts are equal");
				return true;
			}
			else
			{
				System.out.println(otherContextVO.adminDescriptors.containsAll(this.adminDescriptors));
				System.out.println(this.adminDescriptors.containsAll(otherContextVO.adminDescriptors));
				System.out.println(otherContextVO.creator == null && this.creator == null);
				System.out.println(otherContextVO.creator.equals(this.creator));

				System.out.println(otherContextVO.defaultMetadata == null && this.defaultMetadata == null);
				System.out.println(otherContextVO.defaultMetadata.equals(this.defaultMetadata));

				System.out.println(otherContextVO.description == null && this.description == null);
				System.out.println(otherContextVO.description.equals(this.description));

				System.out.println(otherContextVO.name == null && this.name == null);
				System.out.println(otherContextVO.name.equals(this.name));

				System.out.println(otherContextVO.reference == null && this.reference == null);
				System.out.println(otherContextVO.reference.equals(this.reference));

				System.out.println(otherContextVO.responsibleAffiliations.containsAll(this.responsibleAffiliations));
				System.out.println(this.responsibleAffiliations.containsAll(otherContextVO.responsibleAffiliations));
				System.out.println(otherContextVO.state == this.state);
				System.out.println(otherContextVO.type == null && this.type == null);
				System.out.println(otherContextVO.type.equals(this.type));

				System.out.println(otherContextVO.type == null && this.type == null);
				System.out.println(otherContextVO.type.equals(this.type));

				System.out.println(otherContextVO.validationPoints.containsAll(this.validationPoints));
				System.out.println(this.validationPoints.containsAll(otherContextVO.validationPoints));
				return false;
			}
		}
	}



}