/**
 * 
 */
package com.fullmetalgalaxy.client.board;


import com.fullmetalgalaxy.client.ModelFmpMain;
import com.fullmetalgalaxy.client.WgtView;
import com.fullmetalgalaxy.client.ressources.Icons;
import com.fullmetalgalaxy.client.ressources.Messages;
import com.fullmetalgalaxy.model.EnuZoom;
import com.fullmetalgalaxy.model.RpcFmpException;
import com.fullmetalgalaxy.model.SourceModelUpdateEvents;
import com.fullmetalgalaxy.model.TokenType;
import com.fullmetalgalaxy.model.persist.EbToken;
import com.fullmetalgalaxy.model.persist.gamelog.EbEvtPlayerTurn;
import com.fullmetalgalaxy.model.persist.gamelog.EbEvtTakeOff;
import com.fullmetalgalaxy.model.persist.gamelog.EventBuilderMsg;
import com.fullmetalgalaxy.model.persist.gamelog.EventsPlayBuilder;
import com.fullmetalgalaxy.model.persist.gamelog.GameLogType;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Vincent Legendre
 *
 */
public class WgtContextAction extends WgtView implements ClickListener
{
  HorizontalPanel m_panel = new HorizontalPanel();
  Image m_btnOk = Icons.s_instance.ok32().createImage();
  Image m_btnCancel = Icons.s_instance.cancel32().createImage();
  Image m_btnRepairTurret = Icons.s_instance.repair32().createImage();
  Image m_btnFire = Icons.s_instance.shoot32().createImage();
  Image m_btnControl = Icons.s_instance.control32().createImage();
  Image m_btnFireCoverOn = Icons.s_instance.fireCoverOn32().createImage();
  Image m_btnFireCoverOff = Icons.s_instance.fireCoverOff32().createImage();
  Image m_btnEndTurn = Icons.s_instance.endTurn32().createImage();
  Image m_btnZoomIn = Icons.s_instance.zoomIn32().createImage();
  Image m_btnZoomOut = Icons.s_instance.zoomOut32().createImage();
  Image m_btnGrid = Icons.s_instance.grid32().createImage();
  Image m_btnRegister = Icons.s_instance.register32().createImage();
  FocusPanel m_pnlRegister = null;
  Image m_btnInfo = Icons.s_instance.info32().createImage();
  Image m_btnTakeOff = Icons.s_instance.takeOff32().createImage();
  Image m_btnMiniMap = Icons.s_instance.map32().createImage();
  Image m_btnPlayer = Icons.s_instance.player32().createImage();
  Image m_iconAction = Icons.s_instance.action16().createImage();
  Label m_lblAction = new Label( "" );

  private DlgGameDetail m_dlgGameDetail = new DlgGameDetail();

  /**
   * 
   */
  public WgtContextAction()
  {
    m_btnOk.addClickListener( this );
    m_btnOk.setTitle( "Valider l'action" );
    m_btnOk.setStyleName( "fmp-button" );
    m_btnCancel.addClickListener( this );
    m_btnCancel.setTitle( "Annuler l'action [ESC]" );
    m_btnCancel.setStyleName( "fmp-button" );
    m_btnRepairTurret.addClickListener( this );
    m_btnRepairTurret.setTitle( "Reparer la tourelle" );
    m_btnRepairTurret.setStyleName( "fmp-button" );
    m_btnTakeOff.addClickListener( this );
    m_btnTakeOff.setTitle( "Decollage" );
    m_btnTakeOff.setStyleName( "fmp-button" );
    m_btnFire.addClickListener( this );
    m_btnFire.setTitle( "Tirer" );
    m_btnFire.setStyleName( "fmp-button" );
    m_btnControl.addClickListener( this );
    m_btnControl.setTitle( "Controle" );
    m_btnControl.setStyleName( "fmp-button" );
    m_btnFireCoverOn.addClickListener( this );
    m_btnFireCoverOn.setTitle( "Afficher les couvertures de feux [F]" );
    m_btnFireCoverOn.setStyleName( "fmp-button" );
    m_btnFireCoverOff.addClickListener( this );
    m_btnFireCoverOff.setTitle( "Cacher les couvertures de feux [F]" );
    m_btnFireCoverOff.setStyleName( "fmp-button" );
    m_btnEndTurn.addClickListener( this );
    m_btnEndTurn.setTitle( "Fin de tour" );
    m_btnEndTurn.setStyleName( "fmp-button" );
    m_btnZoomIn.addClickListener( this );
    m_btnZoomIn.setTitle( "Zoom tactique [+]" );
    m_btnZoomIn.setStyleName( "fmp-button" );
    m_btnZoomOut.addClickListener( this );
    m_btnZoomOut.setTitle( "Zoom strategique [-]" );
    m_btnZoomOut.setStyleName( "fmp-button" );
    m_btnGrid.addClickListener( this );
    m_btnGrid.setTitle( "Afficher/cacher la grille [G]" );
    m_btnGrid.setStyleName( "fmp-button" );
    m_btnRegister.addClickListener( this );
    m_btnRegister.setTitle( "S'inscrire a cette partie" );
    m_btnRegister.setStyleName( "fmp-button" );
    HorizontalPanel hPanel = new HorizontalPanel();
    hPanel.add( Icons.s_instance.register32().createImage() );
    hPanel.add( new Label( "Cette partie recherche des joueurs. Inscrivez vous !" ) );
    m_pnlRegister = new FocusPanel( hPanel );
    m_pnlRegister.addClickListener( this );

    m_btnInfo.addClickListener( this );
    m_btnInfo.setTitle( "Information detailles sur cette partie" );
    m_btnInfo.setStyleName( "fmp-button" );
    m_btnMiniMap.addClickListener( this );
    m_btnMiniMap.setTitle( "Affichage de la minimap" );
    m_btnMiniMap.setStyleName( "fmp-button" );
    m_btnPlayer.addClickListener( this );
    m_btnPlayer.setTitle( "Joueurs connectes" );
    m_btnPlayer.setStyleName( "fmp-button" );
    m_iconAction.setTitle( "Cout en point d'action" );
    m_lblAction.setStyleName( "fmp-status-text" );

    // subscribe all needed models update event
    ModelFmpMain.model().subscribeModelUpdateEvent( this );

    m_panel.setSize( "100%", "100%" );
    m_panel.setHorizontalAlignment( HasHorizontalAlignment.ALIGN_CENTER );
    m_panel.setVerticalAlignment( HasVerticalAlignment.ALIGN_MIDDLE );
    initWidget( m_panel );
  }



  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.ClickListener#onClick(com.google.gwt.user.client.ui.Widget)
   */
  public void onClick(Widget p_sender)
  {
    try
    {
      EventsPlayBuilder actionBuilder = ModelFmpMain.model().getActionBuilder();
      if( p_sender == m_btnOk )
      {
        actionBuilder.userOk();
        ModelFmpMain.model().runCurrentAction();
      }
      else if( p_sender == m_btnCancel )
      {
        actionBuilder.userCancel();
        ModelFmpMain.model().notifyModelUpdate();
      }
      else if( p_sender == m_btnRepairTurret )
      {
        EventBuilderMsg eventBuilderMsg = actionBuilder.userAction( GameLogType.EvtRepair );
        if( eventBuilderMsg == EventBuilderMsg.MustRun )
        {
          ModelFmpMain.model().runSingleAction( actionBuilder.getSelectedAction() );
        }
      }
      else if( p_sender == m_btnFire )
      {
        actionBuilder.userAction( GameLogType.EvtFire );
        MAppMessagesStack.s_instance
            .showMessage( "Selectionez un second destructeur a porte, puis votre cible" );
      }
      else if( p_sender == m_btnControl )
      {
        actionBuilder.userAction( GameLogType.EvtControl );
        MAppMessagesStack.s_instance
            .showMessage( "Selectionez un second destructeur au contact, puis votre cible" );
      }
      else if( p_sender == m_btnFireCoverOn )
      {
        ModelFmpMain.model().setFireCoverDisplayed( true );
      }
      else if( p_sender == m_btnFireCoverOff )
      {
        ModelFmpMain.model().setFireCoverDisplayed( false );
      }
      else if( p_sender == m_btnGrid )
      {
        ModelFmpMain.model().setGridDisplayed( !ModelFmpMain.model().isGridDisplayed() );
      }
      else if( p_sender == m_btnZoomIn )
      {
        ModelFmpMain.model().setZoomDisplayed( EnuZoom.Medium );
      }
      else if( p_sender == m_btnZoomOut )
      {
        ModelFmpMain.model().setZoomDisplayed( EnuZoom.Small );
      }
      else if( p_sender == m_btnMiniMap )
      {
        ModelFmpMain.model().setMiniMapDisplayed( true );
      }
      else if( p_sender == m_btnPlayer )
      {
        ModelFmpMain.model().setMiniMapDisplayed( false );
      }
      else if( p_sender == m_btnInfo )
      {
        m_dlgGameDetail.center();
        m_dlgGameDetail.show();
      }
      else if( p_sender == m_btnRegister || p_sender == m_pnlRegister )
      {
        DlgJoinGame dlg = new DlgJoinGame();
        dlg.show();
        dlg.center();
      }
      else if( p_sender == m_btnEndTurn )
      {
        EbEvtPlayerTurn action = new EbEvtPlayerTurn();
        action.setGame( ModelFmpMain.model().getGame() );
        action.setAccountId( ModelFmpMain.model().getMyAccountId() );
        ModelFmpMain.model().runSingleAction( action );
      }
      else if( p_sender == m_btnTakeOff )
      {
        if( Window.confirm( "Confirmez-vous le decolage de "
            + Messages.getTokenString( actionBuilder.getSelectedToken() ) + " ?" ) )
        {
          EbEvtTakeOff action = new EbEvtTakeOff();
          action.setGame( ModelFmpMain.model().getGame() );
          action.setAccountId( ModelFmpMain.model().getMyAccountId() );
          action.setToken( actionBuilder.getSelectedToken() );
          action.setOldPosition( actionBuilder.getSelectedToken().getPosition() );
          ModelFmpMain.model().runSingleAction( action );
        }
      }
    } catch( RpcFmpException e )
    {
      MAppMessagesStack.s_instance.showWarning( Messages.getString( e ) );
    }
  }

  protected void redraw()
  {
    m_panel.clear();
    ModelFmpMain model = ModelFmpMain.model();
    if( (model == null) )
    {
      return;
    }

    EventsPlayBuilder action = ModelFmpMain.model().getActionBuilder();
    EbToken mainToken = action.getSelectedToken();

    if( !action.isTokenSelected() || ModelFmpMain.model().getGame().isFinished() )
    {
      // add standard actions icon set
      if( ModelFmpMain.model().isFireCoverDisplayed() )
      {
        m_panel.add( m_btnFireCoverOff );
      }
      else
      {
        m_panel.add( m_btnFireCoverOn );
      }
      m_panel.add( m_btnGrid );
      if( ModelFmpMain.model().getZoomDisplayed().getValue() == EnuZoom.Small )
      {
        m_panel.add( m_btnZoomIn );
      }
      else
      {
        m_panel.add( m_btnZoomOut );
      }
      if( (!ModelFmpMain.model().getGame().isAsynchron())
          && (ModelFmpMain.model().getMyRegistration() != null)
          && (ModelFmpMain.model().getGame().getCurrentPlayerRegistration() == ModelFmpMain.model()
              .getMyRegistration()) )
      {
        m_panel.add( m_btnEndTurn );
      }
      if( ModelFmpMain.model().isLogged()
          && ModelFmpMain.model().getMyRegistration() == null
          && !ModelFmpMain.model().getGame().isStarted() )
      {
        m_panel.add( m_btnRegister );
        MAppMessagesStack.s_instance.showMessage( m_pnlRegister );
      }
      if( ModelFmpMain.model().isMiniMapDisplayed() )
      {
        m_panel.add( m_btnPlayer );
      }
      else
      {
        m_panel.add( m_btnMiniMap );
      }
      m_panel.add( m_btnInfo );
    }
    else if( ModelFmpMain.model().getMyRegistration() == null )
    {
      m_panel.add( m_btnCancel );
    }
    else if( action.isRunnable() )
    {
      m_panel.add( m_btnOk );
      m_panel.add( m_iconAction );
      m_lblAction.setText( String.valueOf( ModelFmpMain.model().getActionBuilder().getCost() ) );
      m_panel.add( m_lblAction );
      m_panel.add( m_btnCancel );
    }
    else
    {
      try
      {
        action.check();
      } catch( RpcFmpException e )
      {
        if( (e.getMessage() != null) && !(e.getMessage().trim().length() == 0) )
        {
          MAppMessagesStack.s_instance.showWarning( e.getMessage() );
        }
      }

      // add current actions icon set
      if( (action.isTokenSelected()) || (action.isActionsPending())
          || (action.getActionList().size() != 0) )
      {
        // add cancel button
        m_panel.add( m_btnCancel );
      }

      if( (ModelFmpMain.model().getGame().getTokenFireLength( action.getSelectedToken() ) > 0)
          && (ModelFmpMain.model().getMyRegistration().getEnuColor().isColored( action
              .getSelectedToken().getColor() )) )
      {
        try
        {
          action.exec();
          if( action.getSelectedToken().getBulletCount() > 0 )
          {
            m_panel.add( m_btnFire );
          }
          if( action.getSelectedToken().haveOponentNeighbor() )
          {
            m_panel.add( m_btnControl );
          }
        } catch( RpcFmpException e )
        {
        } finally
        {
          try
          {
            action.unexec();
          } catch( RpcFmpException e )
          {
          }
        }
      }

      if( (action.isBoardTokenSelected()) && (!action.isActionsPending())
          && (mainToken.getType() == TokenType.Freighter) && (model.getMyRegistration() != null)
          && (model.getMyRegistration().getTurretsToRepair() > 0)
          && (model.getMyRegistration().getPtAction() >= 2)
          && (!mainToken.getEnuColor().isColored( model.getMyRegistration().getOriginalColor() ))
          && (model.getMyRegistration().getEnuColor().isColored( mainToken.getColor() ))
          && (model.getGame().getToken( action.getSelectedPosition(), TokenType.Turret ) == null)
          && (!mainToken.getPosition().equals( action.getSelectedPosition() )) )
      {
        // player select a destroyed pod. of a freighter he own (but different
        // from the original one)
        // add the repair turret button
        m_panel.add( m_btnRepairTurret );
      }

      if( (action.isBoardTokenSelected())
          && (!action.isActionsPending())
          && (mainToken.getType() == TokenType.Freighter)
          && (model.getGame().getAllowedTakeOffTurns().contains( model.getGame()
              .getCurrentTimeStep() ))
          && (mainToken.getEnuColor().isColored( model.getMyRegistration().getColor() )) )
      {
        m_panel.add( m_btnTakeOff );
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
