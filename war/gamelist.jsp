<%@page import="com.google.gwt.i18n.client.impl.ConstantMap"%>
<%@ page import="java.util.*,com.fullmetalgalaxy.server.*,com.fullmetalgalaxy.model.persist.*,com.fullmetalgalaxy.model.constant.*" %>
<%@page pageEncoding="utf-8" contentType="text/html; charset=utf-8" %>
<!DOCTYPE html>
<html>
<head>
<title>Full Metal Galaxy - Liste des parties</title>
        
<%@include file="include/meta.jsp"%>
<style type="text/css">@import url( <%= I18n.localize(request,response,"/style.css") %> );</style>

</head>
<body>
<%@include file="include/header.jsp"%>

<%
final int COUNT_PER_PAGE = 20;
int offset = 0;
int gameCount = 0;
try
{
  offset = Integer.parseInt( request.getParameter( "offset" ) );
} catch( NumberFormatException e )
{
}
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
	<jsp:include page="<%= I18n.localize(request,response,\"/puzzleslist.jsp\") %>" />
<% } else { 
	Iterable<EbGamePreview> gameList = new ArrayList<EbGamePreview>();
	if( !Auth.isUserLogged( request, response ) && (tab <= 1) ) {
		// my games or open games but not logged
		out.println("<center><h2>Vous n'êtes pas connecté</h2></center>");
	} else if( tab == 0 ) {
		// new or open games
		gameList = FmgDataStore.dao().query(EbGamePreview.class).filter( "m_isOpen", true );
		gameCount = ((com.googlecode.objectify.Query<EbGamePreview>)gameList).count();
		((com.googlecode.objectify.Query<EbGamePreview>)gameList).limit(COUNT_PER_PAGE);
		((com.googlecode.objectify.Query<EbGamePreview>)gameList).offset( offset );
	} else if( tab == 1 && Auth.isUserLogged( request, response ) ) {
		// my games
		String myPseudo = Auth.getUserPseudo( request, response );
		gameList = FmgDataStore.dao().query(EbGamePreview.class).filter( "m_history", false ).filter( "m_setRegistration.m_account.m_pseudo", myPseudo );
		gameCount = ((com.googlecode.objectify.Query<EbGamePreview>)gameList).count();
		((com.googlecode.objectify.Query<EbGamePreview>)gameList).limit(COUNT_PER_PAGE);
		((com.googlecode.objectify.Query<EbGamePreview>)gameList).offset( offset );
	} else if( tab == 3 ) {
		// all games
		gameList = FmgDataStore.dao().query(EbGamePreview.class);
		gameCount = ((com.googlecode.objectify.Query<EbGamePreview>)gameList).count();
		((com.googlecode.objectify.Query<EbGamePreview>)gameList).limit(COUNT_PER_PAGE);
		((com.googlecode.objectify.Query<EbGamePreview>)gameList).offset( offset );
	}
%>
	<p><%= gameCount %> exploitation(s) trouvée(s) :</p>
	<table class="fmp-array" style="width:100%;">
	<%  for( EbGamePreview game : gameList )
	    {
	      out.println( "<tr>" );
	      // minimap
	      out.println( "<td style=\"width:100px;\"><a href=\"/game.jsp?id="+game.getId()+"\"><img src=\""+game.getMinimapUri()+"\" height=\"50\"></a></td>" );
	      // game name
	      out.println( "<td><a href=\"/game.jsp?id="+game.getId()+"\"><big>"+ game.getName() + "</big><br/><small>" );
	      // player name and number
	      out.println( game.getPlayersAsString()  );
	      if( game.getCurrentNumberOfRegiteredPlayer() != game.getMaxNumberOfPlayer() ) {
	      	out.println( " (" + game.getCurrentNumberOfRegiteredPlayer()+"/"+ game.getMaxNumberOfPlayer() + ")" );
	      }
  		  out.println( "</small></a></td>" );
	      
	      // time option
	      out.println("<td>" );
	      out.println( game.getIconsAsHtml() );
	          
	      if( ConfigGameTime.getEbConfigGameTime(game.getConfigGameTime()).isAsynchron() )
	      {
	        out.println(""+(game.getCurrentTimeStep()*100/ConfigGameTime.getEbConfigGameTime(game.getConfigGameTime()).getTotalTimeStep())+"%");
	      } else {
	        out.println(""+game.getCurrentTimeStep()+"/"+ConfigGameTime.getEbConfigGameTime(game.getConfigGameTime()).getTotalTimeStep());
	      }
	      out.println("</td>" );
	      
	      // admin option
	      if(Auth.isUserAdmin(request, response))
	      {
	      	out.println("<td><a href=\"/admin/Servlet?deletegame="+game.getId()+"\">effacer</a></td>" );
	      }
	      out.println("</tr>" );
	    }
	%>
	</table>
	<p>Pages :
	<%
		int p = 0;
		while(gameCount > 0)
		{
		  out.println( "<a href='"+ request.getRequestURL() +"?tab="+tab+"&offset="+(p*COUNT_PER_PAGE)+"'>"+(p+1)+"</a> " );
		  gameCount -= COUNT_PER_PAGE;
		  p++;
		}
	%>
	</p>
	<% if( tab == 0 && Auth.isUserLogged(request, response)) { %>
		<center><a href="editgame.jsp"><big>Cr&eacute;er une nouvelle partie</big></a></center>
	<% } %>
	<br/><small>
	<img src='/images/icons/turnbyturn16.png'/> : Partie en mode tour par tour<br/>
	<img src='/images/icons/parallele16.png'/> : Partie en mode parallèle<br/>
	<img src='/images/icons/slow16.png'/> : Partie lente (25 jours ou illimité)<br/>
	<img src='/images/icons/fast16.png'/> : Partie rapide (1h30)<br/>
	<img src='/images/icons/pause16.png'/> : Partie en pause<br/>
	<img src='/images/icons/history16.png'/> : Partie archivé<br/>
	</small>
<% } %>


<%@include file="include/footer.jsp"%>
</body>
</html>
