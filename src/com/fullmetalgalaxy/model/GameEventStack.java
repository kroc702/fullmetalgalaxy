/**
 * 
 */
package com.fullmetalgalaxy.model;

import javax.persistence.Transient;

import com.fullmetalgalaxy.model.persist.gamelog.AnEvent;


/**
 * @author Vincent Legendre
 *
 */
public interface GameEventStack
{
  @Transient
  public AnEvent getLastGameLog();
}
