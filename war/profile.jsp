<%@ page import="java.util.*,java.text.*,com.fullmetalgalaxy.server.*,com.fullmetalgalaxy.model.persist.*,com.fullmetalgalaxy.model.constant.*,com.fullmetalgalaxy.model.*" %>
<%@page pageEncoding="Cp1252" contentType="text/html; charset=Cp1252" %>

<!DOCTYPE html>
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

<% String url = "/gameprofile.jsp?id=" + account.getId(); %>
<jsp:include page="<%= url %>"></jsp:include>

<%@include file="include/footer.jsp"%>
</body>
</html>
