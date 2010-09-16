<%@ page import="java.util.*,com.fullmetalgalaxy.server.*,com.fullmetalgalaxy.server.datastore.*,com.fullmetalgalaxy.model.persist.*,com.fullmetalgalaxy.model.constant.*" %>
<%@page pageEncoding="Cp1252" contentType="text/html; charset=Cp1252" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
<title>Full Metal Galaxy - Liste des parties</title>
        
<%@include file="include/meta.jsp"%>

</head>
<body>
<%@include file="include/header.jsp"%>

<%
long tab = 2;
if( Auth.isUserLogged( request, response ) )
{
	tab = 1; // my games
}
try
{
	tab = Long.parseLong( request.getParameter( "tab" ) );
} catch( NumberFormatException e )
{
}
if(tab < 0 || tab > 3 )
{
	tab = 2; // solo games
}
%>

	<table style="width:100%;"><tr>
	<td><%= tab==0? "<div class='selected-gamelisttab'>" : "<a class='icon-gamelisttab' href='/gamelist.jsp?tab=0'>" %>
		<img class='icon' src="/images/clear.cache.gif" title="Nouvelles parties"/> <br/> 
		<img class='text' src="/images/clear.cache.gif"/><%= tab==0? "</div>" : "</a>" %></td>
	<td><%= tab==1? "<div class='selected-gamelisttab'>" : "<a class='icon-gamelisttab' href='/gamelist.jsp?tab=1'>" %>
		<img class='icon' src="/images/clear.cache.gif" style="background-position: -170px 0px;" title="Mes parties en cours"/> <br/>
		<img class='text' src="/images/clear.cache.gif" style="background-position: -170px 0px;"/><%= tab==1? "</div>" : "</a>" %></td>
	<td><%= tab==2? "<div class='selected-gamelisttab'>" : "<a class='icon-gamelisttab' href='/gamelist.jsp?tab=2'>" %>
		<img class='icon' src="/images/clear.cache.gif" style="background-position: -340px 0px;" title="Jeux solo"/> <br/> 
		<img class='text' src="/images/clear.cache.gif" style="background-position: -340px 0px;"/><%= tab==2? "</div>" : "</a>" %></td>
	<td><%= tab==3? "<div class='selected-gamelisttab'>" : "<a class='icon-gamelisttab' href='/gamelist.jsp?tab=3'>" %>
		<img class='icon' src="/images/clear.cache.gif" style="background-position: -510px 0px;" title="Toutes les parties"/> <br/>
		<img class='text' src="/images/clear.cache.gif" style="background-position: -510px 0px;"/><%= tab==3? "</div>" : "</a>" %></td>
	</tr></table>
	<br/><br/>
	
<% if(tab==2) { /* solo games */ %>
	<%@include file="include/puzzleslist.html"%>
<% } else { 
	Iterable<PersistGame> gameList = new ArrayList<PersistGame>();
	if( tab == 0 ) {
		// new or open games
		gameList = FmgDataStore.getPersistGameList();
		((com.googlecode.objectify.Query<PersistGame>)gameList).filter( "m_isOpen", true );
	} else if( tab == 1 && !Auth.isUserLogged( request, response ) ) {
		// my games but not logged
		out.println("<center><h2>Vous n'�tes pas connect�</h2></center>");
	} else if( tab == 1 && Auth.isUserLogged( request, response ) ) {
		// my games
		String myPseudo = " " + Auth.getUserPseudo( request, response ) + " ";
		com.googlecode.objectify.Query<PersistGame> query = FmgDataStore.getPersistGameList();
		query.filter( "m_history", false );
		for( PersistGame game : query )
	    {
	    	if(game.getPlayers() != null && game.getPlayers().contains( myPseudo ))
	    	{
	    		((ArrayList<PersistGame>)gameList).add( game );
	    	}
	    }
	} else if( tab == 3 ) {
		// all games
		gameList = FmgDataStore.getPersistGameList();
	}
%>
	<table class="fmp-array" style="width:100%;">
	<%  for( PersistGame game : gameList )
	    {
	      out.println( "<tr>" );
	      // minimap
	      out.println( "<td style=\"width:100px;\"><a href=\"/game.jsp?id="+game.getId()+"\"><img src=\"/ImageServlet?minimap="+game.getId()+"\" height=\"50\"></a></td>" );
	      // game name
	      out.println( "<td><a href=\"/game.jsp?id="+game.getId()+"\"><big>"+ game.getName() + "</big><br/><small>" );
	      // player name and number
	      out.println( game.getPlayers()  );
	      if( game.getCurrentNumberOfRegiteredPlayer() != game.getMaxNumberOfPlayer() ) {
	      	out.println( " (" + game.getCurrentNumberOfRegiteredPlayer()+"/"+ game.getMaxNumberOfPlayer() + ")" );
	      }
  		  out.println( "</small></a></td>" );
	      
	      // time option
	      out.println("<td>" );
	      if( !game.isStarted() ) {
	      	out.println( "<img src='/images/css/icon_pause.cache.png' title='En pause' />" );
	      }
	      if( game.getConfigGameTime() == ConfigGameTime.Standard || game.getConfigGameTime() == ConfigGameTime.QuickTurnBased ) {
		    out.println( "<img src='/images/css/icon_tbt.cache.png' title='"+game.getConfigGameTime().getEbConfigGameTime().getDescription()+"' />" );
	      }
	      if( game.getConfigGameTime() == ConfigGameTime.StandardAsynch ) {
		    out.println( "<img src='/images/css/icon_slow.cache.png' title='"+game.getConfigGameTime().getEbConfigGameTime().getDescription()+"' />" );
		  }
	      if( game.getConfigGameTime() == ConfigGameTime.QuickTurnBased || game.getConfigGameTime() == ConfigGameTime.QuickAsynch ) {
		    out.println( "<img src='/images/css/icon_fast.cache.png' title='"+game.getConfigGameTime().getEbConfigGameTime().getDescription()+"' />" );
		  }
	      if( game.isHistory() ) {
	      	out.println( "<img src='/images/css/icon_history.cache.png' title='Archive' />" );
	      }
	      out.println("</tr>" );
	      
	      // admin option
	      if(Auth.isUserAdmin(request, response))
	      {
	      	out.println("<td><a href=\"/admin/Servlet?deletegame="+game.getId()+"\">effacer</a></td>" );
	      }
	      out.println("</tr>" );
	    }
	%>
	</table>
	<% if( tab == 0 && Auth.isUserLogged(request, response)) { %>
		<center><a href="editgame.jsp"><big>Cr&eacute;er une nouvelle partie</big></a></center>
	<% } %>
	<br/><small>
	<img src='/images/css/icon_pause.cache.png'/> : Partie en pause<br/>
	<img src='/images/css/icon_tbt.cache.png'/> : Partie en mode tour par tour sans limite de temps<br/>
	<img src='/images/css/icon_tbt.cache.png'/><img src='/images/css/icon_fast.cache.png'/> : Partie rapide en tour par tour (3 min pour jouer)<br/>
	<img src='/images/css/icon_slow.cache.png'/> : Partie lente en mode asynchrone (25 jours)<br/>
	<img src='/images/css/icon_fast.cache.png'/> : Partie rapide en mode asynchrone (1h20)<br/>
	</small>
<% } %>


<%@include file="include/footer.jsp"%>
</body>
</html>
