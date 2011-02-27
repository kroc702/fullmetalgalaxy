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
package com.fullmetalgalaxy.server.image;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.fullmetalgalaxy.model.LandType;
import com.fullmetalgalaxy.model.PlanetType;
import com.fullmetalgalaxy.model.constant.FmpConstant;
import com.fullmetalgalaxy.model.persist.EbGame;
import com.google.appengine.api.images.Composite;
import com.google.appengine.api.images.Composite.Anchor;
import com.google.appengine.api.images.Image;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;

/**
 * @author Vincent Legendre
 *
 */
public class MiniMapProducer implements ImageProducer
{
  // mime type of the returned image
  public static final String IMAGE_TYPE = "image/png";
  protected static final long MAX_COMPOSITION = ImagesService.MAX_COMPOSITES_PER_REQUEST;
  static private Map<PlanetType, Map<LandType, Image>> s_landImage = null;

  private EbGame m_game = null;


  /**
   * 
   */
  public MiniMapProducer(String p_servletBasePath, EbGame p_game)
  {
    synchronized( IMAGE_TYPE )
    {
      if( s_landImage == null )
      {
        s_landImage = new HashMap<PlanetType, Map<LandType, Image>>();
        for( PlanetType planet : PlanetType.values() )
        {
          loadFiles( p_servletBasePath, planet );
        }
      }
    }
    m_game = p_game;
  }

  private Image readImage(String p_file) throws IOException
  {
    FileInputStream fis = new FileInputStream( new File( p_file ) );
    byte[] buf = new byte[1000];
    int length = fis.read( buf );
    if( length < 0 || length >= buf.length )
    {
      throw new IOException( "unable to read file " + p_file );
    }
    Image image = ImagesServiceFactory.makeImage( buf );
    return image;
  }

  private void loadFiles(String p_servletBasePath, PlanetType p_planet)
  {
    String base = p_servletBasePath + "/images/board/" + p_planet.getFolderName();
    Map<LandType, Image> landImage = new HashMap<LandType, Image>();
    s_landImage.put( p_planet, landImage );
    try
    {
      landImage.put( LandType.Montain, readImage( base + "/minimap/montain.png" ) );
      landImage.put( LandType.Plain, readImage( base + "/minimap/plain.png" ) );
      landImage.put( LandType.Marsh, readImage( base + "/minimap/marsh.png" ) );
      landImage.put( LandType.Reef, readImage( base + "/minimap/reef.png" ) );
      landImage.put( LandType.Sea, readImage( base + "/minimap/sea.png" ) );
    } catch( IOException e )
    {
      e.printStackTrace();
    }
  }

  private Image getImage(PlanetType p_planet, LandType p_land)
  {
    Image image = s_landImage.get( p_planet ).get( p_land );
    if( image == null )
    {
      System.err.println( "error while loading image " + p_planet + " " + p_land );
    }
    return image;
  }

  /**
   * @return the game
   */
  private EbGame getGame()
  {
    return m_game;
  }

  public static String getImageType()
  {
    return IMAGE_TYPE;
  }

  public byte[] getImage()
  {
    if( getGame() == null )
    {
      return getImage( PlanetType.Desert, LandType.Sea ).getImageData();
    }


    // compute width x height to keep a good aspect ratio
    int width = getLandWidth();
    int height = FmpConstant.miniMapHeight * width / FmpConstant.miniMapWidth;
    if( height < getLandHeight() )
    {
      height = getLandHeight();
      width = FmpConstant.miniMapWidth * height / FmpConstant.miniMapHeight;
    }
    int tileWidth = getImage( m_game.getPlanetType(), LandType.Plain ).getWidth();
    int tileHeight = getImage( m_game.getPlanetType(), LandType.Plain ).getHeight();
    // compute offset to draw minimap in image center
    width *= tileWidth;
    height *= tileHeight;
    height += tileHeight / 2;
    int xOffset = (width - getLandWidth() * tileWidth) / 2;
    int yOffset = (height - getLandHeight() * tileHeight) / 2;

    Collection<Composite> composites = new ArrayList<Composite>();
    Image minimap = null;

    for( int ix = 0; ix < getLandWidth(); ix++ )
    {
      int offset = yOffset;
      if( ix % 2 != 0 )
      {
        offset += tileHeight / 2;
      }
      for( int iy = 0; iy < getLandHeight(); iy++ )
      {
        if( composites.size() == MAX_COMPOSITION )
        {
          minimap = ImagesServiceFactory.getImagesService()
              .composite( composites, width, height, 0 );
          composites.clear();
          composites.add( ImagesServiceFactory.makeComposite( minimap, 0, 0, 1, Anchor.TOP_LEFT ) );
        }
        if( getLand( ix, iy ) != LandType.None )
        {
          composites.add( ImagesServiceFactory.makeComposite( getImage( m_game.getPlanetType(),
              getLand( ix, iy ) ), ix * tileWidth + xOffset, iy * tileHeight + offset, 1,
              Anchor.TOP_LEFT ) );
        }
      }
    }

    minimap = ImagesServiceFactory.getImagesService().composite( composites, width, height, 0 );
    return minimap.getImageData();
  }

  /* (non-Javadoc)
   * @see nc.kroc.fmp.server.image.ImageProducer#createImage(java.io.OutputStream)
   */
  @Override
  public String createImage(OutputStream p_stream) throws IOException
  {
    p_stream.write( getImage() );
    return getImageType();
  }

  /**
   * @param p_x
   * @param p_y
   * @return
   * @deprecated
   * @see nc.kroc.fmp.rpc.persist.AnGame#getLand(int, int)
   */
  public LandType getLand(int p_x, int p_y)
  {
    return m_game.getLand( p_x, p_y );
  }

  /**
   * @return
   * @see nc.kroc.fmp.rpc.persist.AnGame#getLandHeight()
   */
  public int getLandHeight()
  {
    return m_game.getLandHeight();
  }

  /**
   * @return
   * @see nc.kroc.fmp.rpc.persist.AnGame#getLandWidth()
   */
  public int getLandWidth()
  {
    return m_game.getLandWidth();
  }



}
