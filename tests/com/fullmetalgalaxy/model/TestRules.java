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

import org.junit.Test;

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
    gameEngine.play( "./war/puzzles/test/oreManipulation.script" );

    gameEngine.assertRewind();
  }

  @Test
  public void testFireAndControl() throws IOException, ClassNotFoundException, RpcFmpException
  {
    GameEngine4Test gameEngine = new GameEngine4Test( "./war/puzzles/test/model.bin" );
    gameEngine.play( "./war/puzzles/test/fireAndControl.script" );

    gameEngine.assertRewind();
  }

  @Test
  public void testFireSequences() throws IOException, ClassNotFoundException, RpcFmpException
  {
    GameEngine4Test gameEngine = new GameEngine4Test( "./war/puzzles/test/model.bin" );
    // TODO
    gameEngine.assertRewind();
  }

  @Test
  public void testFireCovers() throws IOException, ClassNotFoundException, RpcFmpException
  {
    GameEngine4Test gameEngine = new GameEngine4Test( "./war/puzzles/test/model.bin" );
    // TODO
    gameEngine.assertRewind();
  }

  @Test
  public void testFirePontoon() throws IOException, ClassNotFoundException, RpcFmpException
  {
    GameEngine4Test gameEngine = new GameEngine4Test( "./war/puzzles/test/model.bin" );
    // TODO
    gameEngine.assertRewind();
  }

}
