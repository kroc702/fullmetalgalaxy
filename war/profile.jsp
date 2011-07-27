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

<p>level: <%= account.getCurrentLevel() %></p>

<h2>Partie en cours</h2>
<table class="fmp-array" style="width:100%;">
<!--tr><td>Date</td><td>score</td><td>score</td><td>Locale</td><td>Mail</td><td>Pseudo</td></tr-->
<%
SimpleDateFormat  simpleFormat = new SimpleDateFormat("dd/MM/yyyy");
for(EbAccountStats astat : account.getStats())
{
  if( astat instanceof StatsGamePlayer && ((StatsGamePlayer)astat).getStatus() == StatsGame.Status.Running )
  {
    StatsGamePlayer stat = ((StatsGamePlayer)astat);
	out.println("<tr>") ;
	out.println("<td>"+simpleFormat.format(stat.getLastUpdate())+"</td>" );
	out.println("<td>"+stat.getGameName()+"</td>" );
  	out.println("<td>"+stat.getFinalScore()+"</td>" );
    out.println("</tr>") ;
  }
}
%>
</table>

<h2>Arbitre sur...</h2>
<table class="fmp-array" style="width:100%;">
<%
for(EbAccountStats astat : account.getStats())
{
  if( astat instanceof StatsGame && ((StatsGame)astat).isCreator() && ((StatsGame)astat).getStatus() == StatsGame.Status.Running )
  {
    StatsGame stat = ((StatsGame)astat);
	out.println("<tr>") ;
	out.println("<td>"+simpleFormat.format(stat.getLastUpdate())+"</td>" );
	out.println("<td>"+stat.getGameName()+"</td>" );
    out.println("</tr>") ;
  }
}
%>
</table>

<h2>Autre</h2>
<table class="fmp-array" style="width:100%;">
<%
for(EbAccountStats astat : account.getStats())
{
  if( astat instanceof StatsGame && ((StatsGame)astat).getStatus() != StatsGame.Status.Running )
  {
    StatsGame stat = ((StatsGame)astat);
	out.println("<tr>") ;
	out.println("<td>"+simpleFormat.format(stat.getLastUpdate())+"</td>" );
	out.println("<td>"+stat.getGameName()+"</td>" );
	out.println("<td>"+stat.getFinalScore()+"</td>" );
	if( stat.isCreator() )
	{
	  out.println("<td>créateur</td>" );
	}
    out.println("</tr>") ;
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
