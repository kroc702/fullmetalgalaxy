/**
 * 
 */
package com.fullmetalgalaxy.model.persist.triggers.actions;

import java.util.ArrayList;
import java.util.List;

import com.fullmetalgalaxy.model.Tide;
import com.fullmetalgalaxy.model.persist.EbBase;
import com.fullmetalgalaxy.model.persist.EbGame;
import com.fullmetalgalaxy.model.persist.gamelog.AnEvent;
import com.fullmetalgalaxy.model.persist.gamelog.EbEvtTide;


/**
 * @author Vincent Legendre
 * This action create a message event.
 */
public class EbActChangeTide extends AnAction
{
  static final long serialVersionUID = 123;

  private Tide m_nextTide = null;


  /**
   * 
   */
  public EbActChangeTide()
  {
    init();
  }

  /**
   * @param p_base
   */
  public EbActChangeTide(EbBase p_base)
  {
    super( p_base );
    init();
  }

  private void init()
  {
    m_nextTide = null;
  }

  @Override
  public void reinit()
  {
    super.reinit();
    this.init();
  }

  /* (non-Javadoc)
   * @see com.fullmetalgalaxy.model.persist.triggers.actions.AnAction#exec(com.fullmetalgalaxy.model.persist.EbGame)
   */
  @Override
  public List<AnEvent> createEvents(EbGame p_game, List<Object> p_params)
  {
    List<AnEvent> events = new ArrayList<AnEvent>();
    Tide nextTide = getNextTide();
    if( nextTide == null )
    {
      nextTide = Tide.getRandom();
    }
    EbEvtTide event = new EbEvtTide();
    event.setGame( p_game );
    event.setNextTide( nextTide );
    event.setOldTide( p_game.getCurrentTide() );
    event.setOldTideChange( p_game.getLastTideChange() );
    events.add( event );

    return events;
  }

  /**
   * @return the nextTide
   */
  public Tide getNextTide()
  {
    return m_nextTide;
  }

  /**
   * @param p_nextTide the nextTide to set
   */
  public void setNextTide(Tide p_nextTide)
  {
    m_nextTide = p_nextTide;
  }



}
