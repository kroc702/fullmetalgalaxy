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
package com.fullmetalgalaxy.client.game.board;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.fullmetalgalaxy.client.ClientUtil;
import com.fullmetalgalaxy.client.game.GameEngine;
import com.fullmetalgalaxy.model.EnuColor;
import com.fullmetalgalaxy.model.EnuZoom;
import com.fullmetalgalaxy.model.constant.FmpConstant;
import com.fullmetalgalaxy.model.persist.EbTeam;
import com.fullmetalgalaxy.model.persist.Game;
import com.google.gwt.user.client.ui.HTML;

/**
 * @author Vincent Legendre
 *
 */
public class WgtBoardLayerFireCover extends WgtBoardLayerBase
{
  private HashMap<EbTeam, HTML> m_fireCoverLayers = new HashMap<EbTeam, HTML>();

  private Set<EbTeam> m_validLayersSet = new HashSet<EbTeam>();

  /**
   * last update of the currently displayed fire cover
   */
  private long m_tokenLastUpdate = 0;
  private long m_gameLastVersion = 0;

  /**
   * 
   */
  public WgtBoardLayerFireCover()
  {
  }

  /**
   * display or hide all fire cover.
   * @param p_isVisible
   */
  public void displayFireCover(boolean p_isVisible)
  {
    Game game = GameEngine.model().getGame();
    for( EbTeam team : game.getTeams() )
    {
      displayFireCover( p_isVisible, team );
    }
  }

  /**
   * display or hide one fire cover layer of a specific registration.
   * @param p_isVisible
   * @param p_team
   */
  public void displayFireCover(boolean p_isVisible, EbTeam p_team)
  {
    HTML html = (HTML)m_fireCoverLayers.get( p_team );
    if( p_isVisible )
    {
      if( html == null )
      {
        html = new HTML();
        add( html, 0, 0 );
        m_fireCoverLayers.put( p_team, html );
        m_validLayersSet.add( p_team );
        html.setHTML( getFireCoverHtml( p_team ) );
      }
      if( !m_validLayersSet.contains( p_team ) )
      {
        m_validLayersSet.add( p_team );
        html.setHTML( getFireCoverHtml( p_team ) );
      }
    }

    if( html != null )
    {
      html.setVisible( p_isVisible );
    }

  }



  private long m_lastGameId = 0;

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.client.board.test.BoardLayerBase#onModelChange()
   */
  @Override
  public void onModelChange(boolean p_forceRedraw)
  {
    super.onModelChange( p_forceRedraw );
    Game game = GameEngine.model().getGame();
    if( game.getId() != m_lastGameId || p_forceRedraw )
    {
      m_lastGameId = game.getId();
      m_tokenLastUpdate = game.getLastTokenUpdate().getTime();
      m_gameLastVersion = game.getVersion();
      // new game: clear all fire cover layer.
      for( HTML html : m_fireCoverLayers.values() )
      {
        remove( html );
      }
      m_fireCoverLayers.clear();
      m_validLayersSet.clear();
      redraw();
    }
    else if( ((game.getVersion() > m_gameLastVersion))
        || (m_tokenLastUpdate != GameEngine.model().getGame().getLastTokenUpdate().getTime()) )
    {
      redraw();
    }
    else if( isAnyCoverVisible() != GameEngine.model().isFireCoverDisplayed() )
    {
      displayFireCover( GameEngine.model().isFireCoverDisplayed() );
    }
  }

  @Override
  public void redraw()
  {
    m_tokenLastUpdate = GameEngine.game().getLastTokenUpdate().getTime();
    m_gameLastVersion = GameEngine.game().getVersion();
    // same game but something changed: redraw all visible layers.
    m_validLayersSet.clear();
    for( Map.Entry<EbTeam, HTML> entry : m_fireCoverLayers.entrySet() )
    {
      HTML layer = entry.getValue();
      if( layer.isVisible() )
      {
        layer.setHTML( getFireCoverHtml( (EbTeam)entry.getKey() ) );
        m_validLayersSet.add( (EbTeam)entry.getKey() );
      }
    }
  }


  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.client.board.test.BoardLayerBase#setZoom(com.fullmetalgalaxy.model.EnuZoom)
   */
  @Override
  public void setZoom(EnuZoom p_zoom)
  {
    super.setZoom( p_zoom );
    // redraw all visible layers.
    m_validLayersSet.clear();
    for( Map.Entry<EbTeam, HTML> entry : m_fireCoverLayers.entrySet() )
    {
      HTML layer = ((HTML)entry.getValue());
      if( layer.isVisible() )
      {
        layer.setHTML( getFireCoverHtml( (EbTeam)entry.getKey() ) );
        m_validLayersSet.add( (EbTeam)entry.getKey() );
      }
    }
  }



  /**
   * @return true if at least one fire cover is visible.
   */
  private boolean isAnyCoverVisible()
  {
    for( HTML html : m_fireCoverLayers.values() )
    {
      if( html.isVisible() )
      {
        return true;
      }
    }
    return false;
  }

  /**
   * redraw a firecover layer in a specific HTML widget.
   * @param p_team
   */
  private String getFireCoverHtml(EbTeam p_team)
  {
    StringBuffer html = new StringBuffer();
    Game game = GameEngine.model().getGame();


    // compute the size of the widget
    resetPixelSize();

    int pxHexWidth = FmpConstant.getHexWidth( getZoom() );
    int pxHexHeight = FmpConstant.getHexHeight( getZoom() );

    // determine the css class for this fire cover
    String cssClass = "cover-";
    if( getZoom().getValue() == EnuZoom.Small )
    {
      cssClass += "strategy-";
    }
    else
    {
      cssClass += "tactic-";
    }
    assert EnuColor.isSingleColor( p_team.getFireColor() );
    cssClass += EnuColor.singleColorToString( p_team.getFireColor() );
    String hCssClass = "h" + cssClass;

    html.append( "<div style=\"overflow: hidden; width: 100%; height: 100%;\">" );
    // for( int ix = 0; ix < game.getLandWidth(); ix++ )
    for( int ix = m_cropLeftHex; ix < m_cropRightHex; ix++ )
    {
      int pxX = (ix - m_cropLeftHex) * (pxHexWidth * 3 / 4);
      int yOffset = 0;
      if( ix % 2 != 0 )
      {
        yOffset = pxHexHeight / 2;
      }
      int iy = m_cropTopHex;
      while( iy < m_cropBotomHex )
      {
        int pxY = (iy - m_cropTopHex) * pxHexHeight + yOffset;
        if( game.getFireCover( ix, iy, p_team ) >= 2 )
        {
          int hexHeight = 1;
          while( iy + 1 < m_cropBotomHex && game.getFireCover( ix, iy + 1, p_team ) >= 2 )
          {
            hexHeight++;
            iy++;
          }
          html.append( "<div style=\"left: " + pxX + "px; top: " + pxY + "px; height: "
              + (hexHeight * pxHexHeight) + "px;\" class=\"" + cssClass + "\"></div>" );
        }
        else if( game.getFireCover( ix, iy, p_team )
            + game.getBoardFireCover().getDisabledFireCover( ix, iy, p_team ) >= 2 )
        {
          html.append( "<div style=\"left: " + pxX + "px; top: " + pxY + "px; height: "
              + pxHexHeight + "px;\" class=\"" + hCssClass + "\"></div>" );
        }
        iy++;        
      }
    }
    html.append( "</div>" );

    return html.toString();
  }

  @Override
  public void cropDisplay(int p_cropLeftHex, int p_cropTopHex, int p_cropRightHex,
      int p_cropBotomHex)
  {
    super.cropDisplay( p_cropLeftHex, p_cropTopHex, p_cropRightHex, p_cropBotomHex );
    redraw();
  }

  static
  {
    // all fire cover css rules creation
    for( int iColor = 0; iColor < EnuColor.getTotalNumberOfColor(); iColor++ )
    {
      createFireCoverRules( EnuColor.getColorFromIndex( iColor ).toString() );
    }
  }

  public static void createFireCoverRules(String p_color)
  {
    ClientUtil.setCssRule( ".cover-tactic-" + p_color,
        " {position: absolute; width: 77px; background: url(images/board/" + p_color
            + "/tactic/cover.png);}" );
    ClientUtil.setCssRule( ".cover-strategy-" + p_color,
        " {position: absolute; width: 34px; background: url(images/board/" + p_color
            + "/strategy/cover.png);}" );
    ClientUtil.setCssRule( ".hcover-tactic-" + p_color,
        " {position: absolute; width: 77px; background: url(images/board/" + p_color
            + "/tactic/hcover.png);}" );
    ClientUtil.setCssRule( ".hcover-strategy-" + p_color,
        " {position: absolute; width: 34px; background: url(images/board/" + p_color
            + "/strategy/hcover.png);}" );
  }


}
