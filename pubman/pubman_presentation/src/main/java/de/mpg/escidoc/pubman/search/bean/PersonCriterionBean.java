package de.mpg.escidoc.pubman.search.bean;

import java.util.ArrayList;

import de.mpg.escidoc.pubman.search.bean.criterion.Criterion;
import de.mpg.escidoc.pubman.search.bean.criterion.PersonCriterion;
import de.mpg.escidoc.services.common.valueobjects.metadata.CreatorVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.CreatorVO.CreatorRole;

/**
 * POJO bean to deal with one PersonCriterionVO.
 * 
 * @author Mario Wagner
 */
public class PersonCriterionBean extends CriterionBean
{
    public static final String BEAN_NAME = "PersonCriterionBean";
    
    private PersonCriterion personCriterionVO;
    
    // selection fields for the CreatorVO.CreatorRole enum
    private boolean searchAuthor, searchEditor, searchAdvisor, searchArtist, searchCommentator, searchContributor;
    private boolean searchIllustrator, searchPainter, searchPhotographer, searchTranscriber, searchTranslator, searchReferee, searchHonoree;
    private boolean searchInventor, searchApplicant;

    public PersonCriterionBean()
    {
        // ensure the parentVO is never null;
        this(new PersonCriterion());
    }

    public PersonCriterionBean(PersonCriterion personCriterionVO)
    {
        setPersonCriterionVO(personCriterionVO);
    }

    @Override
    public Criterion getCriterionVO()
    {
        return personCriterionVO;
    }

    public PersonCriterion getPersonCriterionVO()
    {
        return personCriterionVO;
    }

    public void setPersonCriterionVO(PersonCriterion personCriterionVO)
    {
        this.personCriterionVO = personCriterionVO;
        if (personCriterionVO.getCreatorRole() == null)
        {
            personCriterionVO.setCreatorRole(new ArrayList<CreatorRole>());
        }
            
        for (CreatorRole role : personCriterionVO.getCreatorRole())
        {
            if (CreatorVO.CreatorRole.ADVISOR.equals(role))
                searchAdvisor = true;
            else if (CreatorVO.CreatorRole.ARTIST.equals(role))
                searchArtist = true;
            else if (CreatorVO.CreatorRole.AUTHOR.equals(role))
                searchAuthor = true;
            else if (CreatorVO.CreatorRole.COMMENTATOR.equals(role))
                searchCommentator = true;
            else if (CreatorVO.CreatorRole.CONTRIBUTOR.equals(role))
                searchContributor = true;
            else if (CreatorVO.CreatorRole.EDITOR.equals(role))
                searchEditor = true;
            else if (CreatorVO.CreatorRole.ILLUSTRATOR.equals(role))
                searchIllustrator = true;
            else if (CreatorVO.CreatorRole.PAINTER.equals(role))
                searchPainter = true;
            else if (CreatorVO.CreatorRole.PHOTOGRAPHER.equals(role))
                searchPhotographer = true;
            else if (CreatorVO.CreatorRole.TRANSCRIBER.equals(role))
                searchTranscriber = true;
            else if (CreatorVO.CreatorRole.TRANSLATOR.equals(role))
                searchTranslator = true;
            else if (CreatorVO.CreatorRole.REFEREE.equals(role))
                searchReferee = true;
            else if (CreatorVO.CreatorRole.HONOREE.equals(role))
                searchHonoree= true;
            else if (CreatorVO.CreatorRole.INVENTOR.equals(role))
                searchInventor= true;
            else if (CreatorVO.CreatorRole.APPLICANT.equals(role))
                searchApplicant= true;
        }
    }
    
    /**
     * Action navigation call to select all CreatorVO.CreatorRole enums
     * @return null
     */
    public String selectAll()
    {
        setSearchAuthor(true);
        setSearchEditor(true);
        setSearchAdvisor(true);
        setSearchArtist(true);
        setSearchCommentator(true);
        setSearchContributor(true);
        setSearchIllustrator(true);
        setSearchPainter(true);
        setSearchPhotographer(true);
        setSearchTranscriber(true);
        setSearchTranslator(true);
        setSearchHonoree(true);
        setSearchReferee(true);

        // navigation refresh
        return null;
    }
    
    /**
     * Action navigation call to clear the current part of the form
     * @return null
     */
    public String clearCriterion()
    {
        setSearchAuthor(false);
        setSearchEditor(false);
        setSearchAdvisor(false);
        setSearchArtist(false);
        setSearchCommentator(false);
        setSearchContributor(false);
        setSearchIllustrator(false);
        setSearchPainter(false);
        setSearchPhotographer(false);
        setSearchTranscriber(false);
        setSearchTranslator(false);
        setSearchHonoree(false);
        setSearchReferee(false);

        personCriterionVO.getCreatorRole().clear();
        personCriterionVO.setSearchString("");
        
        // navigation refresh
        return null;
    }

    public boolean isSearchAdvisor()
    {
        return searchAdvisor;
    }

    public void setSearchAdvisor(boolean searchAdvisor)
    {
        this.searchAdvisor = searchAdvisor;
        if (searchAdvisor == true)
        {
            if (!personCriterionVO.getCreatorRole().contains(CreatorVO.CreatorRole.ADVISOR))
            {
                personCriterionVO.getCreatorRole().add(CreatorVO.CreatorRole.ADVISOR);
            }
        }
        else
        {
            personCriterionVO.getCreatorRole().remove(CreatorVO.CreatorRole.ADVISOR);
        }
    }

    public boolean isSearchArtist()
    {
        return searchArtist;
    }

    public void setSearchArtist(boolean searchArtist)
    {
        this.searchArtist = searchArtist;
        if (searchArtist == true)
        {
            if (!personCriterionVO.getCreatorRole().contains(CreatorVO.CreatorRole.ARTIST))
            {
                personCriterionVO.getCreatorRole().add(CreatorVO.CreatorRole.ARTIST);
            }
        }
        else
        {
            personCriterionVO.getCreatorRole().remove(CreatorVO.CreatorRole.ARTIST);
        }
    }

    public boolean isSearchAuthor()
    {
        return searchAuthor;
    }

    public void setSearchAuthor(boolean searchAuthor)
    {
        this.searchAuthor = searchAuthor;
        if (searchAuthor == true)
        {
            if (!personCriterionVO.getCreatorRole().contains(CreatorVO.CreatorRole.AUTHOR))
            {
                personCriterionVO.getCreatorRole().add(CreatorVO.CreatorRole.AUTHOR);
            }
        }
        else
        {
            personCriterionVO.getCreatorRole().remove(CreatorVO.CreatorRole.AUTHOR);
        }
    }

    public boolean isSearchCommentator()
    {
        return searchCommentator;
    }

    public void setSearchCommentator(boolean searchCommentator)
    {
        this.searchCommentator = searchCommentator;
        if (searchCommentator == true)
        {
            if (!personCriterionVO.getCreatorRole().contains(CreatorVO.CreatorRole.COMMENTATOR))
            {
                personCriterionVO.getCreatorRole().add(CreatorVO.CreatorRole.COMMENTATOR);
            }
        }
        else
        {
            personCriterionVO.getCreatorRole().remove(CreatorVO.CreatorRole.COMMENTATOR);
        }
    }

    public boolean isSearchContributor()
    {
        return searchContributor;
    }

    public void setSearchContributor(boolean searchContributor)
    {
        this.searchContributor = searchContributor;
        if (searchContributor == true)
        {
            if (!personCriterionVO.getCreatorRole().contains(CreatorVO.CreatorRole.CONTRIBUTOR))
            {
                personCriterionVO.getCreatorRole().add(CreatorVO.CreatorRole.CONTRIBUTOR);
            }
        }
        else
        {
            personCriterionVO.getCreatorRole().remove(CreatorVO.CreatorRole.CONTRIBUTOR);
        }
    }

    public boolean isSearchEditor()
    {
        return searchEditor;
    }

    public void setSearchEditor(boolean searchEditor)
    {
        this.searchEditor = searchEditor;
        if (searchEditor == true)
        {
            if (!personCriterionVO.getCreatorRole().contains(CreatorVO.CreatorRole.EDITOR))
            {
                personCriterionVO.getCreatorRole().add(CreatorVO.CreatorRole.EDITOR);
            }
        }
        else
        {
            personCriterionVO.getCreatorRole().remove(CreatorVO.CreatorRole.EDITOR);
        }
    }

    public boolean isSearchIllustrator()
    {
        return searchIllustrator;
    }

    public void setSearchIllustrator(boolean searchIllustrator)
    {
        this.searchIllustrator = searchIllustrator;
        if (searchIllustrator == true)
        {
            if (!personCriterionVO.getCreatorRole().contains(CreatorVO.CreatorRole.ILLUSTRATOR))
            {
                personCriterionVO.getCreatorRole().add(CreatorVO.CreatorRole.ILLUSTRATOR);
            }
        }
        else
        {
            personCriterionVO.getCreatorRole().remove(CreatorVO.CreatorRole.ILLUSTRATOR);
        }
    }

    public boolean isSearchPainter()
    {
        return searchPainter;
    }

    public void setSearchPainter(boolean searchPainter)
    {
        this.searchPainter = searchPainter;
        if (searchPainter == true)
        {
            if (!personCriterionVO.getCreatorRole().contains(CreatorVO.CreatorRole.PAINTER))
            {
                personCriterionVO.getCreatorRole().add(CreatorVO.CreatorRole.PAINTER);
            }
        }
        else
        {
            personCriterionVO.getCreatorRole().remove(CreatorVO.CreatorRole.PAINTER);
        }
    }

    public boolean isSearchPhotographer()
    {
        return searchPhotographer;
    }

    public void setSearchPhotographer(boolean searchPhotographer)
    {
        this.searchPhotographer = searchPhotographer;
        if (searchPhotographer == true)
        {
            if (!personCriterionVO.getCreatorRole().contains(CreatorVO.CreatorRole.PHOTOGRAPHER))
            {
                personCriterionVO.getCreatorRole().add(CreatorVO.CreatorRole.PHOTOGRAPHER);
            }
        }
        else
        {
            personCriterionVO.getCreatorRole().remove(CreatorVO.CreatorRole.PHOTOGRAPHER);
        }
    }

    public boolean isSearchTranscriber()
    {
        return searchTranscriber;
    }

    public void setSearchTranscriber(boolean searchTranscriber)
    {
        this.searchTranscriber = searchTranscriber;
        if (searchTranscriber == true)
        {
            if (!personCriterionVO.getCreatorRole().contains(CreatorVO.CreatorRole.TRANSCRIBER))
            {
                personCriterionVO.getCreatorRole().add(CreatorVO.CreatorRole.TRANSCRIBER);
            }
        }
        else
        {
            personCriterionVO.getCreatorRole().remove(CreatorVO.CreatorRole.TRANSCRIBER);
        }
    }

    public boolean isSearchTranslator()
    {
        return searchTranslator;
    }

    public void setSearchTranslator(boolean searchTranslator)
    {
        this.searchTranslator = searchTranslator;
        if (searchTranslator == true)
        {
            if (!personCriterionVO.getCreatorRole().contains(CreatorVO.CreatorRole.TRANSLATOR))
            {
                personCriterionVO.getCreatorRole().add(CreatorVO.CreatorRole.TRANSLATOR);
            }
        }
        else
        {
            personCriterionVO.getCreatorRole().remove(CreatorVO.CreatorRole.TRANSLATOR);
        }
    }
    
       
    public boolean isSearchReferee()
    {
        return searchReferee;
    }

    public void setSearchReferee(boolean searchReferee)
    {
        this.searchReferee = searchReferee;
        if (searchReferee == true)
        {
            if (!personCriterionVO.getCreatorRole().contains(CreatorVO.CreatorRole.REFEREE))
            {
                personCriterionVO.getCreatorRole().add(CreatorVO.CreatorRole.REFEREE);
            }
        }
        else
        {
            personCriterionVO.getCreatorRole().remove(CreatorVO.CreatorRole.REFEREE);
        }
    }

    public boolean isSearchHonoree()
    {
        return searchHonoree;
    }

    public void setSearchHonoree(boolean searchHonoree)
    {
        this.searchHonoree = searchHonoree;
        if (searchHonoree == true)
        {
            if (!personCriterionVO.getCreatorRole().contains(CreatorVO.CreatorRole.HONOREE))
            {
                personCriterionVO.getCreatorRole().add(CreatorVO.CreatorRole.HONOREE);
            }
        }
        else
        {
            personCriterionVO.getCreatorRole().remove(CreatorVO.CreatorRole.HONOREE);
        }
    }

    public void setSearchInventor(boolean searchInventor) {
        this.searchInventor = searchInventor;
        if (searchInventor)
        {
            if (!personCriterionVO.getCreatorRole().contains(CreatorVO.CreatorRole.INVENTOR))
            {
                personCriterionVO.getCreatorRole().add(CreatorVO.CreatorRole.INVENTOR);
            }
        }
        else
        {
            personCriterionVO.getCreatorRole().remove(CreatorVO.CreatorRole.INVENTOR);
        }
    }

    public boolean isSearchInventor() {
        return searchInventor;
    }

    public void setSearchApplicant(boolean searchApplicant) {
        this.searchApplicant = searchApplicant;
         if (searchApplicant)
            {
                if (!personCriterionVO.getCreatorRole().contains(CreatorVO.CreatorRole.APPLICANT))
                {
                    personCriterionVO.getCreatorRole().add(CreatorVO.CreatorRole.APPLICANT);
                }
            }
            else
            {
                personCriterionVO.getCreatorRole().remove(CreatorVO.CreatorRole.APPLICANT);
            }
    }

    public boolean isSearchApplicant() {
        return searchApplicant;
    }

}
