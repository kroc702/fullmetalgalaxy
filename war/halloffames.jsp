<%@ page import="java.util.*,java.text.*,com.fullmetalgalaxy.server.*,com.fullmetalgalaxy.model.persist.*,com.fullmetalgalaxy.model.*,com.fullmetalgalaxy.model.constant.*,com.googlecode.objectify.Query,com.fullmetalgalaxy.model.ressources.SharedI18n" %>
<%@page pageEncoding="utf-8" contentType="text/html; charset=utf-8" %>
<!DOCTYPE html>
<html>
<head>
<title>Full Metal Galaxy - Joueurs</title>
        
<%@include file="include/meta.jsp"%>

</head>
<body>
<%@include file="include/header.jsp"%>

<%
final int COUNT_PER_PAGE = 20;
int offset = 0;
try
{
  offset = Integer.parseInt( request.getParameter( "offset" ) );
} catch( NumberFormatException e )
{
}

String otherParams = "";
String title = "Joueurs classés dans les 18 derniers mois";
boolean allPlayers = false;
if( request.getParameter( "all" ) != null ) {
  title = "Tous les joueurs";
  otherParams += "&all";
  allPlayers = true;
}
String orderby = request.getParameter( "orderby" );
if( orderby == null )
{
  if( allPlayers )
    orderby = "-m_fullStats.m_averageNormalizedRank";
  else
    orderby = "-m_currentStats.m_averageNormalizedRank";
}
if( orderby.equals("-m_lastConnexion") )
{
  title = "Les derniers joueurs connectés";
}

Query<EbAccount> accountQuery = FmgDataStore.dao().query(EbAccount.class);
if( !allPlayers ) {
  accountQuery.filter( "m_currentStats.m_includedInRanking", true );
}
accountQuery.order(orderby);
DateFormat dateFormat = new SimpleDateFormat( SharedI18n.getMisc( Auth.getUserId(request,response) ).dateFormat() );
DecimalFormat df = new DecimalFormat("#.#");

out.println( "<h2>"+title+"</h2>" );
if( allPlayers ) {
  out.println( "<a href='"+ request.getRequestURL() +"?orderby="+orderby+"'>Joueurs classés dans les 18 derniers mois</a> <br/><br/>" );
} else {
  out.println( "<a href='"+ request.getRequestURL() +"?orderby="+orderby+"&all'>Tous les joueurs</a> <br/><br/>" );
}
out.println("<table width='100%'>");
out.println("<tr><td>Avatar</td>" );
out.println("<td><a href='"+ request.getRequestURL() +"?orderby=m_pseudo&all'>Pseudo</a></td>");
if( allPlayers ) {
  out.println("<td><a href='"+ request.getRequestURL() +"?orderby=-m_fullStats.m_averageNormalizedRank"+otherParams+"'>Niveau</a></td>");
  out.println("<td>Nb <a href='"+ request.getRequestURL() +"?orderby=-m_fullStats.m_victoryCount"+otherParams+"'>victoire</a> / <a href='"+ request.getRequestURL() +"?orderby=-m_fullStats.m_finshedGameCount"+otherParams+"'>partie</a></td>");
  out.println("<td><a href='"+ request.getRequestURL() +"?orderby=-m_fullStats.m_score"+otherParams+"'>Gains</a> / <a href='"+ request.getRequestURL() +"?orderby=-m_fullStats.m_averageProfitability"+otherParams+"'>Rentabilité</a></td>");
} else {
	out.println("<td><a href='"+ request.getRequestURL() +"?orderby=-m_currentStats.m_averageNormalizedRank"+otherParams+"'>Niveau</a></td>");
	out.println("<td>Nb <a href='"+ request.getRequestURL() +"?orderby=-m_currentStats.m_victoryCount"+otherParams+"'>victoire</a> / <a href='"+ request.getRequestURL() +"?orderby=-m_currentStats.m_finshedGameCount"+otherParams+"'>partie</a></td>");
	out.println("<td><a href='"+ request.getRequestURL() +"?orderby=-m_currentStats.m_score"+otherParams+"'>Gains</a> / <a href='"+ request.getRequestURL() +"?orderby=-m_currentStats.m_averageProfitability"+otherParams+"'>Rentabilité</a></td>");
}
out.println("<td></td>");
out.println("<td><a href='"+ request.getRequestURL() +"?orderby=-m_lastConnexion&all'>Dernière connexion</a></td><td></td>");
out.println("<td></td>");
out.println("<td><a href='"+request.getRequestURL()+"?orderby=-m_trueSkillLevel&ts"+otherParams+"'>TS</a></td>");
out.println("</tr>");

for( EbAccount account : accountQuery.offset(offset).limit(COUNT_PER_PAGE) )
{
  out.println("<tr>");
  // avatar
  out.println("<td><a href='"+ account.getProfileUrl() + "'><img src='" + account.getAvatarUrl() + "' height='40px' /></a></td>");
  // pseudo
  out.println("<td><a href='"+ account.getProfileUrl() + "'>" + account.getPseudo() + "</a></td>");
  if( allPlayers ) {
    // level
    out.println("<td><img src='"+account.getGradUrl()+"' border=0> " +account.getFullStats().getAverageNormalizedRankInPercent()+"</td>");
    // game victory / played count
    out.println("<td>"+account.getFullStats().getVictoryCount()+" / "+account.getFullStats().getFinshedGameCount()+"</td>");
    // total score sum
    out.println("<td>"+account.getFullStats().getScore()+" / "+account.getFullStats().getAverageProfitabilityInPercent()+"%</td>");
  } else {
	  // level
	  out.println("<td><img src='"+account.getGradUrl()+"' border=0> " +account.getCurrentStats().getAverageNormalizedRankInPercent()+"</td>");
	  // game victory / played count
	  out.println("<td>"+account.getCurrentStats().getVictoryCount()+" / "+account.getCurrentStats().getFinshedGameCount()+"</td>");
	  // total score sum
	  out.println("<td>"+account.getCurrentStats().getScore()+" / "+account.getCurrentStats().getAverageProfitabilityInPercent()+"%</td>");
  }
  // fairplay
  out.println("<td>");
  if( account.getFairplay() > 0 )
  {
    out.println(" <img src='/images/icons/thumbup.gif' title='"+account.getFairplay()+"'/>");
  }
  else if( account.getFairplay() < 0 )
  {
    out.println(" <img src='/images/icons/thumbdown.gif' title='"+account.getFairplay()+"'/>");
  }
  out.println("</td>");
  
  // last connexion
  out.println("<td><span class='date'>"+ dateFormat.format( account.getLastConnexion() ) +"</span></td>");
  // private message
  if( (account.allowMsgFromPlayer() && account.haveEmail()) )
  {
    out.println("<td><a href='"+ account.getEMailUrl() + "'><img src='/images/css/icon_pm.gif' border=0 alt='PM'></a></td>" );
  } else {
    out.println("<td></td>" );
  }
  // level TS
  out.println("<td>"+df.format( account.getCurrentLevel() )+"</td>" );
  out.println("</tr>");
}
out.println("</table>");
%>
	
	<p>Pages :
	<%
		int p = 0;
		int accountListCount = GlobalVars.getAccountCount();
		while(accountListCount > 0)
		{
		  out.println( "<a href='"+ request.getRequestURL() +"?orderby="+orderby+otherParams+"&offset="+(p*COUNT_PER_PAGE)+"'>"+(p+1)+"</a> " );
		  accountListCount -= COUNT_PER_PAGE;
		  p++;
		}
	%>
	</p>
	
<%@include file="include/footer.jsp"%>
</body>
</html>
