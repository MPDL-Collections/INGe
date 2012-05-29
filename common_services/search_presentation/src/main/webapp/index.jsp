<%
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
* fï¿½r wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Fï¿½rderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/
%>
<%@page import="de.mpg.escidoc.services.framework.PropertyReader"%>
<%
	//response.sendRedirect("services");
%>

<jsp:directive.page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"/>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<title>eSciDoc SearchAndExport Service</title>
		
		<link href="<%= PropertyReader.getProperty("escidoc.pubman.common.presentation.url") %>resources/cssFramework/main.css" type="text/css" rel="stylesheet"/>	
		<link href="<%= PropertyReader.getProperty("escidoc.pubman.stylesheet.contrast.url") %>" id="HighContrast" type="text/css" title="high contrast" rel="alternate stylesheet"/>
		<link href="<%= PropertyReader.getProperty("escidoc.pubman.stylesheet.classic.url") %>" id="Classic" type="text/css" title="classic" rel="alternate stylesheet"/>	
		<% if ("true".equals(PropertyReader.getProperty("escidoc.pubman.stylesheet.special.apply"))) { %><link href="<%= PropertyReader.getProperty("escidoc.pubman.stylesheet.special.url") %>" id="Special" type="text/css" title="special" rel="alternate stylesheet"/><% } %>	
		<link href="<%= PropertyReader.getProperty("escidoc.pubman.stylesheet.standard.url") %>" id="Standard" type="text/css" title="blue" rel="stylesheet"/>
		
		<link rel="SHORTCUT ICON" href="/pubman/resources/favicon.ico"/>
		
		<script src="/pubman/resources/commonJavaScript/jquery/jquery.min.js" language="JavaScript" type="text/javascript">;</script>
		<script src="/pubman/resources/commonJavaScript/componentJavaScript/eSciDoc_full_item.js" language="JavaScript" type="text/javascript">;</script>
		<script type="text/javascript">$(document).ready(function(){installFullItem();});</script>

		<script language="JavaScript" type="text/javascript">
			  function applyCookieStyle() {
					var cookieValue = ""
					var cookie = "layout=";
					var dc = document.cookie;
					if (dc.length > 0) {
						var start = dc.indexOf(cookie);
						if (start != -1) {
							start += cookie.length;
							var stop = dc.indexOf(";", start);
							if (stop == -1) stop = dc.length;
							cookieValue = unescape(dc.substring(start,stop));
						}
					}
					var enableHiddenShemes = false;
					cookie = "enableHiddenSchemes=";
					if (dc.length > 0) {
						var start = dc.indexOf(cookie);
						if (start != -1) {
							start += cookie.length;
							var stop = dc.indexOf(";", start);
							if (stop == -1) stop = dc.length;
							if(unescape(dc.substring(start,stop)) == 'true') {enableHiddenShemes = true;};
						}
					}
					
					var el = null;
					
					if (cookieValue != "" && document.getElementsByTagName && document.getElementById(cookieValue)) {
						el = document.getElementsByTagName("link");
						for (var i = 0; i < el.length; i++ ) {
							if (el[i].getAttribute("rel").indexOf("style") != -1 && el[i].getAttribute("id") == cookieValue && enableHiddenShemes && (el[i].getAttribute("title") == null || el[i].getAttribute("title") == "" ) ) {
								el[i].setAttribute("title", el[i].getAttribute("id"));
							}
							if (el[i].getAttribute("rel").indexOf("style") != -1 && el[i].getAttribute("id")) {
								el[i].disabled = true;
								if (el[i].getAttribute("id") == cookieValue) el[i].disabled = false;
							}
						}
					} else if ( (!cookieValue || (cookieValue && !document.getElementById(cookieValue))) && document.getElementsByTagName ) {
						el = document.getElementsByTagName("link"); 
						for (var j = 0; j < el.length; j++ ) {
							if (el[j].id && el[j].rel == 'alternate stylesheet' && el[j].title && el[j].type == "text/css") {
								el[j].disabled = true;
							} else if (el[j].id && el[j].rel == 'stylesheet' && el[j].title && el[j].type == "text/css") {
								el[j].disabled = false;
							}
						}
					} 
					
					setStyleCookie();
				}
			
				function setStyleCookie() {
					var cookieValue = "Standard";
					if(document.getElementsByTagName) {
						var el = document.getElementsByTagName("link");
						for (var i = 0; i < el.length; i++ ) {
							var enabledCounter = 0;
							if (el[i].getAttribute("rel").indexOf("style") != -1 && el[i].getAttribute("id") && el[i].getAttribute("title") && el[i].disabled == false && enabledCounter == 0) {
								cookieValue = el[i].getAttribute("id");
								enabledCounter++;
							}
						}
					}
					var now = new Date();
					var exp = new Date(now.getTime() + (1000*60*60*24*30));
					if(cookieValue != "") {
						document.cookie = "layout=" + escape(cookieValue) + ";" +
											"expires=" + exp.toGMTString() + ";" +
											"path=/";
					}
				}
				applyCookieStyle();
				window.onunload=function(e){setStyleCookie();};
		</script>

		<link rel="search" href="opensearch_apa_html_all.xml" type="application/opensearchdescription+xml" title="eSciDoc SearchAndExport APA" />
		<link rel="search" href="opensearch_endnote_all.xml" type="application/opensearchdescription+xml" title="eSciDoc SearchAndExport EndNote" />

	</head>
	<body lang="en">
		<div class="full wrapper">
			<!-- start: skip link navigation -->
				<a href="#mainMenuSkipLinkAnchor" title="Skiplink to the main menu." class="skipLink">Skip to the main menu.</a>
				<a href="#contentSkipLinkAnchor" title="Skiplink to the page content." class="skipLink">Skip to the page content.</a>
				<a href="#metaMenuSkipLinkAnchor" title="Skiplink to the meta menu." class="skipLink">Skip to the meta menu.</a>
			<!-- end: skip link navigation -->
			<div class="full_area0 header alternateHeader clear">
				<!-- begin: short header section (including meta menu and main menu)-->
		
					<div id="metaMenuSkipLinkAnchor" class="full_area0 metaMenu">
					<!-- meta Menu starts here -->
					
						<!-- logo alternate area starts here || Please use this area only in short headers without logo -->
						<div class="free_area0 small_marginLExcl logoAlternate">
							<a href="index.jsp" title="Go to the start page.">
								<span>eSciDoc</span>
								<span>SearchAndExport Service</span>
							</a>
						</div>
						<!-- logo alternate area ends here -->
					<!-- meta Menu ends here -->
					</div>
					<div class="full_area0 searchExportLogo">&#160;</div>
					<div id="mainMenuSkipLinkAnchor" class="full_area0 mainMenu">
						<!-- main Menu starts here -->

						<span class="free_area0">Description</span>
					
						<a href="SearchAndExport_rest_sample.jsp" class="free_area0" title="Go to the REST example page.">REST Example</a>

						<!-- main Menu ends here -->
					</div>
						
				<!-- end: short header section -->
			</div>
			<div id="content" class="full_area0 clear">
				<!-- begin: content section (including elements that visualy belong to the header (breadcrumb, headline, subheader and content menu)) -->
					<div class="headerSection">
<%--							
						<div class="clear breadcrumb">
							<!-- Breadcrumb starts here -->
							<ol>
								<li>&#160;</li>
							</ol>
							<!-- Breadcrumb ends here -->
						</div>
--%>			
						<div id="contentSkipLinkAnchor" class="clear headLine">
							<!-- Headline starts here -->
							<h1>eSciDoc SearchAndExport Service</h1>
							<!-- Headline ends here -->
						</div>
					</div>
<%--				
					<div class="small_marginLIncl subHeaderSection">
						<div class="contentMenu">
							<!-- content menu starts here -->
								<div class="free_area0 sub">
								<!-- content menu upper line starts here -->
									&#160;
								<!-- content menu upper line ends here -->
								</div>
								<div class="free_area0 sub action">
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
--%>
					<div class="full_area0 fullItem">
						<div class="full_area0 itemBlock noTopBorder">
							<h3 class="xLarge_area0_p8 endline blockHeader">
								&#160;
							</h3>
							<div class="free_area0 itemBlockContent endline">
								<div class="free_area0 endline itemLine noTopBorder">
									<span class="free_area0_p2 xTiny_marginLExcl endline">
										<p>The SearchAndExport interface is a REST interface, with which one can extract data from PubMan <a href="http://colab.mpdl.mpg.de/mediawiki/ESciDoc_Services_Search%26Export" target="_blank">[more]</a>.</p>
									</span>
								</div>
							</div>							
						</div>
						<div class="full_area0 itemBlock">
							<h3 class="xLarge_area0_p8 endline blockHeader">
								REST Interface Description
							</h3>
							<span class="seperator"></span>
							<div class="free_area0 itemBlockContent endline">
								<div class="free_area0 endline itemLine noTopBorder">
									<span class="free_area0_p2 xTiny_marginLExcl endline">
										<p>This service expects a HTTP GET request containing following parameters:</p>
									</span>
									<b class="xLarge_area0_p8 endline labelLine clear">
										cqlQuery<span class="noDisplay">: </span>
									</b>
									<span class="xHuge_area0 xTiny_marginLExcl endline">
										defines CQL search request (required)
									</span>
									<b class="xLarge_area0_p8 endline labelLine clear">
										exportFormat<span class="noDisplay">: </span>
									</b>
									<span class="xHuge_area0 xTiny_marginLExcl endline">
										can be <i>APA</i>, <i>APA(CJK)</i>, <i>AJP</i>, <i>BIBTEX</i>, <i>ENDNOTE</i> or <i>eSciDoc XML</i>. (Default:&#160;<i>ENDNOTE</i>)
									</span>
									<b class="xLarge_area0_p8 endline labelLine clear">
										outputFormat<span class="noDisplay">: </span>
									</b>
									<span class="xHuge_area0 xTiny_marginLExcl endline">
										is only relevant for <i>APA</i>, <i>APA(CJK)</i>, <i>AJP</i>, <i>JUS</i> exportFormat and can be <i>pdf, html (plain, linked and styled), rtf, odt, snippet v5.x (escidoc xml metadata set before v6.0), snippet v6.x (escidoc xml metadata set v6.0 and higher)</i>. (Default:&#160;<i>pdf</i>). 
										<i>ENDNOTE</i> will be always returned as plain text.
									</span>
								</div>
								<a class="free_area0 collapse">hide</a>
							</div>
							<a class="free_area0 expand">show</a>								
						</div>
					</div>
				
				<!-- end: content section -->
			</div>	
		</div>
		<div class="footer">
			<div class="full_area0">
			<!-- begin: footer section-->
				&#160;
			<!-- end: footer section -->
			</div>
		</div>
	</body>
</html>