<%@ page import="java.util.*,java.text.*,com.fullmetalgalaxy.server.*,com.fullmetalgalaxy.model.persist.*,com.fullmetalgalaxy.model.constant.*,com.fullmetalgalaxy.model.*,com.fullmetalgalaxy.model.ressources.SharedI18n" %>
<%@page pageEncoding="utf-8" contentType="text/html; charset=utf-8" %>
<!DOCTYPE html>
<html>
<head>
<title>Full Metal Galaxy - Profil du joueur</title>
        
<%@include file="include/meta.jsp"%>

</head>
<body>
<%@include file="include/header.jsp"%>

<%
EbAccount account = ServerUtil.findRequestedAccount(request);
if( account == null ) 
{ 
	out.println("<h2>Le profil " + request.getParameter( "id" ) + " n'a pas été trouvé.</h2>" );
	return;
}
if( Auth.isUserAdmin( request, response ) )
{
	out.println("<a href=\"/account.jsp?id="+account.getId()+"\">editer</a><br/>" );
}

DateFormat dateFormat = new SimpleDateFormat( SharedI18n.getMisc( Auth.getUserId(request,response) ).dateFormat() );
DecimalFormat df = new DecimalFormat("#.#");
%>

<img src='<%= account.getAvatarUrl() %>' border=0 alt='Avatar' style="float:right;">

<h2> <%= account.getPseudo() %> </h2>
<% if(account.allowMsgFromPlayer() ) { %>
<a href="<%= account.getEMailUrl() %>">Ecrire un message</a><br/>
<% } %>

<%--
<img src='<%= account.getGradUrl() %>'/><br/>
Gain total: <%= account.getTotalScoreSum() %><br/>
Nb victoire: <%= account.getVictoryCount() %><br/>
Nb partie joué: <%= account.getFinshedGameCount() %><br/>
Agressivité: <%= df.format(account.getStyleRatio()) %><br/>
Fairplay: <%= account.getFairplay() %><br/>
 --%>
<p>
Inscription : <span class='date'><%= dateFormat.format( account.getSubscriptionDate() ) %></span><br/>
Dernière connexion : <span class='date'><%= dateFormat.format( account.getLastConnexion() ) %></span><br/>
Level TS: <%= df.format(account.getCurrentLevel()) %>
<small style="color:grey">(M=<%=df.format(account.getTrueSkillMean())%> SD=<%=df.format(account.getTrueSkillSD()) %>)</small><br/>
</p>

<h4>Stats sur les 18 derniers mois</h4>
<pre>
Gains total: <%= account.getCurrentStats().getScore() %>
Nb de victoires/défaites/parties: <%= account.getCurrentStats().getVictoryCount() %> / <%= account.getCurrentStats().getLosedCount() %> / <%= account.getCurrentStats().getFinshedGameCount() %>
Adversaires rencontrés (<%= account.getCurrentStats().getOpponentPlayerCount() %>): <% for(EbPublicAccount player : account.getCurrentStats().getOpponentPlayers()) {
  out.print( player.getPseudo() +", " );
} %>
<% if(account.getCurrentStats().getFinshedGameCount()>0) { %>Période: <%= dateFormat.format( account.getCurrentStats().getFirstGameDate() ) %> - <%= dateFormat.format( account.getCurrentStats().getLastGameDate() ) %>

Stats moyennes:
Rank: <%= account.getCurrentStats().getAverageNormalizedRank() %>
Rentabilité: <%= account.getCurrentStats().getAverageProfitabilityInPercent() %> %
Réactivité: <%= account.getCurrentStats().getAverageReactivityInSec()/(60*60) %> heures
Capture de minerais: <%= df.format( account.getCurrentStats().getOreLoad()*1.0/account.getCurrentStats().getFinshedGameCount() ) %>
Construction: <%= df.format( account.getCurrentStats().getConstruction()*1.0/account.getCurrentStats().getFinshedGameCount() ) %>
Destruction: <%= df.format( account.getCurrentStats().getDestruction()*1.0/account.getCurrentStats().getFinshedGameCount() ) %>
Capture astronef: <%= df.format( account.getCurrentStats().getFreighterCapture()*1.0/account.getCurrentStats().getFinshedGameCount() ) %>
Capture unité: <%= df.format( account.getCurrentStats().getUnitsCapture()*1.0/account.getCurrentStats().getFinshedGameCount() ) %>
<% } %></pre>

<h4>Stats complètes</h4>
<pre>
Gains total: <%= account.getFullStats().getScore() %>
Nb de victoires/défaites/parties: <%= account.getFullStats().getVictoryCount() %> / <%= account.getFullStats().getLosedCount() %> / <%= account.getFullStats().getFinshedGameCount() %>
Adversaires rencontrés (<%= account.getFullStats().getOpponentPlayerCount() %>): <% for(EbPublicAccount player : account.getFullStats().getOpponentPlayers()) {
  out.print( player.getPseudo() +", " );
} %>
<% if(account.getFullStats().getFinshedGameCount()>0) { %>Période: <%= dateFormat.format( account.getFullStats().getFirstGameDate() ) %> - <%= dateFormat.format( account.getFullStats().getLastGameDate() ) %>

Stats moyennes:
Rank: <%= account.getFullStats().getAverageNormalizedRank() %>
Rentabilité: <%= account.getFullStats().getAverageProfitabilityInPercent() %> %
Réactivité: <%= account.getFullStats().getAverageReactivityInSec()/(60*60) %> heures
Capture de minerais: <%= df.format( account.getFullStats().getOreLoad()*1.0/account.getFullStats().getFinshedGameCount() ) %>
Construction: <%= df.format( account.getFullStats().getConstruction()*1.0/account.getFullStats().getFinshedGameCount() ) %>
Destruction: <%= df.format( account.getFullStats().getDestruction()*1.0/account.getFullStats().getFinshedGameCount() ) %>
Capture astronef: <%= df.format( account.getFullStats().getFreighterCapture()*1.0/account.getFullStats().getFinshedGameCount() ) %>
Capture unité: <%= df.format( account.getFullStats().getUnitsCapture()*1.0/account.getFullStats().getFinshedGameCount() ) %>
<% } %></pre>

<%
String requestURL = request.getRequestURI().toString();
requestURL += "?id=" + account.getId();
%>
<big><a href="<%=requestURL%>">Parties en cours</a> | <a href="<%=requestURL+"&uri=oldgameprofile.jsp"%>">Parties terminés</a></big> 
<hr/>
<%
String url = request.getParameter( "uri" );
if( url == null ) url = "/gameprofile.jsp";
url += "?id=" + account.getId(); 
%>
<jsp:include page="<%= url %>"></jsp:include>

<%@include file="include/footer.jsp"%>
</body>
</html>
