<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:param name="site-path" select="site-path" />
	<xsl:variable name="portlet-id" select="portlet/portlet-id" />

	<xsl:template match="portlet">
	
	<xsl:variable name="device_class">
	<xsl:choose>
		<xsl:when test="string(display-on-small-device)='0'">hide-for-small</xsl:when>
		<xsl:otherwise></xsl:otherwise>
	</xsl:choose>
	</xsl:variable>

	<div class="portlet  $device_class} append-bottom -lutece-border-radius" id="portlet_id_{portlet-id}">
	        <xsl:if test="not(string(display-portlet-title)='1')">
				<h3 class="portlet-header">
					<xsl:value-of disable-output-escaping="yes" select="portlet-name" />
				</h3>
				<br />
	        </xsl:if>
	
			<div class="portlet-content">
				<xsl:value-of disable-output-escaping="yes" select="xpage-content" />
			</div>
		</div>
	</xsl:template>

</xsl:stylesheet>
