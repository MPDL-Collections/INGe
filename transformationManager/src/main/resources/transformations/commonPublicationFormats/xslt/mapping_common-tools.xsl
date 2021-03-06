<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
	xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl"
	xmlns:misc="http://www.editura.de/ns/2012/misc"
	xmlns:tools="http://www.editura.de/ns/2012/tools"
	xmlns:intern="http://www.editura.de/ns/2012/intern"
	xmlns:Util="java:de.mpg.mpdl.inge.transformation.Util" exclude-result-prefixes="xsl xs xd tools intern Util" version="2.0">
	<xsl:variable name="intern:maximum-depth-for-recursions" as="xs:integer">300</xsl:variable>
	<xsl:function name="tools:render-context-and-parent-as-string" as="xs:string">
		<xsl:param name="context" as="node()?"/>
		<xsl:variable name="temp" as="xs:string*">
			<xsl:choose>
				<xsl:when test="not($context/self::node())"/>
				<xsl:when test="$context/self::node()/..">
					<xsl:text>//</xsl:text>
					<xsl:sequence select="tools:render-context-as-string($context/..)"/>
					<xsl:text>/</xsl:text>
					<xsl:sequence select="tools:render-context-as-string($context/.)"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>//</xsl:text>
					<xsl:sequence select="tools:render-context-as-string($context)"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:sequence select="string-join($temp, '' )"/>
	</xsl:function>
	<xsl:function name="tools:render-context-as-string" as="xs:string">
		<xsl:param name="context" as="node()?"/>
		<xsl:variable name="kind" as="xs:string" select="tools:node-kind($context)"/>
		<xsl:variable name="temp" as="xs:string*">
			<xsl:choose>
				<xsl:when test="$kind = '' "/>
				<xsl:when test="$kind = 'document-node' ">document-node()</xsl:when>
				<xsl:when test="$kind = 'element' ">
					<xsl:sequence select="name($context)"/>
					<xsl:if test="$context/@*">
						<xsl:variable name="mit-klammern" as="xs:boolean" select="if ($context/@*[2]) then true() else false()"/>
						<xsl:text>[</xsl:text>
						<xsl:for-each select="$context/@*">
							<xsl:if test="$mit-klammern">(</xsl:if>
							<xsl:text>@</xsl:text>
							<xsl:sequence select="name(.)"/>
							<xsl:text>="</xsl:text>
							<xsl:sequence select="."/>
							<xsl:text>"</xsl:text>
							<xsl:if test="$mit-klammern">)</xsl:if>
							<xsl:if test="position() lt last()">
								<xsl:text> and </xsl:text>
							</xsl:if>
						</xsl:for-each>
						<xsl:text>]</xsl:text>
					</xsl:if>
				</xsl:when>
				<xsl:when test="$kind = 'text' ">text()</xsl:when>
				<xsl:when test="$kind = 'attribute' ">@
					<xsl:sequence select="name($context/.)"/>[. ="
					<xsl:sequence select="$context"/>"]
				</xsl:when>
				<xsl:when test="$kind = 'comment' ">comment()</xsl:when>
				<xsl:when test="$kind = 'processing-instruction' ">processing-instruction()</xsl:when>
				<xsl:when test="$kind = 'namespace' ">namespace::
					<xsl:sequence select="local-name($context)"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>#undefined</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:sequence select="string-join($temp, '')"/>
	</xsl:function>
	<xsl:function name="tools:node-kind" as="xs:string">
		<xsl:param name="context" as="node()?"/>
		<xsl:choose>
			<xsl:when test="not($context)">
				<xsl:sequence select=" '' "/>
			</xsl:when>
			<xsl:when test="$context/self::document-node()">document-node</xsl:when>
			<xsl:when test="$context/self::element()">element</xsl:when>
			<xsl:when test="$context/self::attribute()">attribute</xsl:when>
			<xsl:when test="$context/self::text()">text</xsl:when>
			<xsl:when test="$context/self::comment()">comment</xsl:when>
			<xsl:when test="$context/self::processing-instruction()">processing-instruction</xsl:when>
			<xsl:when test="local-name($context)">namespace</xsl:when>
			<xsl:otherwise>
				<xsl:text>#undefined</xsl:text>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:function>
	<xsl:function name="tools:fill-right" as="xs:string">
		<xsl:param name="input" as="xs:string?"/>
		<xsl:param name="fill-with" as="xs:string?"/>
		<xsl:param name="length" as="xs:integer"/>
		<xsl:choose>
			<xsl:when test="string-length($fill-with) eq 0">
				<xsl:sequence select="concat('', $input)"/>
			</xsl:when>
			<xsl:when test="string-length($input) lt $length">
				<xsl:sequence select="string-join( ($input, for $i in 1 to $length - string-length($input) return substring($fill-with, 1, 1) ), '')"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:sequence select="concat('', $input)"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:function>
	<xsl:variable name="tools:mediatypes" as="document-node()">
		<xsl:document>
			<mapping-table
				xmlns="http://www.editura.de/ns/2012/tools" xml:id="mimetypes-extensions">
				<l>
					<m>application/andrew-inset</m>
					<e>ez</e>
				</l>
				<l>
					<m>application/excel</m>
					<e>xls</e>
				</l>
				<l>
					<m>application/mac-binhex40</m>
					<e>hqx</e>
				</l>
				<l>
					<m>application/mac-compactpro</m>
					<e>cpt</e>
				</l>
				<l>
					<m>application/mathml+xml</m>
					<e>mathml</e>
				</l>
				<l>
					<m>application/msword</m>
					<e>doc</e>
				</l>
				<l>
					<m>application/octet-stream</m>
					<e>bin</e>
				</l>
				<l>
					<m>application/oda</m>
					<e>oda</e>
				</l>
				<l>
					<m>application/ogg</m>
					<e>ogg</e>
				</l>
				<l>
					<m>application/pdf</m>
					<e>pdf</e>
				</l>
				<l>
					<m>application/pgp</m>
					<e>pgp</e>
				</l>
				<l>
					<m>application/pgp-encrypted</m>
					<e>pgp</e>
				</l>
				<l>
					<m>application/pgp-keys</m>
					<e>pgp</e>
				</l>
				<l>
					<m>application/pgp-signature</m>
					<e>sig</e>
				</l>
				<l>
					<m>application/postscript</m>
					<e>ps</e>
				</l>
				<l>
					<m>application/postscript</m>
					<e>ai</e>
				</l>
				<l>
					<m>application/postscript</m>
					<e>eps</e>
				</l>
				<l>
					<m>application/rdf+xml</m>
					<e>rdf</e>
				</l>
				<l>
					<m>application/rdf</m>
					<e>rdf</e>
				</l>
				<l>
					<m>application/rtf</m>
					<e>rtf</e>
				</l>
				<l>
					<m>application/smil</m>
					<e>smil</e>
				</l>
				<l>
					<m>application/smil</m>
					<e>smi</e>
				</l>
				<l>
					<m>application/srgs</m>
					<e>gram</e>
				</l>
				<l>
					<m>application/srgs+xml</m>
					<e>grxml</e>
				</l>
				<l>
					<m>application/vnd.mif</m>
					<e>mif</e>
				</l>
				<l>
					<m>application/vnd.mozilla.xul+xml</m>
					<e>xul</e>
				</l>
				<l>
					<m>application/vnd.ms-excel</m>
					<e>xls</e>
				</l>
				<l>
					<m>application/vnd.ms-powerpoint</m>
					<e>ppt</e>
				</l>
				<l>
					<m>application/vnd.ms-project</m>
					<e>mpp</e>
				</l>
				<l>
					<m>application/vnd.ms-tnef</m>
					<e>tnef</e>
				</l>
				<l>
					<m>application/vnd.rn-realmedia</m>
					<e>rm</e>
				</l>
				<l>
					<m>application/vnd.wap.wbxml</m>
					<e>wbxml</e>
				</l>
				<l>
					<m>application/vnd.wap.wmlc</m>
					<e>wmlc</e>
				</l>
				<l>
					<m>application/vnd.wap.wmlscriptc</m>
					<e>wmlsc</e>
				</l>
				<l>
					<m>application/voicexml+xml</m>
					<e>vxml</e>
				</l>
				<l>
					<m>application/x-arj-compressed</m>
					<e>arj</e>
				</l>
				<l>
					<m>application/x-bcpio</m>
					<e>bcpio</e>
				</l>
				<l>
					<m>application/x-cdlink</m>
					<e>vcd</e>
				</l>
				<l>
					<m>application/x-chess-pgn</m>
					<e>pgn</e>
				</l>
				<l>
					<m>application/x-compress</m>
					<e>Z</e>
				</l>
				<l>
					<m>application/x-cpio</m>
					<e>cpio</e>
				</l>
				<l>
					<m>application/x-csh</m>
					<e>csh</e>
				</l>
				<l>
					<m>application/x-debian-package</m>
					<e>deb</e>
				</l>
				<l>
					<m>application/x-director</m>
					<e>dcr</e>
				</l>
				<l>
					<m>application/x-director</m>
					<e>dir</e>
				</l>
				<l>
					<m>application/x-director</m>
					<e>dxr</e>
				</l>
				<l>
					<m>application/x-dtbncx+xml</m>
					<e>ncx</e>
				</l>
				<l>
					<m>application/x-dvi</m>
					<e>dvi</e>
				</l>
				<l>
					<m>application/x-futuresplash</m>
					<e>spl</e>
				</l>
				<l>
					<m>application/x-gtar</m>
					<e>gtar</e>
				</l>
				<l>
					<m>application/x-gunzip</m>
					<e>gz</e>
				</l>
				<l>
					<m>application/x-gzip</m>
					<e>gz</e>
				</l>
				<l>
					<m>application/x-hdf</m>
					<e>hdf</e>
				</l>
				<l>
					<m>application/x-javascript</m>
					<e>js</e>
				</l>
				<l>
					<m>application/x-koan</m>
					<e>skp</e>
				</l>
				<l>
					<m>application/x-koan</m>
					<e>skd</e>
				</l>
				<l>
					<m>application/x-koan</m>
					<e>skt</e>
				</l>
				<l>
					<m>application/x-koan</m>
					<e>skm</e>
				</l>
				<l>
					<m>application/x-latex</m>
					<e>latex</e>
				</l>
				<l>
					<m>application/x-mif</m>
					<e>mif</e>
				</l>
				<l>
					<m>application/x-msdos-program</m>
					<e>exe</e>
				</l>
				<l>
					<m>application/x-netcdf</m>
					<e>cdf</e>
				</l>
				<l>
					<m>application/x-netcdf</m>
					<e>nc</e>
				</l>
				<l>
					<m>application/x-netcdf</m>
					<e>nc</e>
				</l>
				<l>
					<m>application/x-perl</m>
					<e>pl</e>
				</l>
				<l>
					<m>application/x-perl</m>
					<e>pm</e>
				</l>
				<l>
					<m>application/x-python</m>
					<e>py</e>
				</l>
				<l>
					<m>application/x-rar-compressed</m>
					<e>rar</e>
				</l>
				<l>
					<m>application/x-sh</m>
					<e>sh</e>
				</l>
				<l>
					<m>application/x-shar</m>
					<e>shar</e>
				</l>
				<l>
					<m>application/x-shockwave-flash</m>
					<e>swf</e>
				</l>
				<l>
					<m>application/x-stuffit</m>
					<e>sit</e>
				</l>
				<l>
					<m>application/x-sv4cpio</m>
					<e>sv4cpio</e>
				</l>
				<l>
					<m>application/x-sv4crc</m>
					<e>sv4crc</e>
				</l>
				<l>
					<m>application/x-tar</m>
					<e>tar</e>
				</l>
				<l>
					<m>application/x-tar-gz</m>
					<e>tgz</e>
				</l>
				<l>
					<m>application/x-tcl</m>
					<e>tcl</e>
				</l>
				<l>
					<m>application/x-tex</m>
					<e>tex</e>
				</l>
				<l>
					<m>application/x-texinfo</m>
					<e>texi</e>
				</l>
				<l>
					<m>application/x-texinfo</m>
					<e>texinfo</e>
				</l>
				<l>
					<m>application/x-troff</m>
					<e>t</e>
				</l>
				<l>
					<m>application/x-troff</m>
					<e>tr</e>
				</l>
				<l>
					<m>application/x-troff</m>
					<e>troff</e>
				</l>
				<l>
					<m>application/x-troff-man</m>
					<e>man</e>
				</l>
				<l>
					<m>application/x-troff-me</m>
					<e>me</e>
				</l>
				<l>
					<m>application/x-troff-ms</m>
					<e>ms</e>
				</l>
				<l>
					<m>application/x-ustar</m>
					<e>ustar</e>
				</l>
				<l>
					<m>application/x-wais-source</m>
					<e>src</e>
				</l>
				<l>
					<m>application/x-zip-compressed</m>
					<e>zip</e>
				</l>
				<l>
					<m>application/xhtml+xml</m>
					<e>xhtml</e>
				</l>
				<l>
					<m>application/xhtml+xml</m>
					<e>xht</e>
				</l>
				<l>
					<m>application/xhtml</m>
					<e>xhtml</e>
				</l>
				<l>
					<m>application/xhtml</m>
					<e>xht</e>
				</l>
				<l>
					<m>application/xml</m>
					<e>xml</e>
				</l>
				<l>
					<m>application/xml-dtd</m>
					<e>dtd</e>
				</l>
				<l>
					<m>application/xslt+xml</m>
					<e>xslt</e>
				</l>
				<l>
					<m>application/xslt</m>
					<e>xslt</e>
				</l>
				<l>
					<m>application/zip</m>
					<e>zip</e>
				</l>
				<l>
					<m>audio/basic</m>
					<e>au</e>
				</l>
				<l>
					<m>audio/midi</m>
					<e>mid</e>
				</l>
				<l>
					<m>audio/midi</m>
					<e>midi</e>
				</l>
				<l>
					<m>audio/mpeg</m>
					<e>mpa</e>
				</l>
				<l>
					<m>audio/ulaw</m>
					<e>au</e>
				</l>
				<l>
					<m>audio/ulaw</m>
					<e>snd</e>
				</l>
				<l>
					<m>audio/x-aiff</m>
					<e>aif</e>
				</l>
				<l>
					<m>audio/x-mpegurl</m>
					<e>m3u</e>
				</l>
				<l>
					<m>audio/x-pn-realaudio</m>
					<e>ram</e>
				</l>
				<l>
					<m>audio/x-wav</m>
					<e>wav</e>
				</l>
				<l>
					<m>chemical/x-pdb</m>
					<e>pdb</e>
				</l>
				<l>
					<m>chemical/x-xyz</m>
					<e>xyz</e>
				</l>
				<l>
					<m>image/bmp</m>
					<e>bmp</e>
				</l>
				<l>
					<m>image/cgm</m>
					<e>cgm</e>
				</l>
				<l>
					<m>image/g3fax</m>
					<e>fax</e>
				</l>
				<l>
					<m>image/gif</m>
					<e>gif</e>
				</l>
				<l>
					<m>image/ief</m>
					<e>ief</e>
				</l>
				<l>
					<m>image/jpeg</m>
					<e>jpg</e>
				</l>
				<l>
					<m>image/jpeg</m>
					<e>jpeg</e>
				</l>
				<l>
					<m>image/png</m>
					<e>png</e>
				</l>
				<l>
					<m>image/svg+xml</m>
					<e>svg</e>
				</l>
				<l>
					<m>image/tiff</m>
					<e>tif</e>
				</l>
				<l>
					<m>image/tiff</m>
					<e>tiff</e>
				</l>
				<l>
					<m>image/vnd.djvu</m>
					<e>djvu</e>
				</l>
				<l>
					<m>image/vnd.wap.wbmp</m>
					<e>wbmp</e>
				</l>
				<l>
					<m>image/x-cmu-raster</m>
					<e>ras</e>
				</l>
				<l>
					<m>image/x-icon</m>
					<e>ico</e>
				</l>
				<l>
					<m>image/x-portable-anymap</m>
					<e>pnm</e>
				</l>
				<l>
					<m>image/x-portable-bitmap</m>
					<e>pbm</e>
				</l>
				<l>
					<m>image/x-portable-graymap</m>
					<e>pgm</e>
				</l>
				<l>
					<m>image/x-portable-pixmap</m>
					<e>ppm</e>
				</l>
				<l>
					<m>image/x-rgb</m>
					<e>rgb</e>
				</l>
				<l>
					<m>image/x-xbitmap</m>
					<e>xbm</e>
				</l>
				<l>
					<m>image/x-xpixmap</m>
					<e>xpm</e>
				</l>
				<l>
					<m>image/x-xwindowdump</m>
					<e>xwd</e>
				</l>
				<l>
					<m>model/iges</m>
					<e>igs</e>
				</l>
				<l>
					<m>model/mesh</m>
					<e>msh</e>
				</l>
				<l>
					<m>model/vrml</m>
					<e>vrml</e>
				</l>
				<l>
					<m>text/calendar</m>
					<e>ics</e>
				</l>
				<l>
					<m>text/css</m>
					<e>css</e>
				</l>
				<l>
					<m>text/html</m>
					<e>html</e>
				</l>
				<l>
					<m>text/plain</m>
					<e>txt</e>
				</l>
				<l>
					<m>text/richtext</m>
					<e>rtx</e>
				</l>
				<l>
					<m>text/rtf</m>
					<e>rtf</e>
				</l>
				<l>
					<m>text/sgml</m>
					<e>sgml</e>
				</l>
				<l>
					<m>text/tab-separated-values</m>
					<e>tsv</e>
				</l>
				<l>
					<m>text/vnd.wap.wml</m>
					<e>wml</e>
				</l>
				<l>
					<m>text/vnd.wap.wmlscript</m>
					<e>wmls</e>
				</l>
				<l>
					<m>text/x-setext</m>
					<e>etx</e>
				</l>
				<l>
					<m>text/xml</m>
					<e>xml</e>
				</l>
				<l>
					<m>video/dl</m>
					<e>dl</e>
				</l>
				<l>
					<m>video/fli</m>
					<e>fli</e>
				</l>
				<l>
					<m>video/gl</m>
					<e>gl</e>
				</l>
				<l>
					<m>video/mpeg</m>
					<e>mpg</e>
				</l>
				<l>
					<m>video/quicktime</m>
					<e>mov</e>
				</l>
				<l>
					<m>video/vnd.mpegurl</m>
					<e>mxu</e>
				</l>
				<l>
					<m>video/x-msvideo</m>
					<e>avi</e>
				</l>
				<l>
					<m>video/x-sgi-movie</m>
					<e>movie</e>
				</l>
				<l>
					<m>x-conference/x-cooltalk</m>
					<e>ice</e>
				</l>
				<l>
					<m>x-world/x-vrml</m>
					<e>vrml</e>
				</l>
			</mapping-table>
		</xsl:document>
	</xsl:variable>
	<xsl:key name="fileextension-to-mediatypes" match="tools:m" use="../tools:e"/>
	<xsl:key name="mediatypes-to-fileextension" match="tools:e" use="../tools:m"/>
	<xsl:function name="tools:mediatype-from-url" as="xs:string?">
		<xsl:param name="URLwithFileExtension" as="xs:string?"/>
		<xsl:variable name="file-extension" as="xs:string" select="lower-case(tokenize($URLwithFileExtension, '\.')[last()])"/>
		<xsl:if test="normalize-space($file-extension)">
			<xsl:sequence select="Util:getMimetype($file-extension)" use-when="function-available('Util:getMimetype')"/>
			<xsl:sequence select="key('fileextension-to-mediatypes', $file-extension, $tools:mediatypes)[1]" use-when="not(function-available('Util:getMimetype'))"/>
		</xsl:if>
	</xsl:function>
	<xsl:function name="tools:remove-query-and-fragment-from-url" as="xs:string">
		<xsl:param name="URL" as="xs:string?"/>
		<xsl:choose>
			<xsl:when test="contains($URL, '?')">
				<xsl:sequence select="substring-before($URL, '?')"/>
			</xsl:when>
			<xsl:when test="contains($URL, '#')">
				<xsl:sequence select="substring-before($URL, '#')"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:sequence select="concat($URL, '')"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:function>
	<xsl:function name="tools:fileName-and-fileExtention-from-url" as="xs:string">
		<xsl:param name="URL" as="xs:string?"/>
		<xsl:variable name="cleanURL" as="xs:string" select="tools:remove-query-and-fragment-from-url($URL)"/>
		<xsl:choose>
			<xsl:when test="matches($cleanURL, '[/:]$')">
				<xsl:sequence select=" '' "/>
			</xsl:when>
			<xsl:when test="contains($cleanURL, '/')">
				<xsl:sequence select="tokenize($cleanURL, '/')[last()]"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:sequence select="$cleanURL"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:function>
	<xsl:function name="tools:fileExtention-from-url" as="xs:string">
		<xsl:param name="URL" as="xs:string?"/>
		<xsl:variable name="cleanFileNameExtension" as="xs:string" select="tools:fileName-and-fileExtention-from-url($URL)"/>
		<xsl:sequence select="if (contains($cleanFileNameExtension, '.')) then (tokenize($cleanFileNameExtension, '\.')[last()]) else ( '' )"/>
	</xsl:function>
	<xsl:function name="tools:parse-date" as="xs:string">
		<xsl:param name="date-string" as="xs:string?"/>
		<xsl:sequence select="intern:parse-date(intern:normalize-date-string($date-string))"/>
	</xsl:function>
	<xsl:function name="intern:parse-date" as="xs:string">
		<xsl:param name="input" as="xs:string?"/>
		<xsl:variable name="vorläufiges-ergebnis" as="xs:string?">
			<xsl:choose>
				<xsl:when test="matches($input, '^(([0-9]{4})-[0-9]{2}-[0-9]{2})$')">
					<xsl:sequence select="$input"/>
				</xsl:when>
				<xsl:when test="matches($input, '^[0-9]{1,2}\.[0-9]{1,2}\.([0-9]{4})$')">
					<xsl:variable name="temp" as="xs:string+" select="tokenize($input, '\.')"/>
					<xsl:sequence select="intern:date-string($temp[3], $temp[2], $temp[1])"/>
				</xsl:when>
				<xsl:when test="matches($input, '^[0-9]{1,2}(\.)?(JAN|FEB|MRZ|APR|MAI|JUN|JUL|AUG|SEP|OKT|NOV|DEZ)([0-9]{4})$')">
					<xsl:variable name="temp" as="xs:string+">
						<xsl:analyze-string select="$input" regex="(JAN|FEB|MRZ|APR|MAI|JUN|JUL|AUG|SEP|OKT|NOV|DEZ)">
							<xsl:matching-substring>
								<xsl:sequence select="intern:month-to-integer-string(.)"/>
							</xsl:matching-substring>
							<xsl:non-matching-substring>
								<xsl:sequence select="translate(., '.', '')"/>
							</xsl:non-matching-substring>
						</xsl:analyze-string>
					</xsl:variable>
					<xsl:sequence select="intern:date-string($temp[3], $temp[2], $temp[1])"/>
				</xsl:when>
				<xsl:when test="matches($input, '^([0-9]{4})(JAN|FEB|MRZ|APR|MAI|JUN|JUL|AUG|SEP|OKT|NOV|DEZ)[0-9]{1,2}$')">
					<xsl:variable name="temp" as="xs:string+">
						<xsl:analyze-string select="$input" regex="(JAN|FEB|MRZ|APR|MAI|JUN|JUL|AUG|SEP|OKT|NOV|DEZ)">
							<xsl:matching-substring>
								<xsl:sequence select="intern:month-to-integer-string(.)"/>
							</xsl:matching-substring>
							<xsl:non-matching-substring>
								<xsl:sequence select="."/>
							</xsl:non-matching-substring>
						</xsl:analyze-string>
					</xsl:variable>
					<xsl:sequence select="intern:date-string($temp[1], $temp[2], $temp[3])"/>
				</xsl:when>
				<xsl:when test="matches($input, '^[0-9]{1,4}-[0-9]{1,2}-[0-9]{1,2}$')">
					<xsl:variable name="temp" as="xs:string+" select="tokenize($input, '-')"/>
					<xsl:sequence select="intern:date-string($temp[1], $temp[2], $temp[3])"/>
				</xsl:when>
				<xsl:when test="matches($input, '^[0-9]{1,2}\.(JAN|FEB|MRZ|APR|MAI|JUN|JUL|AUG|SEP|OKT|NOV|DEZ)[0-9]{1,4}$')">
					<xsl:variable name="temp" as="xs:string+">
						<xsl:analyze-string select="$input" regex="(JAN|FEB|MRZ|APR|MAI|JUN|JUL|AUG|SEP|OKT|NOV|DEZ)">
							<xsl:matching-substring>
								<xsl:sequence select="intern:month-to-integer-string(.)"/>
							</xsl:matching-substring>
							<xsl:non-matching-substring>
								<xsl:sequence select="translate(., '.', '')"/>
							</xsl:non-matching-substring>
						</xsl:analyze-string>
					</xsl:variable>
					<xsl:sequence select="intern:date-string($temp[3], $temp[2], $temp[1])"/>
				</xsl:when>
				<xsl:when test="matches($input, '^[0-9]{1,4}(JAN|FEB|MRZ|APR|MAI|JUN|JUL|AUG|SEP|OKT|NOV|DEZ)[0-9]{1,2}$')">
					<xsl:variable name="temp" as="xs:string+">
						<xsl:analyze-string select="$input" regex="(JAN|FEB|MRZ|APR|MAI|JUN|JUL|AUG|SEP|OKT|NOV|DEZ)">
							<xsl:matching-substring>
								<xsl:sequence select="intern:month-to-integer-string(.)"/>
							</xsl:matching-substring>
							<xsl:non-matching-substring>
								<xsl:sequence select="."/>
							</xsl:non-matching-substring>
						</xsl:analyze-string>
					</xsl:variable>
					<xsl:sequence select="intern:date-string($temp[1], $temp[2], $temp[3])"/>
				</xsl:when>
			</xsl:choose>
		</xsl:variable>
		<xsl:choose>
			<xsl:when test="not(normalize-space($input))">
				<xsl:sequence select="''"/>
			</xsl:when>
			<xsl:when test="$vorläufiges-ergebnis castable as xs:date">
				<xsl:sequence select="$vorläufiges-ergebnis"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:sequence select="''"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:function>
	<xsl:function name="intern:date-string" as="xs:string">
		<xsl:param name="year" as="xs:string"/>
		<xsl:param name="month" as="xs:string"/>
		<xsl:param name="day" as="xs:string"/>
		<xsl:sequence select="concat( format-number(xs:integer($year), '0000'), '-', format-number(xs:integer($month), '00'), '-', format-number(xs:integer($day), '00') )"/>
	</xsl:function>
	<xsl:function name="intern:month-to-integer-string" as="xs:string">
		<xsl:param name="month-string" as="xs:string"/>
		<xsl:sequence select="format-number((index-of(tokenize(translate('(JAN|FEB|MRZ|APR|MAI|JUN|JUL|AUG|SEP|OKT|NOV|DEZ)', '()', ''), '\|'), $month-string) ), '00')"/>
	</xsl:function>
	<xsl:function name="intern:normalize-date-string" as="xs:string">
		<xsl:param name="input" as="xs:string?"/>
		<xsl:variable name="prepared" as="xs:string" select="upper-case(translate(normalize-space($input), ' ', '') )"/>
		<xsl:variable name="replace-list" as="element(p)+">
			<p>
				<s>JANUARY|JAN\.|JANUAR</s>
				<r>JAN</r>
			</p>
			<p>
				<s>FEBRUARY|FEB\.|FEBRUAR</s>
				<r>FEB</r>
			</p>
			<p>
				<s>MÄRZ|MARCH</s>
				<r>MRZ</r>
			</p>
			<p>
				<s>APRIL|APR\.</s>
				<r>APR</r>
			</p>
			<p>
				<s>MAY</s>
				<r>MAI</r>
			</p>
			<p>
				<s>JUNI|JUNE</s>
				<r>JUN</r>
			</p>
			<p>
				<s>JULI|JULY</s>
				<r>JUL</r>
			</p>
			<p>
				<s>AUGUST|AUG\.</s>
				<r>AUG</r>
			</p>
			<p>
				<s>SEPTEMBER|SEP\.|SEPT\.</s>
				<r>SEP</r>
			</p>
			<p>
				<s>OKTOBER|OKT\.|OCTOBER|OCT\.</s>
				<r>OKT</r>
			</p>
			<p>
				<s>NOVEMBER|NOV\.</s>
				<r>NOV</r>
			</p>
			<p>
				<s>DEZEMBER|DEZ\.|DECEMBER|DEC\.</s>
				<r>DEZ</r>
			</p>
			<p>
				<s>UM</s>
				<r>CA</r>
			</p>
			<p>
				<s>CA\.</s>
				<r>CA</r>
			</p>
			<p>
				<s>1ST</s>
				<r>1.</r>
			</p>
			<p>
				<s>2ND</s>
				<r>2.</r>
			</p>
			<p>
				<s>(0|[3-9])TH</s>
				<r>$1.</r>
			</p>
			<p>
				<s>OF</s>
				<r/>
			</p>
			<p>
				<s>UND|U\.</s>
				<r>;</r>
			</p>
			<p>
				<s>BIS</s>
				<r>-</r>
			</p>
			<p>
				<s>JH(DT)?\.</s>
				<r>JHD.</r>
			</p>
			<p>
				<s>SAEC\.</s>
				<r>SEC.</r>
			</p>
			<p>
				<s>SÄC\.</s>
				<r>SEC.</r>
			</p>
			<p>
				<s>SEC\.([0-9]+)</s>
				<r>$1.JHD.</r>
			</p>
			<p>
				<s>QUARTAL</s>
				<r>QU</r>
			</p>
			<p>
				<s/>
				<r/>
			</p>
			<p>
				<s>\(|\)|\[|\]</s>
				<r>;</r>
			</p>
			<p>
				<s>^;+|;+$</s>
				<r/>
			</p>
			<p>
				<s>;+</s>
				<r>;</r>
			</p>
		</xsl:variable>
		<xsl:sequence select="tools:replace($prepared, $replace-list/s/string(), $replace-list/r/string() )"/>
	</xsl:function>
	<xsl:function name="tools:replace" as="xs:string">
		<xsl:param name="input" as="xs:string?"/>
		<xsl:param name="pattern" as="xs:string*"/>
		<xsl:param name="replacement" as="xs:string*"/>
		<xsl:param name="flags" as="xs:string?"/>
		<xsl:sequence select="intern:replace($input, $pattern, $replacement, $flags, false())"/>
	</xsl:function>
	<xsl:function name="intern:replace" as="xs:string">
		<xsl:param name="input" as="xs:string?"/>
		<xsl:param name="pattern" as="xs:string*"/>
		<xsl:param name="replacement" as="xs:string*"/>
		<xsl:param name="flags" as="xs:string?"/>
		<xsl:param name="recurse" as="xs:boolean"/>
		<xsl:choose>
			<xsl:when test="exists($pattern[1])">
				<xsl:sequence select=" intern:replace( if (boolean($pattern[1]) ) then if ($recurse) then intern:recursive-replace($input, $pattern[1], string($replacement[1]), $flags) else replace($input, $pattern[1], string($replacement[1]), $flags) else $input, $pattern[position() gt 1], $replacement[position() gt 1], $flags, $recurse )"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:sequence select="string($input)"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:function>
	<xsl:function name="tools:replace" as="xs:string">
		<xsl:param name="input" as="xs:string?"/>
		<xsl:param name="pattern" as="xs:string*"/>
		<xsl:param name="replacement" as="xs:string*"/>
		<xsl:sequence select="tools:replace($input, $pattern, $replacement, '')"/>
	</xsl:function>
	<xsl:function name="tools:recursive-replace" as="xs:string">
		<xsl:param name="input" as="xs:string?"/>
		<xsl:param name="pattern" as="xs:string*"/>
		<xsl:param name="replacement" as="xs:string*"/>
		<xsl:param name="flags" as="xs:string?"/>
		<xsl:sequence select="intern:replace($input, $pattern, $replacement, $flags, true())"/>
	</xsl:function>
	<xsl:function name="tools:recursive-replace" as="xs:string">
		<xsl:param name="input" as="xs:string?"/>
		<xsl:param name="pattern" as="xs:string*"/>
		<xsl:param name="replacement" as="xs:string*"/>
		<xsl:sequence select="tools:recursive-replace($input, $pattern, $replacement, '')"/>
	</xsl:function>
	<xsl:function name="intern:recursive-replace" as="xs:string">
		<xsl:param name="input" as="xs:string?"/>
		<xsl:param name="pattern" as="xs:string*"/>
		<xsl:param name="replacement" as="xs:string*"/>
		<xsl:param name="flags" as="xs:string?"/>
		<xsl:param name="remaining-recursions" as="xs:integer"/>
		<xsl:choose>
			<xsl:when test="$remaining-recursions eq 0">
				<xsl:sequence select="string($input)"/>
				<xsl:call-template name="misc:message">
					<xsl:with-param name="level">FATAL</xsl:with-param>
					<xsl:with-param name="show-context" select="false()"/>
					<xsl:with-param name="message">[mapping-common-tools.xsl#intern:recursive-replace] reached maximum count of 
						<xsl:value-of select="$intern:maximum-depth-for-recursions"/> for recursive replacements
					</xsl:with-param>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="matches($input, $pattern, $flags)">
				<xsl:sequence select="intern:recursive-replace(replace($input, $pattern, $replacement, $flags), $pattern, $replacement, $flags, $remaining-recursions - 1)"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:sequence select="string($input)"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:function>
	<xsl:function name="intern:recursive-replace" as="xs:string">
		<xsl:param name="input" as="xs:string?"/>
		<xsl:param name="pattern" as="xs:string*"/>
		<xsl:param name="replacement" as="xs:string*"/>
		<xsl:param name="flags" as="xs:string?"/>
		<xsl:sequence select="intern:recursive-replace($input, $pattern, $replacement, $flags, $intern:maximum-depth-for-recursions)"/>
	</xsl:function>
	<xsl:function name="tools:start-date-and-end-date" as="xs:string+">
		<xsl:param name="input" as="xs:string?"/>
		<xsl:variable name="parsed-date" as="xs:string+" select="tools:parse-dates-as-W3CDTF($input)"/>
		<xsl:sequence select="tools:earliest-day($parsed-date), tools:latest-day($parsed-date)"/>
	</xsl:function>
	<xsl:function name="tools:is-W3CDTF" as="xs:boolean">
		<xsl:param name="input" as="xs:string?"/>
		<xsl:sequence select=" ($input castable as xs:gYear) or ($input castable as xs:gYearMonth) or ($input castable as xs:date) "/>
	</xsl:function>
	<xsl:function name="tools:earliest-day" as="xs:string">
		<xsl:param name="sequence-of-W3CDTF-dates" as="xs:string*"/>
		<xsl:sequence select="string(min( (for $i in ($sequence-of-W3CDTF-dates, '') return tokenize($i, '[,/] ?') )[tools:is-W3CDTF(.)] ) )"/>
	</xsl:function>
	<xsl:function name="tools:latest-day" as="xs:string">
		<xsl:param name="sequence-of-W3CDTF-dates" as="xs:string*"/>
		<xsl:sequence select="string(max( (for $i in ($sequence-of-W3CDTF-dates, '') return tokenize($i, '[,/] ?') )[tools:is-W3CDTF(.)] ) )"/>
	</xsl:function>
	<xsl:function name="tools:parse-dates-as-W3CDTF" as="xs:string+">
		<xsl:param name="input" as="xs:string?"/>
		<xsl:sequence select="intern:parse-dates-as-W3CDTF(intern:normalize-date-string($input) )"/>
	</xsl:function>
	<xsl:function name="intern:parse-dates-as-W3CDTF" as="xs:string+">
		<xsl:param name="input" as="xs:string?"/>
		<xsl:variable name="recursive-replacements" as="element(p)+">
			<p>
				<s>(JAN|FEB|MRZ|APR|MAI|JUN|JUL|AUG|SEP|OKT|NOV|DEZ)[/\-]([0-9]{1,2})(\.)?(JAN|FEB|MRZ|APR|MAI|JUN|JUL|AUG|SEP|OKT|NOV|DEZ)([0-9]{4})</s>
				<r>$1$5-$2$3$4$5</r>
			</p>
			<p>
				<s>(JAN|FEB|MRZ|APR|MAI|JUN|JUL|AUG|SEP|OKT|NOV|DEZ)[;]([0-9]{1,2})(\.)?(JAN|FEB|MRZ|APR|MAI|JUN|JUL|AUG|SEP|OKT|NOV|DEZ)([0-9]{4})</s>
				<r>$1$5;$2$3$4$5</r>
			</p>
			<p>
				<s>(JAN|FEB|MRZ|APR|MAI|JUN|JUL|AUG|SEP|OKT|NOV|DEZ)[,]([0-9]{1,2})(\.)?(JAN|FEB|MRZ|APR|MAI|JUN|JUL|AUG|SEP|OKT|NOV|DEZ)([0-9]{4})</s>
				<r>$1$5,$2$3$4$5</r>
			</p>
			<p>
				<s>(^|[^0-9])(([0-9]{1,2})(\.)?)[/\-](([0-9]{1,2})(\.)?)((JAN|FEB|MRZ|APR|MAI|JUN|JUL|AUG|SEP|OKT|NOV|DEZ)([0-9]{4}))</s>
				<r>$1$2$8-$5$8</r>
			</p>
			<p>
				<s>(^|[^0-9])(([0-9]{1,2})(\.)?)[;,](([0-9]{1,2})(\.)?)((JAN|FEB|MRZ|APR|MAI|JUN|JUL|AUG|SEP|OKT|NOV|DEZ)([0-9]{4}))</s>
				<r>$1$2$8;$5$8</r>
			</p>
			<p>
				<s>(^|[^0-9])([0-9]{1,2})(\.)?(JAN|FEB|MRZ|APR|MAI|JUN|JUL|AUG|SEP|OKT|NOV|DEZ)[/\-]([0-9]{1,2})(\.)?(JAN|FEB|MRZ|APR|MAI|JUN|JUL|AUG|SEP|OKT|NOV|DEZ)([0-9]{4})</s>
				<r>$1$2$3$4$8-$5$6$7$8</r>
			</p>
			<p>
				<s>(^|[^0-9])([0-9]{1,2})(\.)?(JAN|FEB|MRZ|APR|MAI|JUN|JUL|AUG|SEP|OKT|NOV|DEZ)[;,]([0-9]{1,2})(\.)?(JAN|FEB|MRZ|APR|MAI|JUN|JUL|AUG|SEP|OKT|NOV|DEZ)([0-9]{4})</s>
				<r>$1$2$3$4$8;$5$6$7$8</r>
			</p>
			<p>
				<s>(JAN|FEB|MRZ|APR|MAI|JUN|JUL|AUG|SEP|OKT|NOV|DEZ)[/\-](JAN|FEB|MRZ|APR|MAI|JUN|JUL|AUG|SEP|OKT|NOV|DEZ)([0-9]{4})</s>
				<r>$1$3-$2$3</r>
			</p>
			<p>
				<s>(JAN|FEB|MRZ|APR|MAI|JUN|JUL|AUG|SEP|OKT|NOV|DEZ)[;,](JAN|FEB|MRZ|APR|MAI|JUN|JUL|AUG|SEP|OKT|NOV|DEZ)([0-9]{4})</s>
				<r>$1$3;$2$3</r>
			</p>
			<p>
				<s>^([0-9]{4})(JAN|FEB|MRZ|APR|MAI|JUN|JUL|AUG|SEP|OKT|NOV|DEZ)([0-9]{1,2})(\.)?[/\-](JAN|FEB|MRZ|APR|MAI|JUN|JUL|AUG|SEP|OKT|NOV|DEZ)([0-9]{1,2})(\.)?(,([0-9]{1,2})(\.)?)*$</s>
				<r>$3$4$2$1-$6$7$5$1</r>
			</p>
			<p>
				<s>^([0-9]{4})(JAN|FEB|MRZ|APR|MAI|JUN|JUL|AUG|SEP|OKT|NOV|DEZ)([0-9]{1,2})(\.)?[;,](JAN|FEB|MRZ|APR|MAI|JUN|JUL|AUG|SEP|OKT|NOV|DEZ)([0-9]{1,2})(\.)?(,([0-9]{1,2})(\.)?)*$</s>
				<r>$3$4$2$1;$6$7$5$1</r>
			</p>
			<p>
				<s>^([0-9]{4})(JAN|FEB|MRZ|APR|MAI|JUN|JUL|AUG|SEP|OKT|NOV|DEZ)([0-9]{1,2})(\.)?[/\-]([0-9]{1,2})(\.)?(,([0-9]{1,2})(\.)?)*$</s>
				<r>$3$4$2$1-$5$6$2$1</r>
			</p>
			<p>
				<s>^([0-9]{4})(JAN|FEB|MRZ|APR|MAI|JUN|JUL|AUG|SEP|OKT|NOV|DEZ)([0-9]{1,2})(\.)?[;,]([0-9]{1,2})(\.)?(,([0-9]{1,2})(\.)?)*$</s>
				<r>$3$4$2$1;$5$6$2$1</r>
			</p>
			<p>
				<s>([0-9]{4})(JAN|FEB|MRZ|APR|MAI|JUN|JUL|AUG|SEP|OKT|NOV|DEZ)([0-9]{1,2})(\.)?</s>
				<r>$3$4$2$1</r>
			</p>
			<p>
				<s>([0-9]{4})(JAN|FEB|MRZ|APR|MAI|JUN|JUL|AUG|SEP|OKT|NOV|DEZ)</s>
				<r>$2$1</r>
			</p>
			<p>
				<s>([1-9][0-9]?\.)([/;,\-])([1-9][0-9]?\.)(JHD\.)</s>
				<r>$1$4$2$3$4</r>
			</p>
			<p>
				<s>(I{1,3}|IV)\.?[/\-](I{1,3}|IV)\.?([0-9]{4})</s>
				<r>$1QU$3#$2QU$3</r>
			</p>
			<p>
				<s>(I{1,3}|IV)/([0-9]{4})</s>
				<r>$1QU$2</r>
			</p>
			<p>
				<s>((I{1,3}|IV)|([1-4]))\.?([/\-])((I{1,3}|IV)|([1-4]))\.?QU([0-9]{4})</s>
				<r>$1QU$8#$5QU$8</r>
			</p>
			<p>
				<s>((I{1,3}|IV)|([1-4]))\.?([;,])((I{1,3}|IV)|([1-4]))\.?QU([0-9]{4})</s>
				<r>$1QU$8$4$5QU$8</r>
			</p>
			<p>
				<s>((I{1,3}|IV)|([1-4]))\.QU</s>
				<r>$1QU</r>
			</p>
			<p>
				<s>1QU</s>
				<r>IQU</r>
			</p>
			<p>
				<s>2QU</s>
				<r>IIQU</r>
			</p>
			<p>
				<s>3QU</s>
				<r>IIIQU</r>
			</p>
			<p>
				<s>4QU</s>
				<r>IVQU</r>
			</p>
			<p>
				<s>IVQU([0-9]{4})</s>
				<r>1OKT$1-31DEZ$1</r>
			</p>
			<p>
				<s>IIIQU([0-9]{4})</s>
				<r>1JUL$1-30SEP$1</r>
			</p>
			<p>
				<s>IIQU([0-9]{4})</s>
				<r>1APR$1-30JUN$1</r>
			</p>
			<p>
				<s>IQU([0-9]{4})</s>
				<r>1JAN$1-31MRZ$1</r>
			</p>
			<p>
				<s>/</s>
				<r>-</r>
			</p>
			<p>
				<s>,</s>
				<r>;</r>
			</p>
		</xsl:variable>
		<xsl:variable name="prepared" as="xs:string?" select="tools:recursive-replace($input, $recursive-replacements/s/string(), $recursive-replacements/r/string())"/>
		<xsl:variable name="vorläufiges-ergebnis" as="xs:string*">
			<xsl:choose>
				<xsl:when test="not($prepared)"/>
				<xsl:when test="matches($input, '^([0-9]{4})$')">
					<xsl:sequence select="concat($input, '/', $input)"/>
				</xsl:when>
				<xsl:when test="matches($prepared, '^([0-9]{4})-(0?[1-9]|1[012])$')">
					<xsl:variable name="year" as="xs:string" select="substring($prepared, 1, 4)"/>
					<xsl:variable name="month" as="xs:string" select="substring-after($prepared, '-')"/>
					<xsl:sequence select="concat($year, '-', $month, '/', $year, '-', $month)"/>
				</xsl:when>
				<xsl:when test="matches($prepared, '^(JAN|FEB|MRZ|APR|MAI|JUN|JUL|AUG|SEP|OKT|NOV|DEZ)([0-9]{4})$')">
					<xsl:variable name="year" as="xs:string" select="translate($prepared, '^(JAN|FEB|MRZ|APR|MAI|JUN|JUL|AUG|SEP|OKT|NOV|DEZ)', '')"/>
					<xsl:variable name="month" as="xs:string" select="replace(replace($prepared, '[1-9][0-9]{0,3}$', ''), '(JAN|FEB|MRZ|APR|MAI|JUN|JUL|AUG|SEP|OKT|NOV|DEZ)', '$1')"/>
					<xsl:sequence select="concat($year, '-', intern:month-to-integer-string($month), '/', $year, '-', intern:month-to-integer-string($month))"/>
				</xsl:when>
				<xsl:when test="matches($prepared, '^([0-9]{1,2})(\.)?(JAN|FEB|MRZ|APR|MAI|JUN|JUL|AUG|SEP|OKT|NOV|DEZ)([0-9]{4})$')">
					<xsl:variable name="year" as="xs:string" select="replace($prepared, '^([0-9]{1,2})(\.)?(JAN|FEB|MRZ|APR|MAI|JUN|JUL|AUG|SEP|OKT|NOV|DEZ)', '')"/>
					<xsl:variable name="month" as="xs:string" select="replace(replace($prepared, '^([0-9]{1,2})(\.)?|([0-9]{4})$', ''), '(JAN|FEB|MRZ|APR|MAI|JUN|JUL|AUG|SEP|OKT|NOV|DEZ)', '$1')"/>
					<xsl:variable name="day" as="xs:string" select="replace($prepared, '(\.)?(JAN|FEB|MRZ|APR|MAI|JUN|JUL|AUG|SEP|OKT|NOV|DEZ)([0-9]{4})$', '')"/>
					<xsl:sequence select="concat( intern:date-string($year, intern:month-to-integer-string($month), $day), '/', intern:date-string($year, intern:month-to-integer-string($month), $day) )"/>
				</xsl:when>
				<xsl:when test="string(intern:parse-date($prepared) )">
					<xsl:variable name="temp" as="xs:string" select="string(intern:parse-date($prepared) )"/>
					<xsl:sequence select="concat($temp, '/', $temp)"/>
				</xsl:when>
				<xsl:when test="matches($prepared, '^([0-9]{4})-([0-9]{4})$')">
					<xsl:variable name="temp" as="xs:string+" select="tokenize($prepared, '-')"/>
					<xsl:sequence select="concat($temp[1], '/', $temp[2])"/>
				</xsl:when>
				<xsl:when test="matches($prepared, '^([0-9]{4})-(0?[1-9]|1[012])-([0-9]{4})-(0?[1-9]|1[012])$')">
					<xsl:variable name="year1" as="xs:string" select="substring($prepared, 1, 4)"/>
					<xsl:variable name="month1" as="xs:string" select="substring($prepared, 6, 2)"/>
					<xsl:variable name="year2" as="xs:string" select="substring($prepared, 9, 4)"/>
					<xsl:variable name="month2" as="xs:string" select="substring($prepared, 14, 2)"/>
					<xsl:sequence select="concat($year1, '-', $month1, '/', $year2, '-', $month2)"/>
				</xsl:when>
				<xsl:when test="matches($prepared, '^([0-9]{4})-(0?[1-9]|1[012])-([0-9]{1,2})(\.)?-([0-9]{4})-(0?[1-9]|1[012])-([0-9]{1,2})(\.)?$')">
					<xsl:variable name="year1" as="xs:string" select="substring($prepared, 1, 4)"/>
					<xsl:variable name="month1" as="xs:string" select="substring($prepared, 6, 2)"/>
					<xsl:variable name="days1" as="xs:string" select="substring($prepared, 9, 2)"/>
					<xsl:variable name="year2" as="xs:string" select="substring($prepared, 12, 4)"/>
					<xsl:variable name="month2" as="xs:string" select="substring($prepared, 17, 2)"/>
					<xsl:variable name="days2" as="xs:string" select="substring($prepared, 20, 2)"/>
					<xsl:sequence select="concat($year1, '-', $month1, '-', $days1, '/', $year2, '-', $month2, '-', $days2)"/>
				</xsl:when>
				<xsl:when test="matches($prepared, '^(JAN|FEB|MRZ|APR|MAI|JUN|JUL|AUG|SEP|OKT|NOV|DEZ)([0-9]{4})-(JAN|FEB|MRZ|APR|MAI|JUN|JUL|AUG|SEP|OKT|NOV|DEZ)([0-9]{4})$')">
					<xsl:sequence select="concat( substring($prepared, 4,4), '-', intern:month-to-integer-string(substring($prepared, 1, 3) ), '/', substring($prepared, 12,4), '-', intern:month-to-integer-string(substring($prepared, 9, 3) ) )"/>
				</xsl:when>
				<xsl:when test="matches($prepared, '^([0-9]{1,2})(\.)?(JAN|FEB|MRZ|APR|MAI|JUN|JUL|AUG|SEP|OKT|NOV|DEZ)([0-9]{4})-([0-9]{1,2})(\.)?(JAN|FEB|MRZ|APR|MAI|JUN|JUL|AUG|SEP|OKT|NOV|DEZ)([0-9]{4})$')">
					<xsl:variable name="temp" as="xs:string+" select="tokenize($prepared, '-')"/>
					<xsl:sequence select="concat(intern:parse-date($temp[1]), '/', intern:parse-date($temp[2]) )"/>
				</xsl:when>
				<xsl:when test="normalize-space(intern:century-to-normalized-date($prepared) )">
					<xsl:sequence select="intern:century-to-normalized-date($prepared)"/>
				</xsl:when>
				<xsl:when test="matches($prepared, '^[1-9][0-9]?\.JHD\.-[1-9][0-9]?\.JHD\.$')">
					<xsl:sequence select="concat( substring-before(intern:century-to-normalized-date(substring-before($prepared, '-') ), '/'), '/', substring-after(intern:century-to-normalized-date(substring-after($prepared, '-') ), '/') )"/>
				</xsl:when>
				<xsl:when test="intern:parse-dates-as-W3CDTF(substring-before($prepared, '#') ) and intern:parse-dates-as-W3CDTF(substring-after($prepared, '#') )">
					<xsl:variable name="start" as="xs:string" select="substring-before(tokenize(intern:parse-dates-as-W3CDTF(substring-before($prepared, '#') ), ',')[1], '/')"/>
					<xsl:variable name="end" as="xs:string" select="substring-after(tokenize(intern:parse-dates-as-W3CDTF(substring-after($prepared, '#') ), ',')[last()], '/')"/>
					<xsl:sequence select="concat($start, '/', $end)"/>
				</xsl:when>
				<xsl:when test="$input ne intern:replace-month-integers-with-month-names($input) and normalize-space(intern:parse-dates-as-W3CDTF(intern:replace-month-integers-with-month-names($input) ) )">
					<xsl:sequence select="intern:parse-dates-as-W3CDTF(intern:replace-month-integers-with-month-names($input) )"/>
				</xsl:when>
				<xsl:when test="contains($prepared, ';') and (every $i in tokenize($prepared, ';') satisfies intern:parse-dates-as-W3CDTF($i) )">
					<xsl:sequence select="string-join(for $i in tokenize($prepared, ';') return intern:parse-dates-as-W3CDTF($i), ', ')"/>
				</xsl:when>
				<xsl:when test="contains($prepared, ',') and (every $i in tokenize($prepared, ',') satisfies intern:parse-dates-as-W3CDTF($i) )">
					<xsl:sequence select="string-join(for $i in tokenize($prepared, ',') return intern:parse-dates-as-W3CDTF($i), ', ')"/>
				</xsl:when>
				<xsl:when test="matches($input, '[,;]') and (every $i in tokenize($input, '[,;]') satisfies intern:parse-dates-as-W3CDTF($i) )">
					<xsl:sequence select="string-join(for $i in tokenize($input, '[,;]') return intern:parse-dates-as-W3CDTF($i), ', ')"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:call-template name="misc:message">
						<xsl:with-param name="level">DEBUG</xsl:with-param>
						<xsl:with-param name="show-context" select="false()"/>
						<xsl:with-param name="message">[mapping-common-tools.xsl#intern:parse-dates-as-W3CDTF] no match for $prepared = "
							<xsl:value-of select="$prepared"/>"
						</xsl:with-param>
					</xsl:call-template>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:choose>
			<xsl:when test="normalize-space(string-join($vorläufiges-ergebnis, '') ) and (every $i in (for $j in $vorläufiges-ergebnis return tokenize($j, '[,/]') ) satisfies tools:is-W3CDTF(normalize-space($i) ) )">
				<xsl:sequence select="$vorläufiges-ergebnis"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:sequence select="''"/>
				<xsl:call-template name="misc:message">
					<xsl:with-param name="level">INFO</xsl:with-param>
					<xsl:with-param name="show-context" select="false()"/>
					<xsl:with-param name="message">[mapping-common-tools.xsl#intern:parse-dates-as-W3CDTF] can't parse string "
						<xsl:value-of select="$input"/>" to W3CDTF date(s)
					</xsl:with-param>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:function>
	<xsl:function name="tools:last-day-of-month" as="xs:string">
		<xsl:param name="YYYY-year" as="xs:string?"/>
		<xsl:param name="month" as="xs:string?"/>
		<xsl:variable name="temp-month" as="xs:string?" select="intern:normalize-date-string($month)"/>
		<xsl:choose>
			<xsl:when test="$temp-month = ('JAN', 'MRZ', 'MAI', 'JUL', 'AUG', 'OKT', 'DEZ')">
				<xsl:sequence select=" '31' "/>
			</xsl:when>
			<xsl:when test="$temp-month = ('APR', 'JUN', 'SEP', 'NOV')">
				<xsl:sequence select=" '30' "/>
			</xsl:when>
			<xsl:when test="$temp-month = ('FEB')">
				<xsl:choose>
					<xsl:when test="concat($YYYY-year, '-', intern:month-to-integer-string($month), '-29') castable as xs:date">
						<xsl:sequence select=" '29' "/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:sequence select=" '28' "/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:otherwise>
				<xsl:sequence select=" '' "/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:function>
	<xsl:function name="intern:century-to-normalized-date" as="xs:string">
		<xsl:param name="input" as="xs:string?"/>
		<xsl:variable name="prepared" as="xs:string?" select="replace($input, '\.?JHD.?', '')"/>
		<xsl:choose>
			<xsl:when test="$prepared castable as xs:integer">
				<xsl:sequence select="concat( intern:date-string(xs:string((xs:integer($prepared) - 1) * 100 + 1 ), '1', '1'), '/', intern:date-string(xs:string(xs:integer($prepared)*100 ), '12', '31') )"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:sequence select="''"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:function>
	<xsl:function name="intern:replace-month-integers-with-month-names" as="xs:string">
		<xsl:param name="date-string" as="xs:string?"/>
		<xsl:variable name="temp" as="xs:string*">
			<xsl:analyze-string select="$date-string" regex="\.(0?[1-9]|1[012])\.([0-9][0-9][0-9][0-9])">
				<xsl:matching-substring>
					<xsl:variable name="tokenized-matching-substring" as="xs:string+" select="tokenize(., '\.')"/>
					<xsl:sequence select="concat('.', intern:integer-string-to-month($tokenized-matching-substring[2]), $tokenized-matching-substring[3])"/>
				</xsl:matching-substring>
				<xsl:non-matching-substring>
					<xsl:sequence select="."/>
				</xsl:non-matching-substring>
			</xsl:analyze-string>
		</xsl:variable>
		<xsl:sequence select="string-join($temp, '')"/>
	</xsl:function>
	<xsl:function name="intern:integer-string-to-month" as="xs:string">
		<xsl:param name="integer-string" as="xs:string"/>
		<xsl:choose>
			<xsl:when test="($integer-string castable as xs:integer) and ((xs:integer($integer-string) ge 1) and (xs:integer($integer-string) le 12) )">
				<xsl:sequence select="tokenize(translate('(JAN|FEB|MRZ|APR|MAI|JUN|JUL|AUG|SEP|OKT|NOV|DEZ)', '()', ''), '\|')[xs:integer($integer-string)]"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:sequence select="$integer-string"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:function>
	<xsl:function name="tools:logging-level" as="xs:integer">
		<xsl:param name="verbal-logging-level" as="xs:string?"/>
		<xsl:variable name="temp" as="xs:integer?" select="index-of( ('ALL', 'TRACE', 'DEBUG', 'INFO', 'WARN', 'ERROR', 'FATAL'), concat('', upper-case($verbal-logging-level) ) )"/>
		<xsl:choose>
			<xsl:when test="$temp">
				<xsl:sequence select="$temp - 1"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:sequence select="4"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:function>
</xsl:stylesheet>