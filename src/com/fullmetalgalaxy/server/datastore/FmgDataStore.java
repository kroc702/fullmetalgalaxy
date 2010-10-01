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

  static public EbGame sgetGame(long p_id)
  {
    PersistEntity entity = DataStore.getEntity( PersistGame.class, p_id );
    if( entity == null )
    {
      return null;
    }
    return PersistGame.class.cast( entity ).getGame();
  }

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

  static public PersistAccount getPersistAccount(String p_login)
  {
    Iterable<PersistAccount> resultList = (Iterable<PersistAccount>)getList( PersistAccount.class,
        "m_login", p_login );
    if( !resultList.iterator().hasNext() )
    {
      return null;
    }
    return resultList.iterator().next();
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
    Iterable<?> resultList = getList( PersistAccount.class, "m_login", p_login );
    return resultList.iterator().hasNext();
  }

  static public boolean isPseudoExist(String p_pseudo)
  {
    Iterable<?> resultList = getList( PersistAccount.class, "m_pseudo", p_pseudo );
    return resultList.iterator().hasNext();
  }


  static public Iterable<EbAccount> getAccountList()
  {
    List<EbAccount> returnedList = new ArrayList<EbAccount>();
    Iterable<PersistAccount> accountList = null;
    accountList = (Iterable<PersistAccount>)getList( PersistAccount.class, null, null );
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


  static public com.googlecode.objectify.Query<PersistGame> getPersistGameList()
  {
    com.googlecode.objectify.Query<PersistGame> gameList = null;
    gameList = (com.googlecode.objectify.Query<PersistGame>)getList( PersistGame.class, null, null );
    return gameList;
  }

  @Deprecated
  static public List<com.fullmetalgalaxy.model.persist.EbGamePreview> getGamePreviewList()
  {
    List<EbGamePreview> returnedList = new ArrayList<EbGamePreview>();
    Iterable<PersistGame> gameList = getPersistGameList();
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

  public void save(PersistAccount p_account)
  {
    super.save( p_account );
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