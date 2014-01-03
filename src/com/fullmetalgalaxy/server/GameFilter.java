/* *********************************************************************
 *
 *  This file is part of Full Metal Galaxy.
 *  http://www.fullmetalgalaxy.com
 *
 *  Full Metal Galaxy is free software: you can redistribute it and/or 
 *  modify it under the terms of the GNU Affero General Public License
 *  as published by the Free Software Foundation, either version 3 of 
 *  the License, or (at your option) any later version.
 *
 *  Full Metal Galaxy is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public 
 *  License along with Full Metal Galaxy.  
 *  If not, see <http://www.gnu.org/licenses/>.
 *
 *  Copyright 2010 to 2014 Vincent Legendre
 *
 * *********************************************************************/

package com.fullmetalgalaxy.server;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import com.fullmetalgalaxy.model.GameStatus;
import com.fullmetalgalaxy.model.constant.ConfigGameTime;
import com.fullmetalgalaxy.model.persist.EbGamePreview;
import com.googlecode.objectify.Query;

/**
 * @author Vincent
 *
 */
public class GameFilter
{
  private String m_tab = "";
  private String m_orderByDate = "checked";

  private String m_name = "";
  private String m_numberOfPlayer = "";
  private String m_pseudo = "";
  private String m_fast = "checked";
  private String m_slow = "checked";
  private String m_turnbyturn = "checked";
  private String m_parallele = "checked";
  private String m_protected = "checked";
  private String m_unprotected = "checked";
  private String m_open = "checked";
  private String m_running = "checked";
  private String m_pause = "checked";
  private String m_history = "";
  private String m_canceled = "";


  /**
   * 
   * @param p_request
   * @param p_param
   * @return empty string is parameters not found
   */
  private String getParameter(HttpServletRequest p_request, String p_param)
  {
    String parameter = p_request.getParameter( p_param );
    if( parameter == null )
    {
      parameter = "";
    }
    return parameter;
  }


  public GameFilter(HttpServletRequest p_request)
  {
    m_tab = getParameter( p_request, "tab" );

    if( p_request.getParameterMap().size() > 1 )
    {
      m_name = getParameter( p_request, "name" );
      if( !m_name.isEmpty() )
      {
        m_orderByDate = "";
      }
      else
      {
        m_orderByDate = "checked";
      }
      m_pseudo = getParameter( p_request, "pseudo" );
      m_numberOfPlayer = getParameter( p_request, "numberOfPlayer" );
      m_fast = getParameter( p_request, "fast" );
      m_slow = getParameter( p_request, "slow" );
      m_turnbyturn = getParameter( p_request, "turnbyturn" );
      m_parallele = getParameter( p_request, "parallele" );
      m_protected = getParameter( p_request, "protected" );
      m_unprotected = getParameter( p_request, "unprotected" );
      m_open = getParameter( p_request, "open" );
      m_running = getParameter( p_request, "running" );
      m_pause = getParameter( p_request, "pause" );
      m_history = getParameter( p_request, "history" );
      m_canceled = getParameter( p_request, "canceled" );
    }

    if( m_fast.isEmpty() && m_slow.isEmpty() )
    {
      m_fast = m_slow = "checked";
    }
    if( m_parallele.isEmpty() && m_turnbyturn.isEmpty() )
    {
      m_parallele = m_turnbyturn = "checked";
    }

    if( m_open.isEmpty() && m_running.isEmpty() && m_pause.isEmpty() && m_history.isEmpty()
        && m_canceled.isEmpty() )
    {
      m_open = m_running = m_pause = "checked";
    }
  }

  public Iterable<EbGamePreview> getGameList()
  {
    Query<EbGamePreview> gameList = FmgDataStore.dao().query( EbGamePreview.class );
    if( !m_name.isEmpty() )
    {
      gameList.filter("m_name >=",m_name).filter("m_name <", m_name + "\uFFFD");
    }
    if( !m_pseudo.isEmpty() )
    {
      gameList.filter( "m_setRegistration.m_account.m_pseudo =", m_pseudo );
    }
    // filter on config time
    // =====================
    ArrayList<ConfigGameTime> configTimes = new ArrayList<ConfigGameTime>();
    if( !m_fast.isEmpty() && !m_parallele.isEmpty() )
    {
      configTimes.add( ConfigGameTime.QuickAsynch );
    }
    if( !m_fast.isEmpty() && !m_turnbyturn.isEmpty() )
    {
      configTimes.add( ConfigGameTime.QuickTurnBased );
    }
    if( !m_slow.isEmpty() && !m_parallele.isEmpty() )
    {
      configTimes.add( ConfigGameTime.StandardAsynch );
    }
    if( !m_slow.isEmpty() && !m_turnbyturn.isEmpty() )
    {
      configTimes.add( ConfigGameTime.Standard );
    }
    if( configTimes.size() > 0 )
    {
      gameList.filter( "m_configGameTime in", configTimes.toArray() );
    }
    // filter on game status
    // =====================
    ArrayList<GameStatus> status = new ArrayList<GameStatus>();
    if( !m_open.isEmpty() )
    {
      status.add( GameStatus.Open );
    }
    if( !m_running.isEmpty() )
    {
      status.add( GameStatus.Running );
    }
    if( !m_pause.isEmpty() )
    {
      status.add( GameStatus.Pause );
    }
    if( !m_history.isEmpty() )
    {
      status.add( GameStatus.History );
    }
    if( !m_canceled.isEmpty() )
    {
      status.add( GameStatus.Aborted );
    }
    if( status.size() > 0 )
    {
      gameList.filter( "m_status in", status.toArray() );
    }
    // filter on password
    // ==================
    /*if( !m_protected.isEmpty() )
    {
      gameList.filter( "m_password =", "" );
    }*/
    /*if( !m_protected.isEmpty() )
    {
      gameList.filter( "m_status =", );
    }*/
    
    
    if( !m_orderByDate.isEmpty() )
    {
      gameList.order( "-m_creationDate" );
    }
    return gameList;

  }

  public String getHtml()
  {
    // TODO i18n 
    return "<form name='myform' action='/gamelist.jsp' method='get'><hr/>"
        + "<img src='/images/css/calendar.png' title='classé par date'/> <input type='checkbox' name='orderByDate' value='checked' "
        + m_orderByDate
        + "> "
        + "<img src='/images/icons/fast16.png' title='Partie rapide (1h30)'/> <input type='checkbox' name='fast' value='checked' "+m_fast+"> "
        + "<img src='/images/icons/slow16.png' title='Partie lente (25 jours ou illimité)'/> <input type='checkbox' name='slow' value='checked' "
        + m_slow
        + "> "
        + "<img src='/images/icons/turnbyturn16.png' title='Partie en mode tour par tour' /> <input type='checkbox' name='turnbyturn' value='checked' "+m_turnbyturn+"> "
        + "<img src='/images/icons/parallele16.png' title='Partie en mode parallèle'/> <input type='checkbox' name='parallele' value='checked' "
        + m_parallele
        + "> "
        // +
        // "<img src='/images/icons/protected16.png' title='Partie protégé par un mot de passe'/> <input type='checkbox' name='protected' value='checked' "
        // + m_protected
        // + "> "
        // +
        // "<img src='/images/icons/unprotected16.png' title='Partie non protégé'/> <input type='checkbox' name='unprotected' value='checked' "
        // + m_unprotected
        // + "> "
        + "<img src='/images/icons/open16.png' title='Partie ouverte aux inscriptions'/> <input type='checkbox' name='open' value='checked' "+m_open+"> "
        + "<img src='/images/icons/running16.png' title='Partie en cours'/> <input type='checkbox' name='running' value='checked' "+m_running+"> "
        + "<img src='/images/icons/pause16.png' title='Partie en pause'/> <input type='checkbox' name='pause' value='checked' "+m_pause+"> "
        + "<img src='/images/icons/history16.png' title='Partie archivé'/> <input type='checkbox' name='history' value='checked' "
        + m_history
        + "> "
        + "<img src='/images/icons/canceled16.png' title='Partie annulé'/> <input type='checkbox' name='canceled' value='checked' "
        + m_canceled
        + "> <br/>"
        + "nom de la partie: <input type='text' name='name' value='" + m_name + "'>"
        + "nom d'un joueur: <input type='text' name='pseudo' value='" + m_pseudo + "'>"
        + "<input type='hidden' name='tab' value='" + m_tab
        + "'><input type='submit' name='Submit' value='Rechercher'/><hr/></form>";

  }

}
