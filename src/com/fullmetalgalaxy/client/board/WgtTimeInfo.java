/**
 * 
 */
package com.fullmetalgalaxy.client.board;

import java.util.ArrayList;

import com.fullmetalgalaxy.client.ClientUtil;
import com.fullmetalgalaxy.client.ModelFmpMain;
import com.fullmetalgalaxy.client.WgtView;
import com.fullmetalgalaxy.client.ressources.BoardIcons;
import com.fullmetalgalaxy.client.ressources.Icons;
import com.fullmetalgalaxy.client.ressources.Messages;
import com.fullmetalgalaxy.model.EnuColor;
import com.fullmetalgalaxy.model.SourceModelUpdateEvents;
import com.fullmetalgalaxy.model.persist.EbGame;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

/**
 * @author Vincent Legendre
 *
 */
public class WgtTimeInfo extends WgtView
{
  private HorizontalPanel m_panel = new HorizontalPanel();
  private Image m_iconTime = Icons.s_instance.time16().createImage();

  // private Label m_lblTurn = new Label( " " );

  // private HTML m_html = new HTML();


  /**
   * 
   */
  public WgtTimeInfo()
  {
    // subscribe all needed models update event
    ModelFmpMain.model().subscribeModelUpdateEvent( this );

    // m_html.setStyleName( "fmp-status-text" );
    // m_html.setWidth( "100%" );

    m_panel.add( m_iconTime );
    // m_panel.add( m_lblTurn );

    initWidget( m_panel );
  }

  private long m_gameLastVersion = 0;
  boolean m_haveWeatherHen = false;


  protected void redraw()
  {
    EbGame game = ModelFmpMain.model().getGame();
    if( game == null )
    {
      return;
    }
    boolean haveWeatherHen = ModelFmpMain.model().haveWeatherHen();
    if( m_gameLastVersion == game.getVersion() && m_haveWeatherHen == haveWeatherHen )
    {
      return;
    }
    m_haveWeatherHen = haveWeatherHen;
    m_gameLastVersion = game.getVersion();

    m_panel.clear();
    m_panel.add( m_iconTime );

    // display end game date or player's turn
    // ======================================
    Label lblTurn = new Label( " : " + game.getCurrentTimeStep() + "/"
        + game.getEbConfigGameTime().getTotalTimeStep() + "  " );
    lblTurn.setStyleName( "fmp-status-text" );
    lblTurn.setTitle( "Tour actuel / Nombre total de tours" );
    m_panel.add( lblTurn );

    if( !game.isAsynchron() )
    {
      EnuColor color = new EnuColor( EnuColor.None );
      if( game.getCurrentPlayerRegistration() != null )
      {
        color = game.getCurrentPlayerRegistration().getEnuColor();
        Label lbl = new Label( " (" );
        lbl.setStyleName( "fmp-status-text" );
        lbl.setTitle( "Joueur actuel" );
        m_panel.add( lbl );

        int colorIndex = 0;
        for( colorIndex = 0; colorIndex < EnuColor.getTotalNumberOfColor(); colorIndex++ )
        {
          if( color.isColored( EnuColor.getColorFromIndex( colorIndex ) ) )
          {
            Image image = BoardIcons.icon16( EnuColor.getColorFromIndex( colorIndex ).getValue() )
                .createImage();
            image.setTitle( "Joueur actuel" );
            m_panel.add( image );
          }
        }

        if( game.getCurrentPlayerRegistration().haveAccount() )
        {
          lbl = new Label( ModelFmpMain.model().getAccount(
              game.getCurrentPlayerRegistration().getAccountId() ).getPseudo()
              + ")" );
        }
        else
        {
          lbl = new Label( "unknown)" );
        }
        lbl.setStyleName( "fmp-status-text" );
        lbl.setTitle( "Joueur actuel" );
        m_panel.add( lbl );
      }
      // m_panelMiniMap.add( new HTML( htmlTurn ) );
    }

    // Display tides
    // =============
    Image image = null;
    if( game.getAllowedTakeOffTurns().contains( game.getCurrentTimeStep() ) )
    {
      // take off is allowed : display it !
      image = Icons.s_instance.takeOff16().createImage();
      image.setTitle( "Decollage autorisé !" );
      m_panel.add( image );
      m_panel.setCellWidth( image, "20px" );
    }

    image = BoardIcons.iconTide( game.getCurrentTide() ).createImage();
    image.setTitle( "marree actuelle: " + Messages.getTideString( game.getCurrentTide() ) );
    m_panel.add( image );
    m_panel.setCellWidth( image, "20px" );

    if( game.isAsynchron() )
    {
      Label lblDate = new Label( ClientUtil.formatDateTime( game.estimateNextTimeStep() ) );
      lblDate.setStyleName( "fmp-status-text" );
      lblDate.setTitle( "Date du prochain increment de temps" );
      m_panel.add( lblDate );
    }
    else
    {
      if( ModelFmpMain.model().getMyRegistration() == game.getCurrentPlayerRegistration() )
      {
        Label lblDate = new Label( ClientUtil.formatDateTime( game.getCurrentPlayerRegistration()
            .getEndTurnDate() ) );
        lblDate.setStyleName( "fmp-status-text" );
        lblDate.setTitle( "Date de fin de tour" );
        m_panel.add( lblDate );
      }
    }

    if( ModelFmpMain.model().haveWeatherHen() )
    {
      image = BoardIcons.iconTide( game.getNextTide() ).createImage();
      image.setTitle( "marree futur: " + Messages.getTideString( game.getNextTide() ) );
      m_panel.setCellWidth( image, "20px" );
      m_panel.add( image );
    }
    else
    {
      image = Icons.s_instance.tide_unknown().createImage();
      image.setTitle( MAppBoard.s_messages.noForecast() );
      m_panel.setCellWidth( image, "20px" );
      m_panel.add( image );
    }

    if( (!game.isAsynchron()) && (ModelFmpMain.model().getMyRegistration() != null)
        && (ModelFmpMain.model().getMyRegistration() != game.getCurrentPlayerRegistration()) )
    {
      Label lblDate = new Label( ClientUtil.formatDateTime( ModelFmpMain.model()
          .getMyRegistration().getEndTurnDate() ) );
      lblDate.setStyleName( "fmp-status-text" );
      lblDate.setTitle( "Date de fin de prochain tour" );
      m_panel.add( lblDate );
    }

    // display next take off
    // ===================
    ArrayList<Integer> allowedTakeOff = game.getAllowedTakeOffTurns();
    if( allowedTakeOff != null )
    {
      int index = 0;
      int currentTurn = game.getCurrentTimeStep();
      while( (index < allowedTakeOff.size())
          && (currentTurn >= allowedTakeOff.get( index ).intValue()) )
      {
        index++;
      }
      if( index < allowedTakeOff.size() )
      {
        image = Icons.s_instance.takeOff16().createImage();
        image.setTitle( "prochaine fenetre de decolage : tour " + allowedTakeOff.get( index ) );
        m_panel.add( image );
        m_panel.setCellWidth( image, "20px" );
      }
    }
  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.client.WgtView#notifyHmiUpdate()
   */
  public void notifyHmiUpdate()
  {
    redraw();
  }


  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.client.ModelUpdateListener#notifyModelUpdate(com.fullmetalgalaxy.client.CtrModel)
   */
  public void onModelUpdate(SourceModelUpdateEvents p_ModelSender)
  {
    redraw();
  }

}
