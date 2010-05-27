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


 Copyright 2006-2010 Fachinformationszentrum Karlsruhe Gesellschaft
 für wissenschaftlich-technische Information mbH and Max-Planck-
 Gesellschaft zur Förderung der Wissenschaft e.V.
 All rights reserved. Use is subject to license terms.
-->
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:dcterms="http://purl.org/dc/terms/" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance/">
	
	<xsl:output method="html" encoding="UTF-8" media-type="text/html"/>

	<xsl:template match="/">
		<xsl:apply-templates select="rdf:RDF/rdf:Description"/>
	</xsl:template>

	<xsl:template match="rdf:Description">
		<html xmlns="http://www.w3.org/1999/xhtml">
			<head>
				<title>CoNE - <xsl:value-of select="dc:title"/></title>
			</head>
			<body>
				<h1>
					<xsl:value-of select="dc:title"/>
				</h1>
				<xsl:if test="dcterms:alternative != ''">
					<div>
						Alternative titles:
						<ul>
							<xsl:for-each select="dcterms:alternative">
								<li><xsl:value-of select="."/></li>
							</xsl:for-each>
						</ul>
					</div>
				</xsl:if>
				<xsl:if test="dc:publisher != ''">
					<div>
						Publisher: <xsl:value-of select="dc:publisher"/>
					</div>
				</xsl:if>
				<xsl:if test="dcterms:publisher != ''">
					<div>
						Place: <xsl:value-of select="dcterms:publisher"/>
					</div>
				</xsl:if>
				<div>
					Main identifier (SFX): <xsl:value-of select="dc:identifier/rdf:Description[xsi:type = 'http://purl.org/dc/elements/1.1/identifier/SFX']/rdf:value"/>
				</div>
				<xsl:if test="dc:identifier/rdf:Description[xsi:type != 'http://purl.org/dc/elements/1.1/identifier/SFX']">
					<div>
						Additional identifiers:
						<ul>
							<xsl:for-each select="dc:identifier/rdf:Description[xsi:type != 'http://purl.org/dc/elements/1.1/identifier/SFX']">
								<li><xsl:value-of select="xsi:type"/>: <xsl:value-of select="rdf:value"/></li>
							</xsl:for-each>
						</ul>
					</div>
				</xsl:if>
			</body>
		</html>
	</xsl:template>
	
</xsl:stylesheet>
