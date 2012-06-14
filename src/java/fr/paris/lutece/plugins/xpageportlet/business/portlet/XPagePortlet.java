/*
 * Copyright (c) 2002-2012, Mairie de Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.xpageportlet.business.portlet;

import fr.paris.lutece.plugins.xpageportlet.service.http.XPagePortletHttpServletRequest;
import fr.paris.lutece.plugins.xpageportlet.service.portlet.IXPagePortletService;
import fr.paris.lutece.plugins.xpageportlet.service.portlet.XPagePortletService;
import fr.paris.lutece.portal.business.portlet.Portlet;
import fr.paris.lutece.portal.service.content.XPageAppService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.SiteMessage;
import fr.paris.lutece.portal.service.message.SiteMessageException;
import fr.paris.lutece.portal.service.message.SiteMessageService;
import fr.paris.lutece.portal.service.security.UserNotSignedException;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.web.PortalJspBean;
import fr.paris.lutece.portal.web.xpages.XPage;
import fr.paris.lutece.portal.web.xpages.XPageApplicationEntry;
import fr.paris.lutece.util.xml.XmlUtil;

import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;


/**
 *
 * XPagePortlet
 *
 */
public class XPagePortlet extends Portlet
{
    private static final String TAG_XPAGE_NAME = "xpage-name";
    private static final String TAG_XPAGE_CONTENT = "xpage-content";
    private static final String MESSAGE_USER_NOT_SIGNED = "xpageportlet.message.userNotSigned";
    private String _strXPageName;
    private int _nNbParams;
    private Map<String, List<String>> _mapParameters;

    /**
     * {@inheritDoc}
     */
    @Override
    public String getXml( HttpServletRequest request )
    {
        StringBuffer sbXml = new StringBuffer(  );
        XmlUtil.addElement( sbXml, TAG_XPAGE_NAME, _strXPageName );

        XPageApplicationEntry entry = XPageAppService.getApplicationEntry( _strXPageName );

        if ( ( entry != null ) && ( entry.getApplication(  ) != null ) && ( request != null ) )
        {
            HttpServletRequest xPageRequest = new XPagePortletHttpServletRequest( request, _mapParameters );
            String strXPageContent = StringUtils.EMPTY;
            XPage page = null;

            try
            {
                page = entry.getApplication(  ).getPage( xPageRequest, PortalJspBean.MODE_HTML, entry.getPlugin(  ) );
            }
            catch ( UserNotSignedException e )
            {
                strXPageContent = I18nService.getLocalizedString( MESSAGE_USER_NOT_SIGNED, request.getLocale(  ) );
            }
            catch ( SiteMessageException e )
            {
                SiteMessage message = SiteMessageService.getMessage( request );

                if ( message != null )
                {
                    strXPageContent = message.getText( request.getLocale(  ) );
                }

                // Delete message in session
                SiteMessageService.cleanMessageSession( request );
            }

            if ( page != null )
            {
                strXPageContent = page.getContent(  );
            }

            XmlUtil.addElementHtml( sbXml, TAG_XPAGE_CONTENT, strXPageContent );
        }

        return addPortletTags( sbXml );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getXmlDocument( HttpServletRequest request )
        throws SiteMessageException
    {
        return XmlUtil.getXmlHeader(  ) + getXml( request );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void remove(  )
    {
        IXPagePortletService xPagePortletService = SpringContextService.getBean( XPagePortletService.BEAN_SERVICE );
        xPagePortletService.remove( this );
    }

    /**
     * @param strXPageName the strXPageName to set
     */
    public void setXPageName( String strXPageName )
    {
        this._strXPageName = strXPageName;
    }

    /**
     * @return the strXPageName
     */
    public String getXPageName(  )
    {
        return _strXPageName;
    }

    /**
     * @param nNbParams the _nNbParams to set
     */
    public void setNbParams( int nNbParams )
    {
        this._nNbParams = nNbParams;
    }

    /**
     * @return the _nNbParams
     */
    public int getNbParams(  )
    {
        return _nNbParams;
    }

    /**
     * @param mapParameters the mapParameters to set
     */
    public void setMapParameters( Map<String, List<String>> mapParameters )
    {
        this._mapParameters = mapParameters;
    }

    /**
     * @return the mapParameters
     */
    public Map<String, List<String>> getMapParameters(  )
    {
        return _mapParameters;
    }
}
