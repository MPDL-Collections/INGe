package de.mpg.escidoc.services.batchprocess.elements;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import de.mpg.escidoc.services.batchprocess.BatchProcess.CoreServiceObjectType;
import de.mpg.escidoc.services.batchprocess.helper.CommandHelper;

public abstract class Elements<ListElementType>
{
    protected List<ListElementType> elements = new ArrayList<ListElementType>();
    protected int maximumNumberOfElements = 50;

    public Elements(String[] args)
    {
        String max = CommandHelper.getArgument("-n", args, false);
        if (max != null)
        {
            this.maximumNumberOfElements = Integer.parseInt(max);
        }
        retrieveElements();
    }

    public static Elements<?> getBatchProcessList(String[] args)
    {
        try
        {
            Constructor c = Class.forName(CommandHelper.getArgument("-e", args, true)).getConstructor(
                    new Class[] { String[].class });
            return (Elements<?>)c.newInstance(new Object[] { args });
        }
        catch (Exception e)
        {
            throw new RuntimeException(CommandHelper.getArgument("-e", args, true) + " is not a valid Element name", e);
        }
    }

    public List<ListElementType> getElements()
    {
        return elements;
    }

    public void setElements(List<ListElementType> elements)
    {
        this.elements = elements;
    }

    public abstract void retrieveElements();

    public abstract CoreServiceObjectType getObjectType();
}
