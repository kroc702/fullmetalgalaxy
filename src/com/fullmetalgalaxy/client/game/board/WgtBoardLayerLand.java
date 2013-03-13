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
 *  Copyright 2010, 2011, 2012, 2013 Vincent Legendre
 *
 * *********************************************************************/
package com.fullmetalgalaxy.client.game.board;


import com.fullmetalgalaxy.client.ClientUtil;
import com.fullmetalgalaxy.client.game.GameEngine;
import com.fullmetalgalaxy.model.EnuZoom;
import com.fullmetalgalaxy.model.LandType;
import com.fullmetalgalaxy.model.Tide;
import com.fullmetalgalaxy.model.constant.FmpConstant;
import com.fullmetalgalaxy.model.persist.Game;
import com.google.gwt.user.client.ui.HTML;

/**
 * @author Vincent Legendre
 *
 */
public class WgtBoardLayerLand extends WgtBoardLayerBase
{
  protected HTML m_html = new HTML();
  protected String m_baseUrl = null;

  /**
   * 
   */
  public WgtBoardLayerLand()
  {
    super();
    add( m_html, 0, 0 );

    m_baseUrl = ClientUtil.getUrlParameter( "graphicpack" );
    if( m_baseUrl == null )
    {
      m_baseUrl = "/images/board/";
    }
    if( !m_baseUrl.endsWith( "/" ) )
    {
      m_baseUrl += "/";
    }
  }

  private Tide m_lastTideValue = Tide.Unknown;
  protected long m_lastGameId = 0;

  /**
   * 
   * @see com.fullmetalgalaxy.client.game.board.WgtBoardLayerBase#redraw()
   */
  @Override
  public void onModelChange(boolean p_forceRedraw)
  {
    super.onModelChange( p_forceRedraw );

    if( (m_lastGameId != GameEngine.model().getGame().getId()) || (p_forceRedraw) )
    {
      m_lastGameId = GameEngine.model().getGame().getId();
      m_lastTideValue = GameEngine.model().getGame().getCurrentTide();
      m_htmlLandTactic = null;
      m_htmlLandStrategy = null;
      setZoom( getZoom() );
      /*EbGame game = ModelFmpMain.model().getGame();
      int pxW = game.getLandPixWidth( getZoom() );
      int pxH = game.getLandPixHeight( getZoom() );
      setPixelSize( pxW, pxH );
      m_html.setPixelSize( pxW, pxH );

      m_html.setHTML( getHtmlLand() );
      onTideChange();*/
    }
    if( m_lastTideValue != GameEngine.model().getGame().getCurrentTide() )
    {
      m_lastTideValue = GameEngine.model().getGame().getCurrentTide();
      onTideChange();
    }
  }


  private String m_htmlLandTactic = null;
  private String m_htmlLandStrategy = null;

  public String getHtmlLand()
  {
    switch( getZoom().getValue() )
    {
    default:
    case EnuZoom.Medium:
      if( m_htmlLandTactic == null )
      {
        m_htmlLandTactic = buildHtmlLand( GameEngine.model().getGame(), EnuZoom.Medium );
      }
      return m_htmlLandTactic;
    case EnuZoom.Small:
      if( m_htmlLandStrategy == null )
      {
        m_htmlLandStrategy = buildHtmlLand( GameEngine.model().getGame(), EnuZoom.Small );
      }
      return m_htmlLandStrategy;
    }
  }


  private static String buildHtmlLand(Game p_game, int p_zoom)
  {
    StringBuffer html = new StringBuffer();
    int[] indexTextures = new int[LandType.values().length];
    for( int i = 0; i < indexTextures.length; i++ )
    {
      indexTextures[i] = 1;
    }

    // compute the size of the widget
    int pxW = p_game.getLandPixWidth( new EnuZoom( p_zoom ) );
    int pxH = p_game.getLandPixHeight( new EnuZoom( p_zoom ) );

    int pxHexWidth = FmpConstant.getHexWidth( p_zoom );
    int pxHexHeight = FmpConstant.getHexHeight( p_zoom );

    html.append( "<div style=\"overflow: hidden; width: " + pxW + "; height: " + pxH + "px;\">" );
    for( int ix = 0; ix < p_game.getLandWidth(); ix++ )
    {
      int tmppxX = ix * (pxHexWidth * 3 / 4);
      int yOffset = 0;
      if( ix % 2 != 0 )
      {
        yOffset = pxHexHeight / 2;
      }
      int iy = 0;
      while( iy < p_game.getLandHeight() )
      {
        int pxY = iy * pxHexHeight;
        LandType land = p_game.getLand( ix, iy );
        int hexHeight = 1;
        iy++;
        while( (iy < p_game.getLandHeight()) 
            && (land == p_game.getLand( ix, iy )) 
            && (hexHeight < getTextureHexCount( land )) )
        {
          hexHeight++;
          iy++;
        }
        int pxX = tmppxX;
        if( land == LandType.Montain )
        {
          pxX -= getHexMontainWidthMargin( p_zoom );
          pxY += yOffset - getHexMontainHeightMargin( p_zoom );
          html.append( "<div style=\"left: " + pxX + "px; top: " + pxY + "px; height: "
              + ((hexHeight * pxHexHeight) + getHexMontainHeightMargin( p_zoom ) + (getHexHeightMargin( p_zoom )))
              + "px; z-index:" + (iy * 2 + ix % 2 - 1) + ";\" class=\"fmp-" + land
              + indexTextures[land.ordinal()] + "\"></div>" );
        }
        else
        {
          pxX -= getHexWidthMargin( p_zoom );
          pxY += yOffset - getHexHeightMargin( p_zoom );
          html.append( "<div style=\"left: " + pxX + "px; top: " + pxY + "px; height: "
              + ((hexHeight * pxHexHeight) + (getHexHeightMargin( p_zoom ) * 2))
              + "px;\" class=\"fmp-" + land + indexTextures[land.ordinal()]
              + "\"></div>" );
        }

        if( indexTextures[land.ordinal()] == 1 )
          indexTextures[land.ordinal()] = 2;
        else
          indexTextures[land.ordinal()] = 1;
      }
    }
    html.append( "</div>" );
    return html.toString();
  }


  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.client.board.test.BoardLayer#setZoom(com.fullmetalgalaxy.model.EnuZoom)
   */
  @Override
  public void setZoom(EnuZoom p_zoom)
  {
    super.setZoom( p_zoom );
    int width = (FmpConstant.getHexWidth( p_zoom ) + (getHexWidthMargin( p_zoom.getValue() ) * 2));
    setLandsWidth( LandType.Sea, width );
    setLandsWidth( LandType.Reef, width );
    setLandsWidth( LandType.Marsh, width );
    setLandsWidth( LandType.Plain, width );
    width = (FmpConstant.getHexWidth( p_zoom ) + (getHexMontainWidthMargin( p_zoom.getValue() ) + getHexWidthMargin( p_zoom
        .getValue() )));
    setLandsWidth( LandType.Montain, width );
    Game game = GameEngine.model().getGame();
    int pxW = game.getLandPixWidth( getZoom() );
    int pxH = game.getLandPixHeight( getZoom() );
    setPixelSize( pxW, pxH );
    m_html.setPixelSize( pxW, pxH );

    m_html.setHTML( getHtmlLand() );
    onTideChange();
    show();
  }

  /**
   * called when tide as changed.
   */
  public void onTideChange()
  {
    Game game = GameEngine.model().getGame();
    String baseUrl = m_baseUrl + game.getPlanetType().getFolderName();
    if( getZoom().getValue() == EnuZoom.Small )
    {
      baseUrl += "/strategy/";
    }
    else
    {
      baseUrl += "/tactic/";
    }
    switch( game.getCurrentTide() )
    {
    case Low:
      WgtBoardLayerLand.setLandsImages( LandType.Reef, baseUrl + "reef_low" );
      WgtBoardLayerLand.setLandsImages( LandType.Marsh, baseUrl + "swamp_low" );
      break;
    default:
    case Medium:
      WgtBoardLayerLand.setLandsImages( LandType.Reef, baseUrl + "reef_hight" );
      WgtBoardLayerLand.setLandsImages( LandType.Marsh, baseUrl + "swamp_low" );
      break;
    case Hight:
      WgtBoardLayerLand.setLandsImages( LandType.Reef, baseUrl + "reef_hight" );
      WgtBoardLayerLand.setLandsImages( LandType.Marsh, baseUrl + "swamp_hight" );
      break;
    }
    WgtBoardLayerLand.setLandsImages( LandType.Sea, baseUrl + "sea" );
    WgtBoardLayerLand.setLandsImages( LandType.Plain, baseUrl + "plain" );
    WgtBoardLayerLand.setLandsImages( LandType.Montain, baseUrl + "montain" );
  }


  public static void setLandsImages(LandType p_land, String p_imageUrl)
  {
    setBackgroundRules( s_firstLandRuleIndex1 + p_land.ordinal(), "url(" + p_imageUrl + "1.png)" );
    setBackgroundRules( s_firstLandRuleIndex2 + p_land.ordinal(), "url(" + p_imageUrl + "2.png)" );
  }

  private static native void setBackgroundRules(int p_index, String p_value) /*-{
		var theRules = new Array();
		if ($doc.styleSheets[0].cssRules)
			theRules = $doc.styleSheets[0].cssRules
		else if ($doc.styleSheets[0].rules)
			theRules = $doc.styleSheets[0].rules
		else
			return;
		theRules[p_index].style.background = p_value;
  }-*/;

  public static void setLandsWidth(LandType p_land, int p_widthPx)
  {
    setWidthRules( s_firstLandRuleIndex1 + p_land.ordinal(), "" + p_widthPx + "px" );
    setWidthRules( s_firstLandRuleIndex2 + p_land.ordinal(), "" + p_widthPx + "px" );
  }

  private static native void setWidthRules(int p_index, String p_value) /*-{
		var theRules = new Array();
		if ($doc.styleSheets[0].cssRules)
			theRules = $doc.styleSheets[0].cssRules
		else if ($doc.styleSheets[0].rules)
			theRules = $doc.styleSheets[0].rules
		else
			return;
		theRules[p_index].style.width = p_value;
  }-*/;



  private static int s_firstLandRuleIndex1 = 0;
  private static int s_firstLandRuleIndex2 = 0;

  static
  {
    createLandsRules();
  }

  public static void createLandsRules()
  {
    s_firstLandRuleIndex1 = ClientUtil.setCssRule( ".fmp-None1",
        "{position: absolute; width: 77px; background: url(images/clear.cache.gif);}" ) - 1;
    ClientUtil.setCssRule( ".fmp-Sea1",
            " {position: absolute; width: 77px; background: url(images/board/desert/tactic/sea1.png);}" );
    ClientUtil
        .setCssRule( ".fmp-Reef1",
            " {position: absolute; width: 77px; background: url(images/board/desert/tactic/reef_hight1.png);}" );
    ClientUtil
        .setCssRule( ".fmp-Marsh1",
            " {position: absolute; width: 77px; background: url(images/board/desert/tactic/swamp_low1.png);}" );
    ClientUtil
        .setCssRule( ".fmp-Plain1",
            " {position: absolute; width: 77px; background: url(images/board/desert/tactic/plain1.png);}" );
    ClientUtil
        .setCssRule( ".fmp-Montain1",
            " {position: absolute; width: 77px; background: url(images/board/desert/tactic/montain1.png);}" );

    s_firstLandRuleIndex2 = ClientUtil.setCssRule( ".fmp-None2",
        "{position: absolute; width: 77px; background: url(images/clear.cache.gif);}" ) - 1;
    ClientUtil
        .setCssRule( ".fmp-Sea2",
            " {position: absolute; width: 77px; background: url(images/board/desert/tactic/sea2.png);}" );
    ClientUtil
        .setCssRule( ".fmp-Reef2",
            " {position: absolute; width: 77px; background: url(images/board/desert/tactic/reef_hight2.png);}" );
    ClientUtil
        .setCssRule( ".fmp-Marsh2",
            " {position: absolute; width: 77px; background: url(images/board/desert/tactic/swamp_low2.png);}" );
    ClientUtil
        .setCssRule( ".fmp-Plain2",
            " {position: absolute; width: 77px; background: url(images/board/desert/tactic/plain2.png);}" );
    ClientUtil
        .setCssRule( ".fmp-Montain2",
            " {position: absolute; width: 77px; background: url(images/board/desert/tactic/montain2.png);}" );

  }



  public static int getHexHeightMargin(int p_zoom)
  {
    switch( p_zoom )
    {
    case EnuZoom.Small:
      return 3;
    default:
    case EnuZoom.Medium:
      return 6;
    }
  }

  public static int getHexWidthMargin(int p_zoom)
  {
    switch( p_zoom )
    {
    case EnuZoom.Small:
      return 3;
    default:
    case EnuZoom.Medium:
      return 6;
    }
  }

  public static int getHexMontainHeightMargin(int p_zoom)
  {
    switch( p_zoom )
    {
    case EnuZoom.Small:
      return 17;
    default:
    case EnuZoom.Medium:
      return 27;
    }
  }

  public static int getHexMontainWidthMargin(int p_zoom)
  {
    switch( p_zoom )
    {
    case EnuZoom.Small:
      return 22;
    default:
    case EnuZoom.Medium:
      return 40;
    }
  }

  private static int getTextureHexCount(LandType p_land)
  {
    switch( p_land )
    {
    default:
    case Montain:
      return 1;
    case Marsh:
    case Reef:
      return 2;
    case Plain:
    case Sea:
      return 4;
    case None:
      return 50;
    }
  }

}
