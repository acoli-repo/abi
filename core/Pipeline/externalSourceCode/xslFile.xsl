<?xml version="1.0" ?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
   <xsl:output omit-xml-declaration="yes" indent="yes"/>
   <xsl:strip-space elements="*"/>


<xsl:variable name="max">
  <xsl:for-each select="/DOCUMENT/PAGE/TEXT/TOKEN/@font-size">
    	<xsl:sort select="count(//DOCUMENT/PAGE/TEXT/TOKEN/@font-size[number()=current()])" data-type="number" order="descending"/>
    	<xsl:if test="position() = 1"><xsl:value-of select="."/></xsl:if>
  </xsl:for-each>
</xsl:variable>

<!--<xsl:variable name="min">
  <xsl:for-each select="/DOCUMENT/PAGE/TEXT/TOKEN/@font-size">
    <xsl:sort select="." data-type="number" order="ascending"/>
    <xsl:if test="position() = 1"><xsl:value-of select="."/></xsl:if>
  </xsl:for-each>
</xsl:variable>
-->


   <xsl:template match="TOKEN">
	<xsl:variable name="show" select="."/>
        	<xsl:if test="contains($show, '¨')">
			<xsl:if test="@font-size = $max">
				<xsl:value-of select="." />
			</xsl:if>
		</xsl:if>
		<xsl:if test="not(contains($show, '¨'))">
			<xsl:if test="@font-size = $max">
				<xsl:value-of select="concat(.,' ')" />
			</xsl:if>
		</xsl:if>
<!--	Most common value: "<xsl:value-of select="$max"/>"-->
<!--        <xsl:if test="contains($show, '¨')"><xsl:if test="not(@font-size = $min) and not(@font-size = $max)"><xsl:value-of select="." /></xsl:if></xsl:if>
        <xsl:if test="not(contains($show, '¨'))"><xsl:if test="not(@font-size = $min) and not(@font-size = $max)"><xsl:value-of select="concat(.,' ')" /></xsl:if></xsl:if>
-->   </xsl:template>

   <xsl:template match="text()" />


</xsl:stylesheet>

