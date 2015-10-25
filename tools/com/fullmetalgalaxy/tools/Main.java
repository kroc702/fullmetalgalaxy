package com.fullmetalgalaxy.tools;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintStream;

import com.fullmetalgalaxy.model.ModelFmpInit;


/**
 * Goal of this tool, is to convert puzzles game between xml and binary format.
 * XML is used for human read and binary is used for FMG server.
 * As XML format is using xstream and GAE can't use it, I create this little workaround.
 * 
 * @author Vincent
 *
 */
public class Main
{
  
  private static void printUsage(PrintStream p_out)
  {
    p_out.println("Usage: FMGTools <format> [InputFile] <format> [OutputFile]");
    p_out.println("Availble format: -bin -xml -fmp");
  }

  private static DriverFileFormat parseFormat(String p_format)
  {
    assert p_format != null;
    DriverFileFormat driver = null;
    if(p_format.endsWith( "fmp" ))
    {
      driver = new DriverFMP();
    }
    if(p_format.endsWith( "bin" ))
    {
      driver = new DriverBin();
    }
    if(p_format.endsWith( "xml" ))
    {
      driver = new DriverXML();
    }
    if(driver == null)
    {
      driver = new DriverEmpty();
    }
    return driver;
  }
  
  /**
   * @param args
   */
  public static void main(String[] args)
  {
    
    if(args.length != 4 && args.length != 2)
    {
      printUsage(System.out);
      return;
    }
    
    // parse arguments
    String inputFormat = null;
    String outputFormat = null;
    String inputFile = null;
    String outputFile = null;
    if(args.length == 2)
    {
      inputFormat = args[0];
      inputFile = args[0];
      outputFormat = args[1];
      outputFile = args[1];
    } else {
      inputFormat = args[0];
      inputFile = args[1];
      outputFormat = args[2];
      outputFile = args[3];
    }
    
    // load file drivers
    DriverFileFormat inputDriver = parseFormat( inputFormat );
    DriverFileFormat outputDriver = parseFormat( outputFormat );

    // do the job !
    FileInputStream fis = null;
    FileOutputStream fos = null;
    try
    {
      fis = new FileInputStream( new File( inputFile ) );
      
      ModelFmpInit model = inputDriver.loadGame( fis );
      if(model != null)
      {
        model.getGame().getPreview().onLoad();
        model.getGame().onLoad();
        
        fos = new FileOutputStream( new File( outputFile ) );
        outputDriver.saveGame( model, fos ); 
      } else {
        System.err.println("Error while loading model");
      }
      
      fis.close();
      fos.close();
      
    } catch( Exception ex )
    {
      ex.printStackTrace();
    }

  }


}
