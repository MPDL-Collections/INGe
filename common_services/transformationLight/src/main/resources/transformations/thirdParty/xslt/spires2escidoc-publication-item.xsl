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


 Copyright 2006-2011 Fachinformationszentrum Karlsruhe Gesellschaft
 für wissenschaftlich-technische Information mbH and Max-Planck-
 Gesellschaft zur Förderung der Wissenschaft e.V.
 All rights reserved. Use is subject to license terms.
-->
<!-- 
	Transformations from BioMedCentral to eSciDoc PubItem 
	Author: Julia Kurt (initial creation) 
	$Author: mfranke $ (last changed)
	$Revision: 4021 $ 
	$LastChangedDate: 2011-05-13 14:20:15 +0200 (Fr, 13 Mai 2011) $
-->
<xsl:stylesheet version="2.0" xmlns:pm="http://dtd.nlm.nih.gov/2.0/xsd/archivearticle" xmlns:bmc="http://www.biomedcentral.com/xml/schemas/oai/2.0/"
   xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
   xmlns:xs="http://www.w3.org/2001/XMLSchema"
   xmlns:fns="http://www.w3.org/2005/02/xpath-functions"
   xmlns:fn="http://www.w3.org/2005/xpath-functions"
   xmlns:xlink="http://www.w3.org/1999/xlink"
   xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
   xmlns:dc="${xsd.metadata.dc}"
   xmlns:dcterms="${xsd.metadata.dcterms}"
   xmlns:mdr="${xsd.soap.common.mdrecords}"   
   xmlns:ei="${xsd.soap.item.item}"   
   xmlns:srel="${xsd.soap.common.srel}"
   xmlns:prop="${xsd.soap.common.prop}"
   xmlns:oaipmh="http://www.openarchives.org/OAI/2.0/"   
   xmlns:ec="${xsd.soap.item.components}"
   xmlns:pub="${xsd.metadata.publication}"
   xmlns:person="${xsd.metadata.person}"
	xmlns:source="${xsd.metadata.source}"
	xmlns:event="${xsd.metadata.event}"
	xmlns:organization="${xsd.metadata.organization}"		
	xmlns:eterms="${xsd.metadata.terms}"   
   xmlns:escidoc="urn:escidoc:functions">
   
	<xsl:import href="../../vocabulary-mappings.xsl"/>   
   
	<xsl:param name="user" select="'dummy-user'"/>
	<xsl:param name="context" select="'dummy-context'"/>	
	<xsl:param name="content-model"/>

	<xsl:output method="xml" encoding="UTF-8" indent="yes"/>	
	
	<xsl:template match="/">		
		<xsl:apply-templates select="results"/>
	</xsl:template>	
	
	
	<xsl:template match="results">
		<xsl:apply-templates select="document"/>
	</xsl:template>
	
	<xsl:template match="document">
		<xsl:choose>
			<xsl:when test="journal">
				<xsl:call-template name="createItem">
					<xsl:with-param name="genre">
						<xsl:value-of select="$genre-ves/enum[.='journal']/@uri"/>
					</xsl:with-param>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="createItem">
					<xsl:with-param name="genre">
						<xsl:value-of select="$genre-ves/enum[.='conference-paper']/@uri"/>
					</xsl:with-param>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>	
			
	</xsl:template>
	
	<!-- CREATE ITEM -->	
	<xsl:template name="createItem">
		<xsl:param name="genre"/>
		<xsl:element name="ei:item">
			<xsl:element name="ei:properties">
				<srel:context objid="{$context}"/>
				<srel:content-model objid="{$content-model}"/>
				<xsl:element name="prop:content-model-specific"/>
			</xsl:element>
			<xsl:element name="mdr:md-records">
				<mdr:md-record name="escidoc">
					<xsl:call-template name="createMDRecord">
						<xsl:with-param name="genre" select="$genre"/>
					</xsl:call-template>
				</mdr:md-record>
			</xsl:element>	
			<xsl:element name="ec:components">				
						
			</xsl:element>
		</xsl:element>
	</xsl:template>
	
	
	
	
	<!-- CREATE MD-RECORD -->
	<xsl:template name="createMDRecord">
		<xsl:param name="genre"/>
		<xsl:element name="pub:publication">			
			<xsl:attribute name="type" select="$genre-ves/enum[.='conference-paper']/@uri"/>
			<!-- CREATOR -->
			<xsl:apply-templates select="authaffgrp"/>
			<!-- TITLE -->
			<xsl:apply-templates select="title"/>
			
			<!-- IDENTIFIER -->
			<xsl:apply-templates select="doi"/>
			<xsl:apply-templates select="eprint"/>
			<xsl:apply-templates select="spires_key"/>
			
			<!-- DATES -->
			<xsl:apply-templates select="date"/>
			<!-- EVENT -->
			<xsl:apply-templates select="conference"/>
			<!-- No PAGES -->
			<xsl:apply-templates select="pages"/>
			<!-- SUBJECT -->			
			
			<xsl:if test="report_num">
				<xsl:element name="dcterms:subject">
					<xsl:for-each select="report_num">
						<xsl:value-of select="concat(.,'; ')"/>
					</xsl:for-each>
				</xsl:element>
			</xsl:if>
			
			<!-- SOURCE:JOURNAL -->
			<xsl:apply-templates select="journal"/>
			
		</xsl:element>
	</xsl:template>
	

	<!-- CREATOR -->
	<xsl:template match="authaffgrp">
		<xsl:apply-templates select="author"/>		
	</xsl:template>
	
	<xsl:template match="author">		
		<xsl:element name="eterms:creator">
			<xsl:attribute name="role" select="$creator-ves/enum[.='author']/@uri"/>
			<xsl:call-template name="createPerson"/>
		</xsl:element>		
	</xsl:template>
	<xsl:template name="createPerson">
		<xsl:element name="person:person">
			<xsl:element name="eterms:family-name">
				<xsl:value-of select="."/>
			</xsl:element>		
			<xsl:if test="../aff">
				<xsl:call-template name="createOrganization"/>
			</xsl:if>
		</xsl:element>
	</xsl:template>
	
	
	<xsl:template name="createOrganization">		
		<xsl:element name="organization:organization">
			<xsl:element name="dc:title">
				<xsl:value-of select="../aff"/>
			</xsl:element>			
		</xsl:element>		
	</xsl:template>
	
	
	<!-- TITLE -->
	<xsl:template match="title">
		<xsl:element name="dc:title">			
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	
	<!-- IDENTIFIER -->	
	<xsl:template match="doi">
	   	<xsl:if test="not(. = '')">
			<xsl:element name="dc:identifier">
				<xsl:attribute name="xsi:type">eterms:DOI</xsl:attribute>							
				<xsl:value-of select="."/>
			</xsl:element>
		</xsl:if>
	</xsl:template>
	<xsl:template match="eprint">
		<xsl:if test="not(. = '')">
			<xsl:element name="dc:identifier">
				<xsl:attribute name="xsi:type">eterms:URI</xsl:attribute>							
				<xsl:value-of select="."/>
			</xsl:element>
		</xsl:if>
	</xsl:template>
	<xsl:template match="spires_key">
		<xsl:if test="not(. = '')">
			<xsl:element name="dc:identifier">
				<xsl:attribute name="xsi:type">eterms:OTHER</xsl:attribute>							
				<xsl:value-of select="concat('spires:',.)"/>
			</xsl:element>
		</xsl:if>
	</xsl:template>
	
	<!-- DATES -->
	<xsl:template match="date">		
		<xsl:element name="eterms:published-online">
			<xsl:call-template name="createDate"/>
		</xsl:element>		
	</xsl:template>
	
	<xsl:template name="createDate">
		<xsl:variable name="year" select="substring(.,1,4)"/>
		<xsl:variable name="month" select="substring(.,5,2)"/>
		<xsl:variable name="d" select="substring(.,7,8)"/>
		
		
		<xsl:variable name="day">	
			<xsl:choose>
				<xsl:when test="string-length($d)=1">	
					<xsl:value-of select="concat('0',$d)"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="$d"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="date">
			<xsl:value-of select="normalize-space($year)"/>
			<xsl:if test="not($month='00') and not($month='')">
				<xsl:value-of select="concat('-',$month)"/>
			</xsl:if>
			<xsl:if test="not($day='00') and not($day='')">
				<xsl:value-of select="concat('-',$day)"/>
			</xsl:if>
		</xsl:variable>
		<xsl:value-of select="$date"/>
	</xsl:template>
	
	<xsl:template name="createEventDate">
		<xsl:variable name="d" select="substring-before(., ' ')"/>
		<xsl:variable name="sd" select="substring-before(.,'-')"/>
		<xsl:variable name="ed" select="substring-after($d,'-')"/>
		<xsl:variable name="m" select="substring-before(substring-after(.,' '),' ')"/>
		<xsl:variable name="year" select="substring(normalize-space(substring-after(.,' ')),4,7)"/>
		<xsl:variable name="month">
		
				<xsl:choose>
					<xsl:when test="$m='Jan'">01</xsl:when>
					<xsl:when test="$m='Feb'">02</xsl:when>
					<xsl:when test="$m='Mar'">03</xsl:when>
					<xsl:when test="$m='Apr'">04</xsl:when>
					<xsl:when test="$m='May'">05</xsl:when>
					<xsl:when test="$m='Jun'">06</xsl:when>
					<xsl:when test="$m='Jul'">07</xsl:when>
					<xsl:when test="$m='Aug'">08</xsl:when>
					<xsl:when test="$m='Sep'">09</xsl:when>
					<xsl:when test="$m='Oct'">10</xsl:when>
					<xsl:when test="$m='Nov'">11</xsl:when>
					<xsl:when test="$m='Dec'">12</xsl:when>					
				</xsl:choose>
			
		</xsl:variable>
		<xsl:variable name="day">	
			<xsl:choose>
				<xsl:when test="string-length($sd)=1">	
					<xsl:value-of select="concat('0',$sd)"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="$sd"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="date">
			<xsl:value-of select="normalize-space($year)"/>
			<xsl:if test="not($month='00' or $month='')">
				<xsl:value-of select="concat('-',$month)"/>
			</xsl:if>
			<xsl:if test="not($day='00' or $day='')">
				<xsl:value-of select="concat('-',$day)"/>
			</xsl:if>
		</xsl:variable>
		<xsl:value-of select="$date"/>
	</xsl:template>
	
	<!-- EVENT -->
	<xsl:template match="conference">
		<xsl:element name="event:event">
			<xsl:apply-templates select="name"/>			
			<xsl:choose>
				<xsl:when test="dater">
					<xsl:apply-templates select="dater"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:apply-templates select="dates"/>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:apply-templates select="address"/>
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="address">
		<xsl:element name="eterms:place">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	
	<!-- EVENT DATE -->
	<xsl:template match="dates">
			
			<xsl:element name="eterms:start-date">
				<xsl:variable name="year" select="substring(.,1,4)"/>
				<xsl:variable name="month" select="substring(.,5,2)"/>
				<xsl:variable name="day" select="substring(.,7,2)"/>
				<xsl:value-of select="concat($year,'-',$month,'-',$day)"/>
				<xsl:variable name="date">
					<xsl:value-of select="normalize-space($year)"/>
					<xsl:if test="not($month='00' or $month='')">
						<xsl:value-of select="concat('-',$month)"/>
					</xsl:if>
					<xsl:if test="not($day='00' or $day='')">
						<xsl:value-of select="concat('-',$day)"/>
					</xsl:if>
				</xsl:variable>
				<xsl:value-of select="$date"/>
			</xsl:element>
		
	</xsl:template>
	<xsl:template match="dater">
		<xsl:element name="eterms:start-date">
			<xsl:call-template name="createEventDate"/>
		</xsl:element>
	</xsl:template>
	
	
	<!-- PAGES -->
	<xsl:template match="pages">
		<xsl:element name="eterms:total-number-of-pages">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	
	<!-- CREATE JOURNAL -->
	<xsl:template match="journal">		
		<xsl:element name="source:source">	
			<xsl:attribute name="type" select="$genre-ves/enum[.='journal']/@uri"/>
			<!-- SOURCE TITLE -->
			<xsl:apply-templates select="name"/>			
			<!-- SOURCE VOLUME -->
			<xsl:apply-templates select="volume"/>
			<!-- SOURCE PAGES -->
			<xsl:apply-templates select="page"/>			
		</xsl:element>
	</xsl:template>
	<!-- VOLUME -->	
	<xsl:template match="volume">
		<xsl:element name="eterms:volume">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	
	<!-- PAGES -->
	<xsl:template match="page">
		<xsl:element name="eterms:start-page">
			<xsl:value-of select="."/>
		</xsl:element>		
	</xsl:template>
	
	
	<!-- SOURCE TITLE -->
	<xsl:template match="name">
		<xsl:element name="dc:title">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	
	
	
</xsl:stylesheet>	

