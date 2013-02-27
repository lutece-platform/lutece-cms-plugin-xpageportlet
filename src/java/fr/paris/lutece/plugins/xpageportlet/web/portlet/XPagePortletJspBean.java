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
package fr.paris.lutece.plugins.xpageportlet.web.portlet;

import fr.paris.lutece.plugins.xpageportlet.business.portlet.XPagePortlet;
import fr.paris.lutece.plugins.xpageportlet.service.portlet.IXPagePortletService;
import fr.paris.lutece.plugins.xpageportlet.service.portlet.XPagePortletService;
import fr.paris.lutece.portal.business.portlet.Portlet;
import fr.paris.lutece.portal.service.content.XPageAppService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.web.constants.Messages;
import fr.paris.lutece.portal.web.portlet.PortletJspBean;
import fr.paris.lutece.portal.web.xpages.XPageApplicationEntry;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.html.HtmlTemplate;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;


/**
 * XPage Portlet Jsp Bean
 */
public class XPagePortletJspBean extends PortletJspBean
{
    // MARKS
    private static final String MARK_XPAGE_PORTLET = "xPagePortlet";
    private static final String MARK_XPAGE_APPLICATIONS = "xPageApplications";

    // PARAMETERS
    private static final String PARAMETER_PARAM_KEY = "paramKey";
    private static final String PARAMETER_PARAM_VALUE = "paramValue";
    private static final String PARAMETER_PARAM_XPAGE_NAME = "XPageName";
    private static final String PARAMETER_PARAM_NB_PARAMS = "nbParams";

    // SERVICES
    private IXPagePortletService _xPagePorletService = SpringContextService.getBean( XPagePortletService.BEAN_SERVICE );

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCreate( HttpServletRequest request )
    {
        String strPageId = request.getParameter( PARAMETER_PAGE_ID );
        String strPortletTypeId = request.getParameter( PARAMETER_PORTLET_TYPE_ID );

        Map<String, Object> model = new HashMap<String, Object>(  );
        model.put( MARK_XPAGE_APPLICATIONS, getXPageApplications(  ) );

        HtmlTemplate template = getCreateTemplate( strPageId, strPortletTypeId, model );

        return template.getHtml(  );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getModify( HttpServletRequest request )
    {
        String strPortletId = request.getParameter( PARAMETER_PORTLET_ID );

        if ( StringUtils.isNotBlank( strPortletId ) && StringUtils.isNumeric( strPortletId ) )
        {
            int nPortletId = Integer.parseInt( strPortletId );
            XPagePortlet portlet = _xPagePorletService.getPortlet( nPortletId );

            if ( portlet != null )
            {
                Map<String, Object> model = new HashMap<String, Object>(  );
                model.put( MARK_XPAGE_PORTLET, portlet );
                model.put( MARK_XPAGE_APPLICATIONS, getXPageApplications(  ) );

                HtmlTemplate template = getModifyTemplate( portlet, model );

                return template.getHtml(  );
            }
        }

        return StringUtils.EMPTY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String doCreate( HttpServletRequest request )
    {
        String strUrl;
        String strIdPage = request.getParameter( PARAMETER_PAGE_ID );

        if ( StringUtils.isNotBlank( strIdPage ) && StringUtils.isNumeric( strIdPage ) )
        {
            XPagePortlet portlet = new XPagePortlet(  );

            int nIdPage = Integer.parseInt( strIdPage );
            portlet.setPageId( nIdPage );

            // Getting portlet's common attibuts
            strUrl = setPortletCommonData( request, portlet );

            if ( StringUtils.isBlank( strUrl ) )
            {
                portlet.setXPageName( request.getParameter(PARAMETER_PARAM_XPAGE_NAME));
                portlet.setNbParams( Integer.parseInt( request.getParameter(PARAMETER_PARAM_NB_PARAMS)));

                // Creating portlet
                _xPagePorletService.create( portlet );

                // Displays the page with the new Portlet
                strUrl = getPageUrl( portlet.getPageId(  ) );
            }
        }
        else
        {
            strUrl = AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_ERROR );
        }

        return strUrl;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String doModify( HttpServletRequest request )
    {
        String strUrl = StringUtils.EMPTY;
        String strIdPortlet = request.getParameter( PARAMETER_PORTLET_ID );

        if ( StringUtils.isNotBlank( strIdPortlet ) && StringUtils.isNumeric( strIdPortlet ) )
        {
            int nIdPortlet = Integer.parseInt( strIdPortlet );
            XPagePortlet portlet = _xPagePorletService.getPortlet( nIdPortlet );

            if ( portlet != null )
            {
                // Getting portlet's common attibuts
                strUrl = setPortletCommonData( request, portlet );

                if ( StringUtils.isBlank( strUrl ) )
                {
                    portlet.setXPageName( request.getParameter(PARAMETER_PARAM_XPAGE_NAME));
                    portlet.setNbParams( Integer.parseInt( request.getParameter(PARAMETER_PARAM_NB_PARAMS)));
                    setPortletData( request, portlet );

                    // Creating portlet
                    _xPagePorletService.update( portlet );

                    // Displays the page with the new Portlet
                    strUrl = getPageUrl( portlet.getPageId(  ) );
                }
            }
        }
        else
        {
            strUrl = AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_ERROR );
        }

        return strUrl;
    }

    /**
     * Get the list of xpage applications
     * @return a {@link ReferenceList}
     */
    private ReferenceList getXPageApplications(  )
    {
        ReferenceList listXPageApp = new ReferenceList(  );

        for ( XPageApplicationEntry entry : XPageAppService.getXPageApplicationsList(  ) )
        {
            listXPageApp.addItem( entry.getId(  ), entry.getId(  ) );
        }

        return listXPageApp;
    }

    /**
     * Set the portlet data
     * @param request the HTTP request
     * @param portlet the portlet
     */
    private void setPortletData( HttpServletRequest request, Portlet portlet )
    {
        XPagePortlet xPagePortlet = (XPagePortlet) portlet;
        Map<String, List<String>> map = new HashMap<String, List<String>>(  );

        if ( xPagePortlet.getNbParams(  ) > 0 )
        {
            for ( int nIndex = 1; nIndex <= xPagePortlet.getNbParams(  ); nIndex++ )
            {
                String strParamKey = request.getParameter( PARAMETER_PARAM_KEY + nIndex );
                String strParamValue = request.getParameter( PARAMETER_PARAM_VALUE + nIndex );

                if ( StringUtils.isNotBlank( strParamKey ) && StringUtils.isNotBlank( strParamValue ) )
                {
                    List<String> listValues = map.get( strParamKey );

                    if ( listValues == null )
                    {
                        listValues = new ArrayList<String>(  );
                    }

                    listValues.add( strParamValue );
                    map.put( strParamKey, listValues );
                }
            }
        }

        xPagePortlet.setMapParameters( map );
    }
}
