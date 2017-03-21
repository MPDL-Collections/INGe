<!DOCTYPE html>
<!--

 CDDL HEADER START

 The contents of this file are subject to the terms of the
 Common Development and Distribution License, Version 1.0 only
 (the "License"). You may not use this file except in compliance
 with the License.

 You can obtain a copy of the license at license/ESCIDOC.LICENSE
 or http://www.escidoc.org/license.
 See the License for the specific language governing permissions
 and limitations under the License.

 When distributing Covered Code, include this CDDL HEADER in each
 file and include the License file at license/ESCIDOC.LICENSE.
 If applicable, add the following below this CDDL HEADER, with the
 fields enclosed by brackets "[]" replaced with your own identifying
 information: Portions Copyright [yyyy] [name of copyright owner]

 CDDL HEADER END


 Copyright 2006-2012 Fachinformationszentrum Karlsruhe Gesellschaft
 für wissenschaftlich-technische Information mbH and Max-Planck-
 Gesellschaft zur Förderung der Wissenschaft e.V.
 All rights reserved. Use is subject to license terms.
-->


	 

	
	<f:view encoding="UTF-8" locale="#{InternationalizationHelper.userLocale}" xmlns:f="http://java.sun.com/jsf/core" xmlns:h="http://java.sun.com/jsf/html" xmlns:ui="http://java.sun.com/jsf/facelets" xmlns:p="http://primefaces.org/ui">
		<f:loadBundle var="lbl" basename="Label"/>
		<f:loadBundle var="msg" basename="Messages"/>
		<f:loadBundle var="tip" basename="Tooltip"/>
		<html xmlns="http://www.w3.org/1999/xhtml">
			<h:head>
				<title><h:outputText value="#{ApplicationBean.appTitle}"/></title>
				<ui:include src="header/ui/StandardImports.jspf" />
				
				<h:outputStylesheet name="commonJavaScript/jquery/css/jquery-ui-1.10.4.min.css"/>
				<h:outputScript name="commonJavaScript/jquery/jquery-ui-1.10.4.min.js"/>
				<!-- 
				<script src="./resources/commonJavaScript/jquery/jquery.jdialog.min.js" language="JavaScript" type="text/javascript">;</script>
				-->
				<style>
				.dialogNoTitleBar .ui-dialog-titlebar {display:none;}
				.dialogNoTitleBar	{background:none; border:none;}
				</style>
			</h:head>
			<body lang="${InternationalizationHelper.locale}">
				<script type="text/javascript">
				/* <![CDATA[ */
					var detailsAwaiting = '<tr class="full_area0 importDetails"><td colspan="8" class="full_area0"><div class="big_imgArea half_marginLIncl smallThrobber"></div></td></tr>';
					var currentDialog;
					
					function showDialog(detailsLink){
						fullItemReloadAjax();
						currentDialog = $('<div class="big_imgArea smallThrobber">&#160;</div>').load(detailsLink, function() {fullItemReloadStop();} ).dialog(
								{
									dialogClass: 'dialogNoTitleBar',
									modal:true, 
									closeOnEscape:true,  
									width: 'auto',
									resizable: false,
									draggable:false,
									close: function(event, ui)
									{ 
				            			$(this).dialog('destroy');
				        			} 
			        			});
						}
						
					/* ]]> */
				</script>
				
					<div class="full wrapper">
						<h:inputHidden id="offset"></h:inputHidden>
						<ui:include src="header/Header.jspf" />
						<h:form id="form1">
						<div id="content" class="full_area0 clear">
							<!-- begin: content section (including elements that visualy belong to the header (breadcrumb, headline, subheader and content menu)) -->

							<h:panelGroup layout="block" styleClass="clear">
			                    <h:panelGroup layout="block" styleClass="headerSection">
			                        <ui:include src="header/Breadcrumb.jspf" />
									<div id="contentSkipLinkAnchor" class="clear headLine">
										<!-- Headline starts here -->
										<h1><h:outputText value="#{lbl.import_workspace_title}"/></h1>
										<!-- Headline ends here -->
									</div>
			                    </h:panelGroup>
								<h:panelGroup layout="block" styleClass="small_marginLIncl subHeaderSection">
									<div class="contentMenu">
									<!-- content menu starts here -->
										<h:panelGroup layout="block" styleClass="free_area0 sub">
											<h:outputLink id="lnkMenuQAWorkspace" title="#{tip.chooseWorkspace_QAWorkspace}"  value="#{ApplicationBean.appContext}QAWSPage.jsp" rendered="#{LoginHelper.isModerator and ContextListSessionBean.moderatorContextListSize>0}">
												<h:outputText value="#{lbl.chooseWorkspace_optMenuQAWorkspace}" rendered="#{LoginHelper.isModerator and ContextListSessionBean.moderatorContextListSize>0}"/>
											</h:outputLink>
											
											<h:panelGroup id="txtMenuImportWorkspace" rendered="#{DepositorWSSessionBean.newSubmission and ContextListSessionBean.depositorContextListSize>0}">
												<h:outputText styleClass="seperator void"  />
												<h:outputText value="#{lbl.chooseWorkspace_optMenuImportWorkspace}" />
											</h:panelGroup>
											
											
											<h:outputText styleClass="seperator void" rendered="#{LoginHelper.isYearbookEditor}" />
											<h:outputLink id="lnkMenuYearbookWorkspace" title="#{tip.chooseWorkspace_YearbookWorkspace}" value="#{ApplicationBean.appContext}YearbookPage.jsp" rendered="#{LoginHelper.isYearbookEditor}">
												<h:outputText value="#{lbl.chooseWorkspace_optMenuYearbookWorkspace}"/>
											</h:outputLink>
											
											<h:outputText styleClass="seperator void" rendered="#{BreadcrumbItemHistorySessionBean.lastPageIdentifier != 'ReportWorkspacePage' and LoginHelper.isReporter and ContextListSessionBean.moderatorContextListSize>0}" />
											<h:outputLink id="lnkMenuReportWorkspace" title="#{tip.chooseWorkspace_ReportWorkspace}" value="#{ApplicationBean.appContext}ReportWorkspacePage.jsp" rendered="#{BreadcrumbItemHistorySessionBean.lastPageIdentifier != 'ReportWorkspacePage' and LoginHelper.isReporter and ContextListSessionBean.moderatorContextListSize>0}">
												<h:outputText value="#{lbl.chooseWorkspace_optMenuReportWorkspace}"/>
											</h:outputLink>
										</h:panelGroup>
										<h:panelGroup layout="block" styleClass="free_area0 sub action">
										<!-- content menu lower line starts here -->
											
										<!-- content menu lower line ends here -->
										</h:panelGroup>
									<!-- content menu ends here -->
									</div>
									<h:panelGroup layout="block" styleClass="subHeader" rendered="false">
										<!-- Subheadline starts here -->
									 	
										<!-- Subheadline ends here -->
									</h:panelGroup>
								</h:panelGroup>
			              	</h:panelGroup>
							<div class="full_area0">
								<table class="full_area0 itemList listBackground loggedIn"
									style="border-collapse: collapse;">
									<thead style="text-align: left; vertical-align: top;">
										<tr class="full_area0 listHeader">
											<th class="tiny_area0">&#160;</th>
											<th class="free_area0 endline status statusArea">
												<h:panelGroup styleClass="seperator"></h:panelGroup> 
												<h:outputLink id="lnkSort1" value="ImportWorkspace.jsp?sortColumn=STATUS&amp;currentColumn=#{ImportWorkspace.sortColumn}&amp;currentDirection=#{ImportWorkspace.sortDirection}" rendered="#{ImportWorkspace.sortColumn != 'STATUS'}">
													<h:outputText styleClass="medium_area0_p8" value="#{lbl.import_workspace_status}" />
												</h:outputLink> 
												<h:outputLink id="lnkSort2" value="ImportWorkspace.jsp?sortColumn=STATUS&amp;currentColumn=#{ImportWorkspace.sortColumn}&amp;currentDirection=#{ImportWorkspace.sortDirection}" rendered="#{ImportWorkspace.sortColumn == 'STATUS' and ImportWorkspace.sortDirection == 'ASCENDING'}">
													<h:outputText styleClass="medium_area0_p8 ascSort" value="#{lbl.import_workspace_status}" />
												</h:outputLink> 
												<h:outputLink id="lnkSort3" value="ImportWorkspace.jsp?sortColumn=STATUS&amp;currentColumn=#{ImportWorkspace.sortColumn}&amp;currentDirection=#{ImportWorkspace.sortDirection}" rendered="#{ImportWorkspace.sortColumn == 'STATUS' and ImportWorkspace.sortDirection == 'DESCENDING'}">
													<h:outputText styleClass="medium_area0_p8 desSort" value="#{lbl.import_workspace_status}" />
												</h:outputLink>
											</th>
											<th class="large_area0">
												<h:panelGroup styleClass="seperator"></h:panelGroup> 
												<h:outputLink id="lnkSort4" value="ImportWorkspace.jsp?sortColumn=NAME&amp;currentColumn=#{ImportWorkspace.sortColumn}&amp;currentDirection=#{ImportWorkspace.sortDirection}" rendered="#{ImportWorkspace.sortColumn != 'NAME'}">
													<h:outputText styleClass="large_area0_p8" value="#{lbl.import_workspace_name}" />
												</h:outputLink> 
												<h:outputLink id="lnkSort5" value="ImportWorkspace.jsp?sortColumn=NAME&amp;currentColumn=#{ImportWorkspace.sortColumn}&amp;currentDirection=#{ImportWorkspace.sortDirection}" rendered="#{ImportWorkspace.sortColumn == 'NAME' and ImportWorkspace.sortDirection == 'ASCENDING'}">
													<h:outputText styleClass="large_area0_p8 ascSort" value="#{lbl.import_workspace_name}" />
												</h:outputLink> 
												<h:outputLink id="lnkSort6" value="ImportWorkspace.jsp?sortColumn=NAME&amp;currentColumn=#{ImportWorkspace.sortColumn}&amp;currentDirection=#{ImportWorkspace.sortDirection}" rendered="#{ImportWorkspace.sortColumn == 'NAME' and ImportWorkspace.sortDirection == 'DESCENDING'}">
													<h:outputText styleClass="large_area0_p8 desSort" value="#{lbl.import_workspace_name}" />
												</h:outputLink>
											</th>
											<th class="large_area0">
												<h:panelGroup styleClass="seperator"></h:panelGroup> 
												<h:outputLink id="lnkSort7" value="ImportWorkspace.jsp?sortColumn=FORMAT&amp;currentColumn=#{ImportWorkspace.sortColumn}&amp;currentDirection=#{ImportWorkspace.sortDirection}" rendered="#{ImportWorkspace.sortColumn != 'FORMAT'}">
													<h:outputText styleClass="large_area0_p8" value="#{lbl.import_workspace_format}" />
												</h:outputLink> 
												<h:outputLink id="lnkSort8" value="ImportWorkspace.jsp?sortColumn=FORMAT&amp;currentColumn=#{ImportWorkspace.sortColumn}&amp;currentDirection=#{ImportWorkspace.sortDirection}" rendered="#{ImportWorkspace.sortColumn == 'FORMAT' and ImportWorkspace.sortDirection == 'ASCENDING'}">
													<h:outputText styleClass="large_area0_p8 ascSort" value="#{lbl.import_workspace_format}" />
												</h:outputLink> 
												<h:outputLink id="lnkSort9" value="ImportWorkspace.jsp?sortColumn=FORMAT&amp;currentColumn=#{ImportWorkspace.sortColumn}&amp;currentDirection=#{ImportWorkspace.sortDirection}" rendered="#{ImportWorkspace.sortColumn == 'FORMAT' and ImportWorkspace.sortDirection == 'DESCENDING'}">
													<h:outputText styleClass="large_area0_p8 desSort" value="#{lbl.import_workspace_format}" />
												</h:outputLink>
											</th>
											<th class="large_area0">
												<h:panelGroup styleClass="seperator"></h:panelGroup> 
												<h:outputLink id="lnkSort10" value="ImportWorkspace.jsp?sortColumn=STARTDATE&amp;currentColumn=#{ImportWorkspace.sortColumn}&amp;currentDirection=#{ImportWorkspace.sortDirection}" rendered="#{ImportWorkspace.sortColumn != 'STARTDATE'}">
													<h:outputText styleClass="large_area0_p8" value="#{lbl.import_workspace_startdate}" />
												</h:outputLink> <h:outputLink id="lnkSort11" value="ImportWorkspace.jsp?sortColumn=STARTDATE&amp;currentColumn=#{ImportWorkspace.sortColumn}&amp;currentDirection=#{ImportWorkspace.sortDirection}" rendered="#{ImportWorkspace.sortColumn == 'STARTDATE' and ImportWorkspace.sortDirection == 'ASCENDING'}">
													<h:outputText styleClass="large_area0_p8 ascSort" value="#{lbl.import_workspace_startdate}" />
												</h:outputLink> 
												<h:outputLink id="lnkSort12" value="ImportWorkspace.jsp?sortColumn=STARTDATE&amp;currentColumn=#{ImportWorkspace.sortColumn}&amp;currentDirection=#{ImportWorkspace.sortDirection}" rendered="#{ImportWorkspace.sortColumn == 'STARTDATE' and ImportWorkspace.sortDirection == 'DESCENDING'}">
													<h:outputText styleClass="large_area0_p8 desSort" value="#{lbl.import_workspace_startdate}" />
												</h:outputLink>
											</th>
											<th class="large_area0">
												<h:panelGroup styleClass="seperator"></h:panelGroup> 
												<h:outputLink id="lnkSort13" value="ImportWorkspace.jsp?sortColumn=ENDDATE&amp;currentColumn=#{ImportWorkspace.sortColumn}&amp;currentDirection=#{ImportWorkspace.sortDirection}" rendered="#{ImportWorkspace.sortColumn != 'ENDDATE'}">
													<h:outputText styleClass="large_area0_p8" value="#{lbl.import_workspace_enddate}" />
												</h:outputLink> 
												<h:outputLink id="lnkSort14" value="ImportWorkspace.jsp?sortColumn=ENDDATE&amp;currentColumn=#{ImportWorkspace.sortColumn}&amp;currentDirection=#{ImportWorkspace.sortDirection}" rendered="#{ImportWorkspace.sortColumn == 'ENDDATE' and ImportWorkspace.sortDirection == 'ASCENDING'}">
													<h:outputText styleClass="large_area0_p8 ascSort" value="#{lbl.import_workspace_enddate}" />
												</h:outputLink> 
												<h:outputLink id="lnkSort15" value="ImportWorkspace.jsp?sortColumn=ENDDATE&amp;currentColumn=#{ImportWorkspace.sortColumn}&amp;currentDirection=#{ImportWorkspace.sortDirection}" rendered="#{ImportWorkspace.sortColumn == 'ENDDATE' and ImportWorkspace.sortDirection == 'DESCENDING'}">
													<h:outputText styleClass="large_area0_p8 desSort" value="#{lbl.import_workspace_enddate}" />
												</h:outputLink></th>
											<th class="large_area0">
												<h:panelGroup styleClass="seperator"></h:panelGroup> 
												<h:outputText styleClass="large_area0_p8" value="#{lbl.import_workspace_details}" />
											</th>
											<th class="large_area0 endline">
												<h:panelGroup styleClass="seperator"></h:panelGroup> 
												<h:outputText styleClass="large_area0_p8" value="#{lbl.import_workspace_actions}" />
											</th>
										</tr>
									</thead>
									<tbody style="text-align: left; vertical-align: top;">
										<ui:repeat var="import" value="#{ImportWorkspace.imports}" varStatus="status">
											<h:panelGroup>
												<tr class="full_area0 listItem">
													<td class="tiny_area0 endline">
														&#160;
													</td>
													<td class="free_area0 endline status">
														<h:panelGroup styleClass="seperator"></h:panelGroup> 
														<h:panelGroup styleClass="free_area0 endline statusArea">
															<h:panelGroup layout="block" styleClass="big_imgArea statusIcon #{import.status} import#{import.status}#{import.errorLevel}" />
															<h:outputLabel id="lblErrorLevel" styleClass="free_area0_p3 medium_label endline" title="#{import.errorLevel}">
																<h:panelGroup rendered="#{!import.finished}">
																	<h:outputText value="#{import.percentage}" />% - </h:panelGroup>
																<h:outputText value="#{import.status}" />
															</h:outputLabel>
															<h:inputHidden id="inpImportLogLink" value="#{import.logLink}" />
														</h:panelGroup>
													</td>
													<td class="free_area0 endline">
														<h:panelGroup styleClass="seperator"></h:panelGroup> 
														<span class="large_area0_p8"> 
															<h:outputLink id="lnkItems" value="#{import.myItemsLink}" rendered="#{import.importedItems}">
																<h:outputText value="#{import.message}" />
															</h:outputLink> 
															<h:outputText value="#{import.message}" rendered="#{!import.importedItems}" /> 
														</span>
													</td>
													<td class="free_area0 endline">
														<h:panelGroup styleClass="seperator"></h:panelGroup> 
														<span class="large_area0_p8"> 
															<h:outputText value="#{ImportWorkspace.getFormatLabel(import)}" />
															&#160; 
														</span>
													</td>
													<td class="free_area0 endline">
														<h:panelGroup styleClass="seperator"></h:panelGroup> 
														<span class="large_area0_p8"> 
															<h:outputText value="#{import.startDateFormatted}" />
															&#160; 
														</span>
													</td>
													<td class="free_area0 endline">
														<h:panelGroup styleClass="seperator"></h:panelGroup> 
														<span class="large_area0_p8"> 
															<h:outputText value="#{import.endDateFormatted}" />
															&#160; 
														</span>
													</td>
													<td class="free_area0 endline">
														<h:panelGroup styleClass="seperator"></h:panelGroup> 
														<span class="large_area0_p8 detailsLinkArea"> 
															<h:inputHidden id="inpImportItemsLink" value="#{import.itemsLink}" /> 
															<a onclick="if(!$(this).parents('tr').next('tr').hasClass('importDetails')) {$(this).parents('tr').after(detailsAwaiting); $(this).parents('tr').next('.importDetails').find('td').load($(this).siblings('input').val())} else {$(this).parents('tr').next('.importDetails').remove();}">
																<h:outputText value="#{lbl.import_workspace_detailsView}" />
															</a> 
														</span>
													</td>
													<td class="free_area0 endline">
														<h:panelGroup styleClass="seperator"></h:panelGroup> 
														<span class="large_area0 endline"> 
															<h:panelGroup rendered="false" styleClass="large_area0_p8 noPaddingTopBottom endline">
																<h:outputText value="#{import.errorLevel}" />
															</h:panelGroup> 
															<h:panelGroup rendered="#{import.finished}">
																<h:commandLink id="lnkRemove" title="#{tip.import_workspace_remove_import}" styleClass="small_area0_p8 noPaddingTopBottom endline" action="#{import.remove}" onclick="fullItemReloadAjax();">
																	<h:outputText value="#{lbl.import_workspace_remove_import}" />
																</h:commandLink>
			
																<h:commandLink id="lnkDeleteAll" title="#{tip.import_workspace_delete_items}" styleClass="small_area0_p8 noPaddingTopBottom endline" action="#{import.deleteAll}" rendered="#{import.importedItems}" onclick="fullItemReloadAjax();">
																	<h:outputText value="#{lbl.import_workspace_delete_items}" />
																</h:commandLink>
																
																<h:commandLink id="lnkSubmitAll" title="#{tip.import_workspace_submit_items}" styleClass="small_area0_p8 noPaddingTopBottom endline" action="#{import.submitAll}" rendered="#{import.importedItems and !import.simpleWorkflow and !LoginHelper.isModerator}">
																	<h:outputText value="#{lbl.import_workspace_submit_items}" />
																</h:commandLink>
			
																<h:commandLink id="lnkSubmitAndReleaseAll" title="#{tip.import_workspace_submit_release_items}" styleClass="large_area0_p8 noPaddingTopBottom endline" action="#{import.submitAndReleaseAll}" rendered="#{import.importedItems and (LoginHelper.isModerator or import.simpleWorkflow)}" onclick="fullItemReloadAjax();">
																	<h:outputText
																		value="#{lbl.import_workspace_submit_release_items}" />
																</h:commandLink>
															</h:panelGroup> 
														</span>
													</td>
												</tr>
											</h:panelGroup>
										</ui:repeat>
									</tbody>
								</table>
							</div>
							<!-- end: content section -->
						</div>
						</h:form>			
					</div>
					<ui:include src="footer/Footer.jspf" />
				
				<script type="text/javascript">
					function reloadImports() {
						$('.listItem').find('.statusArea').find('div:not(.FINISHED)').siblings('input').each(
							function(i,ele) {
								$.get($(ele).val(), function(data){
									$(ele).parents('tr').replaceWith($(data).find('tr'));
								});
							}
						);
						window.setTimeout("reloadImports()", 2000);
					}
					function reloadDetails() {
						$('.listItem').find('.statusArea').find('div:not(.FINISHED),span.ajaxedImport').parents('tr').next('.importDetails').each(
							function(i,ele) {	
								$.get($(ele).prev('.listItem').find('.detailsLinkArea').find('input').val(), function(data) {
									$(ele).children('td').empty().append(data);
								});
								if(($(ele).prev('.listItem').find('.statusArea').find('.FINISHED').length != 0 ) &amp;&amp; ($(ele).prev('.listItem').find('.ajaxedImport').length != 0)) {
									$(ele).prev('.listItem').find('.ajaxedImport').removeClass('ajaxedImport');
								}
							}
						);
						window.setTimeout("reloadDetails()", 5000);
					}
					window.setTimeout("reloadImports()", 2000);
					window.setTimeout("reloadDetails()", 5000);
				</script>
			</body>
		</html>
	</f:view>

