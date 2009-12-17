package org.fao.oa.ingestion.faodoc;

import java.util.ArrayList;

import org.fao.oa.ingestion.uris.FaoUris;
import org.fao.oa.ingestion.uris.FaoUris.URI_TYPE;

import noNamespace.ITEMType;
import noNamespace.FAOConferenceDocument.FAOConference;

public class ConferenceName
{
    public ConferenceName()
    {
        
    }
    
    public String[] getEnglish(ITEMType faodoc, String name)
    {
        String label_en = null;
        String href = null;
        FaoUris uris = new FaoUris();
        ArrayList<Object> conferences = uris.getUriList(URI_TYPE.CONFERENCES);
        
        for (Object conf : conferences)
        {
            ArrayList<String> alternatives = new ArrayList<String>();
            if (((FAOConference)conf).getLABELEN() != null)
            {
                alternatives.add(((FAOConference)conf).getLABELEN());
            }
            if (((FAOConference)conf).getAlternativeEN1() != null)
            {
                alternatives.add(((FAOConference)conf).getAlternativeEN1());
            }
            if (((FAOConference)conf).getAlternativeEN2() != null)
            {
                alternatives.add(((FAOConference)conf).getAlternativeEN2());
            }
            if (alternatives.contains(name))
            {
                label_en = ((FAOConference)conf).getLABELEN();
                href = ((FAOConference)conf).getID();
            }
        }
        if (label_en != null && href != null)
        {
            String confName = conferenceName(faodoc, label_en);
            return new String[] {confName, href};
        }
        return null;
    }
    
    public String[] getFrench(ITEMType faodoc, String name)
    {
        String label_fr = null;
        String href = null;
        FaoUris uris = new FaoUris();
        ArrayList<Object> conferences = uris.getUriList(URI_TYPE.CONFERENCES);
        
        for (Object conf : conferences)
        {
            ArrayList<String> alternatives = new ArrayList<String>();
            if (((FAOConference)conf).getLABELFR() != null)
            {
                alternatives.add(((FAOConference)conf).getLABELFR());
            }
            if (((FAOConference)conf).getAlternativeFR1() != null)
            {
                alternatives.add(((FAOConference)conf).getAlternativeFR1());
            }
            if (((FAOConference)conf).getAlternativeFR2() != null)
            {
                alternatives.add(((FAOConference)conf).getAlternativeFR2());
            }
            if (((FAOConference)conf).getAlternativeFR3() != null)
            {
                alternatives.add(((FAOConference)conf).getAlternativeFR3());
            }
            if (alternatives.contains(name))
            {
                label_fr = ((FAOConference)conf).getLABELFR();
                href = ((FAOConference)conf).getID();
            }
        }
        if (label_fr != null && href != null)
        {
            String confName = conferenceName(faodoc, label_fr);
            return new String[] {confName, href};
        }
        return null;
    }
    
    public String[] getOther(ITEMType faodoc, String name)
    {
        String label = null;
        String href = null;
        FaoUris uris = new FaoUris();
        ArrayList<Object> conferences = uris.getUriList(URI_TYPE.CONFERENCES);
        
        for (Object conf : conferences)
        {
            if (((FAOConference)conf).getLABELRU() != null)
            {
                if (name.equalsIgnoreCase(((FAOConference)conf).getLABELRU()) || name.equalsIgnoreCase(((FAOConference)conf).getAlternativeRU()))
                {
                    label = ((FAOConference)conf).getLABELRU();
                    href = ((FAOConference)conf).getID();
                }
            }
            if (((FAOConference)conf).getLABELAR() != null)
            {
                if (name.equalsIgnoreCase(((FAOConference)conf).getLABELAR()))
                {
                    label = ((FAOConference)conf).getLABELAR();
                    href = ((FAOConference)conf).getID();
                }
            }
            if (((FAOConference)conf).getLABELIT() != null)
            {
                if (name.equalsIgnoreCase(((FAOConference)conf).getLABELIT()))
                {
                    label = ((FAOConference)conf).getLABELIT();
                    href = ((FAOConference)conf).getID();
                }
            }
            if (((FAOConference)conf).getLABELZH() != null)
            {
                if (name.equalsIgnoreCase(((FAOConference)conf).getLABELZH()))
                {
                    label = ((FAOConference)conf).getLABELZH();
                    href = ((FAOConference)conf).getID();
                }
            }
            if (((FAOConference)conf).getLABELPT() != null)
            {
                if (name.equalsIgnoreCase(((FAOConference)conf).getLABELPT()))
                {
                    label = ((FAOConference)conf).getLABELPT();
                    href = ((FAOConference)conf).getID();
                }
            }
            if (((FAOConference)conf).getLABELDE() != null)
            {
                if (name.equalsIgnoreCase(((FAOConference)conf).getLABELDE()))
                {
                    label = ((FAOConference)conf).getLABELDE();
                    href = ((FAOConference)conf).getID();
                }
            }
            if (((FAOConference)conf).getLABELTR() != null)
            {
                if (name.equalsIgnoreCase(((FAOConference)conf).getLABELTR()))
                {
                    label = ((FAOConference)conf).getLABELTR();
                    href = ((FAOConference)conf).getID();
                }
            }
            if (((FAOConference)conf).getLABELID() != null)
            {
                if (name.equalsIgnoreCase(((FAOConference)conf).getLABELID()))
                {
                    label = ((FAOConference)conf).getLABELID();
                    href = ((FAOConference)conf).getID();
                }
            }
        }
        if (label != null && href != null)
        {
            String confName = conferenceName(faodoc, label);
            return new String[] {confName, href};
        }
        return null;
    }
    
    public String[] getSpanish(ITEMType faodoc, String name)
    {
        String label_es = null;
        String href = null;
        FaoUris uris = new FaoUris();
        ArrayList<Object> conferences = uris.getUriList(URI_TYPE.CONFERENCES);
        
        for (Object conf : conferences)
        {
            ArrayList<String> alternatives = new ArrayList<String>();
            if (((FAOConference)conf).getLABELES() != null)
            {
                alternatives.add(((FAOConference)conf).getLABELES());
            }
            if (((FAOConference)conf).getAlternativeES1() != null)
            {
                alternatives.add(((FAOConference)conf).getAlternativeES1());
            }
            if (((FAOConference)conf).getAlternativeES2() != null)
            {
                alternatives.add(((FAOConference)conf).getAlternativeES2());
            }
            if (((FAOConference)conf).getAlternativeES3() != null)
            {
                alternatives.add(((FAOConference)conf).getAlternativeES3());
            }
            if (((FAOConference)conf).getAlternativeES4() != null)
            {
                alternatives.add(((FAOConference)conf).getAlternativeES4());
            }
            if (alternatives.contains(name))
            {
                label_es = ((FAOConference)conf).getLABELES();
                href = ((FAOConference)conf).getID();
            }
        }
        if (label_es != null && href != null)
        {
            String confName = conferenceName(faodoc, label_es);
            return new String[] {confName, href};
        }
        return null;
    }
    
    public String conferenceName(ITEMType item, String name)
    {
        StringBuilder sb = new StringBuilder(name);
        sb.append(" (");
        if (item.sizeOfCONFNOArray() > 0)
        {
            sb.append(item.getCONFNOArray(0) + ": ");
        }
        if (item.sizeOfCONFDATEArray() > 0)
        {
            sb.append(item.getCONFDATEArray(0) + " : ");
        }
        if (item.sizeOfCONFPLACEArray() > 0)
        {
            String confCity = null;
            String confState = null;
            String confCountry = null;
            String confPlace = item.getCONFPLACEArray(0);
            if (!confPlace.contains(","))
            {
                confCity = confPlace.split("\\s\\(")[0];
                confState = "";
                confCountry = confPlace.substring(confPlace.indexOf("("), confPlace.indexOf(")") + 1);
            }
            else
            {
                confCity = confPlace.split(",")[0];
                confState = confPlace.split(",")[1].substring(1, confPlace.split(",")[1].indexOf("("));
                confCountry = confPlace.split(",")[1].substring(confPlace.split(",")[1].indexOf("("), confPlace.split(",")[1].indexOf(")") + 1);
            }
            if (confState != "")
            {
                sb.append(confCity + " (" + confState + ") " + confCountry);
            }
            else
            {
                sb.append(confCity + " " + confCountry);
            }
        }
        
        return sb.toString();
    }
}
