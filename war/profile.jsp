<%@ page import="java.util.*,java.text.*,com.fullmetalgalaxy.server.*,com.fullmetalgalaxy.model.persist.*,com.fullmetalgalaxy.model.constant.*,com.fullmetalgalaxy.model.*" %>
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
	out.println("<a href=\"/account.jsp?id="+id+"\">editer</a><br/>" );
}
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
<% if(account.isAllowPrivateMsg()) { %>
<a href="<%= account.getPMUrl()%>">Ecrire un message privé</a><br/>
<a href="/email.jsp?id=<%= account.getId()%>">Ecrire un email</a><br/>
<% } %>

<pre><%= account.getDescription() %></pre>

<p>level: <%= account.getCurrentLevel() %><br/>
<img src='<%= account.getGradUrl() %>'/></p>

<% String url = "/gameprofile.jsp?id=" + account.getId(); %>
<jsp:include page="<%= url %>"></jsp:include>

<%@include file="include/footer.jsp"%>
</body>
</html>
