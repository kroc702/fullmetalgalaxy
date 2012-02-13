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
 *  Copyright 2010, 2011 Vincent Legendre
 *
 * *********************************************************************/

package com.fullmetalgalaxy.client.ressources.smiley;

import java.util.HashMap;

import com.google.gwt.user.client.ui.AbstractImagePrototype;

/**
 * @author vlegendr
 *
 */
public class SmileyCollection extends HashMap<String,AbstractImagePrototype>
{
  private static final long serialVersionUID = 1L;
  public static SmileyCollection INSTANCE = new SmileyCollection();
  
  public SmileyCollection()
  {
    put( " :/", AbstractImagePrototype.create( Smiley.INSTANCE.skeptical() ) );
    put( ":-/", AbstractImagePrototype.create( Smiley.INSTANCE.skeptical() ) );
    put( "/'\\", AbstractImagePrototype.create( Smiley.INSTANCE.bell() ) );
    put( "/&#39;\\", AbstractImagePrototype.create( Smiley.INSTANCE.bell() ) );
    put( "B-)", AbstractImagePrototype.create( Smiley.INSTANCE.cool() ) );
    put( "B)", AbstractImagePrototype.create( Smiley.INSTANCE.cool() ) );
    put( ":'(", AbstractImagePrototype.create( Smiley.INSTANCE.cry() ) );
    put( "}:-)", AbstractImagePrototype.create( Smiley.INSTANCE.devil() ) );
    put( "}:)", AbstractImagePrototype.create( Smiley.INSTANCE.devil() ) );
    put( "x-(", AbstractImagePrototype.create( Smiley.INSTANCE.grimace() ) );
    put( "<3", AbstractImagePrototype.create( Smiley.INSTANCE.heart() ) );
    put( "&lt;3", AbstractImagePrototype.create( Smiley.INSTANCE.heart() ) );
    put( ":-|", AbstractImagePrototype.create( Smiley.INSTANCE.indifferent() ) );
    put( ":|", AbstractImagePrototype.create( Smiley.INSTANCE.indifferent() ) );
    put( ":-D", AbstractImagePrototype.create( Smiley.INSTANCE.lol() ) );
    put( ":D", AbstractImagePrototype.create( Smiley.INSTANCE.lol() ) );
    put( ":-o", AbstractImagePrototype.create( Smiley.INSTANCE.no() ) );
    put( "~@~", AbstractImagePrototype.create( Smiley.INSTANCE.poo() ) );
    put( "[:-|]", AbstractImagePrototype.create( Smiley.INSTANCE.robot() ) );
    put( "[:|]", AbstractImagePrototype.create( Smiley.INSTANCE.robot() ) );
    put( "\\m/", AbstractImagePrototype.create( Smiley.INSTANCE.rock() ) );
    put( ":-(", AbstractImagePrototype.create( Smiley.INSTANCE.sad() ) );
    put( ":(", AbstractImagePrototype.create( Smiley.INSTANCE.sad() ) );
    put( ":-)", AbstractImagePrototype.create( Smiley.INSTANCE.smile() ) );
    put( ":)", AbstractImagePrototype.create( Smiley.INSTANCE.smile() ) );
    put( ":-p", AbstractImagePrototype.create( Smiley.INSTANCE.tongue() ) );
    put( ":p", AbstractImagePrototype.create( Smiley.INSTANCE.tongue() ) );
    put( ":P", AbstractImagePrototype.create( Smiley.INSTANCE.tongue() ) );
    put( ";-)", AbstractImagePrototype.create( Smiley.INSTANCE.wink() ) );
    put( ";)", AbstractImagePrototype.create( Smiley.INSTANCE.wink() ) );
    
  }
  
  public String remplace(String in)
  {
    String out = in.replace( " :/", "  :/" );
    for( java.util.Map.Entry<String, AbstractImagePrototype> entry : this.entrySet() )
    {
      out = out.replace( entry.getKey(), entry.getValue().getHTML() );
    }
    return out;
  }
  
}
