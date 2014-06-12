<!DOCTYPE html>
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


	 

	
	<f:view encoding="UTF-8" locale="#{InternationalizationHelper.userLocale}" xmlns:f="http://java.sun.com/jsf/core" xmlns:h="http://java.sun.com/jsf/html" xmlns:ui="http://java.sun.com/jsf/facelets" xmlns:p="http://primefaces.org/ui">
			<f:loadBundle var="lbl" basename="Label"/>
			<f:loadBundle var="msg" basename="Messages"/>
			<f:loadBundle var="tip" basename="Tooltip"/>
		<html xmlns="http://www.w3.org/1999/xhtml">
			<h:head>

				<title><h:outputText value="#{ApplicationBean.appTitle}"/></title>

				<ui:include src="header/ui/StandardImports.jspf" />


			</h:head>
			<body lang="${InternationalizationHelper.locale}">
			<h:outputText value="#{SubmitItemPage.beanName}" styleClass="noDisplay" />
			<h:form >
			<div class="full wrapper">
			<h:inputHidden id="offset"></h:inputHidden>
			
				<ui:include src="header/Header.jspf" />

				<div id="content" class="full_area0 clear">
				<!-- begin: content section (including elements that visualy belong to the header (breadcrumb, headline, subheader and content menu)) -->
					<div class="clear">
						<div class="headerSection">
							
						<ui:include src="header/Breadcrumb.jspf" />
				
							<div id="contentSkipLinkAnchor" class="clear headLine">
								<!-- Headline starts here -->
								<h1>
									<h:outputText value="#{lbl.SubmitItemPage_Submit}" rendered="#{SubmitItem.isStandardWorkflow and !SubmitItem.isSubmitted}"/>
									<h:outputText value="#{lbl.SubmitItemPage_Release}" rendered="#{SubmitItem.isSimpleWorkflow || SubmitItem.isSubmitted}"/>
								</h1>
								<!-- Headline ends here -->
							</div>
						</div>
						<div class="small_marginLIncl subHeaderSection">
							<div class="contentMenu">
							<!-- content menu starts here -->
								<div class="free_area0 sub">
								<!-- content menu lower line starts here -->										
									&#160;
								<!-- content menu lower line ends here -->
								</div>
							<!-- content menu ends here -->
							</div>
							<div class="subHeader">
								<!-- Subheadline starts here -->
									&#160;
								<!-- Subheadline ends here -->
							</div>
						</div>
					</div>			
					<div class="full_area0">
						<div class="full_area0 fullItem">

							<ui:include src="submitItem/SubmitItem.jspf" />

						</div>
						<div class="full_area0 formButtonArea">

							<h:commandLink styleClass="free_area1_p8 cancelButton xLarge_marginLIncl" id="lnkCancel" value="#{lbl.SubmitItem_lnkCancel}" action="#{SubmitItem.cancel}" onclick="fullItemReloadAjax();"/>
							<h:commandLink styleClass="free_area1_p8 activeButton" id="lnkSave" value="#{lbl.SubmitItem_lnkSubmit}" action="#{SubmitItem.submit}" rendered="#{SubmitItem.isStandardWorkflow and !SubmitItem.isSubmitted}" onclick="fullItemReloadAjax();" />
							<h:commandLink styleClass="free_area1_p8 activeButton" id="lnkRelease" value="#{lbl.SubmitItem_lnkRelease}" action="#{SubmitItem.submit}" rendered="#{SubmitItem.isSimpleWorkflow || SubmitItem.isSubmitted}" onclick="fullItemReloadAjax();" />
				
						</div>
					</div>
				<!-- end: content section -->
				</div>
			</div>
			<ui:include src="footer/Footer.jspf" />
			</h:form>
			<script type="text/javascript">
				$("input[id$='offset']").submit(function() {
					$(this).val($(window).scrollTop());
				});
				$(document).ready(function () {
					$(window).scrollTop($("input[id$='offset']").val());
					$(window).scroll(function(){$("input[id$='offset']").val($(window).scrollTop());});
				});
			</script>
			</body>
		</html>
	</f:view>

