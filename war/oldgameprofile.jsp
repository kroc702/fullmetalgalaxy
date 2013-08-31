<%@page import="com.fullmetalgalaxy.model.ressources.SharedI18n"%>
<%@page import="java.util.*,java.text.*,com.fullmetalgalaxy.server.*,com.fullmetalgalaxy.model.persist.*,com.fullmetalgalaxy.model.constant.*,com.fullmetalgalaxy.model.*" %>
<%@page pageEncoding="utf-8" contentType="text/html; charset=utf-8" %>
<!DOCTYPE html>
<html>
<head>
<title>Full Metal Galaxy - Partie en cours</title>
        
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

<center>Parties terminé</center>
<table class="fmp-array" style="width:100%;">
<!--tr><td>Date</td><td>score</td><td>score</td><td>Locale</td><td>Mail</td><td>Pseudo</td></tr-->
<%
Iterable<PlayerGameStatistics> gameList = new ArrayList<PlayerGameStatistics>();
String pseudo = account.getPseudo();
gameList = FmgDataStore.dao().query(PlayerGameStatistics.class)
                             .filter( "m_account.m_pseudo", pseudo )
                             .order("-m_gameEndDate");
for( PlayerGameStatistics game : gameList )
{
  out.println( "<tr style=\"width:100px;\">" );
  // game name
  out.print( "<td><a href=\"/game.jsp?id=" + game.getKeyGamePreview().getId()
      + "\"><big>" + game.getGameName()
      + "</big></a></td>" );

  out.print( "<td><pre>");
  
  if( game.isWasBanned() )
  {
    out.println("Bani de cette partie" );
  } else if( game.getReplacement() != null )
  {
    out.println("remplace "+ game.getReplacement().getPseudo() );
  }

  if(game.getPartnerPlayers().size() > 0 )
  {
    out.print( "partenaires: " );
    for(EbPublicAccount player : game.getPartnerPlayers())
    {
      out.print( player.getPseudo() );
      out.print( ", " );
    }
  }
  out.println("");
  out.print( "adverssaires: " );
  for(EbPublicAccount player : game.getOpponentPlayers())
  {
    out.print( player.getPseudo() );
    out.print( ", " );
  }
  out.println("");
  out.println("rang: "+game.getRank());
  out.println("score: "+game.getScore());
  out.println("Corpo: "+game.getCompany());
  out.println("investissement: "+game.getInvestment(  ));
  out.println("config time: "+game.getConfigGameTime());
  out.println("ore load: "+game.getOreLoad());
  out.println("construction: "+game.getConstruction());
  out.println("destruction: "+game.getDestruction());
  out.println("freighter capture: "+game.getFreighterCapture());
  out.println("unit capture: "+game.getUnitsCapture());
  if( game.getAverageReactivityInSec() > 0 )
  {
    out.println("réactivité moyenne: "+(game.getAverageReactivityInSec()/(60*60))+" heures");
  }
  out.println("nb tour: "+game.getPlayerTurnCount());
  out.println("TS update: "+game.getTsUpdate());
  
  out.print( "</pre></td>" );
  out.println("</tr>" );
}
%>
</table>


</body>
</html>
