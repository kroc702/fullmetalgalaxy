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
package com.fullmetalgalaxy.server;

import java.nio.ByteBuffer;

import com.fullmetalgalaxy.model.persist.EbGameData;
import com.fullmetalgalaxy.model.persist.EbGamePreview;
import com.fullmetalgalaxy.model.persist.Game;
import com.fullmetalgalaxy.server.image.MiniMapProducer;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.google.appengine.api.files.AppEngineFile;
import com.google.appengine.api.files.FileService;
import com.google.appengine.api.files.FileServiceFactory;
import com.google.appengine.api.files.FileWriteChannel;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.NotFoundException;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.Query;

/**
 * Specific DAO for FMG
 * 
 * @author vlegendr
 *
 */
public class FmgDataStore extends DataStore
{
  static
  {
    ObjectifyService.register( EbAccount.class );
    ObjectifyService.register( EbGameData.class );
    ObjectifyService.register( EbGamePreview.class );
  }

  /**
   * a static read only DAO
   */
  private final static FmgDataStore s_dao = new FmgDataStore( true );

  /**
   * This method return a static instance of a read only data store.
   * You can't use it to put and commit data.
   * @return
   */
  public static FmgDataStore dao()
  {
    return s_dao;
  }
  
  public static String getPseudoFromJid(String p_jid)
  {
    if( p_jid == null )
    {
      return "???";
    }
    String jid = p_jid.split("/")[0];
    Query<EbAccount> query = dao().query( EbAccount.class ).filter( "m_jabberId", jid );
    QueryResultIterator<EbAccount> it = query.iterator();
    if( !it.hasNext() )
    {
      // we could remove the end of jid @...
      // but in this case someone can use a jid in the form of <existingPseudo>@anydomain
      // to fool players !
      return jid;
    }
    return it.next().getPseudo();
  }
  
  /**
   * 
   * @param p_pseudo
   * @return true if p_pseudo exist as a login or pseudo in data base.
   */
  public static boolean isPseudoExist( String p_pseudo )
  {
    String pseudo = EbAccount.compactPseudo( p_pseudo );
    Query<EbAccount> query = dao().query( EbAccount.class ).filter( "m_compactPseudo", pseudo );
    if( !query.fetchKeys().iterator().hasNext() )
    {
      query = dao().query( EbAccount.class ).filter( "m_login", p_pseudo );
      if( !query.fetchKeys().iterator().hasNext() )
      {
        return false;
      }
    }
    return true;
  }
  
  
  public static boolean updatePseudo(long p_accountId, String p_newPseudo)
  {
    if( !EbAccount.isValidPseudo( p_newPseudo ) )
    {
      return false;
    }
    FmgDataStore ds = new FmgDataStore( false );
    EbAccount account = ds.find( EbAccount.class, p_accountId );
    String oldPseudo = account.getPseudo();
    if( !EbAccount.compactPseudo( oldPseudo ).equals( EbAccount.compactPseudo( p_newPseudo ) )
        && isPseudoExist( p_newPseudo ) )
    {
      ds.rollback();
      return false;
    }
    account.setPseudo( p_newPseudo );
    ds.put( account );
    ds.commit();

    // TODO change all pseudo in all games...

    return true;
  }


  public static boolean storeMinimap(Game p_game)
  {
    if( p_game.getMinimapBlobKey() != null )
    {
      deleteMinimap( p_game.getMinimapBlobKey() );
      p_game.setMinimapUri( null );
      p_game.setMinimapBlobKey( null );
    }

    MiniMapProducer miniMapProducer = new MiniMapProducer( ServerUtil.getBasePath(), p_game );
    byte[] data = miniMapProducer.getImage();

    // Get a file service
    FileService fileService = FileServiceFactory.getFileService();

    try
    {
      // Create a new Blob file with mime-type "text/plain"
      AppEngineFile file = fileService.createNewBlobFile( "image/png" );

      // Open a channel to write to it
      FileWriteChannel writeChannel = fileService.openWriteChannel( file, true );
      writeChannel.write( ByteBuffer.wrap( data ) );
      // Now finalize
      writeChannel.closeFinally();

      // Now read from the file using the Blobstore API
      BlobKey blobKey = fileService.getBlobKey( file );

      // update game
      p_game.setMinimapUri( ImagesServiceFactory.getImagesService().getServingUrl( blobKey ) );
      p_game.setMinimapBlobKey( blobKey.getKeyString() );

    } catch( Exception e )
    {
      ServerUtil.logger.severe( e.getMessage() );
      return false;
    }
    return true;
  }



  /**
   * @param p_isReadOnly
   */
  public FmgDataStore(boolean p_isReadOnly)
  {
    super( p_isReadOnly );
  }


  /**
   * 
   * @param p_id
   * @return null if game not found
   */
  public Game getGame(Long p_id)
  {
    EbGamePreview preview = find( EbGamePreview.class, p_id );
    return getGame( preview );
  }

  public Game getGame( EbGamePreview p_preview )
  {
    if( p_preview == null )
    {
      return null;
    }
    Key<EbGamePreview> keyPreview = new Key<EbGamePreview>(EbGamePreview.class, p_preview.getId());
    Key<EbGameData> keyData = new Key<EbGameData>(keyPreview, EbGameData.class, p_preview.getId());
    EbGameData data = find( keyData );
    if( data == null )
    {
      return null;
    }
    return new Game( p_preview, data );
  }
  
  public Game getGame( Key<EbGamePreview> p_keyPreview )
  {
    if( p_keyPreview == null )
    {
      return null;
    }
    EbGamePreview preview = find(p_keyPreview); 
    if( preview == null )
    {
      return null;
    }
    Key<EbGameData> keyData = new Key<EbGameData>(p_keyPreview, EbGameData.class, preview.getId());
    EbGameData data = find( keyData );
    if( data == null )
    {
      return null;
    }
    return new Game( preview, data );
  }
  


  /**
   * put a game and his associated game preview
   * @param p_game
   * @return null if fail
   */
  public Long put(Game p_game)
  {
    if( isReadOnly() )
    {
      return null;
    }
    // to update is open flag
    p_game.isOpen();

    Key<EbGamePreview> keyPreview = put( p_game.getPreview() );
    p_game.getData().setId( keyPreview.getId() );
    p_game.getData().setKeyPreview( keyPreview );
    put( p_game.getData() );
    return keyPreview.getId();
  }
  

  private static void deleteMinimap(String p_minimapBlobKey)
  {
    BlobKey blobKey = new BlobKey( p_minimapBlobKey );
    try
    {
      BlobstoreServiceFactory.getBlobstoreService().delete( blobKey );
    } catch( Exception e )
    {
      // This try/catch section is because of some blobstore internal error !
      e.printStackTrace();
    }
  }

  /**
   * delete the minimap blob and both preview and data entity
   * @param p_id
   */
  protected void deleteGame(EbGamePreview p_gamePreview)
  {
    if( p_gamePreview != null )
    {
      Long id = p_gamePreview.getId();
      if( p_gamePreview.getMinimapBlobKey() != null )
      {
        deleteMinimap( p_gamePreview.getMinimapBlobKey() );
      }
      Key<EbGamePreview> keyPreview = new Key<EbGamePreview>(EbGamePreview.class, id );
      Key<EbGameData> keyData = new Key<EbGameData>(keyPreview, EbGameData.class, id );
      super.delete( keyPreview, keyData );
    }
  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.server.DataStore#delete(java.lang.Object[])
   */
  @Override
  public void delete(Object p_object)
  {
    if( p_object instanceof EbGamePreview )
    {
      deleteGame( (EbGamePreview)p_object );
    }
    else if( p_object instanceof Game )
    {
      deleteGame( ((Game)p_object).getPreview() );
    }
    else
    {
      super.delete( p_object );
    }
  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.server.DataStore#delete(java.lang.Class, long)
   */
  @Override
  public <T> void delete(Class<T> p_arg0, long p_id)
  {
    if( p_arg0 == EbGamePreview.class || p_arg0 == Game.class )
    {
      EbGamePreview gamePreview = get( EbGamePreview.class, p_id );
      deleteGame( gamePreview );
    }
    else
    {
      super.delete( p_arg0, p_id );
    }
  }


  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.server.DataStore#get(java.lang.Class, long)
   */
  @Override
  public <T> T get(Class<? extends T> p_arg0, long p_arg1) throws NotFoundException
  {
    if( p_arg0 == Game.class )
    {
      return p_arg0.cast( getGame( p_arg1 ) );
    }
    else
    {
      return super.get( p_arg0, p_arg1 );
    }
  }



}
