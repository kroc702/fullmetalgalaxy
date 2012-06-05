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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.fullmetalgalaxy.model.persist.AnBoardPosition;
import com.fullmetalgalaxy.model.persist.EbToken;
import com.fullmetalgalaxy.model.persist.gamelog.EventBuilderMsg;
import com.fullmetalgalaxy.model.persist.gamelog.GameLogType;

/**
 * @author Vincent
 *
 */
public class ScriptInterpretor
{
  private GameEngine4Test m_gameEngine = null;
  private int m_assertExceptionLine = -1;
  private int m_lineCount = 0;

  /**
   * 
   */
  public ScriptInterpretor(GameEngine4Test p_gameEngine)
  {
    assert p_gameEngine != null;
    m_gameEngine = p_gameEngine;
  }


  private void playLine(String p_line) throws RpcFmpException
  {
    if( p_line == null || p_line.isEmpty() )
    {
      return;
    }
    String line[] = p_line.split( " " );
    if( line[0].equalsIgnoreCase( "action" ) )
    {
      // action button click
      //
      GameLogType type = GameLogType.valueOf( line[1] );
      EventBuilderMsg eventBuilderMsg = m_gameEngine.getActionBuilder().userAction( type );
      if( eventBuilderMsg == EventBuilderMsg.MustRun )
      {
        m_gameEngine.runCurrentAction();
      }
    }
    else if( line[0].equalsIgnoreCase( "board" ) )
    {
      // board click
      //
      AnBoardPosition position = new AnBoardPosition( Integer.parseInt( line[1] ),
          Integer.parseInt( line[2] ) );
      boolean searchPath = Boolean.parseBoolean( line[3] );
      EventBuilderMsg eventBuilderMsg = m_gameEngine.getActionBuilder().userBoardClick( position,
          searchPath );
      if( eventBuilderMsg == EventBuilderMsg.MustRun )
      {
        m_gameEngine.runCurrentAction();
      }
    }
    else if( line[0].equalsIgnoreCase( "cancel" ) )
    {
      // cancel click
      //
      m_gameEngine.getActionBuilder().userCancel();
    }
    else if( line[0].equalsIgnoreCase( "ok" ) )
    {
      // ok button click
      //
      m_gameEngine.getActionBuilder().userOk();
      m_gameEngine.runCurrentAction();
    }
    else if( line[0].equalsIgnoreCase( "token" ) )
    {
      // token that aren't on board click
      //
      EbToken token = m_gameEngine.getActionBuilder().getGame()
          .getToken( Long.parseLong( line[1] ) );

      // special case for weather hen token construction
      if( line.length >= 3 && !line[2].equalsIgnoreCase( "Ore" ) )
      {
        EbToken fakeToken = new EbToken( TokenType.valueOf( line[2] ) );
        fakeToken.setId( token.getId() );
        if( fakeToken.getType().canBeColored(  ) )
        {
          fakeToken.setColor( token.getCarrierToken().getColor() );
        }
        fakeToken.setVersion( token.getVersion() );
        fakeToken.setLocation( Location.ToBeConstructed );
        fakeToken.setCarrierToken( token.getCarrierToken() );
        token = fakeToken;
      }
      m_gameEngine.getActionBuilder().userTokenClick( token );
    }
    else if( line[0].equalsIgnoreCase( "assert" ) )
    {
      // assert action (not a user action)
      //
      if( line.length == 2 && line[1].equalsIgnoreCase( "exception" ) )
      {
        m_assertExceptionLine = m_lineCount + 1;
      }
    }
    m_gameEngine.checkActionBuilder();
  }

  public void run(BufferedReader p_reader) throws IOException
  {
    String strLine;
    m_lineCount = 0;
    // Read File Line By Line
    do
    {
      m_lineCount++;
      strLine = p_reader.readLine();
      try
      {
        playLine( strLine );
      } catch( RpcFmpException e )
      {
        if( m_assertExceptionLine != m_lineCount )
        {
          throw new RuntimeException( "Rules error at line " + m_lineCount + ": " + strLine, e );
        }
        m_assertExceptionLine = -1;
      } catch( Throwable th )
      {
        throw new RuntimeException( "Error while running script at line " + m_lineCount + ": "
            + strLine, th );
      }
      if( m_assertExceptionLine == m_lineCount )
      {
        throw new RuntimeException( "Rules error expected at line " + m_lineCount + ": " + strLine );
      }
    } while( strLine != null );
  }

  public void runFile(String p_fileName) throws IOException
  {
    FileInputStream fstream = new FileInputStream( p_fileName );
    // Get the object of DataInputStream
    DataInputStream in = new DataInputStream( fstream );
    run( new BufferedReader( new InputStreamReader( in ) ) );
  }

  public void run(String p_script) throws IOException
  {
    InputStream fstream = new ByteArrayInputStream( p_script.getBytes() );
    // Get the object of DataInputStream
    DataInputStream in = new DataInputStream( fstream );
    run( new BufferedReader( new InputStreamReader( in ) ) );
  }


}
