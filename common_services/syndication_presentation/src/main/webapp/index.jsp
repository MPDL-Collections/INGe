<%
/*
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
* Copyright 2006-2010 Fachinformationszentrum Karlsruhe Gesellschaft
* f�r wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur F�rderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/

/**
 * eSciDoc Syndication Service Web presentation.
 *
 * Home page. 
 *
 * @author Vlad Makarenko  (initial creation) 
 * @author $Author$ (last modification)
 * $Revision$
 * $LastChangedDate$ 
 */
%>
<%@page import="de.mpg.escidoc.services.framework.PropertyReader"%>
<%
	String urlBase = (request.getProtocol().contains("HTTPS") ? "https" : "http") + "://" + request.getServerName() + (request.getServerPort() != 80 ? ":" + request.getServerPort() : "");
/*	
	String searchPath = urlBase + "/search/SearchAndExport";
	String feedPath = urlBase + "/syndication/feed";
	String feedImage = "<img src=\"http://kb.mozillazine.org/images/Live_bookmarks.png\" />";
*/
%>
<html>
	<head>
		<link rel="stylesheet" type="text/css" href="./resources/escidoc-css/css/main.css" />	
		<title>eSciDoc Syndication Service</title>
	</head>
	<body>
		<div id="col3">
			<div class="content">
				<h1 class="topSpace">
					eSciDoc Syndication Service
				</h1>
				<div class="topSpace">
					<div class="editItemSingleCol topSpace"> 
						<span>
							The eSciDoc Syndication Service generates Atom or RSS feeds against according URLs.<br/>
							The following feed classes are available:<br/>  
						</span>
						<ul>
							<li>
								<a href="recent_releases.jsp">Recent Releases in repository.</a>  
							</li>
							<li>
								 <a href="organizational_units.jsp">Recent Releases for a specific Organizational Unit.</a>  
							</li>
							<li>
								 <a href="<%= urlBase %>/search/SearchAndExport_rest_sample.jsp">eSciDoc search can be presented as feed.</a>  
							</li>
						</ul>
					</div>
				</div>
			</div>
		</div>
	</body>	
</html>