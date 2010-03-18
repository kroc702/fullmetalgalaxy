/**
 * 
 */
package com.fullmetalgalaxy.client.home;

import java.util.List;

import com.fullmetalgalaxy.client.AppMain;
import com.fullmetalgalaxy.client.ModelFmpMain;
import com.fullmetalgalaxy.model.GameFilter;
import com.fullmetalgalaxy.model.GameStatus;
import com.fullmetalgalaxy.model.Services;
import com.fullmetalgalaxy.model.persist.EbGamePreview;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Vincent Legendre
 * This basic game filter replace the usual game filter to simplify ihm
 */
public class WgtBasicGameFilter extends Composite implements ClickListener
{
  // UI
  private Button m_btnNewGame = new Button( "Nouvelle exploitation" );
  private Button m_btnMyGame = new Button( "Mes exploitations" );
  private Button m_btnPuzzleGame = new Button( "Jeux solo/puzzle" );
  private Button m_btnOpenGame = new Button( "Prospections" );

  private AsyncCallback<List<EbGamePreview>> m_callbackGameList = null;
  private GameFilter m_gameFilter = new GameFilter();


  /**
   * 
   */
  public WgtBasicGameFilter(AsyncCallback<List<EbGamePreview>> p_callback)
  {
    assert p_callback != null;
    m_callbackGameList = p_callback;
    HorizontalPanel hpanel = new HorizontalPanel();
    hpanel.add( m_btnNewGame );
    hpanel.add( m_btnOpenGame );
    hpanel.setSize( "100%", "100%" );
    hpanel.add( m_btnPuzzleGame );
    hpanel.add( m_btnMyGame );
    hpanel.setSize( "100%", "100%" );
    m_btnOpenGame.addClickListener( this );
    m_btnNewGame.addClickListener( this );
    m_btnPuzzleGame.addClickListener( this );
    m_btnMyGame.addClickListener( this );

    initWidget( hpanel );
  }

  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.ClickListener#onClick(com.google.gwt.user.client.ui.Widget)
   */
  public void onClick(Widget p_sender)
  {
    if( p_sender == m_btnNewGame )
    {
      if( ModelFmpMain.model().isLogged() )
      {
        AppMain.instance().gotoCreateGame();
      }
      else
      {
        Window.alert( "Vous devez vous identifer pour creer une partie" );
      }
    }
    else if( p_sender == m_btnOpenGame )
    {
      ModelFmpMain.model().getGameFilter().reinit();
      getGameFilter().reinit();
      getGameFilter().setStatus( GameStatus.Open );
      resfreshGameList();
    }
    else if( p_sender == m_btnPuzzleGame )
    {
      ModelFmpMain.model().getGameFilter().reinit();
      getGameFilter().reinit();
      getGameFilter().setStatus( GameStatus.Puzzle );
      resfreshGameList();
    }
    else if( p_sender == m_btnMyGame )
    {
      ModelFmpMain.model().getGameFilter().reinit();
      getGameFilter().reinit();
      getGameFilter().setPlayerName( ModelFmpMain.model().getMyPseudo() );
      resfreshGameList();
    }
  }


  public void resfreshGameList()
  {
    ModelFmpMain.model().setGameFilter( m_gameFilter.newInstance() );
    Services.Util.getInstance().getGameList( m_gameFilter, m_callbackGameList );
  }

  /**
   * @return the gameFilter
   */
  public GameFilter getGameFilter()
  {
    return m_gameFilter;
  }


}
