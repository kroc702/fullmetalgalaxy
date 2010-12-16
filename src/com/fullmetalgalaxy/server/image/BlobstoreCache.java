package com.fullmetalgalaxy.server.image;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fullmetalgalaxy.model.persist.EbGame;
import com.fullmetalgalaxy.server.datastore.FmgDataStore;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.utils.SystemProperty;
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
  
  private static final boolean PRODUCTION_MODE = SystemProperty.environment.value() == SystemProperty.Environment.Value.Production;
  private static final String URL_PREFIX = PRODUCTION_MODE ? "" : "http://localhost:8888";



  /**
   * 
   * @param p_data data to be cached
   * @return null if failed.
   */
  public static void storeMinimap(long p_gameId, InputStream p_data)
  {
    ClientHttpRequest request = null;
    BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
    try
    {
      request = new ClientHttpRequest( URL_PREFIX
          + blobstoreService.createUploadUrl( BLOBSTORE_CACHE_URL ) );
      request.setParameter( "id", p_gameId );
      request.setParameter( DATA_FIELD, "minimap.png", p_data );
      request.post();
    } catch( IOException e )
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
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
    String idParam = req.getParameter( "id" );
    long gameId = 0;

    if( keyParam != null )
    {
      BlobKey blobKey = new BlobKey( keyParam );
      blobstoreService.serve(blobKey, res);
    }
    else if( idParam != null )
    {
      Map<String, BlobKey> blobs = blobstoreService.getUploadedBlobs( req );
      Entry<String, BlobKey> firstEntry = blobs.entrySet().iterator().next();
      if( firstEntry != null )
      {
        BlobKey blobKey = firstEntry.getValue();
        // load corresponding game
        gameId = Long.parseLong( idParam );
        FmgDataStore dataStore = new FmgDataStore();
        EbGame game = dataStore.getGame( gameId );
        if( game == null )
        {
          // erase minimap...
          BlobstoreServiceFactory.getBlobstoreService().delete( blobKey );
        }
        else
        {
          // update game
          game.setMinimapUri( ImagesServiceFactory.getImagesService().getServingUrl( blobKey ) );
          game.setMinimapBlobKey( blobKey.getKeyString() );
          dataStore.save( game );
          dataStore.close();
        }
      }
      res.sendRedirect( BLOBSTORE_CACHE_URL );
    }
    else
    {
      res.getOutputStream().print( "dummy" );
    }
  }




}
