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
	<f:view encoding="UTF-8" xmlns="http://www.w3.org/1999/xhtml" locale="#{InternationalizationHelper.userLocale}" xmlns:f="http://java.sun.com/jsf/core" xmlns:h="http://java.sun.com/jsf/html" xmlns:ui="http://java.sun.com/jsf/facelets">
		<f:loadBundle var="lbl" basename="Label"/>
		<f:loadBundle var="msg" basename="Messages"/>
		<f:loadBundle var="tip" basename="Tooltip"/>
		<html>
			<h:head>
				<title><h:outputText value="#{ApplicationBean.appTitle}"/></title>
				<link rel="sword" type="application/xml" title="Sword Servicedocument Location" href="${ApplicationBean.pubmanInstanceUrl}/pubman/faces/sword-app/servicedocument"/>
				<meta name="description" content="${lbl.Pubman_descriptionMetaTag}"></meta>
				<ui:include src="header/ui/StandardImports.jspf" />
				<ui:include src="home/HomePageFeedLinks.jspf" />
			</h:head>
			<body lang="${InternationalizationHelper.locale}">
				<h:outputText value="#{HomePage.beanName}" styleClass="noDisplay" />
				<h:form id="form1">
					<div class="full wrapper">
						<h:inputHidden id="offset"></h:inputHidden>
		
						<!-- import header -->
						<ui:include src="header/Header.jspf" />
						<div id="content" class="full_area0 clear">
						<!-- begin: content section (including elements that visualy belong to the header (breadcrumb, headline, subheader and content menu)) -->
							<div class="clear">
								<div class="headerSection">
									
								<ui:include src="header/Breadcrumb.jspf" />
						
									<div id="contentSkipLinkAnchor" class="clear headLine">
										<!-- Headline starts here -->
										<h1><h:outputText value="#{lbl.HomePage}" /></h1>
										<!-- Headline ends here -->
									</div>
								</div>
								<div class="small_marginLIncl subHeaderSection">
									<div class="contentMenu">
									<!-- content menu starts here -->
										<div class="free_area0 sub">
										<!-- content menu upper line starts here -->
											&#160;
										<!-- content menu upper line ends here -->
										</div>
									<!-- content menu ends here -->
									</div>
									<div class="subHeader">
										<!-- Subheadline starts here -->
										<h:messages styleClass="singleMessage" errorClass="messageError" warnClass="messageWarn" fatalClass="messageFatal" infoClass="messageStatus" layout="list" globalOnly="true" showDetail="false" showSummary="true" rendered="#{HomePage.numberOfMessages == 1}"/>
										<h:panelGroup layout="block" styleClass="half_area2_p6 messageArea errorMessageArea" rendered="#{HomePage.hasErrorMessages and HomePage.numberOfMessages != 1}">
											<h2><h:outputText value="#{lbl.warning_lblMessageHeader}"/></h2>
											<h:messages errorClass="messageError" warnClass="messageWarn" fatalClass="messageFatal" infoClass="messageStatus" layout="list" globalOnly="true" showDetail="false" showSummary="true" rendered="#{HomePage.hasMessages}"/>
										</h:panelGroup>
										<h:panelGroup layout="block" styleClass="half_area2_p6 messageArea infoMessageArea" rendered="#{HomePage.hasMessages and !HomePage.hasErrorMessages and HomePage.numberOfMessages != 1}">
											<h2><h:outputText value="#{lbl.info_lblMessageHeader}"/></h2>
											<h:messages errorClass="messageError" warnClass="messageWarn" fatalClass="messageFatal" infoClass="messageStatus" layout="list" globalOnly="true" showDetail="false" showSummary="true" rendered="#{HomePage.hasMessages}"/>
										</h:panelGroup>
										<h:outputText value="&#160;" rendered="#{!HomePage.hasErrorMessages}" />
										<!-- Subheadline ends here -->
									</div>
								</div>
							</div>
							<div class="full_area0">
								<div class="full_area0 infoPage">
									<!-- Main Content -->
									
									<h:panelGroup styleClass="half_area0_p8 mainSection" rendered="#{!PubManSessionBean.loggedIn and InternationalizationHelper.homeContent!=null}">
										<h:outputText value="#{InternationalizationHelper.homeContent}" escape="false"/>
									</h:panelGroup>
									
									<h:panelGroup styleClass="half_area0_p8 mainSection" rendered="#{!PubManSessionBean.loggedIn and InternationalizationHelper.homeContent==null}">
										<ui:include src="home/StartPageLoggedOut.jspf" />
									</h:panelGroup>
									
									<h:panelGroup styleClass="half_area0_p8 mainSection" rendered="#{PubManSessionBean.loggedIn}">
										<ui:include src="home/StartPageLoggedIn.jspf" />
									</h:panelGroup>
									
									<!-- Side Panels -->
									<h:panelGroup styleClass="sideSectionArea">
										<h:panelGroup styleClass="free_area0_p8 sideSection">
											<ui:include src="home/LastReleased.jspf" />
											<h:panelGroup rendered="#{ApplicationBean.pubmanBlogFeedUrl != ''}" >
												<ui:include src="home/BlogIntegration.jspf"  />
											</h:panelGroup>
											<h:panelGroup>
												<div id="searchCloudDiv">&#160;</div>
											</h:panelGroup>
										</h:panelGroup>
									</h:panelGroup>
									
								</div>	
							</div>
						</div>
						<!-- end: content section -->
						<ui:include src="footer/Footer.jspf" />
						<script type="text/javascript">
							$("input[id$='offset']").submit(function() {
								$(this).val($(window).scrollTop());
							});
							$(document).ready(function () {
								$(window).scrollTop($("input[id$='offset']").val());
								$(window).scroll(function(){$("input[id$='offset']").val($(window).scrollTop())});
							});
						</script>
					</div> <!-- end: full wrapper -->
				</h:form>
			</body>
		</html>
	</f:view>
