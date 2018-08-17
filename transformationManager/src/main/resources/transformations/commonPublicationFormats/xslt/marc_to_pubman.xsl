<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
	xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl"
	xmlns:xhtml="http://www.w3.org/1999/xhtml"
	xmlns:saxon="http://saxon.sf.net/"
	xmlns:misc="http://www.editura.de/ns/2012/misc"
	xmlns:misc-marc="http://www.editura.de/ns/2012/misc-marc"
	xmlns:local="http://www.editura.de/ns/2012/local"
	xmlns:tools="http://www.editura.de/ns/2012/tools"
	xmlns:dc="${xsd.metadata.dc}"
	xmlns:dcterms="${xsd.metadata.dcterms}"
	xmlns:escidocComponents="${xsd.soap.item.components}"
	xmlns:escidocItem="${xsd.soap.item.item}"
	xmlns:escidocItemList="${xsd.soap.item.itemlist}"
	xmlns:escidocMetadataRecords="${xsd.soap.common.metadatarecords}"
	xmlns:eterms="${xsd.metadata.escidocprofile.types}"
	xmlns:event="${xsd.metadata.event}"
	xmlns:eves="http://purl.org/escidoc/metadata/ves/0.1/"
	xmlns:file="${xsd.metadata.file}"
	xmlns:organization="${xsd.metadata.organization}"
	xmlns:person="${xsd.metadata.person}"
	xmlns:prop="${xsd.soap.common.prop}"
	xmlns:publication="${xsd.metadata.publication}"
	xmlns:source="${xsd.metadata.source}"
	xmlns:srel="${xsd.soap.common.srel}"
	xmlns:xlink="http://www.w3.org/1999/xlink"
	xmlns:marc="http://www.loc.gov/MARC21/slim" exclude-result-prefixes="xs xd misc local tools" version="2.0">
	<xsl:import href="mapping_commons.xsl"/>
	<xsl:import href="mapping_commons_marc.xsl"/>
	<xsl:strip-space elements="*"/>
	<xsl:output method="xml" encoding="UTF-8" indent="yes"/>
	<xsl:template match="/" xml:id="match-root">
		<xsl:apply-templates/>
	</xsl:template>
	<xsl:template match="marc:collection" xml:id="match-marc_collection" as="element()">
		<xsl:variable name="temp-result" as="element()+">
			<xsl:apply-templates/>
		</xsl:variable>
		<xsl:choose>
			<xsl:when test="$misc:target-format eq 'eSciDoc-publication-item'">
				<xsl:sequence select="$temp-result"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="misc:make_escidocItemList-item-list">
					<xsl:with-param name="escidocItem:item" as="element()+" select="$temp-result"/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="marc:record" as="element(escidocItem:item)" xml:id="match_marc-record">
		<xsl:call-template name="misc:message">
			<xsl:with-param name="level">DEBUG</xsl:with-param>
			<xsl:with-param name="message">processing //marc:record[
				<xsl:sequence select="count(preceding-sibling::marc:record) + 1"/>]
			</xsl:with-param>
			<xsl:with-param name="show-context" select="false()"/>
		</xsl:call-template>
		<xsl:call-template name="misc:make_escidocItem-item">
			<xsl:with-param name="escidocItem:properties" as="element()">
				<xsl:call-template name="misc:make_escidocItem-properties"/>
			</xsl:with-param>
			<xsl:with-param name="escidocMetadataRecords:md-records" as="element()">
				<xsl:call-template name="misc:make_escidocMetadataRecords-md-records">
					<xsl:with-param name="escidocMetadataRecords:md-record" as="element()">
						<xsl:call-template name="misc:make_escidocMetadataRecords-md-record">
							<xsl:with-param name="publication:publication_or_file-file" as="element()">
								<xsl:variable name="retrieved-dates" as="element()*" select="local:dates(.)"/>
								<xsl:call-template name="misc:make_publication-publication">
									<xsl:with-param name="att_type">
										<xsl:sequence select="misc-marc:pubman-genre(.)"/>
									</xsl:with-param>
									<xsl:with-param name="eterms:creator" as="element()+">
										<xsl:choose>
											<xsl:when test="*[local:is-eterms-creator(.)]">
												<xsl:apply-templates select="*[local:is-eterms-creator(.)]"/>
											</xsl:when>
											<xsl:otherwise>
												<xsl:call-template name="misc:message">
													<xsl:with-param name="level">INFO</xsl:with-param>
													<xsl:with-param name="message">marc:record without author or editor</xsl:with-param>
													<xsl:with-param name="show-context" select="false()"/>
												</xsl:call-template>
												<xsl:call-template name="misc:make_eterms-creator_from_person">
													<xsl:with-param name="att_role">http://www.loc.gov/loc.terms/relators/AUT</xsl:with-param>
													<xsl:with-param name="person:person" as="element()">
														<xsl:call-template name="misc:make_person-person">
															<xsl:with-param name="eterms:complete-name" as="element()">
																<xsl:sequence select="misc:create_eterms-complete-name($misc:anonymous-name)"/>
															</xsl:with-param>
															<xsl:with-param name="eterms:family-name" select="misc:create_eterms-family-name($misc:anonymous-name)"/>
														</xsl:call-template>
													</xsl:with-param>
												</xsl:call-template>
											</xsl:otherwise>
										</xsl:choose>
									</xsl:with-param>
									<xsl:with-param name="dc:title" as="element()">
										<xsl:sequence select="local:dc-title(.)"/>
									</xsl:with-param>
									<xsl:with-param name="dc:language" select="local:dc-language(.)"/>
									<xsl:with-param name="dcterms:alternative" as="element()*">
										<xsl:apply-templates select="local:datafield(., ('246') )"/>
									</xsl:with-param>
									<xsl:with-param name="dc:identifier" as="element()*">
										<xsl:apply-templates select="local:datafield(., ('020', '022', '024', '856') )"/>
									</xsl:with-param>
									<xsl:with-param name="eterms:publishing-info" as="element()?">
										<xsl:sequence select="local:eterms-publishing-info(.)"/>
									</xsl:with-param>
									<xsl:with-param name="dcterms:created" as="element()*">
										<xsl:sequence select="$retrieved-dates[self::dcterms:created]"/>
									</xsl:with-param>
									<xsl:with-param name="dcterms:modified" as="element()*">
										<xsl:sequence select="$retrieved-dates[self::dcterms:modified]"/>
									</xsl:with-param>
									<xsl:with-param name="dcterms:dateSubmitted" as="element()?"/>
									<xsl:with-param name="dcterms:dateAccepted" as="element()?"/>
									<xsl:with-param name="eterms:published-online" as="element()*">
										<xsl:sequence select="$retrieved-dates[self::eterms:published-online]"/>
									</xsl:with-param>
									<xsl:with-param name="dcterms:issued" as="element()*">
										<xsl:sequence select="$retrieved-dates[self::dcterms:issued]"/>
									</xsl:with-param>
									<xsl:with-param name="eterms:review-method" as="element()?"/>
									<xsl:with-param name="source:source" as="element()*">
										<xsl:apply-templates select="local:datafield(., ('490', '770', '773') )"/>
									</xsl:with-param>
									<xsl:with-param name="event:event" as="element()?">
										<xsl:apply-templates select="(local:datafield(., ('111', '711') ), local:datafield(., ('518') ) )[1]" mode="MakeEventEvent"/>
									</xsl:with-param>
									<xsl:with-param name="eterms:total-number-of-pages" as="element()?">
										<xsl:apply-templates select="local:datafield(., '300')"/>
									</xsl:with-param>
									<xsl:with-param name="dcterms:abstract" as="element()*">
										<xsl:apply-templates select="local:datafield(., '520', '3')"/>
									</xsl:with-param>
									<xsl:with-param name="dc:subject" as="element()*">
										<xsl:apply-templates select="local:datafield(., '082')"/>
									</xsl:with-param>
									<xsl:with-param name="dcterms:subject" as="element()*">
										<xsl:apply-templates select="local:datafield(., '653', ' ', '0')"/>
									</xsl:with-param>
									<xsl:with-param name="dcterms:tableOfContents" as="element()?">
										<xsl:apply-templates select="local:datafield(., '505')"/>
									</xsl:with-param>
								</xsl:call-template>
							</xsl:with-param>
						</xsl:call-template>
					</xsl:with-param>
				</xsl:call-template>
			</xsl:with-param>
			<xsl:with-param name="escidocComponents:components" as="element()">
				<xsl:call-template name="misc:make_escidocComponents-components">
					<xsl:with-param name="escidocComponents:component" as="element()*">
						<xsl:apply-templates select="local:get-datafields-which-refer-to-downloadable-files(.)" mode="MakeComponent"/>
					</xsl:with-param>
				</xsl:call-template>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="marc:datafield[@tag = ('100', '700')]" as="element(eterms:creator)" xml:id="match-100-700">
		<xsl:call-template name="misc:make_eterms-creator_from_person">
			<xsl:with-param name="att_role">
				<xsl:sequence select="local:creator-role(.)"/>
			</xsl:with-param>
			<xsl:with-param name="person:person" as="element()">
				<xsl:call-template name="local:make_person-person_from_x00"/>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="marc:datafield[@tag = ('110', '111', '710', '711')]" as="element(eterms:creator)" xml:id="match-110-111-710-711">
		<xsl:call-template name="misc:make_eterms-creator_from_organization">
			<xsl:with-param name="att_role">
				<xsl:sequence select="local:creator-role(.)"/>
			</xsl:with-param>
			<xsl:with-param name="organization:organization" as="element()">
				<xsl:call-template name="local:make_organization-organization_from_x10_x11"/>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="marc:datafield[@tag eq '856']" as="element(escidocComponents:component)?" mode="MakeComponent" xml:id="match-856-MakeComponent">
		<xsl:variable name="import-URI" as="xs:string" select="local:downloadable-document-uri-from-856(.)"/>
		<xsl:variable name="filename" as="xs:string?" select="tools:fileName-and-fileExtention-from-url($import-URI)"/>
		<xsl:variable name="mediatype-from-subfield-q" as="xs:string" select="normalize-space(local:subfield(., 'q'))"/>
		<xsl:variable name="mediatype-from-filename" as="xs:string?" select="tools:mediatype-from-url($import-URI)"/>
		<xsl:variable name="mediatype" as="xs:string?" select="($mediatype-from-subfield-q, $mediatype-from-filename)[normalize-space(.)][1]"/>
		<xsl:if test="$import-URI">
			<xsl:call-template name="misc:make_escidocComponents-component">
				<xsl:with-param name="escidocComponents:properties" as="element()">
					<xsl:call-template name="misc:make_escidocComponents-properties">
						<xsl:with-param name="prop:visibility" as="element()">
							<xsl:variable name="datafield-506-ind1-0" as="element(marc:datafield)?" select="local:datafield(.., '506', '0')"/>
							<xsl:variable name="datafield-506-subfield-f" as="element(marc:subfield)?" select="local:datafield(.., '506')/local:subfield(., 'f')[normalize-space(.)]"/>
							<xsl:variable name="subfields-z" as="xs:string*" select="local:subfield(., 'z')/normalize-space()[.]"/>
							<xsl:variable name="visibility" as="xs:string">
								<xsl:choose>
									<xsl:when test="$datafield-506-ind1-0">public</xsl:when>
									<xsl:when test="$datafield-506-subfield-f and (every $i in $datafield-506-subfield-f satisfies $i eq 'Unrestricted online access')">public</xsl:when>
									<xsl:when test="$datafield-506-subfield-f and (every $i in $datafield-506-subfield-f satisfies $i eq 'Online access with authorization')">audience</xsl:when>
									<xsl:when test="$datafield-506-subfield-f and (every $i in $datafield-506-subfield-f satisfies $i eq 'No online access')">private</xsl:when>
									<xsl:when test="$subfields-z and (every $i in $subfields-z satisfies lower-case($i) eq 'audience')">audience</xsl:when>
									<xsl:when test="$subfields-z and (every $i in $subfields-z satisfies lower-case($i) eq 'private')">private</xsl:when>
									<xsl:when test="$subfields-z and (every $i in $subfields-z satisfies lower-case($i) eq 'public')">public</xsl:when>
									<xsl:otherwise>
										<xsl:sequence select="$misc:default-prop-visibility"/>
									</xsl:otherwise>
								</xsl:choose>
							</xsl:variable>
							<xsl:sequence select="misc:create_prop-visibility($visibility)"/>
						</xsl:with-param>
						<xsl:with-param name="prop:content-category" select="local:prop-content-category(.)"/>
						<xsl:with-param name="prop:mime-type" select="misc:create_prop-mime-type($mediatype)"/>
					</xsl:call-template>
				</xsl:with-param>
				<xsl:with-param name="escidocComponents:content" as="element()">
					<xsl:call-template name="misc:make_escidocComponents-content">
						<xsl:with-param name="att_xlink-title" select="$filename"/>
						<xsl:with-param name="att_xlink-href" select="$import-URI"/>
						<xsl:with-param name="att_storage" select="$misc:default-content-att_storage"/>
					</xsl:call-template>
				</xsl:with-param>
				<xsl:with-param name="escidocMetadataRecords:md-records" as="element()">
					<xsl:call-template name="misc:make_escidocMetadataRecords-md-records">
						<xsl:with-param name="escidocMetadataRecords:md-record" as="element()">
							<xsl:call-template name="misc:make_escidocMetadataRecords-md-record">
								<xsl:with-param name="publication:publication_or_file-file" as="element()">
									<xsl:call-template name="misc:make_file-file">
										<xsl:with-param name="dc:title" select="misc:create_dc-title($filename)"/>
										<xsl:with-param name="dc:description" select="misc:create_dc-description($misc:default-file-dc-description)"/>
										<xsl:with-param name="dc:format" select="misc:create_dc-format($mediatype)"/>
										<xsl:with-param name="dcterms:extent" select="misc:dcterms-extent-from-file-uri($import-URI)"/>
										<xsl:with-param name="dcterms:dateCopyrighted" as="element()?" select="local:dcterms-dateCopyrighted(..)"/>
										<xsl:with-param name="dc:rights" select="local:dc-rights(..)" as="element()"/>
										<xsl:with-param name="dcterms:license" select="local:dcterms-license(..)" as="element()"/>
									</xsl:call-template>
								</xsl:with-param>
							</xsl:call-template>
						</xsl:with-param>
					</xsl:call-template>
				</xsl:with-param>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
	<xsl:template match="marc:datafield[@tag eq '505']" as="element(dcterms:tableOfContents)?" xml:id="match-505">
		<xsl:variable name="content-505-a" as="xs:string" select="string-join(local:subfield(., 'a'), ' ')"/>
		<xsl:if test="$content-505-a">
			<xsl:sequence select="misc:create_dcterms-tableOfContents(concat(misc-marc:display-constant-505(.), $content-505-a ) )"/>
		</xsl:if>
	</xsl:template>
	<xsl:template match="marc:datafield[@tag = ('111', '711')]" as="element(event:event)" mode="MakeEventEvent" xml:id="match-111-711-MakeEventEvent">
		<xsl:variable name="dates" as="xs:string+" select="tools:start-date-and-end-date(local:subfield(., 'd'))"/>
		<xsl:call-template name="misc:make_event-event">
			<xsl:with-param name="dc:title" select="misc:create_dc-title(normalize-space(local:subfield(., 'a') ) )" as="element()"/>
			<xsl:with-param name="eterms:start-date" as="element()?">
				<xsl:if test="$dates[1]">
					<xsl:sequence select="misc:create_eterms-start-date($dates[1])"/>
				</xsl:if>
			</xsl:with-param>
			<xsl:with-param name="eterms:end-date" as="element()?">
				<xsl:if test="$dates[2]">
					<xsl:sequence select="misc:create_eterms-end-date($dates[2])"/>
				</xsl:if>
			</xsl:with-param>
			<xsl:with-param name="eterms:place" as="element()?">
				<xsl:if test="normalize-space(local:subfield(., 'c'))">
					<xsl:sequence select="misc:create_eterms-place(local:subfield(., 'c'))"/>
				</xsl:if>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="marc:datafield[@tag eq '490']" as="element(source:source)" xml:id="match-490">
		<xsl:call-template name="misc:make_source-source">
			<xsl:with-param name="att_type">http://purl.org/escidoc/metadata/ves/publication-types/series</xsl:with-param>
			<xsl:with-param name="dc:title" select="misc:create_dc-title(string-join(local:subfield(., 'a'), ', ') )" as="element()"/>
			<xsl:with-param name="eterms:volume" as="element()?">
				<xsl:for-each select="string-join(local:subfield(., 'v')[normalize-space(.)], '; ')[normalize-space(.)]">
					<xsl:sequence select="misc:create_eterms-volume(.)"/>
				</xsl:for-each>
			</xsl:with-param>
			<xsl:with-param name="dc:identifier" as="element()*">
				<xsl:for-each select="local:subfield(., 'x')[normalize-space(.)]">
					<xsl:sequence select="misc:create_dc-identifier_from_issn(.)"/>
				</xsl:for-each>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="marc:datafield[@tag = ('770', '773')]" as="element(source:source)" xml:id="match-770-773">
		<xsl:variable name="volume-issue-etc" as="element()">
			<root>
				<xsl:sequence select="local:parse-952(../marc:datafield[@tag eq '952'])"/>
				<xsl:sequence select="misc:parse-773-q-SICI(local:subfield(., 'q'))"/>
			</root>
		</xsl:variable>
		<xsl:call-template name="misc:make_source-source">
			<xsl:with-param name="att_type">
				<xsl:choose>
					<xsl:when test="@tag eq '770'">http://purl.org/escidoc/metadata/ves/publication-types/issue</xsl:when>
					<xsl:when test="(@tag eq '773') and (misc-marc:pubman-genre(..) eq 'http://purl.org/escidoc/metadata/ves/publication-types/article')">http://purl.org/escidoc/metadata/ves/publication-types/journal</xsl:when>
					<xsl:when test="(@tag eq '773') and (misc-marc:pubman-genre(..) eq 'http://purl.org/eprint/type/BookItem')">http://purl.org/eprint/type/Book</xsl:when>
					<xsl:when test="(@tag eq '773') and (substring(local:subfield(., '7'), 4, 1) eq 'm')">http://purl.org/eprint/type/Book</xsl:when>
					<xsl:when test="(@tag eq '773') and (substring(local:subfield(., '7'), 4, 1) eq 's')">http://purl.org/escidoc/metadata/ves/publication-types/journal</xsl:when>
					<xsl:when test="(@tag eq '773') and local:subfield(., 'z')[normalize-space(.)]">http://purl.org/eprint/type/Book</xsl:when>
					<xsl:when test="(@tag eq '773') and local:subfield(., 'x')[normalize-space(.)]">http://purl.org/escidoc/metadata/ves/publication-types/journal</xsl:when>
					<xsl:otherwise>http://purl.org/escidoc/metadata/ves/publication-types/other</xsl:otherwise>
				</xsl:choose>
			</xsl:with-param>
			<xsl:with-param name="dc:title" select="misc:create_dc-title(local:subfield(., 't') )" as="element()"/>
			<xsl:with-param name="dcterms:alternative" as="element()*">
				<xsl:for-each select="local:subfield(., 'p')[normalize-space(.)]">
					<xsl:sequence select="misc:create_dcterms-alternative(., 'eterms:ABBREVIATION')"/>
				</xsl:for-each>
			</xsl:with-param>
			<xsl:with-param name="eterms:volume" as="element()?">
				<xsl:choose>
					<xsl:when test="$volume-issue-etc/eterms:volume">
						<xsl:sequence select="$volume-issue-etc/eterms:volume[1]"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:for-each select="string-join(local:subfield(., 'v')[normalize-space(.)], '; ')[normalize-space(.)]">
							<xsl:sequence select="misc:create_eterms-volume(.)"/>
						</xsl:for-each>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:with-param>
			<xsl:with-param name="eterms:issue" select="$volume-issue-etc/eterms:issue[1]"/>
			<xsl:with-param name="eterms:start-page" select="$volume-issue-etc/eterms:start-page[1]"/>
			<xsl:with-param name="eterms:end-page" select="$volume-issue-etc/eterms:end-page[1]"/>
			<xsl:with-param name="eterms:total-number-of-pages" select="$volume-issue-etc/eterms:total-number-of-pages[1]"/>
			<xsl:with-param name="dc:identifier" as="element()*">
				<xsl:for-each select="local:subfield(., 'o')[normalize-space(.)]">
					<xsl:sequence select="misc:dc-identifier(.)"/>
				</xsl:for-each>
				<xsl:for-each select="local:subfield(., 'x')[normalize-space(.)]">
					<xsl:sequence select="misc:create_dc-identifier_from_issn(.)"/>
				</xsl:for-each>
				<xsl:for-each select="local:subfield(., 'y')[normalize-space(.)]">
					<xsl:sequence select="misc:create_dc-identifier_from_coden(.)"/>
				</xsl:for-each>
				<xsl:for-each select="local:subfield(., 'z')[normalize-space(.)]">
					<xsl:sequence select="misc:create_dc-identifier_from_isbn(.)"/>
				</xsl:for-each>
			</xsl:with-param>
			<xsl:with-param name="eterms:publishing-info" as="element()?">
				<xsl:for-each select="local:subfield(., 'd')[normalize-space(.)]">
					<xsl:call-template name="misc:make_eterms-publishing-info">
						<xsl:with-param name="dc:publisher" select="misc:create_dc-publisher(.)"/>
						<xsl:with-param name="eterms:edition" as="element()?">
							<xsl:for-each select="local:subfield(.., 'b')[normalize-space(.)]">
								<xsl:sequence select="misc:create_eterms-edition(.)"/>
							</xsl:for-each>
						</xsl:with-param>
					</xsl:call-template>
				</xsl:for-each>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="marc:datafield[@tag eq '518']" as="element(event:event)?" mode="MakeEventEvent" xml:id="match-518-MakeEventEvent">
		<xsl:if test="not(local:datafield(.., ('111', '711') ) )">
			<xsl:variable name="start-date-and-end-date" as="xs:string*" select="tools:start-date-and-end-date(local:subfield(., 'd')[normalize-space(.)][1])"/>
			<xsl:call-template name="misc:make_event-event">
				<xsl:with-param name="dc:title" as="element()">
					<xsl:sequence select="misc:create_dc-title(normalize-space(local:subfield(., 'a') ) )"/>
				</xsl:with-param>
				<xsl:with-param name="dcterms:alternative" as="element()?">
					<xsl:for-each select="local:subfield(., 'o')[normalize-space(.)]">
						<xsl:sequence select="misc:create_dcterms-alternative(.)"/>
					</xsl:for-each>
				</xsl:with-param>
				<xsl:with-param name="eterms:start-date" as="element()?">
					<xsl:if test="$start-date-and-end-date[1]">
						<xsl:sequence select="misc:create_eterms-start-date($start-date-and-end-date[1])"/>
					</xsl:if>
				</xsl:with-param>
				<xsl:with-param name="eterms:end-date" as="element()?">
					<xsl:if test="$start-date-and-end-date[2]">
						<xsl:sequence select="misc:create_eterms-end-date($start-date-and-end-date[2])"/>
					</xsl:if>
				</xsl:with-param>
				<xsl:with-param name="eterms:place" as="element()?">
					<xsl:for-each select="string-join(local:subfield(., 'p')[normalize-space(.)], ', ')">
						<xsl:sequence select="misc:create_eterms-place(.)"/>
					</xsl:for-each>
				</xsl:with-param>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
	<xsl:template match="marc:datafield[local:is-eterms-creator(.)]/marc:subfield[@code eq 'c']" xml:id="tm-df_creator-sf_c" as="element(eterms:person-title)">
		<xsl:sequence select="misc:create_eterms-person-title(normalize-space(.))"/>
	</xsl:template>
	<xsl:template match="marc:datafield[local:is-eterms-creator(.)]/marc:subfield[@code eq 'q']" xml:id="tm-df_creator-sf_q" as="element(eterms:alternative-name)">
		<xsl:sequence select="misc:create_eterms-alternative-name(normalize-space(.))"/>
	</xsl:template>
	<xsl:template match="marc:datafield[local:is-eterms-creator(.)]/marc:subfield[@code eq 'u']" xml:id="tm-df_creator-sf_u" as="element(organization:organization)">
		<xsl:call-template name="misc:make_organization-organization">
			<xsl:with-param name="dc:title" as="element()?">
				<xsl:sequence select="misc:create_dc-title(normalize-space(.))"/>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="marc:datafield[@tag eq '300']" as="element(eterms:total-number-of-pages)?" xml:id="match-300">
		<xsl:variable name="extent" as="xs:string" select="string-join(local:subfield(., 'a')[normalize-space(.)], ', ')"/>
		<xsl:if test="$extent">
			<xsl:sequence select="misc:create_eterms-total-number-of-pages($extent)"/>
		</xsl:if>
	</xsl:template>
	<xsl:template match="marc:datafield[@tag eq '020']" as="element(dc:identifier)*" xml:id="match-020">
		<xsl:for-each select="marc:subfield[@code eq 'a'][normalize-space(.)]">
			<xsl:sequence select="misc:create_dc-identifier_from_isbn(normalize-space(.))"/>
		</xsl:for-each>
	</xsl:template>
	<xsl:template match="marc:datafield[@tag eq '022']" as="element(dc:identifier)*" xml:id="match-022">
		<xsl:for-each select="marc:subfield[@code eq 'a'][normalize-space(.)]">
			<xsl:sequence select="misc:create_dc-identifier_from_issn(normalize-space(.))"/>
		</xsl:for-each>
	</xsl:template>
	<xsl:template match="marc:datafield[@tag eq '024']" as="element(dc:identifier)?" xml:id="match-024">
		<xsl:variable name="prepared" as="xs:string" select="normalize-space(marc:subfield[@code eq 'a'][1])"/>
		<xsl:variable name="type-of-sf_2" as="xs:string" select="misc:parse-identifier-types(marc:subfield[@code eq '2'])"/>
		<xsl:if test="$prepared">
			<xsl:choose>
				<xsl:when test="$type-of-sf_2 ne 'eterms:OTHER'">
					<xsl:sequence select="misc:create_dc-identifier($type-of-sf_2, $prepared)"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:sequence select="misc:dc-identifier($prepared)"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>
	</xsl:template>
	<xsl:template match="marc:datafield[@tag eq '030']" as="element(dc:identifier)*" xml:id="match-030">
		<xsl:for-each select="marc:subfield[@code eq 'a'][normalize-space(.)]">
			<xsl:sequence select="misc:create_dc-identifier_from_coden(normalize-space(.))"/>
		</xsl:for-each>
	</xsl:template>
	<xsl:template match="marc:datafield[@tag eq '082']" as="element(dc:subject)*" xml:id="match-082">
		<xsl:for-each select="marc:subfield[@code eq 'a'][normalize-space(.)]">
			<xsl:sequence select="misc:create_dc-subject(normalize-space(.))"/>
		</xsl:for-each>
	</xsl:template>
	<xsl:template match="marc:datafield[@tag eq '246'][not(@ind2 = ('0', '3'))]" as="element(dcterms:alternative)*" xml:id="match-246">
		<xsl:for-each select="marc:subfield[@code eq 'a'][normalize-space(.)]">
			<xsl:sequence select="misc:create_dcterms-alternative(normalize-space(.))"/>
		</xsl:for-each>
	</xsl:template>
	<xsl:template match="marc:datafield[@tag eq '246'][@ind2 eq '0']" as="element(dcterms:alternative)*" xml:id="match-246-#0">
		<xsl:for-each select="marc:subfield[@code eq 'a'][normalize-space(.)]">
			<xsl:sequence select="misc:create_dcterms-alternative(normalize-space(.), 'eterms:ABBREVIATION')"/>
		</xsl:for-each>
	</xsl:template>
	<xsl:template match="marc:datafield[@tag eq '246'][@ind2 eq '3']" as="element(dcterms:alternative)*" xml:id="match-246-#3">
		<xsl:for-each select="marc:subfield[@code eq 'a'][normalize-space(.)]">
			<xsl:sequence select="misc:create_dcterms-alternative(normalize-space(.), 'eterms:OTHER')"/>
		</xsl:for-each>
	</xsl:template>
	<xsl:template match="marc:datafield[@tag eq '520'][@ind1 = (' ', '#', '3')]" as="element(dcterms:abstract)*" xml:id="match-520-3">
		<xsl:if test="some $i in local:subfield(., ('a', 'b')) satisfies normalize-space($i)">
			<xsl:sequence select="misc:create_dcterms-abstract(string-join(local:subfield(., ('a', 'b'))/normalize-space(.), ' '))"/>
		</xsl:if>
	</xsl:template>
	<xsl:template match="marc:datafield[@tag eq '653']" as="element(dcterms:subject)*" xml:id="match-653">
		<xsl:for-each select="marc:subfield[@code eq 'a'][normalize-space(.)]">
			<xsl:sequence select="misc:create_dcterms-subject(normalize-space(.))"/>
		</xsl:for-each>
	</xsl:template>
	<xsl:template match="marc:datafield[@tag eq '856']" as="element(dc:identifier)*" xml:id="match-856">
		<xsl:if test="@ind1 eq '4'">
			<xsl:for-each select="marc:subfield[@code eq 'u'][normalize-space(.)]">
				<xsl:sequence select="misc:create_dc-identifier_from_uri(normalize-space(.))"/>
			</xsl:for-each>
		</xsl:if>
	</xsl:template>
	<xsl:template match="*" mode="#all" as="node()?" xml:id="match-all">
		<xsl:call-template name="misc:message">
			<xsl:with-param name="level">INFO</xsl:with-param>
			<xsl:with-param name="message">[marc_to_pubman.xsl#match-all]
				<xsl:if test="function-available('saxon:current-mode-name')">[Mode: 
					<xsl:sequence select="saxon:current-mode-name()" use-when="function-available('saxon:current-mode-name')"/>]
				</xsl:if> no matching template found
			</xsl:with-param>
			<xsl:with-param name="show-context" select="true()"/>
		</xsl:call-template>
	</xsl:template>
	<xsl:function name="local:mock-up-001" as="element(person:person)?">
		<xsl:param name="context" as="element(marc:datafield)"/>
		<xsl:for-each select="$context">
			<xsl:call-template name="local:make_person-person_from_x00"/>
		</xsl:for-each>
	</xsl:function>
	<xsl:template name="local:make_person-person_from_x00" as="element(person:person)?" xml:id="make_person_person_from_x00">
		<xsl:variable name="authors" as="element(authors)" select="misc:author-decoder(local:subfield(.,'a')/local:clean-up-string(.))"/>
		<xsl:variable name="family-name" as="xs:string?">
			<xsl:choose>
				<xsl:when test="@ind1 eq '1'">
					<xsl:sequence select="concat(if (normalize-space($authors/author[1]/prefix)) then concat($authors/author[1]/prefix, ' ') else (), $authors/author[1]/familyname)"/>
				</xsl:when>
				<xsl:when test="@ind1 eq '3'">
					<xsl:sequence select="local:clean-up-string(local:subfield(.,'a') )"/>
				</xsl:when>
				<xsl:when test="@ind1 eq '0'"/>
				<xsl:otherwise>
					<xsl:sequence select="replace(local:clean-up-string(local:subfield(.,'a') ), '^([^,]+),?.*$', '$1')"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="given-name" as="xs:string?">
			<xsl:choose>
				<xsl:when test="@ind1 eq '0'">
					<xsl:sequence select="local:clean-up-string(local:subfield(.,'a') )"/>
				</xsl:when>
				<xsl:when test="@ind1 eq '1'">
					<xsl:sequence select="$authors/author[1]/givenname"/>
				</xsl:when>
			</xsl:choose>
		</xsl:variable>
		<xsl:call-template name="misc:make_person-person">
			<xsl:with-param name="eterms:complete-name" as="element()">
				<xsl:sequence select="misc:create_eterms-complete-name(normalize-space(concat($given-name, ' ', $family-name) ) )"/>
			</xsl:with-param>
			<xsl:with-param name="eterms:family-name" as="element()">
				<xsl:sequence select="misc:create_eterms-family-name($family-name, false())"/>
			</xsl:with-param>
			<xsl:with-param name="eterms:given-name" as="element()?">
				<xsl:if test="normalize-space($given-name)">
					<xsl:sequence select="misc:create_eterms-given-name($given-name)"/>
				</xsl:if>
			</xsl:with-param>
			<xsl:with-param name="eterms:alternative-name" as="element()?">
				<xsl:apply-templates select="local:subfield(., 'q')"/>
			</xsl:with-param>
			<xsl:with-param name="eterms:person-title" as="element()?">
				<xsl:apply-templates select="local:subfield(., 'c')"/>
			</xsl:with-param>
			<xsl:with-param name="organization:organization" as="element()?">
				<xsl:apply-templates select="local:subfield(., 'u')"/>
			</xsl:with-param>
			<xsl:with-param name="dc:identifier" as="element()*">
				<xsl:for-each select="local:subfield(., '0')[normalize-space(.)]">
					<xsl:sequence select="misc:dc-identifier(.)"/>
				</xsl:for-each>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>
	<xsl:template name="local:make_organization-organization_from_x10_x11" as="element(organization:organization)" xml:id="make_organization-organization_from_x10_x11">
		<xsl:call-template name="misc:make_organization-organization">
			<xsl:with-param name="dc:title" as="element()">
				<xsl:sequence select="misc:create_dc-title( string-join( ( local:subfield(., 'a')/normalize-space(.), local:subfield(., 'b')/normalize-space(.) )[normalize-space(.)], '; ' ) )"/>
			</xsl:with-param>
			<xsl:with-param name="eterms:address" as="element()?">
				<xsl:if test="local:subfield(., 'c')[normalize-space(.)]">
					<xsl:sequence select="misc:create_eterms-address(local:subfield(., 'c')[normalize-space(.)][1])"/>
				</xsl:if>
			</xsl:with-param>
			<xsl:with-param name="dc:identifier" as="element()?">
				<xsl:for-each select="local:subfield(., '0')[normalize-space(.)][1]">
					<xsl:sequence select="misc:dc-identifier(.)"/>
				</xsl:for-each>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>
	<xsl:function name="local:eterms-publishing-info" as="element(eterms:publishing-info)?" xml:id="fct_local_eterms-publishing-info">
		<xsl:param name="MARC_record" as="element(marc:record)"/>
		<xsl:variable name="dc-publisher-content-collection" as="xs:string*">
			<xsl:for-each select="$MARC_record/local:datafield(., '260')">
				<xsl:variable name="temp" as="xs:string*">
					<xsl:sequence select="(local:subfield(., ('b', 'f'))/local:clean-up-string(.)[normalize-space(.)])[1]"/>
				</xsl:variable>
				<xsl:sequence select="string-join($temp, ', ')"/>
			</xsl:for-each>
		</xsl:variable>
		<xsl:variable name="dc-publisher-content" as="xs:string" select="string-join($dc-publisher-content-collection, '; ')"/>
		<xsl:if test="some $i in $dc-publisher-content satisfies boolean($i)">
			<xsl:call-template name="misc:make_eterms-publishing-info">
				<xsl:with-param name="dc:publisher" as="element()">
					<xsl:sequence select="misc:create_dc-publisher($dc-publisher-content)"/>
				</xsl:with-param>
				<xsl:with-param name="eterms:place" as="element()*">
					<xsl:variable name="eterms-place-content-collection" as="xs:string*">
						<xsl:for-each select="$MARC_record/local:datafield(., '260')">
							<xsl:variable name="temp" as="xs:string*">
								<xsl:sequence select="(local:subfield(., ('a', 'e'))//local:clean-up-string(.)[normalize-space(.)])[1]"/>
							</xsl:variable>
							<xsl:sequence select="string-join($temp, ', ')"/>
						</xsl:for-each>
					</xsl:variable>
					<xsl:variable name="eterms-place-content" as="xs:string" select="string-join($eterms-place-content-collection, '; ')"/>
					<xsl:if test="$eterms-place-content">
						<xsl:sequence select="misc:create_eterms-place($eterms-place-content)"/>
					</xsl:if>
				</xsl:with-param>
				<xsl:with-param name="eterms:edition" as="element()?">
					<xsl:variable name="eterms-edition-content" as="xs:string" select="string-join(local:datafield($MARC_record, '250')/local:subfield(., ('a', 'b') ), '')"/>
					<xsl:if test="$eterms-edition-content">
						<xsl:sequence select="misc:create_eterms-edition($eterms-edition-content)"/>
					</xsl:if>
				</xsl:with-param>
			</xsl:call-template>
		</xsl:if>
	</xsl:function>
	<xsl:function name="local:dc-title" as="element(dc:title)" xml:id="fnc_local_dc-title">
		<xsl:param name="MARC_record" as="element(marc:record)"/>
		<xsl:variable name="title-string" as="xs:string">
			<xsl:choose>
				<xsl:when test="some $i in local:datafield($MARC_record, '245')/local:subfield(., 'a') satisfies normalize-space($i)">
					<xsl:sequence select="local:clean-up-string(string-join(local:datafield($MARC_record, '245')/local:subfield(., ('a', 'b'))[normalize-space(.)], '') )"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:sequence select="$misc:default-publication-title"/>
					<xsl:call-template name="misc:message">
						<xsl:with-param name="level">INFO</xsl:with-param>
						<xsl:with-param name="show-context" select="false()"/>
						<xsl:with-param name="message">[marc_to_pubman.xsl#local_dc-title] could not retrieve title from marc record //marc:record[(marc:leader eq '
							<xsl:value-of select="$MARC_record/marc:leader"/>') and (marc:controlfield[@tag eq 008] eq '
							<xsl:value-of select="$MARC_record/marc:controlfield[@tag eq '008']"/>')]
						</xsl:with-param>
					</xsl:call-template>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:sequence select="misc:create_dc-title($title-string)"/>
	</xsl:function>
	<xsl:function name="local:is-eterms-creator" as="xs:boolean">
		<xsl:param name="marc:datafield" as="element()"/>
		<xsl:sequence select="some $i in ('100', '110', '111', '700', '710', '711') satisfies $marc:datafield/@tag eq $i"/>
	</xsl:function>
	<xsl:function name="local:clean-up-string" as="xs:string">
		<xsl:param name="string" as="xs:string?"/>
		<xsl:variable name="abbreviations" as="xs:string">etc|usw|u.a|z.B</xsl:variable>
		<xsl:variable name="prepared" as="xs:string" select="replace(normalize-space($string), concat('(', $abbreviations, ')\.$') , '$1#')"/>
		<xsl:variable name="processed" as="xs:string" select="replace($prepared, '^[ ;,:.]+|[ ;,:./]+$|&lt;&lt;|&gt;&gt;', '')"/>
		<xsl:sequence select="replace($processed, concat('(', $abbreviations, ')#$') , '$1.')"/>
	</xsl:function>
	<xsl:function name="local:is-online-resource" as="xs:boolean">
		<xsl:param name="MARC_record" as="element(marc:record)"/>
		<xsl:choose>
			<xsl:when test="misc-marc:form-of-item($MARC_record) = 'o'">
				<xsl:sequence select="true()"/>
			</xsl:when>
			<xsl:when test="local:get-datafields-which-refer-to-downloadable-files($MARC_record)">
				<xsl:sequence select="true()"/>
			</xsl:when>
			<xsl:when test="some $i in ('online', 'elektronische ressource') satisfies $MARC_record/local:datafield(., '245')/local:subfield(., 'h')/lower-case(.)[contains(., $i)]">
				<xsl:sequence select="true()"/>
			</xsl:when>
			<xsl:when test="some $i in ('online', 'elektronische ressource') satisfies $MARC_record/local:datafield(., '533')/local:subfield(., 'n')/lower-case(.)[contains(., $i)]">
				<xsl:sequence select="true()"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:sequence select="false()"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:function>
	<xsl:function name="local:dates" as="element()*" xml:id="fct_local_dates">
		<xsl:param name="MARC_record" as="element(marc:record)"/>
		<xsl:variable name="cf008" as="xs:string?" select="$MARC_record/local:controlfield(., '008')"/>
		<xsl:variable name="type-of-date_publication-status" as="xs:string" select="misc-marc:type-of-date_publication-status($cf008)"/>
		<xsl:variable name="is-online-resource" as="xs:boolean" select="local:is-online-resource($MARC_record)"/>
		<xsl:variable name="type-of-cf008" as="xs:string" select="misc-marc:type-of-cf008($MARC_record)"/>
		<xsl:variable name="date1" as="xs:string?" select="normalize-space(substring($cf008, 8, 4))[tools:is-W3CDTF(.)]"/>
		<xsl:variable name="date2" as="xs:string?" select="normalize-space(substring($cf008, 12, 4))[tools:is-W3CDTF(.)]"/>
		<xsl:choose>
			<xsl:when test="$type-of-date_publication-status = ('p', 'r', 't') and $is-online-resource">
				<xsl:sequence select="misc:create_eterms-published-online($date1)"/>
				<xsl:sequence select="misc:create_dcterms-created($date2)"/>
			</xsl:when>
			<xsl:when test="$type-of-date_publication-status = ('p', 'r', 't') and not($is-online-resource)">
				<xsl:sequence select="misc:create_dcterms-issued($date1)"/>
				<xsl:sequence select="misc:create_dcterms-created($date2)"/>
			</xsl:when>
			<xsl:when test="$date1 and $is-online-resource">
				<xsl:sequence select="misc:create_eterms-published-online($date1)"/>
			</xsl:when>
			<xsl:when test="$date1 and not($is-online-resource)">
				<xsl:sequence select="misc:create_dcterms-issued($date1)"/>
			</xsl:when>
		</xsl:choose>
		<xsl:for-each select="$MARC_record/local:datafield(., '046')/local:subfield(.,'j')[local:parse-date(.)][1]">
			<xsl:sequence select="misc:create_dcterms-modified(.)"/>
		</xsl:for-each>
	</xsl:function>
	<xsl:function name="local:get-datafields-which-refer-to-downloadable-files" as="element(marc:datafield)*">
		<xsl:param name="MARC_record" as="element(marc:record)"/>
		<xsl:sequence select="$MARC_record/local:datafield(., '856')[local:downloadable-document-uri-from-856(.)]"/>
	</xsl:function>
	<xsl:function name="local:downloadable-document-uri-from-856" as="xs:string">
		<xsl:param name="datafield-856" as="element(marc:datafield)"/>
		<xsl:choose>
			<xsl:when test="$datafield-856/@ind1 ne '4'">
				<xsl:sequence select="''"/>
			</xsl:when>
			<xsl:when test="$datafield-856[marc:subfield[@code eq 'y'] = ('Volltext')]/local:subfield(., 'u')[normalize-space(.)]">
				<xsl:sequence select="$datafield-856[marc:subfield[@code eq 'y'] = ('Volltext')]/local:subfield(., 'u')[normalize-space(.)][1]"/>
			</xsl:when>
			<xsl:when test="$datafield-856[@ind2 eq '0']/local:subfield(., 'u')[normalize-space(.)]">
				<xsl:sequence select="$datafield-856[@ind2 eq '0']/local:subfield(., 'u')[normalize-space(.)][1]"/>
			</xsl:when>
			<xsl:when test="$datafield-856[@ind2 eq '1']/local:subfield(., 'u')[normalize-space(.)]">
				<xsl:sequence select="$datafield-856[@ind2 eq '1']/local:subfield(., 'u')[normalize-space(.)][1]"/>
			</xsl:when>
			<xsl:when test="misc-marc:form-of-item($datafield-856/parent::marc:record) eq 'o' and $datafield-856/local:subfield(., 'u')[normalize-space(.)]">
				<xsl:sequence select="$datafield-856/local:subfield(., 'u')[normalize-space(.)][1]"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:sequence select="''"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:function>
	<xsl:function name="local:parse-date" as="xs:string">
		<xsl:param name="input" as="xs:string?"/>
		<xsl:variable name="prepared" as="xs:string" select="upper-case(translate(normalize-space($input), ' ', '') )"/>
		<xsl:choose>
			<xsl:when test="matches($prepared, '^[0-9]{4,8}$')">
				<xsl:sequence select="string-join((substring($prepared, 1, 4), substring($prepared, 5, 2), substring($prepared, 7, 2))[normalize-space(.)], '-')"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:sequence select="substring-after(tools:parse-dates-as-W3CDTF($input)[1], '/')"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:function>
	<xsl:function name="local:parse-952" as="element()*">
		<xsl:param name="df952" as="element(marc:datafield)?"/>
		<xsl:if test="normalize-space($df952/@tag) eq '952'">
			<xsl:variable name="volume" as="xs:string" select="normalize-space($df952/local:subfield(., 'd') )"/>
			<xsl:variable name="issue" as="xs:string" select="normalize-space($df952/local:subfield(., 'e') )"/>
			<xsl:variable name="total-number-of-pages" as="xs:string" select="normalize-space($df952/local:subfield(., 'g') )"/>
			<xsl:variable name="pages" as="xs:string" select="normalize-space($df952/local:subfield(., 'h') )"/>
			<xsl:variable name="year" as="xs:string" select="normalize-space($df952/local:subfield(., 'j') )"/>
			<xsl:variable name="parsed-pages" as="xs:string*" select="misc:parse-pages($pages)"/>
			<xsl:variable name="total-number-of-pages_2" as="xs:string?" select="misc:total-number-of-pages($parsed-pages[1], $parsed-pages[2])"/>
			<xsl:if test="$volume">
				<xsl:sequence select="misc:create_eterms-volume($volume)"/>
			</xsl:if>
			<xsl:if test="$issue">
				<xsl:sequence select="misc:create_eterms-issue($issue)"/>
			</xsl:if>
			<xsl:choose>
				<xsl:when test="$total-number-of-pages">
					<xsl:sequence select="misc:create_eterms-total-number-of-pages($total-number-of-pages)"/>
				</xsl:when>
				<xsl:when test="$total-number-of-pages_2">
					<xsl:sequence select="misc:create_eterms-total-number-of-pages($total-number-of-pages_2)"/>
				</xsl:when>
			</xsl:choose>
			<xsl:if test="$year">
				<xsl:sequence select="misc:create_dcterms-issued($year)"/>
			</xsl:if>
			<xsl:if test="$parsed-pages[1]">
				<xsl:sequence select="misc:create_eterms-start-page($parsed-pages[1])"/>
			</xsl:if>
			<xsl:if test="$parsed-pages[2]">
				<xsl:sequence select="misc:create_eterms-end-page($parsed-pages[2])"/>
			</xsl:if>
		</xsl:if>
	</xsl:function>
	<xsl:function name="local:creator-role" as="xs:string">
		<xsl:param name="datafield" as="element(marc:datafield)"/>
		<xsl:choose>
			<xsl:when test="$datafield/local:subfield(., '4')[normalize-space(.)]/misc-marc:marc_relator_code-to-eterms_creator_role(.)[normalize-space(.)]">
				<xsl:sequence select="$datafield/local:subfield(., '4')[normalize-space(.)]/misc-marc:marc_relator_code-to-eterms_creator_role(.)[normalize-space(.)][1]"/>
			</xsl:when>
			<xsl:when test="not($datafield/@tag = ('111', '711')) and $datafield/local:subfield(., 'e')[normalize-space(.)]/misc-marc:marc_relator_term-to-eterms_creator_role(.)[normalize-space(.)]">
				<xsl:sequence select="$datafield/local:subfield(., 'e')[normalize-space(.)]/misc-marc:marc_relator_term-to-eterms_creator_role(.)[normalize-space(.)][1]"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:sequence select="misc-marc:marc_relator_code-to-eterms_creator_role('aut')"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:function>
	<xsl:function name="local:prop-content-category" as="element(prop:content-category)">
		<xsl:param name="datafield-856" as="element(marc:datafield)"/>
		<xsl:variable name="content-category" as="xs:string">
			<xsl:choose>
				<xsl:when test="$datafield-856[@ind2 = ('0', '1')] and local:datafield($datafield-856/.., ('020', '022'))/local:subfield(., ('a'))[normalize-space(.)]">publisher-version</xsl:when>
				<xsl:when test="$datafield-856[@ind2 = ('0', '1')] and local:datafield($datafield-856/.., ('490', '770', '773'))/local:subfield(., ('x', 'z'))[normalize-space(.)]">publisher-version</xsl:when>
				<xsl:otherwise>any-fulltext</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:sequence select="misc:create_prop-content-category($content-category)"/>
	</xsl:function>
	<xsl:function name="local:dc-rights" as="element(dc:rights)">
		<xsl:param name="MARC_record" as="element(marc:record)"/>
		<xsl:variable name="datafield-542-subfield-f" as="xs:string" select="string-join( distinct-values( local:datafield($MARC_record, '542')/local:subfield(., 'f')/normalize-space(.)[.] ), '; ' )"/>
		<xsl:variable name="dc-rights-content" as="xs:string">
			<xsl:choose>
				<xsl:when test="$datafield-542-subfield-f">
					<xsl:sequence select="$datafield-542-subfield-f"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:sequence select="$misc:default-dc-rights"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:sequence select="misc:create_dc-rights($dc-rights-content)"/>
	</xsl:function>
	<xsl:function name="local:dcterms-dateCopyrighted" as="element(dcterms:dateCopyrighted)?">
		<xsl:param name="MARC_record" as="element(marc:record)"/>
		<xsl:variable name="copyright-dates" as="xs:string*" select="$MARC_record/local:datafield($MARC_record, '542')/local:subfield(., 'g')[normalize-space(.)]/local:parse-date(.)[normalize-space(.)]"/>
		<xsl:if test="count(distinct-values($copyright-dates)) eq 1">
			<xsl:sequence select="misc:create_dcterms-dateCopyrighted($copyright-dates[1])"/>
		</xsl:if>
	</xsl:function>
	<xsl:function name="local:dcterms-license" as="element(dcterms:license)">
		<xsl:param name="MARC_record" as="element(marc:record)"/>
		<xsl:variable name="datafield-540-subfield-a" as="xs:string" select="string-join( distinct-values( local:datafield($MARC_record, '540')/local:subfield(., 'a')/normalize-space(.)[.] ), '; ' )"/>
		<xsl:variable name="dcterms-license-content" as="xs:string">
			<xsl:choose>
				<xsl:when test="$datafield-540-subfield-a">
					<xsl:sequence select="$datafield-540-subfield-a"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:sequence select="$misc:default-dcterms-license"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:sequence select="misc:create_dcterms-license($dcterms-license-content)"/>
	</xsl:function>
	<xsl:function name="local:dc-language" as="element(dc:language)*" xml:id="fct_local-dc-language">
		<xsl:param name="MARC_record" as="element(marc:record)"/>
		<xsl:for-each select="misc-marc:languages($MARC_record)">
			<xsl:sequence select="misc:create_dc-language(misc:iso-639-2_to_iso-639-3(.))"/>
		</xsl:for-each>
	</xsl:function>
	<xsl:function name="local:controlfield" as="element(marc:controlfield)*">
		<xsl:param name="marc-record" as="element(marc:record)"/>
		<xsl:param name="tags" as="xs:string+"/>
		<xsl:sequence select="$marc-record/marc:controlfield[some $i in $tags satisfies (@tag eq $i)]"/>
	</xsl:function>
	<xsl:function name="local:datafield" as="element(marc:datafield)*">
		<xsl:param name="marc-record" as="element(marc:record)"/>
		<xsl:param name="tags" as="xs:string+"/>
		<xsl:sequence select="$marc-record/marc:datafield[some $i in $tags satisfies (@tag eq $i)]"/>
	</xsl:function>
	<xsl:function name="local:datafield" as="element(marc:datafield)*">
		<xsl:param name="marc-record" as="element(marc:record)"/>
		<xsl:param name="tags" as="xs:string+"/>
		<xsl:param name="ind1" as="xs:string?"/>
		<xsl:sequence select="local:datafield($marc-record, $tags)[if ((@ind1, $ind1) = (' ', '#')) then true() else @ind1 eq $ind1]"/>
	</xsl:function>
	<xsl:function name="local:datafield" as="element(marc:datafield)*">
		<xsl:param name="marc-record" as="element(marc:record)"/>
		<xsl:param name="tags" as="xs:string+"/>
		<xsl:param name="ind1" as="xs:string?"/>
		<xsl:param name="ind2" as="xs:string?"/>
		<xsl:sequence select="local:datafield($marc-record, $tags, $ind1)[if ((@ind2, $ind2) = (' ', '#')) then true() else @ind2 eq $ind2]"/>
	</xsl:function>
	<xsl:function name="local:subfield" as="element(marc:subfield)*">
		<xsl:param name="datafield" as="element(marc:datafield)"/>
		<xsl:param name="codes" as="xs:string+"/>
		<xsl:sequence select="$datafield/marc:subfield[some $i in $codes satisfies (@code eq $i)]"/>
	</xsl:function>
</xsl:stylesheet>