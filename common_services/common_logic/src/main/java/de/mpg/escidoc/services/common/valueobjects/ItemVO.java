/*
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
package de.mpg.escidoc.services.common.valueobjects;

import java.util.Date;
import java.util.List;

import de.mpg.escidoc.services.common.referenceobjects.AccountUserRO;
import de.mpg.escidoc.services.common.referenceobjects.ContextRO;
import de.mpg.escidoc.services.common.referenceobjects.ItemRO;
import de.mpg.escidoc.services.common.valueobjects.interfaces.Searchable;

/**
 * Item object which consists of descriptive metadata and may have one or more files associated.
 * 
 * @revised by MuJ: 28.08.2007
 * @version $Revision$ $LastChangedDate$ by $Author$
 * @updated 21-Nov-2007 11:52:58
 */
public class ItemVO extends ValueObject implements Searchable
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
 
    public enum ItemAction{
        RETRIEVE, SUBMIT, RELEASE, EXPORT
    }

    /**
     * The possible states of an item.
     * 
     * @updated 21-Nov-2007 11:52:58
     */
    public enum State
    {
        PENDING, SUBMITTED, RELEASED, WITHDRAWN, IN_REVISION
    }

    /**
     * The possible lock status of an item.
     * 
     * @updated 21-Nov-2007 11:52:58
     */
    public enum LockStatus
    {
        LOCKED, UNLOCKED
    }

    private java.util.List<FileVO> files = new java.util.ArrayList<FileVO>();
    private java.util.List<String> localTags = new java.util.ArrayList<String>();
    private List<MetadataSetVO> metadataSets = new java.util.ArrayList<MetadataSetVO>();

    private String baseUrl;
    
    private AccountUserRO owner;
    /**
     * The persistent identifier of the released item.
     */
    private String pid;
    private ContextRO contextRO;
    
    private String contentModel;
    
    /**
     * Version information of this item version.
     */
    private ItemRO version = new ItemRO();
    
    /**
     * Version information of the latest version of this item.
     */
    private ItemRO latestVersion = new ItemRO();
    
    /**
     * Version information of the latest release of this item.
     */
    private ItemRO latestRelease = new ItemRO();

    /**
     * This list of relations is a quickfix and cannot be found in the model yet. The reason for this is that the
     * relations are delivered with every item retrieval from the framework, and they get deleted when they are note
     * provided on updates. TODO MuJ or BrP: model and implement correctly, transforming too. Remove quickfix-VO
     * ("ItemRelationVO").
     */
    private List<ItemRelationVO> relations = new java.util.ArrayList<ItemRelationVO>();

    private java.util.Date creationDate;
    private ItemVO.LockStatus lockStatus;
    private ItemVO.State publicStatus;
    private String publicStatusComment;

    public String getPublicStatusComment()
    {
        return publicStatusComment;
    }

    public void setPublicStatusComment(String publicStatusComment)
    {
        this.publicStatusComment = publicStatusComment;
    }

    /**
     * Public constructor.
     * 
     * @author Thomas Diebaecker
     */
    public ItemVO()
    {
        super();
    }

    /**
     * Copy constructor.
     * 
     * @author Thomas Diebaecker
     * @param other The instance to copy.
     */
    public ItemVO(ItemVO other)
    {
        this.setCreationDate(other.getCreationDate());

        this.setBaseUrl(other.getBaseUrl());
        
        for (FileVO file : other.getFiles())
        {
            this.getFiles().add((FileVO) file.clone());
        }
        this.setLockStatus(other.getLockStatus());
        this.setPublicStatus(other.getPublicStatus());
        this.setPublicStatusComment(other.getPublicStatusComment());
        for (MetadataSetVO mds : other.getMetadataSets())
        {
            this.getMetadataSets().add(mds.clone());
        }
        if (other.getOwner() != null)
        {
            this.setOwner((AccountUserRO) other.getOwner().clone());
        }
        this.setPid(other.getPid());
        if (other.getContext() != null)
        {
            this.setContext((ContextRO) other.getContext().clone());
        }
        if (other.getContentModel() != null)
        {
            this.setContentModel(other.getContentModel());
        }
        try
        {
            if (other.getVersion() != null)
            {
                this.setVersion((ItemRO) other.getVersion().clone());
            }
            if (other.getLatestVersion() != null)
            {
                this.setLatestVersion((ItemRO) other.getLatestVersion().clone());
            }
            if (other.getLatestRelease() != null)
            {
                this.setLatestRelease((ItemRO) other.getLatestRelease().clone());
            }
            for (ItemRelationVO relation : other.getRelations())
            {
                this.getRelations().add((ItemRelationVO) relation.clone());
            }
        }
        catch (CloneNotSupportedException cnse)
        {
            throw new RuntimeException(cnse);
        }
        for (String localTag : other.getLocalTags())
        {
            this.localTags.add(localTag);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @author Thomas Diebaecker
     */
    @Override
    public Object clone()
    {
        return new ItemVO(this);
    }

    /**
     * Helper method for JiBX transformations. This method helps JiBX to determine if this is a 'create' or an 'update'
     * transformation.
     * 
     * @return true, if this item already has a version object.
     */
    boolean alreadyExistsInFramework()
    {
        return (this.version != null);
    }

    /**
     * Helper method for JiBX transformations. This method helps JiBX to determine if a "components" XML structure has
     * to be created during marshalling.
     * 
     * @return true, if the item contains one or more files.
     */
    boolean hasFiles()
    {
        return (this.files.size() >= 1);
    }

    /**
     * Helper method for JiBX transformations.
     */
    boolean hasPID()
    {
        return (this.pid != null);
    }

    /**
     * Helper method for JiBX transformations. This method helps JiBX to determine if a "relations" XML structure has to
     * be created during marshalling.
     */
    boolean hasRelations()
    {
        return (this.relations.size() >= 1);
    }

    /**
     * Delivers the list of files in this item.
     */
    public java.util.List<FileVO> getFiles()
    {
        return files;
    }

    /**
     * Delivers the metadata sets of the item.
     */
    public List<MetadataSetVO> getMetadataSets()
    {
        return metadataSets;
    }

    /**
     * Delivers the owner of the item.
     */
    public AccountUserRO getOwner()
    {
        return owner;
    }

    /**
     * Delivers the persistent identifier of the item.
     */
    public String getPid()
    {
        return pid;
    }

    /**
     * Delivers the reference of the collection the item is contained in.
     */
    public ContextRO getContext()
    {
        return contextRO;
    }

    /**
     * Delivers the reference of the item.
     */
    public ItemRO getVersion()
    {
        return version;
    }

    /**
     * Delivers the list of relations in this item.
     */
    public java.util.List<ItemRelationVO> getRelations()
    {
        return relations;
    }

    /**
     * Sets the owner of the item.
     * 
     * @param newVal
     */
    public void setOwner(AccountUserRO newVal)
    {
        owner = newVal;
    }

    /**
     * Sets the persistent identifier of the item.
     * 
     * @param newVal
     */
    public void setPid(String newVal)
    {
        pid = newVal;
    }

    /**
     * Sets the reference of the collection the item is contained in.
     * 
     * @param newVal
     */
    public void setContext(ContextRO newVal)
    {
        contextRO = newVal;
    }

    /**
     * Sets the reference of the item.
     * 
     * @param newVal
     */
    public void setVersion(ItemRO newVal)
    {
        version = newVal;
    }

    /**
     * Delivers the date when the item was created.
     */
    public java.util.Date getCreationDate()
    {
        return creationDate;
    }

    /**
     * Delivers the lock status of the item.
     */
    public LockStatus getLockStatus()
    {
        return lockStatus;
    }

    /**
     * Sets the date when the item was created.
     * 
     * @param newVal
     */
    public void setCreationDate(java.util.Date newVal)
    {
        creationDate = newVal;
    }

    /**
     * Sets the lock status of the item.
     * 
     * @param newVal
     */
    public void setLockStatus(LockStatus newVal)
    {
        this.lockStatus = newVal;
    }

    /**
     * Delivers the comment which has to be given when an item is withdrawn.
     */
    public String getWithdrawalComment()
    {
        if (getPublicStatus() == ItemVO.State.WITHDRAWN)
        {
            return getPublicStatusComment();
        }
        else
        {
            return null;
        }
    }

    /**
     * Delivers the comment which has to be given when an item is withdrawn.
     * 
     * @return The modification date as {@link Date}.
     */
    public Date getModificationDate()
    {
        if (getVersion() != null)
        {
            return getVersion().getModificationDate();
        }
        else
        {
            return null;
        }
    }
    
    public ItemRO getLatestVersion()
    {
        return latestVersion;
    }

    public void setLatestVersion(ItemRO latestVersion)
    {
        this.latestVersion = latestVersion;
    }

    public ItemRO getLatestRelease()
    {
        return latestRelease;
    }

    public void setLatestRelease(ItemRO latestRelease)
    {
        this.latestRelease = latestRelease;
    }

    public String getContentModel()
    {
        return contentModel;
    }

    public void setContentModel(String contentModel)
    {
        this.contentModel = contentModel;
    }

    public State getPublicStatus()
    {
        return publicStatus;
    }
    
    public void setPublicStatus(State publicStatus)
    {
        this.publicStatus = publicStatus;
    }

    public java.util.List<String> getLocalTags()
    {
        return localTags;
    }

    public String getBaseUrl()
    {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl)
    {
        this.baseUrl = baseUrl;
    }

}