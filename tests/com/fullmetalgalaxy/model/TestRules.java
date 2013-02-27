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


import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import com.fullmetalgalaxy.model.persist.AnBoardPosition;
import com.fullmetalgalaxy.model.persist.EbToken;

/**
 * @author Vincent
 *
 */
public class TestRules
{


  @Test
  public void testOreManipulation() throws IOException, ClassNotFoundException, RpcFmpException
  {
    GameEngine4Test gameEngine = new GameEngine4Test( "./war/puzzles/test/model.bin" );
    gameEngine.runScriptFile( "./war/puzzles/test/oreManipulation.script" );

    gameEngine.assertRewind();
  }

  @Test
  public void testFireAndControl() throws IOException, ClassNotFoundException, RpcFmpException
  {
    GameEngine4Test gameEngine = new GameEngine4Test( "./war/puzzles/test/model.bin" );
    gameEngine.runScriptFile( "./war/puzzles/test/fireAndControl.script" );

    // check that barge 16,11 and his content are cyan
    EbToken barge = gameEngine.getGame().getToken( new AnBoardPosition( 16, 11 ) );
    Assert.assertEquals( barge.getType(), TokenType.Barge );
    Assert.assertEquals( barge.getColor(), EnuColor.Cyan );
    for( EbToken token : barge.getContains() )
    {
      Assert.assertEquals( token.getColor(), EnuColor.Cyan );
    }

    gameEngine.assertRewind();
  }

  @Test
  public void testFireSequences() throws IOException, ClassNotFoundException, RpcFmpException
  {
    GameEngine4Test gameEngine = new GameEngine4Test( "./war/puzzles/test/model.bin" );
    gameEngine.runScriptFile( "./war/puzzles/test/simpleFireSequences.script" );

    gameEngine.assertRewind();
  }

  @Test
  public void testFireCovers() throws IOException, ClassNotFoundException, RpcFmpException
  {
    GameEngine4Test gameEngine = new GameEngine4Test( "./war/puzzles/test/model.bin" );
    gameEngine.runScriptFile( "./war/puzzles/test/fireCovers.script" );

    gameEngine.assertRewind();
  }

  @Test
  public void testFireCoversIssue183() throws IOException, ClassNotFoundException, RpcFmpException
  {
    GameEngine4Test gameEngine = new GameEngine4Test( "./war/puzzles/test/model.bin" );
    gameEngine.runScriptFile( "./war/puzzles/test/issue183.script" );

    gameEngine.assertRewind();
  }

  @Test
  public void testRotatingNeutralisation() throws IOException, ClassNotFoundException,
      RpcFmpException
  {
    GameEngine4Test gameEngine = new GameEngine4Test( "./war/puzzles/test/model.bin" );
    gameEngine.runScriptFile( "./war/puzzles/test/rotatingNeutralisation.script" );

    gameEngine.assertRewind();
  }

  @Test
  public void testRotatingNeutralisation2() throws IOException, ClassNotFoundException,
      RpcFmpException
  {
    GameEngine4Test gameEngine = new GameEngine4Test( "./war/puzzles/test/model.bin" );
    gameEngine.runScriptFile( "./war/puzzles/test/rotatingNeutralisation2.script" );

    gameEngine.assertRewind();
  }

  @Test
  public void testWeatherHenBuilding() throws IOException, ClassNotFoundException, RpcFmpException
  {
    GameEngine4Test gameEngine = new GameEngine4Test( "./war/puzzles/test/model.bin" );
    gameEngine.runScriptFile( "./war/puzzles/test/weatherHenBuilding.script" );

    gameEngine.assertRewind();
  }

  @Test
  public void testDestroyPontoon() throws IOException, ClassNotFoundException, RpcFmpException
  {
    GameEngine4Test gameEngine = new GameEngine4Test( "./war/puzzles/test/model.bin" );
    gameEngine.runScriptFile( "./war/puzzles/test/destroyPontoon.script" );

    // pontoon 19 4 shouldn't be destroyed
    Assert.assertNotNull( gameEngine.getGame().getToken( new AnBoardPosition( 19, 4 ),
        TokenType.Pontoon ) );
    Assert.assertNull( gameEngine.getGame().getToken( new AnBoardPosition( 19, 5 ) ) );
    
    // check all destroyed in ..
    Assert.assertNull( gameEngine.getGame().getToken( new AnBoardPosition( 9, 10 ) ) );
    Assert.assertNull( gameEngine.getGame().getToken( new AnBoardPosition( 8, 10 ) ) );
    Assert.assertNull( gameEngine.getGame().getToken( new AnBoardPosition( 8, 11 ) ) );
    Assert.assertNotNull( gameEngine.getGame().getToken( new AnBoardPosition( 8, 12 ) ) );

    // same test (so undo all actions) but with crab
    gameEngine = new GameEngine4Test( "./war/puzzles/test/model.bin" );
    gameEngine.runScript( "board 8 12 false\n" +
                  		"board 8 13 false\n" +
                  		"board 8 13 false\n" );
    // check all destroyed in ...
    Assert.assertNull( gameEngine.getGame().getToken( new AnBoardPosition( 9, 10 ) ) );
    Assert.assertNull( gameEngine.getGame().getToken( new AnBoardPosition( 8, 10 ) ) );
    Assert.assertNull( gameEngine.getGame().getToken( new AnBoardPosition( 8, 11 ) ) );
    Assert.assertNull( gameEngine.getGame().getToken( new AnBoardPosition( 8, 12 ) ) );
    
    
    gameEngine.assertRewind();
  }

}
