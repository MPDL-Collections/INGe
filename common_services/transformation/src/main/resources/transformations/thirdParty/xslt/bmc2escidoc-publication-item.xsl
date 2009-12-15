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


 Copyright 2006-2008 Fachinformationszentrum Karlsruhe Gesellschaft
 für wissenschaftlich-technische Information mbH and Max-Planck-
 Gesellschaft zur Förderung der Wissenschaft e.V.
 All rights reserved. Use is subject to license terms.
-->
<!-- 
	Transformations from BioMedCentral to eSciDoc PubItem 
	Author: Julia Kurt (initial creation) 
	$Author$ (last changed)
	$Revision$ 
	$LastChangedDate$
-->
<xsl:stylesheet version="2.0"
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
   xmlns:pub="${xsd.metadata.publication}"
   xmlns:person="${xsd.metadata.person}"
   xmlns:source="${xsd.metadata.source}"
   xmlns:bmc="http://www.biomedcentral.com/xml/schemas/oai/2.0/"
   xmlns:escidoc="urn:escidoc:functions">
   
   	<xsl:import href="../../vocabulary-mappings.xsl"/>   
   
	<xsl:param name="user" select="'dummy-user'"/>
	<xsl:param name="context" select="'escidoc:31013'"/>	

	<xsl:output method="xml" encoding="UTF-8" indent="yes"/>	
	
	<xsl:template match="/">		
		<xsl:for-each select="oaipmh:OAI-PMH/oaipmh:GetRecord/oaipmh:record/oaipmh:metadata/bmc:ArticleSet/bmc:Article">			
			<xsl:call-template name="createItem"/>
		</xsl:for-each>
	</xsl:template>	
	
	
	<!-- CREATE ITEM -->	
	<xsl:template name="createItem">
		<xsl:element name="ei:item">
			<xsl:element name="ei:properties">
				<srel:context objid="escidoc:persistent3" />
				<!--<xsl:element name="srel:context">
					<xsl:attribute name="xlink:href" select="concat('/ir/context/', $context)"/>
				</xsl:element>-->
				<srel:content-model objid="escidoc:persistent4"/>
				<xsl:element name="prop:content-model-specific"/>
			</xsl:element>
			<xsl:element name="mdr:md-records">
				<mdr:md-record name="escidoc">
					<xsl:call-template name="createMDRecord"/>	
				</mdr:md-record>
			</xsl:element>	
			<xsl:element name="ec:components">				
						
			</xsl:element>
		</xsl:element>
	</xsl:template>
	
	
	
	
	<!-- CREATE MD-RECORD -->
	<xsl:template name="createMDRecord">
		<xsl:element name="pub:publication">			
			<xsl:attribute name="type" select="$genre-ves/enum[.='article']/@uri"/>
			<!-- CREATOR -->
			<xsl:apply-templates select="bmc:AuthorList"/>
			<!-- TITLE -->
			<xsl:apply-templates select="bmc:ArticleTitle"/>
			<xsl:apply-templates select="bmc:VernacularTitle"/>
			<!-- IDENTIFIER -->
			<xsl:apply-templates select="bmc:ArticleIdList"/>
			
			<!-- DATES -->
			<xsl:apply-templates select="bmc:History/bmc:PubDate"/>
			<!-- TOTAL PAGES -->
		<!-- 	<xsl:apply-templates select="pm:page-range"/>-->
			<!-- ABSTRACT -->			
			<xsl:apply-templates select="bmc:Abstract"/>
			<xsl:apply-templates select="bmc:OtherAbstract"/>
			<!-- SOURCE:JOURNAL -->
			<xsl:apply-templates select="bmc:Journal"/>
			
		</xsl:element>
	</xsl:template>
	
	

	<!-- CREATOR --><!--TODO organizations-->
	<xsl:template match="bmc:AuthorList">
		<xsl:apply-templates select="bmc:Author"/>
	</xsl:template>
	
	<xsl:template match="bmc:Author">
	
		<xsl:element name="eterms:creator">
			<xsl:attribute name="role" select="$creator-ves/enum[.='author']/@uri"/>
			<xsl:element name="person:person">		
				<xsl:element name="eterms:complete-name">
					<xsl:value-of select="concat(bmc:FirstName, ' ')"/>
					<xsl:value-of select="concat(bmc:MiddleName, ' ')"/>
					<xsl:value-of select="bmc:LastName"/>
				</xsl:element>
				<xsl:element name="eterms:given-name">
					<xsl:value-of select="concat(bmc:FirstName, ' ')"/>
					<xsl:value-of select="bmc:MiddleName"/>
				</xsl:element>
				<xsl:element name="eterms:family-name">
					<xsl:value-of select="bmc:LastName"/>
				</xsl:element>
				<xsl:apply-templates select="bmc:Affiliation"/>
			</xsl:element>
		</xsl:element>
		<xsl:apply-templates select="bmc:CollectiveName"/>
		
	</xsl:template>
	
	<xsl:template match="bmc:Affiliation">
		<xsl:call-template name="createOrganization"/>
	</xsl:template>
	
	<xsl:template match="bmc:CollectiveName">
	
		<xsl:element name="eterms:creator">					
			<xsl:call-template name="createOrganization"/>
		</xsl:element>
	
	</xsl:template>
	<xsl:template name="createOrganization">		
		<xsl:element name="organization:organization">
			<xsl:element name="dc:title">
				<xsl:value-of select="."/>
			</xsl:element>			
		</xsl:element>		
	</xsl:template>
	
	
	<!-- TITLE -->
	<xsl:template match="bmc:ArticleTitle">
		<xsl:element name="dc:title">			
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<xsl:template match="bmc:VernacularTitle">
		<xsl:element name="dcterms:alternative">			
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<!-- IDENTIFIER -->
	<xsl:template match="bmc:ArticleIdList">
		<xsl:apply-templates select="bmc:ArticleId[@IdType='pii']"/>
		<xsl:apply-templates select="bmc:ArticleId[@IdType='doi']"/>
		<xsl:apply-templates select="bmc:ArticleId[@IdType='pmcpid']"/>
		<xsl:apply-templates select="bmc:ArticleId[@IdType='pmpid']"/>
		<xsl:apply-templates select="bmc:ArticleId[@IdType='pmid']"/>
		<xsl:apply-templates select="bmc:ArticleId[@IdType='medline']"/>
		<xsl:apply-templates select="bmc:ArticleId[@IdType='pmcid']"/>
	</xsl:template>
	
	<xsl:template match="bmc:ArticleId[@IdType='pii']">
		<xsl:element name="dc:identifier">
			<xsl:attribute name="xsi:type">eterms:PII</xsl:attribute>
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<xsl:template match="bmc:ArticleId[@IdType='doi']">
		<xsl:element name="dc:identifier">
			<xsl:attribute name="xsi:type">eterms:DOI</xsl:attribute>
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<xsl:template match="bmc:ArticleId[@IdType='pmcpid']">
		<xsl:element name="dc:identifier">
			<xsl:attribute name="xsi:type">eterms:OTHER</xsl:attribute>
			<xsl:value-of select="'pmcpid:'"/>
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<xsl:template match="bmc:ArticleId[@IdType='pmpid']">
		<xsl:element name="dc:identifier">
			<xsl:attribute name="xsi:type">eterms:OTHER</xsl:attribute>
			<xsl:value-of select="'pmpid:'"/>
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<xsl:template match="bmc:ArticleId[@IdType='pmid']">
		<xsl:element name="dc:identifier">
			<xsl:attribute name="xsi:type">eterms:PMID</xsl:attribute>
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<xsl:template match="bmc:ArticleId[@IdType='medline']">
		<xsl:element name="dc:identifier">
			<xsl:attribute name="xsi:type">eterms:OTHER</xsl:attribute>
			<xsl:value-of select="'medline:'"/>
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<xsl:template match="bmc:ArticleId[@IdType='pmcid']">
		<xsl:element name="dc:identifier">
			<xsl:attribute name="xsi:type">eterms:PMC</xsl:attribute>
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	
	<!-- DATES -->
	<xsl:template match="bmc:History/bmc:PubDate">		
		<xsl:choose>
			<xsl:when test="@PubStatus='received'">
				<xsl:element name="dcterms:dateSubmitted">
					<xsl:call-template name="createDate"/>
				</xsl:element>
			</xsl:when>
			<xsl:when test="@PubStatus='accepted'">
				<xsl:element name="dcterms:dateAccepted">
					<xsl:call-template name="createDate"/>
				</xsl:element>
			</xsl:when>
			<xsl:when test="@PubStatus='epublish'">
				<xsl:element name="eterms:published-online">
					<xsl:call-template name="createDate"/>
				</xsl:element>
			</xsl:when>
			<xsl:when test="@PubStatus='ppublish'">
				<xsl:element name="dcterms:issued">					
					<xsl:call-template name="createDate"/>				
				</xsl:element>
			</xsl:when>
			<xsl:when test="@PubStatus='revised'">
				<xsl:element name="dcterms:modified">					
					<xsl:call-template name="createDate"/>				
				</xsl:element>
			</xsl:when>
			<xsl:when test="@PubStatus='aheadofprint'">
				<xsl:element name="eterms:published-online">
					<xsl:call-template name="createDate"/>
				</xsl:element>
			</xsl:when>
		</xsl:choose>
	</xsl:template>
	
	
	<xsl:template name="createDate">
		
		<xsl:variable name="date">
			<xsl:if test="bmc:Year">
				<xsl:value-of select="bmc:Year"/>
				<xsl:if test="bmc:Month">					
					
					<xsl:choose>
						<xsl:when test="bmc:Month='Jan'"><xsl:text>-01</xsl:text></xsl:when>
						<xsl:when test="bmc:Month='Feb'"><xsl:text>-02</xsl:text></xsl:when>
						<xsl:when test="bmc:Month='Mar'"><xsl:text>-03</xsl:text></xsl:when>
						<xsl:when test="bmc:Month='Apr'"><xsl:text>-04</xsl:text></xsl:when>
						<xsl:when test="bmc:Month='May'"><xsl:text>-05</xsl:text></xsl:when>
						<xsl:when test="bmc:Month='Jun'"><xsl:text>-06</xsl:text></xsl:when>
						<xsl:when test="bmc:Month='Jul'"><xsl:text>-07</xsl:text></xsl:when>
						<xsl:when test="bmc:Month='Aug'"><xsl:text>-08</xsl:text></xsl:when>
						<xsl:when test="bmc:Month='Sep'"><xsl:text>-09</xsl:text></xsl:when>
						<xsl:when test="bmc:Month='Oct'"><xsl:text>-10</xsl:text></xsl:when>
						<xsl:when test="bmc:Month='Nov'"><xsl:text>-11</xsl:text></xsl:when>
						<xsl:when test="bmc:Month='Dec'"><xsl:text>-12</xsl:text></xsl:when>
					</xsl:choose>
				</xsl:if>
				<xsl:if test="bmc:Day">
					<xsl:value-of select="concat('-',bmc:Day)"/>
				</xsl:if>
			</xsl:if>
		</xsl:variable>
		<xsl:value-of select="$date"/>
	</xsl:template>
	
	
	<!-- TOTAL NO OF PAGES  -->
	<!-- <xsl:template match="pm:page-range">
		<xsl:element name="eterms:total-number-of-pages">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>-->
	<!-- ABSTRACT -->
	<!--TODO delete tags -->
	<xsl:template match="bmc:Abstract">
		<xsl:element name="dcterms:abstract">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<xsl:template match="bmc:OtherAbstract">
		<xsl:element name="dcterms:abstract">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	
	
	<!-- CREATE JOURNAL -->
	<xsl:template match="bmc:Journal">		
		<xsl:element name="source:source">	
			<xsl:attribute name="type" select="$genre-ves/enum[.='journal']/@uri"/>
			<!-- SOURCE TITLE -->
			<xsl:apply-templates select="bmc:JournalTitle"/>			
			<!-- SOURCE VOLUME -->
			<xsl:apply-templates select="bmc:Volume"/>
			<!-- SOURCE ISSUE -->
			<xsl:apply-templates select="bmc:Issue"/>
			<!-- SOURCE PAGES -->
			<xsl:apply-templates select="bmc:FirstPage"/>
			<xsl:apply-templates select="bmc:LastPage"/>
			<!-- SOURCE SEQ NR -->
			<xsl:apply-templates select="bmc:ELocationID"/>
			<!-- SOURCE PUBLISHINGINFO -->
			<xsl:apply-templates select="bmc:PublisherName"/>
			<!-- SOURCE IDENTIFIER -->
			<xsl:apply-templates select="bmc:Issn"/>
		</xsl:element>
	</xsl:template>
	<!-- VOLUME -->	
	<xsl:template match="bmc:Volume">
		<xsl:element name="eterms:volume">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<!-- ISSUE -->	
	<xsl:template match="bmc:Issue">
		<xsl:element name="eterms:issue">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<!-- PAGES -->
	<xsl:template match="bmc:FirstPages">
		<xsl:element name="eterms:start-page">
			<xsl:value-of select="."/>
		</xsl:element>		
	</xsl:template>
	<xsl:template match="bmc:LastPage">
		<xsl:element name="eterms:end-page">	
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<!-- SEQ NO -->
	<xsl:template match="bmc:ELocationID">
		<xsl:element name="eterms:sequence-number">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	
	<!-- SOURCE TITLE -->
	<xsl:template match="bmc:JournalTitle">
		<xsl:element name="dc:title">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	
	<!-- SOURCE IDENTIFIER -->
	
	<xsl:template match="bmc:Issn">
		<xsl:element name="dc:identifier">
			<xsl:attribute name="xsi:type">eterms:ISSN</xsl:attribute>
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<!-- SOURCE PUBLISHINGINFO -->
	<xsl:template match="bmc:PublisherName">
		<xsl:element name="eterms:publishing-info">
			<xsl:element name="dc:publisher">
				<xsl:value-of select="."/>
			</xsl:element>				
		</xsl:element>
	</xsl:template>
	
</xsl:stylesheet>	
