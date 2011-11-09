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
 *  Copyright 2010, 2011 Vincent Legendre
 *
 * *********************************************************************/

package com.fullmetalgalaxy.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import org.junit.Assert;

import com.fullmetalgalaxy.model.persist.EbPublicAccount;
import com.fullmetalgalaxy.model.persist.EbRegistration;
import com.fullmetalgalaxy.model.persist.EbToken;
import com.fullmetalgalaxy.model.persist.Game;
import com.fullmetalgalaxy.model.persist.gamelog.AnEvent;
import com.fullmetalgalaxy.model.persist.gamelog.AnEventPlay;
import com.fullmetalgalaxy.model.persist.gamelog.EventsPlayBuilder;

/**
 * @author Vincent
 *
 */
public class GameEngine4Test
{
  private Game m_game = null;
  private EbPublicAccount m_myAccount = null;
  private EventsPlayBuilder m_actionBuilder = new EventsPlayBuilder();
  private ScriptInterpretor m_scriptInterpretor = new ScriptInterpretor( this );
  private String m_modelFileName = null;


  public GameEngine4Test()
  {
  }

  public GameEngine4Test(String p_fileName) throws IOException, ClassNotFoundException
  {
    load( p_fileName );
  }

  public void load(String p_fileName) throws IOException, ClassNotFoundException
  {
    m_modelFileName = p_fileName;
    FileInputStream fis = null;
    ObjectInputStream in = null;
    fis = new FileInputStream( new File( p_fileName ) );
    in = new ObjectInputStream( fis );
    ModelFmpInit modelInit = ModelFmpInit.class.cast( in.readObject() );
    in.close();
    fis.close();

    setGame( modelInit.getGame() );
    EbRegistration registration = getGame().getCurrentPlayerRegistration();
    if( registration == null || registration.getAccount() == null )
    {
      registration = getGame().getRegistrationByPlayerOrder().get( 0 );
    }
    if( registration != null )
    {
      setMyAccount( registration.getAccount() );
    }
  }

  public void runCurrentAction() throws RpcFmpException
  {
    getActionBuilder().unexec();

    if( getActionBuilder().getActionList().isEmpty()
        && getActionBuilder().getSelectedAction() != null )
    {
      getActionBuilder().getSelectedAction().checkedExec( getGame() );
      getGame().addEvent( getActionBuilder().getSelectedAction() );
    }
    else if( !getActionBuilder().getActionList().isEmpty() )
    {
      for( AnEventPlay event : getActionBuilder().getActionList() )
      {
        event.checkedExec( getGame() );
        getGame().addEvent( event );
      }
    }
    else
    {
      throw new RuntimeException( "try to run current action, but no action avaiable " );
    }
    getGame().updateLastTokenUpdate( null );
    getActionBuilder().clear();
    // exec trigger after each action
    getGame().execTriggers();
  }

  public void play(String p_fileName) throws RpcFmpException, IOException
  {
    m_scriptInterpretor.play( p_fileName );
  }

  /**
   * note: it throw nothing if no action are under construction
   * @throws RpcFmpException is current building action are invalid
   */
  public void checkActionBuilder() throws RpcFmpException
  {
    if( !m_actionBuilder.getActionList().isEmpty() )
    {
      m_actionBuilder.check();
    }
  }


  protected void rewindAllEvents() throws RpcFmpException
  {
    int currentActionIndex = getGame().getLogs().size();
    while( (currentActionIndex > 0) )
    {
      currentActionIndex--;
      AnEvent action = getGame().getLogs().get( currentActionIndex );
      action.unexec( getGame() );
    }
  }

  public void assertRewind() throws IOException, ClassNotFoundException, RpcFmpException
  {
    // first rewind all event... so check this is working without exception
    rewindAllEvents();
    // then load same game
    Assert.assertNotNull( m_modelFileName );
    GameEngine4Test controlEngine = new GameEngine4Test( m_modelFileName );
    // this isn't useless in case model loaded from file already contain logs
    controlEngine.rewindAllEvents();
    assertSimilarPosition( controlEngine );
  }

  protected void assertSimilarPosition(GameEngine4Test p_controlEngine)
  {
    for( EbToken controlToken : p_controlEngine.getGame().getSetToken() )
    {
      EbToken token = getGame().getToken( controlToken.getId() );
      Assert.assertNotNull( token );
      assertSimilar( controlToken, token );
    }
  }

  private void assertSimilar(EbToken p_tokenA, EbToken p_tokenB)
  {
    Assert.assertEquals( p_tokenA.getId(), p_tokenB.getId() );
    Assert.assertEquals( p_tokenA.getVersion(), p_tokenB.getVersion() );
    Assert.assertEquals( p_tokenA.getType(), p_tokenB.getType() );
    Assert.assertEquals( p_tokenA.getColor(), p_tokenB.getColor() );
    Assert.assertEquals( p_tokenA.getLocation(), p_tokenB.getLocation() );
    if( p_tokenA.getLocation() == Location.Board )
    {
      Assert.assertEquals( p_tokenA.getPosition().getX(), p_tokenB.getPosition().getX() );
      Assert.assertEquals( p_tokenA.getPosition().getY(), p_tokenB.getPosition().getY() );
      Assert.assertEquals( p_tokenA.getPosition().getSector(), p_tokenB.getPosition().getSector() );
    }
    Assert.assertEquals( p_tokenA.getBulletCount(), p_tokenB.getBulletCount(), 0.01 );
    if( p_tokenA.getCarrierToken() == null )
    {
      Assert.assertNull( p_tokenB.getCarrierToken() );
    }
    else
    {
      Assert.assertEquals( p_tokenA.getCarrierToken().getId(), p_tokenB.getCarrierToken().getId() );
    }
    if( p_tokenA.getFireDisablingList() == null )
    {
      Assert.assertNull( p_tokenB.getFireDisablingList() );
    }
    else
    {
      int i = 0;
      while( i < p_tokenA.getFireDisablingList().size()
          || i < p_tokenB.getFireDisablingList().size() )
      {
        Assert.assertEquals( p_tokenA.getFireDisablingList().get( i ), p_tokenB
            .getFireDisablingList().get( i ) );
        i++;
      }
    }
    // private Set<EbToken> m_setContainToken isn't tested...
  }

  /**
   * @return the game
   */
  public Game getGame()
  {
    return m_game;
  }

  /**
   * @param p_game the game to set
   */
  public void setGame(Game p_game)
  {
    m_game = p_game;
    getActionBuilder().setGame( getGame() );
  }

  /**
   * @return the myAccount
   */
  public EbPublicAccount getMyAccount()
  {
    return m_myAccount;
  }

  /**
   * @param p_myAccount the myAccount to set
   */
  public void setMyAccount(EbPublicAccount p_myAccount)
  {
    m_myAccount = p_myAccount;
    getActionBuilder().setMyAccount( getMyAccount() );
  }

  /**
   * @return the actionBuilder
   */
  public EventsPlayBuilder getActionBuilder()
  {
    return m_actionBuilder;
  }



}
