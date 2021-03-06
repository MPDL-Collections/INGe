<?xml version="1.0" encoding="UTF-8"?>
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
<xsl:stylesheet version="2.0" xmlns:escidoc="urn:escidoc:functions" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" xmlns:dc="http://purl.org/dc/elements/1.1/">
	
	<!-- Use xml here, otherwise special invalid HTML characters (e.g. Unicode 152) can produce exceptions in transformation -->
	<xsl:output method="xml" encoding="UTF-8" doctype-public="-//W3C//DTD XHTML 1.0 Transitional//EN" doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd" media-type="text/html"/>
	<xsl:param name="base-URL" select="'https://pure.mpg.de/cone/view.jsp'"/>
	<xsl:template match="/">
		<html xmlns="http://www.w3.org/1999/xhtml">
			<head>
				<title>CoNE - Control of Named Entities</title>
			</head>
			<body>
				<ul>
					<xsl:variable name="model" select="rdf:RDF/rdf:Description/@rdf:about"/>
					<xsl:for-each select="rdf:RDF/rdf:Description">
						<li>
							<xsl:comment>About: <xsl:value-of select="@rdf:about"/></xsl:comment>
							<xsl:comment>Index of: <xsl:value-of select="index-of(@rdf:about, '/cone')"/></xsl:comment>
							<xsl:comment>Index of + 6: <xsl:value-of select="(index-of(@rdf:about, '/cone') + 6)"/></xsl:comment>
							<xsl:comment>Substing after: <xsl:value-of select="substring-after(@rdf:about, '/cone')"/></xsl:comment>
							<xsl:comment>Substring 6: <xsl:value-of select="substring(@rdf:about, 6)"/></xsl:comment>
							<xsl:comment>base url: <xsl:value-of select="$base-URL"/></xsl:comment>
							<xsl:comment>concat base url: <xsl:value-of select="concat($base-URL, $base-URL)"/></xsl:comment>
							<a href="{concat($base-URL, substring-after(@rdf:about, '/cone/'))}"><xsl:value-of select="dc:title"/></a>
						</li>
					</xsl:for-each>
				</ul>
			</body>
		</html>
	</xsl:template>
		
	<!-- <xsl:function name="escidoc:url-encode">
		<xsl:param name="value"/>

		<xsl:value-of select="replace(replace(replace(replace($value, '/', '%2F'), ' ', '%20'), '=', '3D'), '&amp;', '%26')"/>
		
	</xsl:function> -->
	
</xsl:stylesheet>
