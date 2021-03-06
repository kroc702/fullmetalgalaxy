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
import com.fullmetalgalaxy.model.persist.EbRegistration;

/**
 * @author Vincent
 *
 */
public class TestPuzzles
{


  @Test
  public void testTutorial() throws IOException, ClassNotFoundException, RpcFmpException
  {
    GameEngine4Test gameEngine = new GameEngine4Test( "./war/puzzles/tutorial/model_en.bin" );
    gameEngine.runScriptFile( "./war/puzzles/tutorial/solution.script" );

    // check that last action (turret repair) worked
    Assert.assertNotNull( gameEngine.getGame().getToken( new AnBoardPosition( 19, 6 ),
        TokenType.Turret ) );
    EbRegistration registration = gameEngine.getGame().getRegistration(
        gameEngine.getGame().getCurrentPlayerIds().get( 0 ) );
    Assert.assertEquals( registration.getPtAction(), 25 );

    gameEngine.assertRewind();
  }

  @Test
  public void testLesTourellesSauterontTroisFois() throws IOException, ClassNotFoundException,
      RpcFmpException
  {
    GameEngine4Test gameEngine = new GameEngine4Test(
        "./war/puzzles/lestourellessauteronttroisfois/model.bin" );
    gameEngine.runScriptFile( "./war/puzzles/lestourellessauteronttroisfois/solution.script" );

    // check that last action (turret repair) worked
    Assert.assertNotNull( gameEngine.getGame().getToken( new AnBoardPosition( 11, 7 ),
        TokenType.Turret ) );
    EbRegistration registration = gameEngine.getGame().getRegistration(
        gameEngine.getGame().getCurrentPlayerIds().get( 0 ) );
    Assert.assertEquals( registration.getPtAction(), 3 );

    gameEngine.assertRewind();
  }

}
