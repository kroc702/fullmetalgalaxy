/**
 * 
 */
package com.fullmetalgalaxy.client.board;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.fullmetalgalaxy.client.ClientUtil;
import com.fullmetalgalaxy.client.ModelFmpMain;
import com.fullmetalgalaxy.model.EnuColor;
import com.fullmetalgalaxy.model.EnuZoom;
import com.fullmetalgalaxy.model.constant.FmpConstant;
import com.fullmetalgalaxy.model.persist.EbGame;
import com.fullmetalgalaxy.model.persist.EbRegistration;
import com.google.gwt.user.client.ui.HTML;

/**
 * @author Vincent Legendre
 *
 */
public class WgtBoardLayerFireCover extends WgtBoardLayerBase
{
  private HashMap m_fireCoverLayers = new HashMap();

  private Set m_validLayersSet = new HashSet();

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
    EbGame game = ModelFmpMain.model().getGame();
    for( Iterator it = game.getSetRegistration().iterator(); it.hasNext(); )
    {
      EbRegistration registration = (EbRegistration)it.next();
      displayFireCover( p_isVisible, registration );
    }
  }

  /**
   * display or hide one fire cover layer of a specific registration.
   * @param p_isVisible
   * @param p_registration
   */
  public void displayFireCover(boolean p_isVisible, EbRegistration p_registration)
  {
    HTML html = (HTML)m_fireCoverLayers.get( p_registration );
    if( p_isVisible )
    {
      if( html == null )
      {
        html = new HTML();
        add( html, 0, 0 );
        m_fireCoverLayers.put( p_registration, html );
        m_validLayersSet.add( p_registration );
        html.setHTML( getFireCoverHtml( p_registration ) );
      }
      if( !m_validLayersSet.contains( p_registration ) )
      {
        m_validLayersSet.add( p_registration );
        html.setHTML( getFireCoverHtml( p_registration ) );
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
  public void onModelChange(boolean p_forceRedraw)
  {
    super.onModelChange( p_forceRedraw );
    EbGame game = ModelFmpMain.model().getGame();
    if( game.getId() != m_lastGameId || p_forceRedraw )
    {
      m_lastGameId = game.getId();
      m_tokenLastUpdate = game.getLastTokenUpdate().getTime();
      m_gameLastVersion = game.getVersion();
      // new game: clear all fire cover layer.
      for( java.util.Iterator it = m_fireCoverLayers.values().iterator(); it.hasNext(); )
      {
        remove( (HTML)it.next() );
      }
      m_fireCoverLayers.clear();
      m_validLayersSet.clear();
    }
    else if( ((game.getVersion() > m_gameLastVersion))
        || (m_tokenLastUpdate != ModelFmpMain.model().getGame().getLastTokenUpdate().getTime()) )
    {
      m_tokenLastUpdate = game.getLastTokenUpdate().getTime();
      m_gameLastVersion = game.getVersion();
      // same game but somethings changed: redraw all visible layers.
      m_validLayersSet.clear();
      for( java.util.Iterator it = m_fireCoverLayers.entrySet().iterator(); it.hasNext(); )
      {
        Map.Entry entry = (Map.Entry)it.next();
        HTML layer = ((HTML)entry.getValue());
        if( layer.isVisible() )
        {
          layer.setHTML( getFireCoverHtml( (EbRegistration)entry.getKey() ) );
          m_validLayersSet.add( (EbRegistration)entry.getKey() );
        }
      }
    }
    else if( isAnyCoverVisible() != ModelFmpMain.model().isFireCoverDisplayed() )
    {
      displayFireCover( ModelFmpMain.model().isFireCoverDisplayed() );
    }
  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.client.board.test.BoardLayerBase#setZoom(com.fullmetalgalaxy.model.EnuZoom)
   */
  public void setZoom(EnuZoom p_zoom)
  {
    super.setZoom( p_zoom );
    // redraw all visible layers.
    m_validLayersSet.clear();
    for( java.util.Iterator it = m_fireCoverLayers.entrySet().iterator(); it.hasNext(); )
    {
      Map.Entry entry = (Map.Entry)it.next();
      HTML layer = ((HTML)entry.getValue());
      if( layer.isVisible() )
      {
        layer.setHTML( getFireCoverHtml( (EbRegistration)entry.getKey() ) );
        m_validLayersSet.add( (EbRegistration)entry.getKey() );
      }
    }
  }



  /**
   * @return true if at least one fire cover is visible.
   */
  private boolean isAnyCoverVisible()
  {
    for( java.util.Iterator it = m_fireCoverLayers.values().iterator(); it.hasNext(); )
    {
      if( ((HTML)it.next()).isVisible() )
      {
        return true;
      }
    }
    return false;
  }

  /**
   * redraw a firecover layer in a specific HTML widget.
   * @param p_registration
   */
  private String getFireCoverHtml(EbRegistration p_registration)
  {
    StringBuffer html = new StringBuffer();
    EbGame game = ModelFmpMain.model().getGame();


    // compute the size of the widget
    int pxW = game.getLandPixWidth( getZoom() );
    int pxH = game.getLandPixHeight( getZoom() );

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
    assert EnuColor.isSingleColor( p_registration.getOriginalColor() );
    cssClass += EnuColor.singleColorToString( p_registration.getOriginalColor() );
    String hCssClass = "h" + cssClass;

    html.append( "<div style=\"overflow: hidden; width: " + pxW + "; height: " + pxH + "px;\">" );
    for( int ix = 0; ix < game.getLandWidth(); ix++ )
    {
      int pxX = ix * (pxHexWidth * 3 / 4);
      int yOffset = 0;
      if( ix % 2 != 0 )
      {
        yOffset = pxHexHeight / 2;
      }
      int iy = 0;
      while( iy < game.getLandHeight() )
      {
        int pxY = iy * pxHexHeight + yOffset;
        if( game.getFireCover( ix, iy, p_registration ) >= 2 )
        {
          int hexHeight = 1;
          iy++;
          while( game.getFireCover( ix, iy, p_registration ) >= 2 )
          {
            hexHeight++;
            iy++;
          }
          html.append( "<div style=\"left: " + pxX + "px; top: " + pxY + "px; height: "
              + (hexHeight * pxHexHeight) + "px;\" class=\"" + cssClass + "\"></div>" );
        }
        else if( game.getFireCover( ix, iy, p_registration )
            + game.getBoardFireCover().getDisabledFireCover( ix, iy, p_registration ) >= 2 )
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
