/**
 * 
 */
package com.fullmetalgalaxy.client.board;


import com.fullmetalgalaxy.client.ClientUtil;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventPreview;

/**
 * @author Vincent Legendre
 *
 */
public class WgtBoardLayerAtmosphere extends WgtBoardLayerBase implements EventPreview
{

  /**
   * 
   */
  public WgtBoardLayerAtmosphere()
  {
    setStyleName( "fmp-atmosphere" );
  }

  /* (non-Javadoc)
   * @see com.google.gwt.user.client.EventPreview#onEventPreview(com.google.gwt.user.client.Event)
   */
  public boolean onEventPreview(Event p_event)
  {
    // TODO Auto-generated method stub
    return false;
  }


  private static int s_firstGridRuleIndex = createGridRules();

  public static int createGridRules()
  {
    int oldLength = ClientUtil.setCssRule( ".fmp-atmosphere",
        "{background: url(images/board/atmosphere/clear.png);}" ) - 1;
    return oldLength;
  }


}
