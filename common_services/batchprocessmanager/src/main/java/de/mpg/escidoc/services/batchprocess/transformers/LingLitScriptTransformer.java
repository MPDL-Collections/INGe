package de.mpg.escidoc.services.batchprocess.transformers;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;

import de.mpg.escidoc.services.batchprocess.BatchProcessReport.ReportEntryStatusType;
import de.mpg.escidoc.services.common.valueobjects.FileVO;
import de.mpg.escidoc.services.common.valueobjects.FileVO.Storage;
import de.mpg.escidoc.services.common.valueobjects.FileVO.Visibility;
import de.mpg.escidoc.services.common.valueobjects.intelligent.grants.Grant;
import de.mpg.escidoc.services.common.valueobjects.metadata.SourceVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.TextVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.IdentifierVO.IdType;
import de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO;
import de.mpg.escidoc.services.common.valueobjects.publication.MdsPublicationVO.DegreeType;
import de.mpg.escidoc.services.framework.AdminHelper;
import de.mpg.escidoc.services.framework.PropertyReader;

public class LingLitScriptTransformer extends Transformer<PubItemVO>
{
    private static Logger logger = Logger.getLogger(LingLitScriptTransformer.class);

    @Override
    public List<PubItemVO> transform(List<PubItemVO> list)
    {
        System.out.println("Number of items: " + list.size());
        for (PubItemVO item : list)
        {
            System.out.println("Transforming item " + item.getVersion().getObjectIdAndVersion() + "!");
            item = removeURI(item);
            item = removeEmptySubjects(item);
            item = removeBrokenIdentifiers(item);
            item = transformAlternativeTitle(item);
            item = transformTitle(item);
            item = transformDegree(item);
            item = assignUserGroup(item);
            item = transformLocators(item);
            item = transformFreeKeyWords(item);
            item = setIsoToLanguages(item);
            report.addEntry("Transform" + item.getVersion().getObjectId(), "Transform "
                    + item.getVersion().getObjectId(), ReportEntryStatusType.FINE);
        }
        return list;
    }

    private PubItemVO removeBrokenIdentifiers(PubItemVO item)
    {
        for (FileVO fileVO : item.getFiles())
        {
            if (fileVO.getDefaultMetadata().getIdentifiers() != null)
            {
                fileVO.getDefaultMetadata().getIdentifiers().clear();
            }
        }
        return item;
    }

    private PubItemVO removeEmptySubjects(PubItemVO item)
    {
        for (int i = item.getMetadata().getSubjects().size() - 1; i>= 0; i--)
        {
            if ("".equals(item.getMetadata().getSubjects().get(i).getValue()))
            {
                item.getMetadata().getSubjects().remove(i);
            }
            else if ("ISO639_3".equals(item.getMetadata().getSubjects().get(i).getType()))
            {
                try
                {
                    String newSubject = getConeLanguageNameByTitle(item.getMetadata().getSubjects().get(i).getValue());
                    item.getMetadata().getSubjects().get(i).setValue(newSubject);
                }
                catch (Exception e)
                {
                    throw new RuntimeException(e);
                }
            }
        }
        return item;
    }

    public PubItemVO transformAlternativeTitle(PubItemVO item)
    {
        for (SourceVO source : item.getMetadata().getSources())
        {
            String title = source.getTitle().getValue();
            if (!(title.equals("") || (title == null)))
            {
                if (title.indexOf(";") > 0)
                {
                    title = title.substring(0, source.getTitle().getValue().indexOf(";") - 1);
                    source.getTitle().setValue(title);
                    processingAlternativeTitles(item, title);
                    
                    String spareSpaces = "\n             ";
                    TextVO sTitle = source.getTitle();
                    sTitle.setValue(sTitle.getValue().replace(spareSpaces, ""));
                }
                else
                {
                    processingAlternativeTitles(item, title);
                }
            }
        }
        return item;
    }
    
    public PubItemVO transformTitle(PubItemVO item)
    {
        String spareSpaces = "\n             ";
        TextVO title = item.getMetadata().getTitle();
        title.setValue(title.getValue().replace(spareSpaces, ""));
        return item;
    }

    public PubItemVO transformFreeKeyWords(PubItemVO item)
    {
        String fkw = item.getMetadata().getFreeKeywords().getValue();
        List<String> list = new ArrayList<String>();
        item.getMetadata().getLanguages().addAll(list);
        String locClass = null;
        String locSubjectHeading = "";
        String[] fields = fkw.split(",");
        if (fields.length > 0)
        {
            for (int i = 0; i < fields.length; i++)
            {
                if (fields[i].matches("\\s*((ISO 639-3 ?: ?)|(ISO-Code 639 ?: ?)).+"))
                {
                    fields[i] = fields[i].replaceAll("\\s*(ISO 639-3 ?: ?)|(ISO-Code 639 ?: ?)", "");
                    String[] langs = fields[i].split("/");
                    for (int j = 0; j < langs.length; j++)
                    {
                        String lang = langs[j].trim();
                        if (lang.matches("\\b[a-z][a-z][a-z]\\b"))
                        {
                            try
                            {
                                TextVO newSubject = new TextVO(getConeLanguageName(lang), null, "ISO639_3");
                                if (!item.getMetadata().getSubjects().contains(newSubject))
                                {
                                    item.getMetadata().getSubjects().add(newSubject);
                                }
                            }
                            catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                }
                else if (fields[i].trim().matches("\\b[A-Z0-9.]+\\b(\\s.+)?"))
                {
                    if (locClass == null)
                    {
                        locClass = "LoC Class: ";
                    }
                    else
                    {
                        locClass += ", ";
                    }
                    locClass += fields[i];
                }
                else
                {
                    if (!"".equals(locSubjectHeading))
                    {
                        locSubjectHeading += ", ";
                    }
                    else
                    {
                        locSubjectHeading += "LoC Subject Headings: ";
                    }
                    locSubjectHeading += fields[i].trim();
                }
            }
        }
        else
        {
            logger.error("Error parsing freekeywords");
        }
        fkw = "";
        
        if ("LoC Subject Headings: ".equals(locSubjectHeading))
        {
            locSubjectHeading = "";
        }
        if (locClass != null)
        {
            fkw += locClass;
        }
        if (locClass != null && !"".equals(locClass) && !"".equals(locSubjectHeading))
        {
            fkw += ",\n";
        }
        fkw += locSubjectHeading;
        item.getMetadata().getFreeKeywords().setValue(fkw);
        return item;
    }

    public PubItemVO setIsoToLanguages(PubItemVO item)
    {
        List<String> languages = item.getMetadata().getLanguages();
        for (int i = 0; i < languages.size(); i++)
        {
            String currentIsoLangValue = languages.get(i);
            String lang = currentIsoLangValue;
            if (lang.length() != 3)
            {
                setIso639_3(languages, i, currentIsoLangValue);
            }
        }
        return item;
    }

    private void setIso639_3(List<String> languages, int i, String currentIsoLangValue)
    {
        if (currentIsoLangValue.equals("ar"))
        {
            languages.set(i, "ara");
        }
        else if (currentIsoLangValue.equals("az"))
        {
            languages.set(i, "aze");
        }
        else if (currentIsoLangValue.equals("bg"))
        {
            languages.set(i, "bul");
        }
        else if (currentIsoLangValue.equals("bi"))
        {
            languages.set(i, "bis");
        }
        else if (currentIsoLangValue.equals("bn"))
        {
            languages.set(i, "ben");
        }
        else if (currentIsoLangValue.equals("bo"))
        {
            languages.set(i, "bod");
        }
        else if (currentIsoLangValue.equals("de"))
        {
            languages.set(i, "deu");
        }
        else if (currentIsoLangValue.equals("en"))
        {
            languages.set(i, "eng");
        }
        else if (currentIsoLangValue.equals("es"))
        {
            languages.set(i, "spa");
        }
        else if (currentIsoLangValue.equals("fr"))
        {
            languages.set(i, "fra");
        }
        else if (currentIsoLangValue.equals("gn"))
        {
            languages.set(i, "grn");
        }
        else if (currentIsoLangValue.equals("he"))
        {
            languages.set(i, "heb");
        }
        else if (currentIsoLangValue.equals("zh"))
        {
            languages.set(i, "zho");
        }
        else if (currentIsoLangValue.equals("hi"))
        {
            languages.set(i, "hin");
        }
        else if (currentIsoLangValue.equals("id"))
        {
            languages.set(i, "ind");
        }
        else if (currentIsoLangValue.equals("it"))
        {
            languages.set(i, "ita");
        }
        else if (currentIsoLangValue.equals("ka"))
        {
            languages.set(i, "kat");
        }
        else if (currentIsoLangValue.equals("kk"))
        {
            languages.set(i, "kaz");
        }
        else if (currentIsoLangValue.equals("kn"))
        {
            languages.set(i, "kan");
        }
        else if (currentIsoLangValue.equals("ko"))
        {
            languages.set(i, "kor");
        }
        else if (currentIsoLangValue.equals("la"))
        {
            languages.set(i, "lat");
        }
        else if (currentIsoLangValue.equals("mn"))
        {
            languages.set(i, "mon");
        }
        else if (currentIsoLangValue.equals("my"))
        {
            languages.set(i, "mya");
        }
        else if (currentIsoLangValue.equals("nl"))
        {
            languages.set(i, "nld");
        }
        else if (currentIsoLangValue.equals("pt"))
        {
            languages.set(i, "por");
        }
        else if (currentIsoLangValue.equals("qu"))
        {
            languages.set(i, "que");
        }
        else if (currentIsoLangValue.equals("ro"))
        {
            languages.set(i, "ron");
        }
        else if (currentIsoLangValue.equals("ru"))
        {
            languages.set(i, "rus");
        }
        else if (currentIsoLangValue.equals("tr"))
        {
            languages.set(i, "tur");
        }
        else
        {
            if (!"".equals(currentIsoLangValue))
                logger.error("NON KNOWN LANAGUAGE CODE: " + currentIsoLangValue);
        }
    }

    public PubItemVO assignUserGroup(PubItemVO item)
    {
        for (FileVO f : item.getFiles())
        {
            if (Visibility.AUDIENCE.equals(f.getVisibility()))
            {
                Grant grant = new Grant();
                grant.setAssignedOn(f.getReference().getObjectId());
                grant.setGrantedTo("escidoc:175089");
                grant.setGrantType("user-group");
                grant.setRole(Grant.CoreserviceRole.AUDIENCE.getRoleId());
                try
                {
                     //SHOULD STAY OUT COMMENTED UNTIL ACTUAL EDIT
                     grant
                     .createInCoreservice(AdminHelper.loginUser("bibliothek_mpi_eva", "bibliothek"),
                     "Edit Linglit");
                }
                catch (Exception e)
                {
                    logger.warn(f.getReference().getObjectId() + " has already escidoc:175089 as user group");
                }
            }
        }
        return item;
    }

    private PubItemVO transformLocators(PubItemVO item)
    {
        for (int i = 0; i < item.getFiles().size(); i++)
        {
            if (Storage.EXTERNAL_URL.equals(item.getFiles().get(i).getStorage()))
            {
                item.getFiles().get(i).setName(item.getFiles().get(i).getContent());
                if (item.getFiles().get(i).getMetadataSets().size() > 0)
                {
                    item.getFiles().get(i).getMetadataSets().get(0).getTitle().setValue(item.getFiles().get(i).getContent());
                }
            }
        }
        return item;
    }

    private void processingAlternativeTitles(PubItemVO item, String title)
    {
        for (SourceVO source : item.getMetadata().getSources())
        {
            for (int i = 0; i < source.getAlternativeTitles().size(); i++)
            {
                String subtitle = source.getAlternativeTitles().get(i).getValue();
                if (!(subtitle.equals("") || subtitle == null))
                {
                    if (subtitle.equals(title))
                    {
                        source.getAlternativeTitles().remove(i);
                    }
                    else
                    {
                        String spareSpaces = "\n             ";
                        TextVO altTitle = source.getAlternativeTitles().get(i);
                        altTitle.setValue(altTitle.getValue().replace(spareSpaces, ""));
                    }
                }
            }
        }
    }

    public PubItemVO transformDegree(PubItemVO item)
    {
        if (DegreeType.MAGISTER.equals(item.getMetadata().getDegree()))
        {
            item.getMetadata().setDegree(DegreeType.MASTER);
        }
        return item;
    }

    public PubItemVO removeURI(PubItemVO item)
    {
        for (int i = 0; i < item.getMetadata().getIdentifiers().size(); i++)
        {
            if (IdType.URI.equals(item.getMetadata().getIdentifiers().get(i).getType()))
            {
                item.getMetadata().getIdentifiers().remove(i);
            }
        }
        return item;
    }

    @Override
    public CoreServiceObjectType getObjectType()
    {
        return CoreServiceObjectType.ITEM;
    }

    public static String getConeLanguageName(String code) throws Exception
    {
        if (code != null && !"".equals(code.trim()))
        {

            HttpClient client = new HttpClient();
            GetMethod getMethod = new GetMethod(PropertyReader.getProperty("escidoc.cone.service.url") + "iso639-3/query?dc:identifier=\"" + code + "\"&format=json&lang=en");
            getMethod.setRequestHeader("Connection", "close");
            client.executeMethod(getMethod);
            String response = getMethod.getResponseBodyAsString();
            Pattern pattern = Pattern.compile("\"value\" : \\[?\\s*\"(.+)\"");
            Matcher matcher = pattern.matcher(response);
            if (matcher.find())
            {
                return matcher.group(1);
            }
            else
            {
                return null;
            }
        }
        else
        {
            return null;
        }
    }

    public static String getConeLanguageNameByTitle(String title) throws Exception
    {
        if (title != null && !"".equals(title.trim()))
        {

            HttpClient client = new HttpClient();
            GetMethod getMethod = new GetMethod(PropertyReader.getProperty("escidoc.cone.service.url") + "iso639-3/query?dc:title=\"" + title + "\"&format=json&lang=en");
            client.executeMethod(getMethod);
            String response = getMethod.getResponseBodyAsString();
            Pattern pattern = Pattern.compile("\"value\" : \\[?\\s*\"(.+)\"");
            Matcher matcher = pattern.matcher(response);
            if (matcher.find())
            {
                return matcher.group(1);
            }
            else
            {
                return null;
            }
        }
        else
        {
            return null;
        }
    }

}