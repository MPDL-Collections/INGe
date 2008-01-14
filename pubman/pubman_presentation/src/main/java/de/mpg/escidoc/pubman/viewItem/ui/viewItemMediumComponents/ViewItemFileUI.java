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
* Copyright 2006-2007 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/ 

package de.mpg.escidoc.pubman.viewItem.ui.viewItemMediumComponents;

import java.util.ResourceBundle;
import javax.faces.component.html.HtmlGraphicImage;
import javax.faces.component.html.HtmlPanelGroup;
import javax.faces.context.FacesContext;
import de.mpg.escidoc.pubman.ui.HTMLElementUI;
import de.mpg.escidoc.pubman.util.CommonUtils;
import de.mpg.escidoc.pubman.util.InternationalizationHelper;
import de.mpg.escidoc.services.common.referenceobjects.PubFileRO;
import de.mpg.escidoc.services.common.valueobjects.PubItemResultVO;
import de.mpg.escidoc.services.common.valueobjects.PubItemVO;
import de.mpg.escidoc.services.common.valueobjects.SearchHitVO.SearchHitType;

/**
 * UI for creating the files section of a pubitem to be used in the ViewItemMediumUI.
 * 
 * @author: Tobias Schraut, created 27.09.2007
 * @version: $Revision: 1618 $ $LastChangedDate: 2007-11-27 15:54:47 +0100 (Tue, 27 Nov 2007) $
 */
public class ViewItemFileUI extends HtmlPanelGroup
{
    private PubItemVO pubItem;
    private HtmlGraphicImage image;
    private HTMLElementUI htmlElement = new HTMLElementUI();
    
    // get the selected language...
    private InternationalizationHelper i18nHelper = (InternationalizationHelper)FacesContext.getCurrentInstance()
            .getApplication().getVariableResolver().resolveVariable(FacesContext.getCurrentInstance(),
                    InternationalizationHelper.BEAN_NAME);
    // ... and set the refering resource bundle
    private ResourceBundle bundleLabel = ResourceBundle.getBundle(i18nHelper.getSelectedLableBundle());
    
    /**
     * Public constructor.
     */
    public ViewItemFileUI(PubItemVO pubItemVO)
    {
        initialize(pubItemVO);
    }
    
    /**
     * Initializes the UI and sets all attributes of the GUI components.
     * 
     * @param pubItemVO a pubitem
     */
    protected void initialize(PubItemVO pubItemVO)
    {
        this.pubItem = pubItemVO;
        
        // get the selected language...
        this.i18nHelper = (InternationalizationHelper)FacesContext.getCurrentInstance()
                .getApplication().getVariableResolver().resolveVariable(FacesContext.getCurrentInstance(),
                        InternationalizationHelper.BEAN_NAME);
        // ... and set the refering resource bundle
        this.bundleLabel = ResourceBundle.getBundle(i18nHelper.getSelectedLableBundle());
        
        this.getChildren().clear();
        this.setId(CommonUtils.createUniqueId(this));
        
        // *** HEADER ***
        // add an image to the page
        this.getChildren().add(htmlElement.getStartTag("h2"));
        this.image = new HtmlGraphicImage();
        this.image.setId(CommonUtils.createUniqueId(this.image));
        this.image.setUrl("./images/upload_queue_icon.png");
        this.image.setWidth("21");
        this.image.setHeight("25");
        this.getChildren().add(this.image);
        
        // add the subheader
        this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(bundleLabel.getString("ViewItemMedium_lblSubHeaderFile")));
        this.getChildren().add(htmlElement.getEndTag("h2"));
        
        if(this.pubItem.getFiles() != null)
        {
            for(int i = 0; i < this.pubItem.getFiles().size(); i++)
            {
                // *** FILE NAME ***
                // label
                this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemTitle odd"));
                
                this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(bundleLabel.getString("ViewItemMedium_lblFileName")));
                
                this.getChildren().add(htmlElement.getEndTag("div"));
                
                // value
                this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemText odd"));
                
                if(this.pubItem.getFiles().get(i).getName() != null)
                {
                    this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(this.pubItem.getFiles().get(i).getName()));
                }
                else
                {
                    this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(""));
                }
                
                this.getChildren().add(htmlElement.getEndTag("div"));
                
                
                // *** FILE SIZE / MIME TYPE ***
                // label
                this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemTitle"));
                
                this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(bundleLabel.getString("ViewItemMedium_lblFileMimeTypeSize")));
                
                this.getChildren().add(htmlElement.getEndTag("div"));
                
                // value
                this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemText"));
                
                this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(this.pubItem.getFiles().get(i).getMimeType() 
                                        + " / " + this.pubItem.getFiles().get(i).getSize() 
                                        + bundleLabel.getString("ViewItemMedium_lblFileSizeKB")));
                
                this.getChildren().add(htmlElement.getEndTag("div"));
                
                
                // *** CONTENT CATEGORY ***
                // label
                this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemTitle odd"));
                
                this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(bundleLabel.getString("ViewItemMedium_lblFileCategory")));
                
                this.getChildren().add(htmlElement.getEndTag("div"));
                
                // value
                this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemText odd"));
                
                this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(this.pubItem.getFiles().get(i).getContentTypeString()));
                
                this.getChildren().add(htmlElement.getEndTag("div"));
                
                
                // *** DESCRIPTION ***
                // label
                this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemTitle"));
                
                this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(bundleLabel.getString("ViewItemMedium_lblFileDescription")));
                
                this.getChildren().add(htmlElement.getEndTag("div"));
                
                // value
                this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemText"));
                
                this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(this.pubItem.getFiles().get(i).getDescription()));
                
                this.getChildren().add(htmlElement.getEndTag("div"));
                
                
                // *** VISIBILITY ***
                // label
                this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemTitle odd"));
                
                this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(bundleLabel.getString("ViewItemMedium_lblFileVisibility")));
                
                this.getChildren().add(htmlElement.getEndTag("div"));
                
                // value
                this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemText odd"));
                
                this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(this.pubItem.getFiles().get(i).getVisibilityString()));
                
                this.getChildren().add(htmlElement.getEndTag("div"));
                
                //*** SEARCH HITS (IF POSSIBLE) ***
                if(this.pubItem instanceof PubItemResultVO && this.pubItem.getState() != null && !this.pubItem.getState().equals(PubItemVO.State.WITHDRAWN))
                {
                    PubItemResultVO resultItem = (PubItemResultVO)this.pubItem;
                    // label
                    this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemTitle"));
                    
                    this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(bundleLabel.getString("ViewItemFull_lblFileFullTxtSearchHits")));
                    
                    this.getChildren().add(htmlElement.getEndTag("div"));
                    
                    // value
                    addSearchResultHitsToPage(resultItem, this.pubItem.getFiles().get(i).getReference());
                }
                
                // add some empty rows
                this.getChildren().add(htmlElement.getStartTag("br"));
                this.getChildren().add(htmlElement.getStartTag("br"));
                this.getChildren().add(htmlElement.getStartTag("br"));
                this.getChildren().add(htmlElement.getStartTag("br"));
            }
        }
        
    }
    
    /**
     * Adds the search result hits to the page
     * @param resultItem the search result item
     * @param fileRO the reference of the file where the full text was found
     */
    private void addSearchResultHitsToPage(PubItemResultVO resultItem, PubFileRO fileRO)
    {
        // browse through the list of files and examine which of the files is the one the search result hits where found in
        for(int i = 0; i < resultItem.getSearchHitList().size(); i++)
        {
            if(resultItem.getSearchHitList().get(i).getType() == SearchHitType.FULLTEXT)
            {  
                if(resultItem.getSearchHitList().get(i).getHitReference() != null)
                {
                    if(resultItem.getSearchHitList().get(i).getHitReference().equals(fileRO))
                    {
                        for(int j = 0; j < resultItem.getSearchHitList().get(i).getTextFragmentList().size(); j++)
                        {
                            int startPosition = 0;
                            int endPosition = 0;
                            
                            startPosition = resultItem.getSearchHitList().get(i).getTextFragmentList().get(j).getHitwordList().get(0).getStartIndex();
                            endPosition = resultItem.getSearchHitList().get(i).getTextFragmentList().get(j).getHitwordList().get(0).getEndIndex() + 1;
                            
                            // value
                            this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemText"));
                            
                            this.getChildren().add(htmlElement.getStartTagWithStyleClass("span", "item"));
                            this.getChildren().add(htmlElement.getStartTag("p"));
                            this.getChildren().add(htmlElement.getStartTag("samp"));
                            this.getChildren().add(CommonUtils.getTextElementConsideringEmpty("..." + resultItem.getSearchHitList().get(i).getTextFragmentList().get(j).getData().substring(0, startPosition)));
                            this.getChildren().add(htmlElement.getStartTag("em"));
                            this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(resultItem.getSearchHitList().get(i).getTextFragmentList().get(j).getData().substring(startPosition, endPosition)));
                            this.getChildren().add(htmlElement.getEndTag("em"));
                            this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(resultItem.getSearchHitList().get(i).getTextFragmentList().get(j).getData().substring(endPosition) + "..."));
                            this.getChildren().add(htmlElement.getEndTag("samp"));
                            this.getChildren().add(htmlElement.getEndTag("p"));
                            this.getChildren().add(htmlElement.getEndTag("span"));
                            
                            this.getChildren().add(htmlElement.getEndTag("div"));
                        }
                    }
                }
                
            }
        }
    }
}
