<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xlink="http://www.w3.org/1999/xlink"
                xmlns:cit="http://www.escidoc.de/citationstyle"
                xmlns:fn="http://www.w3.org/2005/xpath-functions"
                xmlns:jfunc="java:de.mpg.escidoc.services.citationmanager.utils.XsltHelper"
                xmlns:func="http://www.escidoc.de/citationstyle/functions"
                xmlns:functx="http://www.functx.com"
                xmlns:ei="http://www.escidoc.de/schemas/item/0.10"
                xmlns:mdr="http://www.escidoc.de/schemas/metadatarecords/0.5"
                xmlns:mdp="http://escidoc.mpg.de/metadataprofile/schema/0.1/"
                xmlns:pub="http://purl.org/escidoc/metadata/profiles/0.1/publication"
                xmlns:e="http://purl.org/escidoc/metadata/terms/0.1/"
                xmlns:prop="http://escidoc.de/core/01/properties/"
                xmlns:escidocComponents="http://www.escidoc.de/schemas/components/0.9"
                xmlns:source="http://purl.org/escidoc/metadata/profiles/0.1/source"
                xmlns:eterms="http://purl.org/escidoc/metadata/terms/0.1/"
                xmlns:event="http://purl.org/escidoc/metadata/profiles/0.1/event"
                xmlns:organization="http://purl.org/escidoc/metadata/profiles/0.1/organization"
                xmlns:person="http://purl.org/escidoc/metadata/profiles/0.1/person"
                xmlns:legalCase="http://purl.org/escidoc/metadata/profiles/0.1/legal-case"
                xmlns:dc="http://purl.org/dc/elements/1.1/"
                xmlns:dcterms="http://purl.org/dc/terms/"
                xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                version="2.0">
    <xsl:output method="xml" encoding="UTF-8" indent="yes"
                cdata-section-elements="dcterms:bibliographicCitation dcterms:abstract"/>
    <xsl:param name="pubman_instance"/>
    <xsl:template match="node() | @*">
        <xsl:copy>
            <xsl:apply-templates select="@* | node ()"/>
        </xsl:copy>
    </xsl:template>
    <xsl:template match="prop:content-model-specific">
        <xsl:element name="{name(.)}"><!--XXX - Template copy Of -XXX--><xsl:copy-of select="child::node()"/>
            <xsl:element name="dcterms:bibliographicCitation">
                <xsl:variable name="citation">
                    <xsl:for-each select="../../mdr:md-records/mdr:md-record"><!--### Global Default Variables ###-->
	<xsl:variable name="l_journal">
                            <xsl:value-of select="func:escapeMarkupTags('http://purl.org/escidoc/metadata/ves/publication-types/journal')"/>
                        </xsl:variable>
                        <xsl:variable name="l_article">
                            <xsl:value-of select="func:escapeMarkupTags('http://purl.org/escidoc/metadata/ves/publication-types/article')"/>
                        </xsl:variable>
                        <xsl:variable name="l_issue">
                            <xsl:value-of select="func:escapeMarkupTags('http://purl.org/escidoc/metadata/ves/publication-types/issue')"/>
                        </xsl:variable>
                        <xsl:variable name="l_book">
                            <xsl:value-of select="func:escapeMarkupTags('http://purl.org/eprint/type/Book')"/>
                        </xsl:variable>
                        <xsl:variable name="l_book-item">
                            <xsl:value-of select="func:escapeMarkupTags('http://purl.org/eprint/type/BookItem')"/>
                        </xsl:variable>
                        <xsl:variable name="l_proceedings">
                            <xsl:value-of select="func:escapeMarkupTags('http://purl.org/escidoc/metadata/ves/publication-types/proceedings')"/>
                        </xsl:variable>
                        <xsl:variable name="l_conference-paper">
                            <xsl:value-of select="func:escapeMarkupTags('http://purl.org/eprint/type/ConferencePaper')"/>
                        </xsl:variable>
                        <xsl:variable name="l_meeting-abstract">
                            <xsl:value-of select="func:escapeMarkupTags('http://purl.org/escidoc/metadata/ves/publication-types/meeting-abstract')"/>
                        </xsl:variable>
                        <xsl:variable name="l_conference-report">
                            <xsl:value-of select="func:escapeMarkupTags('http://purl.org/escidoc/metadata/ves/publication-types/conference-report')"/>
                        </xsl:variable>
                        <xsl:variable name="l_poster">
                            <xsl:value-of select="func:escapeMarkupTags('http://purl.org/eprint/type/ConferencePoster')"/>
                        </xsl:variable>
                        <xsl:variable name="l_report">
                            <xsl:value-of select="func:escapeMarkupTags('http://purl.org/eprint/type/Report')"/>
                        </xsl:variable>
                        <xsl:variable name="l_paper">
                            <xsl:value-of select="func:escapeMarkupTags('http://purl.org/escidoc/metadata/ves/publication-types/paper')"/>
                        </xsl:variable>
                        <xsl:variable name="l_talk-at-event">
                            <xsl:value-of select="func:escapeMarkupTags('http://purl.org/escidoc/metadata/ves/publication-types/talk-at-event')"/>
                        </xsl:variable>
                        <xsl:variable name="l_courseware-lecture">
                            <xsl:value-of select="func:escapeMarkupTags('http://purl.org/escidoc/metadata/ves/publication-types/courseware-lecture')"/>
                        </xsl:variable>
                        <xsl:variable name="l_thesis">
                            <xsl:value-of select="func:escapeMarkupTags('http://purl.org/eprint/type/Thesis')"/>
                        </xsl:variable>
                        <xsl:variable name="l_series">
                            <xsl:value-of select="func:escapeMarkupTags('http://purl.org/escidoc/metadata/ves/publication-types/series')"/>
                        </xsl:variable>
                        <xsl:variable name="l_manuscript">
                            <xsl:value-of select="func:escapeMarkupTags('http://purl.org/escidoc/metadata/ves/publication-types/manuscript')"/>
                        </xsl:variable>
                        <xsl:variable name="l_other">
                            <xsl:value-of select="func:escapeMarkupTags('http://purl.org/escidoc/metadata/ves/publication-types/other')"/>
                        </xsl:variable>
                        <xsl:variable name="l_monograph">
                            <xsl:value-of select="func:escapeMarkupTags('http://purl.org/escidoc/metadata/ves/publication-types/monograph')"/>
                        </xsl:variable>
                        <xsl:variable name="l_contr-to-collect-ed">
                            <xsl:value-of select="func:escapeMarkupTags('http://purl.org/escidoc/metadata/ves/publication-types/contribution-to-collected-edition')"/>
                        </xsl:variable>
                        <xsl:variable name="l_case-note">
                            <xsl:value-of select="func:escapeMarkupTags('http://purl.org/escidoc/metadata/ves/publication-types/case-note')"/>
                        </xsl:variable>
                        <xsl:variable name="l_opinion">
                            <xsl:value-of select="func:escapeMarkupTags('http://purl.org/escidoc/metadata/ves/publication-types/opinion')"/>
                        </xsl:variable>
                        <xsl:variable name="l_case-study">
                            <xsl:value-of select="func:escapeMarkupTags('http://purl.org/escidoc/metadata/ves/publication-types/case-study')"/>
                        </xsl:variable>
                        <xsl:variable name="l_book-review">
                            <xsl:value-of select="func:escapeMarkupTags('http://purl.org/escidoc/metadata/ves/publication-types/book-review')"/>
                        </xsl:variable>
                        <xsl:variable name="l_contr-to-commentary">
                            <xsl:value-of select="func:escapeMarkupTags('http://purl.org/escidoc/metadata/ves/publication-types/contribution-to-commentary')"/>
                        </xsl:variable>
                        <xsl:variable name="l_contr-to-festschrift">
                            <xsl:value-of select="func:escapeMarkupTags('http://purl.org/escidoc/metadata/ves/publication-types/contribution-to-festschrift')"/>
                        </xsl:variable>
                        <xsl:variable name="l_contr-to-handbook">
                            <xsl:value-of select="func:escapeMarkupTags('http://purl.org/escidoc/metadata/ves/publication-types/contribution-to-handbook')"/>
                        </xsl:variable>
                        <xsl:variable name="l_contr-to-encyclopedia">
                            <xsl:value-of select="func:escapeMarkupTags('http://purl.org/escidoc/metadata/ves/publication-types/contribution-to-encyclopedia')"/>
                        </xsl:variable>
                        <xsl:variable name="l_newspaper-article">
                            <xsl:value-of select="func:escapeMarkupTags('http://purl.org/escidoc/metadata/ves/publication-types/newspaper-article')"/>
                        </xsl:variable>
                        <xsl:variable name="l_collected-edition">
                            <xsl:value-of select="func:escapeMarkupTags('http://purl.org/escidoc/metadata/ves/publication-types/collected-edition')"/>
                        </xsl:variable>
                        <xsl:variable name="l_commentary">
                            <xsl:value-of select="func:escapeMarkupTags('http://purl.org/escidoc/metadata/ves/publication-types/commentary')"/>
                        </xsl:variable>
                        <xsl:variable name="l_festschrift">
                            <xsl:value-of select="func:escapeMarkupTags('http://purl.org/escidoc/metadata/ves/publication-types/festschrift')"/>
                        </xsl:variable>
                        <xsl:variable name="l_handbook">
                            <xsl:value-of select="func:escapeMarkupTags('http://purl.org/escidoc/metadata/ves/publication-types/handbook')"/>
                        </xsl:variable>
                        <xsl:variable name="l_editorial">
                            <xsl:value-of select="func:escapeMarkupTags('http://purl.org/escidoc/metadata/ves/publication-types/editorial')"/>
                        </xsl:variable>
                        <xsl:variable name="l_multi-volume">
                            <xsl:value-of select="func:escapeMarkupTags('http://purl.org/escidoc/metadata/ves/publication-types/multi-volume')"/>
                        </xsl:variable>
                        <xsl:variable name="l_author">
                            <xsl:value-of select="func:escapeMarkupTags('http://www.loc.gov/loc.terms/relators/AUT')"/>
                        </xsl:variable>
                        <xsl:variable name="l_artist">
                            <xsl:value-of select="func:escapeMarkupTags('http://www.loc.gov/loc.terms/relators/ART')"/>
                        </xsl:variable>
                        <xsl:variable name="l_editor">
                            <xsl:value-of select="func:escapeMarkupTags('http://www.loc.gov/loc.terms/relators/EDT')"/>
                        </xsl:variable>
                        <xsl:variable name="l_painter">
                            <xsl:value-of select="func:escapeMarkupTags('http://purl.org/escidoc/metadata/ves/creator-roles/painter')"/>
                        </xsl:variable>
                        <xsl:variable name="l_photographer">
                            <xsl:value-of select="func:escapeMarkupTags('http://www.loc.gov/loc.terms/relators/PHT')"/>
                        </xsl:variable>
                        <xsl:variable name="l_illustrator">
                            <xsl:value-of select="func:escapeMarkupTags('http://www.loc.gov/loc.terms/relators/ILL')"/>
                        </xsl:variable>
                        <xsl:variable name="l_commentator">
                            <xsl:value-of select="func:escapeMarkupTags('http://www.loc.gov/loc.terms/relators/CMM')"/>
                        </xsl:variable>
                        <xsl:variable name="l_transcriber">
                            <xsl:value-of select="func:escapeMarkupTags('http://www.loc.gov/loc.terms/relators/TRC')"/>
                        </xsl:variable>
                        <xsl:variable name="l_translator">
                            <xsl:value-of select="func:escapeMarkupTags('http://www.loc.gov/loc.terms/relators/TRL')"/>
                        </xsl:variable>
                        <xsl:variable name="l_advisor">
                            <xsl:value-of select="func:escapeMarkupTags('http://www.loc.gov/loc.terms/relators/SAD')"/>
                        </xsl:variable>
                        <xsl:variable name="l_advisor">
                            <xsl:value-of select="func:escapeMarkupTags('http://www.loc.gov/loc.terms/relators/THS')"/>
                        </xsl:variable>
                        <xsl:variable name="l_contributor">
                            <xsl:value-of select="func:escapeMarkupTags('http://www.loc.gov/loc.terms/relators/CTB')"/>
                        </xsl:variable>
                        <xsl:variable name="l_publisher">
                            <xsl:value-of select="func:escapeMarkupTags('http://purl.org/escidoc/metadata/ves/creator-roles/publisher')"/>
                        </xsl:variable>
                        <xsl:variable name="l_honoree">
                            <xsl:value-of select="func:escapeMarkupTags('http://www.loc.gov/loc.terms/relators/HNR')"/>
                        </xsl:variable>
                        <xsl:variable name="l_contributor">
                            <xsl:value-of select="func:escapeMarkupTags('http://purl.org/escidoc/metadata/ves/creator-roles/founder')"/>
                        </xsl:variable>
                        <xsl:variable name="l_contributor">
                            <xsl:value-of select="func:escapeMarkupTags('http://purl.org/escidoc/metadata/ves/creator-roles/referee')"/>
                        </xsl:variable>
                        <xsl:variable name="l_master">
                            <xsl:value-of select="func:escapeMarkupTags('http://purl.org/escidoc/metadata/ves/academic-degrees/master')"/>
                        </xsl:variable>
                        <xsl:variable name="l_diploma">
                            <xsl:value-of select="func:escapeMarkupTags('http://purl.org/escidoc/metadata/ves/academic-degrees/diploma')"/>
                        </xsl:variable>
                        <xsl:variable name="l_magister">
                            <xsl:value-of select="func:escapeMarkupTags('http://purl.org/escidoc/metadata/ves/academic-degrees/magister')"/>
                        </xsl:variable>
                        <xsl:variable name="l_staatsexamen">
                            <xsl:value-of select="func:escapeMarkupTags('http://purl.org/escidoc/metadata/ves/academic-degrees/staatsexamen')"/>
                        </xsl:variable>
                        <xsl:variable name="l_phd">
                            <xsl:value-of select="func:escapeMarkupTags('http://purl.org/escidoc/metadata/ves/academic-degrees/phd')"/>
                        </xsl:variable>
                        <xsl:variable name="l_habilitation">
                            <xsl:value-of select="func:escapeMarkupTags('http://purl.org/escidoc/metadata/ves/academic-degrees/habilitation')"/>
                        </xsl:variable>
                        <xsl:variable name="l_bachelor">
                            <xsl:value-of select="func:escapeMarkupTags('http://purl.org/escidoc/metadata/ves/academic-degrees/bachelor')"/>
                        </xsl:variable>
                        <xsl:variable name="v_degree">
                            <xsl:value-of select="func:escapeMarkupTags(pub:publication/eterms:degree/text())"/>
                        </xsl:variable>
                        <xsl:variable name="l_degree">
                            <xsl:value-of select="func:escapeMarkupTags(&#xA;&#x9;&#x9;if ($v_degree=$l_master) then 'Master' else&#xA;&#x9;&#x9;if ($v_degree=$l_diploma) then 'Diploma' else&#xA;&#x9;&#x9;if ($v_degree=$l_magister) then 'Magister' else&#xA;&#x9;&#x9;if ($v_degree=$l_staatsexamen) then 'Staatsexamen' else&#xA;&#x9;&#x9;if ($v_degree=$l_phd) then 'PhD' else&#xA;&#x9;&#x9;if ($v_degree=$l_habilitation) then 'Habilitation' else&#xA;&#x9;&#x9;if ($v_degree=$l_bachelor) then 'Bachelor' else ''&#xA;&#x9;)"/>
                        </xsl:variable>
                        <!--### JUS_Report specific Default Variables ###-->
	<xsl:variable name="jus_default_variable">
                            <xsl:value-of select="func:escapeMarkupTags('JUS Report specific default variable')"/>
                        </xsl:variable>
                        <xsl:variable name="genre">
                            <xsl:value-of select="func:escapeMarkupTags(pub:publication/@type)"/>
                        </xsl:variable>
                        <xsl:variable name="genreTitle">
                            <xsl:value-of select="func:escapeMarkupTags(pub:publication/dc:title/text())"/>
                        </xsl:variable>
                        <xsl:variable name="creatorsCount">
                            <xsl:value-of select="func:escapeMarkupTags(count(pub:publication/e:creator))"/>
                        </xsl:variable>
                        <xsl:variable name="date">
                            <xsl:value-of select="func:escapeMarkupTags(&#xA;&#x9;&#x9;&#x9;&#xA;&#x9;&#x9;&#x9;&#xA;&#x9;&#x9;&#x9;&#x9;if (pub:publication/dcterms:issued/text()) &#xA;&#x9;&#x9;&#x9;&#x9;then func:get_year(pub:publication/dcterms:issued/text()) &#xA;&#x9;&#x9;&#x9;&#x9;&#xA;&#x9;&#x9;&#x9;&#x9;else if (pub:publication/e:published-online/text())&#xA;&#x9;&#x9;&#x9;&#x9;then func:get_year(pub:publication/e:published-online/text()) &#xA;&#x9;&#x9;&#x9;&#x9;&#xA;&#x9;&#x9;&#x9;&#x9;else if (pub:publication/dcterms:dateAccepted/text())&#xA;&#x9;&#x9;&#x9;&#x9;then func:get_year(pub:publication/dcterms:dateAccepted/text()) &#xA;&#x9;&#x9;&#x9;&#x9;&#xA;&#x9;&#x9;&#x9;&#x9;else if (pub:publication/dcterms:dateSubmitted/text())&#xA;&#x9;&#x9;&#x9;&#x9;then func:get_year(pub:publication/dcterms:dateSubmitted/text()) &#xA;&#x9;&#x9;&#x9;&#x9;&#xA;&#x9;&#x9;&#x9;&#x9;else if (pub:publication/dcterms:modified/text())&#xA;&#x9;&#x9;&#x9;&#x9;then func:get_year(pub:publication/dcterms:modified/text()) &#xA;&#x9;&#x9;&#x9;&#x9;&#xA;&#x9;&#x9;&#x9;&#x9;else if (pub:publication/dcterms:created/text())&#xA;&#x9;&#x9;&#x9;&#x9;then func:get_year(pub:publication/dcterms:created/text()) &#xA;&#x9;&#x9;&#x9;&#x9;&#xA;&#x9;&#x9;&#x9;&#x9;else ''&#xA;&#x9;&#x9;)"/>
                        </xsl:variable>
                        <xsl:variable name="sourceGenre">
                            <xsl:value-of select="func:escapeMarkupTags(pub:publication/source:source[1]/@type)"/>
                        </xsl:variable>
                        <xsl:variable name="sourceStartPage">
                            <xsl:value-of select="func:escapeMarkupTags(pub:publication/source:source[1]/e:start-page/text())"/>
                        </xsl:variable>
                        <xsl:variable name="sourceEndPage">
                            <xsl:value-of select="func:escapeMarkupTags(pub:publication/source:source[1]/e:end-page/text())"/>
                        </xsl:variable>
                        <xsl:variable name="sourceCreatorsCount">
                            <xsl:value-of select="func:escapeMarkupTags(count(pub:publication/source:source/e:creator))"/>
                        </xsl:variable>
                        <xsl:variable name="firstSourceIssue">
                            <xsl:value-of select="func:escapeMarkupTags(pub:publication/source:source[1]/e:issue/text())"/>
                        </xsl:variable>
                        <xsl:variable name="firstSourceVolume">
                            <xsl:value-of select="func:escapeMarkupTags(pub:publication/source:source[1]/e:volume/text())"/>
                        </xsl:variable>
                        <xsl:variable name="degree_test">
                            <xsl:value-of select="func:escapeMarkupTags(pub:publication/e:degree/text())"/>
                        </xsl:variable>
                        <xsl:variable name="l_jus_degree">
                            <xsl:value-of select="func:escapeMarkupTags(&#xA;&#x9;&#x9;if (pub:publication/e:degree/text() = 'http://purl.org/escidoc/metadata/ves/academic-degrees/bachelor')&#xA;&#x9;&#x9;then 'Bachelor'&#xA;&#x9;&#x9;&#xA;&#x9;&#x9;else if (pub:publication/e:degree/text() = 'http://purl.org/escidoc/metadata/ves/academic-degrees/diploma')&#xA;&#x9;&#x9;then 'Diplom'&#xA;&#x9;&#x9;&#xA;&#x9;&#x9;else if (pub:publication/e:degree/text() = 'http://purl.org/escidoc/metadata/ves/academic-degrees/habilitation')&#xA;&#x9;&#x9;then 'Habilitation'&#xA;&#x9;&#x9;&#xA;&#x9;&#x9;else if (pub:publication/e:degree/text() = 'http://purl.org/escidoc/metadata/ves/academic-degrees/master')&#xA;&#x9;&#x9;then 'Master'&#xA;&#x9;&#x9;&#xA;&#x9;&#x9;else if (pub:publication/e:degree/text() = 'http://purl.org/escidoc/metadata/ves/academic-degrees/magister')&#xA;&#x9;&#x9;then 'Magister'&#xA;&#x9;&#x9;&#xA;&#x9;&#x9;else if (pub:publication/e:degree/text() = 'http://purl.org/escidoc/metadata/ves/academic-degrees/phd')&#xA;&#x9;&#x9;then 'Dissertation'&#xA;&#x9;&#x9;&#xA;&#x9;&#x9;else if (pub:publication/e:degree/text() = 'http://purl.org/escidoc/metadata/ves/academic-degrees/staatsexamen')&#xA;&#x9;&#x9;then 'Staatsexamen'&#xA;&#x9;&#x9;&#xA;&#x9;&#x9;else ''&#xA;&#x9;&#x9;&#xA;&#x9;&#x9;)"/>
                        </xsl:variable>
                        <xsl:variable name="onlineIdType">
                            <xsl:value-of select="func:escapeMarkupTags(&#xA;&#x9;&#x9;&#x9;&#xA;&#x9;&#x9;&#x9;&#x9;if ($sourceGenre=$l_journal) then &#xA;&#x9;&#x9;&#x9;&#x9;&#x9;if (pub:publication/dc:identifier[@xsi:type='eterms:DOI']) &#xA;&#x9;&#x9;&#x9;&#x9;&#x9;then 'DOI'&#xA;&#x9;&#x9;&#x9;&#x9;&#x9;&#xA;&#x9;&#x9;&#x9;&#x9;&#x9;else if (pub:publication/dc:identifier[@xsi:type='eterms:URN']) &#xA;&#x9;&#x9;&#x9;&#x9;&#x9;then 'URN'&#xA;&#x9;&#x9;&#x9;&#x9;&#x9;&#xA;&#x9;&#x9;&#x9;&#x9;&#x9;else if (pub:publication/dc:identifier[@xsi:type='eterms:URI']) &#xA;&#x9;&#x9;&#x9;&#x9;&#x9;then 'URI'&#xA;&#x9;&#x9;&#x9;&#x9;&#x9;&#xA;&#x9;&#x9;&#x9;&#x9;&#x9;else if (pub:publication/source:source[1]/dc:identifier[@xsi:type='eterms:URI']) &#xA;&#x9;&#x9;&#x9;&#x9;&#x9;then 'source_URI'&#xA;&#x9;&#x9;&#x9;&#x9;&#x9;&#xA;&#x9;&#x9;&#x9;&#x9;&#x9;else if (../../escidocComponents:components/escidocComponents:component/escidocComponents:content[@storage='external-url'])&#xA;&#x9;&#x9;&#x9;&#x9;&#x9;then 'external_URL'&#xA;&#x9;&#x9;&#x9;&#x9;&#x9;&#xA;&#x9;&#x9;&#x9;&#x9;&#x9;else ''&#xA;&#x9;&#x9;&#x9;&#x9;else ''&#xA;&#x9;&#x9;)"/>
                        </xsl:variable>
                        <xsl:variable name="onlineIdValue">
                            <xsl:value-of select="func:escapeMarkupTags(&#xA;&#x9;&#x9;&#x9;&#xA;&#x9;&#x9;&#x9;if ($sourceGenre=$l_journal) then &#xA;&#x9;&#x9;&#x9;&#x9;if ($onlineIdType = 'DOI') &#xA;&#x9;&#x9;&#x9;&#x9;then normalize-space(pub:publication/dc:identifier[@xsi:type='eterms:DOI'][1]/text())&#xA;&#x9;&#x9;&#x9;&#x9;&#xA;&#x9;&#x9;&#x9;&#x9;else if ($onlineIdType = 'URN') &#xA;&#x9;&#x9;&#x9;&#x9;then pub:publication/dc:identifier[@xsi:type='eterms:URN'][1]/text()&#xA;&#x9;&#x9;&#x9;&#x9;&#xA;&#x9;&#x9;&#x9;&#x9;else if ($onlineIdType = 'URI') &#xA;&#x9;&#x9;&#x9;&#x9;then pub:publication/dc:identifier[@xsi:type='eterms:URI'][1]/text()&#xA;&#x9;&#x9;&#x9;&#x9;&#xA;&#x9;&#x9;&#x9;&#x9;else if ($onlineIdType = 'source_URI') &#xA;&#x9;&#x9;&#x9;&#x9;then pub:publication/source:source[1]/dc:identifier[@xsi:type='eterms:URI'][1]/text()&#xA;&#x9;&#x9;&#x9;&#x9;&#xA;&#x9;&#x9;&#x9;&#x9;&#xA;&#x9;&#x9;&#x9;&#x9;else if (../../escidocComponents:components/escidocComponents:component/escidocComponents:content[@storage='external-url'])&#xA;&#x9;&#x9;&#x9;&#x9;then ../../escidocComponents:components/escidocComponents:component/escidocComponents:content/@xlink:href&#xA;&#x9;&#x9;&#x9;&#x9;&#xA;&#x9;&#x9;&#x9;&#x9;else ''&#xA;&#x9;&#x9;&#x9;&#x9;&#xA;&#x9;&#x9;&#x9;else ''&#xA;&#x9;&#x9;)"/>
                        </xsl:variable>
                        <xsl:variable name="onlineIdTypePaper">
                            <xsl:value-of select="func:escapeMarkupTags(&#xA;&#x9;&#x9;&#x9;&#xA;&#x9;&#x9;&#x9;&#x9;if ($genre = $l_paper) then &#xA;&#x9;&#x9;&#x9;&#x9;&#x9;if (pub:publication/dc:identifier[@xsi:type='eterms:SSRN']) &#xA;&#x9;&#x9;&#x9;&#x9;&#x9;then 'SSRN'&#xA;&#x9;&#x9;&#x9;&#x9;&#x9;&#xA;&#x9;&#x9;&#x9;&#x9;&#x9;else if (pub:publication/dc:identifier[@xsi:type='eterms:URN']) &#xA;&#x9;&#x9;&#x9;&#x9;&#x9;then 'URN'&#xA;&#x9;&#x9;&#x9;&#x9;&#x9;&#xA;&#x9;&#x9;&#x9;&#x9;&#x9;else if (pub:publication/dc:identifier[@xsi:type='eterms:URI']) &#xA;&#x9;&#x9;&#x9;&#x9;&#x9;then 'URI'&#xA;&#x9;&#x9;&#x9;&#x9;&#x9;&#xA;&#x9;&#x9;&#x9;&#x9;&#x9;else if (../../escidocComponents:components/escidocComponents:component/escidocComponents:content[@storage='external-url'])&#xA;&#x9;&#x9;&#x9;&#x9;&#x9;then 'external_URL'&#xA;&#x9;&#x9;&#x9;&#x9;&#x9;&#xA;&#x9;&#x9;&#x9;&#x9;&#x9;else if (pub:publication/source:source[1]/dc:identifier[@xsi:type='eterms:SSRN']) &#xA;&#x9;&#x9;&#x9;&#x9;&#x9;then 'source_SSRN'&#xA;&#x9;&#x9;&#x9;&#x9;&#x9;&#xA;&#x9;&#x9;&#x9;&#x9;&#x9;else ''&#xA;&#x9;&#x9;&#x9;&#x9;else ''&#xA;&#x9;&#x9;)"/>
                        </xsl:variable>
                        <xsl:variable name="onlineIdValuePaper">
                            <xsl:value-of select="func:escapeMarkupTags(&#xA;&#x9;&#x9;&#x9;&#xA;&#x9;&#x9;&#x9;if ($genre = $l_paper) then &#xA;&#x9;&#x9;&#x9;&#x9;if ($onlineIdTypePaper = 'SSRN') &#xA;&#x9;&#x9;&#x9;&#x9;then normalize-space(pub:publication/dc:identifier[@xsi:type='eterms:SSRN'][1]/text())&#xA;&#x9;&#x9;&#x9;&#x9;&#xA;&#x9;&#x9;&#x9;&#x9;else if ($onlineIdTypePaper = 'URN') &#xA;&#x9;&#x9;&#x9;&#x9;then pub:publication/dc:identifier[@xsi:type='eterms:URN'][1]/text()&#xA;&#x9;&#x9;&#x9;&#x9;&#xA;&#x9;&#x9;&#x9;&#x9;else if ($onlineIdTypePaper = 'URI') &#xA;&#x9;&#x9;&#x9;&#x9;then pub:publication/dc:identifier[@xsi:type='eterms:URI'][1]/text()&#xA;&#x9;&#x9;&#x9;&#x9;&#xA;&#x9;&#x9;&#x9;&#x9;else if (../../escidocComponents:components/escidocComponents:component/escidocComponents:content[@storage='external-url'])&#xA;&#x9;&#x9;&#x9;&#x9;then ../../escidocComponents:components/escidocComponents:component/escidocComponents:content[@storage='external-url']/@xlink:href&#xA;&#x9;&#x9;&#x9;&#x9;&#xA;&#x9;&#x9;&#x9;&#x9;else if ($onlineIdTypePaper = 'source_SSRN') &#xA;&#x9;&#x9;&#x9;&#x9;then pub:publication/source:source[1]/dc:identifier[@xsi:type='eterms:SSRN'][1]/text()&#xA;&#x9;&#x9;&#x9;&#x9;&#xA;&#x9;&#x9;&#x9;&#x9;&#xA;&#x9;&#x9;&#x9;&#x9;else ''&#xA;&#x9;&#x9;&#x9;&#x9;&#xA;&#x9;&#x9;&#x9;else ''&#xA;&#x9;&#x9;)"/>
                        </xsl:variable>
                        <xsl:variable name="idType">
                            <xsl:value-of select="func:escapeMarkupTags(&#xA;&#x9;&#x9;&#x9;&#xA;&#x9;&#x9;&#x9;&#x9;if ($sourceGenre=$l_journal) then &#xA;&#x9;&#x9;&#x9;&#x9;&#x9;if (pub:publication/source:source/dc:identifier[@xsi:type='eterms:CONE']) &#xA;&#x9;&#x9;&#x9;&#x9;&#x9;then 'CONE'&#xA;&#x9;&#x9;&#x9;&#x9;&#x9;&#xA;&#x9;&#x9;&#x9;&#x9;&#x9;else if (pub:publication/source:source/dc:identifier[@xsi:type='eterms:ISSN']) &#xA;&#x9;&#x9;&#x9;&#x9;&#x9;then 'ISSN'&#xA;&#x9;&#x9;&#x9;&#x9;&#x9;&#xA;&#x9;&#x9;&#x9;&#x9;&#x9;else if (pub:publication/source:source/dc:identifier[@xsi:type='eterms:ZDB']) &#xA;&#x9;&#x9;&#x9;&#x9;&#x9;then 'ZDB'&#xA;&#x9;&#x9;&#x9;&#x9;&#x9;else ''&#xA;&#x9;&#x9;&#x9;&#x9;else ''&#xA;&#x9;&#x9;)"/>
                        </xsl:variable>
                        <xsl:variable name="idValue">
                            <xsl:value-of select="func:escapeMarkupTags(&#xA;&#x9;&#x9;&#x9;&#xA;&#x9;&#x9;&#x9;if ($sourceGenre=$l_journal) then &#xA;&#x9;&#x9;&#x9;&#x9;if ($idType = 'CONE') &#xA;&#x9;&#x9;&#x9;&#x9;then normalize-space(pub:publication/source:source/dc:identifier[@xsi:type='eterms:CONE'][1]/text())&#xA;&#x9;&#x9;&#x9;&#x9;&#xA;&#x9;&#x9;&#x9;&#x9;else if ($idType = 'ISSN') &#xA;&#x9;&#x9;&#x9;&#x9;then pub:publication/source:source/dc:identifier[@xsi:type='eterms:ISSN'][1]/text()&#xA;&#x9;&#x9;&#x9;&#x9;&#xA;&#x9;&#x9;&#x9;&#x9;else if ($idType = 'ZDB') &#xA;&#x9;&#x9;&#x9;&#x9;then pub:publication/source:source/dc:identifier[@xsi:type='eterms:ZDB'][1]/text()&#xA;&#x9;&#x9;&#x9;&#x9;&#xA;&#x9;&#x9;&#x9;&#x9;else ''&#xA;&#x9;&#x9;&#x9;&#x9;else ''&#xA;&#x9;&#x9;)"/>
                        </xsl:variable>
                        <xsl:variable name="volume-prefix">
                            <xsl:value-of select="func:escapeMarkupTags('Bd.')"/>
                        </xsl:variable>
                        <xsl:variable name="in-prefix">
                            <xsl:value-of select="func:escapeMarkupTags(', in:')"/>
                        </xsl:variable>
                        <xsl:variable name="doi-prefix">
                            <xsl:value-of select="func:escapeMarkupTags('DOI:')"/>
                        </xsl:variable>
                        <xsl:variable name="since-prefix">
                            <xsl:value-of select="func:escapeMarkupTags('seit')"/>
                        </xsl:variable>
                        <xsl:variable name="reviewOf-prefix">
                            <xsl:value-of select="func:escapeMarkupTags('Rezension:')"/>
                        </xsl:variable>
                        <xsl:variable name="editor-postfix">
                            <xsl:value-of select="func:escapeMarkupTags('Hg.')"/>
                        </xsl:variable>
                        <xsl:variable name="etAll-postfix">
                            <xsl:value-of select="func:escapeMarkupTags('et al.')"/>
                        </xsl:variable>
                        <xsl:variable name="edition-postfix">
                            <xsl:value-of select="func:escapeMarkupTags('Aufl.')"/>
                        </xsl:variable>
                        <xsl:variable name="pages-postfix">
                            <xsl:value-of select="func:escapeMarkupTags('S.')"/>
                        </xsl:variable>
                        <xsl:variable name="instalment-postfix">
                            <xsl:value-of select="func:escapeMarkupTags('Lf.')"/>
                        </xsl:variable>
                        <xsl:variable name="phd">
                            <xsl:value-of select="func:escapeMarkupTags('Dissertation')"/>
                        </xsl:variable>
                        <xsl:variable name="in-prefix">
                            <xsl:value-of select="func:escapeMarkupTags(', in:')"/>
                        </xsl:variable>
                        <xsl:variable name="doi-prefix">
                            <xsl:value-of select="func:escapeMarkupTags('DOI:')"/>
                        </xsl:variable>
                        <xsl:variable name="objid">
                            <xsl:value-of select="func:escapeMarkupTags(../../@objid)"/>
                        </xsl:variable>
                        <xsl:variable name="hasPublication" as="xs:boolean">
                            <xsl:value-of select="func:escapeMarkupTags(exists(pub:publication))"/>
                        </xsl:variable>
                        <xsl:variable name="local-tag">
                            <xsl:value-of select="func:escapeMarkupTags('../../escidocItem:properties/prop:content-model-specific/local-tags/local-tag/text()')"/>
                        </xsl:variable>
                        <xsl:variable name="online_article">
                            <xsl:value-of select="func:escapeMarkupTags( 'ONLINE: ')"/>
                        </xsl:variable>
                        <xsl:variable name="print_article">
                            <xsl:value-of select="func:escapeMarkupTags( 'PRINT: ')"/>
                        </xsl:variable>
                        <xsl:variable name="getCitationStyleForJournal">
                            <xsl:value-of select="func:escapeMarkupTags(if ($sourceGenre=$l_journal and $genre != $l_issue) then func:getCitationStyleForJournal($idType,$idValue) else '')"/>
                        </xsl:variable>
                        <!--### Predefined Layout Elements ###-->
	<xsl:variable name="source-editors"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                            <xsl:variable name="var">
                                <xsl:call-template name="applyDelimiter">
                                    <xsl:with-param name="les">
                                        <le>
                                            <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                                                <!--valid-if--><xsl:variable name="var">
                                                    <xsl:if test="$sourceCreatorsCount&lt;2 or $sourceCreatorsCount&gt;3">
                                                        <xsl:variable name="var">
                                                            <xsl:call-template name="applyDelimiter">
                                                                <xsl:with-param name="les">
                                                                    <le>
                                                                        <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var"
                                                                                          select="func:escapeMarkupTags(pub:publication/source:source/e:creator[1]/person:person/e:given-name/text())"/>
                                                                            <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                                                        </xsl:variable>
                                                                        <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                                    </le>
                                                                    <le>
                                                                        <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var"
                                                                                          select="func:escapeMarkupTags(pub:publication/source:source/e:creator[1]/person:person/e:family-name/text())"/>
                                                                            <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                                                        </xsl:variable>
                                                                        <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                                    </le>
                                                                    <le>
                                                                        <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var"
                                                                                          select="func:escapeMarkupTags(pub:publication/source:source/e:creator[1]/organization:organization/dc:title/text())"/>
                                                                            <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                                                        </xsl:variable>
                                                                        <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                                    </le>
                                                                </xsl:with-param>
                                                                <xsl:with-param name="delimiter" select="' '"/>
                                                            </xsl:call-template>
                                                        </xsl:variable>
                                                        <!--XXX - Apply params Copy Of 1 -XXX--><xsl:copy-of select="$var"/>
                                                    </xsl:if>
                                                </xsl:variable>
                                                <!--XXX - Apply params Copy Of 2 -XXX--><xsl:copy-of select="$var"/>
                                            </xsl:variable>
                                            <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                        </le>
                                        <le>
                                            <xsl:variable name="var"><!--### Repeatable Layout Element ###-->
	<xsl:variable name="var" select="''"/>
                                                <!--valid-if--><xsl:variable name="var">
                                                    <xsl:if test="$sourceCreatorsCount&gt;1 and $sourceCreatorsCount&lt;4">
                                                        <xsl:variable name="var">
                                                            <xsl:call-template name="applyDelimiter">
                                                                <xsl:with-param name="les">
                                                                    <xsl:for-each select="pub:publication/source:source/e:creator">
                                                                        <le position-delimiter=", ">
                                                                            <xsl:call-template name="applyDelimiter">
                                                                                <xsl:with-param name="les">
                                                                                    <le>
                                                                                        <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="func:escapeMarkupTags(person:person/e:given-name/text())"/>
                                                                                            <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                                                                        </xsl:variable>
                                                                                        <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                                                    </le>
                                                                                    <le>
                                                                                        <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="func:escapeMarkupTags(person:person/e:family-name/text())"/>
                                                                                            <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                                                                        </xsl:variable>
                                                                                        <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                                                    </le>
                                                                                    <le>
                                                                                        <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var"
                                                                                                          select="func:escapeMarkupTags(organization:organization/dc:title/text())"/>
                                                                                            <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                                                                        </xsl:variable>
                                                                                        <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                                                    </le>
                                                                                </xsl:with-param>
                                                                                <xsl:with-param name="delimiter" select="' '"/>
                                                                            </xsl:call-template>
                                                                        </le>
                                                                    </xsl:for-each>
                                                                </xsl:with-param>
                                                                <xsl:with-param name="delimiter" select="', '"/>
                                                            </xsl:call-template>
                                                        </xsl:variable>
                                                        <!--XXX - Apply params Copy Of 1 -XXX--><xsl:copy-of select="$var"/>
                                                    </xsl:if>
                                                </xsl:variable>
                                                <!--XXX - Apply params Copy Of 2 -XXX--><xsl:copy-of select="$var"/>
                                            </xsl:variable>
                                            <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                        </le>
                                        <le>
                                            <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                                                <xsl:variable name="var">
                                                    <xsl:call-template name="applyDelimiter">
                                                        <xsl:with-param name="les">
                                                            <le>
                                                                <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                                                                    <!--valid-if--><xsl:variable name="var">
                                                                        <xsl:if test="$sourceCreatorsCount&gt;3">
                                                                            <xsl:variable name="var">
                                                                                <xsl:call-template name="applyDelimiter">
                                                                                    <xsl:with-param name="les">
                                                                                        <le>
                                                                                            <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$etAll-postfix"/>
                                                                                                <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                                                                            </xsl:variable>
                                                                                            <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                                                        </le>
                                                                                    </xsl:with-param>
                                                                                    <xsl:with-param name="delimiter" select="' '"/>
                                                                                </xsl:call-template>
                                                                            </xsl:variable>
                                                                            <!--XXX - Apply params Copy Of 1 -XXX--><xsl:copy-of select="$var"/>
                                                                        </xsl:if>
                                                                    </xsl:variable>
                                                                    <!--XXX - Apply params Copy Of 2 -XXX--><xsl:copy-of select="$var"/>
                                                                </xsl:variable>
                                                                <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                            </le>
                                                            <le>
                                                                <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                                                                    <!--valid-if--><xsl:variable name="var">
                                                                        <xsl:if test="$sourceCreatorsCount&gt;0">
                                                                            <xsl:variable name="var">
                                                                                <xsl:call-template name="applyDelimiter">
                                                                                    <xsl:with-param name="les">
                                                                                        <le>
                                                                                            <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$editor-postfix"/>
                                                                                                <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                                                                            </xsl:variable>
                                                                                            <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                                                        </le>
                                                                                    </xsl:with-param>
                                                                                    <xsl:with-param name="delimiter" select="' '"/>
                                                                                </xsl:call-template>
                                                                            </xsl:variable>
                                                                            <!--
				start-with/ends-with
			--><xsl:variable name="var">
                                                                                <xsl:if test="exists($var) and $var!=''">
                                                                                    <xsl:text>(</xsl:text>
                                                                                </xsl:if>
                                                                                <!--XXX - applyStartsWithEndsWith Copy Of 1 -XXX--><xsl:copy-of select="$var"/>
                                                                                <xsl:if test="exists($var) and $var!=''">
                                                                                    <xsl:text>)</xsl:text>
                                                                                </xsl:if>
                                                                            </xsl:variable>
                                                                            <!--XXX - Apply params Copy Of 1 -XXX--><xsl:copy-of select="$var"/>
                                                                        </xsl:if>
                                                                    </xsl:variable>
                                                                    <!--XXX - Apply params Copy Of 2 -XXX--><xsl:copy-of select="$var"/>
                                                                </xsl:variable>
                                                                <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                            </le>
                                                        </xsl:with-param>
                                                        <xsl:with-param name="delimiter" select="' '"/>
                                                    </xsl:call-template>
                                                </xsl:variable>
                                                <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                            </xsl:variable>
                                            <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                        </le>
                                    </xsl:with-param>
                                    <xsl:with-param name="delimiter" select="' '"/>
                                </xsl:call-template>
                            </xsl:variable>
                            <!--
				start-with/ends-with
			--><xsl:variable name="var"><!--XXX - applyStartsWithEndsWith Copy Of 1 -XXX--><xsl:copy-of select="$var"/>
                                <xsl:if test="exists($var) and $var!=''">
                                    <xsl:text>, </xsl:text>
                                </xsl:if>
                            </xsl:variable>
                            <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                        </xsl:variable>
                        <xsl:variable name="edition"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                            <!--valid-if--><xsl:variable name="var">
                                <xsl:if test="exists(pub:publication/e:publishing-info/e:edition/text()) &#xA;&#x9;&#x9;&#x9;&#x9;or exists(pub:publication/source:source[1]/e:publishing-info/e:edition/text())">
                                    <xsl:variable name="var">
                                        <xsl:call-template name="applyDelimiter">
                                            <xsl:with-param name="les">
                                                <le>
                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                                                        <!--valid-if--><xsl:variable name="var">
                                                            <xsl:if test="&#xA;&#x9;&#x9;&#x9;&#x9;&#x9;&#x9;&#xA;&#x9;&#x9;&#x9;&#x9;&#x9;&#x9;(contains(pub:publication/e:publishing-info/e:edition/text(),'instl') and contains(pub:publication/e:publishing-info/e:edition/text(),'ed')) or &#xA;&#x9;&#x9;&#x9;&#x9;&#x9;&#x9;(contains(pub:publication/source:source[1]/e:publishing-info/e:edition/text(),'instl') and contains(pub:publication/source:source[1]/e:publishing-info/e:edition/text(),'ed')) or&#xA;&#x9;&#x9;&#x9;&#x9;&#x9;&#x9;&#xA;&#x9;&#x9;&#x9;&#x9;&#x9;&#x9;(contains(pub:publication/e:publishing-info/e:edition/text(),'Lf') and contains(pub:publication/e:publishing-info/e:edition/text(),'Aufl')) or &#xA;&#x9;&#x9;&#x9;&#x9;&#x9;&#x9;(contains(pub:publication/source:source[1]/e:publishing-info/e:edition/text(),'Lf') and contains(pub:publication/source:source[1]/e:publishing-info/e:edition/text(),'Aufl'))&#xA;&#x9;&#x9;&#x9;&#x9;&#x9;&#x9;&#xA;&#x9;&#x9;&#x9;&#x9;&#x9;&#x9;">
                                                                <xsl:variable name="var">
                                                                    <xsl:call-template name="applyDelimiter">
                                                                        <xsl:with-param name="les">
                                                                            <le>
                                                                                <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var"
                                                                                                  select="func:escapeMarkupTags(func:substringBeforeEdition(pub:publication/e:publishing-info/e:edition/text()))"/>
                                                                                    <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                                                                </xsl:variable>
                                                                                <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                                            </le>
                                                                            <le>
                                                                                <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var"
                                                                                                  select="func:escapeMarkupTags(func:substringBeforeEdition(pub:publication/source:source[1]/e:publishing-info/e:edition/text()))"/>
                                                                                    <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                                                                </xsl:variable>
                                                                                <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                                            </le>
                                                                            <le>
                                                                                <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$edition-postfix"/>
                                                                                    <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                                                                </xsl:variable>
                                                                                <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                                            </le>
                                                                            <le>
                                                                                <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                                                                                    <xsl:variable name="var">
                                                                                        <xsl:call-template name="applyDelimiter">
                                                                                            <xsl:with-param name="les">
                                                                                                <le>
                                                                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var"
                                                                                                                      select="func:escapeMarkupTags(func:substringAfterEdition(pub:publication/e:publishing-info/e:edition/text()))"/>
                                                                                                        <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                                                                                    </xsl:variable>
                                                                                                    <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                                                                </le>
                                                                                                <le>
                                                                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var"
                                                                                                                      select="func:escapeMarkupTags(func:substringAfterEdition(pub:publication/source:source[1]/e:publishing-info/e:edition/text()))"/>
                                                                                                        <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                                                                                    </xsl:variable>
                                                                                                    <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                                                                </le>
                                                                                                <le>
                                                                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$instalment-postfix"/>
                                                                                                        <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                                                                                    </xsl:variable>
                                                                                                    <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                                                                </le>
                                                                                            </xsl:with-param>
                                                                                            <xsl:with-param name="delimiter" select="' '"/>
                                                                                        </xsl:call-template>
                                                                                    </xsl:variable>
                                                                                    <!--
				start-with/ends-with
			--><xsl:variable name="var">
                                                                                        <xsl:if test="exists($var) and $var!=''">
                                                                                            <xsl:text>, </xsl:text>
                                                                                        </xsl:if>
                                                                                        <!--XXX - applyStartsWithEndsWith Copy Of 1 -XXX--><xsl:copy-of select="$var"/>
                                                                                    </xsl:variable>
                                                                                    <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                                                                </xsl:variable>
                                                                                <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                                            </le>
                                                                        </xsl:with-param>
                                                                        <xsl:with-param name="delimiter" select="' '"/>
                                                                    </xsl:call-template>
                                                                </xsl:variable>
                                                                <!--XXX - Apply params Copy Of 1 -XXX--><xsl:copy-of select="$var"/>
                                                            </xsl:if>
                                                        </xsl:variable>
                                                        <!--XXX - Apply params Copy Of 2 -XXX--><xsl:copy-of select="$var"/>
                                                    </xsl:variable>
                                                    <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                </le>
                                                <le>
                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                                                        <!--valid-if--><xsl:variable name="var">
                                                            <xsl:if test="&#xA;&#x9;&#x9;&#x9;&#x9;&#x9;&#x9;&#xA;&#x9;&#x9;&#x9;&#x9;&#x9;&#x9;(contains(pub:publication/e:publishing-info/e:edition/text(),'instl') and not(contains(pub:publication/e:publishing-info/e:edition/text(),'ed'))) or &#xA;&#x9;&#x9;&#x9;&#x9;&#x9;&#x9;(contains(pub:publication/source:source[1]/e:publishing-info/e:edition/text(),'instl') and not(contains(pub:publication/source:source[1]/e:publishing-info/e:edition/text(),'ed'))) or&#xA;&#x9;&#x9;&#x9;&#x9;&#x9;&#x9;&#xA;&#x9;&#x9;&#x9;&#x9;&#x9;&#x9;(contains(pub:publication/e:publishing-info/e:edition/text(),'Lf') and not(contains(pub:publication/e:publishing-info/e:edition/text(),'Aufl'))) or &#xA;&#x9;&#x9;&#x9;&#x9;&#x9;&#x9;(contains(pub:publication/source:source[1]/e:publishing-info/e:edition/text(),'Lf') and not(contains(pub:publication/source:source[1]/e:publishing-info/e:edition/text(),'Aufl')))&#xA;&#x9;&#x9;&#x9;&#x9;&#x9;&#x9;&#xA;&#x9;&#x9;&#x9;&#x9;&#x9;&#x9;">
                                                                <xsl:variable name="var">
                                                                    <xsl:call-template name="applyDelimiter">
                                                                        <xsl:with-param name="les">
                                                                            <le>
                                                                                <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                                                                                    <xsl:variable name="var">
                                                                                        <xsl:call-template name="applyDelimiter">
                                                                                            <xsl:with-param name="les">
                                                                                                <le>
                                                                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var"
                                                                                                                      select="func:escapeMarkupTags(func:substringBeforeInstalment(pub:publication/e:publishing-info/e:edition/text()))"/>
                                                                                                        <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                                                                                    </xsl:variable>
                                                                                                    <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                                                                </le>
                                                                                                <le>
                                                                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var"
                                                                                                                      select="func:escapeMarkupTags(func:substringBeforeInstalment(pub:publication/source:source[1]/e:publishing-info/e:edition/text()))"/>
                                                                                                        <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                                                                                    </xsl:variable>
                                                                                                    <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                                                                </le>
                                                                                                <le>
                                                                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$instalment-postfix"/>
                                                                                                        <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                                                                                    </xsl:variable>
                                                                                                    <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                                                                </le>
                                                                                            </xsl:with-param>
                                                                                            <xsl:with-param name="delimiter" select="' '"/>
                                                                                        </xsl:call-template>
                                                                                    </xsl:variable>
                                                                                    <!--
				start-with/ends-with
			--><xsl:variable name="var">
                                                                                        <xsl:if test="exists($var) and $var!=''">
                                                                                            <xsl:text>, </xsl:text>
                                                                                        </xsl:if>
                                                                                        <!--XXX - applyStartsWithEndsWith Copy Of 1 -XXX--><xsl:copy-of select="$var"/>
                                                                                    </xsl:variable>
                                                                                    <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                                                                </xsl:variable>
                                                                                <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                                            </le>
                                                                        </xsl:with-param>
                                                                        <xsl:with-param name="delimiter" select="' '"/>
                                                                    </xsl:call-template>
                                                                </xsl:variable>
                                                                <!--XXX - Apply params Copy Of 1 -XXX--><xsl:copy-of select="$var"/>
                                                            </xsl:if>
                                                        </xsl:variable>
                                                        <!--XXX - Apply params Copy Of 2 -XXX--><xsl:copy-of select="$var"/>
                                                    </xsl:variable>
                                                    <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                </le>
                                                <le>
                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                                                        <!--valid-if--><xsl:variable name="var">
                                                            <xsl:if test="&#xA;&#x9;&#x9;&#x9;&#x9;&#x9;&#x9;&#xA;&#x9;&#x9;&#x9;&#x9;&#x9;&#x9;not(contains(pub:publication/e:publishing-info/e:edition/text(),'instl')) and &#xA;&#x9;&#x9;&#x9;&#x9;&#x9;&#x9;not(contains(pub:publication/source:source[1]/e:publishing-info/e:edition/text(),'instl')) and&#xA;&#x9;&#x9;&#x9;&#x9;&#x9;&#x9;&#xA;&#x9;&#x9;&#x9;&#x9;&#x9;&#x9;not(contains(pub:publication/e:publishing-info/e:edition/text(),'Lf')) and&#xA;&#x9;&#x9;&#x9;&#x9;&#x9;&#x9;not(contains(pub:publication/source:source[1]/e:publishing-info/e:edition/text(),'Lf'))&#xA;&#x9;&#x9;&#x9;&#x9;&#x9;&#x9;&#xA;&#x9;&#x9;&#x9;&#x9;&#x9;&#x9;">
                                                                <xsl:variable name="var">
                                                                    <xsl:call-template name="applyDelimiter">
                                                                        <xsl:with-param name="les">
                                                                            <le>
                                                                                <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var"
                                                                                                  select="func:escapeMarkupTags(pub:publication/e:publishing-info/e:edition/text())"/>
                                                                                    <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                                                                </xsl:variable>
                                                                                <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                                            </le>
                                                                            <le>
                                                                                <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var"
                                                                                                  select="func:escapeMarkupTags(pub:publication/source:source[1]/e:publishing-info/e:edition/text())"/>
                                                                                    <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                                                                </xsl:variable>
                                                                                <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                                            </le>
                                                                            <le>
                                                                                <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$edition-postfix"/>
                                                                                    <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                                                                </xsl:variable>
                                                                                <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                                            </le>
                                                                        </xsl:with-param>
                                                                        <xsl:with-param name="delimiter" select="'. '"/>
                                                                    </xsl:call-template>
                                                                </xsl:variable>
                                                                <!--XXX - Apply params Copy Of 1 -XXX--><xsl:copy-of select="$var"/>
                                                            </xsl:if>
                                                        </xsl:variable>
                                                        <!--XXX - Apply params Copy Of 2 -XXX--><xsl:copy-of select="$var"/>
                                                    </xsl:variable>
                                                    <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                </le>
                                            </xsl:with-param>
                                            <xsl:with-param name="delimiter" select="' '"/>
                                        </xsl:call-template>
                                    </xsl:variable>
                                    <!--
				start-with/ends-with
			--><xsl:variable name="var">
                                        <xsl:if test="exists($var) and $var!=''">
                                            <xsl:text>, </xsl:text>
                                        </xsl:if>
                                        <!--XXX - applyStartsWithEndsWith Copy Of 1 -XXX--><xsl:copy-of select="$var"/>
                                    </xsl:variable>
                                    <!--XXX - Apply params Copy Of 1 -XXX--><xsl:copy-of select="$var"/>
                                </xsl:if>
                            </xsl:variable>
                            <!--XXX - Apply params Copy Of 2 -XXX--><xsl:copy-of select="$var"/>
                        </xsl:variable>
                        <xsl:variable name="publisher-without-place"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                            <xsl:variable name="var">
                                <xsl:call-template name="applyDelimiter">
                                    <xsl:with-param name="les">
                                        <le>
                                            <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var"
                                                              select="func:escapeMarkupTags(pub:publication/e:publishing-info/dc:publisher/text())"/>
                                                <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                            </xsl:variable>
                                            <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                        </le>
                                    </xsl:with-param>
                                    <xsl:with-param name="delimiter" select="' '"/>
                                </xsl:call-template>
                            </xsl:variable>
                            <!--
				start-with/ends-with
			--><xsl:variable name="var">
                                <xsl:if test="exists($var) and $var!=''">
                                    <xsl:text>, </xsl:text>
                                </xsl:if>
                                <!--XXX - applyStartsWithEndsWith Copy Of 1 -XXX--><xsl:copy-of select="$var"/>
                            </xsl:variable>
                            <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                        </xsl:variable>
                        <xsl:variable name="publisher-place"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                            <xsl:variable name="var">
                                <xsl:call-template name="applyDelimiter">
                                    <xsl:with-param name="les">
                                        <le>
                                            <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var"
                                                              select="func:escapeMarkupTags(pub:publication/e:publishing-info/dc:publisher/text())"/>
                                                <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                            </xsl:variable>
                                            <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                        </le>
                                        <le>
                                            <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var"
                                                              select="func:escapeMarkupTags(pub:publication/e:publishing-info/e:place/text())"/>
                                                <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                            </xsl:variable>
                                            <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                        </le>
                                    </xsl:with-param>
                                    <xsl:with-param name="delimiter" select="', '"/>
                                </xsl:call-template>
                            </xsl:variable>
                            <!--
				start-with/ends-with
			--><xsl:variable name="var">
                                <xsl:if test="exists($var) and $var!=''">
                                    <xsl:text>, </xsl:text>
                                </xsl:if>
                                <!--XXX - applyStartsWithEndsWith Copy Of 1 -XXX--><xsl:copy-of select="$var"/>
                            </xsl:variable>
                            <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                        </xsl:variable>
                        <xsl:variable name="source-publisher-place"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                            <xsl:variable name="var">
                                <xsl:call-template name="applyDelimiter">
                                    <xsl:with-param name="les">
                                        <le>
                                            <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var"
                                                              select="func:escapeMarkupTags(pub:publication/source:source[1]/e:publishing-info/dc:publisher/text())"/>
                                                <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                            </xsl:variable>
                                            <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                        </le>
                                        <le>
                                            <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var"
                                                              select="func:escapeMarkupTags(pub:publication/source:source[1]/e:publishing-info/e:place/text())"/>
                                                <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                            </xsl:variable>
                                            <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                        </le>
                                    </xsl:with-param>
                                    <xsl:with-param name="delimiter" select="', '"/>
                                </xsl:call-template>
                            </xsl:variable>
                            <!--
				start-with/ends-with
			--><xsl:variable name="var">
                                <xsl:if test="exists($var) and $var!=''">
                                    <xsl:text>, </xsl:text>
                                </xsl:if>
                                <!--XXX - applyStartsWithEndsWith Copy Of 1 -XXX--><xsl:copy-of select="$var"/>
                            </xsl:variable>
                            <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                        </xsl:variable>
                        <xsl:variable name="publisher-place-since"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                            <xsl:variable name="var">
                                <xsl:call-template name="applyDelimiter">
                                    <xsl:with-param name="les">
                                        <le>
                                            <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var"
                                                              select="func:escapeMarkupTags(pub:publication/e:publishing-info/dc:publisher/text())"/>
                                                <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                            </xsl:variable>
                                            <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                        </le>
                                        <le>
                                            <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                                                <!--valid-if--><xsl:variable name="var">
                                                    <xsl:if test="&#xA;&#x9;&#x9;&#x9;&#x9;&#x9;&#x9;&#xA;&#x9;&#x9;&#x9;&#x9;&#x9;&#x9;contains(pub:publication/e:publishing-info/e:place/text(),'since') or &#xA;&#x9;&#x9;&#x9;&#x9;&#x9;&#x9;contains(pub:publication/e:publishing-info/e:place/text(),'seit') &#xA;&#x9;&#x9;&#x9;&#x9;&#x9;&#x9;&#xA;&#x9;&#x9;&#x9;&#x9;&#x9;&#x9;">
                                                        <xsl:variable name="var">
                                                            <xsl:call-template name="applyDelimiter">
                                                                <xsl:with-param name="les">
                                                                    <le>
                                                                        <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var"
                                                                                          select="func:escapeMarkupTags(func:substringBeforeSince(pub:publication/e:publishing-info/e:place/text()))"/>
                                                                            <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                                                        </xsl:variable>
                                                                        <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                                    </le>
                                                                    <le>
                                                                        <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$since-prefix"/>
                                                                            <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                                                        </xsl:variable>
                                                                        <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                                    </le>
                                                                    <le>
                                                                        <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var"
                                                                                          select="func:escapeMarkupTags(func:substringAfterSince(pub:publication/e:publishing-info/e:place/text()))"/>
                                                                            <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                                                        </xsl:variable>
                                                                        <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                                    </le>
                                                                </xsl:with-param>
                                                                <xsl:with-param name="delimiter" select="' '"/>
                                                            </xsl:call-template>
                                                        </xsl:variable>
                                                        <!--XXX - Apply params Copy Of 1 -XXX--><xsl:copy-of select="$var"/>
                                                    </xsl:if>
                                                </xsl:variable>
                                                <!--XXX - Apply params Copy Of 2 -XXX--><xsl:copy-of select="$var"/>
                                            </xsl:variable>
                                            <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                        </le>
                                        <le>
                                            <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                                                <!--valid-if--><xsl:variable name="var">
                                                    <xsl:if test="&#xA;&#x9;&#x9;&#x9;&#x9;&#x9;&#x9;&#xA;&#x9;&#x9;&#x9;&#x9;&#x9;&#x9;not (contains(pub:publication/e:publishing-info/e:place/text(),'since')) and &#xA;&#x9;&#x9;&#x9;&#x9;&#x9;&#x9;not (contains(pub:publication/e:publishing-info/e:place/text(),'seit')) &#xA;&#x9;&#x9;&#x9;&#x9;&#x9;&#x9;&#xA;&#x9;&#x9;&#x9;&#x9;&#x9;&#x9;">
                                                        <xsl:variable name="var">
                                                            <xsl:call-template name="applyDelimiter">
                                                                <xsl:with-param name="les">
                                                                    <le>
                                                                        <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var"
                                                                                          select="func:escapeMarkupTags(pub:publication/e:publishing-info/e:place/text())"/>
                                                                            <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                                                        </xsl:variable>
                                                                        <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                                    </le>
                                                                </xsl:with-param>
                                                                <xsl:with-param name="delimiter" select="' '"/>
                                                            </xsl:call-template>
                                                        </xsl:variable>
                                                        <!--XXX - Apply params Copy Of 1 -XXX--><xsl:copy-of select="$var"/>
                                                    </xsl:if>
                                                </xsl:variable>
                                                <!--XXX - Apply params Copy Of 2 -XXX--><xsl:copy-of select="$var"/>
                                            </xsl:variable>
                                            <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                        </le>
                                    </xsl:with-param>
                                    <xsl:with-param name="delimiter" select="', '"/>
                                </xsl:call-template>
                            </xsl:variable>
                            <!--
				start-with/ends-with
			--><xsl:variable name="var">
                                <xsl:if test="exists($var) and $var!=''">
                                    <xsl:text>, </xsl:text>
                                </xsl:if>
                                <!--XXX - applyStartsWithEndsWith Copy Of 1 -XXX--><xsl:copy-of select="$var"/>
                            </xsl:variable>
                            <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                        </xsl:variable>
                        <xsl:variable name="pages"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                            <!--valid-if--><xsl:variable name="var">
                                <xsl:if test="exists(pub:publication/e:total-number-of-pages/text())">
                                    <xsl:variable name="var">
                                        <xsl:call-template name="applyDelimiter">
                                            <xsl:with-param name="les">
                                                <le>
                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var"
                                                                      select="func:escapeMarkupTags(pub:publication/e:total-number-of-pages/text())"/>
                                                        <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                                    </xsl:variable>
                                                    <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                </le>
                                                <le>
                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$pages-postfix"/>
                                                        <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                                    </xsl:variable>
                                                    <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                </le>
                                            </xsl:with-param>
                                            <xsl:with-param name="delimiter" select="' '"/>
                                        </xsl:call-template>
                                    </xsl:variable>
                                    <!--
				start-with/ends-with
			--><xsl:variable name="var">
                                        <xsl:if test="exists($var) and $var!=''">
                                            <xsl:text>, </xsl:text>
                                        </xsl:if>
                                        <!--XXX - applyStartsWithEndsWith Copy Of 1 -XXX--><xsl:copy-of select="$var"/>
                                    </xsl:variable>
                                    <!--XXX - Apply params Copy Of 1 -XXX--><xsl:copy-of select="$var"/>
                                </xsl:if>
                            </xsl:variable>
                            <!--XXX - Apply params Copy Of 2 -XXX--><xsl:copy-of select="$var"/>
                        </xsl:variable>
                        <xsl:variable name="start-end-page"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                            <xsl:variable name="var">
                                <xsl:call-template name="applyDelimiter">
                                    <xsl:with-param name="les">
                                        <le>
                                            <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$sourceStartPage"/>
                                                <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                            </xsl:variable>
                                            <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                        </le>
                                        <le>
                                            <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                                                <!--valid-if--><xsl:variable name="var">
                                                    <xsl:if test="$sourceStartPage != $sourceEndPage">
                                                        <xsl:variable name="var">
                                                            <xsl:call-template name="applyDelimiter">
                                                                <xsl:with-param name="les">
                                                                    <le>
                                                                        <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$sourceEndPage"/>
                                                                            <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                                                        </xsl:variable>
                                                                        <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                                    </le>
                                                                </xsl:with-param>
                                                                <xsl:with-param name="delimiter" select="' '"/>
                                                            </xsl:call-template>
                                                        </xsl:variable>
                                                        <!--XXX - Apply params Copy Of 1 -XXX--><xsl:copy-of select="$var"/>
                                                    </xsl:if>
                                                </xsl:variable>
                                                <!--XXX - Apply params Copy Of 2 -XXX--><xsl:copy-of select="$var"/>
                                            </xsl:variable>
                                            <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                        </le>
                                    </xsl:with-param>
                                    <xsl:with-param name="delimiter" select="' - '"/>
                                </xsl:call-template>
                            </xsl:variable>
                            <!--
				start-with/ends-with
			--><xsl:variable name="var">
                                <xsl:if test="exists($var) and $var!=''">
                                    <xsl:text>, </xsl:text>
                                </xsl:if>
                                <!--XXX - applyStartsWithEndsWith Copy Of 1 -XXX--><xsl:copy-of select="$var"/>
                            </xsl:variable>
                            <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                        </xsl:variable>
                        <xsl:variable name="volume-with-prefix"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                            <!--valid-if--><xsl:variable name="var">
                                <xsl:if test="exists(pub:publication/source:source[1]/e:volume/text())">
                                    <xsl:variable name="var">
                                        <xsl:call-template name="applyDelimiter">
                                            <xsl:with-param name="les">
                                                <le>
                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$volume-prefix"/>
                                                        <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                                    </xsl:variable>
                                                    <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                </le>
                                                <le>
                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$firstSourceVolume"/>
                                                        <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                                    </xsl:variable>
                                                    <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                </le>
                                            </xsl:with-param>
                                            <xsl:with-param name="delimiter" select="' '"/>
                                        </xsl:call-template>
                                    </xsl:variable>
                                    <!--
				start-with/ends-with
			--><xsl:variable name="var">
                                        <xsl:if test="exists($var) and $var!=''">
                                            <xsl:text>, </xsl:text>
                                        </xsl:if>
                                        <!--XXX - applyStartsWithEndsWith Copy Of 1 -XXX--><xsl:copy-of select="$var"/>
                                    </xsl:variable>
                                    <!--XXX - Apply params Copy Of 1 -XXX--><xsl:copy-of select="$var"/>
                                </xsl:if>
                            </xsl:variable>
                            <!--XXX - Apply params Copy Of 2 -XXX--><xsl:copy-of select="$var"/>
                        </xsl:variable>
                        <xsl:variable name="volume-issue-journal"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                            <xsl:variable name="var">
                                <xsl:call-template name="applyDelimiter">
                                    <xsl:with-param name="les">
                                        <le>
                                            <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                                                <!--valid-if--><xsl:variable name="var">
                                                    <xsl:if test=" exists (pub:publication/source:source[1]/e:volume/text()) &#xA;&#x9;&#x9;&#x9;&#x9;&#x9;&#x9;or exists(pub:publication/source:source[1]/e:issue/text())&#xA;&#x9;&#x9;&#x9;&#x9;&#x9;&#x9;">
                                                        <xsl:variable name="var">
                                                            <xsl:call-template name="applyDelimiter">
                                                                <xsl:with-param name="les">
                                                                    <le>
                                                                        <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$firstSourceVolume"/>
                                                                            <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                                                        </xsl:variable>
                                                                        <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                                    </le>
                                                                    <le>
                                                                        <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$firstSourceIssue"/>
                                                                            <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                                                        </xsl:variable>
                                                                        <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                                    </le>
                                                                </xsl:with-param>
                                                                <xsl:with-param name="delimiter" select="', '"/>
                                                            </xsl:call-template>
                                                        </xsl:variable>
                                                        <!--XXX - Apply params Copy Of 1 -XXX--><xsl:copy-of select="$var"/>
                                                    </xsl:if>
                                                </xsl:variable>
                                                <!--XXX - Apply params Copy Of 2 -XXX--><xsl:copy-of select="$var"/>
                                            </xsl:variable>
                                            <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                        </le>
                                    </xsl:with-param>
                                    <xsl:with-param name="delimiter" select="' '"/>
                                </xsl:call-template>
                            </xsl:variable>
                            <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                        </xsl:variable>
                        <xsl:variable name="volume-issue-online-journal"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                            <!--valid-if--><xsl:variable name="var">
                                <xsl:if test="exists(pub:publication/e:published-online/text())&#xA;&#x9;&#x9;&#x9;&#x9;&#x9;&#x9;and not(pub:publication/dcterms:issued/text())&#xA;&#x9;&#x9;&#x9;">
                                    <xsl:variable name="var">
                                        <xsl:call-template name="applyDelimiter">
                                            <xsl:with-param name="les">
                                                <le>
                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                                                        <!--valid-if--><xsl:variable name="var">
                                                            <xsl:if test=" exists (pub:publication/source:source[1]/e:volume/text()) &#xA;&#x9;&#x9;&#x9;&#x9;&#x9;&#x9;or exists(pub:publication/source:source[1]/e:issue/text())&#xA;&#x9;&#x9;&#x9;&#x9;&#x9;&#x9;">
                                                                <xsl:variable name="var">
                                                                    <xsl:call-template name="applyDelimiter">
                                                                        <xsl:with-param name="les">
                                                                            <le>
                                                                                <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$firstSourceVolume"/>
                                                                                    <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                                                                </xsl:variable>
                                                                                <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                                            </le>
                                                                            <le>
                                                                                <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$firstSourceIssue"/>
                                                                                    <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                                                                </xsl:variable>
                                                                                <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                                            </le>
                                                                        </xsl:with-param>
                                                                        <xsl:with-param name="delimiter" select="'.'"/>
                                                                    </xsl:call-template>
                                                                </xsl:variable>
                                                                <!--XXX - Apply params Copy Of 1 -XXX--><xsl:copy-of select="$var"/>
                                                            </xsl:if>
                                                        </xsl:variable>
                                                        <!--XXX - Apply params Copy Of 2 -XXX--><xsl:copy-of select="$var"/>
                                                    </xsl:variable>
                                                    <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                </le>
                                            </xsl:with-param>
                                            <xsl:with-param name="delimiter" select="' '"/>
                                        </xsl:call-template>
                                    </xsl:variable>
                                    <!--XXX - Apply params Copy Of 1 -XXX--><xsl:copy-of select="$var"/>
                                </xsl:if>
                            </xsl:variable>
                            <!--XXX - Apply params Copy Of 2 -XXX--><xsl:copy-of select="$var"/>
                        </xsl:variable>
                        <xsl:variable name="year-default-and-online-journal"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                            <xsl:variable name="var">
                                <xsl:call-template name="applyDelimiter">
                                    <xsl:with-param name="les">
                                        <le>
                                            <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                                                <!--valid-if--><xsl:variable name="var">
                                                    <xsl:if test="exists (pub:publication/source:source[1]/e:volume/text()) &#xA;&#x9;&#x9;&#x9;&#x9;&#x9;&#x9;or exists(pub:publication/source:source[1]/e:issue/text())">
                                                        <xsl:variable name="var">
                                                            <xsl:call-template name="applyDelimiter">
                                                                <xsl:with-param name="les">
                                                                    <le>
                                                                        <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$date"/>
                                                                            <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                                                        </xsl:variable>
                                                                        <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                                    </le>
                                                                </xsl:with-param>
                                                                <xsl:with-param name="delimiter" select="' '"/>
                                                            </xsl:call-template>
                                                        </xsl:variable>
                                                        <!--
				start-with/ends-with
			--><xsl:variable name="var">
                                                            <xsl:if test="exists($var) and $var!=''">
                                                                <xsl:text>(</xsl:text>
                                                            </xsl:if>
                                                            <!--XXX - applyStartsWithEndsWith Copy Of 1 -XXX--><xsl:copy-of select="$var"/>
                                                            <xsl:if test="exists($var) and $var!=''">
                                                                <xsl:text>)</xsl:text>
                                                            </xsl:if>
                                                        </xsl:variable>
                                                        <!--XXX - Apply params Copy Of 1 -XXX--><xsl:copy-of select="$var"/>
                                                    </xsl:if>
                                                </xsl:variable>
                                                <!--XXX - Apply params Copy Of 2 -XXX--><xsl:copy-of select="$var"/>
                                            </xsl:variable>
                                            <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                        </le>
                                        <le>
                                            <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                                                <!--valid-if--><xsl:variable name="var">
                                                    <xsl:if test="not(pub:publication/source:source[1]/e:volume/text()) &#xA;&#x9;&#x9;&#x9;&#x9;&#x9;&#x9;and not(pub:publication/source:source[1]/e:issue/text())">
                                                        <xsl:variable name="var">
                                                            <xsl:call-template name="applyDelimiter">
                                                                <xsl:with-param name="les">
                                                                    <le>
                                                                        <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$date"/>
                                                                            <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                                                        </xsl:variable>
                                                                        <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                                    </le>
                                                                </xsl:with-param>
                                                                <xsl:with-param name="delimiter" select="' '"/>
                                                            </xsl:call-template>
                                                        </xsl:variable>
                                                        <!--XXX - Apply params Copy Of 1 -XXX--><xsl:copy-of select="$var"/>
                                                    </xsl:if>
                                                </xsl:variable>
                                                <!--XXX - Apply params Copy Of 2 -XXX--><xsl:copy-of select="$var"/>
                                            </xsl:variable>
                                            <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                        </le>
                                    </xsl:with-param>
                                    <xsl:with-param name="delimiter" select="' '"/>
                                </xsl:call-template>
                            </xsl:variable>
                            <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                        </xsl:variable>
                        <xsl:variable name="source-title-volume"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                            <!--valid-if--><xsl:variable name="var">
                                <xsl:if test="exists(pub:publication/source:source[1])">
                                    <xsl:variable name="var">
                                        <xsl:call-template name="applyDelimiter">
                                            <xsl:with-param name="les">
                                                <le>
                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var"
                                                                      select="func:escapeMarkupTags(pub:publication/source:source[1]/dc:title/text())"/>
                                                        <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                                    </xsl:variable>
                                                    <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                </le>
                                                <le>
                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$firstSourceVolume"/>
                                                        <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                                    </xsl:variable>
                                                    <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                </le>
                                            </xsl:with-param>
                                            <xsl:with-param name="delimiter" select="', '"/>
                                        </xsl:call-template>
                                    </xsl:variable>
                                    <!--
				start-with/ends-with
			--><xsl:variable name="var">
                                        <xsl:if test="exists($var) and $var!=''">
                                            <xsl:text>(</xsl:text>
                                        </xsl:if>
                                        <!--XXX - applyStartsWithEndsWith Copy Of 1 -XXX--><xsl:copy-of select="$var"/>
                                        <xsl:if test="exists($var) and $var!=''">
                                            <xsl:text>)</xsl:text>
                                        </xsl:if>
                                    </xsl:variable>
                                    <!--XXX - Apply params Copy Of 1 -XXX--><xsl:copy-of select="$var"/>
                                </xsl:if>
                            </xsl:variable>
                            <!--XXX - Apply params Copy Of 2 -XXX--><xsl:copy-of select="$var"/>
                        </xsl:variable>
                        <xsl:variable name="source-title-volume-contr"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                            <xsl:variable name="var">
                                <xsl:call-template name="applyDelimiter">
                                    <xsl:with-param name="les">
                                        <le>
                                            <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var"
                                                              select="func:escapeMarkupTags(pub:publication/source:source[1]/dc:title/text())"/>
                                                <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                            </xsl:variable>
                                            <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                        </le>
                                        <le>
                                            <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                                                <xsl:variable name="var">
                                                    <xsl:call-template name="applyDelimiter">
                                                        <xsl:with-param name="les">
                                                            <le>
                                                                <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var"
                                                                                  select="func:escapeMarkupTags(pub:publication/source:source[2]/dc:title/text())"/>
                                                                    <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                                                </xsl:variable>
                                                                <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                            </le>
                                                            <le>
                                                                <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var"
                                                                                  select="func:escapeMarkupTags(pub:publication/source:source[2]/e:volume/text())"/>
                                                                    <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                                                </xsl:variable>
                                                                <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                            </le>
                                                        </xsl:with-param>
                                                        <xsl:with-param name="delimiter" select="', '"/>
                                                    </xsl:call-template>
                                                </xsl:variable>
                                                <!--
				start-with/ends-with
			--><xsl:variable name="var">
                                                    <xsl:if test="exists($var) and $var!=''">
                                                        <xsl:text>(</xsl:text>
                                                    </xsl:if>
                                                    <!--XXX - applyStartsWithEndsWith Copy Of 1 -XXX--><xsl:copy-of select="$var"/>
                                                    <xsl:if test="exists($var) and $var!=''">
                                                        <xsl:text>)</xsl:text>
                                                    </xsl:if>
                                                </xsl:variable>
                                                <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                            </xsl:variable>
                                            <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                        </le>
                                        <le>
                                            <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$volume-with-prefix"/>
                                                <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                            </xsl:variable>
                                            <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                        </le>
                                    </xsl:with-param>
                                    <xsl:with-param name="delimiter" select="' '"/>
                                </xsl:call-template>
                            </xsl:variable>
                            <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                        </xsl:variable>
                        <xsl:variable name="source-title-volume-paper"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                            <!--valid-if--><xsl:variable name="var">
                                <xsl:if test="exists(pub:publication/source:source[1])">
                                    <xsl:variable name="var">
                                        <xsl:call-template name="applyDelimiter">
                                            <xsl:with-param name="les">
                                                <le>
                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                                                        <!--valid-if--><xsl:variable name="var">
                                                            <xsl:if test="exists(pub:publication/source:source[1]/e:volume/text())">
                                                                <xsl:variable name="var">
                                                                    <xsl:call-template name="applyDelimiter">
                                                                        <xsl:with-param name="les">
                                                                            <le>
                                                                                <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var"
                                                                                                  select="func:escapeMarkupTags(pub:publication/source:source[1]/dc:title/text())"/>
                                                                                    <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                                                                </xsl:variable>
                                                                                <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                                            </le>
                                                                            <le>
                                                                                <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$firstSourceVolume"/>
                                                                                    <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                                                                </xsl:variable>
                                                                                <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                                            </le>
                                                                        </xsl:with-param>
                                                                        <xsl:with-param name="delimiter" select="', '"/>
                                                                    </xsl:call-template>
                                                                </xsl:variable>
                                                                <!--
				start-with/ends-with
			--><xsl:variable name="var">
                                                                    <xsl:if test="exists($var) and $var!=''">
                                                                        <xsl:text>(</xsl:text>
                                                                    </xsl:if>
                                                                    <!--XXX - applyStartsWithEndsWith Copy Of 1 -XXX--><xsl:copy-of select="$var"/>
                                                                    <xsl:if test="exists($var) and $var!=''">
                                                                        <xsl:text>), </xsl:text>
                                                                    </xsl:if>
                                                                </xsl:variable>
                                                                <!--XXX - Apply params Copy Of 1 -XXX--><xsl:copy-of select="$var"/>
                                                            </xsl:if>
                                                        </xsl:variable>
                                                        <!--XXX - Apply params Copy Of 2 -XXX--><xsl:copy-of select="$var"/>
                                                    </xsl:variable>
                                                    <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                </le>
                                                <le>
                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                                                        <!--valid-if--><xsl:variable name="var">
                                                            <xsl:if test="not(pub:publication/source:source[1]/e:volume/text())">
                                                                <xsl:variable name="var">
                                                                    <xsl:call-template name="applyDelimiter">
                                                                        <xsl:with-param name="les">
                                                                            <le>
                                                                                <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var"
                                                                                                  select="func:escapeMarkupTags(pub:publication/source:source[1]/dc:title/text())"/>
                                                                                    <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                                                                </xsl:variable>
                                                                                <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                                            </le>
                                                                        </xsl:with-param>
                                                                        <xsl:with-param name="delimiter" select="' '"/>
                                                                    </xsl:call-template>
                                                                </xsl:variable>
                                                                <!--
				start-with/ends-with
			--><xsl:variable name="var">
                                                                    <xsl:if test="exists($var) and $var!=''">
                                                                        <xsl:text>, </xsl:text>
                                                                    </xsl:if>
                                                                    <!--XXX - applyStartsWithEndsWith Copy Of 1 -XXX--><xsl:copy-of select="$var"/>
                                                                </xsl:variable>
                                                                <!--XXX - Apply params Copy Of 1 -XXX--><xsl:copy-of select="$var"/>
                                                            </xsl:if>
                                                        </xsl:variable>
                                                        <!--XXX - Apply params Copy Of 2 -XXX--><xsl:copy-of select="$var"/>
                                                    </xsl:variable>
                                                    <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                </le>
                                            </xsl:with-param>
                                            <xsl:with-param name="delimiter" select="' '"/>
                                        </xsl:call-template>
                                    </xsl:variable>
                                    <!--XXX - Apply params Copy Of 1 -XXX--><xsl:copy-of select="$var"/>
                                </xsl:if>
                            </xsl:variable>
                            <!--XXX - Apply params Copy Of 2 -XXX--><xsl:copy-of select="$var"/>
                        </xsl:variable>
                        <xsl:variable name="id-online-publication"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                            <!--valid-if--><xsl:variable name="var">
                                <xsl:if test="exists(pub:publication/e:published-online/text())&#xA;&#x9;&#x9;&#x9;&#x9;&#x9;&#x9;and not(pub:publication/dcterms:issued/text())">
                                    <xsl:variable name="var">
                                        <xsl:call-template name="applyDelimiter">
                                            <xsl:with-param name="les">
                                                <le>
                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                                                        <!--valid-if--><xsl:variable name="var">
                                                            <xsl:if test="$onlineIdType = 'DOI'">
                                                                <xsl:variable name="var">
                                                                    <xsl:call-template name="applyDelimiter">
                                                                        <xsl:with-param name="les">
                                                                            <le>
                                                                                <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$doi-prefix"/>
                                                                                    <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                                                                </xsl:variable>
                                                                                <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                                            </le>
                                                                        </xsl:with-param>
                                                                        <xsl:with-param name="delimiter" select="' '"/>
                                                                    </xsl:call-template>
                                                                </xsl:variable>
                                                                <!--XXX - Apply params Copy Of 1 -XXX--><xsl:copy-of select="$var"/>
                                                            </xsl:if>
                                                        </xsl:variable>
                                                        <!--XXX - Apply params Copy Of 2 -XXX--><xsl:copy-of select="$var"/>
                                                    </xsl:variable>
                                                    <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                </le>
                                                <le>
                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$onlineIdValue"/>
                                                        <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                                    </xsl:variable>
                                                    <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                </le>
                                            </xsl:with-param>
                                            <xsl:with-param name="delimiter" select="' '"/>
                                        </xsl:call-template>
                                    </xsl:variable>
                                    <!--
				start-with/ends-with
			--><xsl:variable name="var">
                                        <xsl:if test="exists($var) and $var!=''">
                                            <xsl:text>, </xsl:text>
                                        </xsl:if>
                                        <!--XXX - applyStartsWithEndsWith Copy Of 1 -XXX--><xsl:copy-of select="$var"/>
                                        <xsl:if test="exists($var) and $var!=''">
                                            <xsl:text>, </xsl:text>
                                        </xsl:if>
                                    </xsl:variable>
                                    <!--XXX - Apply params Copy Of 1 -XXX--><xsl:copy-of select="$var"/>
                                </xsl:if>
                            </xsl:variable>
                            <!--XXX - Apply params Copy Of 2 -XXX--><xsl:copy-of select="$var"/>
                        </xsl:variable>
                        <xsl:variable name="id-online-publication-paper"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                            <!--valid-if--><xsl:variable name="var">
                                <xsl:if test="exists(pub:publication/e:published-online/text())&#xA;&#x9;&#x9;&#x9;&#x9;&#x9;&#x9;and not(pub:publication/dcterms:issued/text())">
                                    <xsl:variable name="var">
                                        <xsl:call-template name="applyDelimiter">
                                            <xsl:with-param name="les">
                                                <le>
                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$onlineIdValuePaper"/>
                                                        <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                                    </xsl:variable>
                                                    <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                </le>
                                                <le>
                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                                                        <!--valid-if--><xsl:variable name="var">
                                                            <xsl:if test="exists(pub:publication/e:published-online/text())&#xA;&#x9;&#x9;&#x9;&#x9;&#x9;&#x9;and not(pub:publication/dcterms:issued/text())">
                                                                <xsl:variable name="var">
                                                                    <xsl:call-template name="applyDelimiter">
                                                                        <xsl:with-param name="les">
                                                                            <le>
                                                                                <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var"
                                                                                                  select="func:escapeMarkupTags(func:get_reverse_date(pub:publication/e:published-online/text()))"/>
                                                                                    <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                                                                </xsl:variable>
                                                                                <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                                            </le>
                                                                        </xsl:with-param>
                                                                        <xsl:with-param name="delimiter" select="' '"/>
                                                                    </xsl:call-template>
                                                                </xsl:variable>
                                                                <!--XXX - Apply params Copy Of 1 -XXX--><xsl:copy-of select="$var"/>
                                                            </xsl:if>
                                                        </xsl:variable>
                                                        <!--XXX - Apply params Copy Of 2 -XXX--><xsl:copy-of select="$var"/>
                                                    </xsl:variable>
                                                    <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                </le>
                                            </xsl:with-param>
                                            <xsl:with-param name="delimiter" select="', '"/>
                                        </xsl:call-template>
                                    </xsl:variable>
                                    <!--
				start-with/ends-with
			--><xsl:variable name="var">
                                        <xsl:if test="exists($var) and $var!=''">
                                            <xsl:text>, </xsl:text>
                                        </xsl:if>
                                        <!--XXX - applyStartsWithEndsWith Copy Of 1 -XXX--><xsl:copy-of select="$var"/>
                                    </xsl:variable>
                                    <!--XXX - Apply params Copy Of 1 -XXX--><xsl:copy-of select="$var"/>
                                </xsl:if>
                            </xsl:variable>
                            <!--XXX - Apply params Copy Of 2 -XXX--><xsl:copy-of select="$var"/>
                        </xsl:variable>
                        <xsl:variable name="title"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                            <xsl:variable name="var">
                                <xsl:call-template name="applyDelimiter">
                                    <xsl:with-param name="les">
                                        <le>
                                            <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="func:escapeMarkupTags(pub:publication/dc:title/text())"/>
                                                <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                            </xsl:variable>
                                            <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                        </le>
                                    </xsl:with-param>
                                    <xsl:with-param name="delimiter" select="' '"/>
                                </xsl:call-template>
                            </xsl:variable>
                            <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                        </xsl:variable>
                        <xsl:variable name="source-title"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                            <xsl:variable name="var">
                                <xsl:call-template name="applyDelimiter">
                                    <xsl:with-param name="les">
                                        <le>
                                            <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var"
                                                              select="func:escapeMarkupTags(pub:publication/source:source[1]/dc:title/text())"/>
                                                <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                            </xsl:variable>
                                            <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                        </le>
                                    </xsl:with-param>
                                    <xsl:with-param name="delimiter" select="' '"/>
                                </xsl:call-template>
                            </xsl:variable>
                            <!--
				start-with/ends-with
			--><xsl:variable name="var">
                                <xsl:if test="exists($var) and $var!=''">
                                    <xsl:text>, </xsl:text>
                                </xsl:if>
                                <!--XXX - applyStartsWithEndsWith Copy Of 1 -XXX--><xsl:copy-of select="$var"/>
                            </xsl:variable>
                            <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                        </xsl:variable>
                        <xsl:variable name="source-abbTitle"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                            <xsl:variable name="var">
                                <xsl:call-template name="applyDelimiter">
                                    <xsl:with-param name="les">
                                        <le>
                                            <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var"
                                                              select="func:escapeMarkupTags(pub:publication/source:source[1]/dcterms:alternative[@xsi:type='eterms:ABBREVIATION'][1]/text())"/>
                                                <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                            </xsl:variable>
                                            <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                        </le>
                                    </xsl:with-param>
                                    <xsl:with-param name="delimiter" select="' '"/>
                                </xsl:call-template>
                            </xsl:variable>
                            <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                        </xsl:variable>
                        <xsl:variable name="source-abbTitle-or-normalTitle"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                            <xsl:variable name="var">
                                <xsl:call-template name="applyDelimiter">
                                    <xsl:with-param name="les">
                                        <le>
                                            <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$source-abbTitle"/>
                                                <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                            </xsl:variable>
                                            <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                        </le>
                                        <le>
                                            <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                                                <!--valid-if--><xsl:variable name="var">
                                                    <xsl:if test="&#xA;&#x9;&#x9;&#x9;&#x9;&#x9;not(pub:publication/source:source[1]/dcterms:alternative[@xsi:type='eterms:ABBREVIATION'][1]/text())&#xA;&#x9;&#x9;&#x9;&#x9;&#x9;">
                                                        <xsl:variable name="var">
                                                            <xsl:call-template name="applyDelimiter">
                                                                <xsl:with-param name="les">
                                                                    <le>
                                                                        <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$source-title"/>
                                                                            <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                                                        </xsl:variable>
                                                                        <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                                    </le>
                                                                </xsl:with-param>
                                                                <xsl:with-param name="delimiter" select="' '"/>
                                                            </xsl:call-template>
                                                        </xsl:variable>
                                                        <!--XXX - Apply params Copy Of 1 -XXX--><xsl:copy-of select="$var"/>
                                                    </xsl:if>
                                                </xsl:variable>
                                                <!--XXX - Apply params Copy Of 2 -XXX--><xsl:copy-of select="$var"/>
                                            </xsl:variable>
                                            <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                        </le>
                                    </xsl:with-param>
                                    <xsl:with-param name="delimiter" select="' '"/>
                                </xsl:call-template>
                            </xsl:variable>
                            <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                        </xsl:variable>
                        <xsl:variable name="legal-case"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                            <xsl:variable name="var">
                                <xsl:call-template name="applyDelimiter">
                                    <xsl:with-param name="les">
                                        <le>
                                            <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="func:escapeMarkupTags(pub:publication/dc:title/text())"/>
                                                <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                            </xsl:variable>
                                            <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                        </le>
                                        <le>
                                            <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                                                <!--valid-if--><xsl:variable name="var">
                                                    <xsl:if test="&#xA;&#x9;&#x9;&#x9;&#x9;&#x9;&#x9;&#x9;not(normalize-space(pub:publication/dc:title/text()) = 'Anmerkung zu' or normalize-space(pub:publication/dc:title/text()) = 'Besprechung von')&#xA;&#x9;&#x9;&#x9;&#x9;&#x9;&#x9;">
                                                        <xsl:variable name="var">
                                                            <xsl:call-template name="applyDelimiter">
                                                                <xsl:with-param name="les">
                                                                    <le>
                                                                        <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var"
                                                                                          select="func:escapeMarkupTags(pub:publication/legalCase:legal-case/e:court/text())"/>
                                                                            <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                                                        </xsl:variable>
                                                                        <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                                    </le>
                                                                </xsl:with-param>
                                                                <xsl:with-param name="delimiter" select="' '"/>
                                                            </xsl:call-template>
                                                        </xsl:variable>
                                                        <!--
				start-with/ends-with
			--><xsl:variable name="var">
                                                            <xsl:if test="exists($var) and $var!=''">
                                                                <xsl:text>, </xsl:text>
                                                            </xsl:if>
                                                            <!--XXX - applyStartsWithEndsWith Copy Of 1 -XXX--><xsl:copy-of select="$var"/>
                                                            <xsl:if test="exists($var) and $var!=''">
                                                                <xsl:text>, </xsl:text>
                                                            </xsl:if>
                                                        </xsl:variable>
                                                        <!--XXX - Apply params Copy Of 1 -XXX--><xsl:copy-of select="$var"/>
                                                    </xsl:if>
                                                </xsl:variable>
                                                <!--XXX - Apply params Copy Of 2 -XXX--><xsl:copy-of select="$var"/>
                                            </xsl:variable>
                                            <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                        </le>
                                        <le>
                                            <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                                                <!--valid-if--><xsl:variable name="var">
                                                    <xsl:if test="&#xA;&#x9;&#x9;&#x9;&#x9;&#x9;&#x9;&#x9;normalize-space(pub:publication/dc:title/text()) = 'Anmerkung zu' or normalize-space(pub:publication/dc:title/text()) = 'Besprechung von'&#xA;&#x9;&#x9;&#x9;&#x9;&#x9;&#x9;">
                                                        <xsl:variable name="var">
                                                            <xsl:call-template name="applyDelimiter">
                                                                <xsl:with-param name="les">
                                                                    <le>
                                                                        <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var"
                                                                                          select="func:escapeMarkupTags(pub:publication/legalCase:legal-case/e:court/text())"/>
                                                                            <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                                                        </xsl:variable>
                                                                        <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                                    </le>
                                                                </xsl:with-param>
                                                                <xsl:with-param name="delimiter" select="' '"/>
                                                            </xsl:call-template>
                                                        </xsl:variable>
                                                        <!--
				start-with/ends-with
			--><xsl:variable name="var"><!--XXX - applyStartsWithEndsWith Copy Of 1 -XXX--><xsl:copy-of select="$var"/>
                                                            <xsl:if test="exists($var) and $var!=''">
                                                                <xsl:text>, </xsl:text>
                                                            </xsl:if>
                                                        </xsl:variable>
                                                        <!--XXX - Apply params Copy Of 1 -XXX--><xsl:copy-of select="$var"/>
                                                    </xsl:if>
                                                </xsl:variable>
                                                <!--XXX - Apply params Copy Of 2 -XXX--><xsl:copy-of select="$var"/>
                                            </xsl:variable>
                                            <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                        </le>
                                        <le>
                                            <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var"
                                                              select="func:escapeMarkupTags(func:get_reverse_date(pub:publication/legalCase:legal-case/dcterms:issued/text()))"/>
                                                <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                            </xsl:variable>
                                            <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                        </le>
                                        <le>
                                            <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                                                <xsl:variable name="var">
                                                    <xsl:call-template name="applyDelimiter">
                                                        <xsl:with-param name="les">
                                                            <le>
                                                                <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var"
                                                                                  select="func:escapeMarkupTags(pub:publication/legalCase:legal-case/dc:identifier/text())"/>
                                                                    <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                                                </xsl:variable>
                                                                <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                            </le>
                                                        </xsl:with-param>
                                                        <xsl:with-param name="delimiter" select="' '"/>
                                                    </xsl:call-template>
                                                </xsl:variable>
                                                <!--
				start-with/ends-with
			--><xsl:variable name="var">
                                                    <xsl:if test="exists($var) and $var!=''">
                                                        <xsl:text> - </xsl:text>
                                                    </xsl:if>
                                                    <!--XXX - applyStartsWithEndsWith Copy Of 1 -XXX--><xsl:copy-of select="$var"/>
                                                </xsl:variable>
                                                <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                            </xsl:variable>
                                            <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                        </le>
                                        <le>
                                            <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var"
                                                              select="func:escapeMarkupTags(pub:publication/legalCase:legal-case/dc:title/text())"/>
                                                <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                            </xsl:variable>
                                            <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                        </le>
                                    </xsl:with-param>
                                    <xsl:with-param name="delimiter" select="' '"/>
                                </xsl:call-template>
                            </xsl:variable>
                            <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                        </xsl:variable>
                        <xsl:variable name="degree"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                            <xsl:variable name="var">
                                <xsl:call-template name="applyDelimiter">
                                    <xsl:with-param name="les">
                                        <le>
                                            <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                                                <!--valid-if--><xsl:variable name="var">
                                                    <xsl:if test="$l_jus_degree = 'Bachelor' &#xA;&#x9;&#x9;&#x9;&#x9;&#x9;&#x9;">
                                                        <xsl:variable name="var">
                                                            <xsl:call-template name="applyDelimiter">
                                                                <xsl:with-param name="les">
                                                                    <le>
                                                                        <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$l_jus_degree"/>
                                                                            <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                                                        </xsl:variable>
                                                                        <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                                    </le>
                                                                </xsl:with-param>
                                                                <xsl:with-param name="delimiter" select="' '"/>
                                                            </xsl:call-template>
                                                        </xsl:variable>
                                                        <!--XXX - Apply params Copy Of 1 -XXX--><xsl:copy-of select="$var"/>
                                                    </xsl:if>
                                                </xsl:variable>
                                                <!--XXX - Apply params Copy Of 2 -XXX--><xsl:copy-of select="$var"/>
                                            </xsl:variable>
                                            <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                        </le>
                                        <le>
                                            <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                                                <!--valid-if--><xsl:variable name="var">
                                                    <xsl:if test="$l_jus_degree = 'Diploma' &#xA;&#x9;&#x9;&#x9;&#x9;&#x9;&#x9;">
                                                        <xsl:variable name="var">
                                                            <xsl:call-template name="applyDelimiter">
                                                                <xsl:with-param name="les">
                                                                    <le>
                                                                        <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$l_jus_degree"/>
                                                                            <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                                                        </xsl:variable>
                                                                        <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                                    </le>
                                                                </xsl:with-param>
                                                                <xsl:with-param name="delimiter" select="' '"/>
                                                            </xsl:call-template>
                                                        </xsl:variable>
                                                        <!--XXX - Apply params Copy Of 1 -XXX--><xsl:copy-of select="$var"/>
                                                    </xsl:if>
                                                </xsl:variable>
                                                <!--XXX - Apply params Copy Of 2 -XXX--><xsl:copy-of select="$var"/>
                                            </xsl:variable>
                                            <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                        </le>
                                        <le>
                                            <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                                                <!--valid-if--><xsl:variable name="var">
                                                    <xsl:if test="$l_jus_degree = 'Habilitation' &#xA;&#x9;&#x9;&#x9;&#x9;&#x9;&#x9;">
                                                        <xsl:variable name="var">
                                                            <xsl:call-template name="applyDelimiter">
                                                                <xsl:with-param name="les">
                                                                    <le>
                                                                        <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$l_jus_degree"/>
                                                                            <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                                                        </xsl:variable>
                                                                        <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                                    </le>
                                                                </xsl:with-param>
                                                                <xsl:with-param name="delimiter" select="' '"/>
                                                            </xsl:call-template>
                                                        </xsl:variable>
                                                        <!--XXX - Apply params Copy Of 1 -XXX--><xsl:copy-of select="$var"/>
                                                    </xsl:if>
                                                </xsl:variable>
                                                <!--XXX - Apply params Copy Of 2 -XXX--><xsl:copy-of select="$var"/>
                                            </xsl:variable>
                                            <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                        </le>
                                        <le>
                                            <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                                                <!--valid-if--><xsl:variable name="var">
                                                    <xsl:if test="$l_jus_degree = 'Master' &#xA;&#x9;&#x9;&#x9;&#x9;&#x9;&#x9;">
                                                        <xsl:variable name="var">
                                                            <xsl:call-template name="applyDelimiter">
                                                                <xsl:with-param name="les">
                                                                    <le>
                                                                        <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$l_jus_degree"/>
                                                                            <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                                                        </xsl:variable>
                                                                        <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                                    </le>
                                                                </xsl:with-param>
                                                                <xsl:with-param name="delimiter" select="' '"/>
                                                            </xsl:call-template>
                                                        </xsl:variable>
                                                        <!--XXX - Apply params Copy Of 1 -XXX--><xsl:copy-of select="$var"/>
                                                    </xsl:if>
                                                </xsl:variable>
                                                <!--XXX - Apply params Copy Of 2 -XXX--><xsl:copy-of select="$var"/>
                                            </xsl:variable>
                                            <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                        </le>
                                        <le>
                                            <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                                                <!--valid-if--><xsl:variable name="var">
                                                    <xsl:if test="$l_jus_degree = 'Magister' &#xA;&#x9;&#x9;&#x9;&#x9;&#x9;&#x9;">
                                                        <xsl:variable name="var">
                                                            <xsl:call-template name="applyDelimiter">
                                                                <xsl:with-param name="les">
                                                                    <le>
                                                                        <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$l_jus_degree"/>
                                                                            <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                                                        </xsl:variable>
                                                                        <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                                    </le>
                                                                </xsl:with-param>
                                                                <xsl:with-param name="delimiter" select="' '"/>
                                                            </xsl:call-template>
                                                        </xsl:variable>
                                                        <!--XXX - Apply params Copy Of 1 -XXX--><xsl:copy-of select="$var"/>
                                                    </xsl:if>
                                                </xsl:variable>
                                                <!--XXX - Apply params Copy Of 2 -XXX--><xsl:copy-of select="$var"/>
                                            </xsl:variable>
                                            <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                        </le>
                                        <le>
                                            <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                                                <!--valid-if--><xsl:variable name="var">
                                                    <xsl:if test="$l_jus_degree = 'Dissertation' &#xA;&#x9;&#x9;&#x9;&#x9;&#x9;&#x9;">
                                                        <xsl:variable name="var">
                                                            <xsl:call-template name="applyDelimiter">
                                                                <xsl:with-param name="les">
                                                                    <le>
                                                                        <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$l_jus_degree"/>
                                                                            <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                                                        </xsl:variable>
                                                                        <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                                    </le>
                                                                </xsl:with-param>
                                                                <xsl:with-param name="delimiter" select="' '"/>
                                                            </xsl:call-template>
                                                        </xsl:variable>
                                                        <!--XXX - Apply params Copy Of 1 -XXX--><xsl:copy-of select="$var"/>
                                                    </xsl:if>
                                                </xsl:variable>
                                                <!--XXX - Apply params Copy Of 2 -XXX--><xsl:copy-of select="$var"/>
                                            </xsl:variable>
                                            <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                        </le>
                                        <le>
                                            <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                                                <!--valid-if--><xsl:variable name="var">
                                                    <xsl:if test="$l_jus_degree = 'State examination' &#xA;&#x9;&#x9;&#x9;&#x9;&#x9;&#x9;">
                                                        <xsl:variable name="var">
                                                            <xsl:call-template name="applyDelimiter">
                                                                <xsl:with-param name="les">
                                                                    <le>
                                                                        <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$l_jus_degree"/>
                                                                            <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                                                        </xsl:variable>
                                                                        <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                                    </le>
                                                                </xsl:with-param>
                                                                <xsl:with-param name="delimiter" select="' '"/>
                                                            </xsl:call-template>
                                                        </xsl:variable>
                                                        <!--XXX - Apply params Copy Of 1 -XXX--><xsl:copy-of select="$var"/>
                                                    </xsl:if>
                                                </xsl:variable>
                                                <!--XXX - Apply params Copy Of 2 -XXX--><xsl:copy-of select="$var"/>
                                            </xsl:variable>
                                            <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                        </le>
                                    </xsl:with-param>
                                    <xsl:with-param name="delimiter" select="' '"/>
                                </xsl:call-template>
                            </xsl:variable>
                            <!--
				start-with/ends-with
			--><xsl:variable name="var">
                                <xsl:if test="exists($var) and $var!=''">
                                    <xsl:text>, </xsl:text>
                                </xsl:if>
                                <!--XXX - applyStartsWithEndsWith Copy Of 1 -XXX--><xsl:copy-of select="$var"/>
                            </xsl:variable>
                            <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                        </xsl:variable>
                        <xsl:variable name="get-journal-citation-style"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                            <!--valid-if--><xsl:variable name="var">
                                <xsl:if test="&#xA;&#x9;&#x9;&#x9;&#x9;$sourceGenre = $l_journal &#xA;&#x9;&#x9;&#x9;&#x9; &#xA;&#x9;&#x9;&#x9;">
                                    <xsl:variable name="var">
                                        <xsl:call-template name="applyDelimiter">
                                            <xsl:with-param name="les">
                                                <le>
                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                                                        <xsl:variable name="var">
                                                            <xsl:call-template name="applyDelimiter">
                                                                <xsl:with-param name="les">
                                                                    <le>
                                                                        <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$source-title"/>
                                                                            <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                                                        </xsl:variable>
                                                                        <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                                    </le>
                                                                    <le>
                                                                        <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                                                                            <!--valid-if--><xsl:variable name="var">
                                                                                <xsl:if test="&#xA;&#x9;&#x9;&#x9;&#x9;&#x9;&#x9;&#x9;&#x9;&#x9;$source-abbTitle != ''&#xA;&#x9;&#x9;&#x9;&#x9;&#x9;&#x9;&#x9;&#x9;">
                                                                                    <xsl:variable name="var">
                                                                                        <xsl:call-template name="applyDelimiter">
                                                                                            <xsl:with-param name="les">
                                                                                                <le>
                                                                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$source-abbTitle"/>
                                                                                                        <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                                                                                    </xsl:variable>
                                                                                                    <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                                                                </le>
                                                                                            </xsl:with-param>
                                                                                            <xsl:with-param name="delimiter" select="' '"/>
                                                                                        </xsl:call-template>
                                                                                    </xsl:variable>
                                                                                    <!--
				start-with/ends-with
			--><xsl:variable name="var">
                                                                                        <xsl:if test="exists($var) and $var!=''">
                                                                                            <xsl:text>[</xsl:text>
                                                                                        </xsl:if>
                                                                                        <!--XXX - applyStartsWithEndsWith Copy Of 1 -XXX--><xsl:copy-of select="$var"/>
                                                                                        <xsl:if test="exists($var) and $var!=''">
                                                                                            <xsl:text>]</xsl:text>
                                                                                        </xsl:if>
                                                                                    </xsl:variable>
                                                                                    <!--XXX - Apply params Copy Of 1 -XXX--><xsl:copy-of select="$var"/>
                                                                                </xsl:if>
                                                                            </xsl:variable>
                                                                            <!--XXX - Apply params Copy Of 2 -XXX--><xsl:copy-of select="$var"/>
                                                                        </xsl:variable>
                                                                        <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                                    </le>
                                                                    <le>
                                                                        <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$volume-issue-journal"/>
                                                                            <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                                                        </xsl:variable>
                                                                        <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                                    </le>
                                                                    <le>
                                                                        <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$year-default-and-online-journal"/>
                                                                            <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                                                        </xsl:variable>
                                                                        <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                                    </le>
                                                                </xsl:with-param>
                                                                <xsl:with-param name="delimiter" select="' '"/>
                                                            </xsl:call-template>
                                                        </xsl:variable>
                                                        <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                                    </xsl:variable>
                                                    <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                </le>
                                                <le>
                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$start-end-page"/>
                                                        <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                                    </xsl:variable>
                                                    <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                </le>
                                            </xsl:with-param>
                                            <xsl:with-param name="delimiter" select="' '"/>
                                        </xsl:call-template>
                                    </xsl:variable>
                                    <!--XXX - Apply params Copy Of 1 -XXX--><xsl:copy-of select="$var"/>
                                </xsl:if>
                            </xsl:variable>
                            <!--XXX - Apply params Copy Of 2 -XXX--><xsl:copy-of select="$var"/>
                        </xsl:variable>
                        <!--### End of Predefined Layout Elements ###-->
	<!--### Citation Style Layout Definitions ###-->
	<xsl:choose>
                            <xsl:when test="&#xA;&#x9;&#x9;&#x9;$genre = $l_article or&#xA;&#x9;&#x9;&#x9;$genre = $l_case-note or&#xA;&#x9;&#x9;&#x9;$genre = $l_book-review or&#xA;&#x9;&#x9;&#x9;$genre = $l_case-study or&#xA;&#x9;&#x9;&#x9;$genre = $l_editorial or&#xA;&#x9;&#x9;&#x9;($genre = $l_conference-paper and $sourceGenre = $l_journal) or&#xA;&#x9;&#x9;&#x9;($genre = $l_meeting-abstract and $sourceGenre = $l_journal) or&#xA;&#x9;&#x9;&#x9;($genre = $l_conference-report and $sourceGenre = $l_journal)&#xA;&#x9;&#x9;&#x9;&#xA;&#x9;&#x9;&#x9;">
                                <xsl:variable name="journal-article-and-similar"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                                    <xsl:variable name="var">
                                        <xsl:call-template name="applyDelimiter">
                                            <xsl:with-param name="les">
                                                <le>
                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                                                        <!--valid-if--><xsl:variable name="var">
                                                            <xsl:if test="&#xA;&#x9;&#x9;&#x9;&#x9;&#x9;&#x9;&#xA;&#x9;&#x9;&#x9;&#x9;&#x9;&#x9;contains(pub:publication/dc:title/text(),'Review of:') and&#xA;&#x9;&#x9;&#x9;&#x9;&#x9;&#x9;$genre != $l_case-note&#xA;&#x9;&#x9;&#x9;&#x9;&#x9;&#x9;&#xA;&#x9;&#x9;&#x9;&#x9;&#x9;&#x9;">
                                                                <xsl:variable name="var">
                                                                    <xsl:call-template name="applyDelimiter">
                                                                        <xsl:with-param name="les">
                                                                            <le>
                                                                                <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                                                                                    <xsl:variable name="var">
                                                                                        <xsl:call-template name="applyDelimiter">
                                                                                            <xsl:with-param name="les">
                                                                                                <le>
                                                                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$reviewOf-prefix"/>
                                                                                                        <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                                                                                    </xsl:variable>
                                                                                                    <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                                                                </le>
                                                                                            </xsl:with-param>
                                                                                            <xsl:with-param name="delimiter" select="' '"/>
                                                                                        </xsl:call-template>
                                                                                    </xsl:variable>
                                                                                    <!--font-style--><xsl:variable name="var">
                                                                                        <xsl:if test="exists($var) and $var!=''">&lt;span class="Italic"&gt;<!--XXX - Font Style copy Of -XXX--><xsl:copy-of select="$var"/>&lt;/span&gt;</xsl:if>
                                                                                    </xsl:variable>
                                                                                    <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                                                                </xsl:variable>
                                                                                <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                                            </le>
                                                                            <le>
                                                                                <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var"
                                                                                                  select="func:escapeMarkupTags(func:substringAfterReviewOf(pub:publication/dc:title/text()))"/>
                                                                                    <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                                                                </xsl:variable>
                                                                                <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                                            </le>
                                                                        </xsl:with-param>
                                                                        <xsl:with-param name="delimiter" select="' '"/>
                                                                    </xsl:call-template>
                                                                </xsl:variable>
                                                                <!--XXX - Apply params Copy Of 1 -XXX--><xsl:copy-of select="$var"/>
                                                            </xsl:if>
                                                        </xsl:variable>
                                                        <!--XXX - Apply params Copy Of 2 -XXX--><xsl:copy-of select="$var"/>
                                                    </xsl:variable>
                                                    <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                </le>
                                                <le>
                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                                                        <!--valid-if--><xsl:variable name="var">
                                                            <xsl:if test="&#xA;&#x9;&#x9;&#x9;&#x9;&#x9;&#x9;&#xA;&#x9;&#x9;&#x9;&#x9;&#x9;&#x9;not (contains(pub:publication/dc:title/text(),'Review of:')) and &#xA;&#x9;&#x9;&#x9;&#x9;&#x9;&#x9;$genre != $l_case-note &#xA;&#x9;&#x9;&#x9;&#x9;&#x9;&#x9;&#xA;&#x9;&#x9;&#x9;&#x9;&#x9;&#x9;">
                                                                <xsl:variable name="var">
                                                                    <xsl:call-template name="applyDelimiter">
                                                                        <xsl:with-param name="les">
                                                                            <le>
                                                                                <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$title"/>
                                                                                    <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                                                                </xsl:variable>
                                                                                <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                                            </le>
                                                                        </xsl:with-param>
                                                                        <xsl:with-param name="delimiter" select="' '"/>
                                                                    </xsl:call-template>
                                                                </xsl:variable>
                                                                <!--XXX - Apply params Copy Of 1 -XXX--><xsl:copy-of select="$var"/>
                                                            </xsl:if>
                                                        </xsl:variable>
                                                        <!--XXX - Apply params Copy Of 2 -XXX--><xsl:copy-of select="$var"/>
                                                    </xsl:variable>
                                                    <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                </le>
                                                <le>
                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                                                        <!--valid-if--><xsl:variable name="var">
                                                            <xsl:if test="&#xA;&#x9;&#x9;&#x9;&#x9;&#x9;&#x9;$genre = $l_case-note&#xA;&#x9;&#x9;&#x9;&#x9;&#x9;&#xA;&#x9;&#x9;&#x9;&#x9;&#x9;">
                                                                <xsl:variable name="var">
                                                                    <xsl:call-template name="applyDelimiter">
                                                                        <xsl:with-param name="les">
                                                                            <le>
                                                                                <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$legal-case"/>
                                                                                    <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                                                                </xsl:variable>
                                                                                <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                                            </le>
                                                                        </xsl:with-param>
                                                                        <xsl:with-param name="delimiter" select="' '"/>
                                                                    </xsl:call-template>
                                                                </xsl:variable>
                                                                <!--XXX - Apply params Copy Of 1 -XXX--><xsl:copy-of select="$var"/>
                                                            </xsl:if>
                                                        </xsl:variable>
                                                        <!--XXX - Apply params Copy Of 2 -XXX--><xsl:copy-of select="$var"/>
                                                    </xsl:variable>
                                                    <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                </le>
                                                <le>
                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                                                        <!--valid-if--><xsl:variable name="var">
                                                            <xsl:if test="&#xA;&#x9;&#x9;&#x9;&#x9;&#x9;&#x9;exists(pub:publication/e:published-online/text())&#xA;&#x9;&#x9;&#x9;&#x9;&#x9;&#x9;and not(pub:publication/dcterms:issued/text()) &#xA;&#x9;&#x9;&#x9;&#x9;&#x9;">
                                                                <xsl:variable name="var">
                                                                    <xsl:call-template name="applyDelimiter">
                                                                        <xsl:with-param name="les">
                                                                            <le>
                                                                                <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$source-title"/>
                                                                                    <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                                                                </xsl:variable>
                                                                                <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                                            </le>
                                                                            <le>
                                                                                <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$volume-issue-online-journal"/>
                                                                                    <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                                                                </xsl:variable>
                                                                                <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                                            </le>
                                                                            <le>
                                                                                <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$year-default-and-online-journal"/>
                                                                                    <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                                                                </xsl:variable>
                                                                                <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                                            </le>
                                                                            <le>
                                                                                <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$start-end-page"/>
                                                                                    <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                                                                </xsl:variable>
                                                                                <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                                            </le>
                                                                            <le>
                                                                                <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$id-online-publication"/>
                                                                                    <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                                                                </xsl:variable>
                                                                                <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                                            </le>
                                                                            <le>
                                                                                <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var"
                                                                                                  select="func:escapeMarkupTags(func:get_reverse_date(pub:publication/e:published-online/text()))"/>
                                                                                    <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                                                                </xsl:variable>
                                                                                <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                                            </le>
                                                                        </xsl:with-param>
                                                                        <xsl:with-param name="delimiter" select="' '"/>
                                                                    </xsl:call-template>
                                                                </xsl:variable>
                                                                <!--XXX - Apply params Copy Of 1 -XXX--><xsl:copy-of select="$var"/>
                                                            </xsl:if>
                                                        </xsl:variable>
                                                        <!--XXX - Apply params Copy Of 2 -XXX--><xsl:copy-of select="$var"/>
                                                    </xsl:variable>
                                                    <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                </le>
                                                <le>
                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                                                        <!--valid-if--><xsl:variable name="var">
                                                            <xsl:if test="&#xA;&#x9;&#x9;&#x9;&#x9;&#x9;&#x9;exists(pub:publication/dcterms:issued/text())&#xA;&#x9;&#x9;&#x9;&#x9;&#x9;">
                                                                <xsl:variable name="var">
                                                                    <xsl:call-template name="applyDelimiter">
                                                                        <xsl:with-param name="les">
                                                                            <le>
                                                                                <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$get-journal-citation-style"/>
                                                                                    <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                                                                </xsl:variable>
                                                                                <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                                            </le>
                                                                        </xsl:with-param>
                                                                        <xsl:with-param name="delimiter" select="' '"/>
                                                                    </xsl:call-template>
                                                                </xsl:variable>
                                                                <!--XXX - Apply params Copy Of 1 -XXX--><xsl:copy-of select="$var"/>
                                                            </xsl:if>
                                                        </xsl:variable>
                                                        <!--XXX - Apply params Copy Of 2 -XXX--><xsl:copy-of select="$var"/>
                                                    </xsl:variable>
                                                    <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                </le>
                                            </xsl:with-param>
                                            <xsl:with-param name="delimiter" select="' '"/>
                                        </xsl:call-template>
                                    </xsl:variable>
                                    <!--
				start-with/ends-with
			--><xsl:variable name="var"><!--XXX - applyStartsWithEndsWith Copy Of 1 -XXX--><xsl:copy-of select="$var"/>
                                        <xsl:if test="exists($var) and $var!=''">
                                            <xsl:text>.</xsl:text>
                                        </xsl:if>
                                    </xsl:variable>
                                    <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                </xsl:variable>
                                <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$journal-article-and-similar"/>
                            </xsl:when>
                            <xsl:when test="&#xA;&#x9;&#x9;&#x9;&#x9;$genre = $l_contr-to-collect-ed or&#xA;&#x9;&#x9;&#x9;&#x9;$genre = $l_contr-to-handbook or&#xA;&#x9;&#x9;&#x9;&#x9;$genre = $l_contr-to-encyclopedia or&#xA;&#x9;&#x9;&#x9;&#x9;$genre = $l_contr-to-festschrift or&#xA;&#x9;&#x9;&#x9;&#x9;( $genre = $l_conference-report and $sourceGenre = $l_proceedings)or&#xA;&#x9;&#x9;&#x9;&#x9;( $genre = $l_conference-paper and $sourceGenre = $l_monograph) or&#xA;&#x9;&#x9;&#x9;&#x9;( $genre = $l_meeting-abstract and $sourceGenre = $l_monograph) or&#xA;&#x9;&#x9;&#x9;&#x9;( $genre = $l_conference-paper and $sourceGenre = $l_collected-edition) or&#xA;&#x9;&#x9;&#x9;&#x9;( $genre = $l_meeting-abstract and $sourceGenre = $l_collected-edition) or&#xA;&#x9;&#x9;&#x9;&#x9;( $genre = $l_conference-paper and $sourceGenre = $l_proceedings) or&#xA;&#x9;&#x9;&#x9;&#x9;( $genre = $l_meeting-abstract and $sourceGenre = $l_proceedings)&#xA;&#x9;&#x9;&#x9;&#x9; &#xA;&#x9;&#x9;&#x9;&#xA;&#x9;&#x9;&#x9;">
                                <xsl:variable name="contr-to-collected-edition"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                                    <xsl:variable name="var">
                                        <xsl:call-template name="applyDelimiter">
                                            <xsl:with-param name="les">
                                                <le>
                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$title"/>
                                                        <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                                    </xsl:variable>
                                                    <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                </le>
                                                <le>
                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$in-prefix"/>
                                                        <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                                    </xsl:variable>
                                                    <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                </le>
                                                <le>
                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                                                        <!--valid-if--><xsl:variable name="var">
                                                            <xsl:if test="&#xA;&#x9;&#x9;&#x9;&#x9;&#x9;$genre != $l_contr-to-festschrift&#xA;&#x9;&#x9;&#x9;&#x9;&#x9;&#xA;&#x9;&#x9;&#x9;&#x9;">
                                                                <xsl:variable name="var">
                                                                    <xsl:call-template name="applyDelimiter">
                                                                        <xsl:with-param name="les">
                                                                            <le>
                                                                                <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$source-editors"/>
                                                                                    <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                                                                </xsl:variable>
                                                                                <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                                            </le>
                                                                        </xsl:with-param>
                                                                        <xsl:with-param name="delimiter" select="' '"/>
                                                                    </xsl:call-template>
                                                                </xsl:variable>
                                                                <!--XXX - Apply params Copy Of 1 -XXX--><xsl:copy-of select="$var"/>
                                                            </xsl:if>
                                                        </xsl:variable>
                                                        <!--XXX - Apply params Copy Of 2 -XXX--><xsl:copy-of select="$var"/>
                                                    </xsl:variable>
                                                    <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                </le>
                                                <le>
                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$source-title-volume-contr"/>
                                                        <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                                    </xsl:variable>
                                                    <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                </le>
                                                <le>
                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$edition"/>
                                                        <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                                    </xsl:variable>
                                                    <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                </le>
                                                <le>
                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$source-publisher-place"/>
                                                        <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                                    </xsl:variable>
                                                    <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                </le>
                                                <le>
                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$date"/>
                                                        <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                                    </xsl:variable>
                                                    <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                </le>
                                                <le>
                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$start-end-page"/>
                                                        <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                                    </xsl:variable>
                                                    <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                </le>
                                            </xsl:with-param>
                                            <xsl:with-param name="delimiter" select="' '"/>
                                        </xsl:call-template>
                                    </xsl:variable>
                                    <!--
				start-with/ends-with
			--><xsl:variable name="var"><!--XXX - applyStartsWithEndsWith Copy Of 1 -XXX--><xsl:copy-of select="$var"/>
                                        <xsl:if test="exists($var) and $var!=''">
                                            <xsl:text>.</xsl:text>
                                        </xsl:if>
                                    </xsl:variable>
                                    <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                </xsl:variable>
                                <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$contr-to-collected-edition"/>
                            </xsl:when>
                            <xsl:when test="&#xA;&#x9;&#x9;&#x9;&#x9;$genre = $l_monograph or&#xA;&#x9;&#x9;&#x9;&#x9;$genre = $l_commentary or&#xA;&#x9;&#x9;&#x9;&#x9;$genre = $l_collected-edition or&#xA;&#x9;&#x9;&#x9;&#x9;$genre = $l_handbook or&#xA;&#x9;&#x9;&#x9;&#x9;$genre = $l_festschrift or&#xA;&#x9;&#x9;&#x9;&#x9;$genre = $l_proceedings or&#xA;&#x9;&#x9;&#x9;&#x9;$genre = $l_issue&#xA;&#x9;&#x9;&#x9;&#x9;&#xA;&#x9;&#x9;&#x9;&#xA;&#x9;&#x9;&#x9;">
                                <xsl:variable name="monograph"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                                    <xsl:variable name="var">
                                        <xsl:call-template name="applyDelimiter">
                                            <xsl:with-param name="les">
                                                <le>
                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$title"/>
                                                        <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                                    </xsl:variable>
                                                    <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                </le>
                                                <le>
                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$source-title-volume"/>
                                                        <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                                    </xsl:variable>
                                                    <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                </le>
                                                <le>
                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$edition"/>
                                                        <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                                    </xsl:variable>
                                                    <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                </le>
                                                <le>
                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$publisher-place"/>
                                                        <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                                    </xsl:variable>
                                                    <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                </le>
                                                <le>
                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$date"/>
                                                        <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                                    </xsl:variable>
                                                    <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                </le>
                                                <le>
                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$pages"/>
                                                        <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                                    </xsl:variable>
                                                    <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                </le>
                                            </xsl:with-param>
                                            <xsl:with-param name="delimiter" select="' '"/>
                                        </xsl:call-template>
                                    </xsl:variable>
                                    <!--
				start-with/ends-with
			--><xsl:variable name="var"><!--XXX - applyStartsWithEndsWith Copy Of 1 -XXX--><xsl:copy-of select="$var"/>
                                        <xsl:if test="exists($var) and $var!=''">
                                            <xsl:text>.</xsl:text>
                                        </xsl:if>
                                    </xsl:variable>
                                    <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                </xsl:variable>
                                <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$monograph"/>
                            </xsl:when>
                            <xsl:when test="&#xA;&#x9;&#x9;&#x9;&#x9;$genre = $l_opinion&#xA;&#x9;&#x9;&#x9;&#xA;&#x9;&#x9;&#x9;">
                                <xsl:variable name="opinion"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                                    <xsl:variable name="var">
                                        <xsl:call-template name="applyDelimiter">
                                            <xsl:with-param name="les">
                                                <le>
                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$title"/>
                                                        <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                                    </xsl:variable>
                                                    <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                </le>
                                                <le>
                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$date"/>
                                                        <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                                    </xsl:variable>
                                                    <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                </le>
                                                <le>
                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$pages"/>
                                                        <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                                    </xsl:variable>
                                                    <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                </le>
                                            </xsl:with-param>
                                            <xsl:with-param name="delimiter" select="' '"/>
                                        </xsl:call-template>
                                    </xsl:variable>
                                    <!--
				start-with/ends-with
			--><xsl:variable name="var"><!--XXX - applyStartsWithEndsWith Copy Of 1 -XXX--><xsl:copy-of select="$var"/>
                                        <xsl:if test="exists($var) and $var!=''">
                                            <xsl:text>.</xsl:text>
                                        </xsl:if>
                                    </xsl:variable>
                                    <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                </xsl:variable>
                                <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$opinion"/>
                            </xsl:when>
                            <xsl:when test="&#xA;&#x9;&#x9;&#x9;&#x9;$genre = $l_newspaper-article&#xA;&#x9;&#x9;&#x9;&#xA;&#x9;&#x9;&#x9;">
                                <xsl:variable name="newspaperArticle"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                                    <xsl:variable name="var">
                                        <xsl:call-template name="applyDelimiter">
                                            <xsl:with-param name="les">
                                                <le>
                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$title"/>
                                                        <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                                    </xsl:variable>
                                                    <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                </le>
                                                <le>
                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$source-abbTitle-or-normalTitle"/>
                                                        <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                                    </xsl:variable>
                                                    <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                </le>
                                                <le>
                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var"
                                                                      select="func:escapeMarkupTags(func:get_reverse_date(pub:publication/dcterms:issued/text()))"/>
                                                        <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                                    </xsl:variable>
                                                    <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                </le>
                                                <le>
                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$start-end-page"/>
                                                        <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                                    </xsl:variable>
                                                    <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                </le>
                                            </xsl:with-param>
                                            <xsl:with-param name="delimiter" select="' '"/>
                                        </xsl:call-template>
                                    </xsl:variable>
                                    <!--
				start-with/ends-with
			--><xsl:variable name="var"><!--XXX - applyStartsWithEndsWith Copy Of 1 -XXX--><xsl:copy-of select="$var"/>
                                        <xsl:if test="exists($var) and $var!=''">
                                            <xsl:text>.</xsl:text>
                                        </xsl:if>
                                    </xsl:variable>
                                    <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                </xsl:variable>
                                <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$newspaperArticle"/>
                            </xsl:when>
                            <xsl:when test="&#xA;&#x9;&#x9;&#x9;&#x9;$genre = $l_contr-to-commentary&#xA;&#x9;&#x9;&#x9;&#xA;&#x9;&#x9;&#x9;">
                                <xsl:variable name="contr-to-commanetary"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                                    <xsl:variable name="var">
                                        <xsl:call-template name="applyDelimiter">
                                            <xsl:with-param name="les">
                                                <le>
                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$title"/>
                                                        <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                                    </xsl:variable>
                                                    <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                </le>
                                                <le>
                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$in-prefix"/>
                                                        <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                                    </xsl:variable>
                                                    <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                </le>
                                                <le>
                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$source-editors"/>
                                                        <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                                    </xsl:variable>
                                                    <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                </le>
                                                <le>
                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$source-abbTitle-or-normalTitle"/>
                                                        <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                                    </xsl:variable>
                                                    <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                </le>
                                                <le>
                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$volume-with-prefix"/>
                                                        <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                                    </xsl:variable>
                                                    <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                </le>
                                                <le>
                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$edition"/>
                                                        <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                                    </xsl:variable>
                                                    <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                </le>
                                                <le>
                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$source-publisher-place"/>
                                                        <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                                    </xsl:variable>
                                                    <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                </le>
                                                <le>
                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$date"/>
                                                        <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                                    </xsl:variable>
                                                    <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                </le>
                                                <le>
                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$start-end-page"/>
                                                        <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                                    </xsl:variable>
                                                    <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                </le>
                                            </xsl:with-param>
                                            <xsl:with-param name="delimiter" select="' '"/>
                                        </xsl:call-template>
                                    </xsl:variable>
                                    <!--
				start-with/ends-with
			--><xsl:variable name="var"><!--XXX - applyStartsWithEndsWith Copy Of 1 -XXX--><xsl:copy-of select="$var"/>
                                        <xsl:if test="exists($var) and $var!=''">
                                            <xsl:text>.</xsl:text>
                                        </xsl:if>
                                    </xsl:variable>
                                    <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                </xsl:variable>
                                <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$contr-to-commanetary"/>
                            </xsl:when>
                            <xsl:when test="&#xA;&#x9;&#x9;&#x9;&#x9;$genre = $l_thesis&#xA;&#x9;&#x9;&#x9;&#xA;&#x9;&#x9;&#x9;">
                                <xsl:variable name="thesis"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                                    <xsl:variable name="var">
                                        <xsl:call-template name="applyDelimiter">
                                            <xsl:with-param name="les">
                                                <le>
                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$title"/>
                                                        <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                                    </xsl:variable>
                                                    <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                </le>
                                                <le>
                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                                                        <!--valid-if--><xsl:variable name="var">
                                                            <xsl:if test="&#xA;&#x9;&#x9;&#x9;&#x9;&#x9;&#x9;exists(pub:publication/source:source[1])&#xA;&#x9;&#x9;&#x9;&#x9;&#x9;&#xA;&#x9;&#x9;&#x9;&#x9;&#x9;">
                                                                <xsl:variable name="var">
                                                                    <xsl:call-template name="applyDelimiter">
                                                                        <xsl:with-param name="les">
                                                                            <le>
                                                                                <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$source-title-volume"/>
                                                                                    <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                                                                </xsl:variable>
                                                                                <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                                            </le>
                                                                            <le>
                                                                                <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$edition"/>
                                                                                    <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                                                                </xsl:variable>
                                                                                <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                                            </le>
                                                                            <le>
                                                                                <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$source-publisher-place"/>
                                                                                    <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                                                                </xsl:variable>
                                                                                <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                                            </le>
                                                                            <le>
                                                                                <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$date"/>
                                                                                    <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                                                                </xsl:variable>
                                                                                <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                                            </le>
                                                                            <le>
                                                                                <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$degree"/>
                                                                                    <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                                                                </xsl:variable>
                                                                                <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                                            </le>
                                                                            <le>
                                                                                <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$publisher-without-place"/>
                                                                                    <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                                                                </xsl:variable>
                                                                                <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                                            </le>
                                                                        </xsl:with-param>
                                                                        <xsl:with-param name="delimiter" select="' '"/>
                                                                    </xsl:call-template>
                                                                </xsl:variable>
                                                                <!--XXX - Apply params Copy Of 1 -XXX--><xsl:copy-of select="$var"/>
                                                            </xsl:if>
                                                        </xsl:variable>
                                                        <!--XXX - Apply params Copy Of 2 -XXX--><xsl:copy-of select="$var"/>
                                                    </xsl:variable>
                                                    <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                </le>
                                                <le>
                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                                                        <!--valid-if--><xsl:variable name="var">
                                                            <xsl:if test="&#xA;&#x9;&#x9;&#x9;&#x9;&#x9;&#x9;not(pub:publication/source:source[1])&#xA;&#x9;&#x9;&#x9;&#x9;&#x9;&#xA;&#x9;&#x9;&#x9;&#x9;&#x9;">
                                                                <xsl:variable name="var">
                                                                    <xsl:call-template name="applyDelimiter">
                                                                        <xsl:with-param name="les">
                                                                            <le>
                                                                                <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$publisher-place"/>
                                                                                    <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                                                                </xsl:variable>
                                                                                <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                                            </le>
                                                                            <le>
                                                                                <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$date"/>
                                                                                    <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                                                                </xsl:variable>
                                                                                <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                                            </le>
                                                                            <le>
                                                                                <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$degree"/>
                                                                                    <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                                                                </xsl:variable>
                                                                                <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                                            </le>
                                                                        </xsl:with-param>
                                                                        <xsl:with-param name="delimiter" select="' '"/>
                                                                    </xsl:call-template>
                                                                </xsl:variable>
                                                                <!--XXX - Apply params Copy Of 1 -XXX--><xsl:copy-of select="$var"/>
                                                            </xsl:if>
                                                        </xsl:variable>
                                                        <!--XXX - Apply params Copy Of 2 -XXX--><xsl:copy-of select="$var"/>
                                                    </xsl:variable>
                                                    <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                </le>
                                                <le>
                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$pages"/>
                                                        <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                                    </xsl:variable>
                                                    <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                </le>
                                            </xsl:with-param>
                                            <xsl:with-param name="delimiter" select="' '"/>
                                        </xsl:call-template>
                                    </xsl:variable>
                                    <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                </xsl:variable>
                                <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$thesis"/>
                            </xsl:when>
                            <xsl:when test="&#xA;&#x9;&#x9;&#x9;&#x9;$genre = $l_series or&#xA;&#x9;&#x9;&#x9;&#x9;$genre = $l_journal&#xA;&#x9;&#x9;&#x9;&#xA;&#x9;&#x9;&#x9;">
                                <xsl:variable name="series"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                                    <xsl:variable name="var">
                                        <xsl:call-template name="applyDelimiter">
                                            <xsl:with-param name="les">
                                                <le>
                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$title"/>
                                                        <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                                    </xsl:variable>
                                                    <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                </le>
                                                <le>
                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$publisher-place-since"/>
                                                        <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                                    </xsl:variable>
                                                    <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                </le>
                                            </xsl:with-param>
                                            <xsl:with-param name="delimiter" select="' '"/>
                                        </xsl:call-template>
                                    </xsl:variable>
                                    <!--
				start-with/ends-with
			--><xsl:variable name="var"><!--XXX - applyStartsWithEndsWith Copy Of 1 -XXX--><xsl:copy-of select="$var"/>
                                        <xsl:if test="exists($var) and $var!=''">
                                            <xsl:text>.</xsl:text>
                                        </xsl:if>
                                    </xsl:variable>
                                    <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                </xsl:variable>
                                <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$series"/>
                            </xsl:when>
                            <xsl:when test="&#xA;&#x9;&#x9;&#x9;&#x9;$genre = $l_paper&#xA;&#x9;&#x9;&#x9;&#xA;&#x9;&#x9;&#x9;">
                                <xsl:variable name="paper"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                                    <xsl:variable name="var">
                                        <xsl:call-template name="applyDelimiter">
                                            <xsl:with-param name="les">
                                                <le>
                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$title"/>
                                                        <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                                    </xsl:variable>
                                                    <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                </le>
                                                <le>
                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$source-title-volume-paper"/>
                                                        <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                                    </xsl:variable>
                                                    <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                </le>
                                                <le>
                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$source-publisher-place"/>
                                                        <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                                    </xsl:variable>
                                                    <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                </le>
                                                <le>
                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$publisher-place"/>
                                                        <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                                    </xsl:variable>
                                                    <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                </le>
                                                <le>
                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                                                        <!--valid-if--><xsl:variable name="var">
                                                            <xsl:if test="exists(pub:publication/source:source[1]) or exists(pub:publication/e:publishing-info)">
                                                                <xsl:variable name="var">
                                                                    <xsl:call-template name="applyDelimiter">
                                                                        <xsl:with-param name="les">
                                                                            <le>
                                                                                <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$date"/>
                                                                                    <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                                                                </xsl:variable>
                                                                                <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                                            </le>
                                                                        </xsl:with-param>
                                                                        <xsl:with-param name="delimiter" select="' '"/>
                                                                    </xsl:call-template>
                                                                </xsl:variable>
                                                                <!--XXX - Apply params Copy Of 1 -XXX--><xsl:copy-of select="$var"/>
                                                            </xsl:if>
                                                        </xsl:variable>
                                                        <!--XXX - Apply params Copy Of 2 -XXX--><xsl:copy-of select="$var"/>
                                                    </xsl:variable>
                                                    <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                </le>
                                                <le>
                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                                                        <!--valid-if--><xsl:variable name="var">
                                                            <xsl:if test="not(pub:publication/source:source[1]) and not(pub:publication/e:publishing-info)">
                                                                <xsl:variable name="var">
                                                                    <xsl:call-template name="applyDelimiter">
                                                                        <xsl:with-param name="les">
                                                                            <le>
                                                                                <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$date"/>
                                                                                    <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                                                                </xsl:variable>
                                                                                <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                                            </le>
                                                                        </xsl:with-param>
                                                                        <xsl:with-param name="delimiter" select="' '"/>
                                                                    </xsl:call-template>
                                                                </xsl:variable>
                                                                <!--
				start-with/ends-with
			--><xsl:variable name="var">
                                                                    <xsl:if test="exists($var) and $var!=''">
                                                                        <xsl:text>, </xsl:text>
                                                                    </xsl:if>
                                                                    <!--XXX - applyStartsWithEndsWith Copy Of 1 -XXX--><xsl:copy-of select="$var"/>
                                                                </xsl:variable>
                                                                <!--XXX - Apply params Copy Of 1 -XXX--><xsl:copy-of select="$var"/>
                                                            </xsl:if>
                                                        </xsl:variable>
                                                        <!--XXX - Apply params Copy Of 2 -XXX--><xsl:copy-of select="$var"/>
                                                    </xsl:variable>
                                                    <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                </le>
                                                <le>
                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                                                        <!--valid-if--><xsl:variable name="var">
                                                            <xsl:if test="&#xA;&#x9;&#x9;&#x9;&#x9;&#x9;&#x9;exists(pub:publication/e:published-online/text())&#xA;&#x9;&#x9;&#x9;&#x9;&#x9;&#xA;&#x9;&#x9;&#x9;&#x9;&#x9;">
                                                                <xsl:variable name="var">
                                                                    <xsl:call-template name="applyDelimiter">
                                                                        <xsl:with-param name="les">
                                                                            <le>
                                                                                <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$pages"/>
                                                                                    <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                                                                </xsl:variable>
                                                                                <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                                            </le>
                                                                            <le>
                                                                                <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$id-online-publication-paper"/>
                                                                                    <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                                                                </xsl:variable>
                                                                                <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                                            </le>
                                                                        </xsl:with-param>
                                                                        <xsl:with-param name="delimiter" select="' '"/>
                                                                    </xsl:call-template>
                                                                </xsl:variable>
                                                                <!--
				start-with/ends-with
			--><xsl:variable name="var"><!--XXX - applyStartsWithEndsWith Copy Of 1 -XXX--><xsl:copy-of select="$var"/>
                                                                    <xsl:if test="exists($var) and $var!=''">
                                                                        <xsl:text>.</xsl:text>
                                                                    </xsl:if>
                                                                </xsl:variable>
                                                                <!--XXX - Apply params Copy Of 1 -XXX--><xsl:copy-of select="$var"/>
                                                            </xsl:if>
                                                        </xsl:variable>
                                                        <!--XXX - Apply params Copy Of 2 -XXX--><xsl:copy-of select="$var"/>
                                                    </xsl:variable>
                                                    <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                </le>
                                                <le>
                                                    <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is not available ###--><xsl:variable name="var" select="''"/>
                                                        <!--valid-if--><xsl:variable name="var">
                                                            <xsl:if test="&#xA;&#x9;&#x9;&#x9;&#x9;&#x9;&#x9;not(pub:publication/e:published-online/text())&#xA;&#x9;&#x9;&#x9;&#x9;&#x9;&#xA;&#x9;&#x9;&#x9;&#x9;&#x9;">
                                                                <xsl:variable name="var">
                                                                    <xsl:call-template name="applyDelimiter">
                                                                        <xsl:with-param name="les">
                                                                            <le>
                                                                                <xsl:variable name="var"><!--### Plain Layout Element ###-->
	<!--### @ref is available ###--><xsl:variable name="var" select="$pages"/>
                                                                                    <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                                                                </xsl:variable>
                                                                                <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                                            </le>
                                                                        </xsl:with-param>
                                                                        <xsl:with-param name="delimiter" select="' '"/>
                                                                    </xsl:call-template>
                                                                </xsl:variable>
                                                                <!--XXX - Apply params Copy Of 1 -XXX--><xsl:copy-of select="$var"/>
                                                            </xsl:if>
                                                        </xsl:variable>
                                                        <!--XXX - Apply params Copy Of 2 -XXX--><xsl:copy-of select="$var"/>
                                                    </xsl:variable>
                                                    <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$var"/>
                                                </le>
                                            </xsl:with-param>
                                            <xsl:with-param name="delimiter" select="' '"/>
                                        </xsl:call-template>
                                    </xsl:variable>
                                    <!--
				start-with/ends-with
			--><xsl:variable name="var"><!--XXX - applyStartsWithEndsWith Copy Of 1 -XXX--><xsl:copy-of select="$var"/>
                                        <xsl:if test="exists($var) and $var!=''">
                                            <xsl:text>.</xsl:text>
                                        </xsl:if>
                                    </xsl:variable>
                                    <!--XXX - Apply params Copy Of 3 -XXX--><xsl:copy-of select="$var"/>
                                </xsl:variable>
                                <!--XXX -Create LE copy Of -XXX--><xsl:copy-of select="$paper"/>
                            </xsl:when>
                        </xsl:choose>
                        <!--### End of Citation Style Layout Definitions ###-->
	</xsl:for-each>
                </xsl:variable>
                <xsl:value-of select="func:cleanCitation($citation)"/>
            </xsl:element>
        </xsl:element>
    </xsl:template>
    <!--### Includes ###-->
	<xsl:template match="escidocComponents:content[@storage='internal-managed']">
    	   <xsl:element name="{name(.)}">
    		      <xsl:copy-of select="@*[name(.)!='xlink:href']"/>
    		      <xsl:attribute name="xlink:href"
                           select="concat(         $pubman_instance,         '/item/',          ../../../ei:properties/prop:version/@objid,         '/component/',         ../@objid,         '/',         ../escidocComponents:properties/prop:file-name        )"/>
    	   </xsl:element>
    </xsl:template>
    <xsl:template name="applyDelimiter">
		      <xsl:param name="les"/>
		      <xsl:param name="delimiter"/>
		      <xsl:variable name="les_filled" select="$les/le[exists(text()) and text()!='']"/>
		      <xsl:for-each select="$les_filled">
			         <xsl:value-of select="if (position()&gt;1) then @position-delimiter else ''"/>
			         <xsl:copy-of select="child::node()" copy-namespaces="no"/>
			         <xsl:if test="position()!=last() and not(@position-delimiter)">
				            <xsl:value-of select="$delimiter"/>
			         </xsl:if>
		      </xsl:for-each>
	   </xsl:template>
    <!--### Runtime Functions ###-->
	<xsl:function xmlns="http://www.escidoc.de/citationstyle" xmlns:exslt="http://exslt.org/common"
                  name="func:get_year">
		      <xsl:param name="date"/>
		      <xsl:value-of select="substring($date,1,4)"/>
	   </xsl:function>
    <xsl:function xmlns="http://www.escidoc.de/citationstyle" xmlns:exslt="http://exslt.org/common"
                  name="func:get_month">
		      <xsl:param name="date"/>
		      <xsl:value-of select="substring($date,6,2)"/>
	   </xsl:function>
    <xsl:function xmlns="http://www.escidoc.de/citationstyle" xmlns:exslt="http://exslt.org/common"
                  name="func:get_month_name">
		      <xsl:param name="date"/>
		      <xsl:variable name="months">
			         <m n="0?1">January</m>
			         <m n="0?2">February</m>
			         <m n="0?3">March</m>
			         <m n="0?4">April</m>
			         <m n="0?5">May</m>
			         <m n="0?6">June</m>
			         <m n="0?7">July</m>
			         <m n="0?8">August</m>
			         <m n="0?9">September</m>
			         <m n="10">October</m>
			         <m n="11">November</m>
			         <m n="12">December</m>
		      </xsl:variable>
		      <xsl:value-of select="     $months/m[     matches(      tokenize($date, '-')[2], @n     )     ]   "/>
	   </xsl:function>
    <xsl:function xmlns="http://www.escidoc.de/citationstyle" xmlns:exslt="http://exslt.org/common"
                  name="func:get_initials">
		      <xsl:param name="str"/>
		      <xsl:variable name="delim" select="if (contains ($str, '-')) then '-' else ' '"/>
		      <xsl:variable name="result">
		          <xsl:for-each select="tokenize(normalize-space ($str), '\s+|\.\s+|\-\s*')">
			             <xsl:value-of select="concat(substring (., 1, 1), if (position()!=last())then concat ('.', $delim) else '.')"/>
		          </xsl:for-each>
		      </xsl:variable>
		      <xsl:value-of select="$result"/>
	   </xsl:function>
    <xsl:function xmlns="http://www.escidoc.de/citationstyle" xmlns:exslt="http://exslt.org/common"
                  name="func:fname_initials">
		      <xsl:param name="fname"/>
		      <xsl:param name="gname"/>
		      <xsl:param name="delim"/>
		
		      <xsl:value-of select="    if ( jfunc:isCJK(concat($fname, $gname) ) )     then string-join( ($fname, $gname), $delim )    else string-join( ($fname, func:get_initials($gname)), $delim )   "/>
		
	   </xsl:function>
    <xsl:function xmlns="http://www.escidoc.de/citationstyle" xmlns:exslt="http://exslt.org/common"
                  name="func:initials_fname">
		      <xsl:param name="gname"/>
		      <xsl:param name="fname"/>
		      <xsl:param name="delim"/>
		
		      <xsl:value-of select="    if ( jfunc:isCJK(concat($fname, $gname) ) )     then string-join( ($fname, $gname), $delim )    else string-join( (func:get_initials($gname), $fname), $delim )   "/>
		
	   </xsl:function>
    <xsl:function xmlns="http://www.escidoc.de/citationstyle" xmlns:exslt="http://exslt.org/common"
                  name="func:escapeMarkupTags">
		      <xsl:param name="str"/>
		      <xsl:value-of select="jfunc:escapeMarkupTags($str)"/>
	   </xsl:function>
    <xsl:function xmlns="http://www.escidoc.de/citationstyle" xmlns:exslt="http://exslt.org/common"
                  name="func:cleanCitation">
		      <xsl:param name="str"/>
			     <xsl:value-of select="     normalize-space (     functx:replace-multi (      $str,      ( '([.,?!:;])\s*(&lt;[/]span&gt;)\s*\1', '([.,?!:;])\s*\1', '\.&#34;\.', '\s+([.,?!:;])', '\s*(&lt;[/]?span&gt;)\s*([.,?!:;])', '([?!])+\.' ),      ( '$1$2',         '$1',    '.&#34;',  '$1',     '$1$2',         '$1' )     )     )    "/>
			     <!-- 																	.".=>." ??? -->
	</xsl:function>
    <xsl:function xmlns="http://www.escidoc.de/citationstyle" xmlns:exslt="http://exslt.org/common"
                  name="functx:replace-multi"
                  as="xs:string?">
	       <xsl:param name="arg" as="xs:string?"/> 
	       <xsl:param name="changeFrom" as="xs:string*"/> 
	       <xsl:param name="changeTo" as="xs:string*"/> 
	 
	       <xsl:sequence select="      if (count($changeFrom) &gt; 0)     then functx:replace-multi(            replace($arg, $changeFrom[1],                       functx:if-absent($changeTo[1],'')),            $changeFrom[position() &gt; 1],            $changeTo[position() &gt; 1])     else $arg   "/>
	   
	   </xsl:function>
    <xsl:function xmlns="http://www.escidoc.de/citationstyle" xmlns:exslt="http://exslt.org/common"
                  name="functx:if-absent"
                  as="item()*">
	       <xsl:param name="arg" as="item()*"/> 
	       <xsl:param name="value" as="item()*"/> 
	 
	       <xsl:sequence select="       if (exists($arg))      then $arg      else $value   "/>
	   </xsl:function>
    <xsl:function xmlns="http://www.escidoc.de/citationstyle" xmlns:exslt="http://exslt.org/common"
                  name="func:get_reverse_date">
		      <xsl:param name="input_date"/>
		      <xsl:if test="$input_date[.!=''] ">
			         <xsl:value-of select="concat(substring($input_date,9,2),'.',substring($input_date,6,2),'.',substring($input_date,1,4))"/>
		      </xsl:if>
	   </xsl:function>
    <xsl:function xmlns="http://www.escidoc.de/citationstyle" xmlns:exslt="http://exslt.org/common"
                  name="func:getCitationStyleForJournal">
		      <xsl:param name="idType"/>
		      <xsl:param name="idValue"/>
		      <xsl:value-of select="jfunc:getCitationStyleForJournal($idType,$idValue)"/>
	   </xsl:function>
    <xsl:function xmlns="http://www.escidoc.de/citationstyle" xmlns:exslt="http://exslt.org/common"
                  name="func:substringAfterEdition">
		      <xsl:param name="inputWithSpaceComma"/>
		      <xsl:value-of select="substring-before(substring-after($inputWithSpaceComma,', '), ' ')"/>
	   </xsl:function>
    <xsl:function xmlns="http://www.escidoc.de/citationstyle" xmlns:exslt="http://exslt.org/common"
                  name="func:substringBeforeEdition">
		      <xsl:param name="inputWithSpaceComma"/>
		      <xsl:value-of select="substring-before(substring-before($inputWithSpaceComma,', '), ' ')"/>
	   </xsl:function>
    <xsl:function xmlns="http://www.escidoc.de/citationstyle" xmlns:exslt="http://exslt.org/common"
                  name="func:substringBeforeInstalment">
		      <xsl:param name="inputWithInstalment"/>
		      <xsl:value-of select="      if (contains($inputWithInstalment, 'instl'))      then substring-before($inputWithInstalment, 'instl')        else if (contains($inputWithInstalment, 'Lf'))      then substring-before($inputWithInstalment, 'Lf')        else ''      "/>
	   </xsl:function>
    <xsl:function xmlns="http://www.escidoc.de/citationstyle" xmlns:exslt="http://exslt.org/common"
                  name="func:substringBeforeSince">
		      <xsl:param name="inputWithSince"/>
	       <xsl:value-of select="       if (contains($inputWithSince, 'since'))       then substring-before($inputWithSince, 'since')         else if (contains($inputWithSince, 'seit'))       then substring-before($inputWithSince, 'seit')         else ''       "/>
	   </xsl:function>
    <xsl:function xmlns="http://www.escidoc.de/citationstyle" xmlns:exslt="http://exslt.org/common"
                  name="func:substringAfterSince">
	       <xsl:param name="inputWithSince"/>
		      <xsl:value-of select="      if (contains($inputWithSince, 'since'))      then substring-after($inputWithSince, 'since')        else if (contains($inputWithSince, 'seit'))      then substring-after($inputWithSince, 'seit')        else ''      "/>
	   </xsl:function>
    <xsl:function xmlns="http://www.escidoc.de/citationstyle" xmlns:exslt="http://exslt.org/common"
                  name="func:substringAfterReviewOf">
		      <xsl:param name="inputReviewTitle"/>
		      <xsl:value-of select="substring-after($inputReviewTitle,'Review of:')"/>
	   </xsl:function>
</xsl:stylesheet>