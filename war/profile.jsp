<%@ page import="java.util.*,java.text.*,com.fullmetalgalaxy.server.*,com.fullmetalgalaxy.model.persist.*,com.fullmetalgalaxy.model.constant.*,com.fullmetalgalaxy.model.*" %>
<%@page pageEncoding="Cp1252" contentType="text/html; charset=Cp1252" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
<title>Full Metal Galaxy - Profil du joueur</title>
        
<%@include file="include/meta.jsp"%>

</head>
<body>
<%@include file="include/header.jsp"%>

<%
EbAccount account = FmgDataStore.dao().find( EbAccount.class, id );
if( account == null ) 
{ 
	out.println("<h2>Le profil " + request.getParameter( "id" ) + " n'existe pas.</h2>" );
	return;
}
if( account.isIsforumIdConfirmed() && account.getForumId() != null )
{
  //response.sendRedirect( account.getProfileUrl() );
  //return;
}
if( Auth.isUserAdmin( request, response ) )
{
	out.println("<a href=\"/account.jsp?id="+id+"\">editer</a><br/>" );
}
%>

<h2> <%= account.getPseudo() %> </h2>
<% if(account.getForumId() != null) { %>
Un compte similaire existe sur le forum, mais n'est pas lié à FMG.<br/>
<% } else { %>
Ce compte FMG n'est pas lié a un compte du forum.<br/>
<% } %>
<% if(account.isAllowPrivateMsg() && account.haveEmail()) { %>
<a href="/privatemsg.jsp?id=<%= account.getId()%>">Ecrire un message</a><br/>
<% } %>

<pre><%= account.getDescription() %></pre>

<p>level: <%= account.getCurrentLevel() %><br/>
<img src='<%= account.getGradUrl() %>'/></p>

<h2>Partie en cours</h2>
<table class="fmp-array" style="width:100%;">
<!--tr><td>Date</td><td>score</td><td>score</td><td>Locale</td><td>Mail</td><td>Pseudo</td></tr-->
<%
SimpleDateFormat  simpleFormat = new SimpleDateFormat("dd/MM/yyyy");
for(EbAccountStats astat : account.getStats())
{
  if( astat instanceof StatsGame && ((StatsGame)astat).getStatus() == StatsGame.Status.Running )
  {
    out.println("<tr>") ;
    StatsGame stat = ((StatsGame)astat);
    EbGamePreview game = FmgDataStore.dao().find( EbGamePreview.class, stat.getGameId() );
    if( game == null )
    {
      // game wasn't found but his stat was "running"... it's an error !
      System.err.println("game wasn't found but his stat was 'running'...");
      System.err.println("user: "+account.getPseudo()+" gameid: "+stat.getGameId());
      
      out.println("<td><img src='/images/unknown-minimap.jpg' height='50px'/></td>");
      out.println("<td>"+stat.getGameName()+"<br/>"
          +simpleFormat.format(stat.getLastUpdate())+"</td>");
      out.println("<td>"+stat.getConfigGameTime().getIconsAsHtml()+"</td>");
    } else {
      out.println("<td><img src='"+game.getMinimapUri()+"' height='50px'/></td>");
      out.println("<td><a href='/game.jsp?id="+game.getId()+"' >"+stat.getGameName()+"</a><br/>"
          +simpleFormat.format(stat.getLastUpdate())+"</td>");
      out.println("<td>"+game.getIconsAsHtml()+"</td>");
    }
    
    out.println("<td>" );
    if( stat.isCreator() )
    {
      out.println("<img src='/images/icons/referee.png' title='arbitre' />");
    }
	if( astat instanceof StatsGamePlayer )
	{
	  StatsGamePlayer statPlayer = ((StatsGamePlayer)astat);
	  out.println("<img src='/images/icons/color/"+EnuColor.singleColorToString(statPlayer.getInitialColor())+"/icon32.png'/>" );
	}
	out.println("</td>");
	 
	out.println("<td>-</td>" );
	out.println("</tr>");
  }
}
%>
</table>


<h2>Historique</h2>
<table class="fmp-array" style="width:100%;">
<%
for(EbAccountStats astat : account.getStats())
{
  if( astat instanceof StatsGame && ((StatsGame)astat).getStatus() != StatsGame.Status.Running )
  {
    out.println("<tr>") ;
    StatsGame stat = ((StatsGame)astat);
    EbGamePreview game = FmgDataStore.dao().find( EbGamePreview.class, stat.getGameId() );
    if( game == null )
    {
      // game wasn't found but his stat was "running"... it's an error !
      System.err.println("game wasn't found but his stat was 'running'...");
      System.err.println("user: "+account.getPseudo()+" gameid: "+stat.getGameId());
      
      out.println("<td><img src='/images/unknown-minimap.jpg' height='50px'/></td>");
      out.println("<td>"+stat.getGameName()+"<br/>"
          +simpleFormat.format(stat.getLastUpdate())+"</td>");
      out.println("<td>");
      if( stat.getStatus() == StatsGame.Status.Aborted )
	  {
	    out.println("<img src='/images/icons/canceled16.png'/>");
	  }
      out.println(stat.getConfigGameTime().getIconsAsHtml()+"</td>");
    } else {
      out.println("<td><img src='"+game.getMinimapUri()+"' height='50px'/></td>");
      out.println("<td><a href='/game.jsp?id="+game.getId()+"' >"+stat.getGameName()+"</a><br/>"
          +simpleFormat.format(stat.getLastUpdate())+"</td>");
      out.println("<td>"+game.getIconsAsHtml()+"</td>");
    }
    
    out.println("<td>" );
    if( stat.isCreator() )
    {
      out.println("<img src='/images/icons/referee.png' title='arbitre' />");
    }
	if( astat instanceof StatsGamePlayer )
	{
	  StatsGamePlayer statPlayer = ((StatsGamePlayer)astat);
	  out.println("<img src='/images/icons/color/"+EnuColor.singleColorToString(statPlayer.getInitialColor())+"/icon32.png'/>" );
	  out.println("<img src='"+statPlayer.getPlayerStyle().getIconUrl()+"'/>");
	  if( statPlayer.getStatus() == StatsGame.Status.Banned )
	  {
	    out.println("<img src='/images/icons/ban.gif'/>");
	  }
	}
	out.println("</td>");
	 
	out.println("<td>"+stat.getFinalScore()+"</td>" );
	out.println("</tr>");
  }
  else if( astat instanceof StatsErosion  )
  {
    StatsErosion stat = ((StatsErosion)astat);
	out.println("<tr>") ;
	out.println("<td>"+simpleFormat.format(stat.getLastUpdate())+"</td>" );
	out.println("<td>Erosion</td>" );
	out.println("<td>"+stat.getFinalScore()+"</td>" );
    out.println("</tr>") ;
  }
}
%>
</table>

<%@include file="include/footer.jsp"%>
</body>
</html>
