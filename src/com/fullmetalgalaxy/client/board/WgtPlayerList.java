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

import java.util.List;

import com.fullmetalgalaxy.client.ModelFmpMain;
import com.fullmetalgalaxy.client.WgtView;
import com.fullmetalgalaxy.client.ressources.Messages;
import com.fullmetalgalaxy.model.EnuColor;
import com.fullmetalgalaxy.model.SourceModelUpdateEvents;
import com.fullmetalgalaxy.model.persist.EbRegistration;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author Kroc
 *
 */
public class WgtPlayerList extends WgtView
{
  // UI
  VerticalPanel m_panel = new VerticalPanel();

  /**
   * @param p_fmpCtrl
   */
  public WgtPlayerList()
  {
    super();

    initWidget( m_panel );

    // subscribe all needed models update event
    ModelFmpMain.model().subscribeModelUpdateEvent( this );

    // Give the overall composite a style name.
    setStyleName( "WgtPlayerList" );
  }

  protected void redraw()
  {
    assert ModelFmpMain.model().getGame().getSetRegistration() != null;
    int winnerPoint = 0;
    String winnerLogin = "";

    m_panel.clear();
    m_panel.add( new Label( MAppBoard.s_messages.xPlayers( ModelFmpMain.model().getGame()
        .getSetRegistration().size() ) ) );

    // get player order
    List<EbRegistration> sortedRegistration = ModelFmpMain.model().getGame()
        .getRegistrationByPlayerOrder();

    String htmlPlayers = "";
    for( EbRegistration registration : sortedRegistration )
    {
      // display all colors
      EnuColor color = registration.getEnuColor();
      int colorIndex = 0;
      for( colorIndex = 0; colorIndex < EnuColor.getTotalNumberOfColor(); colorIndex++ )
      {
        if( color.isColored( EnuColor.getColorFromIndex( colorIndex ) ) )
        {
          htmlPlayers += " <IMG SRC='" + EnuColor.getColorFromIndex( colorIndex ).toString()
              + "/icon.gif' WIDTH=16 HEIGHT=16 BORDER=0 TITLE='"
              + Messages.getColorString( color.getValue() ) + "'> ";
        }
      }
      if( color.getValue() == EnuColor.None )
      {
        htmlPlayers += " <IMG SRC='icon.gif' WIDTH=16 HEIGHT=16 BORDER=0 TITLE='"
            + Messages.getColorString( color.getValue() ) + "'> ";
      }

      // display player login
      if( ModelFmpMain.model().getGame().getCurrentPlayerRegistration() == registration )
      {
        htmlPlayers += "<b>";
      }

      int point = 0;
      if( registration.haveAccount() )
      {
        point = registration.getWinningPoint();
        String pseudo = ModelFmpMain.model().getAccount( registration.getAccountId() ).getPseudo();
        if( point > winnerPoint )
        {
          winnerPoint = point;
          winnerLogin = pseudo;
        }
        htmlPlayers += MAppBoard.s_messages.playerDescription( pseudo, point );
      }
      else if( registration.getAccountId() == ModelFmpMain.model().getMyAccountId() )
      {
        point = registration.getWinningPoint();
        if( point > winnerPoint )
        {
          winnerPoint = point;
          winnerLogin = ModelFmpMain.model().getMyPseudo();
        }
        htmlPlayers += MAppBoard.s_messages.playerDescription( ModelFmpMain.model().getMyPseudo(),
            point );
      }
      else
      {
        point = registration.getWinningPoint();
        if( point > winnerPoint )
        {
          winnerPoint = point;
          winnerLogin = "???";
        }
        htmlPlayers += MAppBoard.s_messages.playerDescription( "???", point );
      }

      if( ModelFmpMain.model().getGame().getCurrentPlayerRegistration().getId() == registration
          .getId() )
      {
        htmlPlayers += "</b>";
      }
      htmlPlayers += "<br/>";
    }
    HTMLPanel playersPanel = new HTMLPanel( htmlPlayers );
    playersPanel.setWidth( "100%" );
    m_panel.add( playersPanel );

    // display winner !
    if( ModelFmpMain.model().getGame().isFinished() )
    {
      Image image = new Image( "winner.jpg" );
      image.setWidth( "100%" );
      m_panel.add( image );
      m_panel.add( new HTMLPanel( "<center><big><b>" + winnerLogin + "</b></big></center>" ) );
    }
  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.client.WgtView#notifyHmiUpdate()
   */
  @Override
  public void notifyHmiUpdate()
  {
    redraw();
  }


  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.client.ModelUpdateListener#notifyModelUpdate(com.fullmetalgalaxy.client.CtrModel)
   */
  @Override
  public void onModelUpdate(SourceModelUpdateEvents p_ModelSender)
  {
    // TODO optimisation: redraw only if required
    redraw();
  }

}
