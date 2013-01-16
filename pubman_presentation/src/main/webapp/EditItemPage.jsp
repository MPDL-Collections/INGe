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
 für wissenschaftlich-technische Information mbH and Max-Planck-
 Gesellschaft zur Förderung der Wissenschaft e.V.
 All rights reserved. Use is subject to license terms.
-->
<jsp:root version="2.1" xmlns:f="http://java.sun.com/jsf/core" xmlns:h="http://java.sun.com/jsf/html" xmlns:jsp="http://java.sun.com/JSP/Page" xmlns:rich="http://richfaces.org/rich" xmlns:a4j="http://richfaces.org/a4j"  xmlns:fn="http://java.sun.com/jsp/jstl/functions">

	<jsp:output doctype-root-element="html"
	       doctype-public="-//W3C//DTD XHTML 1.0 Transitional//EN"
	       doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd" /> 

	<jsp:directive.page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"/>
	<f:view locale="#{InternationalizationHelper.userLocale}">
		<f:loadBundle var="lbl" basename="Label" />
		<f:loadBundle var="msg" basename="Messages" />
		<f:loadBundle var="tip" basename="Tooltip" />
		<f:loadBundle var="genre" basename="#{EditItem.genreBundle}" />
		<html xmlns="http://www.w3.org/1999/xhtml">
			<head>
				<meta http-equiv="X-UA-Compatible" content="IE=EmulateIE8"/>
				<title><h:outputText value="#{ApplicationBean.appTitle}"/></title>
				<jsp:directive.include file="header/ui/StandardImports.jspf" />
				<script src="./resources/commonJavaScript/jquery/jquery.jdialog.min.js" language="JavaScript" type="text/javascript">;</script>
				
				<link rel="stylesheet" href="./resources/cc_license_style.css" />
			
			</head>
			<body lang="#{InternationalizationHelper.locale}">
				<h:outputText value="#{EditItemPage.beanName}" styleClass="noDisplay" />
				
				<h:form id="form1">
					<a4j:status id="a4jstatus" onstart="beforeAjaxRequest();" onstop="afterAjaxRequest();" />
					<div class="full wrapper">
						<h:inputHidden value="#{EditItemSessionBean.offset}" id="offset"></h:inputHidden>
					
						<jsp:directive.include file="header/Header.jspf" />
		
						<div id="content" class="full_area0 clear">
						<!-- begin: content section (including elements that visualy belong to the header (breadcrumb, headline, subheader and content menu)) -->
							<div class="clear">
								<div class="headerSection">
									<jsp:directive.include file="header/Breadcrumb.jspf" />
									<div id="contentSkipLinkAnchor" class="clear headLine">
										<!-- Headline starts here -->
										<h1><h:outputText value="#{lbl.EditItemPage}" /></h1>
										<!-- Headline ends here -->
									</div>
								</div>
								<div class="small_marginLIncl subHeaderSection">
									<div class="contentMenu"> <!-- content menu starts here -->
										<h:panelGroup layout="block" styleClass="free_area0 sub" rendered="#{!( (EditItemSessionBean.currentSubmission  == 'EASY_SUBMISSION') || (EditItemSessionBean.currentSubmission  == 'FULL_SUBMISSION') || (EditItemSessionBean.currentSubmission  == 'IMPORT') )}">
										<!-- content menu lower line starts here -->										
											&#160;
										<!-- content menu lower line ends here -->
										</h:panelGroup>
										
										<h:panelGroup layout="block" styleClass="free_area0 sub" rendered="#{( (EditItemSessionBean.currentSubmission  == 'EASY_SUBMISSION') || (EditItemSessionBean.currentSubmission  == 'FULL_SUBMISSION') || (EditItemSessionBean.currentSubmission  == 'IMPORT') )}" >
											<h:commandLink title="#{tip.submission_lnkEasySubmission}" action="#{EasySubmission.newEasySubmission}" rendered="#{EditItemSessionBean.currentSubmission  != 'EASY_SUBMISSION'}">
												<h:outputText value="#{lbl.submission_lnkEasySubmission}" rendered="#{DepositorWSSessionBean.newSubmission and ContextListSessionBean.depositorContextListSize>0}"/>
											</h:commandLink>
											<h:panelGroup styleClass=" " rendered="#{EditItemSessionBean.currentSubmission  == 'EASY_SUBMISSION'}">
												<h:outputText value="#{lbl.submission_lnkEasySubmission}" rendered="#{DepositorWSSessionBean.newSubmission and ContextListSessionBean.depositorContextListSize>0}"/>
											</h:panelGroup>
											<h:outputText styleClass="seperator void" />
											<h:commandLink title="#{tip.submission_lnkNewSubmission}" action="#{CreateItem.newSubmission}" rendered="#{EditItemSessionBean.currentSubmission  != 'FULL_SUBMISSION'}">
												<h:outputText value="#{lbl.submission_lnkNewSubmission}" rendered="#{DepositorWSSessionBean.newSubmission and ContextListSessionBean.depositorContextListSize>0}" />
											</h:commandLink>
												<h:panelGroup styleClass=" " rendered="#{EditItemSessionBean.currentSubmission  == 'FULL_SUBMISSION'}">
												<h:outputText value="#{lbl.submission_lnkNewSubmission}" rendered="#{DepositorWSSessionBean.newSubmission and ContextListSessionBean.depositorContextListSize>0}" />
											</h:panelGroup>
											<h:outputText styleClass="seperator void" />
											<h:commandLink title="#{tip.submission_lnkImport}" action="#{EasySubmission.newImport}" rendered="#{EditItemSessionBean.currentSubmission  != 'IMPORT'}">
												<h:outputText value="#{lbl.EasySubmission_lblFetchMetadata}" rendered="#{DepositorWSSessionBean.newSubmission and ContextListSessionBean.depositorContextListSize>0}"/>
											</h:commandLink>
											<h:panelGroup styleClass=" " rendered="#{EditItemSessionBean.currentSubmission  == 'IMPORT'}">
												<h:outputText value="#{lbl.EasySubmission_lblFetchMetadata}" rendered="#{DepositorWSSessionBean.newSubmission and ContextListSessionBean.depositorContextListSize>0}"/>
											</h:panelGroup>
											<h:outputText styleClass="seperator void"  rendered="#{LoginHelper.isModerator and DepositorWSSessionBean.newSubmission and ContextListSessionBean.depositorContextListSize>0}"/>
											<h:commandLink title="#{tip.submission_lnkMultipleImport}" action="#{MultipleImport.newImport}" rendered="#{LoginHelper.isModerator and DepositorWSSessionBean.newSubmission and ContextListSessionBean.depositorContextListSize>0}">
												<h:outputText value="#{lbl.submission_lnkMultipleImport}" rendered="#{DepositorWSSessionBean.newSubmission and ContextListSessionBean.depositorContextListSize>0}"/>
											</h:commandLink>
										</h:panelGroup>
									</div> <!-- content menu ends here -->
									<div class="subHeader">
										<!-- Subheadline starts here -->
										<h:outputText value="#{lbl.EditItem_lblItemVersionID} '#{EditItem.item.version.objectId}'." rendered="#{EditItem.item.version.objectId != null}"/><br/>
										<h:outputText value="#{lbl.EditItem_lblCollectionOfItem} '#{EditItem.contextName}'." /><br/>
										<h:outputText value="#{lbl.EditItem_lblItemDepositor} '#{EditItem.owner}'" rendered="#{EditItem.owner != null}"/>
										<h:outputText value="." rendered="#{EditItem.owner != null and EditItem.creationDate == null}"/>
										<h:outputText value=" --- #{EditItem.creationDate}" rendered="#{EditItem.creationDate != null}"/><br/>
										<h:outputText value="#{lbl.EditItem_lblItemLatestModifier} '#{EditItem.lastModifier}'" rendered="#{EditItem.lastModifier != null}"/>
										<h:outputText value="." rendered="#{EditItem.lastModifier != null and EditItem.lastModificationDate == null}"/>
										<h:outputText value=" --- #{EditItem.lastModificationDate}" rendered="#{EditItem.lastModificationDate != null}"/>
									</div>
									<h:panelGroup id="messages" styleClass="subHeader">
										
										<!-- Special validation messages for yearbook -->
										<h:panelGroup layout="block" styleClass="half_area2_p6 messageArea errorMessageArea clear" style="padding-top: 0px !important;" rendered="#{EditItem.item.validationReport!=null}">
											<h2><h:outputText value="#{lbl.Yearbook_validationMessageHeader}"/></h2>
											<ul>
											<a4j:repeat var="valitem" value="#{EditItem.item.validationReport.items}">
													<h:panelGroup rendered="#{valitem.restrictive}">
														<li class="messageWarn">
														<h:outputText value="#{msg[valitem.content]}"/>
														</li>
													</h:panelGroup>
													<h:panelGroup rendered="#{!valitem.restrictive}">
														<li class="messageStatus">
														<h:outputText value="#{msg[valitem.content]}"/>
														</li>
													</h:panelGroup>
											</a4j:repeat>
											</ul>	
									   </h:panelGroup>
										
										<h:panelGroup layout="block" styleClass="half_area2_p6 messageArea errorMessageArea absoluteMessageArea" rendered="#{EditItem.hasErrorMessages}">
											<input type="button" class="min_imgBtn fixErrorMessageBlockBtn" onclick="$pb(this).parents('.messageArea').removeClass('absoluteMessageArea'); $pb(this).hide();" />
											<h2><h:outputText value="#{lbl.warning_lblMessageHeader}" /></h2>
											<h:messages errorClass="messageError" warnClass="messageWarn" fatalClass="messageFatal" infoClass="messageStatus" layout="list" globalOnly="true" showDetail="false" showSummary="true" rendered="#{EditItem.hasMessages}"/>
										</h:panelGroup>
										<h:panelGroup layout="block" styleClass="half_area2_p6 messageArea infoMessageArea absoluteMessageArea" rendered="#{EditItem.hasMessages and !EditItem.hasErrorMessages}">
											<input type="button" class="min_imgBtn fixSuccessMessageBlockBtn" onclick="$pb(this).parents('.messageArea').removeClass('absoluteMessageArea'); $pb(this).hide();" />
											<h2><h:outputText value="#{lbl.info_lblMessageHeader}" /></h2>
											<h:messages errorClass="messageError" warnClass="messageWarn" fatalClass="messageFatal" infoClass="messageStatus" layout="list" globalOnly="true" showDetail="false" showSummary="true" rendered="#{EditItem.hasMessages}"/>
										</h:panelGroup>
										
									</h:panelGroup> <!-- Subheadline ends here -->
								</div>
							</div>
							<div class="full_area0">
								<div id="fullItem" class="full_area0 fullItem">
									<div class="full_area0 fullItemControls">
										<span class="full_area0_p5">
											<h:panelGroup styleClass="seperator" rendered="#{!(genre.sources_display == 'false' and genre.sources_form_id == 'full-submission' or  genre.sources_display == 'false' and genre.sources_form_id == 'all')}"/>
											<h:outputLink styleClass="free_area0" value="#form1:editSource" rendered="#{!(genre.sources_display == 'false' and genre.sources_form_id == 'full-submission' or  genre.sources_display == 'false' and genre.sources_form_id == 'all')}">
												<h:outputText value="#{genre.sources_source_basic_label}" converter="GenreLabelConverter" />
											</h:outputLink>
											
											<h:panelGroup styleClass="seperator" rendered="#{!(genre.legal_case_display == 'false' and genre.legal_case_form_id == 'full-submission' or  genre.legal_case_display == 'false' and genre.legal_case_form_id == 'all')}"/>
											<h:outputLink styleClass="free_area0" value="#form1:editLegalCase" rendered="#{!(genre.legal_case_display == 'false' and genre.legal_case_form_id == 'full-submission' or  genre.legal_case_display == 'false' and genre.legal_case_form_id == 'all')}">
												<h:outputText value="#{genre.legal_case_label}" converter="GenreLabelConverter" />
											</h:outputLink>
											
											<h:panelGroup styleClass="seperator" rendered="#{!(genre.events_display == 'false' and genre.events_form_id == 'full-submission' or  genre.events_display == 'false' and genre.events_form_id == 'all')}"/>
											<h:outputLink styleClass="free_area0" value="#form1:editEvent" rendered="#{!(genre.events_display == 'false' and genre.events_form_id == 'full-submission' or  genre.events_display == 'false' and genre.events_form_id == 'all')}">
												<h:outputText value="#{genre.events_label}" converter="GenreLabelConverter" />
											</h:outputLink>
											
											<h:panelGroup styleClass="seperator" rendered="#{!(genre.details_display == 'false' and genre.details_form_id == 'full-submission' or  genre.details_display == 'false' and genre.details_form_id == 'all')}"/>
											<h:outputLink styleClass="free_area0" value="#form1:editDetail" rendered="#{!(genre.details_display == 'false' and genre.details_form_id == 'full-submission' or  genre.details_display == 'false' and genre.details_form_id == 'all')}">
												<h:outputText value="#{genre.details_label}" converter="GenreLabelConverter" />
											</h:outputLink>
											
											<h:panelGroup styleClass="seperator" rendered="#{!(genre.content_display == 'false' and genre.content_form_id == 'full-submission' or  genre.content_display == 'false' and genre.content_form_id == 'all')}"/>
											<h:outputLink styleClass="free_area0" value="#form1:editContent" rendered="#{!(genre.content_display == 'false' and genre.content_form_id == 'full-submission' or  genre.content_display == 'false' and genre.content_form_id == 'all')}">
												<h:outputText value="#{genre.content_label}"  converter="GenreLabelConverter" />
											</h:outputLink>
											
											<h:panelGroup styleClass="seperator" rendered="#{!(genre.creator_person_organization_display == 'false' and genre.creator_person_organization_form_id == 'full-submission' or  genre.creator_person_organization_display == 'false' and genre.creator_person_organization_form_id == 'all')}"/>
											<h:outputLink styleClass="free_area0" value="#form1:editPerson" rendered="#{!(genre.creator_person_organization_display == 'false' and genre.creator_person_organization_form_id == 'full-submission' or  genre.creator_person_organization_display == 'false' and genre.creator_person_organization_form_id == 'all')}">
												<h:outputText value="#{genre.creator_person_organization_label}" converter="GenreLabelConverter" />
											</h:outputLink>
											
											<h:panelGroup styleClass="seperator" rendered="#{!(genre.locators_display == 'false' and genre.locators_form_id == 'full-submission' or  genre.locators_display == 'false' and genre.locators_form_id == 'all')}"/>
											<h:outputLink styleClass="free_area0" value="#form1:editLocator" rendered="#{!(genre.locators_display == 'false' and genre.locators_form_id == 'full-submission' or  genre.locators_display == 'false' and genre.locators_form_id == 'all')}">
												<h:outputText value="#{genre.locators_label}" converter="GenreLabelConverter" />
											</h:outputLink>
											
											<h:panelGroup styleClass="seperator" rendered="#{!(genre.files_display == 'false' and genre.files_form_id == 'full-submission' or  genre.files_display == 'false' and genre.files_form_id == 'all')}"/>
											<h:outputLink styleClass="free_area0" value="#form1:editFile" rendered="#{!(genre.files_display == 'false' and genre.files_form_id == 'full-submission' or  genre.files_display == 'false' and genre.files_form_id == 'all')}">
												<h:outputText value="#{genre.files_label}" converter="GenreLabelConverter" />
											</h:outputLink>
											
											<h:panelGroup styleClass="seperator"></h:panelGroup>
										</span>
									</div>
		
								 	<jsp:directive.include file="editItem/BasicGroup.jspf" />
									<jsp:directive.include file="editItem/FilesGroup.jspf" />
									<jsp:directive.include file="editItem/LocatorsGroup.jspf" />
									<jsp:directive.include file="editItem/PersOrgGroup.jspf" />
									<jsp:directive.include file="editItem/ContentGroup.jspf" />
									<jsp:directive.include file="editItem/DetailGroup.jspf" />
									<jsp:directive.include file="editItem/EventGroup.jspf" />
									<!--JUS content section -->
									<jsp:directive.include file="editItem/LegalCaseGroup.jspf" />
									<jsp:directive.include file="editItem/SourceGroup.jspf" />
									<p>&#160;</p>
									<div class="free_area0 xTiny_marginLIncl">
										<h:outputText value="* " />
										<h:commandLink id="lnkValidate2"  value="#{msg.mandatoryFieldFS1}" action="#{EditItem.validate}"/>
										<h:outputText value="#{msg.mandatoryFieldFS2}" />
									</div>			 
								</div>
								
								<div id="ImgFullItem">
									<div id="ImgFullItemLoad" class="noDisplay" style="position: fixed;">&#160;</div>
								</div>
								
								<div class="full_area0 formButtonArea">
									<h:commandLink styleClass="free_area1_p8 cancelButton xLarge_marginLIncl" id="lnkCancel" value="#{lbl.EditItem_lnkCancel}" action="#{EditItem.cancel}"/>
									<h:commandLink styleClass="free_area1_p8 activeButton" id="lnkDelete" binding ="#{EditItem.lnkDelete}" onclick="if(!confirm('#{msg.deleteMessage}'))return false;" value="#{lbl.EditItem_lnkDelete}" onmousedown="if(!confirmDelete('form1:EditItem'))return false;" action="#{EditItem.delete}"/>
									<h:commandLink styleClass="free_area1_p8 activeButton" id="lnkRelease" binding ="#{EditItem.lnkRelease}"  value="#{lbl.actionMenu_lnkRelease}" action="#{EditItem.saveAndSubmit}"/>
									<h:commandLink styleClass="free_area1_p8 activeButton" id="lnkReleaseReleasedItem" binding ="#{EditItem.lnkReleaseReleasedItem}"  value="#{lbl.actionMenu_lnkRelease}" action="#{EditItem.saveAndRelease}"/>
									<h:commandLink styleClass="free_area1_p8 activeButton" id="lnkAccept" binding ="#{EditItem.lnkAccept}"  value="#{lbl.EditItem_lnkAccept}" action="#{EditItem.saveAndAccept}"/>
									<h:commandLink styleClass="free_area1_p8 activeButton" id="lnkSaveAndSubmit" binding ="#{EditItem.lnkSaveAndSubmit}"  value="#{lbl.EditItem_lnkSaveAndSubmit}" action="#{EditItem.saveAndSubmit}"/>
									<h:commandLink styleClass="free_area1_p8 activeButton" id="lnkSave" binding ="#{EditItem.lnkSave}"  value="#{lbl.EditItem_lnkSave}" action="#{EditItem.save}"/>							
									<h:commandLink styleClass="free_area1_p8 activeButton" id="lnkValidate"  value="#{lbl.EditItem_lnkValidate}" action="#{EditItem.validate}"/>
								</div>
							</div>
						
						</div> <!-- end: content section -->
					</div> <!--  end: full wrapper -->
					
					<jsp:directive.include file="footer/Footer.jspf" />
					
				</h:form>
				
				<script type="text/javascript">
					function checkUpdatePersonFunction() {
						(typeof updatePersonUi == 'function') ?	updatePersonUi() :	setTimeout("checkUpdatePersonFunction()", 30);
					}
					$pb("input[id$='offset']").submit(function() {
						$pb(this).val($pb(window).scrollTop());
					});
					$pb(document).ready(function () {
						$pb(window).scrollTop($pb("input[id$='offset']").val());
						$pb(window).scroll(function(){$pb("input[id$='offset']").val($pb(window).scrollTop());});
						checkUpdatePersonFunction();
					});
				
					languageSuggestURL = '<h:outputText value="#{EditItem.suggestConeUrl}"/>iso639-3/query';
					journalSuggestURL = '<h:outputText value="#{EditItem.suggestConeUrl}"/>journals/query';
					subjectSuggestURL = '<h:outputText value="#{EditItem.suggestConeUrl}"/>$1/query?lang=en';
					personSuggestURL = '<h:outputText value="#{EditItem.suggestConeUrl}"/>persons/query?lang=*';
					organizationSuggestURL = 'OrganizationSuggest.jsp';
					journalDetailsBaseURL = '$1?format=json';
					personDetailsBaseURL = '$1?format=json<![CDATA[&]]>lang=$2';
					languageDetailsBaseURL = '$1?format=json<![CDATA[&]]>lang=$2';
					journalSuggestCommonParentClass = 'itemBlock';
					personSuggestCommonParentClass = 'suggestAnchor';
					journalSuggestTrigger = 'JOURNAL';
				</script>

			</body>
		</html>
	</f:view>
</jsp:root>