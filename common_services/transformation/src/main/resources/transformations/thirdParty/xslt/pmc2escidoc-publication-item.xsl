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
<!-- 
	Transformations from PubMedCentral to eSciDoc PubItem 
	Author: Julia Kurt (initial creation) 
	$Author$ (last changed)
	$Revision$ 
	$LastChangedDate$
-->
<xsl:stylesheet version="2.0" xmlns:pm="http://dtd.nlm.nih.gov/2.0/xsd/archivearticle"
   xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
   xmlns:xs="http://www.w3.org/2001/XMLSchema"
   xmlns:fns="http://www.w3.org/2005/02/xpath-functions"
   xmlns:fn="http://www.w3.org/2005/xpath-functions"
   xmlns:xlink="http://www.w3.org/1999/xlink"
   xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
   xmlns:dc="${xsd.metadata.dc}"
   xmlns:dcterms="${xsd.metadata.dcterms}"
   xmlns:mdr="${xsd.soap.common.mdrecords}"
   xmlns:eterms="${xsd.metadata.terms}"
   xmlns:ei="${xsd.soap.item.item}"
   xmlns:srel="${xsd.soap.common.srel}"
   xmlns:prop="${xsd.soap.common.prop}"
   xmlns:oaipmh="http://www.openarchives.org/OAI/2.0/"   
   xmlns:ec="${xsd.soap.item.components}"
   xmlns:file="${xsd.metadata.file}"
   xmlns:person="${xsd.metadata.person}"
   xmlns:source="${xsd.metadata.source}"
   xmlns:event="${xsd.metadata.event}"
   xmlns:organization="${xsd.metadata.organization}"
   xmlns:pub="${xsd.metadata.publication}"
   xmlns:escidoc="urn:escidoc:functions">
   
	<xsl:import href="../../vocabulary-mappings.xsl"/>   
	

	<xsl:param name="user" select="'dummy-user'"/>
	<xsl:param name="context" select="'dummy-context'"/>	
	<xsl:param name="content-model"/>

	<xsl:output method="xml" encoding="UTF-8" indent="yes"/>	
	
	<xsl:template match="/">		
		<xsl:for-each select="oaipmh:OAI-PMH/oaipmh:GetRecord/oaipmh:record/oaipmh:metadata/pm:article">			
			<xsl:call-template name="createItem"/>
		</xsl:for-each>
	</xsl:template>	
	
	
	<!-- CREATE ITEM -->	
	<xsl:template name="createItem">
		<xsl:element name="ei:item">
			<xsl:element name="ei:properties">
				<srel:context objid="{$context}" />
				<!--<xsl:element name="srel:context">
					<xsl:attribute name="xlink:href" select="concat('/ir/context/', $context)"/>
				</xsl:element>-->
				<srel:content-model objid="{$content-model}"/>
				<xsl:element name="prop:content-model-specific"/>
			</xsl:element>
			<xsl:element name="mdr:md-records">
				<mdr:md-record name="escidoc">
					<xsl:apply-templates select="pm:front/pm:article-meta"/>	
				</mdr:md-record>
			</xsl:element>	
			<xsl:element name="ec:components">				
						
			</xsl:element>
		</xsl:element>
	</xsl:template>
	
	
	
	
	<!-- CREATE MD-RECORD -->
	<xsl:template match="pm:front/pm:article-meta">
		<xsl:element name="pub:publication">			
			<xsl:attribute name="type" select="$genre-ves/enum[.='article']/@uri"/>
			<!-- CREATOR -->
			<xsl:apply-templates select="pm:contrib-group"/>
			<!-- TITLE -->
			<xsl:apply-templates select="pm:title-group"/>
			<xsl:apply-templates select="pm:alt-title"/>
			<xsl:apply-templates select="pm:trans-title"/>
			<xsl:apply-templates select="pm:trans-subtitle"/>			
			<!-- IDENTIFIER -->
			<xsl:apply-templates select="pm:article-id"/>
			<xsl:apply-templates select="pm:self-uri"/>
			<!-- DATES -->
			<xsl:apply-templates select="pm:pub-date"/>
			<!-- TOTAL PAGES -->
			<xsl:apply-templates select="pm:page-range"/>
			<!-- ABSTRACT -->
			<xsl:apply-templates select="pm:article-categories"/>
			<xsl:apply-templates select="pm:abstract"/>
			<xsl:apply-templates select="pm:trans-abstract"/>			
			<xsl:apply-templates select="pm:kwd-group"/>
			<!-- SOURCE:JOURNAL -->
			<xsl:call-template name="createJournal"/>
			<!-- SOURCE:ISSUE -->
			<xsl:if test="pm:issue-title">
				<xsl:call-template name="createIssue"/>
			</xsl:if>
			<!-- EVENT -->			
			<xsl:apply-templates select="pm:conference"/>
			
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="pm:contrib-group">
		<xsl:apply-templates select="pm:contrib"/>
	</xsl:template>

	<!-- CREATOR --><!--TODO organizations-->
	<xsl:template match="pm:contrib">
		
			<xsl:choose>
				<xsl:when test="@contrib-type='author' or @contrib-type='Author'">
					<xsl:element name="eterms:creator">
					<xsl:attribute name="role" select="$creator-ves/enum[.='author']/@uri"/>					
						<xsl:element name="person:person">
							<xsl:element name="eterms:family-name">
								<xsl:value-of select="pm:name/pm:surname"/>
							</xsl:element>
							<xsl:element name="eterms:given-name">
								<xsl:value-of select="pm:name/pm:given-names"/>
							</xsl:element>
							<xsl:if test="pm:xref">
								<xsl:call-template name="createOrganization"/>
							</xsl:if>
						</xsl:element>					
					</xsl:element>
				</xsl:when>
				<xsl:otherwise>
					<xsl:element name="eterms:creator">
					<xsl:attribute name="role" select="$creator-ves/enum[.='contributor']/@uri"/>				
						<xsl:element name="person:person">
							<xsl:element name="eterms:family-name">
								<xsl:value-of select="pm:name/pm:surname"/>
							</xsl:element>
							<xsl:element name="eterms:given-name">
								<xsl:value-of select="pm:name/pm:given-names"/>
							</xsl:element>
							<xsl:if test="pm:xref">
								<xsl:call-template name="createOrganization"/>
							</xsl:if>
						</xsl:element>
					</xsl:element>
				</xsl:otherwise>
			</xsl:choose>		
	</xsl:template>
	
	<xsl:template name="createOrganization">				
		<xsl:for-each select="pm:xref">			
			<xsl:variable name="orgaRef" select="@rid"/>
			
			<xsl:variable name="orgaString1" select="normalize-space(../../../pm:aff[@id=$orgaRef])"/>
			<xsl:variable name="orgaString2" select="normalize-space(../../pm:aff[@id=$orgaRef])"/>
			
			<xsl:choose>
				<xsl:when test="../../../pm:aff[@id=$orgaRef]">
					<xsl:variable name="orgaName" select="substring($orgaString1, 2)"/>
					<xsl:element name="organization:organization">
						<!--name-->
						<xsl:element name="dc:title">
							<xsl:choose>
								<xsl:when test="../../../pm:aff[@id=$orgaRef]/pm:label">						
									<xsl:value-of select="$orgaName"/>							
								</xsl:when>
								<xsl:otherwise>
									<xsl:value-of select="$orgaString1"/>		
								</xsl:otherwise>
							</xsl:choose>					
						</xsl:element>	
						<!-- address -->
						<!--<xsl:call-template name="createAdressLine">
							<xsl:with-param name="orgaRef" select="$orgaRef"/>	
							<xsl:with-param name="orgaPath" select="../../../aff[@id=$orgaRef]"/>
						</xsl:call-template>-->
					</xsl:element>		
				</xsl:when>
				<xsl:when test="../../pm:aff[@id=$orgaRef]">
					<xsl:variable name="orgaName" select="substring($orgaString2, 2)"/>
					<xsl:element name="organization:organization">
						<!--name-->
						<xsl:element name="dc:title">
							<xsl:choose>
								<xsl:when test="../../pm:aff[@id=$orgaRef]/pm:label">						
									<xsl:value-of select="$orgaName"/>							
								</xsl:when>
								<xsl:otherwise>
									<xsl:value-of select="$orgaString2"/>		
								</xsl:otherwise>
							</xsl:choose>					
						</xsl:element>	
						<!-- address -->
					<!--	<xsl:call-template name="createAdressLine">
							<xsl:with-param name="orgaRef" select="$orgaRef"/>	
							<xsl:with-param name="orgaPath" select="../../aff[@id=$orgaRef]"/>
						</xsl:call-template>-->
					</xsl:element>	
				</xsl:when>
			</xsl:choose>
			
			
		</xsl:for-each>
		
	</xsl:template>
	<!-- creator organization address -->
	<xsl:template name="createAdressLine">
		<xsl:param name="orgaRef"/>
		<xsl:param name="orgaPath"/>
		<xsl:if test="$orgaPath/pm:addr-line">
			<xsl:element name="eterms:address">
				<xsl:value-of select="$orgaPath/pm:addr-line"/>
			</xsl:element>
		</xsl:if>
		
	</xsl:template>
	
	<!-- TITLE -->
	<xsl:template match="pm:title-group">
		<xsl:element name="dc:title">			
			<xsl:value-of select="pm:article-title"/>
		</xsl:element>
	</xsl:template>
	
	<!-- IDENTIFIER -->
	<xsl:template match="pm:article-id">
		<xsl:element name="dc:identifier">
			<xsl:choose>
				<xsl:when test="@pub-id-type='issn'">					
					<xsl:attribute name="xsi:type">eterms:ISSN</xsl:attribute>					
				</xsl:when>
				<xsl:when test="@pub-id-type='doi'">
					<xsl:attribute name="xsi:type">eterms:DOI</xsl:attribute>
				</xsl:when>
				<xsl:when test="@pub-id-type='pmc'">
					<xsl:attribute name="xsi:type">eterms:PMC</xsl:attribute>					
				</xsl:when>
				<xsl:when test="@pub-id-type='pmid'">
					<xsl:attribute name="xsi:type">eterms:PMID</xsl:attribute>					
				</xsl:when>
				<xsl:otherwise>
					<xsl:attribute name="xsi:type">eterms:OTHER</xsl:attribute>
					<xsl:value-of select="@pub-id-type"/>
				</xsl:otherwise>
			</xsl:choose>				
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<xsl:template match="pm:self-uri">
		<xsl:element name="dc:identifier">
			<xsl:attribute name="xsi:type">eterms:URI</xsl:attribute>
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	
	<!-- DATES -->
	<xsl:template match="pm:pub-date">		
		<xsl:choose>
			<xsl:when test="@pub-type='epub'">
				<xsl:element name="eterms:published-online">
					<xsl:value-of select="pm:year"/>
					<xsl:apply-templates select="pm:month"/>
					<xsl:apply-templates select="pm:day"/>
				</xsl:element>
			</xsl:when>
			<xsl:when test="@pub-type='ppub'">
				<xsl:element name="dcterms:issued">
					<xsl:value-of select="pm:year"/>
					<xsl:apply-templates select="pm:month"/>
					<xsl:apply-templates select="pm:day"/>
				</xsl:element>
			</xsl:when>
			<xsl:when test="@pub-type='epub-ppub'">
				<xsl:element name="dcterms:issued">
					<xsl:value-of select="pm:year"/>
					<xsl:apply-templates select="pm:month"/>
					<xsl:apply-templates select="pm:day"/>
				</xsl:element>
				<xsl:element name="eterms:published-online">
					<xsl:value-of select="pm:year"/>
					<xsl:apply-templates select="pm:month"/>
					<xsl:apply-templates select="pm:day"/>
				</xsl:element>
			</xsl:when>
			<xsl:when test="@pub-type='ecorrected'">
				<xsl:element name="dcterms:modified">					
					<xsl:value-of select="pm:year"/>
					<xsl:apply-templates select="pm:month"/>
					<xsl:apply-templates select="pm:day"/>				
				</xsl:element>
			</xsl:when>
			<xsl:when test="@pub-type='pcorrected'">
				<xsl:element name="dcterms:modified">					
					<xsl:value-of select="pm:year"/>
					<xsl:apply-templates select="pm:month"/>
					<xsl:apply-templates select="pm:day"/>					
				</xsl:element>
			</xsl:when>
		</xsl:choose>
	</xsl:template>
	
	<xsl:template match="pm:month">
		<xsl:variable name="month" select="."/>
		<xsl:choose>
			<xsl:when test="fn:string-length($month) = 1">
				<xsl:value-of select="concat('-','0',.)"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="concat('-',.)"/>
			</xsl:otherwise>
		</xsl:choose>		
	</xsl:template>
	<xsl:template match="pm:day">
		<xsl:variable name="day" select="."/>
		<xsl:choose>
			<xsl:when test="fn:string-length($day) = 1">
				<xsl:value-of select="concat('-','0',.)"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="concat('-',.)"/>
			</xsl:otherwise>
		</xsl:choose>	
	</xsl:template>
	<!-- TOTAL NO OF PAGES  -->
	<xsl:template match="pm:page-range">
		<xsl:element name="eterms:total-number-of-pages">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<!-- ABSTRACT -->
	<!--TODO delete tags -->
	<xsl:template match="pm:abstract">
		<xsl:element name="dcterms:abstract">
			<xsl:apply-templates/>
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="trans-abstract">
		<xsl:element name="dcterms:abstract">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<!-- SUBJECT -->
	<xsl:template match="pm:article-categories">
		<xsl:apply-templates select="subject-group"/> 
	</xsl:template>
	<xsl:template match="pm:subject-group">
		<xsl:apply-templates select="subject"/>
	</xsl:template>
	<xsl:template match="pm:subject">
		<xsl:element name="dc:subject">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<xsl:template match="pm:kwd-group">
		<xsl:apply-templates select="pm:kwd"/>
	</xsl:template>
	<xsl:template match="pm:kwd">
		<xsl:element name="dcterms:subject">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	
	<!-- CREATE JOURNAL -->
	<xsl:template name="createJournal">		
		<xsl:element name="source:source">	
			<xsl:attribute name="type" select="$genre-ves/enum[.='journal']/@uri"/>
			<xsl:for-each select="../pm:journal-meta">
				<xsl:apply-templates select="pm:journal-title"/>
				<xsl:apply-templates select="pm:journal-subtitle"/>
				<xsl:apply-templates select="pm:trans-title"/>
				<xsl:apply-templates select="pm:trans-subtitle"/>
				<xsl:apply-templates select="pm:abbrev-journal-title"/>				
				<xsl:call-template name="createVolume"/>
				<xsl:call-template name="createIssueNo"/>
				<xsl:call-template name="createPages"/>
				<xsl:call-template name="createSeqNo"/>
				<xsl:apply-templates select="pm:publisher"/>
				<xsl:apply-templates select="pm:journal-id"/>
				<xsl:apply-templates select="pm:issn"/>				
			</xsl:for-each>			
		</xsl:element>
	</xsl:template>
	<!-- VOLUME -->	
	<xsl:template name="createVolume">
		<xsl:element name="eterms:volume">
			<xsl:value-of select="../pm:article-meta/pm:volume"/>
		</xsl:element>
	</xsl:template>
	<!-- ISSUE -->	
	<xsl:template name="createIssueNo">
		<xsl:element name="eterms:issue">
			<xsl:value-of select="../pm:article-meta/pm:issue"/>
		</xsl:element>
	</xsl:template>
	<!-- PAGES -->
	<xsl:template name="createPages">
		<xsl:for-each select="../pm:article-meta/pm:fpage">
			<xsl:element name="eterms:start-page">
				<xsl:value-of select="."/>
			</xsl:element>
		</xsl:for-each>
		<xsl:for-each select="../pm:article-meta/pm:lpage">
			<xsl:element name="eterms:end-page">
				<xsl:value-of select="."/>
			</xsl:element>
		</xsl:for-each>
	</xsl:template>
	<!-- SEQ NO -->
	<xsl:template name="createSeqNo">
		<xsl:for-each select="../pm:article-meta/pm:elocation-id">
			<xsl:element name="eterms:sequence-number">
				<xsl:value-of select="."/>
			</xsl:element>
		</xsl:for-each>
	</xsl:template>
	<!-- JOURNAL TEMPLATES -->
	<!-- TITLE -->
	<xsl:template match="pm:journal-title">
		<xsl:element name="dc:title">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<xsl:template match="pm:journal-subtitle">
		<xsl:element name="dcterms:alternative">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<xsl:template match="pm:trans-title">
		<xsl:element name="dcterms:alternative">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<xsl:template match="pm:trans-subtitle">
		<xsl:element name="dcterms:alternative">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<xsl:template match="pm:abbrev-journal-title">
		<xsl:element name="dcterms:alternative">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<!-- IDENTIFIER -->
	<xsl:template match="pm:journal-id">
		<xsl:element name="dc:identifier">
			<xsl:choose>
				<xsl:when test="@journal-id-type='issn'">
					<xsl:attribute name="xsi:type">eterms:ISSN</xsl:attribute>
					<xsl:value-of select="."/>
				</xsl:when>
				<xsl:when test="@journal-id-type='doi'">
					<xsl:attribute name="xsi:type">eterms:DOI</xsl:attribute>
					<xsl:value-of select="."/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:attribute name="xsi:type">eterms:OTHER</xsl:attribute>
					<xsl:value-of select="."/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:element>
	</xsl:template>
	<xsl:template match="pm:issn">
		<xsl:element name="dc:identifier">
			<xsl:attribute name="xsi:type">eterms:ISSN</xsl:attribute>
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<!-- PUBLISHINGINFO -->
	<xsl:template match="pm:publisher">
		<xsl:element name="eterms:publishing-info">
			<xsl:element name="dc:publisher">
				<xsl:value-of select="pm:publisher-name"/>
			</xsl:element>s
			<xsl:element name="eterms:place">
				<xsl:value-of select="pm:publisher-loc"/>
			</xsl:element>			
		</xsl:element>
	</xsl:template>
	
	<!-- SOURCE: ISSUE -->
	<xsl:template name="createIssue">
		<source:source type="issue">
			<xsl:element name="dc:title">
				<xsl:value-of select="pm:issue-title"/>
			</xsl:element>
			
		</source:source>
	</xsl:template>
	
	<!-- EVENT -->
	<xsl:template match="pm:conference">
		<xsl:element name="event:event">
			<xsl:apply-templates select="pm:conf-name"/>
			<xsl:apply-templates select="pm:conf-num"/>
			<xsl:apply-templates select="pm:conf-acronym"/>
			<xsl:apply-templates select="pm:conf-date"/>
			<xsl:apply-templates select="pm:conf-place"/>
		</xsl:element>
	</xsl:template>
	<!-- EVENT TEMPLATES -->
	<xsl:template match="pm:conf-name">
		<xsl:element name="dc:title">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<xsl:template match="pm:conf-num">
		<xsl:element name="dcterms:alternative">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<xsl:template match="pm:conf-acronym">
		<xsl:element name="dcterms:alternative">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<xsl:template match="pm:conf-loc">
		<xsl:element name="eterms:place">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<xsl:template match="pm:conf-date">
		<xsl:element name="eterms:start-date">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
</xsl:stylesheet>	
