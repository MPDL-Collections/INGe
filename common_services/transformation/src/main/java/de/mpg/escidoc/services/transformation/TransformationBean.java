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

package de.mpg.escidoc.services.transformation;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.log4j.Logger;

import de.mpg.escidoc.services.transformation.exceptions.TransformationNotSupportedException;
import de.mpg.escidoc.services.transformation.valueObjects.Format;

/**
 * Implementation of the Transformation Service.
 *
 * @author Friederike Kleinfercher (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */

public class TransformationBean implements Transformation, Configurable
{
    
    private final Logger logger = Logger.getLogger(TransformationBean.class);    
    private static TransformationInitializer initializer = null;
    private Class transformationClass = null;
 
    /**
     * Public constructor.
     */
    public TransformationBean()
    {
        if (initializer == null)
        {
            initializer = new TransformationInitializer();
            //Always use local
            initializer.initializeTransformationModules(true);
        }
    }
    
    public TransformationBean(boolean local)
    {
        if (initializer == null)
        {
            initializer = new TransformationInitializer();
            initializer.initializeTransformationModules(local);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public Format[] getSourceFormats() throws RuntimeException
    {
        Format[] allSourceFormats = null;
        String thisMethodName = "getSourceFormats";
        allSourceFormats = this.callMethodOnTransformationModules(thisMethodName, null);
        
        return allSourceFormats;
    }

    /**
     * {@inheritDoc}
     */
    public String getSourceFormatsAsXml() throws RuntimeException
    {
        Format[] allFormats = this.getSourceFormats();       
        return Util.createFormatsXml(allFormats);
    }

    /**
     * {@inheritDoc}
     */
    public String getTargetFormatsAsXml(String srcFormatName, String srcType, String srcEncoding)
        throws RuntimeException
    {
        Format[] allFormats = this.getTargetFormats(new Format(srcFormatName, srcType, srcEncoding));
        return Util.createFormatsXml(allFormats);
    }

    /**
     * {@inheritDoc}
     */
    public Format[] getTargetFormats(Format src) throws RuntimeException
    {
        Format[] allTargetFormats = null;
        String thisMethodName = "getTargetFormats";
        
        //Normalize mimetype to avoid that e.g. application/xml and text/xml need two different transformations
        src.setType(Util.normalizeMimeType(src.getType()));
        allTargetFormats = this.callMethodOnTransformationModules(thisMethodName, src);
        
        return allTargetFormats;
    }
    
    /**
     * {@inheritDoc}
     */
    public Format[] getSourceFormats(Format trg) throws RuntimeException
    {
        //Normalize mimetype to avoid that e.g. application/xml and text/xml need two different transformations
        trg.setType(Util.normalizeMimeType(trg.getType()));
        
        Format[] allSourceFormats = null;
        String thisMethodName = "getSourceFormats";
        allSourceFormats = this.callMethodOnTransformationModules(thisMethodName, trg);
        
        return allSourceFormats;
    }

    /**
     * {@inheritDoc}
     * @throws TransformationNotSupportedException 
     */
    public byte[] transform(byte[] src, String srcFormatName, String srcType, String srcEncoding, String trgFormatName,
        String trgType, String trgEncoding, String service) 
        throws TransformationNotSupportedException, RuntimeException
    {
        Format source = new Format(srcFormatName, srcType, srcEncoding);
        Format target = new Format(trgFormatName, trgType, trgEncoding);       
        return this.transform(src, source, target, service);
    }

    /**
     * {@inheritDoc}
     * @throws TransformationNotSupportedException 
     */
    public byte[] transform(byte[] src, Format srcFormat, Format trgFormat, String service) 
        throws TransformationNotSupportedException, RuntimeException
    {
        //Normalize mimetype to avoid that e.g. application/xml and text/xml need two different transformations
        srcFormat.setType(Util.normalizeMimeType(srcFormat.getType()));
        
        if (service.toLowerCase().equals("escidoc"))
        {
            return this.escidocTransformService(src, srcFormat, trgFormat, service);
        }
        return null;
    }
    
    private byte[] escidocTransformService(byte[] src, Format srcFormat, Format trgFormat, String service) 
        throws TransformationNotSupportedException, RuntimeException
    {
        transformationClass = this.getTransformationClassForTransformation(srcFormat, trgFormat);
        byte[] result = null;
        String methodName = "transform";
        
        
        
        if (transformationClass == null)
        {
            this.logger.warn("Transformation not supported: \n" + srcFormat.getName() + ", " + srcFormat.getType() 
                    + ", " + srcFormat.getEncoding() + "\n" + trgFormat.getName() + ", " + trgFormat.getType() 
                    + ", " + trgFormat.getEncoding());
            throw new TransformationNotSupportedException();
        }
        else 
        {
            try
            {
                //Instanciate the class
                ClassLoader cl = this.getClass().getClassLoader();
                transformationClass = cl.loadClass(transformationClass.getName());
    
                //Set methods parameters
                Class[] parameterTypes = new Class[]{byte[].class, Format.class, Format.class, String.class };
                
                //Call the method
                Method method = transformationClass.getMethod(methodName, parameterTypes);

                //Execute the method
                result = (byte[]) method.invoke(transformationClass.newInstance(), src, srcFormat, trgFormat, service);
            }
            catch (Exception e)
            {
                throw new RuntimeException(e);
            }
        }
        
        return result;
    }
    
   
    
    private Format[] callMethodOnTransformationModules(String methodName, Format param) 
        throws RuntimeException
    {
        Vector<Format[]> allFormats = new Vector<Format[]>();
        Format[] formats = null;
        
        for (Class<?> transformationClass : initializer.getTransformationClasses())
        {
            try
            {
                //Instanciate the class
                //Class transformationClass = (Class) this.initializer.getTransformationClasses().get(i);
                ClassLoader cl = this.getClass().getClassLoader();
                transformationClass = cl.loadClass(transformationClass.getName());
  
                if (param == null)
                {
                    //Call the method
                    Method method = transformationClass.getMethod(methodName, null);

                    //Execute the method
                    formats = (Format[]) method.invoke(transformationClass.newInstance(), null);
                }
                else
                {
                    //Set methods parameters
                    Class[] parameterTypes = new Class[]{Format.class }; 
                    
                    //Call the method
                    Method method = transformationClass.getMethod(methodName, parameterTypes);
                    
                    //Execute the method
                    formats = (Format[]) method.invoke(transformationClass.newInstance(), param);
                }
                allFormats.add(formats);
            } 
            catch (Exception e)
            {
                this.logger.error("An error occurred during the allocation of transformation classes.", e);
                throw new RuntimeException(e);
            }
        }
        
        return Util.mergeFormats(allFormats);    
    }
    
    private Class getTransformationClassForTransformation(Format source, Format target) 
        throws RuntimeException
    {
        
        Format[] targets;
        String methodName = "getTargetFormats";
        
        for (Class<?> transformationClass : initializer.getTransformationClasses())
        {
            try
            {
                //Instanciate the class
                //transformationClass = (Class) this.initializer.getTransformationClasses().get(i);
                ClassLoader cl = this.getClass().getClassLoader();
                transformationClass = cl.loadClass(transformationClass.getName());
                
    
                //Set methods parameters
                Class[] parameterTypes = new Class[]{Format.class };
                
                //Call the method
                Method method = transformationClass.getMethod(methodName, parameterTypes);
    
                //Execute the method
                targets = (Format[]) method.invoke(transformationClass.newInstance(), source);
                
                if (Util.containsFormat(targets, target))
                {
                    return transformationClass;
                }
            }
            catch (Exception e)
            {
                this.logger.error("An error occurred during the allocation of transformation classes.", e);
                throw new RuntimeException(e);
            }
        }
        
        return null;
    }

    /**
     * Checks if a format can be transformed into another one.
     * @param from
     * @param to
     * @return true if 'from' can be transformed into 'to', else false
     */
    public boolean checkTransformation(Format from, Format to)
    {
        boolean check = false;
        
        Format[] targetArr = this.getTargetFormats(from);
        for (int i = 0; i < targetArr.length; i++)
        {
            Format target = targetArr[i];
            if (Util.isFormatEqual(target, to))
            {
                return true;
            }
        }
        
        return check;
    }

    public byte[] transform(byte[] src, Format srcFormat, Format trgFormat, String service,
            Map<String, String> configuration) throws TransformationNotSupportedException, RuntimeException
    {
        //Normalize mimetype to avoid that e.g. application/xml and text/xml need two different transformations
        srcFormat.setType(Util.normalizeMimeType(srcFormat.getType()));
        
        if (service.toLowerCase().equals("escidoc"))
        {
            return this.escidocTransformService(src, srcFormat, trgFormat, service, configuration);
        }
        return null;
    }

    private byte[] escidocTransformService(byte[] src, Format srcFormat, Format trgFormat, String service,
            Map<String, String> configuration) throws TransformationNotSupportedException, RuntimeException
    {
        transformationClass = this.getTransformationClassForTransformation(srcFormat, trgFormat);
        byte[] result = null;
        String methodName = "transform";
        
        
        
        if (transformationClass == null)
        {
            this.logger.warn("Transformation not supported: \n" + srcFormat.getName() + ", " + srcFormat.getType() 
                    + ", " + srcFormat.getEncoding() + "\n" + trgFormat.getName() + ", " + trgFormat.getType() 
                    + ", " + trgFormat.getEncoding());
            throw new TransformationNotSupportedException();
        }
        else 
        {
            try
            {
                //Instanciate the class
                ClassLoader cl = this.getClass().getClassLoader();
                transformationClass = cl.loadClass(transformationClass.getName());
    
                Transformation transformation = (Transformation) transformationClass.newInstance();
                if (transformation instanceof Configurable) 
                {
                    
                    //Set methods parameters
                    Class[] parameterTypes = new Class[]{byte[].class, Format.class, Format.class, String.class, Map.class };
                    
                    //Call the method
                    Method method = transformationClass.getMethod(methodName, parameterTypes);
    
                    //Execute the method
                    result = (byte[]) method.invoke(transformation, src, srcFormat, trgFormat, service, configuration);
                }
                else
                {
                    return transform(src, srcFormat, trgFormat, service);
                }
            }
            catch (Exception e)
            {
                throw new RuntimeException(e);
            }
        }
        
        return result;

    }

    public Map<String, String> getConfiguration(Format srcFormat, Format trgFormat) throws Exception
    {
        transformationClass = this.getTransformationClassForTransformation(srcFormat, trgFormat);
        
        if (transformationClass != null)
        {
            Transformation transformation = (Transformation) transformationClass.newInstance();
            if (transformation instanceof Configurable)
            {
                //Set methods parameters
                Class[] parameterTypes = new Class[]{Format.class, Format.class};
                
                //Call the method
                Method method = transformationClass.getMethod("getConfiguration", parameterTypes);
        
                //Execute the method
                return (Map<String, String>) method.invoke(transformationClass.newInstance(), srcFormat, trgFormat);
            }
            else
            {
                return null;
            }
        }
        return null;
    }

    public List<String> getConfigurationValues(Format srcFormat, Format trgFormat, String key) throws Exception
    {
        transformationClass = this.getTransformationClassForTransformation(srcFormat, trgFormat);
        Transformation transformation = (Transformation) transformationClass.newInstance();
        if (transformation instanceof Configurable)
        {
            //Set methods parameters
            Class[] parameterTypes = new Class[]{Format.class, Format.class, String.class};
              
            //Call the method
            Method method = transformationClass.getMethod("getConfigurationValues", parameterTypes);
            
            //Execute the method
            return (List<String>) method.invoke(transformationClass.newInstance(), srcFormat, trgFormat, key);
        }
        else
        {
            return null;
        }
    }

    
    
}
