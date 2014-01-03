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
 *  Copyright 2010 to 2014 Vincent Legendre
 *
 * *********************************************************************/
package com.fullmetalgalaxy.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * This class represent a location where user can be connected.
 * a gameId uniquely identify rooms.
 * @author Vincent
 *
 */
public class PresenceRoom implements IsSerializable, java.io.Serializable, List<Presence>
{
  private static final long serialVersionUID = 1L;

  private long m_gameId = 0;
  private List<Presence> m_presenceList = new ArrayList<Presence>();

  

  @SuppressWarnings("unused")
  private PresenceRoom()
  {
  }

  public PresenceRoom(long p_gameId)
  {
    m_gameId = p_gameId;
  }

  public List<Presence> getPresence(String p_pseudo)
  {
    List<Presence> presences = new ArrayList<Presence>();
    for( Presence presence : this )
    {
      if( presence.getPseudo().equalsIgnoreCase( p_pseudo ) )
      {
        presences.add( presence );
      }
    }
    return presences;
  }

  public Presence getLastPresence(String p_pseudo)
  {
    Presence lastPresence = null;
    for( Presence presence : getPresence(p_pseudo) )
    {
      if( lastPresence == null )
      {
        lastPresence = presence;
      }
      else if( lastPresence.getLastConnexion().before( presence.getLastConnexion() ))
      {
        lastPresence = presence;
      }
    }
    return lastPresence;
  }

  public Presence getPresence(String p_pseudo, int p_pageId)
  {
    for( Presence presence : this )
    {
      if( presence.getPseudo().equalsIgnoreCase( p_pseudo ) && presence.getPageId() == p_pageId )
      {
        return presence;
      }
    }
    return null;
  }

  public boolean isConnected(String p_pseudo)
  {
    for( Presence presence : this )
    {
      if( presence.getPseudo().equalsIgnoreCase( p_pseudo ) )
      {
        return true;
      }
    }
    return false;
  }

  /**
   * add a presence to this room.
   * couple (pseudo;pageId) must be unique over all connected page on this room.
   * @param p_pseudo
   * @param p_pageId 
   * @return
   */
  public Presence connect(Presence presence)
  {
    int index = indexOf( presence );
    if(index < 0)
    {
      add( presence );
    }
    else
    {
      get(index).getLastConnexion().setTime( presence.getLastConnexion().getTime() );
    }
    return presence;
  }


  /**
   * count number of different connected pseudo 
   * @return
   */
  public int countPseudo()
  {
    Set<String> pseudo = new HashSet<String>();
    for( Presence presence : this )
    {
      if( !pseudo.contains( presence.getPseudo() ) )
      {
        pseudo.add( presence.getPseudo() );
      }
    }
    return pseudo.size();
  }

  /**
   * 
   * @return true if presence list changed 
   */
  /*public boolean removeTooOld()
  {
    ArrayList<Presence> toRemove = new ArrayList<Presence>();
    for( Presence presence : getPresenceList() )
    {
      if( SharedMethods.currentTimeMillis() - presence.lastUp.getTime() > 100 * 1000 )
      {
        toRemove.add( presence );
      }
    }
    getPresenceList().removeAll( toRemove );
    return !toRemove.isEmpty();
  }*/

  /**
   * 
   * @param p_pseudo
   * @param p_channelId
   * @return true if presence list changed
   */
  /*public boolean up(String p_pseudo, int p_channelId)
  {

    boolean isUpdated = false;
    for( Presence presence : this )
    {
      if( p_pseudo.equals( presence.pseudo ) && p_channelId == presence.pageId )
      {
        presence.lastUp.setTime( SharedMethods.currentTimeMillis() );
        isUpdated = true;
      }
    }
    if( !isUpdated )
    {
      getPresenceList().add( new Presence( p_pseudo, p_channelId ) );
      isUpdated = true;
    }
    else
    {
      isUpdated = false;
    }
    isUpdated |= removeTooOld();
    return isUpdated;
  }*/


  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString()
  {
    return "Room ID=" + m_gameId + " " + m_presenceList;
  }

  /**
   * @return the gameId
   */
  public long getGameId()
  {
    return m_gameId;
  }

  /**
   * @param p_gameId the gameId to set
   */
  public void setGameId(long p_gameId)
  {
    m_gameId = p_gameId;
  }


  // List interface
  // ==============

  /**
   * @return
   * @see java.util.List#isEmpty()
   */
  @Override
  public boolean isEmpty()
  {
    return m_presenceList.isEmpty();
  }

  /**
   * @param p_arg0
   * @param p_arg1
   * @see java.util.List#add(int, java.lang.Object)
   */
  @Override
  public void add(int p_arg0, Presence p_arg1)
  {
    m_presenceList.add( p_arg0, p_arg1 );
  }

  /**
   * @param p_arg0 != null and shouldn't present
   * @return
   * @see java.util.List#add(java.lang.Object)
   */
  @Override
  public boolean add(Presence p_arg0)
  {
    if(p_arg0 == null)
    {
      return false;
    }
    return m_presenceList.add( p_arg0 );
  }

  /**
   * @param p_arg0
   * @return
   * @see java.util.List#addAll(java.util.Collection)
   */
  @Override
  public boolean addAll(Collection<? extends Presence> p_arg0)
  {
    return m_presenceList.addAll( p_arg0 );
  }

  /**
   * @param p_arg0
   * @param p_arg1
   * @return
   * @see java.util.List#addAll(int, java.util.Collection)
   */
  @Override
  public boolean addAll(int p_arg0, Collection<? extends Presence> p_arg1)
  {
    return m_presenceList.addAll( p_arg0, p_arg1 );
  }

  /**
   * 
   * @see java.util.List#clear()
   */
  @Override
  public void clear()
  {
    m_presenceList.clear();
  }

  /**
   * @param p_arg0
   * @return
   * @see java.util.List#contains(java.lang.Object)
   */
  @Override
  public boolean contains(Object p_arg0)
  {
    return m_presenceList.contains( p_arg0 );
  }

  /**
   * @param p_arg0
   * @return
   * @see java.util.List#containsAll(java.util.Collection)
   */
  @Override
  public boolean containsAll(Collection<?> p_arg0)
  {
    return m_presenceList.containsAll( p_arg0 );
  }

  /**
   * @param p_arg0
   * @return
   * @see java.util.List#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object p_arg0)
  {
    return m_presenceList.equals( p_arg0 );
  }

  /**
   * @param p_arg0
   * @return
   * @see java.util.List#get(int)
   */
  @Override
  public Presence get(int p_arg0)
  {
    return m_presenceList.get( p_arg0 );
  }

  /**
   * @return
   * @see java.util.List#hashCode()
   */
  @Override
  public int hashCode()
  {
    return m_presenceList.hashCode();
  }

  /**
   * @param p_arg0
   * @return
   * @see java.util.List#indexOf(java.lang.Object)
   */
  @Override
  public int indexOf(Object p_arg0)
  {
    return m_presenceList.indexOf( p_arg0 );
  }

  /**
   * @return
   * @see java.util.List#iterator()
   */
  @Override
  public Iterator<Presence> iterator()
  {
    return m_presenceList.iterator();
  }

  /**
   * @param p_arg0
   * @return
   * @see java.util.List#lastIndexOf(java.lang.Object)
   */
  @Override
  public int lastIndexOf(Object p_arg0)
  {
    return m_presenceList.lastIndexOf( p_arg0 );
  }

  /**
   * @return
   * @see java.util.List#listIterator()
   */
  @Override
  public ListIterator<Presence> listIterator()
  {
    return m_presenceList.listIterator();
  }

  /**
   * @param p_arg0
   * @return
   * @see java.util.List#listIterator(int)
   */
  @Override
  public ListIterator<Presence> listIterator(int p_arg0)
  {
    return m_presenceList.listIterator( p_arg0 );
  }

  /**
   * @param p_arg0
   * @return
   * @see java.util.List#remove(int)
   */
  @Override
  public Presence remove(int p_arg0)
  {
    return m_presenceList.remove( p_arg0 );
  }

  /**
   * @param p_arg0
   * @return
   * @see java.util.List#remove(java.lang.Object)
   */
  @Override
  public boolean remove(Object p_arg0)
  {
    return m_presenceList.remove( p_arg0 );
  }

  /**
   * @param p_arg0
   * @return
   * @see java.util.List#removeAll(java.util.Collection)
   */
  @Override
  public boolean removeAll(Collection<?> p_arg0)
  {
    return m_presenceList.removeAll( p_arg0 );
  }

  /**
   * @param p_arg0
   * @return
   * @see java.util.List#retainAll(java.util.Collection)
   */
  @Override
  public boolean retainAll(Collection<?> p_arg0)
  {
    return m_presenceList.retainAll( p_arg0 );
  }

  /**
   * @param p_arg0
   * @param p_arg1
   * @return
   * @see java.util.List#set(int, java.lang.Object)
   */
  @Override
  public Presence set(int p_arg0, Presence p_arg1)
  {
    return m_presenceList.set( p_arg0, p_arg1 );
  }

  /**
   * @return
   * @see java.util.List#size()
   */
  @Override
  public int size()
  {
    return m_presenceList.size();
  }

  /**
   * @param p_arg0
   * @param p_arg1
   * @return
   * @see java.util.List#subList(int, int)
   */
  @Override
  public List<Presence> subList(int p_arg0, int p_arg1)
  {
    return m_presenceList.subList( p_arg0, p_arg1 );
  }

  /**
   * @return
   * @see java.util.List#toArray()
   */
  @Override
  public Object[] toArray()
  {
    return m_presenceList.toArray();
  }

  /**
   * @param <T>
   * @param p_arg0
   * @return
   * @see java.util.List#toArray(T[])
   */
  @Override
  public <T> T[] toArray(T[] p_arg0)
  {
    return m_presenceList.toArray( p_arg0 );
  }



}
