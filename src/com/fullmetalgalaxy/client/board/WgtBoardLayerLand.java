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
 *  Copyright 2010 Vincent Legendre
 *
 * *********************************************************************/
/**
 * 
 */
package com.fullmetalgalaxy.client.board;


import com.fullmetalgalaxy.client.ClientUtil;
import com.fullmetalgalaxy.client.ModelFmpMain;
import com.fullmetalgalaxy.model.EnuZoom;
import com.fullmetalgalaxy.model.LandType;
import com.fullmetalgalaxy.model.Tide;
import com.fullmetalgalaxy.model.constant.FmpConstant;
import com.fullmetalgalaxy.model.persist.EbGame;
import com.google.gwt.user.client.ui.HTML;

/**
 * @author Vincent Legendre
 *
 */
public class WgtBoardLayerLand extends WgtBoardLayerBase
{
  protected HTML m_html = new HTML();

  /**
   * 
   */
  public WgtBoardLayerLand()
  {
    super();
    add( m_html, 0, 0 );

  }

  private Tide m_lastTideValue = Tide.Unknown;
  protected long m_lastGameId = 0;

  /**
   * 
   * @see com.fullmetalgalaxy.client.board.WgtBoardLayerBase#redraw()
   */
  @Override
  public void onModelChange(boolean p_forceRedraw)
  {
    super.onModelChange( p_forceRedraw );

    if( (m_lastGameId != ModelFmpMain.model().getGame().getId()) || (p_forceRedraw) )
    {
      m_lastGameId = ModelFmpMain.model().getGame().getId();
      m_lastTideValue = ModelFmpMain.model().getGame().getCurrentTide();
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
    if( m_lastTideValue != ModelFmpMain.model().getGame().getCurrentTide() )
    {
      m_lastTideValue = ModelFmpMain.model().getGame().getCurrentTide();
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
        m_htmlLandTactic = buildHtmlLand( ModelFmpMain.model().getGame(), EnuZoom.Medium );
      }
      return m_htmlLandTactic;
    case EnuZoom.Small:
      if( m_htmlLandStrategy == null )
      {
        m_htmlLandStrategy = buildHtmlLand( ModelFmpMain.model().getGame(), EnuZoom.Small );
      }
      return m_htmlLandStrategy;
    }
  }


  private static String buildHtmlLand(EbGame p_game, int p_zoom)
  {
    StringBuffer html = new StringBuffer();

    // compute the size of the widget
    int pxW = p_game.getLandPixWidth( new EnuZoom( p_zoom ) );
    int pxH = p_game.getLandPixHeight( new EnuZoom( p_zoom ) );

    int pxHexWidth = FmpConstant.getHexWidth( p_zoom );
    int pxHexHeight = FmpConstant.getHexHeight( p_zoom );

    html.append( "<div style=\"overflow: hidden; width: " + pxW + "; height: " + pxH + "px;\">" );
    for( int ix = 0; ix < p_game.getLandWidth(); ix++ )
    {
      int pxX = ix * (pxHexWidth * 3 / 4);
      int yOffset = 0;
      if( ix % 2 != 0 )
      {
        yOffset = pxHexHeight / 2;
      }
      int iy = 0;
      while( iy < p_game.getLandHeight() )
      {
        int pxY = iy * pxHexHeight + yOffset;
        LandType land = p_game.getLand( ix, iy );
        int hexHeight = 1;
        iy++;
        while( (iy < p_game.getLandHeight()) && (land == p_game.getLand( ix, iy )) )
        {
          hexHeight++;
          iy++;
        }
        html.append( "<div style=\"left: " + pxX + "px; top: " + pxY + "px; height: "
            + (hexHeight * pxHexHeight) + "px;\" class=\"fmp-" + land + "\"></div>" );
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
    String width = "" + FmpConstant.getHexWidth( p_zoom ) + "px";
    setWidthRules( s_firstLandRuleIndex + LandType.Sea.ordinal(), width );
    setWidthRules( s_firstLandRuleIndex + LandType.Reef.ordinal(), width );
    setWidthRules( s_firstLandRuleIndex + LandType.Marsh.ordinal(), width );
    setWidthRules( s_firstLandRuleIndex + LandType.Plain.ordinal(), width );
    setWidthRules( s_firstLandRuleIndex + LandType.Montain.ordinal(), width );
    EbGame game = ModelFmpMain.model().getGame();
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
    EbGame game = ModelFmpMain.model().getGame();
    String baseUrl = "images/board/" + game.getPlanetType().getFolderName();
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
      WgtBoardLayerLand.setLandsImages( LandType.Reef, baseUrl + "reef_low.png" );
      WgtBoardLayerLand.setLandsImages( LandType.Marsh, baseUrl + "swamp_low.png" );
      break;
    default:
    case Medium:
      WgtBoardLayerLand.setLandsImages( LandType.Reef, baseUrl + "reef_hight.png" );
      WgtBoardLayerLand.setLandsImages( LandType.Marsh, baseUrl + "swamp_low.png" );
      break;
    case Hight:
      WgtBoardLayerLand.setLandsImages( LandType.Reef, baseUrl + "reef_hight.png" );
      WgtBoardLayerLand.setLandsImages( LandType.Marsh, baseUrl + "swamp_hight.png" );
      break;
    }
    WgtBoardLayerLand.setLandsImages( LandType.Sea, baseUrl + "sea.png" );
    WgtBoardLayerLand.setLandsImages( LandType.Plain, baseUrl + "plain.png" );
    WgtBoardLayerLand.setLandsImages( LandType.Montain, baseUrl + "montain.png" );
  }


  public static void setLandsImages(LandType p_land, String p_imageUrl)
  {
    setBackgroundRules( s_firstLandRuleIndex + p_land.ordinal(), "url(" + p_imageUrl + ")" );
  }

  private static native void setBackgroundRules(int p_index, String p_value) /*-{
    var theRules = new Array();
    if ($doc.styleSheets[0].cssRules)
      theRules = $doc.styleSheets[0].cssRules
    else if ($doc.styleSheets[0].rules)
      theRules = $doc.styleSheets[0].rules
    else return;
    theRules[p_index].style.background = p_value;
  }-*/;

  public static void setLandsWidth(LandType p_land, int p_widthPx)
  {
    setWidthRules( s_firstLandRuleIndex + p_land.ordinal(), "" + p_widthPx + "px" );
  }

  private static native void setWidthRules(int p_index, String p_value) /*-{
    var theRules = new Array();
    if ($doc.styleSheets[0].cssRules)
      theRules = $doc.styleSheets[0].cssRules
    else if ($doc.styleSheets[0].rules)
      theRules = $doc.styleSheets[0].rules
    else return;
    theRules[p_index].style.width = p_value;
  }-*/;



  private static int s_firstLandRuleIndex = createLandsRules();

  public static int createLandsRules()
  {
    int oldLength = ClientUtil.setCssRule( ".fmp-None",
        "{position: absolute; width: 77px; background: url(images/clear.cache.gif);}" ) - 1;
    ClientUtil.setCssRule( ".fmp-Sea",
        " {position: absolute; width: 77px; background: url(images/board/desert/tactic/sea.png);}" );
    ClientUtil
        .setCssRule( ".fmp-Reef",
            " {position: absolute; width: 77px; background: url(images/board/desert/tactic/reef_hight.png);}" );
    ClientUtil
        .setCssRule( ".fmp-Marsh",
            " {position: absolute; width: 77px; background: url(images/board/desert/tactic/swamp_low.png);}" );
    ClientUtil
        .setCssRule( ".fmp-Plain",
            " {position: absolute; width: 77px; background: url(images/board/desert/tactic/plain.png);}" );
    ClientUtil
        .setCssRule( ".fmp-Montain",
            " {position: absolute; width: 77px; background: url(images/board/desert/tactic/montain.png);}" );

    return oldLength;
  }

}
