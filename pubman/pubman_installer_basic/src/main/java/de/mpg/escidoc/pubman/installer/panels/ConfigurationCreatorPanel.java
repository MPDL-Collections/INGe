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

package de.mpg.escidoc.pubman.installer.panels;

import java.awt.LayoutManager2;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.xml.rpc.ServiceException;

import org.apache.log4j.Logger;

import com.izforge.izpack.Pack;
import com.izforge.izpack.gui.IzPanelLayout;
import com.izforge.izpack.gui.LabelFactory;
import com.izforge.izpack.installer.InstallData;
import com.izforge.izpack.installer.InstallerFrame;

import de.mpg.escidoc.pubman.installer.ConeDataset;
import de.mpg.escidoc.pubman.installer.ConeInsertProcess;
import de.mpg.escidoc.pubman.installer.StartEscidocProcess;
import de.mpg.escidoc.pubman.installer.UpdatePubmanConfigurationProcess;
import de.mpg.escidoc.pubman.installer.util.LabelPanel;



public class ConfigurationCreatorPanel extends ConfigurationPanel implements IConfigurationCreatorPanel
{   
    private static final long serialVersionUID = 3257848774955905587L;
    
    private StartEscidocProcess startEscidocProcess;
    private ConeInsertProcess coneInsertProcess;
    private UpdatePubmanConfigurationProcess updatePubmanConfigurationProcess;
    private LabelPanel startEscidocPanel, conePanel, configurationPanel;
    private boolean success = true;
    private int numOfThreadsFinished = 0;
    
    private static Logger logger = Logger.getLogger(ConfigurationCreatorPanel.class);

    /**
     * The constructor.
     * 
     * @param parent The parent.
     * @param idata The installation data.
     * @throws IOException
     */
    public ConfigurationCreatorPanel(InstallerFrame parent, InstallData idata) throws IOException, Exception
    {
        this(parent, idata, new IzPanelLayout());
    }

    /**
     * Creates a new HelloPanel object with the given layout manager. Valid layout manager are the IzPanelLayout and the
     * GridBagLayout. New panels should be use the IzPanelLaout. If lm is null, no layout manager will be created or
     * initialized.
     * 
     * @param parent The parent IzPack installer frame.
     * @param idata The installer internal data.
     * @param layout layout manager to be used with this IzPanel
     * @throws Exception 
     */
    public ConfigurationCreatorPanel(InstallerFrame parent, InstallData idata, LayoutManager2 layout)
            throws ServiceException, URISyntaxException, Exception
    {
        super(parent, idata, layout);
        // We create and put the labels
        JLabel welcomeLabel = LabelFactory.create("Starting eSciDoc Framework and writing configuration...", parent.icons.getImageIcon("host"),
                LEADING);
        add(welcomeLabel, NEXT_LINE);
        getLayoutHelper().completeLayout();
    }
    
    @Override
    public String getInstallPath()
    {
        return this.idata.getInstallPath();
    }
    
    @Override
    public String getInstanceUrl()
    {
        return this.idata.getVariable("CoreserviceUrl");
    }
    
    @Override
    public InstallData getInstallData()
    {
        return this.idata;
    }

    /**
     * Indicates whether the panel has been validated or not.
     * 
     * @return Always true.
     */
    public boolean isValidated()
    {
        return isPanelValid;
    }
    
    public void panelActivate()
    {
        parent.lockPrevButton();
        parent.lockNextButton();
        this.textArea = new JTextArea();
        
        startEscidocPanel = new LabelPanel("Starting the eSciDoc Framework. This process may take several minutes.", true);
        add(startEscidocPanel, NEXT_LINE);
        revalidate();
        
        try
        {
            startEscidoc();
        }
        catch (Exception e)
        {
            logger.error("Error while starting eSciDoc Framework!", e);
        }
  
        revalidate();
        
       if (haveToInsertConeData())
        {
            conePanel = new LabelPanel("Writing CoNE data to database. This process may take several minutes.", true);
            add(conePanel, NEXT_LINE);
            revalidate();
            
            try
            {
                insertConeData();
            }
            catch (Exception e)
            {
                logger.error("Error while writing CoNE data to database!", e);
            }
            revalidate();
        }
       
        configurationPanel = new LabelPanel("Updating PubMan configuration (pubman.properties, ...)", true);
        add(configurationPanel, NEXT_LINE);
        try
        {
            updatePubmanConfiguration();
        }
        catch (Exception e)
        {            
            logger.error("Error while updating PubMan configuration!", e);
        }
        revalidate();
    }

    /* (non-Javadoc)
     * @see de.mpg.escidoc.pubman.installer.panels.IConfigurationCreatorPanel#processFinishedSuccessfully(java.lang.String, java.lang.String)
     */
    @Override
    public void processFinishedSuccessfully(String text, String threadName)
    {
        logger.info("Process ended successfully: " + threadName);       
        
        numOfThreadsFinished++;
        updateSuccess(true);
        
        LabelPanel panel = getLabelPanel(threadName);
        panel.showProgressBar(false);
        panel.setEndLabel(text, LabelPanel.ICON_SUCCESS);
        
        checkButtonLocking();
        revalidate();
        setPanelValid(success);
    }

    /* (non-Javadoc)
     * @see de.mpg.escidoc.pubman.installer.panels.IConfigurationCreatorPanel#processFinishedWithError(java.lang.String, java.lang.Exception, java.lang.String)
     */
    @Override
    public void processFinishedWithError(String text, Exception e, String threadName)
    {
        logger.info("Process ended with error: " + threadName);
        
        numOfThreadsFinished++;
        updateSuccess(false);
        
        LabelPanel panel = getLabelPanel(threadName);
        panel.showProgressBar(false);
        panel.setEndLabel(text, LabelPanel.ICON_ERROR);
        panel.addToTextArea(e.toString() + ": " + e.getMessage());
        
        checkButtonLocking();
        revalidate();
        setPanelValid(success);
    }
    
    /**
     * evaluates if some data has to be inserted into cone database
     * 
     * @return boolean true if data has to be inserted into cone database
     */
    private boolean haveToInsertConeData()
    {
        boolean insertConeData = false;
        if (idata.getVariable("ConeCreateJournals").equals("true")
                || idata.getVariable("ConeCreateLanguages").equals("true")
                || idata.getVariable("ConeCreateDDC").equals("true")
                || idata.getVariable("ConeCreateMimetypes").equals("true")
                || idata.getVariable("ConeCreateEscidocMimeTypes").equals("true")
                || idata.getVariable("ConeCreateCcLicenses").equals("true"))
        {
            insertConeData = true;
        }
        return insertConeData;
    }

    /**
     * evaluates if some data has to be inserted into cone database
     * 
     * @return boolean true if data has to be inserted into cone database
     */
    private boolean haveToInstallInitialDataset()
    {
        for (Pack pack : idata.selectedPacks)
        {
            if (pack.name.equals("PubMan Initial Dataset"))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Start new Thread for writing CoNE data into database.
     * @throws Exception
     */
    private void insertConeData() throws Exception
    {
        if (this.haveToInsertConeData() == true)
        {
            ConeDataset coneDataset = new ConeDataset(idata);
            this.coneInsertProcess = new ConeInsertProcess(coneDataset, idata, this);
            getLayoutHelper().completeLayout();
            this.coneInsertProcess.start();
            
            return;
        }
    }
    
    /**
     * Start new Thread for starting the eSciDoc Framework.
     * @throws Exception
     */
    private void startEscidoc() throws Exception
    {
        this.startEscidocProcess = new StartEscidocProcess(this);
        getLayoutHelper().completeLayout();
        this.startEscidocProcess.start();
        
        return;
    }
    
    /**
     * Start a new Thread for updating PubMan configuration and creating an initial data set for PubMan. 
     * This Thread waits until eSciDoc Framework has started.
     * @throws Exception
     */
    private void updatePubmanConfiguration() throws Exception
    {
        this.updatePubmanConfigurationProcess = new UpdatePubmanConfigurationProcess(this, startEscidocProcess, haveToInstallInitialDataset());
        getLayoutHelper().completeLayout();

        this.updatePubmanConfigurationProcess.start();
        
        return;
    }
    
    private LabelPanel getLabelPanel(String threadName)
    {
        if (threadName.equals("StartEscidocProcess"))
        {
            return startEscidocPanel;
        }
        if (threadName.equals("ConeInsertProcess"))
        {
            return conePanel;
        }
        if (threadName.equals("UpdatePubmanConfigurationProcess"))
        {
            return configurationPanel;
        }
       
        return null;
    }
    
    private void updateSuccess(boolean b)
    {
        // if already false, remain false
        if (success)
        {
            success = success & b;
        }
    }
    
    private void checkButtonLocking()
    {
        if (success && (numOfThreadsFinished == 3 && haveToInsertConeData() || numOfThreadsFinished == 2 && !haveToInsertConeData()))
        {
            parent.unlockNextButton();
        }
        if (!success || (numOfThreadsFinished == 3 && haveToInsertConeData() || numOfThreadsFinished == 2 && !haveToInsertConeData()))
        {
            parent.unlockPrevButton();
        }
    }

}
