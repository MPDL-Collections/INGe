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
* Copyright 2006-2008 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/

package de.mpg.escidoc.services.importmanager;

import java.net.URL;
import java.util.Date;
import java.util.Vector;

/**
 * The attributes of an import source
 */

public class ImportSourceVO 
{
	//General source_______________________________________
	private String name;
	private String description;
	private URL url;
	private String type;

	private String encoding;
	private String harvestProtocol;
	private Date retryAfter;
	private int timeout;
	private int numberOfTries;
	private String status;

	//Metadata informations________________________________
	private String mdDesc;
	private URL mdUrl;
	private String mdFormat;
	private String mdLabel;

	//Full text informations_______________________________
	private Vector <FullTextVO> ftFormats;
	

	public ImportSourceVO(){
		
	}
	
	public Vector <FullTextVO> getFtFormats() {
		return this.ftFormats;
	}

	public void setFtFormats(Vector <FullTextVO> ftFormats) {
		this.ftFormats = ftFormats;
	}

	
	public String getName(){
		return this.name;
	}
	
	public void setName(String name){
		this.name = name;
	}

	public String getDescription(){
		return this.description;
	}
	
	public void setDescription(String description){
		this.description = description;
	}
	
	public URL getUrl(){
		return this.url;
	}
	
	public void setUrl(URL url){
		this.url = url;
	}
	
	public String getHarvestProtocol(){
		return this.harvestProtocol;
	}
	
	public void setHarvestProtocol(String harvestProtocol){
		this.harvestProtocol = harvestProtocol;
	}
	
	public Date getRetryAfter(){
		return this.retryAfter;
	}
	
	public void setRetryAfter(Date retryAfter){
		this.retryAfter = retryAfter;
	}
	
	public int getTimeout(){
		return this.timeout;
	}
	
	public void setTimeout(int timeout){
		this.timeout = timeout;
	}
	
	public void setUrl(int timeout){
		this.timeout = timeout;
	}
	
	public int getNumberOfTries(){
		return this.numberOfTries;
	}
	
	public void setNumberOfTries(int numberOfTries){
		this.numberOfTries = numberOfTries;
	}
	
	public URL getMdUrl(){
		return this.mdUrl;
	}
	
	public void setMdUrl(URL mdUrl){
		this.mdUrl = mdUrl;
	}
	
	public String getMdFormat(){
		return this.mdFormat;
	}
	
	public void setMdFormat(String mdFormat){
		this.mdFormat = mdFormat;
	}
	
	public String getEncoding(){
		return this.encoding;
	}
	
	public void setEncoding(String encoding){
		this.encoding = encoding;
	}
	
	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	public String getMdDesc() {
		return this.mdDesc;
	}

	public void setMdDesc(String mdDesc) {
		this.mdDesc = mdDesc;
	}
	
	public String getMdLabel() {
		return this.mdLabel;
	}

	public void setMdLabel(String mdLabel) {
		this.mdLabel = mdLabel;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

}