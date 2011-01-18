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
* Copyright 2006-20110 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/ 

package de.mpg.escidoc.pubman.multipleimport;

import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.faces.context.FacesContext;
import javax.naming.InitialContext;

import org.apache.log4j.Logger;

import de.escidoc.www.services.om.ContextHandler;
import de.mpg.escidoc.pubman.util.InternationalizationHelper;
import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.common.valueobjects.AccountUserVO;
import de.mpg.escidoc.services.common.valueobjects.ContextVO;
import de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO;
import de.mpg.escidoc.services.common.valueobjects.publication.PublicationAdminDescriptorVO.Workflow;
import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.framework.ServiceLocator;

/**
 * Class that describes an import.
 *
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class ImportLog
{
    /**
     * enum to describe the general state of the log.
     */
    public enum Status
    {
        PENDING, SUSPENDED, FINISHED, ROLLBACK
    }
    
    /**
     * enum to describe if something went wrong with this element.
     * 
     * - FINE:      everything is alright
     * - WARNING:   import worked, but something could have been done better
     * - PROBLEM:   some item was not imported because validation failed
     * - ERROR:     some items were not imported because there were system errors during the import
     * - FATAL:     the import was interrupted completely due to system errors
     */
    public enum ErrorLevel
    {
        FINE, WARNING, PROBLEM, ERROR, FATAL
    }
    
    /**
     * enum defining possible sorting columns.
     */
    public enum SortColumn
    {
        STARTDATE, ENDDATE, NAME, FORMAT, STATUS, ERRORLEVEL;
        
        /**
         * @return A representation of the element that is used for storing in a database
         */
        public String toSQL()
        {
            return super.toString().toLowerCase();
        }
    }
    
    /**
     * enum defining sorting directions.
     *
     */
    public enum SortDirection
    {
        ASCENDING, DESCENDING;
        
        /**
         * @return A representation of the element that is used for storing in a database
         */
        public String toSQL()
        {
            String value = super.toString();
            return value.replace("ENDING", "").toLowerCase();
        }
    }
    
    private static Logger logger = Logger.getLogger(ImportLog.class);
    
    /**
     * The data format that is used to display start- and end-date.
     * Example: 2009-12-31 23:59
     */
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    
    private Date startDate;
    private Date endDate;
    
    private Status status;
    private ErrorLevel errorLevel;
    
    private int percentage;
    
    private int storedId;
    private Connection connection;
    
    private String format;
    private String action;
    private String user;
    private String userHandle;
    private String message;
    private String context;
    
    private Workflow workflow;
    
    private List<ImportLogItem> items = new ArrayList<ImportLogItem>();

    private ImportLogItem currentItem = null;
    
    /**
     * Implicit constructor for inheriting classes.
     */
    protected ImportLog()
    {
        // TODO MF: Redirect if not logged in.
    }
    
    /**
     * Constructor.
     * 
     * @param action A string indicating what action is logged by this.
     * - "import"
     * - "delete"
     * - "submit"
     * - "release"
     * TODO: Put this into an enum
     * 
     * @param user The eSciDoc user id of the user that invoces this action.
     * @param format A string holding the format of the import, e.g. "bibtex".
     */
    public ImportLog(String action, String user, String format)
    {
        this.startDate = new Date();
        this.status = Status.PENDING;
        this.errorLevel = ErrorLevel.FINE;
        this.user = user;
        this.format = format;
        this.action = action;
        
        this.connection = getConnection();
        
        saveLog();
    }

    /**
     * Reads the database configuration from the properties and then creates a {@link Connection}.
     * 
     * @return An open connection to the import database
     */
    public static Connection getConnection()
    {
        try
        {
            Class.forName(PropertyReader.getProperty("escidoc.import.database.driver.class"));
            String connectionUrl = PropertyReader.getProperty("escidoc.import.database.connection.url");
            return DriverManager.getConnection(connectionUrl
                    .replaceAll("\\$1", PropertyReader.getProperty("escidoc.import.database.server.name"))
                    .replaceAll("\\$2", PropertyReader.getProperty("escidoc.import.database.server.port"))
                    .replaceAll("\\$3", PropertyReader.getProperty("escidoc.import.database.name")),
                    PropertyReader.getProperty("escidoc.import.database.user.name"),
                    PropertyReader.getProperty("escidoc.import.database.user.password"));
            
//            Context ctx = new InitialContext();
//            DataSource dataSource = (DataSource) ctx.lookup("ImportLog");
//            return dataSource.getConnection();
        }
        catch (Exception e)
        {
            throw new RuntimeException("Error creating database connection", e);
        }
    }

    /**
     * @throws RuntimeException
     */
    public void closeConnection()
    {
        try
        {
            if (this.connection != null && !this.connection.isClosed())
            {
                //this.connection.close();
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException("Error creating database connection", e);
        }
    }
    
    /**
     * Called when, for any reason, this action is over.
     */
    public void close()
    {
        try
        {
            if (!this.connection.isClosed())
            {
                
                this.endDate = new Date();
                this.status = Status.FINISHED;
                this.percentage = 100;
                
                updateLog();
                
                //this.connection.close();
                
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException("Error closing connection", e);
        }
    }
    
    /**
     * Called when this process shall continue.
     */
    public void reopen()
    {

        this.endDate = null;
        this.status = Status.PENDING;
        
        updateLog();

    }
    
    /**
     * Creates a new item using the given message.
     * 
     * Defaults:
     * - Item id will be set to null
     * - Start date will be set to the current date
     * - Error level will be set to FINE.
     * 
     * @param msg A message key for a localized message
     */
    public void startItem(String msg)
    {
        startItem(msg, null);
    }
    
    /**
     * Creates a new item using the given message and item id, then putting the focus of the import on it.
     * 
     * Defaults:
     * - Start date will be set to the current date
     * - Error level will be set to FINE.
     * 
     * @param msg A message key for a localized message
     * @param itemId The eSciDoc id of the imported item
     */
    public void startItem(String msg, String itemId)
    {
        startItem(msg, new Date(), itemId);
    }
    
    /**
     * Creates a new item using the given message, item id and start date, then putting the focus of the import on it.
     * 
     * Defaults:
     * - Error level will be set to FINE.
     * 
     * @param msg A message key for a localized message
     * @param sDate The start date of this item
     * @param itemId The eSciDoc id of the imported item
     */
    public void startItem(String msg, Date sDate, String itemId)
    {
        startItem(ErrorLevel.FINE, msg, sDate, itemId);
    }
    
    /**
     * Creates a new item using the given error level and message, then putting the focus of the import on it.
     * 
     * Defaults:
     * - Item id will be set to null
     * - Start date will be set to the current date
     * 
     * @param errLevel The initial error level of this item
     * @param msg A message key for a localized message
     */
    public void startItem(ErrorLevel errLevel, String msg)
    {
        startItem(errLevel, msg, new Date(), null);
    }
    
    /**
     * Creates a new item using the given error level, message, item id and start date,
     * then putting the focus of the import on it.
     * 
     * @param errLevel The initial error level of this item
     * @param msg A message key for a localized message
     * @param sDate The start date of this item
     * @param itemId The eSciDoc id of the imported item
     */
    public void startItem(ErrorLevel errLevel, String msg, Date sDate, String itemId)
    {
        if (this.currentItem != null)
        {
            throw new RuntimeException("Trying to start logging an item while another is not yet finished");
        }
        
        ImportLogItem newItem = new ImportLogItem(this);
        
        newItem.setErrorLevel(errLevel);
        newItem.setMessage(msg);
        newItem.setStartDate(sDate);
        
        saveItem(newItem);
        
        items.add(newItem);
        
        this.currentItem = newItem;
    }
    
    /**
     * Sets the status of the focused item to FINISHED and the end date to the current date,
     * then removes the focus of the import.
     */
    public void finishItem()
    {
        if (this.currentItem != null)
        {
            this.currentItem.setEndDate(new Date());
            this.currentItem.setStatus(Status.FINISHED);
            
            updateItem(this.currentItem);
            
            this.currentItem = null;
        }
    }
    
    /**
     * Sets the status of the focused item to SUSPENDED. This should be done
     * when it is planned to visit this item again later.
     * I.e. in a first step, all items are transformed and validated, then suspended.
     * In a second step, all items are imported into the repository.
     */
    public void suspendItem()
    {
        if (this.currentItem != null)
        {
            this.currentItem.setStatus(Status.SUSPENDED);
            
            updateItem(this.currentItem);
            
            this.currentItem = null;
        }
    }
    
    /**
     * Adds a detail to the focused item using the given error level and message key.
     * Start- and end-date are set to the current date. Status is set to FINISHED.
     * 
     * Defaults:
     * - The detail id will be set to null
     * 
     * @param errLevel The error level of this item
     * @param msg A message key for a localized message
     */
    public void addDetail(ErrorLevel errLevel, String msg)
    {
        addDetail(errLevel, msg, null);
    }
    
    /**
     * Adds a detail to the focused item using the given error level, message key and detail id.
     * Start- and end-date are set to the current date. Status is set to FINISHED.
     * 
     * @param errLevel The error level of this item
     * @param msg A message key for a localized message
     * @param detailId The (eSciDoc) id related to this detail (e.g. the id of an identified duplicate)
     */
    public void addDetail(ErrorLevel errLevel, String msg, String detailId)
    {
        if (this.currentItem == null)
        {
            throw new RuntimeException("Trying to add a detail but no log item is started.");
        }
        
        ImportLogItem newDetail = new ImportLogItem(currentItem);
        
        newDetail.setErrorLevel(errLevel);
        newDetail.setMessage(msg);
        newDetail.setStartDate(new Date());
        newDetail.setItemId(detailId);
        newDetail.setStatus(Status.FINISHED);
        
        if (this.currentItem == null)
        {
            startItem("");
        }
        
        this.currentItem.getItems().add(newDetail);
        
        saveDetail(newDetail);
        
    }
    
    /**
     * Adds a detail to the focused item using the given error level and a previously caught exception.
     * Start- and end-date are set to the current date. Status is set to FINISHED.
     * The exception is transformed into a stack trace.
     * 
     * @param errLevel The error level of this item
     * @param exception The exception that should be added to the item
     */
    public void addDetail(ErrorLevel errLevel, Exception exception)
    {
        String msg = getExceptionMessage(exception);
        addDetail(errLevel, msg, null);
    }
    
    /**
     * @param itemVO Assigns a value object to the focused item.
     */
    public void setItemVO(PubItemVO itemVO)
    {
        this.currentItem.setItemVO(itemVO);
    }
    
    /**
     * Transforms an exception into a Java stack trace.
     * 
     * @param exception The exception
     * @return The stack trace
     */
    private String getExceptionMessage(Throwable exception)
    {
        StringWriter stringWriter = new StringWriter();
        stringWriter.write(exception.getClass().getSimpleName());
        if (exception.getMessage() != null)
        {
            stringWriter.write(": ");
            stringWriter.write(exception.getMessage());
        }
        stringWriter.write("\n");
        
        StackTraceElement[] stackTraceElements = exception.getStackTrace();
        for (StackTraceElement stackTraceElement : stackTraceElements)
        {
            //at TimeTest.main(TimeTest.java:47)
            stringWriter.write("\tat ");
            stringWriter.write(stackTraceElement.getClassName());
            stringWriter.write(".");
            stringWriter.write(stackTraceElement.getMethodName());
            stringWriter.write("(");
            stringWriter.write(stackTraceElement.getFileName());
            stringWriter.write(":");
            stringWriter.write(stackTraceElement.getLineNumber() + "");
            stringWriter.write(")\n");
        }
        if (exception.getCause() != null)
        {
            stringWriter.write(getExceptionMessage(exception.getCause())); 
        }
        return stringWriter.toString();
    }

    /**
     * Sets the (eSciDoc) id of the focused item.
     * 
     * @param id The id
     */
    public void setItemId(String id)
    {
        this.currentItem.setItemId(id);
        updateItem(this.currentItem);
    }

    /**
     * @return true if this import is already finished
     */
    public boolean isDone()
    {
        return (this.status == Status.FINISHED);
    }
    
    /**
     * @return the startDate
     */
    public Date getStartDate()
    {
        return startDate;
    }
    
    /**
     * @return the startDate
     */
    public String getStartDateFormatted()
    {
        if (this.startDate != null)
        {
            return DATE_FORMAT.format(startDate);
        }
        else
        {
            return "";
        }
    }

    /**
     * @param startDate the startDate to set
     */
    public void setStartDate(Date startDate)
    {
        this.startDate = startDate;
    }

    /**
     * @return the endDate
     */
    public Date getEndDate()
    {
        return endDate;
    }

    /**
     * @return the endDate
     */
    public String getEndDateFormatted()
    {
        if (this.endDate != null)
        {
            return DATE_FORMAT.format(endDate);
        }
        else
        {
            return "";
        }
    }

    /**
     * @param endDate the endDate to set
     */
    public void setEndDate(Date endDate)
    {
        this.endDate = endDate;
    }

    /**
     * @return the status
     */
    public Status getStatus()
    {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(Status status)
    {
        this.status = status;
    }

    /**
     * @return the format
     */
    public String getFormat()
    {
        return format;
    }

    /**
     * @param format the format to set
     */
    public void setFormat(String format)
    {
        this.format = format;
    }

    /**
     * @return the user
     */
    public String getUser()
    {
        return user;
    }

    /**
     * @param user the user to set
     */
    public void setUser(String user)
    {
        this.user = user;
    }

    /**
     * @return the context
     */
    public String getContext()
    {
        return context;
    }

    /**
     * @param context the context to set
     */
    public void setContext(String context)
    {
        this.context = context;
    }

    /**
     * @return the errorLevel
     */
    public ErrorLevel getErrorLevel()
    {
        return errorLevel;
    }

    /**
     * @param errorLevel the errorLevel to set
     */
    public void setErrorLevel(ErrorLevel errorLevel)
    {
        if (this.errorLevel == null
                || errorLevel == ErrorLevel.FATAL
                || (errorLevel == ErrorLevel.ERROR
                        && this.errorLevel != ErrorLevel.FATAL)
                || (errorLevel == ErrorLevel.PROBLEM
                        && this.errorLevel != ErrorLevel.FATAL
                        && this.errorLevel != ErrorLevel.ERROR)
                || (errorLevel == ErrorLevel.WARNING
                        && this.errorLevel != ErrorLevel.FATAL
                        && this.errorLevel != ErrorLevel.ERROR
                        && this.errorLevel != ErrorLevel.PROBLEM))
        {
            this.errorLevel = errorLevel;
        }
        if (this.connection != null)
        {
            updateLog();
        }
    }

    /**
     * Checks if the import is already finished.
     * 
     * @return true if the import is finished
     */
    public boolean getFinished()
    {
        return (this.status == Status.FINISHED);
    }
    
    /**
     * Checks if at least one item was imported.
     * 
     * @return true if one or more items were imported, otherwise false.
     */
    public boolean getImportedItems()
    {
        for (ImportLogItem item : this.items)
        {
            if (item.getItemId() != null)
            {
                return true;
            }
        }
        return false;
    }
    
    /**
     * @return the items
     */
    public List<ImportLogItem> getItems()
    {
        return items;
    }

    /**
     * @param items the items to set
     */
    public void setItems(List<ImportLogItem> items)
    {
        this.items = items;
    }
    
    /**
     * @return the storedId
     */
    public int getStoredId()
    {
        return storedId;
    }

    /**
     * @param storedId the storedId to set
     */
    public void setStoredId(int storedId)
    {
        this.storedId = storedId;
    }

    /**
     * @return the action
     */
    public String getAction()
    {
        return action;
    }

    /**
     * @param action the action to set
     */
    public void setAction(String action)
    {
        this.action = action;
    }

    /**
     * @return the currentItem
     */
    public ImportLogItem getCurrentItem()
    {
        return currentItem;
    }

    /**
     * @return the message
     */
    public String getMessage()
    {
        return message;
    }

    /**
     * @param message the message to set
     */
    public void setMessage(String message)
    {
        this.message = message;
    }

    /**
     * @return the userHandle
     */
    public String getUserHandle()
    {
        return userHandle;
    }

    /**
     * @param userHandle the userHandle to set
     */
    public void setUserHandle(String userHandle)
    {
        this.userHandle = userHandle;
    }

    /**
     * @return the percentage
     */
    public int getPercentage()
    {
        return percentage;
    }

    /**
     * @param percentage the percentage to set
     */
    public void setPercentage(int percentage)
    {
        this.percentage = percentage;
        updateLog();
    }

    private synchronized void saveLog()
    {
        try
        {
            PreparedStatement statement = this.connection.prepareStatement("insert into escidoc_import_log "
                    + "(status, errorlevel, startdate, action, userid, name, context, format, percentage) "
                    + "values (?, ?, ?, ?, ?, ?, ?, ?, 0)");
            
            statement.setString(1, this.status.toString());
            statement.setString(2, this.errorLevel.toString());
            statement.setTimestamp(3, new Timestamp(this.startDate.getTime()));
            statement.setString(4, this.action);
            statement.setString(5, this.user);
            statement.setString(6, this.message);
            statement.setString(7, this.context);
            statement.setString(8, this.format);
            
            statement.executeUpdate();
            //statement.close();
            
            statement = this.connection.prepareStatement("select max(id) as maxid from escidoc_import_log");
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next())
            {
                this.storedId = resultSet.getInt("maxid");
                //resultSet.close();
                //statement.close();
            }
            else
            {
                //resultSet.close();
                //statement.close();
                throw new RuntimeException("Error saving log");
            }
            
        }
        catch (Exception e)
        {
            throw new RuntimeException("Error saving log", e);
        }
    }
    
    private synchronized void updateLog()
    {
        try
        {
            PreparedStatement statement = this.connection.prepareStatement("update escidoc_import_log set "
                    + "status = ?, "
                    + "errorlevel = ?, "
                    + "startdate = ?, "
                    + "enddate = ?, "
                    + "action = ?, "
                    + "userid = ?, "
                    + "name = ?, "
                    + "context = ?, "
                    + "format = ?, "
                    + "percentage = ? "
                    + "where id = ?");
            
            statement.setString(1, this.status.toString());
            statement.setString(2, this.errorLevel.toString());
            statement.setTimestamp(3, new Timestamp(this.startDate.getTime()));
            if (this.endDate != null)
            {
                statement.setTimestamp(4, new Timestamp(this.endDate.getTime()));
            }
            else
            {
                statement.setTimestamp(4, null);
            }
            statement.setString(5, this.action);
            statement.setString(6, this.user);
            statement.setString(7, this.message);
            statement.setString(8, this.context);
            statement.setString(9, this.format);
            statement.setInt(10, this.percentage);
            
            statement.setInt(11, this.storedId);
            
            statement.executeUpdate();
            //statement.close();
                        
        }
        catch (Exception e)
        {
            throw new RuntimeException("Error saving log", e);
        }
    }

    private synchronized void saveItem(ImportLogItem item)
    {
        try
        {
            PreparedStatement statement = this.connection.prepareStatement("insert into escidoc_import_log_item "
                    + "(status, errorlevel, startdate, parent, message, item_id, action) "
                    + "values (?, ?, ?, ?, ?, ?, ?)");
            
            statement.setString(1, item.getStatus().toString());
            statement.setString(2, item.getErrorLevel().toString());
            statement.setTimestamp(3, new Timestamp(item.getStartDate().getTime()));
            statement.setInt(4, this.storedId);
            statement.setString(5, item.getMessage());
            statement.setString(6, item.getItemId());
            statement.setString(7, item.getAction());
            
            statement.executeUpdate();
            //statement.close();
            
            statement = this.connection.prepareStatement("select max(id) as maxid from escidoc_import_log_item");
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next())
            {
                item.setStoredId(resultSet.getInt("maxid"));
                //resultSet.close();
                //statement.close();
            }
            else
            {
                //resultSet.close();
                //statement.close();
                throw new RuntimeException("Error saving log item");
            }
            
        }
        catch (Exception e)
        {
            throw new RuntimeException("Error saving log", e);
        }
    }
    
    private synchronized void updateItem(ImportLogItem item)
    {
        try
        {
            PreparedStatement statement = this.connection.prepareStatement("update escidoc_import_log_item set "
                    + "status = ?, "
                    + "errorlevel = ?, "
                    + "startdate = ?, "
                    + "enddate = ?, "
                    + "parent = ?, "
                    + "message = ?, "
                    + "item_id = ?, "
                    + "action = ? "
                    + "where id = ?");
            
            statement.setString(1, item.getStatus().toString());
            statement.setString(2, item.getErrorLevel().toString());
            statement.setTimestamp(3, new Timestamp(item.getStartDate().getTime()));
            if (item.getEndDate() != null)
            {
                statement.setTimestamp(4, new Timestamp(item.getEndDate().getTime()));
            }
            else
            {
                statement.setDate(4, null);
            }
            statement.setInt(5, this.storedId);
            statement.setString(6, item.getMessage());
            statement.setString(7, item.getItemId());
            statement.setString(8, item.getAction());
            statement.setInt(9, item.getStoredId());
            
            statement.executeUpdate();
            //statement.close();
                        
        }
        catch (Exception e)
        {
            throw new RuntimeException("Error saving log", e);
        }
    }

    private synchronized void saveDetail(ImportLogItem detail)
    {
        try
        {
            PreparedStatement statement = this.connection.prepareStatement("insert into escidoc_import_log_detail "
                    + "(status, errorlevel, startdate, parent, message, item_id, action) "
                    + "values (?, ?, ?, ?, ?, ?, ?)");
            
            statement.setString(1, detail.getStatus().toString());
            statement.setString(2, detail.getErrorLevel().toString());
            statement.setTimestamp(3, new Timestamp(detail.getStartDate().getTime()));
            statement.setInt(4, detail.getParent().getStoredId());
            statement.setString(5, detail.getMessage());
            statement.setString(6, detail.getItemId());
            statement.setString(7, detail.getAction());
            
            statement.executeUpdate();
            //statement.close();
            
            statement = this.connection.prepareStatement("select max(id) as maxid from escidoc_import_log_detail");
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next())
            {
                detail.setStoredId(resultSet.getInt("maxid"));
                //resultSet.close();
                //statement.close();
            }
            else
            {
                //resultSet.close();
                //statement.close();
                throw new RuntimeException("Error saving log item");
            }
            
        }
        catch (Exception e)
        {
            throw new RuntimeException("Error saving log", e);
        }
    }

    /**
     * Retrieves a users imports from the database.
     * 
     * Defaults:
     * - items are loaded
     * - item details are loaded
     * 
     * @param action Usually "import"
     * @param user The user's value object
     * @param sortBy The column the logs should be sorted by
     * @param dir The direction the imports should be sorted by
     * 
     * @return A list of imports
     */
    public static List<ImportLog> getImportLogs(String action, AccountUserVO user, SortColumn sortBy, SortDirection dir)
    {
        return getImportLogs(action, user, sortBy, dir, true);
    }
    
    /**
     * Retrieves a users imports from the database.
     * 
     * Defaults:
     * - items are loaded
     * 
     * @param action Usually "import"
     * @param user The user's value object
     * @param sortBy The column the logs should be sorted by
     * @param dir The direction the imports should be sorted by
     * @param loadDetails Indicates whether the items details should be loaded
     * 
     * @return A list of imports
     */
    public static List<ImportLog> getImportLogs(
            String action,
            AccountUserVO user,
            SortColumn sortBy,
            SortDirection dir,
            boolean loadDetails)
    {
        return getImportLogs(action, user, sortBy, dir, true, loadDetails);
    }
    
    /**
     * Retrieves a users imports from the database.
     *
     * @param action Usually "import"
     * @param user The user's value object
     * @param sortBy The column the logs should be sorted by
     * @param dir The direction the imports should be sorted by
     * @param loadItems Indicates whether the import items should be loaded
     * @param loadDetails Indicates whether the items details should be loaded
     * 
     * @return A list of imports
     */
    public static List<ImportLog> getImportLogs(
            String action,
            AccountUserVO user,
            SortColumn sortBy,
            SortDirection dir,
            boolean loadItems,
            boolean loadDetails)
    {
        List<ImportLog> result = new ArrayList<ImportLog>();
        Connection connection = getConnection();
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        String query = "select id from escidoc_import_log where action = ? and userid = ? "
                + "order by lower(" + sortBy + ") " + dir.toSQL();
        try
        {
            statement = connection.prepareStatement(query);
            statement.setString(1, action);
            statement.setString(2, user.getReference().getObjectId());
            
            resultSet = statement.executeQuery();
            
            while (resultSet.next())
            {
                int id = resultSet.getInt("id");
                ImportLog log = getImportLog(id, loadDetails);
                log.userHandle = user.getHandle();
                result.add(log);
            }
        }
        catch (Exception e)
        {
            try
            {
                //resultSet.close();
                //statement.close();
                //connection.close();
            }
            catch (Exception f)
            {
            }
            throw new RuntimeException("Error getting log", e);
        }
        try
        {
            //resultSet.close();
            //statement.close();
            //connection.close();
        }
        catch (Exception f)
        {
            throw new RuntimeException("Error closing connection", f);
        }
        
        return result;
    }

    /**
     * Get a single import by its stored id.
     * 
     * Defaults:
     * - items are loaded
     * - item details are loaded
     * 
     * @param id The id
     * 
     * @return The import
     */
    public static ImportLog getImportLog(int id)
    {
        return getImportLog(id, true);
    }

    /**
     * Get a single import by its stored id.
     * 
     * Defaults:
     * - items are loaded
     * 
     * @param id The id
     * @param loadDetails Indicates whether the items details should be loaded
     * 
     * @return The import
     */
    public static ImportLog getImportLog(int id, boolean loadDetails)
    {
        return getImportLog(id, true, loadDetails);
    }
    
    /**
     * Get a single import by its stored id.
     * 
     * @param id The id
     * @param loadItems Indicates whether the import items should be loaded
     * @param loadDetails Indicates whether the items details should be loaded
     * 
     * @return The import
     */
    public static ImportLog getImportLog(int id, boolean loadItems, boolean loadDetails)
    {
        Connection connection = getConnection();
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        String query = null;
        ImportLog result = null;

        try
        {
            query = "select * from escidoc_import_log where id = ?";
            statement = connection.prepareStatement(query);
            statement.setInt(1, id);
            resultSet = statement.executeQuery();
            
            if (resultSet.next())
            {
                result = fillLog(resultSet);
            }
            else
            {
                logger.warn("Import log query returned no result for id " + id);
            }
            
            query = "select * from escidoc_import_log_item where parent = ? order by id";
            statement = connection.prepareStatement(query);
            statement.setInt(1, id);
            resultSet = statement.executeQuery();
            
            List<ImportLogItem> items = new ArrayList<ImportLogItem>();
            
            while (resultSet.next())
            {
                ImportLogItem item = fillItem(resultSet, result);
                items.add(item);
            }
            
            result.setItems(items);
            
            if (loadDetails)
            {
                query = "select escidoc_import_log_detail.* "
                        + "from escidoc_import_log_item, escidoc_import_log_detail "
                        + "where escidoc_import_log_item.id = escidoc_import_log_detail.parent "
                        + "and escidoc_import_log_item.parent = ? "
                        + "order by escidoc_import_log_detail.id";
                statement = connection.prepareStatement(query);
                statement.setInt(1, id);
                resultSet = statement.executeQuery();
    
                Iterator<ImportLogItem> iterator = items.iterator();
                
                if (items.size() > 0)
                {
                
                    ImportLogItem currentItem = iterator.next();
                    List<ImportLogItem> details = new ArrayList<ImportLogItem>();
                    currentItem.setItems(details);
                    
                    while (resultSet.next())
                    {
                        int itemId = resultSet.getInt("parent");
                        while (currentItem.getStoredId() != itemId && iterator.hasNext())
                        {
                            currentItem = iterator.next();
                            details = new ArrayList<ImportLogItem>();
                            currentItem.setItems(details);
                        }
                        
                        ImportLogItem detail = fillDetail(resultSet, currentItem);
                        details.add(detail);
                    }
                }
            }
        }
        catch (Exception e)
        {
            try
            {
                //resultSet.close();
                //statement.close();
                //connection.close();
            }
            catch (Exception f)
            {
            }
            throw new RuntimeException("Error getting detail", e);
        }
        try
        {
            //resultSet.close();
            //statement.close();
            //connection.close();
        }
        catch (Exception f)
        {
            throw new RuntimeException("Error closing connection", f);
        }
        
        return result;
    }

    /**
     * @param resultSet
     * @param currentItem
     * @return
     * @throws SQLException
     */
    private static ImportLogItem fillDetail(ResultSet resultSet, ImportLogItem currentItem) throws SQLException
    {
        ImportLogItem detail = new ImportLogItem(currentItem);
        
        detail.setAction(resultSet.getString("action"));
        detail.setEndDate(resultSet.getTimestamp("enddate"));
        detail.setErrorLevel(ErrorLevel.valueOf(resultSet.getString("errorlevel").toUpperCase()));
        detail.setStartDate(resultSet.getTimestamp("startdate"));
        detail.setStatus(Status.valueOf(resultSet.getString("status")));
        detail.setStoredId(resultSet.getInt("id"));
        detail.setItemId(resultSet.getString("item_id"));
        detail.setMessage(resultSet.getString("message"));
        return detail;
    }

    /**
     * @param resultSet
     * @param result
     * @return
     * @throws SQLException
     */
    private static ImportLogItem fillItem(ResultSet resultSet, ImportLog result) throws SQLException
    {
        ImportLogItem item = new ImportLogItem(result);
        
        item.setAction(resultSet.getString("action"));
        item.setEndDate(resultSet.getTimestamp("enddate"));
        item.setErrorLevel(ErrorLevel.valueOf(resultSet.getString("errorlevel").toUpperCase()));
        item.setStartDate(resultSet.getTimestamp("startdate"));
        item.setStatus(Status.valueOf(resultSet.getString("status")));
        item.setStoredId(resultSet.getInt("id"));
        item.setItemId(resultSet.getString("item_id"));
        item.setMessage(resultSet.getString("message"));
        return item;
    }

    /**
     * @param resultSet SQL result set
     * @return The filled import
     * @throws SQLException
     */
    private static ImportLog fillLog(ResultSet resultSet) throws SQLException
    {
        ImportLog result;
        result = new ImportLog();
        result.setAction(resultSet.getString("action"));
        result.setEndDate(resultSet.getTimestamp("enddate"));
        result.setErrorLevel(ErrorLevel.valueOf(resultSet.getString("errorlevel").toUpperCase()));
        result.setFormat(resultSet.getString("format"));
        result.setStartDate(resultSet.getTimestamp("startdate"));
        result.setStatus(Status.valueOf(resultSet.getString("status")));
        result.setStoredId(resultSet.getInt("id"));
        result.setContext(resultSet.getString("context"));
        result.setUser(resultSet.getString("userid"));
        result.setMessage(resultSet.getString("name"));
        result.percentage = resultSet.getInt("percentage");
        return result;
    }
    
    /**
     * Get the details of a certain import item.
     * 
     * @param id The item id
     * @param userid The users id
     * 
     * @return A list of details
     */
    public static List<ImportLogItem> loadDetails(int id, String userid)
    {
        List<ImportLogItem> details = new ArrayList<ImportLogItem>();
        Connection connection = getConnection();
        
        String query = "select escidoc_import_log_detail.* "
            + "from escidoc_import_log_item, escidoc_import_log_detail, escidoc_import_log "
            + "where escidoc_import_log_item.id = escidoc_import_log_detail.parent "
            + "and escidoc_import_log_item.parent = escidoc_import_log.id "
            + "and escidoc_import_log_item.id = ? "
            + "and escidoc_import_log.userid = ? "
            + "order by escidoc_import_log_detail.id";
        try
        {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, id);
            statement.setString(2, userid);
            
            ResultSet resultSet = statement.executeQuery();
        
            while (resultSet.next())
            {
                ImportLogItem detail = fillDetail(resultSet, null);
                details.add(detail);
            }
            return details;
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public String toString()
    {
        StringWriter writer = new StringWriter();

        writer.write(getErrorLevel().toString());
        writer.write(": ");
        if (getRelevantString() != null)
        {
            writer.write(getRelevantString());
        }
        else
        {
            writer.write("- - -");
        }
        writer.write(" (");
        writer.write(DATE_FORMAT.format(getStartDate()));
        writer.write(" - ");
        if (getEndDate() != null)
        {
            writer.write(DATE_FORMAT.format(getEndDate()));
        }
        writer.write(") - ");
        writer.write(getStatus().toString());
        writer.write("\n");

        for (ImportLogItem item : getItems())
        {
            writer.write(item.toString().replaceAll("(.*)\n", "\t$1\n"));
        }
        
        return writer.toString();
    }

    protected String getRelevantString()
    {
        return getAction();
    }

    /**
     * Puts the import's focus on this item.
     * 
     * @param item The item to be activated
     */
    public void activateItem(ImportLogItem item)
    {
        if (this.currentItem == null)
        {
            this.currentItem = item;
        }
        else
        {
            throw new RuntimeException("Trying to start logging an item while another is not yet finished");
        }
    }
    
    /**
     * @return An XML representation of this import. Used to store it in the repository.
     */
    public String toXML()
    {
        StringWriter writer = new StringWriter();

        writer.write("<import-task ");
        writer.write("status=\"");
        writer.write(this.status.toString());
        writer.write("\" error-level=\"");
        writer.write(this.errorLevel.toString());
        writer.write("\" created-by=\"");
        writer.write(this.user);
        writer.write("\">\n");
        
        writer.write("\t<name>");
        writer.write(escape(this.message));
        writer.write("</name>\n");
        
        writer.write("\t<context>");
        writer.write(this.context);
        writer.write("</context>\n");
        
        writer.write("\t<start-date>");
        writer.write(getStartDateFormatted());
        writer.write("</start-date>\n");
        
        if (this.endDate != null)
        {
            writer.write("\t<end-date>");
            writer.write(getEndDateFormatted());
            writer.write("</end-date>\n");
        }
        
        writer.write("\t<format>");
        writer.write(this.format);
        writer.write("</format>\n");
        
        writer.write("\t<items>\n");
        for (ImportLogItem item : this.items)
        {
            writer.write(item.toXML().replaceAll("(.*\\n)", "\t\t$1"));
        }
        writer.write("\t</items>\n");
        writer.write("</import-task>\n");
        
        return writer.toString();
    }

    /**
     * An XML-safe representation of the given string.
     * 
     * @param string The given string
     * @return the escaped string
     */
    protected String escape(String string)
    {
        if (string == null)
        {
            return null;
        }
        else
        {
            return string.replace("&", "&amp;").replace("\"", "&quot;").replace("<", "&lt;");
        }
    }

    /**
     * Reads a localized message from the message resource bundle.
     * 
     * @return A string holding the localized message
     */
    public String getLocalizedMessage()
    {
        FacesContext ctx = FacesContext.getCurrentInstance();
        InternationalizationHelper i18nHelper =
            (InternationalizationHelper) ctx
                .getExternalContext()
                .getSessionMap()
                .get(InternationalizationHelper.BEAN_NAME);
        try
        {
            return ResourceBundle.getBundle(i18nHelper.getSelectedMessagesBundle()).getString(getMessage());
        }
        catch (MissingResourceException mre)
        {
            // No message entry for this message, it's probably raw data.
            return getMessage();
        }
        
    }
    
    /**
     * JSF action to remove an import from the database.
     * 
     * @return Always null.
     */
    public String remove()
    {
        try
        {
            Connection conn = getConnection();
            
            String query = "delete from escidoc_import_log_detail where parent in "
                + "(select id from escidoc_import_log_item where parent = ?)";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setInt(1, this.storedId);
            statement.executeUpdate();
            statement.close();
            
            query = "delete from escidoc_import_log_item where parent  = ?";
            statement = conn.prepareStatement(query);
            statement.setInt(1, this.storedId);
            statement.executeUpdate();
            statement.close();
            
            query = "delete from escidoc_import_log where id  = ?";
            statement = conn.prepareStatement(query);
            statement.setInt(1, this.storedId);
            statement.executeUpdate();
            statement.close();
            
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
        
        FacesContext fc = FacesContext.getCurrentInstance();
        try
        {
            fc.getExternalContext().redirect("ImportWorkspace.jsp");
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
        return null;
    }
    
    /**
     * JSF action to delete all items of an import from the repository.
     * 
     * @return Always null.
     */
    public String deleteAll()
    {
        this.connection = getConnection();
        DeleteProcess deleteProcess = new DeleteProcess(this);
        deleteProcess.start();
        
        FacesContext fc = FacesContext.getCurrentInstance();
        try
        {
            fc.getExternalContext().redirect("ImportWorkspace.jsp");
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
        
        return null;
    }
    
    /**
     * JSF action to submit/release all items of an import from the repository.
     * 
     * @return Always null.
     */
    public String submitAll()
    {
        this.connection = getConnection();
        SubmitProcess submitProcess = new SubmitProcess(this, false);
        submitProcess.start();
        
        FacesContext fc = FacesContext.getCurrentInstance();
        try
        {
            fc.getExternalContext().redirect("ImportWorkspace.jsp");
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
        
        return null;
    }
    
    /**
     * JSF action to submit/release all items of an import from the repository.
     * 
     * @return Always null.
     */
    public String submitAndReleaseAll()
    {
        this.connection = getConnection();
        SubmitProcess submitProcess = new SubmitProcess(this, true);
        submitProcess.start();
        
        FacesContext fc = FacesContext.getCurrentInstance();
        try
        {
            fc.getExternalContext().redirect("ImportWorkspace.jsp");
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
        
        return null;
    }
    
    /**
     * @return A link to a JSP page showing only this import (no items)
     */
    public String getLogLink()
    {
        return "ImportData.jsp?id=" + getStoredId();
    }
    
    /**
     * @return A link to the MyItems page filtering for this import
     */
    public String getMyItemsLink()
    {
        try
        {
            return "DepositorWSPage.jsp?import=" + URLEncoder.encode(
                    getMessage() + " " + getStartDateFormatted(), "ISO-8859-1");
        }
        catch (UnsupportedEncodingException usee)
        {
            // This should not happen as UTF-8 is known
            throw new RuntimeException(usee);
        }
    }
    
    /**
     * @return A link to a JSP page showing the items of this import (no details)
     */
    public String getItemsLink()
    {
        return "ImportItems.jsp?id=" + getStoredId();
    }
    
    /**
     * Dummy setter to avoid JSF warnings.
     * 
     * @param link The link
     */
    public void setItemsLink(String link)
    {
    }
    
    /**
     * Dummy setter to avoid JSF warnings.
     * 
     * @param link The link
     */
    public void setLogLink(String link)
    {
    }
    
    /**
     * @param connection the connection to set
     */
    public void setConnection(Connection connection)
    {
        this.connection = connection;
    }

    private Workflow getWorkflow()
    {
        if (this.workflow == null)
        {
            try
            {
                ContextVO contextVO;
                ContextHandler contextHandler = ServiceLocator.getContextHandler();
                InitialContext ctx = new InitialContext();
                XmlTransforming xmlTransforming = (XmlTransforming) ctx.lookup(XmlTransforming.SERVICE_NAME);
                
                String contextXml = contextHandler.retrieve(this.context);
                contextVO = xmlTransforming.transformToContext(contextXml);
        
                this.workflow = contextVO.getAdminDescriptor().getWorkflow();
            }
            catch (Exception e)
            {
                throw new RuntimeException(e);
            }
        }
        return this.workflow;
    }
    
    /**
     * Indicates whether the workflow of the currently used context is SIMPLE.
     * 
     * @return true if the workflow of the currently used context is SIMPLE.
     */
    public boolean getSimpleWorkflow()
    {
        return (getWorkflow() == Workflow.SIMPLE);
    }
}
