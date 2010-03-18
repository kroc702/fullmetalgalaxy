/**
 * 
 */
package com.fullmetalgalaxy.client.creation;

import java.util.ArrayList;
import java.util.List;

import com.fullmetalgalaxy.client.ModelFmpMain;
import com.fullmetalgalaxy.model.LandType;
import com.fullmetalgalaxy.model.Location;
import com.fullmetalgalaxy.model.RpcFmpException;
import com.fullmetalgalaxy.model.RpcUtil;
import com.fullmetalgalaxy.model.Sector;
import com.fullmetalgalaxy.model.TokenType;
import com.fullmetalgalaxy.model.persist.AnBoardPosition;
import com.fullmetalgalaxy.model.persist.EbGame;
import com.fullmetalgalaxy.model.persist.EbToken;
import com.google.gwt.user.client.Random;


/**
 * @author Kroc
 *
 */
public class GameGenerator
{
  public GameGenerator()
  {
  }

  protected static EbGame getGame()
  {
    return ModelFmpMain.model().getGame();
  }

  /**
   * remove all token from graveyard
   */
  public static void cleanToken()
  {
    EbGame game = ModelFmpMain.model().getGame();
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
      token.setGame( null );
      token.incVersion();
    }
  }

  /**
   * remove all ore from map
   */
  public static void clearOre()
  {
    EbGame game = ModelFmpMain.model().getGame();
    List<EbToken> token2Remove = new ArrayList<EbToken>();
    for( EbToken token : game.getSetToken() )
    {
      if( token.getType() == TokenType.Ore && token.getLocation() == Location.Board )
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
      game.getSetToken().remove( token );
      token.setGame( null );
      token.incVersion();
    }
  }

  /**
   * populate game with minerais token
   */
  public static void populateOres()
  {
    clearOre();
    int width = getGame().getLandWidth();
    int height = getGame().getLandHeight();
    int startx = Random.nextInt( 3 );
    int starty = Random.nextInt( 3 );
    for( int ix = startx; ix < width; ix += 3 )
    {
      for( int iy = starty; iy < height; iy += 3 )
      {
        LandType type = getGame().getLand( ix, iy );
        if( (type == LandType.Reef) || (type == LandType.Marsh) || (type == LandType.Plain)
            || (type == LandType.Montain) )
        {
          EbToken token = new EbToken( TokenType.Ore );
          token.getPosition().setX( ix );
          token.getPosition().setY( iy );
          token.getPosition().setSector( Sector.getRandom() );
          token.setLocation( Location.Board );
          getGame().addToken( token );
        }
      }
    }
  }

  private static MapSize m_mapSize = MapSize.Medium;

  /**
   * according to the maximum number of player and the MapSize enum, set the width/height
   * @param p_size
   */
  public static void setSize(MapSize p_size)
  {
    m_mapSize = p_size;

    int width = getGame().getLandWidth();
    int height = getGame().getLandHeight();
    if( p_size != MapSize.Custom )
    {
      int hexagonCount = p_size.getHexagonPerPlayer() * getGame().getMaxNumberOfPlayer();
      float fheight = (float)Math.sqrt( hexagonCount / 1.6 );
      width = (int)Math.floor( 1.6 * fheight );
      height = (int)Math.ceil( fheight );
      if( isHexagonMap() )
      {
        width = (int)Math.floor( 1.3 * fheight );
        if( width % 2 == 0 )
        {
          width--;
        }
        height = width;
      }
    }
    getGame().setLandSize( width, height );
  }

  public static void setSize(int p_width, int p_height)
  {
    if( (getGame().getLandWidth() != p_width) || (getGame().getLandHeight() != p_height) )
    {
      m_mapSize = MapSize.Custom;
      getGame().setLandSize( p_width, p_height );
    }
  }

  public static MapSize getSize()
  {
    return m_mapSize;
  }

  /**
   * put a specific land type on every hexagon of the board.
   * @param p_landValue
   */
  public static void clearLand(LandType p_land)
  {
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
          if( position.getHexDistance( centre ) > rayon )
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

  private static int s_seaPercent = 40;
  private static int s_landPercent = 60;
  private static boolean s_isHexagonMap = false;

  /**
   * this method was translated from C++ in Full Metal Program.
   */
  public static void generLands()
  {

    AnBoardPosition position = new AnBoardPosition();
    int max;
    int nbNull;
    int nbSea;
    int nbReef;
    int nbMarsh;
    int nbPlain;
    int nbMontain;
    boolean isLakeBoard = getLandPercent() >= 45 ? true : false;
    // (RpcUtil.random( 100 ) < 60);
    int position_Lig = 0;
    int position_Col = 0;


    // Initialiser Carte
    if( isLakeBoard )
    {
      clearLand( LandType.Plain );
    }
    else
    {
      clearLand( LandType.Sea );
    }


    // Carte Lacs
    if( isLakeBoard )
    {
      // Au debut il y avait la Terre ...
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
        for( int iSector = 0; iSector < Sector.values().length; iSector++ )
        {
          if( getGame().getLand( position.getNeighbour( Sector.values()[iSector] ) ) != LandType.None )
          {
            getGame().setLand( position.getNeighbour( Sector.values()[iSector] ), LandType.Sea );
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
            for( int iSector = 0; iSector < Sector.values().length; iSector++ )
            {
              if( getGame().getLand( position.getNeighbour( Sector.values()[iSector] ) ) == LandType.Sea )
              {
                nbSea++;
                if( getGame().getLand( position.getNeighbour( Sector.values()[iSector].getNext() ) ) != LandType.Sea )
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
      // Et Dieu crea la Terre ...
      max = (((getGame().getLandHeight() * getGame().getLandWidth()) * s_landPercent) / 100) / 13;
      for( int i = 0; i < max; i++ )
      {
        do
        {
          position_Lig = (short)(RpcUtil.random( getGame().getLandHeight() ));
          position_Col = (short)(RpcUtil.random( getGame().getLandWidth() ));
          position.setX( position_Col );
          position.setY( position_Lig );
        } while( getGame().getLand( position ) == LandType.None );
        getGame().setLand( position, LandType.Plain );
        if( (int)Math.round( Math.random() * 100 ) < 50 )
        {
          for( int iSector = 0; iSector < Sector.values().length; iSector++ )
          {
            if( getGame().getLand( position.getNeighbour( Sector.values()[iSector] ) ) != LandType.None )
            {
              getGame().setLand( position.getNeighbour( Sector.values()[iSector] ), LandType.Plain );
            }
          }
        }
        else
        {
          for( int iSector = 0; iSector < Sector.values().length; iSector++ )
          {
            if( getGame().getLand( position.getNeighbour( Sector.values()[iSector] ) ) != LandType.None )
            {
              getGame().setLand( position.getNeighbour( Sector.values()[iSector] ), LandType.Plain );
            }
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
        for( int iSector = 0; iSector < Sector.values().length; iSector++ )
        {
          switch( getGame().getLand( position.getNeighbour( Sector.values()[iSector] ) ) )
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
        for( int iSector = 0; iSector < Sector.values().length; iSector++ )
        {
          switch( getGame().getLand( position.getNeighbour( Sector.values()[iSector] ) ) )
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
          for( int iSector = 0; iSector < Sector.values().length; iSector++ )
          {
            switch( getGame().getLand( position.getNeighbour( Sector.values()[iSector] ) ) )
            {
            case Sea:
            case Reef:
            case Marsh:
              nbSea++;
              switch( getGame()
                  .getLand( position.getNeighbour( Sector.values()[iSector].getNext() ) ) )
              {
              case Montain:
              case Plain:
              case None:
                nbPlain++;
              }
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
        for( int iSector = 0; iSector < Sector.values().length; iSector++ )
        {
          switch( getGame().getLand( position.getNeighbour( Sector.values()[iSector] ) ) )
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
          }
        }
        switch( getGame().getLand( position ) )
        {
        // case EnuLand.Marsh :
        case Plain:
          if( nbMontain > 0 )
          {
            for( int iSector = 0; iSector < Sector.values().length; iSector++ )
            {
              if( getGame().getLand( position.getNeighbour( Sector.values()[iSector] ) ) == LandType.Montain )
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
        for( int iSector = 0; iSector < Sector.values().length; iSector++ )
        {
          switch( getGame().getLand( position.getNeighbour( Sector.values()[iSector] ) ) )
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
        }
      }
    }
  }


  /**
   * @return the landPercent
   */
  public static int getLandPercent()
  {
    return s_landPercent;
  }

  /**
   * @param p_landPercent the landPercent to set
   */
  public static void setLandPercent(int p_landPercent)
  {
    s_landPercent = p_landPercent;
    s_seaPercent = 100 - s_landPercent;
  }

  /**
   * @return the isHexagonMap
   */
  public static boolean isHexagonMap()
  {
    return s_isHexagonMap;
  }

  /**
   * @param p_isHexagonMap the isHexagonMap to set
   */
  public static void setHexagonMap(boolean p_isHexagonMap)
  {
    s_isHexagonMap = p_isHexagonMap;
  }



}
