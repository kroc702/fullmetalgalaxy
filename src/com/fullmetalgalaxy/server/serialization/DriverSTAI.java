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
package com.fullmetalgalaxy.server.serialization;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import com.fullmetalgalaxy.model.LandType;
import com.fullmetalgalaxy.model.ModelFmpInit;
import com.fullmetalgalaxy.model.persist.EbToken;
import com.fullmetalgalaxy.server.FmpLogger;

/**
 * @author vlegendr
 *
 *
 * format for AI made by bob arning.
 * 
 * --fmp.start---
HEX. x. y. terrain
ORE. x. y.
for terrain: FMPWater=1 FMPReef=2 FMPSwamp=3 FMPLand=4 FMPMountain=5

---fmp.moves---
'create'. newUnit idNumber. newUnit className
'liftoff'. freighter idNumber
'turn'. currentTurn. application tideAtTurn: currentTurn
'player'. currentPlayer playerNumber
'repair'. turret idNumber
'land'. currentFreighter idNumber. aPoint x. aPoint y. orientation
'captureTurret'. turret idNumber. unit1 idNumber    
'capture'. enemy idNumber. unit1 idNumber. unit2 idNumber
'shoot'. eachEnemy idNumber. unit1 idNumber. unit2 idNumber
'move'. unit idNumber. x1. y1. x2. y2
'build'. newUnit idNumber. x1. y1. weatherHen idNumber.
'load'. unit idNumber. receiver idNumber
'loadOre'. ore idNumber. freighter idNumber
'repair'. turret idNumber
'unload'. unit idNumber. x1. y1. fromWhom idNumber
'transfer'. anItem idNumber. fromUnit idNumber. toUnit idNumber
 */
public class DriverSTAI extends DriverFileFormat
{
  /**
   * The log channel
   */
  private final static FmpLogger LOG = FmpLogger.getLogger( DriverSTAI.class.getName() );
  
  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.tools.DriverFileFormat#loadGame(java.io.InputStream)
   */
  @Override
  public ModelFmpInit loadGame(InputStream p_input)
  {
    LOG.error( "unimplemented" );
    return null;
  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.tools.DriverFileFormat#saveGame(com.fullmetalgalaxy.model.ModelFmpInit, java.io.OutputStream)
   */
  @Override
  public void saveGame(ModelFmpInit p_game, OutputStream p_output)
  {
    PrintStream printStream = new PrintStream( p_output );

    // print terrain
    for( int y = 0; y < p_game.getGame().getLandHeight(); y++ )
    {
      for( int x = 0; x < p_game.getGame().getLandWidth(); x++ )
      {
        printStream.println( "HEX," + y + "," + x + "," + getConstant( p_game.getGame().getLand( x, y ) ) );
      }
    }

    // print ore
    for( EbToken token : p_game.getGame().getSetToken() )
    {
      if( token.getType().isOre() )
      {
        printStream.println( "ORE," + token.getPosition().getY() + "," + token.getPosition().getX() );
      }
    }

    printStream.close();

  }

  private int getConstant(LandType land){
    switch(land){
    default:
    case None:
      return 0;
    case Sea:
      return 1;
    case Reef:
      return 2;
    case Marsh:
      return 3;
    case Plain:
      return 4;
    case Montain:
      return 5;
    }
  }
}
