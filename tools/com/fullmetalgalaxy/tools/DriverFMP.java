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
package com.fullmetalgalaxy.tools;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.fullmetalgalaxy.model.LandType;
import com.fullmetalgalaxy.model.ModelFmpInit;
import com.fullmetalgalaxy.model.PlanetType;
import com.fullmetalgalaxy.model.TokenType;
import com.fullmetalgalaxy.model.constant.ConfigGameTime;
import com.fullmetalgalaxy.model.persist.AnBoardPosition;
import com.fullmetalgalaxy.model.persist.EbToken;
import com.fullmetalgalaxy.model.persist.Game;
import com.fullmetalgalaxy.server.FmpLogger;

/**
 * A set of tools to manage Full Metal Program files.
 * 
 * This class is at very early stage and can't manage all data file.
 * 
 * @author vlegendr
 *
 */
public class DriverFMP extends DriverFileFormat
{
  /**
   * The log channel
   */
  private final static FmpLogger LOG = FmpLogger.getLogger( DriverFMP.class.getName() );

  
  private static String readln(InputStream p_input)
  {
    int ch = 0;
    StringBuffer output = new StringBuffer();
    try
    {
      ch = p_input.read();
      while( ch >= 0 && ch != '\n' )
      {
        output.append( (char)ch );
        ch = p_input.read();
      }
    } catch( IOException e )
    {
      LOG.error( e );
    }
    return output.toString();
  }

  
  @Override
  public ModelFmpInit loadGame(InputStream p_input)
  {
    Game game = new Game();
    game.setName( "upload" );
    game.setPlanetType( PlanetType.Desert );
    game.setConfigGameTime( ConfigGameTime.Standard );
    game.setMaxNumberOfPlayer( 4 );

    String str = readln( p_input );
    if( str == null || !str.startsWith( "<FMP Format=4 Version=\"2.3.7" ) )
    {
      LOG.error( "Bad format" );
      return null;
    }
    // lecture de la carte
    str = readln( p_input );
    if( str == null || !str.startsWith( "<CARTE Lignes=" ) )
    {
      LOG.error( "<CARTE Lignes= not found" );
      return null;
    }
    int height = Integer.parseInt( str.substring( 14, 16 ) );
    int width = Integer.parseInt( str.substring( 26, 28 ) );
    game.setLandSize( width, height );
    for( int iy = 0; iy < height; iy++ )
    {
      str = readln( p_input );
      for( int ix = 0; ix < width; ix++ )
      {
        switch( str.charAt( ix ) )
        {
        case '.':
          game.setLand( ix, iy, LandType.Sea );
          break;
        case '*':
          game.setLand( ix, iy, LandType.Reef );
          break;
        case '%':
          game.setLand( ix, iy, LandType.Marsh );
          break;
        case '$':
          game.setLand( ix, iy, LandType.Plain );
          break;
        case '#':
          game.setLand( ix, iy, LandType.Montain );
          break;
        case '?':
        default:
          game.setLand( ix, iy, LandType.None );
          break;
        }
      }
    }
    str = readln( p_input );
    if( str == null || !str.startsWith( "</CARTE>" ) )
    {
      LOG.error( "</CARTE> not found" );
    }
    // lecture des minerais
    str = readln( p_input );
    if( str != null && str.startsWith( "<REPARTITION Lignes=" ) )
    {
      height = Integer.parseInt( str.substring( 20, 22 ) );
      width = Integer.parseInt( str.substring( 32, 34 ) );
      for( int iy = 0; iy < height; iy++ )
      {
        str = readln( p_input );
        for( int ix = 0; ix < width; ix++ )
        {
          if(str.charAt( ix ) != '.')
          {
            EbToken token = new EbToken( TokenType.Ore );
            game.addToken( token );
            game.moveToken( token, new AnBoardPosition( ix, iy ) );
          }
        }
      }
      str = readln( p_input );
      if( str == null || !str.startsWith( "</REPARTITION>" ) )
      {
        LOG.error( "</REPARTITION> not found" );
      }
    }
    
    try
    {
      p_input.close();
    } catch( IOException e )
    {
      LOG.error( e );
    }

    return game2Model( game );
  }


  @Override
  public void saveGame(ModelFmpInit p_game, OutputStream p_output)
  {
    LOG.error( "unimplemented" );
  }

  /*
  private EbGame createGame(InputStream p_input)
  {
    EbGame game = new EbGame();

    DocumentBuilderFactory factory = null;
    Document document = null;
    try
    {
      factory = DocumentBuilderFactory.newInstance();
      factory.setValidating( false );
      DocumentBuilder builder = factory.newDocumentBuilder();
      // document = builder.parse( correctFmpFile( item.openStream() ) );
      document = builder.parse( p_input );
    } catch( Exception e )
    {
      log.error( e );
    }
    System.out.print( document.getDocumentElement().getFirstChild().getAttributes() );

    System.out.print( document.getChildNodes() );
    Element elemFmp = document.getElementById( "FMP" );
    System.out.print( elemFmp.getAttribute( "Format" ) );
    Element elemCarte = document.getElementById( "CARTE" );
    System.out.print( elemCarte.getAttribute( "Lignes" ) );
    System.out.print( elemCarte.getAttribute( "Colonnes" ) );

    return game;
  }

  private InputStream correctFmpFile(InputStream p_input)
  {
    int ch = 0;
    boolean isCreatingString = false;
    char lastChar = 0;
    StringBuffer output = new StringBuffer();
    try
    {
      ch = p_input.read();
      while( ch >= 0 )
      {
        if( lastChar == '=' && ch != '"' )
        {
          isCreatingString = true;
          output.append( '"' );
        }
        if( (isCreatingString == true) && (ch == ' ' || ch == '>') )
        {
          isCreatingString = false;
          output.append( '"' );
        }
        if( ch == '=' )
        {
          lastChar = '=';
        }
        else
        {
          lastChar = 0;
        }
        output.append( (char)ch );
        ch = p_input.read();
      }
    } catch( IOException e )
    {
      log.error( e );
    }
    System.out.print( output );
    return new ByteArrayInputStream( new String( output ).getBytes() );
  }
  */


}
