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
<xsl:stylesheet
	xml:base="stylesheet" version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:prop="${xsd.soap.common.prop}"
	xmlns:srel="${xsd.soap.common.srel}"
	xmlns:version="${xsd.soap.common.version}"
	xmlns:release="${xsd.soap.common.release}"
	xmlns:item="${xsd.soap.item.item}"
	xmlns:context="${xsd.soap.context.context}"
	xmlns:components="${xsd.soap.item.components}"
	xmlns:contextlist="${xsd.soap.context.contextlist}"
	xmlns:itemlist="${xsd.soap.item.itemlist}"
	xmlns:mdrecords="${xsd.soap.common.mdrecords}"
	xmlns:relations="${xsd.soap.common.relations}"
	xmlns:searchresult="${xsd.soap.searchresult.searchresult}"
	xmlns:useraccount="${xsd.soap.useraccount.useraccount}"
	xmlns:useraccountlist="${xsd.soap.useraccount.useraccountlist}"
	xmlns:usergroup="${xsd.soap.usergroup.usergroup}"
	xmlns:usergrouplist="${xsd.soap.usergroup.usergrouplist}"
	xmlns:oupathlist="${xsd.soap.ou.oupathlist}"
	xmlns:ou="${xsd.soap.ou.ou}"
	xmlns:oulist="${xsd.soap.ou.oulist}"
	xmlns:ouref="${xsd.soap.ou.ouref}"
	xmlns:stagingfile="${xsd.rest.stagingfile.stagingfile}"
	xmlns:commontypes="${xsd.soap.common.commontypes}"
	xmlns:grants="${xsd.soap.useraccount.grants}"
	xmlns:versionhistory="${xsd.soap.common.versionhistory}"
	xmlns:memberlist="${xsd.soap.common.memberlist}"
	xmlns:container="${xsd.soap.container.container}"
	xmlns:structmap="${xsd.soap.container.structmap}"
	xmlns:containerlist="${xsd.soap.container.containerlist}"
	xmlns:organization="${xsd.metadata.organization}"
	xmlns:file="${xsd.metadata.file}"
	xmlns:publication="${xsd.metadata.publication}"
	xmlns:escidocprofile="${xsd.metadata.escidocprofile}"
	xmlns:eterms="${xsd.metadata.escidocprofile.types}"
	xmlns:idtypes="${xsd.metadata.escidocprofile.idtypes}"
	xmlns:properties="${xsd.core.properties}"
	xmlns:metadatarecords="${xsd.soap.common.metadatarecords}"
	xmlns:report="${xsd.soap.statistic.report}"
	xmlns:reportparameters="${xsd.soap.statistic.reportparameters}"
	xmlns:reportdefinitionlist="${xsd.soap.statistic.reportdefinitionlist}"
	xmlns:reportdefinition="${xsd.soap.statistic.reportdefinition}"
	xmlns:toc="${xsd.soap.toc.toc}"
	xmlns:table-of-content="${xsd.soap.toc.table-of-content}"
	xmlns:result="${xsd.soap.result.result}"
	xmlns:dc="${xsd.metadata.dc}"
	xmlns:dcterms="${xsd.metadata.dcterms}"
	xmlns:source="${xsd.metadata.source}"
	xmlns:event="${xsd.metadata.event}"
	xmlns:person="${xsd.metadata.person}"
	xmlns:organizationalunit="${xsd.metadata.organizationalunit}"
	>
	
	<xsl:output method="xml" encoding="UTF-8"/>
	
	<xsl:template match="/">
		<xsl:apply-templates/>
	</xsl:template> 
	
	<xsl:template match="*">
		<xsl:copy>
			<xsl:copy-of select="@*"/>
			<xsl:apply-templates/>
		</xsl:copy>
	</xsl:template>
	
	<xsl:template match="item:item">
		<item:item
			xmlns:prop="${xsd.soap.common.prop}"
			xmlns:srel="${xsd.soap.common.srel}"
			xmlns:version="${xsd.soap.common.version}"
			xmlns:release="${xsd.soap.common.release}"
			xmlns:item="${xsd.soap.item.item}"
			xmlns:context="${xsd.soap.context.context}"
			xmlns:components="${xsd.soap.item.components}"
			xmlns:contextlist="${xsd.soap.context.contextlist}"
			xmlns:itemlist="${xsd.soap.item.itemlist}"
			xmlns:mdrecords="${xsd.soap.common.mdrecords}"
			xmlns:relations="${xsd.soap.common.relations}"
			xmlns:searchresult="${xsd.soap.searchresult.searchresult}"
			xmlns:useraccount="${xsd.soap.useraccount.useraccount}"
			xmlns:useraccountlist="${xsd.soap.useraccount.useraccountlist}"
			xmlns:usergroup="${xsd.soap.usergroup.usergroup}"
			xmlns:usergrouplist="${xsd.soap.usergroup.usergrouplist}"
			xmlns:oupathlist="${xsd.soap.ou.oupathlist}"
			xmlns:ou="${xsd.soap.ou.ou}"
			xmlns:oulist="${xsd.soap.ou.oulist}"
			xmlns:ouref="${xsd.soap.ou.ouref}"
			xmlns:stagingfile="${xsd.rest.stagingfile.stagingfile}"
			xmlns:commontypes="${xsd.soap.common.commontypes}"
			xmlns:grants="${xsd.soap.useraccount.grants}"
			xmlns:versionhistory="${xsd.soap.common.versionhistory}"
			xmlns:memberlist="${xsd.soap.common.memberlist}"
			xmlns:container="${xsd.soap.container.container}"
			xmlns:structmap="${xsd.soap.container.structmap}"
			xmlns:containerlist="${xsd.soap.container.containerlist}"
			xmlns:organization="${xsd.metadata.organization}"
			xmlns:file="${xsd.metadata.file}"
			xmlns:publication="${xsd.metadata.publication}"
			xmlns:escidocprofile="${xsd.metadata.escidocprofile}"
			xmlns:eterms="${xsd.metadata.escidocprofile.types}"
			xmlns:idtypes="${xsd.metadata.escidocprofile.idtypes}"
			xmlns:properties="${xsd.core.properties}"
			xmlns:metadatarecords="${xsd.soap.common.metadatarecords}"
			xmlns:report="${xsd.soap.statistic.report}"
			xmlns:reportparameters="${xsd.soap.statistic.reportparameters}"
			xmlns:reportdefinitionlist="${xsd.soap.statistic.reportdefinitionlist}"
			xmlns:reportdefinition="${xsd.soap.statistic.reportdefinition}"
			xmlns:toc="${xsd.soap.toc.toc}"
			xmlns:table-of-content="${xsd.soap.toc.table-of-content}"
			xmlns:result="${xsd.soap.result.result}"
			xmlns:dc="${xsd.metadata.dc}"
			xmlns:dcterms="${xsd.metadata.dcterms}"
			xmlns:source="${xsd.metadata.source}"
			xmlns:event="${xsd.metadata.event}"
			xmlns:person="${xsd.metadata.person}"
			xmlns:organizationalunit="${xsd.metadata.organizationalunit}"
			>
			<xsl:copy-of select="@*"/>
			<xsl:apply-templates/>
		</item:item>
	</xsl:template>
	
	<xsl:template match="container:container">
		<container:container
			xmlns:prop="${xsd.soap.common.prop}"
			xmlns:srel="${xsd.soap.common.srel}"
			xmlns:version="${xsd.soap.common.version}"
			xmlns:release="${xsd.soap.common.release}"
			xmlns:item="${xsd.soap.item.item}"
			xmlns:context="${xsd.soap.context.context}"
			xmlns:components="${xsd.soap.item.components}"
			xmlns:contextlist="${xsd.soap.context.contextlist}"
			xmlns:itemlist="${xsd.soap.item.itemlist}"
			xmlns:mdrecords="${xsd.soap.common.mdrecords}"
			xmlns:relations="${xsd.soap.common.relations}"
			xmlns:searchresult="${xsd.soap.searchresult.searchresult}"
			xmlns:useraccount="${xsd.soap.useraccount.useraccount}"
			xmlns:useraccountlist="${xsd.soap.useraccount.useraccountlist}"
			xmlns:usergroup="${xsd.soap.usergroup.usergroup}"
			xmlns:usergrouplist="${xsd.soap.usergroup.usergrouplist}"
			xmlns:oupathlist="${xsd.soap.ou.oupathlist}"
			xmlns:ou="${xsd.soap.ou.ou}"
			xmlns:oulist="${xsd.soap.ou.oulist}"
			xmlns:ouref="${xsd.soap.ou.ouref}"
			xmlns:stagingfile="${xsd.rest.stagingfile.stagingfile}"
			xmlns:commontypes="${xsd.soap.common.commontypes}"
			xmlns:grants="${xsd.soap.useraccount.grants}"
			xmlns:versionhistory="${xsd.soap.common.versionhistory}"
			xmlns:memberlist="${xsd.soap.common.memberlist}"
			xmlns:container="${xsd.soap.container.container}"
			xmlns:structmap="${xsd.soap.container.structmap}"
			xmlns:containerlist="${xsd.soap.container.containerlist}"
			xmlns:organization="${xsd.metadata.organization}"
			xmlns:file="${xsd.metadata.file}"
			xmlns:publication="${xsd.metadata.publication}"
			xmlns:escidocprofile="${xsd.metadata.escidocprofile}"
			xmlns:eterms="${xsd.metadata.escidocprofile.types}"
			xmlns:idtypes="${xsd.metadata.escidocprofile.idtypes}"
			xmlns:properties="${xsd.core.properties}"
			xmlns:metadatarecords="${xsd.soap.common.metadatarecords}"
			xmlns:report="${xsd.soap.statistic.report}"
			xmlns:reportparameters="${xsd.soap.statistic.reportparameters}"
			xmlns:reportdefinitionlist="${xsd.soap.statistic.reportdefinitionlist}"
			xmlns:reportdefinition="${xsd.soap.statistic.reportdefinition}"
			xmlns:toc="${xsd.soap.toc.toc}"
			xmlns:table-of-content="${xsd.soap.toc.table-of-content}"
			xmlns:result="${xsd.soap.result.result}"
			xmlns:dc="${xsd.metadata.dc}"
			xmlns:dcterms="${xsd.metadata.dcterms}"
			xmlns:source="${xsd.metadata.source}"
			xmlns:event="${xsd.metadata.event}"
			xmlns:person="${xsd.metadata.person}"
			xmlns:organizationalunit="${xsd.metadata.organizationalunit}"
			>
			<xsl:copy-of select="@*"/>
			<xsl:apply-templates/>
		</container:container>
	</xsl:template>
	
	<xsl:template match="publication:creator">
		<eterms:creator>
			<xsl:copy-of select="@*"/>
			<xsl:apply-templates/>
		</eterms:creator>
	</xsl:template>
	
	<xsl:template match="escidocprofile:publication">
		<publication:publication>
			<xsl:copy-of select="@*"/>
			<xsl:apply-templates/>
		</publication:publication>
	</xsl:template>
	
	<xsl:template match="escidoc:person">
		<person:person>
			<xsl:copy-of select="@*"/>
			<xsl:apply-templates/>
		</person:person>
	</xsl:template>
	
	<xsl:template match="publication:source">
		<source:source>
			<xsl:copy-of select="@*"/>
			<xsl:apply-templates/>
		</source:source>
	</xsl:template>
	
	<xsl:template match="escidoc:source">
		<source:source>
			<xsl:copy-of select="@*"/>
			<xsl:apply-templates/>
		</source:source>
	</xsl:template>
	
	<xsl:template match="publication:event">
		<event:event>
			<xsl:copy-of select="@*"/>
			<xsl:apply-templates/>
		</event:event>
	</xsl:template>
	
	<xsl:template match="publication:total-number-of-pages">
		<eterms:total-number-of-pages>
			<xsl:copy-of select="@*"/>
			<xsl:apply-templates/>
		</eterms:total-number-of-pages>
	</xsl:template>
	
	<xsl:template match="publication:degree">
		<eterms:degree>
			<xsl:copy-of select="@*"/>
			<xsl:apply-templates/>
		</eterms:degree>
	</xsl:template>
	
	<xsl:template match="publication:publishing-info">
		<eterms:publishing-info>
			<xsl:copy-of select="@*"/>
			<xsl:apply-templates/>
		</eterms:publishing-info>
	</xsl:template>
	
	<xsl:template match="publication:review-method">
		<eterms:review-method>
			<xsl:copy-of select="@*"/>
			<xsl:apply-templates/>
		</eterms:review-method>
	</xsl:template>
	
	<xsl:template match="publication:location">
		<eterms:location>
			<xsl:copy-of select="@*"/>
			<xsl:apply-templates/>
		</eterms:location>
	</xsl:template>
	
	<xsl:template match="publication:published-online">
		<eterms:published-online>
			<xsl:copy-of select="@*"/>
			<xsl:apply-templates/>
		</eterms:published-online>
	</xsl:template>

	<xsl:template match="escidoc:organization">
		<organization:organization>
			<xsl:copy-of select="@*"/>
			<xsl:apply-templates/>
		</organization:organization>
	</xsl:template>

	<xsl:template match="escidoc:organization/escidoc:organization-name">
		<dc:title>
			<xsl:copy-of select="@*"/>
			<xsl:apply-templates/>
		</dc:title>
	</xsl:template>

	<xsl:template match="organization:organization/escidoc:identifier">
		<dc:identifier>
			<xsl:copy-of select="@*"/>
			<xsl:apply-templates/>
		</dc:identifier>
	</xsl:template>
	
	<xsl:template match="escidoc:person/escidoc:identifier">
		<dc:identifier>
			<xsl:copy-of select="@*"/>
			<xsl:apply-templates/>
		</dc:identifier>
	</xsl:template>
	
	<xsl:template match="organization:organizational-unit">
		<organizationalunit:organizational-unit>
			<xsl:copy-of select="@*"/>
			<xsl:apply-templates/>
		</organizationalunit:organizational-unit>
	</xsl:template>
	
	<xsl:template match="organization:organizational-unit/organization:city">
		<eterms:city>
			<xsl:copy-of select="@*"/>
			<xsl:apply-templates/>
		</eterms:city>
	</xsl:template>
	
	<xsl:template match="organization:organizational-unit/organization:country">
		<eterms:country>
			<xsl:copy-of select="@*"/>
			<xsl:apply-templates/>
		</eterms:country>
	</xsl:template>
	
	<xsl:template match="organization:organizational-unit/organization:start-date">
		<eterms:start-date>
			<xsl:copy-of select="@*"/>
			<xsl:apply-templates/>
		</eterms:start-date>
	</xsl:template>
	
	<xsl:template match="organization:organizational-unit/organization:end-date">
		<eterms:end-date>
			<xsl:copy-of select="@*"/>
			<xsl:apply-templates/>
		</eterms:end-date>
	</xsl:template>

</xsl:stylesheet>
