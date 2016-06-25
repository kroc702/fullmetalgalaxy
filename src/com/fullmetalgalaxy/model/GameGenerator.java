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
package com.fullmetalgalaxy.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fullmetalgalaxy.model.persist.AnBoardPosition;
import com.fullmetalgalaxy.model.persist.EbToken;
import com.fullmetalgalaxy.model.persist.Game;


/**
 * @author Kroc
 *
 */
public class GameGenerator
{
  Game game = null;

  public Set<LandType> s_oreAllowedOnLands = new HashSet<LandType>();
  public boolean s_useAllOre = false;
  public boolean s_useOreGenerator = false;


  public GameGenerator(Game game)
  {
    this.game = game;
    s_oreAllowedOnLands.add( LandType.Montain );
    s_oreAllowedOnLands.add( LandType.Plain );
    s_oreAllowedOnLands.add( LandType.Marsh );
    s_oreAllowedOnLands.add( LandType.Reef );
  }

  public void setGame(Game game)
  {
    this.game = game;
  }

  protected Game getGame()
  {
    return game;
  }

  /**
   * remove all token from graveyard
   */
  public void cleanToken()
  {
    Game game = getGame();
    List<EbToken> token2Remove = new ArrayList<EbToken>();
    for( EbToken token : game.getSetToken() )
    {
      if( token.getLocation() == Location.Graveyard )
      {
        token2Remove.add( token );
      }
    }
    for( EbToken token : token2Remove )
    {
      game.getSetToken().remove( token );
      token.incVersion();
    }
  }

  /**
   * remove all ore from map
   */
  public void clearOre()
  {
    Game game = getGame();
    List<EbToken> token2Remove = new ArrayList<EbToken>();
    for( EbToken token : game.getSetToken() )
    {
      if( (token.getType().isOre() || token.getType() == TokenType.Ore2Generator || token.getType() == TokenType.Ore3Generator)
          && token.getLocation() == Location.Board )
      {
        token2Remove.add( token );
      }
    }
    for( EbToken token : token2Remove )
    {
      try
      {
        game.moveToken( token, Location.Graveyard );
      } catch( RpcFmpException e )
      {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      game.removeToken( token );
      token.incVersion();
    }
  }



  /**
   * populate game with minerais token
   */
  public void populateOres()
  {
    clearOre();
    int width = getGame().getLandWidth();
    int height = getGame().getLandHeight();
    int ix = RpcUtil.random( 3 );
    int starty = RpcUtil.random( 3 );

    while( ix < width )
    {
      int iy = starty;
      while( iy < height )
      {
        LandType type = getGame().getLand( ix, iy );
        if( (ix >= 0) && (iy >= 0)
            && (s_oreAllowedOnLands.contains( type ))
            && (getGame().getToken( new AnBoardPosition( ix, iy ) ) == null) )
        {
          EbToken token = new EbToken( TokenType.Ore );
          if( s_useAllOre == true )
          {
            // an arbitrary ore distribution that keep the same overall ore value
            switch( RpcUtil.random( 9 ) )
            {
            case 0:
            case 1:
              token.setType( TokenType.Ore0 );
              break;
            case 6:
            case 7:
              token.setType( TokenType.Ore3 );
              break;
            case 8:
              token.setType( TokenType.Ore5 );
              break;
            default:
              break;
            }
          }
          if( s_useOreGenerator == true 
              && Math.floor( ix * 3 / width ) == 1
              && Math.floor( iy * 3 / height ) == 1
              && RpcUtil.random( 3 ) == 0 )
          {
            // ore generator shall only appear in map center...
            token.setType( TokenType.Ore2Generator );
            if( s_useAllOre == true && RpcUtil.random( 3 ) == 0 )
            {
              token.setType( TokenType.Ore3Generator );
            }
          }
          token.getPosition().setX( ix );
          token.getPosition().setY( iy );
          token.getPosition().setSector( Sector.getRandom() );
          token.setLocation( Location.Board );
          getGame().addToken( token );
        }

        iy += 3;
      }
      AnBoardPosition startPosition = new AnBoardPosition( ix, starty );
      // use a flat coordinate system to avoid infinite loop !
      HexCoordinateSystem coodinateSystem = new HexCoordinateSystem();
      startPosition = coodinateSystem.getNeighbor( startPosition, Sector.NorthEast );
      startPosition = coodinateSystem.getNeighbor( startPosition, Sector.NorthEast );
      startPosition = coodinateSystem.getNeighbor( startPosition, Sector.NorthEast );
      ix = startPosition.getX();
      starty = startPosition.getY();
    }
  }

  private MapSize m_mapSize = MapSize.Medium;

  /**
   * according to the maximum number of player and the MapSize enum, set the width/height
   * @param p_size
   */
  public void setSize(MapSize p_size)
  {
    m_mapSize = p_size;

    int width = getGame().getLandWidth();
    int height = getGame().getLandHeight();
    if( p_size != MapSize.Custom )
    {
      int hexagonCount = p_size.getHexagonPerPlayer() * getGame().getMaxNumberOfPlayer();
      
      if( isHexagonMap() )
      {
        width = (int)Math.floor( Math.sqrt( hexagonCount * 1.34 ) );
        height = width;
      }
      else
      {
        float fheight = (float)Math.sqrt( hexagonCount / 1.6 );
        width = (int)Math.floor( 1.6 * fheight );
        height = (int)Math.ceil( fheight );
      }
    }
    // width shall be even for border less map
    width += width % 2;
    getGame().setLandSize( width, height );
  }

  public void setSize(int p_width, int p_height)
  {
    // width shall be even for border less map
    p_width += p_width % 2;
    if( (getGame().getLandWidth() != p_width) || (getGame().getLandHeight() != p_height) )
    {
      m_mapSize = MapSize.Custom;
      getGame().setLandSize( p_width, p_height );
    }
  }

  public MapSize getSize()
  {
    return m_mapSize;
  }

  /**
   * put a specific land type on every hexagon of the board.
   * @param p_landValue
   */
  public void clearLand(LandType p_land)
  {
    clearOre();
    
    // to be sure the blob have the right size.
    setSize( getSize() );
    int width = getGame().getLandWidth();
    int height = getGame().getLandHeight();
    // getGame().setLandSize( width, height );
    if( !isHexagonMap() )
    {
      for( int x = 0; x < width; x++ )
      {
        for( int y = 0; y < height; y++ )
        {
          getGame().setLand( x, y, p_land );
        }
      }
    }
    else
    {
      AnBoardPosition centre = new AnBoardPosition();
      AnBoardPosition position = new AnBoardPosition();
      int rayon = getGame().getLandHeight() / 2;
      centre.setX( rayon );
      centre.setY( rayon );
      for( int x = 0; x < width; x++ )
      {
        position.setX( x );
        for( int y = 0; y < height; y++ )
        {
          position.setY( y );
          if( getGame().getCoordinateSystem().getDiscreteDistance( position, centre ) > rayon )
          {
            getGame().setLand( x, y, LandType.None );
          }
          else
          {
            getGame().setLand( x, y, p_land );
          }
        }
      }
    }
  }

  private int s_seaPercent = 40;
  private int s_landPercent = 60;
  private boolean s_isHexagonMap = false;
  private boolean s_isLakeBoard = true;

  /**
   * this method was translated from C++ in Full Metal Program.
   */
  public void generLands()
  {

    AnBoardPosition position = new AnBoardPosition();
    int max;
    int nbNull;
    int nbSea;
    int nbReef;
    int nbMarsh;
    int nbPlain;
    int nbMontain;
    // (RpcUtil.random( 100 ) < 60);
    int position_Lig = 0;
    int position_Col = 0;



    // Carte Lacs
    if( s_isLakeBoard )
    {
      // Au debut il y avait la Terre ...
      clearLand( LandType.Plain );
      // Et Dieu crea la Mer ...
      max = (((getGame().getLandHeight() * getGame().getLandWidth()) * s_seaPercent) / 100) / 20;
      for( int i = 0; i < max; i++ )
      {
        do
        {
          position.setY( (RpcUtil.random( getGame().getLandHeight() - 6 ) + 3) );
          position.setX( (RpcUtil.random( getGame().getLandWidth() - 6 ) + 3) );
        } while( getGame().getLand( position ) == LandType.None );
        getGame().setLand( position, LandType.Sea );
        for( AnBoardPosition neighborPosition : getGame().getCoordinateSystem().getAllNeighbors( position ) )
        {
          if( getGame().getLand( neighborPosition ) != LandType.None )
          {
            getGame().setLand( neighborPosition, LandType.Sea );
          }
        }
      }
      for( position_Lig = 0; position_Lig < getGame().getLandHeight(); position_Lig++ )
      {
        for( position_Col = 0; position_Col < getGame().getLandWidth(); position_Col++ )
        {
          position.setX( position_Col );
          position.setY( position_Lig );
          if( getGame().getLand( position ) == LandType.Plain )
          {
            nbSea = 0;
            nbPlain = 0;
            // getGame().setLand( position, LandType.Sea );
            for( AnBoardPosition neighborPosition : getGame().getCoordinateSystem().getAllNeighbors( position ) )
            {
              if( getGame().getLand( neighborPosition ) == LandType.Sea )
              {
                nbSea++;
                if( getGame().getLand( neighborPosition ) != LandType.Sea )
                  nbPlain++;
              }
            }
            if( (nbSea >= 2) && (nbPlain == 2) )
              max = 101;
            else if( nbSea > 0 )
              max = (nbSea * 5) + 20;
            else
              max = 1;
            if( (int)Math.round( Math.random() * 100 ) < max )
              getGame().setLand( position, LandType.Sea );
          }
        }
      }
    }

    // Carte Iles
    else
    {
      // Au Debut il y avait la Mer ...
      clearLand( LandType.Sea );
      // Et Dieu crea la Terre ...
      max = (((getGame().getLandHeight() * getGame().getLandWidth()) * s_landPercent) / 100) / 6;
      for( int i = 0; i < max; i++ )
      {
        do
        {
          position.setY( (RpcUtil.random( getGame().getLandHeight() - 2 ) + 1) );
          position.setX( (RpcUtil.random( getGame().getLandWidth() - 2 ) + 1) );
        } while( getGame().getLand( position ) == LandType.None );
        getGame().setLand( position, LandType.Plain );
        for( AnBoardPosition neighborPosition : getGame().getCoordinateSystem().getAllNeighbors( position ) )
        {
          if( getGame().getLand( neighborPosition ) != LandType.None )
          {
            getGame().setLand( neighborPosition, LandType.Plain );
          }
        }
      }
    }

    // Et puis vint les Recifs ...
    for( position_Lig = 0; position_Lig < getGame().getLandHeight(); position_Lig++ )
    {
      for( position_Col = 0; position_Col < getGame().getLandWidth(); position_Col++ )
      {
        position.setX( position_Col );
        position.setY( position_Lig );
        nbSea = 0;
        nbReef = 0;
        nbPlain = 0;
        for( AnBoardPosition neighborPosition : getGame().getCoordinateSystem().getAllNeighbors( position ) )
        {
          switch( getGame().getLand( neighborPosition ) )
          {
          case Sea:
            nbSea++;
            break;
          case Reef:
            nbReef++;
            break;
          case Plain:
            nbPlain++;
            break;
          default:
              break;
          }
        }
        switch( getGame().getLand( position ) )
        {
        case Plain:
          if( nbSea > 2 )
          {
            if( (int)Math.round( Math.random() * 100 ) < 30 )
            {
              getGame().setLand( position, LandType.Sea );
              break;
            }
          }
          if( nbSea > 0 )
            max = (nbSea * 5) + (nbReef * 5) + 20;
          else if( nbReef > 0 )
            max = (nbReef * 5) + 10;
          else
            max = 2;
          if( (int)Math.round( Math.random() * 100 ) < max )
            getGame().setLand( position, LandType.Reef );
          break;
        case Sea:
          if( (nbPlain > 1) && ((int)Math.round( Math.random() * 100 ) < 50) )
            getGame().setLand( position, LandType.Reef );
          break;
        default:
          break;
        }
      }
    }

    // Et puis vint les Marecages ...
    for( position_Lig = 0; position_Lig < getGame().getLandHeight(); position_Lig++ )
    {
      for( position_Col = 0; position_Col < getGame().getLandWidth(); position_Col++ )
      {
        position.setX( position_Col );
        position.setY( position_Lig );
        nbSea = 0;
        nbReef = 0;
        nbMarsh = 0;
        nbPlain = 0;
        for( AnBoardPosition neighborPosition : getGame().getCoordinateSystem().getAllNeighbors( position ) )
        {
          switch( getGame().getLand( neighborPosition ) )
          {
          case Sea:
            nbSea++;
            break;
          case Reef:
            nbReef++;
            break;
          case Marsh:
            nbMarsh++;
            break;
          case Plain:
            nbPlain++;
            break;
          default:
            break;
          }
        }
        switch( getGame().getLand( position ) )
        {
        case Plain:
          if( (nbReef > 0) || (nbSea > 0) )
            max = (nbReef * 5) + (nbSea * 5) + (nbMarsh * 5) + 20;
          else if( nbMarsh > 0 )
            max = (nbMarsh * 5) + 10;
          else
            max = 2;
          if( (int)Math.round( Math.random() * 100 ) < max )
            getGame().setLand( position, LandType.Marsh );
          break;
        case Reef:
          if( (nbPlain > 1) && ((int)Math.round( Math.random() * 100 ) < 20) )
            getGame().setLand( position, LandType.Marsh );
          break;
        default:
          break;
        }
      }
    }
    for( position_Lig = 0; position_Lig < getGame().getLandHeight(); position_Lig++ )
    {
      for( position_Col = 0; position_Col < getGame().getLandWidth(); position_Col++ )
      {
        if( getGame().getLand( position ) == LandType.Plain )
        {
          position.setX( position_Col );
          position.setY( position_Lig );
          nbSea = 0;
          nbPlain = 0;
          for( AnBoardPosition neighborPosition : getGame().getCoordinateSystem().getAllNeighbors( position ) )
          {
            switch( getGame().getLand( neighborPosition ) )
            {
            case Sea:
            case Reef:
            case Marsh:
              nbSea++;
            case Montain:
            case Plain:
            case None:
              nbPlain++;
            }
          }
          if( (nbSea >= 2) && (nbPlain == 2) )
            getGame().setLand( position, LandType.Marsh );
        }
      }
    }

    // Puis Dieu erigea les Montagnes ...
    for( position_Lig = 0; position_Lig < getGame().getLandHeight(); position_Lig++ )
    {
      for( position_Col = 0; position_Col < getGame().getLandWidth(); position_Col++ )
      {
        position.setX( position_Col );
        position.setY( position_Lig );
        nbMarsh = 0;
        nbPlain = 0;
        nbMontain = 0;
        for( AnBoardPosition neighborPosition : getGame().getCoordinateSystem().getAllNeighbors( position ) )
        {
          switch( getGame().getLand( neighborPosition ) )
          {
          case Marsh:
            nbMarsh++;
            break;
          case Plain:
            nbPlain++;
            break;
          case Montain:
            nbMontain++;
            break;
          default:
            break;
          }
        }
        switch( getGame().getLand( position ) )
        {
        // case EnuLand.Marsh :
        case Plain:
          if( nbMontain > 0 )
          {
            for( AnBoardPosition neighborPosition : getGame().getCoordinateSystem().getAllNeighbors( position ) )
            {
              if( getGame().getLand( neighborPosition ) == LandType.Montain )
                nbMontain++;
            }
            if( nbMontain == 1 )
              max = 50;
            else
              max = 40 - (nbMontain * 5);
          }
          else if( nbPlain + nbMarsh > 2 )
            max = 5;
          else
            max = 1;
          if( (int)Math.round( Math.random() * 100 ) < max )
            getGame().setLand( position, LandType.Montain );
          break;
        default:
          break;
        }
      }
    }

    // Enfin Dieu voulu que le monde soit parfait !
    for( position_Lig = 0; position_Lig < getGame().getLandHeight(); position_Lig++ )
    {
      for( position_Col = 0; position_Col < getGame().getLandWidth(); position_Col++ )
      {
        position.setX( position_Col );
        position.setY( position_Lig );
        nbSea = 0;
        nbReef = 0;
        nbMarsh = 0;
        nbPlain = 0;
        nbMontain = 0;
        nbNull = 0;
        for( AnBoardPosition neighborPosition : getGame().getCoordinateSystem().getAllNeighbors( position ) )
        {
          switch( getGame().getLand( neighborPosition ) )
          {
          case None:
            nbNull++;
            break;
          case Sea:
            nbSea++;
            break;
          case Reef:
            nbReef++;
            break;
          case Marsh:
            nbMarsh++;
            break;
          case Plain:
            nbPlain++;
            break;
          case Montain:
            nbMontain++;
            break;
          default:
            break;
          }
        }
        switch( getGame().getLand( position ) )
        {
        case Sea:
          if( nbSea == 0 )
          {
            nbPlain += nbMontain;
            if( nbReef >= nbMarsh )
            {
              if( nbReef >= nbPlain )
                getGame().setLand( position, LandType.Reef );
              else
                getGame().setLand( position, LandType.Plain );
            }
            else
            {
              if( nbMarsh >= nbPlain )
                getGame().setLand( position, LandType.Marsh );
              else
                getGame().setLand( position, LandType.Plain );
            }
          }
          break;
        case Reef:
        case Marsh:
          if( nbReef + nbSea + nbMarsh == 0 )
            getGame().setLand( position, LandType.Plain );
          break;
        case Plain:
          if( nbMontain + nbPlain == 0 )
          {
            if( nbSea >= nbReef )
            {
              if( nbSea >= nbMarsh )
                getGame().setLand( position, LandType.Sea );
              else
                getGame().setLand( position, LandType.Marsh );
            }
            else
            {
              if( nbReef >= nbMarsh )
                getGame().setLand( position, LandType.Reef );
              else
                getGame().setLand( position, LandType.Marsh );
            }
          }
          break;
        case Montain:
          /*if( nbMontain == 0 )
          {
            if( (int)Math.round( Math.random() * 100 ) < 20 )
            {
              if( nbSea > nbPlain )
                AppMain.model().getGame().setLand( position, EnuLand.Sea );
              else
                AppMain.model().getGame().setLand( position, EnuLand.Plain );
            }
          }*/
          break;
        default:
          break;
        }
      }
    }
  }


  /**
   * @return the landPercent
   */
  public int getLandPercent()
  {
    return s_landPercent;
  }

  /**
   * @param p_landPercent the landPercent to set
   */
  public void setLandPercent(int p_landPercent)
  {
    s_landPercent = p_landPercent;
    s_seaPercent = 100 - s_landPercent;
  }

  public void setLakeBoard(boolean p_isLakeBoard)
  {
    s_isLakeBoard = p_isLakeBoard;
  }
  
  /**
   * @return the isHexagonMap
   */
  public boolean isHexagonMap()
  {
    return s_isHexagonMap;
  }

  /**
   * @param p_isHexagonMap the isHexagonMap to set
   */
  public void setHexagonMap(boolean p_isHexagonMap)
  {
    s_isHexagonMap = p_isHexagonMap;
  }



}
