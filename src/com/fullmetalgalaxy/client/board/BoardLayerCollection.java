/**
 * 
 */
package com.fullmetalgalaxy.client.board;

import java.util.ArrayList;


import com.fullmetalgalaxy.model.EnuZoom;
import com.fullmetalgalaxy.model.RpcUtil;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Vincent Legendre
 * This subclass of ArrayList assumes that
 * all objects added to it will be of type BoardLayer.
 */
public class BoardLayerCollection extends ArrayList<BoardLayer> implements BoardLayer
{
  static final long serialVersionUID = 1;

  /**
   * 
   */
  public BoardLayerCollection()
  {
  }


  /**
   * shouldn't be called, as it always return null. 
   * @see com.fullmetalgalaxy.client.board.BoardLayer#getTopWidget()
   */
  public Widget getTopWidget()
  {
    return null;
  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.client.board.BoardLayer#hide()
   */
  public void hide()
  {
    for( java.util.Iterator<BoardLayer> it = iterator(); it.hasNext(); )
    {
      ((BoardLayer)it.next()).hide();
    }
  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.client.board.BoardLayer#onModelChange()
   */
  public void onModelChange()
  {
    for( java.util.Iterator<BoardLayer> it = iterator(); it.hasNext(); )
    {
      try
      {
        ((BoardLayer)it.next()).onModelChange();
      } catch( Exception e )
      {
        RpcUtil.logError( "a board layer bug while notify a model update", e );
      }
    }
  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.client.board.BoardLayer#redraw(int, int, int, int)
   */
  public void redraw(int p_left, int p_top, int p_right, int p_botom)
  {
    for( java.util.Iterator<BoardLayer> it = iterator(); it.hasNext(); )
    {
      it.next().redraw( p_left, p_top, p_right, p_botom );
    }
  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.client.board.BoardLayer#setZoom(com.fullmetalgalaxy.model.EnuZoom)
   */
  public void setZoom(EnuZoom p_zoom)
  {
    for( java.util.Iterator<BoardLayer> it = iterator(); it.hasNext(); )
    {
      it.next().setZoom( p_zoom );
    }
  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.client.board.BoardLayer#show()
   */
  public void show()
  {
    for( java.util.Iterator<BoardLayer> it = iterator(); it.hasNext(); )
    {
      it.next().show();
    }
  }

}
