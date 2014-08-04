<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:iso="http://purl.oclc.org/dsdl/schematron" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:r="http://www.escidoc.de/validation" xmlns:fn="http://www.escidoc.de/functions">

	<xsl:output encoding="UTF-8" indent="yes" method="html"/>

	<xsl:param name="validation-schema"/>
	<xsl:param name="language" select="'english'"/>
	<xsl:param name="rule-text" select="''"/>
	
	<xsl:variable name="max-counter" select="999"/>

	<xsl:variable name="validation-schema-content">
		<xsl:copy-of select="document($validation-schema)"/>
	</xsl:variable>

	<xsl:variable name="fields">
		<xsl:for-each select="/r:ruler/r:fields/r:root">
			<xsl:copy-of select="document(@schema)"/>
		</xsl:for-each>
	</xsl:variable>
			
	<xsl:variable name="context" select="*"/>
		
	<xsl:template match="/">
	

			<html>
				<head>
					<title>eSciDoc VaRE</title>
					<script type="text/javascript" src="scripts.js">;</script>
				</head>
				<body>
					<h1>eSciDoc VaRE</h1>
					<h4>Validation Rule Editor</h4>
					<hr/>
					<form name="form">
					
						<span><xsl:value-of select="fn:get-text('in-every')"/></span>
						<select name="rule-element" size="1" onchange="changeRuleElement(this)">
							<option>...</option>
							<xsl:for-each select="r:ruler/r:fields/r:root">
								<option value="{@name}"><xsl:value-of select="fn:get-text(@name)"/></option>
							</xsl:for-each>
						</select>
						,
						<xsl:apply-templates select="r:ruler/r:grammar/r:term[@type='rule']">
							<xsl:with-param name="rule-text" select="$rule-text"/>
						</xsl:apply-templates>
						, 
						<span><xsl:value-of select="fn:get-text('else-report')"/></span>
						"<input type="text" name="message" value="My message"/>"
					</form>
				</body>
			</html>

		
	</xsl:template>
		
	<xsl:template match="r:term">
		<xsl:param name="rule-text"/>
		<xsl:apply-templates select="*[1]">
			<xsl:with-param name="rule-text" select="$rule-text"/>
		</xsl:apply-templates>
	</xsl:template>
	
	<xsl:template match="r:word">
		<xsl:param name="rule-text"/>
		
		<xsl:choose>
			<xsl:when test="@pattern != ''">
				<xsl:apply-templates select="following-sibling::*[1]">
					<xsl:with-param name="rule-text" select="$rule-text"/>
				</xsl:apply-templates>
			</xsl:when>

			<xsl:when test="@vocabulary != ''">
			
				<xsl:variable name="vocabulary-name" select="@vocabulary"/>
				<xsl:if test="not(exists(/r:ruler/r:vocabularies/r:vocabulary[@name = $vocabulary-name]))"><xsl:value-of select="error(QName('http://www.escidoc.de/validation', 'VocabularyNotDefinedError'), concat('Vocabulary ', $vocabulary-name, ' is not defined'))"/></xsl:if>
		
				<xsl:variable name="word" select="substring-before(concat($rule-text, ' '), ' ')"/>
				
				<select name="{$vocabulary-name}" size="1">
					<option>...</option>
					<xsl:for-each select="/r:ruler/r:vocabularies/r:vocabulary[@name = $vocabulary-name]/r:word">
						<option value="{@name}"><xsl:value-of select="fn:get-text(@name)"/></option>
					</xsl:for-each>
				</select>
				<xsl:value-of select="fn:get-text($word)"/>
				<xsl:text> </xsl:text>
					
				<xsl:apply-templates select="following-sibling::r:value[1]">
					<xsl:with-param name="rule-text" select="normalize-space(substring-after($rule-text, ' '))"/>
				</xsl:apply-templates>
					
				<xsl:apply-templates select="following-sibling::*[local-name() != 'value'][1]">
					<xsl:with-param name="rule-text" select="normalize-space(substring-after(substring-after($rule-text, ' '), ' '))"/>
				</xsl:apply-templates>
					
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="error(QName('http://www.escidoc.de/validation', 'VocabularyOrPatternEmptyError'), 'Vocabulary and pattern are empty')"/>
			</xsl:otherwise>
		</xsl:choose>
				
	</xsl:template>
	
	<xsl:template match="r:value">
		<xsl:param name="rule-text"/>
		
		<xsl:variable name="value-text" select="substring-before(concat($rule-text, ' '), ' ')"/>
		
		<select name="value" size="1">
			<option>...</option>
			<xsl:if test="not(exists(@type)) or contains(concat(',', @type, ','), ',field,')">
				<xsl:for-each select="/r:ruler/r:fields/r:root">
					<optgroup label="{fn:get-text(@name)}">
						<xsl:for-each select="r:field">
							<option value="{@path}"><xsl:value-of select="fn:get-text(@path)"/></option>
						</xsl:for-each>
					</optgroup>
				</xsl:for-each>
			</xsl:if>
			<xsl:if test="not(exists(@type)) or contains(concat(',', @type, ','), ',constant,')">
				<optgroup label="{fn:get-text('constant')}">
					<option><xsl:value-of select="fn:get-text('enter')"/></option>
				</optgroup>
			</xsl:if>
		</select>
		
	</xsl:template>
	
	<xsl:template match="r:section">
		<xsl:param name="rule-text"/>

		<xsl:variable name="section-text" select="normalize-space(fn:get-section-text($rule-text, *[1]))"/>

		<xsl:choose>
			<xsl:when test="starts-with($rule-text, '(')">
				
				<xsl:apply-templates select="." mode="subsection">
					<xsl:with-param name="rule-text" select="$section-text"/>
				</xsl:apply-templates>

			</xsl:when>
			<xsl:otherwise>
			
				<xsl:apply-templates select="*[1]">
					<xsl:with-param name="rule-text" select="$rule-text"/>
				</xsl:apply-templates>
				
			</xsl:otherwise>
		</xsl:choose>
		
		<xsl:apply-templates select="following-sibling::*[1]">
			<xsl:with-param name="rule-text" select="normalize-space(substring-after($rule-text, $section-text))"/>
		</xsl:apply-templates>

	</xsl:template>
	
	<xsl:template match="r:section" mode="subsection">
		<xsl:param name="rule-text"/>

		<xsl:choose>
			<xsl:when test="$rule-text = ' and '">
				<xsl:value-of select="fn:get-text('and')"/>
				<xsl:text> </xsl:text>
			</xsl:when>
			<xsl:when test="$rule-text = ' or '">
				<xsl:value-of select="fn:get-text('or')"/>
				<xsl:text> </xsl:text>
			</xsl:when>
			<xsl:when test="$rule-text = '('">

			</xsl:when>
			<xsl:when test="$rule-text = ')'">

			</xsl:when>
			<xsl:otherwise>

				<xsl:variable name="current-context" select="."/>
			
				<xsl:variable name="terms" select="fn:split-term($rule-text, '', 0, ())"/>
			
				<xsl:choose>
					<xsl:when test="count($terms) &gt; 1">
						<xsl:for-each select="$terms">
							<xsl:apply-templates select="$current-context" mode="subsection">
								<xsl:with-param name="rule-text" select="."/>
							</xsl:apply-templates>
						</xsl:for-each>
					</xsl:when>
					<xsl:otherwise>
						<xsl:apply-templates select="$current-context/*[1]">
							<xsl:with-param name="rule-text" select="$rule-text"/>
						</xsl:apply-templates>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:otherwise>
		</xsl:choose>

	</xsl:template>
	
	<xsl:function name="fn:split-term">
		<xsl:param name="rule-text"/>
		<xsl:param name="consumed-text"/>
		<xsl:param name="level"/>
		<xsl:param name="current-result"/>

		<xsl:choose>
			<xsl:when test="$rule-text = '' or $level = -1">
				<xsl:choose>
					<xsl:when test="normalize-space($consumed-text) != ''">
						<xsl:copy-of select="($current-result, $consumed-text)"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:copy-of select="($current-result)"/>
					</xsl:otherwise>
				</xsl:choose>
				
			</xsl:when>
			<xsl:when test="starts-with($rule-text, ' and ') and $level = 0">
				<xsl:copy-of select="fn:split-term(substring-after($rule-text, ' and '), '', 0, ($current-result, $consumed-text, ' and '))"/>
			</xsl:when>
			<xsl:when test="starts-with($rule-text, ' or ') and $level = 0">
				<xsl:copy-of select="fn:split-term(substring-after($rule-text, ' or '), '', 0, ($current-result, $consumed-text, ' or '))"/>
			</xsl:when>
			<xsl:when test="starts-with($rule-text, '(')">
				<xsl:copy-of select="fn:split-term(substring-after($rule-text, '('), '', $level + 1, ($current-result, '('))"/>
			</xsl:when>
			<xsl:when test="starts-with($rule-text, ')')">
				<xsl:copy-of select="fn:split-term(substring-after($rule-text, ')'), '', $level - 1, ($current-result, $consumed-text, ')'))"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:copy-of select="fn:split-term(substring($rule-text, 2), concat($consumed-text, substring($rule-text, 1, 1)), $level, $current-result)"/>
			</xsl:otherwise>
		</xsl:choose>
		
	</xsl:function>
	
	<xsl:function name="fn:get-section-text" as="xs:string">
		<xsl:param name="rule-text"/>
		<xsl:param name="context"/>

		<xsl:variable name="result">
			<xsl:choose>
				<xsl:when test="starts-with($rule-text, '(')">
					<xsl:value-of select="string-join(fn:split-term($rule-text, '', 0, ()), '')"/>
				</xsl:when>
				<xsl:when test="$context/name() = 'r:word' and $context/@vocabulary != ''">
					<xsl:value-of select="substring-before(concat($rule-text, ' '), ' ')"/>
					<xsl:text> </xsl:text>
					<xsl:if test="exists($context/following-sibling::*[1])">
						<xsl:value-of select="fn:get-section-text(normalize-space(substring-after($rule-text, ' ')), $context/following-sibling::*[1])"/>
					</xsl:if>
				</xsl:when>
				<xsl:when test="$context/name() = 'r:value'">
					<xsl:value-of select="substring-before(concat($rule-text, ' '), ' ')"/>
					<xsl:text> </xsl:text>
					<xsl:if test="exists($context/following-sibling::*[1])">
						<xsl:value-of select="fn:get-section-text(normalize-space(substring-after($rule-text, ' ')), $context/following-sibling::*[1])"/>
					</xsl:if>
				</xsl:when>
			</xsl:choose>
		</xsl:variable>
		
		<xsl:value-of select="$result"/>
		
	</xsl:function>
		
	<xsl:function name="fn:get-text" as="xs:string">
		<xsl:param name="label"/>
		
		<xsl:choose>
			<xsl:when test="exists($context/r:labels/r:language[@name = $language]/r:label[@id = $label])">
				<xsl:value-of select="$context/r:labels/r:language[@name = $language]/r:label[@id = $label]"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$label"/>
			</xsl:otherwise>
		</xsl:choose>
		
	</xsl:function>
	
</xsl:stylesheet>