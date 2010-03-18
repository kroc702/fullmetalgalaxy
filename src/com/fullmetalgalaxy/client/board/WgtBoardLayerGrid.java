/**
 * 
 */
package com.fullmetalgalaxy.client.board;

import com.fullmetalgalaxy.client.ClientUtil;
import com.fullmetalgalaxy.client.ModelFmpMain;
import com.fullmetalgalaxy.model.EnuZoom;


/**
 * @author Vincent Legendre
 *
 */
public class WgtBoardLayerGrid extends WgtBoardLayerBase
{

  /**
   * 
   */
  public WgtBoardLayerGrid()
  {
    setStyleName( "fmp-grid-tactic" );
  }



  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.client.board.test.BoardLayer#setZoom(com.fullmetalgalaxy.model.EnuZoom)
   */
  public void setZoom(EnuZoom p_zoom)
  {
    super.setZoom( p_zoom );
    switch( p_zoom.getValue() )
    {
    default:
    case EnuZoom.Medium:
      setStyleName( "fmp-grid-tactic" );
      break;
    case EnuZoom.Small:
      setStyleName( "fmp-grid-strategy" );
      break;
    }
  }



  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.client.board.WgtBoardLayerBase#onModelChange()
   */
  @Override
  public void onModelChange(boolean p_forceRedraw)
  {
    super.onModelChange( p_forceRedraw );
    if( isVisible() != ModelFmpMain.model().isGridDisplayed() )
    {
      setVisible( ModelFmpMain.model().isGridDisplayed() );
    }
  }



  private static int s_firstGridRuleIndex = createGridRules();

  public static int createGridRules()
  {
    int oldLength = ClientUtil.setCssRule( ".fmp-grid-tactic",
        "{background: url(images/board/desert/tactic/grid.gif);}" ) - 1;
    ClientUtil.setCssRule( ".fmp-grid-strategy",
        "{background: url(images/board/desert/strategy/grid.gif);}" );
    return oldLength;
  }
}
