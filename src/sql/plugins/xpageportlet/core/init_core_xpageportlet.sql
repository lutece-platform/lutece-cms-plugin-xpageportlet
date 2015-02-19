--
-- Dumping data for table core_portlet_type
--
INSERT INTO core_portlet_type (id_portlet_type,name,url_creation,url_update,home_class,plugin_name,url_docreate,create_script,create_specific,create_specific_form,url_domodify,modify_script,modify_specific,modify_specific_form) VALUES 
('XPAGE_PORTLET','xpageportlet.portlet.name','plugins/xpageportlet/CreatePortletXPage.jsp','plugins/xpageportlet/ModifyPortletXPage.jsp','fr.paris.lutece.plugins.xpageportlet.business.portlet.XPagePortletHome','xpageportlet','plugins/xpageportlet/DoCreatePortletXPage.jsp','/admin/portlet/script_create_portlet.html','/admin/plugins/xpageportlet/create_portlet_xpage.html','','plugins/xpageportlet/DoModifyPortletXPage.jsp','/admin/portlet/script_modify_portlet.html','/admin/plugins/xpageportlet/modify_portlet_xpage.html','');


--
-- Dumping data for table core_style
--
INSERT INTO core_style (id_style,description_style,id_portlet_type,id_portal_component) VALUES (888,'xpageportlet','XPAGE_PORTLET',0);
 
 
--
-- Dumping data for table core_style_mode_stylesheet
--
INSERT INTO core_style_mode_stylesheet (id_style,id_mode,id_stylesheet) VALUES (888,0,888);


--
-- Dumping data for table core_stylesheet
--
INSERT INTO core_stylesheet (id_stylesheet, description, file_name, source) VALUES (888,'Rubrique XPage - Défaut','portlet_xpage.xsl','<?xml version=\"1.0\"?>\r\n<xsl:stylesheet version=\"1.0\" xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\">\r\n\r\n	<xsl:param name=\"site-path\" select=\"site-path\" />\r\n	<xsl:variable name=\"portlet-id\" select=\"portlet/portlet-id\" />\r\n\r\n	<xsl:template match=\"portlet\">\r\n	\r\n	<xsl:variable name=\"device_class\">\r\n	<xsl:choose>\r\n		<xsl:when test=\"string(display-on-small-device)=\'0\'\">hidden-phone</xsl:when>\r\n		<xsl:otherwise></xsl:otherwise>\r\n	</xsl:choose>\r\n	</xsl:variable>\r\n\r\n	<div class=\"portlet  ${device_class} append-bottom -lutece-border-radius\" id=\"portlet_id_{portlet-id}\">\r\n	        <xsl:if test=\"not(string(display-portlet-title)=\'1\')\">\r\n				<h3 class=\"portlet-header\">\r\n					<xsl:value-of disable-output-escaping=\"yes\" select=\"portlet-name\" />\r\n				</h3>\r\n				<br />\r\n	        </xsl:if>\r\n	\r\n			<div class=\"portlet-content\">\r\n				<xsl:value-of disable-output-escaping=\"yes\" select=\"xpage-content\" />\r\n			</div>\r\n		</div>\r\n	</xsl:template>\r\n\r\n</xsl:stylesheet>\r\n');
