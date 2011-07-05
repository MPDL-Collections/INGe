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


 Copyright 2006-2011 Fachinformationszentrum Karlsruhe Gesellschaft
 für wissenschaftlich-technische Information mbH and Max-Planck-
 Gesellschaft zur Förderung der Wissenschaft e.V.
 All rights reserved. Use is subject to license terms.
-->
<!-- 
	Transformations from WoS Item to eSciDoc PubItem 
	Author: Julia Kurt (initial creation) 
	$Author$ (last changed)
	$Revision$ 
	$LastChangedDate$
-->
<xsl:stylesheet version="2.0"
   xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
   xmlns:xs="http://www.w3.org/2001/XMLSchema"
   xmlns:fn="http://www.w3.org/2005/xpath-functions"
   xmlns:xlink="http://www.w3.org/1999/xlink"
   xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
   xmlns:dc="${xsd.metadata.dc}"
   xmlns:dcterms="${xsd.metadata.dcterms}"    
   xmlns:srel="${xsd.soap.common.srel}"   
   xmlns:oaipmh="http://www.openarchives.org/OAI/2.0/"   
   xmlns:file="${xsd.metadata.file}"
   xmlns:pub="${xsd.metadata.publication}"
   xmlns:person="${xsd.metadata.person}"
	xmlns:source="${xsd.metadata.source}"
	xmlns:event="${xsd.metadata.event}"
	xmlns:organization="${xsd.metadata.organization}"		
	xmlns:eterms="${xsd.metadata.terms}"
   xmlns:AuthorDecoder="java:de.mpg.escidoc.services.common.util.creators.AuthorDecoder"
   xmlns:escidoc="urn:escidoc:functions"
   xmlns:ei="${xsd.soap.item.item}"
   xmlns:mdr="${xsd.soap.common.mdrecords}"
   xmlns:ec="${xsd.soap.item.components}"
   xmlns:prop="${xsd.soap.common.prop}"
   xmlns:itemlist="${xsd.soap.item.itemlist}">
 
 <xsl:import href="../../vocabulary-mappings.xsl"/>

	<xsl:output method="xml" encoding="UTF-8" indent="yes"/>
	
	<xsl:param name="user" select="'dummy-user'"/>
	<xsl:param name="context" select="'dummy-context'"/>
	<xsl:param name="is-item-list" select="true()"/>
	<xsl:param name="content-model"/>
	<xsl:param name="external-organization" select="'dummy-external-ou'"/>
	<!--
		DC XML  Header
	-->
	
		
	<!-- VARIABLEN -->
	
	
	<xsl:variable name="genre"/>		
	
	<xsl:template match="/">
		<xsl:choose>
			<xsl:when test="$is-item-list">
				<itemlist:item-list>
					<xsl:apply-templates select="//item"/>
				</itemlist:item-list>
			</xsl:when>
			<xsl:when test="count(//item) = 1">
				<xsl:apply-templates select="//item"/>
			</xsl:when>
			
			<xsl:when test="count(//item) = 0">
				<xsl:value-of select="error(QName('http://www.escidoc.de', 'err:NoSourceForSingleTarget' ), 'Single item was selected as target, but the source contained no items')"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="error(QName('http://www.escidoc.de', 'err:MultipleSourceForSingleTarget' ), 'Single item was selected as target, but the source contained multiple items')"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="item">
		<xsl:element name="ei:item">
			<xsl:element name="ei:properties">
				<srel:content-model objid="{$content-model}"/>
				<xsl:element name="prop:content-model-specific"/>
			</xsl:element>
			<xsl:element name="mdr:md-records">
				<mdr:md-record name="escidoc">
					<xsl:call-template name="itemMetadata"/>
				</mdr:md-record>
			</xsl:element>
			<xsl:element name="ec:components"></xsl:element>
		</xsl:element>
	</xsl:template>
	
	<!-- GENRE -->
	<xsl:template name="itemMetadata">
			<xsl:choose>
				<xsl:when test="PT='C'">
					<xsl:call-template name="createEntry">
						<xsl:with-param name="gen" select="'conference-paper'"/>
					</xsl:call-template>
				</xsl:when>				
				<xsl:when test="PT='J'">
					<xsl:call-template name="createEntry">
						<xsl:with-param name="gen" select="'article'"/>
					</xsl:call-template>
				</xsl:when>					
				<xsl:otherwise>
					<xsl:call-template name="createEntry">
						<xsl:with-param name="gen" select="'other'"/>
					</xsl:call-template>
				</xsl:otherwise>
			</xsl:choose>
	</xsl:template>
	
	<!-- Create eSciDoc Entry -->
	<xsl:template name="createEntry">
		<xsl:param name="gen"/>
		
		<xsl:element name="pub:publication">
			<xsl:attribute name="type">
				<xsl:value-of select="$genre-ves/enum[.=$gen]/@uri"/>
			</xsl:attribute>
			<!-- CREATOR -->
			<xsl:choose>
				<xsl:when test="AF">
					<xsl:apply-templates select="AF"/>
				</xsl:when>
				<xsl:when test="AU">
					<xsl:apply-templates select="AU"/>
				</xsl:when>
			</xsl:choose>
			
			<!-- TITLE -->
			<xsl:element name="dc:title">				
				<xsl:value-of select="TI"/>					
			</xsl:element>
			<!-- LANGUAGE -->
			<xsl:apply-templates select="LA"/>
			
			<!-- IDENTIFIER -->
			<xsl:apply-templates select="UT"/>
			<xsl:apply-templates select="DI"/>				
			
			<!-- DATES -->
			<xsl:call-template name="createDate"/>
			<!-- SOURCE -->			
			<xsl:call-template name="createSource"/>			
			<!-- EVENT -->
			<xsl:if test="CT">
				<xsl:call-template name="createEvent"/>
			</xsl:if>
			<!-- PAGES -->
			<xsl:apply-templates select="PG"/>
			<!-- ABSTRACT -->
			<xsl:apply-templates select="AB"/>
			<!-- SUBJECT -->
			<xsl:call-template name="createSubject"/>
			<!--end publication-->
		</xsl:element>
	</xsl:template>
	
	<!-- EVENT -->
	<xsl:template name="createEvent">
		<xsl:element name="event:event">
			<xsl:element name="dc:title">
				<xsl:value-of select="CT"/>
			</xsl:element>
			<xsl:variable name="monthStr" select="substring-before(CY,' ')"/>
			<xsl:variable name="month">
				<xsl:choose>
					<xsl:when test="$monthStr='JAN'">01</xsl:when>
					<xsl:when test="$monthStr='FEB'">02</xsl:when>
					<xsl:when test="$monthStr='MAR'">03</xsl:when>
					<xsl:when test="$monthStr='APR'">04</xsl:when>
					<xsl:when test="$monthStr='MAY'">05</xsl:when>
					<xsl:when test="$monthStr='JUN'">06</xsl:when>
					<xsl:when test="$monthStr='JUL'">07</xsl:when>
					<xsl:when test="$monthStr='AUG'">08</xsl:when>
					<xsl:when test="$monthStr='SEP'">09</xsl:when>
					<xsl:when test="$monthStr='OCT'">10</xsl:when>
					<xsl:when test="$monthStr='NOV'">11</xsl:when>
					<xsl:when test="$monthStr='DEC'">12</xsl:when>
				</xsl:choose>
			</xsl:variable>
			<xsl:variable name="day1" select="substring-before(substring-after(CY,' '),'-')"/>
			<xsl:variable name="day2" select="substring-after(substring-before(CY,', '),'-')"/>
			<xsl:variable name="year" select="substring-after(CY,', ')"/>
			<xsl:element name="eterms:start-date">
				<xsl:value-of select="$year"/>
				<xsl:if test="not($month='')">
					<xsl:value-of select="concat('-',$month)"/>
				</xsl:if>
				<xsl:if test="not($day1='')">
					<xsl:value-of select="concat('-',$day1)"/>
				</xsl:if>
			</xsl:element>
			<xsl:element name="eterms:end-date">
				<xsl:value-of select="$year"/>
				<xsl:if test="not($month='')">
					<xsl:value-of select="concat('-',$month)"/>
				</xsl:if>
				<xsl:if test="not($day2='')">
					<xsl:value-of select="concat('-',$day2)"/>
				</xsl:if>
			</xsl:element>
			<xsl:element name="eterms:place">
				<xsl:value-of select="CL"/>
			</xsl:element>
		</xsl:element>
	</xsl:template>
	<!-- CREATOR -->
	<xsl:template name="createPerson">
		<xsl:param name="familyname"/>
		<xsl:param name="givenname"/>
		<xsl:param name="title"/>
		<xsl:element name="person:person">			
			<xsl:element name="eterms:family-name">
				<xsl:value-of select="$familyname"/>
			</xsl:element>
			<xsl:element name="eterms:given-name">
				<xsl:value-of select="$givenname"/>
			</xsl:element>
			<xsl:choose>
			<xsl:when test="../CA">
				<xsl:element name="organization:organization">
					<xsl:element name="dc:title">
						<xsl:value-of select="../CA"/>
					</xsl:element>
				</xsl:element>
			</xsl:when>
			<xsl:otherwise>
				<organization:organization>
					<dc:title>External Organizations</dc:title>
					<dc:identifier><xsl:value-of select="$external-organization"/></dc:identifier>
				</organization:organization>
			</xsl:otherwise>
			</xsl:choose>
		</xsl:element>
	</xsl:template>
	<xsl:template match="AF|AU">
		<xsl:variable name="var">
           <xsl:copy-of select="AuthorDecoder:parseAsNode(.)"/>
      	</xsl:variable>
        <xsl:for-each select="$var/authors/author">
        	<xsl:element name="eterms:creator">
				<xsl:attribute name="role" select="$creator-ves/enum[.='author']/@uri"/>
					<xsl:call-template name="createPerson">
						<xsl:with-param name="familyname" select="familyname"/>
						<xsl:with-param name="givenname" select="givenname"/>
						<xsl:with-param name="title" select="title"/>
					</xsl:call-template>
			</xsl:element>
          
       </xsl:for-each>
         
		
	</xsl:template>
	
	<!--  
	<xsl:template match="AU">
		<xsl:element name="eterms:creator">
			<xsl:attribute name="role" select="$creator-ves/enum[.='author']/@uri"/>
			<xsl:call-template name="createPerson"/>
		</xsl:element>
	</xsl:template>
	-->
	
	<xsl:template match="ED">
		<xsl:element name="eterms:creator">
			<xsl:attribute name="role" select="$creator-ves/enum[.='contributor']/@uri"/>
			<xsl:call-template name="createPerson"/>
		</xsl:element>
	</xsl:template>
	<xsl:template name="parseCreators">
		<xsl:param name="string"/>
		<xsl:choose>
			<xsl:when test="substring-before($string,';')=''">
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="parseCreators">
					<xsl:with-param name="string" select="substring-after($string,';')"/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<!-- LANGUAGE -->
	<xsl:template match="LA">
		<!-- <xsl:element name="dc:language">
			<xsl:value-of select="."/>
		</xsl:element>-->
	</xsl:template>
	<!-- SOURCE -->
	<xsl:template name="createSource">
		<xsl:param name="genre"/>
				
		<xsl:if test="SO">
		<xsl:element name="source:source">
			<!-- SOURCE GENRE -->
			<xsl:attribute name="type">
				<xsl:choose>
					<xsl:when test="SO and not(SN) and not(BN) and PT='C'">
						<xsl:value-of select="$genre-ves/enum[.='proceedings']/@uri"/>
					</xsl:when>
					<xsl:when test="BN">
						<xsl:value-of select="$genre-ves/enum[.='proceedings']/@uri"/>
					</xsl:when>
					<xsl:when test="SN">
						<xsl:choose>
							<xsl:when test="BN">
								<xsl:value-of select="$genre-ves/enum[.='proceedings']/@uri"/>
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="$genre-ves/enum[.='journal']/@uri"/>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:when>					
				</xsl:choose>
			</xsl:attribute>
			<!-- SOURCE TITLE -->
			<xsl:element name="dc:title">
				<xsl:value-of select="SO"/>
			</xsl:element>					
			
			<!-- SOURCE ALTTITLE -->
			<xsl:if test="JI and SE">
				<xsl:element name="dcterms:alternative">
					<xsl:value-of select="JI"/>
				</xsl:element>
			</xsl:if>
			<xsl:if test="J9 and SE">
				<xsl:element name="dcterms:alternative">
					<xsl:value-of select="J9"/>
				</xsl:element>
			</xsl:if>			
				
			<!-- SOURCE CREATOR -->
			<xsl:if test="ED">
				<xsl:variable name="var">
           			<xsl:copy-of select="AuthorDecoder:parseAsNode(ED)"/>
      			</xsl:variable>
       			<xsl:for-each select="$var/authors/author">
        			<xsl:element name="eterms:creator">
						<xsl:attribute name="role" select="$creator-ves/enum[.='editor']/@uri"/>					
							<xsl:call-template name="createPerson">
								<xsl:with-param name="familyname" select="familyname"/>
								<xsl:with-param name="givenname" select="givenname"/>
								<xsl:with-param name="title" select="title"/>
							</xsl:call-template>
						</xsl:element>          
      			 </xsl:for-each>				
			</xsl:if>
			<!-- SOURCE VOLUME -->
			<xsl:if test="VL and not(SE)">
				<xsl:element name="eterms:volume">
					<xsl:value-of select="VL"/>
				</xsl:element>
			</xsl:if>
			<!-- SOURCE ISSUE -->
			<xsl:if test="IS and not(SE)">
				<xsl:element name="eterms:issue">
					<xsl:value-of select="IS"/>
				</xsl:element>				
			</xsl:if>
			<!-- SOURCE PAGES -->
			<xsl:if test="EP">
				<xsl:element name="eterms:start-page">
					<xsl:value-of select="BP"/>
				</xsl:element>
				<xsl:element name="eterms:end-page">
					<xsl:value-of select="EP"/>
				</xsl:element>
			</xsl:if>
			<!-- SOURCE TOTAL NUMBER OF PAGES -->
			<xsl:if test="not(EP) and SP">
				<xsl:element name="eterms:total-number-of-pages">
					<xsl:value-of select="SP"/>
				</xsl:element>
			</xsl:if>
			<!-- SOURCE PUBLISHINGINFO -->
			<xsl:if test="PU">
				<xsl:element name="eterms:publishing-info">
					<xsl:element name="dc:publisher">
						<xsl:value-of select="PU"/>
					</xsl:element>
					<xsl:element name="eterms:place">
						<xsl:value-of select="PA"/>
					</xsl:element>					
				</xsl:element>
			</xsl:if>
			<!-- SOURCE IDENTIFIER -->
			<xsl:if test="SN">
				<xsl:element name="dc:identifier">
					<xsl:attribute name="xsi:type">eterms:ISSN</xsl:attribute>
					<xsl:value-of select="SN"/>
				</xsl:element>
			</xsl:if>
			<xsl:if test="BN">
				<xsl:element name="dc:identifier">
					<xsl:attribute name="xsi:type">eterms:ISBN</xsl:attribute>
					<xsl:value-of select="BN"/>
				</xsl:element>
			</xsl:if>
		</xsl:element>
		</xsl:if>
		<!-- SECOND SOURCE -->
		<xsl:if test="SE">
			<xsl:element name="source:source">
				<xsl:attribute name="type" select="$genre-ves/enum[.='series']/@uri"/>
				<xsl:element name="dc:title">
					<xsl:value-of select="SE"/>
				</xsl:element>
				<xsl:apply-templates select="BS"/>
				<xsl:apply-templates select="J9"/>
				<xsl:apply-templates select="JI"/>
				<xsl:if test="VL">
					<xsl:element name="eterms:volume">
						<xsl:value-of select="VL"/>
					</xsl:element>
				</xsl:if>
				<xsl:if test="IS">
					<xsl:element name="eterms:issue">
						<xsl:value-of select="IS"/>
					</xsl:element>
				</xsl:if>
				<xsl:if test="SN and BN">
					<xsl:element name="dc:identifier">
						<xsl:attribute name="xsi:type">eterms:ISSN</xsl:attribute>
						<xsl:value-of select="SN"/>
					</xsl:element>
				</xsl:if>
			</xsl:element>
		</xsl:if>
	</xsl:template>
	<!-- SOURCE ALTTITLE -->
	<xsl:template match="J9">
		<xsl:element name="dcterms:alternative">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<xsl:template match="JI">
		<xsl:element name="dcterms:alternative">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<xsl:template match="BS">
		<xsl:element name="dcterms:alternative">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>	
	<!-- PAGES -->
	<xsl:template match="PG">
		<xsl:element name="eterms:total-number-of-pages">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<!-- DATES -->
	<xsl:template name="createDate">
		<xsl:if test="PY">
			
			<xsl:variable name="monthStr" select="PD"/>
			<xsl:variable name="month">
				<xsl:choose>
					<xsl:when test="$monthStr='JAN'">01</xsl:when>
					<xsl:when test="$monthStr='FEB'">02</xsl:when>
					<xsl:when test="$monthStr='MAR'">03</xsl:when>
					<xsl:when test="$monthStr='APR'">04</xsl:when>
					<xsl:when test="$monthStr='MAY'">05</xsl:when>
					<xsl:when test="$monthStr='JUN'">06</xsl:when>
					<xsl:when test="$monthStr='JUL'">07</xsl:when>
					<xsl:when test="$monthStr='AUG'">08</xsl:when>
					<xsl:when test="$monthStr='SEP'">09</xsl:when>
					<xsl:when test="$monthStr='OCT'">10</xsl:when>
					<xsl:when test="$monthStr='NOV'">11</xsl:when>
					<xsl:when test="$monthStr='DEC'">12</xsl:when>
				</xsl:choose>
			</xsl:variable>
			
			<xsl:element name="dcterms:created">				
				<xsl:value-of select="PY"/>
				<xsl:if test="not($month='')">
					<xsl:value-of select="concat('-',$month)"/>
				</xsl:if>				
			</xsl:element>
		</xsl:if>
	</xsl:template>
	
	
	
	<!-- ABSTRACT -->
	<xsl:template match="AB">
		<xsl:element name="dcterms:abstract">
			<xsl:value-of select="."/>
		</xsl:element>	
	</xsl:template>
	
	<!-- SUBJECT -->
	<xsl:template name="createSubject">
		<xsl:if test="ID or SC or DE">
			
			<xsl:element name="dcterms:subject">
				<xsl:apply-templates select="ID"/>
				<xsl:apply-templates select="SC"/>
				<xsl:apply-templates select="DE"/>
			</xsl:element>
		</xsl:if>
	</xsl:template>
	<xsl:template match="ID">
		<xsl:value-of select="."/>
	</xsl:template>
	<xsl:template match="SC">
		<xsl:value-of select="concat(.,'; ')"/>
	</xsl:template>
	<xsl:template match="DE">
		<xsl:value-of select="concat(.,'; ')"/>
	</xsl:template>
	<!-- IDENTIFIER -->
	<xsl:template match="UT">		
		<xsl:element name="dc:identifier">
			<xsl:attribute name="xsi:type">eterms:ISI</xsl:attribute>
			<xsl:value-of select="substring-after(.,':')"/>
		</xsl:element>		
	</xsl:template>
	<xsl:template match="DI">
		<xsl:element name="dc:identifier">
			<xsl:attribute name="xsi:type">eterms:DOI</xsl:attribute>
			<xsl:value-of select="."/>
		</xsl:element>		
	</xsl:template>
	
</xsl:stylesheet>