/*
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
 * Copyright 2006-2011 Fachinformationszentrum Karlsruhe Gesellschaft
 * für wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Förderung der Wissenschaft e.V.
 * All rights reserved. Use is subject to license terms.
 */

package de.mpg.escidoc.services.transformationLight.exceptions;
/**
 * Exceptions for data which could not be fetched from an import source.
 * 
 * @author kleinfe1
 */
public class TransformationNotSupportedException extends Exception
{
    private static final long serialVersionUID = 1L;

    /**
     * TransformationNotSupportedException.
     */
    public TransformationNotSupportedException()
    {
    }

    /**
     * TransformationNotSupportedException.
     * @param message
     */
    public TransformationNotSupportedException(String message)
    {
        super(message);
    }

    /**
     * TransformationNotSupportedException.
     * @param cause
     */
    public TransformationNotSupportedException(Throwable cause)
    {
        super(cause);
    }

    /**
     * TransformationNotSupportedException.
     * @param message
     * @param cause
     */
    public TransformationNotSupportedException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
