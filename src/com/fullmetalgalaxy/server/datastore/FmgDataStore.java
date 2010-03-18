/**
 * 
 */
package com.fullmetalgalaxy.server.datastore;

import java.util.ArrayList;
import java.util.List;

import com.fullmetalgalaxy.model.persist.EbAccount;
import com.fullmetalgalaxy.model.persist.EbGame;
import com.fullmetalgalaxy.model.persist.EbGamePreview;


/**
 * @author vincent
 *
 */
public class FmgDataStore extends DataStore
{
  static public EbAccount sgetAccount(long p_id)
  {
    PersistAccount entity = sgetPersistAccount( p_id );
    if( entity == null )
    {
      return null;
    }
    return entity.getAccount();
  }

  static public PersistAccount sgetPersistAccount(long p_id)
  {
    PersistEntity entity = DataStore.getEntity( PersistAccount.class, p_id );
    if( entity == null )
    {
      return null;
    }
    return PersistAccount.class.cast( entity );
  }

  @SuppressWarnings("unchecked")
  static public PersistAccount getPersistAccount(String p_login)
  {
    List<PersistAccount> resultList = (List<PersistAccount>)getList( PersistAccount.class,
        "m_login = '" + p_login + "'" );
    assert resultList.size() <= 1;
    if( resultList.isEmpty() )
    {
      return null;
    }
    return resultList.get( 0 );
  }

  static public EbAccount getAccount(String p_login)
  {
    PersistAccount pAccount = getPersistAccount( p_login );
    if( pAccount == null )
    {
      return null;
    }
    return pAccount.getAccount();
  }

  static public boolean isLoginExist(String p_login)
  {
    List<?> resultList = getList( PersistAccount.class, "m_login = '" + p_login + "'" );
    return resultList.size() >= 1;
  }

  static public boolean isPseudoExist(String p_pseudo)
  {
    List<?> resultList = getList( PersistAccount.class, "m_pseudo = '" + p_pseudo + "'" );
    return resultList.size() >= 1;
  }


  @SuppressWarnings("unchecked")
  static public List<EbAccount> getAccountList()
  {
    List<EbAccount> returnedList = new ArrayList<EbAccount>();
    List<PersistAccount> accountList = null;
    accountList = (List<PersistAccount>)getList( PersistAccount.class, null );
    // TODO this request could be optimized by a lot... I guess
    for( PersistAccount persistGame : accountList )
    {
      EbAccount account = persistGame.getAccount();
      if( account != null )
      {
        returnedList.add( account );
      }
    }

    return returnedList;
  }

  @SuppressWarnings("unchecked")
  static public List<PersistGame> getPersistGameList()
  {
    List<PersistGame> gameList = null;
    gameList = (List<PersistGame>)getList( PersistGame.class, null );
    return gameList;
  }

  @Deprecated
  static public List<com.fullmetalgalaxy.model.persist.EbGamePreview> getGamePreviewList()
  {
    List<EbGamePreview> returnedList = new ArrayList<EbGamePreview>();
    List<PersistGame> gameList = getPersistGameList();
    // TODO this request could be optimized by a lot... I guess
    for( PersistGame persistGame : gameList )
    {
      returnedList.add( new EbGamePreview( persistGame.getGame() ) );
    }

    return returnedList;
  }


  public FmgDataStore()
  {
  }

  public PersistAccount getPersistAccount(long p_id)
  {
    PersistEntity entity = getPersistEntity( PersistAccount.class, p_id );
    if( entity == null )
    {
      return null;
    }
    return PersistAccount.class.cast( entity );
  }

  public EbAccount getAccount(long p_id)
  {
    PersistAccount entity = getPersistAccount( p_id );
    if( entity == null )
    {
      return null;
    }
    return entity.getAccount();
  }

  public EbGame getGame(long p_id)
  {
    PersistEntity entity = getPersistEntity( PersistGame.class, p_id );
    if( entity == null )
    {
      return null;
    }
    return PersistGame.class.cast( entity ).getGame();
  }

  public void save(EbAccount p_account)
  {
    super.save( PersistAccount.class, p_account );
  }

  public void save(EbGame p_game)
  {
    super.save( PersistGame.class, p_game );
  }

  public void deleteAccount(long p_id)
  {
    delete( PersistAccount.class, p_id );
  }

  public void deleteGame(long p_id)
  {
    delete( PersistGame.class, p_id );
  }


}
