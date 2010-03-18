package com.fullmetalgalaxy.server;

import java.util.Date;
import java.util.Map;

import com.fullmetalgalaxy.model.ModelFmpUpdate;
import com.fullmetalgalaxy.model.persist.EbAccount;
import com.fullmetalgalaxy.model.persist.EbGame;
import com.fullmetalgalaxy.model.persist.EbRegistration;
import com.fullmetalgalaxy.server.CacheKey.CacheKeyType;
import com.fullmetalgalaxy.server.datastore.FmgDataStore;
import com.google.appengine.api.memcache.Expiration;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

public class FmpUpdateStatus
{
  private final static long SLEEP_LOOP_MS = 1000;
  private final static long LOOP_TIMEOUT_MS = 26000;
  private final static int CACHE_OBJECT_TTL_SEC = 300; // 5 min
  private final static long SEARCH_OLDER_OBJECT_MS = 60000;
  /**
   * The log channel
   */
  private final static FmpLogger log = FmpLogger.getLogger( FmpUpdateStatus.class.getName() );

  private static MemcacheService s_cache = null;


  public static ModelFmpUpdate waitForModelUpdate(String p_login, long p_gameId, Date p_lastUpdate)
  {
    long startTime = System.currentTimeMillis();
    long timeoutTime = startTime + LOOP_TIMEOUT_MS;
    long clientUpdate = p_lastUpdate.getTime();
    ModelFmpUpdate modelUpdate = getCachedModelUpdate( p_login, p_gameId, p_lastUpdate );
    Long gameUpdate = 0L;
    if( modelUpdate.getLastUpdate().after( p_lastUpdate ) )
    {
      if( p_lastUpdate != null )
      {
        modelUpdate = modelUpdate.getNewModelUpdate( p_lastUpdate );
      }
      gameUpdate = modelUpdate.getLastUpdate().getTime();
    }
    else
    {
      // no model update available...
      gameUpdate = (Long)getCache().get( p_gameId );
      if( gameUpdate == null )
      {
        gameUpdate = 0L;
      }
      if( modelUpdate.getConnectedUsers().size() <= 1 )
      {
        // but nobody is connected: don't wait !
        return null;
      }
      modelUpdate = null;
    }
    try
    {
      while( (System.currentTimeMillis() < timeoutTime) && (gameUpdate <= clientUpdate) )
      {
        Thread.sleep( SLEEP_LOOP_MS );
        gameUpdate = (Long)getCache().get( p_gameId );
        if( gameUpdate == null )
        {
          gameUpdate = 0L;
        }
      }
    } catch( Exception e )
    {
      log.warning( e.getMessage() );
      // google probably find this query too long...
    }

    log.fine( "waitForModelUpdate(" + p_login + ") date=" + new Date( System.currentTimeMillis() )
        + " timeout="
        + new Date( timeoutTime ) + "  durration="
        + ((System.currentTimeMillis() - startTime) / 1000) + "sec" );

    if( gameUpdate <= clientUpdate )
    {
      return null;
    }
    if( modelUpdate == null )
    {
      modelUpdate = getModelUpdate( p_login, p_gameId, p_lastUpdate );
    }
    log.finer( "client update=" + p_lastUpdate + "  game update=" + modelUpdate.getLastUpdate() );
    return modelUpdate;
  }


  /**
   * load account 'p_id' into 'p_account'
   * @param p_accounts
   * @param p_id
   * @return
   */
  private static boolean loadAccount(Map<Long, EbAccount> p_accounts, long p_id)
  {
    if( p_id <= 0 )
    {
      return false;
    }
    if( p_accounts.containsKey( p_id ) )
    {
      return false;
    }
    FmgDataStore dataStore = new FmgDataStore();
    EbAccount account = dataStore.getAccount( p_id );
    p_accounts.put( p_id, account );
    dataStore.close();
    return true;
  }

  public static boolean loadAllAccounts(Map<Long, EbAccount> p_accounts, EbGame p_game)
  {
    boolean isLoaded = false;
    if( p_game != null )
    {
      isLoaded = loadAccount( p_accounts, p_game.getAccountCreatorId() );
      for( EbRegistration registration : p_game.getSetRegistration() )
      {
        isLoaded |= loadAccount( p_accounts, registration.getAccountId() );
      }
    }
    return isLoaded;
  }

  /**
   * all events date are after 'p_fromUpdate'
   * @param p_login
   * @param p_gameId
   * @param p_fromUpdate
   * @return
   */
  public static ModelFmpUpdate getModelUpdate(String p_login, long p_gameId, Date p_fromUpdate)
  {
    if( p_fromUpdate != null )
    {
      return getCachedModelUpdate( p_login, p_gameId, p_fromUpdate ).getNewModelUpdate(
          p_fromUpdate );
    }
    else
    {
      return getCachedModelUpdate( p_login, p_gameId, p_fromUpdate );
    }
  }

  /**
   * WARNING the returned model 'from update' (ie events date) could be older than p_fromUpdate
   * @param p_login
   * @param p_gameId
   * @param p_fromUpdate
   * @return
   */
  private static ModelFmpUpdate getCachedModelUpdate(String p_login, long p_gameId,
      Date p_fromUpdate)
  {
    boolean toSave = false;
    boolean toBroadcast = false;
    CacheKey key = new CacheKey( CacheKeyType.ModelUpdate, p_gameId );
    ModelFmpUpdate updates = (ModelFmpUpdate)getCache().get( key );
    if( updates == null )
    {
      // load it from datastore
      Date oldest = null;
      if( p_fromUpdate != null )
      {
        oldest = new Date( p_fromUpdate.getTime() - SEARCH_OLDER_OBJECT_MS );
      }
      else
      {
        oldest = new Date( System.currentTimeMillis() - SEARCH_OLDER_OBJECT_MS );
      }
      FmgDataStore dataStore = new FmgDataStore();
      EbGame game = dataStore.getGame( p_gameId );
      dataStore.close();
      updates = new ModelFmpUpdate( game, oldest );
      loadAllAccounts( updates.getMapAccounts(), game );
      updates.connectUser( p_login );
      toSave = true;
    }
    int oldConnectedCount = updates.getConnectedUsers().size();
    toSave |= updates.connectUser( p_login );
    if( oldConnectedCount != updates.getConnectedUsers().size() )
    {
      toBroadcast = true;
    }
    toBroadcast |= updates.disconnectTooOldUser();
    toSave |= updates.deleteTooOldChatMessages();
    if( toBroadcast )
    {
      // a broadcast also save data
      broadCastGameUpdate( updates );
    }
    else if( toSave )
    {
      // updates.getLastUpdate().setTime( System.currentTimeMillis() );
      getCache().put( key, updates, Expiration.byDeltaSeconds( CACHE_OBJECT_TTL_SEC ) );
    }
    return updates;
  }

  public static void broadCastGameUpdate(ModelFmpUpdate p_updates)
  {
    assert p_updates != null;
    assert p_updates.getGameId() != 0;
    p_updates.getLastUpdate().setTime( System.currentTimeMillis() );
    getCache().put( new CacheKey( CacheKeyType.ModelUpdate, p_updates.getGameId() ), p_updates,
        Expiration.byDeltaSeconds( CACHE_OBJECT_TTL_SEC ) );
    getCache().put( p_updates.getGameId(), p_updates.getLastUpdate().getTime(),
        Expiration.byDeltaSeconds( CACHE_OBJECT_TTL_SEC ) );
  }

  public static void broadCastGameUpdate(EbGame p_game)
  {
    assert p_game != null;
    ModelFmpUpdate updates = getCachedModelUpdate( null, p_game.getId(), new Date( System
        .currentTimeMillis() ) );
    updates.setGameEvents( p_game.getLogs(), new Date( System.currentTimeMillis()
        - SEARCH_OLDER_OBJECT_MS ) );
    broadCastGameUpdate( updates );
  }


  /**
   * @return the s_cache
   */
  private static MemcacheService getCache()
  {
    if( s_cache == null )
    {
      s_cache = MemcacheServiceFactory.getMemcacheService();
    }
    return s_cache;
  }



}
