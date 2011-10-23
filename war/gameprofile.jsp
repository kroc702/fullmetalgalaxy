<%@page import="com.fullmetalgalaxy.model.ressources.SharedI18n"%>
<%@ page import="java.util.*,java.text.*,com.fullmetalgalaxy.server.*,com.fullmetalgalaxy.model.persist.*,com.fullmetalgalaxy.model.constant.*,com.fullmetalgalaxy.model.*" %>
<%@page pageEncoding="utf-8" contentType="text/html; charset=utf-8" %>
<!DOCTYPE html>
<html>
<head>
<title>Full Metal Galaxy - Profil du joueur</title>
        
</head>
<body>

<%
EbAccount account = ServerUtil.findRequestedAccount(request);
if( account == null ) 
{ 
	out.println("<h1>Ce profil n'a pas été trouvé sur <a href='http://www.fullmetalgalaxy.com'>www.fullmetalgalaxy.com</a></h1>" );
	return;
}
%>

<h1>Partie en cours</h1>
<table class="fmp-array" style="width:100%;">
<!--tr><td>Date</td><td>score</td><td>score</td><td>Locale</td><td>Mail</td><td>Pseudo</td></tr-->
<%
long clientid = 0;
EbAccount clientaccount = Auth.getUserAccount(request,response);
if( clientaccount != null )
{
  clientid = clientaccount.getId();
}
SimpleDateFormat  simpleFormat = new SimpleDateFormat(SharedI18n.getMisc(clientid).dateFormat());
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
      out.println("<td><a href='/game.jsp?id="+game.getId()+"' target='_top'>"+stat.getGameName()+"</a><br/>"
          +simpleFormat.format(stat.getGameCreation())+"</td>");
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


<h1>Historique</h1>
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
      // game wasn't found 
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
          +simpleFormat.format(stat.getGameCreation())+" -> "+simpleFormat.format(stat.getLastUpdate())+"</td>");
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
		out.println("</td><td>"+((StatsGamePlayer)stat).getFmpScore()+" -> "+stat.getFinalScore()+"</td>" );
	}
	else
	{
		out.println("</td><td>-</td>");
	} 
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

</body>
</html>
