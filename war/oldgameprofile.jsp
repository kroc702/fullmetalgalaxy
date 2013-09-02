<%@page import="com.fullmetalgalaxy.model.ressources.SharedI18n"%>
<%@page import="java.util.*,java.text.*,com.fullmetalgalaxy.server.*,com.fullmetalgalaxy.model.persist.*,com.fullmetalgalaxy.model.constant.*,com.fullmetalgalaxy.model.*" %>
<%@page pageEncoding="utf-8" contentType="text/html; charset=utf-8" %>
<!DOCTYPE html>
<html>
<head>
<title>Full Metal Galaxy - Partie terminées</title>
        
</head>
<body>

<%
Iterable<PlayerGameStatistics> gameList = new ArrayList<PlayerGameStatistics>();

EbAccount account = ServerUtil.findRequestedAccount(request);
if( account != null ) 
{ 
  String pseudo = account.getPseudo();
  gameList = FmgDataStore.dao().query(PlayerGameStatistics.class)
                               .filter( "m_account.m_pseudo", pseudo )
                               .order("-m_gameEndDate");
}
else if( request.getParameter( "corpo" ) != null ) // search in corporation
{
  Company corpo = Company.valueOf( request.getParameter( "corpo" ) );
  gameList = FmgDataStore.dao().query(PlayerGameStatistics.class)
      .filter( "m_company", corpo )
      .order("-m_gameEndDate");
  out.println("<h1>"+corpo.getFullName()+"</h1>");
}
else // error
{ 
	out.println("<h1>Ce profil n'a pas été trouvé sur <a href='http://www.fullmetalgalaxy.com'>www.fullmetalgalaxy.com</a></h1>" );
	return;
}
%>

<center>Parties terminé</center>
<table class="fmp-array" style="width:100%;">
<!--tr><td>Date</td><td>score</td><td>score</td><td>Locale</td><td>Mail</td><td>Pseudo</td></tr-->
<%
HashSet<Long> displayedGameIds = new HashSet<Long>();

for( PlayerGameStatistics game : gameList )
{
  // avoid displaying several time the same game for corpo old games
  if( displayedGameIds.contains( game.getKeyGamePreview().getId() ) ) continue;
  displayedGameIds.add( game.getKeyGamePreview().getId() );
  
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
  out.println("rang: "+game.getRank()+" / "+game.getGameTeamCount());
  out.println("score: "+game.getScore());
  out.println("Corpo: "+game.getCompany());
  out.println("investissement: "+game.getInvestment());
  out.println("config time: "+game.getConfigGameTime());
  out.println("ore load: "+game.getOreLoad());
  out.println("construction: "+game.getConstruction());
  out.println("destruction: "+game.getDestruction());
  out.println("freighter capture: "+game.getFreighterCapture());
  out.println("unit capture: "+game.getUnitsCapture());
  if( game.getAverageReactivityInSec() > 0 )
  {
    if( game.getConfigGameTime().isQuick() )
    {
      out.println("réactivité moyenne: "+(game.getAverageReactivityInSec()/(60))+" secondes");
    } else {
      out.println("réactivité moyenne: "+(game.getAverageReactivityInSec()/(60*60))+" heures");
    }
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
