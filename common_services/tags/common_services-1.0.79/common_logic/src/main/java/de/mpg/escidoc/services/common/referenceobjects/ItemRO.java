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

package de.mpg.escidoc.services.common.referenceobjects;

import java.util.Date;

import de.mpg.escidoc.services.common.valueobjects.ItemVO;
import de.mpg.escidoc.services.common.valueobjects.ItemVO.State;

/**
 * The class for item references.
 *
 * @revised by MuJ: 27.08.2007
 * @version 1.0
 * @updated 21-Nov-2007 12:37:07
 */
public class ItemRO extends ReferenceObject implements Cloneable
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
    private static final long serialVersionUID = 1L;
    
    /**
     * The xlink:href prefix for an ItemRO
     */
    private final String PATH_FOR_XLINK_HREF = "/ir/item/";

    /**
     * The version number of the referenced item. This attribute is optional.
     */
    private int versionNumber;

    /**
     * The date of the last modification of the referenced item.
     */
    private Date modificationDate;

    /**
     * The message of the last action event of this item.
     */
    private String lastMessage;

    /**
     * The state of the item.
     */
    private ItemVO.State state;

    /**
     * The eSciDoc ID of the user that modified that version.
     */
    private AccountUserRO modifiedByRO;

    /**
     * The version PID of the item.
     */
    private String pid;

    /**
     * Creates a new instance.
     */
    public ItemRO()
    {
        super();
    }

    /**
     * Creates a new instance with the given objectId.
     * @param objectId The id of the object.
     */
    public ItemRO(String objectId)
    {
        super(objectId);
    }

    /**
     * Copy constructor.
     *
     * @author Thomas Diebaecker
     * @param other The instance to copy.
     */
    public ItemRO(ItemRO other)
    {
        super(other);
        this.versionNumber = other.versionNumber;
        this.lastMessage = other.lastMessage;
        this.state = other.state;
        this.modifiedByRO = other.modifiedByRO;
        this.modificationDate = other.modificationDate;
        this.pid = other.pid;
    }

    /**
     * {@inheritDoc}
     * @author Thomas Diebaecker
     */
    @Override
    public Object clone() throws CloneNotSupportedException
    {
        super.clone();
        return new ItemRO(this);
    }

    /**
     * Get the full identification of an item version.
     *
     * @return A String in the form objid:versionNumber e.g. "escidoc:345:2"
     */
    public String getObjectIdAndVersion()
    {
        if (versionNumber != 0)
        {
            return getObjectId() + ":" + versionNumber;
        }
        else
        {
            return getObjectId();
        }

    }

    /**
     * Set the full identification of an item version.
     *
     * @param idString A String in the form objid:versionNumber e.g. "escidoc:345:2"
     */
    public void setObjectIdAndVersion(String idString)
    {
        int ix = idString.lastIndexOf(":");
        if (ix == -1)
        {
            setObjectId(idString);
            versionNumber = 0;
        }
        else
        {
            setObjectId(idString.substring(0, ix));
            versionNumber = Integer.parseInt(idString.substring(ix + 1));
        }
    }

    /**
     * The version number of the referenced item. This attribute is optional.
     */
    public int getVersionNumber()
    {
        return versionNumber;
    }

    /**
     * The version number of the referenced item. This attribute is optional.
     *
     * @param newVal
     */
    public void setVersionNumber(int newVal)
    {
        versionNumber = newVal;
    }

    public Date getModificationDate()
    {
        return modificationDate;
    }

    public void setModificationDate(Date modificationDate)
    {
        this.modificationDate = modificationDate;
    }

    public String getLastMessage()
    {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage)
    {
        this.lastMessage = lastMessage;
    }

    /**
     * Delivers the state of the item.
     * 
     * @return The current State.
     */
    public ItemVO.State getState()
    {
        return state;
    }

    /**
     * Sets the state of the item.
     *
     * @param newVal The new state.
     */
    public void setState(ItemVO.State newVal)
    {
        state = newVal;
    }

    public AccountUserRO getModifiedByRO()
    {
        return modifiedByRO;
    }

    public void setModifiedByRO(AccountUserRO modifiedByRO)
    {
        this.modifiedByRO = modifiedByRO;
    }

    public String getPid()
    {
        return pid;
    }
    
    // remove "hdl:" if possible (needed for URLs including a handle-resolver)
    public String getPidWithoutPrefix()
    {
        if (pid.startsWith("hdl:"))
        {
            return pid.substring(4);
        }
        else 
        {
            return pid;
        }
    }

    public void setPid(String pid)
    {
        this.pid = pid;
    }

    @Override
    public boolean equals(Object object)
    {
        if (super.equals(object))
        {
            return (((ItemRO) object).versionNumber == this.versionNumber);
        }
        else
        {
            return false;
        }
    }

    public int getVersionNumberForXml()
    {
        if (versionNumber > 0)
        {
            return versionNumber;
        }
        else
        {
            return 1;
        }
    }
    
    public Date getModificationDateForXml()
    {
        if (modificationDate == null)
        {
            return new Date();
        }
        else
        {
            return modificationDate;
        }
    }
    
    public State getStateForXml()
    {
        if (state == null)
        {
            return State.PENDING;
        }
        else
        {
            return state;
        }
    }
    
    public AccountUserRO getModifiedByForXml()
    {
        if (modifiedByRO == null)
        {
            return new AccountUserRO();
        }
        else
        {
            return modifiedByRO;
        }
    }
    
    public String getLastMessageForXml()
    {
        if (lastMessage == null)
        {
            return "";
        }
        else
        {
            return lastMessage;
        }
    }

    @Override
    public void setObjectId(String objectId)
    {
        if (objectId != null && objectId.contains(":") && objectId.substring(objectId.indexOf(":") + 1).contains(":"))
        {
            super.setObjectId(objectId.substring(0, objectId.lastIndexOf(":")));
            setVersionNumber(Integer.parseInt(objectId.substring(objectId.lastIndexOf(":") + 1)));
        }
        else
        {
            super.setObjectId(objectId);
        }
    }
    
    public void setHref(String href)
    {
        if (href == null)
        {
            return;
        }
        if (href.contains("/"))
        {
            href = href.substring(href.lastIndexOf("/") + 1);
        }
        this.setObjectId(href);      
    }
    
    public String getHref()
    {
        if (this.getObjectId() != null)
        {
            return this.PATH_FOR_XLINK_HREF + this.getObjectId();
        }
        return null;
    }
    
}