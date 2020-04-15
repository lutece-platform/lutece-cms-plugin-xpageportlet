/*
 * Copyright (c) 2002-2020, Mairie de Paris
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

import fr.paris.lutece.portal.business.portlet.IPortletInterfaceDAO;
import fr.paris.lutece.portal.business.portlet.Portlet;
import fr.paris.lutece.util.sql.DAOUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 *
 * XPagePortletDAO
 *
 */
public final class XPagePortletDAO implements IPortletInterfaceDAO
{
    private static final String SQL_QUERY_INSERT = " INSERT INTO xpageportlet_portlet (id_portlet, xpage_name, nb_params) VALUES ( ?,?,? ) ";
    private static final String SQL_QUERY_DELETE = " DELETE FROM xpageportlet_portlet WHERE id_portlet = ? ";
    private static final String SQL_QUERY_SELECT = " SELECT id_portlet, xpage_name, nb_params FROM xpageportlet_portlet WHERE id_portlet = ? ";
    private static final String SQL_QUERY_UPDATE = " UPDATE xpageportlet_portlet SET xpage_name = ?, nb_params = ? WHERE id_portlet = ? ";

    // PARAMS
    private static final String SQL_QUERY_SELECT_PARAMS = " SELECT param_key, param_value FROM xpageportlet_params WHERE id_portlet = ? ";
    private static final String SQL_QUERY_INSERT_PARAMS = " INSERT INTO xpageportlet_params ( id_portlet, param_key, param_value ) VALUES ( ?,?,? ) ";
    private static final String SQL_QUERY_DELETE_PARAMS = " DELETE FROM xpageportlet_params WHERE id_portlet = ? ";

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void insert( Portlet portlet )
    {
        XPagePortlet xPagePortlet = (XPagePortlet) portlet;

        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT );
        int nIndex = 1;

        daoUtil.setInt( nIndex++, xPagePortlet.getId( ) );
        daoUtil.setString( nIndex++, xPagePortlet.getXPageName( ) );
        daoUtil.setInt( nIndex++, xPagePortlet.getNbParams( ) );

        daoUtil.executeUpdate( );
        daoUtil.free( );

        insertParams( xPagePortlet );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete( int nIdPortlet )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE );
        daoUtil.setInt( 1, nIdPortlet );

        daoUtil.executeUpdate( );
        daoUtil.free( );

        // Remove the parameters
        deleteParameters( nIdPortlet );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Portlet load( int nIdPortlet )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT );
        daoUtil.setInt( 1, nIdPortlet );
        daoUtil.executeQuery( );

        XPagePortlet xPagePortlet = new XPagePortlet( );

        int nIndex = 1;

        if ( daoUtil.next( ) )
        {
            xPagePortlet.setId( daoUtil.getInt( nIndex++ ) );
            xPagePortlet.setXPageName( daoUtil.getString( nIndex++ ) );
            xPagePortlet.setNbParams( daoUtil.getInt( nIndex++ ) );
        }

        daoUtil.free( );

        xPagePortlet.setMapParameters( loadParameters( nIdPortlet ) );

        return xPagePortlet;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void store( Portlet portlet )
    {
        XPagePortlet xPagePortlet = (XPagePortlet) portlet;

        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE );
        int nIndex = 1;

        daoUtil.setString( nIndex++, xPagePortlet.getXPageName( ) );
        daoUtil.setInt( nIndex++, xPagePortlet.getNbParams( ) );

        daoUtil.setInt( nIndex++, xPagePortlet.getId( ) );

        daoUtil.executeUpdate( );
        daoUtil.free( );

        // First delete the parameters
        deleteParameters( portlet.getId( ) );
        // Then insert the parameters
        insertParams( xPagePortlet );
    }

    /**
     * Load the parameters
     * 
     * @param nIdPortlet
     *            the id portlet
     * @return the parameters
     */
    private Map<String, List<String>> loadParameters( int nIdPortlet )
    {
        Map<String, List<String>> map = new HashMap<String, List<String>>( );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_PARAMS );
        daoUtil.setInt( 1, nIdPortlet );
        daoUtil.executeQuery( );

        while ( daoUtil.next( ) )
        {
            int nIndex = 1;
            String strKey = daoUtil.getString( nIndex++ );
            String strValue = daoUtil.getString( nIndex++ );
            List<String> listValues = map.get( strKey );

            if ( listValues == null )
            {
                listValues = new ArrayList<String>( );
            }

            listValues.add( strValue );
            map.put( strKey, listValues );
        }

        daoUtil.free( );

        return map;
    }

    /**
     * Insert parameters
     * 
     * @param xPagePortlet
     *            the portlet
     */
    private synchronized void insertParams( XPagePortlet xPagePortlet )
    {
        Map<String, List<String>> mapParams = xPagePortlet.getMapParameters( );

        if ( mapParams != null )
        {
            for ( Entry<String, List<String>> param : mapParams.entrySet( ) )
            {
                String strParamKey = param.getKey( );

                for ( String strParamValue : param.getValue( ) )
                {
                    DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT_PARAMS );
                    int nIndex = 1;

                    daoUtil.setInt( nIndex++, xPagePortlet.getId( ) );
                    daoUtil.setString( nIndex++, strParamKey );
                    daoUtil.setString( nIndex++, strParamValue );

                    daoUtil.executeUpdate( );
                    daoUtil.free( );
                }
            }
        }
    }

    /**
     * Delete the parameters
     * 
     * @param nIdPortlet
     *            the id portlet
     */
    private void deleteParameters( int nIdPortlet )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_PARAMS );
        daoUtil.setInt( 1, nIdPortlet );

        daoUtil.executeUpdate( );
        daoUtil.free( );
    }
}
