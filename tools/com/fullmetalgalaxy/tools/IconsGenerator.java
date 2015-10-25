package com.fullmetalgalaxy.tools;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

/**
 * goal of this tool is to generate all users icon 
 * 
 * @author Vincent
 *
 */
public class IconsGenerator
{
  // mime type of the returned image
  public static final String IMAGE_TYPE = "image/png";
  public static final int IMAGE_WIDTH = 70;
  public static final int IMAGE_HEIGHT = 20;

  private static void printUsage(PrintStream p_out)
  {
    p_out.println( "Usage: FMGIcons <icons directory>" );
  }

  private static Map<String, BufferedImage> s_vipIcons = new HashMap<String, BufferedImage>();
  private static Map<String, BufferedImage> s_levelIcons = new HashMap<String, BufferedImage>();
  private static Map<String, BufferedImage> s_styleIcons = new HashMap<String, BufferedImage>();
  
  /**
   * @param args
   */
  public static void main(String[] args)
  {
    if( args.length < 1 )
    {
      printUsage( System.out );
      return;
    }
    String directory = "";
    for(String arg : args)
    {
      directory += arg +" ";
    }
    directory = directory.substring( 0, directory.length()-1 );

    // load all files
    loadFiles( directory );

    directory += "/user";

    // create all icons
    for( Entry<String, BufferedImage> entryVip : s_vipIcons.entrySet() )
    {
      for( Entry<String, BufferedImage> entryLevel : s_levelIcons.entrySet() )
      {
        for( Entry<String, BufferedImage> entryStyle : s_styleIcons.entrySet() )
        {
          createIcon( entryVip.getKey(), entryLevel.getKey(), entryStyle.getKey(), directory );
        }
      }
    }


  }


  private static void createIcon(String p_keyVip, String p_keyLevel, String p_keyStyle,
      String p_directory)
  {
    BufferedImage bi = new BufferedImage( IMAGE_WIDTH, IMAGE_HEIGHT, BufferedImage.TYPE_4BYTE_ABGR );
    Graphics2D graphics = bi.createGraphics();
    BufferedImage biVip = s_vipIcons.get( p_keyVip );
    BufferedImage biLevel = s_levelIcons.get( p_keyLevel );
    BufferedImage biStyle = s_styleIcons.get( p_keyStyle );
    int spaceW = (int)Math.ceil( (IMAGE_WIDTH - biVip.getWidth() - biLevel.getWidth() - biStyle
        .getWidth()) / 3 );
    int position = 0;
    graphics.drawImage( biVip, null, position, 0 );
    position += biVip.getWidth() + spaceW;
    graphics.drawImage( biLevel, null, position, 0 );
    position += biLevel.getWidth() + spaceW;
    graphics.drawImage( biStyle, null, position, 0 );


    ImageWriter writer = ImageIO.getImageWritersByMIMEType( IMAGE_TYPE ).next();
    File file = new File( p_directory + "/" + p_keyVip + p_keyLevel + p_keyStyle + ".png" );
    try
    {
      FileOutputStream fos = new FileOutputStream( file );
      ImageOutputStream ios;
      ios = ImageIO.createImageOutputStream( fos );
      writer.setOutput( ios );
      writer.write( bi );
      ios.close();
      fos.close();
    } catch( IOException e )
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }

  private static void loadFiles(String p_direcory)
  {
    System.out.println("load files from "+p_direcory);

    loadFile( s_vipIcons, "b", p_direcory + "/ban.png" );
    loadFile( s_vipIcons, "", p_direcory + "/empty.png" );
    loadFile( s_vipIcons, "v", p_direcory + "/vip.png" );

    loadFile( s_levelIcons, "0", p_direcory + "/level0.png" );
    loadFile( s_levelIcons, "1", p_direcory + "/level1.png" );
    loadFile( s_levelIcons, "2", p_direcory + "/level2.png" );
    loadFile( s_levelIcons, "3", p_direcory + "/level3.png" );
    loadFile( s_levelIcons, "4", p_direcory + "/level4.png" );
    loadFile( s_levelIcons, "5", p_direcory + "/level5.png" );
    loadFile( s_levelIcons, "6", p_direcory + "/level6.png" );
    loadFile( s_levelIcons, "7", p_direcory + "/level7.png" );
    loadFile( s_levelIcons, "8", p_direcory + "/level8.png" );
    loadFile( s_levelIcons, "9", p_direcory + "/level9.png" );

    loadFile( s_styleIcons, "s", p_direcory + "/sheep.png" );
    loadFile( s_styleIcons, "p", p_direcory + "/pacific.png" );
    loadFile( s_styleIcons, "b", p_direcory + "/balanced.png" );
    loadFile( s_styleIcons, "a", p_direcory + "/aggressive.png" );

  }

  private static void loadFile(Map<String, BufferedImage> p_store, String p_key, String p_file)
  {
    try
    {
      File f = new File( p_file );
      BufferedImage bi = ImageIO.read( f );
      p_store.put( p_key, bi );

    } catch( IOException e )
    {
      e.printStackTrace();
      System.err.println("for file "+p_file);
    }
  }

}
