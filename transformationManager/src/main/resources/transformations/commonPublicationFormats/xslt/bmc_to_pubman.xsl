<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
	xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl"
	xmlns:xhtml="http://www.w3.org/1999/xhtml"
	xmlns:saxon="http://saxon.sf.net/"
	xmlns:misc="http://www.editura.de/ns/2012/misc"
	xmlns:tools="http://www.editura.de/ns/2012/tools"
	xmlns:local="http://www.editura.de/ns/2012/local"
	xmlns:dc="${xsd.metadata.dc}"
	xmlns:dcterms="${xsd.metadata.dcterms}"
	xmlns:escidocComponents="${xsd.soap.item.components}"
	xmlns:escidocItem="${xsd.soap.item.item}"
	xmlns:escidocItemList="${xsd.soap.item.itemlist}"
	xmlns:escidocMetadataRecords="${xsd.soap.common.metadatarecords}"
	xmlns:eterms="${xsd.metadata.escidocprofile.types}"
	xmlns:event="${xsd.metadata.event}"
	xmlns:eves="http://purl.org/escidoc/metadata/ves/0.1/"
	xmlns:organization="${xsd.metadata.organization}"
	xmlns:person="${xsd.metadata.person}"
	xmlns:prop="${xsd.soap.common.prop}"
	xmlns:publication="${xsd.metadata.publication}"
	xmlns:source="${xsd.metadata.source}"
	xmlns:srel="${xsd.soap.common.srel}"
	xmlns:bmc="http://www.biomedcentral.com/xml/schemas/" exclude-result-prefixes="xsl xs xd xhtml saxon misc local" version="2.0">
	<xsl:import href="mapping_commons.xsl"/>
	<xsl:output method="xml" encoding="UTF-8" indent="yes"/>
	<xsl:param name="Files_to_Import" as="xs:string?">both</xsl:param>
	<xsl:variable name="local:import-xml" as="xs:boolean">
		<xsl:choose>
			<xsl:when test="normalize-space(lower-case($Files_to_Import)) eq 'pdf'">
				<xsl:sequence select="false()"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:sequence select="true()"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:variable>
	<xsl:variable name="local:import-pdf" as="xs:boolean">
		<xsl:choose>
			<xsl:when test="normalize-space(lower-case($Files_to_Import)) eq 'xml'">
				<xsl:sequence select="false()"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:sequence select="true()"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:variable>
	<xsl:variable name="local:default-bmc-article-ui-href-prefix-xml" as="xs:anyURI">http://www.biomedcentral.com/content/download/xml/</xsl:variable>
	<xsl:variable name="local:default-bmc-article-ui-href-suffix-xml" as="xs:anyURI">.xml</xsl:variable>
	<xsl:variable name="local:default-bmc-article-ui-href-prefix-pdf" as="xs:anyURI">http://www.biomedcentral.com/content/pdf/</xsl:variable>
	<xsl:variable name="local:default-bmc-article-ui-href-suffix-pdf" as="xs:anyURI">.pdf</xsl:variable>
	<xsl:variable name="local:default-bmc-article-ui-href-prefix-html" as="xs:anyURI">http://www.biomedcentral.com/</xsl:variable>
	<xsl:variable name="misc:default-prop-visibility" as="xs:string">public</xsl:variable>
	<xsl:variable name="misc:default-dc-rights" as="xs:string">http://www.biomedcentral.com/info/about/copyright</xsl:variable>
	<xsl:variable name="misc:default-dcterms-license" as="xs:string">http://www.biomedcentral.com/info/about/license</xsl:variable>
	<xsl:variable name="misc:default-file-dc-description" as="xs:string" select="concat('File downloaded from BioMed Central at ', if ($misc:run-in-testmode) then misc:format-date-time(xs:dateTime('1900-01-01T00:00:01') ) else misc:format-date-time(current-dateTime() ) )"/>
	<xsl:key name="genres_bmc_to_pubman" match="misc:mapping-table/misc:mapping" use="lower-case(normalize-space(misc:source))"/>
	<xsl:variable name="local:mapping_bmc_genres">
		<misc:mapping-table xml:id="mapping_bmc_genres" group="BMC dochead to PubMan genre">
			<misc:mapping>
				<misc:source>Other</misc:source>
				<misc:target misc:match="100" desc="Other">http://purl.org/escidoc/metadata/ves/publication-types/other</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Analytic Perspective</misc:source>
				<misc:target desc="Other">http://purl.org/escidoc/metadata/ves/publication-types/other</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Annotated Bibliography</misc:source>
				<misc:target desc="Other">http://purl.org/escidoc/metadata/ves/publication-types/other</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Article</misc:source>
				<misc:target misc:match="100" desc="Article">http://purl.org/escidoc/metadata/ves/publication-types/article</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Article de Recherche</misc:source>
				<misc:target desc="Article">http://purl.org/escidoc/metadata/ves/publication-types/article</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Article Original</misc:source>
				<misc:target desc="Article">http://purl.org/escidoc/metadata/ves/publication-types/article</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Book Report</misc:source>
				<misc:target desc="Book Review">http://purl.org/escidoc/metadata/ves/publication-types/book-review</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Book Review</misc:source>
				<misc:target misc:match="100" desc="Book Review">http://purl.org/escidoc/metadata/ves/publication-types/book-review</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Brief Communication</misc:source>
				<misc:target desc="Other">http://purl.org/escidoc/metadata/ves/publication-types/other</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Brief Report</misc:source>
				<misc:target desc="Report">http://purl.org/eprint/type/Report</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Brief Reports</misc:source>
				<misc:target desc="Report">http://purl.org/eprint/type/Report</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Case Control Study</misc:source>
				<misc:target desc="Other">http://purl.org/escidoc/metadata/ves/publication-types/other</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Case Report</misc:source>
				<misc:target desc="Report">http://purl.org/eprint/type/Report</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Case Study</misc:source>
				<misc:target desc="Other">http://purl.org/escidoc/metadata/ves/publication-types/other</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Clinical Study</misc:source>
				<misc:target desc="Other">http://purl.org/escidoc/metadata/ves/publication-types/other</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Cohort Study</misc:source>
				<misc:target desc="Other">http://purl.org/escidoc/metadata/ves/publication-types/other</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Comment</misc:source>
				<misc:target desc="Other">http://purl.org/escidoc/metadata/ves/publication-types/other</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Commentaries</misc:source>
				<misc:target desc="Other">http://purl.org/escidoc/metadata/ves/publication-types/other</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Commentary</misc:source>
				<misc:target desc="Other">http://purl.org/escidoc/metadata/ves/publication-types/other</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Conference Proceedings</misc:source>
				<misc:target desc="Proceedings">http://purl.org/escidoc/metadata/ves/publication-types/proceedings</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Correction</misc:source>
				<misc:target desc="Other">http://purl.org/escidoc/metadata/ves/publication-types/other</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Correspondence</misc:source>
				<misc:target desc="Other">http://purl.org/escidoc/metadata/ves/publication-types/other</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Current Concept</misc:source>
				<misc:target desc="Other">http://purl.org/escidoc/metadata/ves/publication-types/other</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Data Note</misc:source>
				<misc:target desc="Other">http://purl.org/escidoc/metadata/ves/publication-types/other</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Database</misc:source>
				<misc:target desc="Other">http://purl.org/escidoc/metadata/ves/publication-types/other</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Debate</misc:source>
				<misc:target desc="Other">http://purl.org/escidoc/metadata/ves/publication-types/other</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Debate Article</misc:source>
				<misc:target desc="Article">http://purl.org/escidoc/metadata/ves/publication-types/article</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Discovery Notes</misc:source>
				<misc:target desc="Other">http://purl.org/escidoc/metadata/ves/publication-types/other</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Editorial</misc:source>
				<misc:target misc:match="100" desc="Other">http://purl.org/escidoc/metadata/ves/publication-types/other</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Editorial Commentary</misc:source>
				<misc:target desc="Other">http://purl.org/escidoc/metadata/ves/publication-types/other</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Epilogue</misc:source>
				<misc:target desc="Other">http://purl.org/escidoc/metadata/ves/publication-types/other</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Erratum</misc:source>
				<misc:target desc="Other">http://purl.org/escidoc/metadata/ves/publication-types/other</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Featured Talk Presentation</misc:source>
				<misc:target desc="Talk">http://purl.org/escidoc/metadata/ves/publication-types/talk-at-event</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Focus</misc:source>
				<misc:target desc="Other">http://purl.org/escidoc/metadata/ves/publication-types/other</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Foreword</misc:source>
				<misc:target desc="Other">http://purl.org/escidoc/metadata/ves/publication-types/other</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Free Software Session Presentation</misc:source>
				<misc:target desc="Talk">http://purl.org/escidoc/metadata/ves/publication-types/talk-at-event</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Full Research Paper</misc:source>
				<misc:target desc="Working Paper">http://purl.org/escidoc/metadata/ves/publication-types/paper</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Guide</misc:source>
				<misc:target desc="Other">http://purl.org/escidoc/metadata/ves/publication-types/other</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Guidelines</misc:source>
				<misc:target desc="Other">http://purl.org/escidoc/metadata/ves/publication-types/other</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Hypothesis</misc:source>
				<misc:target misc:match="90" desc="Other">http://purl.org/escidoc/metadata/ves/publication-types/other</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Introduction</misc:source>
				<misc:target desc="Other">http://purl.org/escidoc/metadata/ves/publication-types/other</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Introductory Lecture</misc:source>
				<misc:target desc="Other">http://purl.org/escidoc/metadata/ves/publication-types/other</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Introductory Review</misc:source>
				<misc:target desc="Other">http://purl.org/escidoc/metadata/ves/publication-types/other</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source misc:invited="true">Invited Lecture Presentation</misc:source>
				<misc:target desc="Talk">http://purl.org/escidoc/metadata/ves/publication-types/talk-at-event</misc:target>
				<misc:remark>In addition, the transformation produces an event:event/eterms:invitation-status = invited</misc:remark>
			</misc:mapping>
			<misc:mapping>
				<misc:source misc:invited="true">Invited Speaker Presentation</misc:source>
				<misc:target desc="Talk">http://purl.org/escidoc/metadata/ves/publication-types/talk-at-event</misc:target>
				<misc:remark>In addition, the transformation produces an event:event/eterms:invitation-status = invited</misc:remark>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Keynote Lecture Presentation</misc:source>
				<misc:target desc="Talk">http://purl.org/escidoc/metadata/ves/publication-types/talk-at-event</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Keynote Presentation</misc:source>
				<misc:target desc="Talk">http://purl.org/escidoc/metadata/ves/publication-types/talk-at-event</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Keynote Speaker Presentation</misc:source>
				<misc:target desc="Talk">http://purl.org/escidoc/metadata/ves/publication-types/talk-at-event</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Latest Therapeutic Developments</misc:source>
				<misc:target desc="Other">http://purl.org/escidoc/metadata/ves/publication-types/other</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Lecture Presentation</misc:source>
				<misc:target desc="Talk">http://purl.org/escidoc/metadata/ves/publication-types/talk-at-event</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Letter</misc:source>
				<misc:target desc="Other">http://purl.org/escidoc/metadata/ves/publication-types/other</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Letter to Editor</misc:source>
				<misc:target desc="Other">http://purl.org/escidoc/metadata/ves/publication-types/other</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Letter to the Editor</misc:source>
				<misc:target desc="Other">http://purl.org/escidoc/metadata/ves/publication-types/other</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Letter to the Editors</misc:source>
				<misc:target desc="Other">http://purl.org/escidoc/metadata/ves/publication-types/other</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Letters to the Editor</misc:source>
				<misc:target desc="Other">http://purl.org/escidoc/metadata/ves/publication-types/other</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Meeting Abstract</misc:source>
				<misc:target misc:match="100" desc="Meeting Abstract">http://purl.org/escidoc/metadata/ves/publication-types/meeting-abstract</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Meeting Abstracts</misc:source>
				<misc:target desc="Meeting Abstract">http://purl.org/escidoc/metadata/ves/publication-types/meeting-abstract</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Meeting Report</misc:source>
				<misc:target desc="Conference Report">http://purl.org/escidoc/metadata/ves/publication-types/conference-report</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Meeting Reports</misc:source>
				<misc:target desc="Conference Report">http://purl.org/escidoc/metadata/ves/publication-types/conference-report</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Method</misc:source>
				<misc:target desc="Other">http://purl.org/escidoc/metadata/ves/publication-types/other</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Methodology</misc:source>
				<misc:target desc="Other">http://purl.org/escidoc/metadata/ves/publication-types/other</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Methodology Article</misc:source>
				<misc:target desc="Article">http://purl.org/escidoc/metadata/ves/publication-types/article</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Mini-Review</misc:source>
				<misc:target desc="Other">http://purl.org/escidoc/metadata/ves/publication-types/other</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Minireview</misc:source>
				<misc:target desc="Other">http://purl.org/escidoc/metadata/ves/publication-types/other</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Mise au Point</misc:source>
				<misc:target desc="Other">http://purl.org/escidoc/metadata/ves/publication-types/other</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Moderated Poster Presentation</misc:source>
				<misc:target desc="Talk">http://purl.org/escidoc/metadata/ves/publication-types/talk-at-event</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Musings</misc:source>
				<misc:target desc="Other">http://purl.org/escidoc/metadata/ves/publication-types/other</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Non-randomised Controlled Trial</misc:source>
				<misc:target desc="Other">http://purl.org/escidoc/metadata/ves/publication-types/other</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Notice of Redundant Publication</misc:source>
				<misc:target desc="Other">http://purl.org/escidoc/metadata/ves/publication-types/other</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Open Letter</misc:source>
				<misc:target desc="Other">http://purl.org/escidoc/metadata/ves/publication-types/other</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Opinion</misc:source>
				<misc:target desc="Other">http://purl.org/escidoc/metadata/ves/publication-types/other</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Oral Presentation</misc:source>
				<misc:target misc:match="80" desc="Talk">http://purl.org/escidoc/metadata/ves/publication-types/talk-at-event</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Oral Presentations</misc:source>
				<misc:target desc="Talk">http://purl.org/escidoc/metadata/ves/publication-types/talk-at-event</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Oral Presentations - Session 1</misc:source>
				<misc:target desc="Talk">http://purl.org/escidoc/metadata/ves/publication-types/talk-at-event</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Oral Presentations - Session 2</misc:source>
				<misc:target desc="Talk">http://purl.org/escidoc/metadata/ves/publication-types/talk-at-event</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Oral Presentations - Session 3</misc:source>
				<misc:target desc="Talk">http://purl.org/escidoc/metadata/ves/publication-types/talk-at-event</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Oral Presentations - Session 4</misc:source>
				<misc:target desc="Talk">http://purl.org/escidoc/metadata/ves/publication-types/talk-at-event</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Oral Presentations - Session 5</misc:source>
				<misc:target desc="Talk">http://purl.org/escidoc/metadata/ves/publication-types/talk-at-event</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Oral Presentations - Session 6</misc:source>
				<misc:target desc="Talk">http://purl.org/escidoc/metadata/ves/publication-types/talk-at-event</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Original Article</misc:source>
				<misc:target desc="Article">http://purl.org/escidoc/metadata/ves/publication-types/article</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Original Basic Research</misc:source>
				<misc:target desc="Other">http://purl.org/escidoc/metadata/ves/publication-types/other</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Original Clinical Investigation</misc:source>
				<misc:target desc="Other">http://purl.org/escidoc/metadata/ves/publication-types/other</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Original Investigation</misc:source>
				<misc:target desc="Other">http://purl.org/escidoc/metadata/ves/publication-types/other</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Original Research</misc:source>
				<misc:target desc="Other">http://purl.org/escidoc/metadata/ves/publication-types/other</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Overview - Dataset Comparison I</misc:source>
				<misc:target desc="Other">http://purl.org/escidoc/metadata/ves/publication-types/other</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Overview - Dataset Comparison II</misc:source>
				<misc:target desc="Other">http://purl.org/escidoc/metadata/ves/publication-types/other</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Paper</misc:source>
				<misc:target misc:match="100" desc="Working Paper">http://purl.org/escidoc/metadata/ves/publication-types/paper</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Paper Report</misc:source>
				<misc:target desc="Report">http://purl.org/eprint/type/Report</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Perspective</misc:source>
				<misc:target desc="Other">http://purl.org/escidoc/metadata/ves/publication-types/other</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Perspectives</misc:source>
				<misc:target desc="Other">http://purl.org/escidoc/metadata/ves/publication-types/other</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Position Article and Guidelines</misc:source>
				<misc:target desc="Article">http://purl.org/escidoc/metadata/ves/publication-types/article</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Poster Presentation</misc:source>
				<misc:target desc="Talk">http://purl.org/escidoc/metadata/ves/publication-types/talk-at-event</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Poster Presentation without Discussion</misc:source>
				<misc:target desc="Talk">http://purl.org/escidoc/metadata/ves/publication-types/talk-at-event</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Poster Presentations</misc:source>
				<misc:target desc="Talk">http://purl.org/escidoc/metadata/ves/publication-types/talk-at-event</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Preface</misc:source>
				<misc:target desc="Other">http://purl.org/escidoc/metadata/ves/publication-types/other</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Preliminary Communication</misc:source>
				<misc:target desc="Other">http://purl.org/escidoc/metadata/ves/publication-types/other</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Primary Research</misc:source>
				<misc:target desc="Other">http://purl.org/escidoc/metadata/ves/publication-types/other</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Primer</misc:source>
				<misc:target desc="Other">http://purl.org/escidoc/metadata/ves/publication-types/other</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Problem</misc:source>
				<misc:target desc="Other">http://purl.org/escidoc/metadata/ves/publication-types/other</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Proceedings</misc:source>
				<misc:target misc:match="100" desc="Proceedings">http://purl.org/escidoc/metadata/ves/publication-types/proceedings</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Project Note</misc:source>
				<misc:target desc="Other">http://purl.org/escidoc/metadata/ves/publication-types/other</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Protein Family Review</misc:source>
				<misc:target desc="Other">http://purl.org/escidoc/metadata/ves/publication-types/other</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Protocol</misc:source>
				<misc:target desc="Other">http://purl.org/escidoc/metadata/ves/publication-types/other</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Question &amp; Answer</misc:source>
				<misc:target desc="Other">http://purl.org/escidoc/metadata/ves/publication-types/other</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Question and Answer</misc:source>
				<misc:target desc="Other">http://purl.org/escidoc/metadata/ves/publication-types/other</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Questions &amp; Answers</misc:source>
				<misc:target desc="Other">http://purl.org/escidoc/metadata/ves/publication-types/other</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Rapid Communication</misc:source>
				<misc:target desc="Other">http://purl.org/escidoc/metadata/ves/publication-types/other</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Report</misc:source>
				<misc:target misc:match="100" desc="Report">http://purl.org/eprint/type/Report</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Research</misc:source>
				<misc:target desc="Other">http://purl.org/escidoc/metadata/ves/publication-types/other</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Research Article</misc:source>
				<misc:target desc="Article">http://purl.org/escidoc/metadata/ves/publication-types/article</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Research Highlight</misc:source>
				<misc:target desc="Other">http://purl.org/escidoc/metadata/ves/publication-types/other</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Reserach</misc:source>
				<misc:target desc="Other">http://purl.org/escidoc/metadata/ves/publication-types/other</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Resource Review</misc:source>
				<misc:target desc="Other">http://purl.org/escidoc/metadata/ves/publication-types/other</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Retraction</misc:source>
				<misc:target desc="Other">http://purl.org/escidoc/metadata/ves/publication-types/other</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Review</misc:source>
				<misc:target desc="Other">http://purl.org/escidoc/metadata/ves/publication-types/other</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Review and Methods Overview</misc:source>
				<misc:target desc="Other">http://purl.org/escidoc/metadata/ves/publication-types/other</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Review and Recommendations</misc:source>
				<misc:target desc="Other">http://purl.org/escidoc/metadata/ves/publication-types/other</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Review Article</misc:source>
				<misc:target desc="Article">http://purl.org/escidoc/metadata/ves/publication-types/article</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Review of Interventions</misc:source>
				<misc:target desc="Other">http://purl.org/escidoc/metadata/ves/publication-types/other</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Review of Strategies</misc:source>
				<misc:target desc="Other">http://purl.org/escidoc/metadata/ves/publication-types/other</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Reviews</misc:source>
				<misc:target desc="Other">http://purl.org/escidoc/metadata/ves/publication-types/other</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Session Report</misc:source>
				<misc:target desc="Conference Report">http://purl.org/escidoc/metadata/ves/publication-types/conference-report</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Short Communication</misc:source>
				<misc:target desc="Other">http://purl.org/escidoc/metadata/ves/publication-types/other</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Short Paper</misc:source>
				<misc:target desc="Working Paper">http://purl.org/escidoc/metadata/ves/publication-types/paper</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Short Report</misc:source>
				<misc:target desc="Report">http://purl.org/eprint/type/Report</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Short Reports</misc:source>
				<misc:target desc="Report">http://purl.org/eprint/type/Report</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Short Review</misc:source>
				<misc:target desc="Other">http://purl.org/escidoc/metadata/ves/publication-types/other</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Software</misc:source>
				<misc:target desc="Other">http://purl.org/escidoc/metadata/ves/publication-types/other</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Software Article</misc:source>
				<misc:target desc="Article">http://purl.org/escidoc/metadata/ves/publication-types/article</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Software Review</misc:source>
				<misc:target desc="Other">http://purl.org/escidoc/metadata/ves/publication-types/other</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Study Protocol</misc:source>
				<misc:target desc="Other">http://purl.org/escidoc/metadata/ves/publication-types/other</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Supplement</misc:source>
				<misc:target desc="Special Issue">http://purl.org/escidoc/metadata/ves/publication-types/issue</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Supplement Appendix</misc:source>
				<misc:target desc="Other">http://purl.org/escidoc/metadata/ves/publication-types/other</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Survey/Cross Sectional Study</misc:source>
				<misc:target desc="Other">http://purl.org/escidoc/metadata/ves/publication-types/other</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Systematic Review</misc:source>
				<misc:target desc="Other">http://purl.org/escidoc/metadata/ves/publication-types/other</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Systematic Review Protocol</misc:source>
				<misc:target desc="Other">http://purl.org/escidoc/metadata/ves/publication-types/other</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Technical Advance</misc:source>
				<misc:target desc="Other">http://purl.org/escidoc/metadata/ves/publication-types/other</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Technical Innovations</misc:source>
				<misc:target desc="Other">http://purl.org/escidoc/metadata/ves/publication-types/other</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Technical Note</misc:source>
				<misc:target desc="Other">http://purl.org/escidoc/metadata/ves/publication-types/other</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Technical Notes</misc:source>
				<misc:target desc="Other">http://purl.org/escidoc/metadata/ves/publication-types/other</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Technologist Presentation</misc:source>
				<misc:target desc="Talk">http://purl.org/escidoc/metadata/ves/publication-types/talk-at-event</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Tribune libre</misc:source>
				<misc:target desc="Other">http://purl.org/escidoc/metadata/ves/publication-types/other</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Viewpoints</misc:source>
				<misc:target misc:match="90" desc="Other">http://purl.org/escidoc/metadata/ves/publication-types/other</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Workshop Presentation</misc:source>
				<misc:target desc="Talk">http://purl.org/escidoc/metadata/ves/publication-types/talk-at-event</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Book</misc:source>
				<misc:target misc:match="100" desc="Book">http://purl.org/eprint/type/Book</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Book Item</misc:source>
				<misc:target misc:match="100" desc="Book Chapter">http://purl.org/eprint/type/BookItem</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Case Note</misc:source>
				<misc:target desc="Other">http://purl.org/escidoc/metadata/ves/publication-types/other</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Collected Edition</misc:source>
				<misc:target misc:match="100" desc="Collected Edition">http://purl.org/escidoc/metadata/ves/publication-types/collected-edition</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Conference Paper</misc:source>
				<misc:target misc:match="100" desc="Proceedings Paper">http://purl.org/eprint/type/ConferencePaper</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Conference Report</misc:source>
				<misc:target misc:match="100" desc="Conference Report">http://purl.org/escidoc/metadata/ves/publication-types/conference-report</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Conference Poster</misc:source>
				<misc:target misc:match="100" desc="Poster">http://purl.org/eprint/type/ConferencePoster</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Contribution to Handbook</misc:source>
				<misc:target misc:match="100" desc="Contribution to a Handbook">http://purl.org/escidoc/metadata/ves/publication-types/contribution-to-handbook</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Contribution to Encyclopedia</misc:source>
				<misc:target misc:match="100" desc="Contribution to a Encyclopedia">http://purl.org/escidoc/metadata/ves/publication-types/contribution-to-encyclopedia</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Contribution to Festschrift</misc:source>
				<misc:target misc:match="100" desc="Contribution to a Festschrift">http://purl.org/escidoc/metadata/ves/publication-types/contribution-to-festschrift</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Contribution to Commentary</misc:source>
				<misc:target misc:match="100" desc="Other">http://purl.org/escidoc/metadata/ves/publication-types/other</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Contribution to Collected Edition</misc:source>
				<misc:target misc:match="100" desc="Contribution to a Collected edition">http://purl.org/escidoc/metadata/ves/publication-types/contribution-to-collected-edition</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Courseware-Lecture</misc:source>
				<misc:target misc:match="100" desc="Teaching">http://purl.org/escidoc/metadata/ves/publication-types/courseware-lecture</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Encyclopedia</misc:source>
				<misc:target misc:match="100" desc="Encyclopedia">http://purl.org/escidoc/metadata/ves/publication-types/encyclopedia</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Festschrift</misc:source>
				<misc:target misc:match="100" desc="Festschrift">http://purl.org/escidoc/metadata/ves/publication-types/festschrift</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Handbook</misc:source>
				<misc:target misc:match="100" desc="Handbook">http://purl.org/escidoc/metadata/ves/publication-types/handbook</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Issue</misc:source>
				<misc:target misc:match="100" desc="Special Issue">http://purl.org/escidoc/metadata/ves/publication-types/issue</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Journal</misc:source>
				<misc:target misc:match="100" desc="Journal">http://purl.org/escidoc/metadata/ves/publication-types/journal</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Manual</misc:source>
				<misc:target misc:match="100" desc="Manual">http://purl.org/escidoc/metadata/ves/publication-types/manual</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Manuscript</misc:source>
				<misc:target misc:match="100" desc="Manuscript">http://purl.org/escidoc/metadata/ves/publication-types/manuscript</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Monograph</misc:source>
				<misc:target misc:match="100" desc="Monograph">http://purl.org/escidoc/metadata/ves/publication-types/monograph</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Multi Volume Work</misc:source>
				<misc:target misc:match="100" desc="Multi-Volume">http://purl.org/escidoc/metadata/ves/publication-types/multi-volume</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Newspaper</misc:source>
				<misc:target misc:match="100" desc="Newspaper">http://purl.org/escidoc/metadata/ves/publication-types/newspaper</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Newspaper Article</misc:source>
				<misc:target misc:match="100" desc="Newspaper Article">http://purl.org/escidoc/metadata/ves/publication-types/newspaper-article</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Patent</misc:source>
				<misc:target misc:match="100" desc="Patent">http://purl.org/eprint/type/Patent</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Series</misc:source>
				<misc:target misc:match="100" desc="Series">http://purl.org/escidoc/metadata/ves/publication-types/series</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Talk at Event</misc:source>
				<misc:target misc:match="100" desc="Talk">http://purl.org/escidoc/metadata/ves/publication-types/talk-at-event</misc:target>
			</misc:mapping>
			<misc:mapping>
				<misc:source>Thesis</misc:source>
				<misc:target misc:match="100" desc="Thesis">http://purl.org/eprint/type/Thesis</misc:target>
			</misc:mapping>
		</misc:mapping-table>
	</xsl:variable>
	<xsl:template match="/" as="element()" xml:id="match-document-root">
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
	<xsl:template match="*:art" as="element()" xml:id="match-art">
		<xsl:call-template name="misc:make_escidocItem-item">
			<xsl:with-param name="escidocItem:properties" as="element()">
				<xsl:call-template name="misc:make_escidocItem-properties"/>
			</xsl:with-param>
			<xsl:with-param name="escidocMetadataRecords:md-records" as="element()">
				<xsl:call-template name="misc:make_escidocMetadataRecords-md-records">
					<xsl:with-param name="escidocMetadataRecords:md-record" as="element()">
						<xsl:call-template name="misc:make_escidocMetadataRecords-md-record">
							<xsl:with-param name="publication:publication_or_file-file" as="element()">
								<xsl:call-template name="misc:make_publication-publication">
									<xsl:with-param name="att_type">
										<xsl:sequence select="local:escidoc_genre-from-bmc_dochead(*:fm/*:dochead)"/>
									</xsl:with-param>
									<xsl:with-param name="eterms:creator" as="element()+">
										<xsl:apply-templates select="*:fm/*:bibl/*:aug/*"/>
									</xsl:with-param>
									<xsl:with-param name="dc:title" as="element()">
										<xsl:variable name="retrieved-dc_title" as="element(dc:title)?">
											<xsl:apply-templates select="*:fm/*:bibl/*:title"/>
										</xsl:variable>
										<xsl:choose>
											<xsl:when test="$retrieved-dc_title">
												<xsl:sequence select="$retrieved-dc_title"/>
											</xsl:when>
											<xsl:otherwise>
												<xsl:sequence select="misc:create_dc-title($misc:default-publication-title)"/>
											</xsl:otherwise>
										</xsl:choose>
									</xsl:with-param>
									<xsl:with-param name="dc:language" select="misc:create_dc-language('eng')"/>
									<xsl:with-param name="dcterms:alternative" as="element()*">
										<xsl:apply-templates select="*:fm/*:shorttitle"/>
										<xsl:apply-templates select="*:fm/*:bibl/*:trans-title-grp/*:title"/>
									</xsl:with-param>
									<xsl:with-param name="dc:identifier" as="element()*">
										<xsl:apply-templates select="*:ui"/>
										<xsl:apply-templates select="*:fm/*:bibl/*:xrefbib/descendant::*:pubid"/>
									</xsl:with-param>
									<xsl:with-param name="dcterms:dateSubmitted" as="element()?">
										<xsl:apply-templates select="*:fm/*:history/*:rec"/>
									</xsl:with-param>
									<xsl:with-param name="dcterms:dateAccepted" as="element()?">
										<xsl:apply-templates select="*:fm/*:history/*:acc"/>
									</xsl:with-param>
									<xsl:with-param name="eterms:published-online" as="element()?">
										<xsl:variable name="pub-dates" as="element(eterms:published-online)*">
											<xsl:apply-templates select="*:fm/*:history/*:pub"/>
											<xsl:apply-templates select="*:fm/*:bibl/*:pubdate"/>
										</xsl:variable>
										<xsl:sequence select="$pub-dates[1]"/>
									</xsl:with-param>
									<xsl:with-param name="dcterms:issued" as="element()*"/>
									<xsl:with-param name="eterms:review-method" as="element()?">
										<xsl:if test="normalize-space(*:fm/*:history/*:revrec)">
											<xsl:sequence select="misc:create_eterms-review-method('http://purl.org/eprint/status/PeerReviewed')"/>
										</xsl:if>
									</xsl:with-param>
									<xsl:with-param name="source:source" as="element()*">
										<xsl:apply-templates select="*:fm/*:bibl"/>
									</xsl:with-param>
									<xsl:with-param name="event:event" as="element()?">
										<xsl:apply-templates select="*:fm/*:bibl/*:conference"/>
									</xsl:with-param>
									<xsl:with-param name="dcterms:abstract" as="element()*">
										<xsl:apply-templates select="*:fm/*:shortabs, *:fm/*:abs|*:fm/*:trans-abs-grp/*:abs"/>
									</xsl:with-param>
									<xsl:with-param name="dcterms:subject" as="element()*">
										<xsl:apply-templates select="*:fm/*:kwdg/*:kwd|*:fm/*:trans-kwdg-grp/*:kwdg/*:kwd"/>
										<xsl:apply-templates select="*:meta/*:classifications/*:classification"/>
									</xsl:with-param>
									<xsl:with-param name="dcterms:tableOfContents" as="element()?">
										<xsl:call-template name="local:make_ToC"/>
									</xsl:with-param>
								</xsl:call-template>
							</xsl:with-param>
						</xsl:call-template>
					</xsl:with-param>
				</xsl:call-template>
			</xsl:with-param>
			<xsl:with-param name="escidocComponents:components" as="element()?">
				<xsl:call-template name="misc:make_escidocComponents-components">
					<xsl:with-param name="escidocComponents:component" as="element()*">
						<xsl:variable name="dc:rights" select="misc:create_dc-rights( (normalize-space(*:fm/*:cpyrt/*:collab), $misc:default-dc-rights)[normalize-space(.)][1])" as="element()"/>
						<xsl:variable name="dcterms:license" select="misc:create_dcterms-license( (local:string(*:fm/*:cpyrt/*:note/node(), true() ), $misc:default-dcterms-license)[normalize-space(.)][1])" as="element()"/>
						<xsl:if test="$local:import-xml">
							<xsl:variable name="xml-full-path" as="xs:anyURI" select="local:ui-to-bmc-uri(*:ui, 'xml')"/>
							<xsl:variable name="xml-filename" as="xs:string" select="tools:fileName-and-fileExtention-from-url($xml-full-path)"/>
							<xsl:variable name="xml-mediatype" as="xs:string" select="'application/xml'"/>
							<xsl:call-template name="misc:make_escidocComponents-component">
								<xsl:with-param name="escidocComponents:properties" as="element()?">
									<xsl:call-template name="misc:make_escidocComponents-properties">
										<xsl:with-param name="prop:visibility" select="misc:create_prop-visibility($misc:default-prop-visibility)"/>
										<xsl:with-param name="prop:content-category" select="local:prop-content-category(.)"/>
										<xsl:with-param name="prop:mime-type" select="misc:create_prop-mime-type($xml-mediatype)"/>
									</xsl:call-template>
								</xsl:with-param>
								<xsl:with-param name="escidocComponents:content" as="element()?">
									<xsl:call-template name="misc:make_escidocComponents-content">
										<xsl:with-param name="att_xlink-title" select="$xml-filename"/>
										<xsl:with-param name="att_xlink-href" select="$xml-full-path"/>
										<xsl:with-param name="att_storage" select="$misc:default-content-att_storage"/>
									</xsl:call-template>
								</xsl:with-param>
								<xsl:with-param name="escidocMetadataRecords:md-records" as="element()">
									<xsl:call-template name="misc:make_escidocMetadataRecords-md-records">
										<xsl:with-param name="escidocMetadataRecords:md-record" as="element()">
											<xsl:call-template name="misc:make_escidocMetadataRecords-md-record">
												<xsl:with-param name="publication:publication_or_file-file" as="element()">
													<xsl:call-template name="misc:make_file-file">
														<xsl:with-param name="dc:title" select="misc:create_dc-title($xml-filename)"/>
														<xsl:with-param name="dc:description" select="misc:create_dc-description($misc:default-file-dc-description)"/>
														<xsl:with-param name="dc:format" select="misc:create_dc-format($xml-mediatype)"/>
														<xsl:with-param name="dcterms:extent" select="misc:dcterms-extent-from-file-uri($xml-full-path)"/>
														<xsl:with-param name="dcterms:dateCopyrighted" as="element()?">
															<xsl:apply-templates select="*:fm/*:cpyrt/*:year"/>
														</xsl:with-param>
														<xsl:with-param name="dc:rights" select="$dc:rights"/>
														<xsl:with-param name="dcterms:license" select="$dcterms:license" as="element()"/>
													</xsl:call-template>
												</xsl:with-param>
											</xsl:call-template>
										</xsl:with-param>
									</xsl:call-template>
								</xsl:with-param>
							</xsl:call-template>
						</xsl:if>
						<xsl:if test="$local:import-pdf">
							<xsl:variable name="pdf-full-path" as="xs:anyURI" select="local:ui-to-bmc-uri(*:ui, 'pdf')"/>
							<xsl:variable name="pdf-filename" as="xs:string" select="tools:fileName-and-fileExtention-from-url($pdf-full-path)"/>
							<xsl:variable name="pdf-mediatype" as="xs:string" select="'application/pdf'"/>
							<xsl:call-template name="misc:make_escidocComponents-component">
								<xsl:with-param name="escidocComponents:properties" as="element()?">
									<xsl:call-template name="misc:make_escidocComponents-properties">
										<xsl:with-param name="prop:visibility" select="misc:create_prop-visibility($misc:default-prop-visibility)"/>
										<xsl:with-param name="prop:content-category" select="local:prop-content-category(.)"/>
										<xsl:with-param name="prop:mime-type" select="misc:create_prop-mime-type($pdf-mediatype)"/>
									</xsl:call-template>
								</xsl:with-param>
								<xsl:with-param name="escidocComponents:content" as="element()?">
									<xsl:call-template name="misc:make_escidocComponents-content">
										<xsl:with-param name="att_xlink-title" select="$pdf-filename"/>
										<xsl:with-param name="att_xlink-href" select="$pdf-full-path"/>
										<xsl:with-param name="att_storage" select="$misc:default-content-att_storage"/>
									</xsl:call-template>
								</xsl:with-param>
								<xsl:with-param name="escidocMetadataRecords:md-records" as="element()">
									<xsl:call-template name="misc:make_escidocMetadataRecords-md-records">
										<xsl:with-param name="escidocMetadataRecords:md-record" as="element()">
											<xsl:call-template name="misc:make_escidocMetadataRecords-md-record">
												<xsl:with-param name="publication:publication_or_file-file" as="element()">
													<xsl:call-template name="misc:make_file-file">
														<xsl:with-param name="dc:title" select="misc:create_dc-title($pdf-filename)"/>
														<xsl:with-param name="dc:description" select="misc:create_dc-description($misc:default-file-dc-description)"/>
														<xsl:with-param name="dc:format" select="misc:create_dc-format($pdf-mediatype)"/>
														<xsl:with-param name="dcterms:extent" select="misc:dcterms-extent-from-file-uri($pdf-full-path)"/>
														<xsl:with-param name="dcterms:dateCopyrighted" as="element()?">
															<xsl:apply-templates select="*:fm/*:cpyrt/*:year"/>
														</xsl:with-param>
														<xsl:with-param name="dc:rights" select="$dc:rights"/>
														<xsl:with-param name="dcterms:license" select="$dcterms:license" as="element()"/>
													</xsl:call-template>
												</xsl:with-param>
											</xsl:call-template>
										</xsl:with-param>
									</xsl:call-template>
								</xsl:with-param>
							</xsl:call-template>
						</xsl:if>
					</xsl:with-param>
				</xsl:call-template>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="*:au[*:cnm]" as="element()+" xml:id="match-au-cnm">
		<xsl:call-template name="misc:make_eterms-creator_from_organization">
			<xsl:with-param name="att_role">http://www.loc.gov/loc.terms/relators/AUT</xsl:with-param>
			<xsl:with-param name="organization:organization" as="element()">
				<xsl:apply-templates select="*:cnm"/>
			</xsl:with-param>
		</xsl:call-template>
		<xsl:for-each select="*:insr">
			<xsl:call-template name="misc:make_eterms-creator_from_organization">
				<xsl:with-param name="att_role">http://www.loc.gov/loc.terms/relators/AUT</xsl:with-param>
				<xsl:with-param name="organization:organization" as="element()">
					<xsl:apply-templates select="../../../*:insg/*:ins[@id eq current()/@iid]"/>
				</xsl:with-param>
			</xsl:call-template>
		</xsl:for-each>
	</xsl:template>
	<xsl:template match="*:au[not(*:cnm)]" as="element()" xml:id="match-au-not-cnm">
		<xsl:call-template name="misc:make_eterms-creator_from_person">
			<xsl:with-param name="att_role">http://www.loc.gov/loc.terms/relators/AUT</xsl:with-param>
			<xsl:with-param name="person:person" as="element()">
				<xsl:call-template name="misc:make_person-person">
					<xsl:with-param name="eterms:complete-name" select="misc:create_eterms-complete-name(*:fnm, *:mi, *:mnm, *:snm, *:suf)"/>
					<xsl:with-param name="eterms:family-name" as="element()?">
						<xsl:sequence select="misc:create_eterms-family-name( normalize-space( concat( local:string(*:snm, false()), ' ', local:string(*:suf, false()) ) ) )"/>
					</xsl:with-param>
					<xsl:with-param name="eterms:given-name" as="element()?">
						<xsl:sequence select="misc:create_eterms-given-name( normalize-space( concat( local:string(*:fnm, false() ), ' ' , local:string(*:mi, false() ) , ' ', local:string(*:mnm, false() ) ) ) )"/>
					</xsl:with-param>
					<xsl:with-param name="organization:organization" as="element()*">
						<xsl:for-each select="*:insr">
							<xsl:apply-templates select="../../../*:insg/*:ins[@id eq current()/@iid]"/>
						</xsl:for-each>
					</xsl:with-param>
				</xsl:call-template>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="*:editor" as="element()+" xml:id="match-editor">
		<xsl:variable name="authors" as="element()" select="misc:author-decoder(.)"/>
		<xsl:if test="$authors/author">
			<xsl:sequence select="misc:authors-to-eterms_creator($authors, 'http://www.loc.gov/loc.terms/relators/EDT')"/>
		</xsl:if>
	</xsl:template>
	<xsl:template match="*:art/*:fm/*:bibl[not(normalize-space(*:supplement/*:title[1]) )]" xml:id="match-art-fm-bibl" as="element()?">
		<xsl:choose>
			<xsl:when test="normalize-space(*:source)">
				<xsl:call-template name="local:make_source-source_from_fm-bibl"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="misc:message">
					<xsl:with-param name="level">WARN</xsl:with-param>
					<xsl:with-param name="message">[bmc_to_pubman.xsl#match-art-fm-bibl] *:art/*:fm/*:bibl without *:source and *:supplement, could not create source:source @ID 
						<xsl:sequence select="ancestor::*:art/*:ui"/>
					</xsl:with-param>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="*:art/*:fm/*:bibl[(normalize-space(*:supplement/*:title[1]) )]" xml:id="match-art-fm-bibl-supplement" as="element()*">
		<xsl:if test="normalize-space(*:source)">
			<xsl:call-template name="misc:make_source-source">
				<xsl:with-param name="att_type" select="local:escidoc_genre-from-bmc_dochead('Supplement')"/>
				<xsl:with-param name="dc:title" as="element()">
					<xsl:apply-templates select="*:supplement/*:title"/>
				</xsl:with-param>
				<xsl:with-param name="eterms:creator" as="element()*">
					<xsl:apply-templates select="*:editor"/>
					<xsl:apply-templates select="*:supplement/*:editor"/>
				</xsl:with-param>
				<xsl:with-param name="eterms:volume" as="element()?">
					<xsl:apply-templates select="*:volume"/>
				</xsl:with-param>
				<xsl:with-param name="eterms:issue" as="element()?">
					<xsl:apply-templates select="*:issue"/>
				</xsl:with-param>
				<xsl:with-param name="eterms:end-page" as="element()*">
					<xsl:apply-templates select="*:lpage"/>
				</xsl:with-param>
				<xsl:with-param name="eterms:sequence-number" as="element()*">
					<xsl:apply-templates select="*:fpage"/>
				</xsl:with-param>
				<xsl:with-param name="eterms:total-number-of-pages" as="element()?">
					<xsl:if test="misc:total-number-of-pages(*:fpage, *:lpage)">
						<xsl:sequence select="misc:create_eterms-total-number-of-pages(misc:total-number-of-pages(*:fpage, *:lpage))"/>
					</xsl:if>
				</xsl:with-param>
				<xsl:with-param name="eterms:publishing-info" as="element()?">
					<xsl:call-template name="local:make_eterms-publishing-info_from_bibl"/>
				</xsl:with-param>
				<xsl:with-param name="dc:identifier" as="element()*">
					<xsl:apply-templates select="*:supplement/*:url|/*:art/*:ji[lower-case(normalize-space(.) ) ne lower-case( normalize-space(current()/*:issn) ) ]"/>
				</xsl:with-param>
			</xsl:call-template>
			<xsl:call-template name="local:make_source-source_from_fm-bibl">
				<xsl:with-param name="make_bibliography" select="false()"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
	<xsl:template name="local:make_source-source_from_fm-bibl" as="element()" xml:id="named-local_make_source-source_from_fm-bibl">
		<xsl:param name="make_bibliography" as="xs:boolean" select="true()" required="no"/>
		<xsl:call-template name="misc:make_source-source">
			<xsl:with-param name="att_type" select="local:escidoc_genre-from-bmc_bibl(.)"/>
			<xsl:with-param name="dc:title" select="misc:create_dc-title(*:source)"/>
			<xsl:with-param name="eterms:creator" as="element()*">
				<xsl:if test="$make_bibliography">
					<xsl:apply-templates select="*:editor"/>
				</xsl:if>
			</xsl:with-param>
			<xsl:with-param name="eterms:volume" as="element()?">
				<xsl:if test="$make_bibliography">
					<xsl:apply-templates select="*:volume"/>
				</xsl:if>
			</xsl:with-param>
			<xsl:with-param name="eterms:issue" as="element()?">
				<xsl:if test="$make_bibliography">
					<xsl:apply-templates select="*:issue"/>
				</xsl:if>
			</xsl:with-param>
			<xsl:with-param name="eterms:end-page" as="element()*">
				<xsl:if test="$make_bibliography">
					<xsl:apply-templates select="*:lpage"/>
				</xsl:if>
			</xsl:with-param>
			<xsl:with-param name="eterms:sequence-number" as="element()*">
				<xsl:if test="$make_bibliography">
					<xsl:apply-templates select="*:fpage"/>
				</xsl:if>
			</xsl:with-param>
			<xsl:with-param name="eterms:total-number-of-pages" as="element()?">
				<xsl:if test="misc:total-number-of-pages(*:fpage, *:lpage)">
					<xsl:sequence select="misc:create_eterms-total-number-of-pages(misc:total-number-of-pages(*:fpage, *:lpage))"/>
				</xsl:if>
			</xsl:with-param>
			<xsl:with-param name="eterms:publishing-info" as="element()?">
				<xsl:call-template name="local:make_eterms-publishing-info_from_bibl"/>
			</xsl:with-param>
			<xsl:with-param name="dc:identifier" as="element()*">
				<xsl:apply-templates select="*:issn|*:isbn"/>
				<xsl:if test="$make_bibliography">
					<xsl:apply-templates select="/*:art/*:ji[lower-case(normalize-space(.) ) ne lower-case( normalize-space(current()/*:issn) ) ]"/>
				</xsl:if>
			</xsl:with-param>
			<xsl:with-param name="source:source" as="element()*">
				<xsl:apply-templates select="*:series"/>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="*:ins" as="element()" xml:id="match-ins">
		<xsl:call-template name="misc:make_organization-organization">
			<xsl:with-param name="dc:title" select="misc:create_dc-title(normalize-space(string-join(*:p/descendant-or-self::text(), '') ) )"/>
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="*:conference" xml:id="match-conference" as="element()">
		<xsl:variable name="dates" as="xs:string+" select="tools:start-date-and-end-date(*:date-range)"/>
		<xsl:call-template name="misc:make_event-event">
			<xsl:with-param name="dc:title" as="element()">
				<xsl:apply-templates select="*:title"/>
			</xsl:with-param>
			<xsl:with-param name="eterms:start-date" as="element(eterms:start-date)?">
				<xsl:if test="normalize-space($dates[1])">
					<xsl:sequence select="misc:create_eterms-start-date($dates[1])"/>
				</xsl:if>
			</xsl:with-param>
			<xsl:with-param name="eterms:end-date" as="element(eterms:end-date)?">
				<xsl:choose>
					<xsl:when test="normalize-space($dates[2])">
						<xsl:sequence select="misc:create_eterms-end-date($dates[2])"/>
					</xsl:when>
					<xsl:when test="normalize-space($dates[1])">
						<xsl:sequence select="misc:create_eterms-end-date($dates[1])"/>
					</xsl:when>
				</xsl:choose>
			</xsl:with-param>
			<xsl:with-param name="eterms:place" as="element()?">
				<xsl:apply-templates select="*:location"/>
			</xsl:with-param>
			<xsl:with-param name="eterms:invitation-status" as="element()?">
				<xsl:if test="local:is-invited(ancestor::*:art)">
					<xsl:sequence select="misc:create_eterms-invitation-status('invited')"/>
				</xsl:if>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="*:series" as="element()" xml:id="match-series">
		<xsl:call-template name="misc:make_source-source">
			<xsl:with-param name="att_type" select="local:escidoc_genre-from-bmc_dochead('Series')"/>
			<xsl:with-param name="dc:title" as="element()">
				<xsl:apply-templates select="*:title"/>
			</xsl:with-param>
			<xsl:with-param name="eterms:creator" as="element()?">
				<xsl:apply-templates select="*:editor"/>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="*:abs|*:shortabs" as="element()?" xml:id="match-abs">
		<xsl:variable name="abstract-text" as="xs:string?" select="local:string(./*, true() )"/>
		<xsl:if test="normalize-space(string-join($abstract-text, '') )">
			<xsl:variable name="language" as="xs:string?" select="if (normalize-space(@lang)) then misc:iso-639-1_to_iso-639-3(@lang) else ()"/>
			<xsl:sequence select="misc:create_dcterms-abstract($abstract-text, $language)"/>
		</xsl:if>
	</xsl:template>
	<xsl:template match="*:kwd" as="element()?" xml:id="match-kwd">
		<xsl:variable name="textknoten" as="xs:string*" select="local:string(., false())"/>
		<xsl:if test="$textknoten">
			<xsl:variable name="language" as="xs:string?" select="if (normalize-space(parent::*:kwdg/@lang)) then misc:iso-639-1_to_iso-639-3(parent::*:kwdg/@lang) else ()"/>
			<xsl:sequence select="misc:create_dcterms-subject($textknoten, $language)"/>
		</xsl:if>
	</xsl:template>
	<xsl:template match="*:classification[local:classification-is-dcterms_subject(.)]" as="element()?" xml:id="match-classification-is-dcterms_subject">
		<xsl:sequence select="misc:create_dcterms-subject(normalize-space(.))"/>
	</xsl:template>
	<xsl:template match="*:classification[not(local:classification-is-dcterms_subject(.))]" as="element()?" xml:id="match-classification-is-not-dcterms_subject"/>
	<xsl:template match="*:bibl/*:title|*:conference/*:title|*:supplement/*:title|*:series/*:title" as="element()" xml:id="match-bibl-title">
		<xsl:sequence select="misc:create_dc-title(local:string(*:p, false(), true()))"/>
	</xsl:template>
	<xsl:template match="*:bibl/*:trans-title-grp/*:title" as="element()" xml:id="match-bibl-trans-title-grp-title">
		<xsl:sequence select="misc:create_dcterms-alternative(local:string(node(), false()), 'eterms:OTHER')"/>
	</xsl:template>
	<xsl:template match="*:cnm" as="element()" xml:id="match-cnm">
		<xsl:call-template name="misc:make_organization-organization">
			<xsl:with-param name="dc:title" select="misc:create_dc-title(string-join(descendant-or-self::text(), '') )"/>
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="*:fpage" as="element()*" xml:id="match-fpage">
		<xsl:variable name="volume" as="xs:string?" select="(../*:volume/normalize-space(.))[normalize-space(.)][1]"/>
		<xsl:variable name="issue" as="xs:string?" select="(../*:issue/normalize-space(.))[normalize-space(.)][1]"/>
		<xsl:variable name="startpage" as="xs:string" select="normalize-space(.)"/>
		<xsl:variable name="write-supplement" as="xs:boolean" select="boolean(../*:supplement[normalize-space(.)])"/>
		<xsl:sequence select="misc:eterms_sequence-number($volume, $issue, $startpage, $write-supplement)"/>
	</xsl:template>
	<xsl:template match="*:isbn" as="element()" xml:id="match-isbn">
		<xsl:sequence select="misc:create_dc-identifier_from_isbn(.)"/>
	</xsl:template>
	<xsl:template match="*:issn" as="element()" xml:id="match-issn">
		<xsl:sequence select="misc:create_dc-identifier_from_issn(.)"/>
	</xsl:template>
	<xsl:template match="*:bibl/*:url|*:bibl/*:supplement/*:url" as="element()" xml:id="match-bibl-url">
		<xsl:sequence select="misc:create_dc-identifier_from_uri(.)"/>
	</xsl:template>
	<xsl:template match="*:issue" as="element()" xml:id="match-issue">
		<xsl:sequence select="misc:create_eterms-issue(.)"/>
	</xsl:template>
	<xsl:template match="*:lpage" as="element()" xml:id="match-lpage">
		<xsl:sequence select="misc:create_eterms-end-page(.)"/>
	</xsl:template>
	<xsl:template match="*:pubdate" as="element(eterms:published-online)?" xml:id="match-pubdate">
		<xsl:if test="tools:is-W3CDTF(.)">
			<xsl:sequence select="misc:create_eterms-published-online(.)"/>
		</xsl:if>
	</xsl:template>
	<xsl:template match="*:ui" as="element()+" xml:id="match-ui">
		<xsl:sequence select="misc:create_dc-identifier_from_bmc-id(.)"/>
		<xsl:variable name="temp" as="xs:string" select="normalize-space(local:ui-to-bmc-uri(., 'html'))"/>
		<xsl:if test="$temp">
			<xsl:sequence select="misc:create_dc-identifier_from_uri($temp)"/>
		</xsl:if>
	</xsl:template>
	<xsl:template match="*:volume" as="element()" xml:id="match-volume">
		<xsl:sequence select="misc:create_eterms-volume(.)"/>
	</xsl:template>
	<xsl:template match="*:location" as="element()" xml:id="match-location">
		<xsl:sequence select="misc:create_eterms-place(.)"/>
	</xsl:template>
	<xsl:template match="*:acc" as="element()?" xml:id="match-acc">
		<xsl:variable name="temp" as="xs:string?" select="local:date-string(*:date)"/>
		<xsl:if test="$temp">
			<xsl:sequence select="misc:create_dcterms-dateAccepted($temp)"/>
		</xsl:if>
	</xsl:template>
	<xsl:template match="*:pub" as="element()?" xml:id="match-acc">
		<xsl:variable name="temp" as="xs:string?" select="local:date-string(*:date)"/>
		<xsl:if test="$temp">
			<xsl:sequence select="misc:create_eterms-published-online($temp)"/>
		</xsl:if>
	</xsl:template>
	<xsl:template match="*:rec" as="element()?" xml:id="match-rec">
		<xsl:variable name="temp" as="xs:string?" select="local:date-string(*:date)"/>
		<xsl:if test="$temp">
			<xsl:sequence select="misc:create_dcterms-dateSubmitted($temp)"/>
		</xsl:if>
	</xsl:template>
	<xsl:template match="*:shorttitle" as="element()" xml:id="match-shorttitle">
		<xsl:sequence select="misc:create_dcterms-alternative(local:string(./node(), false()), 'eterms:ABBREVIATION')"/>
	</xsl:template>
	<xsl:template match="*:pubid" as="element()?" xml:id="match-pubid">
		<xsl:if test="normalize-space(.)">
			<xsl:choose>
				<xsl:when test="@idtype eq 'arxiv'">
					<xsl:sequence select="misc:create_dc-identifier_from_arxiv-id(.)"/>
				</xsl:when>
				<xsl:when test="@idtype eq 'doi'">
					<xsl:sequence select="misc:create_dc-identifier_from_doi(.)"/>
				</xsl:when>
				<xsl:when test="@idtype eq 'pii'">
					<xsl:sequence select="misc:create_dc-identifier_from_pii(.)"/>
				</xsl:when>
				<xsl:when test="@idtype eq 'pmcid'">
					<xsl:sequence select="misc:create_dc-identifier_from_pmc(.)"/>
				</xsl:when>
				<xsl:when test="@idtype eq 'pmpid'">
					<xsl:sequence select="misc:create_dc-identifier_from_pmid(.)"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:variable name="prefix" as="xs:string?">
						<xsl:if test="normalize-space(@idtype)">
							<xsl:sequence select="concat('(', normalize-space(@idtype), ') ')"/>
						</xsl:if>
					</xsl:variable>
					<xsl:sequence select="misc:create_dc-identifier_from_other(concat($prefix, normalize-space(.)))"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>
	</xsl:template>
	<xsl:template match="*:edition" as="element()" xml:id="match-edition">
		<xsl:sequence select="misc:create_eterms-edition(.)"/>
	</xsl:template>
	<xsl:template match="*:ji" as="element()?" xml:id="match-ji">
		<xsl:variable name="clean-ji" as="xs:string" select="upper-case(normalize-space(.) )"/>
		<xsl:choose>
			<xsl:when test="not($clean-ji)"/>
			<xsl:when test="matches($clean-ji, '^[0-9]{4}-[0-9]{3}[0-9X]$')">
				<xsl:sequence select="misc:create_dc-identifier_from_issn($clean-ji)"/>
			</xsl:when>
			<xsl:when test="matches($clean-ji, '^(RRJ|ARJ|BCJ|CCJ|CVJ|GBJ)$')"/>
			<xsl:otherwise>
				<xsl:call-template name="misc:message">
					<xsl:with-param name="level">INFO</xsl:with-param>
					<xsl:with-param name="show-context" select="false()"/>
					<xsl:with-param name="message">[bmc_to_pubman.xsl#match-ji] did not recognize type of *:ji (Journal-ID) »
						<xsl:sequence select="."/>«
					</xsl:with-param>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="*:fm/*:cpyrt/*:year" as="element()" xml:id="match-fm-cpyrt-year">
		<xsl:variable name="temp" as="xs:string" select="normalize-space(.)"/>
		<xsl:if test="$temp">
			<xsl:sequence select="misc:create_dcterms-dateCopyrighted($temp)"/>
		</xsl:if>
	</xsl:template>
	<xsl:function name="local:string" as="xs:string?">
		<xsl:param name="nodes" as="node()*"/>
		<xsl:param name="with-linebreaks" as="xs:boolean"/>
		<xsl:sequence select="local:string($nodes, $with-linebreaks, false())"/>
	</xsl:function>
	<xsl:function name="local:string" as="xs:string?">
		<xsl:param name="nodes" as="node()*"/>
		<xsl:param name="with-linebreaks" as="xs:boolean"/>
		<xsl:param name="encode-sup-sub" as="xs:boolean"/>
		<xsl:variable name="strings" as="element()">
			<misc:root misc:solved="name">
				<xsl:apply-templates select="$nodes" mode="TextOnly">
					<xsl:with-param name="tunneld_encode-sup-sub" select="$encode-sup-sub" tunnel="yes"/>
				</xsl:apply-templates>
			</misc:root>
		</xsl:variable>
		<xsl:variable name="temp-result" as="xs:string*">
			<xsl:sequence select="string-join( for $i in tokenize($strings, '\n')[normalize-space(.)] return normalize-space($i), '&#xA;' )"/>
		</xsl:variable>
		<xsl:choose>
			<xsl:when test="not(normalize-space(string-join($temp-result, '')) )"/>
			<xsl:when test="$with-linebreaks">
				<xsl:sequence select="string-join($temp-result, '')"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:sequence select="normalize-space(string-join($temp-result, ''))"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:function>
	<xsl:template match="*:break|*:math|*:graphic|*:table" as="text()" mode="TextOnly" xml:id="textonly-match-break">
		<xsl:text></xsl:text>
	</xsl:template>
	<xsl:template match="*:st|*:p|*:sec|*:tbl|*:title|*:caption|*:tblbdy|*:tblfn|*:text|*:suppl|*:fn|*:bibl|*:html|*:boxed-text|*:scheme|*:kwd" as="text()*" mode="TextOnly" xml:id="textonly-match-st">
		<xsl:apply-templates mode="#current"/>
		<xsl:text>&#xa;</xsl:text>
	</xsl:template>
	<xsl:template match="*:it|*:b|*:smcaps|*:ul|*:url|*:email|*:a|*:inline-formula|*:aff|*:abbr| *:abbrgrp|*:tblr|*:figr|*:formr|*:supplr|*:monospace|*:xrefart|*:xfigr|*:xtblr|*:xsupplr|*:ext-link|*:aur|*:schemer|*:display-formula|*:endnoter| *:fnm|*:mi|*:mnm|*:snm|*:suf " as="text()*" mode="TextOnly" xml:id="textonly-match-it">
		<xsl:apply-templates mode="#current"/>
	</xsl:template>
	<xsl:template match="*:sub|*:sup" as="text()*" mode="TextOnly" xml:id="textonly-match-sup">
		<xsl:param name="tunneld_encode-sup-sub" as="xs:boolean" tunnel="yes" select="false()" required="no"/>
		<xsl:choose>
			<xsl:when test="$tunneld_encode-sup-sub">
				<xsl:value-of select="concat('&lt;', local-name(), '&gt;')"/>
				<xsl:apply-templates mode="#current"/>
				<xsl:value-of select="concat('&lt;/', local-name(), '&gt;')"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates mode="#current"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="*:st/*:p[ancestor::*:abs][normalize-space(.) eq 'Abstract']" as="item()*" mode="TextOnly" xml:id="textonly-match-st-abs"/>
	<xsl:template match="text()" as="text()" mode="TextOnly" xml:id="textonly-match-text">
		<xsl:value-of select="concat( if (matches(., '^\s') ) then ' ' else '', normalize-space(.), if (normalize-space(.) and matches(., '\s$') ) then ' ' else () )"/>
	</xsl:template>
	<xsl:template match="*" mode="#all" as="node()?" xml:id="match-all">
		<xsl:call-template name="misc:message">
			<xsl:with-param name="level">INFO</xsl:with-param>
			<xsl:with-param name="message">[bmc_to_pubman.xsl#match-all]
				<xsl:if test="function-available('saxon:current-mode-name')">[Mode: 
					<xsl:sequence select="saxon:current-mode-name()" use-when="function-available('saxon:current-mode-name')"/>]
				</xsl:if>unexpected element, no matching template found
			</xsl:with-param>
			<xsl:with-param name="show-context" select="true()"/>
		</xsl:call-template>
	</xsl:template>
	<xsl:template name="local:make_eterms-publishing-info_from_bibl" as="element()?" xml:id="named-local_make_eterms-publishing-info_from_bibl">
		<xsl:if test="(*:publisher|*:edition)[normalize-space(.)]">
			<xsl:variable name="publisher-place" as="element()+" select="local:split-publisher-in-publisher-place(*:publisher)"/>
			<xsl:call-template name="misc:make_eterms-publishing-info">
				<xsl:with-param name="dc:publisher" select="$publisher-place/self::dc:publisher" as="element()+"/>
				<xsl:with-param name="eterms:place" select="$publisher-place/self::eterms:place" as="element()?"/>
				<xsl:with-param name="eterms:edition" as="element()?">
					<xsl:apply-templates select="*:edition"/>
				</xsl:with-param>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
	<xsl:template name="local:make_ToC" as="element()" xml:id="named-local-make_ToC">
		<xsl:variable name="temp" as="xs:string*">
			<xsl:for-each select="/*:art/*:bdy//*:sec/*:st">
				<xsl:variable name="temp2" as="xs:string*" select="local:string(., false())"/>
				<xsl:sequence select="concat( string-join(for $i in 2 to count(ancestor::*:sec) return '  ', ''), normalize-space(string-join($temp2, '') ), '&#xA;' )"/>
			</xsl:for-each>
		</xsl:variable>
		<xsl:sequence select="misc:create_dcterms-tableOfContents(string-join($temp, '') )"/>
	</xsl:template>
	<xsl:function name="local:escidoc_genre-from-bmc_dochead" as="xs:string" xml:id="escidoc_genre-from-bmc_dochead">
		<xsl:param name="dochead" as="xs:string?"/>
		<xsl:variable name="temp-result" as="xs:string?" select="local:bmc-dochead-to-pubman-genre($dochead)"/>
		<xsl:choose>
			<xsl:when test="$temp-result">
				<xsl:sequence select="$temp-result"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:variable name="default-result" as="xs:string?" select="local:bmc-dochead-to-pubman-genre('other')"/>
				<xsl:sequence select="$default-result"/>
				<xsl:call-template name="misc:message">
					<xsl:with-param name="level">INFO</xsl:with-param>
					<xsl:with-param name="show-context" select="false()"/>
					<xsl:with-param name="message">[bmc_to_pubman.xsl#local:escidoc_genre-from-bmc_dochead] no mapping for »
						<xsl:sequence select="$dochead"/>«, used »
						<xsl:value-of select="$default-result"/>«
					</xsl:with-param>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:function>
	<xsl:function name="local:escidoc_genre-from-bmc_bibl" as="xs:string" xml:id="func_escidoc_genre-from-bmc_bibl">
		<xsl:param name="bibl" as="element()?"/>
		<xsl:variable name="section" as="xs:boolean" select="boolean(normalize-space($bibl/*:section))"/>
		<xsl:variable name="issn" as="xs:boolean" select="boolean(normalize-space($bibl/*:issn))"/>
		<xsl:variable name="publisher" as="xs:boolean" select="boolean(normalize-space($bibl/*:publisher))"/>
		<xsl:variable name="editor" as="xs:boolean" select="boolean(normalize-space($bibl/*:editor))"/>
		<xsl:variable name="edition" as="xs:boolean" select="boolean(normalize-space($bibl/*:edition))"/>
		<xsl:variable name="series" as="xs:boolean" select="boolean(normalize-space($bibl/*:series))"/>
		<xsl:variable name="isbn" as="xs:boolean" select="boolean(normalize-space($bibl/*:isbn))"/>
		<xsl:choose>
			<xsl:when test="$issn">
				<xsl:sequence select=" 'http://purl.org/escidoc/metadata/ves/publication-types/journal' "/>
			</xsl:when>
			<xsl:when test="$isbn">
				<xsl:sequence select=" 'http://purl.org/eprint/type/Book' "/>
			</xsl:when>
			<xsl:when test="$edition or $series">
				<xsl:sequence select=" 'http://purl.org/eprint/type/Book' "/>
			</xsl:when>
			<xsl:when test="$section">
				<xsl:sequence select=" 'http://purl.org/escidoc/metadata/ves/publication-types/journal' "/>
			</xsl:when>
			<xsl:when test="$publisher or $editor">
				<xsl:sequence select=" 'http://purl.org/eprint/type/Book' "/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:variable name="default-result" as="xs:string?" select="local:bmc-dochead-to-pubman-genre('other')"/>
				<xsl:sequence select="$default-result"/>
				<xsl:call-template name="misc:message">
					<xsl:with-param name="level">INFO</xsl:with-param>
					<xsl:with-param name="show-context" select="false()"/>
					<xsl:with-param name="message">[bmc_to_pubman.xsl#local:escidoc_genre-from-bmc_bibl()] no matching rule, used »
						<xsl:value-of select="$default-result"/>«
					</xsl:with-param>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:function>
	<xsl:function name="local:bmc-dochead-to-pubman-genre" as="xs:string?">
		<xsl:param name="bmc-genre" as="xs:string?"/>
		<xsl:sequence select="key('genres_bmc_to_pubman', lower-case(normalize-space($bmc-genre) ), $local:mapping_bmc_genres )/misc:target/text()"/>
	</xsl:function>
	<xsl:function name="local:date-string" as="xs:string?">
		<xsl:param name="date" as="element()?"/>
		<xsl:variable name="year" as="xs:integer" select="if ($date/*:year castable as xs:integer) then xs:integer($date/*:year) else 0"/>
		<xsl:variable name="month" as="xs:integer" select="if ($date/*:month castable as xs:integer) then xs:integer($date/*:month) else 0"/>
		<xsl:variable name="day" as="xs:integer" select="if ($date/*:day castable as xs:integer) then xs:integer($date/*:day) else 0"/>
		<xsl:variable name="temp" as="xs:string" select="string-join( ( format-number($year, '0000'), format-number($month, '00'), format-number($day, '00') ), '-')"/>
		<xsl:choose>
			<xsl:when test="$temp castable as xs:date">
				<xsl:sequence select="$temp"/>
			</xsl:when>
			<xsl:when test="substring($temp, 1,7) castable as xs:gYearMonth and $day eq 0">
				<xsl:sequence select="substring($temp, 1,7)"/>
			</xsl:when>
			<xsl:when test="substring($temp, 1,4) castable as xs:gYear and $month eq 0 and $day eq 0">
				<xsl:sequence select="substring($temp, 1,4)"/>
			</xsl:when>
		</xsl:choose>
	</xsl:function>
	<xsl:function name="local:ui-to-bmc-uri" as="xs:anyURI">
		<xsl:param name="ui" as="xs:string"/>
		<xsl:param name="type" as="xs:string"/>
		<xsl:choose>
			<xsl:when test="$type eq 'xml'">
				<xsl:sequence select="xs:anyURI(concat($local:default-bmc-article-ui-href-prefix-xml, $ui, $local:default-bmc-article-ui-href-suffix-xml) )"/>
			</xsl:when>
			<xsl:when test="$type eq 'pdf'">
				<xsl:sequence select="xs:anyURI(concat($local:default-bmc-article-ui-href-prefix-pdf, $ui, $local:default-bmc-article-ui-href-suffix-pdf) )"/>
			</xsl:when>
			<xsl:when test="$type eq 'html'">
				<xsl:choose>
					<xsl:when test="matches($ui, '^[0-9]{4}-[0-9X]{4}-S?[0-9]+-S?[0-9]+')">
						<xsl:variable name="parts" as="xs:string*" select="tokenize($ui, '-')"/>
						<xsl:sequence select="xs:anyURI(concat($local:default-bmc-article-ui-href-prefix-html, $parts[1], '-', $parts[2], '/', string-join($parts[position() ge 3], '/') ) )"/>
					</xsl:when>
					<xsl:when test="matches($ui, '^gb-[0-9]+-[0-9]+-[0-9]+-r?[0-9]+$')">
						<xsl:variable name="parts" as="xs:string*" select="tokenize(substring-after($ui, 'gb-'), '-')"/>
						<xsl:sequence select="xs:anyURI(concat('http://genomebiology.com/', string-join($parts, '/') ) )"/>
					</xsl:when>
					<xsl:when test="matches($ui, '^(ar|bcr|cc)[0-9]+$')">
						<xsl:sequence select="xs:anyURI('')"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:sequence select="xs:anyURI('')"/>
						<xsl:call-template name="misc:message">
							<xsl:with-param name="level">WARN</xsl:with-param>
							<xsl:with-param name="show-context" select="false()"/>
							<xsl:with-param name="message">[bmc_to_pubman.xsl#local:ui-to-bmc-uri()] can't construct BMC-URL from UI »
								<xsl:value-of select="$ui"/>«
							</xsl:with-param>
						</xsl:call-template>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:otherwise>
				<xsl:sequence select="xs:anyURI('')"/>
				<xsl:call-template name="misc:message">
					<xsl:with-param name="level">ERROR</xsl:with-param>
					<xsl:with-param name="show-context" select="false()"/>
					<xsl:with-param name="message">[bmc_to_pubman.xsl#local:ui-to-bmc-uri()] wrong type »
						<xsl:value-of select="$type"/>« required, type should be "html", "xml" or "pdf"
					</xsl:with-param>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:function>
	<xsl:function name="local:classification-is-dcterms_subject" as="xs:boolean">
		<xsl:param name="classification" as="element()"/>
		<xsl:sequence select="boolean($classification[normalize-space(.)][normalize-space(@type) eq 'BMC'][normalize-space(@subtype) eq 'man_spc_id'][self::*:classification])"/>
	</xsl:function>
	<xsl:function name="local:prop-content-category" as="element()">
		<xsl:param name="art-node" as="element()"/>
		<xsl:variable name="content-category" as="xs:string">
			<xsl:choose>
				<xsl:when test="normalize-space($art-node/*:fm[1]/*:history[1]/*:pub[1])">http://purl.org/escidoc/metadata/ves/content-categories/publisher-version</xsl:when>
				<xsl:when test="normalize-space($art-node/*:fm[1]/*:history[1]/*:acc[1])">http://purl.org/escidoc/metadata/ves/content-categories/pre-print</xsl:when>
				<xsl:when test="normalize-space($art-node/*:fm[1]/*:history[1]/*:revrec[1])">http://purl.org/escidoc/metadata/ves/content-categories/pre-print</xsl:when>
				<xsl:when test="normalize-space($art-node/*:fm[1]/*:history[1]/*:revreq[1])">http://purl.org/escidoc/metadata/ves/content-categories/pre-print</xsl:when>
				<xsl:when test="normalize-space($art-node/*:fm[1]/*:history[1]/*:rec[1])">http://purl.org/escidoc/metadata/ves/content-categories/pre-print</xsl:when>
				<xsl:otherwise>http://purl.org/escidoc/metadata/ves/content-categories/any-fulltext</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:sequence select="misc:create_prop-content-category($content-category)"/>
	</xsl:function>
	<xsl:function name="local:split-publisher-in-publisher-place" as="element()+">
		<xsl:param name="bmc-publisher" as="xs:string"/>
		<xsl:variable name="possible-place" as="xs:string" select="normalize-space(substring-before($bmc-publisher, ':') )"/>
		<xsl:choose>
			<xsl:when test="$possible-place">
				<xsl:sequence select="misc:create_eterms-place($possible-place)"/>
				<xsl:sequence select="misc:create_dc-publisher(normalize-space(substring-after($bmc-publisher, ':') ) )"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:sequence select="misc:create_dc-publisher(normalize-space($bmc-publisher) )"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:function>
	<xsl:function name="local:is-invited" as="xs:boolean">
		<xsl:param name="art" as="element()"/>
		<xsl:sequence select="boolean(key('genres_bmc_to_pubman', lower-case(normalize-space($art/*:fm/*:dochead) ), $local:mapping_bmc_genres)/misc:source/@misc:invited)"/>
	</xsl:function>
</xsl:stylesheet>