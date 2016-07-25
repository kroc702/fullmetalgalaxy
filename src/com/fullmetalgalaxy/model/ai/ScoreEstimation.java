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
 *  Copyright 2010, 2011, 2012, 2013 Vincent Legendre
 *
 * *********************************************************************/

package com.fullmetalgalaxy.model.ai;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.fullmetalgalaxy.model.EnuColor;
import com.fullmetalgalaxy.model.Location;
import com.fullmetalgalaxy.model.TokenType;
import com.fullmetalgalaxy.model.persist.EbRegistration;
import com.fullmetalgalaxy.model.persist.EbTeam;
import com.fullmetalgalaxy.model.persist.EbToken;
import com.fullmetalgalaxy.model.persist.Game;

/**
 * @author Kroc
 *
 */
public class ScoreEstimation
{
  Game game = null;

  /**
   * 
   */
  public ScoreEstimation(Game game)
  {
    this.game = game;
  }

  public Map<EbTeam,Integer> getActionPtPerTimeStep()
  {
    Map<EbTeam,Integer> actionPtPerTimeStep = new HashMap<EbTeam,Integer>();
    List<EbToken> freighters = game.getAllFreighter( EnuColor.getMaxColorValue() );
    for(EbTeam team : game.getTeams()){
      int teamActionPt = 0;
      for( EbRegistration player : team.getPlayers( game.getPreview() ) ) {
        int playerActionPt = 0;
        for( EbToken freighter : freighters )
        {
          if( player.getEnuColor().contain( freighter.getColor() ) && freighter.getLocation() == Location.Board )
          {
            if( playerActionPt == 0 )
            {
              playerActionPt = game.getEbConfigGameTime().getActionPtPerTimeStep();
            }
            else
            {
              playerActionPt += game.getEbConfigGameTime().getActionPtPerExtraShip();
            }
          }
        }
        teamActionPt += playerActionPt;
      }
      actionPtPerTimeStep.put( team, teamActionPt );
    }
    return actionPtPerTimeStep;
  }

  /**
   * return an aproximation of remaining action point for every teams until the game end.
   * @return
   */
  public Map<EbTeam,Integer> estimateTotalActionPoint()
  {
    Map<EbTeam, Integer> totalActionPoint = new HashMap<EbTeam, Integer>();
    int remainingTimeStep = game.getEbConfigGameTime().getTotalTimeStep() - game.getCurrentTimeStep();
    for( Integer takeOffTurn : game.getEbConfigGameTime().getTakeOffTurns() )
    {
      // takeoff turn doesn't receive action points
      if( game.getCurrentTimeStep() < takeOffTurn )
      {
        remainingTimeStep--;
      }
    }
    for( Entry<EbTeam, Integer> entry : getActionPtPerTimeStep().entrySet() )
    {
      totalActionPoint.put( entry.getKey(), entry.getValue() * remainingTimeStep );
    }
    return totalActionPoint;
  }


  protected static class TokenDistance implements Comparable<TokenDistance>
  {
    EbToken token = null;
    int distance = 0;

    public TokenDistance(EbToken p_token, int p_distance)
    {
      super();
      token = p_token;
      distance = p_distance;
    }

    @Override
    public int compareTo(TokenDistance p_o)
    {
      return distance - p_o.distance;
    }


  }

  EbTeam getHigherActionTeam(Map<EbTeam, Integer> totalActionPoint, int minValue)
  {
    EbTeam team = null;
    int actionPoint = minValue;
    for( Entry<EbTeam, Integer> entry : totalActionPoint.entrySet() )
    {
      if( entry.getValue() > actionPoint )
      {
        actionPoint = entry.getValue();
        team = entry.getKey();
      }
    }
    return team;
  }


  Map<EbTeam, Integer> unusedActionPoint = new HashMap<EbTeam, Integer>();

  /**
   * after a call to estimateFinalScore, this method return the unused action point after 
   * mining available ores.
   * @return
   */
  public Map<EbTeam, Integer> getUnusedActionPoint()
  {
    return unusedActionPoint;
  }


  /**
   * estimate the final score if all team are only collecting ore
   * @return
   */
  public Map<EbTeam, Integer> estimateFinalScore()
  {
    Map<EbTeam, Integer> finalScores = new HashMap<EbTeam, Integer>();
    Map<EbTeam, Integer> totalActionPoint = estimateTotalActionPoint();
    unusedActionPoint = new HashMap<EbTeam, Integer>();

    // establish a list of freighter per team
    // for freighter that are not on board, we add dirrectly his score
    List<EbToken> freighters = game.getAllFreighter( EnuColor.getMaxColorValue() );
    Map<EbTeam, List<EbToken>> onBoardFreighters = new HashMap<EbTeam, List<EbToken>>();
    for( EbTeam team : game.getTeams() )
    {
      int teamScore = 0;
      List<EbToken> onBoardFreighter = new ArrayList<EbToken>();
      for( EbToken freighter : freighters )
      {
        if( freighter.getEnuColor().isColored( team.getColors( game.getPreview() ) ) )
        {
          if( freighter.getLocation() == Location.Orbit || freighter.getLocation() == Location.EndGame )
          {
            teamScore += freighter.getWinningPoint();
          }
          else if( freighter.getLocation() == Location.Board )
          {
            onBoardFreighter.add( freighter );
          }
        }
      }
      teamScore -= team.getPlayerIds().size() * game.getInitialScore();
      onBoardFreighters.put( team, onBoardFreighter );
      finalScores.put( team, teamScore );
    }

    // compute distance between all onboard ore and the closest freighter for every team.
    Map<EbTeam, List<TokenDistance>> teamTokenDistances = new HashMap<EbTeam, List<TokenDistance>>();
    for( EbTeam team : game.getTeams() )
    {
      teamTokenDistances.put( team, new ArrayList<TokenDistance>() );
    }
    for( EbToken token : game.getSetToken() )
    {
      if( token.getLocation() == Location.Board )
      {
        if( token.getType().isOre() )
        {
          for( EbTeam team : game.getTeams() )
          {
            // compute distance from ore to closest freighter
            int minDistance = Integer.MAX_VALUE;
            for( EbToken freighter : onBoardFreighters.get( team ) )
            {
              minDistance = Math.min( minDistance,
                  game.getCoordinateSystem().getDiscreteDistance( token.getPosition(), freighter.getPosition() ) );
            }
            teamTokenDistances.get( team ).add( new TokenDistance( token, minDistance ) );
          }
        }
        else if( token.getType() == TokenType.Ore2Generator || token.getType() == TokenType.Ore3Generator )
        {
          // consider a generator produce 10 ores
          for( int i = 0; i < 10; i++ )
          {
            EbToken generatedOre = new EbToken( TokenType.Ore );
            if( token.getType() == TokenType.Ore3Generator )
            {
              generatedOre.setType( TokenType.Ore3 );
            }
            generatedOre.setLocation( token.getLocation() );
            generatedOre.setPosition( token.getPosition() );
            for( EbTeam team : game.getTeams() )
            {
              // compute distance from ore to closest freighter
              int minDistance = Integer.MAX_VALUE;
              for( EbToken freighter : onBoardFreighters.get( team ) )
              {
                minDistance = Math.min( minDistance,
                    game.getCoordinateSystem()
                        .getDiscreteDistance( generatedOre.getPosition(), freighter.getPosition() ) );
              }
              teamTokenDistances.get( team ).add( new TokenDistance( generatedOre, minDistance ) );
            }
          }
        }
        else if( token.getColor() != EnuColor.None )
        {
          // add colored token to score
          EbTeam team = game.getRegistrationByColor( token.getColor() ).getTeam( game );
          // compute distance from token to closest freighter
          int minDistance = Integer.MAX_VALUE;
          for( EbToken freighter : onBoardFreighters.get( team ) )
          {
            minDistance = Math.min( minDistance,
                game.getCoordinateSystem().getDiscreteDistance( token.getPosition(), freighter.getPosition() ) );
          }
          int remainingActionPoint = totalActionPoint.get( team ) - minDistance;
          if( remainingActionPoint >= 0 )
          {
            totalActionPoint.put( team, remainingActionPoint );
            finalScores.put( team, finalScores.get( team ) + token.getWinningPoint() );
          }
        }
      }
    }
    for( List<TokenDistance> tokenDistances : teamTokenDistances.values() )
    {
      Collections.sort( tokenDistances );
    }
    
    // then assign every ore to one team until they don't have anymore action
    // points
    Set<EbToken> assignedOre = new HashSet<EbToken>();
    EbTeam currentTeam = getHigherActionTeam( totalActionPoint, 0 );
    while( currentTeam != null )
    {
      List<TokenDistance> tokenDistances = teamTokenDistances.get( currentTeam );
      TokenDistance oreFound = null;
      for( TokenDistance tokenDistance : tokenDistances )
      {
        if( !assignedOre.contains( tokenDistance.token ) )
        {
          if( totalActionPoint.get( currentTeam ) > tokenDistance.distance )
          {
            assignedOre.add( tokenDistance.token );
            totalActionPoint.put( currentTeam, totalActionPoint.get( currentTeam ) - tokenDistance.distance );
            finalScores.put( currentTeam, finalScores.get( currentTeam ) + tokenDistance.token.getWinningPoint() );
            oreFound = tokenDistance;
          }
          break;
        }
      }
      if( oreFound == null )
      {
        unusedActionPoint.put( currentTeam, totalActionPoint.get( currentTeam ) );
        totalActionPoint.put( currentTeam, 0 );
      }

      currentTeam = getHigherActionTeam( totalActionPoint, 0 );
    }

    return finalScores;
  }

  /**
   * estimate current score of every team. are taken in account: all controlled units, loaded ore 
   * and colorless units (including ore) under team firecover (and no other).
   * @return
   */
  public Map<EbTeam, Integer> estimateCurrentScore()
  {
    // TODO report implementation from EbTeam class
    return null;
  }

}
