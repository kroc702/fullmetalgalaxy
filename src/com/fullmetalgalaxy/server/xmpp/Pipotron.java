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
package com.fullmetalgalaxy.server.xmpp;

import java.util.HashMap;
import java.util.Map;

import net.charabia.generation.Generator;

/**
 * this text generator use software from charabia.net
 * 
 * @author vlegendr
 * 
 * to improve haddock capitain language:
 * http://www.echolalie.org/wiki/index.php?ListeDesJuronsDuCapitaineHaddock
 * http://www.leblogdekickoff.com/article-3136846.html
 * http://www.tintinmilou.free.fr/jurons.htm
 */
public class Pipotron
{
  private static Map<String, Generator> m_generator = new HashMap<String, Generator>();


  private static synchronized Generator generator(String p_ressource)
  {
    Generator generator = null;
    generator = m_generator.get( p_ressource );
    if( generator != null )
    {
      return generator;
    }
    RessourceGraphStorage storage = new RessourceGraphStorage( p_ressource, "initial" );
    generator = new Generator( storage );
    m_generator.put( p_ressource, generator );
    return generator;
  }


  public static String pipo(String p_ressource)
  {
    // System.out.println( "pipo " + p_ressource );
    Generator g = generator( p_ressource );
    g.generate();
    String pipo = concatDeterminant( g.buildText() );
    if( pipo == null || pipo.isEmpty() )
    {
      return p_ressource + " failed";
    }
    return pipo;
  }


  /*
le la  ->  la
le l'   ->  l'
les l'  ->  les
de le   ->  du
de les  ->  des
de un   -> d'un

le
les
de  un
des une

+ 2 voyelles: le ile -> l'ile
aeiouy
*/
  
  private static String concatDeterminant(String p_in)
  {
    String out = p_in;
    out = out.replace( "de un ", "d'un " );
    out = out.replace( "de le un ", "du " );
    out = out.replaceAll( "des? (les? )?une? ", "des " );
    
    out = out.replace( "le une ", "la " );
    out = out.replace( "le un ", "le " );
    out = out.replaceAll( "les une? ", "les " );
    // probably useless...
    out = out.replace( "le l'", "l'" );

    out = out.replaceAll( "l[ea] ([aeiouy])", "l'$1" );

    
    return out;
  }
   

}
