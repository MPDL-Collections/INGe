<?xml version="1.0" encoding="UTF-8"?>
<!--

 CDDL HEADER START

 The contents of this file are subject to the terms of the
 Common Development and Distribution License, Version 1.0 only
 (the "License"). You may not use this file except in compliance
 with the License.

 You can obtain a copy of the license at license/ESCIDOC.LICENSE
 or http://www.escidoc.de/license.
 See the License for the specific language governing permissions
 and limitations under the License.

 When distributing Covered Code, include this CDDL HEADER in each
 file and include the License file at license/ESCIDOC.LICENSE.
 If applicable, add the following below this CDDL HEADER, with the
 fields enclosed by brackets "[]" replaced with your own identifying
 information: Portions Copyright [yyyy] [name of copyright owner]

 CDDL HEADER END


 Copyright 2006-2012 Fachinformationszentrum Karlsruhe Gesellschaft
 fÃ¼r wissenschaftlich-technische Information mbH and Max-Planck-
 Gesellschaft zur FÃ¶rderung der Wissenschaft e.V.
 All rights reserved. Use is subject to license terms.
-->


	
	<f:view locale="#{InternationalizationHelper.userLocale}" xmlns:f="http://java.sun.com/jsf/core" xmlns:h="http://java.sun.com/jsf/html" xmlns:ui="http://java.sun.com/jsf/facelets" xmlns:p="http://primefaces.org/ui">
		<f:loadBundle var="lbl" basename="Label"/>
		<f:loadBundle var="msg" basename="Messages"/>
		<f:loadBundle var="tip" basename="Tooltip"/>
			
		<div class="xHuge_area2_p8 messageArea" style="height: 28.37em; overflow-y: auto;">

			<input type="button" id="btnClose" onclick="$.closeDialog()" value=" " class="min_imgBtn quad_marginLIncl fixMessageBlockBtn"/>

			<h2><h:outputText value="#{lbl.import_workspace_details}"/></h2>
			
			<h:panelGroup styleClass="free_area0" style="margin-bottom: 0.56em;" rendered="#{ImportItemDetails.length == 0}">
				<span class="small_area0">
					<h:outputText value="#{msg.multiple_import_no_details}"/>
				</span>
			</h:panelGroup>
			
			<ui:repeat var="detail" value="#{ImportItemDetails.details}">
				<h:panelGroup styleClass="quad_area0" style="margin-bottom: 0.56em;">
					<span class="small_area0 endline">
						<h:outputText value="#{detail.status}"/>&#160;
					</span>
					<span class="small_area0 endline">
						<h:outputText value="#{detail.errorLevel}"/>&#160;
					</span>
					<span class="medium_area0 endline">
						<h:outputText value="#{detail.startDateFormatted}"/>&#160;
					</span>
					<span class="double_area0 endline">
						<h:outputText value="#{detail.localizedMessage}" converter="HTMLEscapeConverter" escape="false" rendered="#{detail.itemId == null}"/>
						<h:outputLink id="lnkDetail" value="#{detail.link}" rendered="#{detail.itemId != null}">
							<h:outputText value="#{detail.localizedMessage}" converter="HTMLEscapeConverter" escape="false"/>&#160;
						</h:outputLink>&#160;
					</span>
					
				</h:panelGroup>
			</ui:repeat>
		
		</div>
			
	</f:view>

