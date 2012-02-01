<%@page import="com.fullmetalgalaxy.server.EbAccount.AllowMessage"%>
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
<% if( account.isIsforumIdConfirmed() && account.getForumId() != null ) { %>
<a href="<%=account.getProfileUrl()%>">Ce compte est lié a un compte du forum.</a>
<% } else if(account.getForumId() != null) { %>
Un compte similaire existe sur le forum, mais n'est pas lié à FMG.<br/>
<% } else { %>
Ce compte FMG n'est pas lié a un compte du forum.<br/>
<% } %>
<% if(account.getAllowMsgFromPlayer() != AllowMessage.No) { %>
<a href="<%= account.getPMUrl()%>">Ecrire un message</a><br/>
<% } %>

<p>level: <%= df.format(account.getCurrentLevel()) %><br/>
<img src='<%= account.getGradUrl() %>'/><br/>
Gain total: <%= account.getTotalScoreSum() %><br/>
Nb victoire: <%= account.getVictoryCount() %><br/>
Nb partie joué: <%= account.getFinshedGameCount() %><br/>
Agressivité: <%= df.format(account.getStyleRatio()) %><br/>
Fairplay: <%= account.getFairplay() %><br/>
Inscription : <span class='date'><%= dateFormat.format( account.getSubscriptionDate() ) %></span><br/>
Dernière connexion : <span class='date'><%= dateFormat.format( account.getLastConnexion() ) %></span><br/>
</p>

<% String url = "/gameprofile.jsp?id=" + account.getId(); %>
<jsp:include page="<%= url %>"></jsp:include>

<%@include file="include/footer.jsp"%>
</body>
</html>
