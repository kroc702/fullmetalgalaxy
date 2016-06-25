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
 *  Copyright 2010 to 2015 Vincent Legendre
 *
 * *********************************************************************/
package com.fullmetalgalaxy.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fullmetalgalaxy.model.Company;
import com.fullmetalgalaxy.model.EnuColor;
import com.fullmetalgalaxy.model.GameGenerator;
import com.fullmetalgalaxy.model.ModelFmpInit;
import com.fullmetalgalaxy.model.persist.EbPublicAccount;
import com.fullmetalgalaxy.model.persist.Game;
import com.fullmetalgalaxy.model.persist.gamelog.EbGameJoin;

/**
 * @author Vincent
 * 
 * this servlet allow other programs to read game and act as a human player over an http interface.
 * 
 * url mapping: /api/newgame/*
 */
public class PublicNewGameApiServlet extends HttpServlet
{
  private static final long serialVersionUID = 533579014067656255L;
  private final static FmpLogger log = FmpLogger.getLogger( PublicNewGameApiServlet.class.getName() );

  /**
   * 
   */
  public PublicNewGameApiServlet()
  {
  }


  /* (non-Javadoc)
   * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
   */
  @Override
  protected void doGet(HttpServletRequest p_req, HttpServletResponse p_resp)
      throws ServletException, IOException
  {
    try
    {
      if( !Auth.isUserLogged( p_req, p_resp ) )
      {
        log.info( "create new game but not logged" );
        p_resp.sendRedirect( "/auth.jsp" );
        return;
      }

      String[] aiPlayers = p_req.getParameterValues( "ai" );
      if( aiPlayers.length == 0 ) {
        aiPlayers = new String[1];
        aiPlayers[0] = "stai";
      }
      
      
      FmgDataStore dataStore = new FmgDataStore( false );
      EbAccount account = Auth.getUserAccount( p_req, p_resp );

      Game game = new Game();
      GameGenerator generator = new GameGenerator( game );

      // configure options
      game.setMaxNumberOfPlayer( aiPlayers.length + 1 );

      if( p_req.getParameter( "map" ) != null )
      {
        // create map from an existing game
        ModelFmpInit modelInitMap = GameServicesImpl.sgetModelFmpInit( p_req, p_resp, p_req.getParameter( "map" ) );
        Game gameMap = modelInitMap.getGame();
        game.setMapShape( gameMap.getMapShape() );
        game.setLandSize( gameMap.getLandWidth(), gameMap.getLandHeight() );
        game.setLands( gameMap.getLands() );
        game.setPlanetType( gameMap.getPlanetType() );
        game.setMapUri( gameMap.getMapUri() );
      }
      else
      {
        // generate map
        generator.generLands();
      }

      // create ore
      generator.populateOres();

      // game is just created
      GameWorkflow.gameOpen( game );

      // add AI
      for( String aiPlayer : aiPlayers )
      {
        EbGameJoin joinEvent = new EbGameJoin();
        EbPublicAccount aiAccount = new EbPublicAccount();
        // TODO ask server to get this list
        if( aiPlayer.equals( "stai" ) )
        {
          aiAccount.setId( 5180826044071936L );
          joinEvent.setColor( EnuColor.Yellow );
        }
        else if( aiPlayer.equals( "killerai" ) )
        {
          aiAccount.setId( 5087341249036288L );
          joinEvent.setColor( EnuColor.Red );
        }
        else if( aiPlayer.equals( "niceai" ) )
        {
          aiAccount.setId( 5148254354276352L );
          joinEvent.setColor( EnuColor.Green );
        }
        else
        {
          continue;
        }
        aiAccount.setPseudo( aiPlayer );
        aiAccount.setAI( true );
        joinEvent.setAccount( aiAccount );
        joinEvent.setCompany( Company.Freelancer );
        joinEvent.setGame( game );
        // game.getFreePlayersColors().
        joinEvent.checkedExec( game );
        game.addEvent( joinEvent );
      }

      // add logged player
      EbGameJoin joinEvent = new EbGameJoin();
      joinEvent.setAccount( account );
      joinEvent.setColor( EnuColor.Blue );
      joinEvent.setCompany( Company.Freelancer );
      joinEvent.setGame( game );
      joinEvent.checkedExec( game );
      game.addEvent( joinEvent );

      // an automatic update ?
      GameWorkflow.checkUpdate( game );

      dataStore.put( game );
      dataStore.close();



      p_resp.sendRedirect( "/game.jsp?id=" + game.getId() );
    } catch( Throwable th )
    {
      p_resp.sendRedirect( "/genericmsg.jsp?title=Erreur:" + th.getMessage() );
    }
  }




}
