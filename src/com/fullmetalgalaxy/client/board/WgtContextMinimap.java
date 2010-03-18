/**
 * 
 */
package com.fullmetalgalaxy.client.board;


import com.fullmetalgalaxy.client.ModelFmpMain;
import com.fullmetalgalaxy.client.ressources.Icons;
import com.fullmetalgalaxy.model.constant.FmpConstant;
import com.fullmetalgalaxy.model.persist.EbGame;
import com.fullmetalgalaxy.model.persist.EbRegistration;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MouseListener;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Vincent Legendre
 *
 */
public class WgtContextMinimap extends Composite implements MouseListener
{
  private AbsolutePanel m_panel = new AbsolutePanel();
  private Image m_miniMapImage = new Image();

  String m_lastGameId = null;


  /**
   * 
   */
  public WgtContextMinimap()
  {
    super();
    initWidget( m_panel );
    m_miniMapImage.addMouseListener( this );
  }

  public void redraw()
  {
    assert ModelFmpMain.model() != null;
    EbGame game = ModelFmpMain.model().getGame();
    String gameId = ModelFmpMain.model().getGameId();

    m_panel.clear();

    if( m_lastGameId == null || !m_lastGameId.equals( gameId ) )
    {
      m_lastGameId = gameId;
      m_miniMapImage.setUrl( FmpConstant.getMiniMapUrl( gameId ) );
      m_miniMapImage.setPixelSize( FmpConstant.miniMapWidth, FmpConstant.miniMapHeight );
    }
    m_panel.add( m_miniMapImage );
    if( !game.isStarted() )
    {
      m_panel.add( Icons.s_instance.pause32().createImage(), FmpConstant.miniMapWidth / 2 - 16,
          FmpConstant.miniMapHeight / 2 - 16 );
      m_panel.add( new Label( "En Pause" ), 0, FmpConstant.miniMapHeight / 2 + 30 );
    }
    else if( game.isFinished() )
    {
      m_panel.add( new Label( "Partie termin&eacute;e" ), 0, FmpConstant.miniMapHeight / 2 - 40 );
      m_panel.add( Icons.s_instance.winner32().createImage(), FmpConstant.miniMapWidth / 2 - 16,
          FmpConstant.miniMapHeight / 2 - 16 );
      String strWinner = "";
      EbRegistration winner = game.getWinnerRegistration();
      if( (winner != null) && (winner.haveAccount()) )
      {
        strWinner = ModelFmpMain.model().getAccount( winner.getAccountId() ).getPseudo();
      }
      m_panel.add( new Label( strWinner ), 0, FmpConstant.miniMapHeight / 2 + 30 );
    }

  }

  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.MouseListener#onMouseDown(com.google.gwt.user.client.ui.Widget, int, int)
   */
  public void onMouseDown(Widget p_sender, int p_x, int p_y)
  {
  }

  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.MouseListener#onMouseEnter(com.google.gwt.user.client.ui.Widget)
   */
  public void onMouseEnter(Widget p_sender)
  {
  }

  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.MouseListener#onMouseLeave(com.google.gwt.user.client.ui.Widget)
   */
  public void onMouseLeave(Widget p_sender)
  {
  }

  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.MouseListener#onMouseMove(com.google.gwt.user.client.ui.Widget, int, int)
   */
  public void onMouseMove(Widget p_sender, int p_x, int p_y)
  {
  }

  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.MouseListener#onMouseUp(com.google.gwt.user.client.ui.Widget, int, int)
   */
  public void onMouseUp(Widget p_sender, int p_x, int p_y)
  {
    if( p_sender == m_miniMapImage )
    {
      if( MAppBoard.s_instance != null )
      {
        int hexPositionX = (int)(((float)p_x * ModelFmpMain.model().getGame().getLandWidth()) / FmpConstant.miniMapWidth);
        int hexPositionY = (int)(((float)p_y * ModelFmpMain.model().getGame().getLandHeight()) / FmpConstant.miniMapHeight);
        MAppBoard.s_instance.setScrollPosition( hexPositionX, hexPositionY );
      }
    }
  }



}
