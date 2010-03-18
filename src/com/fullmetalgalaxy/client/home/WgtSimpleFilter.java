/**
 * 
 */
package com.fullmetalgalaxy.client.home;


import com.fullmetalgalaxy.client.AppMain;
import com.fullmetalgalaxy.client.ModelFmpMain;
import com.fullmetalgalaxy.model.GameStatus;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Vincent Legendre
 *
 */
public class WgtSimpleFilter extends Composite implements ClickListener
{
  private WgtGameFilter m_mainWgt = null;

  // UI
  private Button m_btnNewGame = new Button( "Nouvelle exploitation" );
  private Button m_btnMyGame = new Button( "Mes exploitations" );
  private Button m_btnPuzzleGame = new Button( "Jeux solo/puzzle" );
  private Button m_btnOpenGame = new Button( "Prospections" );

  /**
   * 
   */
  public WgtSimpleFilter(WgtGameFilter p_mainWgt)
  {
    assert p_mainWgt != null;
    m_mainWgt = p_mainWgt;
    HorizontalPanel hpanel = new HorizontalPanel();
    VerticalPanel vpanel = new VerticalPanel();
    vpanel.add( m_btnOpenGame );
    vpanel.setCellHorizontalAlignment( m_btnOpenGame, HasHorizontalAlignment.ALIGN_RIGHT );
    vpanel.setCellVerticalAlignment( m_btnOpenGame, HasVerticalAlignment.ALIGN_MIDDLE );
    vpanel.add( m_btnNewGame );
    vpanel.setCellHorizontalAlignment( m_btnNewGame, HasHorizontalAlignment.ALIGN_RIGHT );
    vpanel.setCellVerticalAlignment( m_btnNewGame, HasVerticalAlignment.ALIGN_MIDDLE );
    vpanel.setSize( "100%", "100%" );
    hpanel.add( vpanel );
    Image image = new Image( "images/logo-filter.jpg" );
    hpanel.add( image );
    hpanel.setCellHorizontalAlignment( image, HasHorizontalAlignment.ALIGN_CENTER );
    vpanel = new VerticalPanel();
    vpanel.add( m_btnPuzzleGame );
    vpanel.setCellVerticalAlignment( m_btnPuzzleGame, HasVerticalAlignment.ALIGN_MIDDLE );
    vpanel.add( m_btnMyGame );
    vpanel.setCellVerticalAlignment( m_btnMyGame, HasVerticalAlignment.ALIGN_MIDDLE );
    vpanel.setSize( "100%", "100%" );
    hpanel.add( vpanel );
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
      m_mainWgt.getGameFilter().reinit();
      m_mainWgt.getGameFilter().setStatus( GameStatus.Open );
      m_mainWgt.resfreshGameList();
    }
    else if( p_sender == m_btnPuzzleGame )
    {
      ModelFmpMain.model().getGameFilter().reinit();
      m_mainWgt.getGameFilter().reinit();
      m_mainWgt.getGameFilter().setStatus( GameStatus.Puzzle );
      m_mainWgt.resfreshGameList();
    }
    else if( p_sender == m_btnMyGame )
    {
      ModelFmpMain.model().getGameFilter().reinit();
      m_mainWgt.getGameFilter().reinit();
      m_mainWgt.getGameFilter().setPlayerName( ModelFmpMain.model().getMyPseudo() );
      m_mainWgt.resfreshGameList();
    }
  }

}
