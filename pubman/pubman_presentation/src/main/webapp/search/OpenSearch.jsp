
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
	<f:view encoding="UTF-8" locale="#{InternationalizationHelper.userLocale}" xmlns:f="http://java.sun.com/jsf/core" xmlns:h="http://java.sun.com/jsf/html"  xmlns:f="http://java.sun.com/jsf/core" xmlns:h="http://java.sun.com/jsf/html" xmlns:ui="http://java.sun.com/jsf/facelets" xmlns:p="http://primefaces.org/ui">
		<f:loadBundle var="lbl" basename="Label"/>
		<f:loadBundle var="msg" basename="Messages"/>
		<f:loadBundle var="tip" basename="Tooltip"/>
		<h:outputText escape="false" value='&lt;?xml version="1.0" encoding="UTF-8" ?&gt;' />
		<OpenSearchDescription xmlns="http://a9.com/-/spec/opensearch/1.1/">
			<ShortName><h:outputText value="#{lbl.openSearch_shortDesc} #{Header.type}"/></ShortName>
			<Description><h:outputText value="#{lbl.openSearch_desc} #{lbl.openSearch_shortDesc} #{Header.type}"/>.</Description>
			<Tags><h:outputText value="#{lbl.openSearch_shortDesc}"/></Tags>
			<Contact>escidoc-dev-ext@gwdg.de</Contact>
				<h:outputText escape="false" value='&lt;Url type="text/html" template="' />
				<h:outputText value='#{ApplicationBean.pubmanInstanceUrl}#{ApplicationBean.appContext}#{Search.openSearchRequest}' />
				<h:outputText escape="false" value='" /&gt;' />
			<Image height="16" width="16" type="image/vnd.microsoft.icon"><h:outputText value="#{ApplicationBean.pubmanInstanceUrl}#{ApplicationBean.appContext}resources/pubman_favicon.ico" /></Image>
		</OpenSearchDescription>
	</f:view>
