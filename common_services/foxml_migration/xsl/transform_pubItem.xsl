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


 Copyright 2006-2010 Fachinformationszentrum Karlsruhe Gesellschaft
 für wissenschaftlich-technische Information mbH and Max-Planck-
 Gesellschaft zur Förderung der Wissenschaft e.V.
 All rights reserved. Use is subject to license terms.
-->

<!-- 
	Transformation from the eSciDoc metadata profile v1 to v2  
	Author: vmakarenko (initial creation) 
	$Author: vmakarenko $ (last changed)
	$Revision: 2512 $ 
	$LastChangedDate: 2009-12-10 15:27:53 +0100 (Thu, 10 Dec 2009) $
-->

<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:eterms="http://purl.org/escidoc/metadata/terms/0.1/" xmlns:person="http://purl.org/escidoc/metadata/profiles/0.1/person" xmlns:source="http://purl.org/escidoc/metadata/profiles/0.1/source" xmlns:event="http://purl.org/escidoc/metadata/profiles/0.1/event" xmlns:organization="http://purl.org/escidoc/metadata/profiles/0.1/organization" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:dcterms="http://purl.org/dc/terms/" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:escidoc="http://escidoc.mpg.de/metadataprofile/schema/0.1/types" xmlns:publication="http://escidoc.mpg.de/metadataprofile/schema/0.1/publication" xmlns:escidocMetadataProfile="http://escidoc.mpg.de/metadataprofile/schema/0.1/">
	
	<xsl:output method="xml" encoding="UTF-8"/>
	
	<xsl:param name="is-item-list" select="true()"/>
	
	<xsl:param name="path"/>
	
	<xsl:param name="cone_url"/>
	
	<xsl:variable name="vm" select="document( concat( if ($path!='') then concat ($path, '/') else '', 'ves-mapping.xml' ) )/mappings"/>

	<xsl:template name="publication">
		<xsl:apply-templates/>
	</xsl:template>
	
	<xsl:template match="node() | @*">
		<xsl:copy copy-namespaces="no">
			<xsl:apply-templates select="node() | @*"/>
		</xsl:copy>
	</xsl:template>
	
	<xsl:template match="escidocMetadataProfile:publication">
		<xsl:variable name="v1" select="@type"/>
		<xsl:variable name="v2" select="$vm/publication-type/v1-to-v2/map[@v1=$v1]"/>
		<xsl:element name="publication" namespace="http://purl.org/escidoc/metadata/profiles/0.1/publication">
			<xsl:copy-of select="@*[name()!='type']" />
			<!-- publication type (genre) from the ves -->
			<xsl:attribute name="type" select=" if (exists($v2)) then $v2 else error( QName('http://www.escidoc.de/transformation', 'err:NoMappingForEnum' ), concat ('No mapping v1.0 to v2.0 for publication type: ', $v1 ) ) " />
			<xsl:apply-templates/>
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="publication:creator | escidoc:creator" priority="999">
		<xsl:variable name="v1" select="@role"/>
		<xsl:variable name="v2" select="$vm/creator-role/v1-to-v2/map[@v1=$v1]"/>
		<xsl:element name="eterms:creator">
			<xsl:copy-of select="@*[name()!='role']" />
			<!-- creator role from the ves -->
			<xsl:if test="(exists($v2))" >
			<xsl:attribute name="role" select="$v2" />
			</xsl:if>
			<xsl:apply-templates />
		</xsl:element>
	</xsl:template>
 	
	
	<xsl:template match="escidoc:person" priority="999">
		<xsl:element name="person:person">
			<xsl:copy-of select="@*"/>
			<xsl:apply-templates/>
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="escidoc:organization" priority="999">
		<xsl:element name="organization:organization">
			<xsl:copy-of select="@*"/>
			<xsl:apply-templates/>
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="escidoc:organization/escidoc:organization-name" priority="999">
		<xsl:element name="dc:title">
			<xsl:copy-of select="@*"/>
			<xsl:apply-templates/>
		</xsl:element>
	</xsl:template>
	
	<!-- person and organization identifiers, see http://colab.mpdl.mpg.de/mediawiki/Checklist_for_Metadata_Changes#Identifiers_from_CONE_in_Publication_Metadata  -->
	<xsl:template match="escidoc:identifier" priority="999">
		<xsl:element name="dc:identifier">
			<xsl:copy-of select="@*[name() != 'xsi:type']"/>
			<xsl:if test="@*[name() = 'xsi:type']">
				<xsl:attribute name="xsi:type" select="concat('eterms:', local-name-from-QName(resolve-QName(@xsi:type, .)))"/>
				<xsl:value-of select=" if (contains(@xsi:type, 'CONE')) then concat($cone_url, substring-after(., 'urn:cone:')) else . "/>
			</xsl:if>
			<xsl:if test="not(contains(@xsi:type, 'CONE'))">
				<xsl:value-of select="."/>
			</xsl:if>
			<xsl:apply-templates select="*/*"/>
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="dc:identifier"
		xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
		xmlns:xs="http://www.w3.org/2001/XMLSchema"
		exclude-result-prefixes="xsi xs">
		
		<xsl:element name="dc:identifier">
			<xsl:copy-of select="@*[name() != 'xsi:type']"/>
			<xsl:if test="@*[name() = 'xsi:type']">
				<xsl:variable name="value" as="xs:string" select="@xsi:type"/>
				<xsl:variable name="prefix" select="substring-before($value, ':')"/>
				<xsl:variable name="name" select="substring-after($value, ':')"/>
				<xsl:choose>
					<xsl:when test="contains($prefix, 'eidt')">
						<xsl:namespace name="eterms" select="'http://purl.org/escidoc/metadata/terms/0.1/'"/>
						<xsl:attribute name="xsi:type" select="concat('eterms:', $name)"/>
					</xsl:when>
					<xsl:otherwise>
					<!-- 
						<xsl:namespace name="dcterms" select="'http://purl.org/dc/terms/'"/>
					 -->
						<xsl:attribute name="xsi:type" select="concat(concat($prefix, ':'), $name)"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:if>
			<xsl:apply-templates/>
		</xsl:element>
	</xsl:template>
	
	<!-- person, organization and publication identifiers  -->
	<!-- 
	<xsl:template match="dc:identifier" priority="999">
		<xsl:element name="dc:identifier">
			<xsl:copy-of select="@*[name() != 'xsi:type']"/>
			<xsl:if test="@*[name() = 'xsi:type']">
				<xsl:attribute name="xsi:type" select="concat('eterms:', local-name-from-QName(resolve-QName(@xsi:type, .)))"/>
			</xsl:if>
			<xsl:apply-templates/>
		</xsl:element>
	</xsl:template>
	 -->
	
	<xsl:template match="publication:published-online">
		<xsl:element name="eterms:published-online">
			<xsl:copy-of select="@*"/>
			<xsl:apply-templates/>
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="publication:review-method">
		<xsl:variable name="v1" select="."/>
		<xsl:variable name="v2" select="$vm/review-method/v1-to-v2/map[@v1=$v1]"/>
		<!-- review method from the ves -->
		<xsl:element name="eterms:review-method">
			<xsl:value-of select=" if (exists($v2)) then $v2 else error( QName('http://www.escidoc.de/transformation', 'err:NoMappingForEnum' ), concat ('No mapping v1.0 to v2.0 for review method: ', $v1 ) ) " />
			<!-- skip duplicated value of the element -->
			<xsl:apply-templates select="*/*"/>
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="publication:source | escidoc:source" priority="999">
		<xsl:variable name="v1" select="@type"/>
		<!-- no constrains for the source type:  mapping is taken from the publication type list-->
		<xsl:variable name="v2" select="$vm/publication-type/v1-to-v2/map[@v1=$v1]"/>
		<xsl:element name="source:source">
			<xsl:copy-of select="@*[name()!='type']"/>
			<!-- source type from the ves -->
			<xsl:if test="(exists($v2))" >
			<xsl:attribute name="type" select="$v2" />
			</xsl:if>
			<xsl:apply-templates/>
		</xsl:element>
	</xsl:template>

	<!-- see http://colab.mpdl.mpg.de/mediawiki/Checklist_for_Metadata_Changes#Identifiers_from_CONE_in_Publication_Metadata  -->
	<xsl:template match="dc:language">
		<xsl:variable name="v1" select="."/>
		<xsl:variable name="v2" select="$vm/language/v1-to-v2/map[@v1=$v1]"/>
		<xsl:element name="{name()}">
			<xsl:copy-of select="@*[name() != 'xsi:type']"/>
			<xsl:if test="@*[name() = 'xsi:type']">
				<xsl:attribute name="xsi:type" select=" if (@xsi:type=$vm/language/@v1) then $vm/language/@v2 else @xsi:type "/>
				<xsl:value-of select=" if ($v2!='') then $v2 else $vm/language/v1-to-v2/@default "/>
			</xsl:if>
			<xsl:apply-templates select="*/*"/>
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="publication:event">
		<xsl:element name="event:event">
			<xsl:copy-of select="@*"/>
			<xsl:apply-templates/>
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="publication:total-number-of-pages">
		<xsl:element name="eterms:total-number-of-pages">
			<xsl:copy-of select="@*"/>
			<xsl:apply-templates/>
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="publication:degree">
		<xsl:variable name="v1" select="."/>
		<xsl:variable name="v2" select="$vm/academic-degree/v1-to-v2/map[@v1=$v1]"/>
		<xsl:element name="eterms:degree">
			<!-- academic degree from the ves -->
			<xsl:value-of select=" if (exists($v2)) then $v2 else error( QName('http://www.escidoc.de/transformation', 'err:NoMappingForEnum' ), concat ('No mapping v1.0 to v2.0 for academic degree: ', $v1 ) ) " />
			<!-- skip duplicated value of the element -->
			<xsl:apply-templates select="*/*"/>
		</xsl:element>
	</xsl:template>
	
	
	<xsl:template match="publication:location">
		<xsl:element name="eterms:location">
			<xsl:copy-of select="@*"/>
			<xsl:apply-templates />
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="publication:publishing-info">
		<xsl:element name="eterms:publishing-info">
			<xsl:copy-of select="@*"/>
			<xsl:apply-templates/>
		</xsl:element>
	</xsl:template>
	
	<!--
	<xsl:template match="dcterms:subject">
		<xsl:element name="dc:subject">
			<xsl:copy-of select="@*"/>
			<xsl:apply-templates/>
		</xsl:element>
	</xsl:template>
	-->
	

	
	<!-- all escidoc: prefixes to the eterms: 
		Note: escidoc:identifier for person and organization has own processing  
	-->
	<xsl:template match="*[namespace-uri()='http://escidoc.mpg.de/metadataprofile/schema/0.1/types']" priority="1">
		<xsl:element name="eterms:{local-name()}">
			<xsl:copy-of select="@*"/>
			<xsl:apply-templates/>
		</xsl:element>
	</xsl:template>
	
	<!-- changes file namespace from http://escidoc.mpg.de/metadataprofile/schema/0.1/file to http://purl.org/metadata/profiles/0.1/file -->
	<xsl:template match="*[namespace-uri()='http://escidoc.mpg.de/metadataprofile/schema/0.1/file']" priority="1">
		<xsl:element name="{name()}" namespace="http://purl.org/metadata/profiles/0.1/file">
			<xsl:copy-of select="@*"/>
			<xsl:apply-templates/>
		</xsl:element>
	</xsl:template>

	
	
	

	<!-- all namespaces which should be presented in item root element -->
	<xsl:template name="item-namespaces">
		
		<xsl:namespace name="escidocItem">http://www.escidoc.de/schemas/item/0.8</xsl:namespace>
		<xsl:namespace name="escidocContext">http://www.escidoc.de/schemas/context/0.6</xsl:namespace>
		<xsl:namespace name="escidocContextList">http://www.escidoc.de/schemas/contextlist/0.6</xsl:namespace>
		<xsl:namespace name="escidocComponents">http://www.escidoc.de/schemas/components/0.8</xsl:namespace>
		<xsl:namespace name="escidocMetadataRecords">http://www.escidoc.de/schemas/metadatarecords/0.4</xsl:namespace>
		<xsl:namespace name="prop">http://escidoc.de/core/01/properties/</xsl:namespace>
		<xsl:namespace name="srel">http://escidoc.de/core/01/structural-relations/</xsl:namespace>
		<xsl:namespace name="version">http://escidoc.de/core/01/properties/version/</xsl:namespace>
		<xsl:namespace name="release">http://escidoc.de/core/01/properties/release/</xsl:namespace>
		
		<xsl:namespace name="eterms">http://purl.org/escidoc/metadata/terms/0.1/</xsl:namespace>
		<xsl:namespace name="organization">http://purl.org/escidoc/metadata/profiles/0.1/organization</xsl:namespace>
		<xsl:namespace name="person">http://purl.org/escidoc/metadata/profiles/0.1/person</xsl:namespace>
		<xsl:namespace name="source">http://purl.org/escidoc/metadata/profiles/0.1/source</xsl:namespace>
		<xsl:namespace name="idtype">http://purl.org/escidoc/metadata/terms/0.1/</xsl:namespace>
		<xsl:namespace name="event">http://purl.org/escidoc/metadata/profiles/0.1/event</xsl:namespace>

		<xsl:namespace name="file">http://purl.org/metadata/profiles/0.1/file</xsl:namespace>
		<xsl:namespace name="dc">http://purl.org/dc/elements/1.1/</xsl:namespace>
		<xsl:namespace name="dcterms">http://purl.org/dc/terms/</xsl:namespace>

		<xsl:namespace name="xlink">http://www.w3.org/1999/xlink</xsl:namespace>
		<xsl:namespace name="xsi">http://www.w3.org/2001/XMLSchema-instance</xsl:namespace>
	
	</xsl:template>

</xsl:stylesheet>
