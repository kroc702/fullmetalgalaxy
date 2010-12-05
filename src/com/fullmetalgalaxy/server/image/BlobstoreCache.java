package com.fullmetalgalaxy.server.image;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.util.Streams;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.myjavatools.web.ClientHttpRequest;

/**
 * <p>Main purpose of this class is to create a BlobKey from any data.</p>
 * <p>To do that, data is uploaded to blobstore service via a HTTP/POST request.</p>
 * <p>BlobstoreCache is also a servlet which can be used directly to POST and GET data</p>
 * 
 * @author Vincent
 *
 */
@SuppressWarnings("serial")
public class BlobstoreCache extends HttpServlet
{
  private static final String BLOBSTORE_CACHE_URL = "/BlobstoreCache";
  private static final String DATA_FIELD = "myData";
  private static final String KEY_PARAM = "key";
  private static final String PRINT_PARAM = "print";
  
  public static BlobKey store(InputStream p_data)
  {
    return store( DATA_FIELD, p_data );
  }

  /**
   * 
   * @param p_data data to be cached
   * @return null if failed.
   */
  public static BlobKey store(String p_fileName, InputStream p_data)
  {
    BlobKey blobKey = null;
    ClientHttpRequest request = null;
    BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
    try
    {
      request = new ClientHttpRequest( "http://localhost:8888"
          + blobstoreService.createUploadUrl( BLOBSTORE_CACHE_URL ) );
      request.setParameter( DATA_FIELD, DATA_FIELD, p_data );
      String responseStr = Streams.asString( request.post() );
      blobKey = new BlobKey( responseStr );
    } catch( IOException e )
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return blobKey;
  }


  private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();


  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException
  {
    try
    {
      doPost( req, res );
    } catch( ServletException e )
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException,
      IOException
  {
    String keyParam = req.getParameter( KEY_PARAM );
    String printParam = req.getParameter( PRINT_PARAM );
    if( keyParam != null )
    {
      BlobKey blobKey = new BlobKey( keyParam );
      blobstoreService.serve(blobKey, res);
    }
    else if( printParam != null )
    {
      res.getOutputStream().print( req.getParameter( "print" ) );
    }
    else
    {
      Map<String, BlobKey> blobs = blobstoreService.getUploadedBlobs( req );
      Entry<String, BlobKey> firstEntry = blobs.entrySet().iterator().next();
      if( firstEntry != null )
      {
        BlobKey blobKey = firstEntry.getValue();
        res.sendRedirect( BLOBSTORE_CACHE_URL + "?print=" + blobKey.getKeyString() );
      }
      else
      {
        res.sendRedirect( BLOBSTORE_CACHE_URL );
      }
    }
  }




}
